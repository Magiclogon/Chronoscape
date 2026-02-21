package ma.ac.emi.UI;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;
import ma.ac.emi.gamecontrol.*;

public class Window extends JFrame {
    private final CardLayout layout;
    private final JPanel mainPanel;
    private final NavigationManager navigationManager;

    // Screens
    private final LoadingScreen loadingScreen;
    private final PauseMenu pauseMenu;
    private final MainMenu mainMenu;
    private final DifficultyMenu difficultyMenu;
    private final LevelSelection levelSelection;
    private ShopUI shopUI;
    private GameOverPanel gameOverPanel;
    
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

        loadingScreen = new LoadingScreen();
        mainMenu = new MainMenu();
        pauseMenu = new PauseMenu();
        difficultyMenu = new DifficultyMenu();
        levelSelection = new LevelSelection();
        shopUI = new ShopUI();
        gameOverPanel = new GameOverPanel();

        gamePane = new JLayeredPane();
        gamePane.setLayout(new OverlayLayout(gamePane));

        mainPanel.add(loadingScreen, "LOADING");
        mainPanel.add(mainMenu, "MENU");
        mainPanel.add(pauseMenu, "PAUSE");
        mainPanel.add(difficultyMenu, "DIFFICULTY");
        mainPanel.add(levelSelection, "LEVEL_SELECT");
        mainPanel.add(gamePane, "GAME");
        mainPanel.add(shopUI, "SHOP");
        mainPanel.add(gameOverPanel, "GAMEOVER");

        transitionPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        transitionPane.add(fadeOverlay, JLayeredPane.PALETTE_LAYER);

        add(transitionPane);

        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        mainPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        fadeOverlay.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        transitionPane.setSize(getWidth(), getHeight());
        
        // Optional: Add debug navigation listener
        navigationManager.addNavigationListener((from, to) -> {
            System.out.println("Navigation: " + from + " -> " + to);
        });
    }

    /**
     * Navigate to a screen (adds to navigation history)
     */
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
        navigationManager.backToRoot("MENU");
        layout.show(mainPanel, "MENU");
        revalidate();
        repaint();
    }
    

    public boolean canGoBack() {
        return navigationManager.canGoBack();
    }
    

    public boolean canGoForward() {
        return navigationManager.canGoForward();
    }
    

    public String getCurrentScreen() {
        return navigationManager.getCurrentScreen();
    }
    
   
    public NavigationManager getNavigationManager() {
        return navigationManager;
    }
    
    /**
     * Legacy method - now uses navigateTo
     * @deprecated Use navigateTo() instead
     */
    @Deprecated
    public void showScreen(String name) {
        navigateTo(name);
    }
    
    public void refreshShop() {
        shopUI.refresh();
    }
    
    public void addSettings(GraphicsSettingsPanel settings) {
        JScrollPane scrollPane = new JScrollPane(settings);
        mainPanel.add(scrollPane, "SETTINGS");
    }

    public void showGame(GameGLPanel gameGLPanel, GameUIPanel uiPanel) {
        gamePane.removeAll();

        Dimension max = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);

        gameGLPanel.setPreferredSize(new Dimension(1280, 720));
        gameGLPanel.setMaximumSize(max);

        uiPanel.setPreferredSize(new Dimension(1280, 720));
        uiPanel.setMaximumSize(max);

        gamePane.add(gameGLPanel, Integer.valueOf(0)); 
        gamePane.add(uiPanel, Integer.valueOf(1));
        
        gamePane.revalidate();
        gamePane.repaint();
    }

    public void transition(Runnable midAction) {
        Timer timer = new Timer(16, null);
        final float[] alpha = {0f};
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