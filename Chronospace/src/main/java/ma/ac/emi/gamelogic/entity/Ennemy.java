package ma.ac.emi.gamelogic.entity;


import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector2D;

import java.awt.*;

public class Ennemy extends LivingEntity {
	private Player player;
    protected double damage;

	public Ennemy(Vector2D pos, double speed, Player player) {
		super(pos, speed);
        this.player = player;
		this.velocity = new Vector2D();
    }

	@Override
	public void update(double step) {
		velocity.init();
		Vector2D playerPos = player.getPos();
		Vector2D direction = (playerPos.sub(getPos())).normalize();
		setVelocity(direction.mult(getSpeed()));

		velocity.mult(step);
		setPos(getPos().add(velocity));
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect((int)(pos.getX()), (int)(pos.getY()), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
	}
}
