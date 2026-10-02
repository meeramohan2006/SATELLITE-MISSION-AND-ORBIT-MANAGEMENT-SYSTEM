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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

/**
 * Universal Search Screen matching bottom-left design in the reference UI.
 * Allows searching across Satellites, Missions, and Orbits.
 */
public class SearchFrame extends JFrame {

    private final User currentUser;
    private final SatelliteService satelliteService = new SatelliteService();
    private final MissionService missionService = new MissionService();
    private final OrbitService orbitService = new OrbitService();

    private JComboBox<String> cmbSearchBy;
    private JTextField txtKeyword;
    private ModernTable table;
    private DefaultTableModel tableModel;

    public SearchFrame(User user) {
        this.currentUser = user != null ? user : NavigationManager.getCurrentUser();

        setTitle("Search - Satellite Mission and Orbit Data Management System");
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
                NavigationManager.navigate(SearchFrame.this, "exit");
            }

            @Override
            public void onDbSettingsClicked() {
                DbConfigDialog dialog = new DbConfigDialog(SearchFrame.this);
                dialog.setVisible(true);
                executeSearch();
            }
        });
        rootPanel.add(topBar, BorderLayout.NORTH);

        // Body Panel
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(Theme.BG_DARK);

        SidebarPanel sidebar = new SidebarPanel("Search", target -> NavigationManager.navigate(SearchFrame.this, target));
        bodyPanel.add(sidebar, BorderLayout.WEST);

        BackgroundPanel contentPanel = new BackgroundPanel("assets/space_earth_bg.jpg", 0.65f);
        contentPanel.setLayout(null);

        // Header Title
        JLabel lblHeader = new JLabel("Search");
        lblHeader.setFont(Theme.FONT_TITLE);
        lblHeader.setForeground(Theme.TEXT_WHITE);
        lblHeader.setBounds(30, 20, 200, 30);
        contentPanel.add(lblHeader);

        // Search Criteria Panel
        JPanel searchCard = new JPanel() {
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
        searchCard.setBounds(30, 60, 890, 130);
        searchCard.setLayout(null);
        searchCard.setOpaque(false);

        // "Search By" Dropdown
        searchCard.add(createLabel("Search By", 25, 15));
        cmbSearchBy = new JComboBox<>(new String[]{
                "Satellite (All Fields)", "Satellite Name", "Satellite ID", "Mission Name", "Orbit Type"
        });
        styleComboBox(cmbSearchBy);
        cmbSearchBy.setBounds(25, 38, 250, 36);
        searchCard.add(cmbSearchBy);

        // "Enter Keyword" Field
        searchCard.add(createLabel("Enter Keyword", 300, 15));
        txtKeyword = createTextField("e.g. INSAT");
        txtKeyword.setBounds(300, 38, 300, 36);
        txtKeyword.addActionListener(e -> executeSearch());
        searchCard.add(txtKeyword);

        // Buttons
        CustomButton btnSearch = new CustomButton("🔍  Search", CustomButton.Style.PRIMARY);
        btnSearch.setBounds(625, 38, 110, 36);
        btnSearch.addActionListener(e -> executeSearch());
        searchCard.add(btnSearch);

        CustomButton btnClear = new CustomButton("Clear", CustomButton.Style.SECONDARY);
        btnClear.setBounds(745, 38, 110, 36);
        btnClear.addActionListener(e -> {
            txtKeyword.setText("");
            cmbSearchBy.setSelectedIndex(0);
            executeSearch();
        });
        searchCard.add(btnClear);

        contentPanel.add(searchCard);

        // Results Table Panel
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
        scrollPane.setBounds(30, 210, 890, 410);
        contentPanel.add(scrollPane);

        bodyPanel.add(contentPanel, BorderLayout.CENTER);
        rootPanel.add(bodyPanel, BorderLayout.CENTER);
        setContentPane(rootPanel);

        // Initial search load
        executeSearch();
    }

    private void executeSearch() {
        String category = (String) cmbSearchBy.getSelectedItem();
        String keyword = txtKeyword.getText().trim();
        if ("e.g. INSAT".equalsIgnoreCase(keyword)) keyword = "";

        try {
            if (category != null && category.startsWith("Mission")) {
                setupMissionTable();
                List<Mission> list = missionService.searchMissions(keyword);
                for (Mission m : list) {
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
            } else if (category != null && category.startsWith("Orbit")) {
                setupOrbitTable();
                List<Orbit> list = orbitService.searchOrbits(keyword);
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
                List<Satellite> list = satelliteService.searchSatellites(keyword);
                for (Satellite s : list) {
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
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private JLabel createLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_SMALL_BOLD);
        lbl.setForeground(Theme.TEXT_MUTED);
        lbl.setBounds(x, y, 200, 20);
        return lbl;
    }

    private JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(Theme.FONT_BODY);
        tf.setBackground(Theme.BG_INPUT);
        tf.setForeground(Theme.TEXT_WHITE);
        tf.setCaretColor(Theme.TEXT_WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_INPUT, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setFont(Theme.FONT_BODY);
        cb.setBackground(Theme.BG_INPUT);
        cb.setForeground(Theme.TEXT_WHITE);
        cb.setBorder(BorderFactory.createLineBorder(Theme.BORDER_INPUT, 1));
    }
}
