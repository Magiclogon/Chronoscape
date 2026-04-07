package ma.ac.emi.camera;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GameGLPanel;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.math.Matrix4;
import ma.ac.emi.glgraphics.Mat4;
import ma.ac.emi.math.Vector3D;

@Setter
@Getter
public class Camera {
	private double scalingFactor = 0.4;
	private volatile Vector3D pos;
	private double width;
	private double height;
	private volatile AffineTransform camTransform;
	private double renderScale;
	private double shakeIntensity, dampingFactor;
	
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
			
			// get world borders from panel
			double worldPixelWidth = GameController.getInstance().getWorldManager().getCurrentWorld().getWidth() * GamePanel.TILE_SIZE;
			double worldPixelHeight = GameController.getInstance().getWorldManager().getCurrentWorld().getHeight() * GamePanel.TILE_SIZE;
			

			setPos(Vector3D.lerp(getPos(), targetPos, step * 3));

			if(shakeIntensity > 0) {
				Vector3D offset = Vector3D.randomUnit2().mult(shakeIntensity);
				setPos(getPos().add(offset));
				shakeIntensity *= dampingFactor;
				if(shakeIntensity < 0.0001) shakeIntensity = 0;
			}
			calculateTransform();

		}
	}
	
	public void shake(double intensity, double damping) {
		setShakeIntensity(intensity);
		setDampingFactor(damping);
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

	/**
	 * Convert a world-space position to screen-space pixels (origin top-left).
	 *
	 * The key insight: worldToScreen must use the same projection+view that
	 * the renderer uses when drawing the scene.
	 *
	 * The renderer draws into an internal framebuffer of size:
	 *   internalW = physicalW * renderScale
	 *   internalH = physicalH * renderScale
	 *
	 * and getViewMatrix() is used with that internal resolution.
	 * After post-processing, the result is upscaled to fill the full canvas.
	 *
	 * So the correct transform is:
	 *   1. Project using the internal resolution ortho (what the renderer uses)
	 *   2. Map clip space → screen using the PHYSICAL canvas size
	 *      (because the final image fills the full canvas after upscale)
	 */
	public Vector3D worldToScreen(Vector3D worldPos) {
	    float physW = GameController.getInstance().getGameGLPanel().getWidth();
	    float physH = GameController.getInstance().getGameGLPanel().getHeight();

	    // Internal resolution — matches GameRenderer.internalWidth/Height
	    float intW = (float)(physW * renderScale);
	    float intH = (float)(physH * renderScale);

	    // Projection over the internal resolution (same as renderer's PASS 1)
	    float[] projection = Mat4.ortho(-intW / 2, intW / 2, intH / 2, -intH / 2);
	    float[] view       = getViewMatrix();
	    float[] viewProj   = Matrix4.multiply(projection, view);

	    AffineTransform transform = Mat4.toAffineTransform(viewProj);

	    Point2D.Double worldPoint = new Point2D.Double(worldPos.getX(), worldPos.getY());
	    Point2D.Double clipPoint  = new Point2D.Double();
	    transform.transform(worldPoint, clipPoint);

	    // clip [-1,1] → screen [0, physW/physH]
	    // The internal image is upscaled to fill the canvas, so we map to physW/physH.
	    double screenX = (clipPoint.x * scalingFactor * 2  + 1.0) / 2.0 * physW;
	    double screenY = (1.0 - clipPoint.y * scalingFactor * 2)  / 2.0 * physH;

	    return new Vector3D(screenX, screenY);
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