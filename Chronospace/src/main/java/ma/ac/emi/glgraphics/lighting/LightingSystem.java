package ma.ac.emi.glgraphics.lighting;

import com.jogamp.opengl.GL3;

import ma.ac.emi.gamecontrol.GameObject;
import ma.ac.emi.glgraphics.Framebuffer;
import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.Mat4;
import ma.ac.emi.glgraphics.Shader;
import ma.ac.emi.math.Matrix4;

import java.util.ArrayList;
import java.util.List;

public class LightingSystem {
    private Framebuffer lightFBO;
    private Shader lightShader;
    private Shader compositeShader;
    private FullscreenQuad quad;
    private List<Light> lights = new ArrayList<>();
    
    // Ambient light (base lighting level)
    private float ambientR = 0.2f;
    private float ambientG = 0.2f;
    private float ambientB = 0.3f;
    
    public LightingSystem(GL3 gl, int width, int height) {
        lightFBO = new Framebuffer();
        lightFBO.init(gl, width, height, false);
        
        // Shader for rendering individual lights
        lightShader = Shader.load(gl, "post.vert", "light.frag");
        
        // Shader for combining scene + lights
        compositeShader = Shader.load(gl, "post.vert", "light_composite.frag");
        
        quad = new FullscreenQuad(gl);
    }
    
    public void addLight(Light light) {
        lights.add(light);
    }
    
    public void removeLight(Light light) {
        lights.remove(light);
    }
    
    public void clearLights() {
        lights.clear();
    }
    
    public void setAmbientLight(float r, float g, float b) {
        this.ambientR = r;
        this.ambientG = g;
        this.ambientB = b;
    }
    
 
    public void renderLights(GL3 gl, int screenWidth, int screenHeight, float[] viewMatrix, float[] projectionMatrix) {
        lightFBO.bind(gl);
        
        // Clear to ambient light color
        gl.glClearColor(ambientR, ambientG, ambientB, 1.0f);
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
        
        // Enable additive blending for light accumulation
        gl.glEnable(GL3.GL_BLEND);
        gl.glBlendFunc(GL3.GL_ONE, GL3.GL_ONE);
        
        lightShader.use(gl);
        lightShader.setVec2(gl, "uResolution", screenWidth, screenHeight);
        
        // Combine view and projection matrices
        float[] viewProj = Matrix4.multiply(projectionMatrix, viewMatrix);
        
        // Render each light
        for (Light light : lights) {
            if (!light.isEnabled()) continue;
            
            // Transform light position from world space to clip space
            float[] worldPos = {(float)light.pos.getX(), (float)(light.pos.getY()-light.pos.getZ()), 0, 1};
            float[] clipPos = Matrix4.multMatrixVector(viewProj, worldPos);
           
            // Convert from clip space [-1, 1] to screen space [0, width/height]
            float screenX = (clipPos[0] + 1.0f) * 0.5f * screenWidth;
            float screenY = (clipPos[1] + 1.0f) * 0.5f * screenHeight;
            
            // Transform radius to screen space (average of x and y scaling)
            float screenRadius = light.radius * viewMatrix[0];
            
            lightShader.setVec2(gl, "uLightPos", screenX, screenY);
            lightShader.setVec3(gl, "uLightColor", light.r, light.g, light.b);
            lightShader.setFloat(gl, "uLightRadius", screenRadius);
            lightShader.setFloat(gl, "uLightIntensity", light.intensity);
            lightShader.setInt(gl, "uLightType", light.type.ordinal());
            
            quad.draw(gl, 0);
        }
        
        // Restore normal blending
        gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
        
        lightFBO.unbind(gl);
    }
    
    /**
     * Get the light texture to use in post-processing
     */
    public int getLightTextureId() {
        return lightFBO.getTextureId();
    }
    
    public void resize(GL3 gl, int width, int height) {
        lightFBO.init(gl, width, height, false);
    }
    
    public void dispose(GL3 gl) {
        lightFBO.dispose(gl);
        lightShader.dispose(gl);
        compositeShader.dispose(gl);
        quad.dispose(gl);
    }

	public List<Light> getLights() {
		return lights;
	}
    
}