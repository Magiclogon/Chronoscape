package ma.ac.emi.gamelogic.projectile;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import ma.ac.emi.world.World;

public class ProjectileManager {
    private List<Projectile> projectiles = new ArrayList<>();

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
}

