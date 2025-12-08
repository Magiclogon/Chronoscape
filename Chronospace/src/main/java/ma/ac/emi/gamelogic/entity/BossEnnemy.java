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

public class BossEnnemy extends Ennemy{

    public BossEnnemy(Vector3D pos, double speed) {
        super(pos, speed);
    }

    @Override
    protected void initStats() {
        setHpMax(80);
        setHp(getHpMax());
    }

    
    @Override
    public void initWeapon() {
        setWeapon(new Weapon(WeaponItemFactory.getInstance().createWeaponItem("ak47")));
        super.initWeapon();
    }
    
    @Override
	public void setupAnimations() {
        setSpriteSheet(new SpriteSheet(AssetsLoader.getSprite("enemies/boss_robot-Sheet.png"), (int)(GamePanel.TILE_SIZE*3), (int)(GamePanel.TILE_SIZE*3)));
		
		AnimationState idle_right = stateMachine.getAnimationStateByTitle("Idle_Right");
		AnimationState run_right = stateMachine.getAnimationStateByTitle("Running_Right");
		AnimationState back_right = stateMachine.getAnimationStateByTitle("Backing_Right");
		AnimationState die_right = stateMachine.getAnimationStateByTitle("Dying_Right");
		
		AnimationState idle_left = stateMachine.getAnimationStateByTitle("Idle_Left");
		AnimationState run_left = stateMachine.getAnimationStateByTitle("Running_Left");
		AnimationState back_left = stateMachine.getAnimationStateByTitle("Backing_Left");
		AnimationState die_left = stateMachine.getAnimationStateByTitle("Dying_Left");
		
		
		for(Sprite sprite : spriteSheet.getAnimationRow(3, 18)) {
			idle_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(4, 32)) {
			run_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(5, 32)) {
			back_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(0, 18)) {
			idle_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(1, 32)) {
			run_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(2, 32)) {
			back_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(7, 106)) {
			die_left.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(6, 106)) {
			die_right.addFrame(sprite);
		}
    }

    @Override
    public void draw(Graphics g) {
        if(hp > 0) {
            g.setColor(Color.BLUE);
            g.fillRect((int)(pos.getX()), (int)(pos.getY()), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
        }

        g.setColor(Color.yellow);
        g.drawRect(bound.x, bound.y, bound.width, bound.height);
        
        super.draw(g);

    }
}
