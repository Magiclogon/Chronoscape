package ma.ac.emi.postprocessing;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ShaderLoader {
    
    public static String loadShader(String resourcePath) throws IOException {
        try (InputStream is = ShaderLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Shader file not found: " + resourcePath);
            }
            return readInputStream(is);
        }
    }
    

    public static String loadShaderFromFile(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            return readInputStream(fis);
        }
    }
    

    private static String readInputStream(InputStream is) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
    }
    
    public static boolean validateShaderSource(String source) {
        if (source == null || source.trim().isEmpty()) {
            return false;
        }
        return source.contains("#version") && source.contains("void main()");
    }
}