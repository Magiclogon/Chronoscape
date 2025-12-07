package ma.ac.emi.gamecontrol;

import java.awt.Rectangle;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.AttackObject;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.pickable.Pickable;
import ma.ac.emi.gamelogic.pickable.PickableManager;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.Obstacle;
import ma.ac.emi.world.World;
import ma.ac.emi.world.WorldContext;

@Getter
@Setter
public class CollisionManager {
	private Player player;
	private List<Ennemy> enemies;
	private AttackObjectManager attackObjectManager;
	private PickableManager pickableManager;
	private Obstacle obstacles;
	private WorldContext context;
	
	public CollisionManager(WorldContext context) {
		setContext(context);
	}
	
	public void handleCollisions(double step) {
		player = Player.getInstance();

		for(AttackObject attackObject : context.getAttackObjectManager().getEnemyObjects()) {
			if(player.getBound().intersects(attackObject.getBound()) && attackObject.isActive()) {
				attackObject.applyEffect(player);
				
			}
		}
		
		for(AttackObject attackObject : context.getAttackObjectManager().getPlayerObjects()) {
			 for(Ennemy enemy : context.getWaveManager().getCurrentEnemies()){
				if(enemy.getBound().intersects(attackObject.getBound()) && attackObject.isActive()) {
					attackObject.applyEffect(enemy);
				}
			}
		}
		for(Pickable pickable : context.getPickableManager().getPickables()) {
			if(player.getBound().intersects(pickable.getBound()) && !pickable.isPickedUp()) {
				pickable.applyEffect(player);
				pickable.setPickedUp(true);
			}
		}
		
		for(Obstacle obstacle : context.getObstacles()) {
			if(player.getBound().intersects(obstacle.getBound())) {
				Rectangle p_bound = player.getBound();
			    Rectangle o_bound = obstacle.getBound();
			    
			    int overlapRight = (p_bound.x + p_bound.width) - o_bound.x;
			    int overlapLeft = (o_bound.x + o_bound.width) - p_bound.x;
			    int overlapBottom = (p_bound.y + p_bound.height) - o_bound.y;
			    int overlapTop = (o_bound.y + o_bound.height) - p_bound.y;
			    
			    int minOverlap = Math.min(Math.min(overlapRight, overlapLeft), 
			                              Math.min(overlapBottom, overlapTop));
			    
			    System.out.println("obstacles position: " + o_bound.x + ", " + o_bound.y);
			    System.out.println("overlaps: " + overlapRight + ", " + overlapLeft + ", " +
			    				overlapBottom + ", " + overlapTop);
			    
			    if(minOverlap == overlapRight) {
			        player.setPos(new Vector3D(o_bound.x - p_bound.width, player.getPos().getY()));
			    } else if(minOverlap == overlapLeft) {
			        player.setPos(new Vector3D(o_bound.x + o_bound.width, player.getPos().getY()));
			    } else if(minOverlap == overlapBottom) {
			        player.setPos(new Vector3D(player.getPos().getX(), o_bound.y - p_bound.height));
			    } else {
			        player.setPos(new Vector3D(player.getPos().getX(), o_bound.y + o_bound.height));
			    }
			    
			    player.bound.x = (int) player.getPos().getX();
			    player.bound.y = (int) player.getPos().getY();
			}
		}
		
		player.clamp(context.getWorldBounds());
	}
}
