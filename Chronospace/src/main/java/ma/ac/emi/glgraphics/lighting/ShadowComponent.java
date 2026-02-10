package ma.ac.emi.glgraphics.lighting;

import java.awt.Graphics;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.Texture;

public class ShadowComponent {
    float baseRadius;
    float maxAlpha;
    
    Sprite sprite = AssetsLoader.getSprite("shadow_sprite.png");

    public void drawGL(GL3 gl, GLGraphics glGraphics, Entity entity) {
    	if(entity.getCurrentSprite() == null) return;
    	
    	gl.glBlendFunc(GL.GL_DST_COLOR, GL.GL_ZERO);
    	
    	Texture texture = sprite.getTexture(gl);
    	
    	int width = entity.getCurrentSprite().getWidth();
    	int height = entity.getCurrentSprite().getHeight();

	    glGraphics.drawSprite(
	            gl,
	            texture,
	            (float)(entity.getPos().getX() - (width*0.8) / 2f),
	            (float)(entity.getPos().getY() - height / 8f + entity.getDrawnHeight()/2),
	            (int) (width*0.8),
	            height/4
	    );
	    
	    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    }

}

