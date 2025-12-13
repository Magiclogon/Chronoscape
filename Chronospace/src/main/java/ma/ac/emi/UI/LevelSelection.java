package ma.ac.emi.UI;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import ma.ac.emi.gamecontrol.GameController;

public class LevelSelection extends JPanel {
	private Image backgroundImage;

	private JButton level1Button;
	private ImageIcon level1Icon, level1HoverIcon;

	private static final int btnWidth = 224;
	private static final int btnHeight = 56;

	public LevelSelection() {
		try {
			backgroundImage = ImageIO.read(getClass().getResource("/assets/Menus/main_menu_image.png"));

			Image level1ImgRaw = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/Level11.png")).getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
			Image level1HoverImgRaw = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/Level15.png")).getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);

			level1Icon = new ImageIcon(level1ImgRaw);
			level1HoverIcon = new ImageIcon(level1HoverImgRaw);

		} catch (Exception e) {
			e.printStackTrace();
		}

		level1Button = new JButton();

		if (level1Icon != null) {
			level1Button.setIcon(level1Icon);
			level1Button.setRolloverIcon(level1HoverIcon);

			level1Button.setBorderPainted(false);
			level1Button.setContentAreaFilled(false);
			level1Button.setFocusPainted(false);
			level1Button.setOpaque(false);
		} else {
			level1Button.setText("Easy difficulty");
		}

		level1Button.addActionListener((e) -> GameController.getInstance().restartGame());
		level1Button.setAlignmentX(Component.CENTER_ALIGNMENT);

		this.setBackground(Color.BLACK);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(new Box.Filler(new Dimension(0, 20), new Dimension(0, 300), new Dimension(0, 320)));
		this.add(level1Button);

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
		}
	}
}