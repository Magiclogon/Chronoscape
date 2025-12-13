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
import ma.ac.emi.tiles.TileManager;
import ma.ac.emi.tiles.TileType;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WorldContext {
    // World properties
    private final int worldWidth;
    private final int worldHeight;
    private final Rectangle worldBounds;
    
    // Core entities
    private Player player;
    
    // Managers
    private AttackObjectManager attackObjectManager;
    private PickableManager pickableManager;
    private WaveManager waveManager;
    private PathFinder pathFinder;
    
    // Game configuration
    private EnnemySpecieFactory specieFactory;
    
    // World data
    private final TileManager tileManager;
    private final List<Obstacle> obstacles;
    
    private final Color voidColor;
    
    public WorldContext(int width, int height, EnnemySpecieFactory specieFactory, TileManager tileManager) {
        this.worldWidth = width;
        this.worldHeight = height;
        this.worldBounds = new Rectangle(width * ma.ac.emi.gamecontrol.GamePanel.TILE_SIZE, 
                                          height * ma.ac.emi.gamecontrol.GamePanel.TILE_SIZE);
        this.specieFactory = specieFactory;
        
        // Initialize collections
        this.obstacles = new ArrayList<>();
        this.tileManager = tileManager;
        
        this.voidColor = new Color(tileManager.getTileSprites().get(TileType.VOID).getSprite().getRGB(0, 0), true);
    }
    
    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }
    
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
            if (checkRect.intersects(obstacle.getHitbox())) {
                return true;
            }
        }
        
        return false;
    }
    
    public List<Ennemy> getCurrentEnemies() {
        return waveManager != null ? waveManager.getCurrentEnemies() : new ArrayList<>();
    }
    
    public void refreshCurrentMap() {
    	this.tileManager.refreshCurrentMap();
    }
}