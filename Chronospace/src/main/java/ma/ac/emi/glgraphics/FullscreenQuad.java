package ma.ac.emi.glgraphics;

import com.jogamp.opengl.*;
import java.nio.FloatBuffer;

public class FullscreenQuad {

    private final int vao;
    private final int vbo;

    public FullscreenQuad(GL3 gl) {
        // Data: Position (x, y) and Texture Coordinates (u, v)
        float[] vertices = {
            // positions    // texCoords
            -1.0f,  1.0f,   0.0f, 1.0f, // Top Left
            -1.0f, -1.0f,   0.0f, 0.0f, // Bottom Left
             1.0f, -1.0f,   1.0f, 0.0f, // Bottom Right

            -1.0f,  1.0f,   0.0f, 1.0f, // Top Left
             1.0f, -1.0f,   1.0f, 0.0f, // Bottom Right
             1.0f,  1.0f,   1.0f, 1.0f  // Top Right
        };

        int[] buffers = new int[1];
        
        // Create and Bind VAO
        gl.glGenVertexArrays(1, buffers, 0);
        vao = buffers[0];
        gl.glBindVertexArray(vao);

        // Create and Bind VBO
        gl.glGenBuffers(1, buffers, 0);
        vbo = buffers[0];
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
        
        // Upload data
        FloatBuffer data = FloatBuffer.wrap(vertices);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, data, GL3.GL_STATIC_DRAW);

        // Attribute 0: Position (vec2)
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 2, GL3.GL_FLOAT, false, 4 * Float.BYTES, 0);

        // Attribute 1: TexCoords (vec2)
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 2, GL3.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);

        gl.glBindVertexArray(0); // Unbind
    }

    public void draw(GL3 gl, int texture) {
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, texture);
        
        gl.glBindVertexArray(vao);
        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 6);
        gl.glBindVertexArray(0);
    }

    public void dispose(GL3 gl) {
        gl.glDeleteVertexArrays(1, new int[]{vao}, 0);
        gl.glDeleteBuffers(1, new int[]{vbo}, 0);
    }

	public int getVAO() {
		return this.vao;
	}
}