package ma.ac.emi.UI;

import javax.swing.*;
import java.awt.*;
import ma.ac.emi.UI.component.RetroButton;
import ma.ac.emi.gamecontrol.GameController;

public class MainMenuSidebar extends JPanel implements Soundable {

    public MainMenuSidebar(MenuHost host) {
        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel overlay = (JPanel) MenuStyle.makeSidebarOverlay();

        RetroButton startBtn    = new RetroButton("START GAME", RetroButton.Style.MENU, MenuStyle.ACCENT);
        RetroButton settingsBtn = new RetroButton("SETTINGS",   RetroButton.Style.MENU, MenuStyle.ACCENT);
        RetroButton quitBtn     = new RetroButton("QUIT",       RetroButton.Style.MENU, MenuStyle.ACCENT_RED);

        MenuStyle.sizeButton(startBtn);
        MenuStyle.sizeButton(settingsBtn);
        MenuStyle.sizeButton(quitBtn);

        configureButtonSounds(startBtn,    "hover_menu", "select_menu");
        configureButtonSounds(settingsBtn, "hover_menu", "select_menu");
        configureButtonSounds(quitBtn,     "hover_menu", "select_menu");

        startBtn.addActionListener(e    -> host.showDifficultyMenu());
        settingsBtn.addActionListener(e -> GameController.getInstance().showSettings());
        quitBtn.addActionListener(e     -> System.exit(0));

        overlay.add(Box.createVerticalGlue());
        overlay.add(startBtn);
        overlay.add(settingsBtn);
        overlay.add(quitBtn);
        overlay.add(Box.createVerticalStrut(MenuStyle.BOTTOM_STRUT));

        add(overlay, BorderLayout.CENTER);
    }
}