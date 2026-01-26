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

public class MainMenu extends JPanel implements Soundable{
	private static final long serialVersionUID = 1L;
	private JButton startButton, quitButton;
	private Image backgroundImage;

	// Variables to hold the button images
	private ImageIcon startIcon;
	private ImageIcon startHoverIcon;

	private ImageIcon quitIcon;
	private ImageIcon quitHoverIcon;

	public MainMenu() {
		try {

			// 1. Load Background
			backgroundImage = ImageIO.read(getClass().getResource("/assets/Menus/main_menu_image.png"));

			Image startImgRaw = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/Start1.png"));
			Image startHoverImgRaw = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/Start5.png"));
			Image quitImgRaw = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/Quit1.png"));
			Image quitHoverImgRaw = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/Quit5.png"));

			int btnWidth = 224;
			int btnHeight = 56;

			Image startImg = startImgRaw.getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
			Image startHoverImg = startHoverImgRaw.getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
			Image quitImg = quitImgRaw.getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
			Image quitHoverImg = quitHoverImgRaw.getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);

			startIcon = new ImageIcon(startImg);
			startHoverIcon = new ImageIcon(startHoverImg);

			quitIcon = new ImageIcon(quitImg);
			quitHoverIcon = new ImageIcon(quitHoverImg);

		} catch (Exception e) {
			System.err.println("Error loading menu images. Check file paths in MainMenu.java");
			e.printStackTrace();
		}

		startButton = new JButton();

		if (startIcon != null) {
			startButton.setIcon(startIcon);
			startButton.setRolloverIcon(startHoverIcon);

			startButton.setBorderPainted(false);
			startButton.setContentAreaFilled(false);
			startButton.setFocusPainted(false);
			startButton.setOpaque(false);
		} else {
			startButton.setText("Start");
		}

		quitButton = new JButton();

		if (quitIcon != null) {
			quitButton.setIcon(quitIcon);
			quitButton.setRolloverIcon(quitHoverIcon);
			quitButton.setBorderPainted(false);
			quitButton.setContentAreaFilled(false);
			quitButton.setFocusPainted(false);
			quitButton.setOpaque(false);
		} else {
			quitButton.setText("Quit");
		}

		configureButtonSounds(startButton, "hover_menu", "select_menu");
		configureButtonSounds(quitButton, "hover_menu", "select_menu");

		startButton.addActionListener(e -> {
			GameController.getInstance().showDifficultyMenu();
		});

		quitButton.addActionListener(e -> {
			System.exit(0);
		});

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		this.add(new Box.Filler(new Dimension(0, 100), new Dimension(0, 200), new Dimension(0, 300)));
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