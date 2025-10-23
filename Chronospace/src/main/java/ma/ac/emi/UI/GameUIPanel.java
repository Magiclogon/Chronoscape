package ma.ac.emi.UI;

import java.awt.*;

import javax.swing.JPanel;

public class GameUIPanel extends JPanel{
	
	public GameUIPanel() {
		this.setOpaque(false);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(30, 30, 200, 50);
	}
}
