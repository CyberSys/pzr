package zombie.core.utils;

import zombie.GameTime;
import zombie.core.Rand;


public final class OnceEvery {
	private long initialDelayMillis;
	private long triggerIntervalMillis;
	private static float milliFraction = 0.0F;
	private static long currentMillis = 0L;
	private static long prevMillis = 0L;

	public OnceEvery(float float1) {
		this(float1, false);
	}

	public OnceEvery(float float1, boolean boolean1) {
		this.initialDelayMillis = 0L;
		this.triggerIntervalMillis = (long)(float1 * 1000.0F);
		this.initialDelayMillis = 0L;
		if (boolean1) {
			this.initialDelayMillis = Rand.Next(this.triggerIntervalMillis);
		}
	}

	public static long getElapsedMillis() {
		return currentMillis;
	}

	public boolean Check() {
		if (currentMillis < this.initialDelayMillis) {
			return false;
		} else if (this.triggerIntervalMillis == 0L) {
			return true;
		} else {
			long long1 = (prevMillis - this.initialDelayMillis) % this.triggerIntervalMillis;
			long long2 = (currentMillis - this.initialDelayMillis) % this.triggerIntervalMillis;
			if (long1 > long2) {
				return true;
			} else {
				long long3 = currentMillis - prevMillis;
				return this.triggerIntervalMillis < long3;
			}
		}
	}

	public static void update() {
		long long1 = currentMillis;
		float float1 = milliFraction;
		float float2 = GameTime.instance.getTimeDelta();
		float float3 = float2 * 1000.0F + float1;
		long long2 = (long)float3;
		float float4 = float3 - (float)long2;
		long long3 = long1 + long2;
		prevMillis = long1;
		currentMillis = long3;
		milliFraction = float4;
	}
}
