package ma.ac.emi.gamelogic.shop;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UpgradeItemDefinition extends ItemDefinition implements Cloneable{
    private List<Modification> modifications;
    
    public UpgradeItemDefinition(UpgradeItemDefinition other) {
    	this.setId(other.getId());
        this.setName(other.getName());
        this.setDescription(other.getDescription());
        this.setBasePrice(other.getBasePrice());
        this.setIconPath(other.getIconPath());
        this.setRarity(other.getRarity());
        
        modifications = new ArrayList<>();
    }
    
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
        HEALTH_REGEN,
        LUCK
    }

    public enum OperationType {
        MULTIPLY,
        ADD,
        DIVIDE
    }

    @Getter
    @Setter
    public static class Modification implements Cloneable{
        private UpgradeType upgradeType;
        private String stat;
        private double value;
        private OperationType operation;

        public Modification() {
            this.operation = OperationType.MULTIPLY;
        }
        
        public Modification(Modification modification) {
        	this.upgradeType = modification.upgradeType;
        	this.stat = new String(modification.stat);
        	this.value = modification.value;
        	this.operation = modification.operation;
        }
        
        @Override
        public Modification clone() {
    		return new Modification(this);
        	
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

    @Override
    public String getStatsDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Upgrade Effects:\n");

        if (modifications.isEmpty()) {
            sb.append("No modifications");
            return sb.toString();
        }

        for (int i = 0; i < modifications.size(); i++) {
            Modification mod = modifications.get(i);
            sb.append("• ");

            // Format the stat name nicely
            String statName = mod.getStat().replace("_", " ");
            statName = statName.substring(0, 1).toUpperCase() +
                    statName.substring(1).toLowerCase();

            boolean reduceIsGood = mod.getStat().toUpperCase().contains("RELOAD") ||
                    mod.getStat().toUpperCase().contains("TIME");

            String effect = switch (mod.getOperation()) {
                case MULTIPLY -> {
                    double percentage = (mod.getValue() - 1.0) * 100;
                    if (percentage > 0) {
                        yield String.format("increase by %.0f%%", percentage);
                    } else if (percentage < 0) {
                        yield String.format("reduce by %.0f%%", Math.abs(percentage));
                    } else {
                        yield "no change";
                    }
                }
                case ADD -> {
                    if (mod.getValue() > 0) {
                        yield String.format("increase by +%.1f", mod.getValue());
                    } else {
                        yield String.format("decrease by %.1f", mod.getValue());
                    }
                }
                case DIVIDE -> {
                    double percentage = (1.0 - (1.0 / mod.getValue())) * 100;
                    if (reduceIsGood) {
                        yield String.format("reduce by %.0f%%", percentage);
                    } else {
                        yield String.format("decrease by %.0f%%", percentage);
                    }
                }
            };

            sb.append(statName).append(": ").append(effect);
            sb.append(" (").append(mod.getUpgradeType().toString().toLowerCase()).append(")");

            if (i < modifications.size() - 1) {
                sb.append("\n");
            }
        }

        if (stackable) {
            sb.append("\n• Stackable: Yes");
        }

        return sb.toString();
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
                ", modifications=" + getModifications() +
                ", stackable=" + isStackable() +
                '}';
    }
    
    
    @Override
    public UpgradeItemDefinition clone() {
		UpgradeItemDefinition copy = new UpgradeItemDefinition(this);
		for(Modification mod: this.getModifications()) {
			copy.getModifications().add(new Modification(mod));
		}
    	return copy;
    }
}
