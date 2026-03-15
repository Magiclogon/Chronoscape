package ma.ac.emi.UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

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
        setBackground(Color.BLACK);

        try {
            backgroundImage = ImageIO.read(
                    getClass().getResource("/assets/Menus/main_menu_image.png"));
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
        if (backgroundImage != null)
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
