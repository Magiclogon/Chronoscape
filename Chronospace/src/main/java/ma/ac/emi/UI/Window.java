package ma.ac.emi.UI;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window extends JFrame implements Runnable{
	private Thread UIThread;
	private MainMenu mainMenu;
	private LevelSelection levelSelection;
	private DifficultyMenu difficultyMenu;
	
	
	public Window() {
		
		
	}

	public void startUIThread() {
		UIThread = new Thread(this);
		UIThread.start();
	}

	@Override
	public void run() {
		mainMenu = new MainMenu(this);
		difficultyMenu = new DifficultyMenu();
		levelSelection = new LevelSelection(this);
		this.setSize(500, 500);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(mainMenu);

		
	}
	
	private void showPanel(JPanel panel) {
		this.getContentPane().removeAll();
		this.add(panel);
		this.revalidate();
		this.repaint();
	}
	public void showLevelSelection() {
		showPanel(levelSelection);
	}

	public void showDifficultyMenu() {
		showPanel(difficultyMenu);
	}
}
