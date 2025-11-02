package ma.ac.emi.gamelogic.ai;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.math.Vector2D;

import java.util.List;

@Getter
@Setter
public class MeleeAIBehavior implements AIBehavior {
    private PathFinder pathfinder;
    private double attackRange;
    private Vector2D currentTarget;

    public MeleeAIBehavior(PathFinder pathfinder, double attackRange) {
        this.pathfinder = pathfinder;
        this.attackRange = attackRange;
    }

    @Override
    public Vector2D calculateMovement(Ennemy enemy, Vector2D playerPos, double step) {
        double distance = enemy.getPos().distance(playerPos);

        // si très proche, se déplacer directement
        if (distance < 200) {
            return playerPos.sub(enemy.getPos()).normalize();
        }

        // use pathfinding
        if (currentTarget == null || currentTarget.distance(enemy.getPos()) < 10) {
            List<Vector2D> path = pathfinder.findPath(enemy.getPos(), playerPos);
            if (!path.isEmpty()) {
                currentTarget = path.get(0);
            } else {
                currentTarget = playerPos;
            }
        }

        return currentTarget.sub(enemy.getPos()).normalize();
    }

    @Override
    public boolean shouldAttack(Ennemy enemy, Vector2D playerPos) {
        return enemy.getPos().distance(playerPos) <= attackRange;
    }
}
