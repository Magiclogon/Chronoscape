package ma.ac.emi.gamecontrol;

import java.awt.*;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.world.World;

public class GamePanel extends JPanel implements Runnable{
	public final static long SIM_STEP = (long)(Math.pow(10, 9)/60);
	public final static int TILE_SIZE = 16;
	
	private World world;
	double x = 0;
	
	public GamePanel(){
		KeyHandler.getInstance().setupKeyBindings(this);

		world = new World(20, 10);
	}
	@Override
	public void run() {
		long latestTime = System.nanoTime();
		long deltaTime = 0, accumTime = 0, currentTime;
		do{
			/*currentTime = System.nanoTime();
			update(deltaTime/Math.pow(10, 9));
			repaint();
			latestTime = System.nanoTime();
			deltaTime = latestTime - currentTime;*/
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
		g.setColor(Color.yellow);
		System.out.println(x);

		g.fillRect((int)x, 100, 100, 100);
	}
	
	public void update(double step) {
		world.update(step);
		x += 2 * step;
	}
}
