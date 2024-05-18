package org.joml.sampling;


class Random {
	private final Random.Xorshiro128 rnd;

	Random(long long1) {
		this.rnd = new Random.Xorshiro128(long1);
	}

	float nextFloat() {
		return this.rnd.nextFloat();
	}

	int nextInt(int int1) {
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
			this._s0 = this.nextSplitMix64();
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
			this._s0 = Long.rotateLeft(long1, 55) ^ long2 ^ long2 << 14;
			this._s1 = Long.rotateLeft(long2, 36);
			return (int)(long3 & -1L);
		}

		final int nextInt(int int1) {
			long long1 = (long)(this.nextInt() >>> 1);
			long1 = long1 * (long)int1 >> 31;
			return (int)long1;
		}
	}
}
