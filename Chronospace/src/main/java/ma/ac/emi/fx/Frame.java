package ma.ac.emi.fx;

import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Frame {
	private Sprite sprite;
	private double frameTime;
		
	public Frame(Sprite sprite, double frameTime) {
		if(sprite != null) this.sprite = sprite;
		else this.sprite = new Sprite();
		this.frameTime = frameTime;
	}
}
