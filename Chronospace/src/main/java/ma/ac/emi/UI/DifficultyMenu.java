package ma.ac.emi.UI;

import javax.swing.*;

import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.difficulty.EasyDifficultyStrategy;
import ma.ac.emi.gamelogic.difficulty.HardDifficultyStrategy;
import ma.ac.emi.gamelogic.difficulty.MediumDifficultyStrategy;

import java.awt.*;

public class DifficultyMenu extends JPanel{
    private JLabel title;
    private JButton easyButton, mediumButton, hardButton, anwarButton;

    public DifficultyMenu() {
        title = new JLabel();
        easyButton = new JButton("Easy Button");
        mediumButton = new JButton("Medium Button");
        hardButton = new JButton("Hard Button");
        anwarButton = new JButton("Anwar Button");
        
        easyButton.addActionListener((e) -> {
        	GameController.getInstance().setDifficulty(new EasyDifficultyStrategy());
        	GameController.getInstance().showLevelSelection();
        });
        
        mediumButton.addActionListener((e) -> {
        	GameController.getInstance().setDifficulty(new MediumDifficultyStrategy());
        	GameController.getInstance().showLevelSelection();
        });

        hardButton.addActionListener((e) -> {
        	GameController.getInstance().setDifficulty(new HardDifficultyStrategy());
        	GameController.getInstance().showLevelSelection();
        });

        anwarButton.addActionListener((e) -> {
        	GameController.getInstance().setDifficulty(new HardDifficultyStrategy());
        	GameController.getInstance().showLevelSelection();
        });


        title.setForeground(Color.RED);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        easyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mediumButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        anwarButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.setBackground(Color.BLACK);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        title.setText("Difficulty");

        this.add(title);
        this.add(new Box.Filler(new Dimension(0, 20), new Dimension(0, 100), new Dimension(0, 200)));
        this.add(easyButton);
        this.add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 20), new Dimension(0, 20)));
        this.add(mediumButton);
        this.add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 20), new Dimension(0, 20)));
        this.add(hardButton);
        this.add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 20), new Dimension(0, 20)));
        this.add(anwarButton);
    }
}
