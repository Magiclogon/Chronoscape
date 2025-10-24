package ma.ac.emi.gamelogic.wave;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WaveManager {
    private int currentWaveNumber;
    private List<Wave> waves;
    private DifficultyStrategy difficulty;
    private EnnemySpecieFactory specieFactory;
    private WaveState state;
    private double timeBetweenWaves;
    private double waveTimer;


    public WaveManager(DifficultyStrategy difficulty, EnnemySpecieFactory specieFactory) {
        this.difficulty = difficulty;
        this.specieFactory = specieFactory;
        this.waves = new ArrayList<>();
        this.currentWaveNumber = 0;
        this.state = WaveState.WAITING;
        this.timeBetweenWaves = 59;
        this.waveTimer = 0;
    }

    public void update(double deltaTime) {
        switch (state) {
            case WAITING:
                waveTimer += deltaTime;
                if (waveTimer >= timeBetweenWaves) {
                    startNextWave();
                }
                break;

            case ACTIVE:
                Wave currentWave = getCurrentWave();
                if (currentWave != null) {
                    currentWave.update(deltaTime);
                    if (currentWave.isCompleted()) {
                        onWaveCompleted();
                    }
                }
                break;
        }
    }

    public void startNextWave() {
        if (currentWaveNumber < waves.size()) {
            waves.get(currentWaveNumber).spawn();
            currentWaveNumber++;
            state = WaveState.ACTIVE;
            waveTimer = 0;
        }
    }

    private void onWaveCompleted() {
        if (currentWaveNumber >= waves.size()) {
            state = WaveState.COMPLETED;
        } else {
            state = WaveState.WAITING;
            waveTimer = 0;
        }
    }

    public List<Ennemy> getCurrentEnemies() {
        Wave wave = getCurrentWave();
        return wave != null ? wave.getEnemies() : new ArrayList<>();
    }

    private Wave getCurrentWave() {
        return waves.get(currentWaveNumber);
    }

    public boolean isAllWavesCompleted() {
        return state == WaveState.COMPLETED;
    }

    public void reset() {
        currentWaveNumber = 0;
        waves.clear();
        state = WaveState.WAITING;
        waveTimer = 0;
    }

    public void addWave(Wave wave) {
        waves.add(wave);
    }

    public void initializeWaves(int numberOfWaves) {
        waves.clear();

        for (int i = 1; i <= numberOfWaves; i++) {
            Wave wave = new Wave(i, 10, specieFactory, difficulty);
            waves.add(wave);
        }
    }
}