package ma.ac.emi.gamelogic.weapon;

public abstract class Weapon {
    protected double damage;
    protected double range;
    protected double aoe;
    protected double attackSpeed;
    protected double positionX;
    protected double positionY;
    protected double rotation;

    public abstract void attack();

    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }

    public double getRange() { return range; }
    public void setRange(double range) { this.range = range; }

    public double getAoe() { return aoe; }
    public void setAoe(double aoe) { this.aoe = aoe; }

    public double getAttackSpeed() { return attackSpeed; }
    public void setAttackSpeed(double attackSpeed) { this.attackSpeed = attackSpeed; }

    public double getPositionX() { return positionX; }
    public void setPositionX(double positionX) { this.positionX = positionX; }

    public double getPositionY() { return positionY; }
    public void setPositionY(double positionY) { this.positionY = positionY; }

    public double getRotation() { return rotation; }
    public void setRotation(double rotation) { this.rotation = rotation; }
}
