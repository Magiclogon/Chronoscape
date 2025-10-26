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
		setProjectileType(ProjectileFactory.getProjectileType(toString(), null, 100, 4, 4));
		setAoeType(AOEFactory.getAOEType(toString(), null, 16, 16, 10, 0.25));
		setRange(100);
		setAttackSpeed(2); //bullets/s
		setDamage(5);
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform oldTransform = g2d.getTransform();
		
		double theta = Math.atan2(getDir().getY(), getDir().getX());
		
		g2d.translate(getPos().getX(), getPos().getY());
		g2d.rotate(theta);
		
		g2d.setColor(Color.GRAY);
		g2d.fillRect(0, 0, 16, 8);
		
		g2d.setTransform(oldTransform);
		
	}

}
