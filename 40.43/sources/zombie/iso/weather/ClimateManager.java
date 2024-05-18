package zombie.iso.weather;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.GregorianCalendar;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionMain;
import zombie.erosion.season.ErosionIceQueen;
import zombie.erosion.season.ErosionSeason;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
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
	private static ClimateManager instance = new ClimateManager();
	public static boolean WINTER_IS_COMING = false;
	public static boolean THE_DESCENDING_FOG = false;
	public static boolean A_STORM_IS_COMING = false;
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
	protected ClimateManager.ClimateColor globalLight;
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
	public static final int FLOAT_MAX = 12;
	private final ClimateManager.ClimateFloat[] climateFloats = new ClimateManager.ClimateFloat[12];
	public static final int COLOR_GLOBAL_LIGHT = 0;
	public static final int COLOR_MAX = 1;
	private final ClimateManager.ClimateColor[] climateColors = new ClimateManager.ClimateColor[1];
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

	public float getMaxWindspeedKph() {
		return 120.0F;
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
		this.colTemp = new ClimateColorInfo();
		this.thunderStorm = new ThunderStorm(this);
		this.weatherPeriod = new WeatherPeriod(this, this.thunderStorm);
		this.simplexOffsetA = (double)Rand.Next(0, 8000);
		this.simplexOffsetB = (double)Rand.Next(8000, 16000);
		this.simplexOffsetC = (double)Rand.Next(0, -8000);
		this.simplexOffsetD = (double)Rand.Next(-8000, -16000);
		this.initSeasonColors();
		this.setup();
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
		this.globalLight = this.initClimateColor(0, "GLOBAL_LIGHT");
		this.precipitationIsSnow = this.initClimateBool(0, "IS_SNOW");
	}

	public int getFloatMax() {
		return 12;
	}

	private ClimateManager.ClimateFloat initClimateFloat(int int1, String string) {
		if (int1 >= 0 && int1 < 12) {
			return this.climateFloats[int1].init(int1, string);
		} else {
			DebugLog.log("Climate: cannot get float override id.");
			return null;
		}
	}

	public ClimateManager.ClimateFloat getClimateFloat(int int1) {
		if (int1 >= 0 && int1 < 12) {
			return this.climateFloats[int1];
		} else {
			DebugLog.log("Climate: cannot get float override id.");
			return null;
		}
	}

	public int getColorMax() {
		return 1;
	}

	private ClimateManager.ClimateColor initClimateColor(int int1, String string) {
		if (int1 >= 0 && int1 < 1) {
			return this.climateColors[int1].init(int1, string);
		} else {
			DebugLog.log("Climate: cannot get float override id.");
			return null;
		}
	}

	public ClimateManager.ClimateColor getClimateColor(int int1) {
		if (int1 >= 0 && int1 < 1) {
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

	public boolean isRaining() {
		return this.getPrecipitationIntensity() > 0.0F && !this.getPrecipitationIsSnow();
	}

	public float getRainIntensity() {
		return this.isRaining() ? this.getPrecipitationIntensity() : 0.0F;
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
		return this.currentDay.getSeason();
	}

	public float getFrontStrength() {
		return this.currentFront == null ? 0.0F : this.currentFront.strength;
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
			float float2;
			if (!boolean2 && baseVehicle == null) {
				if (boolean1) {
					float1 = Temperature.WindchillCelsiusKph(float1, this.getWindspeedKph());
				}
			} else if (float1 <= 22.0F) {
				float2 = (22.0F - float1) / 8.0F;
				if (baseVehicle == null) {
					if (square.getZ() < 1) {
						float1 = float1 + float2 + float2 * this.dayLightLagged;
					} else {
						float2 = (float)((double)float2 * 0.5);
						float1 = float1 + float2 + float2 * this.dayLightLagged;
					}
				}
			} else {
				float2 = (float1 - 22.0F) / 3.5F;
				if (baseVehicle == null) {
					if (square.getZ() < 1) {
						float1 = float1 - float2 - float2 * this.nightLagged;
					} else {
						float2 = (float)((double)float2 * 0.5);
						float1 = float1 + float2 + float2 * this.dayLightLagged + float2 * this.nightLagged * 0.5F;
					}
				} else {
					float1 = float1 + float2 + float2 * this.dayLightLagged;
				}
			}

			float2 = IsoWorld.instance.getCell().getHeatSourceHighestTemperature(float1, square.getX(), square.getY(), square.getZ());
			if (float2 > float1) {
				float1 = float2;
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
		this.season = ErosionMain.getInstance().getSeasons();
		ThunderStorm.MAP_MIN_X = metaGrid.minX * 300 - 4000;
		ThunderStorm.MAP_MAX_X = metaGrid.maxX * 300 + 4000;
		ThunderStorm.MAP_MIN_Y = metaGrid.minY * 300 - 4000;
		ThunderStorm.MAP_MAX_Y = metaGrid.maxY * 300 + 4000;
		windNoiseOffset = 0.0;
		WINTER_IS_COMING = IsoWorld.instance.getGameMode().equals("Winter is Coming");
		THE_DESCENDING_FOG = IsoWorld.instance.getGameMode().equals("The Descending Fog");
		A_STORM_IS_COMING = IsoWorld.instance.getGameMode().equals("A Storm is Coming");
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
			this.updateDayInfo(this.gt.getDay(), this.gt.getMonth(), this.gt.getYear());
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
				Temperature.updateTemperatureForAllPlayers();
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
			this.weatherPeriod.init(airFront, this.worldAgeHours);
		}
	}

	public boolean triggerCustomWeather(float float1, boolean boolean1) {
		if (!GameClient.bClient && !this.weatherPeriod.isRunning()) {
			ClimateManager.AirFront airFront = new ClimateManager.AirFront();
			airFront.strength = float1;
			airFront.type = boolean1 ? 1 : -1;
			this.weatherPeriod.init(airFront, this.worldAgeHours);
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
			this.weatherPeriod.init(airFront, this.worldAgeHours, int1, float1);
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
		if (this.flareLaunched) {
			if (this.flareLifeTime > this.flareMaxLifeTime) {
				this.flareLaunched = false;
				return;
			}

			float float1 = this.flareLifeTime / this.flareMaxLifeTime;
			--this.nextRandomTargetIntens;
			if (this.nextRandomTargetIntens <= 0) {
				this.flareIntensity.setTarget(Rand.Next(0.8F, 1.0F));
				this.nextRandomTargetIntens = Rand.Next(5, 30);
			}

			this.flareIntensity.update(GameTime.instance.getMultiplier());
			this.flareIntens = clerp(float1, 1.0F, 0.0F);
			float float2 = this.flareIntensity.value();
			this.colFlare.interp(this.globalLight.finalValue, float1, this.globalLight.finalValue);
			this.globalLightIntensity.finalValue = clerp(float1, float2, this.globalLightIntensity.finalValue);
			this.ambient.finalValue = clerp(float1, float2, this.ambient.finalValue);
			this.dayLightStrength.finalValue = clerp(float1, float2, this.dayLightStrength.finalValue);
			if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
				this.desaturation.finalValue = clerp(float1, 1.0F, this.desaturation.finalValue);
			} else {
				this.desaturation.finalValue = clerp(float1, 0.0F, this.desaturation.finalValue);
			}

			++this.flareLifeTime;
		}
	}

	public void launchFlare() {
		if (IsoPlayer.instance != null && !this.flareLaunched) {
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

		float float1 = this.currentDay.season.getDawn();
		float float2 = this.currentDay.season.getDusk();
		float float3 = this.currentDay.season.getDayHighNoon();
		float float4 = this.gt.getTimeOfDay();
		float float5 = float4 / 24.0F;
		float float6 = lerp(float5, this.season.getCurDayPercent(), this.nextDay.season.getCurDayPercent());
		double double1 = this.getAirMassNoiseFrequencyMod(SandboxOptions.instance.getRainModifier());
		float float7 = (float)this.gt.getWorldAgeHours();
		float float8 = (float)SimplexNoise.noise(this.simplexOffsetA, this.worldAgeHours / double1);
		this.airMass = float8;
		this.airMassTemperature = (float)SimplexNoise.noise(this.simplexOffsetA, (this.worldAgeHours - 48.0) / double1);
		float float9;
		if (this.tickIsHourChange) {
			int int1 = this.airMass < 0.0F ? -1 : 1;
			if (this.currentFront.type != int1) {
				if (!this.DISABLE_WEATHER_GENERATION && (!WINTER_IS_COMING || WINTER_IS_COMING && GameTime.instance.getWorldAgeHours() > 96.0)) {
					if (THE_DESCENDING_FOG) {
						this.currentFront.type = -1;
						this.currentFront.strength = Rand.Next(0.2F, 0.45F);
						this.weatherPeriod.init(this.currentFront, this.worldAgeHours);
					} else {
						this.weatherPeriod.init(this.currentFront, this.worldAgeHours);
					}
				}

				this.currentFront.setFrontType(int1);
			}

			if (!WINTER_IS_COMING && !THE_DESCENDING_FOG && GameTime.instance.getWorldAgeHours() >= 72.0 && GameTime.instance.getWorldAgeHours() <= 96.0 && !this.DISABLE_WEATHER_GENERATION && !this.weatherPeriod.isRunning() && Rand.Next(0, 1000) < 50) {
				this.triggerCustomWeatherStage(3, 10.0F);
			}

			if (this.tickIsDayChange) {
				double double2 = Math.floor(this.worldAgeHours) + 12.0;
				float8 = (float)SimplexNoise.noise(this.simplexOffsetA, double2 / double1);
				this.airMassDaily = float8;
				int1 = this.airMassDaily < 0.0F ? -1 : 1;
				if (int1 == this.currentFront.type) {
					this.currentFront.addDaySample(this.airMassDaily);
				}

				this.dayFogStrength = 0.0F;
				this.dayDoFog = false;
				if (!this.weatherPeriod.isRunning()) {
					float9 = (float)Rand.Next(0, 1000);
					this.dayDoFog = float9 < 200.0F;
					if (this.currentFront.getType() == 1) {
						if (float9 < 25.0F) {
							this.dayFogStrength = 1.0F;
						} else {
							this.dayFogStrength = Rand.Next(0.5F, 1.0F);
						}
					} else {
						this.dayFogStrength = Rand.Next(0.5F, 0.75F);
					}
				}
			}
		}

		float float10 = clerp(float5, this.currentDay.season.getDayTemperature(), this.nextDay.season.getDayTemperature());
		float float11 = clerp(float5, this.currentDay.season.getDayMeanTemperature(), this.nextDay.season.getDayMeanTemperature());
		boolean boolean1 = float10 < float11;
		this.baseTemperature = float11 + this.airMassTemperature * 8.0F;
		float9 = 4.0F;
		float float12 = float2 + float9;
		if (float12 >= 24.0F) {
			float12 -= 24.0F;
		}

		this.dayLightLagged = this.getTimeLerpHours(float4, float1 + float9, float12, true);
		float float13 = 5.0F * (1.0F - this.dayLightLagged);
		this.nightLagged = this.getTimeLerpHours(float4, float12, float1 + float9, true);
		float13 += 5.0F * this.nightLagged;
		this.temperature.internalValue = this.baseTemperature + 1.0F - float13;
		if (!(this.temperature.internalValue < 0.0F) && !WINTER_IS_COMING) {
			this.precipitationIsSnow.internalValue = false;
		} else {
			this.precipitationIsSnow.internalValue = true;
		}

		float float14 = 1.0F - (this.airMassTemperature + 1.0F) * 0.5F;
		float float15 = 1.0F - float6 * 0.4F;
		float float16 = (float)SimplexNoise.noise(this.worldAgeHours / 40.0, this.simplexOffsetA);
		float float17 = (float16 + 1.0F) * 0.5F;
		float17 *= float14 * float15;
		float17 *= 0.65F;
		this.windIntensity.internalValue = float17;
		float float18 = (float)SimplexNoise.noise(this.worldAgeHours / 80.0, this.simplexOffsetB);
		this.windAngleIntensity.internalValue = float18;
		this.windPower = this.windIntensity.internalValue;
		float float19 = 0.0F;
		if (float18 >= 0.0F) {
			float19 = Rand.Next(90.0F, 270.0F);
		} else {
			float19 = Rand.Next(0.0F, 180.0F);
			if (float19 > 90.0F) {
				float19 = 360.0F - (float19 - 90.0F);
			}
		}

		this.currentFront.setFrontWind(float19);
		this.cloudIntensity.internalValue = clamp01(this.windIntensity.internalValue * 2.0F);
		ClimateManager.ClimateFloat climateFloat = this.cloudIntensity;
		climateFloat.internalValue -= this.cloudIntensity.internalValue * 0.5F * this.nightStrength.internalValue;
		this.precipitationIntensity.internalValue = 0.0F;
		float float20 = this.getTimeLerpHours(float4, float2, float1, true);
		float20 = clamp(0.0F, 1.0F, float20 * 2.0F);
		this.nightStrength.internalValue = float20;
		float float21 = 1.0F - this.nightStrength.internalValue;
		float float22 = 1.0F - 0.15F * float6 - 0.2F * this.windIntensity.internalValue;
		float21 *= float22;
		this.dayLightStrength.internalValue = float21;
		this.ambient.internalValue = this.dayLightStrength.internalValue;
		float float23 = (1.0F - this.season.getCurDayPercent()) * 0.4F;
		float float24 = (1.0F - this.nextDay.season.getCurDayPercent()) * 0.4F;
		this.desaturation.internalValue = lerp(float5, float23, float24);
		float float25 = 1.0F - clamp01((this.airMassTemperature + 0.8F) * 0.625F);
		float25 *= 0.8F;
		float25 = clamp01(float25 + this.windIntensity.internalValue);
		int int2 = this.season.getSeason();
		float float26 = this.season.getSeasonProgression();
		float float27 = 0.0F;
		int int3 = 0;
		int int4 = 0;
		if (int2 == 2) {
			int3 = ClimateManager.SeasonColor.SPRING;
			int4 = ClimateManager.SeasonColor.SUMMER;
			float27 = 0.5F + float26 * 0.5F;
		} else if (int2 == 3) {
			int3 = ClimateManager.SeasonColor.SUMMER;
			int4 = ClimateManager.SeasonColor.FALL;
			float27 = float26 * 0.5F;
		} else if (int2 == 4) {
			if (float26 < 0.5F) {
				int3 = ClimateManager.SeasonColor.SUMMER;
				int4 = ClimateManager.SeasonColor.FALL;
				float27 = 0.5F + float26;
			} else {
				int3 = ClimateManager.SeasonColor.FALL;
				int4 = ClimateManager.SeasonColor.WINTER;
				float27 = float26 - 0.5F;
			}
		} else if (int2 == 5) {
			if (float26 < 0.5F) {
				int3 = ClimateManager.SeasonColor.FALL;
				int4 = ClimateManager.SeasonColor.WINTER;
				float27 = 0.5F + float26;
			} else {
				int3 = ClimateManager.SeasonColor.WINTER;
				int4 = ClimateManager.SeasonColor.SPRING;
				float27 = float26 - 0.5F;
			}
		} else if (int2 == 1) {
			if (float26 < 0.5F) {
				int3 = ClimateManager.SeasonColor.WINTER;
				int4 = ClimateManager.SeasonColor.SPRING;
				float27 = 0.5F + float26;
			} else {
				int3 = ClimateManager.SeasonColor.SPRING;
				int4 = ClimateManager.SeasonColor.SUMMER;
				float27 = float26 - 0.5F;
			}
		}

		this.colDawn = this.seasonColorDawn.update(float25, float27, int3, int4);
		this.colDay = this.seasonColorDay.update(float25, float27, int3, int4);
		this.colDusk = this.seasonColorDusk.update(float25, float27, int3, int4);
		float float28;
		float float29;
		if (!THE_DESCENDING_FOG) {
			if (this.dayDoFog && this.dayFogStrength > 0.0F && float4 > float1 - 2.0F && float4 < float1 + 5.0F) {
				float28 = this.getTimeLerpHours(float4, float1 - 2.0F, float1 + 5.0F, true);
				float28 = clamp(0.0F, 1.0F, float28 * 2.0F);
				this.fogLerpValue = float28;
				this.cloudIntensity.internalValue = lerp(float28, this.cloudIntensity.internalValue, 0.0F);
				float29 = this.dayFogStrength;
				this.fogIntensity.internalValue = clerp(float28, 0.0F, float29);
				if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
					this.desaturation.internalValue = clerp(float28, this.desaturation.internalValue, 0.8F * float29);
				} else {
					this.desaturation.internalValue = clerp(float28, this.desaturation.internalValue, 0.8F * float29);
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

			climateFloat = this.fogIntensity;
			climateFloat.internalValue *= 0.93F;
			this.desaturation.internalValue = 0.8F * this.fogIntensity.internalValue;
		}

		float28 = 0.6F * float21;
		float29 = 0.4F;
		float float30 = 0.25F * float21;
		if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
			float30 = 0.8F * float21;
		}

		float float31;
		if (!(float4 < float1) && !(float4 > float2)) {
			if (float4 < float3 + 2.0F) {
				float31 = (float4 - float1) / (float3 + 2.0F - float1);
				this.colDawn.interp(this.colDay, float31, this.globalLight.internalValue);
				this.globalLightIntensity.internalValue = lerp(float31, float30, float28);
			} else {
				float31 = (float4 - (float3 + 2.0F)) / (float2 - (float3 + 2.0F));
				this.colDay.interp(this.colDusk, float31, this.globalLight.internalValue);
				this.globalLightIntensity.internalValue = lerp(float31, float28, float30);
			}
		} else {
			float float32 = 24.0F - float2 + float1;
			if (float4 > float2) {
				float31 = (float4 - float2) / float32;
				this.colDusk.interp(this.colDawn, float31, this.globalLight.internalValue);
			} else {
				float31 = (24.0F - float2 + float4) / float32;
				this.colDusk.interp(this.colDawn, float31, this.globalLight.internalValue);
			}

			this.globalLightIntensity.internalValue = lerp(float20, float30, float29);
		}

		if (this.fogIntensity.internalValue > 0.0F) {
			if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
				this.globalLight.internalValue.interp(this.colFog, this.fogIntensity.internalValue, this.globalLight.internalValue);
			} else {
				this.globalLight.internalValue.interp(this.colFogLegacy, this.fogIntensity.internalValue, this.globalLight.internalValue);
			}

			this.globalLightIntensity.internalValue = clerp(this.fogLerpValue, this.globalLightIntensity.internalValue, 0.8F);
		}

		this.colNightNoMoon.interp(this.colNightMoon, ClimateMoon.getMoonFloat(), this.colNight);
		this.globalLight.internalValue.interp(this.colNight, this.nightStrength.internalValue, this.globalLight.internalValue);
		IsoPlayer[] playerArray = IsoPlayer.players;
		for (int int5 = 0; int5 < playerArray.length; ++int5) {
			IsoPlayer player = playerArray[int5];
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
		this.updateDayInfo(this.gt.getDay(), this.gt.getMonth(), this.gt.getYear());
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

			this.currentDay.set(int1, int2, int3);
			this.currentDay.calendar = new GregorianCalendar(int3, int2, int1, 0, 0);
			this.currentDay.dateValue = this.currentDay.calendar.getTime().getTime();
			this.currentDay.day = int1;
			this.currentDay.month = int2;
			this.currentDay.year = int3;
			this.currentDay.season = this.season;
			if (this.previousDay == null) {
				this.previousDay = new ClimateManager.DayInfo();
				this.previousDay.season = this.season.clone();
			}

			this.previousDay.calendar = new GregorianCalendar(int3, int2, int1, 0, 0);
			this.previousDay.calendar.add(5, -1);
			this.previousDay.day = this.previousDay.calendar.get(5);
			this.previousDay.month = this.previousDay.calendar.get(2);
			this.previousDay.year = this.previousDay.calendar.get(1);
			this.previousDay.dateValue = this.previousDay.calendar.getTime().getTime();
			this.previousDay.season.setDay(this.previousDay.day, this.previousDay.month, this.previousDay.year);
			if (this.nextDay == null) {
				this.nextDay = new ClimateManager.DayInfo();
				this.nextDay.season = this.season.clone();
			}

			this.nextDay.calendar = new GregorianCalendar(int3, int2, int1, 0, 0);
			this.nextDay.calendar.add(5, 1);
			this.nextDay.day = this.nextDay.calendar.get(5);
			this.nextDay.month = this.nextDay.calendar.get(2);
			this.nextDay.year = this.nextDay.calendar.get(1);
			this.nextDay.dateValue = this.nextDay.calendar.getTime().getTime();
			this.nextDay.season.setDay(this.nextDay.day, this.nextDay.month, this.nextDay.year);
		}
	}

	protected final void transmitClimatePacket(ClimateManager.ClimateNetAuth climateNetAuth, byte byte1, UdpConnection udpConnection) {
		if (GameClient.bClient || GameServer.bServer) {
			if (climateNetAuth == ClimateManager.ClimateNetAuth.Denied) {
				DebugLog.log("Denied ClimatePacket, id = " + byte1 + ", isClient = " + GameClient.bClient);
			} else {
				if (GameClient.bClient && (climateNetAuth == ClimateManager.ClimateNetAuth.ClientOnly || climateNetAuth == ClimateManager.ClimateNetAuth.ClientAndServer)) {
					try {
						if (this.writePacketContents(GameClient.connection, byte1)) {
							GameClient.connection.endPacketImmediate();
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
									udpConnection2.endPacketImmediate();
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
			PacketTypes.doPacket((short)200, byteBufferWriter);
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
					DebugLog.log("Snow = " + this.climateBooleans[int1].adminValue + ", enabled = " + this.climateBooleans[int1].isAdminOverride);
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

			this.triggerCustomWeatherStage(3, this.netInfo.TriggerDuration);
			this.updateOnTick();
			this.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)0, (UdpConnection)null);
		}
	}

	public void transmitServerStartRain(float float1) {
		if (GameServer.bServer) {
			this.precipitationIntensity.setAdminValue(clamp01(float1));
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

	private float getTimeLerpHours(float float1, float float2, float float3) {
		return this.getTimeLerpHours(float1, float2, float3, false);
	}

	private float getTimeLerpHours(float float1, float float2, float float3, boolean boolean1) {
		return this.getTimeLerp(clamp(0.0F, 1.0F, float1 / 24.0F), clamp(0.0F, 1.0F, float2 / 24.0F), clamp(0.0F, 1.0F, float3 / 24.0F), boolean1);
	}

	private float getTimeLerp(float float1, float float2, float float3) {
		return this.getTimeLerp(float1, float2, float3, false);
	}

	private float getTimeLerp(float float1, float float2, float float3, boolean boolean1) {
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

	private static class ClimateNetInfo {
		public boolean IsStopWeather;
		public boolean IsTrigger;
		public boolean IsGenerate;
		public float TriggerDuration;
		public boolean TriggerStorm;
		public boolean TriggerTropical;
		public boolean TriggerBlizzard;
		public float GenerateStrength;
		public int GenerateFront;

		private ClimateNetInfo() {
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

		ClimateNetInfo(Object object) {
			this();
		}
	}
	public static enum ClimateNetAuth {

		Denied,
		ClientOnly,
		ServerOnly,
		ClientAndServer;
	}

	public class DayInfo {
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

		private void reset() {
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
}
