package gui;

import gui.components.BackgroundPanel;
import gui.components.CustomButton;
import gui.components.DbConfigDialog;
import gui.components.SidebarPanel;
import gui.components.StatCard;
import gui.components.TopBarPanel;
import gui.theme.Theme;
import model.User;
import service.MissionService;
import service.OrbitService;
import service.SatelliteService;

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
 * Dashboard Screen matching the top-right visual design in the reference UI.
 * Displays metric stat cards, space hero banner, and quick navigation.
 */
public class DashboardFrame extends JFrame {

    private final User currentUser;
    private final SatelliteService satelliteService = new SatelliteService();
    private final MissionService missionService = new MissionService();
    private final OrbitService orbitService = new OrbitService();

    private StatCard cardTotalSatellites;
    private StatCard cardActiveSatellites;
    private StatCard cardTotalMissions;
    private StatCard cardActiveMissions;

    public DashboardFrame(User user) {
        this.currentUser = user != null ? user : NavigationManager.getCurrentUser();

        setTitle("Dashboard - Satellite Mission and Orbit Data Management System");
        setSize(1180, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Theme.BG_DARK);

        // Top Bar
        TopBarPanel topBar = new TopBarPanel("Satellite Mission and Orbit Data Management System", currentUser, new TopBarPanel.TopBarActionListener() {
            @Override
            public void onExitClicked() {
                NavigationManager.navigate(DashboardFrame.this, "exit");
            }

            @Override
            public void onDbSettingsClicked() {
                DbConfigDialog dialog = new DbConfigDialog(DashboardFrame.this);
                dialog.setVisible(true);
                refreshStats();
            }
        });
        rootPanel.add(topBar, BorderLayout.NORTH);

        // Center Content Area: Sidebar on West, Main Body in Center
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(Theme.BG_DARK);

        // Left Sidebar
        SidebarPanel sidebar = new SidebarPanel("Dashboard", target -> NavigationManager.navigate(DashboardFrame.this, target));
        bodyPanel.add(sidebar, BorderLayout.WEST);

        // Dashboard Main Content Panel
        BackgroundPanel contentPanel = new BackgroundPanel("assets/space_earth_bg.jpg", 0.55f);
        contentPanel.setLayout(null);

        // Dashboard Section Title
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.TEXT_WHITE);
                g2.setFont(Theme.FONT_TITLE);
                g2.drawString("Dashboard", 0, 24);
                g2.dispose();
            }
        };
        titlePanel.setBounds(30, 20, 250, 35);
        titlePanel.setOpaque(false);
        contentPanel.add(titlePanel);

        // 4 Stat Cards in a row
        int cardY = 65;
        int cardW = 210;
        int cardSpacing = 16;
        int startX = 30;

        cardTotalSatellites = new StatCard("Total Satellites", 0, "🛰", Theme.PRIMARY_BLUE);
        cardTotalSatellites.setBounds(startX, cardY, cardW, 110);
        contentPanel.add(cardTotalSatellites);

        cardActiveSatellites = new StatCard("Active Satellites", 0, "✓", Theme.SUCCESS_GREEN);
        cardActiveSatellites.setBounds(startX + (cardW + cardSpacing), cardY, cardW, 110);
        contentPanel.add(cardActiveSatellites);

        cardTotalMissions = new StatCard("Total Missions", 0, "🚀", Theme.PURPLE_ACCENT);
        cardTotalMissions.setBounds(startX + (cardW + cardSpacing) * 2, cardY, cardW, 110);
        contentPanel.add(cardTotalMissions);

        cardActiveMissions = new StatCard("Active Missions", 0, "📈", Theme.WARNING_ORANGE);
        cardActiveMissions.setBounds(startX + (cardW + cardSpacing) * 3, cardY, cardW, 110);
        contentPanel.add(cardActiveMissions);

        // Hero Banner Panel (matching bottom half of dashboard in reference image)
        JPanel heroPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Dark Translucent Glass Card
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, w, h, 18, 18);

                // Border
                g2.setColor(Theme.BORDER_CARD);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);

                // Earth curvature graphic at right
                g2.setColor(new Color(0, 150, 255, 30));
                g2.fillOval(w - 320, h - 180, 400, 300);

                // Title: Explore. Manage. Monitor.
                g2.setColor(Theme.TEXT_WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
                g2.drawString("Explore. Manage. Monitor.", 35, 60);

                // Subtitle
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.FONT_BODY);
                g2.drawString("Satellite Mission and Orbit Data Management System", 35, 90);
                g2.drawString("Comprehensive mission oversight, constellation tracking, and orbital telemetry.", 35, 115);

                g2.dispose();
            }
        };
        heroPanel.setBounds(30, 200, 890, 420);
        heroPanel.setLayout(null);
        heroPanel.setOpaque(false);

        // Quick Navigation Buttons inside Hero Panel
        int btnY = 160;
        int btnW = 190;
        int btnH = 42;
        int btnGap = 20;

        CustomButton btnSat = new CustomButton("🛰  Manage Satellites", CustomButton.Style.PRIMARY);
        btnSat.setBounds(35, btnY, btnW, btnH);
        btnSat.addActionListener(e -> NavigationManager.navigate(DashboardFrame.this, "satellites"));
        heroPanel.add(btnSat);

        CustomButton btnMis = new CustomButton("🚀  Track Missions", CustomButton.Style.SECONDARY);
        btnMis.setBounds(35 + (btnW + btnGap), btnY, btnW, btnH);
        btnMis.addActionListener(e -> NavigationManager.navigate(DashboardFrame.this, "missions"));
        heroPanel.add(btnMis);

        CustomButton btnOrb = new CustomButton("🌐  Monitor Orbits", CustomButton.Style.SECONDARY);
        btnOrb.setBounds(35 + (btnW + btnGap) * 2, btnY, btnW, btnH);
        btnOrb.addActionListener(e -> NavigationManager.navigate(DashboardFrame.this, "orbits"));
        heroPanel.add(btnOrb);

        CustomButton btnRep = new CustomButton("📊  Generate Reports", CustomButton.Style.SECONDARY);
        btnRep.setBounds(35 + (btnW + btnGap) * 3, btnY, btnW, btnH);
        btnRep.addActionListener(e -> NavigationManager.navigate(DashboardFrame.this, "reports"));
        heroPanel.add(btnRep);

        contentPanel.add(heroPanel);
        bodyPanel.add(contentPanel, BorderLayout.CENTER);
        rootPanel.add(bodyPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);

        // Load Real Stats
        refreshStats();
    }

    private void refreshStats() {
        int totalSat = satelliteService.getTotalSatellitesCount();
        int activeSat = satelliteService.getActiveSatellitesCount();
        int totalMis = missionService.getTotalMissionsCount();
        int activeMis = missionService.getActiveMissionsCount();

        cardTotalSatellites.setValue(totalSat);
        cardActiveSatellites.setValue(activeSat);
        cardTotalMissions.setValue(totalMis);
        cardActiveMissions.setValue(activeMis);
    }
}
