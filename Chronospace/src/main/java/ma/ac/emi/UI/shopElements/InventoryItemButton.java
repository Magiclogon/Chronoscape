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
import ma.ac.emi.UI.ShopUI;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.gamelogic.shop.ShopItem;

public class InventoryItemButton extends JButton {
    private static final long serialVersionUID = 1L;

    private static final Color BG_NORMAL = new Color(45, 45, 55);
    private static final Color BG_HOVER  = new Color(65, 65, 75);
    private static final Color BG_PRESS  = new Color(30, 30, 40);
    private static final String FONT_NAME = "ByteBounce";

    private static final int ICON_PADDING = 10;

    private Color borderColor = new Color(180, 180, 190);
    private Sprite icon = null;

    private int count;
    
    public InventoryItemButton(ShopUI shopUI, ShopItem item, int count) {
    	this.count = count;
    	
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
            case LEGENDARY: borderColor = new Color(255, 215,   0); break;
            case EPIC:      borderColor = new Color(160,  32, 240); break;
            case RARE:      borderColor = new Color( 65, 105, 225); break;
            case COMMON:    borderColor = new Color( 50, 205,  50); break;
            default:        borderColor = new Color(100, 100, 110); break;
        }

        // Load icon if a path is provided
        String iconPath = item.getItemDefinition().getIconPath();
        if (iconPath != null && !iconPath.isBlank()) {
            icon = AssetsLoader.getSprite(iconPath);
            setText("");
        }else {
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

        // ── 1. Background ────────────────────────────────────────────────────
        Graphics2D g2bg = (Graphics2D) g.create();
        g2bg.setColor(getModel().isPressed() ? BG_PRESS
                    : getModel().isRollover() ? BG_HOVER
                    : BG_NORMAL);
        g2bg.fillRect(0, 0, getWidth(), getHeight());
        g2bg.dispose();

        // ── 2. Icon ──────────────────────────────────────────────────────────
        int iconX = 0, iconY = 0, drawW = 0, drawH = 0;
        if (icon != null) {
            Graphics2D g2icon = (Graphics2D) g.create();
            g2icon.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            int imgW = icon.getWidth();
            int imgH = icon.getHeight();

            // Available area after padding
            int availW = getWidth()  - ICON_PADDING * 2;
            int availH = getHeight() - ICON_PADDING * 2;

            double scale = Math.min((double) availW / imgW, (double) availH / imgH);
            drawW = (int) (imgW * scale);
            drawH = (int) (imgH * scale);

            // Center within the padded area
            iconX = ICON_PADDING + (availW - drawW) / 2;
            iconY = ICON_PADDING + (availH - drawH) / 2;

            g2icon.drawImage(icon.getSprite(), iconX, iconY, drawW, drawH, null);
            g2icon.dispose();
        }

        // ── 3. Count badge (bottom-right of icon) ────────────────────────────
        if (count > 1) {
            String badge = "x" + count;
            Graphics2D g2badge = (Graphics2D) g.create();
            g2badge.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Font badgeFont = new Font(FONT_NAME, Font.PLAIN, 16);
            g2badge.setFont(badgeFont);

            int badgePad  = 3;
            int textW = g2badge.getFontMetrics().stringWidth(badge);
            int textH = g2badge.getFontMetrics().getAscent();

            int badgeW = textW + badgePad * 2;
            int badgeH = textH + badgePad * 2;

            // Anchor to bottom-right of the icon
            int badgeX = getWidth() - badgeW ;
            int badgeY = getHeight() - badgeH;

            // Dark pill background
            g2badge.setColor(new Color(0, 0, 0, 180));
            g2badge.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 6, 6);

            // Yellow text
            g2badge.setColor(new Color(255, 215, 0));
            g2badge.drawString(badge, badgeX + badgePad, badgeY + badgePad + textH - 2);

            g2badge.dispose();
        }

        // ── 4. Text — skip entirely, no name shown ───────────────────────────
        // (remove super.paintComponent call or call with no text set)
        super.paintComponent(g); // still needed for Swing internals, but setText("") so nothing renders
    
        Graphics2D g2border = (Graphics2D) g.create();
        g2border.setColor(borderColor);
        g2border.setStroke(new BasicStroke(2));
        g2border.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
        g2border.dispose();
    }
}