package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MoneyPickable extends Pickable {
    private int moneyGain;

    public MoneyPickable(int moneyGain) {
        this.moneyGain = moneyGain;
        this.probability = 0.30;
    }
}
