package ma.ac.emi.UI;

import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.sound.SoundManager;

import javax.swing.*;

public interface Soundable {

    private void playSound(String name) {
        SoundManager manager = GameController.getInstance().getSoundManager();
        manager.play(name);
    }

    default void configureButtonSounds(JButton button, String hoverSoundName,String selectSoundName) {

        if(hoverSoundName != null) {
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    playSound(hoverSoundName);
                }
            });
        }

        if(selectSoundName != null) {
            button.addActionListener(e -> playSound(selectSoundName));
        }
    }
}
