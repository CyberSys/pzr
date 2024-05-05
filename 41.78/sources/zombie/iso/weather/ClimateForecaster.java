package zombie.iso.weather;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import zombie.GameTime;


public class ClimateForecaster {
	private static final int OffsetToday = 10;
	private ClimateValues climateValues;
	private ClimateForecaster.DayForecast[] forecasts = new ClimateForecaster.DayForecast[40];
	private ArrayList forecastList = new ArrayList(40);

	public ArrayList getForecasts() {
		return this.forecastList;
	}

	public ClimateForecaster.DayForecast getForecast() {
		return this.getForecast(0);
	}

	public ClimateForecaster.DayForecast getForecast(int int1) {
		int int2 = 10 + int1;
		return int2 >= 0 && int2 < this.forecasts.length ? this.forecasts[int2] : null;
	}

	private void populateForecastList() {
		this.forecastList.clear();
		for (int int1 = 0; int1 < this.forecasts.length; ++int1) {
			this.forecastList.add(this.forecasts[int1]);
		}
	}

	protected void init(ClimateManager climateManager) {
		this.climateValues = climateManager.getClimateValuesCopy();
		for (int int1 = 0; int1 < this.forecasts.length; ++int1) {
			int int2 = int1 - 10;
			ClimateForecaster.DayForecast dayForecast = new ClimateForecaster.DayForecast();
			dayForecast.weatherPeriod = new WeatherPeriod(climateManager, climateManager.getThunderStorm());
			dayForecast.weatherPeriod.setDummy(true);
			dayForecast.indexOffset = int2;
			dayForecast.airFront = new ClimateManager.AirFront();
			this.sampleDay(climateManager, dayForecast, int2);
			this.forecasts[int1] = dayForecast;
		}

		this.populateForecastList();
	}

	protected void updateDayChange(ClimateManager climateManager) {
		ClimateForecaster.DayForecast dayForecast = this.forecasts[0];
		for (int int1 = 0; int1 < this.forecasts.length; ++int1) {
			if (int1 > 0 && int1 < this.forecasts.length) {
				this.forecasts[int1].indexOffset = int1 - 1 - 10;
				this.forecasts[int1 - 1] = this.forecasts[int1];
			}
		}

		dayForecast.reset();
		this.sampleDay(climateManager, dayForecast, this.forecasts.length - 1 - 10);
		dayForecast.indexOffset = this.forecasts.length - 1 - 10;
		this.forecasts[this.forecasts.length - 1] = dayForecast;
		this.populateForecastList();
	}

	protected void sampleDay(ClimateManager climateManager, ClimateForecaster.DayForecast dayForecast, int int1) {
		GameTime gameTime = GameTime.getInstance();
		int int2 = gameTime.getYear();
		int int3 = gameTime.getMonth();
		int int4 = gameTime.getDayPlusOne();
		GregorianCalendar gregorianCalendar = new GregorianCalendar(int2, int3, int4, 0, 0);
		gregorianCalendar.add(5, int1);
		boolean boolean1 = true;
		ClimateForecaster.DayForecast dayForecast2 = this.getWeatherOverlap(int1 + 10, 0.0F);
		dayForecast.weatherOverlap = dayForecast2;
		dayForecast.weatherPeriod.stopWeatherPeriod();
		int int5 = gregorianCalendar.get(1);
		dayForecast.name = "day: " + int5 + " - " + (gregorianCalendar.get(2) + 1) + " - " + gregorianCalendar.get(5);
		for (int int6 = 0; int6 < 24; ++int6) {
			if (int6 != 0) {
				gregorianCalendar.add(11, 1);
			}

			this.climateValues.pollDate(gregorianCalendar);
			if (int6 == 0) {
				boolean1 = this.climateValues.getNoiseAirmass() >= 0.0F;
				dayForecast.airFrontString = boolean1 ? "WARM" : "COLD";
				dayForecast.dawn = this.climateValues.getDawn();
				dayForecast.dusk = this.climateValues.getDusk();
				dayForecast.dayLightHours = dayForecast.dusk - dayForecast.dawn;
			}

			float float1;
			if (!dayForecast.weatherStarts && (boolean1 && this.climateValues.getNoiseAirmass() < 0.0F || !boolean1 && this.climateValues.getNoiseAirmass() >= 0.0F)) {
				int int7 = this.climateValues.getNoiseAirmass() >= 0.0F ? -1 : 1;
				dayForecast.airFront.setFrontType(int7);
				climateManager.CalculateWeatherFrontStrength(gregorianCalendar.get(1), gregorianCalendar.get(2), gregorianCalendar.get(5), dayForecast.airFront);
				dayForecast.airFront.setFrontWind(this.climateValues.getWindAngleDegrees());
				if (dayForecast.airFront.getStrength() >= 0.1F) {
					ClimateForecaster.DayForecast dayForecast3 = this.getWeatherOverlap(int1 + 10, (float)int6);
					float1 = dayForecast3 != null ? dayForecast3.weatherPeriod.getTotalStrength() : -1.0F;
					if (float1 < 0.1F) {
						dayForecast.weatherStarts = true;
						dayForecast.weatherStartTime = (float)int6;
						dayForecast.weatherPeriod.init(dayForecast.airFront, this.climateValues.getCacheWorldAgeHours(), gregorianCalendar.get(1), gregorianCalendar.get(2), gregorianCalendar.get(5));
					}
				}

				if (!dayForecast.weatherStarts) {
					boolean1 = !boolean1;
				}
			}

			boolean boolean2 = (float)int6 > this.climateValues.getDawn() && (float)int6 <= this.climateValues.getDusk();
			float float2 = this.climateValues.getTemperature();
			float1 = this.climateValues.getHumidity();
			float float3 = this.climateValues.getWindAngleDegrees();
			float float4 = this.climateValues.getWindIntensity();
			float float5 = this.climateValues.getCloudIntensity();
			if (dayForecast.weatherStarts || dayForecast.weatherOverlap != null) {
				WeatherPeriod weatherPeriod = dayForecast.weatherStarts ? dayForecast.weatherPeriod : dayForecast.weatherOverlap.weatherPeriod;
				if (weatherPeriod != null) {
					float3 = weatherPeriod.getWindAngleDegrees();
					WeatherPeriod.WeatherStage weatherStage = weatherPeriod.getStageForWorldAge(this.climateValues.getCacheWorldAgeHours());
					if (weatherStage != null) {
						if (!dayForecast.weatherStages.contains(weatherStage.getStageID())) {
							dayForecast.weatherStages.add(weatherStage.getStageID());
						}

						switch (weatherStage.getStageID()) {
						case 1: 
							dayForecast.hasHeavyRain = true;
						
						case 4: 
						
						case 5: 
						
						case 6: 
						
						default: 
							float2 -= WeatherPeriod.getMaxTemperatureInfluence() * 0.25F;
							float5 = 0.35F + 0.5F * weatherPeriod.getTotalStrength();
							break;
						
						case 2: 
							float4 = 0.5F * weatherPeriod.getTotalStrength();
							float2 -= WeatherPeriod.getMaxTemperatureInfluence() * float4;
							float5 = 0.5F + 0.5F * float4;
							dayForecast.hasHeavyRain = true;
							break;
						
						case 3: 
							float4 = 0.2F + 0.5F * weatherPeriod.getTotalStrength();
							float2 -= WeatherPeriod.getMaxTemperatureInfluence() * float4;
							float5 = 0.5F + 0.5F * float4;
							dayForecast.hasStorm = true;
							break;
						
						case 7: 
							dayForecast.chanceOnSnow = true;
							float4 = 0.75F + 0.25F * weatherPeriod.getTotalStrength();
							float2 -= WeatherPeriod.getMaxTemperatureInfluence() * float4;
							float5 = 0.5F + 0.5F * float4;
							dayForecast.hasBlizzard = true;
							break;
						
						case 8: 
							float4 = 0.4F + 0.6F * weatherPeriod.getTotalStrength();
							float2 -= WeatherPeriod.getMaxTemperatureInfluence() * float4;
							float5 = 0.5F + 0.5F * float4;
							dayForecast.hasTropicalStorm = true;
						
						}
					} else if (dayForecast.weatherOverlap != null && (float)int6 < dayForecast.weatherEndTime) {
						dayForecast.weatherEndTime = (float)int6;
					}
				}

				if (float2 < 0.0F) {
					dayForecast.chanceOnSnow = true;
				}
			}

			dayForecast.temperature.add(float2, boolean2);
			dayForecast.humidity.add(float1, boolean2);
			dayForecast.windDirection.add(float3, boolean2);
			dayForecast.windPower.add(float4, boolean2);
			dayForecast.cloudiness.add(float5, boolean2);
		}

		dayForecast.temperature.calculate();
		dayForecast.humidity.calculate();
		dayForecast.windDirection.calculate();
		dayForecast.windPower.calculate();
		dayForecast.cloudiness.calculate();
		dayForecast.hasFog = this.climateValues.isDayDoFog();
		dayForecast.fogStrength = this.climateValues.getDayFogStrength();
		dayForecast.fogDuration = this.climateValues.getDayFogDuration();
	}

	private ClimateForecaster.DayForecast getWeatherOverlap(int int1, float float1) {
		int int2 = Math.max(0, int1 - 10);
		if (int2 == int1) {
			return null;
		} else {
			for (int int3 = int2; int3 < int1; ++int3) {
				if (this.forecasts[int3].weatherStarts) {
					float float2 = (float)this.forecasts[int3].weatherPeriod.getDuration() / 24.0F;
					float float3 = (float)int3 + this.forecasts[int3].weatherStartTime / 24.0F;
					float3 += float2;
					float float4 = (float)int1 + float1 / 24.0F;
					if (float3 > float4) {
						return this.forecasts[int3];
					}
				}
			}

			return null;
		}
	}

	public int getDaysTillFirstWeather() {
		int int1 = -1;
		for (int int2 = 10; int2 < this.forecasts.length - 1; ++int2) {
			if (this.forecasts[int2].weatherStarts && int1 < 0) {
				int1 = int2;
			}
		}

		return int1;
	}

	public static class DayForecast {
		private int indexOffset = 0;
		private String name = "Day x";
		private WeatherPeriod weatherPeriod;
		private ClimateForecaster.ForecastValue temperature = new ClimateForecaster.ForecastValue();
		private ClimateForecaster.ForecastValue humidity = new ClimateForecaster.ForecastValue();
		private ClimateForecaster.ForecastValue windDirection = new ClimateForecaster.ForecastValue();
		private ClimateForecaster.ForecastValue windPower = new ClimateForecaster.ForecastValue();
		private ClimateForecaster.ForecastValue cloudiness = new ClimateForecaster.ForecastValue();
		private boolean weatherStarts = false;
		private float weatherStartTime = 0.0F;
		private float weatherEndTime = 24.0F;
		private boolean chanceOnSnow = false;
		private String airFrontString = "";
		private boolean hasFog = false;
		private float fogStrength = 0.0F;
		private float fogDuration = 0.0F;
		private ClimateManager.AirFront airFront;
		private ClimateForecaster.DayForecast weatherOverlap;
		private boolean hasHeavyRain = false;
		private boolean hasStorm = false;
		private boolean hasTropicalStorm = false;
		private boolean hasBlizzard = false;
		private float dawn = 0.0F;
		private float dusk = 0.0F;
		private float dayLightHours = 0.0F;
		private ArrayList weatherStages = new ArrayList();

		public int getIndexOffset() {
			return this.indexOffset;
		}

		public String getName() {
			return this.name;
		}

		public ClimateForecaster.ForecastValue getTemperature() {
			return this.temperature;
		}

		public ClimateForecaster.ForecastValue getHumidity() {
			return this.humidity;
		}

		public ClimateForecaster.ForecastValue getWindDirection() {
			return this.windDirection;
		}

		public ClimateForecaster.ForecastValue getWindPower() {
			return this.windPower;
		}

		public ClimateForecaster.ForecastValue getCloudiness() {
			return this.cloudiness;
		}

		public WeatherPeriod getWeatherPeriod() {
			return this.weatherPeriod;
		}

		public boolean isWeatherStarts() {
			return this.weatherStarts;
		}

		public float getWeatherStartTime() {
			return this.weatherStartTime;
		}

		public float getWeatherEndTime() {
			return this.weatherEndTime;
		}

		public boolean isChanceOnSnow() {
			return this.chanceOnSnow;
		}

		public String getAirFrontString() {
			return this.airFrontString;
		}

		public boolean isHasFog() {
			return this.hasFog;
		}

		public ClimateManager.AirFront getAirFront() {
			return this.airFront;
		}

		public ClimateForecaster.DayForecast getWeatherOverlap() {
			return this.weatherOverlap;
		}

		public String getMeanWindAngleString() {
			return ClimateManager.getWindAngleString(this.windDirection.getTotalMean());
		}

		public float getFogStrength() {
			return this.fogStrength;
		}

		public float getFogDuration() {
			return this.fogDuration;
		}

		public boolean isHasHeavyRain() {
			return this.hasHeavyRain;
		}

		public boolean isHasStorm() {
			return this.hasStorm;
		}

		public boolean isHasTropicalStorm() {
			return this.hasTropicalStorm;
		}

		public boolean isHasBlizzard() {
			return this.hasBlizzard;
		}

		public ArrayList getWeatherStages() {
			return this.weatherStages;
		}

		public float getDawn() {
			return this.dawn;
		}

		public float getDusk() {
			return this.dusk;
		}

		public float getDayLightHours() {
			return this.dayLightHours;
		}

		private void reset() {
			this.weatherPeriod.stopWeatherPeriod();
			this.temperature.reset();
			this.humidity.reset();
			this.windDirection.reset();
			this.windPower.reset();
			this.cloudiness.reset();
			this.weatherStarts = false;
			this.weatherStartTime = 0.0F;
			this.weatherEndTime = 24.0F;
			this.chanceOnSnow = false;
			this.hasFog = false;
			this.fogStrength = 0.0F;
			this.fogDuration = 0.0F;
			this.weatherOverlap = null;
			this.hasHeavyRain = false;
			this.hasStorm = false;
			this.hasTropicalStorm = false;
			this.hasBlizzard = false;
			this.weatherStages.clear();
		}
	}

	public static class ForecastValue {
		private float dayMin;
		private float dayMax;
		private float dayMean;
		private int dayMeanTicks;
		private float nightMin;
		private float nightMax;
		private float nightMean;
		private int nightMeanTicks;
		private float totalMin;
		private float totalMax;
		private float totalMean;
		private int totalMeanTicks;

		public ForecastValue() {
			this.reset();
		}

		public float getDayMin() {
			return this.dayMin;
		}

		public float getDayMax() {
			return this.dayMax;
		}

		public float getDayMean() {
			return this.dayMean;
		}

		public float getNightMin() {
			return this.nightMin;
		}

		public float getNightMax() {
			return this.nightMax;
		}

		public float getNightMean() {
			return this.nightMean;
		}

		public float getTotalMin() {
			return this.totalMin;
		}

		public float getTotalMax() {
			return this.totalMax;
		}

		public float getTotalMean() {
			return this.totalMean;
		}

		protected void add(float float1, boolean boolean1) {
			if (boolean1) {
				if (float1 < this.dayMin) {
					this.dayMin = float1;
				}

				if (float1 > this.dayMax) {
					this.dayMax = float1;
				}

				this.dayMean += float1;
				++this.dayMeanTicks;
			} else {
				if (float1 < this.nightMin) {
					this.nightMin = float1;
				}

				if (float1 > this.nightMax) {
					this.nightMax = float1;
				}

				this.nightMean += float1;
				++this.nightMeanTicks;
			}

			if (float1 < this.totalMin) {
				this.totalMin = float1;
			}

			if (float1 > this.totalMax) {
				this.totalMax = float1;
			}

			this.totalMean += float1;
			++this.totalMeanTicks;
		}

		protected void calculate() {
			if (this.totalMeanTicks <= 0) {
				this.totalMean = 0.0F;
			} else {
				this.totalMean /= (float)this.totalMeanTicks;
			}

			if (this.dayMeanTicks <= 0) {
				this.dayMin = this.totalMin;
				this.dayMax = this.totalMax;
				this.dayMean = this.totalMean;
			} else {
				this.dayMean /= (float)this.dayMeanTicks;
			}

			if (this.nightMeanTicks <= 0) {
				this.nightMin = this.totalMin;
				this.nightMax = this.totalMax;
				this.nightMean = this.totalMean;
			} else {
				this.nightMean /= (float)this.nightMeanTicks;
			}
		}

		protected void reset() {
			this.dayMin = 10000.0F;
			this.dayMax = -10000.0F;
			this.dayMean = 0.0F;
			this.dayMeanTicks = 0;
			this.nightMin = 10000.0F;
			this.nightMax = -10000.0F;
			this.nightMean = 0.0F;
			this.nightMeanTicks = 0;
			this.totalMin = 10000.0F;
			this.totalMax = -10000.0F;
			this.totalMean = 0.0F;
			this.totalMeanTicks = 0;
		}
	}
}
