package ma.ac.emi.math;

public class Matrix2x2 {
	private double[][] values;
	
	public Matrix2x2(double a00, double a01, double a10, double a11) {
		values = new double[2][2];
		values[0][0] = a00; values[0][1] = a01; values[1][0] = a10; values[1][1] = a11;
	}
	
	public Matrix2x2() {
		this(0, 0, 0, 0);
	}
	
	public double[][] getValues(){
		return values;
	}
	
	public void setValues(double[][] values) {
		this.values = values;
	}
	
	public static Matrix2x2 add(Matrix2x2 m1, Matrix2x2 m2) {
		Matrix2x2 m = new Matrix2x2();
		double[][] values = new double[2][2];
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				values[i][j] = m1.getValues()[i][j] + m2.getValues()[i][j];
			}
		}
		m.setValues(values);
		return m;
	}
	
	public static Matrix2x2 mult(Matrix2x2 m1, Matrix2x2 m2) {
		Matrix2x2 m = new Matrix2x2();
		double[][] values = new double[2][2];
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				for(int k = 0; k < 2; k++) {
					values[i][j] += m1.getValues()[i][k] * m2.getValues()[k][j];
				}
			}
		}
		m.setValues(values);
		return m;
	}
	
	public static Matrix2x2 mult(Matrix2x2 m, double a) {
		Matrix2x2 new_m = new Matrix2x2();
		double[][] values = new double[2][2];
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				values[i][j] *= a;
			}
		}
		new_m.setValues(values);
		return new_m;
	}
}
