package ma.ac.emi.UI;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;
import ma.ac.emi.gamecontrol.*;

public class Window extends JFrame {
    private final CardLayout layout;
    private final JPanel mainPanel;

    // Screens
    private final LoadingScreen loadingScreen;
    private final PauseMenu pauseMenu;
    private final MainMenu mainMenu;
    private final DifficultyMenu difficultyMenu;
    private final LevelSelection levelSelection;
    private ShopUI shopUI;
    private GameOverPanel gameOverPanel;
    
    private final JLayeredPane gamePane;
    
    //private final GameGLPanel gameGLPanel;

    public Window() {
        layout = new CardLayout();
        mainPanel = new JPanel(layout);

        loadingScreen = new LoadingScreen();
        mainMenu = new MainMenu();
        pauseMenu = new PauseMenu();
        difficultyMenu = new DifficultyMenu();
        levelSelection = new LevelSelection();
        gamePane = new JLayeredPane();
        shopUI = new ShopUI();
        gameOverPanel = new GameOverPanel();
        
        //gameGLPanel = new GameGLPanel();

        mainPanel.add(loadingScreen, "LOADING");
        mainPanel.add(mainMenu, "MENU");
        mainPanel.add(pauseMenu, "PAUSE");
        mainPanel.add(difficultyMenu, "DIFFICULTY");
        mainPanel.add(levelSelection, "LEVEL_SELECT");
        mainPanel.add(gamePane, "GAME");
        mainPanel.add(shopUI, "SHOP");
        mainPanel.add(gameOverPanel, "GAMEOVER");


        setSize(1280, 720);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        add(mainPanel);

    }
    
    public void refreshShop() {
    	shopUI.refresh();
    }

    public void showScreen(String name) {
        layout.show(mainPanel, name);
        revalidate();
        repaint();
    }
    

    public void showGame(GameGLPanel gameGLPanel, GameUIPanel uiPanel) {
    	gamePane.removeAll();
    	gamePane.setSize(getSize());
        gameGLPanel.setBounds(0, 0, gamePane.getSize().width, gamePane.getSize().height);
        uiPanel.setBounds(0, 0, gamePane.getSize().width, gamePane.getSize().height);
        
        gamePane.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {
				gameGLPanel.setBounds(0, 0, gamePane.getSize().width, gamePane.getSize().height);
		        uiPanel.setBounds(0, 0, gamePane.getSize().width, gamePane.getSize().height);
				
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        gamePane.add(gameGLPanel, Integer.valueOf(0));
        gamePane.add(uiPanel, Integer.valueOf(1));
    }
}
