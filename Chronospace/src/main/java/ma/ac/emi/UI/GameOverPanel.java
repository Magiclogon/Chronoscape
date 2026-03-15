package ma.ac.emi.UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import ma.ac.emi.UI.component.RetroButton;
import ma.ac.emi.gamecontrol.GameController;

public class GameOverPanel extends JPanel implements Soundable {

    private static final long serialVersionUID = 1L;
    private static final Color ACCENT_GREEN = new Color(80, 200, 100);

    private Image backgroundImage;

    public GameOverPanel() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/assets/Menus/game_over_image.png"));
        } catch (Exception e) {
            System.err.println("Error loading game over background: " + e.getMessage());
        }

        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(0, 0, 0, 0),
                        getWidth(), 0, new Color(0, 0, 0, 160)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        overlay.setOpaque(false);
        overlay.setLayout(new BoxLayout(overlay, BoxLayout.Y_AXIS));

        RetroButton tryAgainBtn  = new RetroButton("TRY AGAIN",  RetroButton.Style.MENU, ACCENT_GREEN);
        RetroButton mainMenuBtn  = new RetroButton("MAIN MENU",  RetroButton.Style.MENU, ACCENT_GREEN);

        Dimension rowSize = new Dimension(Integer.MAX_VALUE, 64);
        tryAgainBtn.setMaximumSize(rowSize);  tryAgainBtn.setPreferredSize(new Dimension(0, 64));
        mainMenuBtn.setMaximumSize(rowSize);  mainMenuBtn.setPreferredSize(new Dimension(0, 64));

        tryAgainBtn.setHorizontalAlignment(SwingConstants.LEFT);
        mainMenuBtn.setHorizontalAlignment(SwingConstants.LEFT);

        configureButtonSounds(tryAgainBtn, "hover_menu", "select_menu");
        configureButtonSounds(mainMenuBtn, "hover_menu", "select_menu");

        tryAgainBtn.addActionListener(e -> GameController.getInstance().restartGameWithTransition());
        mainMenuBtn.addActionListener(e -> GameController.getInstance().showMainMenu());

        overlay.add(Box.createVerticalGlue());
        overlay.add(tryAgainBtn);
        overlay.add(mainMenuBtn);
        overlay.add(Box.createVerticalStrut(80));

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