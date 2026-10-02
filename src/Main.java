import database.DBConnection;
import gui.LandingFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Toolkit;

/**
 * Application Entry Point.
 * Sets up system properties, tests database connectivity,
 * and launches the Landing / Welcome Screen.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("   Satellite Mission and Orbit Data Management System               ");
        System.out.println("   Java Swing Desktop Application with JDBC and MySQL               ");
        System.out.println("====================================================================");

        // Enable smooth text antialiasing in Swing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            // Use System Look and Feel for native integration
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Test and initialize MySQL database in background
        new Thread(() -> {
            boolean connected = DBConnection.testAndInitializeDatabase();
            if (connected) {
                System.out.println("[Main] Successfully connected to MySQL database: " + DBConnection.getDbName());
            } else {
                System.out.println("[Main] MySQL offline or access denied. Starting with robust offline data cache.");
                System.out.println("[Main] Tip: Check or update db.properties or use 'DB Settings' inside the application.");
            }
        }).start();

        // Launch Landing Screen on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LandingFrame landing = new LandingFrame();
            landing.setVisible(true);
        });
    }
}
