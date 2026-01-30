package ma.ac.emi.glgraphics;

import com.jogamp.opengl.GL3;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.lighting.LightingStrategy;

public class GLGraphics {
    private final Shader spriteShader;
    private Camera camera;
    
    public GLGraphics(GL3 gl) {
        spriteShader = Shader.load(gl, "sprite.vert", "sprite.frag");
        SpriteQuad.init(gl);
    }
    
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
    public void beginFrame(GL3 gl, int width, int height) {
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
        spriteShader.use(gl);
        spriteShader.setMat4(gl, "uProjection", Mat4.ortho(-width/2, width/2, height/2, -height/2));
        spriteShader.setMat4(gl, "uView", camera != null ? camera.getViewMatrix() : Mat4.identity());
    }
    
    public void drawSprite(GL3 gl, Texture texture, float x, float y, float w, float h) {
        float[] model = Mat4.transform(x, y, w, h);
        drawSprite(gl, texture, model, null, null);
    }
    
    public void drawSprite(GL3 gl, Texture texture, float x, float y, float w, float h, LightingStrategy lighting) {
        float[] model = Mat4.transform(x, y, w, h);
        drawSprite(gl, texture, model, lighting, null);
    }
    
    public void drawSprite(GL3 gl, Texture texture, float[] model) {
        drawSprite(gl, texture, model, null, null);
    }
    
	public void drawSprite(GL3 gl, Texture texture, float x, float y, float w, float h, 
            SpriteColorCorrection color) {
		float[] model = Mat4.transform(x, y, w, h);
		drawSprite(gl, texture, model, null, color);
	}
		
		public void drawSprite(GL3 gl, Texture texture, float x, float y, float w, float h, LightingStrategy strategy,
			SpriteColorCorrection color) {
		float[] model = Mat4.transform(x, y, w, h);
		drawSprite(gl, texture, model, strategy, color);
	}
        
    public void drawSprite(GL3 gl, Texture texture, float[] model, LightingStrategy lighting,
            SpriteColorCorrection colorCorrection) {
		spriteShader.setMat4(gl, "uModel", model);
		
		// Apply color correction
		if (colorCorrection != null) {
			colorCorrection.apply(gl, spriteShader);
		} else {
			SpriteColorCorrection.resetUniforms(gl, spriteShader);
		}
		
		// Apply lighting if provided
		if (lighting != null) {
			lighting.applyLighting(gl, spriteShader);
		} else {
			spriteShader.setFloat(gl, "uEmission", 0.0f);
			spriteShader.setVec3(gl, "uEmissionColor", 0.0f, 0.0f, 0.0f);
		}
		
		// Draw textured quad
		spriteShader.setInt(gl, "uUseTexture", 1);
		
		gl.glBindTexture(GL3.GL_TEXTURE_2D, texture.id);
		gl.glBindVertexArray(SpriteQuad.VAO);
		gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, 4);
		
	}
    
    
    public void drawQuad(GL3 gl, float x, float y, float w, float h) {
        drawQuad(gl, x, y, w, h, 1f, 1f, 1f, 1f);
    }

    public void drawQuad(GL3 gl, float x, float y, float w, float h, float gray) {
        drawQuad(gl, x, y, w, h, gray, gray, gray, 1f);
    }
    
    /**
     * Draw sprite assuming texture is already bound (for batching)
     */
    public void drawSpriteBatched(GL3 gl, Texture texture, float x, float y, float w, float h,
                                 LightingStrategy lighting, SpriteColorCorrection colorCorrection) {
        float[] model = Mat4.transform(x, y, w, h);
        drawSpriteBatched(gl, texture, model, lighting, colorCorrection);
    }

    public void drawSpriteBatched(GL3 gl, Texture texture, float[] model, 
                                 LightingStrategy lighting, SpriteColorCorrection colorCorrection) {
        spriteShader.setMat4(gl, "uModel", model);
        
        // Apply color correction
        if (colorCorrection != null) {
            colorCorrection.apply(gl, spriteShader);
        } else {
            SpriteColorCorrection.resetUniforms(gl, spriteShader);
        }
        
        // Apply lighting
        if (lighting != null) {
            lighting.applyLighting(gl, spriteShader);
        } else {
            spriteShader.setFloat(gl, "uEmission", 0.0f);
            spriteShader.setVec3(gl, "uEmissionColor", 0.0f, 0.0f, 0.0f);
        }
        
        // Texture is already bound by batch renderer - don't bind again!
        // gl.glBindTexture(GL3.GL_TEXTURE_2D, texture.id); // SKIP THIS
        
        gl.glBindVertexArray(SpriteQuad.VAO);
        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, 4);
    }
    
    public void drawQuad(
            GL3 gl,
            float x, float y,
            float w, float h,
            float r, float g, float b, float a
    ) {
        float[] model = Mat4.transform(x, y, w, h);

        spriteShader.setMat4(gl, "uModel", model);

        // Disable texture usage
        spriteShader.setInt(gl, "uUseTexture", 0);
        spriteShader.setVec4(gl, "uColor", r, g, b, a);

        // Reset lighting & color correction
        SpriteColorCorrection.resetUniforms(gl, spriteShader);
        spriteShader.setFloat(gl, "uEmission", 0.0f);
        spriteShader.setVec3(gl, "uEmissionColor", 0.0f, 0.0f, 0.0f);

        gl.glBindVertexArray(SpriteQuad.VAO);
        gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, 4);

        // Restore default behavior for sprites
        spriteShader.setInt(gl, "uUseTexture", 1);
    }
    
    public void endFrame(GL3 gl) {
        gl.glBindVertexArray(0);
    }
    
    public void dispose(GL3 gl) {
    	this.spriteShader.dispose(gl);
    	SpriteQuad.dispose(gl);
    }
}