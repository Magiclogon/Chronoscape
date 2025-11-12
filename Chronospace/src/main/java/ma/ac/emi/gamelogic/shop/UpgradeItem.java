package ma.ac.emi.gamelogic.shop;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.player.Player;

@Getter
@Setter
public class UpgradeItem extends ShopItem {

    public UpgradeItem(UpgradeItemDefinition itemDefinition) {
        super(itemDefinition);
    }

    @Override
    public void apply(Player player) {
        UpgradeItemDefinition def = (UpgradeItemDefinition) getItemDefinition();

        // Check if this upgrade affects weapons or players
        boolean hasWeaponMods = !def.getWeaponModifications().isEmpty();
        boolean hasPlayerMods = !def.getPlayerModifications().isEmpty();

        // Add to appropriate tracking lists (only once per upgrade)
        if (hasWeaponMods) {
            player.getInventory().addWeaponUpgrade(this);
        }
        if (hasPlayerMods) {
            player.getInventory().addPlayerUpgrade(this);
        }

        player.getInventory().addItem(this);
    }
}