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

    // physics
    private Vector3D currentVelocity = new Vector3D(0, 0);
    private double acceleration = 0.25;
    private double friction = 0.90;

    private enum Phase {
        ZONER,    // phase 1: spawns minions, keeps distance
        ASSASSIN, // phase 2: teleports, strikes from behind
        BERSERK   // phase 3: fast chase, raw aggression
    }

    private enum BossState {
        IDLE,
        KITING,          // run away from player
        CHASING,         // run towards player

        // ability 1: spawning minions
        SUMMON_WINDUP,

        // ability 2: teleportation
        BLINK_WINDUP,    // vanish
        BLINK_STRIKE     // appear and hit
    }

    private BossState currentState;
    private Phase currentPhase;

    // timers
    private double stateTimer;
    private double globalCooldown;
    private double abilityCooldown;

    // movement
    private Vector3D moveTarget;
    private List<Vector3D> path;
    private double pathTimer;

    // stats
    private double moveSpeed;
    private boolean isInvulnerable;
    private boolean justHit;
    private boolean isSpawningNow;

    public AlienBossAIBehavior(PathFinder pathfinder) {
        this.pathfinder = pathfinder;
        this.random = new Random();
        this.currentState = BossState.IDLE;
        this.currentPhase = Phase.ZONER;
    }

    @Override
    public Vector3D calculateMovement(Ennemy enemy, Vector3D playerPos, double step) {
        updatePhase(enemy);
        tickTimers(step);

        Vector3D steering = new Vector3D(0,0);
        double distToPlayer = enemy.getPos().distance(playerPos);

        switch (currentState) {
            case IDLE:
                steering = handleIdle(enemy, playerPos);
                break;
            case KITING:
                steering = handleKiting(enemy, playerPos, distToPlayer);
                break;
            case CHASING:
                steering = handleChasing(enemy, playerPos);
                break;
            case SUMMON_WINDUP:
                // stationary while summoning
                if (stateTimer > 1.5) spawnMinionsAndReset();
                break;
            case BLINK_WINDUP:
            case BLINK_STRIKE:
                handleBlinkSequence(enemy, playerPos);
                break;
        }

        // physics application
        if (steering.norm() > 0.1) {
            currentVelocity = currentVelocity.add(steering.sub(currentVelocity).mult(acceleration));
        } else {
            currentVelocity = currentVelocity.mult(friction);
        }

        if(currentVelocity.norm() < 0.05) currentVelocity = new Vector3D(0,0);

        return currentVelocity;
    }

    private void tickTimers(double step) {
        stateTimer += step;
        globalCooldown = Math.max(0, globalCooldown - step);
        abilityCooldown = Math.max(0, abilityCooldown - step);
        pathTimer = Math.max(0, pathTimer - step);
        isSpawningNow = false; // reset frame flag
    }

    private Vector3D handleIdle(Ennemy enemy, Vector3D playerPos) {
        // fast reaction in later phases
        double reactionTime = (currentPhase == Phase.BERSERK) ? 0.2 : 0.5;

        if (stateTimer > reactionTime) {
            chooseTactics(enemy, playerPos);
        }
        return new Vector3D(0,0);
    }

    private void chooseTactics(Ennemy enemy, Vector3D playerPos) {
        // priority 1: use special ability if ready
        if (abilityCooldown <= 0) {
            if (currentPhase == Phase.ZONER) {
                changeState(BossState.SUMMON_WINDUP);
                return;
            } else if (currentPhase == Phase.ASSASSIN) {
                changeState(BossState.BLINK_WINDUP);
                return;
            }
            // phase 3 has no special cast time, it just chases aggressively
        }

        // priority 2: movement based on phase
        if (currentPhase == Phase.ZONER) {
            changeState(BossState.KITING);
        } else {
            changeState(BossState.CHASING);
        }
    }

    // --- ability 1: minions ---
    private void spawnMinionsAndReset() {
        isSpawningNow = true; // tell game loop to spawn entity
        abilityCooldown = 15.0;
        changeState(BossState.KITING);
    }

    // --- ability 2: teleportation ---
    private void handleBlinkSequence(Ennemy enemy, Vector3D playerPos) {
        if (currentState == BossState.BLINK_WINDUP) {
            isInvulnerable = true;
            if (stateTimer > 0.5) {
                // teleport behind player
                Vector3D dir = playerPos.sub(enemy.getPos()).normalize();
                Vector3D behind = playerPos.add(dir.mult(-TILE_SIZE * 1.5));
                enemy.setPos(behind);
                changeState(BossState.BLINK_STRIKE);
            }
        } else { // strike
            isInvulnerable = false;
            if (stateTimer > 0.4) {
                justHit = true; // deal damage
                abilityCooldown = 5.0;
                changeState(BossState.IDLE);
            }
        }
    }

    // --- movement logic ---
    private Vector3D handleKiting(Ennemy enemy, Vector3D playerPos, double dist) {
        // phase 1: keep distance
        if (dist < TILE_SIZE * 6) {
            return enemy.getPos().sub(playerPos).normalize().mult(moveSpeed);
        }
        changeState(BossState.IDLE);
        return new Vector3D(0,0);
    }

    private Vector3D handleChasing(Ennemy enemy, Vector3D playerPos) {
        // phase 2 & 3: aggressive
        if (enemy.getPos().distance(playerPos) <= TILE_SIZE) return new Vector3D(0,0);

        if (currentPhase == Phase.BERSERK) {
            // berserk ignores pathfinding for raw speed
            return playerPos.sub(enemy.getPos()).normalize().mult(moveSpeed);
        }
        return getPathDirection(enemy.getPos(), playerPos).mult(moveSpeed);
    }

    private Vector3D getPathDirection(Vector3D from, Vector3D to) {
        if (pathTimer <= 0) {
            path = pathfinder.findPath(from, to);
            pathTimer = 0.5;
            if (path != null && !path.isEmpty()) moveTarget = path.get(0);
        }
        if (moveTarget != null) {
            if (from.distance(moveTarget) < 4.0) moveTarget = null;
            else return moveTarget.sub(from).normalize();
        }
        return to.sub(from).normalize();
    }

    private void updatePhase(Ennemy enemy) {
        double hpPct = (double) enemy.getHp() / enemy.getHpMax();
        Phase nextPhase; // Determine what phase we SHOULD be in

        if (hpPct > 0.6) {
            nextPhase = Phase.ZONER;
            moveSpeed = 2.5;
        } else if (hpPct > 0.3) {
            nextPhase = Phase.ASSASSIN;
            moveSpeed = 3.5;
        } else {
            nextPhase = Phase.BERSERK;
            moveSpeed = 5.5;
        }

        if (nextPhase != currentPhase) {
            currentPhase = nextPhase;
            abilityCooldown = 0;
            stateTimer = 0;      // Reset reaction timer
            System.out.println("Boss entered Phase: " + currentPhase);
        }
    }

    private void changeState(BossState newState) {
        this.currentState = newState;
        this.stateTimer = 0;
    }

    @Override
    public boolean shouldAttack(Ennemy enemy, Vector3D playerPos) {
        // trigger damage for abilities
        if (justHit) {
            justHit = false;
            return true;
        }

        // standard melee attack
        if (currentState == BossState.CHASING && enemy.getPos().distance(playerPos) < TILE_SIZE * 1.5) {
            if (globalCooldown <= 0) {
                globalCooldown = 1.0;
                return true;
            }
        }
        return false;
    }
}