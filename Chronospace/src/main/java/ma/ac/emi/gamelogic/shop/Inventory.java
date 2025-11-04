package ma.ac.emi.gamelogic.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Inventory {
    public static final int MAX_EQU = 3;
    private List<ShopItem> purchasedItems;
    private WeaponItem[] equippedWeapons;
    private Map<String, List<StatModifier>> activeEffects;

    // track upgrades
    private List<UpgradeItem> weaponUpgrades;
    private List<UpgradeItem> playerUpgrades;
    private double defenseMultiplier;
    private double healthRegenRate;

    public Inventory() {
        this.purchasedItems = new ArrayList<>();
        this.equippedWeapons = new WeaponItem[MAX_EQU];
        this.activeEffects = new HashMap<>();
        this.weaponUpgrades = new ArrayList<>();
        this.playerUpgrades = new ArrayList<>();
        this.defenseMultiplier = 1.0;
        this.healthRegenRate = 0.0;
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

    public void equipWeapon(WeaponItem item, int index) {
        if(!getPurchasedItems().contains(item)) {
            return;
        }
        unequipWeapon(getEquippedWeapons()[index]);
        this.equippedWeapons[index] = item;
        getPurchasedItems().remove(item);

        applyWeaponUpgradesToItem(item);
    }

    public void unequipWeapon(WeaponItem weaponItem) {
        for(int i = 0; i < Inventory.MAX_EQU; i++) {
            if(getEquippedWeapons()[i] == null) continue;
            if(getEquippedWeapons()[i].equals(weaponItem)) {
                getEquippedWeapons()[i] = null;
                if(!getPurchasedItems().contains(weaponItem)) {
                    getPurchasedItems().add(weaponItem);
                }
                return;
            }
        }
    }

    public List<ShopItem> getWeaponItems(){
        return getPurchasedItems().stream().filter((item) -> item instanceof WeaponItem).toList();
    }

    public List<ShopItem> getStatModifierItems(){
        return getPurchasedItems().stream().filter((item) -> item instanceof StatModifierItem).toList();
    }

    public List<ShopItem> getUpgradeItems(){
        return getPurchasedItems().stream().filter((item) -> item instanceof UpgradeItem).toList();
    }

    // Upgrade management methods
    public void addWeaponUpgrade(UpgradeItem upgrade) {
        weaponUpgrades.add(upgrade);
    }

    public void addPlayerUpgrade(UpgradeItem upgrade) {
        playerUpgrades.add(upgrade);
    }

    public void addDefenseMultiplier(double multiplier) {
        this.defenseMultiplier *= multiplier;
    }

    public void addHealthRegenRate(double regenRate) {
        this.healthRegenRate += regenRate;
    }

    private void applyWeaponUpgradesToItem(WeaponItem weapon) {
        for (UpgradeItem upgrade : weaponUpgrades) {
            UpgradeItemDefinition def = (UpgradeItemDefinition) upgrade.getItemDefinition();
            applyUpgradeToWeapon(weapon, def);
        }
    }

    private void applyUpgradeToWeapon(WeaponItem weapon, UpgradeItemDefinition def) {
        WeaponItemDefinition weaponDef = (WeaponItemDefinition) weapon.getItemDefinition();

        switch (def.getWeaponStat()) {
            case DAMAGE:
                double newDamage = weaponDef.getDamage() * def.getMultiplier();
                weaponDef.setDamage(newDamage);
                break;

            case ATTACK_SPEED:
                double newAttackSpeed = weaponDef.getAttackSpeed() * def.getMultiplier();
                weaponDef.setAttackSpeed(newAttackSpeed);
                break;

            case RANGE:
                double newRange = weaponDef.getRange() * def.getMultiplier();
                weaponDef.setRange(newRange);
                break;

            case MAGAZINE_SIZE:
                int newMagSize = (int) (weaponDef.getMagazineSize() * def.getMultiplier());
                weaponDef.setMagazineSize(newMagSize);
                break;

            case RELOAD_TIME:
                double newReloadTime = weaponDef.getReloadingTime() / def.getMultiplier();
                weaponDef.setReloadingTime(newReloadTime);
                break;
        }
    }

    public double calculateDamageReduction(double incomingDamage) {
        return incomingDamage / defenseMultiplier;
    }
}