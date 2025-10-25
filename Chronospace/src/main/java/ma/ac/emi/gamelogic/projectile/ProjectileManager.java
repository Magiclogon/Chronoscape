package ma.ac.emi.gamelogic.projectile;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.world.World;

@Getter
@Setter
public class ProjectileManager {
    private List<Projectile> projectiles;
    
    public ProjectileManager() {
    	projectiles = new ArrayList<>();

    }

    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    
    }

    public void update(double deltaTime, World world) {
        for (Projectile p : projectiles) {
            p.update(deltaTime, world);
        }

        // Remove inactive ones
        projectiles.removeIf(p -> !p.isActive());
    }

    public void draw(Graphics g) {
        for (Projectile p : projectiles) {
            p.draw(g);
        }
    }
    
    public List<Projectile> getEnemyProjectiles(){
		return getProjectiles().stream().filter((p) -> !p.isFromPlayer()).toList();

    }
    
    public List<Projectile> getPlayerProjectiles(){
		return getProjectiles().stream().filter((p) -> p.isFromPlayer()).toList();

    }
}

