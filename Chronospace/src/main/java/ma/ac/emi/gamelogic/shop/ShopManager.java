package ma.ac.emi.gamelogic.shop;

import ma.ac.emi.gamelogic.player.Player;

import java.util.*;

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
        this.player  = player;
        this.itemsMap = ItemLoader.getInstance().getItemsCopy();
        init();
    }

    public void init() {
        this.availableItems = new ArrayList<>();
        this.rerollPrice    = 0;

        // Clear all behavioral effects from the previous round
        player.getInventory().getEffectContext().clear(player);

        refreshAvailableItems();
    }

    public void addItem(ShopItem item)    { availableItems.add(item); }
    public void removeItem(ShopItem item) { availableItems.remove(item); }

    public void refreshAvailableItems() {
        if (rerollPrice > player.getMoney()) return;

        setAvailableItems(new ArrayList<>());

        int i = 0;
        while (i < SLOTNUM) {
            Rarity         rarity = determineRarityWithLuck(player.getLuck());
            ItemDefinition item   = pickRandomItem(itemsMap.get(rarity));
            if (item == null || availableItems.contains(item.getItem())) continue;
            if (!item.isStackable() && item.isBought())                  continue;
            availableItems.add(item.getItem());
            i++;
        }

        player.setMoney(player.getMoney() - rerollPrice);
        if (rerollPrice == 0) rerollPrice += 5;
        else                  rerollPrice  = (int)(rerollPrice * 1.2);
    }

    public boolean purchaseItem(ShopItem item) {
        if (player.getMoney() < item.getPrice()) return false;

        player.setMoney(player.getMoney() - item.getPrice());
        item.apply(player);   // registers effect + numeric upgrades

        // Mark as bought in the master map
        for (Map<String, ItemDefinition> defs : itemsMap.values())
            for (ItemDefinition def : defs.values())
                if (def.equals(item.getItemDefinition())) { def.setBought(true); break; }

        refreshItem(item);
        player.getInventory().recalculateAllUpgrades(player);
        return true;
    }

    public boolean sellItem(ShopItem item) {
        if (!player.getInventory().canSellItem(item)) return false;

        // Unregister behavioral effect before removing the item
        if (item instanceof UpgradeItem ui)
            player.getInventory().unregisterEffect(ui, player);

        player.setMoney(player.getMoney() + item.getPrice() * SELLING_PERCENTAGE);
        player.getInventory().removeItem(item);
        player.getInventory().recalculateAllUpgrades(player);
        return true;
    }

    public void refreshItem(ShopItem item) {
        int index = -1;
        for (int i = 0; i < SLOTNUM; i++)
            if (availableItems.get(i).equals(item)) { index = i; break; }

        int i = 0;
        while (i < 1) {
            Rarity         rarity  = determineRarityWithLuck(player.getLuck());
            ItemDefinition newItem = pickRandomItem(itemsMap.get(rarity));
            if (newItem == null || availableItems.contains(newItem.getItem())) continue;
            availableItems.remove(item);
            availableItems.add(index, newItem.getItem());
            i++;
        }
    }

    private Rarity determineRarityWithLuck(double luck) {
        double lf  = Math.max(0, luck);
        double com = Rarity.COMMON.getChance();
        
        // REDUCED SCALING:
        // Rare: 15% -> 8%
        // Epic: 25% -> 12%
        // Legendary: 50% -> 15%
        
        double rar = Rarity.RARE.getChance()      * (1.0 + lf * 0.08); 
        double epi = Rarity.EPIC.getChance()      * (1.0 + lf * 0.12); 
        double leg = Rarity.LEGENDARY.getChance() * (1.0 + lf * 0.15); 
        
        double tot = com + rar + epi + leg;
        double r   = Math.random() * tot;
        
        if (r < com)             return Rarity.COMMON;
        if (r < com + rar)       return Rarity.RARE;
        if (r < com + rar + epi) return Rarity.EPIC;
        return Rarity.LEGENDARY;
    }

    private ItemDefinition pickRandomItem(Map<String, ItemDefinition> items) {
        if (items.isEmpty()) return null;
        int index = new Random().nextInt(items.size());
        return items.values().toArray(new ItemDefinition[0])[index];
    }
}