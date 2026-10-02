package gui;

import gui.components.ExitDialog;
import model.User;

import javax.swing.JFrame;
import java.awt.Point;

/**
 * Coordinates seamless screen transitions between all frames in the application,
 * maintaining window position and current authenticated session.
 */
public class NavigationManager {

    private static User currentUser = new User("admin", "admin123", "Mission Director", "ADMIN");

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void navigate(JFrame currentFrame, String destination) {
        Point loc = currentFrame != null ? currentFrame.getLocation() : null;

        JFrame nextFrame = null;
        switch (destination.toLowerCase()) {
            case "landing":
                nextFrame = new LandingFrame();
                break;
            case "login":
                nextFrame = new LoginFrame();
                break;
            case "dashboard":
                nextFrame = new DashboardFrame(currentUser);
                break;
            case "satellites":
            case "satellite":
                nextFrame = new SatelliteFrame(currentUser);
                break;
            case "missions":
            case "mission":
                nextFrame = new MissionFrame(currentUser);
                break;
            case "orbits":
            case "orbit":
                nextFrame = new OrbitFrame(currentUser);
                break;
            case "search":
                nextFrame = new SearchFrame(currentUser);
                break;
            case "reports":
            case "report":
                nextFrame = new ReportsFrame(currentUser);
                break;
            case "logout":
                if (ExitDialog.showConfirmation(currentFrame)) {
                    currentUser = null;
                    nextFrame = new LoginFrame();
                } else {
                    return; // cancelled
                }
                break;
            case "exit":
                if (ExitDialog.showConfirmation(currentFrame)) {
                    System.exit(0);
                }
                return;
            default:
                nextFrame = new DashboardFrame(currentUser);
                break;
        }

        if (nextFrame != null) {
            if (loc != null) {
                nextFrame.setLocation(loc);
            } else {
                nextFrame.setLocationRelativeTo(null);
            }
            nextFrame.setVisible(true);
            if (currentFrame != null) {
                currentFrame.dispose();
            }
        }
    }
}
