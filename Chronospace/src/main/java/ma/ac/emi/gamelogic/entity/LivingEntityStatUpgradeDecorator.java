package ma.ac.emi.gamelogic.entity;

public abstract class LivingEntityStatUpgradeDecorator extends LivingEntity {
    protected LivingEntity wrappee;

    public LivingEntityStatUpgradeDecorator(LivingEntity wrappee) {
        this.wrappee = wrappee;
    }
}