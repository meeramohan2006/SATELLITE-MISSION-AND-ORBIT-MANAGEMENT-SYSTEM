package dao;

import database.DBConnection;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for User authentication and profiles.
 */
public class UserDAO {

    public User authenticate(String username, String password) {
        // Fallback default admin credentials
        if (username != null && password != null) {
            if ("admin".equalsIgnoreCase(username.trim()) && "admin123".equals(password)) {
                return new User("admin", "admin123", "Mission Director", "ADMIN");
            }
            if ("operator".equalsIgnoreCase(username.trim()) && "operator123".equals(password)) {
                return new User("operator", "operator123", "Telemetry Controller", "OPERATOR");
            }
        }

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Auth DB query failed, using demo account fallback: " + e.getMessage());
        }
        return null;
    }
}
