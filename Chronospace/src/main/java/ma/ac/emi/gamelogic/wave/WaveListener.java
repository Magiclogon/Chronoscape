package ma.ac.emi.gamelogic.wave;

import ma.ac.emi.math.Vector3D;
import java.util.List;

public interface WaveListener {
    void subscribe(WaveNotifier waveNotifier);
    void unsubscribe(WaveNotifier waveNotifier);
    void onNotify(List<Vector3D> spawnPoints);
}