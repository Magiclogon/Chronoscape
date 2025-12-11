package ma.ac.emi.gamelogic.attack.manager;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.AttackObject;
import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.world.World;
import ma.ac.emi.world.WorldContext;

@Getter
@Setter
public class AttackObjectManager {
	private List<AttackObject> attackObjects;
	private WorldContext context;
    
    public AttackObjectManager(WorldContext context) {
    	this.context = context;
    	attackObjects = new ArrayList<>();
    }

    public void addObject(AttackObject attackObject) {
    	attackObjects.add(attackObject);
    
    }

    public void update(double step) {
        for (AttackObject attackObject : attackObjects) {
        	attackObject.update(step);
        	if(attackObject.isOutOfWorld(context)) {
            	attackObject.setActive(false);
            }
        }
        List<AttackObject> copy = new ArrayList<>(getAttackObjects());
        copy.forEach((object) -> {
        	if(!(object.isActive())) {
        		object.onDesactivate();
        	}
        });
        
        attackObjects.removeIf(attackObject -> !attackObject.isActive());
       
    }
    
    public List<AttackObject> getEnemyObjects(){
		return getAttackObjects().stream().filter((o) -> !o.isFromPlayer()).toList();

    }
    
    public List<AttackObject> getPlayerObjects(){
		return getAttackObjects().stream().filter((o) -> o.isFromPlayer()).toList();

    }
    
    public List<Projectile> getProjectiles(){
    	return getAttackObjects().stream().filter(o -> o instanceof Projectile).map(o -> (Projectile) o).toList();
    }
}
