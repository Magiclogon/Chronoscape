package ma.ac.emi.gamecontrol;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;

/**
 * Enhanced GamePanel with GPU post-processing support.
 * 
 * This class renders your game to an offscreen buffer, then applies
 * GPU-accelerated post-processing effects like bloom, color correction, etc.
 */
@Getter
@Setter
public class GamePanel extends GLJPanel implements GLEventListener {

    public static final int TILE_SIZE = 32;
    public List<GameObject> drawables;
    private Camera camera;
    
    // Offscreen rendering buffer (your game renders here)
    private BufferedImage gameBuffer;
    private Texture gameTexture;
    private Graphics2D gameGraphics;
    private int renderWidth;
    private int renderHeight;
    
    // OpenGL resources for post-processing
    private int shaderProgram;
    private int frameTexture;
    private int vao, vbo;
    private boolean glInitialized = false;
    
    // Post-processing parameters (can be adjusted at runtime)
    private float bloomIntensity = 0.3f;
    private float bloomThreshold = 0.8f;
    private float saturation = 1.0f;
    private float brightness = 1.0f;
    private float contrast = 1.0f;
    
    public GamePanel() {
        this(640, 480); // Default resolution
    }
    
    public GamePanel(int width, int height) {
        super(createGLCapabilities());
        
        this.renderWidth = width;
        this.renderHeight = height;
        
        // Create offscreen buffer for game rendering
        gameBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        gameGraphics = gameBuffer.createGraphics();
        gameGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        // Input handlers
        addMouseListener(MouseHandler.getInstance());
        addMouseMotionListener(MouseHandler.getInstance());
        addMouseWheelListener(MouseHandler.getInstance());
        KeyHandler.getInstance().setupKeyBindings(this);
        
        this.drawables = new ArrayList<>();
        drawables = Collections.synchronizedList(drawables);
        
        // Setup OpenGL
        addGLEventListener(this);
        setDoubleBuffered(true);
    }
    
    private static GLCapabilities createGLCapabilities() {
        GLProfile profile = GLProfile.get(GLProfile.GL4bc); 
        GLCapabilities caps = new GLCapabilities(profile);
        caps.setDoubleBuffered(true);
        return caps;
    }
    
    @Override
    public void addNotify() {
        super.addNotify();          // makes the component realised → getWidth/getHeight valid
        SwingUtilities.invokeLater(() -> {
            renderGameToBuffer();   // create the very first buffer with the right size
        });
    }
    
    /**
     * This replaces the old paintComponent.
     * Now we render to offscreen buffer, then let OpenGL handle display.
     */
    public void renderGameToBuffer() {
    	int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        // ❗❗❗  ADD THIS BLOCK  ❗❗❗
        if (gameBuffer == null ||
            gameBuffer.getWidth() != w ||
            gameBuffer.getHeight() != h) {
            gameBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            gameGraphics = gameBuffer.createGraphics();
            gameGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                         RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        try {
            GameController.getInstance();
            GameController.draw.acquire();
            
            renderWidth = getWidth();
            renderHeight = getHeight();
            
            gameBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            gameGraphics = gameBuffer.createGraphics();
            gameGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            
            // Clear background
            gameGraphics.setColor(GameController.getInstance().getWorldManager().getCurrentWorld().getVoidColor());
            gameGraphics.fillRect(0, 0, renderWidth, renderHeight);
            
            if (camera == null) {
                System.out.println("Camera is null");
                GameController.update.release();
                return;
            }
            
            AffineTransform oldTransform = gameGraphics.getTransform();
            
            AffineTransform camTx;
            synchronized (camera) {
                camTx = new AffineTransform(camera.getCamTransform());
            }
            gameGraphics.transform(camTx);
            
            GameObject o = null;
            try {
                List<GameObject> snapshot;
                synchronized (drawables) {
                    snapshot = new ArrayList<>(drawables);
                }
                
                for (GameObject d : snapshot) {
                    o = d;
                    d.draw(gameGraphics);
                }
                
            } catch (Exception e) {
                System.err.println("drawing error from: " + o.getClass());
                e.printStackTrace();
            }
            
            gameGraphics.setTransform(oldTransform);
            
            gameGraphics.dispose();
            GameController.getInstance();
            GameController.update.release();
            
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        
        gameTexture = AWTTextureIO.newTexture(gl.getGLProfile(), gameBuffer, false);
        
        // Set basic texture parameters
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
    }
    
    @Override
    public void display(GLAutoDrawable drawable) { 

        this.renderGameToBuffer();
        GL2 gl = drawable.getGL().getGL2();


        TextureData textureData = AWTTextureIO.newTextureData(
            gl.getGLProfile(), gameBuffer, false
        );
        
        gameTexture.updateImage(gl, textureData); 
        
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gameTexture.enable(gl);
        gameTexture.bind(gl); 
        
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        
        float left = gameTexture.getImageTexCoords().left();
        float right = gameTexture.getImageTexCoords().right();
        float top = gameTexture.getImageTexCoords().top();
        float bottom = gameTexture.getImageTexCoords().bottom();

        gl.glBegin(GL2.GL_QUADS);
        // Top-Left Vertex
        gl.glTexCoord2f(left, top); 
        gl.glVertex3f(-1.0f, 1.0f, 0.0f); 
        // Top-Right Vertex
        gl.glTexCoord2f(right, top); 
        gl.glVertex3f(1.0f, 1.0f, 0.0f);
        // Bottom-Right Vertex
        gl.glTexCoord2f(right, bottom); 
        gl.glVertex3f(1.0f, -1.0f, 0.0f);
        // Bottom-Left Vertex
        gl.glTexCoord2f(left, bottom); 
        gl.glVertex3f(-1.0f, -1.0f, 0.0f);
        
        gl.glEnd();

        gameTexture.disable(gl);
        gl.glFlush();
        
        // --- D. SHADER POST-PROCESSING ---
        // If you enable your shader system (which you haven't fully done yet), 
        // this is where you would call glUseProgram() and draw the final screen quad.
    }
    
    private void uploadTextureData(GL3 gl) {
        // Convert BufferedImage to byte buffer
        int[] pixels = gameBuffer.getRGB(0, 0, renderWidth, renderHeight, null, 0, renderWidth);
        ByteBuffer buffer = ByteBuffer.allocate(renderWidth * renderHeight * 3);
        
        for (int i = 0; i < pixels.length; i++) {
            buffer.put((byte)((pixels[i] >> 16) & 0xFF)); // R
            buffer.put((byte)((pixels[i] >> 8) & 0xFF));  // G
            buffer.put((byte)(pixels[i] & 0xFF));         // B
        }
        buffer.flip();
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, frameTexture);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, renderWidth, renderHeight, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, buffer);
    }
    
    private int createShaderProgram(GL3 gl, String vertexSource, String fragmentSource) {
        int vertexShader = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
        gl.glShaderSource(vertexShader, 1, new String[]{vertexSource}, null);
        gl.glCompileShader(vertexShader);
        checkShaderCompilation(gl, vertexShader, "Vertex");
        
        int fragmentShader = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fragmentShader, 1, new String[]{fragmentSource}, null);
        gl.glCompileShader(fragmentShader);
        checkShaderCompilation(gl, fragmentShader, "Fragment");
        
        int program = gl.glCreateProgram();
        gl.glAttachShader(program, vertexShader);
        gl.glAttachShader(program, fragmentShader);
        gl.glLinkProgram(program);
        checkProgramLinking(gl, program);
        
        gl.glDeleteShader(vertexShader);
        gl.glDeleteShader(fragmentShader);
        
        return program;
    }
    
    private void checkShaderCompilation(GL3 gl, int shader, String type) {
        int[] compiled = new int[1];
        gl.glGetShaderiv(shader, GL3.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            int[] logLength = new int[1];
            gl.glGetShaderiv(shader, GL3.GL_INFO_LOG_LENGTH, logLength, 0);
            byte[] log = new byte[logLength[0]];
            gl.glGetShaderInfoLog(shader, logLength[0], null, 0, log, 0);
            System.err.println(type + " shader compilation error: " + new String(log));
        }
    }
    
    private void checkProgramLinking(GL3 gl, int program) {
        int[] linked = new int[1];
        gl.glGetProgramiv(program, GL3.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0) {
            int[] logLength = new int[1];
            gl.glGetProgramiv(program, GL3.GL_INFO_LOG_LENGTH, logLength, 0);
            byte[] log = new byte[logLength[0]];
            gl.glGetProgramInfoLog(program, logLength[0], null, 0, log, 0);
            System.err.println("Program linking error: " + new String(log));
        }
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glViewport(0, 0, width, height);        
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        if (glInitialized) {
            gl.glDeleteProgram(shaderProgram);
            gl.glDeleteTextures(1, new int[]{frameTexture}, 0);
            gl.glDeleteVertexArrays(1, new int[]{vao}, 0);
            gl.glDeleteBuffers(1, new int[]{vbo}, 0);
        }
    }
    
    // Existing methods from your original GamePanel
    public void update(double step) {
        drawables.removeIf(d -> !d.isDrawn());
        Collections.sort(drawables);
    }
    
    public void addDrawable(GameObject gameObject) {
        synchronized (drawables) {
            gameObject.setDrawn(true);
            if (drawables.contains(gameObject)) return;
            this.drawables.add(gameObject);
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
    
    // Post-processing control methods
    public void setBloom(float intensity, float threshold) {
        this.bloomIntensity = Math.max(0, Math.min(1, intensity));
        this.bloomThreshold = Math.max(0, Math.min(1, threshold));
    }
    
    public void setColorCorrection(float saturation, float brightness, float contrast) {
        this.saturation = Math.max(0, saturation);
        this.brightness = Math.max(0, brightness);
        this.contrast = Math.max(0, contrast);
    }
    
    public void disablePostProcessing() {
        this.bloomIntensity = 0;
        this.saturation = 1.0f;
        this.brightness = 1.0f;
        this.contrast = 1.0f;
    }
}