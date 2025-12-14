package ma.ac.emi.gamelogic.shop;

import lombok.*;
import ma.ac.emi.gamelogic.weapon.Weapon;

@Getter
@Setter
@ToString
public class WeaponItemDefinition extends ItemDefinition implements Cloneable{
	private String spriteSheetPath;
	private int spriteWidth;
	private int spriteHeight;
    private String attackStrategy;
    private double damage;
    private double range;
    private double attackSpeed;
    private String projectileId;
    private double reloadingTime;
    private int magazineSize;

	@Override
	public ShopItem getItem() {
		return new WeaponItem(this);
	}

    @Override
    public String getStatsDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Weapon Stats:\n");
        sb.append("• Damage: ").append(String.format("%.1f", damage)).append("\n");
        sb.append("• Range: ").append(String.format("%.1f", range)).append("\n");
        sb.append("• Attack Speed: ").append(String.format("%.2f", attackSpeed)).append("/s\n");
        sb.append("• Magazine Size: ").append(magazineSize).append("\n");
        sb.append("• Reload Time: ").append(String.format("%.2f", reloadingTime)).append("s\n");
        return sb.toString();
    }

    public WeaponItemDefinition(WeaponItemDefinition other) {
        this.setId(other.getId());
        this.setName(other.getName());
        this.setDescription(other.getDescription());
        this.setBasePrice(other.getBasePrice());
        this.setIconPath(other.getIconPath());
        this.setRarity(other.getRarity());

        this.attackStrategy = other.attackStrategy;
        this.damage = other.damage;
        this.range = other.range;
        this.attackSpeed = other.attackSpeed;
        this.projectileId = other.projectileId;
        this.reloadingTime = other.reloadingTime;
        this.magazineSize = other.magazineSize;
        this.spriteSheetPath = other.spriteSheetPath;
        this.spriteWidth = other.spriteWidth;
        this.spriteHeight = other.spriteHeight;
    }
    
    
    @Override
    public WeaponItemDefinition clone() {
		return new WeaponItemDefinition(this);
    	
    }

}
