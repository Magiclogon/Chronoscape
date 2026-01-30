package ma.ac.emi.glgraphics;

import java.nio.FloatBuffer;

import com.jogamp.opengl.GL3;

public class SpriteQuad {

    public static int VAO;

    public static void init(GL3 gl) {
        float[] vertices = {
            // pos       // uv
            0f, 1f,      0f, 0f,
            1f, 1f,      1f, 0f,
            1f, 0f,      1f, 1f,
            0f, 0f,      0f, 1f
        };

        int[] tmp = new int[1];
        gl.glGenVertexArrays(1, tmp, 0);
        VAO = tmp[0];
        gl.glBindVertexArray(VAO);

        gl.glGenBuffers(1, tmp, 0);
        int VBO = tmp[0];
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, VBO);

        gl.glBufferData(
                GL3.GL_ARRAY_BUFFER,
                vertices.length * Float.BYTES,
                FloatBuffer.wrap(vertices),
                GL3.GL_STATIC_DRAW
        );

        int stride = 4 * Float.BYTES;

        gl.glVertexAttribPointer(0, 2, GL3.GL_FLOAT, false, stride, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glVertexAttribPointer(1, 2, GL3.GL_FLOAT, false, stride, 2 * Float.BYTES);
        gl.glEnableVertexAttribArray(1);

        gl.glBindVertexArray(0);
    }
}

