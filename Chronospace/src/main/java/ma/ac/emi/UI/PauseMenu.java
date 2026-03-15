package ma.ac.emi.UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import ma.ac.emi.UI.component.RetroButton;
import ma.ac.emi.gamecontrol.GameController;

public class PauseMenu extends JPanel implements Soundable {

    private static final long serialVersionUID = 1L;
    private static final Color ACCENT_GREEN = new Color(80, 200, 100);

    private Image backgroundImage;

    public PauseMenu() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/assets/Menus/pause_image.png"));
        } catch (Exception e) {
            System.err.println("Error loading pause background: " + e.getMessage());
        }

        setOpaque(false);
        setLayout(new BorderLayout());

        // ── Right column with gradient overlay — mirrors menu sidebar ──────
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

        RetroButton resumeBtn   = new RetroButton("RESUME",    RetroButton.Style.MENU, ACCENT_GREEN);
        RetroButton settingsBtn = new RetroButton("SETTINGS",  RetroButton.Style.MENU, ACCENT_GREEN);
        RetroButton mainMenuBtn = new RetroButton("MAIN MENU", RetroButton.Style.MENU, ACCENT_GREEN);
        RetroButton quitBtn     = new RetroButton("QUIT",      RetroButton.Style.MENU, ACCENT_GREEN);

        Dimension rowSize = new Dimension(Integer.MAX_VALUE, 64);
        resumeBtn.setMaximumSize(rowSize);   resumeBtn.setPreferredSize(new Dimension(0, 64));
        settingsBtn.setMaximumSize(rowSize); settingsBtn.setPreferredSize(new Dimension(0, 64));
        mainMenuBtn.setMaximumSize(rowSize); mainMenuBtn.setPreferredSize(new Dimension(0, 64));
        quitBtn.setMaximumSize(rowSize);     quitBtn.setPreferredSize(new Dimension(0, 64));

        resumeBtn.setHorizontalAlignment(SwingConstants.LEFT);
        settingsBtn.setHorizontalAlignment(SwingConstants.LEFT);
        mainMenuBtn.setHorizontalAlignment(SwingConstants.LEFT);
        quitBtn.setHorizontalAlignment(SwingConstants.LEFT);

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
        overlay.add(Box.createVerticalStrut(80));

        // Same 3-column split as MenuHost — buttons in rightmost third
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