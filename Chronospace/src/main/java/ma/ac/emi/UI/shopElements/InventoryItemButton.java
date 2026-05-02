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

    // Colors sampled directly from the Shop's "Icon Box"
    private static final Color BOX_FILL = new Color(35, 35, 42);
    private static final int ICON_PADDING = 10;
    private static final int ROUNDING = 8; // Matches Shop icon box

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

        if (item == null) {
            setEnabled(false);
            return;
        }

        // Rarity Logic
        switch (item.getItemDefinition().getRarity()) {
            case LEGENDARY: borderColor = new Color(255, 215,   0); break;
            case EPIC:      borderColor = new Color(160,  32, 240); break;
            case RARE:      borderColor = new Color( 65, 105, 225); break;
            case COMMON:    borderColor = new Color( 50, 205,  50); break;
            default:        borderColor = new Color( 60,  60,  70); break;
        }

        String iconPath = item.getItemDefinition().getIconPath();
        if (iconPath != null && !iconPath.isBlank()) {
            icon = AssetsLoader.getSprite(iconPath);
        }

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                shopUI.playHoverSound();
            }
        });

        addActionListener(e -> shopUI.showItemDetails(item, true));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int w = getWidth();
        int h = getHeight();

        // 1. Background - using the Shop's inner box color
        g2.setColor(BOX_FILL);
        g2.fillRoundRect(0, 0, w, h, ROUNDING, ROUNDING);

        // 2. Icon Drawing
        if (icon != null) {
            int availW = w - ICON_PADDING * 2;
            int availH = h - ICON_PADDING * 2;
            double scale = Math.min((double) availW / icon.getWidth(), (double) availH / icon.getHeight());
            int drawW = (int)(icon.getWidth() * scale);
            int drawH = (int)(icon.getHeight() * scale);
            int iconX = (w - drawW) / 2;
            int iconY = (h - drawH) / 2;
            g2.drawImage(icon.getSprite(), iconX, iconY, drawW, drawH, null);
        }

        // 3. Count Badge
        if (count > 1) {
            drawBadge(g2, w, h);
        }

        // 4. Border - Using the darker rarity stroke from the shop
        g2.setColor(borderColor.darker());
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(1, 1, w - 2, h - 2, ROUNDING, ROUNDING);

        g2.dispose();
    }

    private void drawBadge(Graphics2D g2, int w, int h) {
        String badge = "x" + count;
        g2.setFont(MenuStyle.FONT_SMALL);
        FontMetrics fm = g2.getFontMetrics();
        int pad = 3;
        int badgeW = fm.stringWidth(badge) + pad * 2;
        int badgeH = fm.getAscent() + pad;
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(w - badgeW - 2, h - badgeH - 2, badgeW, badgeH, 4, 4);
        g2.setColor(new Color(255, 215, 0));
        g2.drawString(badge, w - badgeW - 2 + pad, h - badgeH - 2 + fm.getAscent() - 1);
    }
}