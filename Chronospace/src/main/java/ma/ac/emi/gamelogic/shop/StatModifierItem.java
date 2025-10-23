package ma.ac.emi.gamelogic.shop;

public class StatModifierItem extends ShopItem {
    private double hpModifier;
    private double strengthModifier;
    private double velocityModifier;
    private double regenerationModifier;

    @Override
    public void apply(Player player) {
        StatModifier hpMod = new StatModifier("hp", hpModifier, true);
        StatModifier strMod = new StatModifier("strength", strengthModifier, true);
        StatModifier velMod = new StatModifier("velocity", velocityModifier, true);
        StatModifier regenMod = new StatModifier("regeneration", regenerationModifier, true);

        player.getInventory().addStatModifier("hp", hpMod);
        player.getInventory().addStatModifier("strength", strMod);
        player.getInventory().addStatModifier("velocity", velMod);
        player.getInventory().addStatModifier("regeneration", regenMod);
    }

    public double getHpModifier() { return hpModifier; }
    public void setHpModifier(double hpModifier) { this.hpModifier = hpModifier; }

    public double getStrengthModifier() { return strengthModifier; }
    public void setStrengthModifier(double strengthModifier) {
        this.strengthModifier = strengthModifier;
    }

    public double getVelocityModifier() { return velocityModifier; }
    public void setVelocityModifier(double velocityModifier) {
        this.velocityModifier = velocityModifier;
    }

    public double getRegenerationModifier() { return regenerationModifier; }
    public void setRegenerationModifier(double regenerationModifier) {
        this.regenerationModifier = regenerationModifier;
    }
}
