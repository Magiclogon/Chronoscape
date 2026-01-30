package ma.ac.emi.camera;

import java.awt.*;
import java.awt.geom.AffineTransform;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GameGLPanel;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.math.Vector3D;

@Setter
@Getter
public class Camera {
	private double scalingFactor = 0.3;
	private volatile Vector3D pos;
	private double width;
	private double height;
	private volatile AffineTransform camTransform;
	private double renderScale;
	
	private GameGLPanel gameGLPanel;
	private Entity followed;

	public Camera(Vector3D pos, double w, double h, GameGLPanel gameGLPanel, Entity followed ) {
		this.pos = pos;
		this.width = w;
		this.height = h;
		this.gameGLPanel = gameGLPanel;
		this.followed = followed;
		
		this.renderScale = 1;
		calculateTransform();

	}

	public void update(double step) {
		synchronized(this) {
			if (followed == null) {
				return;
			}
			// camera match panel aspect ratio
			this.width = gameGLPanel.getWidth()*scalingFactor; 
			this.height = gameGLPanel.getHeight()*scalingFactor;		

			Vector3D targetPos = followed.getPos();
			Vector3D relativeCamCenter = new Vector3D(this.width/2, this.height/2);
			
			targetPos = targetPos.sub(relativeCamCenter);
			
			setPos(Vector3D.lerp(getPos(), targetPos, step * 3));
			// get world borders from panel
			double worldPixelWidth = GameController.getInstance().getWorldManager().getCurrentWorld().getWidth() * GamePanel.TILE_SIZE;
			double worldPixelHeight = GameController.getInstance().getWorldManager().getCurrentWorld().getHeight() * GamePanel.TILE_SIZE;
			
			calculateTransform();

			// bloquer cam aux bordures
			//this.pos.setX(Math.max(0, Math.min(this.pos.getX(), worldPixelWidth - this.width)));
			//this.pos.setY(Math.max(0, Math.min(this.pos.getY(), worldPixelHeight - this.height)));
		}
	}
	
	public void calculateTransform() {
		camTransform = new AffineTransform();
		camTransform.scale(1/scalingFactor, 1/scalingFactor);
		camTransform.translate(-getPos().getX(), -getPos().getY());
	}

	public void snapTo(Entity entity) {
		followed = entity;
		this.pos = entity.getPos();
	}
	
	public AffineTransform getCamTransform() {
		return camTransform;
	}
	
	public float[] getViewMatrix() {
	    float sx = (float)(1.0 / scalingFactor * renderScale);
	    float sy = (float)(1.0 / scalingFactor * renderScale);

	    float tx = (float)(-pos.getX()-width/2);
	    float ty = (float)(-pos.getY()-height/2);

	    // Column-major order
	    return new float[]{
	        sx, 0,  0,  0,
	        0,  sy, 0,  0,
	        0,  0,  1,  0,
	        tx*sx, ty*sy, 0, 1
	    };
	}

}
