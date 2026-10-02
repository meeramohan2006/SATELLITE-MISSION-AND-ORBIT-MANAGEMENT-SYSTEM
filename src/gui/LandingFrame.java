package gui;

import gui.components.BackgroundPanel;
import gui.components.CustomButton;
import gui.theme.Theme;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Landing / Welcome Screen matching the top-left design in the reference UI.
 */
public class LandingFrame extends JFrame {

    public LandingFrame() {
        setTitle("Satellite Mission and Orbit Data Management System");
        setSize(1180, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Space Background with Earth visual
        BackgroundPanel bgPanel = new BackgroundPanel("assets/satellite_orbit_bg.jpg", 0.35f);
        bgPanel.setLayout(null);

        // Header Panel (Top-Left)
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Satellite icon
                g2.setColor(Theme.ACCENT_CYAN);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
                g2.drawString("🛰", 0, 32);

                // Main Title
                g2.setColor(Theme.TEXT_WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                g2.drawString("Satellite Mission and Orbit Data Management System", 42, 28);

                // Subtitle
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.FONT_SMALL_BOLD);
                g2.drawString("Manage Satellites   |   Track Missions   |   Monitor Orbits", 42, 48);

                g2.dispose();
            }
        };
        headerPanel.setBounds(50, 40, 700, 70);
        headerPanel.setOpaque(false);
        bgPanel.add(headerPanel);

        // Welcome Card (Left-Center)
        JPanel welcomeCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Dark Translucent Card
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, w, h, 18, 18);

                // Subtle Glowing Border
                g2.setColor(Theme.BORDER_CARD);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);

                // Title
                g2.setColor(Theme.TEXT_WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                g2.drawString("Welcome to the", 30, 50);
                g2.drawString("Satellite Mission and Orbit", 30, 80);
                g2.drawString("Data Management System", 30, 110);

                // Description
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.FONT_BODY);
                g2.drawString("A centralized platform to manage satellite, mission", 30, 155);
                g2.drawString("and orbit information efficiently.", 30, 178);

                g2.dispose();
            }
        };
        welcomeCard.setBounds(50, 180, 460, 290);
        welcomeCard.setLayout(null);
        welcomeCard.setOpaque(false);

        // Login Button
        CustomButton btnLogin = new CustomButton("➔  Login", CustomButton.Style.PRIMARY);
        btnLogin.setBounds(30, 215, 150, 42);
        btnLogin.setFont(Theme.FONT_BODY_BOLD);
        btnLogin.addActionListener(e -> NavigationManager.navigate(LandingFrame.this, "login"));
        welcomeCard.add(btnLogin);

        bgPanel.add(welcomeCard);

        setContentPane(bgPanel);
    }
}
