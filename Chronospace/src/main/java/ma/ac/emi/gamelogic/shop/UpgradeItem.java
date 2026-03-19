package ma.ac.emi.gamelogic.shop;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.effect.PlayerEffect;
import ma.ac.emi.gamelogic.player.Player;

@Getter
@Setter
public class UpgradeItem extends ShopItem {

 
    private PlayerEffect effect;

    public UpgradeItem(UpgradeItemDefinition itemDefinition) {
        super(itemDefinition);
        this.effect = itemDefinition.createEffect();
    }

    @Override
    public void apply(Player player) {
        UpgradeItemDefinition def = (UpgradeItemDefinition) getItemDefinition();

        // Numeric side — existing system unchanged
        if (!def.getWeaponModifications().isEmpty())
            player.getInventory().addWeaponUpgrade(this);
        if (!def.getPlayerModifications().isEmpty())
            player.getInventory().addPlayerUpgrade(this);

        player.getInventory().addItem(this);

        // Behavioral side — register effect if present
        player.getInventory().registerEffect(this, player);
    }
}