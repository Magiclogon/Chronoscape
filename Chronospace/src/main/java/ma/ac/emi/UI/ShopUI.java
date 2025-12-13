package ma.ac.emi.UI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

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

    // --- ROGUELIKE PALETTE ---
    private static final Color BG_DARK      = new Color(18, 18, 24);   // Deep Void
    private static final Color PANEL_BG     = new Color(30, 30, 38);   // Panel Background
    private static final Color BORDER_LIGHT = new Color(180, 180, 190); // Retro White/Grey
    private static final Color TEXT_MAIN    = new Color(235, 235, 245);
    private static final Color ACCENT_GOLD  = new Color(255, 215, 0);  // Money
    private static final Color ACCENT_RED   = new Color(220, 60, 60);  // Health/Sell
    private static final Color ACCENT_GREEN = new Color(80, 200, 100); // Buy/Next Wave

    // --- FONTS ---
    private static final String FONT_NAME = "ByteBounce";
    // Pixel fonts need to be large to be readable
    private static final Font FONT_HEADER = new Font(FONT_NAME, Font.PLAIN, 32);
    private static final Font FONT_BODY   = new Font(FONT_NAME, Font.PLAIN, 20);
    private static final Font FONT_SMALL  = new Font(FONT_NAME, Font.PLAIN, 16);
    public final Dimension inventoryButtonSize = new Dimension(80, 80);

    // --- COMPONENTS ---
    private JLabel moneyLabel;

    // The three main columns
    private JPanel heroPanel;      // Left
    private JPanel shopPanel;      // Center
    private JPanel bagPanel;       // Right

    // Dynamic containers
    private JPanel availableItemsGrid;
    private JPanel statsContainer;
    private JPanel detailsContainer;

    // Inventory references
    private InventoryScrollable weaponPane;
    private InventoryScrollable statItemsPane;

    // Buttons
    private RetroButton nextWaveButton;
    private RetroButton rerollButton;

    public ShopUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_DARK);

        // 1. TOP HEADER (Money & Wave Control)
        add(createHeader(), BorderLayout.NORTH);

        // 2. MAIN CONTENT (3 Columns)
        JPanel contentGrid = new JPanel(new GridLayout(1, 3, 10, 0)); // 3 Cols, 10px gap
        contentGrid.setBackground(BG_DARK);
        contentGrid.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- LEFT: HERO STATS & EQUIPPED ---
        heroPanel = createSectionPanel("HERO STATUS");
        statsContainer = new JPanel();
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setBackground(PANEL_BG);
        heroPanel.add(statsContainer, BorderLayout.CENTER);
        contentGrid.add(heroPanel);

        // --- CENTER: SHOP ---
        shopPanel = createSectionPanel("MERCHANT");

        // Shop Grid
        availableItemsGrid = new JPanel(new GridLayout(3, 2, 8, 8)); // 3x2 Grid for items
        availableItemsGrid.setBackground(PANEL_BG);
        shopPanel.add(availableItemsGrid, BorderLayout.CENTER);

        // Reroll Area (Bottom of Center)
        JPanel rerollContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rerollContainer.setBackground(PANEL_BG);
        rerollContainer.setBorder(new EmptyBorder(10, 0, 0, 0));

        rerollButton = new RetroButton("REROLL", ACCENT_GOLD, Color.BLACK);
        rerollButton.setPreferredSize(new Dimension(150, 40));
        rerollButton.addActionListener(e -> {
            GameController.getInstance().getShopManager().refreshAvailableItems();
            refresh();
        });
        rerollContainer.add(rerollButton);
        shopPanel.add(rerollContainer, BorderLayout.SOUTH);

        contentGrid.add(shopPanel);

        // --- RIGHT: INVENTORY & DETAILS ---
        bagPanel = createSectionPanel("INVENTORY & INSPECT");

        // Split right panel: Top is Inventory, Bottom is Details
        JPanel rightSplit = new JPanel(new GridLayout(2, 1, 0, 10));
        rightSplit.setBackground(PANEL_BG);

        // Inventory Tabs
        JTabbedPane invTabs = createRetroTabbedPane();
        weaponPane = new InventoryScrollable(this, ""); // Title handled by tab
        statItemsPane = new InventoryScrollable(this, "");

        invTabs.addTab("WEAPONS", weaponPane);
        invTabs.addTab("ITEMS", statItemsPane);
        rightSplit.add(invTabs);

        // Item Details Area
        detailsContainer = new JPanel();
        detailsContainer.setLayout(new BoxLayout(detailsContainer, BoxLayout.Y_AXIS));
        detailsContainer.setBackground(PANEL_BG);
        detailsContainer.setBorder(createRetroBorder()); // Inner border for details

        // Wrap details in a scroll pane just in case description is long
        JScrollPane detailsScroll = new JScrollPane(detailsContainer);
        detailsScroll.getViewport().setBackground(PANEL_BG);
        detailsScroll.setBorder(null);
        rightSplit.add(detailsScroll);

        bagPanel.add(rightSplit, BorderLayout.CENTER);
        contentGrid.add(bagPanel);

        add(contentGrid, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(10, 10, 15)); // Darker top bar
        header.setBorder(new CompoundBorder(
                new LineBorder(BORDER_LIGHT, 0, false), // No outside border
                new EmptyBorder(15, 20, 15, 20)         // Padding
        ));

        moneyLabel = new JLabel("GOLD: 0");
        moneyLabel.setFont(FONT_HEADER);
        moneyLabel.setForeground(ACCENT_GOLD);

        nextWaveButton = new RetroButton("START WAVE >", ACCENT_GREEN, Color.BLACK);
        nextWaveButton.setPreferredSize(new Dimension(160, 45));
        nextWaveButton.addActionListener((e) -> {
            Player.getInstance().initWeapons();
            GameController.getInstance().resumeGame();
        });

        header.add(moneyLabel, BorderLayout.WEST);
        header.add(nextWaveButton, BorderLayout.EAST);

        // Bottom line of header
        JPanel separator = new JPanel();
        separator.setPreferredSize(new Dimension(0, 4));
        separator.setBackground(BORDER_LIGHT);
        header.add(separator, BorderLayout.SOUTH);

        return header;
    }

    // --- REFRESH LOGIC ---
    public void refresh() {
        Player player = Player.getInstance();
        ShopManager shop = GameController.getInstance().getShopManager();

        // 1. Header
        moneyLabel.setText("GOLD: " + (int)player.getMoney());
        rerollButton.setText("REROLL (" + shop.getRerollPrice() + "$)");

        // 2. Stats (Left Panel)
        statsContainer.removeAll();
        statsContainer.add(createStatRow("HP", String.format("%.0f/%.0f", player.getHp(), player.getHpMax()), ACCENT_RED));
        statsContainer.add(Box.createVerticalStrut(10));
        statsContainer.add(createStatRow("SPEED", String.format("%.0f", player.getSpeed()), new Color(100, 200, 255)));
        statsContainer.add(Box.createVerticalStrut(10));
        statsContainer.add(createStatRow("MIGHT", String.format("%.1f", player.getStrength()), new Color(255, 150, 50)));
        statsContainer.add(Box.createVerticalStrut(10));
        statsContainer.add(createStatRow("REGEN", String.format("%.1f/s", player.getRegenerationSpeed()), new Color(100, 255, 100)));

        // Add Equipped Weapons Visuals below stats
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

        // Fill slots (assuming max 3 weapons)
        WeaponItem[] weapons = player.getInventory().getEquippedWeapons();
        for(int i=0; i<3; i++) {
            if(i < weapons.length && weapons[i] != null) {
                // If you have an icon for the weapon, use it here.
                // For now reusing InventoryItemButton
                equippedGrid.add(new InventoryItemButton(this, weapons[i], 1));
            } else {
                // Empty Slot Visual
                JLabel emptySlot = new JLabel("EMPTY");
                emptySlot.setFont(FONT_SMALL);
                emptySlot.setForeground(Color.DARK_GRAY);
                emptySlot.setHorizontalAlignment(SwingConstants.CENTER);
                emptySlot.setBorder(new LineBorder(Color.DARK_GRAY, 2));
                equippedGrid.add(emptySlot);
            }
        }
        statsContainer.add(equippedGrid);

        // 3. Shop Items (Center Panel)
        availableItemsGrid.removeAll();
        for (ShopItem item : shop.getAvailableItems()) {
            availableItemsGrid.add(new ShopItemButton(this, item));
        }

        // 4. Inventory (Right Panel)
        weaponPane.getPanel().removeAll();
        refreshInventoryCategory(player.getInventory().getWeaponItems(), weaponPane);

        statItemsPane.getPanel().removeAll();
        refreshInventoryCategory(player.getInventory().getUpgradeItems(), statItemsPane);

        revalidate();
        repaint();
    }

    private void refreshInventoryCategory(java.util.List<? extends ShopItem> items, InventoryScrollable pane) {
        Map<String, Integer> counts = new HashMap<>();
        Map<String, ShopItem> refs = new HashMap<>();

        for (ShopItem item : items) {
            String id = item.getItemDefinition().getId();
            counts.put(id, counts.getOrDefault(id, 0) + 1);
            refs.putIfAbsent(id, item);
        }
        for (String id : counts.keySet()) {
            pane.add(new InventoryItemButton(this, refs.get(id), counts.get(id)));
        }
    }

    // --- ITEM DETAILS PANEL ---
    public void showItemDetails(ShopItem item) {
        detailsContainer.removeAll();

        // Name
        JLabel nameLbl = new JLabel(item.getItemDefinition().getName().toUpperCase());
        nameLbl.setFont(FONT_HEADER);
        nameLbl.setForeground(ACCENT_GOLD);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Rarity
        JLabel rarityLbl = new JLabel(item.getItemDefinition().getRarity().toString());
        rarityLbl.setFont(FONT_SMALL);
        rarityLbl.setForeground(Color.CYAN);
        rarityLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Description Box
        JTextArea descArea = new JTextArea(item.getItemDefinition().getDescription());
        descArea.setFont(FONT_BODY);
        descArea.setForeground(TEXT_MAIN);
        descArea.setBackground(PANEL_BG);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBorder(new EmptyBorder(10, 0, 10, 0));
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add to panel
        detailsContainer.add(nameLbl);
        detailsContainer.add(rarityLbl);
        detailsContainer.add(Box.createVerticalStrut(10));
        detailsContainer.add(descArea);

        // Equip Logic (If Weapon)
        if (item instanceof WeaponItem) {
            detailsContainer.add(Box.createVerticalStrut(10));
            detailsContainer.add(createEquipControls((WeaponItem)item));
        }

        // Sell Button
        detailsContainer.add(Box.createVerticalGlue());
        RetroButton sellBtn = new RetroButton("SELL FOR " + (int)(item.getPrice() * 0.5) + "$", ACCENT_RED, Color.WHITE);
        sellBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        sellBtn.setMaximumSize(new Dimension(300, 40));
        sellBtn.addActionListener(e -> {
            GameController.getInstance().getShopManager().sellItem(item);
            refresh();
            detailsContainer.removeAll();
            detailsContainer.repaint();
        });

        detailsContainer.add(Box.createVerticalStrut(10));
        detailsContainer.add(sellBtn);

        detailsContainer.revalidate();
        detailsContainer.repaint();
    }

    private JPanel createEquipControls(WeaponItem weaponItem) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(PANEL_BG);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel("SLOT: ");
        lbl.setFont(FONT_BODY);
        lbl.setForeground(Color.GRAY);

        JComboBox<String> box = new JComboBox<>();
        box.addItem("- BACKPACK -");
        box.addItem("SLOT 1");
        box.addItem("SLOT 2");
        box.addItem("SLOT 3");
        box.setFont(FONT_SMALL);

        Integer equippedIndex = Player.getInstance().isWeaponEquipped(weaponItem);
        if (equippedIndex != null && equippedIndex >= 0) {
            box.setSelectedIndex(equippedIndex + 1);
        } else {
            box.setSelectedIndex(0);
        }

        box.addActionListener(e -> {
            int selectedIndex = box.getSelectedIndex();
            if (selectedIndex == 0) Player.getInstance().getInventory().unequipWeapon(weaponItem);
            else Player.getInstance().getInventory().equipWeapon(weaponItem, selectedIndex - 1);
            refresh();
        });

        panel.add(lbl);
        panel.add(box);
        return panel;
    }

    // --- HELPER VISUALS ---

    private JPanel createSectionPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PANEL_BG);
        // Custom Titled Border with Pixel aesthetic
        Border line = new LineBorder(BORDER_LIGHT, 2);
        Border titled = BorderFactory.createTitledBorder(line, " " + title + " ",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                FONT_BODY, BORDER_LIGHT);
        p.setBorder(new CompoundBorder(titled, new EmptyBorder(10, 10, 10, 10)));
        return p;
    }

    private JPanel createStatRow(String label, String value, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(PANEL_BG);
        row.setMaximumSize(new Dimension(300, 30));

        JLabel l = new JLabel(label);
        l.setFont(FONT_BODY);
        l.setForeground(Color.GRAY);

        JLabel v = new JLabel(value);
        v.setFont(FONT_BODY);
        v.setForeground(color);

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);

        // Dashed line between label and value
        JLabel dots = new JLabel(" . . . . . . . . . . . . . . . . . . . ");
        dots.setFont(FONT_SMALL);
        dots.setForeground(new Color(60, 60, 70));
        dots.setHorizontalAlignment(SwingConstants.CENTER);
        row.add(dots, BorderLayout.CENTER);

        return row;
    }

    private Border createRetroBorder() {
        return new LineBorder(BORDER_LIGHT, 2);
    }

    private JTabbedPane createRetroTabbedPane() {
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(FONT_BODY);
        tab.setForeground(Color.BLACK); // Text color of tabs
        tab.setBackground(BORDER_LIGHT); // Bg color of tabs
        return tab;
    }

    // --- CUSTOM RETRO BUTTON CLASS ---
    private class RetroButton extends JButton {
        private Color normalColor;
        private Color hoverColor;

        public RetroButton(String text, Color bg, Color fg) {
            super(text);
            this.normalColor = bg;
            this.hoverColor = bg.brighter();

            setFont(FONT_BODY);
            setForeground(fg);
            setBackground(bg);
            setFocusPainted(false);
            setBorderPainted(false); // We draw our own border
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF); // Pixel feel

            // Draw Background
            if (getModel().isPressed()) g2.setColor(normalColor.darker());
            else if (getModel().isRollover()) g2.setColor(hoverColor);
            else g2.setColor(normalColor);

            // Draw rectangle (Solid block)
            g2.fillRect(2, 2, getWidth()-4, getHeight()-4);

            // Draw Border (Thick)
            g2.setColor(BORDER_LIGHT);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(1, 1, getWidth()-3, getHeight()-3);

            // Draw Text
            super.paintComponent(g);
        }
    }
}