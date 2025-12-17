package ma.ac.emi.postprocessing;

import com.jogamp.opengl.GL3;


@FunctionalInterface
public interface ShaderUniformSetter {
    
    void setUniforms(GL3 gl3, int shaderProgram);
}
