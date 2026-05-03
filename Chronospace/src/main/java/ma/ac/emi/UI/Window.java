package ma.ac.emi.UI;

import java.awt.*;
import javax.swing.*;
import ma.ac.emi.gamecontrol.*;
import ma.ac.emi.input.InputConfig;

public class Window extends JFrame {

    // These handle the swap between the Menus and the 3D Game
    private final CardLayout rootLayout;
    private final JPanel rootContainer;

    // These handle the swap between different menu screens (Loading, Menu, Shop)
    private final CardLayout layout; // Restored your original name
    private final JPanel mainPanel;
    
    private final NavigationManager navigationManager;

    private final LoadingScreen loadingScreen;
    private final PauseMenu pauseMenu;
    private final MenuHost menuHost;
    private ShopUI shopUI;
    private GameOverPanel gameOverPanel;
    private final Settings settings;
    private StartupWeaponSelection weaponSelection;

    private final JLayeredPane transitionPane;
    private final FadeOverlay fadeOverlay;
    private final TransitionManager transitionManager;

    private boolean isFullScreen = false;
    private Rectangle previousBounds;

    private GameGLPanel glCanvas;
    private volatile boolean glPaused = false;

    public Window() {
        // 1. Initialize the Root System (The "Master" Switch)
        rootLayout = new CardLayout();
        rootContainer = new JPanel(rootLayout);
        rootContainer.setBackground(Color.BLACK);

        // 2. Initialize the UI System (The Menu Switch)
        layout = new CardLayout();
        mainPanel = new JPanel(layout);
        mainPanel.setOpaque(false); // Allows GL to show through if needed
        
        navigationManager = new NavigationManager();

        loadingScreen = new LoadingScreen();
        pauseMenu = new PauseMenu();
        menuHost = new MenuHost();
        shopUI = new ShopUI();
        gameOverPanel = new GameOverPanel();
        settings = new Settings(this::goBack);
        weaponSelection = new StartupWeaponSelection();

        mainPanel.add(loadingScreen, "LOADING");
        mainPanel.add(menuHost, "MENU_HOST");
        mainPanel.add(pauseMenu, "PAUSE");
        mainPanel.add(shopUI, "SHOP");
        mainPanel.add(gameOverPanel, "GAMEOVER");
        mainPanel.add(settings, "SETTINGS");
        mainPanel.add(weaponSelection, "WEAPON_SELECT");

        // 3. Setup the Layered Pane (Menus + Fade Effect)
        fadeOverlay = new FadeOverlay();
        
        // Fix for OverlayLayout resizing: components must share the same alignment
        mainPanel.setAlignmentX(0.0f);
        mainPanel.setAlignmentY(0.0f);
        fadeOverlay.setAlignmentX(0.0f);
        fadeOverlay.setAlignmentY(0.0f);

        transitionPane = new JLayeredPane();
        transitionPane.setLayout(new OverlayLayout(transitionPane));
        
        transitionPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        transitionPane.add(fadeOverlay, JLayeredPane.PALETTE_LAYER);

        transitionManager = new TransitionManager(this, loadingScreen, fadeOverlay);

        // 4. Assemble the Root
        rootContainer.add(transitionPane, "UI_VIEW");
        
        // JFrame Setup
        setTitle("Chronospace"); // Use your game title
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);
        add(rootContainer, BorderLayout.CENTER);

        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        navigationManager.addNavigationListener((from, to) ->
                System.out.println("Navigation: " + from + " -> " + to));
        
        setVisible(true);
        
        CursorManager.init();
        CursorManager.apply(rootContainer, CursorManager.CursorType.ARROW);
    }

    // ── GLCanvas registration ─────────────────────────────────────────────

    public void registerGLCanvas(GameGLPanel canvas) {
        if (this.glCanvas != null) {
            rootContainer.remove(this.glCanvas);
        }
        this.glCanvas = canvas;
        
        rootContainer.add(glCanvas, "GAME_VIEW");
        
        rootLayout.show(rootContainer, "UI_VIEW");

        revalidate();
    }

    // ── Navigation ─────────────────────────

    public void navigateTo(String name) {
        navigationManager.navigateTo(name);

        if ("GAME".equals(name)) {
            resumeGL(); // handles card switch internally
            CursorManager.apply(glCanvas,
                    InputConfig.getInstance().keyboardAimMode ?
                            CursorManager.CursorType.HIDDEN :
                            CursorManager.CursorType.TARGET);
        } else {
            pauseGL(); // handles card switch internally
            layout.show(mainPanel, name);
            CursorManager.apply(rootContainer, CursorManager.CursorType.ARROW);
        }

        revalidate();
        repaint();
    }

    private void pauseGL() {
        glPaused = true;
        SwingUtilities.invokeLater(() -> {
            if (glCanvas != null) {
                rootContainer.remove(glCanvas);
                rootContainer.revalidate();
            }
            rootLayout.show(rootContainer, "UI_VIEW");
            rootContainer.repaint();
        });
    }

    private void resumeGL() {
        SwingUtilities.invokeLater(() -> {
            if (glCanvas != null) {
                rootContainer.add(glCanvas, "GAME_VIEW");
                rootContainer.revalidate();
                rootLayout.show(rootContainer, "GAME_VIEW");
                glCanvas.requestFocusInWindow();
            }
            glPaused = false;
        });
    }

    public boolean isGLPaused() { return glPaused; }

    public void jumpTo(String name) {
        navigationManager.jumpTo(name);
        layout.show(mainPanel, name);
        revalidate();
        repaint();
    }

    // ── MenuHost passthrough ──────────────────────────────────────────────

    public void showMenuMain() {
        menuHost.showMainMenu();
        navigateTo("MENU_HOST");
    }

    public void showMenuDifficulty() {
        menuHost.showDifficultyMenu();
        navigateTo("MENU_HOST");
    }

    // ── Misc & Helpers (Matching GameController requirements) ──────────────

    public boolean goBack() {
        String previous = navigationManager.goBack();
        if (previous != null) {
            navigateTo(previous);
            return true;
        }
        return false;
    }

    public boolean goForward() {
        String next = navigationManager.goForward();
        if (next != null) {
            navigateTo(next);
            return true;
        }
        return false;
    }

    public void backToMainMenu() {
        navigationManager.backToRoot("MENU_HOST");
        menuHost.showMainMenu();
        navigateTo("MENU_HOST");
    }
    
    public void showWeaponSelection(Runnable onSelected) {
        weaponSelection.setOnWeaponSelected(() -> {
            navigateTo("GAME"); // Hide the UI
            onSelected.run();
        });
        navigateTo("WEAPON_SELECT");
    }

    public boolean canGoBack()       { return navigationManager.canGoBack(); }
    public boolean canGoForward()    { return navigationManager.canGoForward(); }
    public String  getCurrentScreen() { return navigationManager.getCurrentScreen(); }
    
    public void refreshShop()        { if(shopUI != null) shopUI.refresh(); }
    public void addSettings(JPanel s, String title) { settings.addTab(title, s); }
    
    public TransitionManager getTransitionManager() { return transitionManager; }
    public LoadingScreen     getLoadingScreen()      { return loadingScreen; }
    public NavigationManager getNavigationManager() { return navigationManager; }

    public void setFullScreen(boolean fullScreen) {
        if (this.isFullScreen == fullScreen) return;
        this.isFullScreen = fullScreen;

        dispose();
        
        if (fullScreen) {
            previousBounds = getBounds();
            setUndecorated(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (gd.isFullScreenSupported()) {
                gd.setFullScreenWindow(this);
            }
        } else {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (gd.getFullScreenWindow() == this) {
                gd.setFullScreenWindow(null);
            }
            setUndecorated(false);
            setExtendedState(JFrame.NORMAL);
            if (previousBounds != null) {
                setBounds(previousBounds);
            } else {
                setSize(1280, 720);
                setLocationRelativeTo(null);
            }
        }

        setVisible(true);
    }

    public void transition(Runnable midAction) {
        transitionManager.fadeTo(midAction, null);
    }
}