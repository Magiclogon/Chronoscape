package ma.ac.emi.gamelogic.weapon;

import java.util.HashMap;
import java.util.Map;

public class WeaponStrategies {
	public static final Map<String, AttackStrategy> STRATEGIES = new HashMap<>();
	static {
        STRATEGIES.put("Range", new RangeStrategy());
        STRATEGIES.put("Melee", new MeleeStrategy());
    }
}
