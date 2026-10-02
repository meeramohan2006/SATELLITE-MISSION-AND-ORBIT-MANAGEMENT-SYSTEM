package gui.components;

import gui.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Modern dark-themed JTable with rounded borders, custom header rendering,
 * alternating row colors, and status pill badges.
 */
public class ModernTable extends JTable {

    public ModernTable(DefaultTableModel model) {
        super(model);
        initStyle();
    }

    private void initStyle() {
        setRowHeight(38);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setBackground(new Color(13, 25, 48));
        setForeground(Theme.TEXT_WHITE);
        setFont(Theme.FONT_BODY);
        setSelectionBackground(new Color(25, 60, 115));
        setSelectionForeground(Theme.TEXT_WHITE);

        // Header styling
        JTableHeader header = getTableHeader();
        header.setPreferredSize(new Dimension(100, 42));
        header.setFont(Theme.FONT_BODY_BOLD);
        header.setBackground(new Color(20, 38, 72));
        header.setForeground(Theme.TEXT_WHITE);
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(new Color(20, 38, 72));
                lbl.setForeground(Theme.TEXT_WHITE);
                lbl.setFont(Theme.FONT_BODY_BOLD);
                lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
                lbl.setHorizontalAlignment(column == 0 ? JLabel.LEFT : JLabel.LEFT);
                return lbl;
            }
        });

        // Cell Renderer for standard data
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Alternating rows
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(13, 25, 48) : new Color(17, 32, 62));
                    c.setForeground(Theme.TEXT_WHITE);
                } else {
                    c.setBackground(new Color(25, 65, 120));
                    c.setForeground(Color.WHITE);
                }

                setBorder(new EmptyBorder(0, 12, 0, 12));
                return c;
            }
        });
    }

    /**
     * Installs the custom Status pill badge renderer on the specified column index.
     */
    public void setStatusColumn(int columnIndex) {
        if (columnIndex >= 0 && columnIndex < getColumnCount()) {
            getColumnModel().getColumn(columnIndex).setCellRenderer(new StatusBadgeRenderer());
        }
    }

    /**
     * Wraps the table inside a modern styled dark JScrollPane with border.
     */
    public static JScrollPane createScrollPane(ModernTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(13, 25, 48));
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_CARD, 1));
        scrollPane.setOpaque(false);
        return scrollPane;
    }

    /**
     * Custom renderer that renders "Active", "Inactive", "Completed" as rounded pills.
     */
    public static class StatusBadgeRenderer extends JPanel implements TableCellRenderer {
        private String text = "";
        private boolean isSelected = false;

        public StatusBadgeRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.text = value != null ? value.toString() : "";
            this.isSelected = isSelected;

            if (isSelected) {
                setBackground(new Color(25, 65, 120));
            } else {
                setBackground(row % 2 == 0 ? new Color(13, 25, 48) : new Color(17, 32, 62));
            }
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (text == null || text.trim().isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color pillBg;
            Color pillFg;

            if ("Active".equalsIgnoreCase(text)) {
                pillBg = Theme.BADGE_ACTIVE_BG;
                pillFg = Theme.BADGE_ACTIVE_TEXT;
            } else if ("Inactive".equalsIgnoreCase(text) || "Failed".equalsIgnoreCase(text)) {
                pillBg = Theme.BADGE_INACTIVE_BG;
                pillFg = Theme.BADGE_INACTIVE_TEXT;
            } else if ("Completed".equalsIgnoreCase(text)) {
                pillBg = new Color(20, 115, 230, 45);
                pillFg = Theme.ACCENT_CYAN;
            } else {
                pillBg = new Color(245, 158, 11, 45);
                pillFg = Theme.WARNING_ORANGE;
            }

            int h = getHeight();
            int pillH = 24;
            int pillW = 75;
            int pillX = 12;
            int pillY = (h - pillH) / 2;

            // Draw pill background
            g2.setColor(pillBg);
            g2.fillRoundRect(pillX, pillY, pillW, pillH, 12, 12);

            // Draw pill border
            g2.setColor(pillFg);
            g2.drawRoundRect(pillX, pillY, pillW, pillH, 12, 12);

            // Draw text centered in pill
            g2.setFont(Theme.FONT_SMALL_BOLD);
            int textW = g2.getFontMetrics().stringWidth(text);
            int textX = pillX + (pillW - textW) / 2;
            int textY = pillY + (pillH - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
            g2.drawString(text, textX, textY);

            g2.dispose();
        }
    }
}
