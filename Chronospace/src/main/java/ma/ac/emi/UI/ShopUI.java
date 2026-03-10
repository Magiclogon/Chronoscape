package ma.ac.emi.UI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import ma.ac.emi.UI.component.RetroButton;
import ma.ac.emi.UI.component.RetroScrollBar;
import ma.ac.emi.UI.component.RetroSpinner;
import ma.ac.emi.UI.shopElements.InventoryItemButton;
import ma.ac.emi.UI.shopElements.InventoryScrollable;
import ma.ac.emi.UI.shopElements.ShopItemButton;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.ShopItem;
import ma.ac.emi.gamelogic.shop.ShopManager;
import ma.ac.emi.gamelogic.shop.WeaponItem;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ShopUI extends JPanel {

    private static final Color BG_DARK      = new Color(18, 18, 24);
    private static final Color PANEL_BG     = new Color(30, 30, 38);
    private static final Color PANEL_DARK   = new Color(22, 22, 28);
    private static final Color BORDER_DIM   = new Color(60, 60, 70);
    private static final Color BORDER_LIGHT = new Color(180, 180, 190);
    private static final Color TEXT_MAIN    = new Color(235, 235, 245);
    private static final Color TEXT_GRAY    = new Color(150, 150, 160);
    private static final Color ACCENT_GOLD  = new Color(255, 215, 0);
    private static final Color ACCENT_RED   = new Color(220, 60, 60);
    private static final Color ACCENT_GREEN = new Color(80, 200, 100);

    private static final String FONT_NAME = "ByteBounce";
    private static final Font FONT_HEADER = new Font(FONT_NAME, Font.PLAIN, 32);
    private static final Font FONT_BODY   = new Font(FONT_NAME, Font.PLAIN, 20);
    private static final Font FONT_SMALL  = new Font(FONT_NAME, Font.PLAIN, 16);
    public final Dimension inventoryButtonSize = new Dimension(80, 80);

    private JLabel moneyLabel;
    private JPanel heroPanel, shopPanel, bagPanel;
    private JPanel availableItemsGrid, statsContainer, detailsContainer;
    private InventoryScrollable weaponPane, statItemsPane;
    private RetroButton nextWaveButton, rerollButton;

    public ShopUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_DARK);
        add(createHeader(), BorderLayout.NORTH);

        JPanel contentGrid = new JPanel(new GridBagLayout());
        contentGrid.setBackground(BG_DARK);
        contentGrid.setBorder(new EmptyBorder(10, 10, 10, 10));

        heroPanel = createSectionPanel("HERO STATUS");
        statsContainer = new JPanel();
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setBackground(PANEL_BG);
        heroPanel.add(statsContainer, BorderLayout.CENTER);

        shopPanel = createSectionPanel("MERCHANT");
        availableItemsGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        availableItemsGrid.setBackground(PANEL_BG);
        shopPanel.add(availableItemsGrid, BorderLayout.CENTER);

        JPanel rerollContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rerollContainer.setBackground(PANEL_BG);
        rerollContainer.setBorder(new EmptyBorder(10, 0, 0, 0));
        rerollButton = new RetroButton("REROLL", RetroButton.Style.SOLID, ACCENT_GOLD, Color.BLACK);
        rerollButton.setPreferredSize(new Dimension(150, 38));
        rerollButton.addActionListener(e -> {
            GameController.getInstance().getShopManager().refreshAvailableItems();
            refresh();
        });
        rerollContainer.add(rerollButton);
        shopPanel.add(rerollContainer, BorderLayout.SOUTH);

        bagPanel = createSectionPanel("INVENTORY & INSPECT");
        JPanel rightSplit = new JPanel(new GridLayout(2, 1, 0, 10));
        rightSplit.setBackground(PANEL_BG);

        JTabbedPane invTabs = createRetroTabbedPane();
        weaponPane    = new InventoryScrollable(this, "");
        statItemsPane = new InventoryScrollable(this, "");
        invTabs.addTab("WEAPONS", weaponPane);
        invTabs.addTab("ITEMS",   statItemsPane);
        rightSplit.add(invTabs);

        detailsContainer = new JPanel();
        detailsContainer.setLayout(new BoxLayout(detailsContainer, BoxLayout.Y_AXIS));
        detailsContainer.setBackground(PANEL_BG);
        detailsContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane detailsScroll = new JScrollPane(detailsContainer);
        detailsScroll.getViewport().setBackground(PANEL_BG);
        detailsScroll.setBorder(new LineBorder(BORDER_DIM, 1));
        JScrollBar dBar = new RetroScrollBar(JScrollBar.VERTICAL);
        dBar.setPreferredSize(new Dimension(12, 0));
        detailsScroll.setVerticalScrollBar(dBar);
        rightSplit.add(detailsScroll);
        bagPanel.add(rightSplit, BorderLayout.CENTER);

        heroPanel.setMinimumSize(new Dimension(0, 0)); heroPanel.setPreferredSize(new Dimension(1, 1));
        shopPanel.setMinimumSize(new Dimension(0, 0)); shopPanel.setPreferredSize(new Dimension(1, 1));
        bagPanel.setMinimumSize(new Dimension(0, 0));  bagPanel.setPreferredSize(new Dimension(1, 1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.gridx = 0; gbc.weightx = 0.2; contentGrid.add(heroPanel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; contentGrid.add(shopPanel, gbc);
        gbc.gridx = 2; gbc.weightx = 0.3; contentGrid.add(bagPanel,  gbc);
        add(contentGrid, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(10, 10, 15));
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        moneyLabel = new JLabel("GOLD: 0");
        moneyLabel.setFont(FONT_HEADER);
        moneyLabel.setForeground(ACCENT_GOLD);

        nextWaveButton = new RetroButton("START WAVE >", RetroButton.Style.SOLID, ACCENT_GREEN, Color.BLACK);
        nextWaveButton.setPreferredSize(new Dimension(180, 44));
        nextWaveButton.addActionListener(e -> {
            Player.getInstance().initWeapons();
            GameController.getInstance().nextWave();
        });

        header.add(moneyLabel,     BorderLayout.WEST);
        header.add(nextWaveButton, BorderLayout.EAST);

        JPanel sep = new JPanel();
        sep.setPreferredSize(new Dimension(0, 2));
        sep.setBackground(BORDER_DIM);
        header.add(sep, BorderLayout.SOUTH);
        return header;
    }

    public void refresh() {
        Player player    = Player.getInstance();
        ShopManager shop = GameController.getInstance().getShopManager();

        moneyLabel.setText("GOLD: " + (int) player.getMoney());
        rerollButton.setText("REROLL (" + shop.getRerollPrice() + "$)");

        statsContainer.removeAll();
        statsContainer.add(createStatRow("HP",    String.format("%.0f/%.0f", player.getHp(), player.getHpMax()), ACCENT_RED));
        statsContainer.add(Box.createVerticalStrut(10));
        statsContainer.add(createStatRow("SPEED", String.format("%.0f",     player.getSpeed()),                  new Color(100, 200, 255)));
        statsContainer.add(Box.createVerticalStrut(10));
        statsContainer.add(createStatRow("MIGHT", String.format("%.1f",     player.getStrength()),               new Color(255, 150, 50)));
        statsContainer.add(Box.createVerticalStrut(10));
        statsContainer.add(createStatRow("REGEN", String.format("%.1f/s",   player.getRegenerationSpeed()),      new Color(100, 255, 100)));

        statsContainer.add(Box.createVerticalStrut(30));
        JLabel equipHeader = new JLabel("EQUIPPED LOADOUT");
        equipHeader.setFont(FONT_BODY);
        equipHeader.setForeground(Color.GRAY);
        equipHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsContainer.add(equipHeader);
        statsContainer.add(Box.createVerticalStrut(10));

        JPanel equippedGrid = new JPanel(new GridLayout(1, 3, 5, 0));
        equippedGrid.setBackground(PANEL_BG);
        equippedGrid.setMaximumSize(new Dimension(300, 80));
        WeaponItem[] weapons = player.getInventory().getEquippedWeapons();
        for (int i = 0; i < 3; i++) {
            if (i < weapons.length && weapons[i] != null) {
                equippedGrid.add(new InventoryItemButton(this, weapons[i], 1));
            } else {
                JLabel empty = new JLabel("EMPTY", SwingConstants.CENTER);
                empty.setFont(FONT_SMALL);
                empty.setForeground(Color.DARK_GRAY);
                empty.setBorder(new LineBorder(Color.DARK_GRAY, 2));
                equippedGrid.add(empty);
            }
        }
        statsContainer.add(equippedGrid);

        availableItemsGrid.removeAll();
        int i = 0;
        for (ShopItem item : shop.getAvailableItems()) {
            if (i >= 4) break;
            availableItemsGrid.add(new ShopItemButton(this, item));
            i++;
        }

        weaponPane.getPanel().removeAll();
        refreshInventoryCategory(player.getInventory().getWeaponItems(), weaponPane);
        statItemsPane.getPanel().removeAll();
        refreshInventoryCategory(player.getInventory().getUpgradeItems(), statItemsPane);

        revalidate();
        repaint();
    }

    private void refreshInventoryCategory(java.util.List<? extends ShopItem> items, InventoryScrollable pane) {
        Map<String, Integer> counts = new HashMap<>();
        Map<String, ShopItem> refs  = new HashMap<>();
        for (ShopItem item : items) {
            String id = item.getItemDefinition().getId();
            counts.put(id, counts.getOrDefault(id, 0) + 1);
            refs.putIfAbsent(id, item);
        }
        for (String id : counts.keySet())
            pane.add(new InventoryItemButton(this, refs.get(id), counts.get(id)));
    }

    public void showItemDetails(ShopItem item) {
        detailsContainer.removeAll();

        JLabel nameLbl = new JLabel(item.getItemDefinition().getName().toUpperCase());
        nameLbl.setFont(FONT_HEADER);
        nameLbl.setForeground(ACCENT_GOLD);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rarityLbl = new JLabel(item.getItemDefinition().getRarity().toString());
        rarityLbl.setFont(FONT_SMALL);
        rarityLbl.setForeground(ACCENT_GREEN);
        rarityLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descArea = new JTextArea(item.getItemDefinition().getDescription());
        descArea.setFont(FONT_BODY);
        descArea.setForeground(TEXT_MAIN);
        descArea.setBackground(PANEL_BG);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBorder(new EmptyBorder(8, 0, 8, 0));
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsContainer.add(nameLbl);
        detailsContainer.add(rarityLbl);
        detailsContainer.add(Box.createVerticalStrut(6));
        detailsContainer.add(makeSeparator());
        detailsContainer.add(Box.createVerticalStrut(8));
        detailsContainer.add(descArea);

        if (item instanceof WeaponItem) {
            detailsContainer.add(Box.createVerticalStrut(8));
            detailsContainer.add(createEquipControls((WeaponItem) item));
        }

        detailsContainer.add(Box.createVerticalGlue());
        detailsContainer.add(Box.createVerticalStrut(8));

        RetroButton sellBtn = new RetroButton(
                "SELL FOR " + (int)(item.getPrice() * 0.5) + "$", RetroButton.Style.DANGER, ACCENT_RED, Color.WHITE);
        sellBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        sellBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        sellBtn.addActionListener(e -> {
            GameController.getInstance().getShopManager().sellItem(item);
            refresh();
            detailsContainer.removeAll();
            detailsContainer.repaint();
        });
        detailsContainer.add(sellBtn);

        detailsContainer.revalidate();
        detailsContainer.repaint();
    }

    private JPanel createEquipControls(WeaponItem weaponItem) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(PANEL_BG);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lbl = new JLabel("SLOT");
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_GRAY);

        Integer equippedIndex = Player.getInstance().isWeaponEquipped(weaponItem);
        int initial = (equippedIndex != null && equippedIndex >= 0) ? equippedIndex + 1 : 0;

        RetroSpinner spinner = new RetroSpinner(0, initial, 3, 1) {
            private static final long serialVersionUID = 1L;
            @Override protected String getDisplayValue(int v) {
                return v == 0 ? "BAG" : "  " + v;
            }
        };
        spinner.addChangeListener(e -> {
            int sel = spinner.getValue();
            if (sel == 0) Player.getInstance().getInventory().unequipWeapon(weaponItem);
            else          Player.getInstance().getInventory().equipWeapon(weaponItem, sel - 1);
            refresh();
        });

        panel.add(lbl,     BorderLayout.WEST);
        panel.add(spinner, BorderLayout.CENTER);
        return panel;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private JPanel createSectionPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PANEL_BG);
        Border line   = new LineBorder(BORDER_LIGHT, 2);
        Border titled = BorderFactory.createTitledBorder(line, " " + title + " ",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                FONT_BODY, BORDER_LIGHT);
        p.setBorder(new CompoundBorder(titled, new EmptyBorder(10, 10, 10, 10)));
        return p;
    }

    private JPanel makeSeparator() {
        JPanel sep = new JPanel();
        sep.setBackground(BORDER_DIM);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setPreferredSize(new Dimension(0, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }

    private JPanel createStatRow(String label, String value, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(PANEL_BG);
        row.setMaximumSize(new Dimension(300, 30));
        JLabel l = new JLabel(label);
        l.setFont(FONT_BODY); l.setForeground(Color.GRAY);
        JLabel v = new JLabel(value);
        v.setFont(FONT_BODY); v.setForeground(color);
        JLabel dots = new JLabel(" . . . . . . . . . . . . . . . . . . . ");
        dots.setFont(FONT_SMALL); dots.setForeground(new Color(60, 60, 70));
        dots.setHorizontalAlignment(SwingConstants.CENTER);
        row.add(l, BorderLayout.WEST); row.add(dots, BorderLayout.CENTER); row.add(v, BorderLayout.EAST);
        return row;
    }

    private JTabbedPane createRetroTabbedPane() {
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(FONT_BODY);
        tab.setFocusable(false);
        tab.setBackground(PANEL_BG);
        tab.setForeground(BORDER_LIGHT);
        tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tab.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override protected void installDefaults() {
                super.installDefaults();
                highlight = lightHighlight = shadow = darkShadow = focus = PANEL_BG;
                tabPane.setBorder(BorderFactory.createEmptyBorder());
            }
            @Override protected void paintTabBackground(Graphics g, int tp, int ti, int x, int y, int w, int h, boolean sel) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BG_DARK); g2.fillRect(x, y, w, h);
                g2.setColor(sel ? new Color(45,45,55) : new Color(35,35,45)); g2.fillRect(x+1, y+1, w-2, h-2);
                g2.dispose();
            }
            @Override protected void paintTabBorder(Graphics g, int tp, int ti, int x, int y, int w, int h, boolean sel) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (sel) { g2.setColor(ACCENT_GREEN); g2.fillRect(x, y+h-3, w, 3); }
                g2.setColor(new Color(60,60,70)); g2.drawRect(x, y, w-1, h);
                g2.dispose();
            }
            @Override protected void paintFocusIndicator(Graphics g, int tp, java.awt.Rectangle[] r, int ti, java.awt.Rectangle ir, java.awt.Rectangle tr, boolean sel) {}
            @Override protected void paintContentBorder(Graphics g, int tp, int si) {
                Graphics2D g2 = (Graphics2D) g.create();
                int tabH = calculateTabAreaHeight(tp, runCount, maxTabHeight);
                int w = tabPane.getWidth(), h = tabPane.getHeight();
                g2.setColor(PANEL_BG); g2.fillRect(0, tabH, w, h-tabH);
                g2.setColor(new Color(60,60,70)); g2.drawLine(0, tabH, w, tabH);
                g2.dispose();
            }
            @Override protected Insets getContentBorderInsets(int tp) { return new Insets(1,0,0,0); }
            @Override protected Insets getTabAreaInsets(int tp)       { return new Insets(4,4,0,4); }
            @Override protected int calculateTabHeight(int tp, int ti, int fh) { return 34; }
        });
        return tab;
    }
}