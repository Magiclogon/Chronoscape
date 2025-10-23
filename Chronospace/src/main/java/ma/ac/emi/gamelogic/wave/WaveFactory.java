package ma.ac.emi.gamelogic.wave;

import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;

public class WaveFactory {
    public Wave createWave(WaveConfig config, DifficultyStrategy difficulty,
                           EnnemySpecieFactory specieFactory) {
        Wave wave = new Wave(0, specieFactory, difficulty);
        // Configure wave based on config
        return wave;
    }
}