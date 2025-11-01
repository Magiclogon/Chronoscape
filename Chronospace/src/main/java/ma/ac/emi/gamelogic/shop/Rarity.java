package ma.ac.emi.gamelogic.shop;

public enum Rarity {
    COMMON(100),
    RARE(50),
    EPIC(10),
    LEGENDARY(5);
	
	private int chance;
	Rarity(int chance) {
		this.setChance(chance);
	}
	public int getChance() {
		return chance;
	}
	public void setChance(int chance) {
		this.chance = chance;
	}
}
