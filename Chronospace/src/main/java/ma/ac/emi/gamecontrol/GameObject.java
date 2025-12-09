package ma.ac.emi.gamecontrol;

import java.awt.Graphics;
import java.awt.Rectangle;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public abstract class GameObject implements GameDrawable{
	protected Vector3D pos;
	protected Rectangle hitbox, bound;
	protected SpriteSheet spriteSheet;
	protected boolean drawn;
	
	public GameObject() {
		pos = new Vector3D();
		hitbox = new Rectangle();
		bound = new Rectangle();
		GameController.getInstance().getGamePanel().addDrawable(this);
	}
	
	public abstract void update(double step);
	

	@Override
	public int compareTo(GameObject gameObject) {
		return (int)Math.signum(getPos().getY() - gameObject.getPos().getY());
	}
}
