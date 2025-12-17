package ma.ac.emi.postprocessing;

import java.io.IOException;

import com.jogamp.opengl.GL3;

import lombok.Getter;

@Getter
public class ShaderManager {
    
    private int shaderProgram;
    
    private static final String DEFAULT_VERTEX_SHADER = "/shaders/screen.vert";
    private static final String DEFAULT_FRAGMENT_SHADER = "/shaders/postprocess.frag";
    
    private static final String FALLBACK_VERT_SHADER =
        "#version 330 core\n" +
        "layout (location = 0) in vec2 aPos;\n" +
        "layout (location = 1) in vec2 aTexCoord;\n" +
        "out vec2 TexCoord;\n" +
        "void main() {\n" +
        "    gl_Position = vec4(aPos.x, aPos.y, 0.0, 1.0);\n" +
        "    TexCoord = aTexCoord;\n" +
        "}";

    private static final String FALLBACK_FRAG_SHADER =
        "#version 330 core\n" +
        "out vec4 FragColor;\n" +
        "in vec2 TexCoord;\n" +
        "uniform sampler2D screenTexture;\n" +

        "void main() {\n" +
        "    vec4 color = texture(screenTexture, TexCoord);\n" +
        "    FragColor = color;\n" +
        "}";
    

    public void initialize(GL3 gl3) {
        initialize(gl3, DEFAULT_VERTEX_SHADER, DEFAULT_FRAGMENT_SHADER);
    }
    

    public void initialize(GL3 gl3, String vertexPath, String fragmentPath) {
        String vertexSource = loadShaderSource(vertexPath, FALLBACK_VERT_SHADER);
        String fragmentSource = loadShaderSource(fragmentPath, FALLBACK_FRAG_SHADER);
        
        shaderProgram = createShaderProgram(gl3, vertexSource, fragmentSource);
    }
    

    private String loadShaderSource(String resourcePath, String fallback) {
        try {
            return ShaderLoader.loadShader(resourcePath);
        } catch (IOException e) {
            System.err.println("Failed to load shader from " + resourcePath + 
                             ", using fallback: " + e.getMessage());
            return fallback;
        }
    }
    
    public void use(GL3 gl3) {
        gl3.glUseProgram(shaderProgram);
    }
    
    public void release(GL3 gl3) {
        gl3.glUseProgram(0);
    }
    
    public void setUniforms(GL3 gl3, float saturation, float brightness, float contrast) {
        gl3.glUniform1i(gl3.glGetUniformLocation(shaderProgram, "screenTexture"), 0);
        gl3.glUniform1f(gl3.glGetUniformLocation(shaderProgram, "uSaturation"), saturation);
        gl3.glUniform1f(gl3.glGetUniformLocation(shaderProgram, "uBrightness"), brightness);
        gl3.glUniform1f(gl3.glGetUniformLocation(shaderProgram, "uContrast"), contrast);
    }
    
    public void dispose(GL3 gl3) {
        gl3.glDeleteProgram(shaderProgram);
    }
    
    private int createShaderProgram(GL3 gl, String vertexSource, String fragmentSource) {
        int vertexShader = compileShader(gl, GL3.GL_VERTEX_SHADER, vertexSource, "Vertex");
        int fragmentShader = compileShader(gl, GL3.GL_FRAGMENT_SHADER, fragmentSource, "Fragment");
        
        int program = gl.glCreateProgram();
        gl.glAttachShader(program, vertexShader);
        gl.glAttachShader(program, fragmentShader);
        gl.glLinkProgram(program);
        checkProgramLinking(gl, program);
        
        gl.glDeleteShader(vertexShader);
        gl.glDeleteShader(fragmentShader);
        
        return program;
    }
    
    private int compileShader(GL3 gl, int type, String source, String typeName) {
        int shader = gl.glCreateShader(type);
        gl.glShaderSource(shader, 1, new String[]{source}, null);
        gl.glCompileShader(shader);
        checkShaderCompilation(gl, shader, typeName);
        return shader;
    }
    
    private void checkShaderCompilation(GL3 gl, int shader, String type) {
        int[] compiled = new int[1];
        gl.glGetShaderiv(shader, GL3.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            int[] logLength = new int[1];
            gl.glGetShaderiv(shader, GL3.GL_INFO_LOG_LENGTH, logLength, 0);
            byte[] log = new byte[logLength[0]];
            gl.glGetShaderInfoLog(shader, logLength[0], null, 0, log, 0);
            System.err.println(type + " shader compilation error: " + new String(log));
        }
    }
    
    private void checkProgramLinking(GL3 gl, int program) {
        int[] linked = new int[1];
        gl.glGetProgramiv(program, GL3.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0) {
            int[] logLength = new int[1];
            gl.glGetProgramiv(program, GL3.GL_INFO_LOG_LENGTH, logLength, 0);
            byte[] log = new byte[logLength[0]];
            gl.glGetProgramInfoLog(program, logLength[0], null, 0, log, 0);
            System.err.println("Program linking error: " + new String(log));
        }
    }
}
