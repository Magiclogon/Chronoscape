package ma.ac.emi.UI;

import ma.ac.emi.camera.Camera;
import ma.ac.emi.glgraphics.hud.BitmapFont;
import ma.ac.emi.math.Vector3D;
import java.awt.*;
import java.util.List;
import com.jogamp.opengl.GL3;

/**
 * Renders floating text (damage, level up, etc.) in both 
 * Graphics2D (Legacy) and OpenGL (High-DPI aware).
 */
public class FloatingTextRenderer {

    private FloatingTextRenderer() {}

    /** Legacy AWT Render — uses logical pixels */
    public static void render(Graphics g, Camera camera, int panelW, int panelH) {
        List<FloatingText> texts = FloatingTextManager.getInstance().getActive();
        if (texts.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (FloatingText ft : texts) {
            Vector3D world = ft.getWorldPos().add(new Vector3D(0, ft.getOffsetY(), 0));
            Vector3D screen = camera.worldToScreen(world);

            float sx = (float) screen.getX();
            float sy = (float) screen.getY();

            if (sx < -100 || sx > panelW + 100 || sy < -60 || sy > panelH + 60) continue;

            float alpha = ft.alpha();
            Font font = MenuStyle.font(ft.preset.fontSize).deriveFont(Font.BOLD);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(ft.text);
            int drawX = (int)(sx - textW / 2f);
            int drawY = (int)(sy);

            g2.setComposite(AlphaComposite.SrcOver.derive(alpha * 0.55f));
            g2.setColor(Color.BLACK);
            g2.drawString(ft.text, drawX + 2, drawY + 2);

            g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
            g2.setColor(Color.decode(ft.preset.hex));
            g2.drawString(ft.text, drawX, drawY);
        }
        g2.dispose();
    }

    /** * Modern OpenGL Render — DPI aware.
     * @param screenW Physical width
     * @param screenH Physical height
     * @param dpiScale Scaling factor (e.g., 1.25 for 125%)
     */
    public static void renderGL(GL3 gl, BitmapFont font, Camera camera,
                                int screenW, int screenH, float dpiScale) {
        List<FloatingText> active = FloatingTextManager.getInstance().getActive();
        if (active.isEmpty()) return;

        for (FloatingText ft : active) {
            // Apply rise offset in world space
            Vector3D world  = ft.getWorldPos().add(new Vector3D(0, ft.getOffsetY(), 0));
            Vector3D screen = camera.worldToScreen(world);
            System.out.println(screen);
            float sx = (float) screen.getX()*dpiScale;
            float sy = (float) screen.getY()*dpiScale;

            // Skip if off-screen (using physical bounds)
            if (sx < -100 * dpiScale || sx > screenW + 100 * dpiScale || 
                sy < -60 * dpiScale || sy > screenH + 60 * dpiScale) continue;

            float alpha      = ft.alpha();
            float scaledSize = ft.preset.fontSize * dpiScale; // Scale the font
            float shadowOff  = 2.0f * dpiScale;               // Scale the shadow offset
            float[] col      = hexToRGB(ft.preset.hex);

            // Center horizontally based on the SCALED width
            float tw = font.measureWidth(ft.text, scaledSize);
            float tx = sx - tw / 2f;
            float ty = sy - font.lineHeight(scaledSize) / 2f;

            // Render Shadow
            font.drawText(gl, ft.text, tx + shadowOff, ty + shadowOff, scaledSize,
                    0f, 0f, 0f, alpha * 0.55f, screenW, screenH);
            
            // Render Main Text
            font.drawText(gl, ft.text, tx, ty, scaledSize,
                    col[0], col[1], col[2], alpha, screenW, screenH);
        }
    }

    /** Parse "#FF6B6B" → {r, g, b} in 0–1 range. */
    private static float[] hexToRGB(String hex) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        return new float[]{
                Integer.parseInt(hex.substring(0, 2), 16) / 255f,
                Integer.parseInt(hex.substring(2, 4), 16) / 255f,
                Integer.parseInt(hex.substring(4, 6), 16) / 255f
        };
    }
}