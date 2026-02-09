package ma.ac.emi.gamelogic.attack.type;

import ma.ac.emi.gamelogic.attack.AOE;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;

public class AOEFactory {
	private static AOEFactory instance;
	private AOEFactory() {}
	
	public static AOEFactory getInstance() {
		if(instance == null) instance = new AOEFactory();
		return instance;
	}

	public AOE createAOE(String id, Vector3D pos, Weapon weapon) {
		AOEDefinition def = AOELoader.getInstance().get(id);
		if (def == null) {
	        throw new IllegalArgumentException("Unknown AOE id: " + id);
	    }
		
		AOE aoe = new AOE(def, pos, weapon);
		aoe.setBaseColorCorrection(def.getColorCorrection());
		aoe.setLightingStrategy(def.getLightingStrategy());
		
	    return aoe;
	}

}
