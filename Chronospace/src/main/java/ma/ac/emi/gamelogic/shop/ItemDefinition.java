package ma.ac.emi.gamelogic.shop;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode
public abstract class ItemDefinition {
    private String id;
    private String name;
    private String description;
    private int basePrice;
    private String iconPath;
    private Rarity rarity;
    
    public abstract ShopItem getItem();
    
   
}