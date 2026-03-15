package ma.ac.emi.UI;

import javax.swing.*;
import java.awt.*;
import ma.ac.emi.UI.component.RetroButton;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.difficulty.EasyDifficultyStrategy;
import ma.ac.emi.gamelogic.difficulty.HardDifficultyStrategy;
import ma.ac.emi.gamelogic.difficulty.MediumDifficultyStrategy;

/**
 * Difficulty selection sidebar — same visual style as MainMenuSidebar.
 * Uses RetroButton with the gradient overlay, vertical glue, and 64px row height.
 * Selecting a difficulty goes straight to the game (no level selection step).
 */
public class DifficultyMenuSidebar extends JPanel implements Soundable {

    private static final Color ACCENT_GREEN = new Color(80, 200, 100);

    public DifficultyMenuSidebar(MenuHost host) {
        setOpaque(false);
        setLayout(new BorderLayout());

        // Same dark gradient overlay as MainMenuSidebar
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

        RetroButton easyBtn   = new RetroButton("EASY",   RetroButton.Style.MENU, ACCENT_GREEN);
        RetroButton mediumBtn = new RetroButton("MEDIUM", RetroButton.Style.MENU, ACCENT_GREEN);
        RetroButton hardBtn   = new RetroButton("HARD",   RetroButton.Style.MENU, ACCENT_GREEN);
        RetroButton anwarBtn  = new RetroButton("ANWAR",  RetroButton.Style.MENU, ACCENT_GREEN);
        RetroButton backBtn   = new RetroButton("BACK",   RetroButton.Style.MENU, ACCENT_GREEN);

        Dimension rowSize = new Dimension(Integer.MAX_VALUE, 64);
        easyBtn.setMaximumSize(rowSize);   easyBtn.setPreferredSize(new Dimension(0, 64));
        mediumBtn.setMaximumSize(rowSize); mediumBtn.setPreferredSize(new Dimension(0, 64));
        hardBtn.setMaximumSize(rowSize);   hardBtn.setPreferredSize(new Dimension(0, 64));
        anwarBtn.setMaximumSize(rowSize);  anwarBtn.setPreferredSize(new Dimension(0, 64));
        backBtn.setMaximumSize(rowSize);   backBtn.setPreferredSize(new Dimension(0, 64));

        easyBtn.setHorizontalAlignment(SwingConstants.LEFT);
        mediumBtn.setHorizontalAlignment(SwingConstants.LEFT);
        hardBtn.setHorizontalAlignment(SwingConstants.LEFT);
        anwarBtn.setHorizontalAlignment(SwingConstants.LEFT);
        backBtn.setHorizontalAlignment(SwingConstants.LEFT);

        configureButtonSounds(easyBtn,   "hover_menu", "select_menu");
        configureButtonSounds(mediumBtn, "hover_menu", "select_menu");
        configureButtonSounds(hardBtn,   "hover_menu", "select_menu");
        configureButtonSounds(anwarBtn,  "hover_menu", "select_menu");
        configureButtonSounds(backBtn,   "hover_menu", "select_menu");

        easyBtn.addActionListener(e -> {
            GameController.getInstance().setDifficulty(new EasyDifficultyStrategy());
            GameController.getInstance().restartGameWithTransition();
        });
        mediumBtn.addActionListener(e -> {
            GameController.getInstance().setDifficulty(new MediumDifficultyStrategy());
            GameController.getInstance().restartGameWithTransition();
        });
        hardBtn.addActionListener(e -> {
            GameController.getInstance().setDifficulty(new HardDifficultyStrategy());
            GameController.getInstance().restartGameWithTransition();
        });
        anwarBtn.addActionListener(e -> {
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
        overlay.add(Box.createVerticalStrut(80));

        add(overlay, BorderLayout.CENTER);
    }
}