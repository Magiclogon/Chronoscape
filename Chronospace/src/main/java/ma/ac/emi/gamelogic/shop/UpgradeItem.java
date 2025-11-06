package ma.ac.emi.gamelogic.shop;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;

@Getter
@Setter
public class UpgradeItem extends ShopItem {

    public UpgradeItem(UpgradeItemDefinition itemDefinition) {
        super(itemDefinition);
    }

    @Override
    public void apply(Player player) {
        UpgradeItemDefinition def = (UpgradeItemDefinition) getItemDefinition();

        for(UpgradeItemDefinition.Modification mod: def.getModifications()) {
            switch (mod.getUpgradeType()) {
                case WEAPON:
                    applyWeaponUpgrade(player, mod);
                    break;
                case PLAYER:
                    applyPlayerUpgrade(player, mod);
                    break;
            }
        }

        player.getInventory().addItem(this);
    }

    private void applyWeaponUpgrade(Player player, UpgradeItemDefinition.Modification mod) {
        // Apply upgrade to all equipped weapons
        WeaponItem[] equippedWeapons = player.getInventory().getEquippedWeapons();

        for (WeaponItem weapon : equippedWeapons) {
            if (weapon != null) {
                applyModificationToWeapon(weapon, mod);
            }
        }

        player.getInventory().addWeaponUpgrade(this);
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

    private void applyPlayerUpgrade(Player player, UpgradeItemDefinition.Modification mod) {
        try {
            UpgradeItemDefinition.PlayerStat playerStat =
                    UpgradeItemDefinition.PlayerStat.valueOf(mod.getStat().toUpperCase());

            switch (playerStat) {
                case MAX_HEALTH:
                    double maxHp = applyOperation(player.getHpMax(), mod.getValue(), mod.getOperation());
                    player.setHpMax(maxHp);
                    break;

                case MOVEMENT_SPEED:
                    double speed = applyOperation(player.getSpeed(), mod.getValue(), mod.getOperation());
                    player.setSpeed(speed);
                    break;

                case DEFENSE:
                    if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) {
                        player.getInventory().addDefenseMultiplier(mod.getValue());
                    } else {
                        double current = player.getInventory().getDefenseMultiplier();
                        double result = applyOperation(current, mod.getValue(), mod.getOperation());
                        player.getInventory().setDefenseMultiplier(result);
                    }
                    break;

                case HEALTH_REGEN:
                    double regenRate = applyOperation(player.getInventory().getHealthRegenRate(),
                            mod.getValue(), mod.getOperation());
                    player.getInventory().setHealthRegenRate(regenRate);
                    break;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown player stat: " + mod.getStat());
        }

        player.getInventory().addPlayerUpgrade(this);
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
}
