package ma.ac.emi.gamelogic.shop;

import ma.ac.emi.gamelogic.player.Player;

public abstract class ShopItem {
    protected String id;
    protected String name;
    protected String description;
    protected double price;
    protected String iconPath;

    public abstract void apply(Player player);

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getIconPath() { return iconPath; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setIconPath(String iconPath) { this.iconPath = iconPath; }
}