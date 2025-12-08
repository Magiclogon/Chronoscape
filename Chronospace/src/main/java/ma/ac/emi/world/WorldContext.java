package ma.ac.emi.world;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.ai.PathFinder;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.gamelogic.pickable.PickableManager;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.wave.WaveManager;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * WorldContext holds all shared data and managers for a world instance.
 * This allows clean dependency injection and makes components testable.
 */
@Getter
@Setter
public class WorldContext {
    // World properties
    private final int worldWidth;
    private final int worldHeight;
    private final Rectangle worldBounds;
    
    // Core entities
    private Player player;
    private final List<Ennemy> enemies;
    
    // Managers
    private AttackObjectManager attackObjectManager;
    private PickableManager pickableManager;
    private WaveManager waveManager;
    private PathFinder pathFinder;
    
    // Game configuration
    private EnnemySpecieFactory specieFactory;
    
    // World data
    private final List<Obstacle> obstacles;
    
    public WorldContext(int width, int height, EnnemySpecieFactory specieFactory) {
        this.worldWidth = width;
        this.worldHeight = height;
        this.worldBounds = new Rectangle(width * ma.ac.emi.gamecontrol.GamePanel.TILE_SIZE, 
                                          height * ma.ac.emi.gamecontrol.GamePanel.TILE_SIZE);
        this.specieFactory = specieFactory;
        
        // Initialize collections
        this.enemies = new ArrayList<>();
        this.obstacles = new ArrayList<>();
    }
    
    /**
     * Add an obstacle to the world
     */
    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }
    
    /**
     * Check if a grid position contains an obstacle
     */
    public boolean isObstacle(int gridX, int gridY) {
        if (gridX < 0 || gridY < 0 || gridX >= worldWidth || gridY >= worldHeight) {
            return true;
        }
        
        Rectangle checkRect = new Rectangle(
            gridX * ma.ac.emi.gamecontrol.GamePanel.TILE_SIZE,
            gridY * ma.ac.emi.gamecontrol.GamePanel.TILE_SIZE,
            ma.ac.emi.gamecontrol.GamePanel.TILE_SIZE,
            ma.ac.emi.gamecontrol.GamePanel.TILE_SIZE
        );
        
        for (Obstacle obstacle : obstacles) {
            if (checkRect.intersects(obstacle.getBound())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get current active enemies (from wave manager)
     */
    public List<Ennemy> getCurrentEnemies() {
        return waveManager != null ? waveManager.getCurrentEnemies() : enemies;
    }
}