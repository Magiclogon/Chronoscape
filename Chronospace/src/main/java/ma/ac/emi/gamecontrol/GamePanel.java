package ma.ac.emi.gamecontrol;

import java.awt.*;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.world.World;

public class GamePanel extends JPanel implements Runnable{
	public final static long SIM_STEP = 1000000000/60;
	public final static int TILE_SIZE = 16;
	
	private World world;
	
	public GamePanel(){
		KeyHandler.getInstance().setupKeyBindings(this);

		world = new World(1000, 1000);
	}
	@Override
	public void run() {
		long latestTime = System.nanoTime();
		long deltaTime = 0, accumTime = 0;
		do{
			deltaTime = System.nanoTime() - latestTime;
			latestTime += deltaTime;
			accumTime += deltaTime;
			while(accumTime > SIM_STEP) {
				update(SIM_STEP/Math.pow(10, 9));
				accumTime -= SIM_STEP;
			}
			repaint();
			
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}while(true);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		world.draw(g);
	}
	
	public void update(double step) {
		world.update(step);
	}
}
