package ma.ac.emi.UI;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import ma.ac.emi.UI.component.RetroButton;
import ma.ac.emi.gamecontrol.GameController;

public class PauseMenu extends JPanel implements Soundable {
    private static final long serialVersionUID = 1L;

    private static final String[] IMAGE_PATHS = {
        "/assets/Menus/pause_art1.png",
        "/assets/Menus/pause_art2.png",
        "/assets/Menus/pause_art3.png"
    };

    private final List<Image> backgrounds = new ArrayList<>();
    private final Random random = new Random();
    private Image currentBackground;

    public PauseMenu() {
        // Load all available background images at construction time
        for (String path : IMAGE_PATHS) {
            try {
                Image img = ImageIO.read(getClass().getResource(path));
                if (img != null) backgrounds.add(img);
            } catch (Exception e) {
                System.err.println("Could not load pause background: " + path);
            }
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

    /** Call this whenever the pause menu is shown to pick a new random background. */
    public void randomizeBackground() {
        if (!backgrounds.isEmpty())
            currentBackground = backgrounds.get(random.nextInt(backgrounds.size()));
    }
    
    @Override
    public void setVisible(boolean visible) {
        if (visible) randomizeBackground();
        System.out.println(visible);
        super.setVisible(visible);
    }

    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
        if (currentBackground == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int imgWidth = currentBackground.getWidth(this);
        int imgHeight = currentBackground.getHeight(this);

        // Calculate the scale to "Cover" the panel
        double scale = Math.max((double) panelWidth / imgWidth, (double) panelHeight / imgHeight);

        int newWidth = (int) (imgWidth * scale);
        int newHeight = (int) (imgHeight * scale);

        // Center the image
        int x = (panelWidth - newWidth) / 2;
        int y = (panelHeight - newHeight) / 2;

        g2d.drawImage(currentBackground, x, y, newWidth, newHeight, this);
    
    }
}