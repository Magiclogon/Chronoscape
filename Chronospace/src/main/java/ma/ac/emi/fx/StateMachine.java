package ma.ac.emi.fx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;

@Getter
public class StateMachine {
	private class StateTrigger{
		private String trigger;
		private String state;
		
		public StateTrigger() {}
		
		public StateTrigger(String trigger, String state) {
			this.trigger = trigger;
			this.state = state;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof StateTrigger stateTrigger) {
				return trigger.equals(stateTrigger.trigger) && state.equals(stateTrigger.state);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(trigger, state);
		}
	}
	
	private Map<StateTrigger, String> stateTransfers;
	private List<AnimationState> animationStates;
	private AnimationState currentAnimationState;
	private String defaultState;
	
	public StateMachine() {
		stateTransfers = new HashMap<>();
		animationStates = new ArrayList<>();
		currentAnimationState = null;
		defaultState = "";
	}
	
	public void addAnimationState(AnimationState state) {
		this.animationStates.add(state);
	}
	
	public void addStateTransfer(String trigger, String fromState, String toState) {
		this.stateTransfers.put(new StateTrigger(trigger, fromState), toState);
	}
	
	public void setDefaultState(String defaultState) {
		for(AnimationState state : animationStates) {
			if(state.getTitle().equals(defaultState)) {
				defaultState = state.getTitle();
				if(currentAnimationState == null) currentAnimationState = state;
				return;
			}
		}
		System.out.println("Unable to find the animation state.");
	}
	
	public void trigger(String trigger) {
		for(StateTrigger stateTrigger : stateTransfers.keySet()) {
			if(stateTrigger.equals(new StateTrigger(trigger, currentAnimationState.getTitle()))) {
				String nextStateTitle = stateTransfers.get(stateTrigger);
				if(nextStateTitle != null) {
					for(int i = 0; i < animationStates.size(); i++) {
						if(animationStates.get(i).getTitle().equals(nextStateTitle)) {
							currentAnimationState = animationStates.get(i);
							break;
						}
					}
				}
				return;
			}
		}
		
		System.out.println("Unable to find the trigger '" + trigger + "'.");
	}
	
	public void update(double step) {
		if(currentAnimationState != null) {
			currentAnimationState.update(step);
		}
	}
	
	public AnimationState getAnimationStateByTitle(String title) {
		for(AnimationState state : animationStates) {
			if(state.getTitle().equals(title)) {
				return state;
			}
		}
		return null;
	}
}
