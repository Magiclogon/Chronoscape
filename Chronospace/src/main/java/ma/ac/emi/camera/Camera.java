package ma.ac.emi.camera;

import java.awt.*;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.world.World;

@Setter
@Getter
public class Camera {
	private Vector2D pos;
	private double width;
	private double height;
	private GamePanel gamePanel;
	private Entity followed;

	public Camera(Vector2D pos, double w, double h, GamePanel gamePanel, Entity followed ) {
		this.pos = pos;
		this.width = w;
		this.height = h;
		this.gamePanel = gamePanel;
		this.followed = followed;
	}

	public void update(double step) {
		if (followed == null) {
			return;
		}

		// camera match panel size
		this.width = gamePanel.getWidth();
		this.height = gamePanel.getHeight();

		Vector2D targetPos = followed.getPos();

		// center on player
		double camX = targetPos.getX() - (this.width / 2.0);
		double camY = targetPos.getY() - (this.height / 2.0);

		// get world borders from panel
		double worldPixelWidth = gamePanel.getWorld().getWidth() * GamePanel.TILE_SIZE;
		double worldPixelHeight = gamePanel.getWorld().getHeight() * GamePanel.TILE_SIZE;

		// bloquer cam aux bordures
		camX = Math.max(0, Math.min(camX, worldPixelWidth - this.width));
		camY = Math.max(0, Math.min(camY, worldPixelHeight - this.height));

		this.pos.setX(camX);
		this.pos.setY(camY);
	}

	public Vector2D camTransform(Vector2D worldVector) {
		Vector2D ratios = getScreenCamRatios();
		Vector2D transformedVector = worldVector.sub(this.pos);
		transformedVector.setX(transformedVector.getX() * ratios.getX());
		transformedVector.setY(transformedVector.getY() * ratios.getY());

		return transformedVector;
	}

	public Vector2D getScreenCamRatios() {
		double panelWidth = this.gamePanel.getSize().getWidth();
		double panelHeight = this.gamePanel.getSize().getHeight();

		if (this.width == 0 || this.height == 0) {
			return new Vector2D(1, 1);
		}
		return new Vector2D(panelWidth / this.width, panelHeight / this.height);
	}

	public void snapTo(Entity entity) {
		followed = entity;
	}
}
