package gui.components;

import gui.theme.Theme;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Exit Confirmation Modal Dialog matching the bottom-right screenshot design.
 */
public class ExitDialog extends JDialog {

    private boolean confirmed = false;

    public ExitDialog(JFrame parent) {
        super(parent, "Exit Confirmation", true);
        setUndecorated(true);
        setSize(420, 220);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Dark Card Background
                g2.setColor(Theme.BG_CARD_OPAQUE);
                g2.fillRoundRect(0, 0, w, h, 16, 16);

                // Border
                g2.setColor(Theme.BORDER_CARD);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

                // Blue Question Mark Circle Icon
                int iconX = 40;
                int iconY = 40;
                int iconD = 50;
                g2.setColor(new Color(20, 115, 230, 45));
                g2.fillOval(iconX, iconY, iconD, iconD);
                g2.setColor(Theme.PRIMARY_BLUE);
                g2.drawOval(iconX, iconY, iconD, iconD);

                g2.setColor(Theme.ACCENT_CYAN);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
                g2.drawString("?", iconX + 18, iconY + 35);

                // Title
                g2.setColor(Theme.TEXT_WHITE);
                g2.setFont(Theme.FONT_SUBTITLE);
                g2.drawString("Exit Confirmation", 110, 55);

                // Message
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.FONT_BODY);
                g2.drawString("Are you sure you want to exit the application?", 110, 80);

                g2.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Bottom button row
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);

        CustomButton btnYes = new CustomButton("Yes", CustomButton.Style.PRIMARY);
        btnYes.setPreferredSize(new Dimension(100, 38));
        btnYes.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        CustomButton btnNo = new CustomButton("No", CustomButton.Style.SECONDARY);
        btnNo.setPreferredSize(new Dimension(100, 38));
        btnNo.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        buttonPanel.add(btnYes);
        buttonPanel.add(btnNo);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    public static boolean showConfirmation(JFrame parent) {
        ExitDialog dialog = new ExitDialog(parent);
        dialog.setVisible(true);
        return dialog.confirmed;
    }
}
