package ma.ac.emi.gamelogic.shop;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UpgradeItemDefinition extends ItemDefinition {
    private List<Modification> modifications;
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

    public enum OperationType {
        MULTIPLY,
        ADD,
        DIVIDE
    }

    @Getter
    @Setter
    public static class Modification {
        private UpgradeType upgradeType;
        private String stat;
        private double value;
        private OperationType operation;

        public Modification() {
            this.operation = OperationType.MULTIPLY;
        }
    }

    public UpgradeItemDefinition() {
        super();
        this.modifications = new ArrayList<>();
    }

    @Override
    public ShopItem getItem() {
        return new UpgradeItem(this);
    }

    public List<Modification> getWeaponModifications() {
        return modifications.stream()
                .filter(m -> m.getUpgradeType() == UpgradeType.WEAPON)
                .toList();
    }

    public List<Modification> getPlayerModifications() {
        return modifications.stream()
                .filter(m -> m.getUpgradeType() == UpgradeType.PLAYER)
                .toList();
    }

    @Override
    public String toString() {
        return "UpgradeItemDefinition{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", modifications=" + modifications +
                ", stackable=" + stackable +
                '}';
    }
}
