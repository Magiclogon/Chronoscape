package ma.ac.emi.gamecontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.*;

import com.jogamp.opengl.GLAutoDrawable;
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
import ma.ac.emi.glgraphics.post.config.PostFXConfig;
import ma.ac.emi.glgraphics.post.config.PostFXConfigLoader;
import ma.ac.emi.input.KeyHandler;
import ma.ac.emi.input.MouseHandler;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.sound.SoundManager;
import ma.ac.emi.world.World;
import ma.ac.emi.world.WorldManager;

@Getter
@Setter
public class GameController implements Runnable {
    private static final long SIM_STEP = (long)(Math.pow(10, 9) / 60);
    private static GameController instance;

    public static Semaphore draw   = new Semaphore(0);
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
    private GraphicsSettingsPanel settings;

    private Camera camera;
    private Thread gameThread;
    private GameState state = GameState.MENU;

    private ShopManager shopManager;
    private ParticleSystem particleSystem;

    private SoundManager soundManager;

    private DifficultyStrategy difficulty;
    private List<DifficultyObserver> difficultyObservers;

    private PostFXConfig postFXConfig;

    private GameController() {
        window = new Window();
        showLoadingScreen();

        // Yield to the EDT so the loading screen renders its first frame,
        // then do all heavy startup work on a background thread.
        SwingUtilities.invokeLater(() ->
            new Thread(this::startupLoad, "GameController-StartupLoader").start()
        );
    }

    /**
     * All heavy initialisation that was previously blocking the EDT.
     * Runs on a background thread — must not touch Swing components directly.
     * Swing setup at the end is marshalled back onto the EDT via invokeLater.
     */
    private void startupLoad() {
        AssetsLoader.loadAssets("assets");
        particleSystem = new ParticleSystem();
        particleSystem.loadFromJson("src/main/resources/configs/particles.json");
        ItemLoader.getInstance().loadItems("src/main/resources/configs/items.json");
        ProjectileLoader.getInstance().load("src/main/resources/configs/projectiles.json");
        AOELoader.getInstance().load("src/main/resources/configs/aoe.json");
        postFXConfig = PostFXConfigLoader.load();

        difficultyObservers = new ArrayList<>();

        soundManager = new SoundManager();
        loadSounds();

        // All loading done — kick off the fade on the EDT.
        // Heavy Swing setup runs inside switchAction (at peak black) so the
        // fade plays smoothly before any EDT work blocks it.
        SwingUtilities.invokeLater(() ->
            window.getTransitionManager().fadeTo(
                () -> {
                    // At peak black: create Swing components — freeze is invisible
                    gamePanel   = new GamePanel();
                    gameUIPanel = new GameUIPanel();
                    gameGLPanel = new GameGLPanel();

                    GraphicsSettingsCallback callback = (updatedConfig) -> {
                        gameGLPanel.invoke(false, (glDrawable) -> {
                            gameGLPanel.getRenderer().reloadPostProcessing(
                                    glDrawable.getGL().getGL3(),
                                    updatedConfig);
                            return true;
                        });
                    };
                    settings = new GraphicsSettingsPanel(postFXConfig, callback);

                    window.addSettings(settings, "Graphics");
                    window.addSettings(new SoundSettingsPanel(), "Sound");
                    window.showGame(gameGLPanel, gameUIPanel);

                    // Switch card to main menu while still black
                    window.showMenuMain();
                },
                null  // fade back in reveals the main menu
            )
        );
    }

    private void loadSounds() {
        soundManager.load("main_menu_music", "/sounds/main_menu_1.wav");
        soundManager.load("select_menu",     "/sounds/select_004.wav");
        soundManager.load("hover_menu",      "/sounds/pluck_001.wav");
        soundManager.load("pause_menu_music","/sounds/pause_menu_1.wav");
        soundManager.load("game_over_music", "/sounds/game_over_1.wav");

        soundManager.load("light_weapon",  "/sounds/tick_001.wav");
        soundManager.load("heavy_weapon",  "/sounds/bong_001.wav");
        soundManager.load("ranged_weapon", "/sounds/click_001.wav");
        soundManager.load("melee_weapon",  "/sounds/scratch_001.wav");
        soundManager.load("ak47",          "/sounds/ak47.wav");
        soundManager.load("sniper",        "/sounds/sniper.wav");
        soundManager.load("machine_gun",   "/sounds/machine_gun.wav");
        soundManager.load("gun",           "/sounds/gun.wav");
        soundManager.load("spear",         "/sounds/spear.wav");
        soundManager.load("explosion",     "/sounds/explosion.wav");
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
            window.navigateTo("LOADING");
            window.getLoadingScreen().startAnimation();
        });
    }

    /** Shows the main menu — navigates to MENU_HOST and shows the main button set. */
    public void showMainMenu() {
        state = GameState.MENU;
        SwingUtilities.invokeLater(() -> window.showMenuMain());
        //soundManager.loop("main_menu_music");
    }

    /** Shows the difficulty sidebar inside MENU_HOST — background stays put. */
    public void showDifficultyMenu() {
        state = GameState.DIFFICULTY_SELECT;
        SwingUtilities.invokeLater(() -> window.showMenuDifficulty());
    }

    /**
     * Level selection is removed (only one level).
     * DifficultyMenuSidebar calls restartGameWithTransition() directly.
     * This stub is kept so any lingering call-sites compile without error.
     */
    public void showLevelSelection() {
        restartGameWithTransition();
    }

    public void showSettings() {
        state = GameState.SETTINGS;
        SwingUtilities.invokeLater(() -> window.navigateTo("SETTINGS"));
    }

    public void showGame() {
        SwingUtilities.invokeLater(() -> window.navigateTo("GAME"));
    }

    public void showShop() {
        state = GameState.SHOP;
        shopManager.init();
        particleSystem.clearActiveEffects();

        SwingUtilities.invokeLater(() -> {
            System.out.println("showing shop..");
            window.refreshShop();
            window.navigateTo("SHOP");
        });
    }

    public void showGameOver() {
        state = GameState.GAME_OVER;
        SwingUtilities.invokeLater(() -> window.navigateTo("GAMEOVER"));
        soundManager.stopAll();
        soundManager.play("game_over_music");
    }

    // ── Pause ─────────────────────────────────────────────────────────────

    public void togglePause() {
        if (state == GameState.PLAYING) pauseGame();
        else if (state == GameState.PAUSED) resumeGame();
    }

    public void pauseGame() {
        state = GameState.PAUSED;
        SwingUtilities.invokeLater(() -> window.navigateTo("PAUSE"));
        soundManager.stopAll();
    }

    public void resumeGame() {
        state = GameState.PLAYING;
        latestTime = System.nanoTime();
        KeyHandler.getInstance().reset();
        SwingUtilities.invokeLater(() -> window.navigateTo("GAME"));
        soundManager.stopAll();
    }

    public void nextWave() {
        worldManager.getCurrentWorld().clearAttackObjects();
        resumeGame();
    }

    // ── Game start ────────────────────────────────────────────────────────

    public void restartGameWithTransition() {
        // loadingWork runs off the EDT — safe for heavy setup
        // onComplete runs on the EDT after the second fade-in begins
        window.getTransitionManager().startWithLoading(
                this::loadGame,   // background thread: world/player init
                this::startGame   // EDT: camera setup + show game card
        );
    }

    /**
     * Heavy initialisation — runs on a background thread inside TransitionManager.
     * Must NOT touch Swing components directly.
     */
    private void loadGame() {
        removeAllDrawables();
        particleSystem.init();

        addDrawable(Player.getInstance());
        Player.getInstance().setDrawn(true);
        shopManager  = new ShopManager(Player.getInstance());
        worldManager = new WorldManager(difficulty);
        shopManager.init();
    }

    /** @deprecated kept so external call-sites compile; use restartGameWithTransition() */
    public void restartGame() { restartGameWithTransition(); }

    public void startGame() {
        state = GameState.PLAYING;
        soundManager.stopAll();

        World world = worldManager.getCurrentWorld();

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
        deltaTime  = 0;
        accumTime  = 0;

        if (gameThread != null && gameThread.isAlive()) return;

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (true) {
            if (KeyHandler.getInstance().consumeTogglePause()) togglePause();

            long currentTime = System.nanoTime();
            deltaTime  = currentTime - latestTime;
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

            SwingUtilities.invokeLater(() -> gameUIPanel.repaint());

            try { Thread.sleep(1); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public void update(double step) {
        worldManager.update(step);
        camera.update(step);
        Player.getInstance().update(step);
        particleSystem.update(step);
        gameGLPanel.update(step);
        GameTime.addTime(step);
    }

    public void setDifficulty(DifficultyStrategy difficulty) {
        this.difficulty = difficulty;
        notifyDifficultyObservers();
    }

    public void notifyDifficultyObservers() {
        difficultyObservers.forEach(o -> o.refreshDifficulty(difficulty));
    }

    public void addDifficultyObserver(DifficultyObserver observer) {
        this.difficultyObservers.add(observer);
        if (difficulty != null) notifyDifficultyObservers();
    }

    public void removeDrawable(GameObject object)  { gameGLPanel.getRenderer().removeDrawable(object); }
    public void addDrawable(GameObject object)     { gameGLPanel.getRenderer().addDrawable(object); }
    private void removeAllDrawables()              { gameGLPanel.getRenderer().removeAllDrawables(); }
    public double getFPS()                         { return gameGLPanel.getRenderer().getFPS(); }

    public void addLightObject(LightObject lightObject) {
        getWorldManager().getCurrentWorld().addLightObject(lightObject);
    }

    public void removeLightObject(LightObject lightObject) {
        getWorldManager().getCurrentWorld().removeLightObject(lightObject);
    }

    public double getWorldZ() {
        return getWorldManager().getCurrentWorld().getPos().getZ();
    }
}