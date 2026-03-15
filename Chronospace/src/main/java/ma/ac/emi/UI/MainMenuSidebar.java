package ma.ac.emi.UI;

import javax.swing.*;
import java.awt.*;
import ma.ac.emi.UI.component.RetroButton;
import ma.ac.emi.gamecontrol.GameController;

/**
 * Right-column button set for the main menu.
 * No background — MenuHost owns it.
 */
public class MainMenuSidebar extends JPanel implements Soundable {

    private static final Color ACCENT_GREEN = new Color(80, 200, 100);

    public MainMenuSidebar(MenuHost host) {
        setOpaque(false);
        setLayout(new BorderLayout());

        // Gradient overlay so buttons stay readable over any background
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

        RetroButton startBtn    = new RetroButton("START GAME", RetroButton.Style.MENU, ACCENT_GREEN);
        RetroButton settingsBtn = new RetroButton("SETTINGS",   RetroButton.Style.MENU, ACCENT_GREEN);
        RetroButton quitBtn     = new RetroButton("QUIT",       RetroButton.Style.MENU, ACCENT_GREEN);

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

        // Navigate via host — background never re-renders
        startBtn.addActionListener(e    -> host.showDifficultyMenu());
        settingsBtn.addActionListener(e -> GameController.getInstance().showSettings());
        quitBtn.addActionListener(e     -> System.exit(0));

        overlay.add(Box.createVerticalGlue());
        overlay.add(startBtn);
        overlay.add(settingsBtn);
        overlay.add(quitBtn);
        overlay.add(Box.createVerticalStrut(80));

        add(overlay, BorderLayout.CENTER);
    }
}
