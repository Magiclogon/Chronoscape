package ma.ac.emi.gamecontrol;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.world.World;

@Getter
@Setter
public class GamePanel extends JPanel {

	public static final int TILE_SIZE = 16;

	private final World world;
	private Camera camera;

	public GamePanel(World world) {
		this.world = world;
		Dimension panelSize = new Dimension(world.getWidth() * TILE_SIZE, world.getHeight() * TILE_SIZE);
		this.setPreferredSize(panelSize);
		KeyHandler.getInstance().setupKeyBindings(this);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		if (camera == null) {
			world.draw(g);
			return;
		}
		AffineTransform oldTransform = g2d.getTransform();

		Vector2D camPos = camera.getPos();
		Vector2D ratios = camera.getScreenCamRatios();

		g2d.scale(ratios.getX(), ratios.getY());
		g2d.translate(-camPos.getX(), -camPos.getY());
		world.draw(g2d);

		g2d.setTransform(oldTransform);
	}
}
