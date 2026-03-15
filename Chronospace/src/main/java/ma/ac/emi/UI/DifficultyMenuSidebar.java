package ma.ac.emi.UI;

import javax.swing.*;
import java.awt.*;
import ma.ac.emi.UI.component.RetroButton;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.difficulty.EasyDifficultyStrategy;
import ma.ac.emi.gamelogic.difficulty.HardDifficultyStrategy;
import ma.ac.emi.gamelogic.difficulty.MediumDifficultyStrategy;

public class DifficultyMenuSidebar extends JPanel implements Soundable {

    // Prevents multiple rapid clicks from firing multiple transitions
    private boolean transitioning = false;

    @Override
    public void setVisible(boolean visible) {
        if (visible) transitioning = false;
        super.setVisible(visible);
    }

    public DifficultyMenuSidebar(MenuHost host) {
        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel overlay = (JPanel) MenuStyle.makeSidebarOverlay();

        RetroButton easyBtn   = new RetroButton("EASY",   RetroButton.Style.MENU, MenuStyle.ACCENT);
        RetroButton mediumBtn = new RetroButton("MEDIUM", RetroButton.Style.MENU, MenuStyle.ACCENT);
        RetroButton hardBtn   = new RetroButton("HARD",   RetroButton.Style.MENU, MenuStyle.ACCENT);
        RetroButton anwarBtn  = new RetroButton("ANWAR",  RetroButton.Style.MENU, MenuStyle.ACCENT);
        RetroButton backBtn   = new RetroButton("BACK",   RetroButton.Style.MENU, MenuStyle.ACCENT_DIM);

        MenuStyle.sizeButton(easyBtn);
        MenuStyle.sizeButton(mediumBtn);
        MenuStyle.sizeButton(hardBtn);
        MenuStyle.sizeButton(anwarBtn);
        MenuStyle.sizeButton(backBtn);

        configureButtonSounds(easyBtn,   "hover_menu", "select_menu");
        configureButtonSounds(mediumBtn, "hover_menu", "select_menu");
        configureButtonSounds(hardBtn,   "hover_menu", "select_menu");
        configureButtonSounds(anwarBtn,  "hover_menu", "select_menu");
        configureButtonSounds(backBtn,   "hover_menu", "select_menu");

        easyBtn.addActionListener(e -> {
            if (transitioning) return;
            transitioning = true;
            GameController.getInstance().setDifficulty(new EasyDifficultyStrategy());
            GameController.getInstance().restartGameWithTransition();
        });
        mediumBtn.addActionListener(e -> {
            if (transitioning) return;
            transitioning = true;
            GameController.getInstance().setDifficulty(new MediumDifficultyStrategy());
            GameController.getInstance().restartGameWithTransition();
        });
        hardBtn.addActionListener(e -> {
            if (transitioning) return;
            transitioning = true;
            GameController.getInstance().setDifficulty(new HardDifficultyStrategy());
            GameController.getInstance().restartGameWithTransition();
        });
        anwarBtn.addActionListener(e -> {
            if (transitioning) return;
            transitioning = true;
            GameController.getInstance().setDifficulty(new HardDifficultyStrategy());
            GameController.getInstance().restartGameWithTransition();
        });
        backBtn.addActionListener(e -> host.showMainMenu());

        overlay.add(Box.createVerticalGlue());
        overlay.add(easyBtn);
        overlay.add(mediumBtn);
        overlay.add(hardBtn);
        overlay.add(anwarBtn);
        overlay.add(backBtn);
        overlay.add(Box.createVerticalStrut(MenuStyle.BOTTOM_STRUT));

        add(overlay, BorderLayout.CENTER);
    }
}