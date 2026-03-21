package ma.ac.emi.UI;

import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.entity.BossEnnemy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.Inventory;
import ma.ac.emi.gamelogic.shop.WeaponItem;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehaviorDefinition;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
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

	// Icon cache — keyed by weapon id, loaded once and reused every frame
	private final java.util.Map<String, java.awt.image.BufferedImage> iconCache = new java.util.HashMap<>();

	private java.awt.image.BufferedImage getWeaponIcon(WeaponItemDefinition def) {
		String id = def.getId();
		if (iconCache.containsKey(id)) return iconCache.get(id);
		String path = def.getIconPath();
		if (path != null && !path.isBlank()) {
			try {
				Sprite sprite = AssetsLoader.getSprite(path);
				if (sprite != null) {
					iconCache.put(id, sprite.getSprite());
					return sprite.getSprite();
				}
			} catch (Exception ignored) {}
		}
		iconCache.put(id, null); // cache null so we don't retry every frame
		return null;
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
		drawAmmo(g2, player, panelWidth, panelHeight);
		drawWeaponSlots(g2, inventory, player.getWeaponIndex(), panelWidth, panelHeight);
		drawMinimap(g2, player, panelWidth, panelHeight);

		g2.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
		g2.setColor(GOLD_COLOR);

		String fps = "FPS: " + String.valueOf(GameController.getInstance().getFPS());
		g2.drawString(fps, panelWidth - 100, panelHeight - 8);

		drawBossHud(g2, getWidth(), getHeight());

		// Floating text (dodge, damage numbers, etc.)
		if (GameController.getInstance().getCamera() != null)
			FloatingTextRenderer.render(g2, GameController.getInstance().getCamera(), panelWidth, panelHeight);
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

	private void drawAmmo(Graphics2D g2, Player player, int w, int h) {
		if (player.getActiveWeapon() == null) return;
		var weapon = player.getActiveWeapon();
		WeaponItemDefinition def = (WeaponItemDefinition) weapon.getWeaponItem().getItemDefinition();
		int mag = def.getMagazineSize();
		if (mag <= 0) return;

		int    ammo = weapon.getAmmo();
		String text = ammo + " / " + mag;

		g2.setFont(new Font(FONT_NAME, Font.PLAIN, 28));
		FontMetrics fm = g2.getFontMetrics();
		int x = (int) (0.03 * w);
		int y = (int) (0.05 * h) + 40 + 32 + fm.getAscent() + 4;

		g2.setColor(Color.BLACK);
		g2.drawString(text, x + 2, y + 2);

		float ratio    = mag > 0 ? (float) ammo / mag : 0;
		Color ammoColor = ratio > 0.5f ? new Color(100, 220, 100)
		                : ratio > 0.2f ? new Color(220, 200, 60)
		                :                new Color(220, 60, 60);
		g2.setColor(ammoColor);
		g2.drawString(text, x, y);
	}

	private void drawWeaponSlots(Graphics2D g2, Inventory inventory, int activeIndex, int w, int h) {
		final int SLOT_SIZE  = 64;
		final int SPACING    = 10;
		final int BOTTOM_PAD = 16;

		WeaponItem[] equipped = inventory.getEquippedWeapons();
		Player       player   = Player.getInstance();

		int totalSlotsW = Inventory.MAX_EQU * SLOT_SIZE + (Inventory.MAX_EQU - 1) * SPACING;
		int startX      = (int) (0.03 * w);
		int slotsY      = h - SLOT_SIZE - BOTTOM_PAD;

		// ── Passive info strip above slots ────────────────────────────────
		WeaponItem activeItem = (activeIndex >= 0 && activeIndex < Inventory.MAX_EQU)
				? equipped[activeIndex] : null;
		if (activeItem != null) {
			WeaponItemDefinition def       = (WeaponItemDefinition) activeItem.getItemDefinition();
			java.util.List<String> passiveLines = collectPassiveLines(def);
			java.util.List<String> liveStats    = collectLiveStats(player, def);

			if (!passiveLines.isEmpty() || !liveStats.isEmpty()) {
				g2.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
				FontMetrics fm = g2.getFontMetrics();
				int lineH      = fm.getHeight();

				// PASSIVE header (1 line) + one line per passive + one line per live stat
				int headerLines = passiveLines.isEmpty() ? 0 : 1;
				int totalLines  = headerLines + passiveLines.size() + liveStats.size();
				int stripH      = totalLines * lineH + 14;
				int stripY      = slotsY - stripH - 6;

				// Measure the widest line so the box always fits its content
				int maxTextW = fm.stringWidth("PASSIVE");
				for (String line : passiveLines)
					maxTextW = Math.max(maxTextW, fm.stringWidth("  " + line));
				for (String stat : liveStats)
					maxTextW = Math.max(maxTextW, fm.stringWidth(stat));
				int stripW = Math.max(totalSlotsW, maxTextW + 16); // 16 = left padding (8) + right margin (8)

				g2.setColor(new Color(12, 12, 18, 210));
				g2.fillRoundRect(startX - 4, stripY, stripW + 8, stripH, 8, 8);
				g2.setColor(new Color(80, 80, 100, 100));
				g2.setStroke(new BasicStroke(1f));
				g2.drawRoundRect(startX - 4, stripY, stripW + 8, stripH, 8, 8);

				int ty = stripY + 7 + fm.getAscent();

				if (!passiveLines.isEmpty()) {
					g2.setColor(new Color(160, 160, 180));
					g2.drawString("PASSIVE", startX, ty);
					ty += lineH;
					for (String line : passiveLines) {
						g2.setColor(new Color(200, 200, 210));
						g2.drawString("  " + line, startX, ty);
						ty += lineH;
					}
				}

				for (String stat : liveStats) {
					int colon = stat.indexOf(':');
					if (colon != -1) {
						String label  = stat.substring(0, colon + 1);
						String value  = stat.substring(colon + 1).trim();
						int    labelW = fm.stringWidth(label + " ");
						g2.setColor(new Color(130, 130, 150));
						g2.drawString(label + " ", startX, ty);
						g2.setColor(new Color(120, 220, 120));
						g2.drawString(value, startX + labelW, ty);
					} else {
						g2.setColor(new Color(120, 220, 120));
						g2.drawString(stat, startX, ty);
					}
					ty += lineH;
				}
			}
		}

		// ── Weapon slots ──────────────────────────────────────────────────
		for (int i = 0; i < Inventory.MAX_EQU; i++) {
			int     slotX    = startX + i * (SLOT_SIZE + SPACING);
			boolean isActive = (i == activeIndex && equipped[i] != null);

			g2.setColor(new Color(22, 22, 30, 220));
			g2.fillRoundRect(slotX, slotsY, SLOT_SIZE, SLOT_SIZE, 6, 6);

			g2.setStroke(new BasicStroke(isActive ? 2.5f : 1.5f));
			g2.setColor(isActive        ? Color.WHITE
			          : equipped[i] != null ? rarityColor(equipped[i])
			          : new Color(55, 55, 65));
			g2.drawRoundRect(slotX, slotsY, SLOT_SIZE, SLOT_SIZE, 6, 6);

			g2.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
			g2.setColor(isActive ? Color.WHITE : new Color(90, 90, 100));
			g2.drawString(String.valueOf(i + 1), slotX + 4, slotsY + 14);

			if (equipped[i] != null) {
				WeaponItemDefinition def = (WeaponItemDefinition) equipped[i].getItemDefinition();

				// Draw weapon icon, or fall back to a rarity-tinted oval if none
				java.awt.image.BufferedImage icon = getWeaponIcon(def);
				if (icon != null) {
					int iconPad  = 10;
					int iconSize = SLOT_SIZE - iconPad * 2;
					double scale = Math.min((double) iconSize / icon.getWidth(),
					                        (double) iconSize / icon.getHeight());
					int drawW = (int)(icon.getWidth()  * scale);
					int drawH = (int)(icon.getHeight() * scale);
					int iconX = slotX + (SLOT_SIZE - drawW) / 2;
					int iconY = slotsY + iconPad + (iconSize - drawH) / 2;
					g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
					g2.drawImage(icon, iconX, iconY, drawW, drawH, null);
				} else {
					// Fallback — rarity-tinted oval
					Color iconColor = rarityColor(equipped[i]);
					g2.setColor(new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 60));
					g2.fillOval(slotX + 12, slotsY + 12, SLOT_SIZE - 24, SLOT_SIZE - 24);
					g2.setColor(iconColor.darker());
					g2.setStroke(new BasicStroke(1f));
					g2.drawOval(slotX + 12, slotsY + 12, SLOT_SIZE - 24, SLOT_SIZE - 24);
				}

				String name = def.getName();
				if (name.length() > 7) name = name.substring(0, 7) + ".";
				g2.setFont(new Font(FONT_NAME, Font.PLAIN, 13));
				FontMetrics fm  = g2.getFontMetrics();
				int         txtX = slotX + (SLOT_SIZE - fm.stringWidth(name)) / 2;
				g2.setColor(isActive ? Color.WHITE : new Color(180, 180, 190));
				g2.drawString(name, txtX, slotsY + SLOT_SIZE - 6);

				if (isActive && def.getMagazineSize() > 0)
					drawAmmoPips(g2, slotX, slotsY, SLOT_SIZE, player, def);

			} else {
				g2.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
				g2.setColor(new Color(55, 55, 65));
				FontMetrics fm    = g2.getFontMetrics();
				String      empty = "EMPTY";
				g2.drawString(empty,
						slotX + (SLOT_SIZE - fm.stringWidth(empty)) / 2,
						slotsY + SLOT_SIZE / 2 + fm.getAscent() / 2);
			}
		}
	}

	private void drawAmmoPips(Graphics2D g2, int slotX, int slotY, int slotSize,
	                           Player player, WeaponItemDefinition def) {
		int mag  = def.getMagazineSize();
		if (mag <= 0 || player.getActiveWeapon() == null) return;
		int ammo = player.getActiveWeapon().getAmmo();

		int maxPips   = Math.min(mag, 10);
		int pipW      = Math.max(2, (slotSize - 8) / maxPips - 2);
		int pipH      = 4;
		int pipY      = slotY + slotSize - pipH - 3;
		int totalW    = maxPips * (pipW + 2) - 2;
		int pipStartX = slotX + (slotSize - totalW) / 2;

		for (int p = 0; p < maxPips; p++) {
			int     px     = pipStartX + p * (pipW + 2);
			boolean filled = mag <= 10 ? (p < ammo)
			               : (p < (int)((float) ammo / mag * maxPips));
			g2.setColor(filled ? new Color(120, 220, 120) : new Color(50, 50, 60));
			g2.fillRoundRect(px, pipY, pipW, pipH, 2, 2);
		}
	}

	private java.util.List<String> collectPassiveLines(WeaponItemDefinition def) {
		java.util.List<String> lines = new java.util.ArrayList<>();
		for (WeaponBehaviorDefinition b : def.getBehaviorDefinitions()) {
			String desc = null;
			if (b instanceof ma.ac.emi.gamelogic.weapon.behavior.passive.PassiveWeaponEffectDefinition p)
				desc = p.describe();
			else if (b instanceof ma.ac.emi.gamelogic.weapon.behavior.passive.WeaponPassiveDefinition p)
				desc = p.describe();
			if (desc != null) lines.add(desc);
		}
		return lines;
	}

	private java.util.List<String> collectLiveStats(Player player, WeaponItemDefinition def) {
		boolean hasDodge = false, hasDefense = false,
		        hasSpeed = false, hasStrength = false, hasRegen = false;

		for (WeaponBehaviorDefinition b : def.getBehaviorDefinitions()) {
			if (b instanceof ma.ac.emi.gamelogic.weapon.behavior.passive.PassiveWeaponEffectDefinition p) {
				switch (p.getStat().toLowerCase()) {
					case "dodge"        -> hasDodge    = true;
					case "defense"      -> hasDefense  = true;
					case "speed"        -> hasSpeed    = true;
					case "strength"     -> hasStrength = true;
					case "health_regen" -> hasRegen    = true;
				}
			} else if (b instanceof ma.ac.emi.gamelogic.weapon.behavior.passive.WeaponPassiveDefinition p) {
				for (String stat : p.getAffectedStats()) {
					switch (stat) {
						case "dodge"        -> hasDodge    = true;
						case "defense"      -> hasDefense  = true;
						case "speed"        -> hasSpeed    = true;
						case "strength"     -> hasStrength = true;
						case "health_regen" -> hasRegen    = true;
					}
				}
			}
		}

		java.util.List<String> stats = new java.util.ArrayList<>();
		if (hasDodge)   stats.add(String.format("DODGE: %.0f%%", player.getDodge() * 100));
		if (hasDefense) stats.add(String.format("ARMOR: %.0f",   player.getDefense()));
		if (hasSpeed)   stats.add(String.format("SPEED: %.0f",   player.getSpeed()));
		if (hasStrength) {
			// Show the weapon's actual current damage (includes item upgrades + effect bonuses),
			// not player.strength which is a fixed config multiplier
			stats.add(String.format("DMG:   %.1f", def.getDamage()));
		}
		if (hasRegen)   stats.add(String.format("REGEN: %.1f/s", player.getRegenerationSpeed()));
		return stats;
	}

	private Color rarityColor(WeaponItem item) {
		return switch (item.getItemDefinition().getRarity()) {
			case LEGENDARY -> new Color(255, 215,   0);
			case EPIC      -> new Color(160,  32, 240);
			case RARE      -> new Color( 65, 105, 225);
			case COMMON    -> new Color( 50, 205,  50);
			default        -> new Color( 80,  80,  90);
		};
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