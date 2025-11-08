package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.difficulty.DifficultyObserver;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.wave.WaveListener;
import ma.ac.emi.gamelogic.wave.WaveNotifier;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.World;
import ma.ac.emi.world.WorldContext;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class PickableManager implements WaveListener, DifficultyObserver{
    private WaveNotifier waveNotifier;
    private List<Pickable> pickables;
    private WorldContext context;
    private Random random;
    private List<Pickable> pickableTypes;
    private DifficultyStrategy difficulty;

    public PickableManager(WorldContext context) {
        this.context = context;
 
        GameController.getInstance().addDifficultyObserver(this);
    }
    
    public void init() {
    	if(pickables != null) pickables.forEach(p -> GameController.getInstance().getGamePanel().removeDrawable(p));
        this.pickables = new ArrayList<>();
        this.random = new Random();
        this.pickableTypes = new ArrayList<>();
        initializePickableTypes();
        
    }

    private void initializePickableTypes() {
        // Base stats of pickables
        pickableTypes.add(new HpPickable(20.0, 0.70, false));
        pickableTypes.add(new MoneyPickable(10, 0.30, false));
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
    public void onNotify(List<Vector3D> spawnPoints) {
        System.out.println("Notified with " + spawnPoints.size() + " spawn points");

        for (Vector3D pos : spawnPoints) {
            Pickable pickable = createRandomPickable(pos);
            if (pickable != null) {
                addPickable(pickable);
            }
        }
    }

    private Pickable createRandomPickable(Vector3D position) {
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
                getDifficulty().adjustPickableDrop(pickable);
                return pickable;
            }
        }
        return null;
    }

	public void update(double step) {
		getPickables().forEach((p) -> p.update(step));
	}
	

	@Override
	public void refreshDifficulty(DifficultyStrategy difficutly) {
		this.difficulty = difficutly;
	}
}
