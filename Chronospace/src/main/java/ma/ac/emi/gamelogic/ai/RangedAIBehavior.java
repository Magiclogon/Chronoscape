package ma.ac.emi.gamelogic.ai;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.math.Vector3D;

import java.util.List;

@Getter
@Setter
public class RangedAIBehavior implements AIBehavior {
    private PathFinder pathfinder;
    private double optimalRange;
    private double attackRange;
    private double minRange;
    private Vector3D currentTarget;

    public RangedAIBehavior(PathFinder pathfinder, double optimalRange, double attackRange) {
        this.pathfinder = pathfinder;
        this.optimalRange = optimalRange;
        this.attackRange = attackRange;
        this.minRange = optimalRange * 0.7;
    }

    @Override
    public Vector3D calculateMovement(Ennemy enemy, Vector3D playerPos, double step) {
        double distance = enemy.getPos().distance(playerPos);

        // Too close - retreat
        if (distance < minRange) {
            Vector3D retreatDirection = enemy.getPos().sub(playerPos).normalize();
            return retreatDirection;
        }

        // Too far - approach
        if (distance > optimalRange) {
            if (distance < 200) {
                return playerPos.sub(enemy.getPos()).normalize();
            }

            // Use pathfinding for longer distances
            if (currentTarget == null || currentTarget.distance(enemy.getPos()) < 10) {
                List<Vector3D> path = pathfinder.findPath(enemy.getPos(), playerPos);
                if (!path.isEmpty()) {
                    currentTarget = path.get(0);
                } else {
                    currentTarget = playerPos;
                }
            }
            return currentTarget.sub(enemy.getPos()).normalize();
        }

        // At optimal range - strafe (move perpendicular)
        Vector3D toPlayer = playerPos.sub(enemy.getPos());
        Vector3D perpendicular = new Vector3D(-toPlayer.getY(), toPlayer.getX()).normalize();
        return perpendicular;
    }

    @Override
    public boolean shouldAttack(Ennemy enemy, Vector3D playerPos) {
        double distance = enemy.getPos().distance(playerPos);
        return distance <= attackRange && distance >= minRange;
    }
}
