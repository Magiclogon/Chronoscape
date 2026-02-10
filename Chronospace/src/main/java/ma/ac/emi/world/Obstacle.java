package ma.ac.emi.world;

import java.awt.Graphics;
import java.util.List;

import com.jogamp.opengl.GL3;

import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.gamecontrol.GameObject;
import ma.ac.emi.gamecontrol.GamePanel;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.glgraphics.Texture;
import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.gamelogic.physics.AABB;
import ma.ac.emi.tiles.TileManager;
import ma.ac.emi.tiles.TileType;

public class Obstacle extends GameObject {

    private final TileType type;
    private final TileManager tileManager;

    public Obstacle(Vector3D pos, TileType type, TileManager tileManager) {
        this.pos = pos;
        this.type = type;
        this.tileManager = tileManager;

        Sprite sprite = tileManager.getTileSprites().get(type);
        if (sprite == null) sprite = AssetsLoader.getSprite("default_sprite.png");

        setSpriteSheet(new SpriteSheet(
                sprite,
                GamePanel.TILE_SIZE,
                GamePanel.TILE_SIZE
        ));

        this.hitbox = createHitbox(type);

        baseColorCorrection = new SpriteColorCorrection();
        baseColorCorrection.setValue(0.3f);
    }

    /* ============================
       HITBOX CREATION
       ============================ */

    private AABB createHitbox(TileType type) {

        double half = GamePanel.TILE_SIZE / 2.0;
        Vector3D center = pos;
        Vector3D halfSize = new Vector3D(half, half);

        switch (type) {

            // Vertical walls
            case LEFT_EDGE_IN:
            case RIGHT_EDGE_OUT:
                halfSize = new Vector3D(half / 2.0, half);
                center = pos.add(new Vector3D(-half / 2.0, 0));
                break;

            case RIGHT_EDGE_IN:
            case LEFT_EDGE_OUT:
                halfSize = new Vector3D(half / 2.0, half);
                center = pos.add(new Vector3D(half / 2.0, 0));
                break;

            // Horizontal walls
            case TOP_EDGE_IN:
            case TOP_EDGE_OUT:
            case TOP_LEFT_IN:
            case TOP_RIGHT_IN:
                halfSize = new Vector3D(half, half / 2.0);
                center = pos.add(new Vector3D(0, half / 2.0));
                break;

            case BOTTOM_EDGE_IN:
            case BOTTOM_EDGE_OUT:
                halfSize = new Vector3D(half, half / 2.0);
                center = pos.add(new Vector3D(0, half / 2.0));
                break;

            // Solid blocks
            case BOTTOM_LEFT_OUT:
            case BOTTOM_RIGHT_OUT:
            case BOTTOM_LEFT_IN:
            case BOTTOM_RIGHT_IN:
                halfSize = new Vector3D(half, half);
                center = pos;
                break;

            default:
                // Non-collidable
                return null;
        }

        return new AABB(center, halfSize);
    }

    /* ============================
       COLLISION API
       ============================ */

    public AABB getPhysicsAABB() {
        return hitbox;
    }

    public boolean isSolid() {
        return hitbox != null;
    }

    public static boolean isPositionInObstacles(Vector3D pos, List<Obstacle> obstacles) {
        for (Obstacle o : obstacles) {
            if (o.hitbox != null &&
                Math.abs(pos.getX() - o.hitbox.center.getX()) <= o.hitbox.half.getX() &&
                Math.abs(pos.getY() - o.hitbox.center.getY()) <= o.hitbox.half.getY()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAABBInObstacles(AABB box, List<Obstacle> obstacles) {
        for (Obstacle o : obstacles) {
            if (o.hitbox == null) continue;

            boolean overlapX = Math.abs(box.center.getX() - o.hitbox.center.getX()) < (box.half.getX() + o.hitbox.half.getX());
            boolean overlapY = Math.abs(box.center.getY() - o.hitbox.center.getY()) < (box.half.getY() + o.hitbox.half.getY());

            if (overlapX && overlapY) {
                return true;
            }
        }
        return false;
    }

    /* ============================
       RENDERING
       ============================ */

    @Override
    public void draw(Graphics g) {
        g.drawImage(
            getSpriteSheet().getSprite(0, 0,
                    GamePanel.TILE_SIZE,
                    GamePanel.TILE_SIZE
            ).getSprite(),
            (int) (pos.getX() - GamePanel.TILE_SIZE / 2),
            (int) (pos.getY() - GamePanel.TILE_SIZE / 2),
            null
        );
    }

    @Override
    public void drawGL(GL3 gl, GLGraphics glGraphics) {
        Sprite sprite = getSpriteSheet().getSprite(0, 0,
                GamePanel.TILE_SIZE,
                GamePanel.TILE_SIZE);

        Texture texture = sprite.getTexture(gl);

        glGraphics.drawSprite(
                gl,
                texture,
                (float) (pos.getX() - GamePanel.TILE_SIZE / 2),
                (float) (pos.getY() - GamePanel.TILE_SIZE / 2),
                GamePanel.TILE_SIZE,
                GamePanel.TILE_SIZE,
                baseColorCorrection
        );

        //if(hitbox != null) glGraphics.drawQuad(gl, (float)(hitbox.center.getX()-hitbox.half.getX()), (float)(hitbox.center.getY()-hitbox.half.getY()),
        //		(float)(hitbox.half.getX()*2), (float)(hitbox.half.getY()*2));
    }

    @Override
    public void update(double step) {
        // Static object
    }
}
