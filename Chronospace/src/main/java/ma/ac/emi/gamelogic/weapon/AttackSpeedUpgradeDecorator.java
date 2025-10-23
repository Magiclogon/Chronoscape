package ma.ac.emi.gamelogic.weapon;

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
}