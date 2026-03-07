package ma.ac.emi.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class Settings extends JPanel{
	private static final long serialVersionUID = 1L;
	
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
		
	private JTabbedPane tabs;
	
	public Settings() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_DARK);
		
        tabs = createRetroTabbedPane();
        add(tabs);
	}
	
	public void addTab(String title, JScrollPane tab) {
		tabs.add(title, tab);
	}
	
	private JTabbedPane createRetroTabbedPane() {
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(FONT_BODY);
        tab.setForeground(Color.BLACK); // Text color of tabs
        tab.setBackground(BORDER_LIGHT); // Bg color of tabs
        return tab;
    }
}
