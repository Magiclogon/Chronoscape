package ma.ac.emi.input;

import ma.ac.emi.math.Vector3D;


public class AimController {

    private static AimController instance;
    public static AimController getInstance() {
        if (instance == null) instance = new AimController();
        return instance;
    }

    /** World units ahead of the player the keyboard aim reticle sits. */
    private static final double AIM_DISTANCE = 150;

    private Vector3D target = new Vector3D(0, 0, 0);

    private AimController() {}

    public void update(Vector3D playerPos) {
        if (InputConfig.getInstance().keyboardAimMode) {
            updateKeyboardAim(playerPos);
        } else {
            updateMouseAim();
        }
    }

    private void updateMouseAim() {
        Vector3D mouse = MouseHandler.getInstance().getMouseWorldPos();
        if (mouse != null) target = mouse;
    }

    private void updateKeyboardAim(Vector3D playerPos) {
        Vector3D dir = KeyHandler.getInstance().getAimDirection();

        if (dir.getX() == 0 && dir.getY() == 0) {
            return;
        }

        target = new Vector3D(
                playerPos.getX() + dir.getX() * AIM_DISTANCE,
                playerPos.getY() + dir.getY() * AIM_DISTANCE,
                playerPos.getZ()
        );
    }


    public Vector3D getTarget() {
        return target;
    }

    public boolean isFiring() {
        if (InputConfig.getInstance().keyboardAimMode) {
            KeyHandler kh = KeyHandler.getInstance();
            return kh.isAimUp() || kh.isAimDown() || kh.isAimLeft() || kh.isAimRight();
        }
        return MouseHandler.getInstance().isMouseDown();
    }
}