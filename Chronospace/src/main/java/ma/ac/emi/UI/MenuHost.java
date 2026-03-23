package ma.ac.emi.UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Persistent host panel that lives permanently as the "MENU_HOST" card in Window.
 * It owns the background image and swaps only the right-column sidebar via CardLayout.
 * The background never flickers or reloads during menu navigation.
 */
public class MenuHost extends JPanel {

    public static final String CARD_MAIN       = "MAIN";
    public static final String CARD_DIFFICULTY = "DIFFICULTY";

    private Image backgroundImage;

    private final CardLayout cardLayout;
    private final JPanel     sidebarContainer;

    public MenuHost() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        try {
            backgroundImage = ImageIO.read(
                    getClass().getResource("/assets/Menus/main_menu_art.png"));
        } catch (Exception e) {
            System.err.println("MenuHost: could not load background image.");
        }

        // ── Sidebar card switcher (right 1/3) ─────────────────────────────
        cardLayout       = new CardLayout();
        sidebarContainer = new JPanel(cardLayout);
        sidebarContainer.setOpaque(false);

        sidebarContainer.add(new MainMenuSidebar(this),       CARD_MAIN);
        sidebarContainer.add(new DifficultyMenuSidebar(this), CARD_DIFFICULTY);

        // ── 3-column split: left spacer | middle spacer | sidebar ──────────
        JPanel split = new JPanel(new GridLayout(1, 3, 0, 0));
        split.setOpaque(false);
        split.add(new JPanel() {{ setOpaque(false); }});
        split.add(new JPanel() {{ setOpaque(false); }});
        split.add(sidebarContainer);

        add(split, BorderLayout.CENTER);

        showMainMenu();
    }

    // ── Navigation ────────────────────────────────────────────────────────

    public void showMainMenu() {
        cardLayout.show(sidebarContainer, CARD_MAIN);
    }

    public void showDifficultyMenu() {
        cardLayout.show(sidebarContainer, CARD_DIFFICULTY);
    }

    // ── Background paint — persists under every sidebar swap ──────────────

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
