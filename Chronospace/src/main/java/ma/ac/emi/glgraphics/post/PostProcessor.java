package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;
import ma.ac.emi.glgraphics.Framebuffer;
import ma.ac.emi.glgraphics.FullscreenQuad;

import java.util.ArrayList;
import java.util.List;

public class PostProcessor {
    private Framebuffer sceneFBO;
    private Framebuffer fboA, fboB;
    private Framebuffer bloomFboA, bloomFboB;
    private Framebuffer glowFBO;
    private Framebuffer snapshotFBO;
    private final FullscreenQuad quad;
    private final List<PostEffect> effects = new ArrayList<>();
    private int currentWidth, currentHeight;
    private int bloomDownscale = 2; 
    private float renderScale;
    
    public PostProcessor(GL3 gl, int width, int height, float renderScale) {
        this.currentWidth = width;
        this.currentHeight = height;
        
        this.sceneFBO = new Framebuffer();
        this.fboA = new Framebuffer();
        this.fboB = new Framebuffer();
        this.bloomFboA = new Framebuffer();
        this.bloomFboB = new Framebuffer();
        this.glowFBO = new Framebuffer();
        this.snapshotFBO = new Framebuffer();
        
        this.sceneFBO.init(gl, width, height, true);
        this.fboA.init(gl, width, height, true);
        this.fboB.init(gl, width, height, true);
        bloomFboA.init(gl, width / bloomDownscale, height / bloomDownscale, false);
        bloomFboB.init(gl, width / bloomDownscale, height / bloomDownscale, false);
        this.glowFBO.init(gl, width, height, true);
        this.snapshotFBO.init(gl, width, height, true);
        
        this.quad = new FullscreenQuad(gl);
        
        this.renderScale = renderScale;
    }
    
    public void setBloomDownscale(int factor) {
        this.bloomDownscale = factor;
    }
    
    public void addEffect(PostEffect effect) {
        this.effects.add(effect);
    }
    
    public void capture(GL3 gl) {
        sceneFBO.bind(gl);
    }
    
    public void release(GL3 gl) {
        sceneFBO.unbind(gl);
    }
    
    public void render(GL3 gl) {
        int currentInputTexture = sceneFBO.getTextureId();
        
        Framebuffer source = fboA;
        Framebuffer destination = fboB;
        
        boolean inDownscaledSection = false;
        int renderWidth = currentWidth;
        int renderHeight = currentHeight;
        
        for (int i = 0; i < effects.size() - 1; i++) {
            PostEffect effect = effects.get(i);
            
            // Detect downscaled section (bloom or glow - between extract and combine)
            if (effect instanceof BloomExtractEffect || effect instanceof GlowExtractEffect) {
                inDownscaledSection = true;
                source = bloomFboA;
                destination = bloomFboB;
                renderWidth = currentWidth / bloomDownscale;
                renderHeight = currentHeight / bloomDownscale;
            } else if (effect instanceof BloomCombineEffect || effect instanceof GlowCombineEffect) {
                inDownscaledSection = false;
                source = fboA;
                destination = fboB;
                renderWidth = currentWidth;
                renderHeight = currentHeight;
            }
            
            destination.bind(gl);
            gl.glViewport(0, 0, renderWidth, renderHeight);
            gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
            
            effect.apply(gl, currentInputTexture, quad);
            
            currentInputTexture = destination.getTextureId();
            
            Framebuffer temp = source;
            source = destination;
            destination = temp;
        }
        
        // Final pass to screen
        gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);
        gl.glViewport(0, 0, (int)(currentWidth/renderScale), (int)(currentHeight/renderScale));
        
        if (!effects.isEmpty()) {
            effects.get(effects.size() - 1).apply(gl, currentInputTexture, quad);
        } else {
            quad.draw(gl, sceneFBO.getTextureId());
        }
    }
    
    public void saveSnapshot(GL3 gl, int currentTextureId) {
        // Save current framebuffer binding
        int[] currentFBO = new int[1];
        gl.glGetIntegerv(GL3.GL_FRAMEBUFFER_BINDING, currentFBO, 0);
        
        // Get current viewport
        int[] viewport = new int[4];
        gl.glGetIntegerv(GL3.GL_VIEWPORT, viewport, 0);
        
        // Render to snapshot FBO
        snapshotFBO.bind(gl);
        gl.glViewport(0, 0, currentWidth, currentHeight);
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
        quad.draw(gl, currentTextureId); 
        
        // Restore the previous state
        gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, currentFBO[0]);
        gl.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
    }
    
    public int getSnapshotTextureId() {
        return snapshotFBO.getTextureId();
    }
    
    public void resize(GL3 gl, int width, int height) {
        this.currentWidth = width;
        this.currentHeight = height;
        
        this.sceneFBO.init(gl, width, height, true);
        this.fboA.init(gl, width, height, true);
        this.fboB.init(gl, width, height, true);
        bloomFboA.init(gl, width / bloomDownscale, height / bloomDownscale, false);
        bloomFboB.init(gl, width / bloomDownscale, height / bloomDownscale, false);
        this.glowFBO.init(gl, width, height, true);
        this.snapshotFBO.init(gl, width, height, true);

    }
    
    public void dispose(GL3 gl) {
        sceneFBO.dispose(gl);
        fboA.dispose(gl);
        fboB.dispose(gl);
        bloomFboA.dispose(gl);
        bloomFboB.dispose(gl);
        glowFBO.dispose(gl);
        snapshotFBO.dispose(gl);
        quad.dispose(gl);
        
        for (PostEffect e : effects) e.dispose(gl);
    }
    
    public void clearEffects(GL3 gl) {
    	for (PostEffect e: effects) e.dispose(gl);
    	effects.clear();
    }
    
    public void captureGlow(GL3 gl) {
        glowFBO.bind(gl);
        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
    }

    public void releaseGlow(GL3 gl) {
        glowFBO.unbind(gl);
    }

    public int getGlowTextureId() {
        return glowFBO.getTextureId();
    }
}