package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HpPickable extends Pickable {
    private double hpGain;

    public HpPickable(double hpGain) {
        this.hpGain = hpGain;
        this.probability = 0.70;
    }
}