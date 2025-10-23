package ma.ac.emi.gamelogic.weapon;

public abstract class RangeAOE extends Weapon {
    protected double range;
    protected double aoe;

    @Override
    public double getRange() { return range; }
    @Override
    public void setRange(double range) { this.range = range; }

    @Override
    public double getAoe() { return aoe; }
    @Override
    public void setAoe(double aoe) { this.aoe = aoe; }
}