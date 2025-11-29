package ma.ac.emi.tiles;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.gamecontrol.GamePanel;

public class TileManager {

    private TileType[][] mapLayout;
    private final int width;
    private final int height;
    private final MapTheme theme;
    private final Map<TileType, Sprite> tileSprites;
    private final Random random;

    // OPTIMIZATION: This holds the pre-drawn map
    private BufferedImage mapCache;

    public TileManager(int width, int height, MapTheme theme) {
        this.width = width;
        this.height = height;
        this.theme = theme;
        this.mapLayout = new TileType[width][height];
        this.tileSprites = new HashMap<>();
        this.random = new Random(19L);

        loadTileSprites();
        generateMap();

        // Render the map once into memory
        cacheMap();
    }

    private void loadTileSprites() {
        String p = theme.getPath();

        tileSprites.put(TileType.TOP_LEFT, AssetsLoader.getSprite(p + "/topleft-001.png"));
        tileSprites.put(TileType.TOP_RIGHT, AssetsLoader.getSprite(p + "/topright-001.png"));
        tileSprites.put(TileType.BOTTOM_LEFT, AssetsLoader.getSprite(p + "/bottomleft-001.png"));
        tileSprites.put(TileType.BOTTOM_RIGHT, AssetsLoader.getSprite(p + "/bottomright-001.png"));

        tileSprites.put(TileType.TOP_EDGE, AssetsLoader.getSprite(p + "/top-001.png"));
        tileSprites.put(TileType.BOTTOM_EDGE, AssetsLoader.getSprite(p + "/bottom-001.png"));
        tileSprites.put(TileType.LEFT_EDGE, AssetsLoader.getSprite(p + "/left-001.png"));
        tileSprites.put(TileType.RIGHT_EDGE, AssetsLoader.getSprite(p + "/right-001.png"));

        tileSprites.put(TileType.BORDER, AssetsLoader.getSprite(p + "/border.png"));

        tileSprites.put(TileType.GROUND_DEFAULT, AssetsLoader.getSprite(p + "/default.png"));
        tileSprites.put(TileType.GROUND_VAR_1_OBS, AssetsLoader.getSprite(p + "/basic-001.png"));
        tileSprites.put(TileType.GROUND_VAR_2_OBS, AssetsLoader.getSprite(p + "/basic-002.png"));
        tileSprites.put(TileType.GROUND_VAR_3_OBS, AssetsLoader.getSprite(p + "/basic-003.png"));
        tileSprites.put(TileType.GROUND_VAR_4_OBS, AssetsLoader.getSprite(p + "/basic-004.png"));
        tileSprites.put(TileType.GROUND_VAR_5_OBS, AssetsLoader.getSprite(p + "/basic-005.png"));
    }

    private void generateMap() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                mapLayout[x][y] = determineTileType(x, y);
            }
        }
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
                TileType type = mapLayout[x][y];
                Sprite sprite = tileSprites.get(type);

                if (sprite != null) {
                    g2.drawImage(sprite.getSprite(),
                            x * tileSize,
                            y * tileSize,
                            tileSize,
                            tileSize,
                            null);
                }
            }
        }
        g2.dispose(); // Release resources
    }

    private TileType determineTileType(int x, int y) {
        if (x == 0 && y == 0) return TileType.TOP_LEFT;
        if (x == width - 1 && y == 0) return TileType.TOP_RIGHT;
        if (x == 0 && y == height - 1) return TileType.BOTTOM_LEFT;
        if (x == width - 1 && y == height - 1) return TileType.BOTTOM_RIGHT;

        if (y == 0) return TileType.TOP_EDGE;
        if (y == height - 1) return TileType.BOTTOM_EDGE;
        if (x == 0) return TileType.LEFT_EDGE;
        if (x == width - 1) return TileType.RIGHT_EDGE;

        return getRandomGroundTile();
    }

    private TileType getRandomGroundTile() {
        double chance = random.nextDouble();
        if (chance > 0.05) return TileType.GROUND_DEFAULT;
        if (chance < 0.01) return TileType.GROUND_VAR_1_OBS;
        if (chance < 0.02) return TileType.GROUND_VAR_2_OBS;
        if (chance < 0.03) return TileType.GROUND_VAR_3_OBS;
        if (chance < 0.04) return TileType.GROUND_VAR_4_OBS;
        return TileType.GROUND_VAR_5_OBS;
    }

    public void draw(Graphics g) {
        if (mapCache != null) {
            g.drawImage(mapCache, 0, 0, null);
        }
    }

    public boolean isSolid(int x, int y) {
        TileType type = mapLayout[x][y];
        return !type.name().startsWith("GROUND") || type.name().endsWith("OBS");
    }
}