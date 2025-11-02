package ma.ac.emi.UI.shopElements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;

import ma.ac.emi.UI.ShopUI;
import ma.ac.emi.gamelogic.shop.ItemDefinition;

public class InventoryItemButton extends JButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public InventoryItemButton(ShopUI shopUI, ItemDefinition item) {
        setPreferredSize(shopUI.inventoryButtonSize);
        setFocusPainted(false);
        setBackground(new Color(80, 80, 80));
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.PLAIN, 12));
		
        if(item == null) return;
		
        setText("<html><b>" + item.getName() + "</html>");
        addActionListener(e -> {
            //Show item description
        });
	}

}
