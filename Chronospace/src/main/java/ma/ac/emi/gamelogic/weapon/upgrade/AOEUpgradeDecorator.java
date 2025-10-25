package ma.ac.emi.gamelogic.weapon.upgrade;

import ma.ac.emi.gamelogic.weapon.Weapon;

public class AOEUpgradeDecorator extends WeaponUpgradeDecorator {
    private double multiplier;

    public AOEUpgradeDecorator(Weapon wrappee, double multiplier) {
        super(wrappee);
        this.multiplier = multiplier;
    }

    @Override
    public double getAoe() {
        return wrappee.getAoe() * multiplier;
    }
}