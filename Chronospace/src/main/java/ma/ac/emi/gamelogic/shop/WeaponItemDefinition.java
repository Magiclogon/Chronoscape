package ma.ac.emi.gamelogic.shop;

public class WeaponItemDefinition extends ItemDefinition {
    private String attackType;
    private float damage;
    private float range;
    private float attackSpeed;
    private String projectileType;
    private float reloadingTime;
    
	@Override
	public ShopItem getItem() {
		return new WeaponItem(this);
	}
}
