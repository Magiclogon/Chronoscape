package ma.ac.emi.gamelogic.entity;

public class HpMaxUpgradeDecorator extends LivingEntityStatUpgradeDecorator {
    private double multiplier;

    public HpMaxUpgradeDecorator(LivingEntity wrappee, double multiplier) {
        super(wrappee);
        this.multiplier = multiplier;
    }

    @Override
    public double getHpMax() {
        return wrappee.getHpMax() * multiplier;
    }
}