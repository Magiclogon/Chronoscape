package ma.ac.emi.gamelogic.entity;

import java.awt.*;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.StateMachine;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GameObject;
import ma.ac.emi.math.Vector3D;

@Setter
@Getter
public abstract class Entity extends GameObject{
	protected Vector3D velocity;
	protected StateMachine stateMachine;
	
	protected Rectangle bound;
	public Entity(boolean drawn) {
		super(drawn);
		stateMachine = new StateMachine();
		initStateMachine();
		setupAnimations();
	}
	
	public abstract void initStateMachine();
	public abstract void setupAnimations();
	
}
