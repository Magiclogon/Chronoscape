package ma.ac.emi.gamelogic.entity;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.AnimationState;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector3D;

@Setter
@Getter
public abstract class LivingEntity extends Entity {
	private static final String[] ACTIONS = {"Idle", "Running", "Backing", "Dying"};
	private static final String[] DIRECTIONS = {"Left", "Right"};
	
	// Movement state constants
	private static final String TRIGGER_RUN = "Run";
	private static final String TRIGGER_STOP = "Stop";
	private static final String TRIGGER_BACK = "Back";
	private static final String TRIGGER_DIE = "Die";
	
	protected Weapon activeWeapon;
	protected double hp;
	protected double hpMax;
	protected double baseHP;
	protected double baseHPMax;
	protected double baseSpeed;
	protected double strength;
	protected double baseStrength;
	protected double regenerationSpeed;
	protected double speed;
	protected AttackObjectManager attackObjectManager;
	
	
	@Override
	public void initStateMachine() {
		// Create all animation states programmatically
		for (String action : ACTIONS) {
			for (String direction : DIRECTIONS) {
				String stateName = action + "_" + direction;
				AnimationState state = new AnimationState(stateName);
				if(action.equals("Dying")) state.setDoesLoop(false);
				stateMachine.addAnimationState(state);
			}
		}
		
		// Add transitions for running and stopping
		addMovementTransitions(TRIGGER_RUN, "Idle", "Running");
		addMovementTransitions(TRIGGER_STOP, "Running", "Idle");
		addMovementTransitions(TRIGGER_DIE, "Idle", "Dying");
		addMovementTransitions(TRIGGER_STOP, "Dying", "Idle");
		// Add transitions for backing and stopping
		addBackingTransitions();
		
		// Add look direction transitions
		addLookTransitions();
		
		stateMachine.setDefaultState("Idle_Right");
	}
	
	private void addMovementTransitions(String trigger, String fromAction, String toAction) {
		for (String direction : DIRECTIONS) {
			stateMachine.addStateTransfer(trigger, 
				fromAction + "_" + direction, 
				toAction + "_" + direction);
		}
	}
	
	private void addBackingTransitions() {
		// Backing reverses the direction
		stateMachine.addStateTransfer(TRIGGER_BACK, "Idle_Left", "Backing_Right");
		stateMachine.addStateTransfer(TRIGGER_STOP, "Backing_Right", "Idle_Left");
		stateMachine.addStateTransfer(TRIGGER_BACK, "Idle_Right", "Backing_Left");
		stateMachine.addStateTransfer(TRIGGER_STOP, "Backing_Left", "Idle_Right");
	}
	
	private void addLookTransitions() {
		stateMachine.addStateTransfer("Look_Left", "Idle_Right", "Idle_Left");
		stateMachine.addStateTransfer("Look_Right", "Idle_Left", "Idle_Right");
	}
	
	public abstract void attack();
	
	protected boolean isIdle() {
		return isInState("Idle");
	}
	
	protected boolean isRunning() {
		return isInState("Running");
	}
	
	protected boolean isBacking() {
		return isInState("Backing");
	}
	
	protected boolean isDying() {
		return isInState("Dying");
	}
	
	private boolean isInState(String action) {
		String currentState = stateMachine.getCurrentAnimationState().getTitle();
		return currentState.startsWith(action + "_");
	}
	
	protected void changeStateDirection() {
		if (activeWeapon == null) {
			return;
		}
		
		if(isDying()) return;
		
		DirectionInfo directionInfo = determineDirection();
		boolean wasMoving = isRunning() || isBacking();
		
		// Stop movement if currently moving
		if (wasMoving) {
			stateMachine.trigger(TRIGGER_STOP);
		}
		
		// Update facing direction
		updateFacingDirection(directionInfo.direction);
		
		// Resume movement if was moving
		if (wasMoving) {
			Vector3D rightVect = new Vector3D(1, 0, 0);
			boolean movingForward = rightVect.mult(getVelocity().dotP(rightVect)).dotP(activeWeapon.getDir()) >= 0;
			stateMachine.trigger(movingForward ? TRIGGER_RUN : TRIGGER_BACK);
		}
	}
	
	private DirectionInfo determineDirection() {
		Vector3D weaponDir = activeWeapon.getDir();
		
		double dpLeft = weaponDir.dotP(new Vector3D(-1, 0, 0));
		double dpRight = weaponDir.dotP(new Vector3D(1, 0, 0));
		
		double[] dotProducts = {dpLeft, dpRight};
		int maxIndex = 0;
		double maxDotProduct = dpLeft;
		
		for (int i = 1; i < dotProducts.length; i++) {
			if (dotProducts[i] > maxDotProduct) {
				maxDotProduct = dotProducts[i];
				maxIndex = i;
			}
		}
		
		return new DirectionInfo(maxIndex, DIRECTIONS[maxIndex]);
	}
	
	private void updateFacingDirection(String targetDirection) {
		String currentState = stateMachine.getCurrentAnimationState().getTitle();
		String targetIdleState = "Idle_" + targetDirection;
		
		if (currentState.equals(targetIdleState)) {
			return;
		}
		
		stateMachine.trigger("Look_" + targetDirection);
		
	}
	
	private static class DirectionInfo {
		final int index;
		final String direction;
		
		DirectionInfo(int index, String direction) {
			this.index = index;
			this.direction = direction;
		}
	}
}