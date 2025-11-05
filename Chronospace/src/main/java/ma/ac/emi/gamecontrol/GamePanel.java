package ma.ac.emi.gamecontrol;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.world.World;

@Getter
@Setter
public class GamePanel extends JPanel {

	public static final int TILE_SIZE = 16;

	private World world;
	private Camera camera;

	public GamePanel(World world) {
		addMouseListener(MouseHandler.getInstance());
		addMouseMotionListener(MouseHandler.getInstance());
		addMouseWheelListener(MouseHandler.getInstance());
		this.world = world;
		KeyHandler.getInstance().setupKeyBindings(this);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		if (camera == null) {
			System.out.println("Camera is null");
			world.draw(g);
			return;
		}		
		AffineTransform oldTransform = g2d.getTransform();
		
		AffineTransform newTransform = oldTransform;
		newTransform.concatenate(camera.getCamTransform());
		
		g2d.setTransform(newTransform);
		world.draw(g2d);

		g2d.setTransform(oldTransform);
	}
}
