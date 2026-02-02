package ma.ac.emi.gamelogic.entity;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.ai.AIBehavior;
import ma.ac.emi.gamelogic.physics.AABB;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;
import java.awt.*;

@Setter
@Getter
public abstract class Ennemy extends LivingEntity {

	protected double damage;
	protected Weapon weapon;
	protected AIBehavior aiBehavior;

	public Ennemy(Vector3D pos, double speed) {
		super();
		this.pos = pos;
		this.speed = speed;
		this.velocity = new Vector3D();

		initStats();
		setupAnimations();
		
		double width = spriteSheet == null ? GamePanel.TILE_SIZE : spriteSheet.getTileWidth();
		double height = spriteSheet == null ? GamePanel.TILE_SIZE : spriteSheet.getTileHeight();
		hitbox = new AABB(new Vector3D(), new Vector3D(width, height).mult(0.5));
		bound = new AABB(new Vector3D(), new Vector3D(width, height/4).mult(0.5));
	}

	protected abstract void initStats();

	public void initWeapon() {
		if(getWeapon() == null) return;
		getWeapon().setAttackObjectManager(getAttackObjectManager());
		getWeapon().snapTo(this);
	}

	public void update(double step, Vector3D targetPos) {
		velocity.init();

		// State Machine Logic
		if(!isIdle() && !isDying()) stateMachine.trigger("Stop");

		if(getHp() <= 0) {
			if(!isDying()) stateMachine.trigger("Die");
			stateMachine.update(step);
			if(dustEmitter.isActive()) dustEmitter.setActive(false);
			return;
		}

		// AI Logic
		if (aiBehavior != null) {
			Vector3D direction = aiBehavior.calculateMovement(this, targetPos, step);
			setVelocity(direction.mult(getSpeed()));

			if (aiBehavior.shouldAttack(this, targetPos)) {
				attack();
			} else {
				stopAttacking();
			}

			pointAt(targetPos);

			if(!isIdle()) stateMachine.trigger("Stop");
			stateMachine.trigger("Run");
		} else {
			// Fallback basic movement
			Vector3D direction = (targetPos.sub(getPos())).normalize();
			setVelocity(direction.mult(getSpeed()));
		}

		if(getWeapon() != null) {
			getWeapon().update(step);
		}

		changeStateDirection();
		stateMachine.update(step);

		super.update(step);
	}

	@Override
	public void update(double step) {
		// EMPTY and LEAVE IT EMPTY plz mat9isch hadi
	}

	@Override
	public void attack() {
		if (this.weapon != null) {
			this.weapon.attack();
		}
	}

	@Override
	public void stopAttacking() {
		if (this.weapon != null) {
			this.weapon.stopAttacking();
		}
	}

	@Override
	public void consumeAmmo() {}

}