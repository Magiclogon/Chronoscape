package ma.ac.emi.gamelogic.attack.type;

import java.awt.Image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AttackObjectType {
	private final String id;

    public AttackObjectType(String id) {
    	this.id = id;
    }
    
 

}