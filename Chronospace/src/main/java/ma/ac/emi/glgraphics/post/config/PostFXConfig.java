package ma.ac.emi.glgraphics.post.config;

public class PostFXConfig {

    public ColorCorrection colorCorrection;
    public Bloom bloom;
    public Glow glow;

    public static class ColorCorrection {
        public boolean enabled;
        public float r, g, b, a;
        public float brightness, contrast;
        public float hue, saturation, value;
    }

    public static class Bloom {
        public boolean enabled;
        public int downscale;
        public float threshold;
        public float blurRadius;
        public float intensity;
    }
    
    public static class Glow {
        public boolean enabled = true;
        public int downscale = 2;
        public float blurRadius = 5.0f;
        public float intensity = 1.0f;
    }

}

