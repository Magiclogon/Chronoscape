package ma.ac.emi.gamelogic.weapon;

import ma.ac.emi.gamelogic.shop.ItemLoader;
import ma.ac.emi.gamelogic.shop.Rarity;
import ma.ac.emi.gamelogic.shop.WeaponItem;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;

public class WeaponItemFactory {
	private static WeaponItemFactory instance;
	private WeaponItemFactory() {}
	
	public static WeaponItemFactory getInstance() {
		if(instance == null) instance = new WeaponItemFactory();
		return instance;
	}
	
	public WeaponItem createWeaponItem(String id) {
		WeaponItemDefinition weaponDef = null;
		for(Rarity rarity : Rarity.values()) {
			weaponDef = (WeaponItemDefinition) ItemLoader.getInstance().getItemsByRarity().get(rarity).get(id);
			if(weaponDef != null) break;
		}
		return new WeaponItem(weaponDef);
	}
}
