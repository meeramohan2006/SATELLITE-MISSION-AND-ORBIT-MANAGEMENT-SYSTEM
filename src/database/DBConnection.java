package database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Manages database connectivity to MySQL using JDBC.
 * Supports auto-loading and persisting connection settings,
 * automatic schema initialization, and diagnostics.
 */
public class DBConnection {

    private static String host = "localhost";
    private static String port = "3306";
    private static String dbName = "satellite_management";
    private static String username = "root";
    private static String password = "root";

    private static boolean driverLoaded = false;
    private static boolean fallbackMode = false;
    private static String lastError = null;

    static {
        loadProperties();
        loadDriver();
    }

    private static void loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            driverLoaded = true;
            System.out.println("[DBConnection] MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] MySQL JDBC Driver not found in classpath: " + e.getMessage());
            driverLoaded = false;
        }
    }

    public static void loadProperties() {
        Properties props = new Properties();
        // Try loading from external file first
        File file = new File("db.properties");
        if (!file.exists()) {
            file = new File("src/resources/db.properties");
        }

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
            } catch (Exception e) {
                System.err.println("[DBConnection] Could not read db.properties file: " + e.getMessage());
            }
        } else {
            // Try classpath resource
            try (InputStream is = DBConnection.class.getResourceAsStream("/db.properties")) {
                if (is != null) {
                    props.load(is);
                }
            } catch (Exception e) {
                System.err.println("[DBConnection] Could not read db.properties resource: " + e.getMessage());
            }
        }

        if (props.getProperty("db.host") != null) host = props.getProperty("db.host").trim();
        if (props.getProperty("db.port") != null) port = props.getProperty("db.port").trim();
        if (props.getProperty("db.name") != null) dbName = props.getProperty("db.name").trim();
        if (props.getProperty("db.user") != null) username = props.getProperty("db.user").trim();
        if (props.getProperty("db.password") != null) password = props.getProperty("db.password").trim();
    }

    public static void saveProperties(String h, String p, String db, String user, String pass) {
        host = h;
        port = p;
        dbName = db;
        username = user;
        password = pass;

        Properties props = new Properties();
        props.setProperty("db.host", host);
        props.setProperty("db.port", port);
        props.setProperty("db.name", dbName);
        props.setProperty("db.user", username);
        props.setProperty("db.password", password);
        props.setProperty("db.autoReconnect", "true");
        props.setProperty("db.useSSL", "false");
        props.setProperty("db.serverTimezone", "UTC");

        try (FileOutputStream fos = new FileOutputStream("db.properties")) {
            props.store(fos, "Saved Database Configuration");
        } catch (Exception e) {
            System.err.println("[DBConnection] Error saving db.properties: " + e.getMessage());
        }
    }

    public static String getUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName + 
               "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&connectTimeout=2000&socketTimeout=3000";
    }

    public static String getServerUrl() {
        return "jdbc:mysql://" + host + ":" + port + 
               "/?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&connectTimeout=2000&socketTimeout=3000";
    }

    /**
     * Establishes and returns an active Connection to the satellite_management database.
     * Throws SQLException if connection cannot be established.
     */
    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) {
            loadDriver();
        }
        try {
            Connection conn = DriverManager.getConnection(getUrl(), username, password);
            fallbackMode = false;
            lastError = null;
            return conn;
        } catch (SQLException e) {
            lastError = e.getMessage();
            fallbackMode = true;
            throw e;
        }
    }

    /**
     * Tests connectivity to MySQL and verifies if satellite_management exists.
     * If the database does not exist but MySQL server connects, it automatically creates it!
     */
    public static boolean testAndInitializeDatabase() {
        try {
            // First attempt to connect to database directly
            try (Connection conn = getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    checkAndCreateTablesIfMissing(conn);
                    fallbackMode = false;
                    return true;
                }
            } catch (SQLException e) {
                // If database doesn't exist (error 1049), connect to server and create it
                if (e.getErrorCode() == 1049 || (e.getMessage() != null && e.getMessage().contains("Unknown database"))) {
                    try (Connection serverConn = DriverManager.getConnection(getServerUrl(), username, password);
                         Statement stmt = serverConn.createStatement()) {
                        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
                        System.out.println("[DBConnection] Database " + dbName + " created successfully.");
                    }
                    // Try connecting to new database and create tables
                    try (Connection conn = getConnection()) {
                        checkAndCreateTablesIfMissing(conn);
                        fallbackMode = false;
                        return true;
                    }
                }
                lastError = e.getMessage();
                fallbackMode = true;
                return false;
            }
        } catch (Exception e) {
            lastError = e.getMessage();
            fallbackMode = true;
            return false;
        }
        return false;
    }

    /**
     * Checks if tables exist in the database; if not, automatically executes database.sql
     */
    private static void checkAndCreateTablesIfMissing(Connection conn) {
        try {
            boolean tablesExist = false;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'satellites'")) {
                if (rs.next()) {
                    tablesExist = true;
                }
            }

            if (!tablesExist) {
                System.out.println("[DBConnection] Tables not found. Initializing schema and seed data from database.sql...");
                executeSqlScript(conn, "database.sql");
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Error checking/creating tables: " + e.getMessage());
        }
    }

    public static void executeSqlScript(Connection conn, String scriptPath) {
        File file = new File(scriptPath);
        InputStream is = null;
        try {
            if (file.exists()) {
                is = new FileInputStream(file);
            } else {
                is = DBConnection.class.getResourceAsStream("/" + scriptPath);
            }

            if (is == null) {
                System.err.println("[DBConnection] SQL script not found: " + scriptPath);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                 Statement stmt = conn.createStatement()) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("--") || trimmed.startsWith("//") || trimmed.isEmpty()) {
                        continue;
                    }
                    sb.append(line).append("\n");
                    if (trimmed.endsWith(";")) {
                        String sql = sb.toString().trim();
                        // strip trailing semicolon
                        sql = sql.substring(0, sql.length() - 1);
                        if (!sql.isEmpty() && !sql.toUpperCase().startsWith("CREATE DATABASE") && !sql.toUpperCase().startsWith("USE")) {
                            try {
                                stmt.execute(sql);
                            } catch (SQLException ex) {
                                System.err.println("[DBConnection] SQL statement warning: " + ex.getMessage());
                            }
                        }
                        sb.setLength(0);
                    }
                }
            }
            System.out.println("[DBConnection] Schema initialization completed.");
        } catch (Exception e) {
            System.err.println("[DBConnection] Failed to execute SQL script: " + e.getMessage());
        } finally {
            if (is != null) {
                try { is.close(); } catch (Exception ignored) {}
            }
        }
    }

    // Getters and Setters
    public static String getHost() { return host; }
    public static void setHost(String h) { host = h; }

    public static String getPort() { return port; }
    public static void setPort(String p) { port = p; }

    public static String getDbName() { return dbName; }
    public static void setDbName(String db) { dbName = db; }

    public static String getUsername() { return username; }
    public static void setUsername(String u) { username = u; }

    public static String getPassword() { return password; }
    public static void setPassword(String p) { password = p; }

    public static boolean isFallbackMode() { return fallbackMode; }
    public static void setFallbackMode(boolean fallback) { fallbackMode = fallback; }

    public static String getLastError() { return lastError; }
}
