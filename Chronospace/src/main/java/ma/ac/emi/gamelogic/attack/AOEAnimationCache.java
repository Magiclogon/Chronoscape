package ma.ac.emi.gamelogic.attack;

import java.util.HashMap;
import java.util.Map;

import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.gamelogic.attack.type.AOEDefinition;

public class AOEAnimationCache {

    private static final Map<String, AOEAnimation> CACHE = new HashMap<>();

    public static AOEAnimation get(AOEDefinition def) {
    	 return CACHE.computeIfAbsent(def.getAnimationDetails().spritePath, key -> {
             SpriteSheet sheet = new SpriteSheet(
                 AssetsLoader.getSprite(def.getAnimationDetails().spritePath),
                 def.getAnimationDetails().spriteWidth,
                 def.getAnimationDetails().spriteHeight
             );

             return new AOEAnimation(
                 sheet.getAnimationRow(0, def.getAnimationDetails().initLength),
                 sheet.getAnimationRow(1, def.getAnimationDetails().loopLength),
                 sheet.getAnimationRow(2, def.getAnimationDetails().finishLength)
             );
         });
    }
}
