package ma.ac.emi.UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ma.ac.emi.gamecontrol.GameController;

public class MainMenu extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel title;
	private JButton startButton, quitButton;
	
	public MainMenu() {
		title = new JLabel();
		startButton = new JButton("Start");
		startButton.addActionListener(e -> GameController.getInstance().showDifficultyMenu());
		quitButton = new JButton("Quit");
		quitButton.addActionListener(e -> System.exit(0));
		
		this.setBackground(Color.BLACK);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		title.setText("Chronospace");
		title.setForeground(Color.RED);
		title.setFont(new Font("Arial", Font.BOLD, 28));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.add(title);
		this.add(new Box.Filler(new Dimension(0, 20), new Dimension(0, 100), new Dimension(0, 200)));
		this.add(startButton);
		this.add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 20), new Dimension(0, 20)));
		this.add(quitButton);
	}
	
	
	
}
