package ma.ac.emi.glgraphics;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

public class Framebuffer {
    private int fboId = 0;
    private int textureId = 0;
    private int rboId = 0;

    public void init(GL3 gl, int width, int height) {
        // Clean up if already exists (on resize)
        dispose(gl);

        // 1. Create Framebuffer Object
        int[] bufs = new int[1];
        gl.glGenFramebuffers(1, bufs, 0);
        fboId = bufs[0];
        gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, fboId);

        // 2. Create the Color Texture
        gl.glGenTextures(1, bufs, 0);
        textureId = bufs[0];
        gl.glBindTexture(GL3.GL_TEXTURE_2D, textureId);
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA16F, width, height, 0, GL3.GL_RGBA, GL3.GL_FLOAT, null);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
        gl.glFramebufferTexture2D(GL3.GL_FRAMEBUFFER, GL3.GL_COLOR_ATTACHMENT0, GL3.GL_TEXTURE_2D, textureId, 0);

        // 3. Create Renderbuffer for Depth/Stencil (Necessary for 3D/Z-layering)
        gl.glGenRenderbuffers(1, bufs, 0);
        rboId = bufs[0];
        gl.glBindRenderbuffer(GL3.GL_RENDERBUFFER, rboId);
        gl.glRenderbufferStorage(GL3.GL_RENDERBUFFER, GL3.GL_DEPTH24_STENCIL8, width, height);
        gl.glFramebufferRenderbuffer(GL3.GL_FRAMEBUFFER, GL3.GL_DEPTH_STENCIL_ATTACHMENT, GL3.GL_RENDERBUFFER, rboId);

        if (gl.glCheckFramebufferStatus(GL3.GL_FRAMEBUFFER) != GL3.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("Error: Framebuffer is incomplete!");
        }

        gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);
    }

    public void bind(GL3 gl) {
        gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, fboId);
    }

    public void unbind(GL3 gl) {
        gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);
    }

    public int getTextureId() { return textureId; }

    public void dispose(GL3 gl) {
        if (fboId != 0) gl.glDeleteFramebuffers(1, new int[]{fboId}, 0);
        if (textureId != 0) gl.glDeleteTextures(1, new int[]{textureId}, 0);
        if (rboId != 0) gl.glDeleteRenderbuffers(1, new int[]{rboId}, 0);
        fboId = textureId = rboId = 0;
    }
}