package ma.ac.emi.world;

import java.awt.*;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector2D;

@Getter
@Setter
public class World {
	private int width;
	private int height;
	private Player player;
	private Ennemy ennemy;

	public World(int w, int h) {
		width = w; height = h;
		player = new Player(new Vector2D(500,500), 2);
		ennemy = new Ennemy(new Vector2D(320,320), 1);
	}

	public void update(double step) {
		player.update(step);
		Vector2D playerPos = player.getPos();
		ennemy.update(step, playerPos);

	}

	public void draw(Graphics g) {
		player.draw(g);
		ennemy.draw(g);
	}
}
