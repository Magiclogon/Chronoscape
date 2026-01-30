package ma.ac.emi.gamecontrol;

import java.awt.Graphics;

import com.jogamp.opengl.GL3;

import ma.ac.emi.glgraphics.GLGraphics;

public interface GameDrawable extends Comparable<GameObject>{
	void draw(Graphics g);
	
	void drawGL(GL3 gl, GLGraphics glGraphics);
}
