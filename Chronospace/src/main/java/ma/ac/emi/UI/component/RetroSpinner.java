package ma.ac.emi.UI.component;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * RetroSpinner — ◀ value ▶ horizontal layout, no border, green accent arrows.
 */
public class RetroSpinner extends JPanel {

    private static final Color PANEL_BG     = new Color(30, 30, 38);
    private static final Color TEXT_MAIN    = new Color(235, 235, 245);
    private static final Color ACCENT_GREEN = new Color(80, 200, 100);
    private static final Color BTN_HOVER    = new Color(50, 50, 62);
    private static final Color BTN_PRESS    = new Color(20, 20, 28);

    private static final String FONT_NAME = "ByteBounce";
    private static final Font   FONT_BODY = new Font(FONT_NAME, Font.PLAIN, 20);

    private static final int BTN_W = 28;

    private int value, min, max, step;
    private final List<ChangeListener> listeners = new ArrayList<>();

    private final ArrowBtn decBtn;
    private final ArrowBtn incBtn;
    protected final JLabel valueLabel;

    public RetroSpinner(int min, int value, int max, int step) {
        this.min   = min;
        this.max   = max;
        this.step  = step;
        this.value = Math.max(min, Math.min(max, value));

        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(120, 32));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        decBtn = new ArrowBtn(false);
        incBtn = new ArrowBtn(true);

        valueLabel = new JLabel(getDisplayValue(this.value), SwingConstants.CENTER);
        valueLabel.setFont(FONT_BODY);
        valueLabel.setForeground(TEXT_MAIN);
        valueLabel.setOpaque(false);

        add(decBtn,     BorderLayout.WEST);
        add(valueLabel, BorderLayout.CENTER);
        add(incBtn,     BorderLayout.EAST);

        decBtn.addActionListener(e -> changeValue(-step));
        incBtn.addActionListener(e -> changeValue(+step));
        addMouseWheelListener(e -> changeValue(e.getWheelRotation() < 0 ? +step : -step));
    }

    // ── Public API ────────────────────────────────────────────────────────

    public int getValue() { return value; }

    public void setValue(int v) {
        value = Math.max(min, Math.min(max, v));
        valueLabel.setText(getDisplayValue(value));
        repaint();
    }

    public void addChangeListener(ChangeListener l)    { listeners.add(l); }
    public void removeChangeListener(ChangeListener l) { listeners.remove(l); }

    protected String getDisplayValue(int v) { return String.valueOf(v); }

    // ── Internals ─────────────────────────────────────────────────────────

    private void changeValue(int delta) {
        int next = value + delta;
        if (next < min || next > max) return;
        value = next;
        valueLabel.setText(getDisplayValue(value));
        fireChange();
        repaint();
    }

    private void fireChange() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener l : listeners) l.stateChanged(e);
    }

    @Override protected void paintComponent(Graphics g) { /* no background, no border */ }

    // ── ArrowBtn ──────────────────────────────────────────────────────────

    private class ArrowBtn extends JButton {
        private final boolean isInc;
        private boolean hovered = false;
        private boolean pressed = false;

        ArrowBtn(boolean isInc) {
            this.isInc = isInc;
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(BTN_W, 32));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                public void mousePressed(MouseEvent e) { pressed = true;  repaint(); }
                public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int cx = w / 2, cy = h / 2;

            // Subtle bg on hover/press only
            if (pressed) {
                g2.setColor(BTN_PRESS);
                g2.fillRoundRect(0, 0, w, h, 6, 6);
            } else if (hovered) {
                g2.setColor(BTN_HOVER);
                g2.fillRoundRect(0, 0, w, h, 6, 6);
            }

            // ◀ or ▶ triangle
            int[] px, py;
            if (isInc) {
                // pointing right ▶
                px = new int[]{cx - 4, cx - 4, cx + 5};
                py = new int[]{cy - 6, cy + 6, cy    };
            } else {
                // pointing left ◀
                px = new int[]{cx + 4, cx + 4, cx - 5};
                py = new int[]{cy - 6, cy + 6, cy    };
            }

            g2.setColor(pressed ? ACCENT_GREEN.darker() : hovered ? Color.WHITE : ACCENT_GREEN);
            g2.fillPolygon(px, py, 3);

            g2.dispose();
        }
    }
}