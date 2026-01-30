package ma.ac.emi.glgraphics.lighting;

import java.awt.Graphics;

import com.jogamp.opengl.GL3;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GameObject;
import ma.ac.emi.gamelogic.particle.Particle;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class LightObject extends GameObject{
	public static final String LIGHT_ID = "light";
	private double ageMax;
	private boolean alive = true;
	
    public LightObject(Vector3D pos) {
    	this(pos, 0, 0);
    }
    
    public LightObject(Vector3D pos, double ageMax, float radius, float r, float g, float b, float intensity) {
    	setLight(new Light((float) pos.getX(), (float) pos.getY(), radius, r, g, b, intensity));
    	this.ageMax = ageMax;
    	GameController.getInstance().addLightObject(this);
    	
    }
    
    public LightObject(Vector3D pos, double ageMax, float radius) {
        this(pos, ageMax, radius, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public LightObject(Vector3D pos, double ageMax, float radius, float intensity) {
    	this(pos, ageMax, radius, 1.0f, 1.0f, 1.0f, intensity);
    }
    
    public LightObject(double ageMax, float radius) {
    	this(new Vector3D(), ageMax, radius, 1.0f, 1.0f, 1.0f, 1.0f);
    }
        
    public void setColor(float r, float g, float b) {
        this.light.r = r;
        this.light.g = g;
        this.light.b = b;
    }
    
    public void setEnabled(boolean enabled) {
        light.setEnabled(enabled);
    }
    
    public boolean isEnabled() {
        return light.isEnabled();
    }
    
    @Override
    public void update(double step) {
    	super.update(step);
    	
    	if(isEnabled()) ageMax -= step;
    	if(ageMax <= 0) {
    		alive = false;
    	}
    	
    }
    
    @Override
    public double getDrawnHeight() {
    	return 0;
    }
    
	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawGL(GL3 gl, GLGraphics glGraphics) {}

}
