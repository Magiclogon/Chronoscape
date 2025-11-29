package ma.ac.emi.UI;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import ma.ac.emi.gamecontrol.GameController;

public class LevelSelection extends JPanel {
	private JLabel title;
	private List<JButton> levelsButtons;
	private Image backgroundImage;

	public LevelSelection() {
		try {
			backgroundImage = ImageIO.read(getClass().getResource("/assets/Menus/main_menu_image.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		title = new JLabel();

		levelsButtons = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			JButton levelButton = new JButton("Level " + i);
			levelButton.addActionListener((e) -> GameController.getInstance().restartGame());
			levelsButtons.add(levelButton);
		}

		this.setBackground(Color.BLACK);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		title.setText("Choose Level");
		title.setForeground(Color.RED);
		title.setFont(new Font("Arial", Font.BOLD, 28));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);

		this.add(title);
		this.add(new Box.Filler(new Dimension(0, 20), new Dimension(0, 100), new Dimension(0, 200)));

		for (JButton levelButton : levelsButtons) {
			this.add(levelButton);
			levelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			this.add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 20), new Dimension(0, 20)));
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
		}
	}
}