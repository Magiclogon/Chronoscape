package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.wave.WaveListener;
import ma.ac.emi.gamelogic.wave.WaveNotifier;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class PickableManager implements WaveListener {
    private WaveNotifier waveNotifier;
    private List<Pickable> pickables;
    private World world;
    private Random random;
    private List<Pickable> pickableTypes;
    private double difficultyMultiplier;

    public PickableManager(World world) {
        this.world = world;
        this.pickables = new ArrayList<>();
        this.random = new Random();
        this.pickableTypes = new ArrayList<>();
        this.difficultyMultiplier = 1.0;
        initializePickableTypes();
    }

    private void initializePickableTypes() {
        // Base stats of pickables
        pickableTypes.add(new HpPickable(20.0, 0.70));
        pickableTypes.add(new MoneyPickable(10, 0.30));
    }

    public void addPickable(Pickable pickable) {
        pickables.add(pickable);
    }

    public void removePickable(Pickable pickable) {
        pickables.remove(pickable);
    }

    @Override
    public void subscribe(WaveNotifier waveNotifier) {
        this.waveNotifier = waveNotifier;
        waveNotifier.addListener(this);
    }

    @Override
    public void unsubscribe(WaveNotifier waveNotifier) {
        waveNotifier.removeListener(this);
    }

    @Override
    public void onNotify(List<Vector2D> spawnPoints) {
        System.out.println("Notified with " + spawnPoints.size() + " spawn points");

        for (Vector2D pos : spawnPoints) {
            Pickable pickable = createRandomPickable(pos);
            if (pickable != null) {
                addPickable(pickable);
            }
        }
    }

    private Pickable createRandomPickable(Vector2D position) {
        if (pickableTypes.isEmpty()) {
            return null;
        }

        // calculer proba totale
        double totalProbability = 0.0;
        for (Pickable type : pickableTypes) {
            totalProbability += type.getDropProbability();
        }

        // Roll for pickable
        double roll = random.nextDouble() * totalProbability;
        double cumulative = 0.0;

        for (Pickable type : pickableTypes) {
            cumulative += type.getDropProbability();
            if (roll <= cumulative) {
                Pickable pickable = type.createInstance();
                pickable.setPos(position);
                pickable.adjustForDifficulty(difficultyMultiplier);
                return pickable;
            }
        }
        return null;
    }
}
