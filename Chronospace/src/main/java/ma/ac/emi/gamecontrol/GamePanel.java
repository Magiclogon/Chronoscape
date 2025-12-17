package ma.ac.emi.gamecontrol;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.postprocessing.GLCapabilitiesFactory;
import ma.ac.emi.postprocessing.GameRenderer;
import ma.ac.emi.postprocessing.PostProcessingManager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class GamePanel extends GLJPanel implements GLEventListener {
    
    public static final int TILE_SIZE = 32;
    public List<GameObject> drawables;
    private Camera camera;
    
    private final GameRenderer gameRenderer;
    private final PostProcessingManager postProcessingManager;
    
    public GamePanel() {
        this(640, 480);
    }
    
    public GamePanel(int width, int height) {
        super(GLCapabilitiesFactory.createCapabilities());
        
        this.drawables = Collections.synchronizedList(new ArrayList<>());
        this.gameRenderer = new GameRenderer(width, height);
        this.postProcessingManager = new PostProcessingManager();
        
        setColorCorrection(0.95f, 0.5f, 1f, -10);
        
        setupInputHandlers();
        addGLEventListener(this);
        setDoubleBuffered(true);
        
        setBackground(Color.RED);
    }
    
    private void setupInputHandlers() {
        addMouseListener(MouseHandler.getInstance());
        addMouseMotionListener(MouseHandler.getInstance());
        addMouseWheelListener(MouseHandler.getInstance());
        KeyHandler.getInstance().setupKeyBindings(this);
    }
    
    
    @Override
    public void init(GLAutoDrawable drawable) {
        postProcessingManager.initialize(drawable, gameRenderer.getGameBuffer());
        
        GL gl = drawable.getGL();
        if (gl.isGL3()) {
            GL3 gl3 = gl.getGL3();
            
            // Add additional passes here:
            /*postProcessingManager.addShaderPass(gl3, 
            		"/shaders/screen.vert", 
            		"/shaders/basic.frag");
 
            /*postProcessingManager.addShaderPass(gl3, 
            		"/shaders/screen.vert", 
            		"/shaders/linear_space.frag");
            
            postProcessingManager.addShaderPass(gl3, 
            		"/shaders/screen.vert",
            		"/shaders/color_correction.frag",
            	(g, program) -> {
                    gl3.glUniform1i(gl3.glGetUniformLocation(program, "screenTexture"), 0);
                    gl3.glUniform1f(gl3.glGetUniformLocation(program, "uSaturation"), postProcessingManager.getSaturation());
                    gl3.glUniform1f(gl3.glGetUniformLocation(program, "uBrightness"), postProcessingManager.getBrightness());
                    gl3.glUniform1f(gl3.glGetUniformLocation(program, "uContrast"), postProcessingManager.getContrast());
                    gl3.glUniform1f(gl3.glGetUniformLocation(program, "uHue"), postProcessingManager.getHue());
            	});

            postProcessingManager.addShaderPass(gl3,
                "/shaders/screen.vert",
                "/shaders/bloom.frag",
                (g, program) -> {
                    g.glUniform1f(g.glGetUniformLocation(program, "uBloomIntensity"), 0.5f);
                    g.glUniform1f(g.glGetUniformLocation(program, "uBloomThreshold"), 0.8f);
                    
                });
            
            postProcessingManager.addShaderPass(gl3, 
            		"/shaders/screen.vert", 
            		"/shaders/gamma_correction.frag");
            		*/
            
        }
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
    	System.out.println("=============");
    	System.out.println(GameController.getInstance().getWindow().getWidth() + " " + GameController.getInstance().getWindow().getHeight());
    	System.out.println(getWidth() + " " + getHeight());
    	System.out.println(gameRenderer.getGameBuffer().getWidth() + " " + gameRenderer.getGameBuffer().getHeight());
        //gameRenderer.renderToBuffers(GameController.getInstance().getWindow().getWidth(), 
        //		GameController.getInstance().getWindow().getHeight(), camera, drawables);
        gameRenderer.renderToBuffers(getWidth(), getHeight(), camera, drawables);
        
        postProcessingManager.render(drawable, gameRenderer.getGameBuffer());
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        drawable.getGL().glViewport(0, 0, width, height);
        
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
        postProcessingManager.dispose(drawable);
    }
    
    public void update(double step) {
        drawables.removeIf(d -> !d.isDrawn());
        Collections.sort(drawables);
    }
    
    public void addDrawable(GameObject gameObject) {
        synchronized (drawables) {
            gameObject.setDrawn(true);
            if (!drawables.contains(gameObject)) {
                drawables.add(gameObject);
            }
        }
    }
    
    public void removeDrawable(GameObject gameObject) {
        gameObject.setDrawn(false);
    }
    
    public void removeAllDrawables() {
        synchronized (drawables) {
            drawables.forEach(d -> d.setDrawn(false));
        }
    }
    
    public void setColorCorrection(float saturation, float brightness, float contrast, float hue) {
        postProcessingManager.setColorCorrection(saturation, brightness, contrast, hue);
    }
    
    public void disablePostProcessing() {
        postProcessingManager.disablePostProcessing();
    }
}
