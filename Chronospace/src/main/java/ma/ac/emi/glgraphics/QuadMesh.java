package ma.ac.emi.glgraphics;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import java.nio.ByteBuffer;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class QuadMesh {

    public static int VAO;
    private static int VBO;
    private static int EBO;

    public static void init(GL3 gl) {
    	
    	// Quad vertices (two triangles)
    	float[] vertices = {
    		    0f,   0f,   0f,
    		    100f, 0f,   0f,
    		    100f, 100f, 0f,
    		    0f,   100f, 0f
    		};

        
        // Create VAO
        int[] vaos = new int[1];
        gl.glGenVertexArrays(1, vaos, 0);
        VAO = vaos[0];
        gl.glBindVertexArray(VAO);
        
        // Create VBO
        int[] vbos = new int[1];
        gl.glGenBuffers(1, vbos, 0);
        VBO = vbos[0];
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, VBO);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertices.length * Float.BYTES, 
                        FloatBuffer.wrap(vertices), GL3.GL_STATIC_DRAW);
        
        // Configure vertex attributes
        gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 3 * Float.BYTES, 0);
        gl.glEnableVertexAttribArray(0);
        
        gl.glBindVertexArray(0);

    }


	public static void dispose(GL3 gl) {
        gl.glDeleteVertexArrays(1, new int[]{VAO}, 0);
        gl.glDeleteBuffers(1, new int[]{VBO}, 0);
		
	}
  
}
