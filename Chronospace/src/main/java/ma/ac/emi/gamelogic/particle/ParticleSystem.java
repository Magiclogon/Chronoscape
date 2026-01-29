package ma.ac.emi.gamelogic.particle;

import java.util.*;
import com.jogamp.opengl.GL3;
import lombok.Getter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.Texture;
import ma.ac.emi.glgraphics.lighting.LightingSystem;
import ma.ac.emi.math.Vector3D;

@Getter
public class ParticleSystem {
    private Map<String, ParticleDefinition> definitions;
    private Map<String, Double> lastSpawnTimes;
    private List<Particle> activeEffects;
    private ParticleEmitterManager emitterManager;
    private ParticleLoader loader;
    
    // Batching cache - reuse to avoid allocations
    private Map<Texture, List<Particle>> batchCache;
    
    // Z-layered rendering - thread-safe snapshots
    private volatile Map<Float, List<Particle>> zLayersSnapshot;
    private static final float Z_LAYER_THRESHOLD = 10.0f;
    
    // Pre-allocate reusable lists
    private static final int INITIAL_BATCH_SIZE = 64;
    
    public ParticleSystem() {
        definitions = new HashMap<>();
        batchCache = new HashMap<>();
        zLayersSnapshot = new HashMap<>();
        init();
        
        emitterManager = new ParticleEmitterManager();
        loader = new ParticleLoader();
    }
    
    public void init() {
        if(activeEffects != null) {
            activeEffects.forEach(p -> GameController.getInstance().getGamePanel().removeDrawable(p));
        }
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
        if (currentTime - lastTime >= 1.0 / def.getSpawnRate()) {
            Particle p = new Particle(def, new Vector3D(position));
            activeEffects.add(p);
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
            }
        }
        
        emitterManager.update(step);
        
        // Update z-layers snapshot
        updateZLayersSnapshot();
    }
    
    /**
     * Group particles by z-layers and create immutable snapshot for thread-safe rendering
     */
    private void updateZLayersSnapshot() {
        Map<Float, List<Particle>> tempLayers = new HashMap<>();
        
        // Group particles by z-layer
        for (Particle p : activeEffects) {
            float z = (float)(p.getPos().getY() + p.getDrawnHeight() + p.getPos().getZ() * 100000);
            float zLayer = Math.round(z / Z_LAYER_THRESHOLD) * Z_LAYER_THRESHOLD;
            
            tempLayers.computeIfAbsent(zLayer, k -> new ArrayList<>(INITIAL_BATCH_SIZE)).add(p);
        }
        
        // Create immutable copies
        Map<Float, List<Particle>> snapshot = new HashMap<>();
        for (Map.Entry<Float, List<Particle>> entry : tempLayers.entrySet()) {
            snapshot.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        
        zLayersSnapshot = snapshot;
    }
    
    /**
     * Get all z-layers (thread-safe)
     */
    public Set<Float> getZLayers() {
        return zLayersSnapshot.keySet(); // Already immutable since snapshot is replaced atomically
    }
    
    /**
     * Render particles at a specific z-layer in batched mode (thread-safe)
     */
    public void renderBatchedAtZ(GL3 gl, GLGraphics glGraphics, float zLayer) {
        Map<Float, List<Particle>> snapshot = zLayersSnapshot;
        List<Particle> particles = snapshot.get(zLayer);
        
        if (particles == null || particles.isEmpty()) return;
        
        // Clear batch cache (reuse lists to avoid allocation)
        for (List<Particle> list : batchCache.values()) {
            list.clear();
        }
        
        // Group by texture
        for (Particle p : particles) {
            Texture texture = p.getAnimation().getTexture(p.getPhase(), p.getFrameIndex());
            if (texture == null) continue;
            
            batchCache.computeIfAbsent(texture, k -> new ArrayList<>(INITIAL_BATCH_SIZE)).add(p);
        }
        
        // Render each batch
        for (Map.Entry<Texture, List<Particle>> entry : batchCache.entrySet()) {
            Texture texture = entry.getKey();
            List<Particle> batch = entry.getValue();
            
            if (batch.isEmpty()) continue;
            
            // Bind texture once for entire batch
            gl.glBindTexture(GL3.GL_TEXTURE_2D, texture.id);
            
            // Draw all particles with this texture
            for (Particle p : batch) {
                p.drawGLBatched(gl, glGraphics, texture);
            }
        }
        
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
    }
    
    /**
     * Add all particle lights to the lighting system (optimized)
     */
    public void addAllLights(LightingSystem lightingSystem) {
        Map<Float, List<Particle>> snapshot = zLayersSnapshot;
        
        for (List<Particle> particles : snapshot.values()) {
            for (Particle p : particles) {
                if (p.getLight() != null) {
                    lightingSystem.addLight(p.getLight());
                }
            }
        }
    }
    
    public int getActiveParticleCount() {
        return activeEffects.size();
    }
    
    public int getActiveBatchCount() {
        Set<Texture> uniqueTextures = new HashSet<>();
        Map<Float, List<Particle>> snapshot = zLayersSnapshot;
        for (List<Particle> particles : snapshot.values()) {
            for (Particle p : particles) {
                Texture tex = p.getAnimation().getTexture(p.getPhase(), p.getFrameIndex());
                if (tex != null) uniqueTextures.add(tex);
            }
        }
        return uniqueTextures.size();
    }
}