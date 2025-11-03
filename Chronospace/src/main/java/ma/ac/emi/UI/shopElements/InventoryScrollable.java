package ma.ac.emi.UI.shopElements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import ma.ac.emi.UI.ShopUI;


public class InventoryScrollable extends JScrollPane{
	private JPanel gridPanel;
	private ShopUI shopUI;
	
	public InventoryScrollable(ShopUI shopUI, String title) {
		this.shopUI = shopUI;
		gridPanel = new JPanel(new GridLayout(0, 1, 10, 10));
		gridPanel.setBackground(new Color(60, 60, 60));
		setBackground(new Color(60, 60, 60));
		Dimension preferredItemSize = shopUI.inventoryButtonSize;
		
		setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        title,
                        TitledBorder.LEADING,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14),
                        Color.WHITE
                ));
		
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel outerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        outerPanel.setBackground(new Color(60, 60, 60));
        outerPanel.add(gridPanel);

        setViewportView(outerPanel);

        
		getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getViewport().getWidth();
                int columns = Math.max(1, width / (preferredItemSize.width + 10));
                
                ((GridLayout) gridPanel.getLayout()).setColumns(columns);
                gridPanel.revalidate();
            }
        });
	}
	
	public void add(InventoryItemButton item) {
		this.gridPanel.add(item);
		this.gridPanel.revalidate();
		this.gridPanel.repaint();
	}
	
	public JPanel getPanel() {
		return this.gridPanel;
	}
}
