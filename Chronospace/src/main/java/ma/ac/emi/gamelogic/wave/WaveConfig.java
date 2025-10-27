package ma.ac.emi.gamelogic.wave;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class WaveConfig {
    private int waveNumber;
    private Map<String, Integer> enemies;
    private double spawnDelay;
    private boolean isBossWave;

    public WaveConfig() {
        this.enemies = new HashMap<>();
        this.isBossWave = false;
        this.spawnDelay = 0.5;
    }

    public int getTotalEnemyCount() {
        return enemies.values().stream().mapToInt(Integer::intValue).sum();
    }
}