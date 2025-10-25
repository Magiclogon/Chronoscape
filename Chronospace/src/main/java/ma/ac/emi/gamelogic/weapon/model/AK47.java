package ma.ac.emi.gamelogic.weapon.model;

import java.awt.*;
import java.awt.geom.AffineTransform;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.projectile.Projectile;
import ma.ac.emi.gamelogic.weapon.RangeSingleHit;

@Getter
@Setter
public class AK47 extends RangeSingleHit{
	public AK47() {
		setExample(new Projectile(getPos(), getDir().mult(500), new Rectangle(4,4), this, bearer instanceof Player));
		setProjectileSpeed(250);
		setRange(500);
		setAttackSpeed(10); //10 bullets/s
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
