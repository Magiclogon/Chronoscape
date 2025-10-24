package ma.ac.emi.gamecontrol;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.UI.GameUIPanel;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.world.World;

@Setter
@Getter
public class GameController implements Runnable {

	public static final long SIM_STEP = (long)(Math.pow(10, 9) / 60);

	private final World world;
	private final GamePanel gamePanel;
	private final GameUIPanel gameUIPanel;
	private Camera camera;
	private Thread gameThread;

	public GameController() {
		world = new World(500, 500);
		gamePanel = new GamePanel(world);
		gameUIPanel = new GameUIPanel(world);

		double camWidth = 800;
		double camHeight = 600;

		camera = new Camera(new Vector2D(0, 0), camWidth, camHeight, gamePanel, world.getPlayer());
		camera.snapTo(world.getPlayer());

		gamePanel.setCamera(camera);
	}

	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void run() {
		long latestTime = System.nanoTime();
		long deltaTime;
		long accumTime = 0;

		do {
			deltaTime = System.nanoTime() - latestTime;
			latestTime += deltaTime;
			accumTime += deltaTime;

			while(accumTime > SIM_STEP) {
				update(SIM_STEP / Math.pow(10, 9));
				accumTime -= SIM_STEP;
			}
			gamePanel.repaint();
			gameUIPanel.repaint();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while(true);
	}

	public void update(double step) {
		world.update(step);
		camera.update(step);
	}
}
