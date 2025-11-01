package ma.ac.emi.gamelogic.shop;

import ma.ac.emi.gamelogic.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopManager {
	private final int SLOTNUM = 6;
    private List<ShopItem> availableItems;

    public ShopManager() {
        this.availableItems = new ArrayList<>();
        refreshAvailableItems();

    }

    public void addItem(ShopItem item) {
        availableItems.add(item);
    }

    public void removeItem(ShopItem item) {
        availableItems.remove(item);
    }

    public List<ShopItem> getAvailableItems() {
        return availableItems;
    }
    
    public void refreshAvailableItems() {
    	setAvailableItems(new ArrayList<>());
    	Map<Rarity, List<ItemDefinition>> itemsMap = ItemLoader.getInstance().getItemsByRarity();
    	int chanceSum = 0;
    	for(Rarity rarity : Rarity.values()) {
    		chanceSum += rarity.getChance();
    	}
    	for(int i = 0; i < SLOTNUM; i++) {
    		double r = Math.random()*chanceSum;
    		ItemDefinition item = null;
    		if(r < Rarity.COMMON.getChance()) {
    			item = pickRandomItem(itemsMap.get(Rarity.COMMON));
    		}else if(r >= Rarity.COMMON.getChance() &&
    				r < Rarity.COMMON.getChance()+Rarity.RARE.getChance()) {
    			item = pickRandomItem(itemsMap.get(Rarity.RARE));
    		}else if(r >= Rarity.COMMON.getChance()+Rarity.RARE.getChance() && 
    				r < Rarity.COMMON.getChance()+Rarity.RARE.getChance()+Rarity.EPIC.getChance()) {
    			item = pickRandomItem(itemsMap.get(Rarity.EPIC));
    		}else if(r >= Rarity.COMMON.getChance()+Rarity.RARE.getChance() && 
    				r < Rarity.COMMON.getChance()+Rarity.RARE.getChance()+Rarity.EPIC.getChance()+Rarity.LEGENDARY.getChance()) {
    			item = pickRandomItem(itemsMap.get(Rarity.LEGENDARY));
    		}
    		
    		if(item == null) continue;
    		if(availableItems.contains(item.getItem())) continue;
    		
    		availableItems.add(item.getItem());
    	}
    }
    
    private ItemDefinition pickRandomItem(List<ItemDefinition> items) {
    	if(items.size() == 0) return null;
    	Random r = new Random();
    	System.out.println(items.size());
    	int index = r.nextInt(items.size());
    	return items.get(index);
    }

    public boolean purchaseItem(Player player, ShopItem item) {
        if (player.getMoney() >= item.getItemDefintion().getBasePrice()) {
            player.setMoney(player.getMoney() - item.getItemDefintion().getBasePrice());
            item.apply(player);
            player.getInventory().addItem(item);
            return true;
        }
        return false;
    }
}