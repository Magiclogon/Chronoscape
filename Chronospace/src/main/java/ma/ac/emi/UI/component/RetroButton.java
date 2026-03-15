package ma.ac.emi.UI.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RetroButton extends JButton {

    public enum Style {
        SOLID,    // filled — primary actions
        OUTLINE,  // transparent fill, colored border — secondary
        DANGER,   // always red fill — destructive
        GHOST,    // no border, no fill — tertiary
        MENU      // full-width row for main menu
    }

    private static final Color BG_DARK      = new Color(18, 18, 24);
    private static final Color PANEL_BG     = new Color(30, 30, 38);
    private static final Color BORDER_LIGHT = new Color(180, 180, 190);
    private static final Color ACCENT_RED   = new Color(220, 60, 60);
    private static final int   ARC          = 6;
    private static final int   BTN_H        = 38;

    private static final String FONT_NAME = "ByteBounce";
    private static final Font   FONT_BODY = new Font(FONT_NAME, Font.PLAIN, 20);
    private static final Font   FONT_MENU = new Font(FONT_NAME, Font.PLAIN, 28);

    private final Style style;
    private final Color accent;
    private final Color fg;
    private boolean hovered  = false;
    private boolean pressed  = false;
    private boolean toggled  = false;

    // ── Constructors ──────────────────────────────────────────────────────

    public RetroButton(String text, Color accent, Color fg) {
        this(text, Style.SOLID, accent, fg);
    }

    public RetroButton(String text, Style style, Color accent) {
        this(text, style, accent, style == Style.SOLID ? Color.BLACK : accent);
    }

    public RetroButton(String text, Style style, Color accent, Color fg) {
        super(text);
        this.style  = style;
        this.accent = accent;
        this.fg     = fg;

        setFont(style == Style.MENU ? FONT_MENU : FONT_BODY);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (style != Style.MENU)
            setPreferredSize(new Dimension(getPreferredSize().width, BTN_H));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e)  { hovered = true;  repaint(); }
            public void mouseExited (MouseEvent e)  { hovered = false; repaint(); }
            public void mousePressed(MouseEvent e)  { pressed = true;  repaint(); }
            public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
        });
    }

    // ── Toggle support (for mute button etc.) ─────────────────────────────

    public void setToggled(boolean t) { this.toggled = t; repaint(); }
    public boolean isToggled()        { return toggled; }

    // ── Paint ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        Color base = (style == Style.DANGER || toggled) ? ACCENT_RED : accent;

        switch (style) {

            case MENU: {
                // Row fill
                g2.setColor(pressed ? new Color(220, 220, 220)
                          : hovered ? Color.WHITE
                          :           new Color(0, 0, 0, 100));
                g2.fillRect(0, 0, w, h);

                // Green left accent bar
                g2.setColor(base);
                g2.fillRect(0, 0, 5, h);

                // Bottom separator
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 60));
                g2.fillRect(0, h - 1, w, 1);

                // Text — left-aligned, inverts on hover
                g2.setFont(FONT_MENU);
                g2.setColor(hovered || pressed ? BG_DARK : Color.WHITE);
                FontMetrics fm = g2.getFontMetrics(FONT_MENU);
                g2.drawString(getText(), 20, (h - fm.getHeight()) / 2 + fm.getAscent());

                g2.dispose();
                return; // own text draw — skip shared draw below
            }

            case SOLID:
            case DANGER: {
                Color fill = pressed ? base.darker().darker()
                           : hovered ? base.brighter()
                           : base;
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, w, h, ARC, ARC);
                if (!pressed) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRoundRect(1, 1, w - 2, h / 2, ARC, ARC);
                }
                g2.setColor(pressed ? base.darker() : hovered ? Color.WHITE : BORDER_LIGHT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, w - 2, h - 2, ARC, ARC);
                break;
            }

            case OUTLINE: {
                if (hovered) {
                    g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 30));
                    g2.fillRoundRect(0, 0, w, h, ARC, ARC);
                }
                g2.setColor(pressed ? base.darker() : hovered ? base.brighter() : base);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, w - 2, h - 2, ARC, ARC);
                break;
            }

            case GHOST: {
                if (hovered || pressed) {
                    g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), pressed ? 40 : 20));
                    g2.fillRoundRect(0, 0, w, h, ARC, ARC);
                }
                break;
            }
        }

        // Shared text draw for all non-MENU styles
        g2.setFont(FONT_BODY);
        g2.setColor(style == Style.OUTLINE || style == Style.GHOST
                ? (hovered ? base.brighter() : base)
                : fg);
        FontMetrics fm = g2.getFontMetrics(FONT_BODY);
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), tx, ty);

        g2.dispose();
    }
}