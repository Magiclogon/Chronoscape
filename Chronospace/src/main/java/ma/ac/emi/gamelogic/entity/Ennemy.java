package ma.ac.emi.gamelogic.entity;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.ai.AIBehavior;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.math.Vector2D;
import java.awt.*;

@Setter
@Getter
public class Ennemy extends LivingEntity {
	protected double damage;
	protected Weapon weapon;
	protected AIBehavior aiBehavior;

	public Ennemy(Vector2D pos, double speed) {
		this.pos = pos;
		this.speed = speed;
		this.velocity = new Vector2D();
		hpMax = 100;
		hp = 100;
		bound = new Rectangle(GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
		hp = 30;
	}

	public void update(double step, Vector2D targetPos) {
		velocity.init();

		if (aiBehavior != null) {
			Vector2D direction = aiBehavior.calculateMovement(this, targetPos, step);
			setVelocity(direction.mult(getSpeed()));

			if (aiBehavior.shouldAttack(this, targetPos)) {
				attack();
			}
		} else {
			// basic
			Vector2D direction = (targetPos.sub(getPos())).normalize();
			setVelocity(direction.mult(getSpeed()));
		}

		setPos(getPos().add(velocity.mult(step)));

		bound.x = (int) getPos().getX();
		bound.y = (int) getPos().getY();
	}

	@Override
	public void update(double step) {/* todo nothing */}

	@Override
	public void draw(Graphics g) {
		if(hp > 0) {
			g.setColor(Color.RED);
			g.fillRect((int)(pos.getX()), (int)(pos.getY()),
					GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
		}

		g.setColor(Color.yellow);
		g.drawRect(bound.x, bound.y, bound.width, bound.height);
	}

	@Override
	public void attack() {
		if (this.weapon != null) {
			this.weapon.attack();
		}
	}
}
