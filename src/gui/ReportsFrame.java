package gui;

import gui.components.BackgroundPanel;
import gui.components.CustomButton;
import gui.components.DbConfigDialog;
import gui.components.ModernTable;
import gui.components.SidebarPanel;
import gui.components.TopBarPanel;
import gui.theme.Theme;
import model.Mission;
import model.Orbit;
import model.Satellite;
import model.User;
import service.MissionService;
import service.OrbitService;
import service.SatelliteService;
import util.ValidationUtil;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

/**
 * Reports and Data Viewing Screen matching the bottom-middle design in the reference UI.
 * Provides reports generation across satellites, missions, orbits, and CSV export.
 */
public class ReportsFrame extends JFrame {

    private final User currentUser;
    private final SatelliteService satelliteService = new SatelliteService();
    private final MissionService missionService = new MissionService();
    private final OrbitService orbitService = new OrbitService();

    private JComboBox<String> cmbReportType;
    private ModernTable table;
    private DefaultTableModel tableModel;

    public ReportsFrame(User user) {
        this.currentUser = user != null ? user : NavigationManager.getCurrentUser();

        setTitle("Reports / View Data - Satellite Mission and Orbit Data Management System");
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
                NavigationManager.navigate(ReportsFrame.this, "exit");
            }

            @Override
            public void onDbSettingsClicked() {
                DbConfigDialog dialog = new DbConfigDialog(ReportsFrame.this);
                dialog.setVisible(true);
                generateReport();
            }
        });
        rootPanel.add(topBar, BorderLayout.NORTH);

        // Body Panel
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(Theme.BG_DARK);

        SidebarPanel sidebar = new SidebarPanel("Reports", target -> NavigationManager.navigate(ReportsFrame.this, target));
        bodyPanel.add(sidebar, BorderLayout.WEST);

        BackgroundPanel contentPanel = new BackgroundPanel("assets/space_earth_bg.jpg", 0.65f);
        contentPanel.setLayout(null);

        // Header Title
        JLabel lblHeader = new JLabel("Reports / View Data");
        lblHeader.setFont(Theme.FONT_TITLE);
        lblHeader.setForeground(Theme.TEXT_WHITE);
        lblHeader.setBounds(30, 15, 300, 30);
        contentPanel.add(lblHeader);

        // Filter / Report Selector Card
        JPanel filterCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(Theme.BORDER_CARD);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        filterCard.setBounds(30, 50, 890, 80);
        filterCard.setLayout(null);
        filterCard.setOpaque(false);

        JLabel lblSelect = new JLabel("Select Report Type");
        lblSelect.setFont(Theme.FONT_SMALL_BOLD);
        lblSelect.setForeground(Theme.TEXT_MUTED);
        lblSelect.setBounds(25, 12, 200, 20);
        filterCard.add(lblSelect);

        cmbReportType = new JComboBox<>(new String[]{
                "All Satellites",
                "Active Satellites Only",
                "Inactive / Decommissioned Satellites",
                "All Space Missions",
                "Active Missions Only",
                "All Orbital Telemetry"
        });
        styleComboBox(cmbReportType);
        cmbReportType.setBounds(25, 32, 320, 36);
        filterCard.add(cmbReportType);

        CustomButton btnView = new CustomButton("👁  View", CustomButton.Style.PRIMARY);
        btnView.setBounds(365, 32, 100, 36);
        btnView.addActionListener(e -> generateReport());
        filterCard.add(btnView);

        contentPanel.add(filterCard);

        // Table Panel
        String[] columns = {"ID", "Name", "Type", "Country/Org", "Launch Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new ModernTable(tableModel);
        table.setStatusColumn(5);

        JScrollPane scrollPane = ModernTable.createScrollPane(table);
        scrollPane.setBounds(30, 145, 890, 420);
        contentPanel.add(scrollPane);

        // Bottom Action Button: Export to CSV
        CustomButton btnExport = new CustomButton("⬇  Export to CSV", CustomButton.Style.PRIMARY);
        btnExport.setBounds(30, 580, 170, 38);
        btnExport.addActionListener(e -> exportToCsv());
        contentPanel.add(btnExport);

        bodyPanel.add(contentPanel, BorderLayout.CENTER);
        rootPanel.add(bodyPanel, BorderLayout.CENTER);
        setContentPane(rootPanel);

        // Generate default report
        generateReport();
    }

    private void generateReport() {
        String report = (String) cmbReportType.getSelectedItem();
        if (report == null) return;

        try {
            if (report.contains("Mission")) {
                setupMissionTable();
                List<Mission> list = missionService.getAllMissions();
                for (Mission m : list) {
                    if (report.contains("Active") && !m.isActive()) continue;
                    tableModel.addRow(new Object[]{
                            m.getMissionId(),
                            m.getMissionName(),
                            m.getMissionType(),
                            m.getObjective(),
                            ValidationUtil.formatDate(m.getLaunchDate()),
                            m.getMissionStatus(),
                            m.getSatelliteId()
                    });
                }
            } else if (report.contains("Orbital")) {
                setupOrbitTable();
                List<Orbit> list = orbitService.getAllOrbits();
                for (Orbit o : list) {
                    tableModel.addRow(new Object[]{
                            o.getOrbitId(),
                            o.getSatelliteId(),
                            o.getOrbitType(),
                            o.getAltitude(),
                            o.getInclination(),
                            o.getOrbitalPeriod(),
                            o.getEccentricity()
                    });
                }
            } else {
                setupSatelliteTable();
                List<Satellite> list = satelliteService.getAllSatellites();
                for (Satellite s : list) {
                    if (report.contains("Active Satellites") && !s.isActive()) continue;
                    if (report.contains("Inactive") && s.isActive()) continue;
                    tableModel.addRow(new Object[]{
                            s.getSatelliteId(),
                            s.getSatelliteName(),
                            s.getSatelliteType(),
                            s.getCountryOrg(),
                            ValidationUtil.formatDate(s.getLaunchDate()),
                            s.getOperationalStatus()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to generate report: " + ex.getMessage(), "Report Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupSatelliteTable() {
        String[] cols = {"ID", "Name", "Type", "Country/Org", "Launch Date", "Status"};
        tableModel.setDataVector(new Object[][]{}, cols);
        table.setStatusColumn(5);
    }

    private void setupMissionTable() {
        String[] cols = {"Mission ID", "Mission Name", "Type", "Objective", "Launch Date", "Status", "Satellite ID"};
        tableModel.setDataVector(new Object[][]{}, cols);
        table.setStatusColumn(5);
    }

    private void setupOrbitTable() {
        String[] cols = {"Orbit ID", "Satellite ID", "Orbit Type", "Altitude (km)", "Inclination (°)", "Period (min)", "Eccentricity"};
        tableModel.setDataVector(new Object[][]{}, cols);
    }

    private void exportToCsv() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No records to export.", "Empty Report", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Report as CSV");
        chooser.setSelectedFile(new File("satellite_report_" + System.currentTimeMillis() + ".csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                // Header
                int colCount = tableModel.getColumnCount();
                for (int c = 0; c < colCount; c++) {
                    pw.print(escapeCsv(tableModel.getColumnName(c)));
                    if (c < colCount - 1) pw.print(",");
                }
                pw.println();

                // Rows
                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    for (int c = 0; c < colCount; c++) {
                        Object val = tableModel.getValueAt(r, c);
                        pw.print(escapeCsv(val != null ? val.toString() : ""));
                        if (c < colCount - 1) pw.print(",");
                    }
                    pw.println();
                }

                JOptionPane.showMessageDialog(this, "Report successfully exported to:\n" + file.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to export CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String escapeCsv(String str) {
        if (str == null) return "\"\"";
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return "\"" + str + "\"";
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setFont(Theme.FONT_BODY);
        cb.setBackground(Theme.BG_INPUT);
        cb.setForeground(Theme.TEXT_WHITE);
        cb.setBorder(BorderFactory.createLineBorder(Theme.BORDER_INPUT, 1));
    }
}
