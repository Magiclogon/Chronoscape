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
import ma.ac.emi.gamelogic.player.PlayerConfig;
import ma.ac.emi.gamelogic.shop.Inventory;
import ma.ac.emi.gamelogic.shop.ShopItem;
import ma.ac.emi.gamelogic.shop.ShopManager;
import ma.ac.emi.gamelogic.shop.WeaponItem;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import java.util.HashMap;
import java.util.List;
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
    private JPanel equippedFooter;
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

        // Scrollable stats area (player stats + weapon bonuses)
        statsContainer = new JPanel();
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setBackground(MenuStyle.BG_PANEL);

        JScrollPane statsScroll = new JScrollPane(statsContainer);
        statsScroll.setBackground(MenuStyle.BG_PANEL);
        statsScroll.getViewport().setBackground(MenuStyle.BG_PANEL);
        statsScroll.setBorder(null);
        statsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar statsVBar = new ma.ac.emi.UI.component.RetroScrollBar(JScrollBar.VERTICAL);
        statsVBar.setPreferredSize(new Dimension(10, 0));
        statsVBar.setUnitIncrement(20);
        statsScroll.setVerticalScrollBar(statsVBar);

        // Fixed equipped loadout footer — always visible at the bottom
        equippedFooter = new JPanel(new BorderLayout());
        equippedFooter.setBackground(MenuStyle.BG_PANEL);
        equippedFooter.setBorder(new EmptyBorder(8, 0, 0, 0));

        heroPanel.add(statsScroll,    BorderLayout.CENTER);
        heroPanel.add(equippedFooter, BorderLayout.SOUTH);

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

        // ScrollablePanel: tracks viewport width so BoxLayout wraps text correctly.
        detailsContainer = new ScrollablePanel();
        detailsContainer.setLayout(new BoxLayout(detailsContainer, BoxLayout.Y_AXIS));
        detailsContainer.setBackground(MenuStyle.BG_PANEL);
        detailsContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane detailsScroll = new JScrollPane(detailsContainer);
        detailsScroll.getViewport().setBackground(MenuStyle.BG_PANEL);
        detailsScroll.setBorder(new LineBorder(BORDER_DIM, 1));
        detailsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
        gbc.gridx = 0; gbc.weightx = 0.26; contentGrid.add(heroPanel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.48; contentGrid.add(shopPanel, gbc);
        gbc.gridx = 2; gbc.weightx = 0.26; contentGrid.add(bagPanel,  gbc);
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
            GameController.getInstance().nextWave();
        });

        header.add(moneyLabel,     BorderLayout.WEST);
        header.add(nextWaveButton, BorderLayout.EAST);

        
        return header;
    }

    public void refresh() {
        Player player    = Player.getInstance();
        ShopManager shop = GameController.getInstance().getShopManager();

        moneyLabel.setText("GOLD: " + (int) player.getMoney());
        rerollButton.setText("REROLL (" + shop.getRerollPrice() + ")");

        statsContainer.removeAll();

        statsContainer.add(createStatRow("HP",       String.format("%.0f/%.0f", player.getHp(), player.getHpMax())));
        statsContainer.add(Box.createVerticalStrut(6));
        statsContainer.add(createStatRow("SPEED",    formatCapped(player.getSpeed(), player.getConfig() != null ? player.getConfig().getCaps().minSpeed : 50, player.getConfig() != null ? player.getConfig().getCaps().maxSpeed : 600, false)));
        statsContainer.add(Box.createVerticalStrut(6));
        statsContainer.add(createStatRow("REGEN",    formatCapped(player.getRegenerationSpeed(), player.getConfig() != null ? player.getConfig().getCaps().minRegen : 0, player.getConfig() != null ? player.getConfig().getCaps().maxRegen : 50, true)));
        statsContainer.add(Box.createVerticalStrut(6));
        statsContainer.add(createStatRow("DEFENSE",  String.format("%.0f",      player.getDefense())));
        statsContainer.add(Box.createVerticalStrut(6));
        statsContainer.add(createStatRow("DODGE",    formatDodge(player.getDodge(), player.getConfig() != null ? player.getConfig().getCaps().maxDodge : 0.75)));
        statsContainer.add(Box.createVerticalStrut(6));
        statsContainer.add(createStatRow("LUCK",     formatLuck(player.getLuck(), player.getConfig() != null ? player.getConfig().getCaps().minLuck : 0)));

        statsContainer.add(Box.createVerticalStrut(30));

        // ── Weapon bonuses — always shown ─────────────────────────────────
        Inventory.WeaponBonusSummary wb = player.getInventory().getWeaponBonusSummary();

        JLabel weaponHeader = new JLabel("WEAPON BONUSES");
        weaponHeader.setFont(MenuStyle.FONT_BODY);
        weaponHeader.setForeground(MenuStyle.TEXT_GRAY);
        weaponHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsContainer.add(weaponHeader);
        statsContainer.add(Box.createVerticalStrut(8));

        statsContainer.add(createBonusRow("DAMAGE",    wb.damageMul,      wb.damageAdd,      false));
        statsContainer.add(Box.createVerticalStrut(6));
        statsContainer.add(createBonusRow("ATK SPEED", wb.attackSpeedMul, wb.attackSpeedAdd, false));
        statsContainer.add(Box.createVerticalStrut(6));
        statsContainer.add(createBonusRow("RANGE",     wb.rangeMul,       wb.rangeAdd,       false));
        statsContainer.add(Box.createVerticalStrut(6));
        statsContainer.add(createBonusRow("MAGAZINE",  wb.magazineMul,    wb.magazineAdd,    false));
        statsContainer.add(Box.createVerticalStrut(6));
        statsContainer.add(createBonusRow("RELOAD",    wb.reloadDiv,      0,                 true));

        // ── Equipped loadout — fixed footer, always visible ────────────────
        equippedFooter.removeAll();

        JLabel equipHeader = new JLabel("EQUIPPED LOADOUT");
        equipHeader.setFont(MenuStyle.FONT_BODY);
        equipHeader.setForeground(MenuStyle.TEXT_GRAY);
        equipHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel equippedGrid = new JPanel(new GridLayout(1, 3, 5, 0));
        equippedGrid.setBackground(MenuStyle.BG_PANEL);
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

        JPanel footerInner = new JPanel();
        footerInner.setLayout(new BoxLayout(footerInner, BoxLayout.Y_AXIS));
        footerInner.setBackground(MenuStyle.BG_PANEL);
        footerInner.setBorder(new EmptyBorder(0, 0, 4, 0));
        footerInner.add(equipHeader);
        footerInner.add(Box.createVerticalStrut(6));
        footerInner.add(equippedGrid);
        equippedFooter.add(footerInner, BorderLayout.CENTER);

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

        // ── Rarity border color ───────────────────────────────────────────
        Color rarityColor;
        switch (item.getItemDefinition().getRarity()) {
            case LEGENDARY: rarityColor = new Color(255, 215,   0); break;
            case EPIC:      rarityColor = new Color(160,  32, 240); break;
            case RARE:      rarityColor = new Color( 65, 105, 225); break;
            case COMMON:    rarityColor = new Color( 50, 205,  50); break;
            default:        rarityColor = MenuStyle.TEXT_GRAY;      break;
        }

        // ── Name + rarity ─────────────────────────────────────────────────
        JTextArea nameLbl = new JTextArea(item.getItemDefinition().getName().toUpperCase());
        nameLbl.setFont(MenuStyle.FONT_HEADER);
        nameLbl.setForeground(ACCENT_GOLD);
        nameLbl.setBackground(MenuStyle.BG_PANEL);
        nameLbl.setLineWrap(true);
        nameLbl.setWrapStyleWord(true);
        nameLbl.setEditable(false);
        nameLbl.setFocusable(false);
        nameLbl.setBorder(null);
        nameLbl.setColumns(1); // prevents unbounded preferred width in BoxLayout
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        String rarityStr = item.getItemDefinition().getRarity().toString().substring(0, 1).toUpperCase()
                         + item.getItemDefinition().getRarity().toString().substring(1).toLowerCase();
        JLabel rarityLbl = new JLabel(rarityStr);
        rarityLbl.setFont(MenuStyle.FONT_SMALL);
        rarityLbl.setForeground(rarityColor);
        rarityLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsContainer.add(nameLbl);
        detailsContainer.add(Box.createVerticalStrut(4));
        detailsContainer.add(rarityLbl);
        detailsContainer.add(Box.createVerticalStrut(8));
        detailsContainer.add(makeSeparator());
        detailsContainer.add(Box.createVerticalStrut(8));

        // ── Action bar: equip/unequip + slot spinner + sell — always visible ──
        // Sell button
        RetroButton sellBtn = new RetroButton(
                "SELL  " + (int)(item.getPrice() * ShopManager.SELLING_PERCENTAGE) + "$",
                RetroButton.Style.DANGER, MenuStyle.ACCENT_RED, Color.WHITE);
        sellBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, MenuStyle.BTN_HEIGHT_SM));
        sellBtn.addActionListener(e -> {
            GameController.getInstance().getShopManager().sellItem(item);
            refresh();
            detailsContainer.removeAll();
            detailsContainer.repaint();
        });

        if (item instanceof WeaponItem) {
            detailsContainer.add(createEquipControls((WeaponItem) item, sellBtn));
        } else {
            // Non-weapon: just show sell button full-width
            sellBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            detailsContainer.add(sellBtn);
        }

        detailsContainer.add(Box.createVerticalStrut(8));
        detailsContainer.add(makeSeparator());
        detailsContainer.add(Box.createVerticalStrut(10));

        // ── Flavor / description ──────────────────────────────────────────
        String fullDesc = item.getItemDefinition().getStatsDescription();
        String[] descParts = fullDesc.split("---", 2);
        String flavorText = descParts[0].trim();
        String statBlock  = descParts.length > 1 ? descParts[1].trim() : "";

        if (!flavorText.isBlank()) {
            JTextArea descArea = new JTextArea(flavorText);
            descArea.setFont(MenuStyle.FONT_BODY);
            descArea.setForeground(MenuStyle.TEXT_GRAY);
            descArea.setBackground(MenuStyle.BG_PANEL);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setEditable(false);
            descArea.setFocusable(false);
            descArea.setBorder(new EmptyBorder(0, 0, 8, 0));
            descArea.setColumns(1);
            descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
            detailsContainer.add(descArea);
            detailsContainer.add(makeSeparator());
            detailsContainer.add(Box.createVerticalStrut(8));
        }

        // ── Weapon base stats ─────────────────────────────────────────────
        if (item instanceof WeaponItem) {
            WeaponItemDefinition wdef = (WeaponItemDefinition) item.getItemDefinition();
            boolean isMelee = wdef.getMagazineSize() == 0;

            JLabel statsHeader = new JLabel(isMelee ? "BASE STATS  (MELEE)" : "BASE STATS");
            statsHeader.setFont(MenuStyle.FONT_SMALL);
            statsHeader.setForeground(MenuStyle.TEXT_GRAY);
            statsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
            detailsContainer.add(statsHeader);
            detailsContainer.add(Box.createVerticalStrut(6));

            detailsContainer.add(createStatRow("DAMAGE",    String.format("%.0f", wdef.getDamage())));
            detailsContainer.add(Box.createVerticalStrut(4));
            detailsContainer.add(createStatRow("ATK SPEED", String.format("%.1f", wdef.getAttackSpeed())));
            detailsContainer.add(Box.createVerticalStrut(4));
            detailsContainer.add(createStatRow("RANGE",     String.format("%.0f", wdef.getRange())));
            if (!isMelee) {
                detailsContainer.add(Box.createVerticalStrut(4));
                detailsContainer.add(createStatRow("MAGAZINE", String.format("%d", wdef.getMagazineSize())));
            }
            detailsContainer.add(Box.createVerticalStrut(10));
            detailsContainer.add(makeSeparator());
            detailsContainer.add(Box.createVerticalStrut(8));
        }

        // ── Colored stat lines (same logic as ShopItemButton) ─────────────
        if (!statBlock.isBlank()) {
            for (String rawLine : statBlock.split("\n")) {
                String line = rawLine.trim();
                if (line.isBlank()) continue;
                detailsContainer.add(createColoredStatRow(line));
                detailsContainer.add(Box.createVerticalStrut(4));
            }
            detailsContainer.add(Box.createVerticalStrut(6));
            detailsContainer.add(makeSeparator());
            detailsContainer.add(Box.createVerticalStrut(8));
        }

        // ── Price ─────────────────────────────────────────────────────────
        detailsContainer.add(createStatRow("BUY PRICE",  item.getPrice() + "$"));
        detailsContainer.add(Box.createVerticalStrut(4));
        detailsContainer.add(createStatRow("SELL VALUE", (int)(item.getPrice() * ShopManager.SELLING_PERCENTAGE) + "$"));

        detailsContainer.add(Box.createVerticalGlue());

        detailsContainer.revalidate();
        detailsContainer.repaint();
    }

    /**
     * Renders a stat line with per-word color based on sign — mirrors
     * ShopItemButton.drawColoredStatLine but produces a JPanel row instead.
     */
    private JPanel createColoredStatRow(String text) {
        // Section headers (all-caps, no digits) — rendered as a dim label
        if (text.equals(text.toUpperCase()) && !text.matches(".*\\d.*")) {
            JLabel hdr = new JLabel(text);
            hdr.setFont(MenuStyle.FONT_SMALL);
            hdr.setForeground(MenuStyle.TEXT_GRAY);
            hdr.setAlignmentX(Component.LEFT_ALIGNMENT);
            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setBackground(MenuStyle.BG_PANEL);
            wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
            wrap.add(hdr, BorderLayout.WEST);
            return wrap;
        }

        // Build a flow-like panel of colored word labels
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        row.setBackground(MenuStyle.BG_PANEL);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        boolean indented = text.startsWith("INDENT:");
        if (indented) text = text.substring("INDENT:".length());

        for (String word : text.split(" ")) {
            if (word.isBlank()) continue;
            Color c;
            if (word.matches("^[+\\d].*"))  c = new Color(80, 200, 80);
            else if (word.startsWith("-"))   c = MenuStyle.ACCENT_RED;
            else                             c = MenuStyle.TEXT_BORDER;

            JLabel lbl = new JLabel(word);
            lbl.setFont(MenuStyle.FONT_SMALL);
            lbl.setForeground(c);
            if (indented) lbl.setBorder(new EmptyBorder(0, 16, 0, 0));
            row.add(lbl);
            indented = false; // only indent first word
        }
        return row;
    }

    /**
     * Builds the weapon action bar shown directly under the title/rarity.
     *
     * Row 1: [EQUIP / UNEQUIP toggle]  [SELL button]
     * Row 2: [SLOT  ◀ 1 / 2 / 3 ▶]   (only visible when equipped)
     *
     * Equip behaviour:
     *   - Equip places the weapon in the first free slot (or slot 0 if all full).
     *   - The slot spinner wraps through slots 1-MAX_EQU and moves the weapon
     *     to the selected slot (swapping with whatever is there).
     *   - Unequip removes the weapon from its slot back to the bag.
     */
    private JPanel createEquipControls(WeaponItem weaponItem, RetroButton sellBtn) {
        Inventory inv        = Player.getInstance().getInventory();
        int       currentSlot = inv.getEquippedSlot(weaponItem); // -1 = in bag
        boolean   isEquipped  = currentSlot >= 0;

        // ── Slot spinner (1-based) — only shown when equipped ────────────
        int spinnerInitial = isEquipped ? currentSlot + 1 : 1;
        RetroSpinner slotSpinner = new RetroSpinner(1, spinnerInitial, Inventory.MAX_EQU, 1) {
            private static final long serialVersionUID = 1L;
            @Override protected String getDisplayValue(int v) { return "SLOT  " + v; }
        };
        slotSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        slotSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, MenuStyle.BTN_HEIGHT_SM));

        JPanel spinnerRow = new JPanel(new BorderLayout());
        spinnerRow.setBackground(MenuStyle.BG_PANEL);
        spinnerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        spinnerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, MenuStyle.BTN_HEIGHT_SM));
        spinnerRow.add(slotSpinner, BorderLayout.CENTER);
        spinnerRow.setVisible(isEquipped);

        // ── Row 1: equip btn placeholder + sell btn side by side ──────────
        // We use a single-cell panel for the equip button so we can swap it
        // in place without touching the sell button or rebuilding the row.
        JPanel equipSlot = new JPanel(new BorderLayout());
        equipSlot.setBackground(MenuStyle.BG_PANEL);

        JPanel actionRow = new JPanel(new GridLayout(1, 2, 6, 0));
        actionRow.setBackground(MenuStyle.BG_PANEL);
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, MenuStyle.BTN_HEIGHT_SM));
        actionRow.add(equipSlot);
        actionRow.add(sellBtn);

        // Helper: (re)builds the equip button with the correct style and wires it up
        Runnable[] buildEquipBtn = new Runnable[1];
        buildEquipBtn[0] = () -> {
            boolean eq = Player.getInstance().getInventory().getEquippedSlot(weaponItem) >= 0;
            RetroButton btn = new RetroButton(
                    eq ? "UNEQUIP" : "EQUIP",
                    eq ? RetroButton.Style.OUTLINE : RetroButton.Style.SOLID,
                    eq ? MenuStyle.TEXT_GRAY       : MenuStyle.ACCENT,
                    eq ? MenuStyle.TEXT_GRAY       : Color.BLACK);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, MenuStyle.BTN_HEIGHT_SM));
            btn.addActionListener(e -> {
                Inventory inv2 = Player.getInstance().getInventory();
                int slot2 = inv2.getEquippedSlot(weaponItem);
                if (slot2 >= 0) {
                    inv2.unequipWeapon(weaponItem);
                    spinnerRow.setVisible(false);
                } else {
                    int freeSlot = 0;
                    WeaponItem[] slots = inv2.getEquippedWeapons();
                    for (int i = 0; i < slots.length; i++) {
                        if (slots[i] == null) { freeSlot = i; break; }
                    }
                    inv2.equipWeapon(weaponItem, freeSlot);
                    slotSpinner.setValue(freeSlot + 1);
                    spinnerRow.setVisible(true);
                }
                refresh();
                // Swap the button for the correctly-styled one
                equipSlot.removeAll();
                buildEquipBtn[0].run();
                equipSlot.revalidate();
                equipSlot.repaint();
                spinnerRow.revalidate();
                spinnerRow.repaint();
            });
            equipSlot.add(btn, BorderLayout.CENTER);
        };
        buildEquipBtn[0].run(); // build initial button

        // ── Spinner moves weapon to selected slot (swap) ──────────────────
        slotSpinner.addChangeListener(e -> {
            Player.getInstance().getInventory().equipWeapon(weaponItem, slotSpinner.getValue() - 1);
            refresh();
        });

        // ── Outer wrapper stacks both rows vertically ─────────────────────
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(MenuStyle.BG_PANEL);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(actionRow);
        wrapper.add(Box.createVerticalStrut(6));
        wrapper.add(spinnerRow);
        return wrapper;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private String formatDodge(double raw, double max) {
        double clamped  = Math.max(0, Math.min(max, raw));
        double overflow = raw - clamped;
        String base = String.format("%.0f%%", clamped * 100);
        if (Math.abs(overflow) < 0.001) return base;
        String extra = overflow > 0
                ? String.format(" (+%.0f%%)", overflow * 100)
                : String.format(" (%.0f%%)",  overflow * 100);
        return base + extra;
    }

    /**
     * Shows the effective (clamped) value plus the raw value in parentheses
     * when the raw differs from the effective. This way the player sees both
     * what the game actually uses and what their raw stat is.
     *
     * Example: raw=-131, min=50 → "50 (raw: -131)"
     * Example: raw=480, max=600 → "480"  (within range, no parenthetical)
     */
    private String formatCapped(double raw, double min, double max, boolean usePerSec) {
        double effective = Math.max(min, Math.min(max, raw));
        String suffix = usePerSec ? "/s" : "";
        String fmt    = usePerSec ? "%.1f" : "%.0f";
        String base   = String.format(fmt + suffix, effective);
        if (Math.abs(raw - effective) < 0.01) return base;
        // Show raw value so player can reason about item effects on the underlying stat
        String rawStr = String.format(fmt + suffix, raw);
        return base + " (" + rawStr + ")";
    }

    /**
     * Formats luck — only has a minimum. Shows raw value if below min.
     */
    private String formatLuck(double raw, double min) {
        if (raw >= min) return String.format("%.1f", raw);
        return String.format("%.1f (%.1f)", min, raw);
    }

    private JPanel createBonusRow(String label, double mul, double add, boolean isReload) {
        String display;
        if (isReload) {
            // reloadDiv is a bonus fraction: 0 = no bonus, 0.08 = 8% faster
            display = mul == 0.0 ? "--" : String.format("+%.0f%% faster", mul * 100);
        } else {
            if (mul == 1.0 && add == 0) {
                display = "--";
            } else {
                StringBuilder sb = new StringBuilder();
                if (mul != 1.0) sb.append(String.format("x%.2f", mul));
                if (add != 0) {
                    if (sb.length() > 0) sb.append("  ");
                    sb.append(add > 0 ? String.format("+%.0f", add) : String.format("%.0f", add));
                }
                display = sb.toString();
            }
        }
        return createStatRow(label, display);
    }

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

    private JPanel createStatRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setBackground(MenuStyle.BG_PANEL);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(MenuStyle.FONT_BODY);
        l.setForeground(MenuStyle.TEXT_GRAY);
        l.setPreferredSize(new Dimension(90, 20));

        JLabel v = new JLabel(value);
        v.setFont(MenuStyle.FONT_BODY);
        v.setForeground(MenuStyle.TEXT_MAIN);
        v.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel dots = new JLabel();
        dots.setFont(MenuStyle.FONT_SMALL);
        dots.setForeground(new Color(60, 60, 70));
        dots.setHorizontalAlignment(SwingConstants.CENTER);
        // Dots fill whatever space is left — label and value get priority
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
    // ── Inner class: JPanel that tracks viewport width for correct text wrapping ──
    private static class ScrollablePanel extends JPanel implements Scrollable {
        @Override public boolean getScrollableTracksViewportWidth()  { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(java.awt.Rectangle r, int o, int d)  { return 16; }
        @Override public int getScrollableBlockIncrement(java.awt.Rectangle r, int o, int d) { return 64; }
    }

}