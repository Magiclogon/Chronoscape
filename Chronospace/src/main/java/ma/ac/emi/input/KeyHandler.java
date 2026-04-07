package ma.ac.emi.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import ma.ac.emi.input.InputConfig;
import ma.ac.emi.math.Vector3D;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class KeyHandler extends KeyAdapter {

    private boolean left, right, up, down, switchWeapon, togglePause;

    private boolean aimUp, aimDown, aimLeft, aimRight;
    
    private Vector3D aimDirection;

    private static KeyHandler instance;

    private KeyHandler() {
        reset();
    }

    public static KeyHandler getInstance() {
        if (instance == null) instance = new KeyHandler();
        return instance;
    }

    public void setupKeyBindings(JFrame frame) {
        frame.removeKeyListener(this);
        frame.addKeyListener(this);
        frame.setFocusable(true);
    }


    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        InputConfig cfg = InputConfig.getInstance();
        if      (k == cfg.moveLeft)     setLeft(true);
        else if (k == cfg.moveRight)    setRight(true);
        else if (k == cfg.moveUp)       setUp(true);
        else if (k == cfg.moveDown)     setDown(true);
        else if (k == cfg.switchWeapon) setSwitchWeapon(true);
        else if (k == cfg.pause)        setTogglePause(true);
        if      (k == cfg.aimUp)    setAimUp(true);
        else if (k == cfg.aimDown)  setAimDown(true);
        else if (k == cfg.aimLeft)  setAimLeft(true);
        else if (k == cfg.aimRight) setAimRight(true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        InputConfig cfg = InputConfig.getInstance();
        if      (k == cfg.moveLeft)  setLeft(false);
        else if (k == cfg.moveRight) setRight(false);
        else if (k == cfg.moveUp)    setUp(false);
        else if (k == cfg.moveDown)  setDown(false);
        if      (k == cfg.aimUp)    setAimUp(false);
        else if (k == cfg.aimDown)  setAimDown(false);
        else if (k == cfg.aimLeft)  setAimLeft(false);
        else if (k == cfg.aimRight) setAimRight(false);
    }


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
        setAimUp(false);
        setAimDown(false);
        setAimLeft(false);
        setAimRight(false);
        
        setAimDirection(new Vector3D(1,0));
    }

    
    public Vector3D getAimDirection() {
        double dx = 0, dy = 0;
        if (aimLeft)  dx -= 1;
        if (aimRight) dx += 1;
        if (aimUp)    dy -= 1;
        if (aimDown)  dy += 1;
        if (dx != 0 || dy != 0) {
            double len = Math.sqrt(dx * dx + dy * dy);
            aimDirection = new Vector3D(dx / len, dy / len, 0);
        }

        return aimDirection;
    }
}