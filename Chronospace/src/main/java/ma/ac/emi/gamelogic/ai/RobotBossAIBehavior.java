package ma.ac.emi.gamelogic.ai;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.gamelogic.weapon.WeaponItemFactory;
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
        IDLE, CHASSE, STRAFE, CIRCLE, CHARGE_WINDUP, CHARGING, RETRAITE, SPECIAL_ATTACK
    }

    private BossState currentState;
    private Phase currentPhase;

    private double attackRange;
    private double chargeRange;
    private double retreatRange;

    private Vector3D currentVelocityNormal;
    private double smoothingFactor = 0.15;

    private Vector3D currentTarget;
    private Vector3D chargeDirection;
    private double circleAngle;

    private double stateTimer;
    private double specialAttackCooldown;
    private double chargeTimer;

    private double chaseSpeed = 1.0;
    private double strafeSpeed = 0.7;
    private double chargeSpeed = 3.5;
    private double retreatSpeed = 0.9;

    public RobotBossAIBehavior(PathFinder pathfinder, double attackRange) {
        this.pathfinder = pathfinder;
        this.attackRange = attackRange;
        this.chargeRange = 450.0;
        this.retreatRange = 150.0;
        this.random = new Random();
        this.currentState = BossState.IDLE;
        this.currentPhase = Phase.PHASE_1;
        this.stateTimer = 0;
        this.currentVelocityNormal = new Vector3D(0, 0);
    }

    @Override
    public Vector3D calculateMovement(Ennemy enemy, Vector3D playerPos, double step) {
        updatePhase(enemy);

        stateTimer += step;
        specialAttackCooldown = Math.max(0, specialAttackCooldown - step);

        double distance = enemy.getPos().distance(playerPos);
        Vector3D desiredDirection = new Vector3D(0, 0);

        switch (currentState) {
            case IDLE: desiredDirection = handleIdleState(enemy, playerPos, distance); break;
            case CHASSE: desiredDirection = handleChasingState(enemy, playerPos, distance); break;
            case STRAFE: desiredDirection = handleStrafeState(enemy, playerPos, distance); break;
            case CIRCLE: desiredDirection = handleCirclingState(enemy, playerPos, distance, step); break;
            case CHARGE_WINDUP:
                if (stateTimer > 0.8) changeState(BossState.CHARGING);
                desiredDirection = new Vector3D(0, 0);
                break;
            case CHARGING: desiredDirection = handleChargingState(enemy, playerPos, distance, step); break;
            case RETRAITE: desiredDirection = handleRetreatingState(enemy, playerPos, distance); break;
            case SPECIAL_ATTACK: desiredDirection = handleSpecialAttackState(enemy, playerPos, distance); break;
            default: changeState(BossState.CHASSE); break;
        }

        return smoothSteer(desiredDirection);
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
        changeState(BossState.SPECIAL_ATTACK);
        specialAttackCooldown = 0;
        String newWeaponId = "ak47";
        switch (currentPhase) {
            case PHASE_1: newWeaponId = "ak47"; smoothingFactor = 0.15; break;
            case PHASE_2: newWeaponId = "rpg7"; smoothingFactor = 0.05; break;
            case PHASE_3: newWeaponId = "hammer"; smoothingFactor = 0.3; break;
        }
        Weapon newWeapon = new Weapon(WeaponItemFactory.getInstance().createWeaponItem(newWeaponId), boss);
        newWeapon.setAttackObjectManager(boss.getAttackObjectManager());
        boss.setWeapon(newWeapon);
        boss.initWeapon();
    }

    private Vector3D handleIdleState(Ennemy enemy, Vector3D playerPos, double distance) {
        if (stateTimer > 0.5) {
            if (currentPhase == Phase.PHASE_1) {
                if (distance < attackRange) changeState(BossState.STRAFE);
                else changeState(BossState.CHASSE);
            } else if (currentPhase == Phase.PHASE_3) {
                if (random.nextDouble() < 0.4 && distance > 200) changeState(BossState.CHARGE_WINDUP);
                else changeState(BossState.CHASSE);
            } else {
                changeState(BossState.CIRCLE);
            }
        }
        return new Vector3D(0, 0);
    }

    private Vector3D handleStrafeState(Ennemy enemy, Vector3D playerPos, double distance) {
        if (stateTimer > 2.0 || distance > attackRange * 1.2) {
            changeState(BossState.IDLE);
            return new Vector3D(0, 0);
        }
        Vector3D toPlayer = playerPos.sub(enemy.getPos()).normalize();
        Vector3D strafeDir = new Vector3D(-toPlayer.getY(), toPlayer.getX());
        if (random.nextBoolean()) strafeDir = strafeDir.mult(-1);
        return strafeDir.mult(strafeSpeed);
    }

    private Vector3D handleChasingState(Ennemy enemy, Vector3D playerPos, double distance) {
        double stopDist = (currentPhase == Phase.PHASE_3) ? 40 : attackRange * 0.8;
        if (distance < stopDist) {
            changeState(BossState.IDLE);
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

    private Vector3D handleCirclingState(Ennemy enemy, Vector3D playerPos, double distance, double step) {
        if (stateTimer > 3.0) {
            changeState(BossState.IDLE);
            return new Vector3D(0, 0);
        }
        Vector3D toPlayer = playerPos.sub(enemy.getPos());
        double currentDist = toPlayer.norm();
        double targetRadius = 250.0;
        Vector3D tangent = new Vector3D(-toPlayer.getY(), toPlayer.getX()).normalize();
        Vector3D radial = toPlayer.normalize().mult(currentDist - targetRadius).mult(0.5);
        return tangent.add(radial).normalize().mult(1.2);
    }

    private Vector3D handleChargingState(Ennemy enemy, Vector3D playerPos, double distance, double step) {
        if (chargeDirection == null) {
            chargeDirection = playerPos.sub(enemy.getPos()).normalize();
            chargeTimer = 0;
        }
        chargeTimer += step;
        if (chargeTimer > 1.0 || distance < 30) {
            chargeDirection = null;
            changeState(BossState.IDLE);
            return new Vector3D(0, 0);
        }
        return chargeDirection.mult(chargeSpeed);
    }

    private Vector3D handleRetreatingState(Ennemy enemy, Vector3D playerPos, double distance) {
        if (stateTimer > 1.5 || distance > retreatRange * 2) {
            changeState(BossState.IDLE);
            return new Vector3D(0, 0);
        }
        return enemy.getPos().sub(playerPos).normalize().mult(retreatSpeed);
    }

    private Vector3D handleSpecialAttackState(Ennemy enemy, Vector3D playerPos, double distance) {
        if (stateTimer > 1.5) {
            specialAttackCooldown = getSpecialAttackCooldown();
            changeState(BossState.IDLE);
        }
        return new Vector3D(0, 0);
    }

    private void changeState(BossState newState) {
        currentState = newState;
        stateTimer = 0;
        if (newState == BossState.CHARGING) chargeDirection = null;
        if (newState == BossState.CHASSE) currentTarget = null;
    }

    @Override
    public boolean shouldAttack(Ennemy enemy, Vector3D playerPos) {
        double distance = enemy.getPos().distance(playerPos);

        // 1. Priority: Special Attack
        if (specialAttackCooldown <= 0 && shouldTriggerSpecialAttack()) {
            changeState(BossState.SPECIAL_ATTACK);
            return true;
        }

        // 2. Standard Attack (No manual cooldown, weapon handles fire rate)
        if (currentPhase == Phase.PHASE_1) {
            if (distance < attackRange && currentState != BossState.RETRAITE) {
                return true;
            }
        }
        else if (currentPhase == Phase.PHASE_2) {
            if ((currentState == BossState.CIRCLE || currentState == BossState.IDLE) && distance < attackRange * 1.5) {
                return true;
            }
        }
        else if (currentPhase == Phase.PHASE_3) {
            if (distance < 80) {
                return true;
            }
        }
        return false;
    }

    private boolean shouldTriggerSpecialAttack() {
        double chance = switch (currentPhase) {
            case PHASE_1 -> 0.05;
            case PHASE_2 -> 0.10;
            case PHASE_3 -> 0.20;
        };
        return random.nextDouble() < chance;
    }

    private double getSpecialAttackCooldown() {
        return switch (currentPhase) {
            case PHASE_1 -> 10.0;
            case PHASE_2 -> 8.0;
            case PHASE_3 -> 5.0;
        };
    }
}