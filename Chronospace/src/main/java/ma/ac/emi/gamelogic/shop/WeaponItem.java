package ma.ac.emi.gamelogic.shop;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.weapon.Weapon;

@Getter
@Setter
public class WeaponItem extends ShopItem {
    private double upgradeMultiplier;
    
    public WeaponItem(WeaponItemDefinition itemDefinition) {
		super(itemDefinition);
	}

    @Override
    public void apply(Player player) {
        Weapon weapon = createWeapon();
        weapon = upgradeWeapon(weapon);
        // Apply weapon to player
    }

    private Weapon createWeapon() {
		return null;
     
    }

    private Weapon upgradeWeapon(Weapon weapon) {
        // Apply upgrades based on upgradeMultiplier
        return weapon;
    }

}