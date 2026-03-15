package ma.ac.emi.gamelogic.factory;

import java.util.ArrayList;
import java.util.List;

public class BossEnemyDefinition extends EnemyDefinition {
	public List<String> weaponIds = new ArrayList<>();
	public BossEnemyDefinition(double speed, double hpMax, String weaponId, List<String> weaponIds, double projectileSpeedMultiplier,
			AnimationDetails animationDetails, int weaponXOffset, int weaponYOffset) {
		super(speed, hpMax, weaponId, projectileSpeedMultiplier, animationDetails, weaponXOffset, weaponYOffset);
		this.weaponIds = weaponIds;
	}
}

