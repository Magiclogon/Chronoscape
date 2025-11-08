package ma.ac.emi.gamelogic.shop;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
 
@Getter
@Setter
public abstract class ItemDefinition {
    private String id;
    private String name;
    private String description;
    private int basePrice;
    private String iconPath;
    private Rarity rarity;
    
    public abstract ShopItem getItem();

    public abstract String getStatsDescription();
    
    @Override
    public boolean equals(Object o) {
    	if(o instanceof ItemDefinition item) {
    		return item.getId().equals(getId());
    	}
    	return false;
    }
}