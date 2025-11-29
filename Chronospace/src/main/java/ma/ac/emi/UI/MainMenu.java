package ma.ac.emi.UI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import ma.ac.emi.gamecontrol.GameController;

public class MainMenu extends JPanel {
	private static final long serialVersionUID = 1L;
	private JButton startButton, quitButton;
	private Image backgroundImage;

	// Variables to hold the button images
	private ImageIcon playIcon;
	private ImageIcon playHoverIcon;

	public MainMenu() {
		try {
			// 1. Load Background
			backgroundImage = ImageIO.read(getClass().getResource("/assets/Menus/main_menu_image.png"));

			// 2. Load Button Images (RENAME THESE TO MATCH YOUR FILES)
			Image playImg = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/play.png"));
			Image playHoverImg = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/play_hover.png"));

			playIcon = new ImageIcon(playImg);
			playHoverIcon = new ImageIcon(playHoverImg);

		} catch (Exception e) {
			System.err.println("Error loading menu images. Check file paths in MainMenu.java");
			e.printStackTrace();
		}

		startButton = new JButton();

		if (playIcon != null) {
			startButton.setIcon(playIcon);
			startButton.setRolloverIcon(playHoverIcon);

			startButton.setBorderPainted(false);
			startButton.setContentAreaFilled(false);
			startButton.setFocusPainted(false);
			startButton.setOpaque(false);
		} else {
			startButton.setText("Start");
		}

		startButton.addActionListener(e -> GameController.getInstance().showDifficultyMenu());

		quitButton = new JButton("Quit");
		quitButton.addActionListener(e -> System.exit(0));

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		this.add(new Box.Filler(new Dimension(0, 20), new Dimension(0, 100), new Dimension(0, 200)));
		this.add(startButton);
		this.add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 20), new Dimension(0, 20)));
		this.add(quitButton);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
		}
	}
}