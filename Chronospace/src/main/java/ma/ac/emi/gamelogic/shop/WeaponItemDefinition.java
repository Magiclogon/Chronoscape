package ma.ac.emi.gamelogic.shop;

import java.util.ArrayList;
import java.util.List;

import lombok.*;
import ma.ac.emi.camera.CameraShakeDefinition;
import ma.ac.emi.gamelogic.weapon.AttackStrategy;
import ma.ac.emi.gamelogic.weapon.MeleeStrategy;
import ma.ac.emi.gamelogic.weapon.RangeStrategy;
import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehaviorDefinition;
import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.entitypost.config.PostProcessingDetails;
import ma.ac.emi.glgraphics.lighting.LightingStrategy;

@Getter
@Setter
@ToString
public class WeaponItemDefinition extends ItemDefinition implements Cloneable{
    private double damage;
    private double range;
    private double attackSpeed;
    private String projectileId;
    private double projectileSpeed;
    private double reloadingTime;
    private int magazineSize;
	private double recoilForce;
    private int relativeProjectilePosX;
    private int relativeProjectilePosY;
    
    private WeaponAnimationDetails animationDetails;
    private AttackStrategyDefinition attackStrategyDefinition;
    private transient SpriteColorCorrection colorCorrection;
    private transient LightingStrategy lightingStrategy;
    
    private List<WeaponBehaviorDefinition> behaviorDefinitions = new ArrayList<>();
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
		this.recoilForce = other.recoilForce;
        this.range = other.range;
        this.attackSpeed = other.attackSpeed;
        this.projectileId = other.projectileId;
        this.projectileSpeed = other.projectileSpeed;
        this.reloadingTime = other.reloadingTime;
        this.magazineSize = other.magazineSize;
        this.relativeProjectilePosX = other.relativeProjectilePosX;
        this.relativeProjectilePosY = other.relativeProjectilePosY;
        
        this.animationDetails = other.animationDetails;
        this.attackStrategyDefinition = other.attackStrategyDefinition;
        this.colorCorrection = other.colorCorrection;
        this.lightingStrategy = other.lightingStrategy;
        this.behaviorDefinitions = other.behaviorDefinitions;
    }
    
    
    @Override
    public WeaponItemDefinition clone() {
		return new WeaponItemDefinition(this);
    	
    }
    
    public static abstract class AttackStrategyDefinition{
    	public CameraShakeDefinition cameraShakeDefinition;
    	
    	public AttackStrategyDefinition() {this.cameraShakeDefinition = new CameraShakeDefinition(0, 0);}
    	public abstract AttackStrategy create();
    	
    	public void setCameraShakeDefinition(CameraShakeDefinition cameraShakeDefinition) {this.cameraShakeDefinition = cameraShakeDefinition;}
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
			return new RangeStrategy(projectileCount, spread, new CameraShakeDefinition(cameraShakeDefinition));
		}
    }
    
    public static class MeleeStrategyDefinition extends AttackStrategyDefinition{

		@Override
		public AttackStrategy create() {
			return new MeleeStrategy(new CameraShakeDefinition(cameraShakeDefinition));
		}

    }
    
    public static class WeaponAnimationDetails{
    	public String spriteSheetPath;
    	public String handSpriteSheetPath;
		public int spriteWidth;
    	public int spriteHeight;
    	public int idleLength, attackingInitLength, attackingLength, reloadInitLength, reloadLength, reloadFinishLength;
    	

    	public WeaponAnimationDetails(String spriteSheetPath, String handSpriteSheetPath, int spriteWidth, int spriteHeight, int idleLength,
				int attackingInitLength, int attackingLength, int reloadInitLength, int reloadLength, int reloadFinishLength) {
			this.spriteSheetPath = spriteSheetPath;
			this.handSpriteSheetPath = handSpriteSheetPath;
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
    			details.handSpriteSheetPath,
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
