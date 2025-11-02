package ma.ac.emi.gamelogic.ai;

import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.math.Vector2D;

public interface AIBehavior {
    Vector2D calculateMovement(Ennemy enemy, Vector2D playerPos, double step);
    boolean shouldAttack(Ennemy enemy, Vector2D playerPos);
}