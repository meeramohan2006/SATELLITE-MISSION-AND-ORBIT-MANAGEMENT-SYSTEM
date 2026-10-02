package gui;

import gui.components.BackgroundPanel;
import gui.components.CustomButton;
import gui.components.DbConfigDialog;
import gui.components.ModernTable;
import gui.components.SidebarPanel;
import gui.components.TopBarPanel;
import gui.theme.Theme;
import model.Satellite;
import model.User;
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.util.List;

/**
 * Satellite Management Screen matching middle-left design in the reference UI.
 * Provides Add, View, Update, Delete, and Search operations.
 */
public class SatelliteFrame extends JFrame {

    private final User currentUser;
    private final SatelliteService satelliteService = new SatelliteService();

    // Form inputs
    private JTextField txtId;
    private JTextField txtName;
    private JComboBox<String> cmbType;
    private JTextField txtCountry;
    private JTextField txtLaunchDate;
    private JComboBox<String> cmbStatus;
    private JTextField txtSearch;

    // Table
    private ModernTable table;
    private DefaultTableModel tableModel;

    public SatelliteFrame(User user) {
        this.currentUser = user != null ? user : NavigationManager.getCurrentUser();

        setTitle("Satellite Management - Satellite Mission and Orbit Data Management System");
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
                NavigationManager.navigate(SatelliteFrame.this, "exit");
            }

            @Override
            public void onDbSettingsClicked() {
                DbConfigDialog dialog = new DbConfigDialog(SatelliteFrame.this);
                dialog.setVisible(true);
                loadTableData();
            }
        });
        rootPanel.add(topBar, BorderLayout.NORTH);

        // Body Panel: Sidebar + Main Area
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(Theme.BG_DARK);

        SidebarPanel sidebar = new SidebarPanel("Satellites", target -> NavigationManager.navigate(SatelliteFrame.this, target));
        bodyPanel.add(sidebar, BorderLayout.WEST);

        // Content Area with Space Background
        BackgroundPanel contentPanel = new BackgroundPanel("assets/space_earth_bg.jpg", 0.65f);
        contentPanel.setLayout(null);

        // Header Title
        JLabel lblHeader = new JLabel("Satellite Management");
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
        formCard.setBounds(30, 50, 890, 210);
        formCard.setLayout(null);
        formCard.setOpaque(false);

        // Row 1: ID, Name, Type
        int colW = 265;
        int gapX = 22;
        int x1 = 20;
        int x2 = x1 + colW + gapX;
        int x3 = x2 + colW + gapX;

        formCard.add(createLabel("Satellite ID", x1, 12));
        txtId = createTextField("e.g. SAT001");
        txtId.setBounds(x1, 34, colW, 36);
        formCard.add(txtId);

        formCard.add(createLabel("Satellite Name", x2, 12));
        txtName = createTextField("e.g. INSAT-3D");
        txtName.setBounds(x2, 34, colW, 36);
        formCard.add(txtName);

        formCard.add(createLabel("Satellite Type", x3, 12));
        cmbType = new JComboBox<>(new String[]{
                "Select Type", "Communication", "Earth Observation", "Navigation", "Research", "Planetary", "Meteorological"
        });
        styleComboBox(cmbType);
        cmbType.setBounds(x3, 34, colW, 36);
        formCard.add(cmbType);

        // Row 2: Country/Org, Launch Date, Status
        int r2Y = 80;
        formCard.add(createLabel("Country/Organization", x1, r2Y));
        txtCountry = createTextField("e.g. ISRO");
        txtCountry.setBounds(x1, r2Y + 22, colW, 36);
        formCard.add(txtCountry);

        formCard.add(createLabel("Launch Date", x2, r2Y));
        txtLaunchDate = createTextField("dd-mm-yyyy");
        txtLaunchDate.setBounds(x2, r2Y + 22, colW, 36);
        formCard.add(txtLaunchDate);

        formCard.add(createLabel("Operational Status", x3, r2Y));
        cmbStatus = new JComboBox<>(new String[]{
                "Select Status", "Active", "Inactive", "Decommissioned"
        });
        styleComboBox(cmbStatus);
        cmbStatus.setBounds(x3, r2Y + 22, colW, 36);
        formCard.add(cmbStatus);

        // Row 3: Action Buttons & Search
        int r3Y = 152;
        CustomButton btnAdd = new CustomButton("+ Add", CustomButton.Style.PRIMARY);
        btnAdd.setBounds(x1, r3Y, 85, 36);
        btnAdd.addActionListener(e -> addSatellite());
        formCard.add(btnAdd);

        CustomButton btnUpdate = new CustomButton("Update", CustomButton.Style.SUCCESS);
        btnUpdate.setBounds(x1 + 95, r3Y, 85, 36);
        btnUpdate.addActionListener(e -> updateSatellite());
        formCard.add(btnUpdate);

        CustomButton btnDelete = new CustomButton("Delete", CustomButton.Style.DANGER);
        btnDelete.setBounds(x1 + 190, r3Y, 85, 36);
        btnDelete.addActionListener(e -> deleteSatellite());
        formCard.add(btnDelete);

        CustomButton btnClear = new CustomButton("Clear", CustomButton.Style.SECONDARY);
        btnClear.setBounds(x1 + 285, r3Y, 85, 36);
        btnClear.addActionListener(e -> clearForm());
        formCard.add(btnClear);

        // Search Field & Button on right of Row 3
        txtSearch = createTextField("Search by ID or Name");
        txtSearch.setBounds(x3 - 50, r3Y, 190, 36);
        formCard.add(txtSearch);

        CustomButton btnSearch = new CustomButton("Search", CustomButton.Style.PRIMARY);
        btnSearch.setBounds(x3 + 150, r3Y, 85, 36);
        btnSearch.addActionListener(e -> searchSatellites());
        formCard.add(btnSearch);

        contentPanel.add(formCard);

        // Table Panel
        String[] columns = {"ID", "Name", "Type", "Country/Org", "Launch Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new ModernTable(tableModel);
        table.setStatusColumn(5); // column 5 is "Status"

        // Selection listener to populate form fields
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtId.setText(tableModel.getValueAt(row, 0).toString());
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    cmbType.setSelectedItem(tableModel.getValueAt(row, 2).toString());
                    txtCountry.setText(tableModel.getValueAt(row, 3).toString());
                    txtLaunchDate.setText(tableModel.getValueAt(row, 4).toString());
                    cmbStatus.setSelectedItem(tableModel.getValueAt(row, 5).toString());
                }
            }
        });

        JScrollPane scrollPane = ModernTable.createScrollPane(table);
        scrollPane.setBounds(30, 275, 890, 345);
        contentPanel.add(scrollPane);

        bodyPanel.add(contentPanel, BorderLayout.CENTER);
        rootPanel.add(bodyPanel, BorderLayout.CENTER);
        setContentPane(rootPanel);

        loadTableData();
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        try {
            List<Satellite> list = satelliteService.getAllSatellites();
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching satellites: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSatellite() {
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String type = (String) cmbType.getSelectedItem();
            String country = txtCountry.getText().trim();
            String dateStr = txtLaunchDate.getText().trim();
            String status = (String) cmbStatus.getSelectedItem();

            LocalDate date = ValidationUtil.parseDate(dateStr);
            Satellite s = new Satellite(id, name, type, country, date, status);

            boolean ok = satelliteService.addSatellite(s);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Satellite '" + id + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
                clearForm();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSatellite() {
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String type = (String) cmbType.getSelectedItem();
            String country = txtCountry.getText().trim();
            String dateStr = txtLaunchDate.getText().trim();
            String status = (String) cmbStatus.getSelectedItem();

            LocalDate date = ValidationUtil.parseDate(dateStr);
            Satellite s = new Satellite(id, name, type, country, date, status);

            boolean ok = satelliteService.updateSatellite(s);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Satellite '" + id + "' updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSatellite() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select or enter a Satellite ID to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete satellite '" + id + "'?\nThis will cascade and remove associated missions and orbits!",
                "Delete Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = satelliteService.deleteSatellite(id);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Satellite '" + id + "' deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Satellite not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting satellite: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchSatellites() {
        String keyword = txtSearch.getText().trim();
        if (keyword.equalsIgnoreCase("Search by ID or Name") || keyword.isEmpty()) {
            loadTableData();
            return;
        }
        try {
            List<Satellite> results = satelliteService.searchSatellites(keyword);
            tableModel.setRowCount(0);
            for (Satellite s : results) {
                tableModel.addRow(new Object[]{
                        s.getSatelliteId(),
                        s.getSatelliteName(),
                        s.getSatelliteType(),
                        s.getCountryOrg(),
                        ValidationUtil.formatDate(s.getLaunchDate()),
                        s.getOperationalStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        cmbType.setSelectedIndex(0);
        txtCountry.setText("");
        txtLaunchDate.setText("");
        cmbStatus.setSelectedIndex(0);
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
