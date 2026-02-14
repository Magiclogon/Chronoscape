package ma.ac.emi.gamelogic.attack.behavior;

import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.world.Obstacle;

public class LobbingBehavior implements Behavior{
	private double g;
	private double scale;
	private Vector3D v;
	
	private double t = 0;
	
	public LobbingBehavior(double g, double scale) {
		this.g = g;
		this.scale = scale;
	}
	
	@Override
	public void onInit(Projectile p) {
		v = new Vector3D(p.getVelocity());
		
		double dx = (p.getTarget().getX() - p.getStartingPos().getX());
		double dy = (p.getTarget().getY() - p.getStartingPos().getY());
		double num = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		double den = Math.sqrt(Math.pow(v.getX(), 2) + Math.pow(v.getY(), 2));
		
		if(den != 0) v.setZ(g * num/den);
	}

	@Override
	public void onUpdate(Projectile p, double step) {
		p.getPos().setZ((-g*t*t + v.getZ()*t)*scale);
		t += step;

		
	}

	@Override
	public void onHit(Projectile p, LivingEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDesactivate(Projectile p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHit(Projectile p, Obstacle obstacle) {
		// TODO Auto-generated method stub
		
	}
	

}
