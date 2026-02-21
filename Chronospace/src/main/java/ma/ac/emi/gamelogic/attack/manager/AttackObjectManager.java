package ma.ac.emi.gamelogic.attack.manager;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.ObjectPool;
import ma.ac.emi.gamelogic.attack.AOE;
import ma.ac.emi.gamelogic.attack.AttackObject;
import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.world.World;
import ma.ac.emi.world.WorldContext;

@Getter
@Setter
public class AttackObjectManager {
	private List<AttackObject> activeObjects;
	private WorldContext context;
	
	Map<Class<? extends AttackObject>, ObjectPool<?>> pools;
    
    public AttackObjectManager(WorldContext context) {
    	this.context = context;
    	activeObjects = new ArrayList<>();
    	
    	pools = new HashMap<>();
    	
    	ObjectPool<Projectile> projectilePool = new ObjectPool<Projectile>(
                () -> new Projectile(),
                100
            );
    	ObjectPool<AOE> aoePool = new ObjectPool<>(
    			() -> new AOE(),
    			100
    		);
    	
    	pools.put(Projectile.class, projectilePool);
    	pools.put(AOE.class, aoePool);
    }

    public void addObject(AttackObject attackObject) {
    	activeObjects.add(attackObject);
    
    }

    public void update(double step) {
        for (AttackObject attackObject : activeObjects) {
        	attackObject.update(step);
        	if(attackObject.isOutOfWorld(context)) {
            	attackObject.desactivate();
            }
        }
        List<AttackObject> copy = new ArrayList<>(getActiveObjects());
        copy.forEach((object) -> {
        	if(!(object.isActive())) {
        		object.onDesactivate();
        		GameController.getInstance().removeDrawable(object);
            	if(object.getShadow() != null) {
            		GameController.getInstance().removeDrawable(object.getShadow());
            	}

        		
        		@SuppressWarnings("unchecked")
				ObjectPool<AttackObject> pool =
        		        (ObjectPool<AttackObject>) pools.get(object.getClass());

    		    if (pool != null) {
    		        pool.free(object);
    		    }
        	}
        });
        
        activeObjects.removeIf(attackObject -> !attackObject.isActive());
       
    }
    
    public List<AttackObject> getEnemyObjects(){
		return getActiveObjects().stream().filter((o) -> !o.isFromPlayer()).toList();

    }
    
    public List<AttackObject> getPlayerObjects(){
		return getActiveObjects().stream().filter((o) -> o.isFromPlayer()).toList();

    }
    
    public List<Projectile> getProjectiles(){
    	return getActiveObjects().stream().filter(o -> o instanceof Projectile).map(o -> (Projectile) o).toList();
    }

	public void clearObjects() {
		this.activeObjects.forEach(o -> o.setActive(false));
	}

	
	@SuppressWarnings("unchecked")
	public <T extends AttackObject> ObjectPool<T> getPool(Class<T> type) {
	    return (ObjectPool<T>) pools.get(type);
	}


}
