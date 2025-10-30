package ma.ac.emi.UI;

import java.awt.*;
import javax.swing.*;
import ma.ac.emi.gamecontrol.*;

public class Window extends JFrame {
    private final CardLayout layout;
    private final JPanel mainPanel;

    // Screens
    private final MainMenu mainMenu;
    private final DifficultyMenu difficultyMenu;
    private final LevelSelection levelSelection;

    public Window(GameController controller) {
        layout = new CardLayout();
        mainPanel = new JPanel(layout);

        mainMenu = new MainMenu();
        difficultyMenu = new DifficultyMenu();
        levelSelection = new LevelSelection();

        mainPanel.add(mainMenu, "MENU");
        mainPanel.add(difficultyMenu, "DIFFICULTY");
        mainPanel.add(levelSelection, "LEVEL_SELECT");

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
        // You can still use a JLayeredPane for the actual gameplay
        JLayeredPane layeredPane = new JLayeredPane();
        Dimension size = getContentPane().getSize();
        layeredPane.setPreferredSize(size);

        gamePanel.setBounds(0, 0, size.width, size.height);
        uiPanel.setBounds(0, 0, size.width, size.height);

        layeredPane.add(gamePanel, Integer.valueOf(0));
        layeredPane.add(uiPanel, Integer.valueOf(1));

        mainPanel.add(layeredPane, "GAME");
        layout.show(mainPanel, "GAME");
    }
}
