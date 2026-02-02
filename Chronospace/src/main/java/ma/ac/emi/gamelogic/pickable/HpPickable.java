package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;

import java.awt.*;

@Getter
@Setter
public class HpPickable extends Pickable {

    public HpPickable(double hpGain, double dropProbability) {
        super(dropProbability, hpGain);
    }

    @Override
    public void applyEffect(Player player) {
        double currentHp = player.getHp();
        double maxHp = player.getHpMax();

        double newHp = Math.min(currentHp + this.value, maxHp);

        player.setHp(newHp);
        System.out.println("Player healed by " + this.value);
        this.isPickedUp = true;
    }

    @Override
    public Pickable createInstance() {
        return new HpPickable(this.value, this.dropProbability);
    }

    @Override
    public void draw(Graphics g) {
        if (isPickedUp) return;
        g.setColor(Color.RED);
        g.fillOval((int)pos.getX(), (int)pos.getY(), 10, 10);
        g.setColor(Color.GREEN);
        //g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }
}