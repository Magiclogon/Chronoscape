package ma.ac.emi.gamelogic.attack.type;

import java.awt.Image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AOEType extends AttackObjectType{
	private double effectRate;
	private double ageMax;

	public AOEType(Image sprite, int boundWidth, int boundHeight, double effectRate, double ageMax) {
		super(sprite, boundWidth, boundHeight);
		this.effectRate = effectRate;
		this.ageMax = ageMax;
	}

}
