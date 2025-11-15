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
		this.sprite = sprite;
		this.frameTime = frameTime;
	}
}
