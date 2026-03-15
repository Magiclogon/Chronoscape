package ma.ac.emi.UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import ma.ac.emi.UI.component.RetroButton;
import ma.ac.emi.gamecontrol.GameController;

public class PauseMenu extends JPanel implements Soundable {

    private static final long serialVersionUID = 1L;

    private Image backgroundImage;

    public PauseMenu() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/assets/Menus/pause_image.png"));
        } catch (Exception e) {
            System.err.println("Error loading pause background: " + e.getMessage());
        }

        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel overlay = (JPanel) MenuStyle.makeSidebarOverlay();

        RetroButton resumeBtn   = new RetroButton("RESUME",    RetroButton.Style.MENU, MenuStyle.ACCENT);
        RetroButton settingsBtn = new RetroButton("SETTINGS",  RetroButton.Style.MENU, MenuStyle.ACCENT);
        RetroButton mainMenuBtn = new RetroButton("MAIN MENU", RetroButton.Style.MENU, MenuStyle.ACCENT_DIM);
        RetroButton quitBtn     = new RetroButton("QUIT",      RetroButton.Style.MENU, MenuStyle.ACCENT_RED);

        MenuStyle.sizeButton(resumeBtn);
        MenuStyle.sizeButton(settingsBtn);
        MenuStyle.sizeButton(mainMenuBtn);
        MenuStyle.sizeButton(quitBtn);

        configureButtonSounds(resumeBtn,   "hover_menu", "select_menu");
        configureButtonSounds(settingsBtn, "hover_menu", "select_menu");
        configureButtonSounds(mainMenuBtn, "hover_menu", "select_menu");
        configureButtonSounds(quitBtn,     "hover_menu", "select_menu");

        resumeBtn.addActionListener(e   -> GameController.getInstance().resumeGame());
        settingsBtn.addActionListener(e -> GameController.getInstance().showSettings());
        mainMenuBtn.addActionListener(e -> GameController.getInstance().showMainMenu());
        quitBtn.addActionListener(e     -> System.exit(0));

        overlay.add(Box.createVerticalGlue());
        overlay.add(resumeBtn);
        overlay.add(settingsBtn);
        overlay.add(mainMenuBtn);
        overlay.add(quitBtn);
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