package ma.ac.emi.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import ma.ac.emi.UI.MenuStyle;
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
import java.util.HashMap;
import java.util.Map;

public class ShopUI extends JPanel {

    // ── Shop-specific colors not covered by MenuStyle ─────────────────────
    private static final Color BG_HEADER  = new Color(10, 10, 15);
    private static final Color BORDER_DIM = new Color(60, 60, 70);
    private static final Color ACCENT_GOLD = new Color(255, 215, 0);

    public final Dimension inventoryButtonSize = new Dimension(80, 80);

    private JLabel moneyLabel;
    private JPanel heroPanel, shopPanel, bagPanel;
    private JPanel availableItemsGrid, statsContainer, detailsContainer;
    private InventoryScrollable weaponPane, statItemsPane;
    private RetroButton nextWaveButton, rerollButton;

    public ShopUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(MenuStyle.BG_DARK);
        add(createHeader(), BorderLayout.NORTH);

        JPanel contentGrid = new JPanel(new GridBagLayout());
        contentGrid.setBackground(MenuStyle.BG_DARK);
        contentGrid.setBorder(new EmptyBorder(10, 10, 10, 10));

        heroPanel = createSectionPanel("HERO STATUS");
        statsContainer = new JPanel();
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setBackground(MenuStyle.BG_PANEL);
        heroPanel.add(statsContainer, BorderLayout.CENTER);

        shopPanel = createSectionPanel("MERCHANT");
        availableItemsGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        availableItemsGrid.setBackground(MenuStyle.BG_PANEL);
        shopPanel.add(availableItemsGrid, BorderLayout.CENTER);

        JPanel rerollContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rerollContainer.setBackground(MenuStyle.BG_PANEL);
        rerollContainer.setBorder(new EmptyBorder(10, 0, 0, 0));
        rerollButton = new RetroButton("REROLL", RetroButton.Style.SOLID, ACCENT_GOLD, Color.BLACK);
        rerollButton.setPreferredSize(new Dimension(150, MenuStyle.BTN_HEIGHT_SM));
        rerollButton.addActionListener(e -> {
            GameController.getInstance().getShopManager().refreshAvailableItems();
            refresh();
        });
        rerollContainer.add(rerollButton);
        shopPanel.add(rerollContainer, BorderLayout.SOUTH);

        bagPanel = createSectionPanel("INVENTORY & INSPECT");
        JPanel rightSplit = new JPanel(new GridLayout(2, 1, 0, 10));
        rightSplit.setBackground(MenuStyle.BG_PANEL);

        JTabbedPane invTabs = createRetroTabbedPane();
        weaponPane    = new InventoryScrollable(this, "");
        statItemsPane = new InventoryScrollable(this, "");
        invTabs.addTab("WEAPONS", weaponPane);
        invTabs.addTab("ITEMS",   statItemsPane);
        rightSplit.add(invTabs);

        detailsContainer = new JPanel();
        detailsContainer.setLayout(new BoxLayout(detailsContainer, BoxLayout.Y_AXIS));
        detailsContainer.setBackground(MenuStyle.BG_PANEL);
        detailsContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane detailsScroll = new JScrollPane(detailsContainer);
        detailsScroll.getViewport().setBackground(MenuStyle.BG_PANEL);
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
        header.setBackground(BG_HEADER);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        moneyLabel = new JLabel("GOLD: 0");
        moneyLabel.setFont(MenuStyle.FONT_HEADER);
        moneyLabel.setForeground(ACCENT_GOLD);

        nextWaveButton = new RetroButton("START WAVE >", RetroButton.Style.SOLID, MenuStyle.ACCENT, Color.BLACK);
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
        // All stats use the same neutral color — no distracting rainbow
        statsContainer.add(createStatRow("HP",    String.format("%.0f/%.0f", player.getHp(), player.getHpMax())));
        statsContainer.add(Box.createVerticalStrut(10));
        statsContainer.add(createStatRow("SPEED", String.format("%.0f",     player.getSpeed())));
        statsContainer.add(Box.createVerticalStrut(10));
        statsContainer.add(createStatRow("MIGHT", String.format("%.1f",     player.getStrength())));
        statsContainer.add(Box.createVerticalStrut(10));
        statsContainer.add(createStatRow("REGEN", String.format("%.1f/s",   player.getRegenerationSpeed())));

        statsContainer.add(Box.createVerticalStrut(30));
        JLabel equipHeader = new JLabel("EQUIPPED LOADOUT");
        equipHeader.setFont(MenuStyle.FONT_BODY);
        equipHeader.setForeground(MenuStyle.TEXT_GRAY);
        equipHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsContainer.add(equipHeader);
        statsContainer.add(Box.createVerticalStrut(10));

        JPanel equippedGrid = new JPanel(new GridLayout(1, 3, 5, 0));
        equippedGrid.setBackground(MenuStyle.BG_PANEL);
        equippedGrid.setMaximumSize(new Dimension(300, 80));
        WeaponItem[] weapons = player.getInventory().getEquippedWeapons();
        for (int i = 0; i < 3; i++) {
            if (i < weapons.length && weapons[i] != null) {
                equippedGrid.add(new InventoryItemButton(this, weapons[i], 1));
            } else {
                JLabel empty = new JLabel("EMPTY", SwingConstants.CENTER);
                empty.setFont(MenuStyle.FONT_SMALL);
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
        nameLbl.setFont(MenuStyle.FONT_HEADER);
        nameLbl.setForeground(ACCENT_GOLD);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rarityLbl = new JLabel(item.getItemDefinition().getRarity().toString());
        rarityLbl.setFont(MenuStyle.FONT_SMALL);
        rarityLbl.setForeground(MenuStyle.ACCENT);
        rarityLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descArea = new JTextArea(item.getItemDefinition().getDescription());
        descArea.setFont(MenuStyle.FONT_BODY);
        descArea.setForeground(MenuStyle.TEXT_MAIN);
        descArea.setBackground(MenuStyle.BG_PANEL);
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
                "SELL FOR " + (int)(item.getPrice() * ShopManager.SELLING_PERCENTAGE) + "$",
                RetroButton.Style.DANGER, MenuStyle.ACCENT_RED, Color.WHITE);
        sellBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        sellBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, MenuStyle.BTN_HEIGHT_SM));
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
        panel.setBackground(MenuStyle.BG_PANEL);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lbl = new JLabel("SLOT");
        lbl.setFont(MenuStyle.FONT_BODY);
        lbl.setForeground(MenuStyle.TEXT_GRAY);

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
        p.setBackground(MenuStyle.BG_PANEL);
        Border line   = new LineBorder(MenuStyle.TEXT_BORDER, 2);
        Border titled = BorderFactory.createTitledBorder(line, " " + title + " ",
                TitledBorder.CENTER, TitledBorder.TOP,
                MenuStyle.FONT_BODY, MenuStyle.TEXT_BORDER);
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

    /** Single neutral color for all stat values — no per-stat colors. */
    private JPanel createStatRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(MenuStyle.BG_PANEL);
        row.setMaximumSize(new Dimension(300, 30));

        JLabel l = new JLabel(label);
        l.setFont(MenuStyle.FONT_BODY);
        l.setForeground(MenuStyle.TEXT_GRAY);

        JLabel v = new JLabel(value);
        v.setFont(MenuStyle.FONT_BODY);
        v.setForeground(MenuStyle.TEXT_MAIN);   // uniform neutral white

        JLabel dots = new JLabel(" . . . . . . . . . . . . . . . . . . . ");
        dots.setFont(MenuStyle.FONT_SMALL);
        dots.setForeground(new Color(60, 60, 70));
        dots.setHorizontalAlignment(SwingConstants.CENTER);

        row.add(l,    BorderLayout.WEST);
        row.add(dots, BorderLayout.CENTER);
        row.add(v,    BorderLayout.EAST);
        return row;
    }

    private JTabbedPane createRetroTabbedPane() {
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(MenuStyle.FONT_BODY);
        tab.setFocusable(false);
        tab.setBackground(MenuStyle.BG_PANEL);
        tab.setForeground(MenuStyle.TEXT_BORDER);
        tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tab.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override protected void installDefaults() {
                super.installDefaults();
                highlight = lightHighlight = shadow = darkShadow = focus = MenuStyle.BG_PANEL;
                tabPane.setBorder(BorderFactory.createEmptyBorder());
            }
            @Override protected void paintTabBackground(Graphics g, int tp, int ti, int x, int y, int w, int h, boolean sel) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(MenuStyle.BG_DARK); g2.fillRect(x, y, w, h);
                g2.setColor(sel ? new Color(45,45,55) : new Color(35,35,45)); g2.fillRect(x+1, y+1, w-2, h-2);
                g2.dispose();
            }
            @Override protected void paintTabBorder(Graphics g, int tp, int ti, int x, int y, int w, int h, boolean sel) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (sel) { g2.setColor(MenuStyle.ACCENT); g2.fillRect(x, y+h-3, w, 3); }
                g2.setColor(BORDER_DIM); g2.drawRect(x, y, w-1, h);
                g2.dispose();
            }
            @Override protected void paintFocusIndicator(Graphics g, int tp, java.awt.Rectangle[] r, int ti, java.awt.Rectangle ir, java.awt.Rectangle tr, boolean sel) {}
            @Override protected void paintContentBorder(Graphics g, int tp, int si) {
                Graphics2D g2 = (Graphics2D) g.create();
                int tabH = calculateTabAreaHeight(tp, runCount, maxTabHeight);
                int w = tabPane.getWidth(), h = tabPane.getHeight();
                g2.setColor(MenuStyle.BG_PANEL); g2.fillRect(0, tabH, w, h-tabH);
                g2.setColor(BORDER_DIM); g2.drawLine(0, tabH, w, tabH);
                g2.dispose();
            }
            @Override protected Insets getContentBorderInsets(int tp) { return new Insets(1,0,0,0); }
            @Override protected Insets getTabAreaInsets(int tp)       { return new Insets(4,4,0,4); }
            @Override protected int calculateTabHeight(int tp, int ti, int fh) { return 34; }
        });
        return tab;
    }
}