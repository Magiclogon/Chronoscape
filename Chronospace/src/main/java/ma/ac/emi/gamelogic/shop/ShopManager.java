package ma.ac.emi.gamelogic.shop;

import ma.ac.emi.gamelogic.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopManager {
	public static final double SELLING_PERCENTAGE = 0.5;
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

		// Track ids chosen this session — prevents the same item appearing twice
		// at once, even for stackables (they can appear in a future reroll though)
		Set<String> sessionIds = new HashSet<>();
		int attempts    = 0;
		int maxAttempts = SLOTNUM * 20; // safety valve

		while(availableItems.size() < SLOTNUM && attempts < maxAttempts) {
			attempts++;
			Rarity selectedRarity = determineRarityWithLuck(player.getLuck());
			ItemDefinition item   = pickRandomItem(itemsMap.get(selectedRarity));

			if(item == null) continue;

			// Weapons and non-stackable upgrades disappear once purchased
			if(!item.isStackable() && item.isBought()) continue;

			// No duplicate ids in the same shop display
			if(sessionIds.contains(item.getId())) continue;

			availableItems.add(item.getItem());
			sessionIds.add(item.getId());
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
		if(!player.getInventory().canSellItem(item)) return false;

		player.setMoney(player.getMoney() + item.getPrice() * SELLING_PERCENTAGE);
		player.getInventory().removeItem(item);
		player.getInventory().recalculateAllUpgrades(player);
		return true;
	}

	public void refreshItem(ShopItem item) {
		int index = -1;
		for(int i = 0; i < availableItems.size(); i++) {
			if(availableItems.get(i).equals(item)) { index = i; break; }
		}
		if(index == -1) return;

		// Collect ids currently in the shop (excluding the slot being replaced)
		Set<String> sessionIds = new HashSet<>();
		for(int i = 0; i < availableItems.size(); i++)
			if(i != index) sessionIds.add(availableItems.get(i).getItemDefinition().getId());

		int attempts = 0;
		while(attempts < 40) {
			attempts++;
			Rarity         selectedRarity = determineRarityWithLuck(player.getLuck());
			ItemDefinition newItem        = pickRandomItem(itemsMap.get(selectedRarity));

			if(newItem == null) continue;
			if(!newItem.isStackable() && newItem.isBought()) continue;
			if(sessionIds.contains(newItem.getId())) continue;

			availableItems.set(index, newItem.getItem());
			return;
		}
		// If nothing suitable found, just remove the slot
		availableItems.remove(index);
	}
}