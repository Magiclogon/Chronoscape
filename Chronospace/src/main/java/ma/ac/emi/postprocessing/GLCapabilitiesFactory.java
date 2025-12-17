package ma.ac.emi.postprocessing;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

public class GLCapabilitiesFactory {
    
    public static GLCapabilities createCapabilities() {
        GLProfile profile = GLProfile.get(GLProfile.GL4bc);
        GLCapabilities caps = new GLCapabilities(profile);
        caps.setDoubleBuffered(true);
        return caps;
    }
}