package ma.ac.emi.gamelogic.wave;

import ma.ac.emi.math.Vector3D;
import java.util.ArrayList;
import java.util.List;

public abstract class WaveNotifier {
    private final List<WaveListener> listeners;

    public WaveNotifier() {
        listeners = new ArrayList<>();
    }

    public void addListener(WaveListener listener) {
        listeners.add(listener);
    }

    public void removeListener(WaveListener listener) {
        listeners.remove(listener);
    }

    protected void notifyListeners(List<Vector3D> spawnPoints) {
    	System.out.println("Notifying pickables");
        for (WaveListener listener : listeners) {
            listener.onNotify(spawnPoints);
        }
    }
}
