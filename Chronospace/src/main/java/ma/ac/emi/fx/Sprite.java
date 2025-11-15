package ma.ac.emi.fx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sprite {
	private BufferedImage sprite;
	
	public Sprite() {
		//Load default image from assets
	}
	
	
	public Sprite(String path) {
		try {
            sprite = ImageIO.read(getClass().getClassLoader().getResourceAsStream(path));
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException("Failed to load spritesheet: " + path, e);
        }
	}
	
	public Sprite(BufferedImage sprite) {
		this.sprite = sprite;
	}
	
	public Sprite getSubimage(int x, int y, int width, int height) {
		return new Sprite(sprite.getSubimage(x, y, width, height));
	}
}
