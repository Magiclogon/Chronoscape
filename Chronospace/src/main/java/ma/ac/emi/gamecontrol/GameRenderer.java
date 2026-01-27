package ma.ac.emi.gamecontrol;

import com.jogamp.opengl.*;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.glgraphics.Framebuffer;
import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.Shader;
import ma.ac.emi.glgraphics.SpriteQuad;
import ma.ac.emi.glgraphics.post.BloomCombineEffect;
import ma.ac.emi.glgraphics.post.BloomExtractEffect;
import ma.ac.emi.glgraphics.post.BlurEffect;
import ma.ac.emi.glgraphics.post.ColorCorrectionEffect;
import ma.ac.emi.glgraphics.post.GammaEffect;
import ma.ac.emi.glgraphics.post.GammaToLinearEffect;
import ma.ac.emi.glgraphics.post.GrayscaleEffect;
import ma.ac.emi.glgraphics.post.PostProcessor;
import ma.ac.emi.glgraphics.post.SnapshotEffect;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameRenderer implements GLEventListener {

    private Camera camera;
    private final List<GameObject> drawables = Collections.synchronizedList(new ArrayList<>());
    private GLGraphics glGraphics;
    private int width, height;

    // Post-processing components
    private Framebuffer framebuffer;
    private FullscreenQuad quad;
    private Shader postShader;
	private PostProcessor postProcessor;
	
	private Color bg;

    public void setCamera(Camera camera) { this.camera = camera; }

    public void addDrawable(GameObject o) {
        o.setDrawn(true);
        if (!drawables.contains(o)) drawables.add(o);
    }

    public void removeDrawable(GameObject o) { o.setDrawn(false); }

    public void update() {
        drawables.removeIf(d -> !d.isDrawn());
        Collections.sort(drawables);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        glGraphics = new GLGraphics(gl);

        postProcessor = new PostProcessor(gl, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
        ColorCorrectionEffect colorEffect = new ColorCorrectionEffect(gl);
        //colorEffect.setContrast(1.05f);      
        colorEffect.setSaturation(0.75f);   
        colorEffect.setBrightness(-0.01f);
        
        postProcessor.addEffect(new GammaToLinearEffect(gl));
        //postProcessor.addEffect(colorEffect);
        //postProcessor.addEffect(new BlurEffect(gl, true));
        postProcessor.addEffect(new SnapshotEffect(postProcessor));
        postProcessor.addEffect(new BloomExtractEffect(gl, 0.05f));
        postProcessor.addEffect(new BlurEffect(gl, true));
        postProcessor.addEffect(new BlurEffect(gl, false));
        postProcessor.addEffect(new BloomCombineEffect(gl, postProcessor.getSnapshotTextureId()));
        postProcessor.addEffect(new GammaEffect(gl));
        
        bg = GameController.getInstance().getWorldManager().getCurrentWorld().getVoidColor();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        // Pass 1: Draw scene to Framebuffer
        postProcessor.capture(gl);
        	gl.glClearColor(bg.getRed()/255f, bg.getGreen()/255f, bg.getBlue()/255f, bg.getAlpha()/255f);
	        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	
	        List<GameObject> snapshot;
	        synchronized (drawables) {
	            snapshot = new ArrayList<>(drawables);
	        }
	
	        glGraphics.setCamera(camera);
	        glGraphics.beginFrame(gl, width, height);
	        for (GameObject o : snapshot) {
	            o.drawGL(gl, glGraphics);
	        }
	        glGraphics.endFrame(gl);
        postProcessor.release(gl);

        // Pass 2: Draw Framebuffer texture to the screen using SpriteQuad
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        postProcessor.render(gl);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL3 gl = drawable.getGL().getGL3();
        width = w;
        height = h;
        gl.glViewport(0, 0, w, h);
        
        postProcessor.resize(gl, w, h);
    }

    @Override 
    public void dispose(GLAutoDrawable d) {
    }
}