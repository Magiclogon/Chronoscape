package ma.ac.emi.glgraphics;

import com.jogamp.opengl.GL3;
import ma.ac.emi.fx.Sprite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Texture {
    public int id;
    public final int width;
    public final int height;

    public Texture(GL3 gl, Sprite sprite) {
        BufferedImage image = sprite.getSprite();
        width = image.getWidth();
        height = image.getHeight();
        
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        
        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
        
        // ARGB → RGBA
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                int p = pixels[y * width + x];
                buffer.put((byte) ((p >> 16) & 0xFF)); // R
                buffer.put((byte) ((p >> 8) & 0xFF));  // G
                buffer.put((byte) (p & 0xFF));         // B
                buffer.put((byte) ((p >> 24) & 0xFF)); // A
            }
        }
        buffer.flip();
        
        int[] tmp = new int[1];
        gl.glGenTextures(1, tmp, 0);
        id = tmp[0];
        
        gl.glBindTexture(GL3.GL_TEXTURE_2D, id);
        gl.glTexImage2D(
            GL3.GL_TEXTURE_2D,
            0,
            GL3.GL_RGBA,
            width,
            height,
            0,
            GL3.GL_RGBA,
            GL3.GL_UNSIGNED_BYTE,
            buffer
        );
        
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_NEAREST);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);
    }
    
   
    public void dispose(GL3 gl) {
        if (id != 0) {
            gl.glDeleteTextures(1, new int[]{id}, 0);
            id = 0;
        }
    }
    
    public boolean isValid(GL3 gl) {
        return id != 0 && gl.glIsTexture(id);
    }

}