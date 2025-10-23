package ma.ac.emi.gamelogic.shop;

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

    public String getStatName() { return statName; }
    public void setStatName(String statName) { this.statName = statName; }

    public double getModifier() { return modifier; }
    public void setModifier(double modifier) { this.modifier = modifier; }

    public boolean isMultiplicative() { return isMultiplicative; }
    public void setMultiplicative(boolean multiplicative) {
        isMultiplicative = multiplicative;
    }
}