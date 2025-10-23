package ma.ac.emi.gamelogic.shop;

import ma.ac.emi.gamelogic.player.Player;

import java.util.List;

public class Shop {
    private static Shop instance;
    private List<ShopItem> availableItems;

    private Shop() {
        this.availableItems = new ArrayList<>();
    }

    public static Shop getInstance() {
        if (instance == null) {
            instance = new Shop();
        }
        return instance;
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

    public boolean purchaseItem(Player player, ShopItem item) {
        if (player.getMoney() >= item.getPrice()) {
            player.setMoney(player.getMoney() - item.getPrice());
            item.apply(player);
            player.getInventory().addItem(item);
            return true;
        }
        return false;
    }
}