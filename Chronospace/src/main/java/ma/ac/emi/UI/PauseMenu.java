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

public class PauseMenu extends JPanel implements Soundable{

    private static final int btnWidth = 224;
    private static final int btnHeight = 56;

    private static final long serialVersionUID = 1L;

    private Image backgroundImage;
    private JButton btnResume, btnMainMenu, btnSettings, btnQuit;

    public PauseMenu() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/assets/Menus/pause_image.png"));
        } catch (IOException | NullPointerException e) {
            System.err.println("Error loading pause background: " + e.getMessage());
        }

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        setOpaque(false);

        btnResume = createImageButton("Resume1.png", "Resume5.png");
        btnMainMenu = createImageButton("Main Menu1.png", "Main Menu5.png");
        btnSettings = new JButton("Settings");
        btnQuit = createImageButton("Quit1.png", "Quit5.png");

        configureButtonSounds(btnResume, "hover_menu", "select_menu");
        configureButtonSounds(btnMainMenu, "hover_menu", "select_menu");
        configureButtonSounds(btnSettings, "hover_menu", "select_menu");
        configureButtonSounds(btnQuit, "hover_menu", "select_menu");

        btnResume.addActionListener(e -> GameController.getInstance().resumeGame());

        btnMainMenu.addActionListener(e -> {
            GameController.getInstance().resumeGame();
            GameController.getInstance().showLevelSelection();
        });
        
        btnSettings.addActionListener(e -> {
        	GameController.getInstance().showSettings();
        });

        btnQuit.addActionListener(e -> System.exit(0));



        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(150, 0, 10, 0);
        add(btnResume, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        add(btnMainMenu, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 10, 0);
        add(btnSettings, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 10, 0);
        add(btnQuit, gbc);
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
            btn.setText(normalImage);
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