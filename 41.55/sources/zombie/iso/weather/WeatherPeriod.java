package zombie.iso.weather;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.erosion.season.ErosionSeason;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class WeatherPeriod {
	public static final int STAGE_START = 0;
	public static final int STAGE_SHOWERS = 1;
	public static final int STAGE_HEAVY_PRECIP = 2;
	public static final int STAGE_STORM = 3;
	public static final int STAGE_CLEARING = 4;
	public static final int STAGE_MODERATE = 5;
	public static final int STAGE_DRIZZLE = 6;
	public static final int STAGE_BLIZZARD = 7;
	public static final int STAGE_TROPICAL_STORM = 8;
	public static final int STAGE_INTERMEZZO = 9;
	public static final int STAGE_MODDED = 10;
	public static final int STAGE_KATEBOB_STORM = 11;
	public static final int STAGE_MAX = 12;
	public static final float FRONT_STRENGTH_THRESHOLD = 0.1F;
	private ClimateManager climateManager;
	private ClimateManager.AirFront frontCache = new ClimateManager.AirFront();
	private double startTime;
	private double duration;
	private double currentTime;
	private WeatherPeriod.WeatherStage currentStage;
	private ArrayList weatherStages = new ArrayList(20);
	private int weatherStageIndex = 0;
	private Stack stagesPool = new Stack();
	private boolean isRunning = false;
	private float totalProgress = 0.0F;
	private float stageProgress = 0.0F;
	private float weatherNoise;
	private static float maxTemperatureInfluence = 7.0F;
	private float temperatureInfluence = 0.0F;
	private float currentStrength;
	private float rainThreshold;
	private float windAngleDirMod = 1.0F;
	private boolean isThunderStorm = false;
	private boolean isTropicalStorm = false;
	private boolean isBlizzard = false;
	private float precipitationFinal = 0.0F;
	private ThunderStorm thunderStorm;
	private ClimateColorInfo cloudColor = new ClimateColorInfo(0.4F, 0.2F, 0.2F, 0.4F);
	private ClimateColorInfo cloudColorReddish = new ClimateColorInfo(0.66F, 0.12F, 0.12F, 0.4F);
	private ClimateColorInfo cloudColorGreenish = new ClimateColorInfo(0.32F, 0.48F, 0.12F, 0.4F);
	private ClimateColorInfo cloudColorBlueish = new ClimateColorInfo(0.16F, 0.48F, 0.48F, 0.4F);
	private ClimateColorInfo cloudColorPurplish = new ClimateColorInfo(0.66F, 0.12F, 0.66F, 0.4F);
	private ClimateColorInfo cloudColorTropical = new ClimateColorInfo(0.4F, 0.2F, 0.2F, 0.4F);
	private ClimateColorInfo cloudColorBlizzard = new ClimateColorInfo(0.12F, 0.13F, 0.21F, 0.5F, 0.38F, 0.4F, 0.5F, 0.8F);
	private static boolean PRINT_STUFF = false;
	private static float kateBobStormProgress = 0.45F;
	private int kateBobStormX = 2000;
	private int kateBobStormY = 2000;
	private Random seededRandom;
	private ClimateValues climateValues;
	private boolean isDummy = false;
	private boolean hasStartedInit = false;
	private static final HashMap cache = new HashMap();

	public WeatherPeriod(ClimateManager climateManager, ThunderStorm thunderStorm) {
		this.climateManager = climateManager;
		this.thunderStorm = thunderStorm;
		for (int int1 = 0; int1 < 30; ++int1) {
			this.stagesPool.push(new WeatherPeriod.WeatherStage());
		}

		PRINT_STUFF = true;
		this.seededRandom = new Random(1984L);
		this.climateValues = climateManager.getClimateValuesCopy();
	}

	public void setDummy(boolean boolean1) {
		this.isDummy = boolean1;
	}

	public static float getMaxTemperatureInfluence() {
		return maxTemperatureInfluence;
	}

	public void setKateBobStormProgress(float float1) {
		kateBobStormProgress = PZMath.clamp_01(float1);
	}

	public void setKateBobStormCoords(int int1, int int2) {
		this.kateBobStormX = int1;
		this.kateBobStormY = int2;
	}

	public ClimateColorInfo getCloudColorReddish() {
		return this.cloudColorReddish;
	}

	public ClimateColorInfo getCloudColorGreenish() {
		return this.cloudColorGreenish;
	}

	public ClimateColorInfo getCloudColorBlueish() {
		return this.cloudColorBlueish;
	}

	public ClimateColorInfo getCloudColorPurplish() {
		return this.cloudColorPurplish;
	}

	public ClimateColorInfo getCloudColorTropical() {
		return this.cloudColorTropical;
	}

	public ClimateColorInfo getCloudColorBlizzard() {
		return this.cloudColorBlizzard;
	}

	public boolean isRunning() {
		return this.isRunning;
	}

	public double getDuration() {
		return this.duration;
	}

	public ClimateManager.AirFront getFrontCache() {
		return this.frontCache;
	}

	public int getCurrentStageID() {
		return this.currentStage != null ? this.currentStage.stageID : -1;
	}

	public WeatherPeriod.WeatherStage getCurrentStage() {
		return this.currentStage;
	}

	public double getWeatherNoise() {
		return (double)this.weatherNoise;
	}

	public float getCurrentStrength() {
		return this.currentStrength;
	}

	public float getRainThreshold() {
		return this.rainThreshold;
	}

	public boolean isThunderStorm() {
		return this.isThunderStorm;
	}

	public boolean isTropicalStorm() {
		return this.isTropicalStorm;
	}

	public boolean isBlizzard() {
		return this.isBlizzard;
	}

	public float getPrecipitationFinal() {
		return this.precipitationFinal;
	}

	public ClimateColorInfo getCloudColor() {
		return this.cloudColor;
	}

	public void setCloudColor(ClimateColorInfo climateColorInfo) {
		this.cloudColor = climateColorInfo;
	}

	public float getTotalProgress() {
		return this.totalProgress;
	}

	public float getStageProgress() {
		return this.stageProgress;
	}

	public boolean hasTropical() {
		for (int int1 = 0; int1 < this.weatherStages.size(); ++int1) {
			if (((WeatherPeriod.WeatherStage)this.weatherStages.get(int1)).getStageID() == 8) {
				return true;
			}
		}

		return false;
	}

	public boolean hasStorm() {
		for (int int1 = 0; int1 < this.weatherStages.size(); ++int1) {
			if (((WeatherPeriod.WeatherStage)this.weatherStages.get(int1)).getStageID() == 3) {
				return true;
			}
		}

		return false;
	}

	public boolean hasBlizzard() {
		for (int int1 = 0; int1 < this.weatherStages.size(); ++int1) {
			if (((WeatherPeriod.WeatherStage)this.weatherStages.get(int1)).getStageID() == 7) {
				return true;
			}
		}

		return false;
	}

	public boolean hasHeavyRain() {
		for (int int1 = 0; int1 < this.weatherStages.size(); ++int1) {
			if (((WeatherPeriod.WeatherStage)this.weatherStages.get(int1)).getStageID() == 2) {
				return true;
			}
		}

		return false;
	}

	public float getTotalStrength() {
		return this.frontCache.getStrength();
	}

	public WeatherPeriod.WeatherStage getStageForWorldAge(double double1) {
		for (int int1 = 0; int1 < this.weatherStages.size(); ++int1) {
			if (double1 >= ((WeatherPeriod.WeatherStage)this.weatherStages.get(int1)).getStageStart() && double1 < ((WeatherPeriod.WeatherStage)this.weatherStages.get(int1)).getStageEnd()) {
				return (WeatherPeriod.WeatherStage)this.weatherStages.get(int1);
			}
		}

		return null;
	}

	public float getWindAngleDegrees() {
		return this.frontCache.getAngleDegrees();
	}

	public int getFrontType() {
		return this.frontCache.getType();
	}

	private void print(String string) {
		if (PRINT_STUFF && !this.isDummy) {
			DebugLog.log(string);
		}
	}

	public void setPrintStuff(boolean boolean1) {
		PRINT_STUFF = boolean1;
	}

	public boolean getPrintStuff() {
		return PRINT_STUFF;
	}

	public void initSimulationDebug(ClimateManager.AirFront airFront, double double1) {
		GameTime gameTime = GameTime.getInstance();
		this.init(airFront, double1, gameTime.getYear(), gameTime.getMonth(), gameTime.getDayPlusOne(), -1, -1.0F);
	}

	public void initSimulationDebug(ClimateManager.AirFront airFront, double double1, int int1, float float1) {
		GameTime gameTime = GameTime.getInstance();
		this.init(airFront, double1, gameTime.getYear(), gameTime.getMonth(), gameTime.getDayPlusOne(), int1, float1);
	}

	protected void init(ClimateManager.AirFront airFront, double double1, int int1, int int2, int int3) {
		this.init(airFront, double1, int1, int2, int3, -1, -1.0F);
	}

	protected void init(ClimateManager.AirFront airFront, double double1, int int1, int int2, int int3, int int4, float float1) {
		this.climateValues.pollDate(int1, int2, int3);
		this.reseed(int1, int2, int3);
		this.hasStartedInit = false;
		if (this.startInit(airFront, double1)) {
			if (int4 >= 0 && int4 < 12) {
				this.createSingleStage(int4, float1);
			} else {
				this.createWeatherPattern();
			}

			this.endInit();
		}
	}

	protected void reseed(int int1, int int2, int int3) {
		int int4 = (int)this.climateManager.getSimplexOffsetA();
		int int5 = (int)this.climateManager.getSimplexOffsetB();
		long long1 = (long)((int1 - 1990) * 100000);
		long1 += (long)(int2 * int3 * 1234);
		long1 += (long)((int1 - 1990) * int2 * 10000);
		long1 += (long)((int5 - int4) * int3);
		this.print("Reseeding weather period, new seed: " + long1);
		this.seededRandom.setSeed(long1);
	}

	private float RandNext(float float1, float float2) {
		if (float1 == float2) {
			return float1;
		} else {
			if (float1 > float2) {
				float1 = float2;
				float2 = float2;
			}

			return float1 + this.seededRandom.nextFloat() * (float2 - float1);
		}
	}

	private float RandNext(float float1) {
		return this.seededRandom.nextFloat() * float1;
	}

	private int RandNext(int int1, int int2) {
		if (int1 == int2) {
			return int1;
		} else {
			if (int1 > int2) {
				int1 = int2;
				int2 = int2;
			}

			return int1 + this.seededRandom.nextInt(int2 - int1);
		}
	}

	private int RandNext(int int1) {
		return this.seededRandom.nextInt(int1);
	}

	public boolean startCreateModdedPeriod(boolean boolean1, float float1, float float2) {
		double double1 = GameTime.getInstance().getWorldAgeHours();
		ClimateManager.AirFront airFront = new ClimateManager.AirFront();
		float float3 = ClimateManager.clamp(0.0F, 360.0F, float2);
		airFront.setFrontType(boolean1 ? 1 : -1);
		airFront.setFrontWind(float3);
		airFront.setStrength(ClimateManager.clamp01(float1));
		GameTime gameTime = GameTime.getInstance();
		this.reseed(gameTime.getYear(), gameTime.getMonth(), gameTime.getDayPlusOne());
		this.hasStartedInit = false;
		if (!this.startInit(airFront, double1)) {
			return false;
		} else {
			this.print("WeatherPeriod: Creating MODDED weather pattern with strength = " + this.frontCache.getStrength());
			this.clearCurrentWeatherStages();
			return true;
		}
	}

	public boolean endCreateModdedPeriod() {
		if (!this.endInit()) {
			return false;
		} else {
			this.linkWeatherStages();
			this.duration = 0.0;
			for (int int1 = 0; int1 < this.weatherStages.size(); ++int1) {
				this.duration += ((WeatherPeriod.WeatherStage)this.weatherStages.get(int1)).stageDuration;
			}

			this.print("WeatherPeriod: Duration = " + this.duration + ".");
			this.weatherStageIndex = 0;
			this.currentStage = ((WeatherPeriod.WeatherStage)this.weatherStages.get(this.weatherStageIndex)).startStage(this.startTime);
			this.print("WeatherPeriod: PATTERN GENERATION FINISHED.");
			return true;
		}
	}

	private boolean startInit(ClimateManager.AirFront airFront, double double1) {
		if (!this.isRunning && !GameClient.bClient && !(airFront.getStrength() < 0.1F)) {
			this.startTime = double1;
			this.frontCache.copyFrom(airFront);
			if (this.frontCache.getAngleDegrees() >= 90.0F && this.frontCache.getAngleDegrees() < 270.0F) {
				this.windAngleDirMod = 1.0F;
			} else {
				this.windAngleDirMod = -1.0F;
			}

			this.hasStartedInit = true;
			return true;
		} else {
			return false;
		}
	}

	private boolean endInit() {
		if (this.hasStartedInit && !this.isRunning && !GameClient.bClient && this.weatherStages.size() > 0) {
			this.currentStrength = 0.0F;
			this.totalProgress = 0.0F;
			this.stageProgress = 0.0F;
			this.isRunning = true;
			if (GameServer.bServer && !this.isDummy) {
				this.climateManager.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)1, (UdpConnection)null);
			}

			this.hasStartedInit = false;
			return true;
		} else {
			this.hasStartedInit = false;
			return false;
		}
	}

	public void stopWeatherPeriod() {
		this.clearCurrentWeatherStages();
		this.currentStage = null;
		this.resetClimateManagerOverrides();
		this.isRunning = false;
		this.totalProgress = 0.0F;
		this.stageProgress = 0.0F;
	}

	public void writeNetWeatherData(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put((byte)(this.isRunning ? 1 : 0));
		if (this.isRunning) {
			byteBuffer.put((byte)(this.isThunderStorm ? 1 : 0));
			byteBuffer.put((byte)(this.isTropicalStorm ? 1 : 0));
			byteBuffer.put((byte)(this.isBlizzard ? 1 : 0));
			byteBuffer.putFloat(this.currentStrength);
			byteBuffer.putDouble(this.duration);
			byteBuffer.putFloat(this.totalProgress);
			byteBuffer.putFloat(this.stageProgress);
		}
	}

	public void readNetWeatherData(ByteBuffer byteBuffer) throws IOException {
		this.isRunning = byteBuffer.get() == 1;
		if (this.isRunning) {
			this.isThunderStorm = byteBuffer.get() == 1;
			this.isTropicalStorm = byteBuffer.get() == 1;
			this.isBlizzard = byteBuffer.get() == 1;
			this.currentStrength = byteBuffer.getFloat();
			this.duration = byteBuffer.getDouble();
			this.totalProgress = byteBuffer.getFloat();
			this.stageProgress = byteBuffer.getFloat();
		} else {
			this.isThunderStorm = false;
			this.isTropicalStorm = false;
			this.isBlizzard = false;
			this.currentStrength = 0.0F;
			this.duration = 0.0;
			this.totalProgress = 0.0F;
			this.stageProgress = 0.0F;
		}
	}

	public ArrayList getWeatherStages() {
		return this.weatherStages;
	}

	private void linkWeatherStages() {
		WeatherPeriod.WeatherStage weatherStage = null;
		WeatherPeriod.WeatherStage weatherStage2 = null;
		WeatherPeriod.WeatherStage weatherStage3 = null;
		for (int int1 = 0; int1 < this.weatherStages.size(); ++int1) {
			weatherStage3 = (WeatherPeriod.WeatherStage)this.weatherStages.get(int1);
			weatherStage2 = null;
			if (int1 + 1 < this.weatherStages.size()) {
				weatherStage2 = (WeatherPeriod.WeatherStage)this.weatherStages.get(int1 + 1);
			}

			weatherStage3.previousStage = weatherStage;
			weatherStage3.nextStage = weatherStage2;
			weatherStage3.creationFinished = true;
			weatherStage = weatherStage3;
		}
	}

	private void clearCurrentWeatherStages() {
		this.print("WeatherPeriod: Clearing existing stages...");
		Iterator iterator = this.weatherStages.iterator();
		while (iterator.hasNext()) {
			WeatherPeriod.WeatherStage weatherStage = (WeatherPeriod.WeatherStage)iterator.next();
			weatherStage.reset();
			this.stagesPool.push(weatherStage);
		}

		this.weatherStages.clear();
	}

	private void createSingleStage(int int1, float float1) {
		this.print("WeatherPeriod: Creating single stage weather pattern with strength = " + this.frontCache.getStrength());
		if (int1 == 8) {
			this.cloudColor = this.cloudColorTropical;
		} else if (int1 == 7) {
			this.cloudColor = this.cloudColorBlizzard;
		}

		this.clearCurrentWeatherStages();
		this.createAndAddStage(0, 1.0);
		this.createAndAddStage(int1, (double)float1);
		this.createAndAddStage(4, 1.0);
		this.linkWeatherStages();
		this.duration = 0.0;
		for (int int2 = 0; int2 < this.weatherStages.size(); ++int2) {
			this.duration += ((WeatherPeriod.WeatherStage)this.weatherStages.get(int2)).stageDuration;
		}

		this.print("WeatherPeriod: Duration = " + float1 + ".");
		this.weatherStageIndex = 0;
		this.currentStage = ((WeatherPeriod.WeatherStage)this.weatherStages.get(this.weatherStageIndex)).startStage(this.startTime);
		this.print("WeatherPeriod: PATTERN GENERATION FINISHED.");
	}

	private void createWeatherPattern() {
		this.print("WeatherPeriod: Creating weather pattern with strength = " + this.frontCache.getStrength());
		this.clearCurrentWeatherStages();
		ErosionSeason erosionSeason = this.climateManager.getSeason();
		float float1 = this.climateValues.getDayMeanTemperature();
		this.print("WeatherPeriod: Day mean temperature = " + float1 + " C.");
		this.print("WeatherPeriod: season = " + erosionSeason.getSeasonName());
		float float2 = 0.0F;
		float float3 = 0.0F;
		float float4 = 0.0F;
		float float5 = 1.0F;
		float float6 = this.RandNext(0.0F, 100.0F);
		int int1 = erosionSeason.getSeason();
		boolean boolean1 = IsoWorld.instance.getGameMode().equals("Winter is Coming");
		if (boolean1) {
			int1 = 5;
		}

		switch (int1) {
		case 1: 
			if (float6 < 75.0F) {
				this.cloudColor = this.cloudColorGreenish;
			} else {
				this.cloudColor = this.cloudColorBlueish;
			}

			float2 = 75.0F;
			float3 = 10.0F;
			float4 = 0.0F;
			float5 = 1.25F;
			break;
		
		case 2: 
			if (float6 < 25.0F) {
				this.cloudColor = this.cloudColorGreenish;
			} else {
				this.cloudColor = this.cloudColorReddish;
			}

			float2 = 60.0F;
			float3 = 55.0F;
			float4 = 0.0F;
			break;
		
		case 3: 
			this.cloudColor = this.cloudColorReddish;
			float2 = 75.0F;
			float3 = 80.0F;
			float4 = 0.0F;
			float5 = 1.15F;
			break;
		
		case 4: 
			if (float6 < 50.0F) {
				this.cloudColor = this.cloudColorReddish;
			} else if (float6 < 75.0F) {
				this.cloudColor = this.cloudColorPurplish;
			} else {
				this.cloudColor = this.cloudColorBlueish;
			}

			float2 = 100.0F;
			float3 = 25.0F;
			float4 = 0.0F;
			float5 = 1.35F;
			break;
		
		case 5: 
			if (float6 < 45.0F) {
				this.cloudColor = this.cloudColorPurplish;
			} else {
				this.cloudColor = this.cloudColorBlueish;
			}

			float2 = 10.0F;
			float3 = 0.0F;
			if (float1 < 5.5F) {
				float4 = ClimateManager.clamp(0.0F, 85.0F, (5.5F - float1) * 3.0F);
				float4 += 25.0F;
				if (float1 < 2.5F) {
					float4 += 55.0F;
				} else if (float1 < 0.0F) {
					float4 += 75.0F;
				}

				if (float4 > 95.0F) {
					float4 = 95.0F;
				}
			} else {
				float4 = 0.0F;
			}

			if (boolean1) {
				if (this.frontCache.getStrength() > 0.75F) {
					float4 = 100.0F;
				} else {
					float4 = 75.0F;
				}

				if (this.frontCache.getStrength() > 0.5F) {
					float5 = 1.45F;
				}
			}

		
		}
		float5 *= this.climateManager.getRainTimeMultiplierMod(SandboxOptions.instance.getRainModifier());
		float float7 = this.cloudColor.getExterior().r;
		this.print("WeatherPeriod: cloudColor r=" + float7 + ", g=" + this.cloudColor.getExterior().g + ", b=" + this.cloudColor.getExterior().b);
		this.print("WeatherPeriod: chances, storm=" + float2 + ", tropical=" + float3 + ", blizzard=" + float4 + ". rainTimeMulti=" + float5);
		ArrayList arrayList = new ArrayList();
		WeatherPeriod.WeatherStage weatherStage = null;
		float float8;
		if (this.frontCache.getType() == 1) {
			this.print("WeatherPeriod: Warm to cold front selected.");
			boolean boolean2 = false;
			boolean boolean3 = false;
			boolean boolean4 = false;
			if (this.frontCache.getStrength() > 0.75F) {
				if (float3 > 0.0F && this.RandNext(0.0F, 100.0F) < float3) {
					this.print("WeatherPeriod: tropical storm triggered.");
					boolean3 = true;
				} else if (float4 > 0.0F && this.RandNext(0.0F, 100.0F) < float4) {
					this.print("WeatherPeriod: blizzard triggered.");
					boolean2 = true;
				}
			}

			if (!boolean2 && !boolean3 && this.frontCache.getStrength() > 0.5F && float2 > 0.0F && this.RandNext(0.0F, 100.0F) < float2) {
				this.print("WeatherPeriod: storm triggered.");
				boolean4 = true;
			}

			float float9 = this.RandNext(24.0F, 48.0F) * this.frontCache.getStrength();
			float float10 = 0.0F;
			if (boolean3) {
				arrayList.add(this.createStage(8, (double)(8.0F + this.RandNext(0.0F, 16.0F * this.frontCache.getStrength()))));
				this.cloudColor = this.cloudColorTropical;
				if (this.RandNext(0.0F, 100.0F) < 60.0F * this.frontCache.getStrength()) {
					arrayList.add(this.createStage(3, (double)(5.0F + this.RandNext(0.0F, 5.0F * this.frontCache.getStrength()))));
				}

				if (this.RandNext(0.0F, 100.0F) < 30.0F * this.frontCache.getStrength()) {
					arrayList.add(this.createStage(3, (double)(5.0F + this.RandNext(0.0F, 5.0F * this.frontCache.getStrength()))));
				}
			} else if (boolean2) {
				arrayList.add(this.createStage(7, (double)(24.0F + this.RandNext(0.0F, 24.0F * this.frontCache.getStrength()))));
				this.cloudColor = this.cloudColorBlizzard;
			} else if (boolean4) {
				arrayList.add(this.createStage(3, (double)(5.0F + this.RandNext(0.0F, 5.0F * this.frontCache.getStrength()))));
				if (this.RandNext(0.0F, 100.0F) < 70.0F * this.frontCache.getStrength()) {
					arrayList.add(this.createStage(3, (double)(4.0F + this.RandNext(0.0F, 4.0F * this.frontCache.getStrength()))));
				}

				if (this.RandNext(0.0F, 100.0F) < 50.0F * this.frontCache.getStrength()) {
					arrayList.add(this.createStage(3, (double)(4.0F + this.RandNext(0.0F, 4.0F * this.frontCache.getStrength()))));
				}

				if (this.RandNext(0.0F, 100.0F) < 25.0F * this.frontCache.getStrength()) {
					arrayList.add(this.createStage(3, (double)(4.0F + this.RandNext(0.0F, 3.0F * this.frontCache.getStrength()))));
				}

				if (this.RandNext(0.0F, 100.0F) < 12.5F * this.frontCache.getStrength()) {
					arrayList.add(this.createStage(3, (double)(4.0F + this.RandNext(0.0F, 2.0F * this.frontCache.getStrength()))));
				}
			}

			for (int int2 = 0; int2 < arrayList.size(); ++int2) {
				float10 = (float)((double)float10 + ((WeatherPeriod.WeatherStage)arrayList.get(int2)).getStageDuration());
			}

			while (float10 < float9) {
				switch (this.RandNext(0, 10)) {
				case 0: 
					weatherStage = this.createStage(5, (double)(1.0F + this.RandNext(0.0F, 3.0F * this.frontCache.getStrength())));
					break;
				
				case 1: 
				
				case 2: 
				
				case 3: 
					weatherStage = this.createStage(1, (double)(2.0F + this.RandNext(0.0F, 4.0F * this.frontCache.getStrength())));
					break;
				
				default: 
					weatherStage = this.createStage(2, (double)(2.0F + this.RandNext(0.0F, 4.0F * this.frontCache.getStrength())));
				
				}

				float10 = (float)((double)float10 + weatherStage.getStageDuration());
				arrayList.add(weatherStage);
			}
		} else {
			this.print("WeatherPeriod: Cold to warm front selected.");
			if (this.cloudColor == this.cloudColorReddish) {
				float6 = this.RandNext(0.0F, 100.0F);
				if (float6 < 50.0F) {
					this.cloudColor = this.cloudColorBlueish;
				} else {
					this.cloudColor = this.cloudColorPurplish;
				}
			}

			float8 = this.RandNext(12.0F, 24.0F) * this.frontCache.getStrength();
			float float11 = 0.0F;
			while (float11 < float8) {
				switch (this.RandNext(0, 10)) {
				case 0: 
					weatherStage = this.createStage(1, (double)(2.0F + this.RandNext(0.0F, 3.0F * this.frontCache.getStrength())));
					break;
				
				case 1: 
				
				case 2: 
				
				case 3: 
				
				case 4: 
					weatherStage = this.createStage(6, (double)(2.0F + this.RandNext(0.0F, 3.0F * this.frontCache.getStrength())));
					break;
				
				default: 
					weatherStage = this.createStage(5, (double)(2.0F + this.RandNext(0.0F, 3.0F * this.frontCache.getStrength())));
				
				}

				float11 = (float)((double)float11 + weatherStage.getStageDuration());
				arrayList.add(weatherStage);
			}
		}

		Collections.shuffle(arrayList, this.seededRandom);
		float8 = this.RandNext(30.0F, 60.0F);
		this.weatherStages.add(this.createStage(0, (double)(1.0F + this.RandNext(0.0F, 2.0F * this.frontCache.getStrength()))));
		int int3;
		for (int3 = 0; int3 < arrayList.size(); ++int3) {
			this.weatherStages.add((WeatherPeriod.WeatherStage)arrayList.get(int3));
			if (int3 < arrayList.size() - 1 && this.RandNext(0.0F, 100.0F) < float8) {
				this.weatherStages.add(this.createStage(4, (double)(1.0F + this.RandNext(0.0F, 2.0F * this.frontCache.getStrength()))));
				this.weatherStages.add(this.createStage(9, (double)(1.0F + this.RandNext(0.0F, 3.0F * this.frontCache.getStrength()))));
				float8 = this.RandNext(30.0F, 60.0F);
			}
		}

		if (((WeatherPeriod.WeatherStage)this.weatherStages.get(this.weatherStages.size() - 1)).getStageID() != 9) {
			this.weatherStages.add(this.createStage(4, (double)(2.0F + this.RandNext(0.0F, 3.0F * this.frontCache.getStrength()))));
		}

		for (int3 = 0; int3 < this.weatherStages.size(); ++int3) {
			WeatherPeriod.WeatherStage weatherStage2 = (WeatherPeriod.WeatherStage)this.weatherStages.get(int3);
			weatherStage2.stageDuration *= (double)float5;
		}

		this.linkWeatherStages();
		this.duration = 0.0;
		for (int3 = 0; int3 < this.weatherStages.size(); ++int3) {
			this.duration += ((WeatherPeriod.WeatherStage)this.weatherStages.get(int3)).stageDuration;
		}

		this.print("WeatherPeriod: Duration = " + this.duration + ".");
		double double1 = this.startTime;
		for (int int4 = 0; int4 < this.weatherStages.size(); ++int4) {
			double1 = ((WeatherPeriod.WeatherStage)this.weatherStages.get(int4)).setStageStart(double1);
		}

		this.weatherStageIndex = 0;
		this.currentStage = ((WeatherPeriod.WeatherStage)this.weatherStages.get(this.weatherStageIndex)).startStage(this.startTime);
		this.print("WeatherPeriod: PATTERN GENERATION FINISHED.");
	}

	public WeatherPeriod.WeatherStage createAndAddModdedStage(String string, double double1) {
		WeatherPeriod.WeatherStage weatherStage = this.createAndAddStage(10, double1, string);
		return weatherStage;
	}

	public WeatherPeriod.WeatherStage createAndAddStage(int int1, double double1) {
		return this.createAndAddStage(int1, double1, (String)null);
	}

	private WeatherPeriod.WeatherStage createAndAddStage(int int1, double double1, String string) {
		if (!this.isRunning && this.hasStartedInit && (int1 != 10 || string != null)) {
			WeatherPeriod.WeatherStage weatherStage = this.createStage(int1, double1, string);
			this.weatherStages.add(weatherStage);
			return weatherStage;
		} else {
			return null;
		}
	}

	private WeatherPeriod.WeatherStage createStage(int int1, double double1) {
		return this.createStage(int1, double1, (String)null);
	}

	private WeatherPeriod.WeatherStage createStage(int int1, double double1, String string) {
		WeatherPeriod.WeatherStage weatherStage = null;
		if (!this.stagesPool.isEmpty()) {
			weatherStage = (WeatherPeriod.WeatherStage)this.stagesPool.pop();
		} else {
			weatherStage = new WeatherPeriod.WeatherStage();
		}

		weatherStage.stageID = int1;
		weatherStage.modID = string;
		weatherStage.setStageDuration(double1);
		switch (int1) {
		case 0: 
			this.print("WeatherPeriod: Adding stage \'START\' with duration: " + double1 + "%.");
			weatherStage.lerpEntryTo(WeatherPeriod.StrLerpVal.NextTarget);
			break;
		
		case 1: 
			this.print("WeatherPeriod: Adding stage \'SHOWERS\' with duration: " + double1 + "%.");
			weatherStage.targetStrength = this.frontCache.getStrength() * 0.5F;
			weatherStage.lerpEntryTo(WeatherPeriod.StrLerpVal.Target, WeatherPeriod.StrLerpVal.NextTarget);
			break;
		
		case 2: 
			this.print("WeatherPeriod: Adding stage \'HEAVY_PRECIP\' with duration: " + double1 + "%.");
			weatherStage.targetStrength = this.frontCache.getStrength();
			weatherStage.lerpEntryTo(WeatherPeriod.StrLerpVal.Target, WeatherPeriod.StrLerpVal.Target);
			break;
		
		case 3: 
		
		case 11: 
			this.print("WeatherPeriod: Adding stage \'STORM\' with duration: " + double1 + "%.");
			if (int1 == 11) {
				this.print("WeatherPeriod: this storm is a kate and bob storm...");
			}

			weatherStage.targetStrength = this.frontCache.getStrength();
			weatherStage.lerpEntryTo(WeatherPeriod.StrLerpVal.Target, WeatherPeriod.StrLerpVal.Target);
			if (this.RandNext(0, 100) < 33) {
				weatherStage.fogStrength = 0.1F + this.RandNext(0.0F, 0.4F);
			}

			break;
		
		case 4: 
			this.print("WeatherPeriod: Adding stage \'CLEARING\' with duration: " + double1 + "%.");
			weatherStage.targetStrength = this.frontCache.getStrength() * 0.25F;
			weatherStage.lerpEntryTo(WeatherPeriod.StrLerpVal.Target, WeatherPeriod.StrLerpVal.None);
			break;
		
		case 5: 
			this.print("WeatherPeriod: Adding stage \'MODERATE\' with duration: " + double1 + "%.");
			weatherStage.targetStrength = this.frontCache.getStrength() * 0.5F;
			weatherStage.lerpEntryTo(WeatherPeriod.StrLerpVal.Target, WeatherPeriod.StrLerpVal.NextTarget);
			break;
		
		case 6: 
			this.print("WeatherPeriod: Adding stage \'DRIZZLE\' with duration: " + double1 + "%.");
			weatherStage.targetStrength = this.frontCache.getStrength() * 0.25F;
			weatherStage.lerpEntryTo(WeatherPeriod.StrLerpVal.Target, WeatherPeriod.StrLerpVal.NextTarget);
			break;
		
		case 7: 
			this.print("WeatherPeriod: Adding stage \'BLIZZARD\' with duration: " + double1 + "%.");
			weatherStage.targetStrength = 1.0F;
			weatherStage.lerpEntryTo(WeatherPeriod.StrLerpVal.Target, WeatherPeriod.StrLerpVal.Target);
			weatherStage.fogStrength = 0.55F + this.RandNext(0.0F, 0.2F);
			break;
		
		case 8: 
			this.print("WeatherPeriod: Adding stage \'TROPICAL_STORM\' with duration: " + double1 + "%.");
			weatherStage.targetStrength = 1.0F;
			weatherStage.lerpEntryTo(WeatherPeriod.StrLerpVal.Target, WeatherPeriod.StrLerpVal.Target);
			weatherStage.fogStrength = 0.6F + this.RandNext(0.0F, 0.4F);
			break;
		
		case 9: 
			this.print("WeatherPeriod: Adding stage \'INTERMEZZO\' with duration: " + double1 + "%.");
			weatherStage.targetStrength = 0.0F;
			weatherStage.lerpEntryTo(WeatherPeriod.StrLerpVal.Target, WeatherPeriod.StrLerpVal.NextTarget);
			break;
		
		case 10: 
			this.print("WeatherPeriod: Adding stage \'MODDED\' with duration: " + double1 + "%.");
			LuaEventManager.triggerEvent("OnInitModdedWeatherStage", this, weatherStage, this.frontCache.getStrength());
			break;
		
		default: 
			this.print("WeatherPeriod Warning: trying to _INIT_ state that is not recognized, state id=" + int1);
		
		}
		return weatherStage;
	}

	private void updateCurrentStage() {
		if (!this.isDummy) {
			this.isBlizzard = false;
			this.isThunderStorm = false;
			this.isTropicalStorm = false;
			float float1;
			float float2;
			switch (this.currentStage.stageID) {
			case 0: 
				this.rainThreshold = 0.35F - this.frontCache.getStrength() * 0.2F;
				this.climateManager.fogIntensity.setOverride(0.0F, this.currentStage.linearT);
				break;
			
			case 1: 
				this.climateManager.fogIntensity.setOverride(0.0F, 1.0F);
				float2 = ClimateManager.clamp01(this.currentStage.parabolicT * 3.0F);
				this.climateManager.windIntensity.setOverride(0.1F * this.weatherNoise, float2);
				this.climateManager.windAngleIntensity.setOverride(0.0F, float2);
				break;
			
			case 2: 
				float1 = this.frontCache.getStrength() * 0.5F;
				if (this.currentStage.linearT < 0.1F) {
					float1 = ClimateManager.clerp((float)((this.currentTime - this.currentStage.stageStart) / (this.currentStage.stageDuration * 0.1)), 0.0F, this.frontCache.getStrength() * 0.5F);
				} else if (this.currentStage.linearT > 0.9F) {
					float1 = ClimateManager.clerp(1.0F - (float)((this.currentStage.stageEnd - this.currentTime) / (this.currentStage.stageDuration * 0.1)), this.frontCache.getStrength() * 0.5F, 0.0F);
				}

				this.weatherNoise = float1 + this.weatherNoise * (1.0F - float1);
				this.climateManager.fogIntensity.setOverride(0.0F, 1.0F);
				float2 = ClimateManager.clamp01(this.currentStage.parabolicT * 3.0F);
				this.climateManager.windIntensity.setOverride(0.5F * this.weatherNoise, float2);
				this.climateManager.windAngleIntensity.setOverride(0.7F * this.weatherNoise * this.windAngleDirMod, float2);
				break;
			
			case 4: 
				this.climateManager.fogIntensity.setOverride(0.0F, 1.0F - this.currentStage.linearT);
				break;
			
			case 5: 
				this.climateManager.fogIntensity.setOverride(0.0F, 1.0F);
				break;
			
			case 6: 
				this.climateManager.fogIntensity.setOverride(0.0F, 1.0F);
				break;
			
			case 7: 
				this.isBlizzard = true;
				float1 = this.frontCache.getStrength() * 0.5F;
				if (this.currentStage.linearT < 0.1F) {
					float1 = ClimateManager.clerp((float)((this.currentTime - this.currentStage.stageStart) / (this.currentStage.stageDuration * 0.1)), 0.0F, this.frontCache.getStrength() * 0.5F);
				} else if (this.currentStage.linearT > 0.9F) {
					float1 = ClimateManager.clerp(1.0F - (float)((this.currentStage.stageEnd - this.currentTime) / (this.currentStage.stageDuration * 0.1)), this.frontCache.getStrength() * 0.5F, 0.0F);
				}

				this.weatherNoise = float1 + this.weatherNoise * (1.0F - float1);
				float2 = ClimateManager.clamp01(this.currentStage.parabolicT * 3.0F);
				this.climateManager.windIntensity.setOverride(0.75F + 0.25F * this.weatherNoise, float2);
				this.climateManager.windAngleIntensity.setOverride(0.7F * this.weatherNoise * this.windAngleDirMod, float2);
				if (PerformanceSettings.FogQuality != 2) {
					if (this.currentStage.fogStrength > 0.0F) {
						this.climateManager.fogIntensity.setOverride(this.currentStage.fogStrength, float2);
					} else {
						this.climateManager.fogIntensity.setOverride(1.0F, float2);
					}
				}

				break;
			
			case 8: 
				this.isTropicalStorm = true;
			
			case 3: 
			
			case 11: 
				this.isThunderStorm = !this.isTropicalStorm;
				if (!this.currentStage.hasStartedCloud) {
					float float3 = this.frontCache.getAngleDegrees();
					float float4 = this.frontCache.getStrength();
					float float5 = 8000.0F * float4;
					float float6 = float4;
					float float7 = 0.6F * float4;
					double double1 = this.currentStage.stageDuration;
					boolean boolean1 = (double)float4 > 0.7;
					int int1 = Rand.Next(1, 3);
					if (this.currentStage.stageID == 8) {
						int1 = 1;
						float5 = 15000.0F;
						float7 = 0.8F;
						boolean1 = true;
						float4 = 1.0F;
					}

					for (int int2 = 0; int2 < int1; ++int2) {
						ThunderStorm.ThunderCloud thunderCloud = this.thunderStorm.startThunderCloud(float4, float3, float5, float6, float7, double1, boolean1, this.currentStage.stageID == 11 ? kateBobStormProgress : 0.0F);
						if (this.currentStage.stageID == 11 && boolean1 && thunderCloud != null) {
							thunderCloud.setCenter(this.kateBobStormX, this.kateBobStormY, float3);
						}

						boolean1 = false;
					}

					this.currentStage.hasStartedCloud = true;
				}

				float1 = this.frontCache.getStrength() * 0.5F;
				if (this.currentStage.linearT < 0.1F) {
					float1 = ClimateManager.clerp((float)((this.currentTime - this.currentStage.stageStart) / (this.currentStage.stageDuration * 0.1)), 0.0F, this.frontCache.getStrength() * 0.5F);
				} else if (this.currentStage.linearT > 0.9F) {
					float1 = ClimateManager.clerp(1.0F - (float)((this.currentStage.stageEnd - this.currentTime) / (this.currentStage.stageDuration * 0.1)), this.frontCache.getStrength() * 0.5F, 0.0F);
				}

				this.weatherNoise = float1 + this.weatherNoise * (1.0F - float1);
				float2 = ClimateManager.clamp01(this.currentStage.parabolicT * 3.0F);
				if (this.currentStage.stageID == 8) {
					this.climateManager.windIntensity.setOverride(0.4F + 0.6F * this.weatherNoise, float2);
				} else {
					this.climateManager.windIntensity.setOverride(0.2F + 0.5F * this.weatherNoise, float2);
				}

				this.climateManager.windAngleIntensity.setOverride(0.7F * this.weatherNoise * this.windAngleDirMod, float2);
				if (PerformanceSettings.FogQuality != 2) {
					if (this.currentStage.fogStrength > 0.0F) {
						this.climateManager.fogIntensity.setOverride(this.currentStage.fogStrength, float2);
						if (this.currentStage.stageID == 8) {
							this.climateManager.colorNewFog.setOverride(this.climateManager.getFogTintTropical(), float2);
						} else {
							this.climateManager.colorNewFog.setOverride(this.climateManager.getFogTintStorm(), float2);
						}
					} else {
						this.climateManager.fogIntensity.setOverride(0.0F, 1.0F);
					}
				}

				break;
			
			case 9: 
				this.climateManager.fogIntensity.setOverride(0.0F, 1.0F);
				break;
			
			case 10: 
				LuaEventManager.triggerEvent("OnUpdateModdedWeatherStage", this, this.currentStage, this.frontCache.getStrength());
				break;
			
			default: 
				this.print("WeatherPeriod Warning: trying to _UPDATE_ state that is not recognized, state id=" + this.currentStage.stageID);
				this.resetClimateManagerOverrides();
				this.isRunning = false;
				if (GameServer.bServer) {
					this.climateManager.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)1, (UdpConnection)null);
				}

			
			}
		}
	}

	public void update(double double1) {
		if (!GameClient.bClient && !this.isDummy) {
			if (this.isRunning && this.currentStage != null && this.weatherStageIndex >= 0 && this.weatherStages.size() != 0) {
				if (this.currentTime > this.currentStage.stageEnd) {
					++this.weatherStageIndex;
					if (this.weatherStageIndex >= this.weatherStages.size()) {
						this.isRunning = false;
						this.currentStage = null;
						this.resetClimateManagerOverrides();
						if (GameServer.bServer) {
							this.climateManager.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)1, (UdpConnection)null);
						}

						return;
					}

					if (this.currentStage != null) {
						this.currentStage.exitStrength = this.currentStrength;
					}

					this.currentStage = (WeatherPeriod.WeatherStage)this.weatherStages.get(this.weatherStageIndex);
					this.currentStage.entryStrength = this.currentStrength;
					this.currentStage.startStage(double1);
				}

				this.currentTime = double1;
				this.weatherNoise = 0.3F * this.frontCache.getStrength() + (float)SimplexNoise.noise(double1, 24000.0) * (1.0F - 0.3F * this.frontCache.getStrength());
				this.weatherNoise = (this.weatherNoise + 1.0F) * 0.5F;
				this.currentStage.updateT(this.currentTime);
				this.stageProgress = this.currentStage.linearT;
				this.totalProgress = (float)(this.currentTime - ((WeatherPeriod.WeatherStage)this.weatherStages.get(0)).stageStart) / (float)this.duration;
				this.totalProgress = ClimateManager.clamp01(this.totalProgress);
				this.currentStrength = this.currentStage.getStageCurrentStrength();
				this.updateCurrentStage();
				float float1 = ClimateManager.clamp(-1.0F, 1.0F, this.currentStrength * 2.0F) * maxTemperatureInfluence;
				if (this.frontCache.getType() == 1) {
					this.temperatureInfluence = this.climateManager.temperature.internalValue - float1;
				} else {
					this.temperatureInfluence = this.climateManager.temperature.internalValue + float1;
				}

				if (this.isRunning) {
					if (this.weatherNoise > this.rainThreshold) {
						this.precipitationFinal = (this.weatherNoise - this.rainThreshold) / (1.0F - this.rainThreshold);
						this.precipitationFinal *= this.currentStrength;
					} else {
						this.precipitationFinal = 0.0F;
					}

					float float2 = this.precipitationFinal;
					float float3 = float2 * (1.0F - this.climateManager.nightStrength.internalValue);
					float float4 = 0.5F;
					float4 += 0.5F * (1.0F - this.climateManager.nightStrength.internalValue);
					float4 = Math.max(float4, this.climateManager.cloudIntensity.internalValue);
					float float5 = 0.55F;
					if (PerformanceSettings.FogQuality != 2 && this.currentStage.stageID == 8) {
						float5 += 0.35F * this.currentStage.parabolicT;
					}

					float float6 = 1.0F - float5 * float2;
					float6 = Math.min(float6, 1.0F - this.climateManager.nightStrength.internalValue);
					if (PerformanceSettings.FogQuality != 2 && this.currentStage.stageID == 7) {
						float float7 = 1.0F - 0.75F * this.currentStage.parabolicT;
						float4 *= float7;
					}

					this.climateManager.cloudIntensity.setOverride(float4, this.currentStrength);
					this.climateManager.precipitationIntensity.setOverride(this.precipitationFinal, 1.0F);
					this.climateManager.globalLight.setOverride(this.cloudColor, float3);
					this.climateManager.globalLightIntensity.setOverride(0.4F, float3);
					this.climateManager.desaturation.setOverride(0.3F, this.currentStrength);
					this.climateManager.temperature.setOverride(this.temperatureInfluence, this.currentStrength);
					this.climateManager.ambient.setOverride(float6, float2);
					this.climateManager.dayLightStrength.setOverride(float6, float2);
					if ((!(this.climateManager.getTemperature() < 0.0F) || !this.climateManager.getSeason().isSeason(5)) && !ClimateManager.WINTER_IS_COMING) {
						this.climateManager.precipitationIsSnow.setEnableOverride(false);
					} else {
						this.climateManager.precipitationIsSnow.setOverride(true);
					}
				}
			} else {
				if (this.isRunning) {
					this.resetClimateManagerOverrides();
					this.isRunning = false;
					if (GameServer.bServer) {
						this.climateManager.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)1, (UdpConnection)null);
					}
				}
			}
		}
	}

	private void resetClimateManagerOverrides() {
		if (this.climateManager != null && !this.isDummy) {
			this.climateManager.resetOverrides();
		}
	}

	public void save(DataOutputStream dataOutputStream) throws IOException {
		if (GameClient.bClient && !GameServer.bServer) {
			dataOutputStream.writeByte(0);
		} else {
			dataOutputStream.writeByte(1);
			dataOutputStream.writeBoolean(this.isRunning);
			if (this.isRunning) {
				dataOutputStream.writeInt(this.weatherStageIndex);
				dataOutputStream.writeFloat(this.currentStrength);
				dataOutputStream.writeFloat(this.rainThreshold);
				dataOutputStream.writeBoolean(this.isThunderStorm);
				dataOutputStream.writeBoolean(this.isTropicalStorm);
				dataOutputStream.writeBoolean(this.isBlizzard);
				this.frontCache.save(dataOutputStream);
				dataOutputStream.writeInt(this.weatherStages.size());
				for (int int1 = 0; int1 < this.weatherStages.size(); ++int1) {
					WeatherPeriod.WeatherStage weatherStage = (WeatherPeriod.WeatherStage)this.weatherStages.get(int1);
					dataOutputStream.writeInt(weatherStage.stageID);
					dataOutputStream.writeDouble(weatherStage.stageDuration);
					weatherStage.save(dataOutputStream);
				}

				this.cloudColor.save(dataOutputStream);
			}
		}
	}

	public void load(DataInputStream dataInputStream, int int1) throws IOException {
		byte byte1 = dataInputStream.readByte();
		if (byte1 == 1) {
			this.isRunning = dataInputStream.readBoolean();
			if (this.isRunning) {
				this.weatherStageIndex = dataInputStream.readInt();
				this.currentStrength = dataInputStream.readFloat();
				this.rainThreshold = dataInputStream.readFloat();
				this.isThunderStorm = dataInputStream.readBoolean();
				this.isTropicalStorm = dataInputStream.readBoolean();
				this.isBlizzard = dataInputStream.readBoolean();
				this.frontCache.load(dataInputStream);
				if (this.frontCache.getAngleDegrees() >= 90.0F && this.frontCache.getAngleDegrees() < 270.0F) {
					this.windAngleDirMod = 1.0F;
				} else {
					this.windAngleDirMod = -1.0F;
				}

				this.print("WeatherPeriod: Loading weather pattern with strength = " + this.frontCache.getStrength());
				this.clearCurrentWeatherStages();
				int int2 = dataInputStream.readInt();
				int int3;
				for (int3 = 0; int3 < int2; ++int3) {
					int int4 = dataInputStream.readInt();
					double double1 = dataInputStream.readDouble();
					WeatherPeriod.WeatherStage weatherStage = !this.stagesPool.isEmpty() ? (WeatherPeriod.WeatherStage)this.stagesPool.pop() : new WeatherPeriod.WeatherStage();
					weatherStage.stageID = int4;
					weatherStage.setStageDuration(double1);
					weatherStage.load(dataInputStream, int1);
					this.weatherStages.add(weatherStage);
				}

				if (int1 >= 170) {
					this.cloudColor.load(dataInputStream, int1);
				}

				this.linkWeatherStages();
				this.duration = 0.0;
				for (int3 = 0; int3 < this.weatherStages.size(); ++int3) {
					this.duration += ((WeatherPeriod.WeatherStage)this.weatherStages.get(int3)).stageDuration;
				}

				if (this.weatherStageIndex >= 0 && this.weatherStageIndex < this.weatherStages.size()) {
					this.currentStage = (WeatherPeriod.WeatherStage)this.weatherStages.get(this.weatherStageIndex);
					this.print("WeatherPeriod: Pattern loaded!");
				} else {
					this.print("WeatherPeriod: Couldnt load stages correctly.");
					this.isRunning = false;
				}
			}
		}
	}

	public static class WeatherStage {
		protected WeatherPeriod.WeatherStage previousStage;
		protected WeatherPeriod.WeatherStage nextStage;
		private double stageStart;
		private double stageEnd;
		private double stageDuration;
		protected int stageID;
		protected float entryStrength;
		protected float exitStrength;
		protected float targetStrength;
		protected WeatherPeriod.StrLerpVal lerpMidVal;
		protected WeatherPeriod.StrLerpVal lerpEndVal;
		protected boolean hasStartedCloud = false;
		protected float fogStrength = 0.0F;
		protected float linearT;
		protected float parabolicT;
		protected boolean isCycleFirstHalf = true;
		protected boolean creationFinished = false;
		protected String modID;
		private float m;
		private float e;

		public WeatherStage() {
		}

		public WeatherStage(int int1) {
			this.stageID = int1;
		}

		public void setStageID(int int1) {
			this.stageID = int1;
		}

		public double getStageStart() {
			return this.stageStart;
		}

		public double getStageEnd() {
			return this.stageEnd;
		}

		public double getStageDuration() {
			return this.stageDuration;
		}

		public int getStageID() {
			return this.stageID;
		}

		public String getModID() {
			return this.modID;
		}

		public float getLinearT() {
			return this.linearT;
		}

		public float getParabolicT() {
			return this.parabolicT;
		}

		public void setTargetStrength(float float1) {
			this.targetStrength = float1;
		}

		public boolean getHasStartedCloud() {
			return this.hasStartedCloud;
		}

		public void setHasStartedCloud(boolean boolean1) {
			this.hasStartedCloud = true;
		}

		public void save(DataOutputStream dataOutputStream) throws IOException {
			dataOutputStream.writeDouble(this.stageStart);
			dataOutputStream.writeFloat(this.entryStrength);
			dataOutputStream.writeFloat(this.exitStrength);
			dataOutputStream.writeFloat(this.targetStrength);
			dataOutputStream.writeInt(this.lerpMidVal.getValue());
			dataOutputStream.writeInt(this.lerpEndVal.getValue());
			dataOutputStream.writeBoolean(this.hasStartedCloud);
			dataOutputStream.writeByte(this.modID != null ? 1 : 0);
			if (this.modID != null) {
				GameWindow.WriteString(dataOutputStream, this.modID);
			}

			dataOutputStream.writeFloat(this.fogStrength);
		}

		public void load(DataInputStream dataInputStream, int int1) throws IOException {
			this.stageStart = dataInputStream.readDouble();
			this.stageEnd = this.stageStart + this.stageDuration;
			this.entryStrength = dataInputStream.readFloat();
			this.exitStrength = dataInputStream.readFloat();
			this.targetStrength = dataInputStream.readFloat();
			this.lerpMidVal = WeatherPeriod.StrLerpVal.fromValue(dataInputStream.readInt());
			this.lerpEndVal = WeatherPeriod.StrLerpVal.fromValue(dataInputStream.readInt());
			this.hasStartedCloud = dataInputStream.readBoolean();
			if (int1 >= 141 && dataInputStream.readByte() == 1) {
				this.modID = GameWindow.ReadString(dataInputStream);
			}

			if (int1 >= 170) {
				this.fogStrength = dataInputStream.readFloat();
			}
		}

		protected void reset() {
			this.previousStage = null;
			this.nextStage = null;
			this.isCycleFirstHalf = true;
			this.hasStartedCloud = false;
			this.lerpMidVal = WeatherPeriod.StrLerpVal.None;
			this.lerpEndVal = WeatherPeriod.StrLerpVal.None;
			this.entryStrength = 0.0F;
			this.exitStrength = 0.0F;
			this.modID = null;
			this.creationFinished = false;
			this.fogStrength = 0.0F;
		}

		protected WeatherPeriod.WeatherStage startStage(double double1) {
			this.stageStart = double1;
			this.stageEnd = double1 + this.stageDuration;
			this.hasStartedCloud = false;
			return this;
		}

		protected double setStageStart(double double1) {
			this.stageStart = double1;
			this.stageEnd = double1 + this.stageDuration;
			return this.stageEnd;
		}

		protected WeatherPeriod.WeatherStage setStageDuration(double double1) {
			this.stageDuration = double1;
			if (this.stageDuration < 1.0) {
				this.stageDuration = 1.0;
			}

			return this;
		}

		protected WeatherPeriod.WeatherStage overrideStageDuration(double double1) {
			this.stageDuration = double1;
			return this;
		}

		public void lerpEntryTo(int int1, int int2) {
			if (!this.creationFinished) {
				this.lerpEntryTo(WeatherPeriod.StrLerpVal.fromValue(int1), WeatherPeriod.StrLerpVal.fromValue(int2));
			}
		}

		protected void lerpEntryTo(WeatherPeriod.StrLerpVal strLerpVal) {
			this.lerpEntryTo(WeatherPeriod.StrLerpVal.None, strLerpVal);
		}

		protected void lerpEntryTo(WeatherPeriod.StrLerpVal strLerpVal, WeatherPeriod.StrLerpVal strLerpVal2) {
			if (!this.creationFinished) {
				this.lerpMidVal = strLerpVal;
				this.lerpEndVal = strLerpVal2;
			}
		}

		public float getStageCurrentStrength() {
			this.m = this.getLerpValue(this.lerpMidVal);
			this.e = this.getLerpValue(this.lerpEndVal);
			if (this.lerpMidVal == WeatherPeriod.StrLerpVal.None) {
				return ClimateManager.clerp(this.linearT, this.entryStrength, this.e);
			} else {
				return this.isCycleFirstHalf ? ClimateManager.clerp(this.parabolicT, this.entryStrength, this.m) : ClimateManager.clerp(this.parabolicT, this.e, this.m);
			}
		}

		private float getLerpValue(WeatherPeriod.StrLerpVal strLerpVal) {
			switch (strLerpVal) {
			case Entry: 
				return this.entryStrength;
			
			case Target: 
				return this.targetStrength;
			
			case NextTarget: 
				return this.nextStage != null ? this.nextStage.targetStrength : 0.0F;
			
			case None: 
				return 0.0F;
			
			default: 
				return 0.0F;
			
			}
		}

		private WeatherPeriod.WeatherStage updateT(double double1) {
			this.linearT = this.getPeriodLerpT(double1);
			if (this.stageID == 11) {
				this.linearT = WeatherPeriod.kateBobStormProgress + (1.0F - WeatherPeriod.kateBobStormProgress) * this.linearT;
			}

			if (this.linearT < 0.5F) {
				this.parabolicT = this.linearT * 2.0F;
				this.isCycleFirstHalf = true;
			} else {
				this.parabolicT = 2.0F - this.linearT * 2.0F;
				this.isCycleFirstHalf = false;
			}

			return this;
		}

		private float getPeriodLerpT(double double1) {
			if (double1 < this.stageStart) {
				return 0.0F;
			} else {
				return double1 > this.stageEnd ? 1.0F : (float)((double1 - this.stageStart) / this.stageDuration);
			}
		}
	}

	public static enum StrLerpVal {

		Entry,
		Target,
		NextTarget,
		None,
		value;

		private StrLerpVal(int int1) {
			this.value = int1;
			if (WeatherPeriod.cache.containsKey(int1)) {
				DebugLog.log("StrLerpVal WARNING: trying to add id twice. id=" + int1);
			}

			WeatherPeriod.cache.put(int1, this);
		}
		public int getValue() {
			return this.value;
		}
		public static WeatherPeriod.StrLerpVal fromValue(int int1) {
			if (WeatherPeriod.cache.containsKey(int1)) {
				return (WeatherPeriod.StrLerpVal)WeatherPeriod.cache.get(int1);
			} else {
				DebugLog.log("StrLerpVal, trying to get from invalid id: " + int1);
				return None;
			}
		}
		private static WeatherPeriod.StrLerpVal[] $values() {
			return new WeatherPeriod.StrLerpVal[]{Entry, Target, NextTarget, None};
		}
	}
}
