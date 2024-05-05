package zombie.core.skinnedmodel.population;

import java.util.List;
import zombie.core.Color;
import zombie.core.ImmutableColor;
import zombie.util.LocationRNG;


public final class OutfitRNG {
	private static final ThreadLocal RNG = ThreadLocal.withInitial(LocationRNG::new);

	public static void setSeed(long long1) {
		((LocationRNG)RNG.get()).setSeed(long1);
	}

	public static long getSeed() {
		return ((LocationRNG)RNG.get()).getSeed();
	}

	public static int Next(int int1) {
		return ((LocationRNG)RNG.get()).nextInt(int1);
	}

	public static int Next(int int1, int int2) {
		if (int2 == int1) {
			return int1;
		} else {
			int int3;
			if (int1 > int2) {
				int3 = int1;
				int1 = int2;
				int2 = int3;
			}

			int3 = ((LocationRNG)RNG.get()).nextInt(int2 - int1);
			return int3 + int1;
		}
	}

	public static float Next(float float1, float float2) {
		if (float2 == float1) {
			return float1;
		} else {
			if (float1 > float2) {
				float float3 = float1;
				float1 = float2;
				float2 = float3;
			}

			return float1 + ((LocationRNG)RNG.get()).nextFloat() * (float2 - float1);
		}
	}

	public static boolean NextBool(int int1) {
		return Next(int1) == 0;
	}

	public static Object pickRandom(List list) {
		if (list.isEmpty()) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			int int1 = Next(list.size());
			return list.get(int1);
		}
	}

	public static ImmutableColor randomImmutableColor() {
		float float1 = Next(0.0F, 1.0F);
		float float2 = Next(0.0F, 0.6F);
		float float3 = Next(0.0F, 0.9F);
		Color color = Color.HSBtoRGB(float1, float2, float3);
		return new ImmutableColor(color);
	}
}
