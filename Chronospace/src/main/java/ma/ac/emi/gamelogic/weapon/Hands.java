package ma.ac.emi.gamelogic.weapon;

import java.awt.Graphics;

import com.jogamp.opengl.GL3;

import ma.ac.emi.fx.AnimationState;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.Texture;
import ma.ac.emi.math.Matrix4;

public class Hands extends Entity{
	private Weapon weapon;
	
	public Hands(Weapon weapon) {
		this.weapon = weapon;
		
		stateMachine = weapon.getStateMachine().getSkeleton();
		setupAnimations();
		
		GameController.getInstance().removeDrawable(this);
	}
	
	public void update(double step) {
		super.update(step);
		stateMachine.setCurrentAnimationState(weapon.getStateMachine().getCurrentAnimationState().getTitle());
		stateMachine.getCurrentAnimationState().setCurrentFrameIndex(weapon.getStateMachine().getCurrentAnimationState().getCurrentFrameIndex());
		setPos(weapon.getPos());
		
		stateMachine.update(step);
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawGL(GL3 gl, GLGraphics glGraphics) {
		Sprite sprite;
        if (stateMachine.getCurrentAnimationState() != null) {
            sprite = stateMachine.getCurrentAnimationState().getCurrentFrameSprite();
        } else {
            sprite = AssetsLoader.getSprite("default_weapon.png");
        }
        
        if(sprite == null) sprite = new Sprite();
        Texture texture = sprite.getTexture(gl);

        float[] model = new float[16];
        Matrix4.identity(model);

        float px = (float) weapon.getBearer().getPos().getX();
        float py = (float) (weapon.getBearer().getPos().getY() + weapon.getBearer().getWeaponYOffset());
        Matrix4.translate(model, px, py, 0f);

        double theta = weapon.getDir() != null ? Math.atan2(weapon.getDir().getY(), weapon.getDir().getX()) : 0;
        Matrix4.rotateZ(model, (float) theta);

        float wx = (float) weapon.getBearer().getWeaponXOffset() - sprite.getWidth() / 2f;
        float wy = -sprite.getHeight() / 2f;
        Matrix4.translate(model, wx, wy, 0f);

        Matrix4.scale(model, sprite.getWidth(), sprite.getHeight(), 1f);
        
        glGraphics.drawSprite(gl, texture, model, null, getColorCorrection());
	}

	@Override
	public void initStateMachine() {
		
	}

	@Override
	public void setupAnimations() {
		WeaponItemDefinition definition = (WeaponItemDefinition)(weapon.getWeaponItem().getItemDefinition());
		WeaponItemDefinition.WeaponAnimationDetails animationDetails = definition.getAnimationDetails();
		spriteSheet = new SpriteSheet(AssetsLoader.getSprite(animationDetails.handSpriteSheetPath), animationDetails.spriteWidth, animationDetails.spriteHeight);

		AnimationState idle_left = stateMachine.getAnimationStateByTitle("Idle_Left");
		AnimationState idle_right = stateMachine.getAnimationStateByTitle("Idle_Right");
		AnimationState attacking_init_left = stateMachine.getAnimationStateByTitle("Attacking_Init_Left");
		AnimationState attacking_init_right = stateMachine.getAnimationStateByTitle("Attacking_Init_Right");
		AnimationState attacking_left = stateMachine.getAnimationStateByTitle("Attacking_Left");
		AnimationState attacking_right = stateMachine.getAnimationStateByTitle("Attacking_Right");
		AnimationState reload_init_left = stateMachine.getAnimationStateByTitle("Reload_Init_Left");
		AnimationState reload_init_right = stateMachine.getAnimationStateByTitle("Reload_Init_Right");
		AnimationState reload_left = stateMachine.getAnimationStateByTitle("Reload_Left");
		AnimationState reload_right = stateMachine.getAnimationStateByTitle("Reload_Right");
		AnimationState reload_finish_left = stateMachine.getAnimationStateByTitle("Reload_Finish_Left");
		AnimationState reload_finish_right = stateMachine.getAnimationStateByTitle("Reload_Finish_Right");
		AnimationState switching_left = stateMachine.getAnimationStateByTitle("Switching_Left");
		AnimationState switching_right = stateMachine.getAnimationStateByTitle("Switching_Right");
		
		
		if(spriteSheet.getSheet() == null) {
			for(AnimationState state : stateMachine.getAnimationStates()) {
				state.addFrame(new Sprite());
			}
			spriteSheet = new SpriteSheet(new Sprite(), 32, 32);
			return;
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(0, animationDetails.idleLength)) {
			idle_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(6, animationDetails.idleLength)) {
			idle_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(1, animationDetails.attackingInitLength)) {
			attacking_init_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(7, animationDetails.attackingInitLength)) {
			attacking_init_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(2, animationDetails.attackingLength)) {
			attacking_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(8, animationDetails.attackingLength)) {
			attacking_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(3, animationDetails.reloadInitLength)) {
			reload_init_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(9, animationDetails.reloadInitLength)) {
			reload_init_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(4, animationDetails.reloadLength)) {
			reload_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(10, animationDetails.reloadLength)) {
			reload_right.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(5, animationDetails.reloadFinishLength)) {
			reload_finish_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(11, animationDetails.reloadFinishLength)) {
			reload_finish_right.addFrame(sprite);
		}
		
		for(Sprite sprite : spriteSheet.getAnimationRow(5, animationDetails.reloadFinishLength)) {
			switching_left.addFrame(sprite);
		}
		for(Sprite sprite : spriteSheet.getAnimationRow(11, animationDetails.reloadFinishLength)) {
			switching_right.addFrame(sprite);
		}
	}
	
}
