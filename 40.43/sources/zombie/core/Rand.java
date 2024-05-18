package zombie.core;

import org.uncommons.maths.random.CellularAutomatonRNG;
import org.uncommons.maths.random.SecureRandomSeedGenerator;
import org.uncommons.maths.random.SeedException;
import org.uncommons.maths.random.SeedGenerator;
import zombie.network.GameServer;


public class Rand {
	public static CellularAutomatonRNG rand;
	public static CellularAutomatonRNG randlua;
	public static int id = 0;

	public static void init(int int1) {
		try {
			rand = new CellularAutomatonRNG(new Rand.PZSeedGenerator());
			randlua = new CellularAutomatonRNG(new Rand.PZSeedGenerator());
		} catch (SeedException seedException) {
			seedException.printStackTrace();
		}
	}

	public static void init() {
		try {
			rand = new CellularAutomatonRNG(new Rand.PZSeedGenerator());
			randlua = new CellularAutomatonRNG(new Rand.PZSeedGenerator());
		} catch (SeedException seedException) {
			seedException.printStackTrace();
		}
	}

	public static int Next(int int1, CellularAutomatonRNG cellularAutomatonRNG) {
		if (int1 <= 0) {
			return 0;
		} else {
			++id;
			if (id >= 10000) {
				id = 0;
			}

			return cellularAutomatonRNG.nextInt(int1);
		}
	}

	public static int Next(int int1) {
		return Next(int1, rand);
	}

	public static long Next(long long1, CellularAutomatonRNG cellularAutomatonRNG) {
		if (long1 <= 0L) {
			return 0L;
		} else {
			++id;
			if (id >= 10000) {
				id = 0;
			}

			return (long)cellularAutomatonRNG.nextInt((int)long1);
		}
	}

	public static long Next(long long1) {
		return Next(long1, rand);
	}

	public static int Next(int int1, int int2, CellularAutomatonRNG cellularAutomatonRNG) {
		if (int2 == int1) {
			return int1;
		} else {
			int int3;
			if (int1 > int2) {
				int3 = int1;
				int1 = int2;
				int2 = int3;
			}

			++id;
			if (id >= 10000) {
				id = 0;
			}

			int3 = cellularAutomatonRNG.nextInt(int2 - int1);
			return int3 + int1;
		}
	}

	public static int Next(int int1, int int2) {
		return Next(int1, int2, rand);
	}

	public static long Next(long long1, long long2, CellularAutomatonRNG cellularAutomatonRNG) {
		if (long2 == long1) {
			return long1;
		} else {
			if (long1 > long2) {
				long long3 = long1;
				long1 = long2;
				long2 = long3;
			}

			++id;
			if (id >= 10000) {
				id = 0;
			}

			int int1 = cellularAutomatonRNG.nextInt((int)(long2 - long1));
			return (long)int1 + long1;
		}
	}

	public static long Next(long long1, long long2) {
		return Next(long1, long2, rand);
	}

	public static float Next(float float1, float float2, CellularAutomatonRNG cellularAutomatonRNG) {
		if (float2 == float1) {
			return float1;
		} else {
			if (float1 > float2) {
				float float3 = float1;
				float1 = float2;
				float2 = float3;
			}

			++id;
			if (id >= 10000) {
				id = 0;
			}

			return float1 + cellularAutomatonRNG.nextFloat() * (float2 - float1);
		}
	}

	public static float Next(float float1, float float2) {
		return Next(float1, float2, rand);
	}

	public static int AdjustForFramerate(int int1) {
		if (GameServer.bServer) {
			int1 = (int)((float)int1 * 0.33333334F);
		} else {
			int1 = (int)((float)int1 * ((float)PerformanceSettings.LockFPS / 30.0F));
		}

		return int1;
	}

	public static final class PZSeedGenerator implements SeedGenerator {
		private static final SeedGenerator[] GENERATORS = new SeedGenerator[]{new SecureRandomSeedGenerator()};

		private PZSeedGenerator() {
		}

		public byte[] generateSeed(int int1) {
			SeedGenerator[] seedGeneratorArray = GENERATORS;
			int int2 = seedGeneratorArray.length;
			int int3 = 0;
			while (int3 < int2) {
				SeedGenerator seedGenerator = seedGeneratorArray[int3];
				try {
					byte[] byteArray = seedGenerator.generateSeed(int1);
					return byteArray;
				} catch (SeedException seedException) {
					++int3;
				}
			}

			throw new IllegalStateException("All available seed generation strategies failed.");
		}

		PZSeedGenerator(Object object) {
			this();
		}
	}
}
