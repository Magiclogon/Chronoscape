package ma.ac.emi.UI;

import ma.ac.emi.camera.Camera;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.Inventory;
import ma.ac.emi.gamelogic.shop.WeaponItem;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.tiles.TileManager;
import ma.ac.emi.world.World;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GameUIPanel extends JPanel{

	// Minimap Configuration
	private static final double MINIMAP_SCALE_PERCENT = 0.20; // Map takes 20% of screen width
	private static final int MINIMAP_PADDING = 20;

	public GameUIPanel() {
		this.setOpaque(false);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
		int textX = barX;
		int textY = barY + barHeight + moneyFontSize + 5;

		g2.setColor(Color.YELLOW);
		g2.drawString(moneyText, textX, textY);

		drawWeaponSlots(g2, inventory, player.getWeaponIndex(), panelWidth, panelHeight);

		drawMinimap(g2, player, panelWidth, panelHeight);
	}

	private void drawMinimap(Graphics2D g2, Player player, int panelWidth, int panelHeight) {
		try {
			// get cached map
			World currentWorld = GameController.getInstance().getWorldManager().getCurrentWorld();
			if (currentWorld == null) return;

			TileManager tileManager = currentWorld.getTileManager();
			BufferedImage mapImage = tileManager.getMapCache();
			if (mapImage == null) return;

			// minimap dims
			int mapW = mapImage.getWidth();
			int mapH = mapImage.getHeight();

			// Calculate scale to fit in the corner
			double desiredWidth = panelWidth * MINIMAP_SCALE_PERCENT;
			double scale = desiredWidth / mapW;

			int minimapW = (int) (mapW * scale);
			int minimapH = (int) (mapH * scale);

			int miniX = panelWidth - minimapW - MINIMAP_PADDING;
			int miniY = MINIMAP_PADDING;

			g2.setColor(new Color(0, 0, 0, 150));
			g2.fillRect(miniX - 2, miniY - 2, minimapW + 4, minimapH + 4);

			g2.setColor(new Color(200, 200, 200));
			g2.drawRect(miniX - 2, miniY - 2, minimapW + 4, minimapH + 4);

			// map from cached map
			g2.drawImage(mapImage, miniX, miniY, minimapW, minimapH, null);

			// player dot
			Vector3D playerPos = player.getPos();
			int pMiniX = miniX + (int) (playerPos.getX() * scale);
			int pMiniY = miniY + (int) (playerPos.getY() * scale);

			g2.setColor(Color.GREEN);
			g2.fillOval(pMiniX - 3, pMiniY - 3, 6, 6);


		} catch (Exception e) {
			// Fail silently or log to console to prevent UI crash
			System.err.println("Minimap error: " + e.getMessage());
		}
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