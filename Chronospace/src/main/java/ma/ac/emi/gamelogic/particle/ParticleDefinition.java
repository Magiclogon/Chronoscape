package ma.ac.emi.gamelogic.particle;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticleDefinition {
    private String id;
    private String sprite;
    private String color;
    private double lifetime;
    private double spawnRate;
    private int particleCount;
    private double size;

    public void applyDefaults() {
        if (particleCount <= 0) particleCount = 10;
        if (lifetime <= 0) lifetime = 1.0;
    }
}

