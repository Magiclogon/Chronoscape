package ma.ac.emi.gamelogic.entity;

public class VelocityUpgradeDecorator extends LivingEntityStatUpgradeDecorator {
    private double multiplier;

    public VelocityUpgradeDecorator(LivingEntity wrappee, double multiplier) {
        super(wrappee);
        this.multiplier = multiplier;
    }

    @Override
    public double getVelocityX() {
        return wrappee.getVelocityX() * multiplier;
    }

    @Override
    public double getVelocityY() {
        return wrappee.getVelocityY() * multiplier;
    }
}
