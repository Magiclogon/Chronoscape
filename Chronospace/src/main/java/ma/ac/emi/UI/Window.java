package ma.ac.emi.UI;

import javax.swing.JFrame;

public class Window extends JFrame implements Runnable{
	private Thread UIThread;
	private MainMenu mainMenu;
	private DifficultyMenu difficultyMenu;
	
	public Window() {
		UIThread = new Thread(this);
		mainMenu = new MainMenu(this);
		difficultyMenu = new DifficultyMenu();
		add(mainMenu);
	}

	public void startUIThread() {
		UIThread.run();
	}

	@Override
	public void run() {
		this.setSize(500, 500);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void showDifficultyMenu() {
		this.getContentPane().removeAll();
		this.add(difficultyMenu);
		this.revalidate();
		this.repaint();
	}
}
