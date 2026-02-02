package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;

import java.awt.*;

@Setter
@Getter
public class MoneyPickable extends Pickable {

    public MoneyPickable(int moneyGain, double dropProbability) {
        super(dropProbability, (double) moneyGain);
    }

    @Override
    public void applyEffect(Player player) {
        int moneyToAdd = (int) this.value;

        player.setMoney(player.getMoney() + moneyToAdd);
        System.out.println("Player gained " + moneyToAdd + " money");
        this.isPickedUp = true;
    }

    @Override
    public Pickable createInstance() {
        return new MoneyPickable((int)this.value, this.dropProbability);
    }

    @Override
    public void draw(Graphics g) {
        if (isPickedUp) return;
        g.setColor(Color.YELLOW);
        g.fillOval((int)pos.getX(), (int)pos.getY(), 10, 10);
        g.setColor(Color.GREEN);
        g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }
}