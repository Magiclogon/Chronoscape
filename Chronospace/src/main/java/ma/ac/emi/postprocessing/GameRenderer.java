package ma.ac.emi.postprocessing;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamecontrol.GameObject;

public class GameRenderer {
    
    @Getter
    private BufferedImage gameBuffer;
    @Getter
    private BufferedImage emissiveBuffer;
    private Graphics2D gameGraphics;
    private Graphics2D emissiveGraphics;
    
    public GameRenderer(int width, int height) {
        createBuffers(width, height);
    }
    
    private void createBuffers(int width, int height) {
        gameBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        gameGraphics = gameBuffer.createGraphics();
        gameGraphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_OFF
            );
        
        emissiveBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        emissiveGraphics = emissiveBuffer.createGraphics();
        emissiveGraphics.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_OFF
        );
    }
    
    public void renderToBuffers(int width, int height, Camera camera, List<GameObject> drawables) {
        if (width <= 0 || height <= 0) return;
        
        if (gameBuffer.getWidth() != width || gameBuffer.getHeight() != height ||
        		emissiveBuffer.getWidth() != width || emissiveBuffer.getHeight() != height) {
            createBuffers(width, height);
        }
        
        try {
            GameController.getInstance();
			GameController.draw.acquire();
            
            Color voidColor = GameController.getInstance()
                .getWorldManager()
                .getCurrentWorld()
                .getVoidColor();
            gameGraphics.setColor(voidColor);
            gameGraphics.fillRect(0, 0, width, height);
            // Clear emissive buffer to black (no glow by default)
            emissiveGraphics.setColor(Color.BLACK);
            emissiveGraphics.fillRect(0, 0, width, height);

            // Test emissive rectangle
            emissiveGraphics.setColor(Color.WHITE); // Use WHITE for maximum brightness
            emissiveGraphics.fillRect(100, 100, 100, 100);
            
            if (camera == null) {
                GameController.getInstance();
				GameController.update.release();
                return;
            }
            
            renderWithCamera(camera, drawables);
            
            GameController.getInstance();
			GameController.update.release();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void renderWithCamera(Camera camera, List<GameObject> drawables) {
        AffineTransform oldTransform = gameGraphics.getTransform();
        
        AffineTransform camTx;
        synchronized (camera) {
            camTx = new AffineTransform(camera.getCamTransform());
        }
        gameGraphics.transform(camTx);
        
        GameObject currentObject = null;
        try {
            List<GameObject> snapshot;
            synchronized (drawables) {
                snapshot = new ArrayList<>(drawables);
            }
            
            for (GameObject obj : snapshot) {
                currentObject = obj;
                obj.draw(gameGraphics);
            }
            
        } catch (Exception e) {
            String objectName = (currentObject != null) 
                ? currentObject.getClass().getSimpleName() 
                : "Unknown";
            System.err.println("Drawing error from: " + objectName);
            e.printStackTrace();
        }
        
        gameGraphics.setTransform(oldTransform);
    }
}

