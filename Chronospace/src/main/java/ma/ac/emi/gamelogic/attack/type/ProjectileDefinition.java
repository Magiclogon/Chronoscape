package ma.ac.emi.gamelogic.attack.type;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.behavior.definition.BehaviorDefinition;
import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.lighting.LightingStrategy;

@Getter
@Setter
public class ProjectileDefinition extends AttackObjectType{
	private final double baseSpeed;
	private final String spritePath;
	private final int boundWidth, boundHeight;
	private final List<BehaviorDefinition> behaviorDefinitions;
	private SpriteColorCorrection colorCorrection;
	private LightingStrategy lightingStrategy;

    public ProjectileDefinition(String id, String spritePath, double baseSpeed, int boundWidth, int boundHeight, List<BehaviorDefinition> behaviorDefinitions) {
    	super(id);
    	this.spritePath = spritePath;
    	this.boundWidth = boundWidth;
    	this.boundHeight = boundHeight;
        this.baseSpeed = baseSpeed;
        this.behaviorDefinitions = behaviorDefinitions;
    }

}

