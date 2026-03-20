package ma.ac.emi.gamelogic.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.effect.EffectContext;
import ma.ac.emi.gamelogic.player.Player;

@Getter
@Setter
public class Inventory {
    public static final int MAX_EQU = 3;

    private List<ShopItem> purchasedItems;
    private WeaponItem[]   equippedWeapons;

    private List<UpgradeItem> weaponUpgrades;
    private List<UpgradeItem> playerUpgrades;

    private final EffectContext effectContext = new EffectContext();

    public Inventory() { init(); }


    public void addItem(ShopItem item) { purchasedItems.add(item); }

    public void removeItem(ShopItem item) {
        for (int i = 0; i < equippedWeapons.length; i++) {
            if (equippedWeapons[i] != null &&
                equippedWeapons[i].getItemDefinition().getId()
                    .equals(item.getItemDefinition().getId())) {
                equippedWeapons[i] = null;
            }
        }
        purchasedItems.remove(item);
        playerUpgrades.remove(item);
        weaponUpgrades.remove(item);

        if (item instanceof UpgradeItem ui && ui.getEffect() != null) {
            effectContext.unregister(ui.getEffect(), null);
        }
    }


    public void registerEffect(UpgradeItem item, Player player) {
        if (item.getEffect() != null)
            effectContext.register(item.getEffect(), player);
    }

   
    public void unregisterEffect(UpgradeItem item, Player player) {
        if (item.getEffect() != null)
            effectContext.unregister(item.getEffect(), player);
    }

    public List<ShopItem> getItems()       { return purchasedItems; }
    public List<ShopItem> getWeaponItems() {
        return purchasedItems.stream().filter(i -> i instanceof WeaponItem).toList();
    }
    public List<ShopItem> getUpgradeItems() {
        return purchasedItems.stream().filter(i -> i instanceof UpgradeItem).toList();
    }

    public boolean hasItem(String itemId) {
        return purchasedItems.stream()
                .anyMatch(i -> i.getItemDefinition().getId().equals(itemId));
    }


    public void equipWeapon(WeaponItem item, int index) {
        if (index < 0 || index >= MAX_EQU) return;
        if (equippedWeapons[index] == item) return;

        int currentSlot = getEquippedSlot(item);
        if (currentSlot != -1) {
            WeaponItem displaced = equippedWeapons[index];
            equippedWeapons[currentSlot] = displaced;
            equippedWeapons[index] = item;
            if (displaced != null) applyWeaponUpgradesToItem(displaced);
            applyWeaponUpgradesToItem(item);
            return;
        }

        WeaponItem displaced = equippedWeapons[index];
        if (displaced != null && !purchasedItems.contains(displaced))
            purchasedItems.add(displaced);

        equippedWeapons[index] = item;
        purchasedItems.remove(item);
        applyWeaponUpgradesToItem(item);
    }

    public void unequipWeapon(WeaponItem item) {
        if (item == null) return;
        int slot = getEquippedSlot(item);
        if (slot == -1) return;
        equippedWeapons[slot] = null;
        if (!purchasedItems.contains(item)) purchasedItems.add(item);
    }

    public int getEquippedSlot(WeaponItem item) {
        if (item == null) return -1;
        for (int i = 0; i < MAX_EQU; i++)
            if (equippedWeapons[i] == item) return i;
        return -1;
    }

    // ── Numeric upgrade registration (unchanged) ──────────────────────────

    public void addWeaponUpgrade(UpgradeItem upgrade) {
        UpgradeItemDefinition def = (UpgradeItemDefinition) upgrade.getItemDefinition();
        if (!def.isStackable() && weaponUpgrades.stream()
                .anyMatch(e -> e.getItemDefinition().getId().equals(def.getId()))) return;
        weaponUpgrades.add(upgrade);
    }

    public void addPlayerUpgrade(UpgradeItem upgrade) {
        UpgradeItemDefinition def = (UpgradeItemDefinition) upgrade.getItemDefinition();
        if (!def.isStackable() && playerUpgrades.stream()
                .anyMatch(e -> e.getItemDefinition().getId().equals(def.getId()))) return;
        playerUpgrades.add(upgrade);
    }

    
    public double calculateDamageReduction(double incomingDamage) {
        return incomingDamage / Player.getInstance().getDefense();
    }

    // ── Full recalculation (unchanged logic) ──────────────────────────────

    public void recalculateAllUpgrades(Player player) {
        player.setDefense(1);
        player.setRegenerationSpeed(0);

        player.resetBaseStats();

        for (int i = 0; i < equippedWeapons.length; i++) {
            WeaponItem weapon = equippedWeapons[i];
            if (weapon != null) {
                weapon.setItemDefinition(new WeaponItemDefinition(
                    (WeaponItemDefinition) ItemLoader.getInstance()
                        .getBaseItemDefinition(weapon.getItemDefinition().getId())));
            }
        }

        applyPlayerUpgrades(player);
        applyWeaponUpgrades();
    }

    private void applyWeaponUpgradesToItem(WeaponItem weapon) {
        for (UpgradeItem upgrade : weaponUpgrades) {
            UpgradeItemDefinition def = (UpgradeItemDefinition) upgrade.getItemDefinition();
            for (UpgradeItemDefinition.Modification mod : def.getWeaponModifications())
                applyModificationToWeapon(weapon, mod);
        }
    }

    private void applyModificationToWeapon(WeaponItem weapon, UpgradeItemDefinition.Modification mod) {
        WeaponItemDefinition weaponDef    = (WeaponItemDefinition) weapon.getItemDefinition();
        WeaponItemDefinition newWeaponDef = new WeaponItemDefinition(weaponDef);
        try {
            switch (UpgradeItemDefinition.WeaponStat.valueOf(mod.getStat().toUpperCase())) {
                case DAMAGE       -> newWeaponDef.setDamage(applyOp(newWeaponDef.getDamage(),       mod));
                case ATTACK_SPEED -> newWeaponDef.setAttackSpeed(applyOp(newWeaponDef.getAttackSpeed(), mod));
                case RANGE        -> newWeaponDef.setRange(applyOp(newWeaponDef.getRange(),          mod));
                case MAGAZINE_SIZE-> newWeaponDef.setMagazineSize((int) applyOp(newWeaponDef.getMagazineSize(), mod));
                case RELOAD_TIME  -> newWeaponDef.setReloadingTime(applyOp(newWeaponDef.getReloadingTime(), mod));
            }
            weapon.setItemDefinition(newWeaponDef);
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown weapon stat: " + mod.getStat());
        }
    }

    private void applyPlayerUpgrades(Player player) {
        double hpMulBonus    = 0, hpAddBonus    = 0;
        double spdMulBonus   = 0, spdAddBonus   = 0;
        double defAddBonus   = 0; // Defense usually stacks flatly
        double regenAddBonus = 0;
        double dodgeAddBonus = 0;
        double luckMulBonus  = 0, luckAddBonus  = 0;

        for (UpgradeItem upgrade : playerUpgrades) {
            for (UpgradeItemDefinition.Modification mod :
                    ((UpgradeItemDefinition) upgrade.getItemDefinition()).getPlayerModifications()) {
                try {
                    switch (UpgradeItemDefinition.PlayerStat.valueOf(mod.getStat().toUpperCase())) {
                        case MAX_HEALTH      -> { 
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) hpMulBonus  += mod.getValue() - 1.0;
                            else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD)  hpAddBonus  += mod.getValue();
                        }
                        case MOVEMENT_SPEED  -> { 
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) spdMulBonus += mod.getValue() - 1.0;
                            else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD)  spdAddBonus += mod.getValue();
                        }
                        case DEFENSE         -> { 
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) defAddBonus += mod.getValue();
                        }
                        case HEALTH_REGEN    -> { 
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) regenAddBonus += mod.getValue();
                        }
                        case DODGE    -> { 
                        	if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) dodgeAddBonus += mod.getValue();
                        }
                        case LUCK            -> { 
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) luckMulBonus += mod.getValue() - 1.0;
                            else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD)  luckAddBonus += mod.getValue();
                        }
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Unknown player stat: " + mod.getStat());
                }
            }
        }

        // Apply the results to the player object
        player.setHpMax(player.getBaseHPMax() * (1.0 + hpMulBonus) + hpAddBonus);
        player.setSpeed(player.getBaseSpeed() * (1.0 + spdMulBonus) + spdAddBonus);
        player.setLuck(player.getBaseLuck() + luckAddBonus + player.getBaseLuck() * luckMulBonus);
        player.setDefense(Math.max(0, defAddBonus));
        player.setDodge(Math.max( Math.min(dodgeAddBonus, 1), 0));
        player.setRegenerationSpeed(Math.max(0, regenAddBonus));
    }

    private void applyWeaponUpgrades() {
        for (WeaponItem weapon : equippedWeapons) {
            if (weapon == null) continue;
            WeaponItemDefinition baseDef = (WeaponItemDefinition)
                    ItemLoader.getInstance().getBaseItemDefinition(weapon.getItemDefinition().getId());

            double dmgMul = 0, dmgAdd = 0, spdMul = 0, spdAdd = 0;
            double rngMul = 0, rngAdd = 0, magMul = 0, magAdd = 0;
            double rldMul = 0, rldAdd = 0;

            for (UpgradeItem upgrade : weaponUpgrades) {
                for (UpgradeItemDefinition.Modification mod :
                        ((UpgradeItemDefinition) upgrade.getItemDefinition()).getWeaponModifications()) {
                    try {
                        switch (UpgradeItemDefinition.WeaponStat.valueOf(mod.getStat().toUpperCase())) {
                            case DAMAGE        -> { if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) dmgMul += mod.getValue()-1; else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) dmgAdd += mod.getValue(); else dmgMul -= 1.0-1.0/mod.getValue(); }
                            case ATTACK_SPEED  -> { if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) spdMul += mod.getValue()-1; else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) spdAdd += mod.getValue(); else spdMul -= 1.0-1.0/mod.getValue(); }
                            case RANGE         -> { if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) rngMul += mod.getValue()-1; else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) rngAdd += mod.getValue(); else rngMul -= 1.0-1.0/mod.getValue(); }
                            case MAGAZINE_SIZE -> { if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) magMul += mod.getValue()-1; else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) magAdd += mod.getValue(); else magMul -= 1.0-1.0/mod.getValue(); }
                            case RELOAD_TIME   -> { if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) rldMul += mod.getValue()-1; else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD) rldAdd += mod.getValue(); else rldMul -= 1.0-1.0/mod.getValue(); }
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Unknown weapon stat: " + mod.getStat());
                    }
                }
            }

            WeaponItemDefinition newDef = new WeaponItemDefinition(baseDef);
            newDef.setDamage(baseDef.getDamage()               * (1.0 + dmgMul) + dmgAdd);
            newDef.setAttackSpeed(baseDef.getAttackSpeed()     * (1.0 + spdMul) + spdAdd);
            newDef.setRange(baseDef.getRange()                 * (1.0 + rngMul) + rngAdd);
            newDef.setMagazineSize((int)(baseDef.getMagazineSize() * (1.0 + magMul) + magAdd));
            newDef.setReloadingTime(baseDef.getReloadingTime() * (1.0 + rldMul) + rldAdd);
            weapon.setItemDefinition(newDef);
        }
    }

    private double applyOp(double current, UpgradeItemDefinition.Modification mod) {
        return switch (mod.getOperation()) {
            case MULTIPLY -> current * mod.getValue();
            case ADD      -> current + mod.getValue();
            case DIVIDE   -> current / mod.getValue();
        };
    }

    public void init() {
        this.purchasedItems    = new ArrayList<>();
        this.equippedWeapons   = new WeaponItem[MAX_EQU];
        this.weaponUpgrades    = new ArrayList<>();
        this.playerUpgrades    = new ArrayList<>();
        
        
    }

    public static class WeaponBonusSummary {
        public double damageMul    = 1.0;  
        public double damageAdd    = 0.0; 
        public double attackSpeedMul = 1.0;
        public double attackSpeedAdd = 0.0;
        public double rangeMul     = 1.0;
        public double rangeAdd     = 0.0;
        public double magazineMul  = 1.0;
        public double magazineAdd  = 0.0;
        public double reloadDiv    = 1.0;
    }

    
    public WeaponBonusSummary getWeaponBonusSummary() {
        WeaponBonusSummary s = new WeaponBonusSummary();

        for (UpgradeItem upgrade : weaponUpgrades) {
            for (UpgradeItemDefinition.Modification mod :
                    ((UpgradeItemDefinition) upgrade.getItemDefinition()).getWeaponModifications()) {
                try {
                    switch (UpgradeItemDefinition.WeaponStat.valueOf(mod.getStat().toUpperCase())) {
                        case DAMAGE -> {
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) s.damageMul    += mod.getValue() - 1.0;
                            else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD)  s.damageAdd    += mod.getValue();
                        }
                        case ATTACK_SPEED -> {
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) s.attackSpeedMul += mod.getValue() - 1.0;
                            else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD)  s.attackSpeedAdd += mod.getValue();
                        }
                        case RANGE -> {
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) s.rangeMul     += mod.getValue() - 1.0;
                            else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD)  s.rangeAdd     += mod.getValue();
                        }
                        case MAGAZINE_SIZE -> {
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) s.magazineMul  += mod.getValue() - 1.0;
                            else if (mod.getOperation() == UpgradeItemDefinition.OperationType.ADD)  s.magazineAdd  += mod.getValue();
                        }
                        case RELOAD_TIME -> {
                            if (mod.getOperation() == UpgradeItemDefinition.OperationType.DIVIDE)   s.reloadDiv    *= mod.getValue();
                            else if (mod.getOperation() == UpgradeItemDefinition.OperationType.MULTIPLY) s.reloadDiv /= mod.getValue();
                        }
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Unknown weapon stat: " + mod.getStat());
                }
            }
        }

        return s;
    }

    public boolean canSellItem(ShopItem item) {
        return hasItem(item.getItemDefinition().getId()) ||
               Arrays.stream(equippedWeapons)
                     .filter(i -> i != null)
                     .anyMatch(i -> i.getItemDefinition().getId()
                             .equals(item.getItemDefinition().getId()));
    }
}