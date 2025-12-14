package ma.ac.emi.gamecontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.*;

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
    
    public static Semaphore draw = new Semaphore(0);
    public static Semaphore update = new Semaphore(1);
    
    public static GameController getInstance() {
        if (instance == null)
            instance = new GameController();
        return instance;
    }
    
    long latestTime;
    long deltaTime;
    long accumTime;
    
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

        showLoadingScreen();

        AssetsLoader.loadAssets("assets");
        particleSystem = new ParticleSystem();
        particleSystem.loadFromJson("src/main/resources/configs/particles.json");
		ItemLoader.getInstance().loadItems("src/main/resources/configs/items.json");		
		ProjectileLoader.getInstance().load("src/main/resources/configs/projectiles.json");
		AOELoader.getInstance().load("src/main/resources/configs/aoe.json");

		difficultyObservers = new ArrayList<>();
		
        gamePanel = new GamePanel();
        gameUIPanel = new GameUIPanel();

        Timer timer = new Timer(3000, e -> {
            showMainMenu();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    public void nextWorld() {
    	gamePanel.removeAllDrawables();
		gamePanel.addDrawable(Player.getInstance());
		Player.getInstance().setDrawn(true);
    	worldManager.nextWorld();
    }

    public void showLoadingScreen() {
        state = GameState.LOADING;
        SwingUtilities.invokeLater(() -> {
        	window.showScreen("LOADING");
        });
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
        	System.out.println("showing shop..");

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

    // --- PAUSE LOGIC ---

    /**
     * Toggles between Playing and Paused states.
     */
    public void togglePause() {
        if (state == GameState.PLAYING) {
            pauseGame();
        } else if (state == GameState.PAUSED) {
            resumeGame();
        }
    }

    public void pauseGame() {
        state = GameState.PAUSED;
        SwingUtilities.invokeLater(() -> window.showScreen("PAUSE"));
    }

    public void resumeGame() {
        state = GameState.PLAYING;
        latestTime = System.nanoTime();
        
        KeyHandler.getInstance().reset();
        SwingUtilities.invokeLater(() -> window.showScreen("GAME"));
    }



	public void restartGame() {
		gamePanel.removeAllDrawables();
		gamePanel.addDrawable(Player.getInstance());
		Player.getInstance().setDrawn(true);
		Player.getInstance().init();
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
        
        KeyHandler.getInstance().reset();
        MouseHandler.getInstance().setCamera(camera);

        window.showGame(gamePanel, gameUIPanel);
        showGame();
        System.out.println("Starting game");
        startGameThread();
    }
    
	public void startGameThread() {
        latestTime = System.nanoTime();
        deltaTime = 0;
        accumTime = 0;
        
	    if (gameThread != null && gameThread.isAlive()) {
	    	return;
	    }
	
	    // Start a fresh loop

	    gameThread = new Thread(this);
	    gameThread.start();
    }

    @Override
    public void run() {
        while (true) {
            if (KeyHandler.getInstance().consumeTogglePause()) {
                togglePause();
            }

            long currentTime = System.nanoTime();
            deltaTime = currentTime - latestTime;
            latestTime = currentTime;

            try {
                update.acquire();

                if (state == GameState.PLAYING) {
                    accumTime += deltaTime;

                    while (accumTime > SIM_STEP) {
                        update(SIM_STEP / Math.pow(10, 9));
                        accumTime -= SIM_STEP;
                    }
                } else {
                    accumTime = 0;
                }

                draw.release();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SwingUtilities.invokeLater(() -> {
                gamePanel.repaint();
                gameUIPanel.repaint();
            });

            try {
                // Small sleep to prevent CPU hogging
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(double step) {
    	worldManager.update(step);
        camera.update(step);
        Player.getInstance().update(step);

        particleSystem.update(step);
        gamePanel.update(step);
        
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
