package ma.ac.emi.UI.shopElements;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;

import ma.ac.emi.UI.ShopUI;
import ma.ac.emi.gamelogic.shop.ShopItem;

public class InventoryItemButton extends JButton {
    private static final long serialVersionUID = 1L;

    public InventoryItemButton(ShopUI shopUI, ShopItem item, int count) {
        setPreferredSize(shopUI.inventoryButtonSize);
        setFocusPainted(false);
        setBackground(new Color(80, 80, 80));
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.PLAIN, 12));

        if (item == null) return;

        String name = item.getItemDefinition().getName();
        if (count > 1) {
            name += " ×" + count;
        }

        setText("<html><b>" + name + "</b></html>");
        addActionListener(e -> shopUI.showItemDetails(item));
    }
}