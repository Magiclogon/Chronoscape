package ma.ac.emi.gamelogic.ai;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.math.Vector3D;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RangedAIBehavior implements AIBehavior {

    private PathFinder pathfinder;
    private double rangeOptimal;
    private double rangeAttaque;
    private double rangeMin;

    private List<Vector3D> chemin = new ArrayList<>();
    private int idx = 0;

    private long lastTime = 0;
    private static final long DELAI_CALCUL = 250;

    private static final double FORCE = 0.10;
    private static final double TOLERANCE = 16.0;

    private Vector3D vitesseActuelle = new Vector3D(0, 0);

    public RangedAIBehavior(PathFinder pathfinder, double rangeOptimal, double rangeAttaque) {
        this.pathfinder = pathfinder;
        this.rangeOptimal = rangeOptimal;
        this.rangeAttaque = rangeAttaque;
        this.rangeMin = rangeOptimal * 0.7;
    }

    @Override
    public Vector3D calculateMovement(Ennemy ennemy, Vector3D posJoueur, double step) {
        double dist = ennemy.getPos().distance(posJoueur);
        Vector3D directionVoulu;

        //too close
        if (dist < rangeMin) {
            directionVoulu = ennemy.getPos().sub(posJoueur);

            chemin.clear();
        }

        // approach
        else if (dist > rangeOptimal) {
            long now = System.currentTimeMillis();
            boolean check = chemin.isEmpty() || (now - lastTime > DELAI_CALCUL);

            if (check) {
                calculerChemin(ennemy.getPos(), posJoueur);
                lastTime = now;
            }

            Vector3D cible = posJoueur;

            if (!chemin.isEmpty()) {
                while (idx < chemin.size() &&
                        ennemy.getPos().distance(chemin.get(idx)) < TOLERANCE) {
                    idx++;
                }
                if (idx < chemin.size()) {
                    cible = chemin.get(idx);
                }
            }
            directionVoulu = cible.sub(ennemy.getPos());
        }

        // keep distance
        else {
            Vector3D toPlayer = posJoueur.sub(ennemy.getPos());
            directionVoulu = new Vector3D(-toPlayer.getY(), toPlayer.getX());
            chemin.clear();
        }

        // Normalization
        if (directionVoulu.getX() != 0 || directionVoulu.getY() != 0) {
            directionVoulu = directionVoulu.normalize();
        }

        // Smoothing
        double smoothX = lissage(vitesseActuelle.getX(), directionVoulu.getX(), FORCE);
        double smoothY = lissage(vitesseActuelle.getY(), directionVoulu.getY(), FORCE);

        this.vitesseActuelle = new Vector3D(smoothX, smoothY);

        if (vitesseActuelle.getX() != 0 || vitesseActuelle.getY() != 0) {
            return vitesseActuelle.normalize();
        }

        return new Vector3D(0, 0);
    }

    @Override
    public boolean shouldAttack(Ennemy ennemy, Vector3D posJoueur) {
        double dist = ennemy.getPos().distance(posJoueur);
        return dist <= rangeAttaque && dist >= rangeMin;
    }

    private void calculerChemin(Vector3D start, Vector3D end) {
        List<Vector3D> res = pathfinder.findPath(start, end);
        if (res != null && !res.isEmpty()) {
            this.chemin = res;
            this.idx = 0;
        }
    }

    private double lissage(double start, double end, double val) {
        return start + val * (end - start);
    }
}