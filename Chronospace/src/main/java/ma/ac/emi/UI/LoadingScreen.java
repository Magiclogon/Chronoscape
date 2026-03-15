package ma.ac.emi.UI;

import javax.swing.*;
import java.awt.*;
import ma.ac.emi.UI.MenuStyle;

/**
 * Simple retro loading screen — black background, bouncing dot bar,
 * "LOADING" text in the project font. No images required.
 */
public class LoadingScreen extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final int   DOT_COUNT    = 5;
    private static final int   DOT_SIZE     = 14;
    private static final int   DOT_SPACING  = 28;
    private static final int   TIMER_DELAY  = 80;   // ms per frame
    private static final float BOUNCE_AMP   = 18f;  // pixels of vertical travel

    private final Timer animationTimer;
    private int frame = 0;  // increments each tick, drives the bounce wave

    public LoadingScreen() {
        setBackground(Color.BLACK);
        setOpaque(true);

        animationTimer = new Timer(TIMER_DELAY, e -> {
            frame++;
            repaint();
        });
    }

    public void startAnimation() {
        if (!animationTimer.isRunning()) {
            frame = 0;
            animationTimer.start();
        }
    }

    public void stopAnimation() {
        if (animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) startAnimation();
        else         stopAnimation();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // ── "LOADING" label ───────────────────────────────────────────────
        g2.setFont(MenuStyle.FONT_HEADER);
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        String label = "LOADING";
        int textX = (w - fm.stringWidth(label)) / 2;
        int textY = h / 2 - 48;
        g2.drawString(label, textX, textY);

        // ── Bouncing dot bar ──────────────────────────────────────────────
        int totalWidth = DOT_COUNT * DOT_SIZE + (DOT_COUNT - 1) * (DOT_SPACING - DOT_SIZE);
        int startX     = (w - totalWidth) / 2;
        int baseY      = h / 2 + 10;

        for (int i = 0; i < DOT_COUNT; i++) {
            // Sine wave offset — each dot is phase-shifted so they ripple
            double phase  = (frame * 0.25) - (i * 0.6);
            int    bounceY = (int)(Math.sin(phase) * BOUNCE_AMP);

            // Brighten the "peak" dot, dim the rest
            float brightness = (float)(Math.sin(phase) * 0.5 + 0.5);
            Color dotColor = blend(MenuStyle.ACCENT, Color.WHITE, brightness);

            int x = startX + i * DOT_SPACING;
            int y = baseY - DOT_SIZE / 2 + bounceY;

            g2.setColor(dotColor);
            g2.fillRect(x, y, DOT_SIZE, DOT_SIZE);
        }

        g2.dispose();
    }

    /** Linear interpolation between two colors by t in [0,1]. */
    private Color blend(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int)(a.getRed()   + t * (b.getRed()   - a.getRed()));
        int gr = (int)(a.getGreen() + t * (b.getGreen() - a.getGreen()));
        int bl = (int)(a.getBlue()  + t * (b.getBlue()  - a.getBlue()));
        return new Color(r, gr, bl);
    }
}