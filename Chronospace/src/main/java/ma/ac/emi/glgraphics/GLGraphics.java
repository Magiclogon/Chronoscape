package ma.ac.emi.glgraphics;

import com.jogamp.opengl.GL3;
import ma.ac.emi.camera.Camera;

public class GLGraphics {

    private final Shader spriteShader;
    private Camera camera;

    public GLGraphics(GL3 gl) {
        spriteShader = Shader.load("sprite.vert", "sprite.frag", gl);
        SpriteQuad.init(gl);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void beginFrame(GL3 gl, int width, int height) {
        //gl.glViewport(0, 0, width, height);
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);

        spriteShader.use(gl);
        spriteShader.setMat4(gl, "uProjection", Mat4.ortho(-width/2, width/2, height/2, -height/2));
        spriteShader.setMat4(gl, "uView", camera != null ? camera.getViewMatrix() : Mat4.identity());
    }

    public void drawSprite(GL3 gl, Texture texture, float x, float y, float w, float h) {
        float[] model = Mat4.transform(x, y, w, h);
        
        drawSprite(gl, texture, model);
    }

    public void endFrame(GL3 gl) {
        gl.glBindVertexArray(0);
    }

	public void drawSprite(GL3 gl, Texture texture, float[] model) {
        spriteShader.setMat4(gl, "uModel", model);

        gl.glBindTexture(GL3.GL_TEXTURE_2D, texture.id);
        gl.glBindVertexArray(SpriteQuad.VAO);
        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, 4);
	}
}

