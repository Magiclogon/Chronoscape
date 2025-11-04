package ma.ac.emi.gamelogic.shop;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.weapon.Weapon;

@Getter
@Setter
@ToString
public class WeaponItem extends ShopItem {
    private double upgradeMultiplier;
    
    public WeaponItem(WeaponItemDefinition itemDefinition) {
		super(itemDefinition);
	}

    @Override
    public void apply(Player player) {
    	player.getInventory().addItem(this);
    }

    private Weapon createWeapon() {
    	System.out.println(getItemDefinition().getBasePrice());
    	System.out.println(((WeaponItemDefinition)getItemDefinition()).getProjectileId());
    	Weapon weapon = new Weapon(this);
    	weapon.setAttackObjectManager(GameController.getInstance().getWorld().getAttackObjectManager());
		return weapon;
     
    }

    private Weapon upgradeWeapon(Weapon weapon) {
        // Apply upgrades based on upgradeMultiplier
        return weapon;
    }
}