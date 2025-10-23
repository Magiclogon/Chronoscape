package ma.ac.emi.UI;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.input.KeyHandler;

public class Window extends JFrame{
	private MainMenu mainMenu;
	private LevelSelection levelSelection;
	private DifficultyMenu difficultyMenu;	
	private GameUIPanel gameUIPanel;
	private GamePanel gamePanel;
	
	public Window() {
		mainMenu = new MainMenu(this);
		difficultyMenu = new DifficultyMenu(this);
		levelSelection = new LevelSelection(this);
		gameUIPanel = new GameUIPanel();
		gamePanel = new GamePanel();
		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		add(mainMenu);
		
		this.setVisible(true);
	}
	
	private void showComponent(JComponent component) {
		this.getContentPane().removeAll();
		this.add(component);
		this.revalidate();
		this.repaint();
	}
	
	private void showPanel(JPanel panel) {
		showComponent(panel);
	}
	public void showLevelSelection() {
		showPanel(levelSelection);
	}

	public void showDifficultyMenu() {
		showPanel(difficultyMenu);
	}

	public void startGame() {
		SwingUtilities.invokeLater(() -> {
			Thread gameThread = new Thread(gamePanel);
		    gameThread.start();
		});
	    JLayeredPane layeredPane = new JLayeredPane();
	    layeredPane.setPreferredSize(new Dimension(800, 600));

	    gamePanel.setBounds(0, 0, 800, 600);
	    gameUIPanel.setBounds(0, 0, 800, 600);

	    layeredPane.add(gamePanel, Integer.valueOf(0));
	    layeredPane.add(gameUIPanel, Integer.valueOf(1));

	    showComponent(layeredPane);
	}

}
