package ma.ac.emi.gamelogic.entity;

public abstract class LivingEntity extends Entity {
    protected double hp;
    protected double hpMax;
    protected double strength;
    protected double regenerationSpeed;

    public double getHp() { return hp; }
    public void setHp(double hp) { this.hp = hp; }

    public double getHpMax() { return hpMax; }
    public void setHpMax(double hpMax) { this.hpMax = hpMax; }

    public double getStrength() { return strength; }
    public void setStrength(double strength) { this.strength = strength; }

    public double getRegenerationSpeed() { return regenerationSpeed; }
    public void setRegenerationSpeed(double regenerationSpeed) {
        this.regenerationSpeed = regenerationSpeed;
    }
}