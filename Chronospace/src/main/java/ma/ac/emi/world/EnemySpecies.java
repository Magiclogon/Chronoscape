package ma.ac.emi.world;

import java.util.HashMap;
import java.util.Map;

import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.difficulty.EasyDifficultyStrategy;
import ma.ac.emi.gamelogic.factory.AlienFactory;
import ma.ac.emi.gamelogic.factory.AmericanFactory;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.gamelogic.factory.VampireFactory;
import ma.ac.emi.gamelogic.factory.ZombieFactory;

public class EnemySpecies {
	public static final Map<String, EnnemySpecieFactory> SPECIES = new HashMap<>();

	static {
		SPECIES.put("vampire", VampireFactory.getInstance());
		SPECIES.put("zombie", ZombieFactory.getInstance());
		SPECIES.put("american", AmericanFactory.getInstance());
		SPECIES.put("alien", AlienFactory.getInstance());
    }
}
