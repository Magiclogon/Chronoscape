package ma.ac.emi.gamelogic.shop;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
 
@Getter
@Setter
public abstract class ItemDefinition implements Cloneable{
    private String id;
    private String name;
    private String description;
    private int basePrice;
    private String iconPath;
    private Rarity rarity;
    private boolean stackable;
    private boolean bought;
    
    public abstract ShopItem getItem();

    public abstract String getStatsDescription();
    
    @Override
    public boolean equals(Object o) {
    	if(o instanceof ItemDefinition item) {
    		return item.getId().equals(getId());
    	}
    	return false;
    }
    
    @Override
    public ItemDefinition clone() {
    	return null;
    }

}