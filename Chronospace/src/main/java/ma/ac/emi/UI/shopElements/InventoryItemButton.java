package ma.ac.emi.UI.shopElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import ma.ac.emi.UI.ShopUI;
import ma.ac.emi.gamelogic.shop.ShopItem;

public class InventoryItemButton extends JButton {
    private static final long serialVersionUID = 1L;

    private static final Color BG_NORMAL = new Color(45, 45, 55);
    private static final Color BG_HOVER  = new Color(65, 65, 75);
    private static final Color BG_PRESS  = new Color(30, 30, 40);
    private static final String FONT_NAME = "ByteBounce";

    private Color borderColor = new Color(180, 180, 190); // Default (Common)

    public InventoryItemButton(ShopUI shopUI, ShopItem item, int count) {
        setPreferredSize(shopUI.inventoryButtonSize);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setFont(new Font(FONT_NAME, Font.PLAIN, 18));
        setForeground(Color.WHITE);

        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

        if (item == null) {
            setText("");
            setEnabled(false);
            return;
        }

        switch (item.getItemDefinition().getRarity()) {
            case LEGENDARY: borderColor = new Color(255, 215, 0); break;
            case EPIC:      borderColor = new Color(160, 32, 240); break;
            case RARE:      borderColor = new Color(65, 105, 225); break;
            case COMMON:  borderColor = new Color(50, 205, 50); break;
            default:        borderColor = new Color(100, 100, 110); break;
        }

        String name = item.getItemDefinition().getName();
        String countText = "";

        if (count > 1) {
            countText = "<br><span style='color:yellow; font-size:14px'>x" + count + "</span>";
        }

        setText("<html><center>" + name + countText + "</center></html>");

        addActionListener(e -> shopUI.showItemDetails(item));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2.setColor(BG_PRESS);
        } else if (getModel().isRollover()) {
            g2.setColor(BG_HOVER);
        } else {
            g2.setColor(BG_NORMAL);
        }
        g2.fillRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g);

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(1, 1, getWidth() - 3, getHeight() - 3);

        g2.dispose();
    }
}