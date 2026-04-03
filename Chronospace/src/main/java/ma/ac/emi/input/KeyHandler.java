package ma.ac.emi.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;

import lombok.Getter;
import lombok.Setter;

/**
 * KeyHandler rewritten as a plain AWT KeyAdapter.
 *
 * GLCanvas is a heavyweight AWT component so Swing InputMap/ActionMap are
 * unavailable. Instead we register on the JFrame, which always receives key
 * events when the application window is focused — regardless of which child
 * component (GLCanvas or a Swing panel) currently holds focus.
 *
 * Call setupKeyBindings(window) once after the JFrame is created.
 * All other call-sites (consumeSwitchWeapon, consumeTogglePause, reset,
 * getters) are unchanged.
 */
@Getter
@Setter
public class KeyHandler extends KeyAdapter {

    private boolean left, right, up, down, switchWeapon, togglePause;

    private static KeyHandler instance;

    private KeyHandler() {
        reset();
    }

    public static KeyHandler getInstance() {
        if (instance == null) instance = new KeyHandler();
        return instance;
    }

    /**
     * Register this handler on the JFrame.
     * Safe to call multiple times — removes the old listener first to avoid
     * double-registration on game restart.
     */
    public void setupKeyBindings(JFrame frame) {
        frame.removeKeyListener(this);
        frame.addKeyListener(this);
        frame.setFocusable(true);
    }

    // ── KeyListener ───────────────────────────────────────────────────────

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A      -> setLeft(true);
            case KeyEvent.VK_D      -> setRight(true);
            case KeyEvent.VK_W      -> setUp(true);
            case KeyEvent.VK_S      -> setDown(true);
            case KeyEvent.VK_E      -> setSwitchWeapon(true);
            case KeyEvent.VK_ESCAPE -> setTogglePause(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> setLeft(false);
            case KeyEvent.VK_D -> setRight(false);
            case KeyEvent.VK_W -> setUp(false);
            case KeyEvent.VK_S -> setDown(false);
        }
    }

    // ── Consume helpers (unchanged) ───────────────────────────────────────

    public boolean consumeSwitchWeapon() {
        if (switchWeapon) { switchWeapon = false; return true; }
        return false;
    }

    public boolean consumeTogglePause() {
        if (togglePause) { togglePause = false; return true; }
        return false;
    }

    public void reset() {
        setLeft(false);
        setRight(false);
        setUp(false);
        setDown(false);
    }
}