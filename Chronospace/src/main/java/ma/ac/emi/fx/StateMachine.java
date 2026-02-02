package ma.ac.emi.fx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.Getter;

@Getter
public class StateMachine {
	private static class StateTrigger {
		private final String trigger;
		private final String state;
		
		public StateTrigger(String trigger, String state) {
			this.trigger = trigger;
			this.state = state;
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof StateTrigger)) return false;
			StateTrigger that = (StateTrigger) o;
			return trigger.equals(that.trigger) && state.equals(that.state);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(trigger, state);
		}
	}
	
	private final Map<StateTrigger, String> stateTransfers;
	private final Map<String, AnimationState> statesByTitle;
	private AnimationState currentAnimationState;
	private String defaultStateTitle;
	
	public StateMachine() {
		this.stateTransfers = new HashMap<>();
		this.statesByTitle = new HashMap<>();
		this.currentAnimationState = null;
		this.defaultStateTitle = "";
	}
	
	public void addAnimationState(AnimationState state) {
		statesByTitle.put(state.getTitle(), state);
	}
	
	public void addStateTransfer(String trigger, String fromState, String toState) {
		stateTransfers.put(new StateTrigger(trigger, fromState), toState);
	}
	
	public void setDefaultState(String defaultState) {
		AnimationState state = statesByTitle.get(defaultState);
		if (state != null) {
			this.defaultStateTitle = defaultState;
			if (currentAnimationState == null) {
				currentAnimationState = state;
			}
		} else {
			System.err.println("Unable to find animation state: " + defaultState);
		}
	}
	
	public void trigger(String trigger) {
		if (currentAnimationState == null) {
			System.err.println("No current state set. Cannot trigger: " + trigger);
			return;
		}
		
		StateTrigger stateTrigger = new StateTrigger(trigger, currentAnimationState.getTitle());
		String nextStateTitle = stateTransfers.get(stateTrigger);
		
		if (nextStateTitle != null) {
			AnimationState nextState = statesByTitle.get(nextStateTitle);
			if (nextState != null) {
				currentAnimationState = nextState;
			} else {
				System.err.println("Target state not found: " + nextStateTitle);
			}
		} else {
			System.err.println("No transition found for trigger '" + trigger + 
			                   "' from state '" + currentAnimationState.getTitle() + "'");
		}
	}
	
	public void update(double step) {
		if (currentAnimationState != null) {
			currentAnimationState.update(step);
		}
	}
	
	public AnimationState getAnimationStateByTitle(String title) {
		return statesByTitle.get(title);
	}
	

	public List<AnimationState> getAnimationStates() {
		return new ArrayList<>(statesByTitle.values());
	}
	

	public boolean hasState(String title) {
		return statesByTitle.containsKey(title);
	}
	
	public boolean hasTransition(String trigger, String fromState) {
		return stateTransfers.containsKey(new StateTrigger(trigger, fromState));
	}

	public void clearAllStates() {
		for(AnimationState state : getAnimationStates()) {
			state.clear();
		}
	}
	
	public StateMachine getSkeleton() {
		StateMachine skeleton = new StateMachine();
		
		for(AnimationState state: statesByTitle.values()) {
			AnimationState stateSkeleton = new AnimationState(state.getTitle());
			stateSkeleton.setDoesLoop(state.isDoesLoop());
			
			skeleton.addAnimationState(stateSkeleton);
		}
		
		for(StateTrigger trigger: stateTransfers.keySet()) {
			skeleton.addStateTransfer(trigger.trigger, trigger.state, stateTransfers.get(trigger));
		}
		
		skeleton.setDefaultState(defaultStateTitle);
		
		return skeleton;
				
	}

	public void setCurrentAnimationState(String title) {
		this.currentAnimationState = getAnimationStateByTitle(title);
	}
}
