package ma.ac.emi.UI;

import ma.ac.emi.UI.MenuStyle;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.math.Vector3D;

import java.awt.*;
import java.util.List;


public class FloatingTextRenderer {

    private FloatingTextRenderer() {}

    public static void render(Graphics g, Camera camera, int panelW, int panelH) {
        List<FloatingText> texts = FloatingTextManager.getInstance().getActive();
        if (texts.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (FloatingText ft : texts) {
            // Convert world position + rise offset to screen coordinates
            Vector3D world = ft.getWorldPos().add(new Vector3D(0, ft.getOffsetY(), 0));
            Vector3D screen = camera.worldToScreen(world);

            float sx = (float) screen.getX();
            float sy = (float) screen.getY();

            // Skip if off-screen
            if (sx < -100 || sx > panelW + 100 || sy < -60 || sy > panelH + 60) continue;

            float alpha = ft.alpha();

            // ── Shadow pass ───────────────────────────────────────────────
            Font font = MenuStyle.font(ft.preset.fontSize).deriveFont(Font.BOLD);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(ft.text);
            int drawX = (int)(sx - textW / 2f);
            int drawY = (int)(sy);

            g2.setComposite(AlphaComposite.SrcOver.derive(alpha * 0.55f));
            g2.setColor(Color.BLACK);
            g2.drawString(ft.text, drawX + 2, drawY + 2);

            // ── Main text ─────────────────────────────────────────────────
            g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
            g2.setColor(Color.decode(ft.preset.hex));
            g2.drawString(ft.text, drawX, drawY);
        }

        g2.dispose();
    }
}
