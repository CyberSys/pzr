package org.joml.sampling;


public class UniformSampling {

	public static class Sphere {
		private final Random rnd;

		public Sphere(long long1, int int1, Callback3d callback3d) {
			this.rnd = new Random(long1);
			this.generate(int1, callback3d);
		}

		public void generate(int int1, Callback3d callback3d) {
			int int2 = 0;
			while (int2 < int1) {
				float float1 = this.rnd.nextFloat() * 2.0F - 1.0F;
				float float2 = this.rnd.nextFloat() * 2.0F - 1.0F;
				if (!(float1 * float1 + float2 * float2 >= 1.0F)) {
					float float3 = (float)Math.sqrt(1.0 - (double)(float1 * float1) - (double)(float2 * float2));
					float float4 = 2.0F * float1 * float3;
					float float5 = 2.0F * float2 * float3;
					float float6 = 1.0F - 2.0F * (float1 * float1 + float2 * float2);
					callback3d.onNewSample(float4, float5, float6);
					++int2;
				}
			}
		}
	}

	public static class Disk {
		private final Random rnd;

		public Disk(long long1, int int1, Callback2d callback2d) {
			this.rnd = new Random(long1);
			this.generate(int1, callback2d);
		}

		private void generate(int int1, Callback2d callback2d) {
			for (int int2 = 0; int2 < int1; ++int2) {
				float float1 = this.rnd.nextFloat();
				float float2 = this.rnd.nextFloat() * 2.0F * 3.1415927F;
				float float3 = (float)Math.sqrt((double)float1);
				float float4 = float3 * (float)Math.sin_roquen_9((double)float2 + 1.5707963267948966);
				float float5 = float3 * (float)Math.sin_roquen_9((double)float2);
				callback2d.onNewSample(float4, float5);
			}
		}
	}
}
