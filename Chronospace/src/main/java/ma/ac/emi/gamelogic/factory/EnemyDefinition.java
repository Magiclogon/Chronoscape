package ma.ac.emi.gamelogic.factory;

import lombok.Getter;

@Getter
public abstract class EnemyDefinition {
	protected final double speed;
	protected final double hpMax;
	
	protected final String weaponId;
	protected double projectileSpeedMultiplier = 1;
	
	protected final AnimationDetails animationDetails;
	protected final int weaponXOffset, weaponYOffset;
	
	public EnemyDefinition(double speed, double hpMax, String weaponId,
			double projectileSpeedMultiplier, AnimationDetails animationDetails,
			int weaponXOffset, int weaponYOffset) {
		this.speed = speed;
		this.hpMax = hpMax;
		this.weaponId = weaponId;
		this.projectileSpeedMultiplier = projectileSpeedMultiplier;
		
		this.animationDetails = animationDetails;
		this.weaponXOffset = weaponXOffset;
		this.weaponYOffset = weaponYOffset;
	}
	
	public static class AnimationDetails{
		public String spriteSheetPath;
		public int spriteWidth, spriteHeight;
		public int idleLength, runningLength, backingLength, dyingLength;
		
	}

}

