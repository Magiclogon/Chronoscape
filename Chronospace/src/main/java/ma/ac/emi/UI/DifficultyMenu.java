package ma.ac.emi.UI;

import javax.swing.*;
import java.awt.*;

public class DifficultyMenu extends JPanel{
    private JLabel title;
    private JButton easyButton, mediumButton, hardButton, anwarButton;

    public DifficultyMenu(Window window) {
        title = new JLabel();
        easyButton = new JButton("Easy Button");
        mediumButton = new JButton("Medium Button");
        hardButton = new JButton("Hard Button");
        anwarButton = new JButton("Anwar Button");
        
        easyButton.addActionListener((e) -> window.showLevelSelection());

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
