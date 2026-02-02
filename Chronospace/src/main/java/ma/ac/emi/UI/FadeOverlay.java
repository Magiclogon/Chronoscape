package ma.ac.emi.UI;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class FadeOverlay extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private float alpha = 0f;

    public void setAlpha(float a) {
        alpha = Math.max(0f, Math.min(1f, a));
        repaint();
    }

    public float getAlpha() {
        return alpha;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (alpha <= 0f) return;
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
