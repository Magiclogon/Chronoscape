package ma.ac.emi.gamelogic.pickable;

public class HpPickableFactory implements PickableFactory {
    @Override
    public Pickable create() {
        return new HpPickable();
    }
}