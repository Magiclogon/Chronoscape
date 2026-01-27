package ma.ac.emi.gamecontrol;

import com.jogamp.opengl.*;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.glgraphics.Framebuffer;
import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.QuadMesh;
import ma.ac.emi.glgraphics.Shader;
import ma.ac.emi.glgraphics.SpriteQuad;
import ma.ac.emi.glgraphics.Texture;

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
    private Shader spriteShader;
    
    private GLGraphics glGraphics;
    
    private int width;
    private int height;

    private Texture texture;
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

        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
//        gl.glDisable(GL.GL_CULL_FACE);
        //gl.glDisable(GL.GL_DEPTH_TEST); // Since we are just drawing one 2D quad
        
        
//        sceneFBO = new Framebuffer(gl);
//        quad = new FullscreenQuad(gl);
        //postShader = Shader.load("post.vert", "post.frag", gl);
        
        glGraphics = new GLGraphics(gl);
        
//        testShader = Shader.load("test.vert", "test.frag", gl);
        //spriteShader = Shader.load("sprite.vert", "sprite.frag", gl);
//        QuadMesh.init(gl);
        //SpriteQuad.init(gl);
        texture = new Texture(gl, AssetsLoader.getSprite("projectiles/rocket.png"));
        
        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

    }
    
    Shader testShader;
    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        // --- 1) Render scene ---
        //sceneFBO.bind(gl);
        //gl.glClear(GL.GL_COLOR_BUFFER_BIT);

//        applyCamera(gl);
//
//        
        
//        testShader.use(gl);
//
//	     // Optional: identity matrices
//        float[] projection = ortho(
//                -width/2, width/2,
//                height/2, -height/2,
//                -1, 1
//        );
//	     float[] identity = new float[]{
//	         1,0,0,0,
//	         0,1,0,0,
//	         0,0,1,0,
//	         0,0,0,1
//	     };
//	     
//	     float[] view = camera.getViewMatrix();
//	     	     
//	     testShader.setMat4(gl, "uModel", identity);
//	     testShader.setMat4(gl, "uView", view);
//	     testShader.setMat4(gl, "uProjection", projection);
//	
//	     gl.glBindVertexArray(QuadMesh.VAO);
//
//	     gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, 4);
//	     gl.glBindVertexArray(0);
//	     
//	     
	     //drawSprite(gl, texture, 100, 100, 32, 32);
//	     drawSprite(gl, texture, 200, 100, 32, 32);
//        for (GameObject o : snapshot) {
//            //o.drawGL(gl); // NEW METHOD
//        }

        //sceneFBO.unbind(gl);

        // --- 2) Post-processing ---
        //gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        //postShader.use(gl);
        //quad.draw(gl, sceneFBO.getTexture());
        List<GameObject> snapshot;
    	synchronized (drawables) {
    		snapshot = new ArrayList<>(drawables);
  		}
        
        glGraphics.setCamera(camera);
        glGraphics.beginFrame(gl, width, height);
        //glGraphics.drawSprite(gl, texture, 100, 100, 32, 32);
        for (GameObject o : snapshot) {
        	o.drawGL(gl, glGraphics);
        }
        glGraphics.endFrame(gl);
    }

    private void applyCamera(GL3 gl) {
        // upload camera uniforms here (position / zoom)
    }

    @Override public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
    	GL3 gl = drawable.getGL().getGL3();
        width = w;
        height = h;
        gl.glViewport(0, 0, w, h);
    }
    @Override public void dispose(GLAutoDrawable d) {
    	GL3 gl = d.getGL().getGL3();
        //testShader.dispose(gl);
        //QuadMesh.dispose(gl);
    }
    
    public static float[] ortho(
            float left, float right,
            float bottom, float top,
            float near, float far
    ) {
        float[] m = new float[16];

        m[0]  = 2f / (right - left);
        m[5]  = 2f / (top - bottom);
        m[10] = -2f / (far - near);
        m[12] = -(right + left) / (right - left);
        m[13] = -(top + bottom) / (top - bottom);
        m[14] = -(far + near) / (far - near);
        m[15] = 1f;

        return m;
    }
    
    public void drawSprite(GL3 gl, Texture tex, float x, float y, float w, float h) {
        float[] model = {
            w, 0, 0, 0,
            0, h, 0, 0,
            0, 0, 1, 0,
            x, y, 0, 1
        };
        
        float[] projection = ortho(
                -width/2, width/2,
                height/2, -height/2,
                -1, 1
        );
        spriteShader.use(gl);
        
        spriteShader.setMat4(gl, "uView", camera.getViewMatrix());
	     spriteShader.setMat4(gl, "uProjection", projection);
        spriteShader.setMat4(gl, "uModel", model);

        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, tex.id);

        gl.glBindVertexArray(SpriteQuad.VAO);
        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, 4);
        gl.glBindVertexArray(0);
    }


}
