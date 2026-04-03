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
    public double baseRegenerationSpeed;
    public double baseLuck;

    public String startingWeaponId;
    public AnimationDetails animationDetails;
    
    public SpriteColorCorrection colorCorrection;
    public LightingStrategy lightingStrategy;
    
    public List<EntityBehaviorDefinition> behaviorDefinitions;

    /** Optional — if absent, defaults are used. */
    public StatCaps statCaps;

    public static class StatCaps {
        // Player stat caps — raw values stored unclamped, clamped on apply
        public double minSpeed       =   50;
        public double maxSpeed       =  600;
        public double minRegen       =    0;  // raw stored; only display/apply is clamped
        public double maxRegen       =   50;
        public double minLuck        =    0;  // raw stored; only display/apply is clamped
        // defense: intentionally uncapped (negative = glass cannon mechanic)
        // dodge: raw stored unclamped so synergy items can read overflow;
        // maxDodge caps the effective value at consumption (DamageBehavior/AOE)
        public double maxDodge = 0.75; // 75% max dodge chance
        // maxHp: min 1, no max

        // Weapon stat caps
        public double minDamage      =    1;  // no max on damage
        public double minAttackSpeed =  0.1;
        public double maxAttackSpeed =   30;
        public double minRange       =   10;
        public double maxRange       = 2000;
        public int    minMagazine    =    1;
        public int    maxMagazine    =  999;
        public double minReloadTime  =  0.1;
        public double maxReloadTime  =   30;
    }

    /** Returns statCaps, initialising with defaults if not set in JSON. */
    public StatCaps getCaps() {
        if (statCaps == null) statCaps = new StatCaps();
        return statCaps;
    }
    
    public static class AnimationDetails{
		public String spriteSheetPath;
		public int spriteWidth, spriteHeight;
		public int idleLength, runningLength, backingLength, dyingLength;
		public int spawningLength;
		
	}
}