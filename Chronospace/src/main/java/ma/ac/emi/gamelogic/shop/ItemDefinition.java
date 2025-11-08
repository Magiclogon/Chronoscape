package ma.ac.emi.gamelogic.shop;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
 
@Getter
@Setter
public abstract class ItemDefinition implements Cloneable{
    protected String id;
    protected String name;
    protected String description;
    protected int basePrice;
    protected String iconPath;
    protected Rarity rarity;
    protected boolean stackable;
    protected boolean bought;
    
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