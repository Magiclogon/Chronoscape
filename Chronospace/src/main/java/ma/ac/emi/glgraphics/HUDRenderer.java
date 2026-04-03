package ma.ac.emi.glgraphics;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import ma.ac.emi.UI.GameUIPanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

/**
 * Renders the game HUD (GameUIPanel) as a fullscreen OpenGL texture.
 *
 * Strategy:
 *  - GameUIPanel.paintComponent() draws into a BufferedImage via Graphics2D.
 *  - That image is uploaded to a GL texture with glTexSubImage2D.
 *  - A FullscreenQuad draws the texture over the scene with alpha blending.
 *
 * The upload only happens when hudDirty == true. markDirty() is called on a
 * timer (~20 times/second) from GameRenderer, so the cost is negligible.
 * All existing GameUIPanel draw logic is completely unchanged.
 */
public class HUDRenderer {

    private final GameUIPanel hudPanel;

    private BufferedImage   hudImage;
    private Graphics2D      hudGraphics;
    private int             texId   = 0;
    private FullscreenQuad  quad;
    private Shader          shader;

    private int texWidth  = 0;
    private int texHeight = 0;

    private volatile boolean hudDirty = true;

    // Timer: re-upload at most every DIRTY_INTERVAL_MS milliseconds
    private static final long DIRTY_INTERVAL_MS = 50; // 20 fps max upload rate
    private long lastUploadTime = 0;

    public HUDRenderer(GameUIPanel panel) {
        this.hudPanel = panel;
    }

    // ── Init / resize ─────────────────────────────────────────────────────

    public void init(GL3 gl) {
        quad   = new FullscreenQuad(gl);
        shader = Shader.load(gl, "post.vert", "post.frag");

        int[] ids = new int[1];
        gl.glGenTextures(1, ids, 0);
        texId = ids[0];

        gl.glBindTexture(GL3.GL_TEXTURE_2D, texId);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
    }

    /**
     * Called whenever the GL surface is resized.
     * Recreates the BufferedImage and re-allocates the texture storage.
     */
    public void resize(GL3 gl, int screenWidth, int screenHeight) {
        if (screenWidth <= 0 || screenHeight <= 0) return;
        if (screenWidth == texWidth && screenHeight == texHeight) return;

        texWidth  = screenWidth;
        texHeight = screenHeight;

        // Dispose old Graphics2D before replacing the image
        if (hudGraphics != null) hudGraphics.dispose();

        // TYPE_4BYTE_ABGR gives us a direct byte layout we can pass to GL
        hudImage    = new BufferedImage(texWidth, texHeight, BufferedImage.TYPE_4BYTE_ABGR);
        hudGraphics = hudImage.createGraphics();

        // Allocate GL texture storage at new size
        gl.glBindTexture(GL3.GL_TEXTURE_2D, texId);
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA8,
                texWidth, texHeight, 0,
                GL3.GL_RGBA, GL3.GL_UNSIGNED_BYTE, null);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);

        // Force a redraw after resize
        hudDirty = true;

        // Also resize the Swing panel so its coordinate system matches
        hudPanel.setSize(texWidth, texHeight);
    }

    // ── Per-frame ─────────────────────────────────────────────────────────

    public void markDirty() {
        hudDirty = true;
    }

    /**
     * Call once per frame from GameRenderer after the post-processing pass.
     * Uploads the HUD texture if dirty, then draws it as a fullscreen quad.
     */
    public void render(GL3 gl) {
        if (texId == 0 || texWidth == 0) return;

        long now = System.currentTimeMillis();
        if (hudDirty && (now - lastUploadTime) >= DIRTY_INTERVAL_MS) {
            uploadHUD(gl);
            hudDirty      = false;
            lastUploadTime = now;
        }

        // Draw HUD texture over the scene with alpha blending
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        shader.use(gl);
        shader.setInt(gl, "uTexture", 0);

        gl.glActiveTexture(GL3.GL_TEXTURE0);
        quad.draw(gl, texId);

        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
    }

    // ── Upload ────────────────────────────────────────────────────────────

    private void uploadHUD(GL3 gl) {
        // Clear to fully transparent
        hudGraphics.setComposite(AlphaComposite.Clear);
        hudGraphics.fillRect(0, 0, texWidth, texHeight);
        hudGraphics.setComposite(AlphaComposite.SrcOver);

        // Enable antialiasing for text
        hudGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        hudGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Drive GameUIPanel's existing draw logic — zero changes to that class
        hudPanel.paintComponent(hudGraphics);

        // Extract raw bytes: TYPE_4BYTE_ABGR → need to swap to RGBA for GL
        byte[] raw = ((DataBufferByte) hudImage.getRaster().getDataBuffer()).getData();
        byte[] rgba = toRGBA(raw);

        ByteBuffer buf = ByteBuffer.wrap(rgba);

        gl.glBindTexture(GL3.GL_TEXTURE_2D, texId);
        gl.glTexSubImage2D(GL3.GL_TEXTURE_2D, 0,
                0, 0, texWidth, texHeight,
                GL3.GL_RGBA, GL3.GL_UNSIGNED_BYTE, buf);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
    }

    /**
     * BufferedImage.TYPE_4BYTE_ABGR stores bytes as [A, B, G, R].
     * OpenGL GL_RGBA expects [R, G, B, A]. Swap in place.
     */
    private byte[] toRGBA(byte[] abgr) {
        byte[] rgba = new byte[abgr.length];
        for (int i = 0; i < abgr.length; i += 4) {
            rgba[i    ] = abgr[i + 3]; // R
            rgba[i + 1] = abgr[i + 2]; // G
            rgba[i + 2] = abgr[i + 1]; // B
            rgba[i + 3] = abgr[i    ]; // A
        }
        return rgba;
    }

    // ── Dispose ───────────────────────────────────────────────────────────

    public void dispose(GL3 gl) {
        if (hudGraphics != null) hudGraphics.dispose();
        if (quad        != null) quad.dispose(gl);
        if (shader      != null) shader.dispose(gl);
        if (texId != 0) {
            gl.glDeleteTextures(1, new int[]{texId}, 0);
            texId = 0;
        }
    }
}