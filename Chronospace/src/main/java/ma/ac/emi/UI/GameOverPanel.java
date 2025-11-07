package ma.ac.emi.UI;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;

import ma.ac.emi.gamecontrol.GameController;

public class GameOverPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JButton tryAgain, backToLevels;
	
	public GameOverPanel() {
		tryAgain = new JButton("Try Again");
		backToLevels = new JButton("Back to world selection");
		
		tryAgain.addActionListener((e) -> GameController.getInstance().restartGame());
		backToLevels.addActionListener((e) -> GameController.getInstance().showLevelSelection());
		
		setBackground(Color.black);
		add(tryAgain);
		add(backToLevels);
	}
}
