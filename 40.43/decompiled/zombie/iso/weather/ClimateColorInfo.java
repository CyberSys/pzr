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
import zombie.GameWindow;
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

   public ClimateColorInfo(float var1, float var2, float var3, float var4) {
      this(var1, var2, var3, var4, var1, var2, var3, var4);
   }

   public ClimateColorInfo(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.interior = new Color(0, 0, 0, 1);
      this.exterior = new Color(0, 0, 0, 1);
      this.interior.r = var1;
      this.interior.g = var2;
      this.interior.b = var3;
      this.interior.a = var4;
      this.exterior.r = var5;
      this.exterior.g = var6;
      this.exterior.b = var7;
      this.exterior.a = var8;
   }

   public void setInterior(Color var1) {
      this.interior.set(var1);
   }

   public void setInterior(float var1, float var2, float var3, float var4) {
      this.interior.r = var1;
      this.interior.g = var2;
      this.interior.b = var3;
      this.interior.a = var4;
   }

   public Color getInterior() {
      return this.interior;
   }

   public void setExterior(Color var1) {
      this.exterior.set(var1);
   }

   public void setExterior(float var1, float var2, float var3, float var4) {
      this.exterior.r = var1;
      this.exterior.g = var2;
      this.exterior.b = var3;
      this.exterior.a = var4;
   }

   public Color getExterior() {
      return this.exterior;
   }

   public void setTo(ClimateColorInfo var1) {
      this.interior.set(var1.interior);
      this.exterior.set(var1.exterior);
   }

   public ClimateColorInfo interp(ClimateColorInfo var1, float var2, ClimateColorInfo var3) {
      this.interior.interp(var1.interior, var2, var3.interior);
      this.exterior.interp(var1.exterior, var2, var3.exterior);
      return var3;
   }

   public static ClimateColorInfo interp(ClimateColorInfo var0, ClimateColorInfo var1, float var2, ClimateColorInfo var3) {
      return var0.interp(var1, var2, var3);
   }

   public void write(ByteBuffer var1) {
      var1.putFloat(this.interior.r);
      var1.putFloat(this.interior.g);
      var1.putFloat(this.interior.b);
      var1.putFloat(this.interior.a);
      var1.putFloat(this.exterior.r);
      var1.putFloat(this.exterior.g);
      var1.putFloat(this.exterior.b);
      var1.putFloat(this.exterior.a);
   }

   public void read(ByteBuffer var1) {
      this.interior.r = var1.getFloat();
      this.interior.g = var1.getFloat();
      this.interior.b = var1.getFloat();
      this.interior.a = var1.getFloat();
      this.exterior.r = var1.getFloat();
      this.exterior.g = var1.getFloat();
      this.exterior.b = var1.getFloat();
      this.exterior.a = var1.getFloat();
   }

   public void save(DataOutputStream var1) throws IOException {
      var1.writeFloat(this.interior.r);
      var1.writeFloat(this.interior.g);
      var1.writeFloat(this.interior.b);
      var1.writeFloat(this.interior.a);
      var1.writeFloat(this.exterior.r);
      var1.writeFloat(this.exterior.g);
      var1.writeFloat(this.exterior.b);
      var1.writeFloat(this.exterior.a);
   }

   public void load(DataInputStream var1, int var2) throws IOException {
      this.interior.r = var1.readFloat();
      this.interior.g = var1.readFloat();
      this.interior.b = var1.readFloat();
      this.interior.a = var1.readFloat();
      this.exterior.r = var1.readFloat();
      this.exterior.g = var1.readFloat();
      this.exterior.b = var1.readFloat();
      this.exterior.a = var1.readFloat();
   }

   public static boolean writeColorInfoConfig() {
      boolean var0 = false;

      try {
         String var1 = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
         String var2 = "ClimateMain_" + var1;
         String var3 = GameWindow.getCacheDir() + File.separator + var2 + ".lua";
         DebugLog.log("Attempting to save color config to: " + var3);
         File var4 = new File(var3);

         try {
            BufferedWriter var5 = new BufferedWriter(new FileWriter(var4, false));
            Throwable var6 = null;

            try {
               writer = var5;
               ClimateManager var7 = ClimateManager.getInstance();
               write("--[[");
               write("-- Generated file (" + var2 + ")");
               write("-- Climate color configuration");
               write("-- File should be placed in: media/lua/server/Climate/ClimateMain.lua (remove date stamp)");
               write("--]]");
               var5.newLine();
               write("ClimateMain = {};");
               write("ClimateMain.versionStamp = \"" + var1 + "\";");
               var5.newLine();
               write("local WARM,NORMAL,CLOUDY = 0,1,2;");
               var5.newLine();
               write("local SUMMER,FALL,WINTER,SPRING = 0,1,2,3;");
               var5.newLine();
               write("function ClimateMain.onClimateManagerInit(_clim)");
               byte var8 = 1;
               write(var8, "local c;");
               write(var8, "c = _clim:getColNightNoMoon();");
               writeColor(var8, var7.getColNightNoMoon());
               var5.newLine();
               write(var8, "c = _clim:getColNightMoon();");
               writeColor(var8, var7.getColNightMoon());
               var5.newLine();
               write(var8, "c = _clim:getColFog();");
               writeColor(var8, var7.getColFog());
               var5.newLine();
               write(var8, "c = _clim:getColFogLegacy();");
               writeColor(var8, var7.getColFogLegacy());
               var5.newLine();
               WeatherPeriod var9 = var7.getWeatherPeriod();
               write(var8, "local w = _clim:getWeatherPeriod();");
               var5.newLine();
               write(var8, "c = w:getCloudColorReddish();");
               writeColor(var8, var9.getCloudColorReddish());
               var5.newLine();
               write(var8, "c = w:getCloudColorGreenish();");
               writeColor(var8, var9.getCloudColorGreenish());
               var5.newLine();
               write(var8, "c = w:getCloudColorBlueish();");
               writeColor(var8, var9.getCloudColorBlueish());
               var5.newLine();
               write(var8, "c = w:getCloudColorPurplish();");
               writeColor(var8, var9.getCloudColorPurplish());
               var5.newLine();
               String[] var10 = new String[]{"Dawn", "Day", "Dusk"};
               String[] var11 = new String[]{"SUMMER", "FALL", "WINTER", "SPRING"};
               String[] var12 = new String[]{"WARM", "NORMAL", "CLOUDY"};

               for(int var14 = 0; var14 < 3; ++var14) {
                  write(var8, "-- ###################### " + var10[var14] + " ######################");

                  for(int var15 = 0; var15 < 4; ++var15) {
                     for(int var16 = 0; var16 < 3; ++var16) {
                        if (var16 == 0 || var16 == 2 || var16 == 1 && var14 == 2) {
                           ClimateColorInfo var13 = var7.getSeasonColor(var14, var16, var15);
                           writeSeasonColor(var8, var13, var10[var14], var11[var15], var12[var16]);
                           var5.newLine();
                        }
                     }
                  }
               }

               write("end");
               var5.newLine();
               write("Events.OnClimateManagerInit.Add(ClimateMain.onClimateManagerInit);");
               writer = null;
               var5.flush();
               var5.close();
            } catch (Throwable var26) {
               var6 = var26;
               throw var26;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var25) {
                        var6.addSuppressed(var25);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } catch (Exception var28) {
            var28.printStackTrace();
         }
      } catch (Exception var29) {
         var29.printStackTrace();
      }

      return var0;
   }

   private static void writeSeasonColor(int var0, ClimateColorInfo var1, String var2, String var3, String var4) throws IOException {
      Color var5 = var1.exterior;
      write(var0, "_clim:setSeasonColor" + var2 + "(" + var4 + "," + var3 + "," + var5.r + "," + var5.g + "," + var5.b + "," + var5.a + ",true);\t\t--exterior");
      var5 = var1.interior;
      write(var0, "_clim:setSeasonColor" + var2 + "(" + var4 + "," + var3 + "," + var5.r + "," + var5.g + "," + var5.b + "," + var5.a + ",false);\t\t--interior");
   }

   private static void writeColor(int var0, ClimateColorInfo var1) throws IOException {
      Color var2 = var1.exterior;
      write(var0, "c:setExterior(" + var2.r + "," + var2.g + "," + var2.b + "," + var2.a + ");");
      var2 = var1.interior;
      write(var0, "c:setInterior(" + var2.r + "," + var2.g + "," + var2.b + "," + var2.a + ");");
   }

   private static void write(int var0, String var1) throws IOException {
      String var2 = (new String(new char[var0])).replace("\u0000", "\t");
      writer.write(var2);
      writer.write(var1);
      writer.newLine();
   }

   private static void write(String var0) throws IOException {
      writer.write(var0);
      writer.newLine();
   }
}
