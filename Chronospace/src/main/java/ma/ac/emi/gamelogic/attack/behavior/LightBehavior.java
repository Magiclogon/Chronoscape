package ma.ac.emi.gamelogic.attack.behavior;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.glgraphics.lighting.LightObject;

public class LightBehavior implements Behavior{
	private double lightRadius;
	private double intensity;
	
	private LightObject light;
	
	public LightBehavior(double lightRadius, double intensity) {
		this.lightRadius = lightRadius;
		this.intensity = intensity;
	}
	
	@Override
	public void onInit(Projectile p) {
		light = new LightObject(p.getPos(), 999999, (float) lightRadius, (float)intensity);
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

}
