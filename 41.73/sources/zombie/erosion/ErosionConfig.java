package zombie.erosion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import zombie.core.Core;
import zombie.debug.DebugLog;


public final class ErosionConfig {
	public final ErosionConfig.Seeds seeds = new ErosionConfig.Seeds();
	public final ErosionConfig.Time time = new ErosionConfig.Time();
	public final ErosionConfig.Debug debug = new ErosionConfig.Debug();
	public final ErosionConfig.Season season = new ErosionConfig.Season();

	public void save(ByteBuffer byteBuffer) {
		byteBuffer.putInt(this.seeds.seedMain_0);
		byteBuffer.putInt(this.seeds.seedMain_1);
		byteBuffer.putInt(this.seeds.seedMain_2);
		byteBuffer.putInt(this.seeds.seedMoisture_0);
		byteBuffer.putInt(this.seeds.seedMoisture_1);
		byteBuffer.putInt(this.seeds.seedMoisture_2);
		byteBuffer.putInt(this.seeds.seedMinerals_0);
		byteBuffer.putInt(this.seeds.seedMinerals_1);
		byteBuffer.putInt(this.seeds.seedMinerals_2);
		byteBuffer.putInt(this.seeds.seedKudzu_0);
		byteBuffer.putInt(this.seeds.seedKudzu_1);
		byteBuffer.putInt(this.seeds.seedKudzu_2);
		byteBuffer.putInt(this.time.tickunit);
		byteBuffer.putInt(this.time.ticks);
		byteBuffer.putInt(this.time.eticks);
		byteBuffer.putInt(this.time.epoch);
		byteBuffer.putInt(this.season.lat);
		byteBuffer.putInt(this.season.tempMax);
		byteBuffer.putInt(this.season.tempMin);
		byteBuffer.putInt(this.season.tempDiff);
		byteBuffer.putInt(this.season.seasonLag);
		byteBuffer.putFloat(this.season.noon);
		byteBuffer.putInt(this.season.seedA);
		byteBuffer.putInt(this.season.seedB);
		byteBuffer.putInt(this.season.seedC);
		byteBuffer.putFloat(this.season.jan);
		byteBuffer.putFloat(this.season.feb);
		byteBuffer.putFloat(this.season.mar);
		byteBuffer.putFloat(this.season.apr);
		byteBuffer.putFloat(this.season.may);
		byteBuffer.putFloat(this.season.jun);
		byteBuffer.putFloat(this.season.jul);
		byteBuffer.putFloat(this.season.aug);
		byteBuffer.putFloat(this.season.sep);
		byteBuffer.putFloat(this.season.oct);
		byteBuffer.putFloat(this.season.nov);
		byteBuffer.putFloat(this.season.dec);
	}

	public void load(ByteBuffer byteBuffer) {
		this.seeds.seedMain_0 = byteBuffer.getInt();
		this.seeds.seedMain_1 = byteBuffer.getInt();
		this.seeds.seedMain_2 = byteBuffer.getInt();
		this.seeds.seedMoisture_0 = byteBuffer.getInt();
		this.seeds.seedMoisture_1 = byteBuffer.getInt();
		this.seeds.seedMoisture_2 = byteBuffer.getInt();
		this.seeds.seedMinerals_0 = byteBuffer.getInt();
		this.seeds.seedMinerals_1 = byteBuffer.getInt();
		this.seeds.seedMinerals_2 = byteBuffer.getInt();
		this.seeds.seedKudzu_0 = byteBuffer.getInt();
		this.seeds.seedKudzu_1 = byteBuffer.getInt();
		this.seeds.seedKudzu_2 = byteBuffer.getInt();
		this.time.tickunit = byteBuffer.getInt();
		this.time.ticks = byteBuffer.getInt();
		this.time.eticks = byteBuffer.getInt();
		this.time.epoch = byteBuffer.getInt();
		this.season.lat = byteBuffer.getInt();
		this.season.tempMax = byteBuffer.getInt();
		this.season.tempMin = byteBuffer.getInt();
		this.season.tempDiff = byteBuffer.getInt();
		this.season.seasonLag = byteBuffer.getInt();
		this.season.noon = byteBuffer.getFloat();
		this.season.seedA = byteBuffer.getInt();
		this.season.seedB = byteBuffer.getInt();
		this.season.seedC = byteBuffer.getInt();
		this.season.jan = byteBuffer.getFloat();
		this.season.feb = byteBuffer.getFloat();
		this.season.mar = byteBuffer.getFloat();
		this.season.apr = byteBuffer.getFloat();
		this.season.may = byteBuffer.getFloat();
		this.season.jun = byteBuffer.getFloat();
		this.season.jul = byteBuffer.getFloat();
		this.season.aug = byteBuffer.getFloat();
		this.season.sep = byteBuffer.getFloat();
		this.season.oct = byteBuffer.getFloat();
		this.season.nov = byteBuffer.getFloat();
		this.season.dec = byteBuffer.getFloat();
	}

	public void writeFile(String string) {
		try {
			if (Core.getInstance().isNoSave()) {
				return;
			}

			File file = new File(string);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fileWriter = new FileWriter(file, false);
			fileWriter.write("seeds.seedMain_0 = " + this.seeds.seedMain_0 + "\n");
			fileWriter.write("seeds.seedMain_1 = " + this.seeds.seedMain_1 + "\n");
			fileWriter.write("seeds.seedMain_2 = " + this.seeds.seedMain_2 + "\n");
			fileWriter.write("seeds.seedMoisture_0 = " + this.seeds.seedMoisture_0 + "\n");
			fileWriter.write("seeds.seedMoisture_1 = " + this.seeds.seedMoisture_1 + "\n");
			fileWriter.write("seeds.seedMoisture_2 = " + this.seeds.seedMoisture_2 + "\n");
			fileWriter.write("seeds.seedMinerals_0 = " + this.seeds.seedMinerals_0 + "\n");
			fileWriter.write("seeds.seedMinerals_1 = " + this.seeds.seedMinerals_1 + "\n");
			fileWriter.write("seeds.seedMinerals_2 = " + this.seeds.seedMinerals_2 + "\n");
			fileWriter.write("seeds.seedKudzu_0 = " + this.seeds.seedKudzu_0 + "\n");
			fileWriter.write("seeds.seedKudzu_1 = " + this.seeds.seedKudzu_1 + "\n");
			fileWriter.write("seeds.seedKudzu_2 = " + this.seeds.seedKudzu_2 + "\n");
			fileWriter.write("\n");
			fileWriter.write("time.tickunit = " + this.time.tickunit + "\n");
			fileWriter.write("time.ticks = " + this.time.ticks + "\n");
			fileWriter.write("time.eticks = " + this.time.eticks + "\n");
			fileWriter.write("time.epoch = " + this.time.epoch + "\n");
			fileWriter.write("\n");
			fileWriter.write("season.lat = " + this.season.lat + "\n");
			fileWriter.write("season.tempMax = " + this.season.tempMax + "\n");
			fileWriter.write("season.tempMin = " + this.season.tempMin + "\n");
			fileWriter.write("season.tempDiff = " + this.season.tempDiff + "\n");
			fileWriter.write("season.seasonLag = " + this.season.seasonLag + "\n");
			fileWriter.write("season.noon = " + this.season.noon + "\n");
			fileWriter.write("season.seedA = " + this.season.seedA + "\n");
			fileWriter.write("season.seedB = " + this.season.seedB + "\n");
			fileWriter.write("season.seedC = " + this.season.seedC + "\n");
			fileWriter.write("season.jan = " + this.season.jan + "\n");
			fileWriter.write("season.feb = " + this.season.feb + "\n");
			fileWriter.write("season.mar = " + this.season.mar + "\n");
			fileWriter.write("season.apr = " + this.season.apr + "\n");
			fileWriter.write("season.may = " + this.season.may + "\n");
			fileWriter.write("season.jun = " + this.season.jun + "\n");
			fileWriter.write("season.jul = " + this.season.jul + "\n");
			fileWriter.write("season.aug = " + this.season.aug + "\n");
			fileWriter.write("season.sep = " + this.season.sep + "\n");
			fileWriter.write("season.oct = " + this.season.oct + "\n");
			fileWriter.write("season.nov = " + this.season.nov + "\n");
			fileWriter.write("season.dec = " + this.season.dec + "\n");
			fileWriter.write("\n");
			fileWriter.write("debug.enabled = " + this.debug.enabled + "\n");
			fileWriter.write("debug.startday = " + this.debug.startday + "\n");
			fileWriter.write("debug.startmonth = " + this.debug.startmonth + "\n");
			fileWriter.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public boolean readFile(String string) {
		try {
			File file = new File(string);
			if (!file.exists()) {
				return false;
			} else {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				while (true) {
					String string2 = bufferedReader.readLine();
					if (string2 == null) {
						bufferedReader.close();
						return true;
					}

					if (!string2.trim().startsWith("--")) {
						if (!string2.contains("=")) {
							if (!string2.trim().isEmpty()) {
								DebugLog.log("ErosionConfig: unknown \"" + string2 + "\"");
							}
						} else {
							String[] stringArray = string2.split("=");
							if (stringArray.length != 2) {
								DebugLog.log("ErosionConfig: unknown \"" + string2 + "\"");
							} else {
								String string3 = stringArray[0].trim();
								String string4 = stringArray[1].trim();
								if (string3.startsWith("seeds.")) {
									if ("seeds.seedMain_0".equals(string3)) {
										this.seeds.seedMain_0 = Integer.parseInt(string4);
									} else if ("seeds.seedMain_1".equals(string3)) {
										this.seeds.seedMain_1 = Integer.parseInt(string4);
									} else if ("seeds.seedMain_2".equals(string3)) {
										this.seeds.seedMain_2 = Integer.parseInt(string4);
									} else if ("seeds.seedMoisture_0".equals(string3)) {
										this.seeds.seedMoisture_0 = Integer.parseInt(string4);
									} else if ("seeds.seedMoisture_1".equals(string3)) {
										this.seeds.seedMoisture_1 = Integer.parseInt(string4);
									} else if ("seeds.seedMoisture_2".equals(string3)) {
										this.seeds.seedMoisture_2 = Integer.parseInt(string4);
									} else if ("seeds.seedMinerals_0".equals(string3)) {
										this.seeds.seedMinerals_0 = Integer.parseInt(string4);
									} else if ("seeds.seedMinerals_1".equals(string3)) {
										this.seeds.seedMinerals_1 = Integer.parseInt(string4);
									} else if ("seeds.seedMinerals_2".equals(string3)) {
										this.seeds.seedMinerals_2 = Integer.parseInt(string4);
									} else if ("seeds.seedKudzu_0".equals(string3)) {
										this.seeds.seedKudzu_0 = Integer.parseInt(string4);
									} else if ("seeds.seedKudzu_1".equals(string3)) {
										this.seeds.seedKudzu_1 = Integer.parseInt(string4);
									} else if ("seeds.seedKudzu_2".equals(string3)) {
										this.seeds.seedKudzu_2 = Integer.parseInt(string4);
									} else {
										DebugLog.log("ErosionConfig: unknown \"" + string2 + "\"");
									}
								} else if (string3.startsWith("time.")) {
									if ("time.tickunit".equals(string3)) {
										this.time.tickunit = Integer.parseInt(string4);
									} else if ("time.ticks".equals(string3)) {
										this.time.ticks = Integer.parseInt(string4);
									} else if ("time.eticks".equals(string3)) {
										this.time.eticks = Integer.parseInt(string4);
									} else if ("time.epoch".equals(string3)) {
										this.time.epoch = Integer.parseInt(string4);
									} else {
										DebugLog.log("ErosionConfig: unknown \"" + string2 + "\"");
									}
								} else if (string3.startsWith("season.")) {
									if ("season.lat".equals(string3)) {
										this.season.lat = Integer.parseInt(string4);
									} else if ("season.tempMax".equals(string3)) {
										this.season.tempMax = Integer.parseInt(string4);
									} else if ("season.tempMin".equals(string3)) {
										this.season.tempMin = Integer.parseInt(string4);
									} else if ("season.tempDiff".equals(string3)) {
										this.season.tempDiff = Integer.parseInt(string4);
									} else if ("season.seasonLag".equals(string3)) {
										this.season.seasonLag = Integer.parseInt(string4);
									} else if ("season.noon".equals(string3)) {
										this.season.noon = Float.parseFloat(string4);
									} else if ("season.seedA".equals(string3)) {
										this.season.seedA = Integer.parseInt(string4);
									} else if ("season.seedB".equals(string3)) {
										this.season.seedB = Integer.parseInt(string4);
									} else if ("season.seedC".equals(string3)) {
										this.season.seedC = Integer.parseInt(string4);
									} else if ("season.jan".equals(string3)) {
										this.season.jan = Float.parseFloat(string4);
									} else if ("season.feb".equals(string3)) {
										this.season.feb = Float.parseFloat(string4);
									} else if ("season.mar".equals(string3)) {
										this.season.mar = Float.parseFloat(string4);
									} else if ("season.apr".equals(string3)) {
										this.season.apr = Float.parseFloat(string4);
									} else if ("season.may".equals(string3)) {
										this.season.may = Float.parseFloat(string4);
									} else if ("season.jun".equals(string3)) {
										this.season.jun = Float.parseFloat(string4);
									} else if ("season.jul".equals(string3)) {
										this.season.jul = Float.parseFloat(string4);
									} else if ("season.aug".equals(string3)) {
										this.season.aug = Float.parseFloat(string4);
									} else if ("season.sep".equals(string3)) {
										this.season.sep = Float.parseFloat(string4);
									} else if ("season.oct".equals(string3)) {
										this.season.oct = Float.parseFloat(string4);
									} else if ("season.nov".equals(string3)) {
										this.season.nov = Float.parseFloat(string4);
									} else if ("season.dec".equals(string3)) {
										this.season.dec = Float.parseFloat(string4);
									} else {
										DebugLog.log("ErosionConfig: unknown \"" + string2 + "\"");
									}
								} else if (string3.startsWith("debug.")) {
									if ("debug.enabled".equals(string3)) {
										this.debug.enabled = Boolean.parseBoolean(string4);
									} else if ("debug.startday".equals(string3)) {
										this.debug.startday = Integer.parseInt(string4);
									} else if ("debug.startmonth".equals(string3)) {
										this.debug.startmonth = Integer.parseInt(string4);
									}
								} else {
									DebugLog.log("ErosionConfig: unknown \"" + string2 + "\"");
								}
							}
						}
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	public ErosionConfig.Debug getDebug() {
		return this.debug;
	}

	public void consolePrint() {
	}

	public static final class Seeds {
		int seedMain_0 = 16;
		int seedMain_1 = 32;
		int seedMain_2 = 64;
		int seedMoisture_0 = 96;
		int seedMoisture_1 = 128;
		int seedMoisture_2 = 144;
		int seedMinerals_0 = 196;
		int seedMinerals_1 = 255;
		int seedMinerals_2 = 0;
		int seedKudzu_0 = 200;
		int seedKudzu_1 = 125;
		int seedKudzu_2 = 50;
	}

	public static final class Time {
		int tickunit = 144;
		int ticks = 0;
		int eticks = 0;
		int epoch = 0;
	}

	public static final class Debug {
		boolean enabled = false;
		int startday = 26;
		int startmonth = 11;

		public boolean getEnabled() {
			return this.enabled;
		}

		public int getStartDay() {
			return this.startday;
		}

		public int getStartMonth() {
			return this.startmonth;
		}
	}

	public static final class Season {
		int lat = 38;
		int tempMax = 25;
		int tempMin = 0;
		int tempDiff = 7;
		int seasonLag = 31;
		float noon = 12.5F;
		int seedA = 64;
		int seedB = 128;
		int seedC = 255;
		float jan = 0.39F;
		float feb = 0.35F;
		float mar = 0.39F;
		float apr = 0.4F;
		float may = 0.35F;
		float jun = 0.37F;
		float jul = 0.29F;
		float aug = 0.26F;
		float sep = 0.23F;
		float oct = 0.23F;
		float nov = 0.3F;
		float dec = 0.32F;
	}
}
