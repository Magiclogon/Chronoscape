package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.difficulty.DifficultyObserver;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.player.Player; // Import Player
import ma.ac.emi.gamelogic.wave.WaveListener;
import ma.ac.emi.gamelogic.wave.WaveNotifier;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.WorldContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class PickableManager implements WaveListener, DifficultyObserver {
    private WaveNotifier waveNotifier;
    private List<Pickable> pickables;
    private WorldContext context;
    private Random random;
    private List<Pickable> pickablePrototypes;

    // current diff
    private DifficultyStrategy currentDifficulty;

    public PickableManager(WorldContext context) {
        this.context = context;
        // observer
        GameController.getInstance().addDifficultyObserver(this);
        this.currentDifficulty = GameController.getInstance().getDifficulty();
    }

    public void init() {
        if(pickables != null) pickables.forEach(p -> GameController.getInstance().removeDrawable(p));
        this.pickables = new ArrayList<>();
        this.random = new Random();
        this.pickablePrototypes = new ArrayList<>();
        initializePickableTypes();
    }

    private void initializePickableTypes() {
        // Base prototypes
        Pickable hpType = new HpPickable(20, 0.7);
        hpType.setDrawn(false);

        Pickable moneyType = new MoneyPickable(50, 0.3);
        moneyType.setDrawn(false);

        pickablePrototypes.add(hpType);
        pickablePrototypes.add(moneyType);
    }

    public void addPickable(Pickable pickable) {
        pickables.add(pickable);
        GameController.getInstance().addDrawable(pickable);
    }

    public void removePickable(Pickable pickable) {
        pickables.remove(pickable);
        GameController.getInstance().removeDrawable(pickable);
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
    public void onNotify(List<Vector3D> spawnPoints) {
        System.out.println("Drop Chance: Processing " + spawnPoints.size() + " points");

        for (Vector3D pos : spawnPoints) {
            // Base Drop Chance
            double dropChance = (currentDifficulty != null) ? currentDifficulty.getPickableDropRate() : 0.5;

            // luck drop chance multiplier
            dropChance *= (1.0 + Player.getInstance().getLuck() * 0.01);

            if (random.nextDouble() <= dropChance) {
                Pickable pickable = createRandomPickable(pos);
                if (pickable != null) {
                    addPickable(pickable);
                }
            }
        }
    }

    private Pickable createRandomPickable(Vector3D position) {
        if (pickablePrototypes.isEmpty()) return null;

        // Relative weight selection
        double totalWeight = pickablePrototypes.stream().mapToDouble(Pickable::getDropProbability).sum();
        double roll = random.nextDouble() * totalWeight;
        double cumulative = 0.0;

        for (Pickable prototype : pickablePrototypes) {
            cumulative += prototype.getDropProbability();
            if (roll <= cumulative) {
                // Create new instance
                Pickable instance = prototype.createInstance();
                instance.setPos(position);

                // difficulty multiplier
                if (currentDifficulty != null) {
                    instance.applyValueMultiplier(currentDifficulty.getPickableValueMultiplier());
                }

                // luck multiplier
                Player player = Player.getInstance();
                if (player != null) {
                    double boostedValue = player.applyLuckToValue(instance.getValue());
                    instance.setValue(boostedValue);
                }

                return instance;
            }
        }
        return null;
    }

    public void update(double step) {
        new ArrayList<>(getPickables()).forEach((p) -> p.update(step));
    }

    @Override
    public void refreshDifficulty(DifficultyStrategy difficulty) {
        this.currentDifficulty = difficulty;
    }

    public List<Pickable> getPickables(){
        if (this.pickables == null) {
            return Collections.emptyList();
        }
        return this.pickables;
    }
}