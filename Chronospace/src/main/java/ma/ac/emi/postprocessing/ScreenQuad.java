package ma.ac.emi.postprocessing;

import com.jogamp.opengl.GL3;
import com.jogamp.common.nio.Buffers;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ScreenQuad {
    
    private int vao;
    private int vbo;
    
    // Normal texture coordinates (for Java2D BufferedImage - origin at top-left)
    private static final float[] QUAD_VERTICES_NORMAL = {
        // positions (x, y)    // texture coords (s, t)
        -1.0f,  1.0f,          0.0f, 0.0f,  // Top-Left
         1.0f,  1.0f,          1.0f, 0.0f,  // Top-Right
         1.0f, -1.0f,          1.0f, 1.0f,  // Bottom-Right
        -1.0f, -1.0f,          0.0f, 1.0f   // Bottom-Left
    };
    
    // Flipped texture coordinates (for OpenGL FBO textures - origin at bottom-left)
    private static final float[] QUAD_VERTICES_FLIPPED = {
        // positions (x, y)    // texture coords (s, t)
        -1.0f,  1.0f,          0.0f, 1.0f,  // Top-Left -> Bottom of texture
         1.0f,  1.0f,          1.0f, 1.0f,  // Top-Right -> Bottom of texture
         1.0f, -1.0f,          1.0f, 0.0f,  // Bottom-Right -> Top of texture
        -1.0f, -1.0f,          0.0f, 0.0f   // Bottom-Left -> Top of texture
    };
    
    public void initialize(GL3 gl3) {
        IntBuffer vaoBuffer = Buffers.newDirectIntBuffer(1);
        IntBuffer vboBuffer = Buffers.newDirectIntBuffer(1);
        
        gl3.glGenVertexArrays(1, vaoBuffer);
        gl3.glGenBuffers(1, vboBuffer);
        vao = vaoBuffer.get(0);
        vbo = vboBuffer.get(0);
        
        gl3.glBindVertexArray(vao);
        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
        
        // Start with normal coordinates
        FloatBuffer vertexBuffer = Buffers.newDirectFloatBuffer(QUAD_VERTICES_NORMAL.length);
        vertexBuffer.put(QUAD_VERTICES_NORMAL).flip();
        gl3.glBufferData(GL3.GL_ARRAY_BUFFER, 
            QUAD_VERTICES_NORMAL.length * Buffers.SIZEOF_FLOAT, 
            vertexBuffer, 
            GL3.GL_DYNAMIC_DRAW); // Changed to DYNAMIC since we'll update it
        
        // Position attribute (layout 0)
        gl3.glVertexAttribPointer(0, 2, GL3.GL_FLOAT, false, 
            4 * Buffers.SIZEOF_FLOAT, 0);
        gl3.glEnableVertexAttribArray(0);
        
        // Texture coordinate attribute (layout 1)
        gl3.glVertexAttribPointer(1, 2, GL3.GL_FLOAT, false, 
            4 * Buffers.SIZEOF_FLOAT, 2 * Buffers.SIZEOF_FLOAT);
        gl3.glEnableVertexAttribArray(1);
        
        gl3.glBindVertexArray(0);
    }
    
    /**
     * Draw with normal texture coordinates (for Java2D textures)
     */
    public void draw(GL3 gl3) {
        draw(gl3, false);
    }
    
    /**
     * Draw with optional Y-flip for framebuffer textures
     * @param flipY true for FBO textures, false for Java2D textures
     */
    public void draw(GL3 gl3, boolean flipY) {
        // Update VBO if we need different coordinates
        float[] vertices = flipY ? QUAD_VERTICES_FLIPPED : QUAD_VERTICES_NORMAL;
        
        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo);
        FloatBuffer vertexBuffer = Buffers.newDirectFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        gl3.glBufferSubData(GL3.GL_ARRAY_BUFFER, 0, 
            vertices.length * Buffers.SIZEOF_FLOAT, vertexBuffer);
        
        gl3.glBindVertexArray(vao);
        gl3.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, 4);
        gl3.glBindVertexArray(0);
    }
    
    public void dispose(GL3 gl3) {
        gl3.glDeleteVertexArrays(1, new int[]{vao}, 0);
        gl3.glDeleteBuffers(1, new int[]{vbo}, 0);
    }
}