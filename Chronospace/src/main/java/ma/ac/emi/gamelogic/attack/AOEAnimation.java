package ma.ac.emi.gamelogic.attack;

import ma.ac.emi.fx.Sprite;

public class AOEAnimation {
    public final Sprite[] initFrames;
    public final Sprite[] loopFrames;
    public final Sprite[] finishFrames;

    public AOEAnimation(
            Sprite[] init,
            Sprite[] loop,
            Sprite[] finish
    ) {
        this.initFrames = init;
        this.loopFrames = loop;
        this.finishFrames = finish;
    }
}
