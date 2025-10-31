package ma.ac.emi.gamelogic.shop;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;

@Getter
@Setter
public abstract class ShopItem {
    protected String id;
    protected String name;
    protected String description;
    protected double price;

    public abstract void apply(Player player);

    
}