package ma.ac.emi.UI;

import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.entity.BossEnnemy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.Inventory;
import ma.ac.emi.gamelogic.shop.WeaponItem;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.tiles.TileManager;
import ma.ac.emi.world.World;

import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class GameUIPanel extends JPanel {

	private static final String FONT_NAME = "ByteBounce";
	private static final double MINIMAP_SCALE_PERCENT = 0.20;
	private static final int MINIMAP_PADDING = 20;

	private static final Color UI_BORDER_DARK = new Color(20, 20, 25);
	private static final Color UI_BORDER_LIGHT = new Color(200, 200, 210);
	private static final Color HP_RED_DARK = new Color(150, 0, 0);
	private static final Color HP_RED_LIGHT = new Color(230, 50, 50);
	private static final Color GOLD_COLOR = new Color(255, 215, 0);

	// Boss bar colors
	private static final Color BOSS_BAR_BG = new Color(20, 0, 0, 200);
	private static final Color BOSS_BAR_FILL = new Color(138, 3, 3);
	private static final Color BOSS_BAR_BORDER = new Color(0, 0, 0);
	private static final Color PHASE_MARKER_COLOR = new Color(0, 0, 0, 180);

	public GameUIPanel() {
		this.setOpaque(false);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

		Player player = Player.getInstance();
		Inventory inventory = player.getInventory();
		int panelWidth = getWidth();
		int panelHeight = getHeight();

		drawHPBar(g2, player, panelWidth, panelHeight);
		drawMoney(g2, player, panelWidth, panelHeight);
		drawWeaponSlots(g2, inventory, player.getWeaponIndex(), panelWidth, panelHeight);
		drawMinimap(g2, player, panelWidth, panelHeight);

		g2.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
		g2.setColor(GOLD_COLOR);

		String fps = "FPS: " + String.valueOf(GameController.getInstance().getFPS());
		g2.drawString(fps, panelWidth - 100, panelHeight - 8);

		drawBossHud(g2, getWidth(), getHeight());
	}


	private void drawHPBar(Graphics2D g2, Player player, int w, int h) {
		int barW = (int) (0.25 * w);
		int barH = 30;
		int x = (int) (0.03 * w);
		int y = (int) (0.05 * h);

		double hp = player.getHp();
		double maxHp = player.getHpMax();
		float percent = (float) Math.max(0, Math.min(1, hp / maxHp));

		g2.setColor(UI_BORDER_DARK);
		g2.fillRect(x, y, barW, barH);

		g2.setStroke(new BasicStroke(3));
		g2.setColor(UI_BORDER_LIGHT);
		g2.drawRect(x, y, barW, barH);

		int fillWidth = (int) ((barW - 6) * percent);
		int fillHeight = barH - 6;

		if (fillWidth > 0) {
			g2.setColor(HP_RED_DARK);
			g2.fillRect(x + 3, y + 3, fillWidth, fillHeight);

			g2.setColor(HP_RED_LIGHT);
			g2.fillRect(x + 3, y + 3, fillWidth, fillHeight / 2);
		}

		String hpText = (int) hp + " / " + (int) maxHp;
		g2.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
		FontMetrics fm = g2.getFontMetrics();
		int textX = x + (barW - fm.stringWidth(hpText)) / 2;
		int textY = y + (barH + fm.getAscent()) / 2 - 2;

		g2.setColor(Color.BLACK);
		g2.drawString(hpText, textX + 2, textY + 2);
		g2.setColor(Color.WHITE);
		g2.drawString(hpText, textX, textY);
	}

	private void drawMoney(Graphics2D g2, Player player, int w, int h) {
		String moneyText = "GOLD: " + (int) player.getMoney();

		g2.setFont(new Font(FONT_NAME, Font.PLAIN, 32));
		FontMetrics fm = g2.getFontMetrics();

		int x = (int) (0.03 * w);
		int y = (int) (0.05 * h) + 40 + fm.getAscent();

		g2.setColor(Color.BLACK);
		g2.drawString(moneyText, x + 2, y + 2);

		g2.setColor(GOLD_COLOR);
		g2.drawString(moneyText, x, y);
	}

	private void drawWeaponSlots(Graphics2D g2, Inventory inventory, int activeIndex, int w, int h) {
		int slotSize = 64;
		int spacing = 10;
		int startX = (int) (0.03 * w);
		int startY = h - slotSize - 40;

		WeaponItem[] equipped = inventory.getEquippedWeapons();

		for (int i = 0; i < Inventory.MAX_EQU; i++) {
			int slotX = startX + i * (slotSize + spacing);
			int slotY = startY;

			boolean isActive = (i == activeIndex && equipped[i] != null);

			g2.setColor(new Color(30, 30, 40, 200));
			g2.fillRect(slotX, slotY, slotSize, slotSize);

			g2.setStroke(new BasicStroke(3));
			if (isActive) {
				g2.setColor(Color.WHITE);
			} else {
				g2.setColor(new Color(80, 80, 90));
			}
			g2.drawRect(slotX, slotY, slotSize, slotSize);

			g2.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
			g2.setColor(Color.GRAY);
			g2.drawString(String.valueOf(i + 1), slotX + 5, slotY + 15);

			if (equipped[i] != null) {
				WeaponItemDefinition def = (WeaponItemDefinition) equipped[i].getItemDefinition();

				g2.setColor(new Color(200, 100, 50));
				g2.fillOval(slotX + 16, slotY + 16, 32, 32);

				String name = def.getName();
				// Truncate
				if (name.length() > 8) name = name.substring(0, 8) + ".";

				g2.setColor(Color.WHITE);
				g2.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
				FontMetrics fm = g2.getFontMetrics();
				int txtX = slotX + (slotSize - fm.stringWidth(name)) / 2;
				g2.drawString(name, txtX, slotY + slotSize - 5);
			} else {
				// Empty text
				g2.setColor(new Color(60, 60, 70));
				g2.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
				g2.drawString("EMPTY", slotX + 18, slotY + 38);
			}
		}
	}

	private void drawMinimap(Graphics2D g2, Player player, int w, int h) {
		try {
			World currentWorld = GameController.getInstance().getWorldManager().getCurrentWorld();
			if (currentWorld == null) return;

			TileManager tileManager = currentWorld.getTileManager();
			if (tileManager == null || tileManager.getMapCache() == null) return;

			BufferedImage mapImage = tileManager.getMapCache().getSprite();
			if (mapImage == null) return;

			// Minimap Logic
			int mapW = mapImage.getWidth();
			int mapH = mapImage.getHeight();
			double desiredWidth = w * MINIMAP_SCALE_PERCENT;
			double scale = desiredWidth / mapW;

			int minimapW = (int) (mapW * scale);
			int minimapH = (int) (mapH * scale);
			int miniX = w - minimapW - MINIMAP_PADDING;
			int miniY = MINIMAP_PADDING;

			// Background with transparency
			g2.setColor(new Color(10, 10, 15, 180));
			g2.fillRect(miniX, miniY, minimapW, minimapH);

			g2.drawImage(mapImage, miniX, miniY, minimapW, minimapH, null);

			// Border
			g2.setStroke(new BasicStroke(3));
			g2.setColor(UI_BORDER_LIGHT);
			g2.drawRect(miniX, miniY, minimapW, minimapH);

			// Save original stroke
			Stroke originalStroke = g2.getStroke();

			// Draw enemies
			if (currentWorld.getWaveManager() != null) {
				List<Ennemy> enemies = currentWorld.getWaveManager().getCurrentEnemies();
				if (enemies != null) {
					for (Ennemy enemy : enemies) {
						if (enemy == null || enemy.getHp() <= 0) continue;

						Vector3D ePos = enemy.getPos();
						int eMiniX = miniX + (int) (ePos.getX() * scale);
						int eMiniY = miniY + (int) (ePos.getY() * scale);

						if (enemy instanceof BossEnnemy) {
							// Boss: larger, pink/purple
							g2.setColor(new Color(255, 50, 150));
							g2.fillOval(eMiniX - 4, eMiniY - 4, 9, 9);
							g2.setColor(Color.WHITE);
							g2.setStroke(new BasicStroke(1));
							g2.drawOval(eMiniX - 4, eMiniY - 4, 9, 9);
						} else {
							// Common enemies: smaller, red
							g2.setColor(new Color(220, 20, 20));
							g2.fillOval(eMiniX - 2, eMiniY - 2, 5, 5);
							g2.setColor(new Color(100, 0, 0));
							g2.setStroke(new BasicStroke(1));
							g2.drawOval(eMiniX - 2, eMiniY - 2, 5, 5);
						}
					}
				}
			}

			// Draw player
			Vector3D playerPos = player.getPos();
			int pMiniX = miniX + (int) (playerPos.getX() * scale);
			int pMiniY = miniY + (int) (playerPos.getY() * scale);

			g2.setColor(Color.GREEN);
			g2.fillOval(pMiniX - 3, pMiniY - 3, 7, 7);
			g2.setColor(Color.WHITE);
			g2.setStroke(new BasicStroke(1));
			g2.drawOval(pMiniX - 3, pMiniY - 3, 7, 7);

			// Restore original stroke
			g2.setStroke(originalStroke);

		} catch (Exception e) {
			// Silently ignore
		}
	}

	private void drawBossHud(Graphics2D g2, int w, int h) {
		World world = GameController.getInstance().getWorldManager().getCurrentWorld();
		if (world == null) return;

		// Active boss?
		List<Ennemy> enemies = world.getWaveManager().getCurrentEnemies();
		BossEnnemy activeBoss = null;

		for (Ennemy e : enemies) {
			if (e instanceof BossEnnemy && e.getHp() > 0) {
				activeBoss = (BossEnnemy) e;
				break;
			}
		}

		// Boss found? draw bar :D
		if (activeBoss != null) {
			drawBossHealthBar(g2, activeBoss, w, h);
		}
	}

	private void drawBossHealthBar(Graphics2D g2, BossEnnemy boss, int screenW, int screenH) {

		// position and dims
		int barWidth = (int) (screenW * 0.4);
		int barHeight = 25;
		int x = (screenW - barWidth) / 2;
		int y = 80;

		double hp = boss.getHp();
		double maxHp = boss.getHpMax();
		float hpPercent = (float) Math.max(0, Math.min(1, hp / maxHp));

		// background
		g2.setColor(BOSS_BAR_BG);
		g2.fillRect(x, y, barWidth, barHeight);

		// fill
		int fillWidth = (int) (barWidth * hpPercent);
		g2.setColor(BOSS_BAR_FILL);
		g2.fillRect(x, y, fillWidth, barHeight);

		// Optional: Add a "glint" on the top half for a 3D effect
		g2.setColor(new Color(255, 255, 255, 30));
		g2.fillRect(x, y, fillWidth, barHeight / 2);

		// phase markers
		g2.setStroke(new BasicStroke(3));
		g2.setColor(PHASE_MARKER_COLOR);

		int xPhase2 = x + (int) (barWidth * 0.333);
		int xPhase1 = x + (int) (barWidth * 0.666);

		g2.drawLine(xPhase1, y - 2, xPhase1, y + barHeight + 2);
		g2.drawLine(xPhase2, y - 2, xPhase2, y + barHeight + 2);

		// border
		g2.setStroke(new BasicStroke(4));
		g2.setColor(BOSS_BAR_BORDER);
		g2.drawRect(x, y, barWidth, barHeight);

		// Inner white trim for style
		g2.setStroke(new BasicStroke(2));
		g2.setColor(new Color(200, 200, 200, 100));
		g2.drawRect(x - 2, y - 2, barWidth + 4, barHeight + 4);


		String name = "BOSS";
		g2.setFont(new Font("ByteBounce", Font.BOLD, 24));
		FontMetrics fm = g2.getFontMetrics();
		int textX = x + (barWidth - fm.stringWidth(name)) / 2;
		int textY = y - 10;

		// Text Shadow
		g2.setColor(Color.BLACK);
		g2.drawString(name, textX + 2, textY + 2);
		// Text Color
		g2.setColor(Color.WHITE);
		g2.drawString(name, textX, textY);
	}
}