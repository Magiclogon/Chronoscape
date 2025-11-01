package ma.ac.emi.gamelogic.shop;

public class StatModifierItemDefinition extends ItemDefinition {
    private String stat;  // e.g., "health", "damage", "speed"
    private float modifier; // e.g., +10%, +50 HP
    
	@Override
	public ShopItem getItem() {
		return new StatModifierItem(this);
	}
    
}