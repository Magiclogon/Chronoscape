package ma.ac.emi.gamelogic.entity;

import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.math.Vector2D;
import java.awt.*;

public class Ennemy extends LivingEntity {
	protected double damage;

	public Ennemy(Vector2D pos, double speed) {
		super(pos, speed);
		this.velocity = new Vector2D();
	}

	public void update(double step, Vector2D targetPos) {
		velocity.init();
		Vector2D direction = (targetPos.sub(getPos())).normalize();
		setVelocity(direction.mult(getSpeed()));

		setPos(getPos().add(velocity.mult(step)));
	}

	@Override
	public void update(double step) {/* todo nothing */}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect((int)(pos.getX()), (int)(pos.getY()), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
	}
}
