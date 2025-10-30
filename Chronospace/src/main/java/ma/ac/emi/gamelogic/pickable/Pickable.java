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
    protected Rectangle bound;

    public Pickable() {
        this.isPickedUp = false;
        this.bound = new Rectangle(10, 10);
    }

    public abstract void draw(java.awt.Graphics g);

    public abstract void applyEffect(Player player);
}