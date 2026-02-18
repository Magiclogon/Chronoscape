package ma.ac.emi.UI;

import ma.ac.emi.glgraphics.post.config.PostFXConfig;

@FunctionalInterface
public interface GraphicsSettingsCallback {
    
    void onSettingsChanged(PostFXConfig config);
}