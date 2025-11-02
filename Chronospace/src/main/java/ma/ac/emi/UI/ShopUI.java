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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

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
    private InventoryScrollable weaponPane;
    private InventoryScrollable activeWeaponsPane;
    private InventoryScrollable statItemsPane;
    private JPanel statsPanel;
    private JButton nextWaveButton;
    private JButton rerollButton;

    public ShopUI() {

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nextWaveButton = new JButton("Next Wave");
        nextWaveButton.addActionListener((e) -> {
        	GameController.getInstance().resumeGame();
        });
        add(createTopPanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(30, 30, 30));
        centerPanel.add(createAvailableItemsPanel());
        
        JPanel rerollPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rerollPanel.setBackground(new Color(60, 60, 60));
        rerollButton = new JButton("Reroll");
        rerollButton.addActionListener((e) -> {
        	GameController.getInstance().getShopManager().refreshAvailableItems();
        	refresh();
        });
        rerollPanel.add(rerollButton);
        
        centerPanel.add(rerollPanel);
        centerPanel.add(createInventoryPanel());
        add(centerPanel, BorderLayout.CENTER);

   
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

    public void refresh() {
        Player player = GameController.getInstance().getWorld().getPlayer();
        ShopManager shop = GameController.getInstance().getShopManager();

        // Update money
        moneyLabel.setText("Money: " + player.getMoney() + "$");

        // Update available items
        availableItemsPanel.removeAll();
        for (ShopItem item : shop.getAvailableItems()) {
            availableItemsPanel.add(new ShopItemButton(this, item));
        }

        // Update active weapon slots
        activeWeaponsPane.getPanel().removeAll();
        activeWeaponsPane.add(new InventoryItemButton(this, player.getWeapon().getDefinition()));
        activeWeaponsPane.add(new InventoryItemButton(this, player.getSecondaryWeapon().getDefinition()));
        activeWeaponsPane.add(new InventoryItemButton(this, player.getMeleeWeapon().getDefinition()));
        
        //Update weapons in inventory
        weaponPane.getPanel().removeAll();
        for(int i = 0; i < 27; i++) {
        	weaponPane.add(new InventoryItemButton(this, null));
        }
        
        // Update stat modifier items
        statItemsPane.removeAll();
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
