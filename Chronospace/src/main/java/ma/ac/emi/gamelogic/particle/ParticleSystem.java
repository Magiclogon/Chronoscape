package ma.ac.emi.gamelogic.particle;

import java.util.*;
import com.google.gson.*;
import java.io.*;

import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.math.Vector3D;

public class ParticleSystem {
    private Map<String, ParticleDefinition> definitions;
    private Map<String, Double> lastSpawnTimes;
    private List<Particle> activeEffects;
    
    private double gravity;

    public ParticleSystem() {
    	definitions = new HashMap<>();
    	init();
    }
    
    public void init() {
    	if(activeEffects != null) activeEffects.forEach(p -> GameController.getInstance().getGamePanel().removeDrawable(p));
    	lastSpawnTimes = new HashMap<>();
    	activeEffects = new ArrayList<>();
    	
    	gravity = 160;
    }

    // 🔥 Load definitions from JSON
    public void loadFromJson(String filePath) {
        try (Reader reader = new FileReader(filePath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray arr = root.getAsJsonArray("effects");
            Gson gson = new Gson();

            for (JsonElement e : arr) {
            	ParticleDefinition def = gson.fromJson(e, ParticleDefinition.class);
                def.applyDefaults();
                definitions.put(def.getId(), def);
                lastSpawnTimes.put(def.getId(), -999.0);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void spawnEffect(String id, Vector3D position, Object source, double currentTime) {
        ParticleDefinition def = definitions.get(id);
        if (def == null) {
            System.err.println("Unknown particle effect: " + id);
            return;
        }

        // Unique key for this entity-effect pair
        String key = source.hashCode() + ":" + id;
        double lastTime = lastSpawnTimes.getOrDefault(key, -999.0);

        if (currentTime - lastTime >= 1/def.getSpawnRate()) {
            activeEffects.add(new Particle(def, new Vector3D(position), new Vector3D()));
            lastSpawnTimes.put(key, currentTime);
        }
    }

    public void update(double step) {
        Iterator<Particle> iter = activeEffects.iterator();
        while (iter.hasNext()) {
            Particle effect = iter.next();
            effect.update(step);
            if (!effect.isAlive())
                iter.remove();
        }
    }

}
