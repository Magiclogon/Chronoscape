package ma.ac.emi.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.glgraphics.Mat4;
import ma.ac.emi.math.Matrix4;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class MouseHandler implements MouseMotionListener, MouseListener, MouseWheelListener{
	private static MouseHandler instance;
	private Vector3D mouseScreenPos, mouseWorldPos;
	private boolean mouseDown;
	private int mouseWheelRot;
	private Camera camera;
	
	private MouseHandler() {
		mouseScreenPos = new Vector3D();
		mouseWheelRot = 0;
	}
	
	public static MouseHandler getInstance() {
		if(instance == null) {
			instance = new MouseHandler();
		}
		
		return instance;
	}
	
	public void calculateMouseWorldPos() {
	    float width = GameController.getInstance().getGameGLPanel().getWidth();
	    float height = GameController.getInstance().getGameGLPanel().getHeight();

	    float clipX = (float) ((getMouseScreenPos().getX() / width) - 0.5f);
	    float clipY = (float) (0.5f - (getMouseScreenPos().getY() / height)); 

	    float[] projection = Mat4.ortho(-width/2, width/2, height/2, -height/2);
	    float[] view = camera.getViewMatrix();
	    float[] viewProj = Matrix4.multiply(projection, view);

	    AffineTransform transform = Mat4.toAffineTransform(viewProj);

	    try {
	        AffineTransform inverseTransform = transform.createInverse();

	        Point2D.Double clipPoint = new Point2D.Double(clipX, clipY);
	        Point2D.Double worldPoint = new Point2D.Double();
	        inverseTransform.transform(clipPoint, worldPoint);

	        setMouseWorldPos(new Vector3D(worldPoint.x, worldPoint.y));

	    } catch (NoninvertibleTransformException e) {
	        e.printStackTrace();
	    }
	}
	
	public Vector3D getMouseWorldPos() {
		calculateMouseWorldPos();
		
		return mouseWorldPos;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			setMouseDown(true);
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			setMouseDown(false);
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		setMouseScreenPos(new Vector3D(e.getX(), e.getY()));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		setMouseScreenPos(new Vector3D(e.getX(), e.getY()));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		double rotation = Math.signum(e.getPreciseWheelRotation());
		mouseWheelRot += rotation;
	}

}
