package ma.ac.emi.gamelogic.projectile;

import java.awt.Image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectileType {
    private final Image sprite;
    private final double baseSpeed;
    private final int boundWidth, boundHeight;

    public ProjectileType(Image sprite, double baseSpeed, int boundWidth, int boundHeight) {
        this.sprite = sprite;
        this.baseSpeed = baseSpeed;
        this.boundWidth = boundWidth;
        this.boundHeight = boundHeight;
    }

}

