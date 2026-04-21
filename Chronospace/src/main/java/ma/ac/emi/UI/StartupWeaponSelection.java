package ma.ac.emi.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import ma.ac.emi.UI.MenuStyle;
import ma.ac.emi.UI.component.RetroScrollBar;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.*;
import ma.ac.emi.gamelogic.weapon.WeaponItemFactory;
import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehaviorDefinition;
import ma.ac.emi.gamelogic.weapon.behavior.passive.PassiveWeaponEffectDefinition;
import ma.ac.emi.gamelogic.weapon.behavior.passive.WeaponPassiveDefinition;

public class StartupWeaponSelection extends JPanel {

    private static final int GRID_COLUMNS = 3;
    private static final int GRID_GAP     = 12;
    private static final int CARD_HEIGHT  = 260;

    private Runnable onWeaponSelected;
    private JPanel   weaponsGrid;

    public StartupWeaponSelection() {
        setLayout(new BorderLayout());
        setBackground(MenuStyle.BG_DARK);

        // ── Header ────────────────────────────────────────────────────────
        JLabel title = new JLabel("CHOOSE YOUR STARTING WEAPON", SwingConstants.CENTER);
        title.setFont(MenuStyle.FONT_HEADER);
        title.setForeground(MenuStyle.ACCENT);
        title.setBorder(new EmptyBorder(80, 0, 10, 0));

        JLabel subtitle = new JLabel("Select a common weapon to begin your run", SwingConstants.CENTER);
        subtitle.setFont(MenuStyle.FONT_BODY);
        subtitle.setForeground(MenuStyle.TEXT_GRAY);

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(MenuStyle.BG_DARK);
        header.add(title);
        header.add(subtitle);

        // ── Weapons grid ──────────────────────────────────────────────────
        weaponsGrid = new JPanel();
        weaponsGrid.setBackground(MenuStyle.BG_DARK);

        RetroScrollBar vBar = new RetroScrollBar(JScrollBar.VERTICAL);
        vBar.setPreferredSize(new Dimension(10, 0));

        JScrollPane scrollPane = new JScrollPane(weaponsGrid);
        scrollPane.setBackground(MenuStyle.BG_DARK);
        scrollPane.getViewport().setBackground(MenuStyle.BG_DARK);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBar(vBar);
        vBar.setUnitIncrement(CARD_HEIGHT / 3);

        // ── Content grid: spacers on each side cap the centre width ───────
        JPanel contentGrid = new JPanel(new GridBagLayout());
        contentGrid.setBackground(MenuStyle.BG_DARK);
        contentGrid.setBorder(new EmptyBorder(10, 10, 20, 10));

        GridBagConstraints spacer = new GridBagConstraints();
        spacer.fill    = GridBagConstraints.BOTH;
        spacer.weighty = 1.0;
        spacer.gridy   = 0;

        spacer.gridx   = 0;
        spacer.weightx = 0.15;
        contentGrid.add(Box.createHorizontalGlue(), spacer);

        GridBagConstraints centre = new GridBagConstraints();
        centre.fill    = GridBagConstraints.BOTH;
        centre.weighty = 1.0;
        centre.gridy   = 0;
        centre.gridx   = 1;
        centre.weightx = 0.70;
        contentGrid.add(scrollPane, centre);

        spacer.gridx   = 2;
        spacer.weightx = 0.15;
        contentGrid.add(Box.createHorizontalGlue(), spacer);

        add(header,      BorderLayout.NORTH);
        add(contentGrid, BorderLayout.CENTER);
    }

    // ── Visibility trigger ─────────────────────────────────────────────────
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) loadCommonWeapons();
    }

    // ── Load & layout weapons ──────────────────────────────────────────────
    private void loadCommonWeapons() {
        weaponsGrid.removeAll();

        List<WeaponItemDefinition> commonWeapons = ItemLoader.getInstance()
            .getAllWeaponDefinitions()
            .stream()
            .filter(def -> true)
//            .filter(def -> def.getRarity() == Rarity.COMMON)
            .toList();

        int weaponCount = commonWeapons.size();
        int rows = (int) Math.ceil(weaponCount / (double) GRID_COLUMNS);

        weaponsGrid.setLayout(new GridLayout(rows, GRID_COLUMNS, GRID_GAP, GRID_GAP));

        for (WeaponItemDefinition def : commonWeapons) {
            weaponsGrid.add(new WeaponCard(def, this::selectWeapon));
        }

        int prefHeight = rows * CARD_HEIGHT + Math.max(0, rows - 1) * GRID_GAP;
        weaponsGrid.setPreferredSize(new Dimension(0, prefHeight));

        revalidate();
        repaint();
    }

    // ── Weapon selection ───────────────────────────────────────────────────
    private void selectWeapon(WeaponItemDefinition def) {
        Player player = Player.getInstance();
        player.getInventory().init();

        WeaponItem chosenWeapon = WeaponItemFactory.getInstance()
            .createWeaponItem(def.getId());

        player.getInventory().addItem(chosenWeapon);
        player.getInventory().equipWeapon(chosenWeapon, 0);
        player.initWeapons();

        ShopManager shopManager = GameController.getInstance().getShopManager();
        if(shopManager != null) {
        	shopManager.onStartingWeaponPicked(def);
        }else {
        	System.out.println("ShopManager is not initialized!");
        }
        
        if (onWeaponSelected != null) onWeaponSelected.run();
    }

    public void setOnWeaponSelected(Runnable callback) {
        this.onWeaponSelected = callback;
    }

    // ── Passive descriptions ───────────────────────────────────────────────
    private static List<String> getPassiveDescriptions(WeaponItemDefinition def) {
        List<String> descriptions = new java.util.ArrayList<>();
        for (WeaponBehaviorDefinition b : def.getBehaviorDefinitions()) {
            String desc = null;
            if (b instanceof PassiveWeaponEffectDefinition p) desc = p.describe();
            else if (b instanceof WeaponPassiveDefinition p)  desc = p.describe();
            if (desc != null && !desc.isBlank()) descriptions.add(desc);
        }
        return descriptions;
    }

    // ══════════════════════════════════════════════════════════════════════
    // WeaponCard
    // ══════════════════════════════════════════════════════════════════════
    private class WeaponCard extends JButton {
        private static final long serialVersionUID = 1L;

        private final WeaponItemDefinition def;
        private final java.util.function.Consumer<WeaponItemDefinition> onSelect;
        private Sprite icon;
        private Color  borderColor;

        private int drawW, drawH;
        private boolean geometryReady = false;

        private static final int PADDING     = 12;
        private static final int ICON_SIZE   = 56;
        private static final int PRICE_BAR_H = 36;

        public WeaponCard(WeaponItemDefinition def,
                          java.util.function.Consumer<WeaponItemDefinition> onSelect) {
            this.def      = def;
            this.onSelect = onSelect;

            switch (def.getRarity()) {
                case LEGENDARY: borderColor = new Color(255, 215, 0);   break;
                case EPIC:      borderColor = new Color(160, 32, 240);   break;
                case RARE:      borderColor = MenuStyle.ACCENT_PURPLE;   break;
                case COMMON:    borderColor = MenuStyle.ACCENT;          break;
                default:        borderColor = MenuStyle.TEXT_GRAY;       break;
            }

            String iconPath = def.getIconPath();
            if (iconPath != null && !iconPath.isBlank()) {
                icon = AssetsLoader.getSprite(iconPath);
            }

            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setText("");

            addActionListener(e -> onSelect.accept(def));
        }

        private void buildGeometry() {
            if (icon == null) return;
            double scale = Math.min((double) ICON_SIZE / icon.getWidth(),
                                    (double) ICON_SIZE / icon.getHeight());
            drawW = (int)(icon.getWidth()  * scale);
            drawH = (int)(icon.getHeight() * scale);
            geometryReady = true;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,       RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,      RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(getModel().isPressed() ? new Color(20, 20, 26)
                      : getModel().isRollover() ? new Color(38, 38, 48)
                      : MenuStyle.BG_PANEL);
            g2.fillRoundRect(0, 0, w, h, MenuStyle.ARC * 2, MenuStyle.ARC * 2);

            int boxSize = ICON_SIZE + PADDING;
            g2.setColor(MenuStyle.BG_DARK);
            g2.fillRoundRect(PADDING, PADDING, boxSize, boxSize, MenuStyle.ARC, MenuStyle.ARC);
            g2.setColor(borderColor.darker());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(PADDING, PADDING, boxSize, boxSize, MenuStyle.ARC, MenuStyle.ARC);

            if (icon != null) {
                if (!geometryReady) buildGeometry();
                int cx = PADDING + (boxSize - drawW) / 2;
                int cy = PADDING + (boxSize - drawH) / 2;
                g2.drawImage(icon.getSprite(), cx, cy, drawW, drawH, null);
            }

            int textX    = PADDING + boxSize + PADDING;
            int maxTextW = w - textX - PADDING;

            g2.setFont(MenuStyle.FONT_BODY);
            g2.setColor(MenuStyle.TEXT_MAIN);
            int nameH = drawWrappedString(g2, def.getName(), textX,
                PADDING + g2.getFontMetrics().getAscent(), maxTextW);

            g2.setFont(MenuStyle.FONT_SMALL);
            g2.setColor(MenuStyle.TEXT_GRAY);
            String typeStr = def.getRarity().toString().substring(0, 1).toUpperCase()
                           + def.getRarity().toString().substring(1).toLowerCase();
            g2.drawString(typeStr, textX, PADDING + nameH + g2.getFontMetrics().getAscent());

            g2.setFont(MenuStyle.FONT_SMALL);
            FontMetrics fmStat = g2.getFontMetrics();
            int statY    = PADDING + boxSize + PADDING + fmStat.getAscent();
            int maxStatW = w - PADDING * 2;

            String[] statLines = {
                String.format("DMG: %.0f", def.getDamage()),
                String.format("SPD: %.1f", def.getAttackSpeed()),
                String.format("MAG: %d",   def.getMagazineSize()),
                String.format("RNG: %.0f", def.getRange())
            };

            for (String line : statLines) {
                if (line.isBlank()) continue;
                statY += drawColoredStatLine(g2, line, PADDING, statY, maxStatW);
            }

            List<String> passives = getPassiveDescriptions(def);
            if (!passives.isEmpty()) {
                statY += fmStat.getHeight() / 2;
                g2.setColor(MenuStyle.ACCENT_PURPLE);
                g2.drawString("PASSIVE:", PADDING, statY);
                statY += fmStat.getHeight();

                g2.setColor(MenuStyle.TEXT_GRAY);
                for (String passive : passives) {
                    String wrapped = wrapText(g2, passive, maxStatW - PADDING * 2);
                    for (String line : wrapped.split("\n")) {
                        g2.drawString("  " + line, PADDING, statY);
                        statY += fmStat.getHeight();
                    }
                }
            }

            int barY = h - PRICE_BAR_H - PADDING / 2;
            g2.setColor(MenuStyle.BG_DARK);
            g2.fillRoundRect(PADDING, barY, w - PADDING * 2, PRICE_BAR_H, MenuStyle.ARC, MenuStyle.ARC);

            g2.setFont(MenuStyle.FONT_BODY);
            FontMetrics fmBtn = g2.getFontMetrics();
            String btnText = "SELECT";
            int btnX = (w - fmBtn.stringWidth(btnText)) / 2;
            int btnY = barY + (PRICE_BAR_H + fmBtn.getAscent() - fmBtn.getDescent()) / 2 - 1;
            g2.setColor(MenuStyle.ACCENT);
            g2.drawString(btnText, btnX, btnY);

            g2.setColor(getModel().isRollover() ? MenuStyle.ACCENT : borderColor);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, MenuStyle.ARC * 2, MenuStyle.ARC * 2);

            g2.dispose();
        }

        private int drawColoredStatLine(Graphics2D g2, String text, int x, int y, int maxWidth) {
            FontMetrics fm = g2.getFontMetrics();
            int colonIdx = text.indexOf(':');
            if (colonIdx > 0) {
                String label  = text.substring(0, colonIdx + 1);
                String value  = text.substring(colonIdx + 1).trim();
                int    labelW = fm.stringWidth(label + " ");
                g2.setColor(MenuStyle.TEXT_GRAY);
                g2.drawString(label + " ", x, y);
                g2.setColor(MenuStyle.ACCENT);
                g2.drawString(value, x + labelW, y);
            } else {
                g2.setColor(MenuStyle.TEXT_GRAY);
                g2.drawString(text, x, y);
            }
            return fm.getHeight();
        }

        private String wrapText(Graphics2D g2, String text, int maxWidth) {
            FontMetrics   fm     = g2.getFontMetrics();
            StringBuilder result = new StringBuilder();
            StringBuilder line   = new StringBuilder();
            for (String word : text.split(" ")) {
                String test = line.length() == 0 ? word : line + " " + word;
                if (fm.stringWidth(test) > maxWidth && line.length() > 0) {
                    if (result.length() > 0) result.append("\n");
                    result.append(line);
                    line = new StringBuilder(word);
                } else {
                    line = new StringBuilder(test);
                }
            }
            if (line.length() > 0) {
                if (result.length() > 0) result.append("\n");
                result.append(line);
            }
            return result.toString();
        }

        private int drawWrappedString(Graphics2D g2, String text, int x, int y, int maxWidth) {
            FontMetrics   fm    = g2.getFontMetrics();
            StringBuilder line  = new StringBuilder();
            int           lineY = y;
            for (String word : text.split(" ")) {
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
}