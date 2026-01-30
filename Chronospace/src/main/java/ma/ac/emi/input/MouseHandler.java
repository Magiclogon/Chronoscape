package ma.ac.emi.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.gamecontrol.GameController;
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
		AffineTransform camTransform = camera.getCamTransform();
		AffineTransform inverseCamTransform = new AffineTransform();
		try {
			inverseCamTransform = camTransform.createInverse();
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		setMouseWorldPos(getMouseScreenPos().applyTransform(inverseCamTransform));
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
