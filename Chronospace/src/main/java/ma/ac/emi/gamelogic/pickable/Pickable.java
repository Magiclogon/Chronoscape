package ma.ac.emi.gamelogic.pickable;

public abstract class Pickable {
    protected double probability;

    public double getProbability() { return probability; }
    public void setProbability(double probability) { this.probability = probability; }
}