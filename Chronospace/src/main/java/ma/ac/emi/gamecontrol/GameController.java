package ma.ac.emi.gamecontrol;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.UI.*;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.gamelogic.attack.type.AOELoader;
import ma.ac.emi.gamelogic.attack.type.ProjectileLoader;
import ma.ac.emi.gamelogic.difficulty.DifficultyObserver;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.particle.ParticleSystem;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.ItemLoader;
import ma.ac.emi.gamelogic.shop.ShopManager;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector3D;
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
    private ParticleSystem particleSystem;

    private DifficultyStrategy difficulty;
    private List<DifficultyObserver> difficultyObservers;
    
    private GameController() {
        window = new Window();
        
        AssetsLoader.loadAssets("assets");
        particleSystem = new ParticleSystem();
        particleSystem.loadFromJson("src/main/resources/configs/particles.json");
		ItemLoader.getInstance().loadItems("src/main/resources/configs/items.json");		
		ProjectileLoader.getInstance().load("src/main/resources/configs/projectiles.json");
		AOELoader.getInstance().load("src/main/resources/configs/aoe.json");
		
		difficultyObservers = new ArrayList<>();
		
        gamePanel = new GamePanel();
        gameUIPanel = new GameUIPanel();

        showMainMenu();

    }
    
    public void nextWorld() {
    	gamePanel.removeAllDrawables();
		gamePanel.addDrawable(Player.getInstance());
		Player.getInstance().setDrawn(true);
    	worldManager.nextWorld();
    }

    public void showMainMenu() {
        state = GameState.MENU;
        SwingUtilities.invokeLater(() -> {
        	window.showScreen("MENU");
    	});
    }

    public void showDifficultyMenu() {
        state = GameState.DIFFICULTY_SELECT;
        SwingUtilities.invokeLater(() -> {
        	window.showScreen("DIFFICULTY");
    	});
    }

    public void showLevelSelection() {
        state = GameState.LEVEL_SELECT;
        SwingUtilities.invokeLater(() -> {
        	window.showScreen("LEVEL_SELECT");
    	});
    }
    
    public void showGame() {
    	SwingUtilities.invokeLater(() -> {
        	window.showScreen("GAME");
    	});
    }
    
    public void showShop() {
    	state = GameState.SHOP;
    	shopManager.init();
    	SwingUtilities.invokeLater(() -> {
        	window.refreshShop();
        	window.showScreen("SHOP");
    	});

    }
    
    public void showGameOver() {
    	state = GameState.GAME_OVER;
    	SwingUtilities.invokeLater(() -> {
        	window.showScreen("GAMEOVER");
    	});
    }
    
    public void resumeGame() {
        state = GameState.PLAYING;
        startGame();
    }
    

	public void restartGame() {
		gamePanel.removeAllDrawables();
		gamePanel.addDrawable(Player.getInstance());
		Player.getInstance().setDrawn(true);
        shopManager = new ShopManager(Player.getInstance());
        worldManager = new WorldManager(difficulty);
		shopManager.init();
		startGame();
	}


    public void startGame() {
        state = GameState.PLAYING;
        
        World world = worldManager.getCurrentWorld();
        world.getPickableManager().init();
    	particleSystem.init();

        camera = new Camera(new Vector3D(), 640, 480, gamePanel, world.getPlayer());
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
        particleSystem.update(step);
        
        GameTime.addTime(step);
    }
    
    public void setDifficulty(DifficultyStrategy difficulty) {
    	this.difficulty = difficulty;
    	notifyDifficultyObservers();
    }
    
    public void notifyDifficultyObservers() {
    	difficultyObservers.forEach((o) -> o.refreshDifficulty(difficulty));
    }

	public void addDifficultyObserver(DifficultyObserver observer) {
		this.difficultyObservers.add(observer);
		if(difficulty != null) notifyDifficultyObservers();
	}

}
