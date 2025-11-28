package ma.ac.emi.UI;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import ma.ac.emi.UI.shopElements.InventoryItemButton;
import ma.ac.emi.UI.shopElements.InventoryScrollable;
import ma.ac.emi.UI.shopElements.ShopItemButton;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.ShopItem;
import ma.ac.emi.gamelogic.shop.ShopManager;
import ma.ac.emi.gamelogic.shop.WeaponItem;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ShopUI handles the visual representation of the in-game shop.
 * It displays:
 *  - Player money
 *  - Available items for purchase
 *  - Inventory (weapons + stat items)
 *  - Player stats
 */
public class ShopUI extends JPanel {
    public final Dimension inventoryButtonSize = new Dimension(75, 75);

    private JLabel moneyLabel;
    private JPanel availableItemsPanel;
    private JPanel statsPanel;
    private JPanel itemDescriptionPanel;

    private InventoryScrollable weaponPane;
    private InventoryScrollable activeWeaponsPane;
    private InventoryScrollable statItemsPane;

    private JButton nextWaveButton;
    private JButton rerollButton;

    public ShopUI() {

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nextWaveButton = new JButton("Next Wave");
        nextWaveButton.addActionListener((e) -> {
            Player.getInstance().initWeapons();
            GameController.getInstance().resumeGame();
        });
        add(createTopPanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(30, 30, 30));
        centerPanel.add(createAvailableItemsPanel());

        JPanel rerollPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rerollPanel.setBackground(new Color(60, 60, 60));
        rerollButton = new JButton();
        rerollButton.addActionListener((e) -> {
            GameController.getInstance().getShopManager().refreshAvailableItems();
            refresh();
        });
        rerollPanel.add(rerollButton);

        centerPanel.add(rerollPanel);
        centerPanel.add(createInventoryPanel());
        add(centerPanel, BorderLayout.CENTER);

        JPanel descriptionPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        descriptionPanel.setBackground(new Color(60,60,60));
        descriptionPanel.add(createStatsPanel());
        descriptionPanel.add(createItemDescriptionPanel());
        add(descriptionPanel, BorderLayout.EAST);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding
        panel.setBackground(Color.BLACK);

        moneyLabel = new JLabel();
        moneyLabel.setForeground(Color.YELLOW);
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 20));

        panel.add(moneyLabel);
        panel.add(Box.createHorizontalGlue()); // pushes button to the right
        panel.add(nextWaveButton);
        return panel;
    }

    private JPanel createAvailableItemsPanel() {
        availableItemsPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        availableItemsPanel.setPreferredSize(new Dimension(availableItemsPanel.getPreferredSize().width,
                500));
        availableItemsPanel.setBackground(new Color(50, 50, 50));
        availableItemsPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "Available Items",
                        TitledBorder.LEADING,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14),
                        Color.WHITE
                )
        );
        return availableItemsPanel;
    }

    private JPanel createInventoryPanel() {
        JPanel inventoryPanel = new JPanel(new BorderLayout(10, 10));
        inventoryPanel.setPreferredSize(new Dimension(inventoryPanel.getPreferredSize().width, 400));
        inventoryPanel.setBackground(new Color(40, 40, 40));
        inventoryPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "Inventory",
                        TitledBorder.LEADING,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14),
                        Color.WHITE
                )
        );

        weaponPane = new InventoryScrollable(this, "Weapons");

        statItemsPane = new InventoryScrollable(this, "Stat Items");

        activeWeaponsPane = new InventoryScrollable(this, "Active Weapons");

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        bottomPanel.setBackground(new Color(40, 40, 40));
        bottomPanel.add(statItemsPane);
        bottomPanel.add(weaponPane);
        bottomPanel.add(activeWeaponsPane);

        inventoryPanel.add(bottomPanel, BorderLayout.CENTER);
        return inventoryPanel;
    }

    private JPanel createStatsPanel() {
        statsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        statsPanel.setBackground(new Color(50, 50, 50));
        statsPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "Player Stats",
                        TitledBorder.LEADING,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14),
                        Color.WHITE
                )
        );
        statsPanel.setPreferredSize(new Dimension(250, 700));
        return statsPanel;
    }

    public JPanel createItemDescriptionPanel() {
        itemDescriptionPanel = new JPanel();
        itemDescriptionPanel.setBackground(new Color(50, 50, 50));
        itemDescriptionPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "Item description",
                        TitledBorder.LEADING,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14),
                        Color.WHITE
                )
        );
        itemDescriptionPanel.setPreferredSize(new Dimension(250, 700));
        return itemDescriptionPanel;
    }

    public void refresh() {

        Player player = Player.getInstance();
        ShopManager shop = GameController.getInstance().getShopManager();

        // === Update money and reroll ===
        moneyLabel.setText("Money: " + (int)player.getMoney() + "$"); // Cast to int for cleaner look
        rerollButton.setText("Reroll (" + shop.getRerollPrice() + "$)");

        // === Update available shop items ===
        availableItemsPanel.removeAll();
        for (ShopItem item : shop.getAvailableItems()) {
            availableItemsPanel.add(new ShopItemButton(this, item));
        }

        // === Update equipped weapons ===
        activeWeaponsPane.getPanel().removeAll();
        for (WeaponItem item : player.getInventory().getEquippedWeapons()) {
            if (item != null) {
                activeWeaponsPane.add(new InventoryItemButton(this, item, 1));
            }
        }

        weaponPane.getPanel().removeAll();
        Map<String, Integer> weaponCounts = new HashMap<>();
        Map<String, ShopItem> weaponRefs = new HashMap<>();

        for (ShopItem item : player.getInventory().getWeaponItems()) {
            String id = item.getItemDefinition().getId();
            weaponCounts.put(id, weaponCounts.getOrDefault(id, 0) + 1);
            weaponRefs.putIfAbsent(id, item);
        }

        for (String id : weaponCounts.keySet()) {
            ShopItem ref = weaponRefs.get(id);
            int count = weaponCounts.get(id);
            weaponPane.add(new InventoryItemButton(this, ref, count));
        }

        statItemsPane.getPanel().removeAll();
        Map<String, Integer> statCounts = new HashMap<>();
        Map<String, ShopItem> statRefs = new HashMap<>();

        for (ShopItem item : player.getInventory().getUpgradeItems()) {
            String id = item.getItemDefinition().getId();
            statCounts.put(id, statCounts.getOrDefault(id, 0) + 1);
            statRefs.putIfAbsent(id, item);
        }

        for (String id : statCounts.keySet()) {
            ShopItem ref = statRefs.get(id);
            int count = statCounts.get(id);
            statItemsPane.add(new InventoryItemButton(this, ref, count));
        }

        statsPanel.removeAll();

        // Format: Name, Value, Color
        addStatRow("Health",
                String.format("%.0f / %.0f", player.getHp(), player.getHpMax()),
                new Color(100, 255, 100));

        addStatRow("Speed",
                String.format("%.0f", player.getSpeed()),
                new Color(100, 200, 255));

        addStatRow("Strength",
                String.format("%.1f", player.getStrength()),
                new Color(255, 100, 100));

        addStatRow("Regeneration",
                String.format("%.1f /s", player.getRegenerationSpeed()),
                new Color(255, 100, 255));

        statsPanel.revalidate();
        statsPanel.repaint();

        revalidate();
        repaint();
    }

    private JLabel createSlotLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setBackground(new Color(70, 70, 70));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        return label;
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    public void showItemDetails(ShopItem item) {
        itemDescriptionPanel.removeAll();

        JLabel nameLabel = new JLabel(item.getItemDefinition().getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel("<html>" + item.getItemDefinition().getDescription() + "</html>");
        descLabel.setForeground(Color.LIGHT_GRAY);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel rarityLabel = new JLabel("Rarity: " + item.getItemDefinition().getRarity());
        rarityLabel.setForeground(Color.CYAN);

        JLabel priceLabel = new JLabel("Price: $" + item.getPrice());
        priceLabel.setForeground(Color.YELLOW);

        itemDescriptionPanel.setBackground(new Color(40, 40, 40));
        itemDescriptionPanel.setLayout(new BoxLayout(itemDescriptionPanel, BoxLayout.Y_AXIS));
        itemDescriptionPanel.add(nameLabel);
        itemDescriptionPanel.add(Box.createVerticalStrut(5));
        itemDescriptionPanel.add(descLabel);
        itemDescriptionPanel.add(Box.createVerticalStrut(10));
        itemDescriptionPanel.add(rarityLabel);
        itemDescriptionPanel.add(priceLabel);

        //If the item is a WeaponItem, show its equip state
        if (item instanceof WeaponItem) {
            WeaponItem weaponItem = (WeaponItem) item;

            JLabel equipLabel = new JLabel("Equipped State:");
            equipLabel.setForeground(Color.WHITE);
            equipLabel.setFont(new Font("Arial", Font.BOLD, 13));

            JComboBox<String> equipStateBox = new JComboBox<>();
            equipStateBox.addItem("Unequipped");

            // Suppose your player has 3 weapon slots
            for (int i = 0; i < 3; i++) {
                equipStateBox.addItem("Slot " + i);
            }

            // Get equipped slot index
            Integer equippedIndex = Player.getInstance().isWeaponEquipped(weaponItem);
            if (equippedIndex != null && equippedIndex >= 0) {
                equipStateBox.setSelectedIndex(equippedIndex + 1);
            } else {
                equipStateBox.setSelectedIndex(0);
            }

            //Limit the size so it doesn’t stretch across the whole panel
            Dimension comboSize = new Dimension(150, 25);
            equipStateBox.setMaximumSize(comboSize);
            equipStateBox.setPreferredSize(comboSize);
            equipStateBox.setAlignmentX(Component.LEFT_ALIGNMENT);

            itemDescriptionPanel.add(Box.createVerticalStrut(5));
            itemDescriptionPanel.add(equipLabel);
            itemDescriptionPanel.add(Box.createVerticalStrut(3));
            itemDescriptionPanel.add(equipStateBox);

            equipStateBox.addActionListener(e -> {
                int selectedIndex = equipStateBox.getSelectedIndex();
                Player player = Player.getInstance();

                if (selectedIndex == 0) {
                    player.getInventory().unequipWeapon(weaponItem);
                } else {
                    int slotIndex = selectedIndex - 1;
                    player.getInventory().equipWeapon(weaponItem, slotIndex);
                }
                refresh();
            });
        }

        itemDescriptionPanel.add(Box.createVerticalStrut(20));
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        itemDescriptionPanel.add(separator);
        itemDescriptionPanel.add(Box.createVerticalStrut(10));

        JPanel sellPanel = new JPanel();
        sellPanel.setBackground(new Color(50, 50, 50));
        sellPanel.setLayout(new BoxLayout(sellPanel, BoxLayout.Y_AXIS));
        sellPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sellLabel = new JLabel("Sell this item:");
        sellLabel.setForeground(Color.WHITE);
        sellLabel.setFont(new Font("Arial", Font.BOLD, 13));

        JButton sellButton = new JButton("Sell");
        sellButton.setBackground(new Color(200, 50, 50));
        sellButton.setForeground(Color.WHITE);
        sellButton.setFocusPainted(false);
        sellButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        sellButton.setMaximumSize(new Dimension(120, 30));

        sellButton.addActionListener(e -> {
            GameController.getInstance().getShopManager().sellItem(item);
            refresh();
        });

        sellPanel.add(sellLabel);
        sellPanel.add(Box.createVerticalStrut(5));
        sellPanel.add(sellButton);

        itemDescriptionPanel.add(sellPanel);

        itemDescriptionPanel.revalidate();
        itemDescriptionPanel.repaint();
    }

    private void addStatRow(String name, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(50, 50, 50));
        row.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        JLabel nameLbl = new JLabel(name + ": ");
        nameLbl.setForeground(Color.LIGHT_GRAY);
        nameLbl.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel valLbl = new JLabel(value);
        valLbl.setForeground(valueColor);
        valLbl.setFont(new Font("Arial", Font.BOLD, 14));

        row.add(nameLbl, BorderLayout.WEST);
        row.add(valLbl, BorderLayout.EAST);

        statsPanel.add(row);
    }
}