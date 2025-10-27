package ma.ac.emi.gamelogic.weapon.model;

import java.awt.Graphics;

import ma.ac.emi.gamelogic.weapon.MeleeSingleHit;

public class Spear extends MeleeSingleHit{
	
	public Spear() {
		setAttackSpeed(2);
		setDamage(10);
		setRange(32);
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

}
