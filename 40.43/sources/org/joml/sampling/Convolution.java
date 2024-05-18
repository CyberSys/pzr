package org.joml.sampling;

import java.nio.FloatBuffer;


public class Convolution {

	public static void gaussianKernel(int int1, int int2, float float1, FloatBuffer floatBuffer) {
		if ((int1 & 1) == 0) {
			throw new IllegalArgumentException("rows must be an odd number");
		} else if ((int2 & 1) == 0) {
			throw new IllegalArgumentException("cols must be an odd number");
		} else if (floatBuffer == null) {
			throw new IllegalArgumentException("dest must not be null");
		} else if (floatBuffer.remaining() < int1 * int2) {
			throw new IllegalArgumentException("dest must have at least " + int1 * int2 + " remaining values");
		} else {
			float float2 = 0.0F;
			int int3 = floatBuffer.position();
			int int4 = 0;
			for (int int5 = -(int1 - 1) / 2; int5 <= (int1 - 1) / 2; ++int5) {
				for (int int6 = -(int2 - 1) / 2; int6 <= (int2 - 1) / 2; ++int4) {
					float float3 = (float)org.joml.Math.exp((double)(-(int5 * int5 + int6 * int6)) / (2.0 * (double)float1 * (double)float1));
					floatBuffer.put(int3 + int4, float3);
					float2 += float3;
					++int6;
				}
			}

			for (int4 = 0; int4 < int1 * int2; ++int4) {
				floatBuffer.put(int3 + int4, floatBuffer.get(int3 + int4) / float2);
			}
		}
	}
}
