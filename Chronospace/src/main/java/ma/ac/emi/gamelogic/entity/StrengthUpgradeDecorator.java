package ma.ac.emi.gamelogic.entity;

public class StrengthUpgradeDecorator extends LivingEntityStatUpgradeDecorator {
    private double multiplier;

    public StrengthUpgradeDecorator(LivingEntity wrappee, double multiplier) {
        super(wrappee);
        this.multiplier = multiplier;
    }

    @Override
    public double getStrength() {
        return wrappee.getStrength() * multiplier;
    }
}