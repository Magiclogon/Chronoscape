package ma.ac.emi.gamecontrol;

import java.awt.Rectangle;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public abstract class GameObject implements GameDrawable{
	protected Vector3D pos;
	protected SpriteSheet spriteSheet;
	protected boolean drawn;
	
	protected Rectangle bound;
	public GameObject(boolean drawn) {
		this.drawn = drawn;
		if(drawn) GameController.getInstance().getGamePanel().addDrawable(this);
		pos = new Vector3D();
	}
	
	public abstract void update(double step);
	
	@Override
	public int compareTo(GameObject gameObject) {
		return (int)Math.signum(getPos().getY() - gameObject.getPos().getY());
	}
}
