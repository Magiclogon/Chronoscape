package ma.ac.emi.UI.shopElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import ma.ac.emi.UI.ShopUI;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.shop.ShopItem;

public class ShopItemButton extends JButton {
    private static final long serialVersionUID = 1L;

    private static final Color BG_NORMAL     = new Color(18, 18, 22);
    private static final Color BG_HOVER      = new Color(28, 28, 35);
    private static final Color BG_PRESS      = new Color(10, 10, 14);
    private static final Color STAT_POSITIVE = new Color(80, 200, 80);
    private static final Color STAT_NEGATIVE = new Color(220, 70, 70);
    private static final Color STAT_NEUTRAL  = new Color(180, 180, 190);
    private static final Color PRICE_COLOR   = new Color(80, 200, 80);
    private static final String FONT_NAME    = "ByteBounce";

    private static final int PADDING     = 12;
    private static final int ICON_SIZE   = 56;
    private static final int PRICE_BAR_H = 36;

    private Color borderColor = new Color(60, 60, 70);
    private Sprite icon = null;

    private final String   itemName;
    private final String   itemType;
    private final String   itemPrice;
    private final String[] statLines;

    private int iconX, iconY, drawW, drawH;
    private boolean geometryReady = false;

    public ShopItemButton(ShopUI shopUI, ShopItem item) {
        if (item == null) {
            itemName  = "";
            itemPrice = "";
            itemType  = "";
            statLines = new String[]{};
            return;
        }

        this.itemName  = item.getItemDefinition().getName();
        this.itemType  = item.getItemDefinition().getRarity().name().substring(0, 1).toUpperCase()
                       + item.getItemDefinition().getRarity().name().substring(1).toLowerCase();
        this.itemPrice = String.valueOf(item.getPrice());
        this.statLines = item.getItemDefinition().getStatsDescription().split("\n");

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

        String iconPath = item.getItemDefinition().getIconPath();
        if (iconPath != null && !iconPath.isBlank()) {
            icon = AssetsLoader.getSprite(iconPath);
        }

        addActionListener(e -> {
            GameController.getInstance().getShopManager().purchaseItem(item);
            shopUI.refresh();
        });
    }

    private void buildGeometry() {
        int imgW = icon.getWidth();
        int imgH = icon.getHeight();
        double scale = Math.min((double) ICON_SIZE / imgW, (double) ICON_SIZE / imgH);
        drawW = (int) (imgW * scale);
        drawH = (int) (imgH * scale);
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

        int w = getWidth();
        int h = getHeight();

        // ── 1. Background ─────────────────────────────────────────────────
        g2.setColor(getModel().isPressed() ? BG_PRESS
                  : getModel().isRollover() ? BG_HOVER
                  : BG_NORMAL);
        g2.fillRoundRect(0, 0, w, h, 12, 12);

        // ── 2. Icon box (dark rounded square, top-left) ───────────────────
        int boxSize = ICON_SIZE + PADDING;
        g2.setColor(new Color(35, 35, 42));
        g2.fillRoundRect(PADDING, PADDING, boxSize, boxSize, 8, 8);
        g2.setColor(borderColor.darker());
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(PADDING, PADDING, boxSize, boxSize, 8, 8);

        if (icon != null) {
            if (!geometryReady) buildGeometry();
            int cx = PADDING + (boxSize - drawW) / 2;
            int cy = PADDING + (boxSize - drawH) / 2;
            g2.drawImage(icon.getSprite(), cx, cy, drawW, drawH, null);
        }

        // ── 3. Name + type (right of icon) ────────────────────────────────
        int textX    = PADDING + boxSize + PADDING;
        int maxTextW = w - textX - PADDING;

        g2.setFont(new Font(FONT_NAME, Font.PLAIN, 22));
        g2.setColor(Color.WHITE);
        int nameHeightUsed = drawWrappedString(g2, itemName, textX, PADDING + g2.getFontMetrics().getAscent(), maxTextW);

        g2.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
        g2.setColor(new Color(150, 150, 165));
        FontMetrics fmType = g2.getFontMetrics();
        g2.drawString(itemType, textX, PADDING + nameHeightUsed + fmType.getAscent());

        // ── 4. Stat lines (below icon box) ────────────────────────────────
        g2.setFont(new Font(FONT_NAME, Font.PLAIN, 17));
        FontMetrics fmStat = g2.getFontMetrics();
        int statY = PADDING + boxSize + PADDING + fmStat.getAscent();
        int maxStatW = w - PADDING * 2;

        for (String line : statLines) {
            if (line.isBlank()) continue;
            String trimmed = line.trim();
            if (trimmed.startsWith("+"))      g2.setColor(STAT_POSITIVE);
            else if (trimmed.startsWith("-")) g2.setColor(STAT_NEGATIVE);
            else                              g2.setColor(STAT_NEUTRAL);
            statY += drawWrappedString(g2, trimmed, PADDING, statY, maxStatW);
        }

        // ── 5. Price bar (bottom) ─────────────────────────────────────────
        int barY = h - PRICE_BAR_H - PADDING / 2;
        g2.setColor(new Color(30, 30, 38));
        g2.fillRoundRect(PADDING, barY, w - PADDING * 2, PRICE_BAR_H, 10, 10);

        g2.setFont(new Font(FONT_NAME, Font.PLAIN, 22));
        FontMetrics fmPrice = g2.getFontMetrics();
        int priceW    = fmPrice.stringWidth(itemPrice);
        int coinSize  = 14;
        int totalW    = priceW + coinSize + 6;
        int priceX    = (w - totalW) / 2;
        int priceTextY = barY + (PRICE_BAR_H + fmPrice.getAscent() - fmPrice.getDescent()) / 2 - 1;

        g2.setColor(Color.WHITE);
        g2.drawString(itemPrice, priceX, priceTextY);

        // Coin dot
        int coinX = priceX + priceW + 6;
        int coinY = barY + (PRICE_BAR_H - coinSize) / 2;
        g2.setColor(new Color(50, 180, 50));
        g2.fillOval(coinX, coinY, coinSize, coinSize);
        g2.setColor(new Color(80, 220, 80));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(coinX, coinY, coinSize, coinSize);

        // ── 6. Outer border ───────────────────────────────────────────────
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(1, 1, w - 3, h - 3, 12, 12);

        g2.dispose();
    }

    private int drawWrappedString(Graphics2D g2, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
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
        if (line.length() > 0) {
            g2.drawString(line.toString(), x, lineY);
            lineY += fm.getHeight();
        }
        return lineY - y;
    }
}