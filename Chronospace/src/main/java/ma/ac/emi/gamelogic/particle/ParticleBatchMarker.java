package ma.ac.emi.gamelogic.particle;

import java.awt.Graphics;

import com.jogamp.opengl.GL3;
import ma.ac.emi.gamecontrol.GameObject;
import ma.ac.emi.glgraphics.GLGraphics;

/**
 * Marker object that triggers batched particle rendering at the correct z-order position
 */
public class ParticleBatchMarker extends GameObject {
    private final ParticleSystem particleSystem;
    private final float zPosition;
    
    public ParticleBatchMarker(ParticleSystem particleSystem, float zPosition) {
        this.particleSystem = particleSystem;
        this.zPosition = zPosition;
        this.pos = new ma.ac.emi.math.Vector3D(0, 0, zPosition);
    }
    
    @Override
    public double getDrawnHeight() {
        return zPosition;
    }
    
    @Override
    public void drawGL(GL3 gl, GLGraphics glGraphics) {
        // Render all particles at this z-level in batched mode
        particleSystem.renderBatchedAtZ(gl, glGraphics, zPosition);
    }
    
    public void updateZPosition(float z) {
        this.pos.setZ(z);
    }

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}
}
