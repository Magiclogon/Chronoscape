package ma.ac.emi.gamecontrol;

import ma.ac.emi.UI.*;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.world.World;

public class GameController implements Runnable {
	private static final long SIM_STEP = (long)(Math.pow(10, 9)/60);
    private static GameController instance;

    public static GameController getInstance() {
        if (instance == null)
            instance = new GameController();
        return instance;
    }

    private final Window window;
    private World world;
    private GamePanel gamePanel;
    private GameUIPanel gameUIPanel;
    private Camera camera;
    private Thread gameThread;
    private GameState state = GameState.MENU;

    private GameController() {
        window = new Window(this);
        showMainMenu();
    }

    public void showMainMenu() {
        state = GameState.MENU;
        window.showScreen("MENU");
    }

    public void showDifficultyMenu() {
        state = GameState.DIFFICULTY_SELECT;
        window.showScreen("DIFFICULTY");
    }

    public void showLevelSelection() {
        state = GameState.LEVEL_SELECT;
        window.showScreen("LEVEL_SELECT");
    }

    public void startGame() {
        state = GameState.PLAYING;

        world = new World(50, 50);
        gamePanel = new GamePanel(world);
        gameUIPanel = new GameUIPanel(world);

        camera = new Camera(new Vector2D(), 640, 480, gamePanel, world.getPlayer());
        camera.snapTo(world.getPlayer());
        gamePanel.setCamera(camera);
        
        MouseHandler.getInstance().setCamera(camera);

        window.showGame(gamePanel, gameUIPanel);
        startGameThread();
    }
    
    public void startGameThread() {
        if (gameThread != null && gameThread.isAlive()) return;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long latestTime = System.nanoTime();
        long deltaTime;
        long accumTime = 0;

        while (state == GameState.PLAYING) {
            deltaTime = System.nanoTime() - latestTime;
            latestTime += deltaTime;
            accumTime += deltaTime;

            while (accumTime > SIM_STEP) {
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
        }
    }

    public void update(double step) {
        world.update(step);
        camera.update(step);
    }
}
