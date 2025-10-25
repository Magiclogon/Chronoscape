package ma.ac.emi.gamelogic.weapon.upgrade;

import ma.ac.emi.gamelogic.weapon.Weapon;

import java.awt.*;

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

    @Override
    public void draw(Graphics g) {

    }
}