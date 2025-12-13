package ma.ac.emi.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class LoadingScreen extends JPanel {
    private static final long serialVersionUID = 1L;

    private Image backgroundImage;
    private BufferedImage spinnerImage;

    private Font titleFont;
    private Font loadingFont;

    private Timer animationTimer;
    private double rotationAngle = 0;
    private final int ROTATION_SPEED = 5;

    public LoadingScreen() {
        try {
            try {
                backgroundImage = ImageIO.read(getClass().getResource("/assets/Menus/main_menu_image.png"));
            } catch (Exception e) {
                System.err.println("Background image not found, using solid color fallback.");
            }

            spinnerImage = ImageIO.read(getClass().getResource("/assets/Menus/wheel.png"));

            try {
                InputStream is = getClass().getResourceAsStream("/assets/Fonts/ByteBounce.ttf");
                if (is != null) {
                    Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);
                    titleFont = customFont.deriveFont(60f);
                    loadingFont = customFont.deriveFont(30f);
                } else {
                    titleFont = new Font("Monospaced", Font.BOLD, 60);
                    loadingFont = new Font("Monospaced", Font.BOLD, 30);
                }
            } catch (FontFormatException | IOException e) {
                titleFont = new Font("Monospaced", Font.BOLD, 60);
                loadingFont = new Font("Monospaced", Font.BOLD, 30);
                System.err.println("Error loading custom font, using default.");
            }

        } catch (Exception e) {
            System.err.println("Error loading loading screen assets.");
            e.printStackTrace();
        }

        animationTimer = new Timer(16, e -> {
            rotationAngle += ROTATION_SPEED;
            if (rotationAngle >= 360) {
                rotationAngle = 0;
            }
            repaint();
        });
    }

    public void startAnimation() {
        if (!animationTimer.isRunning()) {
            rotationAngle = 0;
            animationTimer.start();
        }
    }

    public void stopAnimation() {
        if (animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int width = getWidth();
        int height = getHeight();

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, width, height, this);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, height);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(titleFont);

        if (spinnerImage != null) {
            int spinnerSize = 128;
            int centerX = width / 2;
            int centerY = height / 2;

            AffineTransform oldTransform = g2d.getTransform();

            g2d.translate(centerX, centerY);
            g2d.rotate(Math.toRadians(rotationAngle));

            g2d.drawImage(spinnerImage, -spinnerSize / 2, -spinnerSize / 2, spinnerSize, spinnerSize, this);

            g2d.setTransform(oldTransform);
        }

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(loadingFont);
        drawCenteredString(g2d, "Loading...", width, height * 3 / 4);
    }

    private void drawCenteredString(Graphics g, String text, int frameWidth, int yPos) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int x = (frameWidth - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, yPos);
    }
}
