package ma.ac.emi.UI;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.world.World;

import java.awt.*;

import javax.swing.JPanel;

public class GameUIPanel extends JPanel{

	private World world;
	
	public GameUIPanel(World world) {
		this.world = world;
		this.setOpaque(false);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		Player player = world.getPlayer();

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
	}
}
