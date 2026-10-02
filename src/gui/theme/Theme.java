package gui.theme;

import java.awt.Color;
import java.awt.Font;

/**
 * Centralized theme colors, typography, and constants matching
 * the space aerospace dark aesthetic in the reference design.
 */
public class Theme {

    // Backgrounds
    public static final Color BG_DARK = new Color(7, 13, 30);            // #070d1e
    public static final Color BG_CARD = new Color(15, 29, 56, 220);       // Translucent Card Navy
    public static final Color BG_CARD_OPAQUE = new Color(15, 29, 56);
    public static final Color BG_INPUT = new Color(12, 22, 45);           // #0c162d
    public static final Color BG_SIDEBAR = new Color(9, 18, 40, 240);     // Left sidebar

    // Borders & Dividers
    public static final Color BORDER_CARD = new Color(34, 56, 92);        // Subtle glowing border
    public static final Color BORDER_INPUT = new Color(40, 65, 105);
    public static final Color BORDER_FOCUS = new Color(0, 150, 255);      // Glowing cyan/blue on focus

    // Accents & Buttons
    public static final Color PRIMARY_BLUE = new Color(20, 115, 230);     // #1473e6
    public static final Color PRIMARY_BLUE_HOVER = new Color(30, 135, 250);
    public static final Color ACCENT_CYAN = new Color(0, 210, 255);       // Highlights & Icons
    public static final Color SUCCESS_GREEN = new Color(0, 200, 115);     // Active Status
    public static final Color DANGER_RED = new Color(235, 60, 60);         // Delete / Inactive
    public static final Color WARNING_ORANGE = new Color(245, 158, 11);   // Alerts / Pending
    public static final Color PURPLE_ACCENT = new Color(147, 51, 234);    // Missions icon
    public static final Color SECONDARY_BTN = new Color(23, 38, 70);      // Dark secondary buttons

    // Status Badges
    public static final Color BADGE_ACTIVE_BG = new Color(0, 200, 115, 40);
    public static final Color BADGE_ACTIVE_TEXT = new Color(0, 230, 130);
    public static final Color BADGE_INACTIVE_BG = new Color(235, 60, 60, 40);
    public static final Color BADGE_INACTIVE_TEXT = new Color(255, 100, 100);

    // Typography Colors
    public static final Color TEXT_WHITE = new Color(245, 248, 255);
    public static final Color TEXT_MUTED = new Color(145, 165, 190);
    public static final Color TEXT_HINT = new Color(95, 115, 140);

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_METRIC_VALUE = new Font("Segoe UI", Font.BOLD, 28);
}
