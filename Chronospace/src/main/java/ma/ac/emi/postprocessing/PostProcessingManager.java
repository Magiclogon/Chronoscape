package ma.ac.emi.postprocessing;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;

@Getter
@Setter
public class PostProcessingManager {
    
    private Texture gameTexture;
    private List<ShaderPass> shaderPasses;
    private ScreenQuad screenQuad;
    private boolean initialized = false;
    
    // Framebuffer objects for ping-pong rendering
    private int[] fbos;
    private int[] fboTextures;
    private int numFBOs = 2; // For ping-pong
    
    // Post-processing parameters (for default shader)
    private float saturation = 1.0f;
    private float brightness = 1.0f;
    private float contrast = 1.0f;
    private float hue = 0;
    
    public PostProcessingManager() {
        shaderPasses = new ArrayList<>();
    }
    
    /**
     * Initialize with default shaders
     */
    public void initialize(GLAutoDrawable drawable, BufferedImage initialBuffer) {
        initialize(drawable, initialBuffer, null, null);
    }
    
    /**
     * Initialize with custom shaders
     * @param vertexShaderPath Path to custom vertex shader (null for default)
     * @param fragmentShaderPath Path to custom fragment shader (null for default)
     */
    public void initialize(GLAutoDrawable drawable, BufferedImage initialBuffer,
                          String vertexShaderPath, String fragmentShaderPath) {
        GL gl = drawable.getGL();
        
        if (!gl.isGL3()) {
            System.err.println("GL3+ profile required for post-processing. Cannot proceed.");
            return;
        }
        
        GL3 gl3 = gl.getGL3();
        
        // Initialize game texture
        gameTexture = AWTTextureIO.newTexture(gl3.getGLProfile(), initialBuffer, false);
        gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
        gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
        
        // Initialize screen quad
        screenQuad = new ScreenQuad();
        screenQuad.initialize(gl3);
        
        // Initialize framebuffers for multi-pass rendering
        initializeFramebuffers(gl3, initialBuffer.getWidth(), initialBuffer.getHeight());
        
        // Add default shader pass if custom shaders provided
        if (vertexShaderPath != null || fragmentShaderPath != null) {
            String vertPath = (vertexShaderPath != null) ? vertexShaderPath : "/shaders/screen.vert";
            String fragPath = (fragmentShaderPath != null) ? fragmentShaderPath : "/shaders/basic.frag";
            addShaderPass(gl3, vertPath, fragPath);
        } else {
            // Add default color correction pass
            addShaderPass(gl3, "/shaders/screen.vert", "/shaders/basic.frag");
        }
        
        initialized = true;
    }
    
    /**
     * Add a shader pass to the rendering pipeline
     */
    public void addShaderPass(GL3 gl3, String vertexPath, String fragmentPath) {
        ShaderPass pass = new ShaderPass();
        pass.initialize(gl3, vertexPath, fragmentPath);
        shaderPasses.add(pass);
    }
    
    /**
     * Add a shader pass with custom uniforms
     */
    public void addShaderPass(GL3 gl3, String vertexPath, String fragmentPath, 
                             ShaderUniformSetter uniformSetter) {
        ShaderPass pass = new ShaderPass(uniformSetter);
        pass.initialize(gl3, vertexPath, fragmentPath);
        shaderPasses.add(pass);
    }
    
    /**
     * Remove all shader passes
     */
    public void clearShaderPasses(GL3 gl3) {
        for (ShaderPass pass : shaderPasses) {
            pass.dispose(gl3);
        }
        shaderPasses.clear();
    }
    
    /**
     * Remove a specific shader pass
     */
    public void removeShaderPass(GL3 gl3, int index) {
        if (index >= 0 && index < shaderPasses.size()) {
            shaderPasses.get(index).dispose(gl3);
            shaderPasses.remove(index);
        }
    }
    
    /**
     * Initialize framebuffers for ping-pong rendering
     */
    private void initializeFramebuffers(GL3 gl3, int width, int height) {
        fbos = new int[numFBOs];
        fboTextures = new int[numFBOs];
        
        gl3.glGenFramebuffers(numFBOs, fbos, 0);
        gl3.glGenTextures(numFBOs, fboTextures, 0);
        
        for (int i = 0; i < numFBOs; i++) {
            // Setup texture
            gl3.glBindTexture(GL3.GL_TEXTURE_2D, fboTextures[i]);
            gl3.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA16F, width, height, 0, 
                    GL3.GL_RGBA, GL3.GL_FLOAT, null);
            gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
            gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
            gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
            gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
            
            // Setup framebuffer
            gl3.glBindFramebuffer(GL3.GL_FRAMEBUFFER, fbos[i]);
            gl3.glFramebufferTexture2D(GL3.GL_FRAMEBUFFER, GL3.GL_COLOR_ATTACHMENT0, 
                                      GL3.GL_TEXTURE_2D, fboTextures[i], 0);
            
            // Check framebuffer status
            int status = gl3.glCheckFramebufferStatus(GL3.GL_FRAMEBUFFER);
            if (status != GL3.GL_FRAMEBUFFER_COMPLETE) {
                System.err.println("Framebuffer " + i + " is not complete: " + status);
            }
        }
        
        gl3.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);
        gl3.glBindTexture(GL3.GL_TEXTURE_2D, 0);
    }
    
    /**
     * Resize framebuffers (call when window resizes)
     */
    public void resizeFramebuffers(GL3 gl3, int width, int height) {
        if (!initialized) return;
        
        // Delete old framebuffers
        gl3.glDeleteFramebuffers(numFBOs, fbos, 0);
        gl3.glDeleteTextures(numFBOs, fboTextures, 0);
        
        // Recreate with new size
        initializeFramebuffers(gl3, width, height);
    }

    public void render(GLAutoDrawable drawable, BufferedImage gameBuffer) {
        GL gl = drawable.getGL();
        if (!initialized || !gl.isGL3()) return;
        
        GL3 gl3 = gl.getGL3();
        
        // Update game texture with new frame
        TextureData textureData = AWTTextureIO.newTextureData(
            gl3.getGLProfile(), gameBuffer, false
        );
        gameTexture.updateImage(gl3, textureData);
        
        if (shaderPasses.isEmpty()) {
            // No shader passes, just render the texture directly
            renderDirect(gl3);
            return;
        }
        
        // Get actual viewport dimensions
        int[] viewport = new int[4];
        gl3.glGetIntegerv(GL3.GL_VIEWPORT, viewport, 0);
        int viewportWidth = viewport[2];
        int viewportHeight = viewport[3];
        
        // Multi-pass rendering with ping-pong buffers
        int currentSource = -1; // -1 means use gameTexture
        int currentTarget = 0;
        
        for (int i = 0; i < shaderPasses.size(); i++) {
            ShaderPass pass = shaderPasses.get(i);
            boolean isLastPass = (i == shaderPasses.size() - 1);
            
            // Bind target framebuffer (or screen for last pass)
            if (isLastPass) {
                gl3.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);
                gl3.glViewport(0, 0, viewportWidth, viewportHeight);
            } else {
                gl3.glBindFramebuffer(GL3.GL_FRAMEBUFFER, fbos[currentTarget]);
                gl3.glViewport(0, 0, gameBuffer.getWidth(), gameBuffer.getHeight());
            }
            
            gl3.glClear(GL3.GL_COLOR_BUFFER_BIT);
            
            // Disable depth test for 2D post-processing
            gl3.glDisable(GL3.GL_DEPTH_TEST);
            
            // Use shader
            pass.use(gl3);
            
            // Bind source texture
            gl3.glActiveTexture(GL3.GL_TEXTURE0);
            if (currentSource == -1) {
                gameTexture.bind(gl3);
            } else {
                gl3.glBindTexture(GL3.GL_TEXTURE_2D, fboTextures[currentSource]);
            }
            
            // Set default uniforms
            gl3.glUniform1i(gl3.glGetUniformLocation(pass.getShaderProgram(), "screenTexture"), 0);
            
            // Set custom uniforms (for default color correction or custom passes)
            if (pass.getUniformSetter() != null) {
                pass.getUniformSetter().setUniforms(gl3, pass.getShaderProgram());
            }
            
            // Draw quad
            screenQuad.draw(gl3, currentSource != -1);
            
            pass.release(gl3);
            
            // Ping-pong: swap source and target
            currentSource = currentTarget;
            currentTarget = 1 - currentTarget;
        }
        
        gl3.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);
        gl3.glFlush();
    }
    
    /**
     * Direct rendering without post-processing
     */
    private void renderDirect(GL3 gl3) {
        gl3.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        gl3.glActiveTexture(GL3.GL_TEXTURE0);
        gameTexture.bind(gl3);
        screenQuad.draw(gl3);
        gl3.glFlush();
    }
    
    /**
     * Set default color correction uniforms
     */
    private void setDefaultUniforms(GL3 gl3, int program) {
        gl3.glUniform1f(gl3.glGetUniformLocation(program, "uSaturation"), saturation);
        gl3.glUniform1f(gl3.glGetUniformLocation(program, "uBrightness"), brightness);
        gl3.glUniform1f(gl3.glGetUniformLocation(program, "uContrast"), contrast);
    }
    
    /**
     * Reload shaders at runtime (useful for shader hot-reloading during development)
     */
    public void reloadShaders(GLAutoDrawable drawable, String vertexPath, String fragmentPath) {
        GL gl = drawable.getGL();
        if (!initialized || !gl.isGL3()) return;
        
        GL3 gl3 = gl.getGL3();
        
        clearShaderPasses(gl3);
        addShaderPass(gl3, vertexPath, fragmentPath);
        
        System.out.println("Shaders reloaded successfully");
    }
    
    public void dispose(GLAutoDrawable drawable) {
        if (!initialized) return;
        
        GL gl = drawable.getGL();
        if (gl.isGL3()) {
            GL3 gl3 = gl.getGL3();
            
            // Dispose all shader passes
            for (ShaderPass pass : shaderPasses) {
                pass.dispose(gl3);
            }
            shaderPasses.clear();
            
            // Delete framebuffers
            gl3.glDeleteFramebuffers(numFBOs, fbos, 0);
            gl3.glDeleteTextures(numFBOs, fboTextures, 0);
            
            screenQuad.dispose(gl3);
        }
    }
    
    public void setColorCorrection(float saturation, float brightness, float contrast, float hue) {
        this.saturation = Math.max(0, saturation);
        this.brightness = Math.max(0, brightness);
        this.contrast = Math.max(0, contrast);
        this.hue = hue;
    }
    
    public void disablePostProcessing() {
        this.saturation = 1.0f;
        this.brightness = 1.0f;
        this.contrast = 1.0f;
        this.hue = 0;
    }
    
    public int getShaderPassCount() {
        return shaderPasses.size();
    }
    
    /**
     * Enable/disable Y-axis flipping for framebuffer textures
     * Call this if you experience upside-down rendering in multi-pass
     */
    public void setFlipFramebufferY(boolean flip) {
        // This would require modifying ScreenQuad to have alternate texture coordinates
        // For now, document this as a potential issue
        System.out.println("Y-flip setting: " + flip + " (modify ScreenQuad if needed)");
    }
    
    /**
     * 
     * Debug method to print current pipeline state
     */
    public void debugPipeline() {
        System.out.println("=== Post-Processing Pipeline Debug ===");
        System.out.println("Initialized: " + initialized);
        System.out.println("Number of passes: " + shaderPasses.size());
        System.out.println("FBO dimensions: " + 
            (fbos != null && fbos.length > 0 ? "configured" : "not configured"));
        System.out.println("=====================================");
    }
}