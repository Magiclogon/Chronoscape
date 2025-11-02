package ma.ac.emi.gamelogic.shop;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;

@Getter
@Setter
@EqualsAndHashCode
public abstract class ShopItem {
	protected ItemDefinition itemDefinition;
	protected double price;
	
	public ShopItem(ItemDefinition itemDefinition) {
		this.itemDefinition = itemDefinition;
	}

    public abstract void apply(Player player);

    
}