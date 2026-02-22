package ma.ac.emi.sound;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoundManager {

    private Map<String, Clip> soundMap = new HashMap<>();
    private Map<String, byte[]> soundDataMap = new HashMap<>();
    private Map<String, AudioFormat> soundFormatMap = new HashMap<>();
    private Map<String, Double> soundDurationMap = new HashMap<>();
    private Map<String, SourceDataLine> activeLines = new ConcurrentHashMap<>();
    private Map<String, Long> pausePositions = new HashMap<>();

    public void load(String name, String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("Sound not found: " + path);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            AudioFormat format = audioIn.getFormat();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n;
            while ((n = audioIn.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
            byte[] data = baos.toByteArray();
            audioIn.close();

            soundDataMap.put(name, data);
            soundFormatMap.put(name, format);

            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            soundMap.put(name, clip);
            soundDurationMap.put(name, clip.getMicrosecondLength() / 1_000_000.0);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play(String name) {
        play(name, true);
    }

    public void play(String name, boolean restart) {
        play(name, restart, 1.0f);
    }

    public void play(String name, boolean restart, float speedFactor) {
        if (speedFactor <= 1.05f) {
            Clip clip = soundMap.get(name);
            if (clip != null) {
                if (clip.isRunning()) {
                    if (restart) {
                        clip.stop();
                    } else {
                        return;
                    }
                }
                clip.setFramePosition(0);
                clip.start();
            }
            return;
        }

        byte[] data = soundDataMap.get(name);
        AudioFormat format = soundFormatMap.get(name);
        if (data == null || format == null) return;

        stop(name);

        float targetRate = format.getSampleRate() * speedFactor;

        try {
            SourceDataLine line = getSupportedLine(format, targetRate);
            if (line == null) {
                // Fallback to Clip if speed up is not possible
                play(name, restart, 1.0f);
                return;
            }

            line.start();
            activeLines.put(name, line);

            new Thread(() -> {
                try {
                    line.write(data, 0, data.length);
                    line.drain();
                } catch (Exception e) {
                    // Ignore errors during playback
                } finally {
                    line.stop();
                    line.close();
                    activeLines.remove(name, line);
                }
            }).start();

        } catch (Exception e) {
            // Last resort fallback
            play(name, restart, 1.0f);
        }
    }

    private SourceDataLine getSupportedLine(AudioFormat format, float targetRate) {
        // Common supported sample rates to try if targetRate fails
        float[] ratesToTry = {targetRate, 192000, 96000, 48000, 44100};
        for (float rate : ratesToTry) {
            if (rate > targetRate && rate != targetRate) continue;
            
            AudioFormat speedFormat = new AudioFormat(
                    format.getEncoding(),
                    rate,
                    format.getSampleSizeInBits(),
                    format.getChannels(),
                    format.getFrameSize(),
                    rate,
                    format.isBigEndian()
            );

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, speedFormat);
            if (AudioSystem.isLineSupported(info)) {
                try {
                    SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                    line.open(speedFormat);
                    return line;
                } catch (LineUnavailableException | IllegalArgumentException e) {
                    // Try next rate
                }
            }
        }
        return null;
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
        SourceDataLine line = activeLines.get(name);
        if (line != null) {
            line.stop();
            line.flush();
            line.close();
            activeLines.remove(name);
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

    public double getDuration(String name) {
        return soundDurationMap.getOrDefault(name, 0.0);
    }

    public void stopAll() {
        for (Clip c : soundMap.values()) {
            if (c.isRunning()) c.stop();
        }
        for (SourceDataLine line : activeLines.values()) {
            line.stop();
            line.flush();
            line.close();
        }
        activeLines.clear();
    }
}