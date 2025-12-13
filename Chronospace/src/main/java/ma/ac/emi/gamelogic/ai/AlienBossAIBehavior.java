package ma.ac.emi.gamelogic.ai;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.math.Vector3D;

import java.util.List;
import java.util.Random;

@Getter
@Setter
public class AlienBossAIBehavior implements AIBehavior {
    private PathFinder pathfinder;
    private Random random;
    private static final double TILE_SIZE = 32.0;

    // Physics
    private Vector3D currentVelocity = new Vector3D(0, 0);
    private double acceleration = 0.25;
    private double friction = 0.90;

    private enum Phase {
        ZONER,      // Phase 1: High HP - Spawn and kite
        ASSASSIN,   // Phase 2: Mid HP - Chase and teleport
        BERSERK     // Phase 3: Low HP - Maximum aggression
    }

    private enum BossState {
        IDLE,
        KITING,
        CHASING,
        SUMMON_WINDUP,
        TELEPORT_VANISH,
        TELEPORT_REAPPEAR
    }

    private BossState currentState;
    private Phase currentPhase;

    // Timers
    private double stateTimer;
    private double attackCooldown;
    private double spawnCooldown;
    private double teleportCooldown;
    private double pathTimer;

    private Vector3D moveTarget;
    private List<Vector3D> path;

    private double moveSpeed;
    private boolean isInvulnerable;
    private boolean isVisible;

    // Spawning Logic
    private boolean isSpawningNow;
    private int spawnQuantity;

    public AlienBossAIBehavior(PathFinder pathfinder) {
        this.pathfinder = pathfinder;
        this.random = new Random();
        this.currentState = BossState.IDLE;
        this.currentPhase = Phase.ZONER;
        this.isVisible = true;
        this.isInvulnerable = false;
    }

    @Override
    public Vector3D calculateMovement(Ennemy enemy, Vector3D playerPos, double step) {
        updatePhase(enemy);
        tickTimers(step);

        Vector3D steering = new Vector3D(0, 0);
        double distToPlayer = enemy.getPos().distance(playerPos);

        switch (currentState) {
            case IDLE:
                steering = handleIdle(enemy, playerPos, distToPlayer);
                break;
            case KITING:
                steering = handleKiting(enemy, playerPos, distToPlayer);
                break;
            case CHASING:
                steering = handleChasing(enemy, playerPos);
                break;
            case SUMMON_WINDUP:
                steering = new Vector3D(0, 0);
                if (stateTimer > 1.0) {
                    completeSummoning();
                }
                break;
            case TELEPORT_VANISH:
                steering = new Vector3D(0, 0);
                if (stateTimer > 1.0) {
                    performTeleport(enemy, playerPos);
                    changeState(BossState.TELEPORT_REAPPEAR);
                }
                break;
            case TELEPORT_REAPPEAR:
                steering = new Vector3D(0, 0);
                if (stateTimer > 1.0) {
                    isInvulnerable = false;
                    isVisible = true;
                    teleportCooldown = 5.0;
                    changeState(BossState.CHASING);
                }
                break;
        }

        if (isVisible && currentState != BossState.SUMMON_WINDUP) {
            if (steering.norm() > 0.1) {
                currentVelocity = currentVelocity.add(steering.sub(currentVelocity).mult(acceleration));
            } else {
                currentVelocity = currentVelocity.mult(friction);
            }
        } else {
            currentVelocity = new Vector3D(0, 0);
        }

        if (currentVelocity.norm() < 0.05) {
            currentVelocity = new Vector3D(0, 0);
        }

        return currentVelocity;
    }

    private void tickTimers(double step) {
        stateTimer += step;
        attackCooldown = Math.max(0, attackCooldown - step);
        spawnCooldown = Math.max(0, spawnCooldown - step);
        teleportCooldown = Math.max(0, teleportCooldown - step);
        pathTimer = Math.max(0, pathTimer - step);
        isSpawningNow = false;
    }

    private Vector3D handleIdle(Ennemy enemy, Vector3D playerPos, double distToPlayer) {
        if (spawnCooldown <= 0) {
            changeState(BossState.SUMMON_WINDUP);
            return new Vector3D(0, 0);
        }

        if (currentPhase == Phase.ZONER) {
            changeState(BossState.KITING);
        }
        else {
            changeState(BossState.CHASING);
        }

        return new Vector3D(0, 0);
    }

    private void completeSummoning() {
        isSpawningNow = true;

        switch (currentPhase) {
            case ZONER:
                spawnCooldown = 6.0;
                spawnQuantity = 2;
                break;
            case ASSASSIN:
                spawnCooldown = 4.0;
                spawnQuantity = 4;
                break;
            case BERSERK:
                spawnCooldown = 2.0;
                spawnQuantity = 6;
                break;
        }

        if (currentPhase == Phase.ZONER) {
            changeState(BossState.KITING);
        } else {
            changeState(BossState.CHASING);
        }
    }

    private Vector3D handleKiting(Ennemy enemy, Vector3D playerPos, double distToPlayer) {
        if (spawnCooldown <= 0) {
            changeState(BossState.SUMMON_WINDUP);
            return new Vector3D(0, 0);
        }

        if (distToPlayer < TILE_SIZE * 6) {
            return enemy.getPos().sub(playerPos).normalize().mult(moveSpeed);
        }

        changeState(BossState.IDLE);
        return new Vector3D(0, 0);
    }

    private Vector3D handleChasing(Ennemy enemy, Vector3D playerPos) {
        double distToPlayer = enemy.getPos().distance(playerPos);

        if (spawnCooldown <= 0) {
            changeState(BossState.SUMMON_WINDUP);
            return new Vector3D(0, 0);
        }

        if (currentPhase != Phase.ZONER && teleportCooldown <= 0 && distToPlayer > TILE_SIZE * 3) {
            changeState(BossState.TELEPORT_VANISH);
            isInvulnerable = true;
            isVisible = false;
            return new Vector3D(0, 0);
        }

        if (distToPlayer <= TILE_SIZE * 1.5) {
            return new Vector3D(0, 0);
        }

        if (currentPhase == Phase.BERSERK) {
            return playerPos.sub(enemy.getPos()).normalize().mult(moveSpeed);
        } else {
            return getPathDirection(enemy.getPos(), playerPos).mult(moveSpeed);
        }
    }

    private Vector3D getPathDirection(Vector3D from, Vector3D to) {
        if (pathTimer <= 0) {
            path = pathfinder.findPath(from, to);
            pathTimer = 0.5;
            if (path != null && !path.isEmpty()) {
                moveTarget = path.get(0);
            }
        }
        if (moveTarget != null) {
            if (from.distance(moveTarget) < 4.0) {
                moveTarget = null;
            } else {
                return moveTarget.sub(from).normalize();
            }
        }
        return to.sub(from).normalize();
    }

    private void performTeleport(Ennemy enemy, Vector3D playerPos) {
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = TILE_SIZE * (2 + random.nextDouble() * 2);

        Vector3D offset = new Vector3D(Math.cos(angle), Math.sin(angle)).mult(distance);
        Vector3D newPos = playerPos.add(offset);

        enemy.setPos(newPos);
    }

    private void updatePhase(Ennemy enemy) {
        double hpPct = (double) enemy.getHp() / enemy.getHpMax();
        Phase nextPhase;

        if (hpPct > 0.6) {
            nextPhase = Phase.ZONER;
            moveSpeed = 2.5;
        } else if (hpPct > 0.3) {
            nextPhase = Phase.ASSASSIN;
            moveSpeed = 4.0;
        } else {
            nextPhase = Phase.BERSERK;
            moveSpeed = 6.5;
        }

        if (nextPhase != currentPhase) {
            currentPhase = nextPhase;
            spawnCooldown = 0;
            teleportCooldown = 0;
            attackCooldown = 0;
            stateTimer = 0;
            System.out.println("Boss entered Phase: " + currentPhase);
        }
    }

    private void changeState(BossState newState) {
        this.currentState = newState;
        this.stateTimer = 0;
    }

    public double getDamageMultiplier() {
        switch (currentPhase) {
            case ZONER:
                return 1.0;
            case ASSASSIN:
                return 1.5;
            case BERSERK:
                return 2.5;
            default:
                return 1.0;
        }
    }

    @Override
    public boolean shouldAttack(Ennemy enemy, Vector3D playerPos) {
        double distToPlayer = enemy.getPos().distance(playerPos);

        if (!isVisible) {
            return false;
        }
        if (distToPlayer < TILE_SIZE * 1.5 && attackCooldown <= 0) {
            // Faster attacks in later phases
            switch (currentPhase) {
                case ZONER:
                    attackCooldown = 1.5;
                    break;
                case ASSASSIN:
                    attackCooldown = 1.0;
                    break;
                case BERSERK:
                    attackCooldown = 0.5;
                    break;
            }
            return true;
        }

        return false;
    }
}