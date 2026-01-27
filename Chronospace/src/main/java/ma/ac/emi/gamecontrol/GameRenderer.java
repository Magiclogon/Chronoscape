package ma.ac.emi.gamecontrol;

import com.jogamp.opengl.*;
import ma.ac.emi.camera.Camera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameRenderer implements GLEventListener {

    private Camera camera;
    private final List<GameObject> drawables =
            Collections.synchronizedList(new ArrayList<>());

    private Framebuffer sceneFBO;
    private FullscreenQuad quad;
    private Shader postShader;

    public void setCamera(Camera camera) {
    	this.camera = camera;
    }

    // === CALLED FROM GAME LOGIC THREAD ===

    public void addDrawable(GameObject o) {
        o.setDrawn(true);
        if (!drawables.contains(o)) drawables.add(o);
    }

    public void removeDrawable(GameObject o) {
        o.setDrawn(false);
    }

    public void update() {
        drawables.removeIf(d -> !d.isDrawn());
        Collections.sort(drawables);
    }

    // === OPENGL THREAD ===

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

//        gl.glEnable(GL.GL_BLEND);
//        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
//        gl.glDisable(GL.GL_CULL_FACE);
        //gl.glDisable(GL.GL_DEPTH_TEST); // Since we are just drawing one 2D quad
        
        
//        sceneFBO = new Framebuffer(gl);
//        quad = new FullscreenQuad(gl);
        //postShader = Shader.load("post.vert", "post.frag", gl);
        testShader = Shader.load("test.vert", "test.frag", gl);
        QuadMesh.init(gl);
        
        gl.glClearColor(1, 0, 0, 1);

    }
    
    Shader testShader;
    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        // --- 1) Render scene ---
        //sceneFBO.bind(gl);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

//        applyCamera(gl);
//
//        List<GameObject> snapshot;
//        synchronized (drawables) {
//            snapshot = new ArrayList<>(drawables);
//        }
        
        testShader.use(gl);

//	     // Optional: identity matrices
//	     float[] identity = new float[]{
//	         1,0,0,0,
//	         0,1,0,0,
//	         0,0,1,0,
//	         0,0,0,1
//	     };
//	     testShader.setMat4(gl, "uModel", identity);
//	     testShader.setMat4(gl, "uView", identity);
//	     testShader.setMat4(gl, "uProjection", identity);
	
	     // --- Draw quad ---
	     gl.glBindVertexArray(QuadMesh.VAO);

	     gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, 4);
	     int error = gl.glGetError();
	     if (error != GL.GL_NO_ERROR) {
	         System.out.println("OpenGL Error: " + error);
	     }
	     System.out.println("hello");
	     gl.glBindVertexArray(0);
//        for (GameObject o : snapshot) {
//            //o.drawGL(gl); // NEW METHOD
//        }

        //sceneFBO.unbind(gl);

        // --- 2) Post-processing ---
        //gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        //postShader.use(gl);
        //quad.draw(gl, sceneFBO.getTexture());
    }

    private void applyCamera(GL3 gl) {
        // upload camera uniforms here (position / zoom)
    }

    @Override public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
    	GL3 gl = drawable.getGL().getGL3();
        gl.glViewport(0, 0, w, h);
    }
    @Override public void dispose(GLAutoDrawable d) {
    	GL3 gl = d.getGL().getGL3();
        testShader.dispose(gl);
        QuadMesh.dispose(gl);
    }
}
