package ma.ac.emi.gamelogic.weapon;

public class WeaponSlot {
    private Weapon weapon;

    public void attack() {
        if (weapon != null) {
            //weapon.attack();
        }
    }

    public Weapon getWeapon() { return weapon; }
    public void setWeapon(Weapon weapon) { this.weapon = weapon; }
}