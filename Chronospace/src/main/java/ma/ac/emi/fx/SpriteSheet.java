package ma.ac.emi.fx;

import javax.imageio.ImageIO;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SpriteSheet {

    private Sprite sheet;
    private int tileWidth;
    private int tileHeight;

    private Map<String, Sprite> cache = new HashMap<>();

    
    public SpriteSheet(String path, int tileWidth, int tileHeight) {
        sheet = new Sprite(path);

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    
    public Sprite getSprite(int col, int row) {
        String key = col + "_" + row;

        if (cache.containsKey(key))
            return cache.get(key);

        Sprite sprite = sheet.getSubimage(
                col * tileWidth,
                row * tileHeight,
                tileWidth,
                tileHeight
        );

        cache.put(key, sprite);
        return sprite;
    }

  
    public Sprite getSprite(int x, int y, int width, int height) {
        String key = x + "_" + y + "_" + width + "_" + height;

        if (cache.containsKey(key))
            return cache.get(key);

        Sprite sprite = sheet.getSubimage(x, y, width, height);
        cache.put(key, sprite);

        return sprite;
    }

    
    public Sprite[] getAnimationRow(int row, int frameCount) {
        Sprite[] frames = new Sprite[frameCount];

        for (int i = 0; i < frameCount; i++) {
            frames[i] = getSprite(i, row);
        }

        return frames;
    }

}

