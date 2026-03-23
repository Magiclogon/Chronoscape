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
            backgroundImage = ImageIO.read(getClass().getResource("/assets/Menus/gameover_art.png"));
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
        if (backgroundImage == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int imgWidth = backgroundImage.getWidth(this);
        int imgHeight = backgroundImage.getHeight(this);

        // Calculate the scale to "Cover" the panel
        double scale = Math.max((double) panelWidth / imgWidth, (double) panelHeight / imgHeight);

        int newWidth = (int) (imgWidth * scale);
        int newHeight = (int) (imgHeight * scale);

        // Center the image
        int x = (panelWidth - newWidth) / 2;
        int y = (panelHeight - newHeight) / 2;

        g2d.drawImage(backgroundImage, x, y, newWidth, newHeight, this);
    
    }
}