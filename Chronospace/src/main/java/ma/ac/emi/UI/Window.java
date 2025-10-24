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

		this.setSize(800, 600);
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
		SwingUtilities.invokeLater(() -> {
			Thread gameThread = new Thread(gamePanel);
		    gameThread.start();
		});
	    JLayeredPane layeredPane = new JLayeredPane();
	    layeredPane.setLayout(null); // still manual layout

	    // Add both at fixed layers
	    layeredPane.add(gamePanel, Integer.valueOf(0));
	    layeredPane.add(gameUIPanel, Integer.valueOf(1));

	    // Auto-resize listener
	    layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
	        @Override
	        public void componentResized(java.awt.event.ComponentEvent e) {
	            Dimension size = layeredPane.getSize();
	            gamePanel.setBounds(0, 0, size.width, size.height);
	            gameUIPanel.setBounds(0, 0, size.width, size.height);
	        }
	    });

	    showComponent(layeredPane);
	}

		layeredPane.add(gamePanel, Integer.valueOf(0));
		layeredPane.add(gameUIPanel, Integer.valueOf(1));

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