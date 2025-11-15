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
	protected Weapon activeWeapon;
    public LivingEntity() {
		super(true);
		initStateMachine();
	}
    
    private void initStateMachine() {
    	AnimationState idle_left = new AnimationState("Idle_Left");
		stateMachine.addAnimationState(idle_left);
		AnimationState idle_right = new AnimationState("Idle_Right");
		stateMachine.addAnimationState(idle_right);
		AnimationState idle_up = new AnimationState("Idle_Up");
		stateMachine.addAnimationState(idle_up);
		AnimationState idle_down = new AnimationState("Idle_Down");
		stateMachine.addAnimationState(idle_down);
		
		AnimationState run_left = new AnimationState("Running_Left");
		stateMachine.addAnimationState(run_left);
		AnimationState run_right = new AnimationState("Running_Right");
		stateMachine.addAnimationState(run_right);
		AnimationState run_up = new AnimationState("Running_Up");
		stateMachine.addAnimationState(run_up);
		AnimationState run_down = new AnimationState("Running_Down");
		stateMachine.addAnimationState(run_down);
		
		AnimationState back_left = new AnimationState("Backing_Left");
		stateMachine.addAnimationState(back_left);
		AnimationState back_right = new AnimationState("Backing_Right");
		stateMachine.addAnimationState(back_right);
		AnimationState back_up = new AnimationState("Backing_Up");
		stateMachine.addAnimationState(back_up);
		AnimationState back_down = new AnimationState("Backing_Down");
		stateMachine.addAnimationState(back_down);
		
		stateMachine.addStateTransfer("Run", "Idle_Left", "Running_Left");
		stateMachine.addStateTransfer("Stop", "Running_Left", "Idle_Left");
		stateMachine.addStateTransfer("Run", "Idle_Right", "Running_Right");
		stateMachine.addStateTransfer("Stop", "Running_Right", "Idle_Right");
		stateMachine.addStateTransfer("Run", "Idle_Up", "Running_Up");
		stateMachine.addStateTransfer("Stop", "Running_Up", "Idle_Up");
		stateMachine.addStateTransfer("Run", "Idle_Down", "Running_Down");
		stateMachine.addStateTransfer("Stop", "Running_Down", "Idle_Down");
		
		stateMachine.addStateTransfer("Back", "Idle_Left", "Backing_Right");
		stateMachine.addStateTransfer("Stop", "Backing_Right", "Idle_Left");
		stateMachine.addStateTransfer("Back", "Idle_Right", "Backing_Left");
		stateMachine.addStateTransfer("Stop", "Backing_Left", "Idle_Right");
		stateMachine.addStateTransfer("Back", "Idle_Up", "Backing_Down");
		stateMachine.addStateTransfer("Stop", "Backing_Down", "Idle_Up");
		stateMachine.addStateTransfer("Back", "Idle_Down", "Backing_Up");
		stateMachine.addStateTransfer("Stop", "Backing_Up", "Idle_Down");
		
		stateMachine.addStateTransfer("Look_Left", "Idle_Up", "Idle_Left");
		stateMachine.addStateTransfer("Look_Left", "Idle_Down", "Idle_Left");
		stateMachine.addStateTransfer("Look_Right", "Idle_Up", "Idle_Right");
		stateMachine.addStateTransfer("Look_Right", "Idle_Down", "Idle_Right");
		stateMachine.addStateTransfer("Look_Up", "Idle_Left", "Idle_Up");
		stateMachine.addStateTransfer("Look_Up", "Idle_Right", "Idle_Up");
		stateMachine.addStateTransfer("Look_Down", "Idle_Left", "Idle_Down");
		stateMachine.addStateTransfer("Look_Down", "Idle_Right", "Idle_Down");
		
		stateMachine.setDefaultState("Idle_Down");
    }

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

    public abstract void attack();
    
	protected boolean isIdle() {
		return stateMachine.getCurrentAnimationState().getTitle().equals("Idle_Up") ||
				stateMachine.getCurrentAnimationState().getTitle().equals("Idle_Down") ||
				stateMachine.getCurrentAnimationState().getTitle().equals("Idle_Left") ||
				stateMachine.getCurrentAnimationState().getTitle().equals("Idle_Right");
	}
	
	protected boolean isRunning() {
		return stateMachine.getCurrentAnimationState().getTitle().equals("Running_Up") ||
				stateMachine.getCurrentAnimationState().getTitle().equals("Running_Down") ||
				stateMachine.getCurrentAnimationState().getTitle().equals("Running_Left") ||
				stateMachine.getCurrentAnimationState().getTitle().equals("Running_Right");
	}
	
	protected boolean isBacking() {
		return stateMachine.getCurrentAnimationState().getTitle().equals("Backing_Up") ||
				stateMachine.getCurrentAnimationState().getTitle().equals("Backing_Down") ||
				stateMachine.getCurrentAnimationState().getTitle().equals("Backing_Left") ||
				stateMachine.getCurrentAnimationState().getTitle().equals("Backing_Right");
	}
	
	protected void changeStateDirection() {
		double dpL = activeWeapon.getDir().dotP(new Vector3D(-1,0,0));
		double dpR = activeWeapon.getDir().dotP(new Vector3D(1,0,0));
		double dpU = activeWeapon.getDir().dotP(new Vector3D(0, -1, 0));
		double dpD = activeWeapon.getDir().dotP(new Vector3D(0, 1, 0));
		
		double[] dps = {dpL, dpR, dpU, dpD};
		int maxIndex = 0;
		double maxDp = dpL;
		for(int i = 0; i < 4; i++) {
			if(maxDp <= dps[i]) {
				maxDp = dps[i];
				maxIndex = i;
			}
		}
		
		boolean running = isRunning();
		boolean backing = isBacking();
		double velocity_dir_dp = getVelocity().dotP(activeWeapon.getDir());

		//Left
		if(maxIndex == 0) {
			if(isRunning() || isBacking()) stateMachine.trigger("Stop");

			if(stateMachine.getCurrentAnimationState().getTitle().equals("Idle_Right")) {
				stateMachine.trigger("Look_Up");
			}
			
			if(!stateMachine.getCurrentAnimationState().getTitle().equals("Idle_Left")) 
				stateMachine.trigger("Look_Left");
			
			if(!running && !backing) return;
			
			if(velocity_dir_dp >= 0) stateMachine.trigger("Run");
			else stateMachine.trigger("Back");
			return;
		}
		
		//Right
		if(maxIndex == 1) {
			if(isRunning() || isBacking()) stateMachine.trigger("Stop");
			
			if(stateMachine.getCurrentAnimationState().getTitle().equals("Idle_Left")) {
				stateMachine.trigger("Look_Up");
			}
			if(!stateMachine.getCurrentAnimationState().getTitle().equals("Idle_Right"))
				stateMachine.trigger("Look_Right");
			
			if(!running && !backing) return;
			
			if(velocity_dir_dp >= 0) stateMachine.trigger("Run");
			else stateMachine.trigger("Back");
			return;
		}
		
		//Up
		if(maxIndex == 2) {
			if(isRunning() || isBacking()) stateMachine.trigger("Stop");
			
			if(stateMachine.getCurrentAnimationState().getTitle().equals("Idle_Down")) {
				stateMachine.trigger("Look_Left");
			}
			
			if(!stateMachine.getCurrentAnimationState().getTitle().equals("Idle_Up")) 
				stateMachine.trigger("Look_Up");
			
			if(!running && !backing) return;
			
			if(velocity_dir_dp >= 0) stateMachine.trigger("Run");
			else stateMachine.trigger("Back");
			return;
		}
		
		//Down
		if(maxIndex == 3) {
			if(isRunning() || isBacking()) stateMachine.trigger("Stop");
			
			if(stateMachine.getCurrentAnimationState().getTitle().equals("Idle_Up")) {
				stateMachine.trigger("Look_Left");
			}
			
			if(!stateMachine.getCurrentAnimationState().getTitle().equals("Idle_Down"))
				stateMachine.trigger("Look_Down");
			
			if(!running && !backing) return;
			
			if(velocity_dir_dp >= 0) stateMachine.trigger("Run");
			else stateMachine.trigger("Back");
			return;
		}
	}

}