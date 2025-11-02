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
    protected double dropProbability;
    protected boolean isPickedUp;
    protected Rectangle bound;

    public Pickable(double dropProbability) {
        this.dropProbability = dropProbability;
        this.isPickedUp = false;
        this.bound = new Rectangle(10, 10);
    }

    public abstract void draw(Graphics g);
    public abstract void applyEffect(Player player);

    // For difficulty
    public abstract void adjustForDifficulty(double difficultyMultiplier);

    // Create with base values
    public abstract Pickable createInstance();
}
