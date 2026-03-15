package ma.ac.emi.UI.shopElements;

import java.awt.*;
import javax.swing.*;
import ma.ac.emi.UI.MenuStyle;
import ma.ac.emi.UI.ShopUI;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.gamelogic.shop.ShopItem;

public class InventoryItemButton extends JButton {
    private static final long serialVersionUID = 1L;

    private static final Color BG_NORMAL  = new Color(45, 45, 55);
    private static final Color BG_HOVER   = new Color(65, 65, 75);
    private static final Color BG_PRESS   = new Color(30, 30, 40);
    private static final int   ICON_PADDING = 10;

    private Color borderColor = MenuStyle.TEXT_BORDER;
    private Sprite icon = null;
    private int count;

    public InventoryItemButton(ShopUI shopUI, ShopItem item, int count) {
        this.count = count;

        setPreferredSize(shopUI.inventoryButtonSize);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(MenuStyle.FONT_BODY);
        setForeground(Color.WHITE);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

        if (item == null) {
            setText("");
            setEnabled(false);
            return;
        }

        switch (item.getItemDefinition().getRarity()) {
            case LEGENDARY: borderColor = new Color(255, 215,   0); break;
            case EPIC:      borderColor = new Color(160,  32, 240); break;
            case RARE:      borderColor = new Color( 65, 105, 225); break;
            case COMMON:    borderColor = new Color( 50, 205,  50); break;
            default:        borderColor = new Color(100, 100, 110); break;
        }

        String iconPath = item.getItemDefinition().getIconPath();
        if (iconPath != null && !iconPath.isBlank()) {
            icon = AssetsLoader.getSprite(iconPath);
            setText("");
        } else {
            String name = item.getItemDefinition().getName();
            String countText = count > 1
                    ? "<br><span style='color:yellow; font-size:14px'>x" + count + "</span>"
                    : "";
            setText("<html><center>" + name + countText + "</center></html>");
        }

        addActionListener(e -> shopUI.showItemDetails(item));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2bg = (Graphics2D) g.create();
        g2bg.setColor(getModel().isPressed() ? BG_PRESS
                    : getModel().isRollover() ? BG_HOVER
                    : BG_NORMAL);
        g2bg.fillRect(0, 0, getWidth(), getHeight());
        g2bg.dispose();

        if (icon != null) {
            Graphics2D g2icon = (Graphics2D) g.create();
            g2icon.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            int availW = getWidth()  - ICON_PADDING * 2;
            int availH = getHeight() - ICON_PADDING * 2;
            double scale = Math.min((double) availW / icon.getWidth(), (double) availH / icon.getHeight());
            int drawW = (int)(icon.getWidth()  * scale);
            int drawH = (int)(icon.getHeight() * scale);
            int iconX = ICON_PADDING + (availW - drawW) / 2;
            int iconY = ICON_PADDING + (availH - drawH) / 2;

            g2icon.drawImage(icon.getSprite(), iconX, iconY, drawW, drawH, null);
            g2icon.dispose();
        }

        if (count > 1) {
            String badge = "x" + count;
            Graphics2D g2b = (Graphics2D) g.create();
            g2b.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2b.setFont(MenuStyle.FONT_SMALL);
            FontMetrics fm = g2b.getFontMetrics();
            int pad    = 3;
            int badgeW = fm.stringWidth(badge) + pad * 2;
            int badgeH = fm.getAscent()        + pad * 2;
            int badgeX = getWidth()  - badgeW;
            int badgeY = getHeight() - badgeH;
            g2b.setColor(new Color(0, 0, 0, 180));
            g2b.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 6, 6);
            g2b.setColor(new Color(255, 215, 0));
            g2b.drawString(badge, badgeX + pad, badgeY + pad + fm.getAscent() - 2);
            g2b.dispose();
        }

        super.paintComponent(g);

        Graphics2D g2border = (Graphics2D) g.create();
        g2border.setColor(borderColor);
        g2border.setStroke(new BasicStroke(2));
        g2border.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
        g2border.dispose();
    }
}