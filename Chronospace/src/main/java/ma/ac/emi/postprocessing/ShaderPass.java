package ma.ac.emi.postprocessing;

import com.jogamp.opengl.GL3;
import lombok.Getter;

@Getter
public class ShaderPass {
    
    private ShaderManager shaderManager;
    private ShaderUniformSetter uniformSetter;
    private int shaderProgram;
    
    public ShaderPass() {
        this(null);
    }
    
    public ShaderPass(ShaderUniformSetter uniformSetter) {
        this.uniformSetter = uniformSetter;
    }
    
    public void initialize(GL3 gl3, String vertexPath, String fragmentPath) {
        shaderManager = new ShaderManager();
        shaderManager.initialize(gl3, vertexPath, fragmentPath);
        shaderProgram = shaderManager.getShaderProgram();
    }
    
    public void use(GL3 gl3) {
        shaderManager.use(gl3);
    }
    
    public void release(GL3 gl3) {
        shaderManager.release(gl3);
    }
    
    public void dispose(GL3 gl3) {
        shaderManager.dispose(gl3);
    }
}
