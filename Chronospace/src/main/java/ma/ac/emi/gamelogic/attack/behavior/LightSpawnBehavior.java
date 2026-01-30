package ma.ac.emi.gamelogic.attack.behavior;

import java.util.ArrayList;
import java.util.List;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.glgraphics.lighting.LightObject;
import ma.ac.emi.math.Vector3D;

public class LightSpawnBehavior implements Behavior{
	private double lightRadius;
	private double ageMax;
	private int count;
	private double radius;
	private double intensity;
	
	private List<LightObject> lights;
	
	public LightSpawnBehavior(int count, double radius, double lightRadius, double ageMax, double intensity) {
		this.count = count;
		this.radius = radius;
		this.lightRadius = lightRadius;
		this.ageMax = ageMax;
		this.intensity = intensity;
		
		lights = new ArrayList<>();
	}
	
	@Override
	public void onInit(Projectile p) {
		for(int i = 0; i < count; i++) {
			Vector3D offset = count == 1? new Vector3D() : Vector3D.randomUnit2().mult(Math.random() * radius);
			LightObject lightObject = new LightObject(offset, ageMax, (float) lightRadius, (float) intensity);
			lightObject.setEnabled(false);
			lights.add(lightObject);
		}
	}

	@Override
	public void onUpdate(Projectile p, double step) {
	}

	@Override
	public void onHit(Projectile p, LivingEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDesactivate(Projectile p) {
		lights.forEach(l -> {
			l.setPos(l.getPos().add(p.getPos()));
			l.setEnabled(true);
		});
	}
	
}
