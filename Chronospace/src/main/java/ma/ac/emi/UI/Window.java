package ma.ac.emi.UI;

import java.awt.*;
import javax.swing.*;
import ma.ac.emi.gamecontrol.*;

public class Window extends JFrame {
    private final CardLayout layout;
    private final JPanel mainPanel;
    private final NavigationManager navigationManager;

    // Screens
    private final LoadingScreen loadingScreen;
    private final PauseMenu pauseMenu;
    private final MenuHost menuHost;          // replaces MainMenu + DifficultyMenu + LevelSelection
    private ShopUI shopUI;
    private GameOverPanel gameOverPanel;
    private final Settings settings;

    private final JLayeredPane gamePane;

    private final JLayeredPane transitionPane;
    private final FadeOverlay fadeOverlay;

    public Window() {
        layout = new CardLayout();
        mainPanel = new JPanel(layout);
        navigationManager = new NavigationManager();

        transitionPane = new JLayeredPane();
        transitionPane.setLayout(new OverlayLayout(transitionPane));

        fadeOverlay = new FadeOverlay();

        loadingScreen  = new LoadingScreen();
        pauseMenu      = new PauseMenu();
        menuHost       = new MenuHost();       // single persistent menu panel
        shopUI         = new ShopUI();
        gameOverPanel  = new GameOverPanel();
        settings       = new Settings(this::goBack);

        gamePane = new JLayeredPane();
        gamePane.setLayout(new OverlayLayout(gamePane));

        mainPanel.add(loadingScreen, "LOADING");
        mainPanel.add(menuHost,      "MENU_HOST"); // one card for all menu screens
        mainPanel.add(pauseMenu,     "PAUSE");
        mainPanel.add(gamePane,      "GAME");
        mainPanel.add(shopUI,        "SHOP");
        mainPanel.add(gameOverPanel, "GAMEOVER");
        mainPanel.add(settings,      "SETTINGS");

        transitionPane.add(mainPanel,    JLayeredPane.DEFAULT_LAYER);
        transitionPane.add(fadeOverlay,  JLayeredPane.PALETTE_LAYER);

        add(transitionPane);

        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        mainPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        fadeOverlay.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        transitionPane.setSize(getWidth(), getHeight());

        navigationManager.addNavigationListener((from, to) ->
                System.out.println("Navigation: " + from + " -> " + to));
    }

    // ── MenuHost passthrough — lets GameController drive internal menu nav ─

    public void showMenuMain() {
        menuHost.showMainMenu();
        navigateTo("MENU_HOST");
    }

    public void showMenuDifficulty() {
        menuHost.showDifficultyMenu();
        navigateTo("MENU_HOST");
    }

    // ── Standard navigation ───────────────────────────────────────────────

    public void navigateTo(String name) {
        navigationManager.navigateTo(name);
        layout.show(mainPanel, name);
        revalidate();
        repaint();
    }

    public void jumpTo(String name) {
        navigationManager.jumpTo(name);
        layout.show(mainPanel, name);
        revalidate();
        repaint();
    }

    public boolean goBack() {
        String previous = navigationManager.goBack();
        if (previous != null) {
            layout.show(mainPanel, previous);
            revalidate();
            repaint();
            return true;
        }
        return false;
    }

    public boolean goForward() {
        String next = navigationManager.goForward();
        if (next != null) {
            layout.show(mainPanel, next);
            revalidate();
            repaint();
            return true;
        }
        return false;
    }

    public void backToMainMenu() {
        navigationManager.backToRoot("MENU_HOST");
        menuHost.showMainMenu();
        layout.show(mainPanel, "MENU_HOST");
        revalidate();
        repaint();
    }

    public boolean canGoBack()        { return navigationManager.canGoBack(); }
    public boolean canGoForward()     { return navigationManager.canGoForward(); }
    public String  getCurrentScreen() { return navigationManager.getCurrentScreen(); }
    public NavigationManager getNavigationManager() { return navigationManager; }

    /** @deprecated Use navigateTo() instead */
    @Deprecated
    public void showScreen(String name) { navigateTo(name); }

    public void refreshShop() { shopUI.refresh(); }

    public void addSettings(JPanel s, String title) { settings.addTab(title, s); }

    public void showGame(GameGLPanel gameGLPanel, GameUIPanel uiPanel) {
        gamePane.removeAll();
        Dimension max = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);

        gameGLPanel.setPreferredSize(new Dimension(1280, 720));
        gameGLPanel.setMaximumSize(max);

        uiPanel.setPreferredSize(new Dimension(1280, 720));
        uiPanel.setMaximumSize(max);

        gamePane.add(gameGLPanel, Integer.valueOf(0));
        gamePane.add(uiPanel,     Integer.valueOf(1));
        gamePane.revalidate();
        gamePane.repaint();
    }

    public void transition(Runnable midAction) {
        Timer timer = new Timer(16, null);
        final float[] alpha    = {0f};
        final boolean[] fadingOut = {true};

        timer.addActionListener(e -> {
            if (fadingOut[0]) {
                alpha[0] += 0.02f;
                if (alpha[0] >= 1f) {
                    alpha[0] = 1f;
                    fadingOut[0] = false;
                    midAction.run();
                }
            } else {
                alpha[0] -= 0.02f;
                if (alpha[0] <= 0f) {
                    alpha[0] = 0f;
                    timer.stop();
                }
            }
            fadeOverlay.setAlpha(alpha[0]);
        });

        timer.start();
    }
}