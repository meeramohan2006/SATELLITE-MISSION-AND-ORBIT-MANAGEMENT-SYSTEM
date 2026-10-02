package gui;

import gui.components.BackgroundPanel;
import gui.components.CustomButton;
import gui.components.DbConfigDialog;
import gui.components.ModernTable;
import gui.components.SidebarPanel;
import gui.components.TopBarPanel;
import gui.theme.Theme;
import model.Mission;
import model.Satellite;
import model.User;
import service.MissionService;
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
import java.time.LocalDate;
import java.util.List;

/**
 * Mission Management Screen matching middle-center design in the reference UI.
 */
public class MissionFrame extends JFrame {

    private final User currentUser;
    private final MissionService missionService = new MissionService();
    private final SatelliteService satelliteService = new SatelliteService();

    // Form inputs
    private JTextField txtId;
    private JTextField txtName;
    private JComboBox<String> cmbType;
    private JTextField txtObjective;
    private JTextField txtLaunchDate;
    private JComboBox<String> cmbStatus;
    private JComboBox<String> cmbSatelliteId;
    private JTextField txtSearch;

    // Table
    private ModernTable table;
    private DefaultTableModel tableModel;

    public MissionFrame(User user) {
        this.currentUser = user != null ? user : NavigationManager.getCurrentUser();

        setTitle("Mission Management - Satellite Mission and Orbit Data Management System");
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
                NavigationManager.navigate(MissionFrame.this, "exit");
            }

            @Override
            public void onDbSettingsClicked() {
                DbConfigDialog dialog = new DbConfigDialog(MissionFrame.this);
                dialog.setVisible(true);
                loadSatellitesIntoComboBox();
                loadTableData();
            }
        });
        rootPanel.add(topBar, BorderLayout.NORTH);

        // Body Panel
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(Theme.BG_DARK);

        SidebarPanel sidebar = new SidebarPanel("Missions", target -> NavigationManager.navigate(MissionFrame.this, target));
        bodyPanel.add(sidebar, BorderLayout.WEST);

        BackgroundPanel contentPanel = new BackgroundPanel("assets/space_earth_bg.jpg", 0.65f);
        contentPanel.setLayout(null);

        // Header Title
        JLabel lblHeader = new JLabel("Mission Management");
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

        // Row 1: Mission ID, Name, Type
        int colW = 265;
        int gapX = 22;
        int x1 = 20;
        int x2 = x1 + colW + gapX;
        int x3 = x2 + colW + gapX;

        formCard.add(createLabel("Mission ID", x1, 10));
        txtId = createTextField("e.g. MIS001");
        txtId.setBounds(x1, 30, colW, 34);
        formCard.add(txtId);

        formCard.add(createLabel("Mission Name", x2, 10));
        txtName = createTextField("e.g. Chandrayaan-3");
        txtName.setBounds(x2, 30, colW, 34);
        formCard.add(txtName);

        formCard.add(createLabel("Mission Type", x3, 10));
        cmbType = new JComboBox<>(new String[]{
                "Select Type", "Lunar Exploration", "Planetary", "Communication", "Research", "Earth Observation", "Meteorological"
        });
        styleComboBox(cmbType);
        cmbType.setBounds(x3, 30, colW, 34);
        formCard.add(cmbType);

        // Row 2: Objective, Launch Date, Status
        int r2Y = 68;
        formCard.add(createLabel("Mission Objective", x1, r2Y));
        txtObjective = createTextField("e.g. Lunar South Pole exploration");
        txtObjective.setBounds(x1, r2Y + 20, colW, 34);
        formCard.add(txtObjective);

        formCard.add(createLabel("Launch Date", x2, r2Y));
        txtLaunchDate = createTextField("dd-mm-yyyy");
        txtLaunchDate.setBounds(x2, r2Y + 20, colW, 34);
        formCard.add(txtLaunchDate);

        formCard.add(createLabel("Mission Status", x3, r2Y));
        cmbStatus = new JComboBox<>(new String[]{
                "Select Status", "Active", "Completed", "Inactive", "Planned"
        });
        styleComboBox(cmbStatus);
        cmbStatus.setBounds(x3, r2Y + 20, colW, 34);
        formCard.add(cmbStatus);

        // Row 3: Satellite ID Selector
        int r3Y = 126;
        formCard.add(createLabel("Assigned Satellite ID", x1, r3Y));
        cmbSatelliteId = new JComboBox<>();
        styleComboBox(cmbSatelliteId);
        cmbSatelliteId.setBounds(x1, r3Y + 20, colW, 34);
        formCard.add(cmbSatelliteId);

        // Row 4: Buttons & Search
        int r4Y = 180;
        CustomButton btnAdd = new CustomButton("+ Add", CustomButton.Style.PRIMARY);
        btnAdd.setBounds(x1, r4Y, 85, 34);
        btnAdd.addActionListener(e -> addMission());
        formCard.add(btnAdd);

        CustomButton btnUpdate = new CustomButton("Update", CustomButton.Style.SUCCESS);
        btnUpdate.setBounds(x1 + 95, r4Y, 85, 34);
        btnUpdate.addActionListener(e -> updateMission());
        formCard.add(btnUpdate);

        CustomButton btnDelete = new CustomButton("Delete", CustomButton.Style.DANGER);
        btnDelete.setBounds(x1 + 190, r4Y, 85, 34);
        btnDelete.addActionListener(e -> deleteMission());
        formCard.add(btnDelete);

        CustomButton btnClear = new CustomButton("Clear", CustomButton.Style.SECONDARY);
        btnClear.setBounds(x1 + 285, r4Y, 85, 34);
        btnClear.addActionListener(e -> clearForm());
        formCard.add(btnClear);

        // Search Field & Button
        txtSearch = createTextField("Search by Name, Type, or ID");
        txtSearch.setBounds(x3 - 50, r4Y, 190, 34);
        formCard.add(txtSearch);

        CustomButton btnSearch = new CustomButton("Search", CustomButton.Style.PRIMARY);
        btnSearch.setBounds(x3 + 150, r4Y, 85, 34);
        btnSearch.addActionListener(e -> searchMissions());
        formCard.add(btnSearch);

        contentPanel.add(formCard);

        // Table Panel
        String[] columns = {"ID", "Name", "Type", "Objective", "Launch Date", "Status", "Satellite ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new ModernTable(tableModel);
        table.setStatusColumn(5); // column 5 is "Status"

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtId.setText(tableModel.getValueAt(row, 0).toString());
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    cmbType.setSelectedItem(tableModel.getValueAt(row, 2).toString());
                    txtObjective.setText(tableModel.getValueAt(row, 3).toString());
                    txtLaunchDate.setText(tableModel.getValueAt(row, 4).toString());
                    cmbStatus.setSelectedItem(tableModel.getValueAt(row, 5).toString());
                    cmbSatelliteId.setSelectedItem(tableModel.getValueAt(row, 6).toString());
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
            List<Mission> list = missionService.getAllMissions();
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching missions: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMission() {
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String type = (String) cmbType.getSelectedItem();
            String objective = txtObjective.getText().trim();
            String dateStr = txtLaunchDate.getText().trim();
            String status = (String) cmbStatus.getSelectedItem();
            String satId = (String) cmbSatelliteId.getSelectedItem();

            LocalDate date = ValidationUtil.parseDate(dateStr);
            Mission m = new Mission(id, name, type, objective, date, status, satId);

            boolean ok = missionService.addMission(m);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Mission '" + id + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
                clearForm();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMission() {
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String type = (String) cmbType.getSelectedItem();
            String objective = txtObjective.getText().trim();
            String dateStr = txtLaunchDate.getText().trim();
            String status = (String) cmbStatus.getSelectedItem();
            String satId = (String) cmbSatelliteId.getSelectedItem();

            LocalDate date = ValidationUtil.parseDate(dateStr);
            Mission m = new Mission(id, name, type, objective, date, status, satId);

            boolean ok = missionService.updateMission(m);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Mission '" + id + "' updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMission() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select or enter a Mission ID to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete mission '" + id + "'?",
                "Delete Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = missionService.deleteMission(id);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Mission '" + id + "' deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Mission not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting mission: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchMissions() {
        String keyword = txtSearch.getText().trim();
        if (keyword.equalsIgnoreCase("Search by Name, Type, or ID") || keyword.isEmpty()) {
            loadTableData();
            return;
        }
        try {
            List<Mission> results = missionService.searchMissions(keyword);
            tableModel.setRowCount(0);
            for (Mission m : results) {
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
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        cmbType.setSelectedIndex(0);
        txtObjective.setText("");
        txtLaunchDate.setText("");
        cmbStatus.setSelectedIndex(0);
        cmbSatelliteId.setSelectedIndex(0);
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
