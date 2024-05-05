package zombie.iso.weather;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import zombie.ZomboidFileSystem;
import zombie.core.Color;
import zombie.debug.DebugLog;


public class ClimateColorInfo {
	private Color interior;
	private Color exterior;
	private static BufferedWriter writer;

	public ClimateColorInfo() {
		this.interior = new Color(0, 0, 0, 1);
		this.exterior = new Color(0, 0, 0, 1);
	}

	public ClimateColorInfo(float float1, float float2, float float3, float float4) {
		this(float1, float2, float3, float4, float1, float2, float3, float4);
	}

	public ClimateColorInfo(float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
		this.interior = new Color(0, 0, 0, 1);
		this.exterior = new Color(0, 0, 0, 1);
		this.interior.r = float1;
		this.interior.g = float2;
		this.interior.b = float3;
		this.interior.a = float4;
		this.exterior.r = float5;
		this.exterior.g = float6;
		this.exterior.b = float7;
		this.exterior.a = float8;
	}

	public void setInterior(Color color) {
		this.interior.set(color);
	}

	public void setInterior(float float1, float float2, float float3, float float4) {
		this.interior.r = float1;
		this.interior.g = float2;
		this.interior.b = float3;
		this.interior.a = float4;
	}

	public Color getInterior() {
		return this.interior;
	}

	public void setExterior(Color color) {
		this.exterior.set(color);
	}

	public void setExterior(float float1, float float2, float float3, float float4) {
		this.exterior.r = float1;
		this.exterior.g = float2;
		this.exterior.b = float3;
		this.exterior.a = float4;
	}

	public Color getExterior() {
		return this.exterior;
	}

	public void setTo(ClimateColorInfo climateColorInfo) {
		this.interior.set(climateColorInfo.interior);
		this.exterior.set(climateColorInfo.exterior);
	}

	public ClimateColorInfo interp(ClimateColorInfo climateColorInfo, float float1, ClimateColorInfo climateColorInfo2) {
		this.interior.interp(climateColorInfo.interior, float1, climateColorInfo2.interior);
		this.exterior.interp(climateColorInfo.exterior, float1, climateColorInfo2.exterior);
		return climateColorInfo2;
	}

	public void scale(float float1) {
		this.interior.scale(float1);
		this.exterior.scale(float1);
	}

	public static ClimateColorInfo interp(ClimateColorInfo climateColorInfo, ClimateColorInfo climateColorInfo2, float float1, ClimateColorInfo climateColorInfo3) {
		return climateColorInfo.interp(climateColorInfo2, float1, climateColorInfo3);
	}

	public void write(ByteBuffer byteBuffer) {
		byteBuffer.putFloat(this.interior.r);
		byteBuffer.putFloat(this.interior.g);
		byteBuffer.putFloat(this.interior.b);
		byteBuffer.putFloat(this.interior.a);
		byteBuffer.putFloat(this.exterior.r);
		byteBuffer.putFloat(this.exterior.g);
		byteBuffer.putFloat(this.exterior.b);
		byteBuffer.putFloat(this.exterior.a);
	}

	public void read(ByteBuffer byteBuffer) {
		this.interior.r = byteBuffer.getFloat();
		this.interior.g = byteBuffer.getFloat();
		this.interior.b = byteBuffer.getFloat();
		this.interior.a = byteBuffer.getFloat();
		this.exterior.r = byteBuffer.getFloat();
		this.exterior.g = byteBuffer.getFloat();
		this.exterior.b = byteBuffer.getFloat();
		this.exterior.a = byteBuffer.getFloat();
	}

	public void save(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeFloat(this.interior.r);
		dataOutputStream.writeFloat(this.interior.g);
		dataOutputStream.writeFloat(this.interior.b);
		dataOutputStream.writeFloat(this.interior.a);
		dataOutputStream.writeFloat(this.exterior.r);
		dataOutputStream.writeFloat(this.exterior.g);
		dataOutputStream.writeFloat(this.exterior.b);
		dataOutputStream.writeFloat(this.exterior.a);
	}

	public void load(DataInputStream dataInputStream, int int1) throws IOException {
		this.interior.r = dataInputStream.readFloat();
		this.interior.g = dataInputStream.readFloat();
		this.interior.b = dataInputStream.readFloat();
		this.interior.a = dataInputStream.readFloat();
		this.exterior.r = dataInputStream.readFloat();
		this.exterior.g = dataInputStream.readFloat();
		this.exterior.b = dataInputStream.readFloat();
		this.exterior.a = dataInputStream.readFloat();
	}

	public static boolean writeColorInfoConfig() {
		boolean boolean1 = false;
		try {
			String string = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
			String string2 = "ClimateMain_" + string;
			String string3 = ZomboidFileSystem.instance.getCacheDir();
			String string4 = string3 + File.separator + string2 + ".lua";
			DebugLog.log("Attempting to save color config to: " + string4);
			File file = new File(string4);
			try {
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false));
				try {
					writer = bufferedWriter;
					ClimateManager climateManager = ClimateManager.getInstance();
					write("--[[");
					write("-- Generated file (" + string2 + ")");
					write("-- Climate color configuration");
					write("-- File should be placed in: media/lua/server/Climate/ClimateMain.lua (remove date stamp)");
					write("--]]");
					bufferedWriter.newLine();
					write("ClimateMain = {};");
					write("ClimateMain.versionStamp = \"" + string + "\";");
					bufferedWriter.newLine();
					write("local WARM,NORMAL,CLOUDY = 0,1,2;");
					bufferedWriter.newLine();
					write("local SUMMER,FALL,WINTER,SPRING = 0,1,2,3;");
					bufferedWriter.newLine();
					write("function ClimateMain.onClimateManagerInit(_clim)");
					byte byte1 = 1;
					write(byte1, "local c;");
					write(byte1, "c = _clim:getColNightNoMoon();");
					writeColor(byte1, climateManager.getColNightNoMoon());
					bufferedWriter.newLine();
					write(byte1, "c = _clim:getColNightMoon();");
					writeColor(byte1, climateManager.getColNightMoon());
					bufferedWriter.newLine();
					write(byte1, "c = _clim:getColFog();");
					writeColor(byte1, climateManager.getColFog());
					bufferedWriter.newLine();
					write(byte1, "c = _clim:getColFogLegacy();");
					writeColor(byte1, climateManager.getColFogLegacy());
					bufferedWriter.newLine();
					write(byte1, "c = _clim:getColFogNew();");
					writeColor(byte1, climateManager.getColFogNew());
					bufferedWriter.newLine();
					write(byte1, "c = _clim:getFogTintStorm();");
					writeColor(byte1, climateManager.getFogTintStorm());
					bufferedWriter.newLine();
					write(byte1, "c = _clim:getFogTintTropical();");
					writeColor(byte1, climateManager.getFogTintTropical());
					bufferedWriter.newLine();
					WeatherPeriod weatherPeriod = climateManager.getWeatherPeriod();
					write(byte1, "local w = _clim:getWeatherPeriod();");
					bufferedWriter.newLine();
					write(byte1, "c = w:getCloudColorReddish();");
					writeColor(byte1, weatherPeriod.getCloudColorReddish());
					bufferedWriter.newLine();
					write(byte1, "c = w:getCloudColorGreenish();");
					writeColor(byte1, weatherPeriod.getCloudColorGreenish());
					bufferedWriter.newLine();
					write(byte1, "c = w:getCloudColorBlueish();");
					writeColor(byte1, weatherPeriod.getCloudColorBlueish());
					bufferedWriter.newLine();
					write(byte1, "c = w:getCloudColorPurplish();");
					writeColor(byte1, weatherPeriod.getCloudColorPurplish());
					bufferedWriter.newLine();
					write(byte1, "c = w:getCloudColorTropical();");
					writeColor(byte1, weatherPeriod.getCloudColorTropical());
					bufferedWriter.newLine();
					write(byte1, "c = w:getCloudColorBlizzard();");
					writeColor(byte1, weatherPeriod.getCloudColorBlizzard());
					bufferedWriter.newLine();
					String[] stringArray = new String[]{"Dawn", "Day", "Dusk"};
					String[] stringArray2 = new String[]{"SUMMER", "FALL", "WINTER", "SPRING"};
					String[] stringArray3 = new String[]{"WARM", "NORMAL", "CLOUDY"};
					for (int int1 = 0; int1 < 3; ++int1) {
						write(byte1, "-- ###################### " + stringArray[int1] + " ######################");
						for (int int2 = 0; int2 < 4; ++int2) {
							for (int int3 = 0; int3 < 3; ++int3) {
								if (int3 == 0 || int3 == 2 || int3 == 1 && int1 == 2) {
									ClimateColorInfo climateColorInfo = climateManager.getSeasonColor(int1, int3, int2);
									writeSeasonColor(byte1, climateColorInfo, stringArray[int1], stringArray2[int2], stringArray3[int3]);
									bufferedWriter.newLine();
								}
							}
						}
					}

					write("end");
					bufferedWriter.newLine();
					write("Events.OnClimateManagerInit.Add(ClimateMain.onClimateManagerInit);");
					writer = null;
					bufferedWriter.flush();
					bufferedWriter.close();
				} catch (Throwable throwable) {
					try {
						bufferedWriter.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedWriter.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}

		return boolean1;
	}

	private static void writeSeasonColor(int int1, ClimateColorInfo climateColorInfo, String string, String string2, String string3) throws IOException {
		Color color = climateColorInfo.exterior;
		write(int1, "_clim:setSeasonColor" + string + "(" + string3 + "," + string2 + "," + color.r + "," + color.g + "," + color.b + "," + color.a + ",true);\t\t--exterior");
		color = climateColorInfo.interior;
		write(int1, "_clim:setSeasonColor" + string + "(" + string3 + "," + string2 + "," + color.r + "," + color.g + "," + color.b + "," + color.a + ",false);\t\t--interior");
	}

	private static void writeColor(int int1, ClimateColorInfo climateColorInfo) throws IOException {
		Color color = climateColorInfo.exterior;
		write(int1, "c:setExterior(" + color.r + "," + color.g + "," + color.b + "," + color.a + ");");
		color = climateColorInfo.interior;
		write(int1, "c:setInterior(" + color.r + "," + color.g + "," + color.b + "," + color.a + ");");
	}

	private static void write(int int1, String string) throws IOException {
		String string2 = (new String(new char[int1])).replace("\u0000", "\t");
		writer.write(string2);
		writer.write(string);
		writer.newLine();
	}

	private static void write(String string) throws IOException {
		writer.write(string);
		writer.newLine();
	}
}
