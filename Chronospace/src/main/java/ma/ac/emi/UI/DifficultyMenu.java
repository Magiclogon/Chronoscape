package ma.ac.emi.UI;

import javax.imageio.ImageIO;
import javax.swing.*;

import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.difficulty.EasyDifficultyStrategy;
import ma.ac.emi.gamelogic.difficulty.HardDifficultyStrategy;
import ma.ac.emi.gamelogic.difficulty.MediumDifficultyStrategy;

import java.awt.*;
import java.io.IOException;

public class DifficultyMenu extends JPanel {
    private JButton easyButton, mediumButton, hardButton, anwarButton;
    private Image backgroundImage;

    private ImageIcon easyIcon, easyHoverIcon, mediumIcon, mediumHoverIcon, hardIcon, hardHoverIcon, anwarIcon, anwarHoverIcon;

    public DifficultyMenu() {
        try {
            // 1. Load Background
            backgroundImage = ImageIO.read(getClass().getResource("/assets/Menus/main_menu_image.png"));

            int btnWidth = 224;
            int btnHeight = 56;

            Image easyImgRaw = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/Easy1.png")).getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
            Image easyHoverImgRaw = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/Easy5.png")).getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
            Image mediumImgRaw = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/Medium1.png")).getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
            Image mediumHoverImgRaw = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/Medium5.png")).getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
            Image hardImgRaw = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/Hard1.png")).getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
            Image hardHoverImgRaw = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/Hard5.png")).getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
            Image anwarImgRaw = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/Anwar1.png")).getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
            Image anwarHoverImgRaw = ImageIO.read(getClass().getResource("/assets/Menus/Buttons/Anwar5.png")).getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);

            easyIcon = new ImageIcon(easyImgRaw);
            easyHoverIcon = new ImageIcon(easyHoverImgRaw);

            mediumIcon = new ImageIcon(mediumImgRaw);
            mediumHoverIcon = new ImageIcon(mediumHoverImgRaw);

            hardIcon = new ImageIcon(hardImgRaw);
            hardHoverIcon = new ImageIcon(hardHoverImgRaw);

            anwarIcon = new ImageIcon(anwarImgRaw);
            anwarHoverIcon = new ImageIcon(anwarHoverImgRaw);

        } catch (Exception e) {
            System.err.println("Error loading menu images. Check file paths in MainMenu.java");
        }

        easyButton = new JButton();

        if (easyIcon != null) {
            easyButton.setIcon(easyIcon);
            easyButton.setRolloverIcon(easyHoverIcon);

            easyButton.setBorderPainted(false);
            easyButton.setContentAreaFilled(false);
            easyButton.setFocusPainted(false);
            easyButton.setOpaque(false);
        } else {
            easyButton.setText("Easy difficulty");
        }

        mediumButton = new JButton();

        if (mediumIcon != null) {
            mediumButton.setIcon(mediumIcon);
            mediumButton.setRolloverIcon(mediumHoverIcon);

            mediumButton.setBorderPainted(false);
            mediumButton.setContentAreaFilled(false);
            mediumButton.setFocusPainted(false);
            mediumButton.setOpaque(false);
        } else {
            mediumButton.setText("Medium Difficulty");
        }

        hardButton = new JButton();

        if (hardIcon != null) {
            hardButton.setIcon(hardIcon);
            hardButton.setRolloverIcon(hardHoverIcon);

            hardButton.setBorderPainted(false);
            hardButton.setContentAreaFilled(false);
            hardButton.setFocusPainted(false);
            hardButton.setOpaque(false);
        } else {
            hardButton.setText("Hard difficulty");
        }

        anwarButton = new JButton();

        if (anwarIcon != null) {
            anwarButton.setIcon(anwarIcon);
            anwarButton.setRolloverIcon(anwarHoverIcon);

            anwarButton.setBorderPainted(false);
            anwarButton.setContentAreaFilled(false);
            anwarButton.setFocusPainted(false);
            anwarButton.setOpaque(false);
        } else {
            anwarButton.setText("Anwar Difficulty");
        }

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

        easyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mediumButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        anwarButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.setBackground(Color.BLACK);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(new Box.Filler(new Dimension(0, 20), new Dimension(0, 100), new Dimension(0, 225)));
        this.add(easyButton);
        this.add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 20), new Dimension(0, 20)));
        this.add(mediumButton);
        this.add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 20), new Dimension(0, 20)));
        this.add(hardButton);
        this.add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 20), new Dimension(0, 20)));
        this.add(anwarButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}