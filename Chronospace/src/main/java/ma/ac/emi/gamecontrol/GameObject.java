package ma.ac.emi.gamecontrol;

import java.awt.Rectangle;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public abstract class GameObject implements GameDrawable{
	protected volatile Vector3D pos;
	protected Rectangle hitbox;
	protected SpriteSheet spriteSheet;
	protected boolean drawn;
	
	public GameObject() {
		pos = new Vector3D();
		hitbox = new Rectangle();
		GameController.getInstance().getGamePanel().addDrawable(this);
	}
	
	public abstract void update(double step);
	public double getDrawnHeight() {
		return spriteSheet.getTileHeight();
	}
	

	@Override
	public int compareTo(GameObject gameObject) {
		if(getPos().getZ() == gameObject.getPos().getZ())
			return (int)Math.signum(getPos().getY() + getDrawnHeight()/2 - gameObject.getPos().getY() - gameObject.getDrawnHeight()/2);
		else
			return (int)Math.signum(getPos().getZ() - gameObject.getPos().getZ());
	}
}
