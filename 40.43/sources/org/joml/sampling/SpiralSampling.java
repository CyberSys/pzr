package org.joml.sampling;


public class SpiralSampling {
	private final Random rnd;

	public SpiralSampling(long long1) {
		this.rnd = new Random(long1);
	}

	public void createEquiAngle(float float1, int int1, int int2, Callback2d callback2d) {
		for (int int3 = 0; int3 < int2; ++int3) {
			float float2 = 6.2831855F * (float)(int3 * int1) / (float)int2;
			float float3 = float1 * (float)int3 / (float)(int2 - 1);
			float float4 = (float)Math.sin_roquen_9((double)(float2 + 1.5707964F)) * float3;
			float float5 = (float)Math.sin_roquen_9((double)float2) * float3;
			callback2d.onNewSample(float4, float5);
		}
	}

	public void createEquiAngle(float float1, int int1, int int2, float float2, Callback2d callback2d) {
		float float3 = float1 / (float)int1;
		for (int int3 = 0; int3 < int2; ++int3) {
			float float4 = 6.2831855F * (float)(int3 * int1) / (float)int2;
			float float5 = float1 * (float)int3 / (float)(int2 - 1) + (this.rnd.nextFloat() * 2.0F - 1.0F) * float3 * float2;
			float float6 = (float)Math.sin_roquen_9((double)(float4 + 1.5707964F)) * float5;
			float float7 = (float)Math.sin_roquen_9((double)float4) * float5;
			callback2d.onNewSample(float6, float7);
		}
	}
}
