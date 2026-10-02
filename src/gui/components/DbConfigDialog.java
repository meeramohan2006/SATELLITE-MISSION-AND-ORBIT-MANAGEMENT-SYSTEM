package gui.components;

import database.DBConnection;
import gui.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

/**
 * Dialog to view and configure MySQL database connection credentials.
 */
public class DbConfigDialog extends JDialog {

    private final JTextField txtHost;
    private final JTextField txtPort;
    private final JTextField txtDbName;
    private final JTextField txtUser;
    private final JPasswordField txtPass;
    private final JLabel lblStatus;

    public DbConfigDialog(JFrame parent) {
        super(parent, "MySQL Database Configuration", true);
        setSize(480, 420);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD_OPAQUE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Title
        JLabel titleLabel = new JLabel("Database Connection Settings");
        titleLabel.setFont(Theme.FONT_SUBTITLE);
        titleLabel.setForeground(Theme.TEXT_WHITE);

        // Form Fields
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 14));
        formPanel.setOpaque(false);

        txtHost = createStyledField(DBConnection.getHost());
        txtPort = createStyledField(DBConnection.getPort());
        txtDbName = createStyledField(DBConnection.getDbName());
        txtUser = createStyledField(DBConnection.getUsername());
        txtPass = new JPasswordField(DBConnection.getPassword());
        stylePasswordField(txtPass);

        formPanel.add(createFieldLabel("Host:"));
        formPanel.add(txtHost);
        formPanel.add(createFieldLabel("Port:"));
        formPanel.add(txtPort);
        formPanel.add(createFieldLabel("Database Name:"));
        formPanel.add(txtDbName);
        formPanel.add(createFieldLabel("Username:"));
        formPanel.add(txtUser);
        formPanel.add(createFieldLabel("Password:"));
        formPanel.add(txtPass);

        // Status Label
        lblStatus = new JLabel(DBConnection.isFallbackMode() ? "Status: Using offline cache (MySQL not connected)" : "Status: Connected to MySQL!");
        lblStatus.setFont(Theme.FONT_SMALL);
        lblStatus.setForeground(DBConnection.isFallbackMode() ? Theme.WARNING_ORANGE : Theme.SUCCESS_GREEN);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(lblStatus, BorderLayout.SOUTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        buttonPanel.setOpaque(false);

        CustomButton btnTest = new CustomButton("Test", CustomButton.Style.SECONDARY);
        btnTest.addActionListener(e -> testSettings());

        CustomButton btnSave = new CustomButton("Save & Connect", CustomButton.Style.PRIMARY);
        btnSave.setPreferredSize(new Dimension(140, 36));
        btnSave.addActionListener(e -> {
            saveAndApplySettings();
            dispose();
        });

        CustomButton btnCancel = new CustomButton("Close", CustomButton.Style.SECONDARY);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnTest);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void testSettings() {
        String host = txtHost.getText().trim();
        String port = txtPort.getText().trim();
        String db = txtDbName.getText().trim();
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        DBConnection.setHost(host);
        DBConnection.setPort(port);
        DBConnection.setDbName(db);
        DBConnection.setUsername(user);
        DBConnection.setPassword(pass);

        boolean ok = DBConnection.testAndInitializeDatabase();
        if (ok) {
            lblStatus.setText("Status: Connection Successful! Database ready.");
            lblStatus.setForeground(Theme.SUCCESS_GREEN);
            JOptionPane.showMessageDialog(this, "Connection to MySQL succeeded!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            lblStatus.setText("Status: " + (DBConnection.getLastError() != null ? DBConnection.getLastError() : "Failed to connect."));
            lblStatus.setForeground(Theme.DANGER_RED);
            JOptionPane.showMessageDialog(this, "Failed to connect to MySQL:\n" + DBConnection.getLastError(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAndApplySettings() {
        String host = txtHost.getText().trim();
        String port = txtPort.getText().trim();
        String db = txtDbName.getText().trim();
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        DBConnection.saveProperties(host, port, db, user, pass);
        DBConnection.testAndInitializeDatabase();
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_BODY_BOLD);
        lbl.setForeground(Theme.TEXT_MUTED);
        return lbl;
    }

    private JTextField createStyledField(String text) {
        JTextField tf = new JTextField(text);
        tf.setFont(Theme.FONT_BODY);
        tf.setBackground(Theme.BG_INPUT);
        tf.setForeground(Theme.TEXT_WHITE);
        tf.setCaretColor(Theme.TEXT_WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_INPUT, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    private void stylePasswordField(JPasswordField pf) {
        pf.setFont(Theme.FONT_BODY);
        pf.setBackground(Theme.BG_INPUT);
        pf.setForeground(Theme.TEXT_WHITE);
        pf.setCaretColor(Theme.TEXT_WHITE);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_INPUT, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }
}
