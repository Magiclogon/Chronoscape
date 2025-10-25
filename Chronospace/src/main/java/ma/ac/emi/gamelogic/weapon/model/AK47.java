package ma.ac.emi.gamelogic.weapon.model;

import java.awt.*;
import java.awt.geom.AffineTransform;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.weapon.RangeSingleHit;
import ma.ac.emi.math.Vector2D;

@Getter
@Setter
public class AK47 extends RangeSingleHit{

	@Override
	public void attack() {
		System.out.println("attacking in direction: " + getDir().toString());
		
	}

	@Override
	public void update(double step) {
		super.update(step);
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
