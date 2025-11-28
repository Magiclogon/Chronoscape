package ma.ac.emi.gamelogic.ai;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.math.Vector3D;

import java.util.List;
import java.util.Random;

@Getter
@Setter
public class BossAIBehavior implements AIBehavior {
    private PathFinder pathfinder;
    private Random random;

    private enum Phase {
        PHASE_1,
        PHASE_2,
        PHASE_3
    }

    private enum BossState {
        IDLE,
        CHASSE,
        CIRCLE,
        CHARGING,
        RETRAITE,
        SPECIAL_ATTACK
    }

    private BossState currentState;
    private Phase currentPhase;

    // Configuration
    private double meleeRange;
    private double chargeRange;
    private double retreatRange;

    private Vector3D currentTarget;
    private Vector3D chargeDirection;
    private Vector3D circleCenter;
    private double circleAngle;

    private double stateTimer;
    private double attackCooldown;
    private double specialAttackCooldown;
    private double chargeTimer;

    private double chaseSpeed = 1.0;
    private double chargeSpeed = 2.5;
    private double retreatSpeed = 0.8;
    private double circleSpeed = 1.2;
    private double circleRadius = 150.0;

    public BossAIBehavior(PathFinder pathfinder, double meleeRange) {
        this.pathfinder = pathfinder;
        this.meleeRange = meleeRange;
        this.chargeRange = 400.0;
        this.retreatRange = 100.0;
        this.random = new Random();
        this.currentState = BossState.IDLE;
        this.currentPhase = Phase.PHASE_1;
        this.stateTimer = 0;
        this.attackCooldown = 0;
        this.specialAttackCooldown = 0;
        this.circleAngle = 0;
    }

    @Override
    public Vector3D calculateMovement(Ennemy enemy, Vector3D playerPos, double step) {

        updatePhase(enemy);

        // mise à jour timers
        stateTimer += step;
        attackCooldown = Math.max(0, attackCooldown - step);
        specialAttackCooldown = Math.max(0, specialAttackCooldown - step);

        double distance = enemy.getPos().distance(playerPos);

        switch (currentState) {
            case IDLE:
                return handleIdleState(enemy, playerPos, distance, step);

            case CHASSE:
                return handleChasingState(enemy, playerPos, distance, step);

            case CIRCLE:
                return handleCirclingState(enemy, playerPos, distance, step);

            case CHARGING:
                return handleChargingState(enemy, playerPos, distance, step);

            case RETRAITE:
                return handleRetreatingState(enemy, playerPos, distance, step);

            case SPECIAL_ATTACK:
                return handleSpecialAttackState(enemy, playerPos, distance, step);

            default:
                changeState(BossState.CHASSE);
                return new Vector3D(0, 0);
        }
    }

    private void updatePhase(Ennemy enemy) {
        double healthPercent = (double) enemy.getHp() / enemy.getHpMax();

        if (healthPercent > 0.66) {
            currentPhase = Phase.PHASE_1;
        } else if (healthPercent > 0.33) {
            if (currentPhase == Phase.PHASE_1) {
                currentPhase = Phase.PHASE_2;
                onPhaseChange();
            }
        } else {
            if (currentPhase != Phase.PHASE_3) {
                currentPhase = Phase.PHASE_3;
                onPhaseChange();
            }
        }
    }

    private void onPhaseChange() {
        changeState(BossState.SPECIAL_ATTACK);
        specialAttackCooldown = 0;
    }

    private Vector3D handleIdleState(Ennemy enemy, Vector3D playerPos, double distance, double step) {
        if (stateTimer > 1.0) {
            if (distance > chargeRange) {
                changeState(BossState.CHASSE);
            } else if (distance < retreatRange) {
                changeState(BossState.RETRAITE);
            } else {
                // random
                double rand = random.nextDouble();
                if (currentPhase == Phase.PHASE_3 && rand < 0.4) {
                    changeState(BossState.CHARGING);
                } else if (rand < 0.6) {
                    changeState(BossState.CIRCLE);
                } else {
                    changeState(BossState.CHASSE);
                }
            }
        }
        return new Vector3D(0, 0);
    }

    private Vector3D handleChasingState(Ennemy enemy, Vector3D playerPos, double distance, double step) {
        // chasser le joueur
        if (distance < meleeRange * 1.5) {
            changeState(BossState.IDLE);
            return new Vector3D(0, 0);
        }

        // charging si la phase > 2 et distance requise pour la charge
        if (currentPhase != Phase.PHASE_1 && distance > 200 && distance < chargeRange && stateTimer > 2.0) {
            if (random.nextDouble() < 0.3) {
                changeState(BossState.CHARGING);
                return new Vector3D(0, 0);
            }
        }

        // sinon
        if (distance > 300) {
            if (currentTarget == null || currentTarget.distance(enemy.getPos()) < 20) {
                List<Vector3D> path = pathfinder.findPath(enemy.getPos(), playerPos);
                if (!path.isEmpty()) {
                    currentTarget = path.get(0);
                } else {
                    currentTarget = playerPos;
                }
            }
            return currentTarget.sub(enemy.getPos()).normalize().mult(chaseSpeed);
        } else {
            return playerPos.sub(enemy.getPos()).normalize().mult(chaseSpeed);
        }
    }

    private Vector3D handleCirclingState(Ennemy enemy, Vector3D playerPos, double distance, double step) {
        // Circle sur le joueur
        if (stateTimer > getCircleDuration()) {
            changeState(BossState.IDLE);
            return new Vector3D(0, 0);
        }

        // Maintain circle radius
        Vector3D toPlayer = playerPos.sub(enemy.getPos());
        double currentDist = toPlayer.norm();

        // bouge perendiculairement tangente
        circleAngle += step * (currentPhase == Phase.PHASE_3 ? 2.0 : 1.0);
        Vector3D tangent = new Vector3D(-toPlayer.getY(), toPlayer.getX()).normalize();

        // Combine tangential movement with radius correction
        Vector3D radialCorrection = toPlayer.normalize().mult(currentDist - circleRadius).mult(0.1);

        return tangent.mult(circleSpeed).add(radialCorrection).normalize().mult(circleSpeed);
    }

    private Vector3D handleChargingState(Ennemy enemy, Vector3D playerPos, double distance, double step) {
        // rapide vers le joueur
        if (chargeDirection == null) {
            // initialiser la direction
            chargeDirection = playerPos.sub(enemy.getPos()).normalize();
            chargeTimer = 0;
        }

        chargeTimer += step;

        // fin de charge
        if (chargeTimer > 1.5 || distance < meleeRange) {
            chargeDirection = null;
            changeState(BossState.IDLE);
            return new Vector3D(0, 0);
        }

        return chargeDirection.mult(chargeSpeed);
    }

    private Vector3D handleRetreatingState(Ennemy enemy, Vector3D playerPos, double distance, double step) {
        // retraite du Joueur
        if (stateTimer > 2.0 || distance > chargeRange * 0.7) {
            changeState(BossState.IDLE);
            return new Vector3D(0, 0);
        }

        // move away
        Vector3D awayDirection = enemy.getPos().sub(playerPos).normalize();
        return awayDirection.mult(retreatSpeed);
    }

    private Vector3D handleSpecialAttackState(Ennemy enemy, Vector3D playerPos, double distance, double step) {
        // special attack
        if (stateTimer > 2.0) {
            specialAttackCooldown = getSpecialAttackCooldown();
            changeState(BossState.IDLE);
        }

        return new Vector3D(0, 0);
    }

    private void changeState(BossState newState) {
        currentState = newState;
        stateTimer = 0;

        if (newState == BossState.CIRCLE) {
            circleAngle = 0;
        } else if (newState == BossState.CHARGING) {
            chargeDirection = null;
        }
    }

    @Override
    public boolean shouldAttack(Ennemy enemy, Vector3D playerPos) {
        double distance = enemy.getPos().distance(playerPos);

        // Melee attack
        if (distance <= meleeRange && attackCooldown <= 0) {
            attackCooldown = getAttackCooldown();
            return true;
        }

        // Special attack trigger
        if (specialAttackCooldown <= 0 && distance < meleeRange * 2.5) {
            if (shouldTriggerSpecialAttack()) {
                changeState(BossState.SPECIAL_ATTACK);
                return true;
            }
        }

        // Charging attack
        if (currentState == BossState.CHARGING && distance < meleeRange * 2) {
            return true;
        }

        return false;
    }

    private boolean shouldTriggerSpecialAttack() {
        switch (currentPhase) {
            case PHASE_1:
                return random.nextDouble() < 0.15;
            case PHASE_2:
                return random.nextDouble() < 0.25;
            case PHASE_3:
                return random.nextDouble() < 0.35;
            default:
                return false;
        }
    }

    private double getAttackCooldown() {
        switch (currentPhase) {
            case PHASE_1:
                return 1.5;
            case PHASE_2:
                return 1.0;
            case PHASE_3:
                return 0.6;
            default:
                return 1.5;
        }
    }

    private double getSpecialAttackCooldown() {
        switch (currentPhase) {
            case PHASE_1:
                return 8.0;
            case PHASE_2:
                return 6.0;
            case PHASE_3:
                return 4.0;
            default:
                return 8.0;
        }
    }

    private double getCircleDuration() {
        switch (currentPhase) {
            case PHASE_1:
                return 4.0;
            case PHASE_2:
                return 3.0;
            case PHASE_3:
                return 2.0;
            default:
                return 4.0;
        }
    }
}