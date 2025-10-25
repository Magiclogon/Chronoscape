package ma.ac.emi.gamecontrol;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.projectile.Projectile;
import ma.ac.emi.gamelogic.projectile.ProjectileManager;

@Getter
@Setter
public class CollisionManager {
	private Player player;
	private List<Ennemy> enemies;
	private ProjectileManager projectileManager;
	
	public void handleCollisions() {
		for(Projectile projectile : projectileManager.getEnemyProjectiles()) {
			if(player.getBound().intersects(projectile.getBound())) {
				//Apply damage
				
				//Desactivate projectile
				projectile.setActive(false);
			}
		}
		int i = 0;
		for(Projectile projectile : projectileManager.getPlayerProjectiles()) {
			System.out.println("player projectile" + i);
			 for(Ennemy enemy : enemies){
				if(enemy.getBound().intersects(projectile.getBound())) {
					System.out.println("enemey hit");
					//Apply damage
					enemy.applyDamage(5);
					//Desactivate projectile
					projectile.setActive(false);
				}
			}
			i++;
		}
		System.out.println(projectileManager.getPlayerProjectiles().size());
	}
}
