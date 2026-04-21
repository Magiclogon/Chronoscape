package ma.ac.emi.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Objects;

public class CursorManager {

    public enum CursorType { ARROW, TARGET, HIDDEN }

    private static Cursor arrowCursor;
    private static Cursor targetCursor;
    private static Cursor hiddenCursor;

    // Call once at startup
    public static void init() {
        arrowCursor  = loadCursor("/cursors/arrow.png",   new Point(0, 0));
        targetCursor = loadCursor("/cursors/target.png",  new Point(16, 16));
        
        BufferedImage blank = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        hiddenCursor = Toolkit.getDefaultToolkit().createCustomCursor(blank, new Point(0, 0), "hidden");
    }

    private static Cursor loadCursor(String resourcePath, Point hotspot) {
        try {
            BufferedImage img = ImageIO.read(
                Objects.requireNonNull(CursorManager.class.getResourceAsStream(resourcePath))
            );
            return Toolkit.getDefaultToolkit().createCustomCursor(img, hotspot, resourcePath);
        } catch (Exception e) {
            System.err.println("CursorManager: failed to load " + resourcePath + " — falling back to default.");
            return Cursor.getDefaultCursor();
        }
    }

    public static void apply(Component component, CursorType type) {
        Cursor c = switch (type) {
            case TARGET -> targetCursor;
            case HIDDEN -> hiddenCursor;
            default     -> arrowCursor;
        };
        component.setCursor(c);
    }
}