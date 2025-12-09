package ma.ac.emi.gamecontrol;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
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
		
		List<Obstacle> collidingObs = new ArrayList<>();
		for(Obstacle obstacle : context.getObstacles()) {
			Rectangle shifted_player = new Rectangle(
					player.getBound().x-player.getBound().width/2,
					player.getBound().y-player.getBound().height/2,
					player.getBound().width,
					player.getBound().height
				);
			Rectangle shifted_obs = new Rectangle(
					obstacle.getBound().x-obstacle.getBound().width/2,
					obstacle.getBound().y-obstacle.getBound().height/2,
					obstacle.getBound().width,
					obstacle.getBound().height
				);
			if(shifted_player.intersects(shifted_obs)) {
				collidingObs.add(obstacle);
					
			}

		}
		
		
		Collections.sort(collidingObs, (o1, o2) -> {
		    double dx1 = (o1.hitbox.x + o1.hitbox.width * 0.5) - (player.hitbox.x + player.hitbox.width * 0.5);
		    double dy1 = (o1.hitbox.y + o1.hitbox.height * 0.5) - (player.hitbox.y + player.hitbox.height * 0.5);

		    double dx2 = (o2.hitbox.x + o2.hitbox.width * 0.5) - (player.hitbox.x + player.hitbox.width * 0.5);
		    double dy2 = (o2.hitbox.y + o2.hitbox.height * 0.5) - (player.hitbox.y + player.hitbox.height * 0.5);

		    double d1 = dx1 * dx1 + dy1 * dy1;
		    double d2 = dx2 * dx2 + dy2 * dy2;

		    return Double.compare(d1, d2);
		});

		
		for(Obstacle obstacle : collidingObs) {
			
			Vector3D start = (new Vector3D(player.getBound().x, player.getBound().y)).sub(player.getVelocity().mult(step));
			Vector3D end   = (new Vector3D(player.getBound().x, player.getBound().y));

			Rectangle expanded = new Rectangle(
				    obstacle.getBound().x,
				    obstacle.getBound().y,
				    obstacle.getBound().width + player.getBound().width,
				    obstacle.getBound().height + player.getBound().height
				);
			
			Vector3D.RayRectCollisionResponse response = Vector3D.rayRectIntersection(start, end, expanded);
			
			if(response.inCollision && response.t <= 1) {
				player.setPos(player.getPos().sub(
						response.contactNormal.mult(player.getVelocity().dotP(response.contactNormal)*(1-response.t)).mult(step)
						));
				player.hitbox.x = (int) (player.getPos().getX());
				player.hitbox.y = (int) (player.getPos().getY()-player.hitbox.height/2+GamePanel.TILE_SIZE/2);
				
				player.bound.x = (int) (player.getPos().getX());
				player.bound.y = (int) (player.getPos().getY()-player.bound.height/2+GamePanel.TILE_SIZE/2);
			}
		}
		
	}
	

}
