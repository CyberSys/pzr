package zombie.iso.objects;

import fmod.fmod.Audio;
import java.util.ArrayList;
import java.util.Stack;
import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameServer;


public class RainManager {
	public static boolean IsRaining = false;
	public static int NumActiveRainSplashes = 0;
	public static int NumActiveRaindrops = 0;
	public static int MaxRainSplashObjects = 500;
	public static int MaxRaindropObjects = 500;
	public static float RainSplashAnimDelay = 0.2F;
	public static int AddNewSplashesDelay = 30;
	public static int AddNewSplashesTimer;
	public static float RaindropGravity;
	public static float GravModMin;
	public static float GravModMax;
	public static float RaindropStartDistance;
	public static IsoGridSquare[] PlayerLocation;
	public static IsoGridSquare[] PlayerOldLocation;
	public static boolean PlayerMoved;
	public static int RainRadius;
	public static Audio RainAmbient;
	public static Audio ThunderAmbient;
	public static ColorInfo RainSplashTintMod;
	public static ColorInfo RaindropTintMod;
	public static ColorInfo DarkRaindropTintMod;
	public static ArrayList RainSplashStack;
	public static ArrayList RaindropStack;
	public static Stack RainSplashReuseStack;
	public static Stack RaindropReuseStack;
	private static float RainChangeTimer;
	private static float RainChangeRate;
	private static float RainChangeRateMin;
	private static float RainChangeRateMax;
	public static float RainIntensity;
	public static float RainDesiredIntensity;
	private static int randRain;
	public static int randRainMin;
	public static int randRainMax;
	private static boolean stopRain;
	static Audio OutsideAmbient;
	static Audio OutsideNightAmbient;
	static ColorInfo AdjustedRainSplashTintMod;

	public static void reset() {
		RainSplashStack.clear();
		RaindropStack.clear();
		RaindropReuseStack.clear();
		RainSplashReuseStack.clear();
		NumActiveRainSplashes = 0;
		NumActiveRaindrops = 0;
		for (int int1 = 0; int1 < 4; ++int1) {
			PlayerLocation[int1] = null;
			PlayerOldLocation[int1] = null;
		}

		RainAmbient = null;
		ThunderAmbient = null;
		IsRaining = false;
		stopRain = false;
	}

	public static void AddRaindrop(IsoRaindrop raindrop) {
		if (NumActiveRaindrops < MaxRaindropObjects) {
			RaindropStack.add(raindrop);
			++NumActiveRaindrops;
		} else {
			IsoRaindrop raindrop2 = null;
			int int1 = -1;
			for (int int2 = 0; int2 < RaindropStack.size(); ++int2) {
				if (((IsoRaindrop)RaindropStack.get(int2)).Life > int1) {
					int1 = ((IsoRaindrop)RaindropStack.get(int2)).Life;
					raindrop2 = (IsoRaindrop)RaindropStack.get(int2);
				}
			}

			if (raindrop2 != null) {
				RemoveRaindrop(raindrop2);
				RaindropStack.add(raindrop);
				++NumActiveRaindrops;
			}
		}
	}

	public static void AddRainSplash(IsoRainSplash rainSplash) {
		if (NumActiveRainSplashes < MaxRainSplashObjects) {
			RainSplashStack.add(rainSplash);
			++NumActiveRainSplashes;
		} else {
			IsoRainSplash rainSplash2 = null;
			int int1 = -1;
			for (int int2 = 0; int2 < RainSplashStack.size(); ++int2) {
				if (((IsoRainSplash)RainSplashStack.get(int2)).Age > int1) {
					int1 = ((IsoRainSplash)RainSplashStack.get(int2)).Age;
					rainSplash2 = (IsoRainSplash)RainSplashStack.get(int2);
				}
			}

			RemoveRainSplash(rainSplash2);
			RainSplashStack.add(rainSplash);
			++NumActiveRainSplashes;
		}
	}

	public static void AddSplashes() {
		if (AddNewSplashesTimer > 0) {
			--AddNewSplashesTimer;
		} else {
			AddNewSplashesTimer = (int)((float)AddNewSplashesDelay * ((float)PerformanceSettings.LockFPS / 30.0F));
			IsoGridSquare square = null;
			int int1;
			IsoRainSplash rainSplash;
			IsoRaindrop raindrop;
			if (!stopRain) {
				if (PlayerMoved) {
					for (int1 = RainSplashStack.size() - 1; int1 >= 0; --int1) {
						rainSplash = (IsoRainSplash)RainSplashStack.get(int1);
						if (!inBounds(rainSplash.square)) {
							RemoveRainSplash(rainSplash);
						}
					}

					for (int1 = RaindropStack.size() - 1; int1 >= 0; --int1) {
						raindrop = (IsoRaindrop)RaindropStack.get(int1);
						if (!inBounds(raindrop.square)) {
							RemoveRaindrop(raindrop);
						}
					}
				}

				int1 = 0;
				int int2;
				for (int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
					if (IsoPlayer.players[int2] != null) {
						++int1;
					}
				}

				int2 = RainRadius * 2 * RainRadius * 2;
				int int3 = int2 / (randRain + 1);
				int3 = Math.min(MaxRainSplashObjects, int3);
				while (NumActiveRainSplashes > int3 * int1) {
					RemoveRainSplash((IsoRainSplash)RainSplashStack.get(0));
				}

				while (NumActiveRaindrops > int3 * int1) {
					RemoveRaindrop((IsoRaindrop)RaindropStack.get(0));
				}

				IsoCell cell = IsoWorld.instance.CurrentCell;
				for (int int4 = 0; int4 < IsoPlayer.numPlayers; ++int4) {
					if (IsoPlayer.players[int4] != null && PlayerLocation[int4] != null) {
						for (int int5 = 0; int5 < int3; ++int5) {
							int int6 = Rand.Next(-RainRadius, RainRadius);
							int int7 = Rand.Next(-RainRadius, RainRadius);
							square = cell.getGridSquare(PlayerLocation[int4].getX() + int6, PlayerLocation[int4].getY() + int7, 0);
							if (square != null && square.isSeen(int4) && !square.getProperties().Is(IsoFlagType.vegitation) && square.getProperties().Is(IsoFlagType.exterior)) {
								StartRainSplash(cell, square, true);
							}
						}
					}
				}
			}

			PlayerMoved = false;
			if (!stopRain) {
				--randRain;
				if (randRain < randRainMin) {
					randRain = randRainMin;
				}
			} else {
				randRain = (int)((float)randRain - 1.0F * GameTime.instance.getMultiplier());
				if (randRain < randRainMin) {
					removeAll();
					randRain = randRainMin;
				} else {
					for (int1 = RainSplashStack.size() - 1; int1 >= 0; --int1) {
						if (Rand.Next(randRain) == 0) {
							rainSplash = (IsoRainSplash)RainSplashStack.get(int1);
							RemoveRainSplash(rainSplash);
						}
					}

					for (int1 = RaindropStack.size() - 1; int1 >= 0; --int1) {
						if (Rand.Next(randRain) == 0) {
							raindrop = (IsoRaindrop)RaindropStack.get(int1);
							RemoveRaindrop(raindrop);
						}
					}
				}
			}
		}
	}

	public static void RemoveRaindrop(IsoRaindrop raindrop) {
		if (raindrop.square != null) {
			raindrop.square.getProperties().UnSet(IsoFlagType.HasRaindrop);
			raindrop.square.setRainDrop((IsoRaindrop)null);
			raindrop.square = null;
		}

		RaindropStack.remove(raindrop);
		--NumActiveRaindrops;
		RaindropReuseStack.push(raindrop);
	}

	public static void RemoveRainSplash(IsoRainSplash rainSplash) {
		if (rainSplash.square != null) {
			rainSplash.square.getProperties().UnSet(IsoFlagType.HasRainSplashes);
			rainSplash.square.setRainSplash((IsoRainSplash)null);
			rainSplash.square = null;
		}

		RainSplashStack.remove(rainSplash);
		--NumActiveRainSplashes;
		RainSplashReuseStack.push(rainSplash);
	}

	public static void SetPlayerLocation(int int1, IsoGridSquare square) {
		PlayerOldLocation[int1] = PlayerLocation[int1];
		PlayerLocation[int1] = square;
		if (PlayerOldLocation[int1] != PlayerLocation[int1]) {
			PlayerMoved = true;
		}
	}

	public static Boolean isRaining() {
		return ClimateManager.getInstance().isRaining();
	}

	public static void stopRaining() {
		stopRain = true;
		randRain = randRainMax;
		RainDesiredIntensity = 0.0F;
		if (GameServer.bServer) {
			GameServer.stopRain();
		}

		LuaEventManager.triggerEvent("OnRainStop");
	}

	public static void startRaining() {
	}

	public static void StartRaindrop(IsoCell cell, IsoGridSquare square, boolean boolean1) {
		if (!square.getProperties().Is(IsoFlagType.HasRaindrop)) {
			IsoRaindrop raindrop = null;
			if (!RaindropReuseStack.isEmpty()) {
				if (boolean1) {
					if (square.getRainDrop() != null) {
						return;
					}

					raindrop = (IsoRaindrop)RaindropReuseStack.pop();
					raindrop.Reset(square, boolean1);
					square.setRainDrop(raindrop);
				}
			} else if (boolean1) {
				if (square.getRainDrop() != null) {
					return;
				}

				raindrop = new IsoRaindrop(cell, square, boolean1);
				square.setRainDrop(raindrop);
			}
		}
	}

	public static void StartRainSplash(IsoCell cell, IsoGridSquare square, boolean boolean1) {
	}

	public static void Update() {
		IsRaining = ClimateManager.getInstance().isRaining();
		RainIntensity = IsRaining ? ClimateManager.getInstance().getPrecipitationIntensity() : 0.0F;
		if (IsoPlayer.getInstance() != null) {
			if (IsoPlayer.getInstance().getCurrentSquare() != null) {
				if (!GameServer.bServer) {
					AddSplashes();
				}
			}
		}
	}

	public static void UpdateServer() {
	}

	public static void setRandRainMax(int int1) {
		randRainMax = int1;
		randRain = randRainMax;
	}

	public static void setRandRainMin(int int1) {
		randRainMin = int1;
	}

	public static boolean inBounds(IsoGridSquare square) {
		if (square == null) {
			return false;
		} else {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && PlayerLocation[int1] != null) {
					if (square.getX() < PlayerLocation[int1].getX() - RainRadius || square.getX() >= PlayerLocation[int1].getX() + RainRadius) {
						return true;
					}

					if (square.getY() < PlayerLocation[int1].getY() - RainRadius || square.getY() >= PlayerLocation[int1].getY() + RainRadius) {
						return true;
					}
				}
			}

			return false;
		}
	}

	public static void RemoveAllOn(IsoGridSquare square) {
		if (square.getRainDrop() != null) {
			RemoveRaindrop(square.getRainDrop());
		}

		if (square.getRainSplash() != null) {
			RemoveRainSplash(square.getRainSplash());
		}
	}

	public static float getRainIntensity() {
		return ClimateManager.getInstance().getPrecipitationIntensity();
	}

	private static void removeAll() {
		int int1;
		for (int1 = RainSplashStack.size() - 1; int1 >= 0; --int1) {
			IsoRainSplash rainSplash = (IsoRainSplash)RainSplashStack.get(int1);
			RemoveRainSplash(rainSplash);
		}

		for (int1 = RaindropStack.size() - 1; int1 >= 0; --int1) {
			IsoRaindrop raindrop = (IsoRaindrop)RaindropStack.get(int1);
			RemoveRaindrop(raindrop);
		}

		RaindropStack.clear();
		RainSplashStack.clear();
		NumActiveRainSplashes = 0;
		NumActiveRaindrops = 0;
	}

	private static boolean interruptSleep(IsoPlayer player) {
		if (player.isAsleep() && player.isOutside() && player.getBed() != null && !player.getBed().getName().equals("Tent")) {
			IsoObject object = player.getBed();
			if (object.getCell().getGridSquare((double)object.getX(), (double)object.getY(), (double)(object.getZ() + 1.0F)) == null || object.getCell().getGridSquare((double)object.getX(), (double)object.getY(), (double)(object.getZ() + 1.0F)).getFloor() == null) {
				return true;
			}
		}

		return false;
	}

	static  {
		AddNewSplashesTimer = AddNewSplashesDelay;
		RaindropGravity = 0.065F;
		GravModMin = 0.28F;
		GravModMax = 0.5F;
		RaindropStartDistance = 850.0F;
		PlayerLocation = new IsoGridSquare[4];
		PlayerOldLocation = new IsoGridSquare[4];
		PlayerMoved = true;
		RainRadius = 18;
		ThunderAmbient = null;
		RainSplashTintMod = new ColorInfo(0.8F, 0.9F, 1.0F, 0.3F);
		RaindropTintMod = new ColorInfo(0.8F, 0.9F, 1.0F, 0.3F);
		DarkRaindropTintMod = new ColorInfo(0.8F, 0.9F, 1.0F, 0.3F);
		RainSplashStack = new ArrayList(1600);
		RaindropStack = new ArrayList(1600);
		RainSplashReuseStack = new Stack();
		RaindropReuseStack = new Stack();
		RainChangeTimer = 1.0F;
		RainChangeRate = 0.01F;
		RainChangeRateMin = 0.006F;
		RainChangeRateMax = 0.01F;
		RainIntensity = 1.0F;
		RainDesiredIntensity = 1.0F;
		randRain = 0;
		randRainMin = 0;
		randRainMax = 0;
		stopRain = false;
		OutsideAmbient = null;
		OutsideNightAmbient = null;
		AdjustedRainSplashTintMod = new ColorInfo();
	}
}
