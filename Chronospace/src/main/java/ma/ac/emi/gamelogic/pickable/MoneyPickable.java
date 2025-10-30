package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;

@Setter
@Getter
public class MoneyPickable extends Pickable {
    private int moneyGain;

    public MoneyPickable(int moneyGain) {
        this.moneyGain = moneyGain;
    }

    @Override
    public void applyEffect(Player player) {
        player.setMoney(player.getMoney() + moneyGain);
        System.out.println("Player gained");
        this.isPickedUp = true;
    }
}
