package gui;

import gui.components.BackgroundPanel;
import gui.components.CustomButton;
import gui.components.DbConfigDialog;
import gui.components.ModernTable;
import gui.components.SidebarPanel;
import gui.components.TopBarPanel;
import gui.theme.Theme;
import model.Orbit;
import model.Satellite;
import model.User;
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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

/**
 * Orbit Management Screen matching middle-right design in the reference UI.
 */
public class OrbitFrame extends JFrame {

    private final User currentUser;
    private final OrbitService orbitService = new OrbitService();
    private final SatelliteService satelliteService = new SatelliteService();

    // Form inputs
    private JTextField txtOrbitId;
    private JComboBox<String> cmbSatelliteId;
    private JComboBox<String> cmbOrbitType;
    private JTextField txtAltitude;
    private JTextField txtInclination;
    private JTextField txtPeriod;
    private JTextField txtEccentricity;
    private JTextField txtSearch;

    // Table
    private ModernTable table;
    private DefaultTableModel tableModel;

    public OrbitFrame(User user) {
        this.currentUser = user != null ? user : NavigationManager.getCurrentUser();

        setTitle("Orbit Management - Satellite Mission and Orbit Data Management System");
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
                NavigationManager.navigate(OrbitFrame.this, "exit");
            }

            @Override
            public void onDbSettingsClicked() {
                DbConfigDialog dialog = new DbConfigDialog(OrbitFrame.this);
                dialog.setVisible(true);
                loadSatellitesIntoComboBox();
                loadTableData();
            }
        });
        rootPanel.add(topBar, BorderLayout.NORTH);

        // Body Panel
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(Theme.BG_DARK);

        SidebarPanel sidebar = new SidebarPanel("Orbits", target -> NavigationManager.navigate(OrbitFrame.this, target));
        bodyPanel.add(sidebar, BorderLayout.WEST);

        BackgroundPanel contentPanel = new BackgroundPanel("assets/space_earth_bg.jpg", 0.65f);
        contentPanel.setLayout(null);

        // Header Title
        JLabel lblHeader = new JLabel("Orbit Management");
        lblHeader.setFont(Theme.FONT_TITLE);
        lblHeader.setForeground(Theme.TEXT_WHITE);
        lblHeader.setBounds(30, 15, 300, 30);
        contentPanel.add(lblHeader);

        // Form Container Panel
        JPanel formCard = new JPanel() {
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
        formCard.setBounds(30, 50, 890, 230);
        formCard.setLayout(null);
        formCard.setOpaque(false);

        // Row 1: Orbit ID, Satellite ID, Orbit Type
        int colW = 265;
        int gapX = 22;
        int x1 = 20;
        int x2 = x1 + colW + gapX;
        int x3 = x2 + colW + gapX;

        formCard.add(createLabel("Orbit ID", x1, 10));
        txtOrbitId = createTextField("e.g. ORB001");
        txtOrbitId.setBounds(x1, 30, colW, 34);
        formCard.add(txtOrbitId);

        formCard.add(createLabel("Satellite ID", x2, 10));
        cmbSatelliteId = new JComboBox<>();
        styleComboBox(cmbSatelliteId);
        cmbSatelliteId.setBounds(x2, 30, colW, 34);
        formCard.add(cmbSatelliteId);

        formCard.add(createLabel("Orbit Type", x3, 10));
        cmbOrbitType = new JComboBox<>(new String[]{
                "Select Type", "LEO", "GEO", "MEO", "SSO", "HEO", "Polar", "Halo Orbit"
        });
        styleComboBox(cmbOrbitType);
        cmbOrbitType.setBounds(x3, 30, colW, 34);
        formCard.add(cmbOrbitType);

        // Row 2: Altitude, Inclination, Period
        int r2Y = 68;
        formCard.add(createLabel("Altitude (km)", x1, r2Y));
        txtAltitude = createTextField("e.g. 500");
        txtAltitude.setBounds(x1, r2Y + 20, colW, 34);
        formCard.add(txtAltitude);

        formCard.add(createLabel("Inclination (°)", x2, r2Y));
        txtInclination = createTextField("e.g. 98.7");
        txtInclination.setBounds(x2, r2Y + 20, colW, 34);
        formCard.add(txtInclination);

        formCard.add(createLabel("Orbital Period (min)", x3, r2Y));
        txtPeriod = createTextField("e.g. 94.5");
        txtPeriod.setBounds(x3, r2Y + 20, colW, 34);
        formCard.add(txtPeriod);

        // Row 3: Eccentricity
        int r3Y = 126;
        formCard.add(createLabel("Eccentricity (0.0 to 0.999)", x1, r3Y));
        txtEccentricity = createTextField("e.g. 0.001");
        txtEccentricity.setBounds(x1, r3Y + 20, colW, 34);
        formCard.add(txtEccentricity);

        // Row 4: Buttons & Search
        int r4Y = 180;
        CustomButton btnAdd = new CustomButton("+ Add", CustomButton.Style.PRIMARY);
        btnAdd.setBounds(x1, r4Y, 85, 34);
        btnAdd.addActionListener(e -> addOrbit());
        formCard.add(btnAdd);

        CustomButton btnUpdate = new CustomButton("Update", CustomButton.Style.SUCCESS);
        btnUpdate.setBounds(x1 + 95, r4Y, 85, 34);
        btnUpdate.addActionListener(e -> updateOrbit());
        formCard.add(btnUpdate);

        CustomButton btnDelete = new CustomButton("Delete", CustomButton.Style.DANGER);
        btnDelete.setBounds(x1 + 190, r4Y, 85, 34);
        btnDelete.addActionListener(e -> deleteOrbit());
        formCard.add(btnDelete);

        CustomButton btnClear = new CustomButton("Clear", CustomButton.Style.SECONDARY);
        btnClear.setBounds(x1 + 285, r4Y, 85, 34);
        btnClear.addActionListener(e -> clearForm());
        formCard.add(btnClear);

        // Search Field & Button
        txtSearch = createTextField("Search by ID or Satellite");
        txtSearch.setBounds(x3 - 50, r4Y, 190, 34);
        formCard.add(txtSearch);

        CustomButton btnSearch = new CustomButton("Search", CustomButton.Style.PRIMARY);
        btnSearch.setBounds(x3 + 150, r4Y, 85, 34);
        btnSearch.addActionListener(e -> searchOrbits());
        formCard.add(btnSearch);

        contentPanel.add(formCard);

        // Table Panel
        String[] columns = {"Orbit ID", "Satellite ID", "Orbit Type", "Altitude (km)", "Inclination (°)", "Period (min)", "Eccentricity"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new ModernTable(tableModel);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtOrbitId.setText(tableModel.getValueAt(row, 0).toString());
                    cmbSatelliteId.setSelectedItem(tableModel.getValueAt(row, 1).toString());
                    cmbOrbitType.setSelectedItem(tableModel.getValueAt(row, 2).toString());
                    txtAltitude.setText(tableModel.getValueAt(row, 3).toString());
                    txtInclination.setText(tableModel.getValueAt(row, 4).toString());
                    txtPeriod.setText(tableModel.getValueAt(row, 5).toString());
                    txtEccentricity.setText(tableModel.getValueAt(row, 6).toString());
                }
            }
        });

        JScrollPane scrollPane = ModernTable.createScrollPane(table);
        scrollPane.setBounds(30, 295, 890, 325);
        contentPanel.add(scrollPane);

        bodyPanel.add(contentPanel, BorderLayout.CENTER);
        rootPanel.add(bodyPanel, BorderLayout.CENTER);
        setContentPane(rootPanel);

        loadSatellitesIntoComboBox();
        loadTableData();
    }

    private void loadSatellitesIntoComboBox() {
        cmbSatelliteId.removeAllItems();
        cmbSatelliteId.addItem("Select Satellite");
        try {
            List<Satellite> sats = satelliteService.getAllSatellites();
            for (Satellite s : sats) {
                cmbSatelliteId.addItem(s.getSatelliteId());
            }
        } catch (Exception ignored) {}
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        try {
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching orbits: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addOrbit() {
        try {
            String orbitId = txtOrbitId.getText().trim();
            String satId = (String) cmbSatelliteId.getSelectedItem();
            String type = (String) cmbOrbitType.getSelectedItem();
            double alt = ValidationUtil.parseDouble(txtAltitude.getText(), "Altitude");
            double inc = ValidationUtil.parseDouble(txtInclination.getText(), "Inclination");
            double period = ValidationUtil.parseDouble(txtPeriod.getText(), "Orbital Period");
            double ecc = ValidationUtil.parseDouble(txtEccentricity.getText(), "Eccentricity");

            Orbit o = new Orbit(orbitId, satId, type, alt, inc, period, ecc);
            boolean ok = orbitService.addOrbit(o);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Orbit '" + orbitId + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
                clearForm();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateOrbit() {
        try {
            String orbitId = txtOrbitId.getText().trim();
            String satId = (String) cmbSatelliteId.getSelectedItem();
            String type = (String) cmbOrbitType.getSelectedItem();
            double alt = ValidationUtil.parseDouble(txtAltitude.getText(), "Altitude");
            double inc = ValidationUtil.parseDouble(txtInclination.getText(), "Inclination");
            double period = ValidationUtil.parseDouble(txtPeriod.getText(), "Orbital Period");
            double ecc = ValidationUtil.parseDouble(txtEccentricity.getText(), "Eccentricity");

            Orbit o = new Orbit(orbitId, satId, type, alt, inc, period, ecc);
            boolean ok = orbitService.updateOrbit(o);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Orbit '" + orbitId + "' updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteOrbit() {
        String id = txtOrbitId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select or enter an Orbit ID to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete orbit '" + id + "'?",
                "Delete Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = orbitService.deleteOrbit(id);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Orbit '" + id + "' deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Orbit not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting orbit: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchOrbits() {
        String keyword = txtSearch.getText().trim();
        if (keyword.equalsIgnoreCase("Search by ID or Satellite") || keyword.isEmpty()) {
            loadTableData();
            return;
        }
        try {
            List<Orbit> results = orbitService.searchOrbits(keyword);
            tableModel.setRowCount(0);
            for (Orbit o : results) {
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
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtOrbitId.setText("");
        cmbSatelliteId.setSelectedIndex(0);
        cmbOrbitType.setSelectedIndex(0);
        txtAltitude.setText("");
        txtInclination.setText("");
        txtPeriod.setText("");
        txtEccentricity.setText("");
        txtSearch.setText("");
        table.clearSelection();
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
