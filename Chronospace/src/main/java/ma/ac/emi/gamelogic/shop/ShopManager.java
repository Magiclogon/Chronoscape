package ma.ac.emi.gamelogic.shop;

import ma.ac.emi.gamelogic.player.Player;

import java.util.ArrayList;
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
    private Player player;
    private int rerollPrice;
    private Map<Rarity, Map<String, ItemDefinition>> itemsMap;

    public ShopManager(Player player) {
    	this.player = player;
    	itemsMap = ItemLoader.getInstance().getItemsCopy();

        init();
    }
    
    public void init() {
    	this.availableItems = new ArrayList<>();
        this.rerollPrice = 0;
        refreshAvailableItems();
    }

    public void addItem(ShopItem item) {
        availableItems.add(item);
    }

    public void removeItem(ShopItem item) {
        availableItems.remove(item);
    }

    public void refreshAvailableItems() {
    	if(rerollPrice > player.getMoney()) return;
    	setAvailableItems(new ArrayList<>());
    	int chanceSum = 0;
    	for(Rarity rarity : Rarity.values()) {
    		chanceSum += rarity.getChance();
    	}
    	int i = 0;
    	while(i < SLOTNUM) {
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
    		
    		if(item == null || availableItems.contains(item.getItem())) continue;
    		if(!item.isStackable() && item.isBought()) continue;
    		
    		availableItems.add(item.getItem());
    		i++;
    	}
    	
    	this.player.setMoney(player.getMoney() - rerollPrice);
    	if(rerollPrice == 0) rerollPrice += 5;
    	else rerollPrice *= 1.2;
    }
    
    private ItemDefinition pickRandomItem(Map<String, ItemDefinition> items) {
    	if(items.isEmpty()) return null;
    	Random r = new Random();
    	int index = r.nextInt(items.size());
    	return items.values().toArray(new ItemDefinition[items.values().size()])[index];
    }

    public boolean purchaseItem(ShopItem item) {
        if (player.getMoney() >= item.getPrice()) {
            player.setMoney(player.getMoney() - item.getPrice());
            item.apply(player);
            for(Map<String, ItemDefinition> defs: itemsMap.values()) {
        		for(ItemDefinition def: defs.values()) {
        			if(def.equals(item.getItemDefinition())) {
        				def.setBought(true);
        				break;
        			}
        		}
        	}
            refreshItem(item);

			player.getInventory().recalculateAllUpgrades(player);

            return true;
        }
        return false;
    }

	public boolean sellItem(ShopItem item) {
		if(!player.getInventory().hasItem(item.getItemDefinition().getId())) return false;
		player.setMoney(player.getMoney() + item.getPrice() * 0.70);
		player.getInventory().removeItem(item);
		player.getInventory().recalculateAllUpgrades(player);
		return true;
	}

	public void refreshItem(ShopItem item) {
		int index = -1;
		for(int i = 0; i < SLOTNUM; i++) {
			if(this.getAvailableItems().get(i).equals(item)) {
				index = i;
				break;
			}
		}
    	int chanceSum = 0;
    	for(Rarity rarity : Rarity.values()) {
    		chanceSum += rarity.getChance();
    	}
    	int i = 0;
    	while(i < 1) {
    		double r = Math.random()*chanceSum;
    		ItemDefinition newItem = null;
    		if(r < Rarity.COMMON.getChance()) {
    			newItem = pickRandomItem(itemsMap.get(Rarity.COMMON));
    		}else if(r >= Rarity.COMMON.getChance() &&
    				r < Rarity.COMMON.getChance()+Rarity.RARE.getChance()) {
    			newItem = pickRandomItem(itemsMap.get(Rarity.RARE));
    		}else if(r >= Rarity.COMMON.getChance()+Rarity.RARE.getChance() && 
    				r < Rarity.COMMON.getChance()+Rarity.RARE.getChance()+Rarity.EPIC.getChance()) {
    			newItem = pickRandomItem(itemsMap.get(Rarity.EPIC));
    		}else if(r >= Rarity.COMMON.getChance()+Rarity.RARE.getChance() && 
    				r < Rarity.COMMON.getChance()+Rarity.RARE.getChance()+Rarity.EPIC.getChance()+Rarity.LEGENDARY.getChance()) {
    			newItem = pickRandomItem(itemsMap.get(Rarity.LEGENDARY));
    		}
    		
    		if(newItem == null || availableItems.contains(newItem.getItem())) continue;
    		
    		availableItems.remove(item);
    		availableItems.add(index, newItem.getItem());
    		i++;
    	}
	}
}