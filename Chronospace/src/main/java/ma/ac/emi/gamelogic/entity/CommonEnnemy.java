package ma.ac.emi.gamelogic.entity;

import ma.ac.emi.fx.AnimationState;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.gamelogic.weapon.WeaponItemFactory;
import ma.ac.emi.math.Vector3D;

import java.awt.*;

public class CommonEnnemy extends Ennemy{
    public CommonEnnemy(Vector3D pos, double speed) {
        super(pos, speed);
    }
    
    @Override
	public void setupAnimations() {
        setSpriteSheet(new SpriteSheet(AssetsLoader.getSprite("enemies/common_robot-Sheet.png"), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE));
		
		AnimationState idle_right = stateMachine.getAnimationStateByTitle("Idle_Right");
		AnimationState run_right = stateMachine.getAnimationStateByTitle("Running_Right");
		AnimationState back_right = stateMachine.getAnimationStateByTitle("Backing_Right");
		AnimationState die_right = stateMachine.getAnimationStateByTitle("Dying_Right");
		
		AnimationState idle_left = stateMachine.getAnimationStateByTitle("Idle_Left");
		AnimationState run_left = stateMachine.getAnimationStateByTitle("Running_Left");
		AnimationState back_left = stateMachine.getAnimationStateByTitle("Backing_Left");
		AnimationState die_left = stateMachine.getAnimationStateByTitle("Dying_Left");
		
		
		for(Sprite sprite : spriteSheet.getAnimationRow(3, 1)) {
			idle_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(4, 6)) {
			run_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(5, 6)) {
			back_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(0, 1)) {
			idle_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(1, 6)) {
			run_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(2, 6)) {
			back_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(7, 28)) {
			die_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(6, 28)) {
			die_right.addFrame(sprite);
		}
	}

    @Override
    protected void initStats() {
        setHpMax(100);
        setHp(getHpMax());
    }
    
    @Override
    public void initWeapon() {
        setWeapon(new Weapon(WeaponItemFactory.getInstance().createWeaponItem("fists")));
        super.initWeapon();
    }

}
