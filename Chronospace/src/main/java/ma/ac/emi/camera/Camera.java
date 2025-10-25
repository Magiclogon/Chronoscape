package ma.ac.emi.camera;

import java.awt.*;
import java.awt.geom.AffineTransform;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.math.Vector2D;

@Setter
@Getter
public class Camera {
	private double scalingFactor = 0.25;
	private Vector2D pos;
	private double width;
	private double height;
	private AffineTransform camTransform;
	
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

		// camera match panel aspect ratio
		this.width = gamePanel.getWidth()*scalingFactor;
		this.height = gamePanel.getHeight()*scalingFactor;		

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
	
	public void calculateTransform() {
		camTransform = new AffineTransform();
		camTransform.scale(1/scalingFactor, 1/scalingFactor);
		camTransform.translate(-getPos().getX(), -getPos().getY());
	}

	public void snapTo(Entity entity) {
		followed = entity;
	}
	
	public AffineTransform getCamTransform() {
		calculateTransform();
		return camTransform;
	}
}
