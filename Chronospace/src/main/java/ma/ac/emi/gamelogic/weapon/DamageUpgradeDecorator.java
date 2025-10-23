package ma.ac.emi.gamelogic.weapon;

public class DamageUpgradeDecorator extends WeaponUpgradeDecorator {
    private double multiplier;

    public DamageUpgradeDecorator(Weapon wrappee, double multiplier) {
        super(wrappee);
        this.multiplier = multiplier;
    }

    @Override
    public double getDamage() {
        return wrappee.getDamage() * multiplier;
    }
}
