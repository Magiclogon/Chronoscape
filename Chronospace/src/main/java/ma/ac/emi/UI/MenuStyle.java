package ma.ac.emi.UI;

import java.awt.*;
import java.io.InputStream;

/**
 * Single source of truth for all UI style across menus, buttons, settings, and overlays.
 * Edit only this file to restyle the entire UI.
 */
public final class MenuStyle {

    private MenuStyle() {}

    // ── Backgrounds ───────────────────────────────────────────────────────

    public static final Color BG_DARK   = new Color(18, 18, 24);
    public static final Color BG_PANEL  = new Color(30, 30, 38);
    public static final Color BG_MUTED  = new Color(80, 80, 90);

    // ── Text ──────────────────────────────────────────────────────────────

    public static final Color TEXT_MAIN = new Color(235, 235, 245);
    public static final Color TEXT_GRAY = new Color(150, 150, 160);
    public static final Color TEXT_BORDER = new Color(180, 180, 190);

    // ── Accents ───────────────────────────────────────────────────────────

    /** Primary action color — start, confirm, apply. */
    public static final Color ACCENT         = new Color(80, 200, 100);

    /** Dimmer variant — secondary/back/navigate-away buttons. */
    public static final Color ACCENT_DIM     = new Color(55, 140, 70);

    /** Destructive actions — quit, reset, cancel. */
    public static final Color ACCENT_RED     = new Color(220, 60, 60);

    /** Decorative / tertiary — "more options", links. */
    public static final Color ACCENT_PURPLE  = new Color(180, 100, 255);

    // ── Sidebar overlay gradient ──────────────────────────────────────────

    public static final Color OVERLAY_FROM  = new Color(0, 0, 0, 0);
    public static final Color OVERLAY_TO    = new Color(0, 0, 0, 160);

    // ── Scrollbar ─────────────────────────────────────────────────────────

    public static final Color SCROLLBAR_TRACK = new Color(10, 10, 15);
    public static final Color SCROLLBAR_THUMB = ACCENT;

    // ── Button / row sizing ───────────────────────────────────────────────

    /** Standard row height for menu buttons. */
    public static final int BTN_HEIGHT   = 64;

    /** Standard height for inline buttons (settings panels, dialogs). */
    public static final int BTN_HEIGHT_SM = 38;

    /** Bottom padding below the last button in a sidebar column. */
    public static final int BOTTOM_STRUT = 80;

    /** Corner arc radius for rounded buttons. */
    public static final int ARC = 6;

    // ── Fonts ─────────────────────────────────────────────────────────────

    public static final String FONT_NAME = "ByteBounce";

    private static Font BASE_FONT = null;

    static {
        try {
            InputStream is = MenuStyle.class.getResourceAsStream("/assets/Fonts/ByteBounce.ttf");
            if (is != null) {
                BASE_FONT = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(BASE_FONT);
            }
        } catch (Exception e) {
            System.err.println("MenuStyle: could not load ByteBounce, falling back to Monospaced.");
        }
    }

    /** Derive a sized font from the project typeface. Falls back to Monospaced. */
    public static Font font(float size) {
        if (BASE_FONT != null) return BASE_FONT.deriveFont(size);
        return new Font("Monospaced", Font.BOLD, (int) size);
    }

    public static final Font FONT_TITLE    = font(60f);   // loading screen, game-over headings
    public static final Font FONT_HEADER   = font(36f);   // settings panel titles
    public static final Font FONT_BODY     = font(20f);   // labels, inline buttons
    public static final Font FONT_MENU     = font(28f);   // full-width menu row buttons
    public static final Font FONT_SMALL    = font(16f);   // hints, descriptions
    public static final Font FONT_SUBTITLE = font(30f);   // loading screen subtitle

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Paints the standard left-to-right dark gradient onto a component.
     * Call from paintComponent() in any sidebar or overlay panel.
     */
    public static void paintOverlay(Graphics g, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, OVERLAY_FROM, width, 0, OVERLAY_TO));
        g2.fillRect(0, 0, width, height);
        g2.dispose();
    }

    /**
     * Returns a ready-to-use sidebar overlay JPanel:
     * gradient background, vertical BoxLayout, opaque=false.
     */
    public static javax.swing.JPanel makeSidebarOverlay() {
        javax.swing.JPanel panel = new javax.swing.JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                MenuStyle.paintOverlay(g, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        return panel;
    }

    /**
     * Applies standard sizing and left alignment to any menu row button.
     */
    public static void sizeButton(javax.swing.JButton btn) {
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, BTN_HEIGHT));
        btn.setPreferredSize(new Dimension(0, BTN_HEIGHT));
        btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    }
}