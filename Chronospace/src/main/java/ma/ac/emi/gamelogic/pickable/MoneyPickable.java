package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;

import java.awt.*;

@Setter
@Getter
public class MoneyPickable extends Pickable {
    private int moneyGain;

    public MoneyPickable(int moneyGain) {
        super();
        this.moneyGain = moneyGain;
    }

    @Override
    public void applyEffect(Player player) {
        player.setMoney(player.getMoney() + moneyGain);
        System.out.println("Player gained");
        this.isPickedUp = true;
    }

    @Override
    public void draw(Graphics g) {
        if (isPickedUp) return;
        g.setColor(Color.YELLOW);
        g.fillOval((int)pos.getX(), (int)pos.getY(), 10, 10);

        g.setColor(Color.GREEN);
        bound.setLocation((int) pos.getX(), (int) pos.getY());
        g.drawRect(bound.x, bound.y, bound.width, bound.height);
    }
}
