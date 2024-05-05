package org.joml;


public class Random {
	private final Random.Xorshiro128 rnd;
	private static long seedHalf = 8020463840L;

	public static long newSeed() {
		Class javaClass = Random.class;
		synchronized (Random.class) {
			long long1 = seedHalf;
			long long2 = long1 * 3512401965023503517L;
			seedHalf = long2;
			return long2;
		}
	}

	public Random() {
		this(newSeed() ^ System.nanoTime());
	}

	public Random(long long1) {
		this.rnd = new Random.Xorshiro128(long1);
	}

	public float nextFloat() {
		return this.rnd.nextFloat();
	}

	public int nextInt(int int1) {
		return this.rnd.nextInt(int1);
	}

	private static final class Xorshiro128 {
		private static final float INT_TO_FLOAT = Float.intBitsToFloat(864026624);
		private long _s0;
		private long _s1;
		private long state;

		Xorshiro128(long long1) {
			this.state = long1;
			this._s0 = this.nextSplitMix64();
			this._s1 = this.nextSplitMix64();
		}

		private long nextSplitMix64() {
			long long1 = this.state += -7046029254386353131L;
			long1 = (long1 ^ long1 >>> 30) * -4658895280553007687L;
			long1 = (long1 ^ long1 >>> 27) * -7723592293110705685L;
			return long1 ^ long1 >>> 31;
		}

		final float nextFloat() {
			return (float)(this.nextInt() >>> 8) * INT_TO_FLOAT;
		}

		private int nextInt() {
			long long1 = this._s0;
			long long2 = this._s1;
			long long3 = long1 + long2;
			long2 ^= long1;
			this.rotateLeft(long1, long2);
			return (int)(long3 & -1L);
		}

		private static long rotl_JDK4(long long1, int int1) {
			return long1 << int1 | long1 >>> 64 - int1;
		}

		private static long rotl_JDK5(long long1, int int1) {
			return Long.rotateLeft(long1, int1);
		}

		private static long rotl(long long1, int int1) {
			return Runtime.HAS_Long_rotateLeft ? rotl_JDK5(long1, int1) : rotl_JDK4(long1, int1);
		}

		private void rotateLeft(long long1, long long2) {
			this._s0 = rotl(long1, 55) ^ long2 ^ long2 << 14;
			this._s1 = rotl(long2, 36);
		}

		final int nextInt(int int1) {
			long long1 = (long)(this.nextInt() >>> 1);
			long1 = long1 * (long)int1 >> 31;
			return (int)long1;
		}
	}
}
