package ma.ac.emi.gamelogic.particle;

import ma.ac.emi.fx.Sprite;

public class ParticleAnimation {
    public final Sprite[] initFrames;
    public final Sprite[] loopFrames;
    public final Sprite[] finishFrames;

    public ParticleAnimation(
            Sprite[] init,
            Sprite[] loop,
            Sprite[] finish
    ) {
        this.initFrames = init;
        this.loopFrames = loop;
        this.finishFrames = finish;
    }
}
