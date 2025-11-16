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
	private static final String[] ACTIONS = {"Idle", "Running", "Backing"};
	private static final String[] DIRECTIONS = {"Left", "Right", "Up", "Down"};
	
	// Movement state constants
	private static final String TRIGGER_RUN = "Run";
	private static final String TRIGGER_STOP = "Stop";
	private static final String TRIGGER_BACK = "Back";
	
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
	
	public LivingEntity() {
		super(true);
		initStateMachine();
	}
	
	private void initStateMachine() {
		// Create all animation states programmatically
		for (String action : ACTIONS) {
			for (String direction : DIRECTIONS) {
				String stateName = action + "_" + direction;
				stateMachine.addAnimationState(new AnimationState(stateName));
			}
		}
		
		// Add transitions for running and stopping
		addMovementTransitions(TRIGGER_RUN, "Idle", "Running");
		addMovementTransitions(TRIGGER_STOP, "Running", "Idle");
		
		// Add transitions for backing and stopping
		addBackingTransitions();
		
		// Add look direction transitions
		addLookTransitions();
		
		stateMachine.setDefaultState("Idle_Down");
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
		stateMachine.addStateTransfer(TRIGGER_BACK, "Idle_Up", "Backing_Down");
		stateMachine.addStateTransfer(TRIGGER_STOP, "Backing_Down", "Idle_Up");
		stateMachine.addStateTransfer(TRIGGER_BACK, "Idle_Down", "Backing_Up");
		stateMachine.addStateTransfer(TRIGGER_STOP, "Backing_Up", "Idle_Down");
	}
	
	private void addLookTransitions() {
		stateMachine.addStateTransfer("Look_Left", "Idle_Up", "Idle_Left");
		stateMachine.addStateTransfer("Look_Left", "Idle_Down", "Idle_Left");
		stateMachine.addStateTransfer("Look_Right", "Idle_Up", "Idle_Right");
		stateMachine.addStateTransfer("Look_Right", "Idle_Down", "Idle_Right");
		stateMachine.addStateTransfer("Look_Up", "Idle_Left", "Idle_Up");
		stateMachine.addStateTransfer("Look_Up", "Idle_Right", "Idle_Up");
		stateMachine.addStateTransfer("Look_Down", "Idle_Left", "Idle_Down");
		stateMachine.addStateTransfer("Look_Down", "Idle_Right", "Idle_Down");
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
	
	private boolean isInState(String action) {
		String currentState = stateMachine.getCurrentAnimationState().getTitle();
		return currentState.startsWith(action + "_");
	}
	
	protected void changeStateDirection() {
		if (activeWeapon == null) {
			return;
		}
		
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
			boolean movingForward = getVelocity().dotP(activeWeapon.getDir()) >= 0;
			stateMachine.trigger(movingForward ? TRIGGER_RUN : TRIGGER_BACK);
		}
	}
	
	private DirectionInfo determineDirection() {
		Vector3D weaponDir = activeWeapon.getDir();
		
		double dpLeft = weaponDir.dotP(new Vector3D(-1, 0, 0));
		double dpRight = weaponDir.dotP(new Vector3D(1, 0, 0));
		double dpUp = weaponDir.dotP(new Vector3D(0, -1, 0));
		double dpDown = weaponDir.dotP(new Vector3D(0, 1, 0));
		
		double[] dotProducts = {dpLeft, dpRight, dpUp, dpDown};
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
		
		// Handle special transitions through intermediate states
		if (needsIntermediateTransition(currentState, targetDirection)) {
			String intermediateDirection = getIntermediateDirection(currentState);
			stateMachine.trigger("Look_" + intermediateDirection);
		}
		
		// Trigger final look direction
		if (!stateMachine.getCurrentAnimationState().getTitle().equals(targetIdleState)) {
			stateMachine.trigger("Look_" + targetDirection);
		}
	}
	
	private boolean needsIntermediateTransition(String currentState, String targetDirection) {
		// Left-Right and Right-Left need intermediate state
		return (currentState.equals("Idle_Right") && targetDirection.equals("Left")) ||
		       (currentState.equals("Idle_Left") && targetDirection.equals("Right")) ||
		       (currentState.equals("Idle_Up") && targetDirection.equals("Down")) ||
		       (currentState.equals("Idle_Down") && targetDirection.equals("Up"));
	}
	
	private String getIntermediateDirection(String currentState) {
		if (currentState.equals("Idle_Right") || currentState.equals("Idle_Left")) {
			return "Up";
		}
		return "Left";
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