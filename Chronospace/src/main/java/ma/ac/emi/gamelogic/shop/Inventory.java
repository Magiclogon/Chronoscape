package ma.ac.emi.gamelogic.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private List<ShopItem> purchasedItems;
    private Map<String, List<StatModifier>> activeEffects;

    public Inventory() {
        this.purchasedItems = new ArrayList<>();
        this.activeEffects = new HashMap<>();
    }

    public void addItem(ShopItem item) {
        purchasedItems.add(item);
    }

    public void removeItem(ShopItem item) {
        purchasedItems.remove(item);
    }

    public List<ShopItem> getItems() {
        return purchasedItems;
    }

    public boolean hasItem(String itemId) {
        return purchasedItems.stream()
                .anyMatch(item -> item.getItemDefinition().getId().equals(itemId));
    }

    public double getTotalStatModifier(String stat) {
        if (!activeEffects.containsKey(stat)) {
            return 1.0;
        }

        double total = 1.0;
        for (StatModifier modifier : activeEffects.get(stat)) {
            total = modifier.apply(total);
        }
        return total;
    }
}