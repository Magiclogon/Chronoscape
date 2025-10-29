package ma.ac.emi.gamelogic.weapon.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import ma.ac.emi.gamelogic.attack.type.AOEFactory;
import ma.ac.emi.gamelogic.attack.type.ProjectileFactory;
import ma.ac.emi.gamelogic.weapon.RangeAOE;

public class RPG7 extends RangeAOE{
	
	public RPG7() {
		setProjectileType(ProjectileFactory.getProjectileType(toString(), null, 500, 4, 4));
		setAoeType(AOEFactory.getAOEType(toString(), null, 48, 48, 10, 0.25));
		setRange(100);
		setAttackSpeed(2); //bullets/s
		setDamage(5);
	}
}
