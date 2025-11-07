package ma.ac.emi.gamelogic.shop;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ma.ac.emi.gamelogic.player.Player;

@Getter
@Setter
@ToString
public abstract class ShopItem {
	protected ItemDefinition itemDefinition;
	protected double price;
	
	public ShopItem(ItemDefinition itemDefinition) {
		this.itemDefinition = itemDefinition;
		price = itemDefinition.getBasePrice();
	}

    public abstract void apply(Player player);
    
    @Override
    public boolean equals(Object o) {
    	if(o instanceof ShopItem item) {
    		return getItemDefinition().equals(item.getItemDefinition());
    	}
    	return false;
    }
}