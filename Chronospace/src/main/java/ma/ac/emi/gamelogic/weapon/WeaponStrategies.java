package ma.ac.emi.gamelogic.weapon;

import java.util.HashMap;
import java.util.Map;

public class WeaponStrategies {
	public static final Map<String, AttackStrategy> STRATEGIES = new HashMap<>();
	static {
        STRATEGIES.put("RangeSingleHit", new RangeSingleHitStrategy());
        STRATEGIES.put("MeleeSingleHit", new MeleeSingleHitStrategy());
        STRATEGIES.put("RangeAOE", new RangeAOEStrategy());
        STRATEGIES.put("MeleeAOE", new MeleeAOEStrategy());
    }
}
