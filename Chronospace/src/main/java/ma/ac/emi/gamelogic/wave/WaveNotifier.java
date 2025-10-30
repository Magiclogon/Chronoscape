package ma.ac.emi.gamelogic.wave;

import ma.ac.emi.math.Vector2D;

import java.net.http.WebSocket;
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

    public void notifyListeners() {
        for (WaveListener listener : listeners) {
            listener.onNotify();
        }
    }

    public abstract List<Vector2D> getState();
}
