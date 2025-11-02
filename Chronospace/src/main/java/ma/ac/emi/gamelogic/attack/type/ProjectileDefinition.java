package ma.ac.emi.gamelogic.attack.type;

import java.awt.Image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ProjectileDefinition extends AttackObjectType{
	private final double baseSpeed;

    public ProjectileDefinition(String id, Image sprite, double baseSpeed, int boundWidth, int boundHeight) {
    	super(id, sprite, boundWidth, boundHeight);
        this.baseSpeed = baseSpeed;
 
    }

}

