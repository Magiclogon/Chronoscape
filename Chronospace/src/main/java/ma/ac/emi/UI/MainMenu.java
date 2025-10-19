package ma.ac.emi.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MainMenu extends JPanel{
	private JLabel title;
	private JButton startButton, quitButton;
	
	public MainMenu() {
		title = new JLabel();
		startButton = new JButton("Start");
		quitButton = new JButton("Quit");
		quitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
			
		});
		
		this.setBackground(Color.BLACK);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		title.setText("Chronospace");
		title.setForeground(Color.RED);
		title.setFont(new Font("Arial", Font.BOLD, 28));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.add(title);
		this.add(new Box.Filler(new Dimension(0, 20), new Dimension(0, 100), new Dimension(0, 200)));
		this.add(startButton);
		this.add(new Box.Filler(new Dimension(0, 5), new Dimension(0, 20), new Dimension(0, 20)));
		this.add(quitButton);
	}
	
	
}
