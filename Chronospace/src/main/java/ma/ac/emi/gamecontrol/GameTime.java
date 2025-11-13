package ma.ac.emi.gamecontrol;

public class GameTime {
	private static double time = 0;
	
	public static double get() {
		return time;
	}
	
	public static void addTime(double step) {
		time += step;
	}
}
