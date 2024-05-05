package zombie.util;


public final class LocationRNG {
	public static final LocationRNG instance = new LocationRNG();
	private static final float INT_TO_FLOAT = Float.intBitsToFloat(864026624);
	private long _s0;
	private long _s1;
	private long state;

	public void setSeed(long long1) {
		this.state = long1;
		this._s0 = this.nextSplitMix64();
		this._s1 = this.nextSplitMix64();
	}

	public long getSeed() {
		return this.state;
	}

	private long nextSplitMix64() {
		long long1 = this.state += -7046029254386353131L;
		long1 = (long1 ^ long1 >>> 30) * -4658895280553007687L;
		long1 = (long1 ^ long1 >>> 27) * -7723592293110705685L;
		return long1 ^ long1 >>> 31;
	}

	public float nextFloat() {
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

	public int nextInt(int int1) {
		long long1 = (long)(this.nextInt() >>> 1);
		long1 = long1 * (long)int1 >> 31;
		return (int)long1;
	}

	public int nextInt(int int1, int int2, int int3, int int4) {
		this.setSeed((long)int4 << 16 | (long)int3 << 32 | (long)int2);
		return this.nextInt(int1);
	}
}
