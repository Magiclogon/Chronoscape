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


    // ... inside QuadMesh class ...

    public static void init(GL3 gl) {
//        float[] vertices = {
//            // position (x, y)   // uv (u, v)
//            -0.5f, -0.5f,        0f, 0f,
//             0.5f, -0.5f,        1f, 0f,
//             0.5f,  0.5f,        1f, 1f,
//            -0.5f,  0.5f,        0f, 1f
//        };
//
//        int[] indices = {
//            0, 1, 2,
//            2, 3, 0
//        };
//
//        int[] tmp = new int[1];
//
//        // 1. Create VAO
//        gl.glGenVertexArrays(1, tmp, 0);
//        VAO = tmp[0];
//        gl.glBindVertexArray(VAO);
//
//        // 2. Create VBO with DIRECT BUFFER
//        gl.glGenBuffers(1, tmp, 0);
//        VBO = tmp[0];
//        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO);
//
//        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertices.length * Float.BYTES)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer()
//                .put(vertices)
//                .flip();
//
//        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.length * Float.BYTES, vertexBuffer, GL.GL_STATIC_DRAW);
//
//        // 3. Create EBO with DIRECT BUFFER
//        gl.glGenBuffers(1, tmp, 0);
//        EBO = tmp[0];
//        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, EBO);
//
//        IntBuffer indexBuffer = ByteBuffer.allocateDirect(indices.length * Integer.BYTES)
//                .order(ByteOrder.nativeOrder())
//                .asIntBuffer()
//                .put(indices)
//                .flip();
//
//        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indices.length * Integer.BYTES, indexBuffer, GL.GL_STATIC_DRAW);
//
//        // 4. Set Attributes
//        int stride = 4 * Float.BYTES;
//
//        // Position (Location 0)
//        gl.glEnableVertexAttribArray(0);
//        gl.glVertexAttribPointer(0, 2, GL.GL_FLOAT, false, stride, 0L); // Use 0L for offset
//
//        // UV (Location 1)
//        gl.glEnableVertexAttribArray(1);
//        gl.glVertexAttribPointer(1, 2, GL.GL_FLOAT, false, stride, 2L * Float.BYTES);
//
//        // 5. UNBIND (Order matters!)
//        gl.glBindVertexArray(0); // Unbind VAO first to lock in EBO
//        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
//        
//        System.out.println("VAO=" + QuadMesh.VAO + " VBO=" + VBO + " EBO=" + EBO);
    	
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
