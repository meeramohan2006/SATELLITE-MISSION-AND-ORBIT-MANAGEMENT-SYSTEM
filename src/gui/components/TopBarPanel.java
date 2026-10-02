package gui.components;

import database.DBConnection;
import gui.theme.Theme;
import model.User;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Top horizontal bar showing system title, user profile, DB status, and window controls.
 */
public class TopBarPanel extends JPanel {

    public interface TopBarActionListener {
        void onExitClicked();
        void onDbSettingsClicked();
    }

    private final String title;
    private final User currentUser;
    private final TopBarActionListener listener;

    private boolean isCloseHovered = false;
    private boolean isDbHovered = false;

    public TopBarPanel(String title, User user, TopBarActionListener listener) {
        this.title = title;
        this.currentUser = user != null ? user : new User("admin", "admin123", "Administrator", "ADMIN");
        this.listener = listener;
        setPreferredSize(new Dimension(1180, 50));
        setLayout(null);
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int w = getWidth();
                // Close button area
                if (e.getX() >= w - 45 && e.getX() <= w - 10 && e.getY() >= 10 && e.getY() <= 40) {
                    if (listener != null) listener.onExitClicked();
                }
                // DB status indicator area
                if (e.getX() >= w - 170 && e.getX() <= w - 60 && e.getY() >= 10 && e.getY() <= 40) {
                    if (listener != null) listener.onDbSettingsClicked();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int w = getWidth();
                boolean closeHov = (e.getX() >= w - 45 && e.getX() <= w - 10 && e.getY() >= 10 && e.getY() <= 40);
                boolean dbHov = (e.getX() >= w - 170 && e.getX() <= w - 60 && e.getY() >= 10 && e.getY() <= 40);

                if (closeHov != isCloseHovered || dbHov != isDbHovered) {
                    isCloseHovered = closeHov;
                    isDbHovered = dbHov;
                    setCursor(new Cursor(closeHov || dbHov ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Top bar background
        g2.setColor(new Color(9, 18, 40, 220));
        g2.fillRect(0, 0, w, h);

        // Bottom border
        g2.setColor(Theme.BORDER_CARD);
        g2.drawLine(0, h - 1, w, h - 1);

        // App title
        g2.setColor(Theme.ACCENT_CYAN);
        g2.setFont(Theme.FONT_TITLE);
        g2.drawString("🛰", 20, 32);

        g2.setColor(Theme.TEXT_WHITE);
        g2.setFont(Theme.FONT_SUBTITLE);
        g2.drawString(title, 55, 32);

        // User badge on right
        int userBadgeX = w - 280;
        g2.setColor(new Color(20, 35, 65));
        g2.fillRoundRect(userBadgeX, 10, 95, 30, 15, 15);
        g2.setColor(Theme.BORDER_CARD);
        g2.drawRoundRect(userBadgeX, 10, 95, 30, 15, 15);

        g2.setColor(Theme.TEXT_WHITE);
        g2.setFont(Theme.FONT_SMALL_BOLD);
        g2.drawString("👤 " + currentUser.getUsername(), userBadgeX + 10, 29);

        // DB Status pill
        int dbX = w - 170;
        boolean dbOk = !DBConnection.isFallbackMode();
        Color dbBg = dbOk ? new Color(0, 200, 115, 35) : new Color(245, 158, 11, 35);
        Color dbBorder = dbOk ? Theme.SUCCESS_GREEN : Theme.WARNING_ORANGE;
        Color dbText = dbOk ? Theme.BADGE_ACTIVE_TEXT : Theme.WARNING_ORANGE;

        g2.setColor(isDbHovered ? dbBg.brighter() : dbBg);
        g2.fillRoundRect(dbX, 10, 110, 30, 15, 15);
        g2.setColor(dbBorder);
        g2.drawRoundRect(dbX, 10, 110, 30, 15, 15);

        g2.setColor(dbText);
        g2.setFont(Theme.FONT_SMALL_BOLD);
        g2.drawString(dbOk ? "● MySQL Online" : "▲ Offline Mode", dbX + 12, 29);

        // Close / Exit button
        int closeX = w - 45;
        g2.setColor(isCloseHovered ? Theme.DANGER_RED : new Color(30, 45, 75));
        g2.fillRoundRect(closeX, 10, 32, 30, 8, 8);
        g2.setColor(Theme.TEXT_WHITE);
        g2.setFont(Theme.FONT_BODY_BOLD);
        g2.drawString("✕", closeX + 11, 30);

        g2.dispose();
    }
}
