package ma.ac.emi.gamecontrol;

import java.util.ArrayList;
import java.util.List;

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
    private GamePanel    gamePanel;
    private GameGLPanel  gameGLPanel;
    private GameUIPanel gameUIPanel;
    private GraphicsSettingsPanel settings;

    private Camera camera;
    private Thread gameThread;
    private GameState state = GameState.MENU;

    private ShopManager    shopManager;
    private ParticleSystem particleSystem;
    private SoundManager   soundManager;

    private DifficultyStrategy       difficulty;
    private List<DifficultyObserver> difficultyObservers;

    private PostFXConfig postFXConfig;

    private GameController() {
        window = new Window();
        showLoadingScreen();

        SwingUtilities.invokeLater(() ->
            new Thread(this::startupLoad, "GameController-StartupLoader").start()
        );
    }

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

        
        SwingUtilities.invokeLater(() ->
            window.getTransitionManager().fadeTo(
                () -> {
                    gamePanel  = new GamePanel();
                    gameGLPanel = new GameGLPanel();
                    gameUIPanel = new GameUIPanel();
                    
                    // Post-processing reload
                    GraphicsSettingsCallback callback = (updatedConfig) -> {
                        gameGLPanel.invoke(false, (glDrawable) -> {
                            gameGLPanel.getRenderer().reloadPostProcessing(
                                    glDrawable.getGL().getGL3(), updatedConfig);
                            return true;
                        });
                    };
                    settings = new GraphicsSettingsPanel(postFXConfig, callback);
                    
                    window.addSettings(new ControlsPanel(), "Controls");
                    window.addSettings(settings, "Graphics");
                    window.addSettings(new SoundSettingsPanel(), "Sound");

                    // Register KeyHandler on the JFrame — works for both the
                    // Swing menus and the GLCanvas (heavyweight, no InputMap)
                    KeyHandler.getInstance().setupKeyBindings(window);

                    // Register the canvas with the window (adds it to the JFrame)
                    window.registerGLCanvas(gameGLPanel);

                    window.showMenuMain();
                },
                null
            )
        );
    }

    private void loadSounds() {

        SoundManager sm = soundManager;   // alias for brevity

        // ── Music ─────────────────────────────────────────────────────────────
        sm.load("main_menu_music",   "/sounds/main_menu_1.wav",   SoundManager.Category.MUSIC);
        sm.load("pause_menu_music",  "/sounds/pause_menu_1.wav",  SoundManager.Category.MUSIC);
        sm.load("game_over_music",   "/sounds/game_over_1.wav",   SoundManager.Category.MUSIC);

        // ── UI  ───────────────────────────────────────────────────────────────
        sm.load("select_menu", "/sounds/select_004.wav", SoundManager.Category.UI);
        sm.load("hover_menu",  "/sounds/pluck_001.wav",  SoundManager.Category.UI);

        // ── Generic SFX ───────────────────────────────────────────────────────
        sm.load("light_weapon",  "/sounds/tick_001.wav",    SoundManager.Category.SFX);
        sm.load("heavy_weapon",  "/sounds/bong_001.wav",    SoundManager.Category.SFX);
        sm.load("ranged_weapon", "/sounds/click_001.wav",   SoundManager.Category.SFX);
        sm.load("melee_weapon",  "/sounds/scratch_001.wav", SoundManager.Category.SFX);
        sm.load("explosion",     "/sounds/explosion.wav",   SoundManager.Category.SFX);
        sm.load("spear",         "/sounds/spear.wav",       SoundManager.Category.SFX);
        sm.load("detonation",         "/sounds/detonation.wav",       SoundManager.Category.SFX);
        sm.load("pickable_collect",   "/sounds/confirmation_001.wav", SoundManager.Category.SFX);

        // ── Weapon-specific SFX ───────────────────────────────────────────────
        sm.load("ak47",        "/sounds/ak47.wav",        SoundManager.Category.SFX);
        sm.load("sniper",      "/sounds/sniper.wav",      SoundManager.Category.SFX);
        sm.load("machine_gun", "/sounds/machine_gun.wav", SoundManager.Category.SFX);
        sm.load("gun",         "/sounds/gun.wav",         SoundManager.Category.SFX);
        sm.load("flamethrower", "/sounds/flamethrower.wav", SoundManager.Category.SFX);
        sm.load("shotgun", "/sounds/shotgun.wav", SoundManager.Category.SFX);
        sm.load("hammer",       "/sounds/hammer.wav",       SoundManager.Category.SFX);
        sm.load("rpg_launch",       "/sounds/rpg_launch.wav",       SoundManager.Category.SFX);

        sm.setCooldown("flamethrower", 2000);  // loop manager handles timing
        sm.setCooldown("ak47",        15);
        sm.setCooldown("rpg_launch", 20);

        // Machine gun fires 12 shots/sec as well
        sm.setCooldown("machine_gun", 100);

        // Shotgun / heavy weapons don't need a pool, but short cooldown prevents
        // accidental double-plays on the same frame.
        sm.setCooldown("heavy_weapon", 30);
        sm.setCooldown("gun",          30);
        sm.setCooldown("hammer", 15);

        // Sniper has a slow reload – generous window
        sm.setCooldown("sniper", 200);
        sm.setCooldown("shotgun", 40);

        // UI sounds: don't spam on fast hover
        sm.setCooldown("hover_menu",  80);
        sm.setCooldown("select_menu", 120);
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

    public void showMainMenu() {
        state = GameState.MENU;
        SwingUtilities.invokeLater(() -> window.showMenuMain());
    }

    public void showDifficultyMenu() {
        state = GameState.DIFFICULTY_SELECT;
        SwingUtilities.invokeLater(() -> window.showMenuDifficulty());
    }

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
            window.refreshShop();
            window.navigateTo("SHOP");
        });
    }

    public void showGameOver() {
        state = GameState.GAME_OVER;
        SwingUtilities.invokeLater(() -> window.navigateTo("GAMEOVER"));
        soundManager.stopAll();
        soundManager.loop("game_over_music");
    }

    // ── Pause ─────────────────────────────────────────────────────────────

    public void togglePause() {
        if (state == GameState.PLAYING) pauseGame();
        else if (state == GameState.PAUSED) resumeGame();
    }

    public void pauseGame() {
        state = GameState.PAUSED;
        SwingUtilities.invokeLater(() -> window.navigateTo("PAUSE"));
        soundManager.stopCategory(SoundManager.Category.SFX);
        soundManager.stopCategory(SoundManager.Category.MUSIC);
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
    	gameGLPanel.resetFade();
        window.getTransitionManager().startWithLoading(
                this::loadGame,
                this::startGame
        );
    }

    private void loadGame() {
        removeAllDrawables();
        particleSystem.init();

        addDrawable(Player.getInstance());
        Player.getInstance().setDrawn(true);
        shopManager  = new ShopManager(Player.getInstance());
        worldManager = new WorldManager(difficulty);
        shopManager.init();
    }

    /** @deprecated use restartGameWithTransition() */
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
        
        gameUIPanel.setSize(gameGLPanel.getWidth(), gameGLPanel.getHeight());
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
            
            gameGLPanel.display();

            try { Thread.sleep(1); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public void update(double step) {
        worldManager.update(step);
        camera.update(step);
        Player.getInstance().update(step);
        particleSystem.update(step);
        gameGLPanel.update(step); // calls renderer.update() then display()
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