package ma.ac.emi.gamelogic.particle;

import java.awt.*;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.math.Vector3D;

@Getter
@Setter
public class Particle extends Entity{
    private ParticleDefinition definition;
    private double age;
    private boolean alive;
    private Image sprite;

    public Particle(ParticleDefinition definition, Vector3D pos, Vector3D vel) {
    	super(true);
        this.pos = new Vector3D(pos);
        this.velocity = vel;
        this.definition = definition;
        this.age = 0;
        this.alive = true;
    }

    public void update(double step) {
        age += step;
        if (age >= definition.getLifetime()) {
        	GameController.getInstance().getGamePanel().removeDrawable(this);
            alive = false;
            return;
        }
        pos = pos.add(velocity.mult(step));
    }

    public void draw(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
        int drawX = (int) (pos.getX() - definition.getSize() / 2);
        int drawY = (int) (pos.getY() - definition.getSize() / 2 - pos.getZ());
        int s = (int) definition.getSize();

        if (sprite != null) {
            g2d.drawImage(sprite, drawX, drawY, s, s, null);
        } else {
            g2d.setColor(Color.gray);
            g2d.fillOval(drawX, drawY, s, s);
        }
    }

	@Override
	public void initStateMachine() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setupAnimations() {
		// TODO Auto-generated method stub
		
	}



}
