package ma.ac.emi.gamecontrol;

import com.jogamp.opengl.*;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.gamelogic.particle.ParticleAnimationCache;
import ma.ac.emi.gamelogic.particle.ParticleBatchMarker;
import ma.ac.emi.gamelogic.particle.ParticleSystem;
import ma.ac.emi.glgraphics.Framebuffer;
import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.Mat4;
import ma.ac.emi.glgraphics.Shader;
import ma.ac.emi.glgraphics.lighting.Light;
import ma.ac.emi.glgraphics.lighting.LightingSystem;
import ma.ac.emi.glgraphics.post.BloomCombineEffect;
import ma.ac.emi.glgraphics.post.BloomExtractEffect;
import ma.ac.emi.glgraphics.post.BlurEffect;
import ma.ac.emi.glgraphics.post.GammaEffect;
import ma.ac.emi.glgraphics.post.GammaToLinearEffect;
import ma.ac.emi.glgraphics.post.LightCompositeEffect;
import ma.ac.emi.glgraphics.post.PostProcessor;
import ma.ac.emi.glgraphics.post.SnapshotEffect;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GameRenderer implements GLEventListener {

    private Camera camera;
    private final List<GameObject> drawables = Collections.synchronizedList(new ArrayList<>());
    private GLGraphics glGraphics;
    private int width, height;

	private PostProcessor postProcessor;
	
	private Color bg;
	private LightingSystem lightingSystem;

    public void setCamera(Camera camera) { this.camera = camera; }

    public void addDrawable(GameObject o) {
        o.setDrawn(true);
        if (!drawables.contains(o)) drawables.add(o);
    }

    public void removeDrawable(GameObject o) { o.setDrawn(false); }

    public void update() {
        drawables.removeIf(d -> !d.isDrawn());
        Collections.sort(drawables);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        
        int width = drawable.getSurfaceWidth();
        int height = drawable.getSurfaceHeight();

        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        glGraphics = new GLGraphics(gl);
        
        ParticleAnimationCache.initializeAllTextures(gl);
        
        // Initialize lighting system
        lightingSystem = new LightingSystem(gl, width, height);
        lightingSystem.setAmbientLight(0.5f, 0.5f, 0.85f); // Dark blue ambient
        
        // Initialize post processor
        postProcessor = new PostProcessor(gl, width, height);
        postProcessor.setBloomDownscale(4);
        
        postProcessor.addEffect(new GammaToLinearEffect(gl));
        
        // Add lighting composite BEFORE bloom
        postProcessor.addEffect(new LightCompositeEffect(gl, lightingSystem.getLightTextureId()));
        
        // Bloom effects
        postProcessor.addEffect(new SnapshotEffect(postProcessor));
        postProcessor.addEffect(new BloomExtractEffect(gl, 0.05f));
        postProcessor.addEffect(new BlurEffect(gl, true, 1.5f));
        postProcessor.addEffect(new BlurEffect(gl, false, 1.5f));
        postProcessor.addEffect(new BloomCombineEffect(gl, postProcessor.getSnapshotTextureId()));
        
        postProcessor.addEffect(new GammaEffect(gl));
        
        bg = GameController.getInstance().getWorldManager().getCurrentWorld().getVoidColor();
    }

//    @Override
//    public void display(GLAutoDrawable drawable) {
//        GL3 gl = drawable.getGL().getGL3();
//
//        // Pass 1: Render scene
//        postProcessor.capture(gl);
//            gl.glClearColor(bg.getRed()/255f, bg.getGreen()/255f, bg.getBlue()/255f, bg.getAlpha()/255f);
//            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
//    
//            List<GameObject> snapshot;
//            synchronized (drawables) {
//                snapshot = new ArrayList<>(drawables);
//            }
//    
//            glGraphics.setCamera(camera);
//            glGraphics.beginFrame(gl, width, height);
//            for (GameObject o : snapshot) {
//                o.drawGL(gl, glGraphics);
//            }
//            
//            ParticleSystem particleSystem = GameController.getInstance().getParticleSystem();
//            particleSystem.renderBatchedOptimized(gl, glGraphics, 
//                (float)camera.getPos().getX(), 
//                (float)camera.getPos().getY(),
//                (float) camera.getWidth(),
//                (float) camera.getHeight()
//            );
//            glGraphics.endFrame(gl);
//        postProcessor.release(gl);
//        
//        // Pass 2: Render lights
//        lightingSystem.clearLights();
//        for (GameObject obj : snapshot) {
//            if (obj.getLight() != null) {
//                Light light = obj.getLight();
//                light.setPosition((float) obj.getPos().getX(), (float) obj.getPos().getY());
//                lightingSystem.addLight(light);
//            }
//        }
//        
//        float[] projection = Mat4.ortho(-width/2, width/2, height/2, -height/2);
//        lightingSystem.renderLights(gl, width, height, camera.getViewMatrix(), projection);
//
//        // Pass 3: Post-processing (combines scene + lights + bloom)
//        gl.glClearColor(0, 0, 0, 1);
//        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
//        postProcessor.render(gl);
//    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        // Pass 1: Render scene
        postProcessor.capture(gl);
            gl.glClearColor(bg.getRed()/255f, bg.getGreen()/255f, bg.getBlue()/255f, bg.getAlpha()/255f);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

            List<GameObject> snapshot;
            synchronized (drawables) {
                snapshot = new ArrayList<>(drawables);
            }
            
            // Get particle system
            ParticleSystem particleSystem = GameController.getInstance().getParticleSystem();
            
            glGraphics.setCamera(camera);
            glGraphics.beginFrame(gl, width, height);
            
            // OPTIMIZED: Interleave rendering without creating/sorting a huge list
            renderInterleavedWithParticles(gl, glGraphics, snapshot, particleSystem);
            
            glGraphics.endFrame(gl);
        postProcessor.release(gl);
        
        // Pass 2: Render lights
        lightingSystem.clearLights();
        for (GameObject obj : snapshot) {
            if (obj.getLight() != null) {
                Light light = obj.getLight();
                light.setPosition((float) obj.getPos().getX(), (float) obj.getPos().getY());
                lightingSystem.addLight(light);
            }
        }
        
        // Add particle lights
        particleSystem.addAllLights(lightingSystem);
        
        float[] projection = Mat4.ortho(-width/2, width/2, height/2, -height/2);
        lightingSystem.renderLights(gl, width, height, camera.getViewMatrix(), projection);

        // Pass 3: Post-processing
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        postProcessor.render(gl);
    }

    /**
     * Efficiently render game objects and particles in z-order without creating huge lists
     */
    private void renderInterleavedWithParticles(GL3 gl, GLGraphics glGraphics, 
                                               List<GameObject> objects, 
                                               ParticleSystem particleSystem) {
        // Get z-layers once
        Set<Float> zLayers = particleSystem.getZLayers();
        if (zLayers.isEmpty()) {
            // No particles - just render objects normally
            for (GameObject o : objects) {
                o.drawGL(gl, glGraphics);
            }
            return;
        }
        
        // Convert to sorted array for efficient binary search
        Float[] sortedZLayers = zLayers.toArray(new Float[0]);
        Arrays.sort(sortedZLayers);
        
        int layerIndex = 0;
        
        // Render objects, inserting particle layers at appropriate z-positions
        for (GameObject obj : objects) {
            double objZ = obj.getDrawnHeight() + obj.getPos().getY();
            
            // Render particle layers before this object
            while (layerIndex < sortedZLayers.length && sortedZLayers[layerIndex] <= objZ && obj.getPos().getZ() > 0) {
                particleSystem.renderBatchedAtZ(gl, glGraphics, sortedZLayers[layerIndex]);
                layerIndex++;
            }
            
            // Render object
            obj.drawGL(gl, glGraphics);
        }
        
        // Render remaining particle layers (those behind all objects)
        while (layerIndex < sortedZLayers.length) {
            particleSystem.renderBatchedAtZ(gl, glGraphics, sortedZLayers[layerIndex]);
            layerIndex++;
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL3 gl = drawable.getGL().getGL3();
        width = w;
        height = h;
        gl.glViewport(0, 0, w, h);
        
        postProcessor.resize(gl, w, h);
        lightingSystem.resize(gl, w, h);
    }

    @Override 
    public void dispose(GLAutoDrawable d) {}
    
    public LightingSystem getLightingSystem() {
        return lightingSystem;
    }
}