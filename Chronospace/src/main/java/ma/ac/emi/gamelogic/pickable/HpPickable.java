package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;

@Getter
@Setter
public class HpPickable extends Pickable {
    private double hpGain;

    public HpPickable(double hpGain) {
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
}