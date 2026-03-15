package ma.ac.emi.UI;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import ma.ac.emi.UI.component.RetroButton;
import ma.ac.emi.gamecontrol.GameController;

public class MainMenu extends JPanel implements Soundable {
    private static final long serialVersionUID = 1L;

    private static final Color ACCENT_GREEN = new Color(80, 200, 100);

    private static final String FONT_NAME = "ByteBounce";

    private Image backgroundImage;

    public MainMenu() {
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/assets/Menus/main_menu_image.png"));
        } catch (Exception e) {
            System.err.println("Error loading background image.");
            e.printStackTrace();
        }

        // ── Right column: 1/3 of screen ───────────────────────────────────
        JPanel rightCol = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Dark gradient overlay so buttons are readable over any bg
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(0, 0, 0, 0),
                        getWidth(), 0, new Color(0, 0, 0, 160)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        rightCol.setOpaque(false);
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));

        RetroButton startBtn    = new RetroButton("START GAME", RetroButton.Style.MENU, ACCENT_GREEN);
        RetroButton settingsBtn = new RetroButton("SETTINGS",   RetroButton.Style.MENU, ACCENT_GREEN);
        RetroButton quitBtn     = new RetroButton("QUIT",       RetroButton.Style.MENU, ACCENT_GREEN);

        // Fixed height, full width
        Dimension rowSize = new Dimension(Integer.MAX_VALUE, 64);
        startBtn.setMaximumSize(rowSize);    startBtn.setPreferredSize(new Dimension(0, 64));
        settingsBtn.setMaximumSize(rowSize); settingsBtn.setPreferredSize(new Dimension(0, 64));
        quitBtn.setMaximumSize(rowSize);     quitBtn.setPreferredSize(new Dimension(0, 64));

        startBtn.setHorizontalAlignment(SwingConstants.LEFT);
        settingsBtn.setHorizontalAlignment(SwingConstants.LEFT);
        quitBtn.setHorizontalAlignment(SwingConstants.LEFT);

        configureButtonSounds(startBtn,    "hover_menu", "select_menu");
        configureButtonSounds(settingsBtn, "hover_menu", "select_menu");
        configureButtonSounds(quitBtn,     "hover_menu", "select_menu");

        startBtn.addActionListener(e    -> GameController.getInstance().showDifficultyMenu());
        settingsBtn.addActionListener(e -> GameController.getInstance().showSettings());
        quitBtn.addActionListener(e     -> System.exit(0));

        rightCol.add(Box.createVerticalGlue());
        rightCol.add(startBtn);
        rightCol.add(settingsBtn);
        rightCol.add(quitBtn);
        rightCol.add(Box.createVerticalStrut(80));

        // Split: 2 invisible cells + 1 menu column = rightmost 1/3
        JPanel split = new JPanel(new GridLayout(1, 3, 0, 0));
        split.setOpaque(false);
        split.add(new JPanel() {{ setOpaque(false); }});
        split.add(new JPanel() {{ setOpaque(false); }});
        split.add(rightCol);
        add(split, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        if (backgroundImage != null)
//            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}