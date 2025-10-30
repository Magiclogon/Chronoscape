package ma.ac.emi.gamelogic.entity;

import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.math.Vector2D;

import java.awt.*;

public class RangedEnnemy extends Ennemy{

    public RangedEnnemy(Vector2D pos, double speed) {
        super(pos, speed);
    }

    @Override
    public void draw(Graphics g) {
        if(hp > 0) {
            g.setColor(Color.MAGENTA);
            g.fillRect((int)(pos.getX()), (int)(pos.getY()), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
        }

        g.setColor(Color.yellow);
        g.drawRect(bound.x, bound.y, bound.width, bound.height);
    }
}