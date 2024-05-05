package zombie;

import zombie.core.PerformanceSettings;


public final class FPSTracking {
	private final double[] lastFPS = new double[20];
	private int lastFPSCount = 0;
	private long timeAtLastUpdate;
	private final long[] last10 = new long[10];
	private int last10index = 0;

	public void init() {
		for (int int1 = 0; int1 < 20; ++int1) {
			this.lastFPS[int1] = (double)PerformanceSettings.getLockFPS();
		}

		this.timeAtLastUpdate = System.nanoTime();
	}

	public long frameStep() {
		long long1 = System.nanoTime();
		long long2 = long1 - this.timeAtLastUpdate;
		if (long2 > 0L) {
			float float1 = 0.0F;
			double double1 = (double)long2 / 1.0E9;
			double double2 = 1.0 / double1;
			this.lastFPS[this.lastFPSCount] = double2;
			++this.lastFPSCount;
			if (this.lastFPSCount >= 5) {
				this.lastFPSCount = 0;
			}

			for (int int1 = 0; int1 < 5; ++int1) {
				float1 = (float)((double)float1 + this.lastFPS[int1]);
			}

			float1 /= 5.0F;
			GameWindow.averageFPS = float1;
			GameTime.instance.FPSMultiplier = (float)(60.0 / double2);
			if (GameTime.instance.FPSMultiplier > 5.0F) {
				GameTime.instance.FPSMultiplier = 5.0F;
			}
		}

		this.timeAtLastUpdate = long1;
		this.updateFPS(long2);
		return long2;
	}

	public void updateFPS(long long1) {
		this.last10[this.last10index++] = long1;
		if (this.last10index >= this.last10.length) {
			this.last10index = 0;
		}

		float float1 = 11110.0F;
		float float2 = -11110.0F;
		long[] longArray = this.last10;
		int int1 = longArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			long long2 = longArray[int2];
			if (long2 != 0L) {
				if ((float)long2 < float1) {
					float1 = (float)long2;
				}

				if ((float)long2 > float2) {
					float2 = (float)long2;
				}
			}
		}
	}
}
