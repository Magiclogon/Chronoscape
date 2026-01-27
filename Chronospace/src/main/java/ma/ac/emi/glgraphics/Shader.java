package ma.ac.emi.glgraphics;

import com.jogamp.opengl.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

public class Shader {

    private int programId;

    private Shader(int programId) {
        this.programId = programId;
    }

    /* ===============================
       Public API
       =============================== */

    public static Shader load(String vertexSource, String fragmentSource, GL3 gl) {
    	
    	String vertexsrc = loadShaderSource(vertexSource);
    	String fragmentsrc = loadShaderSource(fragmentSource);
    	System.out.println(vertexsrc);
    	System.out.println(fragmentsrc);
        int vertexShader = compile(gl, GL3.GL_VERTEX_SHADER, vertexsrc);
        int fragmentShader = compile(gl, GL3.GL_FRAGMENT_SHADER, fragmentsrc);

        int program = gl.glCreateProgram();
        gl.glAttachShader(program, vertexShader);
        gl.glAttachShader(program, fragmentShader);
        gl.glLinkProgram(program);

        // --- Link validation ---
        IntBuffer status = IntBuffer.allocate(1);
        gl.glGetProgramiv(program, GL3.GL_LINK_STATUS, status);

        if (status.get(0) == GL.GL_FALSE) {
            byte[] log = new byte[1024];
            gl.glGetProgramInfoLog(program, log.length, null, 0, log, 0);
            throw new RuntimeException("Shader link error:\n" + new String(log));
        }

        // shaders no longer needed after linking
        gl.glDeleteShader(vertexShader);
        gl.glDeleteShader(fragmentShader);

        return new Shader(program);
    }

    public void use(GL3 gl) {
        gl.glUseProgram(programId);
    }

    public int getId() {
        return programId;
    }

    /* ===============================
       Uniform helpers (minimal)
       =============================== */

    public void setInt(GL3 gl, String name, int value) {
        int loc = gl.glGetUniformLocation(programId, name);
        gl.glUniform1i(loc, value);
    }

    public void setFloat(GL3 gl, String name, float value) {
        int loc = gl.glGetUniformLocation(programId, name);
        gl.glUniform1f(loc, value);
    }

    /* ===============================
       Internal helpers
       =============================== */

    private static int compile(GL3 gl, int type, String source) {

        int shader = gl.glCreateShader(type);

        String[] src = new String[]{ source };
        int[] len = new int[]{ source.length() };

        gl.glShaderSource(shader, 1, src, len, 0);
        gl.glCompileShader(shader);

        // --- Compile validation ---
        IntBuffer status = IntBuffer.allocate(1);
        gl.glGetShaderiv(shader, GL3.GL_COMPILE_STATUS, status);

        if (status.get(0) == GL.GL_FALSE) {
            byte[] log = new byte[1024];
            gl.glGetShaderInfoLog(shader, log.length, null, 0, log, 0);
            throw new RuntimeException("Shader compile error:\n" + new String(log));
        }

        return shader;
    }
    
    public static String loadShaderSource(String path) {
        try (InputStream is = Shader.class.getResourceAsStream("/shaders/" + path)) {
            if (is == null) throw new RuntimeException("Shader file not found: " + path);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shader: " + path, e);
        }
    }

    
    public void setMat4(GL3 gl, String name, float[] mat) {
        int loc = gl.glGetUniformLocation(programId, name);
        if (loc == -1) {
            System.err.println("Uniform " + name + " not found in shader");
            return;
        }
        FloatBuffer buffer = FloatBuffer.wrap(mat);
        gl.glUniformMatrix4fv(loc, 1, false, buffer);
    }

	public void dispose(GL3 gl) {
        gl.glDeleteProgram(programId);
	}
}

