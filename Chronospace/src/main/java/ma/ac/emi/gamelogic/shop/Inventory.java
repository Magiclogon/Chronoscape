package ma.ac.emi.gamelogic.shop;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Inventory {
    public static final int MAX_EQU = 3;
    private List<ShopItem> purchasedItems;
    private WeaponItem[] equippedWeapons;

    // track upgrades
    private List<UpgradeItem> weaponUpgrades;
    private List<UpgradeItem> playerUpgrades;
    private double defenseMultiplier;
    private double healthRegenRate;

    public Inventory() {
    	init();
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

            // Apply all weapon modifications from this upgrade
            for (UpgradeItemDefinition.Modification mod : def.getWeaponModifications()) {
                applyModificationToWeapon(weapon, mod);
            }
        }
    }

    private void applyModificationToWeapon(WeaponItem weapon, UpgradeItemDefinition.Modification mod) {
        WeaponItemDefinition weaponDef = (WeaponItemDefinition) weapon.getItemDefinition();
        WeaponItemDefinition newWeaponDef = new WeaponItemDefinition(weaponDef);

        try {
            UpgradeItemDefinition.WeaponStat weaponStat =
                    UpgradeItemDefinition.WeaponStat.valueOf(mod.getStat().toUpperCase());

            switch (weaponStat) {
                case DAMAGE:
                    double damage = applyOperation(newWeaponDef.getDamage(), mod.getValue(), mod.getOperation());
                    newWeaponDef.setDamage(damage);
                    break;

                case ATTACK_SPEED:
                    double attackSpeed = applyOperation(newWeaponDef.getAttackSpeed(), mod.getValue(), mod.getOperation());
                    newWeaponDef.setAttackSpeed(attackSpeed);
                    break;

                case RANGE:
                    double range = applyOperation(newWeaponDef.getRange(), mod.getValue(), mod.getOperation());
                    newWeaponDef.setRange(range);
                    break;

                case MAGAZINE_SIZE:
                    int magSize = (int) applyOperation(newWeaponDef.getMagazineSize(), mod.getValue(), mod.getOperation());
                    newWeaponDef.setMagazineSize(magSize);
                    break;

                case RELOAD_TIME:
                    double reloadTime = applyOperation(newWeaponDef.getReloadingTime(), mod.getValue(), mod.getOperation());
                    newWeaponDef.setReloadingTime(reloadTime);
                    break;
            }

            weapon.setItemDefinition(newWeaponDef);

        } catch (IllegalArgumentException e) {
            System.err.println("Unknown weapon stat: " + mod.getStat());
        }
    }

    private double applyOperation(double currentValue, double modValue, UpgradeItemDefinition.OperationType operation) {
        switch (operation) {
            case MULTIPLY:
                return currentValue * modValue;
            case ADD:
                return currentValue + modValue;
            case DIVIDE:
                return currentValue / modValue;
            default:
                return currentValue;
        }
    }

    public double calculateDamageReduction(double incomingDamage) {
        return incomingDamage / defenseMultiplier;
    }

	public void init() {
        this.purchasedItems = new ArrayList<>();
        this.equippedWeapons = new WeaponItem[MAX_EQU];
        this.weaponUpgrades = new ArrayList<>();
        this.playerUpgrades = new ArrayList<>();
        this.defenseMultiplier = 1.0;
        this.healthRegenRate = 0.0;		
	}
}