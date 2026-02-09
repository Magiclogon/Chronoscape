package ma.ac.emi.glgraphics;

import java.awt.geom.AffineTransform;

public class Mat4 {

    public static float[] identity() {
        return new float[]{
            1,0,0,0,
            0,1,0,0,
            0,0,1,0,
            0,0,0,1
        };
    }

    public static float[] ortho(float l, float r, float b, float t) {
        return new float[]{
            2f/(r-l), 0, 0, 0,
            0, 2f/(t-b), 0, 0,
            0, 0, -1, 0,
            -(r+l)/(r-l), -(t+b)/(t-b), 0, 1
        };
    }

    public static float[] transform(float x, float y, float w, float h) {
        return new float[]{
            w,0,0,0,
            0,h,0,0,
            0,0,1,0,
            x,y,0,1
        };
    }
    
    public static AffineTransform toAffineTransform(float[] matrix) {
  
        return new AffineTransform(
            matrix[0],  // m00 (scaleX/cosine)
            matrix[1],  // m10 (shearY/sine)
            matrix[4],  // m01 (shearX/-sine)
            matrix[5],  // m11 (scaleY/cosine)
            matrix[12], // m02 (translateX)
            matrix[13]  // m12 (translateY)
        );
    }
}
