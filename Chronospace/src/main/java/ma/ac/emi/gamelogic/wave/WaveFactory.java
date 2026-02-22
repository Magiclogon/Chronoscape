package ma.ac.emi.gamelogic.wave;

import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;

public class WaveFactory {

    public Wave createWave(WaveConfig config, EnnemySpecieFactory specieFactory, int worldWidth, int worldHeight, WaveManager waveManager) {
        int totalEnemies = config.getTotalEnemyCount();

        Wave wave = new Wave(config.getWaveNumber(), totalEnemies, specieFactory, worldWidth, worldHeight, waveManager);
        wave.setEnemyComposition(config.getEnemies());
        wave.setBossWave(config.isBossWave());
        wave.setSpawnDelay(config.getSpawnDelay());

        return wave;
    }
}