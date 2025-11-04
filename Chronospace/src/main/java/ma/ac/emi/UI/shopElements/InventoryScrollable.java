package ma.ac.emi.UI.shopElements;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import ma.ac.emi.UI.ShopUI;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.ShopItem;
import ma.ac.emi.gamelogic.shop.WeaponItem;

public class InventoryScrollable extends JScrollPane {
	private JPanel gridPanel;
	private ShopUI shopUI;
	private String title;

	public InventoryScrollable(ShopUI shopUI, String title) {
		this.shopUI = shopUI;
		this.title = title;

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
				)
		);

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

		// Enable drop support - try BOTH the gridPanel AND this scrollpane
		DropHandler handler = new DropHandler();
		gridPanel.setTransferHandler(handler);
		setTransferHandler(handler);

		// Also set up drop target explicitly
		new DropTarget(gridPanel, DnDConstants.ACTION_MOVE, new DropTargetAdapter() {
			@Override
			public void drop(DropTargetDropEvent evt) {
				System.out.println("DropTarget: Drop detected on panel: " + title);
				try {
					evt.acceptDrop(DnDConstants.ACTION_MOVE);
					Transferable t = evt.getTransferable();
					DataFlavor flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);

					if (t.isDataFlavorSupported(flavor)) {
						ShopItem item = (ShopItem) t.getTransferData(flavor);
						System.out.println("Item received: " + item.getItemDefinition().getName());
						handleDrop(item);
						evt.dropComplete(true);
					} else {
						evt.dropComplete(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
					evt.dropComplete(false);
				}
			}
		});
	}

	public void add(InventoryItemButton item) {
		gridPanel.add(item);
		gridPanel.revalidate();
		gridPanel.repaint();
	}

	public JPanel getPanel() {
		return gridPanel;
	}

	private void handleDrop(ShopItem item) {
		System.out.println("handleDrop called for: " + item.getItemDefinition().getName() + " on panel: " + title);

		if (!(item instanceof WeaponItem)) {
			System.out.println("Not a weapon, ignoring");
			return;
		}

		Player player = Player.getInstance();
		WeaponItem weaponToEquip = (WeaponItem) item;

		if (title.equals("Active Weapons")) {
			System.out.println("Equipping weapon...");

			// Check if already equipped
			Integer currentSlot = player.isWeaponEquipped(weaponToEquip);
			if (currentSlot != null) {
				System.out.println("Already equipped in slot: " + currentSlot);
				shopUI.refresh();
				return;
			}

			// Find first empty slot
			int maxSlots = 3;
			WeaponItem[] currentlyEquipped = player.getInventory().getEquippedWeapons();

			System.out.println("Currently equipped weapons: " + currentlyEquipped.length);

			int noneNullWeaponItems = 0;
			for(WeaponItem w: currentlyEquipped) {
				if(w == null) continue;
				noneNullWeaponItems++;
			}


			if (noneNullWeaponItems >= maxSlots) {
				System.out.println("All slots full!");
				return;
			}

			boolean[] slotTaken = new boolean[maxSlots];
			for (WeaponItem equippedItem : currentlyEquipped) {
				Integer slot = player.isWeaponEquipped(equippedItem);
				if (slot != null && slot >= 0 && slot < maxSlots) {
					slotTaken[slot] = true;
					System.out.println("Slot " + slot + " is taken");
				}
			}

			int emptySlot = -1;
			for (int i = 0; i < maxSlots; i++) {
				if (!slotTaken[i]) {
					emptySlot = i;
					break;
				}
			}

			if (emptySlot == -1) {
				System.out.println("No empty slot found!");
				return;
			}

			System.out.println("Equipping to slot: " + emptySlot);
			player.getInventory().equipWeapon(weaponToEquip, emptySlot);

		} else if (title.equals("Weapons")) {
			System.out.println("Unequipping weapon...");
			player.getInventory().unequipWeapon(weaponToEquip);
		}

		shopUI.refresh();
	}

	// --- DROP HANDLER LOGIC ---
	private class DropHandler extends TransferHandler {

		private static DataFlavor shopItemFlavor;
		static {
			try {
				shopItemFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean canImport(TransferSupport support) {
			System.out.println("TransferHandler: canImport called on " + title);
			if (shopItemFlavor == null) return false;
			boolean result = support.isDataFlavorSupported(shopItemFlavor);
			System.out.println("Can import: " + result);
			return result;
		}

		@Override
		public boolean importData(TransferSupport support) {
			System.out.println("TransferHandler: importData called on " + title);

			if (!canImport(support)) {
				System.out.println("Cannot import!");
				return false;
			}

			try {
				ShopItem item = (ShopItem) support.getTransferable()
						.getTransferData(shopItemFlavor);

				System.out.println("Item extracted: " + item.getItemDefinition().getName());
				handleDrop(item);
				return true;

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
}