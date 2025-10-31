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
    private final MainMenu mainMenu;
    private final DifficultyMenu difficultyMenu;
    private final LevelSelection levelSelection;
    private final ShopUI shopUI;
    
    private final JLayeredPane gamePane;

    public Window() {
        layout = new CardLayout();
        mainPanel = new JPanel(layout);

        mainMenu = new MainMenu();
        difficultyMenu = new DifficultyMenu();
        levelSelection = new LevelSelection();
        shopUI = new ShopUI();
        gamePane = new JLayeredPane();

        mainPanel.add(mainMenu, "MENU");
        mainPanel.add(difficultyMenu, "DIFFICULTY");
        mainPanel.add(levelSelection, "LEVEL_SELECT");
        mainPanel.add(shopUI, "SHOP");
        mainPanel.add(gamePane, "GAME");

        add(mainPanel);

        setSize(1280, 720);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void showScreen(String name) {
        layout.show(mainPanel, name);
        revalidate();
        repaint();
    }

    public void showGame(GamePanel gamePanel, GameUIPanel uiPanel) {
    	gamePane.removeAll();
        gamePanel.setBounds(0, 0, gamePane.getSize().width, gamePane.getSize().height);
        uiPanel.setBounds(0, 0, gamePane.getSize().width, gamePane.getSize().height);
        
        gamePane.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {
				gamePanel.setBounds(0, 0, gamePane.getSize().width, gamePane.getSize().height);
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
        
        gamePane.add(gamePanel, Integer.valueOf(0));
        gamePane.add(uiPanel, Integer.valueOf(1));
    }
}
