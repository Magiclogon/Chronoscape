package ma.ac.emi.gamelogic.wave;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.ObjectPool;
import ma.ac.emi.gamelogic.attack.AttackObject;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.Obstacle;

import java.util.*;

@Getter
@Setter
public class Wave extends WaveNotifier {
    private int number;

    // for logic
    private int enemiesNumber;
    // from config
    private int baseEnemiesNumber;

    private List<Ennemy> enemies;
    private EnnemySpecieFactory specieFactory;
    private Map<String, Integer> enemyComposition;
    private boolean isBossWave;
    private double spawnDelay;
    private double spawnTimer;
    private int enemiesSpawned;
    private WaveSpawnState spawnState;
    private Random random;
    private List<Vector3D> spawnPoints;
    private int worldWidth;
    private int worldHeight;
    private double enemyNumberMultiplier;

    private AttackObjectManager attackObjectManager;
    private WaveManager waveManager;

    public Wave(int number, int baseEnemiesNumber, EnnemySpecieFactory specieFactory, int worldWidth, int worldHeight, WaveManager waveManager) {
        this.number = number;
        this.baseEnemiesNumber = baseEnemiesNumber;
        this.enemiesNumber = baseEnemiesNumber;
        this.specieFactory = specieFactory;
        this.enemies = new ArrayList<>();
        this.isBossWave = false;
        this.spawnDelay = 0.5;
        this.spawnTimer = 0;
        this.enemiesSpawned = 0;
        this.spawnState = WaveSpawnState.NOT_STARTED;
        this.random = new Random();
        spawnPoints = new ArrayList<>();
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        this.enemyNumberMultiplier = 1;
        
        this.waveManager = waveManager;

    }

    // called by WaveManager to apply difficulty
    public void applyDifficulty(DifficultyStrategy strategy) {
        if (strategy == null) return;

        this.enemyNumberMultiplier = strategy.getEnemyCountMultiplier();
        this.enemiesNumber = (int) (this.baseEnemiesNumber * this.enemyNumberMultiplier);

        if (this.baseEnemiesNumber > 0 && this.enemiesNumber == 0) {
            this.enemiesNumber = 1;
        }
    }

    public void spawn() {
        spawnState = WaveSpawnState.SPAWNING;
        spawnTimer = 0;
        enemiesSpawned = 0;
    }

    public void update(double deltaTime, Vector3D playerPos) {
        if (spawnState == WaveSpawnState.SPAWNING) {
            spawnTimer += deltaTime;

            while (spawnTimer >= spawnDelay && enemiesSpawned < enemiesNumber) {
                spawnEnemy();
                enemiesSpawned++;
                spawnTimer -= spawnDelay;
            }

            if (enemiesSpawned >= enemiesNumber) {
                spawnState = WaveSpawnState.SPAWN_COMPLETE;
            }
        }

        // dead enemies positions
        List<Vector3D> deadEnemyPositions = new ArrayList<>();
        enemies.forEach(e -> {
            if(e.getHp() <= 0 && e.deathAnimationDone()) deadEnemyPositions.add(e.getPos());
        });

        // Notify Pickable manager
        if (!deadEnemyPositions.isEmpty()) {
            notifyListeners(deadEnemyPositions);
        }

        enemies.forEach(enemy -> {
            if(!enemy.isActive()) {
                GameController.getInstance().removeDrawable(enemy);
                if(enemy.getShadow() != null) GameController.getInstance().removeDrawable(enemy.getShadow());

        		@SuppressWarnings("unchecked")
				ObjectPool<Ennemy> pool =
        		        (ObjectPool<Ennemy>) waveManager.pools.get(enemy.getClass());

    		    if (pool != null) {
    		        pool.free(enemy);
    		    }
            }
        });

        enemies.removeIf(enemy -> !enemy.isActive());

        for(Ennemy e: enemies) {
            e.update(deltaTime, playerPos);
        }
    }

    private void spawnEnemy() {
        Ennemy enemy = spawnFromComposition();
        
        if (enemy != null) {
            enemy.setAttackObjectManager(getAttackObjectManager());
            enemy.initWeapon();
            setRandomSpawnPosition(enemy);
            enemy.onSpawn();
            enemies.add(enemy);

            GameController.getInstance().addDrawable(enemy);
        }
    }

    private void setRandomSpawnPosition(Ennemy enemy) {
        double x, y;
        List<Obstacle> obstacles = GameController.getInstance().getWorldManager().getCurrentWorld().getContext().getObstacles();

        int tileSize = 32;
        // Half the size of your enemy (safe buffer to prevent clipping into walls)
        double radius = 14.0;

        int attempts = 0;
        boolean validPosition = false;

        do {
            x = tileSize + random.nextDouble() * ((worldWidth - 2) * tileSize);
            y = tileSize + random.nextDouble() * ((worldHeight - 2) * tileSize);

            // Check Center
            if (Obstacle.isPositionInObstacles(new Vector3D(x, y), obstacles)) {
                attempts++;
                continue;
            }

            // Check Corners to ensure body fits
            boolean cornersClear =
                    !Obstacle.isPositionInObstacles(new Vector3D(x - radius, y - radius), obstacles) &&
                            !Obstacle.isPositionInObstacles(new Vector3D(x + radius, y + radius), obstacles) &&
                            !Obstacle.isPositionInObstacles(new Vector3D(x + radius, y - radius), obstacles) &&
                            !Obstacle.isPositionInObstacles(new Vector3D(x - radius, y + radius), obstacles);

            if (cornersClear) {
                validPosition = true;
            }

            attempts++;
        } while(!validPosition && attempts < 100);

        if (!validPosition) {
            System.err.println("Warning: Could not find valid spawn position for enemy. Defaulting to center.");
            x = (worldWidth * tileSize) / 2.0;
            y = (worldHeight * tileSize) / 2.0;
        }

        enemy.setPos(new Vector3D(x, y));
    }

    private Ennemy spawnFromComposition() {
        if (enemyComposition == null || enemyComposition.isEmpty()) {
            return specieFactory.createCommon(waveManager);
        }

        int totalCount = enemyComposition.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = random.nextInt(totalCount);
        int currentCount = 0;

        for (Map.Entry<String, Integer> entry : enemyComposition.entrySet()) {
            currentCount += entry.getValue();
            if (randomValue < currentCount) {
                return createEnemyByType(entry.getKey());
            }
        }

        return specieFactory.createCommon(waveManager);
    }

    private Ennemy createEnemyByType(String type) {
        return switch (type.toLowerCase()) {
            case "boss" -> specieFactory.createBoss(waveManager);
            case "tank" -> specieFactory.createTank(waveManager);
            case "speedster" -> specieFactory.createSpeedster(waveManager);
            case "common" -> specieFactory.createCommon(waveManager);
            case "ranged" -> specieFactory.createRanged(waveManager);
            default -> specieFactory.createCommon(waveManager);
        };
    }

    public boolean isCompleted() {
        return spawnState == WaveSpawnState.SPAWN_COMPLETE && enemies.isEmpty();
    }

    public boolean isSpawning() {
        return spawnState == WaveSpawnState.SPAWNING;
    }

    public int getRemainingEnemies() {
        return enemies.size();
    }

    public int getTotalEnemiesForWave() {
        return enemiesNumber;
    }

    private enum WaveSpawnState {
        NOT_STARTED, SPAWNING, SPAWN_COMPLETE
    }
}