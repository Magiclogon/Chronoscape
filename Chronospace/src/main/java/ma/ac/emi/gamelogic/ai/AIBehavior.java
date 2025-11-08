package ma.ac.emi.gamelogic.ai;

import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.math.Vector3D;

public interface AIBehavior {
    Vector3D calculateMovement(Ennemy enemy, Vector3D playerPos, double step);
    boolean shouldAttack(Ennemy enemy, Vector3D playerPos);
}