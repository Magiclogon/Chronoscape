package ma.ac.emi.sound;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private Map<String, Clip> soundMap = new HashMap<>();
    private Map<String, Long> pausePositions = new HashMap<>();

    public void load(String name, String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("Sound not found: " + path);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            soundMap.put(name, clip);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play(String name) {
        Clip clip = soundMap.get(name);
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void loop(String name) {
        Clip clip = soundMap.get(name);
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop(String name) {
        Clip clip = soundMap.get(name);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void pause(String name) {
        Clip clip = soundMap.get(name);
        if (clip != null && clip.isRunning()) {
            pausePositions.put(name, clip.getMicrosecondPosition());
            clip.stop();
        }
    }

    public void resume(String name) {
        Clip clip = soundMap.get(name);
        if (clip != null && pausePositions.containsKey(name)) {
            clip.setMicrosecondPosition(pausePositions.get(name));
            clip.start();
            if (name.contains("music") || name.contains("theme")) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public void stopAll() {
        for (Clip c : soundMap.values()) {
            if (c.isRunning()) c.stop();
        }
    }
}