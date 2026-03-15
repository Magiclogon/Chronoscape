package ma.ac.emi.UI;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import ma.ac.emi.UI.component.RetroButton;
import ma.ac.emi.UI.component.SettingsPanel;

public class Settings extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color BG_DARK      = new Color(18, 18, 24);
    private static final Color PANEL_BG     = new Color(30, 30, 38);
    private static final Color BORDER_DIM   = new Color(60, 60, 70);
    private static final Color BORDER_LIGHT = new Color(180, 180, 190);
    private static final Color ACCENT_GREEN = new Color(80, 200, 100);
    private static final Color ACCENT_RED   = new Color(220, 60, 60);

    private static final String FONT_NAME = "ByteBounce";
    private static final Font   FONT_BODY = new Font(FONT_NAME, Font.PLAIN, 20);

    private static final int MAX_WIDTH = 760;

    private JTabbedPane tabs;

    public Settings(Runnable goBackAction) {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ── Card ──────────────────────────────────────────────────────────
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL_BG);

        // Center card
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.VERTICAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        tabs = createRetroTabbedPane();
        tabs.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        tabs.setOpaque(true);
        card.add(tabs, BorderLayout.CENTER);

        // ── Shared footer ─────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        footer.setBackground(new Color(22, 22, 28));

        // Top separator
        JPanel footerSep = new JPanel();
        footerSep.setBackground(BORDER_DIM);
        footerSep.setPreferredSize(new Dimension(0, 1));

        RetroButton applyBtn = new RetroButton("APPLY",   RetroButton.Style.SOLID,   ACCENT_GREEN,  Color.BLACK);
        RetroButton resetBtn = new RetroButton("RESET",   RetroButton.Style.DANGER,  ACCENT_RED,    Color.WHITE);
        RetroButton backBtn  = new RetroButton("< BACK",  RetroButton.Style.OUTLINE, BORDER_LIGHT);
        applyBtn.setPreferredSize(new Dimension(140, 38));
        resetBtn.setPreferredSize(new Dimension(140, 38));
        backBtn.setPreferredSize(new Dimension(140, 38));

        applyBtn.addActionListener(e -> withCurrentPanel(SettingsPanel::applyChanges));
        resetBtn.addActionListener(e -> withCurrentPanel(SettingsPanel::resetToDefaults));
        backBtn.addActionListener(e -> goBackAction.run());

        footer.add(applyBtn);
        footer.add(resetBtn);
        footer.add(backBtn);

        JPanel footerWrapper = new JPanel(new BorderLayout());
        footerWrapper.setBackground(new Color(22, 22, 28));
        footerWrapper.add(footerSep, BorderLayout.NORTH);
        footerWrapper.add(footer,    BorderLayout.CENTER);

        card.add(footerWrapper, BorderLayout.SOUTH);

        card.setPreferredSize(new Dimension(MAX_WIDTH, 0));
        card.setMaximumSize(new Dimension(MAX_WIDTH, Integer.MAX_VALUE));
        outer.add(card, gbc);
        add(outer, BorderLayout.CENTER);
    }

    // ── Tab management ────────────────────────────────────────────────────

    public void addTab(String title, JPanel tab) {
        tabs.addTab(title, tab);
    }

    /** Calls action on whichever tab is currently selected, if it implements SettingsPanel. */
    private void withCurrentPanel(java.util.function.Consumer<SettingsPanel> action) {
        int idx = tabs.getSelectedIndex();
        if (idx < 0) return;
        Component c = tabs.getComponentAt(idx);
        if (c instanceof SettingsPanel) action.accept((SettingsPanel) c);
    }

    // ── Retro tab UI (unchanged) ──────────────────────────────────────────

    private JTabbedPane createRetroTabbedPane() {
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(FONT_BODY);
        tab.setFocusable(false);
        tab.setBackground(PANEL_BG);
        tab.setForeground(BORDER_LIGHT);
        tab.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tab.setUI(new BasicTabbedPaneUI() {
            @Override protected void installDefaults() {
                super.installDefaults();
                highlight = lightHighlight = shadow = darkShadow = focus = PANEL_BG;
                tabPane.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            }
            @Override protected void paintTabBackground(Graphics g, int tabPlacement,
                    int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Tab row backdrop
                g2.setColor(BG_DARK); g2.fillRect(x, y, w, h);
                // Tab fill
                g2.setColor(isSelected ? new Color(45,45,55) : new Color(35,35,45));
                g2.fillRect(x+1, y+1, w-2, h-2);
                g2.dispose();
            }
            @Override protected void paintTabBorder(Graphics g, int tabPlacement,
                    int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (isSelected) { g2.setColor(ACCENT_GREEN); g2.fillRect(x, y+h-3, w, 3); }
                g2.setColor(BORDER_DIM); g2.drawRect(x, y, w-1, h);
                g2.dispose();
            }
            @Override protected void paintFocusIndicator(Graphics g, int tp,
                    java.awt.Rectangle[] rects, int ti,
                    java.awt.Rectangle ir, java.awt.Rectangle tr, boolean sel) {}
            @Override protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                Graphics2D g2 = (Graphics2D) g.create();
                int tabAreaH = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                int w = tabPane.getWidth(), h = tabPane.getHeight();
                g2.setColor(PANEL_BG); g2.fillRect(0, tabAreaH, w, h - tabAreaH);
                g2.setColor(BORDER_DIM); g2.drawLine(0, tabAreaH, w, tabAreaH);
                g2.dispose();
            }
            @Override protected Insets getContentBorderInsets(int tp) { return new Insets(1,0,0,0); }
            @Override protected Insets getTabAreaInsets(int tp)       { return new Insets(4,4,0,4); }
            @Override protected int calculateTabHeight(int tp, int ti, int fh) { return 38; }
        });

        return tab;
    }
}