package zombie.iso.weather;

import fmod.javafmod;
import fmod.fmod.FMODManager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.opengl.RenderSettings;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class ThunderStorm {
   public static int MAP_MIN_X = -3000;
   public static int MAP_MIN_Y = -3000;
   public static int MAP_MAX_X = 25000;
   public static int MAP_MAX_Y = 20000;
   private boolean hasActiveThunderClouds = false;
   private float cloudMaxRadius = 20000.0F;
   private ThunderStorm.ThunderEvent[] events = new ThunderStorm.ThunderEvent[30];
   private ThunderStorm.ThunderCloud[] clouds = new ThunderStorm.ThunderCloud[3];
   private ClimateManager climateManager;
   private ArrayList cloudCache;
   private boolean donoise = false;
   private int strikeRadius = 4000;
   private final ThunderStorm.PlayerLightningInfo[] lightningInfos = new ThunderStorm.PlayerLightningInfo[4];
   private ThunderStorm.ThunderEvent networkThunderEvent = new ThunderStorm.ThunderEvent();
   private ThunderStorm.ThunderCloud dummyCloud;

   public ArrayList getClouds() {
      if (this.cloudCache == null) {
         this.cloudCache = new ArrayList(this.clouds.length);

         for(int var1 = 0; var1 < this.clouds.length; ++var1) {
            this.cloudCache.add(this.clouds[var1]);
         }
      }

      return this.cloudCache;
   }

   public ThunderStorm(ClimateManager var1) {
      this.climateManager = var1;

      int var2;
      for(var2 = 0; var2 < this.events.length; ++var2) {
         this.events[var2] = new ThunderStorm.ThunderEvent();
      }

      for(var2 = 0; var2 < this.clouds.length; ++var2) {
         this.clouds[var2] = new ThunderStorm.ThunderCloud();
      }

      for(var2 = 0; var2 < 4; ++var2) {
         this.lightningInfos[var2] = new ThunderStorm.PlayerLightningInfo();
      }

   }

   private ThunderStorm.ThunderEvent getFreeEvent() {
      for(int var1 = 0; var1 < this.events.length; ++var1) {
         if (!this.events[var1].isRunning) {
            return this.events[var1];
         }
      }

      return null;
   }

   private ThunderStorm.ThunderCloud getFreeCloud() {
      for(int var1 = 0; var1 < this.clouds.length; ++var1) {
         if (!this.clouds[var1].isRunning) {
            return this.clouds[var1];
         }
      }

      return null;
   }

   private ThunderStorm.ThunderCloud getCloud(int var1) {
      byte var2 = 0;
      return var2 < this.clouds.length ? this.clouds[var2] : null;
   }

   public boolean HasActiveThunderClouds() {
      return this.hasActiveThunderClouds;
   }

   public void noise(String var1) {
      if (this.donoise && (Core.bDebug || GameServer.bServer && GameServer.bDebug)) {
         DebugLog.log("thunderstorm: " + var1);
      }

   }

   public void stopAllClouds() {
      for(int var1 = 0; var1 < this.clouds.length; ++var1) {
         this.stopCloud(var1);
      }

   }

   public void stopCloud(int var1) {
      ThunderStorm.ThunderCloud var2 = this.getCloud(var1);
      if (var2 != null) {
         var2.isRunning = false;
      }

   }

   private float addToAngle(float var1, float var2) {
      var1 += var2;
      if (var1 > 360.0F) {
         var1 -= 360.0F;
      } else if (var1 < 0.0F) {
         var1 += 360.0F;
      }

      return var1;
   }

   public void startThunderCloud(float var1, float var2, float var3, float var4, float var5, double var6, boolean var8) {
      if (!GameClient.bClient) {
         ThunderStorm.ThunderCloud var9 = this.getFreeCloud();
         if (var9 != null) {
            var2 = this.addToAngle(var2, Rand.Next(-10.0F, 10.0F));
            var9.startTime = GameTime.instance.getWorldAgeHours();
            var9.endTime = var9.startTime + var6;
            var9.duration = var6;
            var9.strength = ClimateManager.clamp01(var1);
            var9.angle = var2;
            var9.radius = var3;
            if (var9.radius > this.cloudMaxRadius) {
               var9.radius = this.cloudMaxRadius;
            }

            var9.eventFrequency = var4;
            var9.thunderRatio = ClimateManager.clamp01(var5);
            float var10 = this.addToAngle(var2, 180.0F);
            int var11 = MAP_MAX_X - MAP_MIN_X;
            int var12 = MAP_MAX_Y - MAP_MIN_Y;
            int var13 = (int)Math.sqrt(Math.pow((double)var11, 2.0D) + Math.pow((double)var12, 2.0D));
            var13 /= 2;
            int var14 = Rand.Next(MAP_MIN_X + var11 / 5, MAP_MAX_X - var11 / 5);
            int var15 = Rand.Next(MAP_MIN_Y + var12 / 5, MAP_MAX_Y - var12 / 5);
            int var17;
            if (var8) {
               if (!GameServer.bServer) {
                  var14 = IsoPlayer.instance.getCurrentSquare().getX();
                  var15 = IsoPlayer.instance.getCurrentSquare().getY();
               } else {
                  if (GameServer.Players.isEmpty()) {
                     return;
                  }

                  ArrayList var16 = GameServer.getPlayers();

                  for(var17 = var16.size() - 1; var17 >= 0; --var17) {
                     if (((IsoPlayer)var16.get(var17)).getCurrentSquare() == null) {
                        var16.remove(var17);
                     }
                  }

                  if (!var16.isEmpty()) {
                     IsoPlayer var22 = (IsoPlayer)var16.get(Rand.Next(var16.size()));
                     var14 = var22.getCurrentSquare().getX();
                     var15 = var22.getCurrentSquare().getY();
                  }
               }
            }

            int var21 = var13 + Rand.Next(1500, 7500);
            var17 = (int)((double)var14 + (double)var21 * Math.cos(Math.toRadians((double)var10)));
            int var18 = (int)((double)var15 + (double)var21 * Math.sin(Math.toRadians((double)var10)));
            var21 = var13 + Rand.Next(1500, 7500);
            int var19 = (int)((double)var14 + (double)var21 * Math.cos(Math.toRadians((double)var2)));
            int var20 = (int)((double)var15 + (double)var21 * Math.sin(Math.toRadians((double)var2)));
            var9.startX = var17;
            var9.startY = var18;
            var9.endX = var19;
            var9.endY = var20;
            var9.currentX = var17;
            var9.currentY = var18;
            var9.isRunning = true;
            var9.suspendTimer.init(3);
         }

      }
   }

   public void update(double var1) {
      int var3;
      if (!GameClient.bClient || GameServer.bServer) {
         this.hasActiveThunderClouds = false;

         for(var3 = 0; var3 < this.clouds.length; ++var3) {
            ThunderStorm.ThunderCloud var4 = this.clouds[var3];
            if (var4.isRunning) {
               if (var1 < var4.endTime) {
                  float var5 = (float)((var1 - var4.startTime) / var4.duration);
                  var4.currentX = (int)ClimateManager.lerp(var5, (float)var4.startX, (float)var4.endX);
                  var4.currentY = (int)ClimateManager.lerp(var5, (float)var4.startY, (float)var4.endY);
                  var4.suspendTimer.update();
                  this.hasActiveThunderClouds = true;
                  if (var4.suspendTimer.finished()) {
                     float var6 = Rand.Next(3.5F - 3.0F * var4.strength, 24.0F - 20.0F * var4.strength);
                     var4.suspendTimer.init((int)(var6 * 60.0F));
                     float var7 = Rand.Next(0.0F, 1.0F);
                     if (var7 < 0.6F) {
                        this.strikeRadius = (int)(var4.radius / 2.0F) / 3;
                     } else if (var7 < 0.9F) {
                        this.strikeRadius = (int)(var4.radius / 2.0F) / 4 * 3;
                     } else {
                        this.strikeRadius = (int)(var4.radius / 2.0F);
                     }

                     if (Rand.Next(0.0F, 1.0F) < var4.thunderRatio) {
                        this.noise("trigger thunder event");
                        this.triggerThunderEvent(Rand.Next(var4.currentX - this.strikeRadius, var4.currentX + this.strikeRadius), Rand.Next(var4.currentY - this.strikeRadius, var4.currentY + this.strikeRadius), true, true, Rand.Next(0.0F, 1.0F) > 0.4F);
                     } else {
                        this.triggerThunderEvent(Rand.Next(var4.currentX - this.strikeRadius, var4.currentX + this.strikeRadius), Rand.Next(var4.currentY - this.strikeRadius, var4.currentY + this.strikeRadius), false, false, true);
                        this.noise("trigger rumble event");
                     }
                  }
               } else {
                  var4.isRunning = false;
               }
            }
         }
      }

      if (GameClient.bClient || !GameServer.bServer) {
         for(var3 = 0; var3 < 4; ++var3) {
            ThunderStorm.PlayerLightningInfo var11 = this.lightningInfos[var3];
            if (var11.lightningState == ThunderStorm.LightningState.ApplyLightning) {
               var11.timer.update();
               if (!var11.timer.finished()) {
                  var11.lightningMod = ClimateManager.clamp01(var11.timer.ratio());
                  ClimateManager.ClimateFloat var10000 = this.climateManager.dayLightStrength;
                  var10000.finalValue += (1.0F - this.climateManager.dayLightStrength.finalValue) * (1.0F - var11.lightningMod);
                  IsoPlayer var13 = IsoPlayer.players[var3];
                  if (var13 != null) {
                     var13.dirtyRecalcGridStackTime = 1.0F;
                  }
               } else {
                  this.noise("apply lightning done.");
                  var11.timer.init(2);
                  var11.lightningStrength = 0.0F;
                  var11.lightningState = ThunderStorm.LightningState.Idle;
               }
            }
         }

         for(var3 = 0; var3 < this.events.length; ++var3) {
            ThunderStorm.ThunderEvent var12 = this.events[var3];
            if (var12.isRunning) {
               var12.soundDelay.update();
               if (var12.soundDelay.finished()) {
                  var12.isRunning = false;
                  if (!Core.SoundDisabled && !IsoPlayer.instance.isDeaf() && FMODManager.instance.getNumListeners() > 0) {
                     long var9;
                     GameSound var14;
                     GameSoundClip var15;
                     long var16;
                     if (var12.doStrike) {
                        this.noise("thunder sound");
                        var14 = GameSounds.getSound("Thunder");
                        var15 = var14 == null ? null : var14.getRandomClip();
                        if (var15 != null && var15.event != null && !var15.event.isEmpty()) {
                           var16 = javafmod.FMOD_Studio_System_GetEvent("event:/" + var15.event);
                           var9 = javafmod.FMOD_Studio_System_CreateEventInstance(var16);
                           javafmod.FMOD_Studio_EventInstance3D(var9, (float)var12.eventX, (float)var12.eventY, 100.0F);
                           javafmod.FMOD_Studio_SetVolume(var9, var15.getEffectiveVolume());
                           javafmod.FMOD_Studio_StartEvent(var9);
                           javafmod.FMOD_Studio_ReleaseEventInstance(var9);
                        }
                     }

                     if (var12.doRumble) {
                        this.noise("rumble sound");
                        var14 = GameSounds.getSound("RumbleThunder");
                        var15 = var14 == null ? null : var14.getRandomClip();
                        if (var15 != null && var15.event != null && !var15.event.isEmpty()) {
                           var16 = javafmod.FMOD_Studio_System_GetEvent("event:/" + var15.event);
                           var9 = javafmod.FMOD_Studio_System_CreateEventInstance(var16);
                           javafmod.FMOD_Studio_EventInstance3D(var9, (float)var12.eventX, (float)var12.eventY, 200.0F);
                           javafmod.FMOD_Studio_SetVolume(var9, var15.getEffectiveVolume());
                           javafmod.FMOD_Studio_StartEvent(var9);
                           javafmod.FMOD_Studio_ReleaseEventInstance(var9);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public void applyLightningForPlayer(RenderSettings.PlayerRenderSettings var1, int var2, IsoPlayer var3) {
      ThunderStorm.PlayerLightningInfo var4 = this.lightningInfos[var2];
      if (var4.lightningState == ThunderStorm.LightningState.ApplyLightning) {
         ClimateColorInfo var5 = var1.CM_GlobalLight;
         var4.lightningColor.getExterior().r = var5.getExterior().r + var4.lightningStrength * (1.0F - var5.getExterior().r);
         var4.lightningColor.getExterior().g = var5.getExterior().g + var4.lightningStrength * (1.0F - var5.getExterior().g);
         var4.lightningColor.getExterior().b = var5.getExterior().b + var4.lightningStrength * (1.0F - var5.getExterior().b);
         var4.lightningColor.getInterior().r = var5.getInterior().r + var4.lightningStrength * (1.0F - var5.getInterior().r);
         var4.lightningColor.getInterior().g = var5.getInterior().g + var4.lightningStrength * (1.0F - var5.getInterior().g);
         var4.lightningColor.getInterior().b = var5.getInterior().b + var4.lightningStrength * (1.0F - var5.getInterior().b);
         var4.lightningColor.interp(var1.CM_GlobalLight, var4.lightningMod, var4.outColor);
         var1.CM_GlobalLight.getExterior().r = var4.outColor.getExterior().r;
         var1.CM_GlobalLight.getExterior().g = var4.outColor.getExterior().g;
         var1.CM_GlobalLight.getExterior().b = var4.outColor.getExterior().b;
         var1.CM_GlobalLight.getInterior().r = var4.outColor.getInterior().r;
         var1.CM_GlobalLight.getInterior().g = var4.outColor.getInterior().g;
         var1.CM_GlobalLight.getInterior().b = var4.outColor.getInterior().b;
         var1.CM_Ambient = ClimateManager.lerp(var4.lightningMod, 1.0F, var1.CM_Ambient);
         var1.CM_DayLightStrength = ClimateManager.lerp(var4.lightningMod, 1.0F, var1.CM_DayLightStrength);
         var1.CM_Desaturation = ClimateManager.lerp(var4.lightningMod, 0.0F, var1.CM_Desaturation);
         if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
            var1.CM_GlobalLightIntensity = ClimateManager.lerp(var4.lightningMod, 1.0F, var1.CM_GlobalLightIntensity);
         } else {
            var1.CM_GlobalLightIntensity = ClimateManager.lerp(var4.lightningMod, 0.0F, var1.CM_GlobalLightIntensity);
         }
      }

   }

   public boolean isModifyingNight() {
      return false;
   }

   public void triggerThunderEvent(int var1, int var2, boolean var3, boolean var4, boolean var5) {
      if (GameServer.bServer) {
         this.networkThunderEvent.eventX = var1;
         this.networkThunderEvent.eventY = var2;
         this.networkThunderEvent.doStrike = var3;
         this.networkThunderEvent.doLightning = var4;
         this.networkThunderEvent.doRumble = var5;
         this.climateManager.transmitClimatePacket(ClimateManager.ClimateNetAuth.ServerOnly, (byte)2, (UdpConnection)null);
      } else if (!GameClient.bClient) {
         this.enqueueThunderEvent(var1, var2, var3, var4, var5);
      }

   }

   public void writeNetThunderEvent(ByteBuffer var1) throws IOException {
      var1.putInt(this.networkThunderEvent.eventX);
      var1.putInt(this.networkThunderEvent.eventY);
      var1.put((byte)(this.networkThunderEvent.doStrike ? 1 : 0));
      var1.put((byte)(this.networkThunderEvent.doLightning ? 1 : 0));
      var1.put((byte)(this.networkThunderEvent.doRumble ? 1 : 0));
   }

   public void readNetThunderEvent(ByteBuffer var1) throws IOException {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      boolean var4 = var1.get() == 1;
      boolean var5 = var1.get() == 1;
      boolean var6 = var1.get() == 1;
      this.enqueueThunderEvent(var2, var3, var4, var5, var6);
   }

   public void enqueueThunderEvent(int var1, int var2, boolean var3, boolean var4, boolean var5) {
      LuaEventManager.triggerEvent("OnThunderEvent", var1, var2, var3, var4, var5);
      if (var3 || var5) {
         int var6 = 9999999;

         for(int var7 = 0; var7 < IsoPlayer.numPlayers; ++var7) {
            IsoPlayer var8 = IsoPlayer.players[var7];
            if (var8 != null) {
               int var9 = this.GetDistance((int)var8.getX(), (int)var8.getY(), var1, var2);
               if (var9 < var6) {
                  var6 = var9;
               }

               if (var4) {
                  this.lightningInfos[var7].distance = var9;
                  this.lightningInfos[var7].x = var1;
                  this.lightningInfos[var7].y = var2;
               }
            }
         }

         this.noise("dist to player = " + var6);
         if (var6 < 10000) {
            ThunderStorm.ThunderEvent var11 = this.getFreeEvent();
            if (var11 != null) {
               var11.doRumble = var5;
               var11.doStrike = var3;
               var11.eventX = var1;
               var11.eventY = var2;
               var11.isRunning = true;
               var11.soundDelay.init((int)((float)var6 / 300.0F * 60.0F));
               if (var4) {
                  for(int var12 = 0; var12 < IsoPlayer.numPlayers; ++var12) {
                     IsoPlayer var13 = IsoPlayer.players[var12];
                     if (var13 != null && (float)this.lightningInfos[var12].distance < 7500.0F) {
                        float var10 = 1.0F - (float)this.lightningInfos[var12].distance / 7500.0F;
                        this.lightningInfos[var12].lightningState = ThunderStorm.LightningState.ApplyLightning;
                        if (var10 > this.lightningInfos[var12].lightningStrength) {
                           this.lightningInfos[var12].lightningStrength = var10;
                           this.lightningInfos[var12].timer.init(20 + (int)(80.0F * this.lightningInfos[var12].lightningStrength));
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private int GetDistance(int var1, int var2, int var3, int var4) {
      return (int)Math.sqrt(Math.pow((double)(var1 - var3), 2.0D) + Math.pow((double)(var2 - var4), 2.0D));
   }

   public void save(DataOutputStream var1) throws IOException {
      if (GameClient.bClient && !GameServer.bServer) {
         var1.writeByte(0);
      } else {
         var1.writeByte(this.clouds.length);

         for(int var2 = 0; var2 < this.clouds.length; ++var2) {
            ThunderStorm.ThunderCloud var3 = this.clouds[var2];
            var1.writeBoolean(var3.isRunning);
            if (var3.isRunning) {
               var1.writeInt(var3.startX);
               var1.writeInt(var3.startY);
               var1.writeInt(var3.endX);
               var1.writeInt(var3.endY);
               var1.writeFloat(var3.radius);
               var1.writeFloat(var3.angle);
               var1.writeFloat(var3.strength);
               var1.writeFloat(var3.thunderRatio);
               var1.writeDouble(var3.startTime);
               var1.writeDouble(var3.endTime);
               var1.writeDouble(var3.duration);
            }
         }
      }

   }

   public void load(DataInputStream var1) throws IOException {
      byte var2 = var1.readByte();
      if (var2 != 0) {
         if (var2 > this.clouds.length && this.dummyCloud == null) {
            this.dummyCloud = new ThunderStorm.ThunderCloud();
         }

         for(int var4 = 0; var4 < var2; ++var4) {
            boolean var5 = var1.readBoolean();
            ThunderStorm.ThunderCloud var3;
            if (var4 >= this.clouds.length) {
               var3 = this.dummyCloud;
            } else {
               var3 = this.clouds[var4];
            }

            var3.isRunning = var5;
            if (var5) {
               var3.startX = var1.readInt();
               var3.startY = var1.readInt();
               var3.endX = var1.readInt();
               var3.endY = var1.readInt();
               var3.radius = var1.readFloat();
               var3.angle = var1.readFloat();
               var3.strength = var1.readFloat();
               var3.thunderRatio = var1.readFloat();
               var3.startTime = var1.readDouble();
               var3.endTime = var1.readDouble();
               var3.duration = var1.readDouble();
            }
         }

      }
   }

   private class PlayerLightningInfo {
      public ThunderStorm.LightningState lightningState;
      public GameTime.AnimTimer timer;
      public float lightningStrength;
      public float lightningMod;
      public ClimateColorInfo lightningColor;
      public ClimateColorInfo outColor;
      public int x;
      public int y;
      public int distance;

      private PlayerLightningInfo() {
         this.lightningState = ThunderStorm.LightningState.Idle;
         this.timer = new GameTime.AnimTimer();
         this.lightningStrength = 1.0F;
         this.lightningMod = 0.0F;
         this.lightningColor = new ClimateColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
         this.outColor = new ClimateColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
         this.x = 0;
         this.y = 0;
         this.distance = 0;
      }

      // $FF: synthetic method
      PlayerLightningInfo(Object var2) {
         this();
      }
   }

   private static enum LightningState {
      Idle,
      ApplyLightning;
   }

   public static class ThunderCloud {
      private int currentX;
      private int currentY;
      private int startX;
      private int startY;
      private int endX;
      private int endY;
      private double startTime;
      private double endTime;
      private double duration;
      private float strength;
      private float angle;
      private float radius;
      private float eventFrequency;
      private float thunderRatio;
      private boolean isRunning = false;
      private GameTime.AnimTimer suspendTimer = new GameTime.AnimTimer();

      public int getCurrentX() {
         return this.currentX;
      }

      public int getCurrentY() {
         return this.currentY;
      }

      public float getRadius() {
         return this.radius;
      }

      public boolean isRunning() {
         return this.isRunning;
      }

      public float getStrength() {
         return this.strength;
      }

      public double lifeTime() {
         return (this.startTime - this.endTime) / this.duration;
      }
   }

   private static class ThunderEvent {
      private int eventX;
      private int eventY;
      private boolean doLightning;
      private boolean doRumble;
      private boolean doStrike;
      private GameTime.AnimTimer soundDelay;
      private boolean isRunning;

      private ThunderEvent() {
         this.doLightning = false;
         this.doRumble = false;
         this.doStrike = false;
         this.soundDelay = new GameTime.AnimTimer();
         this.isRunning = false;
      }

      // $FF: synthetic method
      ThunderEvent(Object var1) {
         this();
      }
   }
}
