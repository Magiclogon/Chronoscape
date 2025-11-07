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
        HEALTH_REGEN
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
