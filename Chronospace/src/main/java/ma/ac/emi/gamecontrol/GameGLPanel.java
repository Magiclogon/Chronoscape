package ma.ac.emi.gamecontrol;

import java.awt.GraphicsConfiguration;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;

public class GameGLPanel extends GLCanvas {

    private final GameRenderer renderer;

    public GameGLPanel() {
        super(createCapabilities());
        this.renderer = new GameRenderer();
        addGLEventListener(renderer);

        addMouseListener(MouseHandler.getInstance());
        addMouseMotionListener(MouseHandler.getInstance());
        addMouseWheelListener(MouseHandler.getInstance());
        addKeyListener(KeyHandler.getInstance());

        setFocusable(true);
    }

    /**
     * addNotify() fires when the component is connected to a native peer
     * and placed on a real screen — the earliest reliable moment to read
     * the actual DPI scaling of the monitor it lives on.
     */
    @Override
    public void addNotify() {
        super.addNotify(); // must come first — realizes the native peer

        GraphicsConfiguration gc = getGraphicsConfiguration();
        if (gc != null) {
            float scaleX = (float) gc.getDefaultTransform().getScaleX();
            renderer.setDpiScale(scaleX);
        }
    }

    private static GLCapabilities createCapabilities() {
        GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL3));
        caps.setDoubleBuffered(true);
        caps.setHardwareAccelerated(true);
        return caps;
    }

    public void setCamera(Camera camera) { this.renderer.setCamera(camera); }
    public GameRenderer getRenderer()    { return renderer; }

    public void update(double step) {
        getRenderer().update();
    }

    public void initParticleCache() {
        GL3 gl = this.getGL().getGL3();
        this.getRenderer().initParticleCache(gl);
    }

	public void resetFade() {
		this.renderer.resetFade();
	}
}