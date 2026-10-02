package gui.components;

import gui.theme.Theme;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern rounded button matching the reference design.
 * Supports primary blue, success green, danger red, and dark secondary variants.
 */
public class CustomButton extends JButton {

    public enum Style {
        PRIMARY, SUCCESS, DANGER, SECONDARY, OUTLINE
    }

    private final Style style;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int cornerRadius = 8;

    public CustomButton(String text, Style style) {
        super(text);
        this.style = style;
        setFont(Theme.FONT_BODY_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(110, 36));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Color bg;
        Color fg = Theme.TEXT_WHITE;
        Color border = null;

        switch (style) {
            case PRIMARY:
                bg = isPressed ? Theme.PRIMARY_BLUE.darker() : (isHovered ? Theme.PRIMARY_BLUE_HOVER : Theme.PRIMARY_BLUE);
                break;
            case SUCCESS:
                bg = isPressed ? Theme.SUCCESS_GREEN.darker() : (isHovered ? new Color(0, 220, 130) : Theme.SUCCESS_GREEN);
                break;
            case DANGER:
                bg = isPressed ? Theme.DANGER_RED.darker() : (isHovered ? new Color(250, 80, 80) : Theme.DANGER_RED);
                break;
            case SECONDARY:
                bg = isPressed ? new Color(18, 30, 55) : (isHovered ? new Color(30, 48, 85) : Theme.SECONDARY_BTN);
                border = isHovered ? Theme.BORDER_FOCUS : Theme.BORDER_CARD;
                break;
            case OUTLINE:
            default:
                bg = isPressed ? new Color(20, 115, 230, 60) : (isHovered ? new Color(20, 115, 230, 40) : new Color(0, 0, 0, 0));
                border = isHovered ? Theme.PRIMARY_BLUE_HOVER : Theme.PRIMARY_BLUE;
                fg = Theme.ACCENT_CYAN;
                break;
        }

        // Draw background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

        // Draw border if needed
        if (border != null) {
            g2.setColor(border);
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        }

        // Draw text
        g2.setColor(fg);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int textX = (w - fm.stringWidth(getText())) / 2;
        int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}
