package ma.ac.emi.gamelogic.pickable;

public class MoneyPickableFactory implements PickableFactory {
    @Override
    public Pickable create() {
        return new MoneyPickable();
    }
}