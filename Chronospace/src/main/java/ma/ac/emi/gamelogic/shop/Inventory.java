package ma.ac.emi.gamelogic.shop;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;

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
        playerUpgrades.remove(item);
        weaponUpgrades.remove(item);
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
        UpgradeItemDefinition def = (UpgradeItemDefinition) upgrade.getItemDefinition();

        if (!def.isStackable()) {
            boolean alreadyHas = weaponUpgrades.stream()
                    .anyMatch(existing -> existing.getItemDefinition().getId().equals(def.getId()));
            if (alreadyHas) {
                return;
            }
        }

        weaponUpgrades.add(upgrade);
    }

    public void addPlayerUpgrade(UpgradeItem upgrade) {
        UpgradeItemDefinition def = (UpgradeItemDefinition) upgrade.getItemDefinition();

        if (!def.isStackable()) {
            boolean alreadyHas = playerUpgrades.stream()
                    .anyMatch(existing -> existing.getItemDefinition().getId().equals(def.getId()));
            if (alreadyHas) {
                return;
            }
        }

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

    public void recalculateAllUpgrades(Player player) {
        this.defenseMultiplier = 1.0;
        this.healthRegenRate = 0.0;

        player.resetBaseStats();

        // Reset weapons to base definitions
        for (int i = 0; i < equippedWeapons.length; i++) {
            WeaponItem weapon = equippedWeapons[i];
            if (weapon != null) {
                WeaponItemDefinition baseDef = new WeaponItemDefinition(
                        (WeaponItemDefinition) ItemLoader.getInstance()
                                .getBaseItemDefinition(weapon.getItemDefinition().getId())
                );
                weapon.setItemDefinition(baseDef);
            }
        }

        applyPlayerUpgrades(player);
        applyWeaponUpgrades();
    }

    private void applyPlayerUpgrades(Player player) {
        double maxHealthMultiplierBonus = 0.0;
        double maxHealthAddBonus = 0.0;
        double movementSpeedMultiplierBonus = 0.0;
        double movementSpeedAddBonus = 0.0;
        double defenseMultiplierBonus = 0.0;
        double defenseAddBonus = 0.0;
        double healthRegenAddBonus = 0.0;
        double luckMultiplierBonus = 0.0;
        double luckAddBonus = 0.0;

        // Collect all modifications
        for (UpgradeItem upgrade : playerUpgrades) {
            for (UpgradeItemDefinition.Modification mod :
                    ((UpgradeItemDefinition) upgrade.getItemDefinition()).getPlayerModifications()) {

                try {
                    UpgradeItemDefinition.PlayerStat stat =
                            UpgradeItemDefinition.PlayerStat.valueOf(mod.getStat().toUpperCase());

                    switch (stat) {
                        case MAX_HEALTH -> {
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) {
                                maxHealthMultiplierBonus += (mod.getValue() - 1.0);
                            } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) {
                                maxHealthAddBonus += mod.getValue();
                            } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.DIVIDE) {
                                maxHealthMultiplierBonus -= (1.0 - (1.0 / mod.getValue()));
                            }
                        }
                        case MOVEMENT_SPEED -> {
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) {
                                movementSpeedMultiplierBonus += (mod.getValue() - 1.0);
                            } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) {
                                movementSpeedAddBonus += mod.getValue();
                            } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.DIVIDE) {
                                movementSpeedMultiplierBonus -= (1.0 - (1.0 / mod.getValue()));
                            }
                        }
                        case DEFENSE -> {
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) {
                                defenseMultiplierBonus += (mod.getValue() - 1.0);
                            } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) {
                                defenseAddBonus += mod.getValue();
                            } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.DIVIDE) {
                                defenseMultiplierBonus -= (1.0 - (1.0 / mod.getValue()));
                            }
                        }
                        case HEALTH_REGEN -> {
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) {
                                healthRegenAddBonus += mod.getValue();
                            } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) {
                                healthRegenAddBonus += (mod.getValue() - 1.0) * player.getInventory().getHealthRegenRate();
                            }
                        }
                        case LUCK -> {
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) {
                                luckMultiplierBonus += (mod.getValue() - 1.0);
                            } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) {
                                luckAddBonus += mod.getValue();
                            } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.DIVIDE) {
                                luckMultiplierBonus -= (1.0 - (1.0 / mod.getValue()));
                            }
                        }
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Unknown player stat: " + mod.getStat());
                }
            }
        }

        double baseHPMax = player.getBaseHPMax();
        player.setHpMax(baseHPMax * (1.0 + maxHealthMultiplierBonus) + maxHealthAddBonus);

        double baseSpeed = player.getBaseSpeed();
        player.setSpeed(baseSpeed * (1.0 + movementSpeedMultiplierBonus) + movementSpeedAddBonus);

        this.defenseMultiplier = 1.0 * (1.0 + defenseMultiplierBonus) + defenseAddBonus;
        this.healthRegenRate = healthRegenAddBonus;

        double baseLuck = player.getBaseLuck();
        player.setLuck(baseLuck + luckAddBonus + (baseLuck * luckMultiplierBonus));
    }

    private void applyWeaponUpgrades() {
        for (WeaponItem weapon : equippedWeapons) {
            if (weapon == null) continue;

            WeaponItemDefinition baseDef = (WeaponItemDefinition) ItemLoader.getInstance()
                    .getBaseItemDefinition(weapon.getItemDefinition().getId());

            double damageMultiplierBonus = 0.0;
            double damageAddBonus = 0.0;
            double attackSpeedMultiplierBonus = 0.0;
            double attackSpeedAddBonus = 0.0;
            double rangeMultiplierBonus = 0.0;
            double rangeAddBonus = 0.0;
            double magazineSizeMultiplierBonus = 0.0;
            double magazineSizeAddBonus = 0.0;
            double reloadTimeMultiplierBonus = 0.0;
            double reloadTimeAddBonus = 0.0;

            for (UpgradeItem upgrade : weaponUpgrades) {
                for (UpgradeItemDefinition.Modification mod :
                        ((UpgradeItemDefinition) upgrade.getItemDefinition()).getWeaponModifications()) {

                    try {
                        UpgradeItemDefinition.WeaponStat weaponStat =
                                UpgradeItemDefinition.WeaponStat.valueOf(mod.getStat().toUpperCase());

                        switch (weaponStat) {
                            case DAMAGE -> {
                                if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) {
                                    damageMultiplierBonus += (mod.getValue() - 1.0);
                                } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) {
                                    damageAddBonus += mod.getValue();
                                } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.DIVIDE) {
                                    damageMultiplierBonus -= (1.0 - (1.0 / mod.getValue()));
                                }
                            }
                            case ATTACK_SPEED -> {
                                if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) {
                                    attackSpeedMultiplierBonus += (mod.getValue() - 1.0);
                                } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) {
                                    attackSpeedAddBonus += mod.getValue();
                                } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.DIVIDE) {
                                    attackSpeedMultiplierBonus -= (1.0 - (1.0 / mod.getValue()));
                                }
                            }
                            case RANGE -> {
                                if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) {
                                    rangeMultiplierBonus += (mod.getValue() - 1.0);
                                } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) {
                                    rangeAddBonus += mod.getValue();
                                } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.DIVIDE) {
                                    rangeMultiplierBonus -= (1.0 - (1.0 / mod.getValue()));
                                }
                            }
                            case MAGAZINE_SIZE -> {
                                if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) {
                                    magazineSizeMultiplierBonus += (mod.getValue() - 1.0);
                                } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) {
                                    magazineSizeAddBonus += mod.getValue();
                                } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.DIVIDE) {
                                    magazineSizeMultiplierBonus -= (1.0 - (1.0 / mod.getValue()));
                                }
                            }
                            case RELOAD_TIME -> {
                                if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) {
                                    reloadTimeMultiplierBonus += (mod.getValue() - 1.0);
                                } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) {
                                    reloadTimeAddBonus += mod.getValue();
                                } else if (mod.getOperation() == UpgradeItemDefinition.OperationType.DIVIDE) {
                                    reloadTimeMultiplierBonus -= (1.0 - (1.0 / mod.getValue()));
                                }
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Unknown weapon stat: " + mod.getStat());
                    }
                }
            }

            WeaponItemDefinition newDef = new WeaponItemDefinition(baseDef);
            newDef.setDamage(baseDef.getDamage() * (1.0 + damageMultiplierBonus) + damageAddBonus);
            newDef.setAttackSpeed(baseDef.getAttackSpeed() * (1.0 + attackSpeedMultiplierBonus) + attackSpeedAddBonus);
            newDef.setRange(baseDef.getRange() * (1.0 + rangeMultiplierBonus) + rangeAddBonus);
            newDef.setMagazineSize((int)(baseDef.getMagazineSize() * (1.0 + magazineSizeMultiplierBonus) + magazineSizeAddBonus));
            newDef.setReloadingTime(baseDef.getReloadingTime() * (1.0 + reloadTimeMultiplierBonus) + reloadTimeAddBonus);

            weapon.setItemDefinition(newDef);
        }
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