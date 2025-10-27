package ma.ac.emi.gamelogic.attack.manager;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.AttackObject;
import ma.ac.emi.world.World;

@Getter
@Setter
public class AttackObjectManager {
	private List<AttackObject> attackObjects;
	private World world;
    
    public AttackObjectManager(World world) {
    	this.world = world;
    	attackObjects = new ArrayList<>();
    }

    public void addObject(AttackObject attackObject) {
    	attackObjects.add(attackObject);
    
    }

    public void update(double step) {
        for (AttackObject attackObject : attackObjects) {
        	attackObject.update(step);
        	if(attackObject.isOutOfWorld(world)) {
            	attackObject.setActive(false);
            }
        }
        
        List<AttackObject> copy = new ArrayList<>(getAttackObjects());
        copy.forEach((object) -> {
        	if(!(object.isActive())) {
        		object.onDesactivate();
        	}
        });
        
        // Remove inactive ones
        attackObjects.removeIf(attackObject -> !attackObject.isActive());
       
    }

    public void draw(Graphics g) {
        for (AttackObject attackObject : attackObjects) {
        	attackObject.draw(g);
        }
    }
    
    public List<AttackObject> getEnemyObjects(){
		return getAttackObjects().stream().filter((o) -> !o.isFromPlayer()).toList();

    }
    
    public List<AttackObject> getPlayerObjects(){
		return getAttackObjects().stream().filter((o) -> o.isFromPlayer()).toList();

    }
    
}
