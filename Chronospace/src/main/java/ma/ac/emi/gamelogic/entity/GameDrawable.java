package ma.ac.emi.gamelogic.entity;

import java.awt.Graphics;

public interface GameDrawable extends Comparable<Entity>{
	void draw(Graphics g);
}
