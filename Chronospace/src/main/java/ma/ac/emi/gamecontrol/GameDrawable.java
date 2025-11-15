package ma.ac.emi.gamecontrol;

import java.awt.Graphics;

import ma.ac.emi.gamecontrol.GameObject;

public interface GameDrawable extends Comparable<GameObject>{
	void draw(Graphics g);
}
