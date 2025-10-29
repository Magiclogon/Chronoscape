package ma.ac.emi.gamelogic.wave;

public interface WaveListener {
    void subscribe(WaveNotifier waveNotifier);
    void unsubscribe(WaveNotifier waveNotifier);
    void onNotify();
}
