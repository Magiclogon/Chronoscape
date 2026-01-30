package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;

import ma.ac.emi.glgraphics.FullscreenQuad;

public class SnapshotEffect implements PostEffect {
    private final PostProcessor processor;

    public SnapshotEffect(PostProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void apply(GL3 gl, int sourceTexture, FullscreenQuad quad) {
        
        quad.draw(gl, sourceTexture);
        processor.saveSnapshot(gl, sourceTexture);

    }

    @Override public void dispose(GL3 gl) {}
}