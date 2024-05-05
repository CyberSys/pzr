package javax.vecmath;


class VecMathUtil {

	static int floatToIntBits(float float1) {
		return float1 == 0.0F ? 0 : Float.floatToIntBits(float1);
	}

	static long doubleToLongBits(double double1) {
		return double1 == 0.0 ? 0L : Double.doubleToLongBits(double1);
	}

	private VecMathUtil() {
	}
}
