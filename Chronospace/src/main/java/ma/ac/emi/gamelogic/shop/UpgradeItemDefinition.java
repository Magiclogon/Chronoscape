package ma.ac.emi.gamelogic.shop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpgradeItemDefinition extends ItemDefinition {
    private UpgradeType upgradeType;
    private WeaponStat weaponStat;
    private PlayerStat playerStat;
    private double multiplier;
    private boolean stackable;


    public enum UpgradeType {
        WEAPON,
        PLAYER
    }

    public enum WeaponStat {
        DAMAGE,
        ATTACK_SPEED,
        RANGE,
        MAGAZINE_SIZE,
        RELOAD_TIME
    }

    public enum PlayerStat {
        MAX_HEALTH,
        MOVEMENT_SPEED,
        DEFENSE,
        HEALTH_REGEN
    }

    public UpgradeItemDefinition() {
        super();
    }

    @Override
    public ShopItem getItem() {
        return new UpgradeItem(this);
    }

    @Override
    public String toString() {
        return "UpgradeItemDefinition{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", upgradeType=" + upgradeType +
                ", weaponStat=" + weaponStat +
                ", playerStat=" + playerStat +
                ", multiplier=" + multiplier +
                ", stackable=" + stackable +
                '}';
    }
}
