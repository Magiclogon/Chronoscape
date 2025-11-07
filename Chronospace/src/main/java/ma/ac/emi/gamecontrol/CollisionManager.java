package ma.ac.emi.gamecontrol;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.AttackObject;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.pickable.Pickable;
import ma.ac.emi.gamelogic.pickable.PickableManager;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.world.World;
import ma.ac.emi.world.WorldContext;

@Getter
@Setter
public class CollisionManager {
	private Player player;
	private List<Ennemy> enemies;
	private AttackObjectManager attackObjectManager;
	private PickableManager pickableManager;
	private WorldContext context;
	
	public CollisionManager(WorldContext context) {
		setContext(context);
	}
	
	public void handleCollisions() {
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
		
		player.clamp(context.getWorldBounds());
	}
}
