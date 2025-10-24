package ma.ac.emi.gamecontrol;

import java.awt.*;

import javax.swing.JPanel;

import ma.ac.emi.camera.Camera;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.world.World;

public class GamePanel extends JPanel implements Runnable{
	public final static long SIM_STEP = (long)(Math.pow(10, 9)/60);
	public final static int TILE_SIZE = 16;
	
	private World world;
	private Camera camera;
	
	public GamePanel(){
		KeyHandler.getInstance().setupKeyBindings(this);
		camera = new Camera(new Vector2D(), 100, 100, this);
		world = new World(20, 10, camera);
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
		camera.update(step);
		world.update(step);
	}
}
