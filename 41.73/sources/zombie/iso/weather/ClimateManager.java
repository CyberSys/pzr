package zombie.iso.weather;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.GregorianCalendar;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionMain;
import zombie.erosion.season.ErosionIceQueen;
import zombie.erosion.season.ErosionSeason;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoPuddles;
import zombie.iso.IsoWater;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.SkyBox;
import zombie.iso.weather.dbg.ClimMngrDebug;
import zombie.iso.weather.fx.IsoWeatherFX;
import zombie.iso.weather.fx.SteppedUpdateFloat;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.vehicles.BaseVehicle;


public class ClimateManager {
	private boolean DISABLE_SIMULATION = false;
	private boolean DISABLE_FX_UPDATE = false;
	private boolean DISABLE_WEATHER_GENERATION = false;
	public static final int FRONT_COLD = -1;
	public static final int FRONT_STATIONARY = 0;
	public static final int FRONT_WARM = 1;
	public static final float MAX_WINDSPEED_KPH = 120.0F;
	public static final float MAX_WINDSPEED_MPH = 74.5645F;
	private ErosionSeason season;
	private long lastMinuteStamp = -1L;
	private KahluaTable modDataTable = null;
	private float airMass;
	private float airMassDaily;
	private float airMassTemperature;
	private float baseTemperature;
	private float snowFall = 0.0F;
	private float snowStrength = 0.0F;
	private float snowMeltStrength = 0.0F;
	private float snowFracNow = 0.0F;
	boolean canDoWinterSprites = false;
	private float windPower = 0.0F;
	private WeatherPeriod weatherPeriod;
	private ThunderStorm thunderStorm;
	private double simplexOffsetA = 0.0;
	private double simplexOffsetB = 0.0;
	private double simplexOffsetC = 0.0;
	private double simplexOffsetD = 0.0;
	private boolean dayDoFog = false;
	private float dayFogStrength = 0.0F;
	private GameTime gt;
	private double worldAgeHours;
	private boolean tickIsClimateTick = false;
	private boolean tickIsDayChange = false;
	private int lastHourStamp = -1;
	private boolean tickIsHourChange = false;
	private boolean tickIsTenMins = false;
	private ClimateManager.AirFront currentFront = new ClimateManager.AirFront();
	private ClimateColorInfo colDay = new ClimateColorInfo();
	private ClimateColorInfo colDusk = new ClimateColorInfo();
	private ClimateColorInfo colDawn = new ClimateColorInfo();
	private ClimateColorInfo colNight = new ClimateColorInfo();
	private ClimateColorInfo colNightNoMoon;
	private ClimateColorInfo colNightMoon = new ClimateColorInfo();
	private ClimateColorInfo colTemp = new ClimateColorInfo();
	private ClimateColorInfo colFog = new ClimateColorInfo();
	private ClimateColorInfo colFogLegacy;
	private ClimateColorInfo colFogNew;
	private ClimateColorInfo fogTintStorm;
	private ClimateColorInfo fogTintTropical;
	private static ClimateManager instance = new ClimateManager();
	public static boolean WINTER_IS_COMING = false;
	public static boolean THE_DESCENDING_FOG = false;
	public static boolean A_STORM_IS_COMING = false;
	private ClimateValues climateValues;
	private ClimateForecaster climateForecaster;
	private ClimateHistory climateHistory;
	float dayLightLagged = 0.0F;
	float nightLagged = 0.0F;
	protected ClimateManager.ClimateFloat desaturation;
	protected ClimateManager.ClimateFloat globalLightIntensity;
	protected ClimateManager.ClimateFloat nightStrength;
	protected ClimateManager.ClimateFloat precipitationIntensity;
	protected ClimateManager.ClimateFloat temperature;
	protected ClimateManager.ClimateFloat fogIntensity;
	protected ClimateManager.ClimateFloat windIntensity;
	protected ClimateManager.ClimateFloat windAngleIntensity;
	protected ClimateManager.ClimateFloat cloudIntensity;
	protected ClimateManager.ClimateFloat ambient;
	protected ClimateManager.ClimateFloat viewDistance;
	protected ClimateManager.ClimateFloat dayLightStrength;
	protected ClimateManager.ClimateFloat humidity;
	protected ClimateManager.ClimateColor globalLight;
	protected ClimateManager.ClimateColor colorNewFog;
	protected ClimateManager.ClimateBool precipitationIsSnow;
	public static final int FLOAT_DESATURATION = 0;
	public static final int FLOAT_GLOBAL_LIGHT_INTENSITY = 1;
	public static final int FLOAT_NIGHT_STRENGTH = 2;
	public static final int FLOAT_PRECIPITATION_INTENSITY = 3;
	public static final int FLOAT_TEMPERATURE = 4;
	public static final int FLOAT_FOG_INTENSITY = 5;
	public static final int FLOAT_WIND_INTENSITY = 6;
	public static final int FLOAT_WIND_ANGLE_INTENSITY = 7;
	public static final int FLOAT_CLOUD_INTENSITY = 8;
	public static final int FLOAT_AMBIENT = 9;
	public static final int FLOAT_VIEW_DISTANCE = 10;
	public static final int FLOAT_DAYLIGHT_STRENGTH = 11;
	public static final int FLOAT_HUMIDITY = 12;
	public static final int FLOAT_MAX = 13;
	private final ClimateManager.ClimateFloat[] climateFloats = new ClimateManager.ClimateFloat[13];
	public static final int COLOR_GLOBAL_LIGHT = 0;
	public static final int COLOR_NEW_FOG = 1;
	public static final int COLOR_MAX = 2;
	private final ClimateManager.ClimateColor[] climateColors = new ClimateManager.ClimateColor[2];
	public static final int BOOL_IS_SNOW = 0;
	public static final int BOOL_MAX = 1;
	private final ClimateManager.ClimateBool[] climateBooleans = new ClimateManager.ClimateBool[1];
	public static final float AVG_FAV_AIR_TEMPERATURE = 22.0F;
	private static double windNoiseOffset = 0.0;
	private static double windNoiseBase = 0.0;
	private static double windNoiseFinal = 0.0;
	private static double windTickFinal = 0.0;
	private ClimateColorInfo colFlare = new ClimateColorInfo(1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F);
	private boolean flareLaunched = false;
	private SteppedUpdateFloat flareIntensity = new SteppedUpdateFloat(0.0F, 0.01F, 0.0F, 1.0F);
	private float flareIntens;
	private float flareMaxLifeTime;
	private float flareLifeTime;
	private int nextRandomTargetIntens = 10;
	float fogLerpValue = 0.0F;
	private ClimateManager.SeasonColor seasonColorDawn;
	private ClimateManager.SeasonColor seasonColorDay;
	private ClimateManager.SeasonColor seasonColorDusk;
	private ClimateManager.DayInfo previousDay;
	private ClimateManager.DayInfo currentDay;
	private ClimateManager.DayInfo nextDay;
	public static final byte PacketUpdateClimateVars = 0;
	public static final byte PacketWeatherUpdate = 1;
	public static final byte PacketThunderEvent = 2;
	public static final byte PacketFlare = 3;
	public static final byte PacketAdminVarsUpdate = 4;
	public static final byte PacketRequestAdminVars = 5;
	public static final byte PacketClientChangedAdminVars = 6;
	public static final byte PacketClientChangedWeather = 7;
	private float networkLerp = 0.0F;
	private long networkUpdateStamp = 0L;
	private float networkLerpTime = 5000.0F;
	private float networkLerpTimeBase = 5000.0F;
	private float networkAdjustVal = 0.0F;
	private boolean networkPrint = false;
	private ClimateManager.ClimateNetInfo netInfo = new ClimateManager.ClimateNetInfo();
	private ClimateValues climateValuesFronts;
	private static float[] windAngles = new float[]{22.5F, 67.5F, 112.5F, 157.5F, 202.5F, 247.5F, 292.5F, 337.5F, 382.5F};
	private static String[] windAngleStr = new String[]{"SE", "S", "SW", "W", "NW", "N", "NE", "E", "SE"};

	public float getMaxWindspeedKph() {
		return 120.0F;
	}

	public float getMaxWindspeedMph() {
		return 74.5645F;
	}

	public static float ToKph(float float1) {
		return float1 * 120.0F;
	}

	public static float ToMph(float float1) {
		return float1 * 74.5645F;
	}

	public static ClimateManager getInstance() {
		return instance;
	}

	public static void setInstance(ClimateManager climateManager) {
		instance = climateManager;
	}

	public ClimateManager() {
		this.colDay = new ClimateColorInfo();
		this.colDawn = new ClimateColorInfo();
		this.colDusk = new ClimateColorInfo();
		this.colNight = new ClimateColorInfo(0.33F, 0.33F, 1.0F, 0.4F, 0.33F, 0.33F, 1.0F, 0.4F);
		this.colNightNoMoon = new ClimateColorInfo(0.33F, 0.33F, 1.0F, 0.4F, 0.33F, 0.33F, 1.0F, 0.4F);
		this.colNightMoon = new ClimateColorInfo(0.33F, 0.33F, 1.0F, 0.4F, 0.33F, 0.33F, 1.0F, 0.4F);
		this.colFog = new ClimateColorInfo(0.4F, 0.4F, 0.4F, 0.8F, 0.4F, 0.4F, 0.4F, 0.8F);
		this.colFogLegacy = new ClimateColorInfo(0.3F, 0.3F, 0.3F, 0.8F, 0.3F, 0.3F, 0.3F, 0.8F);
		this.colFogNew = new ClimateColorInfo(0.5F, 0.5F, 0.55F, 0.4F, 0.5F, 0.5F, 0.55F, 0.8F);
		this.fogTintStorm = new ClimateColorInfo(0.5F, 0.45F, 0.4F, 1.0F, 0.5F, 0.45F, 0.4F, 1.0F);
		this.fogTintTropical = new ClimateColorInfo(0.8F, 0.75F, 0.55F, 1.0F, 0.8F, 0.75F, 0.55F, 1.0F);
		this.colTemp = new ClimateColorInfo();
		this.simplexOffsetA = (double)Rand.Next(0, 8000);
		this.simplexOffsetB = (double)Rand.Next(8000, 16000);
		this.simplexOffsetC = (double)Rand.Next(0, -8000);
		this.simplexOffsetD = (double)Rand.Next(-8000, -16000);
		this.initSeasonColors();
		this.setup();
		this.climateValues = new ClimateValues(this);
		this.thunderStorm = new ThunderStorm(this);
		this.weatherPeriod = new WeatherPeriod(this, this.thunderStorm);
		this.climateForecaster = new ClimateForecaster();
		this.climateHistory = new ClimateHistory();
		try {
			LuaEventManager.triggerEvent("OnClimateManagerInit", this);
		} catch (Exception exception) {
			System.out.print(exception.getMessage());
			System.out.print(exception.getStackTrace());
		}
	}

	public ClimateColorInfo getColNight() {
		return this.colNight;
	}

	public ClimateColorInfo getColNightNoMoon() {
		return this.colNightNoMoon;
	}

	public ClimateColorInfo getColNightMoon() {
		return this.colNightMoon;
	}

	public ClimateColorInfo getColFog() {
		return this.colFog;
	}

	public ClimateColorInfo getColFogLegacy() {
		return this.colFogLegacy;
	}

	public ClimateColorInfo getColFogNew() {
		return this.colFogNew;
	}

	public ClimateColorInfo getFogTintStorm() {
		return this.fogTintStorm;
	}

	public ClimateColorInfo getFogTintTropical() {
		return this.fogTintTropical;
	}

	private void setup() {
		int int1;
		for (int1 = 0; int1 < this.climateFloats.length; ++int1) {
			this.climateFloats[int1] = new ClimateManager.ClimateFloat();
		}

		for (int1 = 0; int1 < this.climateColors.length; ++int1) {
			this.climateColors[int1] = new ClimateManager.ClimateColor();
		}

		for (int1 = 0; int1 < this.climateBooleans.length; ++int1) {
			this.climateBooleans[int1] = new ClimateManager.ClimateBool();
		}

		this.desaturation = this.initClimateFloat(0, "DESATURATION");
		this.globalLightIntensity = this.initClimateFloat(1, "GLOBAL_LIGHT_INTENSITY");
		this.nightStrength = this.initClimateFloat(2, "NIGHT_STRENGTH");
		this.precipitationIntensity = this.initClimateFloat(3, "PRECIPITATION_INTENSITY");
		this.temperature = this.initClimateFloat(4, "TEMPERATURE");
		this.temperature.min = -80.0F;
		this.temperature.max = 80.0F;
		this.fogIntensity = this.initClimateFloat(5, "FOG_INTENSITY");
		this.windIntensity = this.initClimateFloat(6, "WIND_INTENSITY");
		this.windAngleIntensity = this.initClimateFloat(7, "WIND_ANGLE_INTENSITY");
		this.windAngleIntensity.min = -1.0F;
		this.cloudIntensity = this.initClimateFloat(8, "CLOUD_INTENSITY");
		this.ambient = this.initClimateFloat(9, "AMBIENT");
		this.viewDistance = this.initClimateFloat(10, "VIEW_DISTANCE");
		this.viewDistance.min = 0.0F;
		this.viewDistance.max = 100.0F;
		this.dayLightStrength = this.initClimateFloat(11, "DAYLIGHT_STRENGTH");
		this.humidity = this.initClimateFloat(12, "HUMIDITY");
		this.globalLight = this.initClimateColor(0, "GLOBAL_LIGHT");
		this.colorNewFog = this.initClimateColor(1, "COLOR_NEW_FOG");
		this.colorNewFog.internalValue.setExterior(0.9F, 0.9F, 0.95F, 1.0F);
		this.colorNewFog.internalValue.setInterior(0.9F, 0.9F, 0.95F, 1.0F);
		this.precipitationIsSnow = this.initClimateBool(0, "IS_SNOW");
	}

	public int getFloatMax() {
		return 13;
	}

	private ClimateManager.ClimateFloat initClimateFloat(int int1, String string) {
		if (int1 >= 0 && int1 < 13) {
			return this.climateFloats[int1].init(int1, string);
		} else {
			DebugLog.log("Climate: cannot get float override id.");
			return null;
		}
	}

	public ClimateManager.ClimateFloat getClimateFloat(int int1) {
		if (int1 >= 0 && int1 < 13) {
			return this.climateFloats[int1];
		} else {
			DebugLog.log("Climate: cannot get float override id.");
			return null;
		}
	}

	public int getColorMax() {
		return 2;
	}

	private ClimateManager.ClimateColor initClimateColor(int int1, String string) {
		if (int1 >= 0 && int1 < 2) {
			return this.climateColors[int1].init(int1, string);
		} else {
			DebugLog.log("Climate: cannot get float override id.");
			return null;
		}
	}

	public ClimateManager.ClimateColor getClimateColor(int int1) {
		if (int1 >= 0 && int1 < 2) {
			return this.climateColors[int1];
		} else {
			DebugLog.log("Climate: cannot get float override id.");
			return null;
		}
	}

	public int getBoolMax() {
		return 1;
	}

	private ClimateManager.ClimateBool initClimateBool(int int1, String string) {
		if (int1 >= 0 && int1 < 1) {
			return this.climateBooleans[int1].init(int1, string);
		} else {
			DebugLog.log("Climate: cannot get boolean id.");
			return null;
		}
	}

	public ClimateManager.ClimateBool getClimateBool(int int1) {
		if (int1 >= 0 && int1 < 1) {
			return this.climateBooleans[int1];
		} else {
			DebugLog.log("Climate: cannot get boolean id.");
			return null;
		}
	}

	public void setEnabledSimulation(boolean boolean1) {
		if (!GameClient.bClient && !GameServer.bServer) {
			this.DISABLE_SIMULATION = !boolean1;
		} else {
			this.DISABLE_SIMULATION = false;
		}
	}

	public boolean getEnabledSimulation() {
		return !this.DISABLE_SIMULATION;
	}

	public boolean getEnabledFxUpdate() {
		return !this.DISABLE_FX_UPDATE;
	}

	public void setEnabledFxUpdate(boolean boolean1) {
		if (!GameClient.bClient && !GameServer.bServer) {
			this.DISABLE_FX_UPDATE = !boolean1;
		} else {
			this.DISABLE_FX_UPDATE = false;
		}
	}

	public boolean getEnabledWeatherGeneration() {
		return this.DISABLE_WEATHER_GENERATION;
	}

	public void setEnabledWeatherGeneration(boolean boolean1) {
		this.DISABLE_WEATHER_GENERATION = !boolean1;
	}

	public Color getGlobalLightInternal() {
		return this.globalLight.internalValue.getExterior();
	}

	public ClimateColorInfo getGlobalLight() {
		return this.globalLight.finalValue;
	}

	public float getGlobalLightIntensity() {
		return this.globalLightIntensity.finalValue;
	}

	public ClimateColorInfo getColorNewFog() {
		return this.colorNewFog.finalValue;
	}

	public void setNightStrength(float float1) {
		this.nightStrength.finalValue = clamp(0.0F, 1.0F, float1);
	}

	public float getDesaturation() {
		return this.desaturation.finalValue;
	}

	public void setDesaturation(float float1) {
		this.desaturation.finalValue = float1;
	}

	public float getAirMass() {
		return this.airMass;
	}

	public float getAirMassDaily() {
		return this.airMassDaily;
	}

	public float getAirMassTemperature() {
		return this.airMassTemperature;
	}

	public float getDayLightStrength() {
		return this.dayLightStrength.finalValue;
	}

	public float getNightStrength() {
		return this.nightStrength.finalValue;
	}

	public float getDayMeanTemperature() {
		return this.currentDay.season.getDayMeanTemperature();
	}

	public float getTemperature() {
		return this.temperature.finalValue;
	}

	public float getBaseTemperature() {
		return this.baseTemperature;
	}

	public float getSnowStrength() {
		return this.snowStrength;
	}

	public boolean getPrecipitationIsSnow() {
		return this.precipitationIsSnow.finalValue;
	}

	public float getPrecipitationIntensity() {
		return this.precipitationIntensity.finalValue;
	}

	public float getFogIntensity() {
		return this.fogIntensity.finalValue;
	}

	public float getWindIntensity() {
		return this.windIntensity.finalValue;
	}

	public float getWindAngleIntensity() {
		return this.windAngleIntensity.finalValue;
	}

	public float getCorrectedWindAngleIntensity() {
		return (this.windAngleIntensity.finalValue + 1.0F) * 0.5F;
	}

	public float getWindPower() {
		return this.windPower;
	}

	public float getWindspeedKph() {
		return this.windPower * 120.0F;
	}

	public float getCloudIntensity() {
		return this.cloudIntensity.finalValue;
	}

	public float getAmbient() {
		return this.ambient.finalValue;
	}

	public float getViewDistance() {
		return this.viewDistance.finalValue;
	}

	public float getHumidity() {
		return this.humidity.finalValue;
	}

	public float getWindAngleDegrees() {
		float float1;
		if (this.windAngleIntensity.finalValue > 0.0F) {
			float1 = lerp(this.windAngleIntensity.finalValue, 45.0F, 225.0F);
		} else if (this.windAngleIntensity.finalValue > -0.25F) {
			float1 = lerp(Math.abs(this.windAngleIntensity.finalValue), 45.0F, 0.0F);
		} else {
			float1 = lerp(Math.abs(this.windAngleIntensity.finalValue) - 0.25F, 360.0F, 180.0F);
		}

		if (float1 > 360.0F) {
			float1 -= 360.0F;
		}

		if (float1 < 0.0F) {
			float1 += 360.0F;
		}

		return float1;
	}

	public float getWindAngleRadians() {
		return (float)Math.toRadians((double)this.getWindAngleDegrees());
	}

	public float getWindSpeedMovement() {
		float float1 = this.getWindIntensity();
		if (float1 < 0.15F) {
			float1 = 0.0F;
		} else {
			float1 = (float1 - 0.15F) / 0.85F;
		}

		return float1;
	}

	public float getWindForceMovement(IsoGameCharacter gameCharacter, float float1) {
		if (gameCharacter.square != null && !gameCharacter.square.isInARoom()) {
			float float2 = float1 - this.getWindAngleRadians();
			if ((double)float2 > 6.283185307179586) {
				float2 = (float)((double)float2 - 6.283185307179586);
			}

			if (float2 < 0.0F) {
				float2 = (float)((double)float2 + 6.283185307179586);
			}

			if ((double)float2 > 3.141592653589793) {
				float2 = (float)(3.141592653589793 - ((double)float2 - 3.141592653589793));
			}

			float2 = (float)((double)float2 / 3.141592653589793);
			return float2;
		} else {
			return 0.0F;
		}
	}

	public boolean isRaining() {
		return this.getPrecipitationIntensity() > 0.0F && !this.getPrecipitationIsSnow();
	}

	public float getRainIntensity() {
		return this.isRaining() ? this.getPrecipitationIntensity() : 0.0F;
	}

	public boolean isSnowing() {
		return this.getPrecipitationIntensity() > 0.0F && this.getPrecipitationIsSnow();
	}

	public float getSnowIntensity() {
		return this.isSnowing() ? this.getPrecipitationIntensity() : 0.0F;
	}

	public void setAmbient(float float1) {
		this.ambient.finalValue = float1;
	}

	public void setViewDistance(float float1) {
		this.viewDistance.finalValue = float1;
	}

	public void setDayLightStrength(float float1) {
		this.dayLightStrength.finalValue = float1;
	}

	public void setPrecipitationIsSnow(boolean boolean1) {
		this.precipitationIsSnow.finalValue = boolean1;
	}

	public ClimateManager.DayInfo getCurrentDay() {
		return this.currentDay;
	}

	public ClimateManager.DayInfo getPreviousDay() {
		return this.previousDay;
	}

	public ClimateManager.DayInfo getNextDay() {
		return this.nextDay;
	}

	public ErosionSeason getSeason() {
		return this.currentDay != null && this.currentDay.getSeason() != null ? this.currentDay.getSeason() : this.season;
	}

	public float getFrontStrength() {
		if (this.currentFront == null) {
			return 0.0F;
		} else {
			if (Core.bDebug) {
				this.CalculateWeatherFrontStrength(this.gt.getYear(), this.gt.getMonth(), this.gt.getDayPlusOne(), this.currentFront);
			}

			return this.currentFront.strength;
		}
	}

	public void stopWeatherAndThunder() {
		if (!GameClient.bClient) {
			this.weatherPeriod.stopWeatherPeriod();
			this.thunderStorm.stopAllClouds();
			if (GameServer.bServer) {
				this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)1, (UdpConnection)null);
			}
		}
	}

	public ThunderStorm getThunderStorm() {
		return this.thunderStorm;
	}

	public WeatherPeriod getWeatherPeriod() {
		return this.weatherPeriod;
	}

	public boolean getIsThunderStorming() {
		return this.weatherPeriod.isRunning() && (this.weatherPeriod.isThunderStorm() || this.weatherPeriod.isTropicalStorm());
	}

	public float getWeatherInterference() {
		if (this.weatherPeriod.isRunning()) {
			return !this.weatherPeriod.isThunderStorm() && !this.weatherPeriod.isTropicalStorm() && !this.weatherPeriod.isBlizzard() ? 0.35F * this.weatherPeriod.getCurrentStrength() : 0.7F * this.weatherPeriod.getCurrentStrength();
		} else {
			return 0.0F;
		}
	}

	public KahluaTable getModData() {
		if (this.modDataTable == null) {
			this.modDataTable = LuaManager.platform.newTable();
		}

		return this.modDataTable;
	}

	public float getAirTemperatureForCharacter(IsoGameCharacter gameCharacter) {
		return this.getAirTemperatureForCharacter(gameCharacter, false);
	}

	public float getAirTemperatureForCharacter(IsoGameCharacter gameCharacter, boolean boolean1) {
		if (gameCharacter.square != null) {
			return gameCharacter.getVehicle() != null ? this.getAirTemperatureForSquare(gameCharacter.square, gameCharacter.getVehicle(), boolean1) : this.getAirTemperatureForSquare(gameCharacter.square, (BaseVehicle)null, boolean1);
		} else {
			return this.getTemperature();
		}
	}

	public float getAirTemperatureForSquare(IsoGridSquare square) {
		return this.getAirTemperatureForSquare(square, (BaseVehicle)null);
	}

	public float getAirTemperatureForSquare(IsoGridSquare square, BaseVehicle baseVehicle) {
		return this.getAirTemperatureForSquare(square, baseVehicle, false);
	}

	public float getAirTemperatureForSquare(IsoGridSquare square, BaseVehicle baseVehicle, boolean boolean1) {
		float float1 = this.getTemperature();
		if (square != null) {
			boolean boolean2 = square.isInARoom();
			if (!boolean2 && baseVehicle == null) {
				if (boolean1) {
					float1 = Temperature.WindchillCelsiusKph(float1, this.getWindspeedKph());
				}
			} else {
				boolean boolean3 = IsoWorld.instance.isHydroPowerOn();
				float float2;
				if (float1 <= 22.0F) {
					float2 = (22.0F - float1) / 8.0F;
					if (baseVehicle == null) {
						if (boolean2 && boolean3) {
							float1 = 22.0F;
						}

						float2 = 22.0F - float1;
						if (square.getZ() < 1) {
							float1 += float2 * (0.4F + 0.2F * this.dayLightLagged);
						} else {
							float2 = (float)((double)float2 * 0.85);
							float1 += float2 * (0.4F + 0.2F * this.dayLightLagged);
						}
					}
				} else {
					float2 = (float1 - 22.0F) / 3.5F;
					if (baseVehicle == null) {
						if (boolean2 && boolean3) {
							float1 = 22.0F;
						}

						float2 = float1 - 22.0F;
						if (square.getZ() < 1) {
							float2 = (float)((double)float2 * 0.85);
							float1 -= float2 * (0.4F + 0.2F * this.dayLightLagged);
						} else {
							float1 -= float2 * (0.4F + 0.2F * this.dayLightLagged + 0.2F * this.nightLagged);
						}
					} else {
						float1 = float1 + float2 + float2 * this.dayLightLagged;
					}
				}
			}

			float float3 = IsoWorld.instance.getCell().getHeatSourceHighestTemperature(float1, square.getX(), square.getY(), square.getZ());
			if (float3 > float1) {
				float1 = float3;
			}

			if (baseVehicle != null) {
				float1 += baseVehicle.getInsideTemperature();
			}
		}

		return float1;
	}

	public String getSeasonName() {
		return this.season.getSeasonName();
	}

	public float getSeasonProgression() {
		return this.season.getSeasonProgression();
	}

	public float getSeasonStrength() {
		return this.season.getSeasonStrength();
	}

	public void init(IsoMetaGrid metaGrid) {
		WorldFlares.Clear();
		this.season = ErosionMain.getInstance().getSeasons();
		ThunderStorm.MAP_MIN_X = metaGrid.minX * 300 - 4000;
		ThunderStorm.MAP_MAX_X = metaGrid.maxX * 300 + 4000;
		ThunderStorm.MAP_MIN_Y = metaGrid.minY * 300 - 4000;
		ThunderStorm.MAP_MAX_Y = metaGrid.maxY * 300 + 4000;
		windNoiseOffset = 0.0;
		WINTER_IS_COMING = IsoWorld.instance.getGameMode().equals("Winter is Coming");
		THE_DESCENDING_FOG = IsoWorld.instance.getGameMode().equals("The Descending Fog");
		A_STORM_IS_COMING = IsoWorld.instance.getGameMode().equals("A Storm is Coming");
		this.climateForecaster.init(this);
		this.climateHistory.init(this);
	}

	public void updateEveryTenMins() {
		this.tickIsTenMins = true;
	}

	public void update() {
		this.tickIsClimateTick = false;
		this.tickIsHourChange = false;
		this.tickIsDayChange = false;
		this.gt = GameTime.getInstance();
		this.worldAgeHours = this.gt.getWorldAgeHours();
		if (this.lastMinuteStamp != this.gt.getMinutesStamp()) {
			this.lastMinuteStamp = this.gt.getMinutesStamp();
			this.tickIsClimateTick = true;
			this.updateDayInfo(this.gt.getDayPlusOne(), this.gt.getMonth(), this.gt.getYear());
			this.currentDay.hour = this.gt.getHour();
			this.currentDay.minutes = this.gt.getMinutes();
			if (this.gt.getHour() != this.lastHourStamp) {
				this.tickIsHourChange = true;
				this.lastHourStamp = this.gt.getHour();
			}

			if (this.gt.getTimeOfDay() > 12.0F) {
				ClimateMoon.updatePhase(this.currentDay.getYear(), this.currentDay.getMonth(), this.currentDay.getDay());
			}
		}

		if (this.DISABLE_SIMULATION) {
			IsoPlayer[] playerArray = IsoPlayer.players;
			for (int int1 = 0; int1 < playerArray.length; ++int1) {
				IsoPlayer player = playerArray[int1];
				if (player != null) {
					player.dirtyRecalcGridStackTime = 1.0F;
				}
			}
		} else {
			if (this.tickIsDayChange && !GameClient.bClient) {
				this.climateForecaster.updateDayChange(this);
				this.climateHistory.updateDayChange(this);
			}

			if (GameClient.bClient) {
				this.networkLerp = 1.0F;
				long long1 = System.currentTimeMillis();
				if ((float)long1 < (float)this.networkUpdateStamp + this.networkLerpTime) {
					this.networkLerp = (float)(long1 - this.networkUpdateStamp) / this.networkLerpTime;
					if (this.networkLerp < 0.0F) {
						this.networkLerp = 0.0F;
					}
				}

				int int2;
				for (int2 = 0; int2 < this.climateFloats.length; ++int2) {
					this.climateFloats[int2].interpolate = this.networkLerp;
				}

				for (int2 = 0; int2 < this.climateColors.length; ++int2) {
					this.climateColors[int2].interpolate = this.networkLerp;
				}
			}

			if (this.tickIsClimateTick && !GameClient.bClient) {
				this.updateValues();
				this.weatherPeriod.update(this.worldAgeHours);
			}

			if (this.tickIsClimateTick) {
				LuaEventManager.triggerEvent("OnClimateTick", this);
			}

			int int3;
			for (int3 = 0; int3 < this.climateColors.length; ++int3) {
				this.climateColors[int3].calculate();
			}

			for (int3 = 0; int3 < this.climateFloats.length; ++int3) {
				this.climateFloats[int3].calculate();
			}

			for (int3 = 0; int3 < this.climateBooleans.length; ++int3) {
				this.climateBooleans[int3].calculate();
			}

			this.windPower = this.windIntensity.finalValue;
			this.updateWindTick();
			if (this.tickIsClimateTick) {
			}

			this.updateTestFlare();
			this.thunderStorm.update(this.worldAgeHours);
			if (GameClient.bClient) {
				this.updateSnow();
			} else if (this.tickIsClimateTick && !GameClient.bClient) {
				this.updateSnow();
			}

			if (!GameClient.bClient) {
				this.updateViewDistance();
			}

			if (this.tickIsClimateTick && Core.bDebug && !GameServer.bServer) {
				LuaEventManager.triggerEvent("OnClimateTickDebug", this);
			}

			if (this.tickIsClimateTick && GameServer.bServer && this.tickIsTenMins) {
				this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, (UdpConnection)null);
				this.tickIsTenMins = false;
			}

			if (!this.DISABLE_FX_UPDATE) {
				this.updateFx();
			}
		}
	}

	public static double getWindNoiseBase() {
		return windNoiseBase;
	}

	public static double getWindNoiseFinal() {
		return windNoiseFinal;
	}

	public static double getWindTickFinal() {
		return windTickFinal;
	}

	private void updateWindTick() {
		if (!GameServer.bServer) {
			float float1 = this.windIntensity.finalValue;
			windNoiseOffset += (4.0E-4 + 6.0E-4 * (double)float1) * (double)GameTime.getInstance().getMultiplier();
			windNoiseBase = SimplexNoise.noise(0.0, windNoiseOffset);
			windNoiseFinal = windNoiseBase;
			if (windNoiseFinal > 0.0) {
				windNoiseFinal *= 0.04 + 0.1 * (double)float1;
			} else {
				windNoiseFinal *= 0.04 + 0.1 * (double)float1 + (double)(0.05F * float1 * float1);
			}

			float1 = clamp01(float1 + (float)windNoiseFinal);
			windTickFinal = (double)float1;
		}
	}

	public void updateOLD() {
		this.tickIsClimateTick = false;
		this.tickIsHourChange = false;
		this.tickIsDayChange = false;
		this.gt = GameTime.getInstance();
		this.worldAgeHours = this.gt.getWorldAgeHours();
		if (this.lastMinuteStamp != this.gt.getMinutesStamp()) {
			this.lastMinuteStamp = this.gt.getMinutesStamp();
			this.tickIsClimateTick = true;
			this.updateDayInfo(this.gt.getDay(), this.gt.getMonth(), this.gt.getYear());
			this.currentDay.hour = this.gt.getHour();
			this.currentDay.minutes = this.gt.getMinutes();
			if (this.gt.getHour() != this.lastHourStamp) {
				this.tickIsHourChange = true;
				this.lastHourStamp = this.gt.getHour();
			}
		}

		if (GameClient.bClient) {
			if (!this.DISABLE_SIMULATION) {
				this.networkLerp = 1.0F;
				long long1 = System.currentTimeMillis();
				if ((float)long1 < (float)this.networkUpdateStamp + this.networkLerpTime) {
					this.networkLerp = (float)(long1 - this.networkUpdateStamp) / this.networkLerpTime;
					if (this.networkLerp < 0.0F) {
						this.networkLerp = 0.0F;
					}
				}

				int int1;
				for (int1 = 0; int1 < this.climateFloats.length; ++int1) {
					this.climateFloats[int1].interpolate = this.networkLerp;
				}

				for (int1 = 0; int1 < this.climateColors.length; ++int1) {
					this.climateColors[int1].interpolate = this.networkLerp;
				}

				if (this.tickIsClimateTick) {
					LuaEventManager.triggerEvent("OnClimateTick", this);
				}

				this.updateOnTick();
				this.updateTestFlare();
				this.thunderStorm.update(this.worldAgeHours);
				this.updateSnow();
				if (this.tickIsTenMins) {
					this.tickIsTenMins = false;
				}
			}

			this.updateFx();
		} else {
			if (!this.DISABLE_SIMULATION) {
				if (this.tickIsClimateTick) {
					this.updateValues();
					this.weatherPeriod.update(this.gt.getWorldAgeHours());
				}

				this.updateOnTick();
				this.updateTestFlare();
				this.thunderStorm.update(this.worldAgeHours);
				if (this.tickIsClimateTick) {
					this.updateSnow();
					LuaEventManager.triggerEvent("OnClimateTick", this);
				}

				this.updateViewDistance();
				if (this.tickIsClimateTick && this.tickIsTenMins) {
					if (GameServer.bServer) {
						this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, (UdpConnection)null);
					}

					this.tickIsTenMins = false;
				}
			}

			if (!this.DISABLE_FX_UPDATE && this.tickIsClimateTick) {
				this.updateFx();
			}

			if (this.DISABLE_SIMULATION) {
				IsoPlayer[] playerArray = IsoPlayer.players;
				for (int int2 = 0; int2 < playerArray.length; ++int2) {
					IsoPlayer player = playerArray[int2];
					if (player != null) {
						player.dirtyRecalcGridStackTime = 1.0F;
					}
				}
			}
		}
	}

	private void updateFx() {
		IsoWeatherFX weatherFX = IsoWorld.instance.getCell().getWeatherFX();
		if (weatherFX != null) {
			weatherFX.setPrecipitationIntensity(this.precipitationIntensity.finalValue);
			weatherFX.setWindIntensity(this.windIntensity.finalValue);
			weatherFX.setWindPrecipIntensity((float)windTickFinal * (float)windTickFinal);
			weatherFX.setWindAngleIntensity(this.windAngleIntensity.finalValue);
			weatherFX.setFogIntensity(this.fogIntensity.finalValue);
			weatherFX.setCloudIntensity(this.cloudIntensity.finalValue);
			weatherFX.setPrecipitationIsSnow(this.precipitationIsSnow.finalValue);
			SkyBox.getInstance().update(this);
			IsoWater.getInstance().update(this);
			IsoPuddles.getInstance().update(this);
		}
	}

	private void updateSnow() {
		if (GameClient.bClient) {
			IsoWorld.instance.CurrentCell.setSnowTarget((int)(this.snowFracNow * 100.0F));
			ErosionIceQueen.instance.setSnow(this.canDoWinterSprites && this.snowFracNow > 0.2F);
		} else {
			if (!this.tickIsHourChange) {
				this.canDoWinterSprites = this.season.isSeason(5) || WINTER_IS_COMING;
				if (this.precipitationIsSnow.finalValue && this.precipitationIntensity.finalValue > this.snowFall) {
					this.snowFall = this.precipitationIntensity.finalValue;
				}

				if (this.temperature.finalValue > 0.0F) {
					float float1 = this.temperature.finalValue / 10.0F;
					float1 = float1 * 0.2F + float1 * 0.8F * this.dayLightStrength.finalValue;
					if (float1 > this.snowMeltStrength) {
						this.snowMeltStrength = float1;
					}
				}

				if (!this.precipitationIsSnow.finalValue && this.precipitationIntensity.finalValue > 0.0F) {
					this.snowMeltStrength += this.precipitationIntensity.finalValue;
				}
			} else {
				this.snowStrength += this.snowFall;
				this.snowStrength -= this.snowMeltStrength;
				this.snowStrength = clamp(0.0F, 10.0F, this.snowStrength);
				this.snowFracNow = this.snowStrength > 7.5F ? 1.0F : this.snowStrength / 7.5F;
				IsoWorld.instance.CurrentCell.setSnowTarget((int)(this.snowFracNow * 100.0F));
				ErosionIceQueen.instance.setSnow(this.canDoWinterSprites && this.snowFracNow > 0.2F);
				this.snowFall = 0.0F;
				this.snowMeltStrength = 0.0F;
			}
		}
	}

	private void updateSnowOLD() {
	}

	public float getSnowFracNow() {
		return this.snowFracNow;
	}

	public void resetOverrides() {
		int int1;
		for (int1 = 0; int1 < this.climateColors.length; ++int1) {
			this.climateColors[int1].setEnableOverride(false);
		}

		for (int1 = 0; int1 < this.climateFloats.length; ++int1) {
			this.climateFloats[int1].setEnableOverride(false);
		}

		for (int1 = 0; int1 < this.climateBooleans.length; ++int1) {
			this.climateBooleans[int1].setEnableOverride(false);
		}
	}

	public void resetModded() {
		int int1;
		for (int1 = 0; int1 < this.climateColors.length; ++int1) {
			this.climateColors[int1].setEnableModded(false);
		}

		for (int1 = 0; int1 < this.climateFloats.length; ++int1) {
			this.climateFloats[int1].setEnableModded(false);
		}

		for (int1 = 0; int1 < this.climateBooleans.length; ++int1) {
			this.climateBooleans[int1].setEnableModded(false);
		}
	}

	public void resetAdmin() {
		int int1;
		for (int1 = 0; int1 < this.climateColors.length; ++int1) {
			this.climateColors[int1].setEnableAdmin(false);
		}

		for (int1 = 0; int1 < this.climateFloats.length; ++int1) {
			this.climateFloats[int1].setEnableAdmin(false);
		}

		for (int1 = 0; int1 < this.climateBooleans.length; ++int1) {
			this.climateBooleans[int1].setEnableAdmin(false);
		}
	}

	public void triggerWinterIsComingStorm() {
		if (!GameClient.bClient && !this.weatherPeriod.isRunning()) {
			ClimateManager.AirFront airFront = new ClimateManager.AirFront();
			airFront.copyFrom(this.currentFront);
			airFront.strength = 0.95F;
			airFront.type = 1;
			GameTime gameTime = GameTime.getInstance();
			this.weatherPeriod.init(airFront, this.worldAgeHours, gameTime.getYear(), gameTime.getMonth(), gameTime.getDayPlusOne());
		}
	}

	public boolean triggerCustomWeather(float float1, boolean boolean1) {
		if (!GameClient.bClient && !this.weatherPeriod.isRunning()) {
			ClimateManager.AirFront airFront = new ClimateManager.AirFront();
			airFront.strength = float1;
			airFront.type = boolean1 ? 1 : -1;
			GameTime gameTime = GameTime.getInstance();
			this.weatherPeriod.init(airFront, this.worldAgeHours, gameTime.getYear(), gameTime.getMonth(), gameTime.getDayPlusOne());
			return true;
		} else {
			return false;
		}
	}

	public boolean triggerCustomWeatherStage(int int1, float float1) {
		if (!GameClient.bClient && !this.weatherPeriod.isRunning()) {
			ClimateManager.AirFront airFront = new ClimateManager.AirFront();
			airFront.strength = 0.95F;
			airFront.type = 1;
			GameTime gameTime = GameTime.getInstance();
			this.weatherPeriod.init(airFront, this.worldAgeHours, gameTime.getYear(), gameTime.getMonth(), gameTime.getDayPlusOne(), int1, float1);
			return true;
		} else {
			return false;
		}
	}

	private void updateOnTick() {
		int int1;
		for (int1 = 0; int1 < this.climateColors.length; ++int1) {
			this.climateColors[int1].calculate();
		}

		for (int1 = 0; int1 < this.climateFloats.length; ++int1) {
			this.climateFloats[int1].calculate();
		}

		for (int1 = 0; int1 < this.climateBooleans.length; ++int1) {
			this.climateBooleans[int1].calculate();
		}
	}

	private void updateTestFlare() {
		WorldFlares.update();
	}

	public void launchFlare() {
		DebugLog.log("Launching improved flare.");
		IsoPlayer player = IsoPlayer.getInstance();
		float float1 = 0.0F;
		WorldFlares.launchFlare(7200.0F, (int)player.getX(), (int)player.getY(), 50, float1, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
		if (IsoPlayer.getInstance() != null && !this.flareLaunched) {
			this.flareLaunched = true;
			this.flareLifeTime = 0.0F;
			this.flareMaxLifeTime = 7200.0F;
			this.flareIntensity.overrideCurrentValue(1.0F);
			this.flareIntens = 1.0F;
			this.nextRandomTargetIntens = 10;
		}
	}

	protected double getAirMassNoiseFrequencyMod(int int1) {
		if (int1 == 1) {
			return 300.0;
		} else if (int1 == 2) {
			return 240.0;
		} else {
			if (int1 != 3) {
				if (int1 == 4) {
					return 145.0;
				}

				if (int1 == 5) {
					return 120.0;
				}
			}

			return 166.0;
		}
	}

	protected float getRainTimeMultiplierMod(int int1) {
		if (int1 == 1) {
			return 0.5F;
		} else if (int1 == 2) {
			return 0.75F;
		} else if (int1 == 4) {
			return 1.25F;
		} else {
			return int1 == 5 ? 1.5F : 1.0F;
		}
	}

	private void updateValues() {
		if (this.tickIsDayChange && Core.bDebug && !GameClient.bClient && !GameServer.bServer) {
			ErosionMain.getInstance().DebugUpdateMapNow();
		}

		this.climateValues.updateValues(this.worldAgeHours, this.gt.getTimeOfDay(), this.currentDay, this.nextDay);
		this.airMass = this.climateValues.getNoiseAirmass();
		this.airMassTemperature = this.climateValues.getAirMassTemperature();
		int int1;
		if (this.tickIsHourChange) {
			int1 = this.airMass < 0.0F ? -1 : 1;
			if (this.currentFront.type != int1) {
				if (!this.DISABLE_WEATHER_GENERATION && (!WINTER_IS_COMING || WINTER_IS_COMING && GameTime.instance.getWorldAgeHours() > 96.0)) {
					if (THE_DESCENDING_FOG) {
						this.currentFront.type = -1;
						this.currentFront.strength = Rand.Next(0.2F, 0.45F);
						this.weatherPeriod.init(this.currentFront, this.worldAgeHours, this.gt.getYear(), this.gt.getMonth(), this.gt.getDayPlusOne());
					} else {
						this.CalculateWeatherFrontStrength(this.gt.getYear(), this.gt.getMonth(), this.gt.getDayPlusOne(), this.currentFront);
						this.weatherPeriod.init(this.currentFront, this.worldAgeHours, this.gt.getYear(), this.gt.getMonth(), this.gt.getDayPlusOne());
					}
				}

				this.currentFront.setFrontType(int1);
			}

			if (!WINTER_IS_COMING && !THE_DESCENDING_FOG && GameTime.instance.getWorldAgeHours() >= 72.0 && GameTime.instance.getWorldAgeHours() <= 96.0 && !this.DISABLE_WEATHER_GENERATION && !this.weatherPeriod.isRunning() && Rand.Next(0, 1000) < 50) {
			}

			if (this.tickIsDayChange) {
			}
		}

		this.dayDoFog = this.climateValues.isDayDoFog();
		this.dayFogStrength = this.climateValues.getDayFogStrength();
		if (PerformanceSettings.FogQuality == 2) {
			this.dayFogStrength = 0.5F + 0.5F * this.dayFogStrength;
		} else {
			this.dayFogStrength = 0.2F + 0.8F * this.dayFogStrength;
		}

		this.baseTemperature = this.climateValues.getBaseTemperature();
		this.dayLightLagged = this.climateValues.getDayLightLagged();
		this.nightLagged = this.climateValues.getDayLightLagged();
		this.temperature.internalValue = this.climateValues.getTemperature();
		this.precipitationIsSnow.internalValue = this.climateValues.isTemperatureIsSnow();
		this.humidity.internalValue = this.climateValues.getHumidity();
		this.windIntensity.internalValue = this.climateValues.getWindIntensity();
		this.windAngleIntensity.internalValue = this.climateValues.getWindAngleIntensity();
		this.windPower = this.windIntensity.internalValue;
		this.currentFront.setFrontWind(this.climateValues.getWindAngleDegrees());
		this.cloudIntensity.internalValue = this.climateValues.getCloudIntensity();
		this.precipitationIntensity.internalValue = 0.0F;
		this.nightStrength.internalValue = this.climateValues.getNightStrength();
		this.dayLightStrength.internalValue = this.climateValues.getDayLightStrength();
		this.ambient.internalValue = this.climateValues.getAmbient();
		this.desaturation.internalValue = this.climateValues.getDesaturation();
		int1 = this.season.getSeason();
		float float1 = this.season.getSeasonProgression();
		float float2 = 0.0F;
		int int2 = 0;
		int int3 = 0;
		if (int1 == 2) {
			int2 = ClimateManager.SeasonColor.SPRING;
			int3 = ClimateManager.SeasonColor.SUMMER;
			float2 = 0.5F + float1 * 0.5F;
		} else if (int1 == 3) {
			int2 = ClimateManager.SeasonColor.SUMMER;
			int3 = ClimateManager.SeasonColor.FALL;
			float2 = float1 * 0.5F;
		} else if (int1 == 4) {
			if (float1 < 0.5F) {
				int2 = ClimateManager.SeasonColor.SUMMER;
				int3 = ClimateManager.SeasonColor.FALL;
				float2 = 0.5F + float1;
			} else {
				int2 = ClimateManager.SeasonColor.FALL;
				int3 = ClimateManager.SeasonColor.WINTER;
				float2 = float1 - 0.5F;
			}
		} else if (int1 == 5) {
			if (float1 < 0.5F) {
				int2 = ClimateManager.SeasonColor.FALL;
				int3 = ClimateManager.SeasonColor.WINTER;
				float2 = 0.5F + float1;
			} else {
				int2 = ClimateManager.SeasonColor.WINTER;
				int3 = ClimateManager.SeasonColor.SPRING;
				float2 = float1 - 0.5F;
			}
		} else if (int1 == 1) {
			if (float1 < 0.5F) {
				int2 = ClimateManager.SeasonColor.WINTER;
				int3 = ClimateManager.SeasonColor.SPRING;
				float2 = 0.5F + float1;
			} else {
				int2 = ClimateManager.SeasonColor.SPRING;
				int3 = ClimateManager.SeasonColor.SUMMER;
				float2 = float1 - 0.5F;
			}
		}

		float float3 = this.climateValues.getCloudyT();
		this.colDawn = this.seasonColorDawn.update(float3, float2, int2, int3);
		this.colDay = this.seasonColorDay.update(float3, float2, int2, int3);
		this.colDusk = this.seasonColorDusk.update(float3, float2, int2, int3);
		float float4 = this.climateValues.getTime();
		float float5 = this.climateValues.getDawn();
		float float6 = this.climateValues.getDusk();
		float float7 = this.climateValues.getNoon();
		float float8 = this.climateValues.getDayFogDuration();
		float float9;
		float float10;
		if (!THE_DESCENDING_FOG) {
			if (this.dayDoFog && this.dayFogStrength > 0.0F && float4 > float5 - 2.0F && float4 < float5 + float8) {
				float9 = this.getTimeLerpHours(float4, float5 - 2.0F, float5 + float8, true);
				float9 = clamp(0.0F, 1.0F, float9 * (float8 / 3.0F));
				this.fogLerpValue = float9;
				this.cloudIntensity.internalValue = lerp(float9, this.cloudIntensity.internalValue, 0.0F);
				float10 = this.dayFogStrength;
				this.fogIntensity.internalValue = clerp(float9, 0.0F, float10);
				if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
					if (PerformanceSettings.FogQuality == 2) {
						this.desaturation.internalValue = clerp(float9, this.desaturation.internalValue, 0.8F * float10);
					} else {
						this.desaturation.internalValue = clerp(float9, this.desaturation.internalValue, 0.65F * float10);
					}
				} else {
					this.desaturation.internalValue = clerp(float9, this.desaturation.internalValue, 0.8F * float10);
				}
			} else {
				this.fogIntensity.internalValue = 0.0F;
			}
		} else {
			if (this.gt.getWorldAgeHours() < 72.0) {
				this.fogIntensity.internalValue = (float)this.gt.getWorldAgeHours() / 72.0F;
			} else {
				this.fogIntensity.internalValue = 1.0F;
			}

			this.cloudIntensity.internalValue = Math.min(this.cloudIntensity.internalValue, 1.0F - this.fogIntensity.internalValue);
			if (this.weatherPeriod.isRunning()) {
				this.fogIntensity.internalValue = Math.min(this.fogIntensity.internalValue, 0.6F);
			}

			if (PerformanceSettings.FogQuality == 2) {
				ClimateManager.ClimateFloat climateFloat = this.fogIntensity;
				climateFloat.internalValue *= 0.93F;
				this.desaturation.internalValue = 0.8F * this.fogIntensity.internalValue;
			} else {
				this.desaturation.internalValue = 0.65F * this.fogIntensity.internalValue;
			}
		}

		this.humidity.internalValue = clamp01(this.humidity.internalValue + this.fogIntensity.internalValue * 0.6F);
		float9 = 0.6F * this.climateValues.getDayLightStrengthBase();
		float10 = 0.4F;
		float float11 = 0.25F * this.climateValues.getDayLightStrengthBase();
		if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
			float11 = 0.8F * this.climateValues.getDayLightStrengthBase();
		}

		float float12;
		if (!(float4 < float5) && !(float4 > float6)) {
			if (float4 < float7 + 2.0F) {
				float12 = (float4 - float5) / (float7 + 2.0F - float5);
				this.colDawn.interp(this.colDay, float12, this.globalLight.internalValue);
				this.globalLightIntensity.internalValue = lerp(float12, float11, float9);
			} else {
				float12 = (float4 - (float7 + 2.0F)) / (float6 - (float7 + 2.0F));
				this.colDay.interp(this.colDusk, float12, this.globalLight.internalValue);
				this.globalLightIntensity.internalValue = lerp(float12, float9, float11);
			}
		} else {
			float float13 = 24.0F - float6 + float5;
			if (float4 > float6) {
				float12 = (float4 - float6) / float13;
				this.colDusk.interp(this.colDawn, float12, this.globalLight.internalValue);
			} else {
				float12 = (24.0F - float6 + float4) / float13;
				this.colDusk.interp(this.colDawn, float12, this.globalLight.internalValue);
			}

			this.globalLightIntensity.internalValue = lerp(this.climateValues.getLerpNight(), float11, float10);
		}

		if (this.fogIntensity.internalValue > 0.0F) {
			if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
				if (PerformanceSettings.FogQuality == 2) {
					this.globalLight.internalValue.interp(this.colFog, this.fogIntensity.internalValue, this.globalLight.internalValue);
				} else {
					this.globalLight.internalValue.interp(this.colFogNew, this.fogIntensity.internalValue, this.globalLight.internalValue);
				}
			} else {
				this.globalLight.internalValue.interp(this.colFogLegacy, this.fogIntensity.internalValue, this.globalLight.internalValue);
			}

			this.globalLightIntensity.internalValue = clerp(this.fogLerpValue, this.globalLightIntensity.internalValue, 0.8F);
		}

		this.colNightNoMoon.interp(this.colNightMoon, ClimateMoon.getMoonFloat(), this.colNight);
		this.globalLight.internalValue.interp(this.colNight, this.nightStrength.internalValue, this.globalLight.internalValue);
		IsoPlayer[] playerArray = IsoPlayer.players;
		for (int int4 = 0; int4 < playerArray.length; ++int4) {
			IsoPlayer player = playerArray[int4];
			if (player != null) {
				player.dirtyRecalcGridStackTime = 1.0F;
			}
		}
	}

	private void updateViewDistance() {
		float float1 = this.dayLightStrength.finalValue;
		float float2 = this.fogIntensity.finalValue;
		float float3 = 19.0F - float2 * 8.0F;
		float float4 = float3 + 4.0F + 7.0F * float1 * (1.0F - float2);
		float3 *= 3.0F;
		float4 *= 3.0F;
		this.gt.setViewDistMin(float3);
		this.gt.setViewDistMax(float4);
		this.viewDistance.internalValue = float3 + (float4 - float3) * float1;
		this.viewDistance.finalValue = this.viewDistance.internalValue;
	}

	public void setSeasonColorDawn(int int1, int int2, float float1, float float2, float float3, float float4, boolean boolean1) {
		if (boolean1) {
			this.seasonColorDawn.setColorExterior(int1, int2, float1, float2, float3, float4);
		} else {
			this.seasonColorDawn.setColorInterior(int1, int2, float1, float2, float3, float4);
		}
	}

	public void setSeasonColorDay(int int1, int int2, float float1, float float2, float float3, float float4, boolean boolean1) {
		if (boolean1) {
			this.seasonColorDay.setColorExterior(int1, int2, float1, float2, float3, float4);
		} else {
			this.seasonColorDay.setColorInterior(int1, int2, float1, float2, float3, float4);
		}
	}

	public void setSeasonColorDusk(int int1, int int2, float float1, float float2, float float3, float float4, boolean boolean1) {
		if (boolean1) {
			this.seasonColorDusk.setColorExterior(int1, int2, float1, float2, float3, float4);
		} else {
			this.seasonColorDusk.setColorInterior(int1, int2, float1, float2, float3, float4);
		}
	}

	public ClimateColorInfo getSeasonColor(int int1, int int2, int int3) {
		ClimateManager.SeasonColor seasonColor = null;
		if (int1 == 0) {
			seasonColor = this.seasonColorDawn;
		} else if (int1 == 1) {
			seasonColor = this.seasonColorDay;
		} else if (int1 == 2) {
			seasonColor = this.seasonColorDusk;
		}

		return seasonColor != null ? seasonColor.getColor(int2, int3) : null;
	}

	private void initSeasonColors() {
		ClimateManager.SeasonColor seasonColor = new ClimateManager.SeasonColor();
		seasonColor.setIgnoreNormal(true);
		this.seasonColorDawn = seasonColor;
		seasonColor = new ClimateManager.SeasonColor();
		seasonColor.setIgnoreNormal(true);
		this.seasonColorDay = seasonColor;
		seasonColor = new ClimateManager.SeasonColor();
		seasonColor.setIgnoreNormal(false);
		this.seasonColorDusk = seasonColor;
	}

	public void save(DataOutputStream dataOutputStream) throws IOException {
		if (GameClient.bClient && !GameServer.bServer) {
			dataOutputStream.writeByte(0);
		} else {
			dataOutputStream.writeByte(1);
			dataOutputStream.writeDouble(this.simplexOffsetA);
			dataOutputStream.writeDouble(this.simplexOffsetB);
			dataOutputStream.writeDouble(this.simplexOffsetC);
			dataOutputStream.writeDouble(this.simplexOffsetD);
			this.currentFront.save(dataOutputStream);
			dataOutputStream.writeFloat(this.snowFracNow);
			dataOutputStream.writeFloat(this.snowStrength);
			dataOutputStream.writeBoolean(this.canDoWinterSprites);
			dataOutputStream.writeBoolean(this.dayDoFog);
			dataOutputStream.writeFloat(this.dayFogStrength);
		}

		this.weatherPeriod.save(dataOutputStream);
		this.thunderStorm.save(dataOutputStream);
		if (GameServer.bServer) {
			this.desaturation.saveAdmin(dataOutputStream);
			this.globalLightIntensity.saveAdmin(dataOutputStream);
			this.nightStrength.saveAdmin(dataOutputStream);
			this.precipitationIntensity.saveAdmin(dataOutputStream);
			this.temperature.saveAdmin(dataOutputStream);
			this.fogIntensity.saveAdmin(dataOutputStream);
			this.windIntensity.saveAdmin(dataOutputStream);
			this.windAngleIntensity.saveAdmin(dataOutputStream);
			this.cloudIntensity.saveAdmin(dataOutputStream);
			this.ambient.saveAdmin(dataOutputStream);
			this.viewDistance.saveAdmin(dataOutputStream);
			this.dayLightStrength.saveAdmin(dataOutputStream);
			this.globalLight.saveAdmin(dataOutputStream);
			this.precipitationIsSnow.saveAdmin(dataOutputStream);
		}

		if (this.modDataTable != null) {
			dataOutputStream.writeByte(1);
			this.modDataTable.save(dataOutputStream);
		} else {
			dataOutputStream.writeByte(0);
		}

		if (GameServer.bServer) {
			this.humidity.saveAdmin(dataOutputStream);
		}
	}

	public void load(DataInputStream dataInputStream, int int1) throws IOException {
		boolean boolean1 = dataInputStream.readByte() == 1;
		if (boolean1) {
			this.simplexOffsetA = dataInputStream.readDouble();
			this.simplexOffsetB = dataInputStream.readDouble();
			this.simplexOffsetC = dataInputStream.readDouble();
			this.simplexOffsetD = dataInputStream.readDouble();
			this.currentFront.load(dataInputStream);
			this.snowFracNow = dataInputStream.readFloat();
			this.snowStrength = dataInputStream.readFloat();
			this.canDoWinterSprites = dataInputStream.readBoolean();
			this.dayDoFog = dataInputStream.readBoolean();
			this.dayFogStrength = dataInputStream.readFloat();
		}

		this.weatherPeriod.load(dataInputStream, int1);
		this.thunderStorm.load(dataInputStream);
		if (int1 >= 140 && GameServer.bServer) {
			this.desaturation.loadAdmin(dataInputStream, int1);
			this.globalLightIntensity.loadAdmin(dataInputStream, int1);
			this.nightStrength.loadAdmin(dataInputStream, int1);
			this.precipitationIntensity.loadAdmin(dataInputStream, int1);
			this.temperature.loadAdmin(dataInputStream, int1);
			this.fogIntensity.loadAdmin(dataInputStream, int1);
			this.windIntensity.loadAdmin(dataInputStream, int1);
			this.windAngleIntensity.loadAdmin(dataInputStream, int1);
			this.cloudIntensity.loadAdmin(dataInputStream, int1);
			this.ambient.loadAdmin(dataInputStream, int1);
			this.viewDistance.loadAdmin(dataInputStream, int1);
			this.dayLightStrength.loadAdmin(dataInputStream, int1);
			this.globalLight.loadAdmin(dataInputStream, int1);
			this.precipitationIsSnow.loadAdmin(dataInputStream, int1);
		}

		if (int1 >= 141 && dataInputStream.readByte() == 1) {
			if (this.modDataTable == null) {
				this.modDataTable = LuaManager.platform.newTable();
			}

			this.modDataTable.load(dataInputStream, int1);
		}

		if (int1 >= 150 && GameServer.bServer) {
			this.humidity.loadAdmin(dataInputStream, int1);
		}

		this.climateValues = new ClimateValues(this);
	}

	public void postCellLoadSetSnow() {
		IsoWorld.instance.CurrentCell.setSnowTarget((int)(this.snowFracNow * 100.0F));
		ErosionIceQueen.instance.setSnow(this.canDoWinterSprites && this.snowFracNow > 0.2F);
	}

	public void forceDayInfoUpdate() {
		this.currentDay.day = -1;
		this.currentDay.month = -1;
		this.currentDay.year = -1;
		this.gt = GameTime.getInstance();
		this.updateDayInfo(this.gt.getDayPlusOne(), this.gt.getMonth(), this.gt.getYear());
		this.currentDay.hour = this.gt.getHour();
		this.currentDay.minutes = this.gt.getMinutes();
	}

	private void updateDayInfo(int int1, int int2, int int3) {
		this.tickIsDayChange = false;
		if (this.currentDay == null || this.currentDay.day != int1 || this.currentDay.month != int2 || this.currentDay.year != int3) {
			this.tickIsDayChange = this.currentDay != null;
			if (this.currentDay == null) {
				this.currentDay = new ClimateManager.DayInfo();
			}

			this.setDayInfo(this.currentDay, int1, int2, int3, 0);
			if (this.previousDay == null) {
				this.previousDay = new ClimateManager.DayInfo();
				this.previousDay.season = this.season.clone();
			}

			this.setDayInfo(this.previousDay, int1, int2, int3, -1);
			if (this.nextDay == null) {
				this.nextDay = new ClimateManager.DayInfo();
				this.nextDay.season = this.season.clone();
			}

			this.setDayInfo(this.nextDay, int1, int2, int3, 1);
		}
	}

	protected void setDayInfo(ClimateManager.DayInfo dayInfo, int int1, int int2, int int3, int int4) {
		dayInfo.calendar = new GregorianCalendar(int3, int2, int1, 0, 0);
		dayInfo.calendar.add(5, int4);
		dayInfo.day = dayInfo.calendar.get(5);
		dayInfo.month = dayInfo.calendar.get(2);
		dayInfo.year = dayInfo.calendar.get(1);
		dayInfo.dateValue = dayInfo.calendar.getTime().getTime();
		if (dayInfo.season == null) {
			dayInfo.season = this.season.clone();
		}

		dayInfo.season.setDay(dayInfo.day, dayInfo.month, dayInfo.year);
	}

	protected final void transmitClimatePacket(ClimateManager.ClimateNetAuth climateNetAuth, byte byte1, UdpConnection udpConnection) {
		if (GameClient.bClient || GameServer.bServer) {
			if (climateNetAuth == ClimateManager.ClimateNetAuth.Denied) {
				DebugLog.log("Denied ClimatePacket, id = " + byte1 + ", isClient = " + GameClient.bClient);
			} else {
				if (GameClient.bClient && (climateNetAuth == ClimateManager.ClimateNetAuth.ClientOnly || climateNetAuth == ClimateManager.ClimateNetAuth.ClientAndServer)) {
					try {
						if (this.writePacketContents(GameClient.connection, byte1)) {
							PacketTypes.PacketType.ClimateManagerPacket.send(GameClient.connection);
						} else {
							GameClient.connection.cancelPacket();
						}
					} catch (Exception exception) {
						DebugLog.log(exception.getMessage());
					}
				}

				if (GameServer.bServer && (climateNetAuth == ClimateManager.ClimateNetAuth.ServerOnly || climateNetAuth == ClimateManager.ClimateNetAuth.ClientAndServer)) {
					try {
						for (int int1 = 0; int1 < GameServer.udpEngine.connections.size(); ++int1) {
							UdpConnection udpConnection2 = (UdpConnection)GameServer.udpEngine.connections.get(int1);
							if (udpConnection == null || udpConnection != udpConnection2) {
								if (this.writePacketContents(udpConnection2, byte1)) {
									PacketTypes.PacketType.ClimateManagerPacket.send(udpConnection2);
								} else {
									udpConnection2.cancelPacket();
								}
							}
						}
					} catch (Exception exception2) {
						DebugLog.log(exception2.getMessage());
					}
				}
			}
		}
	}

	private boolean writePacketContents(UdpConnection udpConnection, byte byte1) throws IOException {
		if (!GameClient.bClient && !GameServer.bServer) {
			return false;
		} else {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.ClimateManagerPacket.doPacket(byteBufferWriter);
			ByteBuffer byteBuffer = byteBufferWriter.bb;
			byteBuffer.put(byte1);
			int int1;
			switch (byte1) {
			case 0: 
				if (this.networkPrint) {
					DebugLog.log("clim: send PacketUpdateClimateVars");
				}

				for (int1 = 0; int1 < this.climateFloats.length; ++int1) {
					byteBuffer.putFloat(this.climateFloats[int1].finalValue);
				}

				for (int1 = 0; int1 < this.climateColors.length; ++int1) {
					this.climateColors[int1].finalValue.write(byteBuffer);
				}

				for (int1 = 0; int1 < this.climateBooleans.length; ++int1) {
					byteBuffer.put((byte)(this.climateBooleans[int1].finalValue ? 1 : 0));
				}

				byteBuffer.putFloat(this.airMass);
				byteBuffer.putFloat(this.airMassDaily);
				byteBuffer.putFloat(this.airMassTemperature);
				byteBuffer.putFloat(this.snowFracNow);
				byteBuffer.putFloat(this.snowStrength);
				byteBuffer.putFloat(this.windPower);
				byteBuffer.put((byte)(this.dayDoFog ? 1 : 0));
				byteBuffer.putFloat(this.dayFogStrength);
				byteBuffer.put((byte)(this.canDoWinterSprites ? 1 : 0));
				this.weatherPeriod.writeNetWeatherData(byteBuffer);
				return true;
			
			case 1: 
				if (this.networkPrint) {
					DebugLog.log("clim: send PacketWeatherUpdate");
				}

				this.weatherPeriod.writeNetWeatherData(byteBuffer);
				return true;
			
			case 2: 
				if (this.networkPrint) {
					DebugLog.log("clim: send PacketThunderEvent");
				}

				this.thunderStorm.writeNetThunderEvent(byteBuffer);
				return true;
			
			case 3: 
				if (this.networkPrint) {
					DebugLog.log("clim: send PacketFlare");
				}

				return true;
			
			case 4: 
				if (!GameServer.bServer) {
					return false;
				}

				if (this.networkPrint) {
					DebugLog.log("clim: send PacketAdminVarsUpdate");
				}

				for (int1 = 0; int1 < this.climateFloats.length; ++int1) {
					this.climateFloats[int1].writeAdmin(byteBuffer);
				}

				for (int1 = 0; int1 < this.climateColors.length; ++int1) {
					this.climateColors[int1].writeAdmin(byteBuffer);
				}

				for (int1 = 0; int1 < this.climateBooleans.length; ++int1) {
					this.climateBooleans[int1].writeAdmin(byteBuffer);
				}

				return true;
			
			case 5: 
				if (!GameClient.bClient) {
					return false;
				}

				if (this.networkPrint) {
					DebugLog.log("clim: send PacketRequestAdminVars");
				}

				byteBuffer.put((byte)1);
				return true;
			
			case 6: 
				if (!GameClient.bClient) {
					return false;
				}

				if (this.networkPrint) {
					DebugLog.log("clim: send PacketClientChangedAdminVars");
				}

				for (int1 = 0; int1 < this.climateFloats.length; ++int1) {
					this.climateFloats[int1].writeAdmin(byteBuffer);
				}

				for (int1 = 0; int1 < this.climateColors.length; ++int1) {
					this.climateColors[int1].writeAdmin(byteBuffer);
				}

				for (int1 = 0; int1 < this.climateBooleans.length; ++int1) {
					this.climateBooleans[int1].writeAdmin(byteBuffer);
				}

				return true;
			
			case 7: 
				if (!GameClient.bClient) {
					return false;
				}

				if (this.networkPrint) {
					DebugLog.log("clim: send PacketClientChangedWeather");
				}

				byteBuffer.put((byte)(this.netInfo.IsStopWeather ? 1 : 0));
				byteBuffer.put((byte)(this.netInfo.IsTrigger ? 1 : 0));
				byteBuffer.put((byte)(this.netInfo.IsGenerate ? 1 : 0));
				byteBuffer.putFloat(this.netInfo.TriggerDuration);
				byteBuffer.put((byte)(this.netInfo.TriggerStorm ? 1 : 0));
				byteBuffer.put((byte)(this.netInfo.TriggerTropical ? 1 : 0));
				byteBuffer.put((byte)(this.netInfo.TriggerBlizzard ? 1 : 0));
				byteBuffer.putFloat(this.netInfo.GenerateStrength);
				byteBuffer.putInt(this.netInfo.GenerateFront);
				return true;
			
			default: 
				return false;
			
			}
		}
	}

	public final void receiveClimatePacket(ByteBuffer byteBuffer, UdpConnection udpConnection) throws IOException {
		if (GameClient.bClient || GameServer.bServer) {
			byte byte1 = byteBuffer.get();
			this.readPacketContents(byteBuffer, byte1, udpConnection);
		}
	}

	private boolean readPacketContents(ByteBuffer byteBuffer, byte byte1, UdpConnection udpConnection) throws IOException {
		int int1;
		switch (byte1) {
		case 0: 
			if (!GameClient.bClient) {
				return false;
			}

			if (this.networkPrint) {
				DebugLog.log("clim: receive PacketUpdateClimateVars");
			}

			for (int int2 = 0; int2 < this.climateFloats.length; ++int2) {
				ClimateManager.ClimateFloat climateFloat = this.climateFloats[int2];
				climateFloat.internalValue = climateFloat.finalValue;
				climateFloat.setOverride(byteBuffer.getFloat(), 0.0F);
			}

			for (int int3 = 0; int3 < this.climateColors.length; ++int3) {
				ClimateManager.ClimateColor climateColor = this.climateColors[int3];
				climateColor.internalValue.setTo(climateColor.finalValue);
				climateColor.setOverride(byteBuffer, 0.0F);
			}

			for (int int4 = 0; int4 < this.climateBooleans.length; ++int4) {
				ClimateManager.ClimateBool climateBool = this.climateBooleans[int4];
				climateBool.setOverride(byteBuffer.get() == 1);
			}

			this.airMass = byteBuffer.getFloat();
			this.airMassDaily = byteBuffer.getFloat();
			this.airMassTemperature = byteBuffer.getFloat();
			this.snowFracNow = byteBuffer.getFloat();
			this.snowStrength = byteBuffer.getFloat();
			this.windPower = byteBuffer.getFloat();
			this.dayDoFog = byteBuffer.get() == 1;
			this.dayFogStrength = byteBuffer.getFloat();
			this.canDoWinterSprites = byteBuffer.get() == 1;
			long long1 = System.currentTimeMillis();
			if ((float)(long1 - this.networkUpdateStamp) < this.networkLerpTime) {
				++this.networkAdjustVal;
				if (this.networkAdjustVal > 10.0F) {
					this.networkAdjustVal = 10.0F;
				}
			} else {
				--this.networkAdjustVal;
				if (this.networkAdjustVal < 0.0F) {
					this.networkAdjustVal = 0.0F;
				}
			}

			if (this.networkAdjustVal > 0.0F) {
				this.networkLerpTime = this.networkLerpTimeBase / this.networkAdjustVal;
			} else {
				this.networkLerpTime = this.networkLerpTimeBase;
			}

			this.networkUpdateStamp = long1;
			this.weatherPeriod.readNetWeatherData(byteBuffer);
			return true;
		
		case 1: 
			if (this.networkPrint) {
				DebugLog.log("clim: receive PacketWeatherUpdate");
			}

			this.weatherPeriod.readNetWeatherData(byteBuffer);
			return true;
		
		case 2: 
			if (this.networkPrint) {
				DebugLog.log("clim: receive PacketThunderEvent");
			}

			this.thunderStorm.readNetThunderEvent(byteBuffer);
			return true;
		
		case 3: 
			if (this.networkPrint) {
				DebugLog.log("clim: receive PacketFlare");
			}

			return true;
		
		case 4: 
			if (!GameClient.bClient) {
				return false;
			}

			if (this.networkPrint) {
				DebugLog.log("clim: receive PacketAdminVarsUpdate");
			}

			for (int1 = 0; int1 < this.climateFloats.length; ++int1) {
				this.climateFloats[int1].readAdmin(byteBuffer);
			}

			for (int1 = 0; int1 < this.climateColors.length; ++int1) {
				this.climateColors[int1].readAdmin(byteBuffer);
			}

			for (int1 = 0; int1 < this.climateBooleans.length; ++int1) {
				this.climateBooleans[int1].readAdmin(byteBuffer);
			}

			return true;
		
		case 5: 
			if (!GameServer.bServer) {
				return false;
			}

			if (this.networkPrint) {
				DebugLog.log("clim: receive PacketRequestAdminVars");
			}

			byteBuffer.get();
			this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)4, (UdpConnection)null);
			return true;
		
		case 6: 
			if (!GameServer.bServer) {
				return false;
			}

			if (this.networkPrint) {
				DebugLog.log("clim: receive PacketClientChangedAdminVars");
			}

			for (int1 = 0; int1 < this.climateFloats.length; ++int1) {
				this.climateFloats[int1].readAdmin(byteBuffer);
			}

			for (int1 = 0; int1 < this.climateColors.length; ++int1) {
				this.climateColors[int1].readAdmin(byteBuffer);
			}

			for (int1 = 0; int1 < this.climateBooleans.length; ++int1) {
				this.climateBooleans[int1].readAdmin(byteBuffer);
				if (int1 == 0) {
					boolean boolean1 = this.climateBooleans[int1].adminValue;
					DebugLog.log("Snow = " + boolean1 + ", enabled = " + this.climateBooleans[int1].isAdminOverride);
				}
			}

			this.serverReceiveClientChangeAdminVars();
			return true;
		
		case 7: 
			if (!GameServer.bServer) {
				return false;
			}

			if (this.networkPrint) {
				DebugLog.log("clim: receive PacketClientChangedWeather");
			}

			this.netInfo.IsStopWeather = byteBuffer.get() == 1;
			this.netInfo.IsTrigger = byteBuffer.get() == 1;
			this.netInfo.IsGenerate = byteBuffer.get() == 1;
			this.netInfo.TriggerDuration = byteBuffer.getFloat();
			this.netInfo.TriggerStorm = byteBuffer.get() == 1;
			this.netInfo.TriggerTropical = byteBuffer.get() == 1;
			this.netInfo.TriggerBlizzard = byteBuffer.get() == 1;
			this.netInfo.GenerateStrength = byteBuffer.getFloat();
			this.netInfo.GenerateFront = byteBuffer.getInt();
			this.serverReceiveClientChangeWeather();
			return true;
		
		default: 
			return false;
		
		}
	}

	private void serverReceiveClientChangeAdminVars() {
		if (GameServer.bServer) {
			if (this.networkPrint) {
				DebugLog.log("clim: serverReceiveClientChangeAdminVars");
			}

			this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)4, (UdpConnection)null);
			this.updateOnTick();
			this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, (UdpConnection)null);
		}
	}

	private void serverReceiveClientChangeWeather() {
		if (GameServer.bServer) {
			if (this.networkPrint) {
				DebugLog.log("clim: serverReceiveClientChangeWeather");
			}

			if (this.netInfo.IsStopWeather) {
				if (this.networkPrint) {
					DebugLog.log("clim: IsStopWeather");
				}

				this.stopWeatherAndThunder();
			} else if (this.netInfo.IsTrigger) {
				this.stopWeatherAndThunder();
				if (this.netInfo.TriggerStorm) {
					if (this.networkPrint) {
						DebugLog.log("clim: Trigger Storm");
					}

					this.triggerCustomWeatherStage(3, this.netInfo.TriggerDuration);
				} else if (this.netInfo.TriggerTropical) {
					if (this.networkPrint) {
						DebugLog.log("clim: Trigger Tropical");
					}

					this.triggerCustomWeatherStage(8, this.netInfo.TriggerDuration);
				} else if (this.netInfo.TriggerBlizzard) {
					if (this.networkPrint) {
						DebugLog.log("clim: Trigger Blizzard");
					}

					this.triggerCustomWeatherStage(7, this.netInfo.TriggerDuration);
				}
			} else if (this.netInfo.IsGenerate) {
				if (this.networkPrint) {
					DebugLog.log("clim: IsGenerate");
				}

				this.stopWeatherAndThunder();
				this.triggerCustomWeather(this.netInfo.GenerateStrength, this.netInfo.GenerateFront == 0);
			}

			this.updateOnTick();
			this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, (UdpConnection)null);
		}
	}

	public void transmitServerStopWeather() {
		if (GameServer.bServer) {
			this.stopWeatherAndThunder();
			if (this.networkPrint) {
				DebugLog.log("clim: SERVER transmitStopWeather");
			}

			this.updateOnTick();
			this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, (UdpConnection)null);
		}
	}

	public void transmitServerTriggerStorm(float float1) {
		if (GameServer.bServer) {
			if (this.networkPrint) {
				DebugLog.log("clim: SERVER transmitTriggerStorm");
			}

			this.netInfo.TriggerDuration = float1;
			this.triggerCustomWeatherStage(3, this.netInfo.TriggerDuration);
			this.updateOnTick();
			this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, (UdpConnection)null);
		}
	}

	public void transmitServerTriggerLightning(int int1, int int2, boolean boolean1, boolean boolean2, boolean boolean3) {
		if (GameServer.bServer) {
			if (this.networkPrint) {
				DebugLog.log("clim: SERVER transmitTriggerLightning");
			}

			this.thunderStorm.triggerThunderEvent(int1, int2, boolean1, boolean2, boolean3);
		}
	}

	public void transmitServerStartRain(float float1) {
		if (GameServer.bServer) {
			this.precipitationIntensity.setAdminValue(clamp01(float1));
			this.precipitationIntensity.setEnableAdmin(true);
			this.updateOnTick();
			this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, (UdpConnection)null);
		}
	}

	public void transmitServerStopRain() {
		if (GameServer.bServer) {
			this.precipitationIntensity.setEnableAdmin(false);
			this.updateOnTick();
			this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, (UdpConnection)null);
		}
	}

	public void transmitRequestAdminVars() {
		if (this.networkPrint) {
			DebugLog.log("clim: transmitRequestAdminVars");
		}

		this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ClientOnly, (byte)5, (UdpConnection)null);
	}

	public void transmitClientChangeAdminVars() {
		if (this.networkPrint) {
			DebugLog.log("clim: transmitClientChangeAdminVars");
		}

		this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ClientOnly, (byte)6, (UdpConnection)null);
	}

	public void transmitStopWeather() {
		if (this.networkPrint) {
			DebugLog.log("clim: transmitStopWeather");
		}

		this.netInfo.reset();
		this.netInfo.IsStopWeather = true;
		this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ClientOnly, (byte)7, (UdpConnection)null);
	}

	public void transmitTriggerStorm(float float1) {
		if (this.networkPrint) {
			DebugLog.log("clim: transmitTriggerStorm");
		}

		this.netInfo.reset();
		this.netInfo.IsTrigger = true;
		this.netInfo.TriggerStorm = true;
		this.netInfo.TriggerDuration = float1;
		this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ClientOnly, (byte)7, (UdpConnection)null);
	}

	public void transmitTriggerTropical(float float1) {
		if (this.networkPrint) {
			DebugLog.log("clim: transmitTriggerTropical");
		}

		this.netInfo.reset();
		this.netInfo.IsTrigger = true;
		this.netInfo.TriggerTropical = true;
		this.netInfo.TriggerDuration = float1;
		this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ClientOnly, (byte)7, (UdpConnection)null);
	}

	public void transmitTriggerBlizzard(float float1) {
		if (this.networkPrint) {
			DebugLog.log("clim: transmitTriggerBlizzard");
		}

		this.netInfo.reset();
		this.netInfo.IsTrigger = true;
		this.netInfo.TriggerBlizzard = true;
		this.netInfo.TriggerDuration = float1;
		this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ClientOnly, (byte)7, (UdpConnection)null);
	}

	public void transmitGenerateWeather(float float1, int int1) {
		if (this.networkPrint) {
			DebugLog.log("clim: transmitGenerateWeather");
		}

		this.netInfo.reset();
		this.netInfo.IsGenerate = true;
		this.netInfo.GenerateStrength = clamp01(float1);
		this.netInfo.GenerateFront = int1;
		if (this.netInfo.GenerateFront < 0 || this.netInfo.GenerateFront > 1) {
			this.netInfo.GenerateFront = 0;
		}

		this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ClientOnly, (byte)7, (UdpConnection)null);
	}

	protected float getTimeLerpHours(float float1, float float2, float float3) {
		return this.getTimeLerpHours(float1, float2, float3, false);
	}

	protected float getTimeLerpHours(float float1, float float2, float float3, boolean boolean1) {
		return this.getTimeLerp(clamp(0.0F, 1.0F, float1 / 24.0F), clamp(0.0F, 1.0F, float2 / 24.0F), clamp(0.0F, 1.0F, float3 / 24.0F), boolean1);
	}

	protected float getTimeLerp(float float1, float float2, float float3) {
		return this.getTimeLerp(float1, float2, float3, false);
	}

	protected float getTimeLerp(float float1, float float2, float float3, boolean boolean1) {
		boolean boolean2 = float2 > float3;
		float float4;
		float float5;
		float float6;
		if (!boolean2) {
			if (!(float1 < float2) && !(float1 > float3)) {
				float4 = float1 - float2;
				float5 = float3 - float2;
				float6 = float5 * 0.5F;
				if (float4 < float6) {
					return boolean1 ? clerp(float4 / float6, 0.0F, 1.0F) : lerp(float4 / float6, 0.0F, 1.0F);
				} else {
					return boolean1 ? clerp((float4 - float6) / float6, 1.0F, 0.0F) : lerp((float4 - float6) / float6, 1.0F, 0.0F);
				}
			} else {
				return 0.0F;
			}
		} else if (float1 < float2 && float1 > float3) {
			return 0.0F;
		} else {
			float4 = 1.0F - float2;
			float5 = float1 >= float2 ? float1 - float2 : float1 + float4;
			float6 = float3 + float4;
			float float7 = float6 * 0.5F;
			if (float5 < float7) {
				return boolean1 ? clerp(float5 / float7, 0.0F, 1.0F) : lerp(float5 / float7, 0.0F, 1.0F);
			} else {
				return boolean1 ? clerp((float5 - float7) / float7, 1.0F, 0.0F) : lerp((float5 - float7) / float7, 1.0F, 0.0F);
			}
		}
	}

	public static float clamp01(float float1) {
		return clamp(0.0F, 1.0F, float1);
	}

	public static float clamp(float float1, float float2, float float3) {
		float3 = Math.min(float2, float3);
		float3 = Math.max(float1, float3);
		return float3;
	}

	public static int clamp(int int1, int int2, int int3) {
		int3 = Math.min(int2, int3);
		int3 = Math.max(int1, int3);
		return int3;
	}

	public static float lerp(float float1, float float2, float float3) {
		return float2 + float1 * (float3 - float2);
	}

	public static float clerp(float float1, float float2, float float3) {
		float float4 = (float)(1.0 - Math.cos((double)float1 * 3.141592653589793)) / 2.0F;
		return float2 * (1.0F - float4) + float3 * float4;
	}

	public static float normalizeRange(float float1, float float2) {
		return clamp(0.0F, 1.0F, float1 / float2);
	}

	public static float posToPosNegRange(float float1) {
		if (float1 > 0.5F) {
			return (float1 - 0.5F) * 2.0F;
		} else {
			return float1 < 0.5F ? -((0.5F - float1) * 2.0F) : 0.0F;
		}
	}

	public void execute_Simulation() {
		if (Core.bDebug) {
			ClimMngrDebug climMngrDebug = new ClimMngrDebug();
			short short1 = 365;
			short short2 = 5000;
			climMngrDebug.SimulateDays(short1, short2);
		}
	}

	public void execute_Simulation(int int1) {
		if (Core.bDebug) {
			ClimMngrDebug climMngrDebug = new ClimMngrDebug();
			climMngrDebug.setRainModOverride(int1);
			short short1 = 365;
			short short2 = 5000;
			climMngrDebug.SimulateDays(short1, short2);
		}
	}

	public void triggerKateBobIntroStorm(int int1, int int2, double double1, float float1, float float2, float float3, float float4) {
		this.triggerKateBobIntroStorm(int1, int2, double1, float1, float2, float3, float4, (ClimateColorInfo)null);
	}

	public void triggerKateBobIntroStorm(int int1, int int2, double double1, float float1, float float2, float float3, float float4, ClimateColorInfo climateColorInfo) {
		if (!GameClient.bClient) {
			this.stopWeatherAndThunder();
			if (this.weatherPeriod.startCreateModdedPeriod(true, float1, float3)) {
				this.weatherPeriod.setKateBobStormProgress(float2);
				this.weatherPeriod.setKateBobStormCoords(int1, int2);
				this.weatherPeriod.createAndAddStage(11, double1);
				this.weatherPeriod.createAndAddStage(2, double1 / 2.0);
				this.weatherPeriod.createAndAddStage(4, double1 / 4.0);
				this.weatherPeriod.endCreateModdedPeriod();
				if (climateColorInfo != null) {
					this.weatherPeriod.setCloudColor(climateColorInfo);
				} else {
					this.weatherPeriod.setCloudColor(this.weatherPeriod.getCloudColorBlueish());
				}

				IsoPuddles.PuddlesFloat puddlesFloat = IsoPuddles.getInstance().getPuddlesFloat(3);
				puddlesFloat.setFinalValue(float4);
				puddlesFloat = IsoPuddles.getInstance().getPuddlesFloat(1);
				puddlesFloat.setFinalValue(PZMath.clamp_01(float4 * 1.2F));
			}
		}
	}

	public double getSimplexOffsetA() {
		return this.simplexOffsetA;
	}

	public double getSimplexOffsetB() {
		return this.simplexOffsetB;
	}

	public double getSimplexOffsetC() {
		return this.simplexOffsetC;
	}

	public double getSimplexOffsetD() {
		return this.simplexOffsetD;
	}

	public double getWorldAgeHours() {
		return this.worldAgeHours;
	}

	public ClimateValues getClimateValuesCopy() {
		return this.climateValues.getCopy();
	}

	public void CopyClimateValues(ClimateValues climateValues) {
		this.climateValues.CopyValues(climateValues);
	}

	public ClimateForecaster getClimateForecaster() {
		return this.climateForecaster;
	}

	public ClimateHistory getClimateHistory() {
		return this.climateHistory;
	}

	public void CalculateWeatherFrontStrength(int int1, int int2, int int3, ClimateManager.AirFront airFront) {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(int1, int2, int3, 0, 0);
		gregorianCalendar.add(5, -3);
		if (this.climateValuesFronts == null) {
			this.climateValuesFronts = this.climateValues.getCopy();
		}

		int int4 = airFront.type;
		for (int int5 = 0; int5 < 4; ++int5) {
			this.climateValuesFronts.pollDate(gregorianCalendar);
			float float1 = this.climateValuesFronts.getAirFrontAirmass();
			int int6 = float1 < 0.0F ? -1 : 1;
			if (int6 == int4) {
				airFront.addDaySample(float1);
			}

			gregorianCalendar.add(5, 1);
		}

		DebugLog.log("Calculate weather front strength = " + airFront.getStrength());
	}

	public static String getWindAngleString(float float1) {
		for (int int1 = 0; int1 < windAngles.length; ++int1) {
			if (float1 < windAngles[int1]) {
				return windAngleStr[int1];
			}
		}

		return windAngleStr[windAngleStr.length - 1];
	}

	public void sendInitialState(UdpConnection udpConnection) throws IOException {
		if (GameServer.bServer) {
			if (this.writePacketContents(udpConnection, (byte)0)) {
				PacketTypes.PacketType.ClimateManagerPacket.send(udpConnection);
			} else {
				udpConnection.cancelPacket();
			}
		}
	}

	public boolean isUpdated() {
		return this.lastMinuteStamp != -1L;
	}

	public static class AirFront {
		private float days = 0.0F;
		private float maxNoise = 0.0F;
		private float totalNoise = 0.0F;
		private int type = 0;
		private float strength = 0.0F;
		private float tmpNoiseAbs = 0.0F;
		private float[] noiseCache = new float[2];
		private float noiseCacheValue = 0.0F;
		private float frontWindAngleDegrees = 0.0F;

		public float getDays() {
			return this.days;
		}

		public float getMaxNoise() {
			return this.maxNoise;
		}

		public float getTotalNoise() {
			return this.totalNoise;
		}

		public int getType() {
			return this.type;
		}

		public float getStrength() {
			return this.strength;
		}

		public float getAngleDegrees() {
			return this.frontWindAngleDegrees;
		}

		public AirFront() {
			this.reset();
		}

		public void setFrontType(int int1) {
			this.reset();
			this.type = int1;
		}

		protected void setFrontWind(float float1) {
			this.frontWindAngleDegrees = float1;
		}

		public void setStrength(float float1) {
			this.strength = float1;
		}

		protected void reset() {
			this.days = 0.0F;
			this.maxNoise = 0.0F;
			this.totalNoise = 0.0F;
			this.type = 0;
			this.strength = 0.0F;
			this.frontWindAngleDegrees = 0.0F;
			for (int int1 = 0; int1 < this.noiseCache.length; ++int1) {
				this.noiseCache[int1] = -1.0F;
			}
		}

		public void save(DataOutputStream dataOutputStream) throws IOException {
			dataOutputStream.writeFloat(this.days);
			dataOutputStream.writeFloat(this.maxNoise);
			dataOutputStream.writeFloat(this.totalNoise);
			dataOutputStream.writeInt(this.type);
			dataOutputStream.writeFloat(this.strength);
			dataOutputStream.writeFloat(this.frontWindAngleDegrees);
			dataOutputStream.writeInt(this.noiseCache.length);
			for (int int1 = 0; int1 < this.noiseCache.length; ++int1) {
				dataOutputStream.writeFloat(this.noiseCache[int1]);
			}
		}

		public void load(DataInputStream dataInputStream) throws IOException {
			this.days = dataInputStream.readFloat();
			this.maxNoise = dataInputStream.readFloat();
			this.totalNoise = dataInputStream.readFloat();
			this.type = dataInputStream.readInt();
			this.strength = dataInputStream.readFloat();
			this.frontWindAngleDegrees = dataInputStream.readFloat();
			int int1 = dataInputStream.readInt();
			int int2 = int1 > this.noiseCache.length ? int1 : this.noiseCache.length;
			for (int int3 = 0; int3 < int2; ++int3) {
				if (int3 < int1) {
					float float1 = dataInputStream.readFloat();
					if (int3 < this.noiseCache.length) {
						this.noiseCache[int3] = float1;
					}
				} else if (int3 < this.noiseCache.length) {
					this.noiseCache[int3] = -1.0F;
				}
			}
		}

		public void addDaySample(float float1) {
			++this.days;
			if (this.type == 1 && float1 <= 0.0F || this.type == -1 && float1 >= 0.0F) {
				this.strength = 0.0F;
			} else {
				this.tmpNoiseAbs = Math.abs(float1);
				if (this.tmpNoiseAbs > this.maxNoise) {
					this.maxNoise = this.tmpNoiseAbs;
				}

				this.totalNoise += this.tmpNoiseAbs;
				this.noiseCacheValue = 0.0F;
				for (int int1 = this.noiseCache.length - 1; int1 >= 0; --int1) {
					if (this.noiseCache[int1] > this.noiseCacheValue) {
						this.noiseCacheValue = this.noiseCache[int1];
					}

					if (int1 < this.noiseCache.length - 1) {
						this.noiseCache[int1 + 1] = this.noiseCache[int1];
					}
				}

				this.noiseCache[0] = this.tmpNoiseAbs;
				if (this.tmpNoiseAbs > this.noiseCacheValue) {
					this.noiseCacheValue = this.tmpNoiseAbs;
				}

				this.strength = this.noiseCacheValue * 0.75F + this.maxNoise * 0.25F;
			}
		}

		public void copyFrom(ClimateManager.AirFront airFront) {
			this.days = airFront.days;
			this.maxNoise = airFront.maxNoise;
			this.totalNoise = airFront.totalNoise;
			this.type = airFront.type;
			this.strength = airFront.strength;
			this.frontWindAngleDegrees = airFront.frontWindAngleDegrees;
		}
	}

	public static class ClimateFloat {
		protected float internalValue;
		protected float finalValue;
		protected boolean isOverride = false;
		protected float override;
		protected float interpolate;
		private boolean isModded = false;
		private float moddedValue;
		private float modInterpolate;
		private boolean isAdminOverride = false;
		private float adminValue;
		private float min = 0.0F;
		private float max = 1.0F;
		private int ID;
		private String name;

		public ClimateManager.ClimateFloat init(int int1, String string) {
			this.ID = int1;
			this.name = string;
			return this;
		}

		public int getID() {
			return this.ID;
		}

		public String getName() {
			return this.name;
		}

		public float getMin() {
			return this.min;
		}

		public float getMax() {
			return this.max;
		}

		public float getInternalValue() {
			return this.internalValue;
		}

		public float getOverride() {
			return this.override;
		}

		public float getOverrideInterpolate() {
			return this.interpolate;
		}

		public void setOverride(float float1, float float2) {
			this.override = float1;
			this.interpolate = float2;
			this.isOverride = true;
		}

		public void setEnableOverride(boolean boolean1) {
			this.isOverride = boolean1;
		}

		public boolean isEnableOverride() {
			return this.isOverride;
		}

		public void setEnableAdmin(boolean boolean1) {
			this.isAdminOverride = boolean1;
		}

		public boolean isEnableAdmin() {
			return this.isAdminOverride;
		}

		public void setAdminValue(float float1) {
			this.adminValue = ClimateManager.clamp(this.min, this.max, float1);
		}

		public float getAdminValue() {
			return this.adminValue;
		}

		public void setEnableModded(boolean boolean1) {
			this.isModded = boolean1;
		}

		public void setModdedValue(float float1) {
			this.moddedValue = ClimateManager.clamp(this.min, this.max, float1);
		}

		public float getModdedValue() {
			return this.moddedValue;
		}

		public void setModdedInterpolate(float float1) {
			this.modInterpolate = ClimateManager.clamp01(float1);
		}

		public void setFinalValue(float float1) {
			this.finalValue = float1;
		}

		public float getFinalValue() {
			return this.finalValue;
		}

		private void calculate() {
			if (this.isAdminOverride && !GameClient.bClient) {
				this.finalValue = this.adminValue;
			} else {
				if (this.isModded && this.modInterpolate > 0.0F) {
					this.internalValue = ClimateManager.lerp(this.modInterpolate, this.internalValue, this.moddedValue);
				}

				if (this.isOverride && this.interpolate > 0.0F) {
					this.finalValue = ClimateManager.lerp(this.interpolate, this.internalValue, this.override);
				} else {
					this.finalValue = this.internalValue;
				}
			}
		}

		private void writeAdmin(ByteBuffer byteBuffer) {
			byteBuffer.put((byte)(this.isAdminOverride ? 1 : 0));
			byteBuffer.putFloat(this.adminValue);
		}

		private void readAdmin(ByteBuffer byteBuffer) {
			this.isAdminOverride = byteBuffer.get() == 1;
			this.adminValue = byteBuffer.getFloat();
		}

		private void saveAdmin(DataOutputStream dataOutputStream) throws IOException {
			dataOutputStream.writeBoolean(this.isAdminOverride);
			dataOutputStream.writeFloat(this.adminValue);
		}

		private void loadAdmin(DataInputStream dataInputStream, int int1) throws IOException {
			this.isAdminOverride = dataInputStream.readBoolean();
			this.adminValue = dataInputStream.readFloat();
		}
	}

	public static class ClimateColor {
		protected ClimateColorInfo internalValue = new ClimateColorInfo();
		protected ClimateColorInfo finalValue = new ClimateColorInfo();
		protected boolean isOverride = false;
		protected ClimateColorInfo override = new ClimateColorInfo();
		protected float interpolate;
		private boolean isModded = false;
		private ClimateColorInfo moddedValue = new ClimateColorInfo();
		private float modInterpolate;
		private boolean isAdminOverride = false;
		private ClimateColorInfo adminValue = new ClimateColorInfo();
		private int ID;
		private String name;

		public ClimateManager.ClimateColor init(int int1, String string) {
			this.ID = int1;
			this.name = string;
			return this;
		}

		public int getID() {
			return this.ID;
		}

		public String getName() {
			return this.name;
		}

		public ClimateColorInfo getInternalValue() {
			return this.internalValue;
		}

		public ClimateColorInfo getOverride() {
			return this.override;
		}

		public float getOverrideInterpolate() {
			return this.interpolate;
		}

		public void setOverride(ClimateColorInfo climateColorInfo, float float1) {
			this.override.setTo(climateColorInfo);
			this.interpolate = float1;
			this.isOverride = true;
		}

		public void setOverride(ByteBuffer byteBuffer, float float1) {
			this.override.read(byteBuffer);
			this.interpolate = float1;
			this.isOverride = true;
		}

		public void setEnableOverride(boolean boolean1) {
			this.isOverride = boolean1;
		}

		public boolean isEnableOverride() {
			return this.isOverride;
		}

		public void setEnableAdmin(boolean boolean1) {
			this.isAdminOverride = boolean1;
		}

		public boolean isEnableAdmin() {
			return this.isAdminOverride;
		}

		public void setAdminValue(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
			this.adminValue.getExterior().r = float1;
			this.adminValue.getExterior().g = float2;
			this.adminValue.getExterior().b = float3;
			this.adminValue.getExterior().a = float4;
			this.adminValue.getInterior().r = float5;
			this.adminValue.getInterior().g = float6;
			this.adminValue.getInterior().b = float7;
			this.adminValue.getInterior().a = float8;
		}

		public void setAdminValueExterior(float float1, float float2, float float3, float float4) {
			this.adminValue.getExterior().r = float1;
			this.adminValue.getExterior().g = float2;
			this.adminValue.getExterior().b = float3;
			this.adminValue.getExterior().a = float4;
		}

		public void setAdminValueInterior(float float1, float float2, float float3, float float4) {
			this.adminValue.getInterior().r = float1;
			this.adminValue.getInterior().g = float2;
			this.adminValue.getInterior().b = float3;
			this.adminValue.getInterior().a = float4;
		}

		public void setAdminValue(ClimateColorInfo climateColorInfo) {
			this.adminValue.setTo(climateColorInfo);
		}

		public ClimateColorInfo getAdminValue() {
			return this.adminValue;
		}

		public void setEnableModded(boolean boolean1) {
			this.isModded = boolean1;
		}

		public void setModdedValue(ClimateColorInfo climateColorInfo) {
			this.moddedValue.setTo(climateColorInfo);
		}

		public ClimateColorInfo getModdedValue() {
			return this.moddedValue;
		}

		public void setModdedInterpolate(float float1) {
			this.modInterpolate = ClimateManager.clamp01(float1);
		}

		public void setFinalValue(ClimateColorInfo climateColorInfo) {
			this.finalValue.setTo(climateColorInfo);
		}

		public ClimateColorInfo getFinalValue() {
			return this.finalValue;
		}

		private void calculate() {
			if (this.isAdminOverride && !GameClient.bClient) {
				this.finalValue.setTo(this.adminValue);
			} else {
				if (this.isModded && this.modInterpolate > 0.0F) {
					this.internalValue.interp(this.moddedValue, this.modInterpolate, this.internalValue);
				}

				if (this.isOverride && this.interpolate > 0.0F) {
					this.internalValue.interp(this.override, this.interpolate, this.finalValue);
				} else {
					this.finalValue.setTo(this.internalValue);
				}
			}
		}

		private void writeAdmin(ByteBuffer byteBuffer) {
			byteBuffer.put((byte)(this.isAdminOverride ? 1 : 0));
			this.adminValue.write(byteBuffer);
		}

		private void readAdmin(ByteBuffer byteBuffer) {
			this.isAdminOverride = byteBuffer.get() == 1;
			this.adminValue.read(byteBuffer);
		}

		private void saveAdmin(DataOutputStream dataOutputStream) throws IOException {
			dataOutputStream.writeBoolean(this.isAdminOverride);
			this.adminValue.save(dataOutputStream);
		}

		private void loadAdmin(DataInputStream dataInputStream, int int1) throws IOException {
			this.isAdminOverride = dataInputStream.readBoolean();
			if (int1 < 143) {
				this.adminValue.getInterior().r = dataInputStream.readFloat();
				this.adminValue.getInterior().g = dataInputStream.readFloat();
				this.adminValue.getInterior().b = dataInputStream.readFloat();
				this.adminValue.getInterior().a = dataInputStream.readFloat();
				this.adminValue.getExterior().r = this.adminValue.getInterior().r;
				this.adminValue.getExterior().g = this.adminValue.getInterior().g;
				this.adminValue.getExterior().b = this.adminValue.getInterior().b;
				this.adminValue.getExterior().a = this.adminValue.getInterior().a;
			} else {
				this.adminValue.load(dataInputStream, int1);
			}
		}
	}

	public static class ClimateBool {
		protected boolean internalValue;
		protected boolean finalValue;
		protected boolean isOverride;
		protected boolean override;
		private boolean isModded = false;
		private boolean moddedValue;
		private boolean isAdminOverride = false;
		private boolean adminValue;
		private int ID;
		private String name;

		public ClimateManager.ClimateBool init(int int1, String string) {
			this.ID = int1;
			this.name = string;
			return this;
		}

		public int getID() {
			return this.ID;
		}

		public String getName() {
			return this.name;
		}

		public boolean getInternalValue() {
			return this.internalValue;
		}

		public boolean getOverride() {
			return this.override;
		}

		public void setOverride(boolean boolean1) {
			this.isOverride = true;
			this.override = boolean1;
		}

		public void setEnableOverride(boolean boolean1) {
			this.isOverride = boolean1;
		}

		public boolean isEnableOverride() {
			return this.isOverride;
		}

		public void setEnableAdmin(boolean boolean1) {
			this.isAdminOverride = boolean1;
		}

		public boolean isEnableAdmin() {
			return this.isAdminOverride;
		}

		public void setAdminValue(boolean boolean1) {
			this.adminValue = boolean1;
		}

		public boolean getAdminValue() {
			return this.adminValue;
		}

		public void setEnableModded(boolean boolean1) {
			this.isModded = boolean1;
		}

		public void setModdedValue(boolean boolean1) {
			this.moddedValue = boolean1;
		}

		public boolean getModdedValue() {
			return this.moddedValue;
		}

		public void setFinalValue(boolean boolean1) {
			this.finalValue = boolean1;
		}

		private void calculate() {
			if (this.isAdminOverride && !GameClient.bClient) {
				this.finalValue = this.adminValue;
			} else if (this.isModded) {
				this.finalValue = this.moddedValue;
			} else {
				this.finalValue = this.isOverride ? this.override : this.internalValue;
			}
		}

		private void writeAdmin(ByteBuffer byteBuffer) {
			byteBuffer.put((byte)(this.isAdminOverride ? 1 : 0));
			byteBuffer.put((byte)(this.adminValue ? 1 : 0));
		}

		private void readAdmin(ByteBuffer byteBuffer) {
			this.isAdminOverride = byteBuffer.get() == 1;
			this.adminValue = byteBuffer.get() == 1;
		}

		private void saveAdmin(DataOutputStream dataOutputStream) throws IOException {
			dataOutputStream.writeBoolean(this.isAdminOverride);
			dataOutputStream.writeBoolean(this.adminValue);
		}

		private void loadAdmin(DataInputStream dataInputStream, int int1) throws IOException {
			this.isAdminOverride = dataInputStream.readBoolean();
			this.adminValue = dataInputStream.readBoolean();
		}
	}

	private static class ClimateNetInfo {
		public boolean IsStopWeather = false;
		public boolean IsTrigger = false;
		public boolean IsGenerate = false;
		public float TriggerDuration = 0.0F;
		public boolean TriggerStorm = false;
		public boolean TriggerTropical = false;
		public boolean TriggerBlizzard = false;
		public float GenerateStrength = 0.0F;
		public int GenerateFront = 0;

		private void reset() {
			this.IsStopWeather = false;
			this.IsTrigger = false;
			this.IsGenerate = false;
			this.TriggerDuration = 0.0F;
			this.TriggerStorm = false;
			this.TriggerTropical = false;
			this.TriggerBlizzard = false;
			this.GenerateStrength = 0.0F;
			this.GenerateFront = 0;
		}
	}

	public static class DayInfo {
		public int day;
		public int month;
		public int year;
		public int hour;
		public int minutes;
		public long dateValue;
		public GregorianCalendar calendar;
		public ErosionSeason season;

		public void set(int int1, int int2, int int3) {
			this.calendar = new GregorianCalendar(int3, int2, int1, 0, 0);
			this.dateValue = this.calendar.getTime().getTime();
			this.day = int1;
			this.month = int2;
			this.year = int3;
		}

		public int getDay() {
			return this.day;
		}

		public int getMonth() {
			return this.month;
		}

		public int getYear() {
			return this.year;
		}

		public int getHour() {
			return this.hour;
		}

		public int getMinutes() {
			return this.minutes;
		}

		public long getDateValue() {
			return this.dateValue;
		}

		public ErosionSeason getSeason() {
			return this.season;
		}
	}

	public static enum ClimateNetAuth {

		Denied,
		ClientOnly,
		ServerOnly,
		ClientAndServer;

		private static ClimateManager.ClimateNetAuth[] $values() {
			return new ClimateManager.ClimateNetAuth[]{Denied, ClientOnly, ServerOnly, ClientAndServer};
		}
	}

	protected static class SeasonColor {
		public static int WARM = 0;
		public static int NORMAL = 1;
		public static int CLOUDY = 2;
		public static int SUMMER = 0;
		public static int FALL = 1;
		public static int WINTER = 2;
		public static int SPRING = 3;
		private ClimateColorInfo finalCol = new ClimateColorInfo();
		private ClimateColorInfo[] tempCol = new ClimateColorInfo[3];
		private ClimateColorInfo[][] colors = new ClimateColorInfo[3][4];
		private boolean ignoreNormal = true;

		public SeasonColor() {
			for (int int1 = 0; int1 < 3; ++int1) {
				for (int int2 = 0; int2 < 4; ++int2) {
					this.colors[int1][int2] = new ClimateColorInfo();
				}

				this.tempCol[int1] = new ClimateColorInfo();
			}
		}

		public void setIgnoreNormal(boolean boolean1) {
			this.ignoreNormal = boolean1;
		}

		public ClimateColorInfo getColor(int int1, int int2) {
			return this.colors[int1][int2];
		}

		public void setColorInterior(int int1, int int2, float float1, float float2, float float3, float float4) {
			this.colors[int1][int2].getInterior().r = float1;
			this.colors[int1][int2].getInterior().g = float2;
			this.colors[int1][int2].getInterior().b = float3;
			this.colors[int1][int2].getInterior().a = float4;
		}

		public void setColorExterior(int int1, int int2, float float1, float float2, float float3, float float4) {
			this.colors[int1][int2].getExterior().r = float1;
			this.colors[int1][int2].getExterior().g = float2;
			this.colors[int1][int2].getExterior().b = float3;
			this.colors[int1][int2].getExterior().a = float4;
		}

		public ClimateColorInfo update(float float1, float float2, int int1, int int2) {
			for (int int3 = 0; int3 < 3; ++int3) {
				if (!this.ignoreNormal || int3 != 1) {
					this.colors[int3][int1].interp(this.colors[int3][int2], float2, this.tempCol[int3]);
				}
			}

			if (!this.ignoreNormal) {
				float float3;
				if (float1 < 0.5F) {
					float3 = float1 * 2.0F;
					this.tempCol[WARM].interp(this.tempCol[NORMAL], float3, this.finalCol);
				} else {
					float3 = 1.0F - (float1 - 0.5F) * 2.0F;
					this.tempCol[CLOUDY].interp(this.tempCol[NORMAL], float3, this.finalCol);
				}
			} else {
				this.tempCol[WARM].interp(this.tempCol[CLOUDY], float1, this.finalCol);
			}

			return this.finalCol;
		}
	}
}
