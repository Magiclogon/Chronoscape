package ma.ac.emi.gamelogic.wave;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WaveConfigLoader {
    private final Gson gson;

    public WaveConfigLoader() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    // load wave config from json
    public List<WaveConfig> loadWavesFromFile(String filepath) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filepath);
             InputStreamReader reader = new InputStreamReader(
                     inputStream != null ? inputStream : new FileInputStream(filepath),
                     StandardCharsets.UTF_8)) {

            Type listType = new TypeToken<List<WaveConfig>>(){}.getType();
            List<WaveConfig> configs = gson.fromJson(reader, listType);

            if (configs == null) {
                configs = new ArrayList<>();
            }

            return configs;
        }
    }

    // save config to json
    public void saveWavesToFile(List<WaveConfig> waves, String filepath) throws IOException {
        try (FileWriter writer = new FileWriter(filepath, StandardCharsets.UTF_8)) {
            gson.toJson(waves, writer);
        }
    }

    // create sample waves
    public void createSampleConfigFile(String filepath) throws IOException {
        List<WaveConfig> sampleWaves = createSampleWaves();
        saveWavesToFile(sampleWaves, filepath);
    }

    private List<WaveConfig> createSampleWaves() {
        List<WaveConfig> waves = new ArrayList<>();

        WaveConfig wave1 = new WaveConfig();
        wave1.setWaveNumber(1);
        wave1.getEnemies().put("common", 10);
        wave1.setSpawnDelay(0.5);
        waves.add(wave1);

        WaveConfig wave2 = new WaveConfig();
        wave2.setWaveNumber(2);
        wave2.getEnemies().put("common", 12);
        wave2.getEnemies().put("ranged", 3);
        wave2.setSpawnDelay(0.5);
        waves.add(wave2);

        WaveConfig wave3 = new WaveConfig();
        wave3.setWaveNumber(3);
        wave3.getEnemies().put("common", 10);
        wave3.getEnemies().put("speedster", 5);
        wave3.setSpawnDelay(0.4);
        waves.add(wave3);

        WaveConfig wave4 = new WaveConfig();
        wave4.setWaveNumber(4);
        wave4.getEnemies().put("common", 8);
        wave4.getEnemies().put("speedster", 6);
        wave4.getEnemies().put("tank", 2);
        wave4.setSpawnDelay(0.4);
        waves.add(wave4);

        WaveConfig wave5 = new WaveConfig();
        wave5.setWaveNumber(5);
        wave5.getEnemies().put("boss", 1);
        wave5.getEnemies().put("speedster", 5);
        wave5.getEnemies().put("ranged", 3);
        wave5.setSpawnDelay(0.3);
        wave5.setBossWave(true);
        waves.add(wave5);

        return waves;
    }
}
