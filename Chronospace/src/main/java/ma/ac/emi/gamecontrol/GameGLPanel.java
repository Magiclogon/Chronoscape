package ma.ac.emi.gamecontrol;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;

import ma.ac.emi.camera.Camera;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;

public class GameGLPanel extends GLJPanel {

    private final GameRenderer renderer;

    public GameGLPanel() {
        GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL3));
        setRequestedGLCapabilities(caps);
        
        this.renderer = new GameRenderer();
        addGLEventListener(renderer);

        // keep Swing input
        addMouseListener(MouseHandler.getInstance());
        addMouseMotionListener(MouseHandler.getInstance());
        addMouseWheelListener(MouseHandler.getInstance());
        KeyHandler.getInstance().setupKeyBindings(this);
        
    }
    
    public void setCamera(Camera camera) {
    	this.renderer.setCamera(camera);
    }

    public GameRenderer getRenderer() {
        return renderer;
    }
    
    public void update(double step) {
    	getRenderer().update();
    }

    public void initParticleCache() {
    	if(getGL() == null) return;
    	GL3 gl = this.getGL().getGL3();
    	this.getRenderer().initParticleCache(gl);
    }

}
