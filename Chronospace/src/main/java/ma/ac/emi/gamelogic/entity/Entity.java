package ma.ac.emi.gamelogic.entity;

import java.awt.*;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.math.Vector3D;

@Setter
@Getter
public abstract class Entity implements GameDrawable{
	protected Vector3D pos;
	protected Vector3D velocity;
	
	protected Rectangle bound;
	public Entity(boolean drawn) {
		if(drawn) GameController.getInstance().getGamePanel().addDrawable(this);
		pos = new Vector3D();
	}
	
	public abstract void update(double step);
	
	@Override
	public int compareTo(Object o) {
		if(o instanceof Entity entity) {
			return (int)Math.signum(getPos().getY() - entity.getPos().getY());
		}
		return 0;
	}
	
}
