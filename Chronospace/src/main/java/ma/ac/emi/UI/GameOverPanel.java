package ma.ac.emi.UI;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import ma.ac.emi.gamecontrol.GameController;

public class GameOverPanel extends JPanel implements Soundable{

	private static final long serialVersionUID = 1L;

	private Image backgroundImage;
	private JButton tryAgain;
	private JButton backToLevels;

	private static final int btnWidth = 224;
	private static final int btnHeight = 56;

	public GameOverPanel() {
		try {
			backgroundImage = ImageIO.read(getClass().getResource("/assets/Menus/game_over_image.png"));
		} catch (IOException | NullPointerException e) {
			System.err.println("Error loading background image: " + e.getMessage());
		}

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		tryAgain = createImageButton("tryagain1.png", "tryagain5.png");
		backToLevels = createImageButton("worlds1.png", "worlds5.png");

		configureButtonSounds(tryAgain, "hover_menu", "select_menu");
		configureButtonSounds(backToLevels, "hover_menu", "select_menu");

		tryAgain.addActionListener((e) -> GameController.getInstance().restartGame());
		backToLevels.addActionListener((e) -> GameController.getInstance().showLevelSelection());


		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(150, 20, 0, 20);
		add(tryAgain, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(20, 20, 20, 20); // Gap between buttons
		add(backToLevels, gbc);
	}

	private JButton createImageButton(String normalImage, String hoverImage) {
		JButton btn = new JButton();

		try {
			ImageIcon normalIcon = new ImageIcon(ImageIO.read(getClass().getResource("/assets/Menus/Buttons/" + normalImage)).getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH));
			ImageIcon hoverIcon = new ImageIcon(ImageIO.read(getClass().getResource("/assets/Menus/Buttons/" + hoverImage)).getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH));

			btn.setIcon(normalIcon);
			btn.setRolloverIcon(hoverIcon);

			btn.setBorderPainted(false);
			btn.setContentAreaFilled(false);
			btn.setFocusPainted(false);
			btn.setOpaque(false);

		} catch (Exception e) {
			btn.setText(normalImage.replace(".png", ""));
			System.err.println("Could not load button image: " + normalImage);
		}

		return btn;
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}
	}
}