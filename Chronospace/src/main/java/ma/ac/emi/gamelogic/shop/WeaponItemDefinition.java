package ma.ac.emi.gamelogic.shop;

import lombok.*;
import ma.ac.emi.gamelogic.weapon.AttackStrategy;
import ma.ac.emi.gamelogic.weapon.MeleeStrategy;
import ma.ac.emi.gamelogic.weapon.RangeStrategy;
import ma.ac.emi.gamelogic.weapon.Weapon;

@Getter
@Setter
@ToString
public class WeaponItemDefinition extends ItemDefinition implements Cloneable{
    private double damage;
    private double range;
    private double attackSpeed;
    private String projectileId;
    private double reloadingTime;
    private int magazineSize;
    private int relativeProjectilePosX;
    private int relativeProjectilePosY;
    
    private WeaponAnimationDetails animationDetails;
    private AttackStrategyDefinition attackStrategyDefinition;

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

        this.damage = other.damage;
        this.range = other.range;
        this.attackSpeed = other.attackSpeed;
        this.projectileId = other.projectileId;
        this.reloadingTime = other.reloadingTime;
        this.magazineSize = other.magazineSize;
        this.relativeProjectilePosX = other.relativeProjectilePosX;
        this.relativeProjectilePosY = other.relativeProjectilePosY;
        
        this.animationDetails = other.animationDetails;
    }
    
    
    @Override
    public WeaponItemDefinition clone() {
		return new WeaponItemDefinition(this);
    	
    }
    
    public static abstract class AttackStrategyDefinition{
    	public abstract AttackStrategy create();
    }
    
    public static class RangeStrategyDefinition extends AttackStrategyDefinition{
		public final int projectileCount;
    	public final double spread; //angle
    	
       	public RangeStrategyDefinition(int projectileCount, double spread) {
    			this.projectileCount = projectileCount;
    			this.spread = spread;
    	}
       	public RangeStrategyDefinition(RangeStrategyDefinition def) {
       		this(def.projectileCount, def.spread);
       	}
		@Override
		public AttackStrategy create() {
			return new RangeStrategy(projectileCount, spread);
		}
    }
    
    public static class MeleeStrategyDefinition extends AttackStrategyDefinition{

		@Override
		public AttackStrategy create() {
			return new MeleeStrategy();
		}

    }
    
    public static class WeaponAnimationDetails{
    	public String spriteSheetPath;
		public int spriteWidth;
    	public int spriteHeight;
    	public int idleLength, attackingInitLength, attackingLength, reloadInitLength, reloadLength, reloadFinishLength;
    	

    	public WeaponAnimationDetails(String spriteSheetPath, int spriteWidth, int spriteHeight, int idleLength,
				int attackingInitLength, int attackingLength, int reloadInitLength, int reloadLength, int reloadFinishLength) {
			this.spriteSheetPath = spriteSheetPath;
			this.spriteWidth = spriteWidth;
			this.spriteHeight = spriteHeight;
			this.idleLength = idleLength;
			this.attackingInitLength = attackingInitLength;
			this.attackingLength = attackingLength;
			this.reloadInitLength = reloadInitLength;
			this.reloadLength = reloadLength;
			this.reloadFinishLength = reloadFinishLength;
		}
    	
    	public WeaponAnimationDetails(WeaponAnimationDetails details) {
    		this(details.spriteSheetPath,
    			details.spriteWidth,
    			details.spriteHeight,
    			details.idleLength,
    			details.attackingInitLength,
    			details.attackingLength,
    			details.reloadInitLength,
    			details.reloadLength,
    			details.reloadFinishLength
    		);
    	}
    }
}
