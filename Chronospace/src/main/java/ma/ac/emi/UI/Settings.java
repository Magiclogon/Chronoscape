package ma.ac.emi.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class Settings extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color BG_DARK      = new Color(18, 18, 24);
    private static final Color PANEL_BG     = new Color(30, 30, 38);
    private static final Color BORDER_LIGHT = new Color(180, 180, 190);
    private static final Color ACCENT_GREEN = new Color(80, 200, 100);
    private static final Color TAB_SELECTED = new Color(45, 45, 55);
    private static final Color TAB_HOVER    = new Color(35, 35, 45);

    private static final String FONT_NAME = "ByteBounce";
    private static final Font FONT_BODY   = new Font(FONT_NAME, Font.PLAIN, 20);

    private static final int MAX_WIDTH = 760;

    private JTabbedPane tabs;

    public Settings() {
        setLayout(new GridBagLayout());
        setBackground(BG_DARK);

        // ── Card ──────────────────────────────────────────────────────────
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL_BG);
        card.setPreferredSize(new Dimension(MAX_WIDTH, 0));
        card.setMaximumSize(new Dimension(MAX_WIDTH, Integer.MAX_VALUE));

        tabs = createRetroTabbedPane();
        tabs.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        tabs.setOpaque(true);
        card.add(tabs, BorderLayout.CENTER);
        

        // ── Center card ───────────────────────────────────────────────────
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.VERTICAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(card, gbc);
    }

    public void addTab(String title, JPanel tab) {
        tabs.addTab(title, tab);
    }

    private JTabbedPane createRetroTabbedPane() {
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(FONT_BODY);
        tab.setFocusable(false);
        tab.setBackground(PANEL_BG);
        tab.setForeground(BORDER_LIGHT);
        tab.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tab.setUI(new BasicTabbedPaneUI() {
        	@Override
        	protected void installDefaults() {
        	    super.installDefaults();
        	    highlight      = PANEL_BG;
        	    lightHighlight = PANEL_BG;
        	    shadow         = PANEL_BG;
        	    darkShadow     = PANEL_BG;
        	    focus          = PANEL_BG;
        	    tabPane.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        	}

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement,
                    int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected ? TAB_SELECTED : TAB_HOVER);
                g2.fillRect(x, y, w, h);
                g2.dispose();
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement,
                    int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(isSelected ? ACCENT_GREEN : BORDER_LIGHT);
                // Bottom accent line on selected tab
                if (isSelected) {
                    g2.fillRect(x, y + h - 3, w, 3);
                }
                // Outer border
                g2.setColor(new Color(60, 60, 70));
                g2.drawRect(x, y, w - 1, h);
                g2.dispose();
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement,
                    java.awt.Rectangle[] rects, int tabIndex,
                    java.awt.Rectangle iconRect, java.awt.Rectangle textRect,
                    boolean isSelected) {
                // No focus ring
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                Graphics2D g2 = (Graphics2D) g.create();
                int tabAreaH = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                int w = tabPane.getWidth();
                int h = tabPane.getHeight();
                // Fill the content area background cleanly
                g2.setColor(PANEL_BG);
                g2.fillRect(0, tabAreaH, w, h - tabAreaH);
                // Single clean border line
                g2.setColor(new Color(60, 60, 70));
                g2.drawRect(0, tabAreaH, w - 1, h - tabAreaH - 1);
                g2.dispose();
            }
            
            @Override
            protected Insets getContentBorderInsets(int tabPlacement) {
                return new Insets(1, 0, 0, 0); // just 1px top to connect with tab row
            }

            @Override
            protected Insets getTabAreaInsets(int tabPlacement) {
                return new Insets(4, 4, 0, 4);
            }

            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 38; // fixed tab height
            }
        });

        return tab;
    }
}