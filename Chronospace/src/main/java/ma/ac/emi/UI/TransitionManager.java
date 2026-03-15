package ma.ac.emi.UI;

import javax.swing.*;
import java.awt.*;

/**
 * Manages the full menu-to-game transition sequence:
 *
 *   1. Fade to black
 *   2. Switch to LOADING card + start spinner
 *   3. Run loadingWork on a background thread (real game setup)
 *   4. Once done: fade back in showing loading screen
 *   5. Fade to black again
 *   6. Switch to GAME card
 *   7. Fade back in — player sees the game
 *
 * Usage in GameController:
 *   transitionManager.startWithLoading(
 *       this::restartGame,   // runs off the EDT — do your heavy work here
 *       this::showGame       // runs on EDT after fade completes
 *   );
 */
public class TransitionManager {

    private static final float FADE_STEP     = 0.04f;  // speed of each fade
    private static final int   TIMER_DELAY   = 16;     // ~60 fps
    private static final int   LOADING_LINGER = 800;  // ms loading screen stays fully visible

    private final Window       window;
    private final LoadingScreen loadingScreen;
    private final FadeOverlay  fadeOverlay;

    private boolean busy = false;

    public TransitionManager(Window window, LoadingScreen loadingScreen, FadeOverlay fadeOverlay) {
        this.window        = window;
        this.loadingScreen = loadingScreen;
        this.fadeOverlay   = fadeOverlay;
    }

    public boolean isBusy() { return busy; }

    /**
     * Simple transition: fade to black, run switchAction, fade back in.
     * Used for navigation that doesn't need a loading screen (e.g. loading → main menu).
     *
     * @param switchAction  Runs at peak black on the EDT — swap cards here.
     * @param onComplete    Runs on the EDT once fully faded back in. May be null.
     */
    public void fadeTo(Runnable switchAction, Runnable onComplete) {
        if (busy) return;
        busy = true;

        fadeOut(() -> {
            switchAction.run();
            fadeIn(() -> {
                busy = false;
                if (onComplete != null) onComplete.run();
            });
        });
    }

    /**
     * Kicks off the full transition.
     *
     * @param loadingWork  Heavy work to run off the EDT (world init, asset setup, etc.).
     *                     Must NOT touch Swing components directly.
     * @param onComplete   Called on the EDT once the game screen is fully visible.
     */
    public void startWithLoading(Runnable loadingWork, Runnable onComplete) {
        if (busy) return;
        busy = true;

        // Phase 1: fade to black
        fadeOut(() -> {
            // At peak black: show loading screen and start spinner
            window.navigateTo("LOADING");
            loadingScreen.startAnimation();

            // Phase 2: fade back in so player sees the loading screen
            fadeIn(() -> {
                // Phase 3: run heavy work off the EDT
                new Thread(() -> {
                    loadingWork.run();

                    // Minimum linger so the spinner is visible even on fast machines
                    try { Thread.sleep(LOADING_LINGER); } catch (InterruptedException ignored) {}

                    // Back on EDT for the visual finish
                    SwingUtilities.invokeLater(() -> {
                        loadingScreen.stopAnimation();

                        // Phase 4: fade to black again
                        fadeOut(() -> {
                            // Switch to game at peak black — player never sees the snap
                            onComplete.run();

                            // Phase 5: fade back in showing the game
                            fadeIn(() -> busy = false);
                        });
                    });
                }, "TransitionManager-LoadingThread").start();
            });
        });
    }

    // ── Simple fade helpers ───────────────────────────────────────────────

    /** Fades overlay from current alpha to 1, then calls onDone on the EDT. */
    private void fadeOut(Runnable onDone) {
        fade(true, onDone);
    }

    /** Fades overlay from current alpha to 0, then calls onDone on the EDT. */
    private void fadeIn(Runnable onDone) {
        fade(false, onDone);
    }

    private void fade(boolean toBlack, Runnable onDone) {
        Timer timer = new Timer(TIMER_DELAY, null);
        timer.addActionListener(e -> {
            float current = fadeOverlay.getAlpha();
            float next    = toBlack ? current + FADE_STEP : current - FADE_STEP;

            if (toBlack && next >= 1f) {
                fadeOverlay.setAlpha(1f);
                timer.stop();
                onDone.run();
            } else if (!toBlack && next <= 0f) {
                fadeOverlay.setAlpha(0f);
                timer.stop();
                onDone.run();
            } else {
                fadeOverlay.setAlpha(next);
            }
        });
        timer.start();
    }
}
