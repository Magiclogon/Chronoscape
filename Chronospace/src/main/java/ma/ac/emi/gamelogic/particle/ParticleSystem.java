package ma.ac.emi.gamelogic.particle;

import java.util.*;
import com.google.gson.*;

import lombok.Getter;

import java.io.*;

import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.math.Vector3D;

@Getter
public class ParticleSystem {
    private Map<String, ParticleDefinition> definitions;
    private Map<String, Double> lastSpawnTimes;
    private List<Particle> activeEffects;
    private ParticleEmitterManager emitterManager;
    private ParticleLoader loader;
    
    public ParticleSystem() {
    	definitions = new HashMap<>();
    	init();
    	
    	emitterManager = new ParticleEmitterManager();
    	loader = new ParticleLoader();
    }
    
    public void init() {
    	if(activeEffects != null) activeEffects.forEach(p -> GameController.getInstance().getGamePanel().removeDrawable(p));
    	lastSpawnTimes = new HashMap<>();
    	activeEffects = new ArrayList<>();
    	
    }

    public void loadFromJson(String filePath) {
        loader.loadFromJson(filePath, definitions, lastSpawnTimes);
    }

    public void spawnEffect(String id, Vector3D position, Object source, double currentTime) {
        ParticleDefinition def = definitions.get(id);
        if (def == null) {
            System.err.println("Unknown particle effect: " + id);
            return;
        }

        String key = source.hashCode() + ":" + id;
        double lastTime = lastSpawnTimes.getOrDefault(key, -999.0);

        if (currentTime - lastTime >= 1/def.getSpawnRate()) {
            activeEffects.add(new Particle(def, new Vector3D(position)));
            lastSpawnTimes.put(key, currentTime);
        }
    }

    public void update(double step) {
        Iterator<Particle> iter = activeEffects.iterator();
        while (iter.hasNext()) {
            Particle effect = iter.next();
            effect.update(step);
            if (!effect.isAlive()) {
                iter.remove();
                GameController.getInstance().getGamePanel().removeDrawable(effect);
            }
        }
        
        emitterManager.update(step);
    }

}
