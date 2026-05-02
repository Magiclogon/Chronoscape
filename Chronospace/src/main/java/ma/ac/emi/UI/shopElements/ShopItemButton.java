package ma.ac.emi.UI.shopElements;

import java.awt.*;
import javax.swing.*;
import ma.ac.emi.UI.MenuStyle;
import ma.ac.emi.UI.ShopUI;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.shop.ShopItem;
import ma.ac.emi.gamelogic.shop.UpgradeItem;
import ma.ac.emi.gamelogic.shop.UpgradeItemDefinition;

public class ShopItemButton extends JButton {
    private static final long serialVersionUID = 1L;

    private static final Color BG_NORMAL     = new Color(18, 18, 22);
    private static final Color BG_HOVER      = new Color(28, 28, 35);
    private static final Color BG_PRESS      = new Color(10, 10, 14);
    private static final Color STAT_POSITIVE = new Color(80, 200, 80);
    private static final Color STAT_NEGATIVE = MenuStyle.ACCENT_RED;
    private static final Color STAT_NEUTRAL  = MenuStyle.TEXT_BORDER;
    private static final Color PRICE_COLOR   = MenuStyle.ACCENT;
    private static final Color ACCENT_GOLD   = new Color(255, 215, 0);

    private static int PADDING     = 12;
    private static final int ICON_SIZE   = 56;
    private static final int PRICE_BAR_H = 36;

    private Color  borderColor = new Color(60, 60, 70);
    private Sprite icon        = null;

    private final String   itemName;
    private final String   itemType;
    private final String   itemPrice;
    private final String[] statLines;

    private int     iconX, iconY, drawW, drawH;
    private boolean geometryReady = false;
    private boolean isUpgradeItem = false;

    public ShopItemButton(ShopUI shopUI, ShopItem item) {
        if (item == null) {
            itemName  = ""; itemPrice = ""; itemType = ""; statLines = new String[]{};
            return;
        }

        this.itemName  = item.getItemDefinition().getName();
        this.itemType  = item.getItemDefinition().getRarity().name().substring(0, 1).toUpperCase()
                + item.getItemDefinition().getRarity().name().substring(1).toLowerCase();
        this.itemPrice = String.valueOf(item.getPrice());
        // getStatsDescription() uses "---" to separate flavor text from stat lines.
        // We only want the stat lines here — the flavor text goes nowhere (name + rarity suffice).
        String full = item.getItemDefinition().getStatsDescription();
        String[] parts = full.split("---", 2);
        this.statLines = (parts.length > 1 ? parts[1] : parts[0]).trim().split("\n");

        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setText("");

        switch (item.getItemDefinition().getRarity()) {
            case LEGENDARY: borderColor = new Color(255, 215,   0); break;
            case EPIC:      borderColor = new Color(160,  32, 240); break;
            case RARE:      borderColor = new Color( 65, 105, 225); break;
            case COMMON:    borderColor = new Color( 50, 205,  50); break;
            default:        borderColor = new Color( 60,  60,  70); break;
        }

        isUpgradeItem = item instanceof UpgradeItem;

        String iconPath = item.getItemDefinition().getIconPath();
        if (iconPath != null && !iconPath.isBlank())
            icon = AssetsLoader.getSprite(iconPath);

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                shopUI.playHoverSound();
            }
        });

        addActionListener(e -> {
            GameController.getInstance().getShopManager().purchaseItem(item);
            shopUI.playBuySound();
            shopUI.refresh();
        });

    }

    private void buildGeometry() {
        double scale = Math.min((double) ICON_SIZE / icon.getWidth(),
                (double) ICON_SIZE / icon.getHeight());
        drawW = (int)(icon.getWidth()  * scale);
        drawH = (int)(icon.getHeight() * scale);
        iconX = PADDING;
        iconY = PADDING;
        geometryReady = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,     RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int w = getWidth(), h = getHeight();

        // Background
        g2.setColor(getModel().isPressed() ? BG_PRESS
                : getModel().isRollover() ? BG_HOVER
                  : BG_NORMAL);
        g2.fillRoundRect(0, 0, w, h, 12, 12);


        // Icon box
        int boxSize = ICON_SIZE + PADDING;
        g2.setColor(new Color(35, 35, 42));
        g2.fillRoundRect(PADDING, PADDING, boxSize, boxSize, 8, 8);

        if (icon != null) {
            if (!geometryReady) buildGeometry();

            int cx, cy;

            if(isUpgradeItem) {
                cx = PADDING;
                cy = PADDING;
                g2.drawImage(icon.getSprite(), cx, cy, boxSize, boxSize, null);

            }else {
                cx = PADDING + (boxSize - drawW) / 2;
                cy = PADDING + (boxSize - drawH) / 2;
                g2.drawImage(icon.getSprite(), cx, cy, drawW, drawH, null);

            }

        }

        g2.setColor(borderColor.darker());
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(PADDING, PADDING, boxSize, boxSize, 8, 8);

        // Name + type
        int textX    = PADDING + boxSize + PADDING;
        int maxTextW = w - textX - PADDING;
        g2.setFont(MenuStyle.FONT_BODY);
        g2.setColor(Color.WHITE);
        int nameH = drawWrappedString(g2, itemName, textX, PADDING + g2.getFontMetrics().getAscent(), maxTextW);

        g2.setFont(MenuStyle.FONT_SMALL);
        g2.setColor(MenuStyle.TEXT_GRAY);
        g2.drawString(itemType, textX, PADDING + nameH + g2.getFontMetrics().getAscent());

        // Stat lines
        g2.setFont(MenuStyle.FONT_SMALL);
        FontMetrics fmStat = g2.getFontMetrics();
        int statY    = PADDING + boxSize + PADDING + fmStat.getAscent();
        int maxStatW = w - PADDING * 2;
        for (String line : statLines) {
            if (line.isBlank()) continue;
            statY += drawColoredStatLine(g2, line.trim(), PADDING, statY, maxStatW);
        }

        // Price bar
        int barY = h - PRICE_BAR_H - PADDING / 2;
        g2.setColor(new Color(30, 30, 38));
        g2.fillRoundRect(PADDING, barY, w - PADDING * 2, PRICE_BAR_H, 10, 10);

        g2.setFont(MenuStyle.FONT_BODY);
        FontMetrics fmPrice = g2.getFontMetrics();
        int priceW   = fmPrice.stringWidth(itemPrice);
        int coinSize = 14;
        int priceX   = (w - (priceW + coinSize + 6)) / 2;
        int priceTextY = barY + (PRICE_BAR_H + fmPrice.getAscent() - fmPrice.getDescent()) / 2 - 1;

        g2.setColor(Color.WHITE);
        g2.drawString(itemPrice, priceX, priceTextY);

        int coinX = priceX + priceW + 6;
        int coinY = barY + (PRICE_BAR_H - coinSize) / 2;
        g2.setColor(new Color(50, 180, 50));
        g2.fillOval(coinX, coinY, coinSize, coinSize);
        g2.setColor(MenuStyle.ACCENT);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(coinX, coinY, coinSize, coinSize);

        // Outer border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(1, 1, w - 3, h - 3, 12, 12);

        g2.dispose();
    }

    /**
     * Renders a stat line word-by-word with per-word color based on sign,
     * correctly tracking wrapped lines and returning total height consumed.
     * Lines prefixed with "INDENT:" are rendered with a fixed pixel indent.
     */
    private int drawColoredStatLine(Graphics2D g2, String text, int x, int y, int maxWidth) {
        FontMetrics fm     = g2.getFontMetrics();
        int         startY = y;

        // INDENT: prefix — render with a fixed left indent, strip the prefix
        boolean indented = text.startsWith("INDENT:");
        if (indented) {
            text = text.substring("INDENT:".length());
            x       += fm.stringWidth("    "); // ~4 spaces worth of indent
            maxWidth -= fm.stringWidth("    ");
        }

        int curX = x;

        // Section headers (e.g. "PASSIVE WHEN IN USE") — all-caps, no digits
        if (text.equals(text.toUpperCase()) && !text.matches(".*\\d.*")) {
            g2.setColor(MenuStyle.TEXT_GRAY);
            g2.drawString(text, x, y);
            return fm.getHeight();
        }

        for (String word : text.split(" ")) {
            if (word.isBlank()) continue;
            int ww = fm.stringWidth(word + " ");
            if (curX + ww > x + maxWidth) { curX = x; y += fm.getHeight(); }

            if (word.matches("^[+\\d].*"))  g2.setColor(STAT_POSITIVE);
            else if (word.startsWith("-"))   g2.setColor(STAT_NEGATIVE);
            else                             g2.setColor(STAT_NEUTRAL);

            g2.drawString(word + " ", curX, y);
            curX += ww;
        }
        return (y - startY) + fm.getHeight();
    }

    private int drawWrappedString(Graphics2D g2, String text, int x, int y, int maxWidth) {
        FontMetrics fm    = g2.getFontMetrics();
        String[]    words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineY = y;
        for (String word : words) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(test) > maxWidth && line.length() > 0) {
                g2.drawString(line.toString(), x, lineY);
                lineY += fm.getHeight();
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0) { g2.drawString(line.toString(), x, lineY); lineY += fm.getHeight(); }
        return lineY - y;
    }
}