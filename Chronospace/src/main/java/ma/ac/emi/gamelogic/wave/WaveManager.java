package ma.ac.emi.gamelogic.wave;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.math.Vector2D;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;


@Getter
@Setter
public class WaveManager {
    private int currentWaveNumber;
    private List<Wave> waves;
    private WaveFactory waveFactory;
    private WaveState state;
    private double waveDelay;
    private double waveTimer;
    private int worldWidth;
    private int worldHeight;
    
    private AttackObjectManager attackObjectManager;

    public WaveManager(int worldWidth, int worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.waveFactory = new WaveFactory();
        this.waves = new ArrayList<>();
        this.currentWaveNumber = 0;
        this.state = WaveState.WAITING;
        this.waveDelay = 1;
        this.waveTimer = 0;
    }

    public void update(double deltaTime, Vector2D playerPos) {
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
            	SwingUtilities.invokeLater(() -> GameController.getInstance().showShop());
            	GameController.getInstance().nextWorld();
            	break;
            case LOST:
                break;
        }
    }

    public void startNextWave() {
        if (currentWaveNumber < waves.size()) {
            System.out.println("Starting wave " + (currentWaveNumber + 1) + " of " + waves.size());
            Wave wave = waves.get(currentWaveNumber);
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
        } else {
            state = WaveState.WAITING;
            waveTimer = 0;
            SwingUtilities.invokeLater(() -> GameController.getInstance().showShop());
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