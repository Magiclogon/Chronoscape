package ma.ac.emi.gamelogic.attack.type;

import java.awt.Image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AttackObjectType {
    private final Image sprite;
    private final int boundWidth, boundHeight;

    public AttackObjectType(Image sprite,int boundWidth, int boundHeight) {
        this.sprite = sprite;
        this.boundWidth = boundWidth;
        this.boundHeight = boundHeight;
    }

}