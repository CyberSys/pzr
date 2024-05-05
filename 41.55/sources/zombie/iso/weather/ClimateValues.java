package zombie.iso.weather;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.GregorianCalendar;
import java.util.Random;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.debug.DebugLog;


public class ClimateValues {
	private double simplexOffsetA = 0.0;
	private double simplexOffsetB = 0.0;
	private double simplexOffsetC = 0.0;
	private double simplexOffsetD = 0.0;
	private ClimateManager clim;
	private GameTime gt;
	private float time = 0.0F;
	private float dawn = 0.0F;
	private float dusk = 0.0F;
	private float noon = 0.0F;
	private float dayMeanTemperature = 0.0F;
	private double airMassNoiseFrequencyMod = 0.0;
	private float noiseAirmass = 0.0F;
	private float airMassTemperature = 0.0F;
	private float baseTemperature = 0.0F;
	private float dayLightLagged = 0.0F;
	private float nightLagged = 0.0F;
	private float temperature = 0.0F;
	private boolean temperatureIsSnow = false;
	private float humidity = 0.0F;
	private float windIntensity = 0.0F;
	private float windAngleIntensity = 0.0F;
	private float windAngleDegrees = 0.0F;
	private float nightStrength = 0.0F;
	private float dayLightStrength = 0.0F;
	private float ambient = 0.0F;
	private float desaturation = 0.0F;
	private float dayLightStrengthBase = 0.0F;
	private float lerpNight = 0.0F;
	private float cloudyT = 0.0F;
	private float cloudIntensity = 0.0F;
	private float airFrontAirmass = 0.0F;
	private boolean dayDoFog = false;
	private float dayFogStrength = 0.0F;
	private float dayFogDuration = 0.0F;
	private ClimateManager.DayInfo testCurrentDay;
	private ClimateManager.DayInfo testNextDay;
	private double cacheWorldAgeHours = 0.0;
	private int cacheYear;
	private int cacheMonth;
	private int cacheDay;
	private Random seededRandom;

	public ClimateValues(ClimateManager climateManager) {
		this.simplexOffsetA = climateManager.getSimplexOffsetA();
		this.simplexOffsetB = climateManager.getSimplexOffsetB();
		this.simplexOffsetC = climateManager.getSimplexOffsetC();
		this.simplexOffsetD = climateManager.getSimplexOffsetD();
		this.clim = climateManager;
		this.gt = GameTime.getInstance();
		this.seededRandom = new Random(1984L);
	}

	public ClimateValues getCopy() {
		ClimateValues climateValues = new ClimateValues(this.clim);
		this.CopyValues(climateValues);
		return climateValues;
	}

	public void CopyValues(ClimateValues climateValues) {
		if (climateValues != this) {
			climateValues.time = this.time;
			climateValues.dawn = this.dawn;
			climateValues.dusk = this.dusk;
			climateValues.noon = this.noon;
			climateValues.dayMeanTemperature = this.dayMeanTemperature;
			climateValues.airMassNoiseFrequencyMod = this.airMassNoiseFrequencyMod;
			climateValues.noiseAirmass = this.noiseAirmass;
			climateValues.airMassTemperature = this.airMassTemperature;
			climateValues.baseTemperature = this.baseTemperature;
			climateValues.dayLightLagged = this.dayLightLagged;
			climateValues.nightLagged = this.nightLagged;
			climateValues.temperature = this.temperature;
			climateValues.temperatureIsSnow = this.temperatureIsSnow;
			climateValues.humidity = this.humidity;
			climateValues.windIntensity = this.windIntensity;
			climateValues.windAngleIntensity = this.windAngleIntensity;
			climateValues.windAngleDegrees = this.windAngleDegrees;
			climateValues.nightStrength = this.nightStrength;
			climateValues.dayLightStrength = this.dayLightStrength;
			climateValues.ambient = this.ambient;
			climateValues.desaturation = this.desaturation;
			climateValues.dayLightStrengthBase = this.dayLightStrengthBase;
			climateValues.lerpNight = this.lerpNight;
			climateValues.cloudyT = this.cloudyT;
			climateValues.cloudIntensity = this.cloudIntensity;
			climateValues.airFrontAirmass = this.airFrontAirmass;
			climateValues.dayDoFog = this.dayDoFog;
			climateValues.dayFogStrength = this.dayFogStrength;
			climateValues.dayFogDuration = this.dayFogDuration;
			climateValues.cacheWorldAgeHours = this.cacheWorldAgeHours;
			climateValues.cacheYear = this.cacheYear;
			climateValues.cacheMonth = this.cacheMonth;
			climateValues.cacheDay = this.cacheDay;
		}
	}

	public void print() {
		DebugLog.log("==================================================");
		DebugLog.log("Current time of day = " + this.gt.getTimeOfDay());
		DebugLog.log("Current Worldagehours = " + this.gt.getWorldAgeHours());
		DebugLog.log("--------------------------------------------------");
		if (this.testCurrentDay == null) {
			GregorianCalendar gregorianCalendar = new GregorianCalendar(this.cacheYear, this.cacheMonth, this.cacheDay);
			DebugLog.log("Printing climate values for: " + (new SimpleDateFormat("yyyy MM dd")).format(gregorianCalendar.getTime()));
		} else {
			DebugLog.log("Printing climate values for: " + (new SimpleDateFormat("yyyy MM dd")).format(this.testCurrentDay.calendar.getTime()));
		}

		DebugLog.log("--------------------------------------------------");
		DebugLog.log("Poll Worldagehours = " + this.cacheWorldAgeHours);
		DebugLog.log("Poll time = " + this.time);
		DebugLog.log("dawn = " + this.dawn);
		DebugLog.log("dusk = " + this.dusk);
		DebugLog.log("noon = " + this.noon);
		DebugLog.log("daymeantemperature = " + this.dayMeanTemperature);
		DebugLog.log("airMassNoiseFrequencyMod = " + this.airMassNoiseFrequencyMod);
		DebugLog.log("noiseAirmass = " + this.noiseAirmass);
		DebugLog.log("airMassTemperature = " + this.airMassTemperature);
		DebugLog.log("baseTemperature = " + this.baseTemperature);
		DebugLog.log("dayLightLagged = " + this.dayLightLagged);
		DebugLog.log("nightLagged = " + this.nightLagged);
		DebugLog.log("temperature = " + this.temperature);
		DebugLog.log("temperatureIsSnow = " + this.temperatureIsSnow);
		DebugLog.log("humidity = " + this.humidity);
		DebugLog.log("windIntensity = " + this.windIntensity);
		DebugLog.log("windAngleIntensity = " + this.windAngleIntensity);
		DebugLog.log("windAngleDegrees = " + this.windAngleDegrees);
		DebugLog.log("nightStrength = " + this.nightStrength);
		DebugLog.log("dayLightStrength = " + this.dayLightStrength);
		DebugLog.log("ambient = " + this.ambient);
		DebugLog.log("desaturation = " + this.desaturation);
		DebugLog.log("dayLightStrengthBase = " + this.dayLightStrengthBase);
		DebugLog.log("lerpNight = " + this.lerpNight);
		DebugLog.log("cloudyT = " + this.cloudyT);
		DebugLog.log("cloudIntensity = " + this.cloudIntensity);
		DebugLog.log("airFrontAirmass = " + this.airFrontAirmass);
	}

	public void pollDate(int int1, int int2, int int3) {
		this.pollDate(int1, int2, int3, 0, 0);
	}

	public void pollDate(int int1, int int2, int int3, int int4) {
		this.pollDate(int1, int2, int3, int4, 0);
	}

	public void pollDate(int int1, int int2, int int3, int int4, int int5) {
		this.pollDate(new GregorianCalendar(int1, int2, int3, int4, int5));
	}

	public void pollDate(GregorianCalendar gregorianCalendar) {
		if (this.testCurrentDay == null) {
			this.testCurrentDay = new ClimateManager.DayInfo();
		}

		if (this.testNextDay == null) {
			this.testNextDay = new ClimateManager.DayInfo();
		}

		double double1 = this.gt.getWorldAgeHours();
		this.clim.setDayInfo(this.testCurrentDay, gregorianCalendar.get(5), gregorianCalendar.get(2), gregorianCalendar.get(1), 0);
		this.clim.setDayInfo(this.testNextDay, gregorianCalendar.get(5), gregorianCalendar.get(2), gregorianCalendar.get(1), 1);
		GregorianCalendar gregorianCalendar2 = new GregorianCalendar(this.gt.getYear(), this.gt.getMonth(), this.gt.getDayPlusOne(), this.gt.getHour(), this.gt.getMinutes());
		double double2 = (double)ChronoUnit.MINUTES.between(gregorianCalendar2.toInstant(), gregorianCalendar.toInstant());
		double2 /= 60.0;
		double double3 = double1 + double2;
		float float1 = (float)gregorianCalendar.get(11) + (float)gregorianCalendar.get(12) / 60.0F;
		this.updateValues(double3, float1, this.testCurrentDay, this.testNextDay);
	}

	protected void updateValues(double double1, float float1, ClimateManager.DayInfo dayInfo, ClimateManager.DayInfo dayInfo2) {
		float float2;
		float float3;
		if (dayInfo.year != this.cacheYear || dayInfo.month != this.cacheMonth || dayInfo.day != this.cacheDay) {
			int int1 = (int)this.clim.getSimplexOffsetC();
			int int2 = (int)this.clim.getSimplexOffsetD();
			long long1 = (long)((dayInfo.year - 1990) * 100000);
			long1 += (long)(dayInfo.month * dayInfo.day * 1234);
			long1 += (long)((dayInfo.year - 1990) * dayInfo.month * 10000);
			long1 += (long)((int2 - int1) * dayInfo.day);
			this.seededRandom.setSeed(long1);
			this.dayFogStrength = 0.0F;
			this.dayDoFog = false;
			this.dayFogDuration = 0.0F;
			float float4 = (float)this.seededRandom.nextInt(1000);
			this.dayDoFog = float4 < 200.0F;
			if (this.dayDoFog) {
				this.dayFogDuration = 4.0F;
				if (float4 < 25.0F) {
					this.dayFogStrength = 1.0F;
					this.dayFogDuration += 2.0F;
				} else {
					this.dayFogStrength = this.seededRandom.nextFloat();
				}

				float2 = dayInfo.season.getDayMeanTemperature();
				float3 = (float)SimplexNoise.noise(this.simplexOffsetA, (double1 + 12.0 - 48.0) / this.clim.getAirMassNoiseFrequencyMod(SandboxOptions.instance.getRainModifier()));
				float2 += float3 * 8.0F;
				float float5 = this.seededRandom.nextFloat();
				if (float2 < 0.0F) {
					this.dayFogDuration += 5.0F * this.dayFogStrength;
					this.dayFogDuration += 8.0F * float5;
				} else if (float2 < 10.0F) {
					this.dayFogDuration += 2.5F * this.dayFogStrength;
					this.dayFogDuration += 5.0F * float5;
				} else if (float2 < 20.0F) {
					this.dayFogDuration += 1.5F * this.dayFogStrength;
					this.dayFogDuration += 2.5F * float5;
				} else {
					this.dayFogDuration += 1.0F * this.dayFogStrength;
					this.dayFogDuration += 1.0F * float5;
				}

				if (this.dayFogDuration > 24.0F - dayInfo.season.getDawn()) {
					this.dayFogDuration = 24.0F - dayInfo.season.getDawn() - 1.0F;
				}
			}
		}

		this.cacheWorldAgeHours = double1;
		this.cacheYear = dayInfo.year;
		this.cacheMonth = dayInfo.month;
		this.cacheDay = dayInfo.day;
		this.time = float1;
		this.dawn = dayInfo.season.getDawn();
		this.dusk = dayInfo.season.getDusk();
		this.noon = dayInfo.season.getDayHighNoon();
		this.dayMeanTemperature = dayInfo.season.getDayMeanTemperature();
		float float6 = float1 / 24.0F;
		ClimateManager climateManager = this.clim;
		float float7 = ClimateManager.lerp(float6, dayInfo.season.getCurDayPercent(), dayInfo2.season.getCurDayPercent());
		this.airMassNoiseFrequencyMod = this.clim.getAirMassNoiseFrequencyMod(SandboxOptions.instance.getRainModifier());
		this.noiseAirmass = (float)SimplexNoise.noise(this.simplexOffsetA, double1 / this.airMassNoiseFrequencyMod);
		float float8 = (float)SimplexNoise.noise(this.simplexOffsetC, double1 / this.airMassNoiseFrequencyMod);
		this.airMassTemperature = (float)SimplexNoise.noise(this.simplexOffsetA, (double1 - 48.0) / this.airMassNoiseFrequencyMod);
		double double2 = Math.floor(double1) + 12.0;
		this.airFrontAirmass = (float)SimplexNoise.noise(this.simplexOffsetA, double2 / this.airMassNoiseFrequencyMod);
		climateManager = this.clim;
		float2 = ClimateManager.clerp(float6, dayInfo.season.getDayTemperature(), dayInfo2.season.getDayTemperature());
		climateManager = this.clim;
		float3 = ClimateManager.clerp(float6, dayInfo.season.getDayMeanTemperature(), dayInfo2.season.getDayMeanTemperature());
		boolean boolean1 = float2 < float3;
		this.baseTemperature = float3 + this.airMassTemperature * 8.0F;
		float float9 = 4.0F;
		float float10 = this.dusk + float9;
		if (float10 >= 24.0F) {
			float10 -= 24.0F;
		}

		label49: {
			this.dayLightLagged = this.clim.getTimeLerpHours(float1, this.dawn + float9, float10, true);
			float float11 = 5.0F * (1.0F - this.dayLightLagged);
			this.nightLagged = this.clim.getTimeLerpHours(float1, float10, this.dawn + float9, true);
			float11 += 5.0F * this.nightLagged;
			this.temperature = this.baseTemperature + 1.0F - float11;
			if (!(this.temperature < 0.0F)) {
				climateManager = this.clim;
				if (!ClimateManager.WINTER_IS_COMING) {
					this.temperatureIsSnow = false;
					break label49;
				}
			}

			this.temperatureIsSnow = true;
		}
		float float12 = this.temperature;
		float12 = (45.0F - float12) / 90.0F;
		climateManager = this.clim;
		float12 = ClimateManager.clamp01(1.0F - float12);
		float float13 = (1.0F + float8) * 0.5F;
		this.humidity = float13 * float12;
		float float14 = 1.0F - (this.airMassTemperature + 1.0F) * 0.5F;
		float float15 = 1.0F - float7 * 0.4F;
		float float16 = (float)SimplexNoise.noise(double1 / 40.0, this.simplexOffsetA);
		float float17 = (float16 + 1.0F) * 0.5F;
		float17 *= float14 * float15;
		float17 *= 0.65F;
		this.windIntensity = float17;
		float float18 = (float)SimplexNoise.noise(double1 / 80.0, this.simplexOffsetB);
		this.windAngleIntensity = float18;
		float float19 = (float)SimplexNoise.noise(double1 / 40.0, this.simplexOffsetD);
		float19 = (float19 + 1.0F) * 0.5F;
		this.windAngleDegrees = 360.0F * float19;
		this.lerpNight = this.clim.getTimeLerpHours(float1, this.dusk, this.dawn, true);
		ClimateManager climateManager2 = this.clim;
		this.lerpNight = ClimateManager.clamp(0.0F, 1.0F, this.lerpNight * 2.0F);
		this.nightStrength = this.lerpNight;
		this.dayLightStrengthBase = 1.0F - this.nightStrength;
		float float20 = 1.0F - 0.15F * float7 - 0.2F * this.windIntensity;
		this.dayLightStrengthBase *= float20;
		this.dayLightStrength = this.dayLightStrengthBase;
		this.ambient = this.dayLightStrength;
		float float21 = (1.0F - dayInfo.season.getCurDayPercent()) * 0.4F;
		float float22 = (1.0F - dayInfo2.season.getCurDayPercent()) * 0.4F;
		climateManager2 = this.clim;
		this.desaturation = ClimateManager.lerp(float6, float21, float22);
		ClimateManager climateManager3 = this.clim;
		this.cloudyT = 1.0F - ClimateManager.clamp01((this.airMassTemperature + 0.8F) * 0.625F);
		this.cloudyT *= 0.8F;
		climateManager2 = this.clim;
		this.cloudyT = ClimateManager.clamp01(this.cloudyT + this.windIntensity);
		climateManager2 = this.clim;
		this.cloudIntensity = ClimateManager.clamp01(this.windIntensity * 2.0F);
		this.cloudIntensity -= this.cloudIntensity * 0.5F * this.nightStrength;
	}

	public float getTime() {
		return this.time;
	}

	public float getDawn() {
		return this.dawn;
	}

	public float getDusk() {
		return this.dusk;
	}

	public float getNoon() {
		return this.noon;
	}

	public double getAirMassNoiseFrequencyMod() {
		return this.airMassNoiseFrequencyMod;
	}

	public float getNoiseAirmass() {
		return this.noiseAirmass;
	}

	public float getAirMassTemperature() {
		return this.airMassTemperature;
	}

	public float getBaseTemperature() {
		return this.baseTemperature;
	}

	public float getDayLightLagged() {
		return this.dayLightLagged;
	}

	public float getNightLagged() {
		return this.nightLagged;
	}

	public float getTemperature() {
		return this.temperature;
	}

	public boolean isTemperatureIsSnow() {
		return this.temperatureIsSnow;
	}

	public float getHumidity() {
		return this.humidity;
	}

	public float getWindIntensity() {
		return this.windIntensity;
	}

	public float getWindAngleIntensity() {
		return this.windAngleIntensity;
	}

	public float getWindAngleDegrees() {
		return this.windAngleDegrees;
	}

	public float getNightStrength() {
		return this.nightStrength;
	}

	public float getDayLightStrength() {
		return this.dayLightStrength;
	}

	public float getAmbient() {
		return this.ambient;
	}

	public float getDesaturation() {
		return this.desaturation;
	}

	public float getDayLightStrengthBase() {
		return this.dayLightStrengthBase;
	}

	public float getLerpNight() {
		return this.lerpNight;
	}

	public float getCloudyT() {
		return this.cloudyT;
	}

	public float getCloudIntensity() {
		return this.cloudIntensity;
	}

	public float getAirFrontAirmass() {
		return this.airFrontAirmass;
	}

	public double getCacheWorldAgeHours() {
		return this.cacheWorldAgeHours;
	}

	public int getCacheYear() {
		return this.cacheYear;
	}

	public int getCacheMonth() {
		return this.cacheMonth;
	}

	public int getCacheDay() {
		return this.cacheDay;
	}

	public float getDayMeanTemperature() {
		return this.dayMeanTemperature;
	}

	public boolean isDayDoFog() {
		return this.dayDoFog;
	}

	public float getDayFogStrength() {
		return this.dayFogStrength;
	}

	public float getDayFogDuration() {
		return this.dayFogDuration;
	}
}
