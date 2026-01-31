package ma.ac.emi.fx;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jogamp.opengl.GL3;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.glgraphics.Texture;

@Getter
@Setter
public class Sprite {
	private BufferedImage sprite;
	private Texture texture;
		
	public Sprite() {
		//Load default image from assets
		this("assets/default_sprite.png");
	}
	
	
	public Sprite(String path) {
		try {
            sprite = ImageIO.read(getClass().getClassLoader().getResourceAsStream(path));
        } catch (IOException | IllegalArgumentException e) {
        	e.printStackTrace();
            throw new RuntimeException("Failed to load spritesheet: " + path, e);
        }
	}
	
	public Sprite(BufferedImage sprite) {
		this.sprite = sprite;
	}
	
	public Sprite getSubimage(int x, int y, int width, int height) {
		return new Sprite(sprite.getSubimage(x, y, width, height));
	}
	
	public static Sprite superimpose(Sprite s1, Sprite s2) {
		int width = Math.max(s1.getWidth(), s2.getWidth());
        int height = Math.max(s1.getHeight(), s2.getHeight());
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g = combined.createGraphics();
        g.drawImage(s1.getSprite(), 0, 0, null);
        g.drawImage(s2.getSprite(), 0, 0, null);
        
        g.dispose();
        
        return new Sprite(combined);
	}
	
    
    public int getWidth() {
    	return sprite.getWidth();
    }
    
    public int getHeight() {
    	return sprite.getHeight();
    }

    public Texture getTexture(GL3 gl) {
        if (texture == null) {
            texture = new Texture(gl, this);
        }
        return texture;
    }
    
    public void dispose(GL3 gl) {
        if (texture != null) {
            texture.dispose(gl);
            texture = null;
        }
    }
}
