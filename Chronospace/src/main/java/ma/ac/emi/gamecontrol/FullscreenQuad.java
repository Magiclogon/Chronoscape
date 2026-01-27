package ma.ac.emi.gamecontrol;

import com.jogamp.opengl.*;

public class FullscreenQuad {

    public FullscreenQuad(GL3 gl) {}

    public void draw(GL3 gl, int texture) {
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, 6);
    }
}
