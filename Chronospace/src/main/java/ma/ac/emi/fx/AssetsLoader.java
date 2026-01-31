package ma.ac.emi.fx;

import javax.imageio.ImageIO;

import com.jogamp.opengl.GL3;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AssetsLoader {

    private final static Map<String, Sprite> sprites = new HashMap<>();
    private static String rootFolder;
    
    public static Sprite getSprite(String key) {
        return sprites.get(key);
    }

    public static Map<String, Sprite> getAllSprites() {
        return Collections.unmodifiableMap(sprites);
    }

    public static void loadAssets(String rootFolder) {
        try {
            Enumeration<URL> urls = Thread.currentThread()
                    .getContextClassLoader()
                    .getResources(rootFolder);

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();
                
                if (protocol.equals("file")) {
                    loadFromFileSystem(url, rootFolder);
                } else if (protocol.equals("jar")) {
                    loadFromJar(url, rootFolder);
                }
            }
            
            AssetsLoader.rootFolder = rootFolder;
            System.out.println("Assets loaded successfully from: " + AssetsLoader.rootFolder + "!");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load assets folder: " + rootFolder, e);
        }
    }

    //LOADING FROM FILESYSTEM (running in IDE)
    private static void loadFromFileSystem(URL url, String rootFolder) throws IOException {
        java.nio.file.Path rootPath = null;
		try {
			rootPath = java.nio.file.Paths.get(url.toURI());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        java.nio.file.Files.walk(rootPath)
            .filter(java.nio.file.Files::isRegularFile)
            .forEach(filePath -> {
                try {
                    String file = filePath.toString().replace("\\", "/");

                    if (isImage(file)) {
                        String key = file.substring(file.indexOf(rootFolder) + rootFolder.length() + 1);
                        try (InputStream is = java.nio.file.Files.newInputStream(filePath)) {
                            sprites.put(key, new Sprite(ImageIO.read(is)));
                        }
                    }

                } catch (IOException e) {
                    System.err.println("Failed to load image: " + filePath);
                }
            });
    }

    //LOADING FROM JAR (game packaged)
 
    private static void loadFromJar(URL url, String rootFolder) throws IOException {
        String path = url.getPath();
        String jarPath = path.substring("file:".length(), path.indexOf("!"));

        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (name.startsWith(rootFolder) && isImage(name)) {
                    try (InputStream is = jar.getInputStream(entry)) {
                        String key = name.substring(rootFolder.length() + 1);
                        sprites.put(key, new Sprite(ImageIO.read(is)));
                    }
                }
            }
        }
    }

    private static boolean isImage(String path) {
        String lower = path.toLowerCase();
        return lower.endsWith(".png") ||
               lower.endsWith(".jpg") ||
               lower.endsWith(".jpeg") ||
               lower.endsWith(".gif");
    }
    
    public static void disposeAll(GL3 gl) {
        for (Sprite sprite : sprites.values()) {
            sprite.dispose(gl);
        }
        sprites.clear();
        
        System.out.println("Disposing of sprites");
    }

	public static void reloadAssets() {
		loadAssets(rootFolder);
	}

}

