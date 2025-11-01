

import javax.swing.*;
import javax.swing.border.TitledBorder;

import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.ShopItem;
import ma.ac.emi.gamelogic.shop.ShopManager;

import java.awt.*;

/**
 * ShopUI handles the visual representation of the in-game shop.
 * It displays:
 *  - Player money
 *  - Available items for purchase
 *  - Inventory (weapons + stat items)
 *  - Player stats
 */
public class ShopUI extends JPanel {

    private final GameController gameController;

    private JLabel moneyLabel;
    private JPanel availableItemsPanel;
    private JPanel weaponSlotsPanel;
    private JPanel statItemsPanel;
    private JPanel statsPanel;

    public ShopUI() {
        this.gameController = GameController.getInstance();

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top: money panel
        add(createMoneyPanel(), BorderLayout.NORTH);

        // Center: available items + player stats
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBackground(new Color(30, 30, 30));
        centerPanel.add(createAvailableItemsPanel());
        centerPanel.add(createStatsPanel());
        add(centerPanel, BorderLayout.CENTER);

        // Bottom: inventory
        add(createInventoryPanel(), BorderLayout.SOUTH);

        refresh();
    }

    // -------------------------------
    // REGION: Panel Creation Methods
    // -------------------------------

    private JPanel createMoneyPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);

        moneyLabel = new JLabel();
        moneyLabel.setForeground(Color.YELLOW);
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 20));

        panel.add(moneyLabel);
        return panel;
    }

    private JPanel createAvailableItemsPanel() {
        availableItemsPanel = new JPanel(new GridLayout(0, 3, 10, 10));
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

        weaponSlotsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        weaponSlotsPanel.setBackground(new Color(60, 60, 60));
        weaponSlotsPanel.setBorder(BorderFactory.createTitledBorder("Weapons"));

        statItemsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        statItemsPanel.setBackground(new Color(60, 60, 60));
        statItemsPanel.setBorder(BorderFactory.createTitledBorder("Stat Items"));

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        bottomPanel.setBackground(new Color(40, 40, 40));
        bottomPanel.add(weaponSlotsPanel);
        bottomPanel.add(statItemsPanel);

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
        return statsPanel;
    }

    // -------------------------------
    // REGION: Refresh Logic
    // -------------------------------

    public void refresh() {
        Player player = gameController.getWorld().getPlayer();
        ShopManager shop = gameController.getShopManager();

        // Update money
        moneyLabel.setText("Money: " + player.getMoney() + "g");

        // Update available items
        availableItemsPanel.removeAll();
        for (ShopItem item : shop.getAvailableItems()) {
            availableItemsPanel.add(createItemButton(item));
        }

        // Update weapon slots
        weaponSlotsPanel.removeAll();
        /*for (WeaponItem weapon : player.getWeapons()) {
            weaponSlotsPanel.add(createSlotLabel(weapon.getName()));
        }*/

        // Update stat modifier items
        statItemsPanel.removeAll();
        /*for (Item item : player.getStatItems()) {
            statItemsPanel.add(createSlotLabel(item.getName()));
        }*/

        // Update stats
        statsPanel.removeAll();
        //statsPanel.add(createStatLabel("Attack: " + player.getAttack()));
        //statsPanel.add(createStatLabel("Defense: " + player.getDefense()));
        statsPanel.add(createStatLabel("Speed: " + player.getSpeed()));
        //statsPanel.add(createStatLabel("Health: " + player.getHealth()));

        revalidate();
        repaint();
    }

    // -------------------------------
    // REGION: UI Helpers
    // -------------------------------

    private JButton createItemButton(ShopItem item) {
        JButton button = new JButton("<html><b>" + item.getItemDefintion().getName() + "</b><br>" + item.getItemDefintion().getBasePrice() + "g</html>");
        button.setFocusPainted(false);
        button.setBackground(new Color(80, 80, 80));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 12));

        button.addActionListener(e -> {
            //gameController.getShopManager().purchaseItem(item);
            refresh();
        });

        return button;
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
}
