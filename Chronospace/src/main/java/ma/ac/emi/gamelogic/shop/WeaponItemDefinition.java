package ma.ac.emi.gamelogic.shop;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WeaponItemDefinition extends ItemDefinition {
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
}
