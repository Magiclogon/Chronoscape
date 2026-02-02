package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;

import java.awt.*;

@Setter
@Getter
public class MoneyPickable extends Pickable {
    private int baseMoneyGain;
    private int moneyGain;

    public MoneyPickable(int moneyGain, double dropProbability) {
        super(dropProbability);
        this.baseMoneyGain = moneyGain;
        this.moneyGain = moneyGain;
    }

    @Override
    public void applyEffect(Player player) {
        player.setMoney(player.getMoney() + moneyGain);
        System.out.println("Player gained " + moneyGain + " money");
        this.isPickedUp = true;
    }

    @Override
    public void adjustForDifficulty(double difficultyMultiplier) {
        this.moneyGain = (int)(baseMoneyGain * difficultyMultiplier);
    }

    @Override
    public Pickable createInstance() {
        return new MoneyPickable(baseMoneyGain, dropProbability);
    }

    @Override
    public void draw(Graphics g) {
        if (isPickedUp) return;
        g.setColor(Color.YELLOW);
        g.fillOval((int)pos.getX(), (int)pos.getY(), 10, 10);
        g.setColor(Color.GREEN);
        //g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

}
