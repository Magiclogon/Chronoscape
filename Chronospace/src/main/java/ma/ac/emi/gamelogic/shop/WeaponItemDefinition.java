package ma.ac.emi.gamelogic.shop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeaponItemDefinition extends ItemDefinition {
    private String attackStrategy;
    private double damage;
    private double range;
    private double attackSpeed;
    private String projectileId;
    private double reloadingTime;
    private int magazineSize;
    private boolean bought = false;

	@Override
	public ShopItem getItem() {
		return new WeaponItem(this);
	}
}
