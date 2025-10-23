package ma.ac.emi.gamelogic.weapon;

public abstract class WeaponUpgradeDecorator extends Weapon {
    protected Weapon wrappee;

    public WeaponUpgradeDecorator(Weapon wrappee) {
        this.wrappee = wrappee;
    }

    @Override
    public void attack() {
        wrappee.attack();
    }
}