package ma.ac.emi.gamelogic.entity;

import java.awt.*;

import ma.ac.emi.camera.Camera;
import ma.ac.emi.camera.GameDrawable;
import ma.ac.emi.math.Vector2D;

public abstract class Entity extends GameDrawable{
	protected Vector2D pos;
	protected Vector2D vel;
	protected double speed;
		
	public Entity(Vector2D pos, double speed, Camera camera) {
		super(camera);
		this.pos = pos;
		this.speed = speed;
	}

	public abstract void update(double step);
	
	public abstract void draw(Graphics g);
	
	public Vector2D getPos() {
		return pos;
	}

	public void setPos(Vector2D pos) {
		this.pos = pos;
	}

	public Vector2D getVel() {
		return vel;
	}

	public void setVel(Vector2D vel) {
		this.vel = vel;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
}
