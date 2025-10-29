package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.wave.WaveListener;
import ma.ac.emi.gamelogic.wave.WaveNotifier;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.world.World;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PickableManager implements WaveListener {
    private WaveNotifier waveNotifier;
    private List<Pickable> pickables;
    private World world;

    public PickableManager(World world) {
        this.world = world;
        pickables = new ArrayList<>();
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
    public void onNotify() {
        List<Vector2D> state = waveNotifier.getState();

    }
}
