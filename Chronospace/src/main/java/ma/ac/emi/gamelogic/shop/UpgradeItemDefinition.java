package ma.ac.emi.gamelogic.shop;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.effect.PlayerEffect;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UpgradeItemDefinition extends ItemDefinition implements Cloneable {

    private List<Modification> modifications;

    
    private String effectClass;

    private java.util.Map<String, Double> effectParams;


    public enum UpgradeType  { WEAPON, PLAYER }
    public enum WeaponStat   { DAMAGE, ATTACK_SPEED, RANGE, MAGAZINE_SIZE, RELOAD_TIME }
    public enum PlayerStat   { MAX_HEALTH, MOVEMENT_SPEED, DEFENSE, HEALTH_REGEN, DODGE, LUCK }
    public enum OperationType { MULTIPLY, ADD, DIVIDE }


    @Getter @Setter
    public static class Modification implements Cloneable {
        private UpgradeType   upgradeType;
        private String        stat;
        private double        value;
        private OperationType operation;

        public Modification() { this.operation = OperationType.MULTIPLY; }

        public Modification(Modification m) {
            this.upgradeType = m.upgradeType;
            this.stat        = m.stat;
            this.value       = m.value;
            this.operation   = m.operation;
        }

        @Override public Modification clone() { return new Modification(this); }
    }


    public UpgradeItemDefinition() {
        super();
        this.modifications = new ArrayList<>();
        this.stackable = true;
    }

    public UpgradeItemDefinition(UpgradeItemDefinition other) {
        this.setId(other.getId());
        this.setName(other.getName());
        this.setDescription(other.getDescription());
        this.setBasePrice(other.getBasePrice());
        this.setIconPath(other.getIconPath());
        this.setRarity(other.getRarity());
        this.setStackable(other.isStackable());
        this.effectClass   = other.effectClass;
        this.effectParams  = other.effectParams;
        this.modifications = new ArrayList<>();
    }

  
    public PlayerEffect createEffect() {
        if (effectClass == null || effectClass.isBlank()) return null;
        try {
            Class<?> cls = Class.forName(effectClass);
            PlayerEffect effect = (PlayerEffect) cls.getDeclaredConstructor().newInstance();
            effect.configure(effectParams);
            return effect;
        } catch (Exception e) {
            System.err.println("UpgradeItemDefinition: could not instantiate effect '"
                    + effectClass + "': " + e.getMessage());
            return null;
        }
    }


    @Override public ShopItem getItem() { return new UpgradeItem(this); }


    public List<Modification> getWeaponModifications() {
        return modifications.stream()
                .filter(m -> m.getUpgradeType() == UpgradeType.WEAPON).toList();
    }

    public List<Modification> getPlayerModifications() {
        return modifications.stream()
                .filter(m -> m.getUpgradeType() == UpgradeType.PLAYER).toList();
    }


//    @Override
//    public String getStatsDescription() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Upgrade Effects:\n");
//
//        if (modifications.isEmpty() && (effectClass == null || effectClass.isBlank())) {
//            sb.append("No modifications");
//            return sb.toString();
//        }
//
//        for (int i = 0; i < modifications.size(); i++) {
//            Modification mod = modifications.get(i);
//            sb.append("	");
//            String statName = mod.getStat().replace("_", " ");
//            statName = statName.substring(0, 1).toUpperCase() + statName.substring(1).toLowerCase();
//            boolean reduceIsGood = mod.getStat().toUpperCase().contains("RELOAD") ||
//                                   mod.getStat().toUpperCase().contains("TIME");
//            String effect = switch (mod.getOperation()) {
//                case MULTIPLY -> {
//                    double pct = (mod.getValue() - 1.0) * 100;
//                    yield pct > 0 ? String.format("increase by %.0f%%", pct)
//                        : pct < 0 ? String.format("reduce by %.0f%%",   Math.abs(pct))
//                        :           "no change";
//                }
//                case ADD -> mod.getValue() > 0
//                        ? String.format("increase by +%.1f", mod.getValue())
//                        : String.format("decrease by %.1f",  mod.getValue());
//                case DIVIDE -> {
//                    double pct = (1.0 - 1.0 / mod.getValue()) * 100;
//                    yield reduceIsGood ? String.format("reduce by %.0f%%", pct)
//                                       : String.format("decrease by %.0f%%", pct);
//                }
//            };
//            sb.append(statName).append(": ").append(effect);
//            sb.append(" (").append(mod.getUpgradeType().toString().toLowerCase()).append(")");
//            if (i < modifications.size() - 1) sb.append("\n");
//        }
//
//        if (stackable) sb.append("\n• Stackable: Yes");
//        return sb.toString();
//    }
    
    @Override
    public String getStatsDescription() {
    	return this.getDescription();
    }


    @Override
    public UpgradeItemDefinition clone() {
        UpgradeItemDefinition copy = new UpgradeItemDefinition(this);
        for (Modification mod : this.modifications)
            copy.getModifications().add(new Modification(mod));
        return copy;
    }
}