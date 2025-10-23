package ma.ac.emi.gamelogic.weapon;

public abstract class RangeSingleHit extends Weapon {
    protected double range;

    @Override
    public double getRange() { return range; }
    @Override
    public void setRange(double range) { this.range = range; }
}