package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector2D;

import java.awt.*;

@Getter
@Setter
public abstract class Pickable {
    protected Vector2D pos;
    protected double probability;
    protected boolean isPickedUp;

    public Pickable() {
        this.isPickedUp = false;
    }

    public void draw(java.awt.Graphics g) {
        if (isPickedUp) return;

        if (this instanceof HpPickable) {
            g.setColor(Color.RED);
        } else if (this instanceof MoneyPickable) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.WHITE);
        }

        g.fillOval((int)pos.getX(), (int)pos.getY(), 10, 10);
    }

    public abstract void applyEffect(Player player);
}