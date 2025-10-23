package ma.ac.emi.gamelogic.wave;

import java.util.Map;

public class WaveConfig {
    private Map<String, Integer> enemyCounts;
    private boolean isBossWave;
    private String specieType;

    public Map<String, Integer> getEnemyCounts() { return enemyCounts; }
    public void setEnemyCounts(Map<String, Integer> enemyCounts) {
        this.enemyCounts = enemyCounts;
    }

    public boolean isBossWave() { return isBossWave; }
    public void setBossWave(boolean bossWave) { isBossWave = bossWave; }

    public String getSpecieType() { return specieType; }
    public void setSpecieType(String specieType) { this.specieType = specieType; }
}