package ma.ac.emi.glgraphics.post.config;

public class PostFXConfig {

    public ColorCorrection colorCorrection;
    public Bloom bloom;

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
    }
}

