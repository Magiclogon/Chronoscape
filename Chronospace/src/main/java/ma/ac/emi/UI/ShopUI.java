package ma.ac.emi.UI;

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


    private JLabel moneyLabel;
    private JPanel availableItemsPanel;
    private JPanel weaponSlotsPanel;
    private JPanel statItemsPanel;
    private JPanel statsPanel;
    private JButton nextWaveButton;

    public ShopUI() {

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top
        nextWaveButton = new JButton("Next Wave");
        nextWaveButton.addActionListener((e) -> {
        	GameController.getInstance().resumeGame();
        });
        add(createTopPanel(), BorderLayout.NORTH);

        // Center: available items + player stats
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setBackground(new Color(30, 30, 30));
        centerPanel.add(createAvailableItemsPanel());
        centerPanel.add(createInventoryPanel());
        add(centerPanel, BorderLayout.CENTER);

        // Bottom: inventory
        add(createStatsPanel(), BorderLayout.EAST);
        
        
        
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
        weaponSlotsPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "Weapons",
                        TitledBorder.LEADING,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14),
                        Color.WHITE
                )
        );

        statItemsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        statItemsPanel.setBackground(new Color(60, 60, 60));
        statItemsPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        "Items",
                        TitledBorder.LEADING,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14),
                        Color.WHITE
                )
        );

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        bottomPanel.setBackground(new Color(40, 40, 40));
        bottomPanel.add(statItemsPanel);
        bottomPanel.add(weaponSlotsPanel);

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

    public void refresh() {
        Player player = GameController.getInstance().getWorld().getPlayer();
        ShopManager shop = GameController.getInstance().getShopManager();

        // Update money
        moneyLabel.setText("Money: " + player.getMoney() + "g");

        // Update available items
        availableItemsPanel.removeAll();
        for (ShopItem item : shop.getAvailableItems()) {
            availableItemsPanel.add(createItemButton(item));
        }

        // Update weapon slots
        weaponSlotsPanel.removeAll();
        //weaponSlotsPanel.add(createSlotLabel(player.getWeapon().getDefinition().getName()));
        //weaponSlotsPanel.add(createSlotLabel(player.getSecondaryWeapon().getName()));
        //weaponSlotsPanel.add(createSlotLabel(player.getMeleeWeapon().getName()));

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

    private JButton createItemButton(ShopItem item) {
        JButton button = new JButton("<html><b>" + item.getItemDefinition().getName() + "</b><br>" + item.getPrice() + "g</html>");
        button.setFocusPainted(false);
        button.setBackground(new Color(80, 80, 80));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 12));

        button.addActionListener(e -> {
            GameController.getInstance().getShopManager().purchaseItem(item);
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
