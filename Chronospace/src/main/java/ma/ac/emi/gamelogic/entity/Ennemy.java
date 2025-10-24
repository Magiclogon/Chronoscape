package ma.ac.emi.gamelogic.entity;


import ma.ac.emi.camera.Camera;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector2D;
import java.awt.*;

public class Ennemy extends LivingEntity {
    protected double damage;

	public Ennemy(Vector2D pos, double speed, Camera camera, Player player) {
		super(pos, speed, camera);
        this.player = player;
		this.velocity = new Vector2D();
    }

	@Override
	public void update(double step) {
		camTransform();
		velocity.init();
		Vector2D direction = (targetPos.sub(getPos())).normalize();
		setVelocity(direction.mult(getSpeed()));

		velocity.mult(step);
		setPos(getPos().add(velocity));
	}

	@Override
	public void update(double step) { /* TODO Just the fill in the implementation*/ }

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect((int)(getScreenPos().getX()), (int)(getScreenPos().getY()), (int)(GamePanel.TILE_SIZE*scaleRatios.getX()), (int)(GamePanel.TILE_SIZE*scaleRatios.getY()));
		
	}

	@Override
	public void camTransform() {
		setScreenPos(camera.camTransform(getPos()));
		setScaleRatios(camera.getScreenCamRatios());
	}
}
