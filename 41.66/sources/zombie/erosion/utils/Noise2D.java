package zombie.erosion.utils;

import java.util.ArrayList;


public class Noise2D {
	private ArrayList layers = new ArrayList(3);
	private static final int[] perm = new int[]{151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};

	private float lerp(float float1, float float2, float float3) {
		return float2 + float1 * (float3 - float2);
	}

	private float fade(float float1) {
		return float1 * float1 * float1 * (float1 * (float1 * 6.0F - 15.0F) + 10.0F);
	}

	private float noise(float float1, float float2, int[] intArray) {
		int int1 = (int)Math.floor((double)float1 - Math.floor((double)(float1 / 255.0F)) * 255.0);
		int int2 = (int)Math.floor((double)float2 - Math.floor((double)(float2 / 255.0F)) * 255.0);
		float float3 = this.fade(float1 - (float)Math.floor((double)float1));
		float float4 = this.fade(float2 - (float)Math.floor((double)float2));
		int int3 = intArray[int1] + int2;
		int int4 = intArray[int1] + int2 + 1;
		int int5 = intArray[int1 + 1] + int2;
		int int6 = intArray[int1 + 1] + int2 + 1;
		return this.lerp(float4, this.lerp(float3, (float)perm[intArray[int3]], (float)perm[intArray[int5]]), this.lerp(float3, (float)perm[intArray[int4]], (float)perm[intArray[int6]]));
	}

	public float layeredNoise(float float1, float float2) {
		float float3 = 0.0F;
		float float4 = 0.0F;
		for (int int1 = 0; int1 < this.layers.size(); ++int1) {
			Noise2D.Layer layer = (Noise2D.Layer)this.layers.get(int1);
			float4 += layer.amp;
			float3 += this.noise(float1 * layer.freq, float2 * layer.freq, layer.p) * layer.amp;
		}

		return float3 / float4 / 255.0F;
	}

	public void addLayer(int int1, float float1, float float2) {
		int int2 = (int)Math.floor((double)int1 - Math.floor((double)((float)int1 / 256.0F)) * 256.0);
		Noise2D.Layer layer = new Noise2D.Layer();
		layer.freq = float1;
		layer.amp = float2;
		for (int int3 = 0; int3 < 256; ++int3) {
			int int4 = (int)Math.floor((double)(int2 + int3) - Math.floor((double)((float)(int2 + int3) / 256.0F)) * 256.0);
			layer.p[int4] = perm[int3];
			layer.p[256 + int4] = layer.p[int4];
		}

		this.layers.add(layer);
	}

	public void reset() {
		if (this.layers.size() > 0) {
			this.layers.clear();
		}
	}

	private class Layer {
		public float freq;
		public float amp;
		public int[] p = new int[512];
	}
}
