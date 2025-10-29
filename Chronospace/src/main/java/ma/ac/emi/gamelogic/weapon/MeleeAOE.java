package ma.ac.emi.gamelogic.weapon;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.AOE;

@Getter
@Setter
public abstract class MeleeAOE extends Weapon{
    
	@Override
	public void attack() {
		if(tsla >= 1/getAttackSpeed()) {
			AOE aoe = new AOE(getPos().add(dir.mult(16)), this.getAoeType(), this);
			this.getAttackObjectManager().addObject(aoe);
			tsla = 0;
		}
		
		
	}
}