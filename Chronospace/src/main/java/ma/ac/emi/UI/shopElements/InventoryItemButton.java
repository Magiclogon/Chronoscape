package ma.ac.emi.UI.shopElements;

import java.awt.Color;
import java.awt.Font;
import java.awt.datatransfer.*;
// import jakarta.activation.DataHandler; // <-- REMOVED this problematic import
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import ma.ac.emi.UI.ShopUI;
import ma.ac.emi.gamelogic.shop.ShopItem;

public class InventoryItemButton extends JButton {
    private static final long serialVersionUID = 1L;
    private final ShopItem item;
    private final ShopUI shopUI;

    public InventoryItemButton(ShopUI shopUI, ShopItem item) {
        this.shopUI = shopUI;
        this.item = item;

        setPreferredSize(shopUI.inventoryButtonSize);
        setFocusPainted(false);
        setBackground(new Color(80, 80, 80));
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.PLAIN, 12));

        if (item == null) return;

        setText("<html><b>" + item.getItemDefinition().getName() + "</b></html>");
        addActionListener(e -> shopUI.showItemDetails(item));

        // Enable drag
        setTransferHandler(new ValueExportTransferHandler(item));
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                JComponent c = (JComponent) evt.getSource();
                c.getTransferHandler().exportAsDrag(c, evt, TransferHandler.MOVE);
            }
        });
    }

    public ShopItem getItem() {
        return item;
    }

    // Transfer handler to wrap the item
    private static class ValueExportTransferHandler extends TransferHandler {
        private final ShopItem item;

        // --- THIS IS THE FIX ---
        // We define the *exact same* DataFlavor as in InventoryScrollable.java
        // This ensures the drag source and drop target speak the same language.
        private static DataFlavor shopItemFlavor;
        static {
            try {
                shopItemFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        public ValueExportTransferHandler(ShopItem item) {
            this.item = item;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            if (shopItemFlavor == null) {
                return null;
            }

            // Return a custom Transferable object that wraps our ShopItem
            return new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    // This is the only "type" of data we know how to provide
                    return new DataFlavor[]{shopItemFlavor};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    // We only support our specific ShopItem flavor
                    return flavor.equals(shopItemFlavor);
                }

                @Override
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                    // If the drop target asks for our flavor, give them the item
                    if (isDataFlavorSupported(flavor)) {
                        return item;
                    } else {
                        // Otherwise, we can't provide what they're asking for
                        throw new UnsupportedFlavorException(flavor);
                    }
                }
            };
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }
    }
}