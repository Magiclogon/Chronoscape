package ma.ac.emi.gamelogic.wave;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.factory.EnnemySpecieFactory;
import ma.ac.emi.math.Vector2D;

import java.util.*;

@Getter
@Setter
public class Wave extends WaveNotifier {
    private int number;
    private int enemiesNumber;
    private List<Ennemy> enemies;
    private EnnemySpecieFactory specieFactory;
    private DifficultyStrategy difficulty;
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

    public Wave(int number, int enemiesNumber, EnnemySpecieFactory specieFactory,
                DifficultyStrategy difficulty, int worldWidth, int worldHeight) {
        this.number = number;
        this.enemiesNumber = enemiesNumber;
        this.specieFactory = specieFactory;
        this.difficulty = difficulty;
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

        enemies.forEach(e -> {
            if(e.getHp() <= 0) spawnPoints.add(e.getPos());
        });
        notifyListeners();

        enemies.removeIf(enemy -> enemy.getHp() <= 0);
        for(Ennemy e: enemies) {
            e.update(deltaTime, playerPos);
        }
    }

    private void spawnEnemy() {
        Ennemy enemy = spawnFromComposition();

        if (enemy != null) {
            setRandomSpawnPosition(enemy);
            enemies.add(enemy);
        }
    }

    private void setRandomSpawnPosition(Ennemy enemy) {
        // Spawn at edges of the world
        int side = random.nextInt(4); // 0=top, 1=right, 2=bottom, 3=left
        double x, y;

        switch (side) {
            case 0: // Top
                x = random.nextDouble() * worldWidth * 16;
                y = -50;
                break;
            case 1: // Right
                x = worldWidth * 16 + 50;
                y = random.nextDouble() * worldHeight * 16;
                break;
            case 2: // Bottom
                x = random.nextDouble() * worldWidth * 16;
                y = worldHeight * 16 + 50;
                break;
            case 3: // Left
            default:
                x = -50;
                y = random.nextDouble() * worldHeight * 16;
                break;
        }

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
    public List<Vector2D> getState() {
        return spawnPoints;
    }
}