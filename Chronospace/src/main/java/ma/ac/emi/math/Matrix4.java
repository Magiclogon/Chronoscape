package ma.ac.emi.math;

import java.util.Arrays;

public class Matrix4 {
    public static void identity(float[] m) {
        Arrays.fill(m, 0);
        m[0] = m[5] = m[10] = m[15] = 1;
    }


    public static void rotateZ(float[] m, float radians) {
        float cos = (float)Math.cos(radians);
        float sin = (float)Math.sin(radians);
        float m0 = m[0]*cos + m[4]*sin;
        float m4 = -m[0]*sin + m[4]*cos;
        float m1 = m[1]*cos + m[5]*sin;
        float m5 = -m[1]*sin + m[5]*cos;
        m[0] = m0; m[4] = m4;
        m[1] = m1; m[5] = m5;
    }

    public static void scale(float[] m, float sx, float sy, float sz) {
        m[0] *= sx; m[1] *= sx; m[2] *= sx; m[3] *= sx;
        m[4] *= sy; m[5] *= sy; m[6] *= sy; m[7] *= sy;
        m[8] *= sz; m[9] *= sz; m[10] *= sz; m[11] *= sz;
    }

    public static void translate(float[] m, float x, float y, float z) {
        m[12] += m[0] * x + m[4] * y + m[8] * z;
        m[13] += m[1] * x + m[5] * y + m[9] * z;
        m[14] += m[2] * x + m[6] * y + m[10] * z;
        m[15] += m[3] * x + m[7] * y + m[11] * z;
    }
        
    public static float[] multMatrixVector(float[] m, float[] v) {
    	float[] r = new float[4];
    	r[0] = m[0]*v[0] + m[4]*v[1] + m[8]*v[2] + m[12]*v[3];
    	r[1] = m[1]*v[0] + m[5]*v[1] + m[9]*v[2] + m[13]*v[3];
    	r[2] = m[2]*v[0] + m[6]*v[1] + m[10]*v[2] + m[14]*v[3];
    	r[3] = m[3]*v[0] + m[7]*v[1] + m[11]*v[2] + m[15]*v[3];
    	
    	return r;
    }
    
    public static float[] multiply(float[] a, float[] b) {
        float[] result = new float[16];
        
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result[row + col * 4] = 
                    a[row + 0 * 4] * b[0 + col * 4] +
                    a[row + 1 * 4] * b[1 + col * 4] +
                    a[row + 2 * 4] * b[2 + col * 4] +
                    a[row + 3 * 4] * b[3 + col * 4];
            }
        }
        
        return result;
    }
    
    public static void printMatrix(String name, float[] m) {
        if (m == null || m.length != 16) {
            System.out.println(name + ": invalid matrix");
            return;
        }

        System.out.println("Matrix " + name + ":");
        // OpenGL uses column-major order, so print as rows
        for (int row = 0; row < 4; row++) {
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col < 4; col++) {
                sb.append(String.format("%8.3f", m[col * 4 + row])).append(" ");
            }
            System.out.println(sb.toString());
        }
        System.out.println();
    }

}
