package ma.ac.emi.gamecontrol;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.world.World;


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
		drawables = Collections.synchronizedList(drawables);
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
		drawables.removeIf(d -> !d.isDrawn());

		Collections.sort(drawables);
		Class<? extends GameObject> c = null;
		try{
			for(GameObject drawable : drawables) {
				c = drawable.getClass();
				drawable.draw(g);
			}
		}catch(Exception e) {
			System.out.println(Arrays.toString(drawables.stream().map(Object::getClass).collect(Collectors.toList()).toArray()));

			System.err.println("drawing error from: " + c);
		}
		g2d.setTransform(oldTransform);
	}

	public void addDrawable(GameObject gameObject) {
		this.drawables.add(gameObject);
		gameObject.setDrawn(true);
	}

	public void removeDrawable(GameObject gameObject) {
		gameObject.setDrawn(false);
	}
	
	public void removeAllDrawables() {
		drawables.forEach(d -> d.setDrawn(false));
	}
}
