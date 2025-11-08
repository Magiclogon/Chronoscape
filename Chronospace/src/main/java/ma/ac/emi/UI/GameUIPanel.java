package ma.ac.emi.UI;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.Inventory;
import ma.ac.emi.gamelogic.shop.WeaponItem;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.world.World;

import java.awt.*;

import javax.swing.JPanel;

public class GameUIPanel extends JPanel{
	
	public GameUIPanel() {
		this.setOpaque(false);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		Player player = Player.getInstance();
		Inventory inventory = player.getInventory();

		int panelWidth = getWidth();
		int panelHeight = getHeight();

		// HP BAR
		int barWidth = (int) (0.25 * panelWidth);
		int barHeight = (int) (0.035 * panelHeight);
		int barX = (int) (0.03 * panelWidth);
		int barY = (int) (0.05 * panelHeight);

		double playerHP = player.getHp();
		double playerMaxHP = player.getHpMax();
		float hpPercent = (float) Math.max(0, (float) playerHP / playerMaxHP);

		g2.setColor(Color.GRAY);
		g2.fillRect(barX, barY, barWidth, barHeight);

		// HP Filler
		g2.setColor(Color.RED);
		g2.fillRect(barX, barY, (int) (barWidth * hpPercent), barHeight);

		g2.setColor(Color.BLACK);
		g2.drawRect(barX, barY, barWidth, barHeight);

		// Money
		String moneyText = "Money: " + player.getMoney() + " $";
		int moneyFontSize = (int) (panelHeight * 0.035);
		g2.setFont(new Font("Arial", Font.BOLD, moneyFontSize));

		FontMetrics fm = g2.getFontMetrics();
		int textWidth = fm.stringWidth(moneyText);
		int textX = panelWidth - textWidth - (int) (panelWidth * 0.03);
		int textY = (int) (panelHeight * 0.08);

		g2.setColor(Color.YELLOW);
		g2.drawString(moneyText, textX, textY);

		drawWeaponSlots(g2, inventory, player.getWeaponIndex(),panelWidth, panelHeight);
	}

	private void drawWeaponSlots(Graphics2D g2, Inventory inventory, int activeWeaponIndex,int panelWidth, int panelHeight) {
		int slotSize = (int) (panelHeight * 0.08);
		int slotSpacing = (int) (panelHeight * 0.015);
		int startX = (int) (panelWidth * 0.03);
		int startY = panelHeight - slotSize - (int) (panelHeight * 0.05);

		WeaponItem[] equippedWeapons = inventory.getEquippedWeapons();

		for (int i = 0; i < Inventory.MAX_EQU; i++) {
			int slotX = startX + i * (slotSize + slotSpacing);
			int slotY = startY;

			boolean isActive = (i == activeWeaponIndex && equippedWeapons[i] != null);

			// Draw slot background
			if (isActive) {
				g2.setColor(new Color(80, 120, 80)); // Green tint for active weapon
			} else if (equippedWeapons[i] != null) {
				g2.setColor(new Color(60, 60, 60));
			} else {
				g2.setColor(new Color(40, 40, 40));
			}
			g2.fillRect(slotX, slotY, slotSize, slotSize);

			// Draw slot border (highlight active weapon)
			if (isActive) {
				g2.setColor(new Color(0, 255, 0)); // Bright green border
				g2.setStroke(new BasicStroke(4));
			} else {
				g2.setColor(Color.WHITE);
				g2.setStroke(new BasicStroke(2));
			}
			g2.drawRect(slotX, slotY, slotSize, slotSize);

			// Draw slot number
			g2.setFont(new Font("Arial", Font.BOLD, (int) (slotSize * 0.25)));
			g2.setColor(Color.LIGHT_GRAY);
			String slotNum = String.valueOf(i + 1);
			FontMetrics fm = g2.getFontMetrics();
			int numX = slotX + (slotSize - fm.stringWidth(slotNum)) / 2;
			int numY = slotY + fm.getAscent() + 5;
			g2.drawString(slotNum, numX, numY);

			// Draw weapon name if equipped
			if (equippedWeapons[i] != null) {
				WeaponItemDefinition weaponDef = (WeaponItemDefinition) equippedWeapons[i].getItemDefinition();
				String weaponName = weaponDef.getName();

				// Draw weapon icon/indicator
				g2.setColor(new Color(255, 165, 0));
				int iconSize = (int) (slotSize * 0.4);
				int iconX = slotX + (slotSize - iconSize) / 2;
				int iconY = slotY + (int) (slotSize * 0.35);
				g2.fillOval(iconX, iconY, iconSize, iconSize);

				// Draw weapon name below slot
				g2.setFont(new Font("Arial", Font.PLAIN, (int) (slotSize * 0.18)));
				g2.setColor(Color.WHITE);
				fm = g2.getFontMetrics();

				// Truncate name if too long
				String displayName = weaponName;
				if (fm.stringWidth(displayName) > slotSize + slotSpacing) {
					while (fm.stringWidth(displayName + "...") > slotSize + slotSpacing && displayName.length() > 0) {
						displayName = displayName.substring(0, displayName.length() - 1);
					}
					displayName += "...";
				}

				int nameX = slotX + (slotSize - fm.stringWidth(displayName)) / 2;
				int nameY = slotY + slotSize + fm.getAscent() + 5;
				g2.drawString(displayName, nameX, nameY);
			} else {
				// Draw "Empty" text
				g2.setFont(new Font("Arial", Font.ITALIC, (int) (slotSize * 0.18)));
				g2.setColor(Color.GRAY);
				fm = g2.getFontMetrics();
				String emptyText = "Empty";
				int emptyX = slotX + (slotSize - fm.stringWidth(emptyText)) / 2;
				int emptyY = slotY + slotSize + fm.getAscent() + 5;
				g2.drawString(emptyText, emptyX, emptyY);
			}
		}
	}
}
