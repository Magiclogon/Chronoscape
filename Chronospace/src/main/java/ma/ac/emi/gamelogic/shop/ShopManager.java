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

		// Use a loop to fill slots
		int i = 0;
		while(i < SLOTNUM) {
			Rarity selectedRarity = determineRarityWithLuck(player.getLuck());
			ItemDefinition item = pickRandomItem(itemsMap.get(selectedRarity));

			if(item == null || availableItems.contains(item.getItem())) continue;
			if(!item.isStackable() && item.isBought()) continue;

			availableItems.add(item.getItem());
			i++;
		}

		this.player.setMoney(player.getMoney() - rerollPrice);
		if(rerollPrice == 0) rerollPrice += 5;
		else rerollPrice *= 1.2;
	}

	private Rarity determineRarityWithLuck(double luck) {
		// Base Weights
		double commonWeight = Rarity.COMMON.getChance();
		double rareWeight = Rarity.RARE.getChance();
		double epicWeight = Rarity.EPIC.getChance();
		double legWeight = Rarity.LEGENDARY.getChance();

		double luckFactor = Math.max(0, luck);

		rareWeight *= (1.0 + (luckFactor * 0.15));
		epicWeight *= (1.0 + (luckFactor * 0.25));
		legWeight  *= (1.0 + (luckFactor * 0.50));

		double totalWeight = commonWeight + rareWeight + epicWeight + legWeight;
		double r = Math.random() * totalWeight;

		if (r < commonWeight) {
			return Rarity.COMMON;
		} else if (r < commonWeight + rareWeight) {
			return Rarity.RARE;
		} else if (r < commonWeight + rareWeight + epicWeight) {
			return Rarity.EPIC;
		} else {
			return Rarity.LEGENDARY;
		}
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


		int i = 0;
		while(i < 1) {
			Rarity selectedRarity = determineRarityWithLuck(player.getLuck());
			ItemDefinition newItem = pickRandomItem(itemsMap.get(selectedRarity));

			if(newItem == null || availableItems.contains(newItem.getItem())) continue;

			availableItems.remove(item);
			availableItems.add(index, newItem.getItem());
			i++;
		}
	}
}