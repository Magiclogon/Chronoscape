package ma.ac.emi.gamecontrol;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.color.TemporaryColorEffect;
import ma.ac.emi.glgraphics.lighting.Light;
import ma.ac.emi.glgraphics.lighting.LightingStrategy;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public abstract class GameObject implements GameDrawable{
	protected volatile Vector3D pos;
	protected Rectangle hitbox;
	protected SpriteSheet spriteSheet;
	protected boolean drawn;
	
	protected LightingStrategy lightingStrategy;
	protected Light light;
	
	protected SpriteColorCorrection colorCorrection = SpriteColorCorrection.NORMAL;
    protected SpriteColorCorrection baseColorCorrection = SpriteColorCorrection.NORMAL;
    protected List<TemporaryColorEffect> temporaryEffects = new ArrayList<>();
    
	
	public GameObject() {
		pos = new Vector3D();
		hitbox = new Rectangle();
		GameController.getInstance().getGamePanel().addDrawable(this);
		GameController.getInstance().addDrawable(this);
	}

    public void setColorCorrection(SpriteColorCorrection color) {
        this.baseColorCorrection = color;
        updateColorCorrection();
    }
    
    public void addTemporaryEffect(TemporaryColorEffect effect) {
        temporaryEffects.add(effect);
    }
    
    public void update(double step) {
        temporaryEffects.removeIf(effect -> {
            effect.update(step);
            return effect.isFinished();
        });
        updateColorCorrection();
        
        if(getLight() != null) {
        	getLight().x = (float) pos.getX();
        	getLight().y = (float) pos.getY();
        }
    }
    
    private void updateColorCorrection() {
    	if(baseColorCorrection == null) return;
        colorCorrection = baseColorCorrection.copy();
        
        for (TemporaryColorEffect effect : temporaryEffects) {
            effect.apply(colorCorrection);
        }
    }
	
	public double getDrawnHeight() {
		return spriteSheet.getTileHeight();
	}
	
	public double getZOrder() {
		return getDrawnHeight()/2 + getPos().getY() + getPos().getZ() * 100000;
	}
		
	@Override
	public int compareTo(GameObject gameObject) {
		return 
				(int)Math.signum(getZOrder() - gameObject.getZOrder());
	}
}
