package ma.ac.emi.gamelogic.shop;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.weapon.Weapon;

public class WeaponItem extends ShopItem {
    private String weaponId;
    private Class<? extends Weapon> weaponType;
    private double upgradeMultiplier;

    @Override
    public void apply(Player player) {
        Weapon weapon = createWeapon();
        weapon = upgradeWeapon(weapon);
        // Apply weapon to player
    }

    private Weapon createWeapon() {
        try {
            return weaponType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Weapon upgradeWeapon(Weapon weapon) {
        // Apply upgrades based on upgradeMultiplier
        return weapon;
    }

    public String getWeaponId() { return weaponId; }
    public void setWeaponId(String weaponId) { this.weaponId = weaponId; }

    public Class<? extends Weapon> getWeaponType() { return weaponType; }
    public void setWeaponType(Class<? extends Weapon> weaponType) {
        this.weaponType = weaponType;
    }

    public double getUpgradeMultiplier() { return upgradeMultiplier; }
    public void setUpgradeMultiplier(double upgradeMultiplier) {
        this.upgradeMultiplier = upgradeMultiplier;
    }
}