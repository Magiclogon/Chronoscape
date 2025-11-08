package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.player.Player;

import java.awt.*;

@Getter
@Setter
public class HpPickable extends Pickable {
    private double baseHpGain;
    private double hpGain;

    public HpPickable(double hpGain, double dropProbability, boolean drawn) {
        super(dropProbability, drawn);
        this.baseHpGain = hpGain;
        this.hpGain = hpGain;
    }

    @Override
    public void applyEffect(Player player) {
        double currentHp = player.getHp();
        double maxHp = player.getHpMax();
        double newHp = Math.min(currentHp + hpGain, maxHp);
        player.setHp(newHp);
        System.out.println("Player healed by " + hpGain);
        this.isPickedUp = true;
    }

    @Override
    public void adjustForDifficulty(double difficultyMultiplier) {
        this.hpGain = baseHpGain * difficultyMultiplier;
    }

    @Override
    public Pickable createInstance() {
        return new HpPickable(baseHpGain, dropProbability, true);
    }

    @Override
    public void draw(Graphics g) {
        if (isPickedUp) return;
        g.setColor(Color.RED);
        g.fillOval((int)pos.getX(), (int)pos.getY(), 10, 10);
        g.setColor(Color.GREEN);
        g.drawRect(bound.x, bound.y, bound.width, bound.height);
    }

}