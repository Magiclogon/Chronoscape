package ma.ac.emi.gamelogic.attack.type;

import java.awt.Image;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.lighting.LightingStrategy;

@Getter
@Setter
public abstract class AttackObjectType {
	private final String id;
	
	private SpriteColorCorrection colorCorrection;
	private LightingStrategy lightingStrategy;

    public AttackObjectType(String id) {
    	this.id = id;
    }
    
 

}