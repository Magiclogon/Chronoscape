package ma.ac.emi.gamelogic.entity;

public class RegenerationSpeedUpgradeDecorator extends LivingEntityStatUpgradeDecorator {
    private double multiplier;

    public RegenerationSpeedUpgradeDecorator(LivingEntity wrappee, double multiplier) {
        super(wrappee);
        this.multiplier = multiplier;
    }

    @Override
    public double getRegenerationSpeed() {
        return wrappee.getRegenerationSpeed() * multiplier;
    }
}
