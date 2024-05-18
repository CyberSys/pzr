package zombie;

import fmod.javafmod;
import fmod.fmod.FMODManager;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorGroup;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionMain;
import zombie.iso.IsoChunk;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoRoomLight;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.iso.objects.RainManager;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.radio.ZomboidRadio;
import zombie.ui.UIManager;

public class GameTime {
   public String millingtune = "longambient.ogg";
   public String zombieTune = "tune4.ogg";
   public static GameTime instance = new GameTime();
   private boolean lightUpdated = false;
   public int HelicopterTime1Start = 0;
   public int HelicopterTime1End = 0;
   public int HelicopterDay1 = 0;
   public float HelicopterTime2Start = 0.0F;
   public float HelicopterTime2End = 0.0F;
   public float HelicopterDay2 = 0.0F;
   private KahluaTable table = null;
   private int minutesMod = -1;
   private boolean thunderDay = true;
   private boolean randomAmbientToday = true;
   public float Ambient = 0.9F;
   public float AmbientMax = 1.0F;
   public float AmbientMin = 0.24F;
   public int Day = 22;
   public int StartDay = 22;
   public float MaxZombieCountStart = 750.0F;
   public float MinZombieCountStart = 750.0F;
   public float MaxZombieCount = 750.0F;
   public float MinZombieCount = 750.0F;
   public int Month = 7;
   public int StartMonth = 7;
   public float NightTint = 0.0F;
   public float TimeOfDay = 9.0F;
   public float StartTimeOfDay = 9.0F;
   public float ViewDist = 10.0F;
   public float ViewDistMax = 42.0F;
   public float ViewDistMin = 19.0F;
   public int Year = 2012;
   public int StartYear = 2012;
   public int NightsSurvived = 0;
   public double HoursSurvived = 0.0D;
   public GregorianCalendar Calender;
   public float MinutesPerDayStart = 30.0F;
   public float MinutesPerDay;
   public float SleepMultiplier;
   public float LastTimeOfDay;
   public int TargetZombies;
   public int LastCookMinute;
   public boolean RainingToday;
   private float Multiplier;
   public float FPSMultiplier;
   private int dusk;
   private int dawn;
   private static long serverTimeShift = 0L;
   private static long serverTimeShiftLast = 0L;
   private static boolean serverTimeShiftIsSet = false;
   private static long serverTimeuQality = 10000000L;
   public static long serverTimePing = 100000000L;
   private static boolean isUTest = false;
   public boolean bGunFireEventToday;
   public float[] GunFireTimes;
   public int NumGunFireEvents;
   int timeSinceLastMusicChange;
   int timeToMusicChange;
   public long TicksSinceStart;
   private float Night;
   private float NightMin;
   private float NightMax;
   public float Moon;
   public long lastPing;
   public long lastClockSync;
   public float ServerTimeOfDay;
   public float ServerLastTimeOfDay;
   public int ServerNewDays;
   public long timestampForDbUpdate;
   private long minutesStamp;
   private GameTime.ThunderStorm thunderStorm;
   public float lightSourceUpdate;
   public float multiplierBias;
   public float LastLastTimeOfDay;
   public float FPSMultiplier2;
   private int berriesCreated;

   public GameTime() {
      this.MinutesPerDay = this.MinutesPerDayStart;
      this.SleepMultiplier = 1.0F;
      this.TargetZombies = (int)this.MinZombieCountStart;
      this.LastCookMinute = 0;
      this.RainingToday = true;
      this.Multiplier = 1.0F;
      this.FPSMultiplier = 1.1F;
      this.dusk = 3;
      this.dawn = 12;
      this.bGunFireEventToday = false;
      this.GunFireTimes = new float[5];
      this.NumGunFireEvents = 1;
      this.timeSinceLastMusicChange = 0;
      this.timeToMusicChange = 7200;
      this.TicksSinceStart = 0L;
      this.NightMin = 0.0F;
      this.NightMax = 1.0F;
      this.Moon = 0.0F;
      this.lastPing = 0L;
      this.lastClockSync = 0L;
      this.timestampForDbUpdate = 0L;
      this.minutesStamp = 0L;
      this.thunderStorm = new GameTime.ThunderStorm();
      this.lightSourceUpdate = 0.0F;
      this.multiplierBias = 1.0F;
      this.LastLastTimeOfDay = 0.0F;
      this.FPSMultiplier2 = 0.0F;
      this.berriesCreated = 0;
   }

   public static GameTime getInstance() {
      return instance;
   }

   public static void setInstance(GameTime var0) {
      instance = var0;
   }

   public float getRealworldSecondsSinceLastUpdate() {
      return 0.016666668F * this.FPSMultiplier;
   }

   public float getMultipliedSecondsSinceLastUpdate() {
      return 0.016666668F * this.getUnmoddedMultiplier();
   }

   public float getGameWorldSecondsSinceLastUpdate() {
      float var1 = 1440.0F / this.getMinutesPerDay();
      return 0.016666668F * this.getMultiplier() * var1;
   }

   public static void syncServerTime(long var0, long var2, long var4) {
      long var6 = var4 - var0;
      long var8 = var2 - var4 + var6 / 2L;
      serverTimeShiftLast = serverTimeShift;
      if (!serverTimeShiftIsSet) {
         serverTimeShift = var8;
      } else {
         serverTimeShift = (long)((float)serverTimeShift + (float)(var8 - serverTimeShift) * 0.05F);
      }

      if (Math.abs(serverTimeShift - serverTimeShiftLast) > serverTimeuQality) {
         sendTimeSync();
      } else {
         serverTimeShiftIsSet = true;
      }

   }

   public static long getServerTime() {
      if (isUTest) {
         return System.nanoTime() + serverTimeShift;
      } else if (GameServer.bServer) {
         return System.nanoTime();
      } else if (GameClient.bClient) {
         return !serverTimeShiftIsSet ? 0L : System.nanoTime() + serverTimeShift;
      } else {
         return 0L;
      }
   }

   public static boolean getServerTimeShiftIsSet() {
      return serverTimeShiftIsSet;
   }

   public static void setServerTimeShift(long var0) {
      isUTest = true;
      serverTimeShift = var0;
      serverTimeShiftIsSet = true;
   }

   public static void sendTimeSync() {
      ByteBufferWriter var0 = GameClient.connection.startPacket();
      PacketTypes.doPacket((short)160, var0);
      long var1 = System.nanoTime();
      var0.putLong(var1);
      var0.putLong(0L);
      GameClient.connection.endPacket();
   }

   public static void receiveTimeSync(ByteBuffer var0, UdpConnection var1) {
      long var2;
      long var4;
      if (GameServer.bServer) {
         var2 = var0.getLong();
         var4 = System.nanoTime();
         ByteBufferWriter var6 = var1.startPacket();
         PacketTypes.doPacket((short)160, var6);
         var6.putLong(var2);
         var6.putLong(var4);
         var1.endPacketImmediate();
      }

      if (GameClient.bClient) {
         var2 = var0.getLong();
         var4 = var0.getLong();
         long var8 = System.nanoTime();
         syncServerTime(var2, var4, var8);
      }

   }

   public int daysInMonth(int var1, int var2) {
      if (this.Calender == null) {
         this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), this.getMinutes());
      }

      int[] var3 = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
      var3[1] += this.getCalender().isLeapYear(var1) ? 1 : 0;
      return var3[var2];
   }

   public String getDeathString(IsoPlayer var1) {
      return Translator.getText("IGUI_Gametime_SurvivedFor", this.getTimeSurvived(var1));
   }

   public int getDaysSurvived() {
      float var1 = 0.0F;

      int var2;
      for(var2 = 0; var2 < IsoPlayer.numPlayers; ++var2) {
         IsoPlayer var3 = IsoPlayer.players[var2];
         if (var3 != null) {
            var1 = Math.max(var1, (float)var3.getHoursSurvived());
         }
      }

      var2 = (int)var1 / 24;
      var2 %= 30;
      return var2;
   }

   public String getTimeSurvived(IsoPlayer var1) {
      String var2 = "";
      float var3 = (float)var1.getHoursSurvived();
      Integer var4 = (int)var3 % 24;
      Integer var5 = (int)var3 / 24;
      Integer var6 = var5 / 30;
      var5 = var5 % 30;
      Integer var7 = var6 / 12;
      var6 = var6 % 12;
      String var8 = Translator.getText("IGUI_Gametime_day");
      String var9 = Translator.getText("IGUI_Gametime_year");
      String var10 = Translator.getText("IGUI_Gametime_hour");
      String var11 = Translator.getText("IGUI_Gametime_month");
      if (var7 != 0) {
         if (var7 > 1) {
            var9 = Translator.getText("IGUI_Gametime_years");
         }

         var2 = var2 + var7 + " " + var9;
      }

      if (var6 != 0) {
         if (var6 > 1) {
            var11 = Translator.getText("IGUI_Gametime_months");
         }

         if (var2.length() > 0) {
            var2 = var2 + ", ";
         }

         var2 = var2 + var6 + " " + var11;
      }

      if (var5 != 0) {
         if (var5 > 1) {
            var8 = Translator.getText("IGUI_Gametime_days");
         }

         if (var2.length() > 0) {
            var2 = var2 + ", ";
         }

         var2 = var2 + var5 + " " + var8;
      }

      if (var4 != 0) {
         if (var4 > 1) {
            var10 = Translator.getText("IGUI_Gametime_hours");
         }

         if (var2.length() > 0) {
            var2 = var2 + ", ";
         }

         var2 = var2 + var4 + " " + var10;
      }

      if (var2.trim().length() == 0) {
         int var12 = (int)(var3 * 60.0F);
         int var13 = (int)(var3 * 60.0F * 60.0F) - var12 * 60;
         var2 = "" + var12 + " " + Translator.getText("IGUI_Gametime_minutes") + ", " + var13 + " " + Translator.getText("IGUI_Gametime_secondes");
      }

      return var2;
   }

   public String getZombieKilledText(IsoPlayer var1) {
      int var2 = var1.getZombieKills();
      if (var2 != 0 && var2 <= 1) {
         return var2 == 1 ? Translator.getText("IGUI_Gametime_zombieCount", var2) : null;
      } else {
         return Translator.getText("IGUI_Gametime_zombiesCount", var2);
      }
   }

   public String getGameModeText() {
      String var1 = Translator.getText("IGUI_Gametime_" + Core.GameMode);
      if (var1.startsWith("IGUI_")) {
         var1 = Core.GameMode;
      }

      String var2 = Translator.getText("IGUI_Gametime_GameMode", var1);
      if (var2.startsWith("IGUI")) {
         var2 = "Game mode: " + var1;
      }

      if (Core.bDebug) {
         var2 = var2 + " debug";
      }

      return var2;
   }

   public void init() {
      this.setDay(this.getStartDay());
      this.setTimeOfDay(this.getStartTimeOfDay());
      this.setMonth(this.getStartMonth());
      this.setYear(this.getStartYear());
      this.setMaxZombieCount(this.getMaxZombieCountStart());
      this.setMinZombieCount(this.getMinZombieCountStart());
      this.setTargetZombies((int)this.getMinZombieCountStart());
      if (SandboxOptions.instance.Helicopter.getValue() != 1) {
         this.HelicopterDay1 = Rand.Next(6, 10);
         this.HelicopterTime1Start = Rand.Next(9, 19);
         this.HelicopterTime1End = this.HelicopterTime1Start + Rand.Next(4) + 1;
      }

      if (IsoPlayer.DemoMode) {
         this.setTimeOfDay(11.0F);
      }

      this.setMinutesStamp();
   }

   public float Lerp(float var1, float var2, float var3) {
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var3 >= 1.0F) {
         var3 = 1.0F;
      }

      float var4 = var2 - var1;
      float var5 = var4 * var3;
      return var1 + var5;
   }

   /** @deprecated */
   public void RemoveZombies(int var1) {
   }

   public void RemoveZombiesIndiscriminate(int var1) {
      if (var1 != 0) {
         for(int var2 = 0; var2 < IsoWorld.instance.CurrentCell.getZombieList().size(); ++var2) {
            IsoZombie var3 = (IsoZombie)IsoWorld.instance.CurrentCell.getZombieList().get(0);
            IsoWorld.instance.CurrentCell.getZombieList().remove(var2);
            IsoWorld.instance.CurrentCell.getRemoveList().add(var3);
            var3.getCurrentSquare().getMovingObjects().remove(var3);
            --var2;
            --var1;
            if (var1 == 0 || IsoWorld.instance.CurrentCell.getZombieList().isEmpty()) {
               return;
            }
         }

      }
   }

   public float TimeLerp(float var1, float var2, float var3, float var4) {
      float var5 = getInstance().getTimeOfDay();
      if (var4 < var3) {
         var4 += 24.0F;
      }

      boolean var6 = false;
      float var7;
      if (var5 > var4 && var5 > var3 || var5 < var4 && var5 < var3) {
         var3 += 24.0F;
         var6 = true;
         var7 = var3;
         var3 = var4;
         var4 = var7;
         if (var5 < var3) {
            var5 += 24.0F;
         }
      }

      var7 = var4 - var3;
      float var8 = var5 - var3;
      float var9 = 0.0F;
      if (var8 > var7) {
         var9 = 1.0F;
      }

      if (var8 < var7 && var8 > 0.0F) {
         var9 = var8 / var7;
      }

      float var10;
      if (GameClient.bClient) {
         var10 = (float)ServerOptions.instance.nightlengthmodifier.getValue();
         var9 *= var10;
      }

      if (var6) {
         var9 = 1.0F - var9;
      }

      var10 = 0.0F;
      var9 = (var9 - 0.5F) * 2.0F;
      if ((double)var9 < 0.0D) {
         var10 = -1.0F;
      } else {
         var10 = 1.0F;
      }

      var9 = Math.abs(var9);
      var9 = 1.0F - var9;
      var9 = (float)Math.pow((double)var9, 8.0D);
      var9 = 1.0F - var9;
      var9 *= var10;
      var9 = var9 * 0.5F + 0.5F;
      return this.Lerp(var1, var2, var9);
   }

   public float TimeLerpCompressed(float var1, float var2, float var3, float var4) {
      float var5 = getInstance().getTimeOfDay();
      if (var4 < var3) {
         var4 += 24.0F;
      }

      boolean var6 = false;
      float var7;
      if (var5 > var4 && var5 > var3 || var5 < var4 && var5 < var3) {
         var3 += 24.0F;
         var6 = true;
         var7 = var3;
         var3 = var4;
         var4 = var7;
         if (var5 < var3) {
            var5 += 24.0F;
         }
      }

      var7 = var4 - var3;
      float var8 = var5 - var3;
      float var9 = 0.0F;
      if (var8 > var7) {
         var9 = 1.0F;
      }

      if (var8 < var7 && var8 > 0.0F) {
         var9 = var8 / var7;
      }

      if (var6) {
         var9 = 1.0F - var9;
      }

      float var10 = 0.0F;
      var9 = (var9 - 0.5F) * 2.0F;
      if ((double)var9 < 0.0D) {
         var10 = -1.0F;
      } else {
         var10 = 1.0F;
      }

      var9 = Math.abs(var9);
      var9 = 1.0F - var9;
      var9 = (float)Math.pow((double)var9, 8.0D);
      var9 = 1.0F - var9;
      var9 *= var10;
      var9 = var9 * 0.5F + 0.5F;
      var9 = var9 * var9 * var9 * var9;
      return this.Lerp(var1, var2, var9);
   }

   public float getDeltaMinutesPerDay() {
      return this.MinutesPerDayStart / this.MinutesPerDay;
   }

   public float getNightMin() {
      return 1.0F - this.NightMin;
   }

   public float getNightMax() {
      return 1.0F - this.NightMax;
   }

   public void setNightMax(float var1) {
      this.NightMax = 1.0F - var1;
   }

   public void setNightMin(float var1) {
      this.NightMin = 1.0F - var1;
   }

   public int getMinutes() {
      return (int)((this.getTimeOfDay() - (float)((int)this.getTimeOfDay())) * 60.0F);
   }

   public void setMoon(float var1) {
      this.Moon = var1;
   }

   public void update(boolean var1) {
      long var2 = System.currentTimeMillis();
      if (GameClient.bClient && (this.lastPing == 0L || var2 - this.lastPing > (long)(ServerOptions.instance.PingFrequency.getValue() * 1000))) {
         sendTimeSync();
         this.lastPing = var2;
      }

      short var4 = 9000;
      if (SandboxOptions.instance.MetaEvent.getValue() == 1) {
         var4 = -1;
      }

      if (SandboxOptions.instance.MetaEvent.getValue() == 3) {
         var4 = 6000;
      }

      if (!GameClient.bClient && this.randomAmbientToday && var4 != -1 && Rand.Next(Rand.AdjustForFramerate(var4)) == 0 && (UIManager.getSpeedControls() == null || UIManager.getSpeedControls().getCurrentGameSpeed() != 0)) {
         AmbientStreamManager.instance.addRandomAmbient();
         if (SandboxOptions.instance.MetaEvent.getValue() == 3 && Rand.Next(3) == 0) {
            this.randomAmbientToday = true;
         } else {
            this.randomAmbientToday = false;
         }
      }

      if (GameServer.bServer && UIManager.getSpeedControls() != null) {
         UIManager.getSpeedControls().SetCurrentGameSpeed(1);
      }

      Core.getInstance();
      int var5;
      if (GameServer.bServer || !GameClient.bClient) {
         if (this.bGunFireEventToday) {
            for(var5 = 0; var5 < this.NumGunFireEvents; ++var5) {
               if (this.TimeOfDay > this.GunFireTimes[var5] && this.LastLastTimeOfDay < this.GunFireTimes[var5]) {
                  AmbientStreamManager.instance.doGunEvent();
               }
            }
         }

         if (this.NightsSurvived == this.HelicopterDay1 && this.TimeOfDay > (float)this.HelicopterTime1Start && this.TimeOfDay < (float)this.HelicopterTime1End && !IsoWorld.instance.helicopter.isActive() && Rand.Next((int)(800.0F * this.getInvMultiplier())) == 0) {
            this.HelicopterTime1Start = (int)((float)this.HelicopterTime1Start + 0.5F);
            IsoWorld.instance.helicopter.pickRandomTarget();
         }

         if (this.NightsSurvived > this.HelicopterDay1 && (SandboxOptions.instance.Helicopter.getValue() == 3 || SandboxOptions.instance.Helicopter.getValue() == 4)) {
            if (SandboxOptions.instance.Helicopter.getValue() == 3) {
               this.HelicopterDay1 = this.NightsSurvived + Rand.Next(10, 16);
            }

            if (SandboxOptions.instance.Helicopter.getValue() == 4) {
               this.HelicopterDay1 = this.NightsSurvived + Rand.Next(6, 10);
            }

            this.HelicopterTime1Start = Rand.Next(9, 19);
            this.HelicopterTime1End = this.HelicopterTime1Start + Rand.Next(4) + 1;
         }
      }

      ++this.TicksSinceStart;
      var5 = this.getHour();
      this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), (int)((this.getTimeOfDay() - (float)((int)this.getTimeOfDay())) * 60.0F));
      if (Rand.Next(300) == 0 && this.timeSinceLastMusicChange > this.timeToMusicChange) {
         this.timeSinceLastMusicChange = 0;
         if (Rand.Next(3) == 0) {
         }
      }

      float var6 = this.getTimeOfDay();
      float var7;
      int var8;
      if (UIManager.getSpeedControls() == null || UIManager.getSpeedControls() != null && UIManager.getSpeedControls().getCurrentGameSpeed() > 0) {
         var7 = 1.0F / this.getMinutesPerDay() / 60.0F * this.getMultiplier() / 2.0F;
         if (Core.bLastStand) {
            var7 = 1.0F / this.getMinutesPerDay() / 60.0F * this.getUnmoddedMultiplier() / 2.0F;
         }

         this.setTimeOfDay(this.getTimeOfDay() + var7);
         if (this.getHour() != var5) {
            LuaEventManager.triggerEvent("EveryHours");
         }

         IsoPlayer var9;
         if (!GameServer.bServer) {
            for(var8 = 0; var8 < IsoPlayer.numPlayers; ++var8) {
               var9 = IsoPlayer.players[var8];
               if (var9 != null && var9.isAlive()) {
                  var9.setHoursSurvived(var9.getHoursSurvived() + (double)var7);
               }
            }
         }

         IsoPlayer var10;
         ArrayList var12;
         int var13;
         if (GameServer.bServer) {
            var12 = GameClient.instance.getPlayers();

            for(var13 = 0; var13 < var12.size(); ++var13) {
               var10 = (IsoPlayer)var12.get(var13);
               var10.setHoursSurvived(var10.getHoursSurvived() + (double)var7);
            }
         }

         if (GameClient.bClient) {
            var12 = GameClient.instance.getPlayers();

            for(var13 = 0; var13 < var12.size(); ++var13) {
               var10 = (IsoPlayer)var12.get(var13);
               if (var10 != null && !var10.isDead() && !var10.isLocalPlayer()) {
                  var10.setHoursSurvived(var10.getHoursSurvived() + (double)var7);
               }
            }
         }

         for(var8 = 0; var8 < IsoPlayer.numPlayers; ++var8) {
            var9 = IsoPlayer.players[var8];
            if (var9 != null) {
               if (var9.isAsleep()) {
                  var9.setAsleepTime(var9.getAsleepTime() + var7);
                  SleepingEvent.instance.update(var9);
               } else {
                  var9.setAsleepTime(0.0F);
               }
            }
         }
      }

      if (!GameClient.bClient && var6 <= 7.0F && this.getTimeOfDay() > 7.0F) {
         this.setNightsSurvived(this.getNightsSurvived() + 1);
         this.doMetaEvents();
      }

      if (GameClient.bClient) {
         if (this.getTimeOfDay() >= 24.0F) {
            this.setTimeOfDay(this.getTimeOfDay() - 24.0F);
         }

         while(this.ServerNewDays > 0) {
            --this.ServerNewDays;
            this.setDay(this.getDay() + 1);
            if (this.getDay() >= this.daysInMonth(this.getYear(), this.getMonth())) {
               this.setDay(0);
               this.setMonth(this.getMonth() + 1);
               if (this.getMonth() >= 12) {
                  this.setMonth(0);
                  this.setYear(this.getYear() + 1);
               }
            }

            this.setCalender(new GregorianCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), this.getMinutes()));
            LuaEventManager.triggerEvent("EveryDays");
         }
      } else if (this.getTimeOfDay() >= 24.0F) {
         this.setTimeOfDay(this.getTimeOfDay() - 24.0F);
         this.setDay(this.getDay() + 1);
         if (this.getMaxZombieCount() < 1000.0F) {
         }

         if (this.getDay() >= this.daysInMonth(this.getYear(), this.getMonth())) {
            this.setDay(0);
            this.setMonth(this.getMonth() + 1);
            if (this.getMonth() >= 12) {
               this.setMonth(0);
               this.setYear(this.getYear() + 1);
            }
         }

         this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), this.getMinutes());
         LuaEventManager.triggerEvent("EveryDays");
         if (GameServer.bServer) {
            GameServer.syncClock();
            this.lastClockSync = var2;
         }
      }

      var7 = this.Moon * 20.0F;
      this.thunderStorm.update();
      if (!ClimateManager.getInstance().getThunderStorm().isModifyingNight()) {
         this.setAmbient(this.TimeLerp(this.getAmbientMin(), this.getAmbientMax(), (float)this.getDusk(), (float)this.getDawn()));
      }

      this.setViewDist(this.TimeLerp(this.getViewDistMin() + var7, this.getViewDistMax(), (float)this.getDusk(), (float)this.getDawn()));
      if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
         this.setNightTint(0.0F);
      }

      this.UpdateZombieCount();
      this.setMinutesStamp();
      var8 = (int)((this.getTimeOfDay() - (float)((int)this.getTimeOfDay())) * 60.0F);
      if (var8 / 10 != this.minutesMod) {
         IsoPlayer[] var15 = IsoPlayer.players;

         for(int var14 = 0; var14 < var15.length; ++var14) {
            IsoPlayer var11 = var15[var14];
            if (var11 != null) {
               var11.dirtyRecalcGridStackTime = 1.0F;
            }
         }

         ErosionMain.EveryTenMinutes();
         ClimateManager.getInstance().updateEveryTenMins();
         LuaEventManager.triggerEvent("EveryTenMinutes");
         this.minutesMod = var8 / 10;
         ZomboidRadio.getInstance().UpdateScripts(this.getHour(), var8);
      }

      if (GameServer.bServer && (var2 - this.lastClockSync > 10000L || GameServer.bFastForward)) {
         GameServer.syncClock();
         this.lastClockSync = var2;
      }

   }

   private void setMinutesStamp() {
      this.minutesStamp = (long)(this.getWorldAgeHours() * 60.0D) + (long)this.getMinutes();
   }

   public long getMinutesStamp() {
      return this.minutesStamp;
   }

   public void thunderStart() {
   }

   public void thunderStart(boolean var1) {
   }

   public void thunderStop() {
   }

   public boolean getThunderStorm() {
      return ClimateManager.getInstance().getIsThunderStorming();
   }

   public void lightingUpdate() {
      if (this.updateLightSource(false)) {
         this.lightSourceUpdate = 1000.0F;
      }

      if (this.lightSourceUpdate > 30.0F) {
         this.updateLightSource(true);
         this.lightSourceUpdate = 0.0F;
      }

      ++this.lightSourceUpdate;
   }

   private void doMetaEvents() {
      byte var1 = 3;
      if (SandboxOptions.instance.MetaEvent.getValue() == 1) {
         var1 = -1;
      }

      if (SandboxOptions.instance.MetaEvent.getValue() == 3) {
         var1 = 2;
      }

      this.bGunFireEventToday = var1 != -1 && Rand.Next(var1) == 0;
      if (this.bGunFireEventToday) {
         this.NumGunFireEvents = 1;

         for(int var2 = 0; var2 < this.NumGunFireEvents; ++var2) {
            this.GunFireTimes[var2] = (float)Rand.Next(18000) / 1000.0F + 7.0F;
         }
      }

      this.randomAmbientToday = true;
   }

   public boolean updateLightSource(boolean var1) {
      boolean var2 = false;
      if (IsoWorld.instance.CurrentCell == null) {
         return false;
      } else {
         for(int var3 = 0; var3 < IsoWorld.instance.CurrentCell.getLamppostPositions().size(); ++var3) {
            IsoLightSource var4 = (IsoLightSource)IsoWorld.instance.CurrentCell.getLamppostPositions().get(var3);
            IsoChunk var5 = IsoWorld.instance.CurrentCell.getChunkForGridSquare(var4.x, var4.y, var4.z);
            if (var5 != null && var4.chunk != null && var4.chunk != var5) {
               var4.life = 0;
            }

            if (var4.life > 0) {
               var2 = true;
            }

            if (var1) {
               var4.clearInfluence();
            }

            if (var4.life == 0 || !var4.isInBounds()) {
               IsoWorld.instance.CurrentCell.getLamppostPositions().remove(var3);
               --var3;
               if (!var1) {
                  var4.clearInfluence();
                  var2 = true;
               }
            }
         }

         ArrayList var6 = IsoWorld.instance.CurrentCell.roomLights;

         int var7;
         IsoRoomLight var8;
         for(var7 = 0; var7 < var6.size(); ++var7) {
            var8 = (IsoRoomLight)var6.get(var7);
            if (var1) {
               var8.clearInfluence();
            }

            if (!var8.isInBounds()) {
               var6.remove(var7--);
               if (!var1) {
                  var8.clearInfluence();
                  var2 = true;
               }
            }
         }

         if (var1) {
            for(var7 = 0; var7 < var6.size(); ++var7) {
               var8 = (IsoRoomLight)var6.get(var7);
               var8.addInfluence();
            }

            for(var7 = 0; var7 < IsoWorld.instance.CurrentCell.getLamppostPositions().size(); ++var7) {
               IsoLightSource var9 = (IsoLightSource)IsoWorld.instance.CurrentCell.getLamppostPositions().get(var7);
               var9.update();
            }
         }

         return var2;
      }
   }

   /** @deprecated */
   public void UpdateZombieCount() {
   }

   /** @deprecated */
   private void AddZombies(int var1) {
      if (var1 != 0) {
         ;
      }
   }

   /** @deprecated */
   @Deprecated
   public float getAmbient() {
      return ClimateManager.getInstance().getAmbient();
   }

   public void setAmbient(float var1) {
      this.Ambient = var1;
   }

   public float getAmbientMax() {
      return this.AmbientMax;
   }

   public void setAmbientMax(float var1) {
      var1 = Math.min(1.0F, var1);
      var1 = Math.max(0.0F, var1);
      this.AmbientMax = var1;
   }

   public float getAmbientMin() {
      return this.AmbientMin;
   }

   public void setAmbientMin(float var1) {
      var1 = Math.min(1.0F, var1);
      var1 = Math.max(0.0F, var1);
      this.AmbientMin = var1;
   }

   public int getDay() {
      return this.Day;
   }

   public void setDay(int var1) {
      this.Day = var1;
   }

   public int getStartDay() {
      return this.StartDay;
   }

   public void setStartDay(int var1) {
      this.StartDay = var1;
   }

   public float getMaxZombieCountStart() {
      return 0.0F;
   }

   public void setMaxZombieCountStart(float var1) {
      this.MaxZombieCountStart = var1;
   }

   public float getMinZombieCountStart() {
      return 0.0F;
   }

   public void setMinZombieCountStart(float var1) {
      this.MinZombieCountStart = var1;
   }

   public float getMaxZombieCount() {
      return this.MaxZombieCount;
   }

   public void setMaxZombieCount(float var1) {
      this.MaxZombieCount = var1;
   }

   public float getMinZombieCount() {
      return this.MinZombieCount;
   }

   public void setMinZombieCount(float var1) {
      this.MinZombieCount = var1;
   }

   public int getMonth() {
      return this.Month;
   }

   public void setMonth(int var1) {
      this.Month = var1;
   }

   public int getStartMonth() {
      return this.StartMonth;
   }

   public void setStartMonth(int var1) {
      this.StartMonth = var1;
   }

   public float getNightTint() {
      return ClimateManager.getInstance().getNightStrength();
   }

   public void setNightTint(float var1) {
   }

   public float getNight() {
      return ClimateManager.getInstance().getNightStrength();
   }

   public void setNight(float var1) {
   }

   public float getTimeOfDay() {
      return this.TimeOfDay;
   }

   public void setTimeOfDay(float var1) {
      this.TimeOfDay = var1;
   }

   public float getStartTimeOfDay() {
      return this.StartTimeOfDay;
   }

   public void setStartTimeOfDay(float var1) {
      this.StartTimeOfDay = var1;
   }

   public float getViewDist() {
      return ClimateManager.getInstance().getViewDistance();
   }

   public void setViewDist(float var1) {
      this.ViewDist = var1;
   }

   public float getViewDistMax() {
      return this.ViewDistMax;
   }

   public void setViewDistMax(float var1) {
      this.ViewDistMax = var1;
   }

   public float getViewDistMin() {
      return this.ViewDistMin;
   }

   public void setViewDistMin(float var1) {
      this.ViewDistMin = var1;
   }

   public int getYear() {
      return this.Year;
   }

   public void setYear(int var1) {
      this.Year = var1;
   }

   public int getStartYear() {
      return this.StartYear;
   }

   public void setStartYear(int var1) {
      this.StartYear = var1;
   }

   public int getNightsSurvived() {
      return this.NightsSurvived;
   }

   public void setNightsSurvived(int var1) {
      this.NightsSurvived = var1;
   }

   public double getWorldAgeHours() {
      float var1 = (float)(this.getNightsSurvived() * 24);
      if (this.getTimeOfDay() >= 7.0F) {
         var1 += this.getTimeOfDay() - 7.0F;
      } else {
         var1 += this.getTimeOfDay() + 17.0F;
      }

      return (double)var1;
   }

   public double getHoursSurvived() {
      DebugLog.log("GameTime.getHoursSurvived() has no meaning, use IsoPlayer.getHourSurvived() instead");
      return this.HoursSurvived;
   }

   public int getHour() {
      double var1 = Math.floor((double)(this.getTimeOfDay() * 3600.0F));
      return (int)Math.floor(var1 / 3600.0D);
   }

   public void setHoursSurvived(double var1) {
      DebugLog.log("GameTime.getHoursSurvived() has no meaning, use IsoPlayer.getHourSurvived() instead");
      this.HoursSurvived = var1;
   }

   public GregorianCalendar getCalender() {
      this.updateCalendar(this.getYear(), this.getMonth(), this.getDay(), (int)this.getTimeOfDay(), (int)((this.getTimeOfDay() - (float)((int)this.getTimeOfDay())) * 60.0F));
      return this.Calender;
   }

   public void setCalender(GregorianCalendar var1) {
      this.Calender = var1;
   }

   public void updateCalendar(int var1, int var2, int var3, int var4, int var5) {
      if (this.Calender == null) {
         this.Calender = new GregorianCalendar();
      }

      this.Calender.set(var1, var2, var3, var4, var5);
   }

   public float getMinutesPerDay() {
      return this.MinutesPerDay;
   }

   public void setMinutesPerDay(float var1) {
      this.MinutesPerDay = var1;
   }

   public float getSleepMultiplier() {
      return this.SleepMultiplier;
   }

   public void setSleepMultiplier(float var1) {
      this.SleepMultiplier = var1;
   }

   public float getLastTimeOfDay() {
      return this.LastTimeOfDay;
   }

   public void setLastTimeOfDay(float var1) {
      this.LastTimeOfDay = var1;
   }

   public int getTargetZombies() {
      return (int)this.getMinZombieCountStart();
   }

   public void setTargetZombies(int var1) {
      this.TargetZombies = var1;
   }

   public int getLastCookMinute() {
      return this.LastCookMinute;
   }

   public void setLastCookMinute(int var1) {
      this.LastCookMinute = var1;
   }

   public boolean isRainingToday() {
      return this.RainingToday;
   }

   public void setRainingToday(boolean var1) {
      this.RainingToday = var1;
   }

   public float getMultiplier() {
      if (GameServer.bServer) {
         this.Multiplier = 1.0F;
      }

      if (GameServer.bServer) {
         this.Multiplier = GameServer.bFastForward ? new Float(ServerOptions.instance.FastForwardMultiplier.getValue()) / this.getDeltaMinutesPerDay() : 1.0F;
      } else if (GameClient.bClient) {
         this.Multiplier = GameClient.bFastForward ? new Float(ServerOptions.instance.FastForwardMultiplier.getValue()) / this.getDeltaMinutesPerDay() : 1.0F;
      }

      float var1 = this.Multiplier * this.FPSMultiplier;
      var1 *= 0.5F;
      if (!GameServer.bServer && !GameClient.bClient && IsoPlayer.instance != null && IsoPlayer.allPlayersAsleep()) {
         return 200.0F * (30.0F / (float)PerformanceSettings.LockFPS);
      } else {
         var1 *= 1.6F;
         var1 *= this.multiplierBias;
         return var1;
      }
   }

   public float getServerMultiplier() {
      float var1 = 10.0F / GameWindow.averageFPS / (float)(PerformanceSettings.ManualFrameSkips + 1);
      float var2 = this.Multiplier * var1;
      var2 *= 0.5F;
      if (!GameServer.bServer && !GameClient.bClient && IsoPlayer.instance != null && IsoPlayer.allPlayersAsleep()) {
         return 200.0F * (30.0F / (float)PerformanceSettings.LockFPS);
      } else {
         var2 *= 1.6F;
         var2 *= this.multiplierBias;
         return var2;
      }
   }

   public float getUnmoddedMultiplier() {
      float var1 = this.Multiplier * this.FPSMultiplier;
      return !GameServer.bServer && !GameClient.bClient && IsoPlayer.instance != null && IsoPlayer.allPlayersAsleep() ? 200.0F * (30.0F / (float)PerformanceSettings.LockFPS) : var1;
   }

   public float getInvMultiplier() {
      float var1 = this.getMultiplier();
      return 1.0F / var1;
   }

   public float getTrueMultiplier() {
      float var1 = this.Multiplier;
      return var1;
   }

   public void setMultiplier(float var1) {
      this.Multiplier = var1;
   }

   public void save() {
      File var1 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_t.bin");
      FileOutputStream var2 = null;

      try {
         var2 = new FileOutputStream(var1);
      } catch (FileNotFoundException var7) {
         var7.printStackTrace();
         return;
      }

      DataOutputStream var3 = new DataOutputStream(new BufferedOutputStream(var2));

      try {
         instance.save(var3);
      } catch (IOException var6) {
         var6.printStackTrace();
      }

      try {
         var3.flush();
         var3.close();
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   public void save(DataOutputStream var1) throws IOException {
      var1.writeByte(71);
      var1.writeByte(77);
      var1.writeByte(84);
      var1.writeByte(77);
      var1.writeInt(143);
      var1.writeFloat(this.Multiplier);
      var1.writeInt(this.NightsSurvived);
      var1.writeInt(this.TargetZombies);
      var1.writeFloat(this.LastTimeOfDay);
      var1.writeFloat(this.TimeOfDay);
      var1.writeInt(this.Day);
      var1.writeInt(this.Month);
      var1.writeInt(this.Year);
      var1.writeFloat(0.0F);
      var1.writeFloat(0.0F);
      var1.writeInt(IsoWorld.instance.Groups.size());

      for(int var2 = 0; var2 < IsoWorld.instance.Groups.size(); ++var2) {
         SurvivorGroup var3 = (SurvivorGroup)IsoWorld.instance.Groups.get(var2);
         if (var3 == null || var3.Leader == null) {
            boolean var4 = false;
         }

         var1.writeInt(var3.Leader.getID());
         var1.writeInt(var3.Members.size());

         for(int var5 = 0; var5 < var3.Members.size(); ++var5) {
            var1.writeInt(((SurvivorDesc)var3.Members.get(var5)).getID());
         }
      }

      if (this.table != null) {
         var1.writeByte(1);
         this.table.save(var1);
      } else {
         var1.writeByte(0);
      }

      GameWindow.WriteString(var1, Core.getInstance().getPoisonousBerry());
      GameWindow.WriteString(var1, Core.getInstance().getPoisonousMushroom());
      var1.writeInt(this.HelicopterDay1);
      var1.writeInt(this.HelicopterTime1Start);
      var1.writeInt(this.HelicopterTime1End);
      ClimateManager.getInstance().save(var1);
   }

   public void save(ByteBuffer var1) throws IOException {
      var1.putFloat(this.Multiplier);
      var1.putInt(this.NightsSurvived);
      var1.putInt(this.TargetZombies);
      var1.putFloat(this.LastTimeOfDay);
      var1.putFloat(this.TimeOfDay);
      var1.putInt(this.Day);
      var1.putInt(this.Month);
      var1.putInt(this.Year);
      var1.putFloat(0.0F);
      var1.putFloat(0.0F);
      var1.putInt(IsoWorld.instance.Groups.size());

      for(int var2 = 0; var2 < IsoWorld.instance.Groups.size(); ++var2) {
         SurvivorGroup var3 = (SurvivorGroup)IsoWorld.instance.Groups.get(var2);
         if (var3 == null || var3.Leader == null) {
            boolean var4 = false;
         }

         var1.putInt(var3.Leader.getID());
         var1.putInt(var3.Members.size());

         for(int var5 = 0; var5 < var3.Members.size(); ++var5) {
            var1.putInt(((SurvivorDesc)var3.Members.get(var5)).getID());
         }
      }

      if (this.table != null) {
         var1.put((byte)1);
         this.table.save(var1);
      } else {
         var1.put((byte)0);
      }

   }

   public void load(DataInputStream var1) throws IOException {
      int var2 = IsoWorld.SavedWorldVersion;
      if (var2 == -1) {
         var2 = 143;
      }

      var1.mark(0);
      byte var3 = var1.readByte();
      byte var4 = var1.readByte();
      byte var5 = var1.readByte();
      byte var6 = var1.readByte();
      if (var3 == 71 && var4 == 77 && var5 == 84 && var6 == 77) {
         var2 = var1.readInt();
      } else {
         var1.reset();
      }

      this.Multiplier = var1.readFloat();
      this.NightsSurvived = var1.readInt();
      this.TargetZombies = var1.readInt();
      this.LastTimeOfDay = var1.readFloat();
      this.TimeOfDay = var1.readFloat();
      this.Day = var1.readInt();
      this.Month = var1.readInt();
      this.Year = var1.readInt();
      var1.readFloat();
      var1.readFloat();
      int var7 = var1.readInt();

      for(int var8 = 0; var8 < var7; ++var8) {
         int var9 = var1.readInt();
         SurvivorGroup var10 = new SurvivorGroup((SurvivorDesc)IsoWorld.instance.SurvivorDescriptors.get(var9));
         int var11 = var1.readInt();

         for(int var12 = 0; var12 < var11; ++var12) {
            SurvivorDesc var13 = (SurvivorDesc)IsoWorld.instance.SurvivorDescriptors.get(var1.readInt());
            if (var13 != null) {
               var10.addMember(var13);
            }
         }

         IsoWorld.instance.Groups.add(var10);
      }

      if (var1.readByte() == 1) {
         if (this.table == null) {
            this.table = LuaManager.platform.newTable();
         }

         this.table.load(var1, var2);
      }

      if (var2 >= 74) {
         Core.getInstance().setPoisonousBerry(GameWindow.ReadString(var1));
         Core.getInstance().setPoisonousMushroom(GameWindow.ReadString(var1));
      }

      if (var2 >= 90) {
         this.HelicopterDay1 = var1.readInt();
         this.HelicopterTime1Start = var1.readInt();
         this.HelicopterTime1End = var1.readInt();
      }

      if (var2 >= 135) {
         ClimateManager.getInstance().load(var1, var2);
      }

      this.setMinutesStamp();
   }

   public void load(ByteBuffer var1) throws IOException {
      short var2 = 143;
      this.Multiplier = var1.getFloat();
      this.NightsSurvived = var1.getInt();
      this.TargetZombies = var1.getInt();
      this.LastTimeOfDay = var1.getFloat();
      this.TimeOfDay = var1.getFloat();
      this.Day = var1.getInt();
      this.Month = var1.getInt();
      this.Year = var1.getInt();
      var1.getFloat();
      var1.getFloat();
      int var3 = var1.getInt();

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var1.getInt();
         SurvivorGroup var6 = new SurvivorGroup((SurvivorDesc)IsoWorld.instance.SurvivorDescriptors.get(var5));
         int var7 = var1.getInt();

         for(int var8 = 0; var8 < var7; ++var8) {
            SurvivorDesc var9 = (SurvivorDesc)IsoWorld.instance.SurvivorDescriptors.get(var1.getInt());
            if (var9 != null) {
               var6.addMember(var9);
            }
         }

         IsoWorld.instance.Groups.add(var6);
      }

      if (var1.get() == 1) {
         if (this.table == null) {
            this.table = LuaManager.platform.newTable();
         }

         this.table.load((ByteBuffer)var1, var2);
      }

      this.setMinutesStamp();
   }

   public void load() {
      File var1 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_t.bin");
      if (var1.exists()) {
         try {
            FileInputStream var2 = new FileInputStream(var1);
            if (SliceY.SliceBuffer == null) {
               SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
            }

            synchronized(SliceY.SliceBuffer) {
               SliceY.SliceBuffer.rewind();
               var2.read(SliceY.SliceBuffer.array());
            }

            var2.close();
            DataInputStream var3 = new DataInputStream(new ByteArrayInputStream(SliceY.SliceBuffer.array()));
            this.load(var3);
            var3.close();
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }

   }

   public int getDawn() {
      return this.dawn;
   }

   public void setDawn(int var1) {
      this.dawn = var1;
   }

   public int getDusk() {
      return this.dusk;
   }

   public void setDusk(int var1) {
      this.dusk = var1;
   }

   public KahluaTable getModData() {
      if (this.table == null) {
         this.table = LuaManager.platform.newTable();
      }

      return this.table;
   }

   public boolean isThunderDay() {
      return this.thunderDay;
   }

   public void setThunderDay(boolean var1) {
      this.thunderDay = var1;
   }

   public void saveToPacket(ByteBuffer var1) throws IOException {
      KahluaTable var2 = getInstance().getModData();
      Object var3 = var2.rawget("camping");
      Object var4 = var2.rawget("farming");
      Object var5 = var2.rawget("trapping");
      var2.rawset("camping", (Object)null);
      var2.rawset("farming", (Object)null);
      var2.rawset("trapping", (Object)null);
      this.save(var1);
      var2.rawset("camping", var3);
      var2.rawset("farming", var4);
      var2.rawset("trapping", var5);
   }

   private static class ThunderStormOld {
      GameTime.ThunderStormOld.State state;
      int thunderX;
      int thunderY;
      GameTime.AnimTimer timer;
      boolean bigFirstStrikeSet;
      boolean bigFirstStrike;
      boolean stop;

      private ThunderStormOld() {
         this.state = GameTime.ThunderStormOld.State.NoThunder;
         this.timer = new GameTime.AnimTimer();
      }

      public void noise(String var1) {
         if (Core.bDebug || GameServer.bServer && GameServer.bDebug) {
            DebugLog.log("thunder: " + var1);
         }

      }

      public void start() {
         this.state = GameTime.ThunderStormOld.State.Start;
         this.bigFirstStrikeSet = false;
      }

      public void start(boolean var1) {
         this.state = GameTime.ThunderStormOld.State.Start;
         this.bigFirstStrike = var1;
         this.bigFirstStrikeSet = true;
      }

      public void stop() {
         if (this.state != GameTime.ThunderStormOld.State.NoThunder) {
            this.stop = true;
         }

      }

      public void update() {
         GameTime var1 = GameTime.instance;
         float var7;
         switch(this.state) {
         case NoThunder:
            if (!GameClient.bClient && var1.isThunderDay() && IsoWorld.instance.getWeather().equals("rain") && Rand.Next(Rand.AdjustForFramerate(800)) == 0) {
               if (!GameServer.bServer) {
                  this.thunderX = IsoPlayer.instance.getCurrentSquare().getX();
                  this.thunderY = IsoPlayer.instance.getCurrentSquare().getY();
               } else {
                  if (GameServer.Players.isEmpty()) {
                     return;
                  }

                  ArrayList var9 = GameServer.getPlayers();

                  for(int var8 = var9.size() - 1; var8 >= 0; --var8) {
                     if (((IsoPlayer)var9.get(var8)).getCurrentSquare() == null) {
                        var9.remove(var8);
                     }
                  }

                  if (var9.isEmpty()) {
                     return;
                  }

                  IsoPlayer var10 = (IsoPlayer)var9.get(Rand.Next(var9.size()));
                  this.thunderX = var10.getCurrentSquare().getX();
                  this.thunderY = var10.getCurrentSquare().getY();
               }

               if (Rand.Next(2) == 0) {
                  this.thunderX -= Rand.Next(2000);
               } else {
                  this.thunderX += Rand.Next(2000);
               }

               if (Rand.Next(2) == 0) {
                  this.thunderY -= Rand.Next(2000);
               } else {
                  this.thunderY += Rand.Next(2000);
               }

               this.state = GameTime.ThunderStormOld.State.Start;
            }
            break;
         case Start:
            this.noise("started bigFirstStrikeSet=" + this.bigFirstStrikeSet);
            this.stop = false;
            if (!GameClient.bClient && !this.bigFirstStrikeSet) {
               this.bigFirstStrike = Rand.Next(3) == 0;
            }

            this.bigFirstStrikeSet = false;
            this.timer.init(5);
            this.state = GameTime.ThunderStormOld.State.Lightning1Brighter;
            LuaEventManager.triggerEvent("OnThunderStart");
            if (GameServer.bServer) {
               GameServer.sendServerCommandV("thunder", "start", "big", this.bigFirstStrike);
            }
            break;
         case Lightning1Brighter:
            this.timer.update();
            var7 = var1.TimeLerp(var1.NightMax, var1.NightMin, (float)var1.getDusk(), (float)var1.getDawn());
            var1.setNight(var7 - 0.13F * (float)this.timer.Ticks * this.timer.ratio());
            if (this.timer.finished()) {
               this.noise("first strike maximum");
               if (this.bigFirstStrike) {
                  if (GameServer.bServer) {
                     this.noise("thunder sound");
                     GameServer.PlaySoundAtEveryPlayer("event:/Meta/Thunder/Thunder", this.thunderX, this.thunderY, 100);
                  } else if (!GameClient.bClient && !Core.SoundDisabled && !IsoPlayer.instance.isDeaf() && FMODManager.instance.getNumListeners() > 0) {
                     this.noise("thunder sound");
                     long var3 = javafmod.FMOD_Studio_System_GetEvent("event:/Meta/Thunder/Thunder");
                     long var5 = javafmod.FMOD_Studio_System_CreateEventInstance(var3);
                     javafmod.FMOD_Studio_EventInstance3D(var5, (float)this.thunderX, (float)this.thunderY, 100.0F);
                     javafmod.FMOD_Studio_SetVolume(var5, SoundManager.instance.getSoundVolume());
                     javafmod.FMOD_Studio_StartEvent(var5);
                     javafmod.FMOD_Studio_ReleaseEventInstance(var5);
                  }
               }

               this.timer.init(4);
               this.state = GameTime.ThunderStormOld.State.Lightning1Darker;
            }
            break;
         case Lightning1Darker:
            this.timer.update();
            var7 = var1.TimeLerp(var1.NightMax, var1.NightMin, (float)var1.getDusk(), (float)var1.getDawn());
            var1.setNight(var1.getNight() + (float)this.timer.Ticks * 0.08F * this.timer.ratio());
            if (var1.getNight() > var7) {
               var1.setNight(var7);
            }

            if (this.timer.finished()) {
               this.noise("first strike finished");
               this.timer.init(7);
               this.state = GameTime.ThunderStormOld.State.Lightning2Brighter;
            }
            break;
         case Lightning2Brighter:
            this.timer.update();
            var7 = var1.TimeLerp(var1.NightMax, var1.NightMin, (float)var1.getDusk(), (float)var1.getDawn());
            var1.setNight(var7 - 0.14F * (float)this.timer.Ticks * this.timer.ratio());
            if (this.timer.finished()) {
               this.noise("second strike maximum");
               var1.setNight(0.0F);
               this.timer.init(30);
               this.state = GameTime.ThunderStormOld.State.Lightning2Darker;
            }
            break;
         case Lightning2Darker:
            this.timer.update();
            var7 = var1.TimeLerp(var1.NightMax, var1.NightMin, (float)var1.getDusk(), (float)var1.getDawn());
            var1.setNight(var7 * this.timer.ratio());
            if (this.timer.finished()) {
               this.noise("second strike finished");
               if (!this.bigFirstStrike) {
                  this.timer.init(120);
                  this.state = GameTime.ThunderStormOld.State.Rumble;
               } else {
                  this.timer.init(360);
                  this.state = GameTime.ThunderStormOld.State.Interval;
               }
            }
            break;
         case Rumble:
            this.timer.update();
            if (this.timer.finished()) {
               if (GameServer.bServer) {
                  this.noise("rumble sound");
                  GameServer.PlaySoundAtEveryPlayer("event:/Meta/Thunder/RumbleThunder", this.thunderX, this.thunderY, 100);
               } else if (!GameClient.bClient && !Core.SoundDisabled && !IsoPlayer.instance.isDeaf() && FMODManager.instance.getNumListeners() > 0) {
                  this.noise("rumble sound");
                  long var2 = javafmod.FMOD_Studio_System_GetEvent("event:/Meta/Thunder/RumbleThunder");
                  long var4 = javafmod.FMOD_Studio_System_CreateEventInstance(var2);
                  javafmod.FMOD_Studio_EventInstance3D(var4, (float)this.thunderX, (float)this.thunderY, 200.0F);
                  javafmod.FMOD_Studio_SetVolume(var4, SoundManager.instance.getSoundVolume());
                  javafmod.FMOD_Studio_StartEvent(var4);
                  javafmod.FMOD_Studio_ReleaseEventInstance(var4);
               }

               this.timer.init(360);
               this.state = GameTime.ThunderStormOld.State.Interval;
            }
            break;
         case Interval:
            if (!GameClient.bClient && !RainManager.isRaining() && !IsoWorld.instance.getWeather().equals("rain") || Rand.Next(Rand.AdjustForFramerate(1200)) == 0) {
               this.stop = true;
            }

            if (this.stop) {
               this.noise("ended");
               LuaEventManager.triggerEvent("OnThunderStop");
               if (GameServer.bServer) {
                  GameServer.sendServerCommandV("thunder", "stop");
               }

               this.state = GameTime.ThunderStormOld.State.NoThunder;
            } else if (!GameClient.bClient) {
               if (this.timer.finished()) {
                  if (Rand.Next(Rand.AdjustForFramerate(400)) == 0) {
                     this.state = GameTime.ThunderStormOld.State.Start;
                  }
               } else {
                  this.timer.update();
                  if (this.timer.finished()) {
                     this.noise("interval finished");
                  }
               }
            }
         }

      }

      boolean isModifyingNight() {
         return this.state == GameTime.ThunderStormOld.State.Lightning1Brighter || this.state == GameTime.ThunderStormOld.State.Lightning1Darker || this.state == GameTime.ThunderStormOld.State.Lightning2Brighter || this.state == GameTime.ThunderStormOld.State.Lightning2Darker;
      }

      static enum State {
         NoThunder,
         Start,
         Lightning1Brighter,
         Lightning1Darker,
         Lightning2Brighter,
         Lightning2Darker,
         Rumble,
         Interval;
      }
   }

   private static class ThunderStorm {
      private ThunderStorm() {
      }

      public void noise(String var1) {
         if (Core.bDebug || GameServer.bServer && GameServer.bDebug) {
            DebugLog.log("thunder: " + var1);
         }

      }

      public void start(boolean var1) {
      }

      public void stop() {
      }

      public void start() {
      }

      public void update() {
      }

      boolean isModifyingNight() {
         return false;
      }

      // $FF: synthetic method
      ThunderStorm(Object var1) {
         this();
      }
   }

   public static class AnimTimer {
      public float Elapsed;
      public float Duration;
      public boolean Finished = true;
      public int Ticks;

      public void initSeconds(int var1) {
         this.init(var1 * 60);
      }

      public void init(int var1) {
         this.Ticks = var1;
         this.Elapsed = 0.0F;
         this.Duration = (float)(var1 * 1) / 30.0F;
         this.Finished = false;
      }

      public void update() {
         this.Elapsed += GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F / 30.0F;
         if (this.Elapsed >= this.Duration) {
            this.Elapsed = this.Duration;
            this.Finished = true;
         }

      }

      public float ratio() {
         return this.Elapsed / this.Duration;
      }

      public boolean finished() {
         return this.Finished;
      }
   }
}
