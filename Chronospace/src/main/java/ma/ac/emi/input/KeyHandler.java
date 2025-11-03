package ma.ac.emi.input;

import java.awt.event.ActionEvent;

import javax.swing.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyHandler{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean left, right, up, down, switchWeapon;
	private static KeyHandler instance;
	
	private KeyHandler() {
		init();
	}
	
	public static KeyHandler getInstance() {
		if(instance == null) {
			instance = new KeyHandler();
		}
		return instance;
	}

    public void setupKeyBindings(JComponent component) {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();

        // LEFT arrow pressed
        inputMap.put(KeyStroke.getKeyStroke("pressed A"), "A Pressed");
        actionMap.put("A Pressed", new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
                setLeft(true);
            }
        });

        // LEFT arrow released
        inputMap.put(KeyStroke.getKeyStroke("released A"), "A Released");
        actionMap.put("A Released", new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
                setLeft(false);
            }
        });

        // RIGHT pressed
        inputMap.put(KeyStroke.getKeyStroke("pressed D"), "D Pressed");
        actionMap.put("D Pressed", new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
                setRight(true);
            }
        });

        // RIGHT released
        inputMap.put(KeyStroke.getKeyStroke("released D"), "D Released");
        actionMap.put("D Released", new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
                setRight(false);
            }
        });

        // UP pressed
        inputMap.put(KeyStroke.getKeyStroke("pressed W"), "W Pressed");
        actionMap.put("W Pressed", new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
                setUp(true);
            }
        });

        // UP released
        inputMap.put(KeyStroke.getKeyStroke("released W"), "W Released");
        actionMap.put("W Released", new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
            	setUp(false);
            }
        });

        // DOWN pressed
        inputMap.put(KeyStroke.getKeyStroke("pressed S"), "S Pressed");
        actionMap.put("S Pressed", new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
                setDown(true);
            }
        });

        // DOWN released
        inputMap.put(KeyStroke.getKeyStroke("released S"), "S Released");
        actionMap.put("S Released", new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
            	setDown(false);
            }
        });
        
        // E pressed - Switch Weapon (single action trigger)
        inputMap.put(KeyStroke.getKeyStroke("pressed E"), "E Pressed");
        actionMap.put("E Pressed", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
            public void actionPerformed(ActionEvent e) {
                setSwitchWeapon(true);
            }
        });
        

    }
    
    public boolean consumeSwitchWeapon() {
        if (switchWeapon) {
            switchWeapon = false;
            return true;
        }
        return false;
    }

	public void init() {
		setLeft(false);
		setRight(false);
		setUp(false);
		setDown(false);
	}

}
