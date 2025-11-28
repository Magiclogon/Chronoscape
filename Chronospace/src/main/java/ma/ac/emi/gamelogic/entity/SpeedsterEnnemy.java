package ma.ac.emi.gamelogic.entity;

import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.gamelogic.weapon.Weapon;
import ma.ac.emi.gamelogic.weapon.WeaponItemFactory;
import ma.ac.emi.math.Vector3D;

import java.awt.*;

public class SpeedsterEnnemy extends Ennemy{

    public SpeedsterEnnemy(Vector3D pos, double speed) {
        super(pos, speed);
    }

    @Override
    protected void initStats() {
        setHpMax(60);
        setHp(getHpMax());
    }

    @Override
    public void initWeapon() {
        setWeapon(new Weapon(WeaponItemFactory.getInstance().createWeaponItem("sword")));
        super.initWeapon();
    }

    @Override
    public void draw(Graphics g) {
        if(hp > 0) {
            g.setColor(Color.YELLOW);
            g.fillRect((int)(pos.getX()), (int)(pos.getY()), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
        }

        g.setColor(Color.yellow);
        g.drawRect(bound.x, bound.y, bound.width, bound.height);
        super.draw(g);

    }
}
