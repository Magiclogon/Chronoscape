package ma.ac.emi.gamelogic.particle;

import java.util.HashMap;
import java.util.Map;

import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.SpriteSheet;

public class ParticleAnimationCache {

    private static final Map<String, ParticleAnimation> CACHE = new HashMap<>();

    public static ParticleAnimation get(ParticleDefinition def) {
        return CACHE.computeIfAbsent(def.getSpritePath(), key -> {
            SpriteSheet sheet = new SpriteSheet(
                AssetsLoader.getSprite(def.getSpritePath()),
                def.getSpriteWidth(),
                def.getSpriteHeight()
            );

            return new ParticleAnimation(
                sheet.getAnimationRow(0, def.getAnimationDetails().initLength),
                sheet.getAnimationRow(1, def.getAnimationDetails().loopLength),
                sheet.getAnimationRow(2, def.getAnimationDetails().finishLength)
            );
        });
    }
}
