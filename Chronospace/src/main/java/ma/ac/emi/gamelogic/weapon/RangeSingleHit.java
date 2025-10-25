package ma.ac.emi.gamelogic.weapon;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RangeSingleHit extends Weapon {
	public RangeSingleHit() {
		setAoe(0);
	}

}