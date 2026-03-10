package ma.ac.emi.UI.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class RetroScrollBar extends JScrollBar {

	private static final long serialVersionUID = 1L;
	public RetroScrollBar(int orientation) {
        super(orientation); setUI(new RetroScrollBarUI());
    }
    @Override public void updateUI() { setUI(new RetroScrollBarUI()); }
    @Override public void setUI(javax.swing.plaf.ScrollBarUI ui) { super.setUI(new RetroScrollBarUI()); }
}


class RetroScrollBarUI extends BasicScrollBarUI {
    private static final Color ACCENT_GREEN = new Color(80, 200, 100);

    @Override protected void configureScrollBarColors() {
        thumbColor = new Color(255, 215, 0); trackColor = new Color(10, 10, 15);
    }
    @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
    @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
    private JButton zeroBtn() {
        JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0));
        b.setMinimumSize(new Dimension(0,0)); b.setMaximumSize(new Dimension(0,0)); return b;
    }
    @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(ACCENT_GREEN);
        g2.fillRect(r.x+2, r.y+2, r.width-4, r.height-4);
        g2.setColor(new Color(180,180,190));
        g2.drawRect(r.x+2, r.y+2, r.width-4, r.height-4);
        g2.dispose();
    }
    @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        g.setColor(new Color(10,10,15)); g.fillRect(r.x, r.y, r.width, r.height);
    }
}
