package ma.ac.emi.gamelogic.weapon;

public class RangeUpgradeDecorator extends WeaponUpgradeDecorator {
    private double multiplier;

    public RangeUpgradeDecorator(Weapon wrappee, double multiplier) {
        super(wrappee);
        this.multiplier = multiplier;
    }

    @Override
    public double getRange() {
        return wrappee.getRange() * multiplier;
    }
}