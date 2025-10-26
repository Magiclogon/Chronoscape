package ma.ac.emi.gamelogic.entity;

import java.awt.*;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.math.Vector2D;

@Setter
@Getter
public abstract class Entity{
	protected Vector2D pos;
	protected Vector2D velocity;
	
	protected Rectangle bound;
		
	public Entity(Vector2D pos) {
		this.pos = pos;
	}
	
	public abstract void update(double step);
	
	public abstract void draw(Graphics g);

	
}
