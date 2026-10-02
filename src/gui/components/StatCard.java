package gui.components;

import gui.theme.Theme;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Metric card component for the Dashboard.
 * Displays an icon badge, title label, and big count number.
 */
public class StatCard extends JPanel {

    private final String title;
    private int value;
    private final String iconSymbol;
    private final Color accentColor;

    public StatCard(String title, int value, String iconSymbol, Color accentColor) {
        this.title = title;
        this.value = value;
        this.iconSymbol = iconSymbol;
        this.accentColor = accentColor;
        setPreferredSize(new Dimension(210, 110));
        setOpaque(false);
    }

    public void setValue(int value) {
        this.value = value;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Card background (translucent dark navy with slight tint)
        g2.setColor(new Color(15, 29, 56, 220));
        g2.fillRoundRect(0, 0, w, h, 14, 14);

        // Subtle glowing border
        g2.setColor(Theme.BORDER_CARD);
        g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

        // Icon circle badge
        int iconSize = 42;
        int iconX = 16;
        int iconY = 16;
        g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 35));
        g2.fillOval(iconX, iconY, iconSize, iconSize);
        g2.setColor(accentColor);
        g2.drawOval(iconX, iconY, iconSize, iconSize);

        // Icon character
        g2.setFont(Theme.FONT_SUBTITLE);
        g2.setColor(accentColor);
        g2.drawString(iconSymbol, iconX + 13, iconY + 27);

        // Title text
        g2.setFont(Theme.FONT_SMALL_BOLD);
        g2.setColor(Theme.TEXT_MUTED);
        g2.drawString(title, iconX, h - 38);

        // Big numeric value
        g2.setFont(Theme.FONT_METRIC_VALUE);
        g2.setColor(Theme.TEXT_WHITE);
        g2.drawString(String.valueOf(value), iconX, h - 12);

        g2.dispose();
    }
}
