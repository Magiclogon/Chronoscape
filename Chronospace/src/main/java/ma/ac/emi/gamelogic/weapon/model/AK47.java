package ma.ac.emi.gamelogic.weapon.model;

import java.awt.*;
import java.awt.geom.AffineTransform;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.type.ProjectileFactory;
import ma.ac.emi.gamelogic.weapon.RangeSingleHit;

@Getter
@Setter
public class AK47 extends RangeSingleHit{
	public AK47() {
		setProjectileType(ProjectileFactory.getProjectileType(toString(), null, 500, 4, 4));
		setRange(500);
		setAttackSpeed(10); //bullets/s
		setDamage(2);
		setMagazineSize(30);
		setReloadingTime(3);
		
	}
}
