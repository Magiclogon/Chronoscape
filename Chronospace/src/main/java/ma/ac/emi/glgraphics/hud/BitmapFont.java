package ma.ac.emi.glgraphics.hud;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import ma.ac.emi.glgraphics.Shader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Bitmap font renderer.
 *
 * At init time, renders all printable ASCII glyphs (32–126) from a TTF file
 * into a single RGBA texture atlas using AWT. Each character's UV coordinates
 * and advance width are stored in a GlyphInfo array.
 *
 * drawText() renders a string as a series of textured quads directly into
 * the default framebuffer using a screen-space orthographic projection.
 * No texture uploads happen after init.
 *
 * Coordinate system: screen-space pixels, origin top-left,
 * matching standard 2D UI conventions.
 */
public class BitmapFont {

    // ── Atlas config ──────────────────────────────────────────────────────

    private static final int ATLAS_SIZE   = 512;   // texture atlas width & height
    private static final int CELL_SIZE    = 48;    // pixels per glyph cell in atlas
    private static final int COLS         = ATLAS_SIZE / CELL_SIZE; // 10
    private static final int PADDING      = 2;     // pixels of padding around each glyph
    private static final int FIRST_CHAR   = 32;    // space
    private static final int LAST_CHAR    = 126;   // ~
    private static final int NUM_CHARS    = LAST_CHAR - FIRST_CHAR + 1; // 95

    // ── GL resources ─────────────────────────────────────────────────────

    private int    atlasTexId = 0;
    private int    vao, vbo;
    private Shader shader;

    // ── Glyph metrics ─────────────────────────────────────────────────────

    private static class GlyphInfo {
        float u0, v0, u1, v1;   // UV coords in atlas (0–1)
        int   width, height;     // glyph bitmap dimensions in pixels
        int   bearingX;          // horizontal offset from pen origin
        int   bearingY;          // vertical offset from baseline
        int   advance;           // how far to move pen after this glyph
    }

    private final GlyphInfo[] glyphs = new GlyphInfo[NUM_CHARS];

    // ── Font metrics ──────────────────────────────────────────────────────

    private int fontAscent;   // pixels above baseline
    private int fontDescent;  // pixels below baseline (positive value)
    private int lineHeight;   // total line height

    // The AWT font size used to build the atlas
    private static final float BASE_FONT_SIZE = 32f;

    // ── Init ─────────────────────────────────────────────────────────────

    /**
     * Build the glyph atlas and upload it to GL.
     *
     * @param gl       active GL3 context
     * @param fontPath resource path to the .ttf file, e.g. "/assets/Fonts/ByteBounce.ttf"
     */
    public void init(GL3 gl, String fontPath) {
        Font awtFont = loadFont(fontPath);
        buildAtlas(gl, awtFont);
        buildQuadGeometry(gl);
        shader = Shader.load(gl, "hud_text.vert", "hud_text.frag");
    }

    private Font loadFont(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("BitmapFont: font not found at " + path + ", using fallback");
                return new Font("Monospaced", Font.PLAIN, (int) BASE_FONT_SIZE);
            }
            Font f = Font.createFont(Font.TRUETYPE_FONT, is);
            return f.deriveFont(BASE_FONT_SIZE);
        } catch (Exception e) {
            System.err.println("BitmapFont: failed to load font: " + e.getMessage());
            return new Font("Monospaced", Font.PLAIN, (int) BASE_FONT_SIZE);
        }
    }

    private void buildAtlas(GL3 gl, Font font) {
        // ── 1. Measure glyphs ─────────────────────────────────────────────
        BufferedImage probe = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gp = probe.createGraphics();
        gp.setFont(font);
        FontMetrics fm = gp.getFontMetrics();
        fontAscent  = fm.getAscent();
        fontDescent = fm.getDescent();
        lineHeight  = fm.getHeight();
        gp.dispose();

        // ── 2. Render each glyph into atlas ───────────────────────────────
        BufferedImage atlas = new BufferedImage(ATLAS_SIZE, ATLAS_SIZE,
                                                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = atlas.createGraphics();
        g.setFont(font);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        // Transparent background
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, ATLAS_SIZE, ATLAS_SIZE);
        g.setComposite(AlphaComposite.SrcOver);
        g.setColor(Color.WHITE);

        for (int i = 0; i < NUM_CHARS; i++) {
            char c = (char)(FIRST_CHAR + i);
            int col = i % COLS;
            int row = i / COLS;

            int cellX = col * CELL_SIZE;
            int cellY = row * CELL_SIZE;

            // Draw glyph: baseline is cellY + PADDING + ascent
            int drawX = cellX + PADDING;
            int drawY = cellY + PADDING + fontAscent;
            g.drawString(String.valueOf(c), drawX, drawY);

            // Record metrics
            GlyphInfo gi  = new GlyphInfo();
            gi.advance    = fm.charWidth(c);
            gi.width      = fm.charWidth(c);
            gi.height     = lineHeight;
            gi.bearingX   = 0;
            gi.bearingY   = fontAscent;
            gi.u0 = (float)(cellX + PADDING) / ATLAS_SIZE;
            gi.v0 = (float)(cellY + PADDING) / ATLAS_SIZE;
            gi.u1 = (float)(cellX + PADDING + gi.width)  / ATLAS_SIZE;
            gi.v1 = (float)(cellY + PADDING + gi.height) / ATLAS_SIZE;
            glyphs[i] = gi;
        }
        g.dispose();

        // ── 3. Upload to GL ───────────────────────────────────────────────
        byte[] raw  = ((DataBufferByte) atlas.getRaster().getDataBuffer()).getData();
        byte[] rgba = abgrToRgba(raw);
        ByteBuffer buf = ByteBuffer.wrap(rgba);

        int[] ids = new int[1];
        gl.glGenTextures(1, ids, 0);
        atlasTexId = ids[0];

        gl.glBindTexture(GL3.GL_TEXTURE_2D, atlasTexId);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA8,
                ATLAS_SIZE, ATLAS_SIZE, 0,
                GL3.GL_RGBA, GL3.GL_UNSIGNED_BYTE, buf);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
    }

    /** TYPE_4BYTE_ABGR → RGBA byte swap */
    private static byte[] abgrToRgba(byte[] abgr) {
        byte[] rgba = new byte[abgr.length];
        for (int i = 0; i < abgr.length; i += 4) {
            rgba[i    ] = abgr[i + 3]; // R
            rgba[i + 1] = abgr[i + 2]; // G
            rgba[i + 2] = abgr[i + 1]; // B
            rgba[i + 3] = abgr[i    ]; // A
        }
        return rgba;
    }

    // ── Quad geometry (dynamic, updated per draw call) ────────────────────

    private void buildQuadGeometry(GL3 gl) {
        int[] tmp = new int[1];
        gl.glGenVertexArrays(1, tmp, 0);
        vao = tmp[0];
        gl.glBindVertexArray(vao);

        gl.glGenBuffers(1, tmp, 0);
        vbo = tmp[0];
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);

        // 6 vertices × 4 floats (x, y, u, v) — updated every draw call
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, 6L * 4 * Float.BYTES, null, GL3.GL_DYNAMIC_DRAW);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 2, GL3.GL_FLOAT, false, 4 * Float.BYTES, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 2, GL3.GL_FLOAT, false, 4 * Float.BYTES, 2L * Float.BYTES);

        gl.glBindVertexArray(0);
    }

    // ── Public draw API ───────────────────────────────────────────────────

    /**
     * Draw a string at screen-space pixel position (x, y).
     * y is the top of the text (not the baseline).
     *
     * @param gl         active GL3 context
     * @param text       string to draw
     * @param x          screen x in pixels
     * @param y          screen y in pixels (top of text)
     * @param size       desired font size in pixels
     * @param r,g,b,a    text color (0–1)
     * @param screenW    current screen width  (for projection)
     * @param screenH    current screen height (for projection)
     */
    /** Convenience overload accepting a float[4] color array {r,g,b,a}. */
    public void drawText(GL3 gl, String text, float x, float y, float size,
                         float[] color, int screenW, int screenH) {
        drawText(gl, text, x, y, size, color[0], color[1], color[2], color[3], screenW, screenH);
    }

    public void drawText(GL3 gl, String text, float x, float y, float size,
                         float r, float g, float b, float a,
                         int screenW, int screenH) {
        if (text == null || text.isEmpty()) return;

        float scale = size / BASE_FONT_SIZE;

        shader.use(gl);
        shader.setMat4(gl, "uProjection", ortho(screenW, screenH));
        shader.setVec4(gl, "uColor", r, g, b, a);
        shader.setInt(gl, "uTexture", 0);

        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, atlasTexId);
        gl.glBindVertexArray(vao);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);

        float penX = x;
        float penY = y;

        for (char c : text.toCharArray()) {
            if (c == '\n') {
                penX  = x;
                penY += lineHeight * scale;
                continue;
            }
            if (c < FIRST_CHAR || c > LAST_CHAR) continue;

            GlyphInfo gi = glyphs[c - FIRST_CHAR];
            if (gi == null) continue;

            float gx = penX;
            float gy = penY;
            float gw = gi.width  * scale;
            float gh = gi.height * scale;

            // Two triangles forming a quad
            float[] verts = {
                gx,      gy,      gi.u0, gi.v0,
                gx + gw, gy,      gi.u1, gi.v0,
                gx + gw, gy + gh, gi.u1, gi.v1,

                gx,      gy,      gi.u0, gi.v0,
                gx + gw, gy + gh, gi.u1, gi.v1,
                gx,      gy + gh, gi.u0, gi.v1,
            };

            FloatBuffer fb = FloatBuffer.wrap(verts);
            gl.glBufferSubData(GL3.GL_ARRAY_BUFFER, 0, (long) verts.length * Float.BYTES, fb);
            gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 6);

            penX += gi.advance * scale;
        }

        gl.glBindVertexArray(0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
    }

    /**
     * Measure the pixel width of a string at a given size.
     * Use this for centering or right-aligning text.
     */
    public float measureWidth(String text, float size) {
        float scale = size / BASE_FONT_SIZE;
        float w = 0;
        for (char c : text.toCharArray()) {
            if (c < FIRST_CHAR || c > LAST_CHAR) continue;
            GlyphInfo gi = glyphs[c - FIRST_CHAR];
            if (gi != null) w += gi.advance * scale;
        }
        return w;
    }

    /** Height of a single line at the given size. */
    public float lineHeight(float size) {
        return lineHeight * (size / BASE_FONT_SIZE);
    }

    // ── Projection ────────────────────────────────────────────────────────

    /** Standard top-left origin orthographic projection for screen-space UI. */
    private static float[] ortho(int w, int h) {
        // Maps x: [0, w] → [-1, 1], y: [0, h] → [1, -1] (top-left origin)
        return new float[] {
            2f/w,  0,     0, 0,
            0,    -2f/h,  0, 0,
            0,     0,    -1, 0,
           -1,     1,     0, 1
        };
    }

    // ── Dispose ───────────────────────────────────────────────────────────

    public void dispose(GL3 gl) {
        if (atlasTexId != 0) {
            gl.glDeleteTextures(1, new int[]{atlasTexId}, 0);
            atlasTexId = 0;
        }
        if (vao != 0) { gl.glDeleteVertexArrays(1, new int[]{vao}, 0); vao = 0; }
        if (vbo != 0) { gl.glDeleteBuffers(1,      new int[]{vbo}, 0); vbo = 0; }
        if (shader != null) { shader.dispose(gl); shader = null; }
    }
}