package ma.ac.emi.gamecontrol;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;


@Getter
@Setter
public class GamePanel extends JPanel {

	public static final int TILE_SIZE = 32;
	public List<GameObject> drawables;
	private Camera camera;

	public GamePanel() {
		addMouseListener(MouseHandler.getInstance());
		addMouseMotionListener(MouseHandler.getInstance());
		addMouseWheelListener(MouseHandler.getInstance());
		KeyHandler.getInstance().setupKeyBindings(this);
		
		this.drawables = new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		if (camera == null) {
			System.out.println("Camera is null");
			return;
		}		
		AffineTransform oldTransform = g2d.getTransform();
		
		AffineTransform newTransform = oldTransform;
		newTransform.concatenate(camera.getCamTransform());
		
		g2d.setTransform(newTransform);
		
		Collections.sort(drawables);
		drawables.forEach((drawable) -> drawable.draw(g2d));
		drawables.removeIf(d -> !d.isDrawn());

		g2d.setTransform(oldTransform);
	}

	public void addDrawable(GameObject gameObject) {
		this.drawables.add(gameObject);
	}

	public void removeDrawable(GameObject gameObject) {
		gameObject.setDrawn(false);
	}
	
	public void removeAllDrawables() {
		drawables.forEach(d -> d.setDrawn(false));
	}
}
