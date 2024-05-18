package org.joml.sampling;


public class StratifiedSampling {
	private final Random rnd;

	public StratifiedSampling(long long1) {
		this.rnd = new Random(long1);
	}

	public void generateRandom(int int1, Callback2d callback2d) {
		for (int int2 = 0; int2 < int1; ++int2) {
			for (int int3 = 0; int3 < int1; ++int3) {
				float float1 = (this.rnd.nextFloat() / (float)int1 + (float)int3 / (float)int1) * 2.0F - 1.0F;
				float float2 = (this.rnd.nextFloat() / (float)int1 + (float)int2 / (float)int1) * 2.0F - 1.0F;
				callback2d.onNewSample(float1, float2);
			}
		}
	}

	public void generateCentered(int int1, float float1, Callback2d callback2d) {
		float float2 = float1 * 0.5F;
		float float3 = 1.0F - float1;
		for (int int2 = 0; int2 < int1; ++int2) {
			for (int int3 = 0; int3 < int1; ++int3) {
				float float4 = ((float2 + this.rnd.nextFloat() * float3) / (float)int1 + (float)int3 / (float)int1) * 2.0F - 1.0F;
				float float5 = ((float2 + this.rnd.nextFloat() * float3) / (float)int1 + (float)int2 / (float)int1) * 2.0F - 1.0F;
				callback2d.onNewSample(float4, float5);
			}
		}
	}
}
