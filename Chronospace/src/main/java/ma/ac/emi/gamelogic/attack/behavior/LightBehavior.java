package ma.ac.emi.gamelogic.attack.behavior;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.glgraphics.lighting.LightObject;
import ma.ac.emi.world.Obstacle;

public class LightBehavior implements Behavior{
	private double lightRadius;
	private double r, g, b;
	private double intensity;
	
	private LightObject light;
	
	public LightBehavior(double lightRadius, double r, double g, double b, double intensity) {
		this.lightRadius = lightRadius;
		this.r = r; this.g = g; this.b = b;
		this.intensity = intensity;
	}
	
	@Override
	public void onInit(Projectile p) {
		light = new LightObject(p.getPos(), 999999, (float) lightRadius, (float)intensity);
		light.setColor((float)r, (float)g, (float)b);
		p.setShadow(null);
	}

	@Override
	public void onUpdate(Projectile p, double step) {
		light.setPos(p.getPos());
		
	}

	@Override
	public void onHit(Projectile p, LivingEntity entity) {}

	@Override
	public void onDesactivate(Projectile p) {
		light.setAlive(false);
	}

	@Override
	public void onHit(Projectile p, Obstacle obstacle) {
		// TODO Auto-generated method stub
		
	}

}
