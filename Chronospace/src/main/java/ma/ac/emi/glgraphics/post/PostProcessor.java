package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;

import ma.ac.emi.glgraphics.Framebuffer;
import ma.ac.emi.glgraphics.FullscreenQuad;

import java.util.ArrayList;
import java.util.List;

public class PostProcessor {
    private Framebuffer sceneFBO;
    private Framebuffer fboA, fboB;
    private final FullscreenQuad quad;
    private final List<PostEffect> effects = new ArrayList<>();

    public PostProcessor(GL3 gl, int width, int height) {
        this.sceneFBO = new Framebuffer();
        this.fboA = new Framebuffer();
        this.fboB = new Framebuffer();
        
        this.sceneFBO.init(gl, width, height);
        this.fboA.init(gl, width, height);
        this.fboB.init(gl, width, height);
        
        this.quad = new FullscreenQuad(gl);
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

        for (int i = 0; i < effects.size() - 1; i++) {
            destination.bind(gl);
            gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
            
            effects.get(i).apply(gl, currentInputTexture, quad);
            
            currentInputTexture = destination.getTextureId();
            
            Framebuffer temp = source;
            source = destination;
            destination = temp;
        }

        // Final pass to screen
        gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);
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
    	sceneFBO.init(gl, w, h);
        fboA.init(gl, w, h);
        fboB.init(gl, w, h);
    }

    public void dispose(GL3 gl) {
    	sceneFBO.dispose(gl);
        fboA.dispose(gl);
        fboB.dispose(gl);
        quad.dispose(gl);
        for (PostEffect e : effects) e.dispose(gl);
    }

}