package ma.ac.emi.gamecontrol;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.UI.*;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.gamelogic.attack.type.AOELoader;
import ma.ac.emi.gamelogic.attack.type.ProjectileLoader;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.ItemLoader;
import ma.ac.emi.gamelogic.shop.ShopManager;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.world.World;

@Getter
@Setter
public class GameController implements Runnable {
	private static final long SIM_STEP = (long)(Math.pow(10, 9)/60);
    private static GameController instance;
    
    public static GameController getInstance() {
        if (instance == null)
            instance = new GameController();
        return instance;
    }

    private final Window window;
    private Player player;
    private World world;
    private GamePanel gamePanel;
    private GameUIPanel gameUIPanel;
    private Camera camera;
    private Thread gameThread;
    private GameState state = GameState.MENU;
    
    private ShopManager shopManager;

    private GameController() {
        window = new Window();
		ItemLoader.getInstance().loadItems("items.json");		
		ProjectileLoader.getInstance().load("projectiles.json");
		AOELoader.getInstance().load("aoe.json");
        shopManager = new ShopManager(Player.getInstance());
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
    
    public void showGame() {
    	window.showScreen("GAME");
    }
    
    public void showShop() {
    	state = GameState.SHOP;
    	shopManager.refreshAvailableItems();
    	window.refreshShop();
    	window.showScreen("SHOP");
    }
    
    public void resumeGame() {
        state = GameState.PLAYING;
        KeyHandler.getInstance().init();
        showGame();
        startGameThread();
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
        showGame();
        startGameThread();
    }
    
    	public void startGameThread() {
        if (gameThread != null && gameThread.isAlive()) {
            // Stop current loop safely
            state = GameState.STOPPED;
            try {
                gameThread.join(); // wait for it to stop
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Start a fresh loop
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
