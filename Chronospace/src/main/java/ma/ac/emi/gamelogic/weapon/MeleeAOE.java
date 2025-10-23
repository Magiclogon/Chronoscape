package ma.ac.emi.gamelogic.weapon;

public abstract class MeleeAOE extends Weapon {
    protected double aoe;

    @Override
    public double getAoe() { return aoe; }
    @Override
    public void setAoe(double aoe) { this.aoe = aoe; }
}