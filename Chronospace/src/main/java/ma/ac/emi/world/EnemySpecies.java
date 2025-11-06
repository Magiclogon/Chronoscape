package ma.ac.emi.world;

import java.util.HashMap;
import java.util.Map;

import ma.ac.emi.gamelogic.difficulty.EasyDifficultyStrategy;
import ma.ac.emi.gamelogic.factory.AlienFactory;
import ma.ac.emi.gamelogic.factory.AmericanFactory;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.gamelogic.factory.VampireFactory;
import ma.ac.emi.gamelogic.factory.ZombieFactory;

public class EnemySpecies {
	public static final Map<String, EnnemySpecieFactory> SPECIES = new HashMap<>();
	static {
		SPECIES.put("vampire", new VampireFactory(new EasyDifficultyStrategy()));
		SPECIES.put("zombie", new ZombieFactory());
		SPECIES.put("american", new AmericanFactory());
		SPECIES.put("alien", new AlienFactory());
    }
}
