package ma.ac.emi.gamelogic.entity;

import java.awt.*;

import ma.ac.emi.math.Vector2D;

public abstract class Entity {
	protected Vector2D pos;
	protected Vector2D velocity;
	protected double speed;
		
	public Entity(Vector2D pos, double speed) {
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

	public Vector2D getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2D velocity) {
		this.velocity = velocity;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
}
