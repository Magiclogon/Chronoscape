package ma.ac.emi.UI;

import ma.ac.emi.UI.component.RetroButton;
import ma.ac.emi.UI.component.RetroScrollBar;
import ma.ac.emi.UI.component.SettingsPanel;
import ma.ac.emi.input.InputConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

/**
 * Settings panel for key bindings and keyboard aim mode.
 *
 * Each binding row shows the action name and a button.
 * Clicking the button enters "listening" mode — the next key press
 * is captured and assigned to that binding.
 */
public class ControlsPanel extends JPanel implements SettingsPanel {

    // Working copy — written to InputConfig only on applyChanges()
    private int wMoveUp, wMoveDown, wMoveLeft, wMoveRight;
    private int wSwitchWeapon, wPause;
    private int wAimUp, wAimDown, wAimLeft, wAimRight;
    private boolean wKeyboardAim;

    // Binding buttons so we can refresh their labels
    private RetroButton btnMoveUp, btnMoveDown, btnMoveLeft, btnMoveRight;
    private RetroButton btnSwitch, btnPause;
    private RetroButton btnAimUp, btnAimDown, btnAimLeft, btnAimRight;
    private RetroButton btnAimMode;

    // Currently listening button — null when idle
    private RetroButton listeningBtn = null;
    private Consumer<Integer> listeningCallback = null;

    // Key listener attached to the top-level frame while in listening mode
    private final KeyAdapter captureListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            int vk = e.getKeyCode();
            // Ignore modifier-only presses
            if (vk == KeyEvent.VK_SHIFT || vk == KeyEvent.VK_CONTROL
                    || vk == KeyEvent.VK_ALT || vk == KeyEvent.VK_META) return;

            if (listeningCallback != null) listeningCallback.accept(vk);
            stopListening();
        }
    };

    public ControlsPanel() {
        loadFromConfig();

        setLayout(new BorderLayout());
        setBackground(MenuStyle.BG_DARK);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MenuStyle.BG_PANEL);
        card.setBorder(new EmptyBorder(24, 32, 24, 32));

        // ── Keyboard aim toggle ───────────────────────────────────────────
        card.add(makeSectionLabel("AIM MODE"));
        card.add(Box.createVerticalStrut(8));
        card.add(makeAimModeRow());
        card.add(Box.createVerticalStrut(16));
        card.add(makeSeparator());
        card.add(Box.createVerticalStrut(16));

        // ── Movement bindings ─────────────────────────────────────────────
        card.add(makeSectionLabel("MOVEMENT"));
        card.add(Box.createVerticalStrut(8));
        btnMoveUp    = makeBindRow(card, "MOVE UP",    wMoveUp,    vk -> { wMoveUp    = vk; refreshBtn(btnMoveUp,    vk); });
        btnMoveDown  = makeBindRow(card, "MOVE DOWN",  wMoveDown,  vk -> { wMoveDown  = vk; refreshBtn(btnMoveDown,  vk); });
        btnMoveLeft  = makeBindRow(card, "MOVE LEFT",  wMoveLeft,  vk -> { wMoveLeft  = vk; refreshBtn(btnMoveLeft,  vk); });
        btnMoveRight = makeBindRow(card, "MOVE RIGHT", wMoveRight, vk -> { wMoveRight = vk; refreshBtn(btnMoveRight, vk); });
        card.add(Box.createVerticalStrut(16));
        card.add(makeSeparator());
        card.add(Box.createVerticalStrut(16));

        // ── Actions ───────────────────────────────────────────────────────
        card.add(makeSectionLabel("ACTIONS"));
        card.add(Box.createVerticalStrut(8));
        btnSwitch = makeBindRow(card, "SWITCH WEAPON", wSwitchWeapon, vk -> { wSwitchWeapon = vk; refreshBtn(btnSwitch, vk); });
        btnPause  = makeBindRow(card, "PAUSE",         wPause,        vk -> { wPause        = vk; refreshBtn(btnPause,  vk); });
        card.add(Box.createVerticalStrut(16));
        card.add(makeSeparator());
        card.add(Box.createVerticalStrut(16));

        // ── Keyboard aim directions ───────────────────────────────────────
        card.add(makeSectionLabel("AIM DIRECTION (KEYBOARD MODE)"));
        card.add(Box.createVerticalStrut(8));
        btnAimUp    = makeBindRow(card, "AIM UP",    wAimUp,    vk -> { wAimUp    = vk; refreshBtn(btnAimUp,    vk); });
        btnAimDown  = makeBindRow(card, "AIM DOWN",  wAimDown,  vk -> { wAimDown  = vk; refreshBtn(btnAimDown,  vk); });
        btnAimLeft  = makeBindRow(card, "AIM LEFT",  wAimLeft,  vk -> { wAimLeft  = vk; refreshBtn(btnAimLeft,  vk); });
        btnAimRight = makeBindRow(card, "AIM RIGHT", wAimRight, vk -> { wAimRight = vk; refreshBtn(btnAimRight, vk); });
        card.add(Box.createVerticalStrut(24));

        // ── Scroll wrapper ────────────────────────────────────────────────
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(MenuStyle.BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor  = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        wrapper.add(card, gbc);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBackground(MenuStyle.BG_DARK);
        scroll.getViewport().setBackground(MenuStyle.BG_DARK);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar vBar = new RetroScrollBar(JScrollBar.VERTICAL);
        vBar.setPreferredSize(new Dimension(14, 0));
        vBar.setUnitIncrement(40);
        scroll.setVerticalScrollBar(vBar);

        add(scroll, BorderLayout.CENTER);
    }

    // ── SettingsPanel ─────────────────────────────────────────────────────

    @Override
    public void applyChanges() {
        stopListening();
        InputConfig cfg = InputConfig.getInstance();
        cfg.moveUp      = wMoveUp;
        cfg.moveDown    = wMoveDown;
        cfg.moveLeft    = wMoveLeft;
        cfg.moveRight   = wMoveRight;
        cfg.switchWeapon = wSwitchWeapon;
        cfg.pause       = wPause;
        cfg.aimUp       = wAimUp;
        cfg.aimDown     = wAimDown;
        cfg.aimLeft     = wAimLeft;
        cfg.aimRight    = wAimRight;
        cfg.keyboardAimMode = wKeyboardAim;
        cfg.save();
    }

    @Override
    public void resetToDefaults() {
        stopListening();
        // Replace working copy with a fresh default config
        InputConfig defaults = new InputConfig();
        wMoveUp      = defaults.moveUp;
        wMoveDown    = defaults.moveDown;
        wMoveLeft    = defaults.moveLeft;
        wMoveRight   = defaults.moveRight;
        wSwitchWeapon = defaults.switchWeapon;
        wPause       = defaults.pause;
        wAimUp       = defaults.aimUp;
        wAimDown     = defaults.aimDown;
        wAimLeft     = defaults.aimLeft;
        wAimRight    = defaults.aimRight;
        wKeyboardAim = defaults.keyboardAimMode;
        refreshAllButtons();
    }

    // ── Build helpers ─────────────────────────────────────────────────────

    private JLabel makeSectionLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(MenuStyle.FONT_BODY);
        lbl.setForeground(MenuStyle.TEXT_BORDER);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JPanel makeSeparator() {
        JPanel p = new JPanel();
        p.setBackground(new Color(60, 60, 70));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.setPreferredSize(new Dimension(0, 1));
        return p;
    }

    private JPanel makeAimModeRow() {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setBackground(MenuStyle.BG_PANEL);
        row.setBorder(new EmptyBorder(8, 0, 8, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JLabel lbl = new JLabel("KEYBOARD AIM");
        lbl.setFont(MenuStyle.FONT_BODY);
        lbl.setForeground(MenuStyle.TEXT_BORDER);

        JLabel hint = new JLabel("Use arrow keys to aim and shoot instead of the mouse");
        hint.setFont(MenuStyle.FONT_SMALL);
        hint.setForeground(MenuStyle.TEXT_GRAY);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(lbl);
        left.add(hint);

        btnAimMode = new RetroButton(
                wKeyboardAim ? "ON" : "OFF",
                RetroButton.Style.OUTLINE,
                wKeyboardAim ? MenuStyle.ACCENT : MenuStyle.BG_MUTED);
        btnAimMode.setPreferredSize(new Dimension(90, MenuStyle.BTN_HEIGHT_SM));
        btnAimMode.setToggled(wKeyboardAim);
        btnAimMode.addActionListener(e -> {
            wKeyboardAim = !wKeyboardAim;
            btnAimMode.setText(wKeyboardAim ? "ON" : "OFF");
            btnAimMode.setToggled(wKeyboardAim);
            btnAimMode.repaint();
        });

        row.add(left,       BorderLayout.CENTER);
        row.add(btnAimMode, BorderLayout.EAST);
        return row;
    }

    /**
     * Creates a binding row, adds it to the parent, and returns the bind button
     * so the caller can store a reference for later label refresh.
     */
    private RetroButton makeBindRow(JPanel parent, String label, int currentVK,
                                     Consumer<Integer> onBound) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setBackground(MenuStyle.BG_PANEL);
        row.setBorder(new EmptyBorder(6, 0, 6, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel lbl = new JLabel(label);
        lbl.setFont(MenuStyle.FONT_BODY);
        lbl.setForeground(MenuStyle.TEXT_BORDER);
        lbl.setPreferredSize(new Dimension(220, 30));

        RetroButton btn = new RetroButton(
                InputConfig.keyName(currentVK),
                RetroButton.Style.OUTLINE,
                MenuStyle.ACCENT);
        btn.setPreferredSize(new Dimension(120, MenuStyle.BTN_HEIGHT_SM));

        btn.addActionListener(e -> startListening(btn, onBound));

        row.add(lbl, BorderLayout.WEST);
        row.add(btn, BorderLayout.EAST);
        parent.add(row);
        return btn;
    }

    // ── Listening mode ────────────────────────────────────────────────────

    private void startListening(RetroButton btn, Consumer<Integer> callback) {
        stopListening(); // cancel any previous

        listeningBtn      = btn;
        listeningCallback = callback;

        btn.setText("...");
        btn.setToggled(true);
        btn.repaint();

        // Attach key capture listener to the root frame
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.addKeyListener(captureListener);
            frame.requestFocusInWindow();
        }
    }

    private void stopListening() {
        if (listeningBtn != null) {
            listeningBtn.setToggled(false);
            listeningBtn.repaint();
            listeningBtn      = null;
            listeningCallback = null;
        }
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) frame.removeKeyListener(captureListener);
    }

    // ── Label refresh ─────────────────────────────────────────────────────

    private void refreshBtn(RetroButton btn, int vk) {
        btn.setText(InputConfig.keyName(vk));
        btn.repaint();
    }

    private void refreshAllButtons() {
        refreshBtn(btnMoveUp,    wMoveUp);
        refreshBtn(btnMoveDown,  wMoveDown);
        refreshBtn(btnMoveLeft,  wMoveLeft);
        refreshBtn(btnMoveRight, wMoveRight);
        refreshBtn(btnSwitch,    wSwitchWeapon);
        refreshBtn(btnPause,     wPause);
        refreshBtn(btnAimUp,     wAimUp);
        refreshBtn(btnAimDown,   wAimDown);
        refreshBtn(btnAimLeft,   wAimLeft);
        refreshBtn(btnAimRight,  wAimRight);
        btnAimMode.setText(wKeyboardAim ? "ON" : "OFF");
        btnAimMode.setToggled(wKeyboardAim);
        btnAimMode.repaint();
    }

    // ── Load ──────────────────────────────────────────────────────────────

    private void loadFromConfig() {
        InputConfig cfg = InputConfig.getInstance();
        wMoveUp       = cfg.moveUp;
        wMoveDown     = cfg.moveDown;
        wMoveLeft     = cfg.moveLeft;
        wMoveRight    = cfg.moveRight;
        wSwitchWeapon = cfg.switchWeapon;
        wPause        = cfg.pause;
        wAimUp        = cfg.aimUp;
        wAimDown      = cfg.aimDown;
        wAimLeft      = cfg.aimLeft;
        wAimRight     = cfg.aimRight;
        wKeyboardAim  = cfg.keyboardAimMode;
    }
}