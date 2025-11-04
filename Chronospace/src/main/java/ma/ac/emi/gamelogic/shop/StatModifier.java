package ma.ac.emi.gamelogic.shop;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatModifier {
    private String statName;
    private double modifier;
    private boolean isMultiplicative;

    public StatModifier(String statName, double modifier, boolean isMultiplicative) {
        this.statName = statName;
        this.modifier = modifier;
        this.isMultiplicative = isMultiplicative;
    }

    public double apply(double baseValue) {
        if (isMultiplicative) {
            return baseValue * modifier;
        } else {
            return baseValue + modifier;
        }
    }
}