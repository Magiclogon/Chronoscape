package ma.ac.emi.gamelogic.weapon.upgrade;

import ma.ac.emi.gamelogic.weapon.Weapon;

import java.awt.*;

public class AttackSpeedUpgradeDecorator extends WeaponUpgradeDecorator {
    private double multiplier;

    public AttackSpeedUpgradeDecorator(Weapon wrappee, double multiplier) {
        super(wrappee);
        this.multiplier = multiplier;
    }

    @Override
    public double getAttackSpeed() {
        return wrappee.getAttackSpeed() * multiplier;
    }

    @Override
    public void draw(Graphics g) {

    }
}