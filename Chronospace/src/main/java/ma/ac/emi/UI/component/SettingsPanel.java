package ma.ac.emi.UI.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import ma.ac.emi.UI.MenuStyle;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.sound.SoundManager;

public abstract class SettingsPanel extends JPanel {
    protected RetroButton applyButton;
    protected boolean hasUnsavedChanges = false;

    public abstract void applyChanges();
    public abstract void resetToDefaults();

    public void setApplyButton(RetroButton btn) {
        this.applyButton = btn;
    }

    /* ── Change tracking ─────────────────────────────────────────────── */

    protected void markChanged() {
        hasUnsavedChanges = true;
        if (applyButton != null) {
            applyButton.setEnabled(true);
        }
    }

    protected void clearChanged() {
        hasUnsavedChanges = false;
        if (applyButton != null) {
            applyButton.setEnabled(false);
        }
    }

    public boolean isHasUnsavedChanges() {
        return hasUnsavedChanges;
    }

    /* ── Sound helpers ───────────────────────────────────────────────── */

    protected void playUiSound(String name) {
        SoundManager sm = GameController.getInstance().getSoundManager();
        if (sm != null) sm.play(name);
    }

    protected void addHoverSound(JButton btn) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                playUiSound("hover_menu");
            }
        });
    }

    /* ── Common UI helpers ───────────────────────────────────────────── */

    protected JLabel makeSectionLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(MenuStyle.FONT_BODY);
        lbl.setForeground(MenuStyle.TEXT_BORDER);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    protected JPanel makeSeparator() {
        JPanel p = new JPanel();
        p.setBackground(new Color(60, 60, 70));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.setPreferredSize(new Dimension(0, 1));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        return p;
    }
}