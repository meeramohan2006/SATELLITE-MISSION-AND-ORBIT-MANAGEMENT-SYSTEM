package gui.components;

import gui.theme.Theme;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Sidebar navigation panel matching the left navigation bar in the reference UI.
 * Displays navigation items with active blue pill highlights and smooth hovers.
 */
public class SidebarPanel extends JPanel {

    public interface NavigationListener {
        void onNavigate(String target);
    }

    private final List<SidebarItem> items = new ArrayList<>();
    private String activeItem = "Dashboard";
    private NavigationListener listener;

    private static class SidebarItem {
        String id;
        String label;
        String iconSymbol;
        int y;
        int height = 40;
        boolean isHovered = false;

        SidebarItem(String id, String label, String iconSymbol) {
            this.id = id;
            this.label = label;
            this.iconSymbol = iconSymbol;
        }
    }

    public SidebarPanel(String currentActive, NavigationListener listener) {
        this.activeItem = currentActive;
        this.listener = listener;
        setPreferredSize(new Dimension(220, 700));
        setLayout(null);
        setOpaque(false);

        initItems();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int my = e.getY();
                for (SidebarItem item : items) {
                    if (my >= item.y && my <= item.y + item.height) {
                        activeItem = item.id;
                        repaint();
                        if (SidebarPanel.this.listener != null) {
                            SidebarPanel.this.listener.onNavigate(item.id);
                        }
                        break;
                    }
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int my = e.getY();
                boolean changed = false;
                for (SidebarItem item : items) {
                    boolean hover = (my >= item.y && my <= item.y + item.height && e.getX() >= 12 && e.getX() <= getWidth() - 12);
                    if (item.isHovered != hover) {
                        item.isHovered = hover;
                        changed = true;
                    }
                }
                if (changed) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    repaint();
                }
            }
        });
    }

    private void initItems() {
        items.clear();
        items.add(new SidebarItem("Dashboard", "Dashboard", "⊞"));
        items.add(new SidebarItem("Satellites", "Satellite Management", "🛰"));
        items.add(new SidebarItem("Missions", "Mission Management", "🚀"));
        items.add(new SidebarItem("Orbits", "Orbit Management", "🌐"));
        items.add(new SidebarItem("Search", "Search", "🔍"));
        items.add(new SidebarItem("Reports", "Reports / View Data", "📊"));
        items.add(new SidebarItem("Logout", "Logout", "⇦"));
    }

    public void setActiveItem(String id) {
        this.activeItem = id;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Background of sidebar
        g2.setColor(Theme.BG_SIDEBAR);
        g2.fillRect(0, 0, w, h);

        // Right vertical subtle divider line
        g2.setColor(Theme.BORDER_CARD);
        g2.drawLine(w - 1, 0, w - 1, h);

        // Sidebar Header / Logo
        int startY = 24;
        g2.setColor(Theme.ACCENT_CYAN);
        g2.setFont(Theme.FONT_TITLE);
        g2.drawString("🛰", 20, startY + 18);

        g2.setColor(Theme.TEXT_WHITE);
        g2.setFont(Theme.FONT_HEADER);
        g2.drawString("OrbitPulse", 50, startY + 16);

        g2.setColor(Theme.TEXT_MUTED);
        g2.setFont(Theme.FONT_SMALL);
        g2.drawString("Mission Control v1.0", 50, startY + 30);

        // Divider
        g2.setColor(Theme.BORDER_CARD);
        g2.drawLine(15, startY + 45, w - 15, startY + 45);

        // Render Navigation Items
        int currentY = startY + 65;
        int itemW = w - 24;
        int itemH = 40;

        for (int i = 0; i < items.size(); i++) {
            SidebarItem item = items.get(i);

            // Put Logout towards bottom if it's the last item
            if ("Logout".equals(item.id)) {
                item.y = h - 60;
            } else {
                item.y = currentY;
                currentY += itemH + 10;
            }

            boolean isActive = item.id.equalsIgnoreCase(activeItem);

            if (isActive) {
                // Active blue pill
                g2.setColor(Theme.PRIMARY_BLUE);
                g2.fillRoundRect(12, item.y, itemW, itemH, 10, 10);
                g2.setColor(Theme.TEXT_WHITE);
            } else if (item.isHovered) {
                // Hover subtle pill
                g2.setColor(new Color(25, 45, 80));
                g2.fillRoundRect(12, item.y, itemW, itemH, 10, 10);
                g2.setColor(Theme.TEXT_WHITE);
            } else {
                g2.setColor(Theme.TEXT_MUTED);
            }

            // Draw Icon and Label
            g2.setFont(Theme.FONT_BODY_BOLD);
            g2.drawString(item.iconSymbol, 24, item.y + 25);
            g2.drawString(item.label, 48, item.y + 25);
        }

        g2.dispose();
    }
}
