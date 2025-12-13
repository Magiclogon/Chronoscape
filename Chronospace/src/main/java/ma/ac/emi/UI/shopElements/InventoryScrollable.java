package ma.ac.emi.UI.shopElements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import ma.ac.emi.UI.ShopUI;

public class InventoryScrollable extends JScrollPane {
	private JPanel gridPanel;
	private ShopUI shopUI;
	private Dimension preferredItemSize;

	public InventoryScrollable(ShopUI shopUI, String title) {
		this.shopUI = shopUI;
		// Cache the size reference
		this.preferredItemSize = shopUI.inventoryButtonSize;

		// Initialize the internal grid
		gridPanel = new JPanel(new GridLayout(0, 1, 10, 10));
		gridPanel.setBackground(new Color(60, 60, 60));

		setBackground(new Color(60, 60, 60));
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.GRAY),
				title,
				TitledBorder.LEADING,
				TitledBorder.TOP,
				new Font("Arial", Font.BOLD, 14),
				Color.WHITE
		));

		// Scroll settings
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling

		// Outer panel to keep items aligned to top-left rather than centered if few items exist
		JPanel outerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		outerPanel.setBackground(new Color(60, 60, 60));
		outerPanel.add(gridPanel);

		setViewportView(outerPanel);

		// Dynamic Resizing Logic
		getViewport().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				recalculateLayout();
			}
		});
	}

	/**
	 * Clears the current list and adds a new list of buttons.
	 * Useful when refreshing the shop/inventory from the Model.
	 */
	public void updateItems(List<InventoryItemButton> buttons) {
		gridPanel.removeAll();
		for (InventoryItemButton btn : buttons) {
			gridPanel.add(btn);
		}
		recalculateLayout(); // Ensure columns are correct for new count/width
		gridPanel.revalidate();
		gridPanel.repaint();
	}

	/**
	 * Adds a single item to the view.
	 */
	public void add(InventoryItemButton item) {
		this.gridPanel.add(item);
		this.gridPanel.revalidate();
		this.gridPanel.repaint();
	}

	/**
	 * Clears all items from the view.
	 */
	public void clear() {
		this.gridPanel.removeAll();
		this.gridPanel.revalidate();
		this.gridPanel.repaint();
	}

	/**
	 * Helper to calculate how many columns fit in the current width
	 */
	private void recalculateLayout() {
		int width = getViewport().getWidth();
		// Prevent division by zero or negative width issues
		if (width > 0 && preferredItemSize.width > 0) {
			int columns = Math.max(1, width / (preferredItemSize.width + 10));
			((GridLayout) gridPanel.getLayout()).setColumns(columns);
			gridPanel.revalidate();
		}
	}

	public JPanel getPanel() {
		return this.gridPanel;
	}
}