package ma.ac.emi.gamelogic.wave;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.difficulty.DifficultyObserver;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.WorldContext;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WaveManager implements DifficultyObserver {
    private int currentWaveNumber;
    private List<Wave> waves;
    private WaveFactory waveFactory;
    private WaveState state;
    private double waveDelay;
    private double waveTimer;
    private int worldWidth;
    private int worldHeight;
    private WorldContext context;

    private AttackObjectManager attackObjectManager;
    private DifficultyStrategy currentDifficulty;

    public WaveManager(WorldContext context) {
        this.worldWidth = context.getWorldWidth();
        this.worldHeight = context.getWorldHeight();
        this.waveFactory = new WaveFactory();
        this.waves = new ArrayList<>();
        this.currentWaveNumber = 0;
        this.state = WaveState.WAITING;
        this.waveDelay = 30;
        this.waveTimer = 0;

        this.context = context;

        // Register as Observer
        GameController.getInstance().addDifficultyObserver(this);
        // Get initial difficulty
        this.currentDifficulty = GameController.getInstance().getDifficulty();

        this.context.refreshCurrentMap();
    }

    @Override
    public void refreshDifficulty(DifficultyStrategy difficulty) {
        this.currentDifficulty = difficulty;
    }

    public void update(double deltaTime, Vector3D playerPos) {
        switch (state) {
            case WAITING:
                waveTimer += deltaTime;
                if (waveTimer >= waveDelay) {
                    startNextWave();
                }
                break;

            case ACTIVE:
                Wave currentWave = getCurrentWave();
                if (currentWave != null) {
                    currentWave.update(deltaTime, playerPos);
                    if (currentWave.isCompleted()) {
                        waveTimer += deltaTime;
                        if (waveTimer >= waveDelay) {
                            onWaveCompleted();
                        }
                    }
                }
                break;

            case COMPLETED:
                // Logic handled in onWaveCompleted
                break;
            case LOST:
                break;
        }
    }

    public void startNextWave() {
        if (currentWaveNumber < waves.size()) {
            System.out.println("Starting wave " + (currentWaveNumber + 1) + " of " + waves.size());
            Wave wave = waves.get(currentWaveNumber);

            // apply difficulty
            wave.applyDifficulty(currentDifficulty);
            wave.spawn();

            currentWaveNumber++;
            state = WaveState.ACTIVE;
            waveTimer = 0;
        }
    }

    public void forceStartNextWave() {
        if (state == WaveState.WAITING) {
            waveTimer = waveDelay;
        }
    }

    private void onWaveCompleted() {
        System.out.println("Wave " + currentWaveNumber + " completed");
        if (currentWaveNumber >= waves.size()) {
            state = WaveState.COMPLETED;
            GameController.getInstance().showShop();
            GameController.getInstance().nextWorld();
        } else {
            state = WaveState.WAITING;
            waveTimer = 0;
            context.refreshCurrentMap();
            GameController.getInstance().showShop();
        }
    }

    public List<Ennemy> getCurrentEnemies() {
        Wave wave = getCurrentWave();
        return wave != null ? new ArrayList<>(wave.getEnemies()) : new ArrayList<>();
    }

    public void addEnemyToCurrentWave(Ennemy enemy) {
        Wave currentWave = getCurrentWave();
        if (currentWave != null) {
            currentWave.getEnemies().add(enemy);
        }
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
            return Math.max(0, waveDelay - waveTimer);
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