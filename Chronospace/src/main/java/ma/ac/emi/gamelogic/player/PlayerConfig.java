package ma.ac.emi.gamelogic.player;

import java.util.List;

import ma.ac.emi.gamelogic.entity.behavior.EntityBehaviorDefinition;
import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.lighting.LightingStrategy;

public class PlayerConfig {
    public String pseudoname;
    public double money;

    public double baseHP;
    public double baseHPMax;
    public double baseSpeed;
    public double baseStrength;
    public double regenerationSpeed;
    
    public String startingWeaponId;
    
    public SpriteColorCorrection colorCorrection;
    public LightingStrategy lightingStrategy;
    
    public List<EntityBehaviorDefinition> behaviorDefinitions;
    
    
}
