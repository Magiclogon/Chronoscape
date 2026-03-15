package ma.ac.emi.UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import ma.ac.emi.UI.component.RetroButton;
import ma.ac.emi.gamecontrol.GameController;

public class GameOverPanel extends JPanel implements Soundable {

    private static final long serialVersionUID = 1L;

    private Image backgroundImage;

    public GameOverPanel() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/assets/Menus/game_over_image.png"));
        } catch (Exception e) {
            System.err.println("Error loading game over background: " + e.getMessage());
        }

        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel overlay = (JPanel) MenuStyle.makeSidebarOverlay();

        RetroButton tryAgainBtn = new RetroButton("TRY AGAIN",  RetroButton.Style.MENU, MenuStyle.ACCENT);
        RetroButton mainMenuBtn = new RetroButton("MAIN MENU",  RetroButton.Style.MENU, MenuStyle.ACCENT_DIM);

        MenuStyle.sizeButton(tryAgainBtn);
        MenuStyle.sizeButton(mainMenuBtn);

        configureButtonSounds(tryAgainBtn, "hover_menu", "select_menu");
        configureButtonSounds(mainMenuBtn, "hover_menu", "select_menu");

        tryAgainBtn.addActionListener(e -> GameController.getInstance().restartGameWithTransition());
        mainMenuBtn.addActionListener(e -> GameController.getInstance().showMainMenu());

        overlay.add(Box.createVerticalGlue());
        overlay.add(tryAgainBtn);
        overlay.add(mainMenuBtn);
        overlay.add(Box.createVerticalStrut(MenuStyle.BOTTOM_STRUT));

        JPanel split = new JPanel(new GridLayout(1, 3, 0, 0));
        split.setOpaque(false);
        split.add(new JPanel() {{ setOpaque(false); }});
        split.add(new JPanel() {{ setOpaque(false); }});
        split.add(overlay);

        add(split, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null)
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}