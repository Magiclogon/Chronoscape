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
import ma.ac.emi.gamelogic.entity.LivingEntity;
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
			if(intersects(player.getHitbox(),attackObject.getHitbox()) && attackObject.isActive()) {
				attackObject.applyEffect(player);
				
			}
		}
		
		for(AttackObject attackObject : context.getAttackObjectManager().getPlayerObjects()) {
			 for(Ennemy enemy : context.getWaveManager().getCurrentEnemies()){
				if(intersects(enemy.getHitbox(), attackObject.getHitbox()) && attackObject.isActive()) {
					attackObject.applyEffect(enemy);
				}
			}
		}
		for(Pickable pickable : context.getPickableManager().getPickables()) {
			if(intersects(player.getHitbox(), pickable.getHitbox()) && !pickable.isPickedUp()) {
				pickable.applyEffect(player);
				pickable.setPickedUp(true);
			}
		}
		
		
		context.getObstacles().forEach(obstacle -> {
				context.getAttackObjectManager().getProjectiles().forEach(p -> {
			
				if(intersects(p.getHitbox(), obstacle.getHitbox())) {
					p.setActive(false);
				}
			});
		});
		
		handleLivingEntityObstaclesCollisions(player, context.getObstacles(), step);
		context.getCurrentEnemies().forEach( e -> {
			handleLivingEntityObstaclesCollisions(e, context.getObstacles(), step);
		});
		
	}
	
	
	private void handleLivingEntityObstaclesCollisions(LivingEntity entity, List<Obstacle> obstacles, double step) {
		List<Obstacle> collidingObs = new ArrayList<>();
		for(Obstacle obstacle : obstacles) {
			if(intersects(entity.getBound(), obstacle.getHitbox())) {
				collidingObs.add(obstacle);
			}
		}
		
		Collections.sort(collidingObs, (o1, o2) -> {
		    double dx1 = o1.getHitbox().x - entity.getBound().x;
		    double dy1 = o1.getHitbox().y - entity.getBound().y;
		    
		    double dx2 = o2.getHitbox().x - entity.getBound().x;
		    double dy2 = o2.getHitbox().y - entity.getBound().y;

		    double d1 = dx1 * dx1 + dy1 * dy1;
		    double d2 = dx2 * dx2 + dy2 * dy2;

		    return Double.compare(d1, d2);
		});

		for(Obstacle obstacle : collidingObs) {
			resolveCollision(entity, obstacle, step);
		}
	}
	
	private void resolveCollision(LivingEntity entity, Obstacle obstacle, double step) {
		Vector3D start = (new Vector3D(entity.getBound().x, entity.getBound().y)).sub(entity.getVelocity().mult(step));
		Vector3D end   = (new Vector3D(entity.getBound().x, entity.getBound().y));

		Rectangle expanded = new Rectangle(
			    obstacle.getHitbox().x,
			    obstacle.getHitbox().y,
			    obstacle.getHitbox().width + entity.getBound().width,
			    obstacle.getHitbox().height + entity.getBound().height
			);
		
		RayRectCollisionResponse response = rayRectIntersection(start, end, expanded);
		
		if(response.inCollision && response.t <= 1) {
			if(response.contactNormal != null) 
				entity.setPos(entity.getPos().sub(
					response.contactNormal.mult(entity.getVelocity().dotP(response.contactNormal)*(1-response.t)).mult(step)
					));
		}
		
		entity.hitbox.x = (int) (entity.getPos().getX());
		entity.hitbox.y = (int) (entity.getPos().getY()-entity.hitbox.height/2+GamePanel.TILE_SIZE/2);
		
		entity.getBound().x = (int) (entity.getPos().getX());
		entity.getBound().y = (int) (entity.getPos().getY()-entity.getBound().height/2+GamePanel.TILE_SIZE/2);
	}
	
	
	private boolean intersects(Rectangle bound1, Rectangle bound2) {
		Rectangle shifted_bound1 = new Rectangle(
				bound1.x-bound1.width/2,
				bound1.y-bound1.height/2,
				bound1.width,
				bound1.height
			);
		Rectangle shifted_bound2 = new Rectangle(
				bound2.x-bound2.width/2,
				bound2.y-bound2.height/2,
				bound2.width,
				bound2.height
			);
		
		return shifted_bound1.intersects(shifted_bound2);
	}
	
	public static RayRectCollisionResponse rayRectIntersection(Vector3D start, Vector3D end, Rectangle rect) {

	    double halfW = rect.getWidth() / 2.0;
	    double halfH = rect.getHeight() / 2.0;

	    double minX = rect.getX() - halfW;
	    double maxX = rect.getX() + halfW;

	    double minY = rect.getY() - halfH;
	    double maxY = rect.getY() + halfH;

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

	        if (t1 > tNear) {
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

	        if (t1 > tNear) {
	            tNear = t1;
	            normalNear = n1;
	        }
	        tFar = Math.min(tFar, t2);
	    } else {
	        if (start.getY() < minY || start.getY() > maxY)
	            return new RayRectCollisionResponse(null, false, null, null);
	    }

	    if (tNear > tFar) return new RayRectCollisionResponse(null, false, null, null);
	    if (tFar < 0)     return new RayRectCollisionResponse(null, false, null, null);

	    Vector3D contactPoint = start.add(dir.mult(tNear));

	    return new RayRectCollisionResponse(tNear, true, contactPoint, normalNear);
	}


	
	private static class RayRectCollisionResponse{
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
