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
    private final FullscreenQuad quad;
    private final List<PostEffect> effects = new ArrayList<>();
    private int currentWidth, currentHeight;
    private int bloomDownscale = 2; 
    
    public PostProcessor(GL3 gl, int width, int height) {
        this.currentWidth = width;
        this.currentHeight = height;
        
        this.sceneFBO = new Framebuffer();
        this.fboA = new Framebuffer();
        this.fboB = new Framebuffer();
        this.bloomFboA = new Framebuffer();
        this.bloomFboB = new Framebuffer();
        
        this.sceneFBO.init(gl, width, height);
        this.fboA.init(gl, width, height);
        this.fboB.init(gl, width, height);
        
        this.quad = new FullscreenQuad(gl);
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
        
        boolean inBloomSection = false;
        int renderWidth = currentWidth;
        int renderHeight = currentHeight;
        
        for (int i = 0; i < effects.size() - 1; i++) {
            PostEffect effect = effects.get(i);
            
            // Detect bloom section (between extract and combine)
            if (effect instanceof BloomExtractEffect) {
                inBloomSection = true;
                source = bloomFboA;
                destination = bloomFboB;
                renderWidth = currentWidth / bloomDownscale;
                renderHeight = currentHeight / bloomDownscale;
                bloomFboA.init(gl, renderWidth, renderHeight);
                bloomFboB.init(gl, renderWidth, renderHeight);
            } else if (effect instanceof BloomCombineEffect) {
                inBloomSection = false;
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
        gl.glViewport(0, 0, currentWidth, currentHeight);
        
        if (!effects.isEmpty()) {
            effects.get(effects.size() - 1).apply(gl, currentInputTexture, quad);
        } else {
            quad.draw(gl, sceneFBO.getTextureId());
        }
    }
    
    public void saveSnapshot(GL3 gl, int currentTextureId) {
        sceneFBO.bind(gl);
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
        quad.draw(gl, currentTextureId); 
        sceneFBO.unbind(gl);
    }
    
    public int getSnapshotTextureId() {
        return sceneFBO.getTextureId();
    }
    
    public void resize(GL3 gl, int w, int h) {
        this.currentWidth = w;
        this.currentHeight = h;
        
        sceneFBO.init(gl, w, h);
        fboA.init(gl, w, h);
        fboB.init(gl, w, h);
        bloomFboA.init(gl, w / bloomDownscale, h / bloomDownscale);
        bloomFboB.init(gl, w / bloomDownscale, h / bloomDownscale);
    }
    
    public void dispose(GL3 gl) {
        sceneFBO.dispose(gl);
        fboA.dispose(gl);
        fboB.dispose(gl);
        bloomFboA.dispose(gl);
        bloomFboB.dispose(gl);
        quad.dispose(gl);
        for (PostEffect e : effects) e.dispose(gl);
    }
}