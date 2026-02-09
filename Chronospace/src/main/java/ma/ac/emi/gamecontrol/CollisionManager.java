package ma.ac.emi.gamecontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.AttackObject;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.physics.AABB;
import ma.ac.emi.gamelogic.pickable.Pickable;
import ma.ac.emi.gamelogic.pickable.PickableManager;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.Obstacle;
import ma.ac.emi.world.WorldContext;

@Getter
@Setter
public class CollisionManager{
	private List<Ennemy> enemies;
	private AttackObjectManager attackObjectManager;
	private PickableManager pickableManager;
	private Obstacle obstacles;
	private WorldContext context;
	
	public CollisionManager(WorldContext context) {
		setContext(context);
	}
	
	public void handleCollisions(double step) {
		Player player = Player.getInstance();

		for(AttackObject attackObject : context.getAttackObjectManager().getEnemyObjects()) {
			if(player.getHitbox().intersectsZ(attackObject.getHitbox()) && attackObject.isActive()) {
				attackObject.applyEffect(player);
				
			}
		}
		
		for(AttackObject attackObject : context.getAttackObjectManager().getPlayerObjects()) {
			 for(Ennemy enemy : context.getWaveManager().getCurrentEnemies()){
				if(enemy.getHitbox().intersectsZ( attackObject.getHitbox()) && attackObject.isActive()) {
					attackObject.applyEffect(enemy);
				}
			}
		}
		for(Pickable pickable : context.getPickableManager().getPickables()) {
			if(player.getHitbox().intersectsZ(pickable.getHitbox()) && !pickable.isPickedUp()) {
				pickable.applyEffect(player);
				pickable.setPickedUp(true);
			}
		}
		
		context.getObstacles().forEach(obstacle -> {
				context.getAttackObjectManager().getProjectiles().forEach(p -> {
				if(obstacle.getHitbox() == null) return;
				if(p.getHitbox().intersectsZ(obstacle.getHitbox())) {
					p.setActive(false);
				}
			});
		});
		
		handleLivingEntityObstacles(player, context.getObstacles(), false, step);
		context.getCurrentEnemies().forEach( e -> {
			handleLivingEntityObstacles(e, context.getObstacles(), true, step);
		});
		
	}
	
	private void handleLivingEntityObstacles(LivingEntity entity, List<Obstacle> obstacles, boolean broad, double step) { 
		List<Obstacle> collidingObs = new ArrayList<>();
		
		Vector3D velocity = entity.getVelocity();
        if (velocity == null) return;

        Vector3D movement = velocity.mult(step);
        if (movement.norm() == 0) return;
        
		collidingObs = new ArrayList<>(); 
		for(Obstacle obstacle : obstacles) {
			if(obstacle.getHitbox() == null) continue;
			if(entity.getBound().intersectsZ(obstacle.getHitbox()) || !broad) { 
				collidingObs.add(obstacle); 
			} 
		} 
		
		Collections.sort(collidingObs, (o1, o2) -> { 
			Vector3D dv1 = o1.getHitbox().center.sub(entity.getBound().center.sub(movement));
			Vector3D dv2 = o2.getHitbox().center.sub(entity.getBound().center.sub(movement));
	
			double d1 = dv1.getX() * dv1.getX() + dv1.getY() * dv1.getY();
			double d2 = dv2.getX() * dv2.getX() + dv2.getY() * dv2.getY();
			return Double.compare(d1, d2); 
		}); 
		
		for(Obstacle obstacle : collidingObs) { 
			resolveCollision(entity, obstacle, step); 
		}
	}
	
	private void resolveCollision(LivingEntity entity, Obstacle obstacle, double step) {
		Vector3D start = entity.getBound().center.sub(entity.getVelocity().mult(step));
		Vector3D end   = entity.getBound().center;

		AABB expanded = new AABB(obstacle.hitbox.center, obstacle.hitbox.half.add(entity.getBound().half));
		
		RayRectCollisionResponse response = rayRectIntersection(start, end, expanded);

		Vector3D velocity = entity.getVelocity();
        if (velocity == null) return;

        Vector3D movement = velocity.mult(step);
        if (movement.norm() == 0) return;
        

		if(response.inCollision && response.t <= 1) {
			if(response.contactNormal != null) {

				Vector3D moveToImpact = response.contactNormal.mult(movement.mult(1- response.t).dotP(response.contactNormal));
		        entity.setPos(entity.getPos().sub(moveToImpact).add(response.contactNormal.mult(0.2)));
			}
			
		}
		
		entity.hitbox.center = new Vector3D (entity.getPos().getX(), entity.getPos().getY()+GamePanel.TILE_SIZE/2);
		entity.getBound().center = new Vector3D (entity.getPos().getX(), entity.getPos().getY()+GamePanel.TILE_SIZE/2);
		
	
	}

	
	public static RayRectCollisionResponse rayRectIntersection(Vector3D start, Vector3D end, AABB rect) {
	    double minX = rect.center.getX() - rect.half.getX();
	    double maxX = rect.center.getX() + rect.half.getX();
	    double minY = rect.center.getY() - rect.half.getY();
	    double maxY = rect.center.getY() + rect.half.getY();
	    Vector3D dir = end.sub(start);
	    double tNear = Double.NEGATIVE_INFINITY;
	    double tFar  = Double.POSITIVE_INFINITY;
	    Vector3D normalNear = null;
	    
	    if (dir.getX() != 0) {
	        double tx1 = (minX - start.getX()) / dir.getX();
	        double tx2 = (maxX - start.getX()) / dir.getX();
	        double t1 = Math.min(tx1, tx2);
	        double t2 = Math.max(tx1, tx2);
	        Vector3D n1 = tx1 < tx2 ? new Vector3D(-1, 0) : new Vector3D(1, 0);
	        if (t1 >= tNear) {
	            tNear = t1;
	            normalNear = n1;
	        }
	        tFar = Math.min(tFar, t2);
	    } else {
	        if (start.getX() < minX || start.getX() > maxX)
	            return new RayRectCollisionResponse(null, false, null, null);
	    }
	    
	    if (dir.getY() != 0) {
	        double ty1 = (minY - start.getY()) / dir.getY();
	        double ty2 = (maxY - start.getY()) / dir.getY();
	        double t1 = Math.min(ty1, ty2);
	        double t2 = Math.max(ty1, ty2);
	        Vector3D n1 = ty1 < ty2 ? new Vector3D(0, -1) : new Vector3D(0, 1);
	        if (t1 >= tNear) {
	            tNear = t1;
	            normalNear = n1;
	        }
	        tFar = Math.min(tFar, t2);
	    } else {
	        if (start.getY() < minY || start.getY() > maxY)
	            return new RayRectCollisionResponse(null, false, null, null);
	    }
	    
	    if (tNear >= tFar) return new RayRectCollisionResponse(null, false, null, null);
	    if (tFar <= 0)     return new RayRectCollisionResponse(null, false, null, null);
	    
	    // Handle case where ray starts inside the box
	    if (tNear < 0) {
	        tNear = 0;
	        normalNear = null; // or you could set a special flag/value
	    }
	    
	    Vector3D contactPoint = start.add(dir.mult(tNear));
	    return new RayRectCollisionResponse(tNear, true, contactPoint, normalNear);
	}


	
	public static class RayRectCollisionResponse{
		public final Double t;
		public final boolean inCollision;
		public final Vector3D contactPoint;
		public final Vector3D contactNormal;
		
		RayRectCollisionResponse(Double t, boolean inCollision, Vector3D contactPoint, Vector3D contactNormal){
			this.t = t;
			this.inCollision = inCollision;
			this.contactPoint = contactPoint;
			this.contactNormal = contactNormal;
		}
	}

}
