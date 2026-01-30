package ma.ac.emi.gamecontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.*;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.UI.*;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.gamelogic.attack.AttackObject;
import ma.ac.emi.gamelogic.attack.type.AOELoader;
import ma.ac.emi.gamelogic.attack.type.ProjectileLoader;
import ma.ac.emi.gamelogic.difficulty.DifficultyObserver;
import ma.ac.emi.gamelogic.difficulty.DifficultyStrategy;
import ma.ac.emi.gamelogic.particle.ParticleAnimationCache;
import ma.ac.emi.gamelogic.particle.ParticleSystem;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.ItemLoader;
import ma.ac.emi.gamelogic.shop.ShopManager;
import ma.ac.emi.glgraphics.lighting.LightObject;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.sound.SoundManager;
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
    private GameGLPanel gameGLPanel;
    private GameUIPanel gameUIPanel;
    private Camera camera;
    private Thread gameThread;
    private GameState state = GameState.MENU;
    
    private ShopManager shopManager;
    private ParticleSystem particleSystem;

    private SoundManager soundManager;

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

        // Sound Loading
        soundManager = new SoundManager();
        loadSounds();
		
        gamePanel = new GamePanel();
        gameUIPanel = new GameUIPanel();
        
        gameGLPanel = new GameGLPanel();
        
        window.showGame(gameGLPanel, gameUIPanel);
        showMainMenu();
    }

    private void loadSounds() {
        soundManager.load("main_menu_music", "/sounds/main_menu_1.wav");
        soundManager.load("select_menu", "/sounds/select_004.wav");
        soundManager.load("hover_menu", "/sounds/pluck_001.wav");
        soundManager.load("pause_menu_music", "/sounds/pause_menu_1.wav");
        soundManager.load("game_over_music", "/sounds/game_over_1.wav");
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
        soundManager.loop("main_menu_music");
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
    	particleSystem.init();
    	    	
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
        soundManager.stopAll();
        soundManager.play("game_over_music");
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
        soundManager.stopAll();
        soundManager.play("pause_menu_music");
    }

    public void resumeGame() {
        state = GameState.PLAYING;
        latestTime = System.nanoTime();
        
        KeyHandler.getInstance().reset();
        SwingUtilities.invokeLater(() -> window.showScreen("GAME"));
        soundManager.stopAll();
    }



	public void restartGame() {
		removeAllDrawables();
        particleSystem.init();

		addDrawable(Player.getInstance());
		Player.getInstance().setDrawn(true);
		Player.getInstance().init();
        shopManager = new ShopManager(Player.getInstance());
        worldManager = new WorldManager(difficulty);
		shopManager.init();
		startGame();
	}

	public void startGame() {
        state = GameState.PLAYING;

        soundManager.stopAll();
        
        World world = worldManager.getCurrentWorld();
        world.getPickableManager().init();

        camera = new Camera(new Vector3D(), 640, 480, gameGLPanel, world.getPlayer());
        camera.snapTo(world.getPlayer());
        camera.setRenderScale(gameGLPanel.getRenderer().getRenderScale());
        gamePanel.setCamera(camera);
        gameGLPanel.setCamera(camera);
        
        KeyHandler.getInstance().reset();
        MouseHandler.getInstance().setCamera(camera);

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

            if (state == GameState.PLAYING) {
			    accumTime += deltaTime;

			    while (accumTime > SIM_STEP) {
			        update(SIM_STEP / Math.pow(10, 9));
			        accumTime -= SIM_STEP;
			    }
			} else {
			    accumTime = 0;
			}

            SwingUtilities.invokeLater(() -> {
                //gamePanel.repaint();
                //gameGLPanel.repaint();
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
        //gamePanel.update(step);
        gameGLPanel.update(step);
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

	public void removeDrawable(GameObject object) {
		this.gameGLPanel.getRenderer().removeDrawable(object);
	}

	public void addDrawable(GameObject object) {
		this.gameGLPanel.getRenderer().addDrawable(object);
	}
	
    private void removeAllDrawables() {
		gameGLPanel.getRenderer().removeAllDrawables();
	}
	
	public double getFPS() {
		return gameGLPanel.getRenderer().getFPS();
	}
	
	public void addLightObject(LightObject lightObject) {
		getWorldManager().getCurrentWorld().addLightObject(lightObject);
	}
	
	public void removeLightObject(LightObject lightObject) {
		getWorldManager().getCurrentWorld().removeLightObject(lightObject);
	}
}
