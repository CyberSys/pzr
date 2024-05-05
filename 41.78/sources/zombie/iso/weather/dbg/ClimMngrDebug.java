package zombie.iso.weather.dbg;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.erosion.season.ErosionSeason;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.SimplexNoise;
import zombie.iso.weather.ThunderStorm;
import zombie.iso.weather.WeatherPeriod;
import zombie.network.GameClient;


public class ClimMngrDebug extends ClimateManager {
	private GregorianCalendar calendar;
	private double worldAgeHours = 0.0;
	private double worldAgeHoursStart = 0.0;
	private double weatherPeriodTime = 0.0;
	private double simplexOffsetA;
	private ClimateManager.AirFront currentFront = new ClimateManager.AirFront();
	private WeatherPeriod weatherPeriod = new WeatherPeriod(this, (ThunderStorm)null);
	private boolean tickIsDayChange = false;
	public ArrayList runs = new ArrayList();
	private ClimMngrDebug.RunInfo currentRun;
	private ErosionSeason season;
	private int TotalDaysPeriodIndexMod = 5;
	private boolean DoOverrideSandboxRainMod = false;
	private int SandboxRainModOverride = 3;
	private int durDays = 0;
	private static final int WEATHER_NORMAL = 0;
	private static final int WEATHER_STORM = 1;
	private static final int WEATHER_TROPICAL = 2;
	private static final int WEATHER_BLIZZARD = 3;
	private FileWriter writer;

	public ClimMngrDebug() {
		this.weatherPeriod.setPrintStuff(false);
	}

	public void setRainModOverride(int int1) {
		this.DoOverrideSandboxRainMod = true;
		this.SandboxRainModOverride = int1;
	}

	public void unsetRainModOverride() {
		this.DoOverrideSandboxRainMod = false;
		this.SandboxRainModOverride = 3;
	}

	public void SimulateDays(int int1, int int2) {
		this.durDays = int1;
		DebugLog.log("Starting " + int2 + " simulations of " + int1 + " days per run...");
		byte byte1 = 0;
		byte byte2 = 0;
		DebugLog.log("Year: " + GameTime.instance.getYear() + ", Month: " + byte1 + ", Day: " + byte2);
		for (int int3 = 0; int3 < int2; ++int3) {
			this.calendar = new GregorianCalendar(GameTime.instance.getYear(), byte1, byte2, 0, 0);
			this.season = ClimateManager.getInstance().getSeason().clone();
			this.season.init(this.season.getLat(), this.season.getTempMax(), this.season.getTempMin(), this.season.getTempDiff(), this.season.getSeasonLag(), this.season.getHighNoon(), Rand.Next(0, 255), Rand.Next(0, 255), Rand.Next(0, 255));
			this.simplexOffsetA = (double)Rand.Next(0, 8000);
			this.worldAgeHours = 250.0;
			this.weatherPeriodTime = this.worldAgeHours;
			this.worldAgeHoursStart = this.worldAgeHours;
			double double1 = this.getAirMassNoiseFrequencyMod(SandboxOptions.instance.getRainModifier());
			float float1 = (float)SimplexNoise.noise(this.simplexOffsetA, this.worldAgeHours / double1);
			int int4 = float1 < 0.0F ? -1 : 1;
			this.currentFront.setFrontType(int4);
			this.weatherPeriod.stopWeatherPeriod();
			double double2 = this.worldAgeHours + 24.0;
			int int5 = int1 * 24;
			this.currentRun = new ClimMngrDebug.RunInfo();
			this.currentRun.durationDays = int1;
			this.currentRun.durationHours = (double)int5;
			this.currentRun.seedA = this.simplexOffsetA;
			this.runs.add(this.currentRun);
			for (int int6 = 0; int6 < int5; ++int6) {
				this.tickIsDayChange = false;
				++this.worldAgeHours;
				if (this.worldAgeHours >= double2) {
					this.tickIsDayChange = true;
					double2 += 24.0;
					this.calendar.add(5, 1);
					int int7 = this.calendar.get(5);
					int int8 = this.calendar.get(2);
					int int9 = this.calendar.get(1);
					this.season.setDay(int7, int8, int9);
				}

				this.update_sim();
			}
		}

		this.saveData();
	}

	private void update_sim() {
		double double1 = this.getAirMassNoiseFrequencyMod(SandboxOptions.instance.getRainModifier());
		float float1 = (float)SimplexNoise.noise(this.simplexOffsetA, this.worldAgeHours / double1);
		int int1 = float1 < 0.0F ? -1 : 1;
		if (this.currentFront.getType() != int1) {
			if (this.worldAgeHours > this.weatherPeriodTime) {
				this.weatherPeriod.initSimulationDebug(this.currentFront, this.worldAgeHours);
				this.recordAndCloseWeatherPeriod();
			}

			this.currentFront.setFrontType(int1);
		}

		if (!WINTER_IS_COMING && !THE_DESCENDING_FOG && this.worldAgeHours >= this.worldAgeHoursStart + 72.0 && this.worldAgeHours <= this.worldAgeHoursStart + 96.0 && !this.weatherPeriod.isRunning() && this.worldAgeHours > this.weatherPeriodTime && Rand.Next(0, 1000) < 50) {
			this.triggerCustomWeatherStage(3, 10.0F);
		}

		if (this.tickIsDayChange) {
			double double2 = Math.floor(this.worldAgeHours) + 12.0;
			float float2 = (float)SimplexNoise.noise(this.simplexOffsetA, double2 / double1);
			int1 = float2 < 0.0F ? -1 : 1;
			if (int1 == this.currentFront.getType()) {
				this.currentFront.addDaySample(float2);
			}
		}
	}

	private void recordAndCloseWeatherPeriod() {
		if (this.weatherPeriod.isRunning()) {
			if (this.worldAgeHours - this.weatherPeriodTime > 0.0) {
				this.currentRun.addRecord(this.worldAgeHours - this.weatherPeriodTime);
			}

			this.weatherPeriodTime = this.worldAgeHours + Math.ceil(this.weatherPeriod.getDuration());
			boolean boolean1 = false;
			boolean boolean2 = false;
			boolean boolean3 = false;
			Iterator iterator = this.weatherPeriod.getWeatherStages().iterator();
			while (iterator.hasNext()) {
				WeatherPeriod.WeatherStage weatherStage = (WeatherPeriod.WeatherStage)iterator.next();
				if (weatherStage.getStageID() == 3) {
					boolean1 = true;
				}

				if (weatherStage.getStageID() == 8) {
					boolean2 = true;
				}

				if (weatherStage.getStageID() == 7) {
					boolean3 = true;
				}
			}

			this.currentRun.addRecord(this.currentFront.getType(), this.weatherPeriod.getDuration(), this.weatherPeriod.getFrontCache().getStrength(), boolean1, boolean2, boolean3);
		}

		this.weatherPeriod.stopWeatherPeriod();
	}

	public boolean triggerCustomWeatherStage(int int1, float float1) {
		if (!GameClient.bClient && !this.weatherPeriod.isRunning()) {
			ClimateManager.AirFront airFront = new ClimateManager.AirFront();
			airFront.setFrontType(1);
			airFront.setStrength(0.95F);
			this.weatherPeriod.initSimulationDebug(airFront, this.worldAgeHours, int1, float1);
			this.recordAndCloseWeatherPeriod();
			return true;
		} else {
			return false;
		}
	}

	protected double getAirMassNoiseFrequencyMod(int int1) {
		return this.DoOverrideSandboxRainMod ? super.getAirMassNoiseFrequencyMod(this.SandboxRainModOverride) : super.getAirMassNoiseFrequencyMod(int1);
	}

	protected float getRainTimeMultiplierMod(int int1) {
		return this.DoOverrideSandboxRainMod ? super.getRainTimeMultiplierMod(this.SandboxRainModOverride) : super.getRainTimeMultiplierMod(int1);
	}

	public ErosionSeason getSeason() {
		return this.season;
	}

	public float getDayMeanTemperature() {
		return this.season.getDayMeanTemperature();
	}

	public void resetOverrides() {
	}

	private ClimMngrDebug.RunInfo calculateTotal() {
		ClimMngrDebug.RunInfo runInfo = new ClimMngrDebug.RunInfo();
		runInfo.totalDaysPeriod = new int[50];
		double double1 = 0.0;
		double double2 = 0.0;
		float float1 = 0.0F;
		float float2 = 0.0F;
		float float3 = 0.0F;
		Iterator iterator = this.runs.iterator();
		while (iterator.hasNext()) {
			ClimMngrDebug.RunInfo runInfo2 = (ClimMngrDebug.RunInfo)iterator.next();
			if (runInfo2.totalPeriodDuration < runInfo.mostDryPeriod) {
				runInfo.mostDryPeriod = runInfo2.totalPeriodDuration;
			}

			if (runInfo2.totalPeriodDuration > runInfo.mostWetPeriod) {
				runInfo.mostWetPeriod = runInfo2.totalPeriodDuration;
			}

			runInfo.totalPeriodDuration += runInfo2.totalPeriodDuration;
			if (runInfo2.longestPeriod > runInfo.longestPeriod) {
				runInfo.longestPeriod = runInfo2.longestPeriod;
			}

			if (runInfo2.shortestPeriod < runInfo.shortestPeriod) {
				runInfo.shortestPeriod = runInfo2.shortestPeriod;
			}

			runInfo.totalPeriods += runInfo2.totalPeriods;
			runInfo.averagePeriod += runInfo2.averagePeriod;
			if (runInfo2.longestEmpty > runInfo.longestEmpty) {
				runInfo.longestEmpty = runInfo2.longestEmpty;
			}

			if (runInfo2.shortestEmpty < runInfo.shortestEmpty) {
				runInfo.shortestEmpty = runInfo2.shortestEmpty;
			}

			runInfo.totalEmpty += runInfo2.totalEmpty;
			runInfo.averageEmpty += runInfo2.averageEmpty;
			if (runInfo2.highestStrength > runInfo.highestStrength) {
				runInfo.highestStrength = runInfo2.highestStrength;
			}

			if (runInfo2.lowestStrength < runInfo.lowestStrength) {
				runInfo.lowestStrength = runInfo2.lowestStrength;
			}

			runInfo.averageStrength += runInfo2.averageStrength;
			if (runInfo2.highestWarmStrength > runInfo.highestWarmStrength) {
				runInfo.highestWarmStrength = runInfo2.highestWarmStrength;
			}

			if (runInfo2.lowestWarmStrength < runInfo.lowestWarmStrength) {
				runInfo.lowestWarmStrength = runInfo2.lowestWarmStrength;
			}

			runInfo.averageWarmStrength += runInfo2.averageWarmStrength;
			if (runInfo2.highestColdStrength > runInfo.highestColdStrength) {
				runInfo.highestColdStrength = runInfo2.highestColdStrength;
			}

			if (runInfo2.lowestColdStrength < runInfo.lowestColdStrength) {
				runInfo.lowestColdStrength = runInfo2.lowestColdStrength;
			}

			runInfo.averageColdStrength += runInfo2.averageColdStrength;
			runInfo.countNormalWarm += runInfo2.countNormalWarm;
			runInfo.countNormalCold += runInfo2.countNormalCold;
			runInfo.countStorm += runInfo2.countStorm;
			runInfo.countTropical += runInfo2.countTropical;
			runInfo.countBlizzard += runInfo2.countBlizzard;
			int int1;
			int[] intArray;
			for (int1 = 0; int1 < runInfo2.dayCountPeriod.length; ++int1) {
				intArray = runInfo.dayCountPeriod;
				intArray[int1] += runInfo2.dayCountPeriod[int1];
			}

			for (int1 = 0; int1 < runInfo2.dayCountWarmPeriod.length; ++int1) {
				intArray = runInfo.dayCountWarmPeriod;
				intArray[int1] += runInfo2.dayCountWarmPeriod[int1];
			}

			for (int1 = 0; int1 < runInfo2.dayCountColdPeriod.length; ++int1) {
				intArray = runInfo.dayCountColdPeriod;
				intArray[int1] += runInfo2.dayCountColdPeriod[int1];
			}

			for (int1 = 0; int1 < runInfo2.dayCountEmpty.length; ++int1) {
				intArray = runInfo.dayCountEmpty;
				intArray[int1] += runInfo2.dayCountEmpty[int1];
			}

			for (int1 = 0; int1 < runInfo2.exceedingPeriods.size(); ++int1) {
				runInfo.exceedingPeriods.add((Integer)runInfo2.exceedingPeriods.get(int1));
			}

			for (int1 = 0; int1 < runInfo2.exceedingEmpties.size(); ++int1) {
				runInfo.exceedingEmpties.add((Integer)runInfo2.exceedingEmpties.get(int1));
			}

			int1 = (int)(runInfo2.totalPeriodDuration / (double)(this.TotalDaysPeriodIndexMod * 24));
			if (int1 < runInfo.totalDaysPeriod.length) {
				int int2 = runInfo.totalDaysPeriod[int1]++;
			} else {
				int int3 = int1 * this.TotalDaysPeriodIndexMod;
				DebugLog.log("Total days Period is longer than allowed array, days = " + int3);
			}
		}

		if (this.runs.size() > 0) {
			int int4 = this.runs.size();
			runInfo.totalPeriodDuration /= (double)int4;
			runInfo.averagePeriod /= (double)int4;
			runInfo.averageEmpty /= (double)int4;
			runInfo.averageStrength /= (float)int4;
			runInfo.averageWarmStrength /= (float)int4;
			runInfo.averageColdStrength /= (float)int4;
		}

		return runInfo;
	}

	private void saveData() {
		if (this.runs.size() > 0) {
			try {
				Iterator iterator = this.runs.iterator();
				while (iterator.hasNext()) {
					ClimMngrDebug.RunInfo runInfo = (ClimMngrDebug.RunInfo)iterator.next();
					runInfo.calculate();
				}

				ClimMngrDebug.RunInfo runInfo2 = this.calculateTotal();
				String string = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
				ZomboidFileSystem.instance.getFileInCurrentSave("climate").mkdirs();
				File file = ZomboidFileSystem.instance.getFileInCurrentSave("climate");
				if (file.exists() && file.isDirectory()) {
					String string2 = ZomboidFileSystem.instance.getFileNameInCurrentSave("climate", string + ".txt");
					DebugLog.log("Attempting to save test data to: " + string2);
					File file2 = new File(string2);
					DebugLog.log("Saving climate test data: " + string2);
					FileWriter fileWriter;
					try {
						fileWriter = new FileWriter(file2, false);
						try {
							this.writer = fileWriter;
							int int1 = this.runs.size();
							this.write("Simulation results." + System.lineSeparator());
							int int2 = this.runs.size();
							this.write("Runs: " + int2 + ", days per cycle: " + this.durDays);
							if (this.DoOverrideSandboxRainMod) {
								this.write("RainModifier used: " + this.SandboxRainModOverride);
							} else {
								this.write("RainModifier used: " + SandboxOptions.instance.getRainModifier());
							}

							this.write("");
							this.write("===================================================================");
							this.write(" TOTALS OVERVIEW");
							this.write("===================================================================");
							this.write("");
							this.write("Total weather periods: " + runInfo2.totalPeriods + ", average per cycle: " + runInfo2.totalPeriods / int1);
							String string3 = this.formatDuration(runInfo2.longestPeriod);
							this.write("Longest weather: " + string3);
							string3 = this.formatDuration(runInfo2.shortestPeriod);
							this.write("Shortest weather: " + string3);
							string3 = this.formatDuration(runInfo2.averagePeriod);
							this.write("Average weather: " + string3);
							this.write("");
							string3 = this.formatDuration(runInfo2.totalPeriodDuration);
							this.write("Average total weather days per cycle: " + string3);
							this.write("");
							string3 = this.formatDuration(runInfo2.mostDryPeriod);
							this.write("Driest cycle total weather days: " + string3);
							string3 = this.formatDuration(runInfo2.mostWetPeriod);
							this.write("Wettest cycle total weather days: " + string3);
							this.write("");
							this.write("Total clear periods: " + runInfo2.totalEmpty + ", average per cycle: " + runInfo2.totalEmpty / int1);
							string3 = this.formatDuration(runInfo2.longestEmpty);
							this.write("Longest clear: " + string3);
							string3 = this.formatDuration(runInfo2.shortestEmpty);
							this.write("Shortest clear: " + string3);
							string3 = this.formatDuration(runInfo2.averageEmpty);
							this.write("Average clear: " + string3);
							this.write("");
							this.write("Highest Front strength: " + runInfo2.highestStrength);
							this.write("Lowest Front strength: " + runInfo2.lowestStrength);
							this.write("Average Front strength: " + runInfo2.averageStrength);
							this.write("");
							this.write("Highest WarmFront strength: " + runInfo2.highestWarmStrength);
							this.write("Lowest WarmFront strength: " + runInfo2.lowestWarmStrength);
							this.write("Average WarmFront strength: " + runInfo2.averageWarmStrength);
							this.write("");
							this.write("Highest ColdFront strength: " + runInfo2.highestColdStrength);
							this.write("Lowest ColdFront strength: " + runInfo2.lowestColdStrength);
							this.write("Average ColdFront strength: " + runInfo2.averageColdStrength);
							this.write("");
							this.write("Weather period types:");
							double double1 = (double)int1;
							int2 = runInfo2.countNormalWarm;
							this.write("Normal warm: " + int2 + ", average: " + this.round((double)runInfo2.countNormalWarm / double1));
							int2 = runInfo2.countNormalCold;
							this.write("Normal cold: " + int2 + ", average: " + this.round((double)runInfo2.countNormalCold / double1));
							int2 = runInfo2.countStorm;
							this.write("Normal storm: " + int2 + ", average: " + this.round((double)runInfo2.countStorm / (double)int1));
							int2 = runInfo2.countTropical;
							this.write("Normal tropical: " + int2 + ", average: " + this.round((double)runInfo2.countTropical / double1));
							int2 = runInfo2.countBlizzard;
							this.write("Normal blizzard: " + int2 + ", average: " + this.round((double)runInfo2.countBlizzard / double1));
							this.write("");
							this.write("Distribution duration in days (total periods)");
							this.printCountTable(fileWriter, runInfo2.dayCountPeriod);
							this.write("");
							this.write("Distribution duration in days (WARM periods)");
							this.printCountTable(fileWriter, runInfo2.dayCountWarmPeriod);
							this.write("");
							this.write("Distribution duration in days (COLD periods)");
							this.printCountTable(fileWriter, runInfo2.dayCountColdPeriod);
							this.write("");
							this.write("Distribution duration in days (clear periods)");
							this.printCountTable(fileWriter, runInfo2.dayCountEmpty);
							this.write("");
							this.write("Amount of weather periods exceeding threshold: " + runInfo2.exceedingPeriods.size());
							Iterator iterator2;
							Integer integer;
							if (runInfo2.exceedingPeriods.size() > 0) {
								iterator2 = runInfo2.exceedingPeriods.iterator();
								while (iterator2.hasNext()) {
									integer = (Integer)iterator2.next();
									this.writer.write(integer + " days, ");
								}
							}

							this.write("");
							this.write("");
							this.write("Amount of clear periods exceeding threshold: " + runInfo2.exceedingEmpties.size());
							if (runInfo2.exceedingEmpties.size() > 0) {
								iterator2 = runInfo2.exceedingEmpties.iterator();
								while (iterator2.hasNext()) {
									integer = (Integer)iterator2.next();
									this.writer.write(integer + " days, ");
								}
							}

							this.write("");
							this.write("");
							this.write("Distribution duration total weather days:");
							this.printCountTable(this.writer, runInfo2.totalDaysPeriod, this.TotalDaysPeriodIndexMod);
							this.writeDataExtremes();
							this.writer = null;
						} catch (Throwable throwable) {
							try {
								fileWriter.close();
							} catch (Throwable throwable2) {
								throwable.addSuppressed(throwable2);
							}

							throw throwable;
						}

						fileWriter.close();
					} catch (Exception exception) {
						exception.printStackTrace();
					}

					file2 = ZomboidFileSystem.instance.getFileInCurrentSave("climate", string + "_DATA.txt");
					try {
						fileWriter = new FileWriter(file2, false);
						try {
							this.writer = fileWriter;
							this.writeData();
							this.writer = null;
						} catch (Throwable throwable3) {
							try {
								fileWriter.close();
							} catch (Throwable throwable4) {
								throwable3.addSuppressed(throwable4);
							}

							throw throwable3;
						}

						fileWriter.close();
					} catch (Exception exception2) {
						exception2.printStackTrace();
					}

					file2 = ZomboidFileSystem.instance.getFileInCurrentSave("climate", string + "_PATTERNS.txt");
					try {
						fileWriter = new FileWriter(file2, false);
						try {
							this.writer = fileWriter;
							this.writePatterns();
							this.writer = null;
						} catch (Throwable throwable5) {
							try {
								fileWriter.close();
							} catch (Throwable throwable6) {
								throwable5.addSuppressed(throwable6);
							}

							throw throwable5;
						}

						fileWriter.close();
					} catch (Exception exception3) {
						exception3.printStackTrace();
					}
				}
			} catch (Exception exception4) {
				exception4.printStackTrace();
			}
		}
	}

	private double round(double double1) {
		return (double)Math.round(double1 * 100.0) / 100.0;
	}

	private void writeRunInfo(ClimMngrDebug.RunInfo runInfo, int int1) throws Exception {
		this.write("===================================================================");
		this.write(" RUN NR: " + int1);
		this.write("===================================================================");
		this.write("");
		this.write("Total weather periods: " + runInfo.totalPeriods);
		String string = this.formatDuration(runInfo.longestPeriod);
		this.write("Longest weather: " + string);
		string = this.formatDuration(runInfo.shortestPeriod);
		this.write("Shortest weather: " + string);
		string = this.formatDuration(runInfo.averagePeriod);
		this.write("Average weather: " + string);
		this.write("");
		string = this.formatDuration(runInfo.totalPeriodDuration);
		this.write("Total weather days for cycle: " + string);
		this.write("");
		this.write("Total clear periods: " + runInfo.totalEmpty);
		string = this.formatDuration(runInfo.longestEmpty);
		this.write("Longest clear: " + string);
		string = this.formatDuration(runInfo.shortestEmpty);
		this.write("Shortest clear: " + string);
		string = this.formatDuration(runInfo.averageEmpty);
		this.write("Average clear: " + string);
		this.write("");
		this.write("Highest Front strength: " + runInfo.highestStrength);
		this.write("Lowest Front strength: " + runInfo.lowestStrength);
		this.write("Average Front strength: " + runInfo.averageStrength);
		this.write("");
		this.write("Highest WarmFront strength: " + runInfo.highestWarmStrength);
		this.write("Lowest WarmFront strength: " + runInfo.lowestWarmStrength);
		this.write("Average WarmFront strength: " + runInfo.averageWarmStrength);
		this.write("");
		this.write("Highest ColdFront strength: " + runInfo.highestColdStrength);
		this.write("Lowest ColdFront strength: " + runInfo.lowestColdStrength);
		this.write("Average ColdFront strength: " + runInfo.averageColdStrength);
		this.write("");
		this.write("Weather period types:");
		this.write("Normal warm: " + runInfo.countNormalWarm);
		this.write("Normal cold: " + runInfo.countNormalCold);
		this.write("Normal storm: " + runInfo.countStorm);
		this.write("Normal tropical: " + runInfo.countTropical);
		this.write("Normal blizzard: " + runInfo.countBlizzard);
		this.write("");
		this.write("Distribution duration in days (total periods)");
		this.printCountTable(this.writer, runInfo.dayCountPeriod);
		this.write("");
		this.write("Distribution duration in days (WARM periods)");
		this.printCountTable(this.writer, runInfo.dayCountWarmPeriod);
		this.write("");
		this.write("Distribution duration in days (COLD periods)");
		this.printCountTable(this.writer, runInfo.dayCountColdPeriod);
		this.write("");
		this.write("Distribution duration in days (clear periods)");
		this.printCountTable(this.writer, runInfo.dayCountEmpty);
		this.write("");
		this.write("Amount of weather periods exceeding threshold: " + runInfo.exceedingPeriods.size());
		Iterator iterator;
		Integer integer;
		if (runInfo.exceedingPeriods.size() > 0) {
			iterator = runInfo.exceedingPeriods.iterator();
			while (iterator.hasNext()) {
				integer = (Integer)iterator.next();
				this.write(integer + " days.");
			}
		}

		this.write("");
		this.write("Amount of clear periods exceeding threshold: " + runInfo.exceedingEmpties.size());
		if (runInfo.exceedingEmpties.size() > 0) {
			iterator = runInfo.exceedingEmpties.iterator();
			while (iterator.hasNext()) {
				integer = (Integer)iterator.next();
				this.write(integer + " days.");
			}
		}
	}

	private void write(String string) throws Exception {
		this.writer.write(string + System.lineSeparator());
	}

	private void writeDataExtremes() throws Exception {
		int int1 = 0;
		int int2 = -1;
		int int3 = -1;
		ClimMngrDebug.RunInfo runInfo = null;
		ClimMngrDebug.RunInfo runInfo2 = null;
		Iterator iterator = this.runs.iterator();
		while (true) {
			ClimMngrDebug.RunInfo runInfo3;
			do {
				if (!iterator.hasNext()) {
					this.write("");
					this.write("MOST DRY RUN:");
					if (runInfo != null) {
						this.writeRunInfo(runInfo, int2);
					}

					this.write("");
					this.write("MOST WET RUN:");
					if (runInfo2 != null) {
						this.writeRunInfo(runInfo2, int3);
					}

					return;
				}

				runInfo3 = (ClimMngrDebug.RunInfo)iterator.next();
				++int1;
				if (runInfo == null || runInfo3.totalPeriodDuration < runInfo.totalPeriodDuration) {
					runInfo = runInfo3;
					int2 = int1;
				}
			}	 while (runInfo2 != null && !(runInfo3.totalPeriodDuration > runInfo2.totalPeriodDuration));

			runInfo2 = runInfo3;
			int3 = int1;
		}
	}

	private void writeData() throws Exception {
		int int1 = 0;
		Iterator iterator = this.runs.iterator();
		while (iterator.hasNext()) {
			ClimMngrDebug.RunInfo runInfo = (ClimMngrDebug.RunInfo)iterator.next();
			++int1;
			this.writeRunInfo(runInfo, int1);
		}
	}

	private void writePatterns() throws Exception {
		String string = "-";
		String string2 = "#";
		String string3 = "S";
		String string4 = "T";
		String string5 = "B";
		boolean boolean1 = false;
		boolean boolean2 = false;
		Iterator iterator = this.runs.iterator();
		while (iterator.hasNext()) {
			ClimMngrDebug.RunInfo runInfo = (ClimMngrDebug.RunInfo)iterator.next();
			int int1 = 0;
			for (Iterator iterator2 = runInfo.records.iterator(); iterator2.hasNext(); ++int1) {
				ClimMngrDebug.RecordInfo recordInfo = (ClimMngrDebug.RecordInfo)iterator2.next();
				int int2 = (int)Math.ceil(recordInfo.durationHours / 24.0);
				String string6;
				if (recordInfo.isWeather && recordInfo.weatherType == 1) {
					string6 = (new String(new char[int2])).replace("\u0000", string3);
				} else if (recordInfo.isWeather && recordInfo.weatherType == 2) {
					string6 = (new String(new char[int2])).replace("\u0000", string4);
				} else if (recordInfo.isWeather && recordInfo.weatherType == 3) {
					string6 = (new String(new char[int2])).replace("\u0000", string5);
				} else if (int1 == 0 && !recordInfo.isWeather && int2 >= 2) {
					string6 = (new String(new char[int2 - 1])).replace("\u0000", string);
				} else {
					string6 = (new String(new char[int2])).replace("\u0000", recordInfo.isWeather ? string2 : string);
				}

				this.writer.write(string6);
			}

			this.writer.write(System.lineSeparator());
		}
	}

	private void printCountTable(FileWriter fileWriter, int[] intArray) throws Exception {
		this.printCountTable(fileWriter, intArray, 1);
	}

	private void printCountTable(FileWriter fileWriter, int[] intArray, int int1) throws Exception {
		if (intArray != null && intArray.length > 0) {
			int int2 = 0;
			for (int int3 = 0; int3 < intArray.length; ++int3) {
				if (intArray[int3] > int2) {
					int2 = intArray[int3];
				}
			}

			this.write("	DAYS   COUNT GRAPH");
			float float1 = 50.0F / (float)int2;
			if (int2 > 0) {
				for (int int4 = 0; int4 < intArray.length; ++int4) {
					String string = "";
					string = string + String.format("%1$8s", int4 * int1 + "-" + (int4 * int1 + int1));
					int int5 = intArray[int4];
					string = string + String.format("%1$8s", int5);
					string = string + " ";
					int int6 = (int)((float)int5 * float1);
					if (int6 > 0) {
						string = string + (new String(new char[int6])).replace("\u0000", "#");
					} else if (int5 > 0) {
						string = string + "*";
					}

					this.write(string);
				}
			}
		}
	}

	private String formatDuration(double double1) {
		int int1 = (int)(double1 / 24.0);
		int int2 = (int)(double1 - (double)(int1 * 24));
		return int1 + " days, " + int2 + " hours.";
	}

	private class RunInfo {
		public double seedA;
		public int durationDays;
		public double durationHours;
		public ArrayList records = new ArrayList();
		public double totalPeriodDuration = 0.0;
		public double longestPeriod = 0.0;
		public double shortestPeriod = 9.99999999E8;
		public int totalPeriods = 0;
		public double averagePeriod = 0.0;
		public double longestEmpty = 0.0;
		public double shortestEmpty = 9.99999999E8;
		public int totalEmpty = 0;
		public double averageEmpty = 0.0;
		public float highestStrength = 0.0F;
		public float lowestStrength = 1.0F;
		public float averageStrength = 0.0F;
		public float highestWarmStrength = 0.0F;
		public float lowestWarmStrength = 1.0F;
		public float averageWarmStrength = 0.0F;
		public float highestColdStrength = 0.0F;
		public float lowestColdStrength = 1.0F;
		public float averageColdStrength = 0.0F;
		public int countNormalWarm = 0;
		public int countNormalCold = 0;
		public int countStorm = 0;
		public int countTropical = 0;
		public int countBlizzard = 0;
		public int[] dayCountPeriod = new int[16];
		public int[] dayCountWarmPeriod = new int[16];
		public int[] dayCountColdPeriod = new int[16];
		public int[] dayCountEmpty = new int[75];
		public ArrayList exceedingPeriods = new ArrayList();
		public ArrayList exceedingEmpties = new ArrayList();
		public double mostWetPeriod = 0.0;
		public double mostDryPeriod = 9.99999999E8;
		public int[] totalDaysPeriod;

		public ClimMngrDebug.RecordInfo addRecord(double double1) {
			ClimMngrDebug.RecordInfo recordInfo = ClimMngrDebug.this.new RecordInfo();
			recordInfo.durationHours = double1;
			recordInfo.isWeather = false;
			this.records.add(recordInfo);
			return recordInfo;
		}

		public ClimMngrDebug.RecordInfo addRecord(int int1, double double1, float float1, boolean boolean1, boolean boolean2, boolean boolean3) {
			ClimMngrDebug.RecordInfo recordInfo = ClimMngrDebug.this.new RecordInfo();
			recordInfo.durationHours = double1;
			recordInfo.isWeather = true;
			recordInfo.airType = int1;
			recordInfo.strength = float1;
			recordInfo.weatherType = 0;
			if (boolean1) {
				recordInfo.weatherType = 1;
			} else if (boolean2) {
				recordInfo.weatherType = 2;
			} else if (boolean3) {
				recordInfo.weatherType = 3;
			}

			this.records.add(recordInfo);
			return recordInfo;
		}

		public void calculate() {
			double double1 = 0.0;
			double double2 = 0.0;
			float float1 = 0.0F;
			float float2 = 0.0F;
			float float3 = 0.0F;
			int int1 = 0;
			int int2 = 0;
			Iterator iterator = this.records.iterator();
			while (iterator.hasNext()) {
				ClimMngrDebug.RecordInfo recordInfo = (ClimMngrDebug.RecordInfo)iterator.next();
				int int3 = (int)(recordInfo.durationHours / 24.0);
				int int4;
				if (recordInfo.isWeather) {
					this.totalPeriodDuration += recordInfo.durationHours;
					if (recordInfo.durationHours > this.longestPeriod) {
						this.longestPeriod = recordInfo.durationHours;
					}

					if (recordInfo.durationHours < this.shortestPeriod) {
						this.shortestPeriod = recordInfo.durationHours;
					}

					++this.totalPeriods;
					double1 += recordInfo.durationHours;
					if (recordInfo.strength > this.highestStrength) {
						this.highestStrength = recordInfo.strength;
					}

					if (recordInfo.strength < this.lowestStrength) {
						this.lowestStrength = recordInfo.strength;
					}

					float1 += recordInfo.strength;
					if (recordInfo.airType == 1) {
						++int1;
						if (recordInfo.strength > this.highestWarmStrength) {
							this.highestWarmStrength = recordInfo.strength;
						}

						if (recordInfo.strength < this.lowestWarmStrength) {
							this.lowestWarmStrength = recordInfo.strength;
						}

						float2 += recordInfo.strength;
						if (recordInfo.weatherType == 1) {
							++this.countStorm;
						} else if (recordInfo.weatherType == 2) {
							++this.countTropical;
						} else if (recordInfo.weatherType == 3) {
							++this.countBlizzard;
						} else {
							++this.countNormalWarm;
						}

						if (int3 < this.dayCountWarmPeriod.length) {
							int4 = this.dayCountWarmPeriod[int3]++;
						}
					} else {
						++int2;
						if (recordInfo.strength > this.highestColdStrength) {
							this.highestColdStrength = recordInfo.strength;
						}

						if (recordInfo.strength < this.lowestColdStrength) {
							this.lowestColdStrength = recordInfo.strength;
						}

						float3 += recordInfo.strength;
						++this.countNormalCold;
						if (int3 < this.dayCountColdPeriod.length) {
							int4 = this.dayCountColdPeriod[int3]++;
						}
					}

					if (int3 < this.dayCountPeriod.length) {
						int4 = this.dayCountPeriod[int3]++;
					} else {
						DebugLog.log("Period is longer than allowed array, days = " + int3);
						this.exceedingPeriods.add(int3);
					}
				} else {
					if (recordInfo.durationHours > this.longestEmpty) {
						this.longestEmpty = recordInfo.durationHours;
					}

					if (recordInfo.durationHours < this.shortestEmpty) {
						this.shortestEmpty = recordInfo.durationHours;
					}

					++this.totalEmpty;
					double2 += recordInfo.durationHours;
					if (int3 < this.dayCountEmpty.length) {
						int4 = this.dayCountEmpty[int3]++;
					} else {
						DebugLog.log("No-Weather period is longer than allowed array, days = " + int3);
						this.exceedingEmpties.add(int3);
					}
				}
			}

			if (this.totalPeriods > 0) {
				this.averagePeriod = double1 / (double)this.totalPeriods;
				this.averageStrength = float1 / (float)this.totalPeriods;
				if (int1 > 0) {
					this.averageWarmStrength = float2 / (float)int1;
				}

				if (int2 > 0) {
					this.averageColdStrength = float3 / (float)int2;
				}
			}

			if (this.totalEmpty > 0) {
				this.averageEmpty = double2 / (double)this.totalEmpty;
			}
		}
	}

	private class RecordInfo {
		public boolean isWeather;
		public float strength;
		public int airType;
		public double durationHours;
		public int weatherType = 0;
	}
}
