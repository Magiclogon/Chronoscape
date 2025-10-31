package ma.ac.emi.UI;

import java.awt.*;

import javax.swing.JButton;
import javax.swing.JPanel;

import ma.ac.emi.gamecontrol.GameController;

public class ShopUI extends JPanel{
	JButton nextWaveButton;
	
	public ShopUI() {
		nextWaveButton = new JButton("Next Wave");
		nextWaveButton.addActionListener((e) -> {
			GameController.getInstance().resumeGame();
			GameController.getInstance().showGame();

		});
		add(nextWaveButton);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawString("SHOP", GameController.getInstance().getWindow().getWidth()/2, GameController.getInstance().getWindow().getHeight()/2);
	}
}
