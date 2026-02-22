package ma.ac.emi.gamecontrol;

import com.jogamp.opengl.*;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.gamelogic.particle.ParticleAnimationCache;
import ma.ac.emi.gamelogic.particle.ParticleSystem;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.Mat4;
import ma.ac.emi.glgraphics.lighting.Light;
import ma.ac.emi.glgraphics.lighting.LightObject;
import ma.ac.emi.glgraphics.lighting.LightingSystem;
import ma.ac.emi.glgraphics.post.*;
import ma.ac.emi.glgraphics.post.config.PostFXConfig;
import ma.ac.emi.glgraphics.post.config.PostFXConfigLoader;

import java.awt.Color;
import java.util.*;

public class GameRenderer implements GLEventListener {

    private Camera camera;
    private final List<GameObject> drawables =
            Collections.synchronizedList(new ArrayList<>());

    private GLGraphics glGraphics;

    private int width, height;
    private int internalWidth, internalHeight;

    private float renderScale = 0.4f;

    private PostProcessor postProcessor;
    private LightingSystem lightingSystem;
    private Color bg;
    
    private long lastTime = System.nanoTime();
    private int frames = 0;
    private double fps = 0.0;

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void addDrawable(GameObject o) {
        o.setDrawn(true);
        if (!drawables.contains(o)) drawables.add(o);
    }

    public void removeDrawable(GameObject o) {
        o.setDrawn(false);
    }

    public void update() {
        drawables.removeIf(d -> !d.isDrawn());
        Collections.sort(drawables);
        
    }
    
    public void initPostProcessor(GL3 gl, PostFXConfig config) {
    	renderScale = config.renderScale;
    	camera.setRenderScale(renderScale);
    	computeInternalResolution();
    	
    	// --- Lighting (LOW RES) ---
        lightingSystem = new LightingSystem(gl, internalWidth, internalHeight);
        lightingSystem.setAmbientLight(0.2f, 0.2f, 0.6f);

        // --- Post processing (LOW RES SCENE) ---
        postProcessor = new PostProcessor(gl, internalWidth, internalHeight, renderScale);
        
        postProcessor.addEffect(new GammaToLinearEffect(gl));
        
        if (config.colorCorrection != null && config.colorCorrection.enabled) {
            PostFXConfig.ColorCorrection cc = config.colorCorrection;
            postProcessor.addEffect(
                    new ColorCorrectionEffect(gl, 
                    		cc.r, cc.g, cc.b, cc.a,
                    		cc.brightness,
                    		cc.contrast,
                    		cc.saturation,
                    		cc.hue,
                    		cc.value
                    )
            );
        }
        
        postProcessor.addEffect(new LightCompositeEffect(gl, lightingSystem.getLightTextureId()));

        
        if (config.glow != null && config.glow.enabled) {
            postProcessor.setBloomDownscale(config.glow.downscale);
            
            postProcessor.addEffect(new SnapshotEffect(gl, postProcessor));
            
            postProcessor.addEffect(
                new GlowExtractEffect(postProcessor.getGlowTextureId())
            );
                        
            postProcessor.addEffect(
                new BlurEffect(gl, true, config.glow.blurRadius)
            );
            postProcessor.addEffect(
                new BlurEffect(gl, false, config.glow.blurRadius)
            );
            
            postProcessor.addEffect(
                new GlowCombineEffect(
                    gl,
                    postProcessor.getSnapshotTextureId(),
                    config.glow.intensity
                )
            );
        }
        

                
        //Bloom
        if (config.bloom != null && config.bloom.enabled) {

            postProcessor.setBloomDownscale(config.bloom.downscale);

            postProcessor.addEffect(new SnapshotEffect(gl, postProcessor));
            postProcessor.addEffect(
                    new BloomExtractEffect(gl, config.bloom.threshold)
            );

            postProcessor.addEffect(
                    new BlurEffect(gl, true, config.bloom.blurRadius)
            );
            postProcessor.addEffect(
                    new BlurEffect(gl, false, config.bloom.blurRadius)
            );

            BloomCombineEffect bloomCombine = new BloomCombineEffect(gl, postProcessor.getSnapshotTextureId());
            bloomCombine.setIntensity(config.bloom.intensity);
            postProcessor.addEffect(bloomCombine);
        }
        
        
        postProcessor.addEffect(new ToneMappingEffect(gl));
        postProcessor.addEffect(new GammaEffect(gl));
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        System.out.println("Initializing Renderer..");

        GL3 gl = drawable.getGL().getGL3();
        
        camera.setRenderScale(renderScale);
        
        width = drawable.getSurfaceWidth();
        height = drawable.getSurfaceHeight();

        computeInternalResolution();

        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        glGraphics = new GLGraphics(gl);
        ParticleAnimationCache.initializeAllTextures(gl);
        
        PostFXConfig config = GameController.getInstance().getPostFXConfig();
        
        initPostProcessor(gl, config);

        bg = GameController.getInstance()
                .getWorldManager()
                .getCurrentWorld()
                .getVoidColor();
        
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        
        long currentTime = System.nanoTime();
        frames++;

        if (currentTime - lastTime >= 1_000_000_000L) { 
            fps = frames;
            frames = 0;
            lastTime = currentTime;
        }

        
        // ============================
        // PASS 1 — SCENE (LOW RES)
        // ============================
        postProcessor.capture(gl);
        gl.glViewport(0, 0, internalWidth, internalHeight);

        gl.glClearColor(
                bg.getRed() / 255f,
                bg.getGreen() / 255f,
                bg.getBlue() / 255f,
                bg.getAlpha() / 255f
        );
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        List<GameObject> snapshot;
        synchronized (drawables) {
            snapshot = new ArrayList<>(drawables);
        }

        ParticleSystem particleSystem =
                GameController.getInstance().getParticleSystem();

        glGraphics.setCamera(camera);
        glGraphics.beginFrame(gl, internalWidth, internalHeight);

        renderInterleavedWithParticles(gl, glGraphics, snapshot, particleSystem);

        glGraphics.endFrame(gl);
        postProcessor.release(gl);
        
	     // ============================
	     // PASS 1.5 — GLOW OBJECTS (LOW RES)
	     // ============================
	     postProcessor.captureGlow(gl);
	     gl.glViewport(0, 0, internalWidth, internalHeight);
	
	     glGraphics.setCamera(camera);
	     glGraphics.beginFrame(gl, internalWidth, internalHeight);
	
	     // Render only glowing objects
	     renderGlowingInterleavedWithParticles(gl, glGraphics, snapshot, particleSystem);
	     
	     glGraphics.endFrame(gl);
	     postProcessor.releaseGlow(gl);

        // ============================
        // PASS 2 — LIGHTING (LOW RES)
        // ============================
        lightingSystem.clearLights();
        for (GameObject obj : snapshot) {
            if (obj.getLight() != null) {
                Light light = obj.getLight();
                light.setPosition(obj.getPos());
                lightingSystem.addLight(light);
            }
        }
        particleSystem.addAllLights(lightingSystem);

        float[] projection = Mat4.ortho(
                -internalWidth / 2f, internalWidth / 2f,
                internalHeight / 2f, -internalHeight / 2f
        );

        lightingSystem.renderLights(
                gl,
                internalWidth,
                internalHeight,
                camera.getViewMatrix(),
                projection
        );

        // ============================
        // PASS 3 — POST + UPSCALE
        // ============================
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        postProcessor.render(gl);
        

    }

    private void renderInterleavedWithParticles(
            GL3 gl,
            GLGraphics glGraphics,
            List<GameObject> objects,
            ParticleSystem particleSystem
    ) {
        Set<Float> zLayers = particleSystem.getZLayers();
        if (zLayers.isEmpty()) {
            for (GameObject o : objects) {
                o.drawGL(gl, glGraphics);
            }
            return;
        }

        Float[] sortedZLayers = zLayers.toArray(new Float[0]);
        Arrays.sort(sortedZLayers);
        int layerIndex = 0;
        for (GameObject obj : objects) {
            double objZ = obj.getZOrder();

            while (layerIndex < sortedZLayers.length
                    && sortedZLayers[layerIndex] <= objZ) {
                particleSystem.renderBatchedAtZ(
                        gl, glGraphics, sortedZLayers[layerIndex]
                );
                layerIndex++;
            }
            obj.drawGL(gl, glGraphics);
            
        }

        while (layerIndex < sortedZLayers.length) {
            particleSystem.renderBatchedAtZ(
                    gl, glGraphics, sortedZLayers[layerIndex]
            );
            layerIndex++;
        }
    }
    private void renderGlowingInterleavedWithParticles(
    		GL3 gl,
    		GLGraphics glGraphics,
    		List<GameObject> objects,
    		ParticleSystem particleSystem
    		) {
    	Set<Float> zLayers = particleSystem.getZLayers();
    	if (zLayers.isEmpty()) {
    		for (GameObject o : objects) {
    			if(o.getLightingStrategy() == null) continue;
    			if(o.getLightingStrategy().shouldBloom()) o.drawGL(gl, glGraphics);
    		}
    		return;
    	}
    	
    	Float[] sortedZLayers = zLayers.toArray(new Float[0]);
    	Arrays.sort(sortedZLayers);
    	int layerIndex = 0;
    	for (GameObject obj : objects) {
    		if(obj.getLightingStrategy() == null) continue;
			if(!obj.getLightingStrategy().shouldBloom()) continue;
    		double objZ = obj.getZOrder();
    		
    		while (layerIndex < sortedZLayers.length
    				&& sortedZLayers[layerIndex] <= objZ) {
    			particleSystem.renderGlowingBatchedAtZ(
    					gl, glGraphics, sortedZLayers[layerIndex]
    					);
    			layerIndex++;
    		}
    		obj.drawGL(gl, glGraphics);
    		
    	}
    	
    	while (layerIndex < sortedZLayers.length) {
    		particleSystem.renderGlowingBatchedAtZ(
    				gl, glGraphics, sortedZLayers[layerIndex]
    				);
    		layerIndex++;
    	}
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL3 gl = drawable.getGL().getGL3();

        width = w;
        height = h;
        computeInternalResolution();

        postProcessor.resize(gl, internalWidth, internalHeight);
        lightingSystem.resize(gl, internalWidth, internalHeight);

        gl.glViewport(0, 0, w, h);
    }

    private void computeInternalResolution() {
        internalWidth = Math.max(1, (int) (width * renderScale));
        internalHeight = Math.max(1, (int) (height * renderScale));
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    	GL3 gl = drawable.getGL().getGL3();
    	System.out.println("Disposing of Renderer..");
    	
    	ParticleAnimationCache.clear(gl);
    	
    	glGraphics.dispose(gl);
    	postProcessor.dispose(gl);
    	lightingSystem.dispose(gl);
    	
    	AssetsLoader.disposeAll(gl);
    }

    public LightingSystem getLightingSystem() {
        return lightingSystem;
    }

	public double getFPS() {
		return fps;
	}

	public void removeAllDrawables() {
		this.drawables.clear();
	}

	public void initParticleCache(GL3 gl) {
		ParticleAnimationCache.initializeAllTextures(gl);
	}

	public double getRenderScale() {
		return this.renderScale;
	}

	public void reloadPostProcessing(GL3 gl, PostFXConfig updatedConfig) {
		postProcessor.clearEffects(gl);
		initPostProcessor(gl, updatedConfig);
	}
}
