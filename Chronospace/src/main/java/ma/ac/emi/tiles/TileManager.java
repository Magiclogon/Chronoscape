package ma.ac.emi.tiles;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import lombok.Getter;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.gamecontrol.GamePanel;

@Getter
public class TileManager {

    private final int width;
    private final int height;
    private final MapTheme theme;
    private final Map<TileType, Sprite> tileSprites;
    
    private final TileMap map;
    // OPTIMIZATION: This holds the pre-drawn map
    private BufferedImage mapCache;

    public TileManager(MapTheme theme, TileMap map) {
        this.width = map.getMap()[0].length;
        this.height = map.getMap().length;
        this.theme = theme;
        this.tileSprites = new HashMap<>();
        
        this.map = map;

        loadTileSprites();
        //generateMap();

        // Render the map once into memory
        cacheMap();
    }

    private void loadTileSprites() {
        String p = theme.getPath();
        
        SpriteSheet sheet = new SpriteSheet(AssetsLoader.getSprite(p + "/Tileset.png"), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
        tileSprites.put(TileType.TOP_LEFT_OUT, sheet.getSprite(7, 2));
        tileSprites.put(TileType.TOP_EDGE_OUT, sheet.getSprite(1, 0));
        tileSprites.put(TileType.TOP_RIGHT_OUT, sheet.getSprite(5, 2));
        tileSprites.put(TileType.TOP_LEFT_IN, sheet.getSprite(5, 0));
        tileSprites.put(TileType.TOP_RIGHT_IN, sheet.getSprite(7, 0));
        tileSprites.put(TileType.TOP_EDGE_IN, sheet.getSprite(6, 0));
        
        tileSprites.put(TileType.BOTTOM_LEFT_OUT, sheet.getSprite(4, 2));
        tileSprites.put(TileType.BOTTOM_RIGHT_OUT, sheet.getSprite(4, 1));
        tileSprites.put(TileType.BOTTOM_EDGE_OUT, sheet.getSprite(6, 0));
        tileSprites.put(TileType.BOTTOM_LEFT_IN, sheet.getSprite(7, 3));
        tileSprites.put(TileType.BOTTOM_RIGHT_IN, sheet.getSprite(5, 3));
        tileSprites.put(TileType.BOTTOM_EDGE_IN, sheet.getSprite(6, 3));
        
        tileSprites.put(TileType.LEFT_EDGE_OUT, sheet.getSprite(7, 2));
        tileSprites.put(TileType.RIGHT_EDGE_OUT, sheet.getSprite(5, 2));
        tileSprites.put(TileType.LEFT_EDGE_IN, sheet.getSprite(5, 2));
        tileSprites.put(TileType.RIGHT_EDGE_IN, sheet.getSprite(7, 2));
        
        tileSprites.put(TileType.GROUND_DEFAULT, sheet.getSprite(1, 2));
        tileSprites.put(TileType.VOID, sheet.getSprite(6, 1));
    }


    // NEW METHOD: Draws all tiles onto a single image
    private void cacheMap() {
        int tileSize = GamePanel.TILE_SIZE;
        int pixelWidth = width * tileSize;
        int pixelHeight = height * tileSize;

        // Create an empty image the size of the entire world
        mapCache = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = mapCache.createGraphics();

        // Run the heavy loop here (ONCE only)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
            	if(isSolid(x, y)) continue;
                TileType type = map.getMap()[y][x];
                Sprite sprite = tileSprites.get(type);

                if (sprite != null) {
                    g2.drawImage(sprite.getSprite(),
                            x * tileSize - tileSize/2,
                            y * tileSize - tileSize/2,
                            tileSize,
                            tileSize,
                            null);
                }
            }
        }
        g2.dispose(); // Release resources
    }

    public void draw(Graphics g) {
        if (mapCache != null) {
            g.drawImage(mapCache, 0, 0, null);
        }
    }

    public boolean isSolid(int x, int y) {
        TileType type = map.getMap()[y][x];
        return !type.name().startsWith("GROUND") || type.name().endsWith("OBS");
    }
}