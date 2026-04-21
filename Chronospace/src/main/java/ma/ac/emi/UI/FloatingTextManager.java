package ma.ac.emi.UI;

import ma.ac.emi.math.Vector3D;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class FloatingTextManager {

    private static FloatingTextManager instance;
    public static FloatingTextManager getInstance() {
        if (instance == null) instance = new FloatingTextManager();
        return instance;
    }

    private final List<FloatingText> active = new CopyOnWriteArrayList<>();

    private FloatingTextManager() {}

    public void spawn(String text, FloatingText.Preset preset, Vector3D worldPos) {
        // Slight random horizontal scatter so stacked texts don't overlap
        double jitter = (Math.random() - 0.5) * 12;
        active.add(new FloatingText(text, preset, worldPos.add(new Vector3D(jitter, 0, 0))));
    }

    public void update(double step) {
        for (FloatingText ft : active) ft.update(step);
        active.removeIf(FloatingText::isDead);
    }
    
    public void clearFloatingText() {
    	active.clear();
    }

    public List<FloatingText> getActive() { return active; }
}