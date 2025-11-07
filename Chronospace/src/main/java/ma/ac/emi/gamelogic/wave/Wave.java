package ma.ac.emi.gamelogic.wave;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.attack.manager.AttackObjectManager;
import ma.ac.emi.gamelogic.difficulty.DifficultyObserver;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.math.Vector2D;

import java.util.*;

@Getter
@Setter
public class Wave extends WaveNotifier implements DifficultyObserver{
    private int number;
    private int enemiesNumber;
    private List<Ennemy> enemies;
    private EnnemySpecieFactory specieFactory;
    private Map<String, Integer> enemyComposition;
    private boolean isBossWave;
    private double spawnDelay;
    private double spawnTimer;
    private int enemiesSpawned;
    private WaveSpawnState spawnState;
    private Random random;
    private List<Vector2D> spawnPoints;
    private int worldWidth;
    private int worldHeight;
    private double enemyNumberMultiplier;
    
    private AttackObjectManager attackObjectManager;

    public Wave(int number, int enemiesNumber, EnnemySpecieFactory specieFactory, int worldWidth, int worldHeight) {
        this.number = number;
        this.enemiesNumber = enemiesNumber;
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
        
        GameController.getInstance().addDifficultyObserver(this);
    }

    public void spawn() {
        spawnState = WaveSpawnState.SPAWNING;
        spawnTimer = 0;
        enemiesSpawned = 0;
    }

    public void update(double deltaTime, Vector2D playerPos) {
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
        List<Vector2D> deadEnemyPositions = new ArrayList<>();
        enemies.forEach(e -> {
            if(e.getHp() <= 0) deadEnemyPositions.add(e.getPos());
        });

        // Notify
        if (!deadEnemyPositions.isEmpty()) {
            System.out.println("Number of points: " + deadEnemyPositions.size());
            notifyListeners(deadEnemyPositions);
        }

        enemies.removeIf(enemy -> enemy.getHp() <= 0);
        for(Ennemy e: enemies) {
            e.update(deltaTime, playerPos);
        }
    }

    private void spawnEnemy() {
        Ennemy enemy = spawnFromComposition();
        enemy.setAttackObjectManager(getAttackObjectManager());
        enemy.initWeapon();
        
        if (enemy != null) {
            setRandomSpawnPosition(enemy);
            enemies.add(enemy);
        }
    }

    private void setRandomSpawnPosition(Ennemy enemy) {
        // Spawn at edges of the world
        int side = random.nextInt(4); // 0=top, 1=right, 2=bottom, 3=left
        double x, y;
        x = random.nextDouble() * worldWidth * 16;
        y = random.nextDouble() * worldHeight * 16;

        enemy.setPos(new Vector2D(x, y));
    }

    private Ennemy spawnFromComposition() {
        if (enemyComposition == null || enemyComposition.isEmpty()) {
            return specieFactory.createCommon();
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

        return specieFactory.createCommon();
    }

    private Ennemy createEnemyByType(String type) {
        return switch (type.toLowerCase()) {
            case "boss" -> specieFactory.createBoss();
            case "tank" -> specieFactory.createTank();
            case "speedster" -> specieFactory.createSpeedster();
            case "common" -> specieFactory.createCommon();
            case "ranged" -> specieFactory.createRanged();
            default -> null;
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

	@Override
	public void refreshDifficulty(DifficultyStrategy difficutly) {
		difficutly.adjustEnemiesNumberWave(this);
	}
}