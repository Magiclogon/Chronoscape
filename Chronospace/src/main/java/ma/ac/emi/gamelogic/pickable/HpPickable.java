package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;

import java.awt.*;

@Getter
@Setter
public class HpPickable extends Pickable {
    private double hpGain;

    public HpPickable(double hpGain) {
        super();
        this.hpGain = hpGain;
    }

    @Override
    public void applyEffect(Player player) {
        double currentHp = player.getHp();
        double maxHp = player.getHpMax();

        double newHp = Math.min(currentHp + hpGain, maxHp);
        player.setHp(newHp);

        System.out.println("Player healed");
        this.isPickedUp = true;
    }

    @Override
    public void draw(Graphics g) {
        if (isPickedUp) return;
        g.setColor(Color.RED);
        g.fillOval((int)pos.getX(), (int)pos.getY(), 10, 10);

        g.setColor(Color.GREEN);
        bound.setLocation((int) pos.getX(), (int) pos.getY());
        g.drawRect(bound.x, bound.y, bound.width, bound.height);
    }
}