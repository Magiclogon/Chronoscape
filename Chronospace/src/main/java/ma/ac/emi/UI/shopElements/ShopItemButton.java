package ma.ac.emi.UI.shopElements;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;

import ma.ac.emi.UI.ShopUI;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.shop.ShopItem;

public class ShopItemButton extends JButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ShopItemButton(ShopUI shopUI, ShopItem item) {
		if(item == null) return;
		setText("<html><b>" + item.getItemDefinition().getName() + "</b><br>" + item.getPrice() + "$</html>");
        setFocusPainted(false);
        setBackground(new Color(80, 80, 80));
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.PLAIN, 12));

        addActionListener(e -> {
            GameController.getInstance().getShopManager().purchaseItem(item);
            shopUI.refresh();
        });
	}

}
