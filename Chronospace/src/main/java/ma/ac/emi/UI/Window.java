package ma.ac.emi.UI;

import javax.swing.JFrame;

public class Window extends JFrame implements Runnable{
	private Thread UIThread;
	private MainMenu mainMenu;
	
	public Window() {
		UIThread = new Thread(this);
		mainMenu = new MainMenu();
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
}
