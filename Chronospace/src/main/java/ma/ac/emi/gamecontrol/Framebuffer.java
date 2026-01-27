package ma.ac.emi.gamecontrol;

import com.jogamp.opengl.*;

public class Framebuffer {

    private final int fbo;
    private final int texture;

    public Framebuffer(GL3 gl) {
        int[] id = new int[1];

        gl.glGenFramebuffers(1, id, 0);
        fbo = id[0];

        gl.glGenTextures(1, id, 0);
        texture = id[0];

        gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
        gl.glTexImage2D(
                GL.GL_TEXTURE_2D, 0,
                GL.GL_RGBA8,
                1920, 1080, 0,
                GL.GL_RGBA,
                GL.GL_UNSIGNED_BYTE,
                null
        );

        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fbo);
        gl.glFramebufferTexture2D(
                GL.GL_FRAMEBUFFER,
                GL.GL_COLOR_ATTACHMENT0,
                GL.GL_TEXTURE_2D,
                texture,
                0
        );

        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
    }

    public void bind(GL3 gl) {
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fbo);
    }

    public void unbind(GL3 gl) {
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
    }

    public int getTexture() {
        return texture;
    }
}
