package ma.ac.emi.sound;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ─────────────────────────────────────────────────────────────────────────────
 * SoundManager  –  full-featured audio system for the roguelike
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * Features
 * ────────
 *  • Category-based volume control  (MUSIC · SFX · UI)
 *  • Clip pool  – rapid-fire weapons re-use pre-opened Clip instances instead
 *    of allocating new ones on every shot, eliminating GC pressure and
 *    the "missing shots" artefact on high attack-speed weapons.
 *  • Looping streams  – long sounds (flamethrower, music) use SourceDataLine
 *    so they never get cut off mid-buffer.
 *  • Pitch / speed shifting  – accomplished by changing the sample-rate at
 *    which the hardware consumes PCM data; no quality loss.
 *  • Per-sound cooldown  – prevents ear-rape on extremely fast weapons.
 *  • Master mute + individual category mute.
 *  • Clean stop-all on pause / game-over.
 *
 * Usage (unchanged call-sites)
 * ────────────────────────────
 *   soundManager.play("ak47");                      // one-shot SFX
 *   soundManager.play("ak47", false);               // don't restart if already playing
 *   soundManager.playLooped("flamethrower_loop");  // seamless loop
 *   soundManager.stopLoop("flamethrower_loop");    // stop a running loop
 *   soundManager.loop("main_menu_music");           // music helper (alias)
 *   soundManager.stop("ak47");
 *   soundManager.stopAll();
 *   soundManager.setVolume(Category.SFX, 0.8f);    // 0..1
 *   soundManager.setMasterVolume(0.5f);
 */
public class SoundManager {

    // ── Categories ────────────────────────────────────────────────────────────
    public enum Category { MUSIC, SFX, UI }

    // ── Pool size per sound ───────────────────────────────────────────────────
    private static final int POOL_SIZE = 8;

    // ── Minimum ms between two plays of the same one-shot sound ──────────────
    private static final long DEFAULT_COOLDOWN_MS = 40;

    // ─────────────────────────────────────────────────────────────────────────
    // Internal record per loaded sound
    // ─────────────────────────────────────────────────────────────────────────
    private static final class SoundEntry {
        final byte[]       pcmData;
        final AudioFormat  format;
        final double       durationSec;
        final Category     category;
        final Queue<Clip>  pool = new ArrayDeque<>(POOL_SIZE);
        volatile long      lastPlayedMs = 0;
        volatile long      cooldownMs   = DEFAULT_COOLDOWN_MS;

        SoundEntry(byte[] pcmData, AudioFormat format, double durationSec, Category category) {
            this.pcmData     = pcmData;
            this.format      = format;
            this.durationSec = durationSec;
            this.category    = category;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal record for a running loop stream
    // ─────────────────────────────────────────────────────────────────────────
    private static final class LoopStream {
        final SourceDataLine line;
        final Thread         thread;
        final AtomicBoolean  running = new AtomicBoolean(true);

        LoopStream(SourceDataLine line, Thread thread) {
            this.line   = line;
            this.thread = thread;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // State
    // ─────────────────────────────────────────────────────────────────────────
    private final Map<String, SoundEntry>  entries     = new ConcurrentHashMap<>();
    private final Map<String, LoopStream>  loops       = new ConcurrentHashMap<>();

    private final Map<Category, Float>     catVolume   = new EnumMap<>(Category.class);
    private final Map<Category, Boolean>   catMuted    = new EnumMap<>(Category.class);
    private volatile float masterVolume = 1.0f;
    private volatile boolean masterMuted = false;

    private final ExecutorService executor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "SoundManager-worker");
        t.setDaemon(true);
        return t;
    });

    // ─────────────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────────────
    public SoundManager() {
        for (Category c : Category.values()) {
            catVolume.put(c, 1.0f);
            catMuted.put(c, false);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Loading
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Load a sound with an explicit category.
     *
     * @param name     logical name used in play/stop calls
     * @param path     classpath resource path  (e.g. "/sounds/ak47.wav")
     * @param category MUSIC · SFX · UI
     */
    public void load(String name, String path, Category category) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("[SoundManager] Not found: " + path);
                return;
            }

            AudioInputStream raw = AudioSystem.getAudioInputStream(url);
            // Decode to PCM if needed (e.g. MP3 via SPI)
            AudioFormat srcFmt = raw.getFormat();
            AudioFormat pcmFmt = toPCM(srcFmt);
            AudioInputStream pcmStream = AudioSystem.getAudioInputStream(pcmFmt, raw);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = pcmStream.read(buf)) != -1) baos.write(buf, 0, n);
            pcmStream.close();

            byte[] data       = baos.toByteArray();
            long   frameCount = data.length / pcmFmt.getFrameSize();
            double duration   = frameCount / pcmFmt.getFrameRate();

            SoundEntry entry = new SoundEntry(data, pcmFmt, duration, category);

            // Pre-fill the clip pool
            for (int i = 0; i < POOL_SIZE; i++) {
                Clip clip = tryOpenClip(pcmFmt, data);
                if (clip != null) entry.pool.offer(clip);
            }

            entries.put(name, entry);

        } catch (Exception e) {
            System.err.println("[SoundManager] Failed to load '" + name + "': " + e.getMessage());
        }
    }

    /** Convenience – assumes Category.SFX for backward compatibility. */
    public void load(String name, String path) {
        // Auto-detect category from name convention
        Category cat = Category.SFX;
        if (name.contains("music") || name.contains("theme")) cat = Category.MUSIC;
        else if (name.contains("menu") || name.contains("hover") || name.contains("select")) cat = Category.UI;
        load(name, path, cat);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // One-shot playback
    // ─────────────────────────────────────────────────────────────────────────

    /** Play once, restarting if already playing. */
    public void play(String name) {
        play(name, true, 1.0f);
    }

    /** Play once; restart flag controls behaviour when already running. */
    public void play(String name, boolean restart) {
        play(name, restart, 1.0f);
    }

    /**
     * Play a one-shot sound with optional speed/pitch shift.
     *
     * @param name        logical sound name
     * @param restart     if true, stop the current instance and restart from beginning
     * @param speedFactor &gt;1 = higher pitch / faster;  &lt;1 = lower pitch / slower;  1 = normal
     */
    public void play(String name, boolean restart, float speedFactor) {
        SoundEntry entry = entries.get(name);
        if (entry == null) return;
        if (masterMuted || catMuted.getOrDefault(entry.category, false)) return;

        // Cooldown guard
        long now = System.currentTimeMillis();
        if (now - entry.lastPlayedMs < entry.cooldownMs) return;
        entry.lastPlayedMs = now;

        float effectiveGain = masterVolume * catVolume.getOrDefault(entry.category, 1.0f);

        if (Math.abs(speedFactor - 1.0f) < 0.04f) {
            // Fast path – use clip pool (no pitch shift needed)
            playFromPool(entry, restart, effectiveGain);
        } else {
            // Pitch-shift path – use SourceDataLine with modified sample-rate
            executor.submit(() -> playWithPitch(entry, speedFactor, effectiveGain));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Looping playback  (music, flamethrower, …)
    // ─────────────────────────────────────────────────────────────────────────

    /** Start a seamless loop.  Safe to call if already running (no-op). */
    public void playLooped(String name) {
        playLooped(name, 1.0f);
    }

    public void playLooped(String name, float speedFactor) {
        if (loops.containsKey(name)) return; // already looping
        SoundEntry entry = entries.get(name);
        if (entry == null) return;
        if (masterMuted || catMuted.getOrDefault(entry.category, false)) return;

        float effectiveGain = masterVolume * catVolume.getOrDefault(entry.category, 1.0f);
        float targetRate    = entry.format.getSampleRate() * speedFactor;

        executor.submit(() -> {
            try {
                SourceDataLine line = openLine(entry.format, targetRate);
                if (line == null) return;
                applyGain(line, effectiveGain);
                line.start();

                AtomicBoolean running = new AtomicBoolean(true);
                Thread t = Thread.currentThread();

                LoopStream ls = new LoopStream(line, t);
                ls.running.set(true);
                loops.put(name, ls);

                while (ls.running.get()) {
                    line.write(entry.pcmData, 0, entry.pcmData.length);
                    if (!ls.running.get()) break;
                }

                line.drain();
                line.stop();
                line.close();
                loops.remove(name);
            } catch (Exception e) {
                loops.remove(name);
            }
        });
    }

    /** Stop a running loop by name. */
    public void stopLoop(String name) {
        LoopStream ls = loops.remove(name);
        if (ls != null) {
            ls.running.set(false);
            ls.line.stop();
            ls.line.flush();
            ls.line.close();
        }
    }

    /** Backward-compat alias – loops the sound continuously (for music). */
    public void loop(String name) {
        playLooped(name, 1.0f);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Stop
    // ─────────────────────────────────────────────────────────────────────────

    public void stop(String name) {
        SoundEntry entry = entries.get(name);
        if (entry != null) {
            for (Clip c : entry.pool) {
                if (c.isRunning()) c.stop();
            }
        }
        stopLoop(name);
    }

    public void stopAll() {
        for (Map.Entry<String, SoundEntry> e : entries.entrySet()) {
            for (Clip c : e.getValue().pool) {
                if (c.isRunning()) { c.stop(); c.setFramePosition(0); }
            }
        }
        for (String name : new ArrayList<>(loops.keySet())) stopLoop(name);
    }

    /** Stop all sounds in a specific category (e.g. stop music, keep SFX). */
    public void stopCategory(Category category) {
        for (Map.Entry<String, SoundEntry> e : entries.entrySet()) {
            if (e.getValue().category == category) stop(e.getKey());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Pause / Resume  (for game-pause screens)
    // ─────────────────────────────────────────────────────────────────────────

    private final Map<String, Long> pausePositions = new ConcurrentHashMap<>();

    public void pause(String name) {
        SoundEntry entry = entries.get(name);
        if (entry != null) {
            for (Clip c : entry.pool) {
                if (c.isRunning()) {
                    pausePositions.put(name, c.getMicrosecondPosition());
                    c.stop();
                }
            }
        }
        // For loops, just signal stop; resumeLoop must be called explicitly
    }

    public void resumeClip(String name) {
        SoundEntry entry = entries.get(name);
        if (entry == null) return;
        Long pos = pausePositions.remove(name);
        if (pos == null) return;
        Clip clip = entry.pool.peek();
        if (clip != null) {
            clip.setMicrosecondPosition(pos);
            clip.start();
            if (entry.category == Category.MUSIC) clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Volume control
    // ─────────────────────────────────────────────────────────────────────────

    public void setMasterVolume(float v) {
        masterVolume = clamp(v);
    }

    public void setVolume(Category category, float v) {
        catVolume.put(category, clamp(v));
    }

    public void setMasterMuted(boolean muted) { masterMuted = muted; }
    public void setCategoryMuted(Category c, boolean muted) { catMuted.put(c, muted); }

    public float getMasterVolume()               { return masterVolume; }
    public float getCategoryVolume(Category c)   { return catVolume.getOrDefault(c, 1.0f); }

    // ─────────────────────────────────────────────────────────────────────────
    // Per-sound tuning helpers  (call after load())
    // ─────────────────────────────────────────────────────────────────────────

    /** Override the per-sound cooldown (ms between successive plays). */
    public void setCooldown(String name, long ms) {
        SoundEntry e = entries.get(name);
        if (e != null) e.cooldownMs = ms;
    }

    public double getDuration(String name) {
        SoundEntry e = entries.get(name);
        return e == null ? 0.0 : e.durationSec;
    }


    private void playFromPool(SoundEntry entry, boolean restart, float gain) {
        // Find a free clip from the pool, or grab the oldest running one
        Clip chosen = null;
        for (Clip c : entry.pool) {
            if (!c.isRunning()) { chosen = c; break; }
        }
        if (chosen == null) {
            // All clips busy – take the first (oldest) one; acceptable for rapid-fire
            if (restart) chosen = entry.pool.peek();
            else return;
        }

        final Clip clip = chosen;
        executor.submit(() -> {
            clip.stop();
            clip.setFramePosition(0);
            applyGain(clip, gain);
            clip.start();
        });
    }

    private void playWithPitch(SoundEntry entry, float speedFactor, float gain) {
        float targetRate = entry.format.getSampleRate() * speedFactor;
        try {
            SourceDataLine line = openLine(entry.format, targetRate);
            if (line == null) {
                // Fallback – play at normal speed
                playFromPool(entry, true, gain);
                return;
            }
            applyGain(line, gain);
            line.start();
            line.write(entry.pcmData, 0, entry.pcmData.length);
            line.drain();
            line.stop();
            line.close();
        } catch (Exception e) {
            playFromPool(entry, true, gain);
        }
    }

    private Clip tryOpenClip(AudioFormat fmt, byte[] data) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(fmt, data, 0, data.length);
            return clip;
        } catch (Exception e) {
            return null;
        }
    }

    private SourceDataLine openLine(AudioFormat baseFmt, float sampleRate) {
        // Try exact rate first, then common hardware rates downward
        float[] candidates = { sampleRate, 192_000, 96_000, 48_000, 44_100 };
        for (float rate : candidates) {
            if (rate > sampleRate * 1.01f) continue; // don't go faster than requested
            AudioFormat fmt = new AudioFormat(
                    baseFmt.getEncoding(), rate,
                    baseFmt.getSampleSizeInBits(), baseFmt.getChannels(),
                    baseFmt.getFrameSize(), rate, baseFmt.isBigEndian()
            );
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
            if (AudioSystem.isLineSupported(info)) {
                try {
                    SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                    line.open(fmt);
                    return line;
                } catch (LineUnavailableException | IllegalArgumentException ignored) {}
            }
        }
        return null;
    }

    /** Apply a 0..1 gain to a Line that supports MASTER_GAIN. */
    private void applyGain(Line line, float gain) {
        if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl ctrl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            // Convert linear 0..1 → dB
            float db = gain <= 0f ? ctrl.getMinimum() : 20f * (float) Math.log10(gain);
            ctrl.setValue(Math.max(ctrl.getMinimum(), Math.min(ctrl.getMaximum(), db)));
        }
    }

    private static AudioFormat toPCM(AudioFormat src) {
        return new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                src.getSampleRate() > 0 ? src.getSampleRate() : 44_100f,
                16,
                src.getChannels() > 0 ? src.getChannels() : 2,
                src.getChannels() > 0 ? src.getChannels() * 2 : 4,
                src.getSampleRate() > 0 ? src.getSampleRate() : 44_100f,
                false
        );
    }

    private static float clamp(float v) { return Math.max(0f, Math.min(1f, v)); }
}