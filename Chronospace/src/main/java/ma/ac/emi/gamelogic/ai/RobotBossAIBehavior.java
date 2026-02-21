package ma.ac.emi.gamelogic.ai;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.shop.WeaponItem;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.gamelogic.weapon.WeaponItemFactory;
import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehaviorDefinition;
import ma.ac.emi.math.Vector3D;

import java.util.List;
import java.util.Random;

@Getter
@Setter
public class RobotBossAIBehavior implements AIBehavior {
    private PathFinder pathfinder;
    private Random random;

    private enum Phase {
        PHASE_1, // AK-47
        PHASE_2, // RPG-7
        PHASE_3  // Hammer
    }

    private enum BossState {
        IDLE, CHASSE, SUMMONING
    }

    private BossState currentState;
    private Phase currentPhase;

    private double attackRange;

    private Vector3D currentVelocityNormal;
    private double smoothingFactor = 0.15;

    private Vector3D currentTarget;

    private double stateTimer;
    private double weaponSwitchTimer = 10.0;
    private double spawnTimer = 7.0;
    private boolean isSpawningNow = false;
    private int spawnQuantity = 3;
    
    private static final List<String> BOSS_WEAPONS = List.of("robot_boss_machinegun", "robot_boss_spear", "robot_boss_cannon");

    private double chaseSpeed = 1.0;

    public RobotBossAIBehavior(PathFinder pathfinder, double attackRange) {
        this.pathfinder = pathfinder;
        this.attackRange = attackRange;
        this.random = new Random();
        this.currentState = BossState.IDLE;
        this.currentPhase = Phase.PHASE_1;
        this.stateTimer = 0;
        this.weaponSwitchTimer = 5.0 + random.nextDouble() * 3;
        this.spawnTimer = 7.0;
        this.currentVelocityNormal = new Vector3D(0, 0);
    }

    @Override
    public Vector3D calculateMovement(Ennemy enemy, Vector3D playerPos, double step) {
        updatePhase(enemy);
        updateAttackRange(enemy);

        stateTimer += step;
        weaponSwitchTimer -= step;
        spawnTimer -= step;

        isSpawningNow = false;

        if (weaponSwitchTimer <= 0) {
            switchRandomWeapon(enemy);
            weaponSwitchTimer = 5 + random.nextDouble() * 3;
        }

        if (spawnTimer <= 0 && currentState != BossState.SUMMONING) {
            changeState(BossState.SUMMONING);
        }

        double distance = enemy.getPos().distance(playerPos);
        Vector3D desiredDirection = new Vector3D(0, 0);

        switch (currentState) {
            case IDLE: desiredDirection = handleIdleState(enemy, playerPos, distance); break;
            case CHASSE: desiredDirection = handleChasingState(enemy, playerPos, distance); break;
            case SUMMONING: desiredDirection = handleSummoningState(enemy, playerPos, distance); break;
            default: changeState(BossState.CHASSE); break;
        }

        return smoothSteer(desiredDirection);
    }

    private Vector3D handleSummoningState(Ennemy enemy, Vector3D playerPos, double distance) {
        if (stateTimer > 1.2) {
            isSpawningNow = true;

            DifficultyStrategy difficulty = GameController.getInstance().getDifficulty();
            double rateMultiplier = difficulty != null ? difficulty.getBossSpawnRateMultiplier() : 1.0;

            double baseInterval = switch (currentPhase) {
                case PHASE_1 -> 7.0;
                case PHASE_2 -> 5.0;
                case PHASE_3 -> 3.0;
            };
            spawnTimer = baseInterval / rateMultiplier;

            int baseQuantity = switch (currentPhase) {
                case PHASE_1 -> 3;
                case PHASE_2 -> 4;
                case PHASE_3 -> 5;
            };
            spawnQuantity = baseQuantity;

            changeState(BossState.IDLE);
        }
        return calculateChasingDirection(enemy, playerPos, distance);
    }

    private Vector3D calculateChasingDirection(Ennemy enemy, Vector3D playerPos, double distance) {
        double stopDist = (currentPhase == Phase.PHASE_3) ? attackRange * 0.4 : attackRange * 0.8;
        if (distance < stopDist) {
            return new Vector3D(0, 0);
        }
        if (distance > 300) {
            if (currentTarget == null || currentTarget.distance(enemy.getPos()) < 20) {
                List<Vector3D> path = pathfinder.findPath(enemy.getPos(), playerPos);
                if (!path.isEmpty()) currentTarget = path.get(0);
                else currentTarget = playerPos;
            }
            return currentTarget.sub(enemy.getPos()).normalize().mult(chaseSpeed);
        }
        return playerPos.sub(enemy.getPos()).normalize().mult(chaseSpeed);
    }

    private Vector3D smoothSteer(Vector3D desired) {
        double destX = desired.getX();
        double destY = desired.getY();
        double currX = currentVelocityNormal.getX();
        double currY = currentVelocityNormal.getY();
        double newX = currX + (destX - currX) * smoothingFactor;
        double newY = currY + (destY - currY) * smoothingFactor;
        currentVelocityNormal = new Vector3D(newX, newY);
        return currentVelocityNormal;
    }

    private void updatePhase(Ennemy enemy) {
        double healthPercent = (double) enemy.getHp() / enemy.getHpMax();
        Phase oldPhase = currentPhase;

        if (healthPercent > 0.66) currentPhase = Phase.PHASE_1;
        else if (healthPercent > 0.33) currentPhase = Phase.PHASE_2;
        else currentPhase = Phase.PHASE_3;

        if (oldPhase != currentPhase) onPhaseChange(enemy);
    }

    private void onPhaseChange(Ennemy boss) {
        // Stats increase
        boss.setDamage(boss.getDamage() * 1.7);
        boss.setSpeed(boss.getSpeed() * 1.3);
        
        // AI speed multipliers increase
        chaseSpeed *= 1.2;
        
        switchRandomWeapon(boss);

        switch (currentPhase) {
            case PHASE_1: smoothingFactor = 0.15; break;
            case PHASE_2: smoothingFactor = 0.10; break;
            case PHASE_3: smoothingFactor = 0.25; break;
        }
    }

    private void switchRandomWeapon(Ennemy boss) {
        if (boss.getWeapon() != null) {
            GameController.getInstance().removeDrawable(boss.getWeapon());
        }

        String newWeaponId = BOSS_WEAPONS.get(random.nextInt(BOSS_WEAPONS.size()));
        WeaponItem item = WeaponItemFactory.getInstance().createWeaponItem(newWeaponId);
        Weapon newWeapon = new Weapon(item, boss);
        
        List<WeaponBehaviorDefinition> behaviors = ((WeaponItemDefinition) item.getItemDefinition()).getBehaviorDefinitions();
        behaviors.forEach(b -> newWeapon.getBehaviors().add(b.create()));
        newWeapon.init();
        
        newWeapon.setAttackObjectManager(boss.getAttackObjectManager());
        boss.setWeapon(newWeapon);
        newWeapon.snapTo(boss);
        
        updateAttackRange(boss);
    }
    
    private void updateAttackRange(Ennemy boss) {
        if (boss.getWeapon() != null && boss.getWeapon().getWeaponItem() != null) {
            WeaponItemDefinition def = (WeaponItemDefinition) boss.getWeapon().getWeaponItem().getItemDefinition();
            this.attackRange = def.getRange();
        }
    }

    private Vector3D handleIdleState(Ennemy enemy, Vector3D playerPos, double distance) {
        if (stateTimer > 0.5) {
            changeState(BossState.CHASSE);
        }
        return new Vector3D(0, 0);
    }

    private Vector3D handleChasingState(Ennemy enemy, Vector3D playerPos, double distance) {
        Vector3D dir = calculateChasingDirection(enemy, playerPos, distance);
        if (dir.norm() == 0) {
            changeState(BossState.IDLE);
        }
        return dir;
    }

    private void changeState(BossState newState) {
        currentState = newState;
        stateTimer = 0;
        if (newState == BossState.CHASSE) currentTarget = null;
    }

    @Override
    public boolean shouldAttack(Ennemy enemy, Vector3D playerPos) {
        double distance = enemy.getPos().distance(playerPos);

        // Standard Attack (No manual cooldown, weapon handles fire rate)
        if (distance < attackRange && currentState != BossState.SUMMONING) {
            return true;
        }
        return false;
    }
}