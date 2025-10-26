package ma.ac.emi.gamelogic.weapon.model;

import java.awt.Graphics;

import ma.ac.emi.gamelogic.attack.type.AOEFactory;
import ma.ac.emi.gamelogic.weapon.MeleeAOE;


public class Sword extends MeleeAOE{
	
	public Sword() {
		setAoeType(AOEFactory.getAOEType(toString(), null, 16, 16, 1, 0.5));
		setDamage(5);
		setAttackSpeed(1);
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

}
