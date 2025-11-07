package ma.ac.emi.gamecontrol;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.UI.*;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.gamelogic.attack.type.AOELoader;
import ma.ac.emi.gamelogic.attack.type.ProjectileLoader;
import ma.ac.emi.gamelogic.difficulty.EasyDifficultyStrategy;
import ma.ac.emi.gamelogic.factory.VampireFactory;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.ItemLoader;
import ma.ac.emi.gamelogic.shop.ShopManager;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector2D;
import ma.ac.emi.world.World;
import ma.ac.emi.world.WorldManager;

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
    private WorldManager worldManager;
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
        worldManager = new WorldManager(new EasyDifficultyStrategy(), new VampireFactory(new EasyDifficultyStrategy()));
        showMainMenu();
    }
    
    public void nextWorld() {
    	worldManager.nextWorld();
    	gamePanel.setWorld(worldManager.getCurrentWorld());
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
    	shopManager.init();
    	window.refreshShop();
    	window.showScreen("SHOP");
    }
    
    public void showGameOver() {
    	state = GameState.GAME_OVER;
    	window.showScreen("GAMEOVER");
    }
    
    public void resumeGame() {
        state = GameState.PLAYING;
        startGame();
    }
    

	public void restartGame() {
		worldManager.init();
		shopManager.init();
		startGame();
	}


    public void startGame() {
        state = GameState.PLAYING;
        
        World world = worldManager.getCurrentWorld();
        gamePanel = new GamePanel(world);
        gameUIPanel = new GameUIPanel(world);

        camera = new Camera(new Vector2D(), 640, 480, gamePanel, world.getPlayer());
        camera.snapTo(world.getPlayer());
        gamePanel.setCamera(camera);
        
        KeyHandler.getInstance().init();
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
        worldManager.update(step);
        camera.update(step);
    }

}
