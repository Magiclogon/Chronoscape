package ma.ac.emi.gamelogic.entity;

import ma.ac.emi.math.Vector2D;

import java.awt.Graphics;

public class Ennemy extends LivingEntity {
    protected double damage;

	public Ennemy(Vector2D pos, double speed) {
		super(pos, speed);
	}

	public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }
	@Override
	public void update(double step) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}
}
