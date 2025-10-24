package ma.ac.emi.camera;

import java.awt.*;

import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.world.World;

@Setter
@Getter
public class Camera {
	private Vector2D pos;
	private double width, height;
	private GamePanel gamePanel;
	private Entity followed;
	
	public Camera(Vector2D pos, double w, double h, GamePanel gamePanel) {
		this.pos = pos;
		this.width = w;
		this.height = h;
		this.gamePanel = gamePanel;
	}
	
	public void update(double step) {
		setPos(followed.getPos());
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
		
		return new Vector2D(panelWidth/this.width, panelHeight/this.height);
	}
	
	public void snapTo(Entity entity) {
		followed = entity;
	}
	
	public Vector2D getPos() {
		return pos;
	}
	
	public void setPos(Vector2D pos) {
		this.pos = pos;
	}


}
