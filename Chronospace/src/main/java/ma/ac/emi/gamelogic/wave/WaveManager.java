package ma.ac.emi.gamelogic.wave;

import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;

import java.util.ArrayList;
import java.util.List;

public class WaveManager {
    private int currentWaveNumber;
    private List<Wave> waves;
    private DifficultyStrategy difficulty;
    private EnnemySpecieFactory specieFactory;

    public WaveManager(DifficultyStrategy difficulty, EnnemySpecieFactory specieFactory) {
        this.difficulty = difficulty;
        this.specieFactory = specieFactory;
        this.waves = new ArrayList<>();
        this.currentWaveNumber = 0;
    }

    public void startNextWave() {
        if (currentWaveNumber < waves.size()) {
            waves.get(currentWaveNumber).spawn();
            currentWaveNumber++;
        }
    }

    public Wave getCurrentWave() {
        if (currentWaveNumber > 0 && currentWaveNumber <= waves.size()) {
            return waves.get(currentWaveNumber - 1);
        }
        return null;
    }

    public boolean isAllWavesCompleted() {
        return currentWaveNumber >= waves.size() &&
                (getCurrentWave() == null || getCurrentWave().isCompleted());
    }

    public void reset() {
        currentWaveNumber = 0;
        waves.clear();
    }

    public void addWave(Wave wave) {
        waves.add(wave);
    }
}