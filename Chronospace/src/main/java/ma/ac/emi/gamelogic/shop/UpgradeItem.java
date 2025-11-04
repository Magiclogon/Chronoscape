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

        switch (def.getUpgradeType()) {
            case WEAPON:
                applyWeaponUpgrade(player, def);
                break;
            case PLAYER:
                applyPlayerUpgrade(player, def);
                break;
        }

        player.getInventory().addItem(this);
    }

    private void applyWeaponUpgrade(Player player, UpgradeItemDefinition def) {
        // Apply upgrade to all equipped weapons
        WeaponItem[] equippedWeapons = player.getInventory().getEquippedWeapons();

        for (WeaponItem weapon : equippedWeapons) {
            if (weapon != null) {
                applyUpgradeToWeapon(weapon, def);
            }
        }

        player.getInventory().addWeaponUpgrade(this);
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

    private void applyPlayerUpgrade(Player player, UpgradeItemDefinition def) {
        switch (def.getPlayerStat()) {
            case MAX_HEALTH:
                double currentMax = player.getHpMax();
                player.setHpMax(currentMax * def.getMultiplier());
                // Also heal the player by the increased amount
                player.setHp(player.getHp() + (player.getHpMax() - currentMax));
                break;

            case MOVEMENT_SPEED:
                double currentSpeed = player.getSpeed();
                player.setSpeed(currentSpeed * def.getMultiplier());
                break;

            case DEFENSE:
                player.getInventory().addDefenseMultiplier(def.getMultiplier());
                break;

            case HEALTH_REGEN:
                player.getInventory().addHealthRegenRate(def.getMultiplier());
                break;
        }

        player.getInventory().addPlayerUpgrade(this);
    }
}
