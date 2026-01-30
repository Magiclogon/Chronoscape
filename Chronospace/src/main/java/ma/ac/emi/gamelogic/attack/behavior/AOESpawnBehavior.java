package ma.ac.emi.gamelogic.attack.behavior;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.attack.AOE;
import ma.ac.emi.gamelogic.attack.Projectile;
import ma.ac.emi.gamelogic.attack.type.AOEFactory;
import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class AOESpawnBehavior implements Behavior{
	private String aoeId;
	private int count;
	private double radius;

	public AOESpawnBehavior(String aoeId, int count, double radius) {
		this.aoeId = aoeId;
		this.count = count;
		this.radius = radius;
	}

	@Override
	public void onUpdate(Projectile p, double step) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHit(Projectile p, LivingEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDesactivate(Projectile p) {
		//Spawn aoes
		for(int i = 0; i < count; i++) {
			Vector3D offset = count == 1? new Vector3D() : Vector3D.randomUnit2().mult(Math.random() * radius);
			AOE aoe = AOEFactory.getInstance().createAOE(
					aoeId, 
					p.getPos().add(offset), 
					p.getWeapon());
			p.getWeapon().getAttackObjectManager().addObject(aoe);
		}
		
	}

	@Override
	public void onInit(Projectile p) {
		// TODO Auto-generated method stub
		
	}

}
