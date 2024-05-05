package zombie.core;


public class BoxedStaticValues {
	static Double[] doubles = new Double[10000];
	static Double[] negdoubles = new Double[10000];
	static Double[] doublesh = new Double[10000];
	static Double[] negdoublesh = new Double[10000];

	public static Double toDouble(double double1) {
		if (double1 >= 10000.0) {
			return double1;
		} else if (double1 <= -10000.0) {
			return double1;
		} else if ((double)((int)Math.abs(double1)) == Math.abs(double1)) {
			return double1 < 0.0 ? negdoubles[(int)(-double1)] : doubles[(int)double1];
		} else if ((double)((int)Math.abs(double1)) == Math.abs(double1) - 0.5) {
			return double1 < 0.0 ? negdoublesh[(int)(-double1)] : doublesh[(int)double1];
		} else {
			return double1;
		}
	}

	static  {
	for (int var0 = 0; var0 < 10000; ++var0) {
		doubles[var0] = (double)var0;
		negdoubles[var0] = -doubles[var0];
		doublesh[var0] = (double)var0 + 0.5;
		negdoublesh[var0] = -(doubles[var0] + 0.5);
	}
	}
}
