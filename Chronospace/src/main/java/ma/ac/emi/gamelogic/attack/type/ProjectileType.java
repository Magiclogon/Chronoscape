package ma.ac.emi.gamelogic.attack.type;

import java.awt.Image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectileType extends AttackObjectType{
	private final double baseSpeed;

    public ProjectileType(Image sprite, double baseSpeed, int boundWidth, int boundHeight) {
    	super(sprite, boundWidth, boundHeight);
        this.baseSpeed = baseSpeed;
 
    }

}

