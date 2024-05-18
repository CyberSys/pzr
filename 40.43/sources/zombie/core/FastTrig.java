package zombie.core;


public class FastTrig {

	public static double cos(double double1) {
		return sin(double1 + 1.5707963267948966);
	}

	public static double sin(double double1) {
		double1 = reduceSinAngle(double1);
		return Math.abs(double1) <= 0.7853981633974483 ? Math.sin(double1) : Math.cos(1.5707963267948966 - double1);
	}

	private static double reduceSinAngle(double double1) {
		double1 %= 6.283185307179586;
		if (Math.abs(double1) > 3.141592653589793) {
			double1 -= 6.283185307179586;
		}

		if (Math.abs(double1) > 1.5707963267948966) {
			double1 = 3.141592653589793 - double1;
		}

		return double1;
	}
}
