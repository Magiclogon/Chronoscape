package ma.ac.emi.gamelogic.shop;

import lombok.*;
import ma.ac.emi.gamelogic.weapon.Weapon;

@Getter
@Setter
@ToString
public class WeaponItemDefinition extends ItemDefinition implements Cloneable{
    private String attackStrategy;
    private double damage;
    private double range;
    private double attackSpeed;
    private String projectileId;
    private double reloadingTime;
    private int magazineSize;

	@Override
	public ShopItem getItem() {
		return new WeaponItem(this);
	}

    public WeaponItemDefinition(WeaponItemDefinition other) {
        this.setId(other.getId());
        this.setName(other.getName());
        this.setDescription(other.getDescription());
        this.setBasePrice(other.getBasePrice());
        this.setIconPath(other.getIconPath());
        this.setRarity(other.getRarity());

        this.attackStrategy = other.attackStrategy;
        this.damage = other.damage;
        this.range = other.range;
        this.attackSpeed = other.attackSpeed;
        this.projectileId = other.projectileId;
        this.reloadingTime = other.reloadingTime;
        this.magazineSize = other.magazineSize;
    }
    
    
    @Override
    public WeaponItemDefinition clone() {
		return new WeaponItemDefinition(this);
    	
    }

}
