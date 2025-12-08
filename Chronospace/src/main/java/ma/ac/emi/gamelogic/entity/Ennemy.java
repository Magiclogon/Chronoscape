package ma.ac.emi.gamelogic.entity;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.ai.AIBehavior;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.difficulty.DifficultyObserver;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;
import java.awt.*;

import javax.swing.SwingUtilities;

@Setter
@Getter
public abstract class Ennemy extends LivingEntity implements DifficultyObserver{
	protected double damage;
	protected Weapon weapon;
	protected AIBehavior aiBehavior;
	
	public Ennemy(Vector3D pos, double speed) {
		super();
		this.pos = pos;
		this.speed = speed;
		this.velocity = new Vector3D();
		bound = new Rectangle(GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);

		GameController.getInstance().addDifficultyObserver(this);
		initStats();
	}

	protected abstract void initStats();
	
	public void initWeapon() {
		getWeapon().setAttackObjectManager(getAttackObjectManager());
		getWeapon().snapTo(this);
	}

	public void update(double step, Vector3D targetPos) {
		velocity.init();
		if(!isIdle() && !isDying()) stateMachine.trigger("Stop");
		if(getHp() <= 0) {
			if(!isDying()) stateMachine.trigger("Die");
			System.out.print(stateMachine.getCurrentAnimationState().getCurrentFrameIndex());
			System.out.println("/"+stateMachine.getCurrentAnimationState().getFrames().size());
			stateMachine.update(step);
			return;
		}
		
		if (aiBehavior != null) {
			Vector3D direction = aiBehavior.calculateMovement(this, targetPos, step);
			setVelocity(direction.mult(getSpeed()));

			if (aiBehavior.shouldAttack(this, targetPos)) {
				attack();
			}
			
			pointAt(targetPos);
			
			
			if(!isIdle())stateMachine.trigger("Stop");
			stateMachine.trigger("Run");
		} else {
			// basic
			Vector3D direction = (targetPos.sub(getPos())).normalize();
			setVelocity(direction.mult(getSpeed()));
		}

		setPos(getPos().add(velocity.mult(step)));

		bound.x = (int) getPos().getX();
		bound.y = (int) getPos().getY();
		
		if(getWeapon() != null) {
			getWeapon().update(step);
		}
		
		changeStateDirection();
		stateMachine.update(step);
	}

	@Override
	public void update(double step) {/* todo nothing */}


	@Override
	public void attack() {
		if (this.weapon != null) {
			this.weapon.attack();
		}
	}

	@Override
	public void refreshDifficulty(DifficultyStrategy difficutly) {
		difficutly.adjustEnemyStats(this);
	}


	@Override
	public void setupAnimations() {}

	

    @Override
    public void draw(Graphics g) {
        g.drawImage(stateMachine.getCurrentAnimationState().getCurrentFrameSprite().getSprite(), (int)pos.getX(), (int)pos.getY(), null);
        
    }


}
