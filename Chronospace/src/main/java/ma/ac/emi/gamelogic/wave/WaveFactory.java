package ma.ac.emi.gamelogic.wave;

import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;

public class WaveFactory {

    public Wave createWave(WaveConfig config, EnnemySpecieFactory specieFactory, int worldWidth, int worldHeight) {
        int totalEnemies = config.getTotalEnemyCount();

        Wave wave = new Wave(config.getWaveNumber(), totalEnemies, specieFactory, worldWidth, worldHeight);
        wave.setEnemyComposition(config.getEnemies());
        wave.setBossWave(config.isBossWave());
        wave.setSpawnDelay(config.getSpawnDelay());

        return wave;
    }
}