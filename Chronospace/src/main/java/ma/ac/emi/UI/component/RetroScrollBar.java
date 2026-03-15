package ma.ac.emi.UI.component;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import ma.ac.emi.UI.MenuStyle;

public class RetroScrollBar extends JScrollBar {
    private static final long serialVersionUID = 1L;

    public RetroScrollBar(int orientation) {
        super(orientation);
        setUI(new RetroScrollBarUI());
    }

    @Override public void updateUI() { setUI(new RetroScrollBarUI()); }
    @Override public void setUI(javax.swing.plaf.ScrollBarUI ui) { super.setUI(new RetroScrollBarUI()); }
}

class RetroScrollBarUI extends BasicScrollBarUI {

    @Override
    protected void configureScrollBarColors() {
        thumbColor = MenuStyle.SCROLLBAR_THUMB;
        trackColor = MenuStyle.SCROLLBAR_TRACK;
    }

    @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
    @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }

    private JButton zeroBtn() {
        JButton b = new JButton();
        Dimension d = new Dimension(0, 0);
        b.setPreferredSize(d); b.setMinimumSize(d); b.setMaximumSize(d);
        return b;
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(MenuStyle.SCROLLBAR_THUMB);
        g2.fillRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4);
        g2.setColor(MenuStyle.TEXT_BORDER);
        g2.drawRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4);
        g2.dispose();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        g.setColor(MenuStyle.SCROLLBAR_TRACK);
        g.fillRect(r.x, r.y, r.width, r.height);
    }
}