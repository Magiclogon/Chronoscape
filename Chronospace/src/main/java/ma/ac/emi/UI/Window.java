package ma.ac.emi.UI;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import ma.ac.emi.gamecontrol.GameController; // <-- Import Controller
import ma.ac.emi.gamecontrol.GamePanel;

public class Window extends JFrame {
	private MainMenu mainMenu;
	private LevelSelection levelSelection;
	private DifficultyMenu difficultyMenu;
	private GameController gameController;

	public Window() {
		mainMenu = new MainMenu(this);
		difficultyMenu = new DifficultyMenu(this);
		levelSelection = new LevelSelection(this);
		gameController = new GameController();

		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
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
		GamePanel gamePanel = gameController.getGamePanel();
		GameUIPanel gameUIPanel = gameController.getGameUIPanel();

		JLayeredPane layeredPane = new JLayeredPane();

		Dimension size = this.getContentPane().getSize();
		layeredPane.setPreferredSize(size);

		gamePanel.setBounds(0, 0, size.width, size.height);
		gameUIPanel.setBounds(0, 0, size.width, size.height);

		layeredPane.add(gamePanel, Integer.valueOf(0));
		layeredPane.add(gameUIPanel, Integer.valueOf(1));

		// Adjust in real time
		layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent e) {
				Dimension newSize = layeredPane.getSize();
				gamePanel.setBounds(0, 0, newSize.width, newSize.height);
				gameUIPanel.setBounds(0, 0, newSize.width, newSize.height);
				gamePanel.revalidate();
				gamePanel.repaint();
			}
		});

		showComponent(layeredPane);

		gameController.startGameThread();
	}
}