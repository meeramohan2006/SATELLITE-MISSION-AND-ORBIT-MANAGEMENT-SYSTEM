package gui;

import dao.UserDAO;
import gui.components.BackgroundPanel;
import gui.components.CustomButton;
import gui.components.DbConfigDialog;
import gui.theme.Theme;
import model.User;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Login Screen matching the top-middle visual design in the reference UI.
 */
public class LoginFrame extends JFrame {

    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("Login - Satellite Mission and Orbit Data Management System");
        setSize(1180, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Background with space visual
        BackgroundPanel bgPanel = new BackgroundPanel("assets/space_earth_bg.jpg", 0.40f);
        bgPanel.setLayout(null);

        // Center Login Card
        int cardW = 460;
        int cardH = 480;
        int cardX = (1180 - cardW) / 2;
        int cardY = (740 - cardH) / 2 - 20;

        JPanel loginCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Dark Glass Card
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, w, h, 20, 20);

                // Border
                g2.setColor(Theme.BORDER_CARD);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 20, 20);

                // Satellite Icon at Top Center
                g2.setColor(Theme.ACCENT_CYAN);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
                String satIcon = "🛰";
                int iconW = g2.getFontMetrics().stringWidth(satIcon);
                g2.drawString(satIcon, (w - iconW) / 2, 50);

                // System Title
                g2.setColor(Theme.TEXT_WHITE);
                g2.setFont(Theme.FONT_SUBTITLE);
                String t1 = "Satellite Mission and Orbit";
                String t2 = "Data Management System";
                g2.drawString(t1, (w - g2.getFontMetrics().stringWidth(t1)) / 2, 85);
                g2.drawString(t2, (w - g2.getFontMetrics().stringWidth(t2)) / 2, 108);

                // Field Labels
                g2.setFont(Theme.FONT_SMALL_BOLD);
                g2.setColor(Theme.TEXT_MUTED);
                g2.drawString("👤   Username", 45, 155);
                g2.drawString("🔒   Password", 45, 235);

                // Demo Credentials Hint
                g2.setFont(Theme.FONT_SMALL);
                g2.setColor(Theme.TEXT_HINT);
                String hint = "Demo: admin / admin123  or  operator / operator123";
                g2.drawString(hint, (w - g2.getFontMetrics().stringWidth(hint)) / 2, 395);

                // Footer
                String footer = "© 2026. All rights reserved.";
                g2.drawString(footer, (w - g2.getFontMetrics().stringWidth(footer)) / 2, 450);

                g2.dispose();
            }
        };
        loginCard.setBounds(cardX, cardY, cardW, cardH);
        loginCard.setLayout(null);
        loginCard.setOpaque(false);

        // Username Text Field
        txtUsername = new JTextField("admin");
        styleInputField(txtUsername);
        txtUsername.setBounds(45, 168, 370, 42);
        loginCard.add(txtUsername);

        // Password Field
        txtPassword = new JPasswordField("admin123");
        styleInputField(txtPassword);
        txtPassword.setBounds(45, 248, 370, 42);
        loginCard.add(txtPassword);

        // Enter key submits login
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);

        // Buttons
        CustomButton btnLogin = new CustomButton("➔  Login", CustomButton.Style.PRIMARY);
        btnLogin.setBounds(45, 320, 175, 42);
        btnLogin.addActionListener(e -> performLogin());
        loginCard.add(btnLogin);

        CustomButton btnExit = new CustomButton("Exit", CustomButton.Style.SECONDARY);
        btnExit.setBounds(240, 320, 175, 42);
        btnExit.addActionListener(e -> NavigationManager.navigate(LoginFrame.this, "exit"));
        loginCard.add(btnExit);

        // Database settings icon/link at top right
        JLabel lblDbSettings = new JLabel("⚙ DB Settings");
        lblDbSettings.setFont(Theme.FONT_SMALL_BOLD);
        lblDbSettings.setForeground(Theme.ACCENT_CYAN);
        lblDbSettings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblDbSettings.setBounds(1040, 30, 100, 30);
        lblDbSettings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DbConfigDialog dialog = new DbConfigDialog(LoginFrame.this);
                dialog.setVisible(true);
            }
        });
        bgPanel.add(lblDbSettings);

        bgPanel.add(loginCard);
        setContentPane(bgPanel);
    }

    private void styleInputField(JTextField tf) {
        tf.setFont(Theme.FONT_BODY);
        tf.setBackground(Theme.BG_INPUT);
        tf.setForeground(Theme.TEXT_WHITE);
        tf.setCaretColor(Theme.TEXT_WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_INPUT, 1),
                new EmptyBorder(8, 14, 8, 14)
        ));
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            NavigationManager.setCurrentUser(user);
            NavigationManager.navigate(this, "dashboard");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!\nDefault demo credentials:\nUsername: admin\nPassword: admin123", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
