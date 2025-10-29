package ma.ac.emi.gamelogic.wave;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.math.Vector2D;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WaveManager {
    private int currentWaveNumber;
    private List<Wave> waves;
    private DifficultyStrategy difficulty;
    private EnnemySpecieFactory specieFactory;
    private WaveFactory waveFactory;
    private WaveConfigLoader configLoader;
    private WaveState state;
    private double timeBetweenWaves;
    private double waveTimer;
    private int worldWidth;
    private int worldHeight;

    public WaveManager(DifficultyStrategy difficulty, EnnemySpecieFactory specieFactory,
                       int worldWidth, int worldHeight) {
        this.difficulty = difficulty;
        this.specieFactory = specieFactory;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.waveFactory = new WaveFactory();
        this.configLoader = new WaveConfigLoader();
        this.waves = new ArrayList<>();
        this.currentWaveNumber = 0;
        this.state = WaveState.WAITING;
        this.timeBetweenWaves = 10.0;
        this.waveTimer = 0;
    }

    public void loadWavesFromConfig(String filepath) throws IOException {
        List<WaveConfig> configs = configLoader.loadWavesFromFile(filepath);
        waves.clear();

        for (WaveConfig config : configs) {
            Wave wave = waveFactory.createWave(config, difficulty, specieFactory,
                    worldWidth, worldHeight);
            waves.add(wave);
        }

        System.out.println("Loaded " + waves.size() + " waves from " + filepath);
    }

    public void createSampleConfigFile(String filepath) throws IOException {
        configLoader.createSampleConfigFile(filepath);
        System.out.println("Created sample wave config at " + filepath);
    }

    public void update(double deltaTime, Vector2D playerPos) {
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
                    currentWave.update(deltaTime, playerPos);
                    if (currentWave.isCompleted()) {
                        onWaveCompleted();
                    }
                }
                break;

            case COMPLETED:
            case LOST:
                break;
        }
    }

    public void startNextWave() {
        if (currentWaveNumber < waves.size()) {
            Wave wave = waves.get(currentWaveNumber);
            wave.spawn();
            currentWaveNumber++;
            state = WaveState.ACTIVE;
            waveTimer = 0;
        }
    }

    public void forceStartNextWave() {
        if (state == WaveState.WAITING) {
            waveTimer = timeBetweenWaves;
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
        return wave != null ? new ArrayList<>(wave.getEnemies()) : new ArrayList<>();
    }

    public Wave getCurrentWave() {
        if (currentWaveNumber > 0 && currentWaveNumber <= waves.size()) {
            return waves.get(currentWaveNumber - 1);
        }
        return null;
    }

    public int getCurrentWaveIndex() {
        return currentWaveNumber;
    }

    public int getTotalWaves() {
        return waves.size();
    }

    public boolean isAllWavesCompleted() {
        return state == WaveState.COMPLETED;
    }

    public boolean isWaveActive() {
        return state == WaveState.ACTIVE;
    }

    public double getTimeUntilNextWave() {
        if (state == WaveState.WAITING) {
            return Math.max(0, timeBetweenWaves - waveTimer);
        }
        return 0;
    }

    public void reset() {
        currentWaveNumber = 0;
        state = WaveState.WAITING;
        waveTimer = 0;
    }

    public void addWave(Wave wave) {
        waves.add(wave);
    }

    public void addWaveConfig(WaveConfig config) {
        Wave wave = waveFactory.createWave(config, difficulty, specieFactory,
                worldWidth, worldHeight);
        waves.add(wave);
    }

    public void setGameLost() {
        state = WaveState.LOST;
    }

    public int getRemainingEnemiesInCurrentWave() {
        Wave wave = getCurrentWave();
        return wave != null ? wave.getRemainingEnemies() : 0;
    }

    public WaveProgressInfo getProgressInfo() {
        Wave currentWave = getCurrentWave();
        return new WaveProgressInfo(
                currentWaveNumber,
                waves.size(),
                currentWave != null ? currentWave.getRemainingEnemies() : 0,
                currentWave != null ? currentWave.getTotalEnemiesForWave() : 0,
                state,
                getTimeUntilNextWave()
        );
    }

    @Getter
    public static class WaveProgressInfo {
        private final int currentWave;
        private final int totalWaves;
        private final int remainingEnemies;
        private final int totalEnemiesInWave;
        private final WaveState state;
        private final double timeUntilNextWave;

        public WaveProgressInfo(int currentWave, int totalWaves, int remainingEnemies,
                                int totalEnemiesInWave, WaveState state, double timeUntilNextWave) {
            this.currentWave = currentWave;
            this.totalWaves = totalWaves;
            this.remainingEnemies = remainingEnemies;
            this.totalEnemiesInWave = totalEnemiesInWave;
            this.state = state;
            this.timeUntilNextWave = timeUntilNextWave;
        }
    }
}