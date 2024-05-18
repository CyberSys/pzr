package zombie.gameStates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import zombie.AmbientStreamManager;
import zombie.FliesSound;
import zombie.FrameLoader;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.LOSThread;
import zombie.LootRespawn;
import zombie.MapCollisionData;
import zombie.ReanimatedPlayers;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.TileAccessibilityWorker;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaManager;
import zombie.Lua.MapObjects;
import zombie.Quests.QuestManager;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.Mover;
import zombie.ai.astar.Path;
import zombie.ai.astar.heuristics.ManhattanHeuristic;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorFactory;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.traits.TraitFactory;
import zombie.chat.ChatElement;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.RenderSettings;
import zombie.core.physics.WorldSimulation;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.stash.StashSystem;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.core.utils.OnceEvery;
import zombie.core.znet.SteamFriends;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionGlobals;
import zombie.globalObjects.CGlobalObjects;
import zombie.globalObjects.SGlobalObjects;
import zombie.input.GameKeyboard;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.inventory.ItemSoundManager;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LightingThread;
import zombie.iso.LotHeader;
import zombie.iso.WorldStreamer;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.isoregion.IsoRegion;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.Temperature;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.meta.Meta;
import zombie.network.BodyDamageSync;
import zombie.network.ChunkChecksum;
import zombie.network.ClientServerMap;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PassengerMap;
import zombie.network.ServerGUI;
import zombie.network.ServerOptions;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.scripting.ScriptManager;
import zombie.spnetwork.SinglePlayerClient;
import zombie.spnetwork.SinglePlayerServer;
import zombie.ui.ActionProgressBar;
import zombie.ui.FPSGraph;
import zombie.ui.ServerPulseGraph;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.TutorialManager;
import zombie.ui.UIManager;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleCache;
import zombie.vehicles.VehicleIDMap;

public class IngameState extends GameState {
   public static boolean DebugPathfinding = false;
   public static boolean AlwaysDebugPathfinding = false;
   public static int WaitMul = 20;
   public static IngameState instance;
   static int last = -1;
   public long numberTicks = 0L;
   public boolean Paused = false;
   public float SaveDelay = 0.0F;
   boolean alt = false;
   static float xPos;
   static float yPos;
   static float offx;
   static float offy;
   static float zoom;
   public static float draww;
   public static float drawh;
   private float SadisticMusicDirectorTime;
   public static Long GameID = 0L;
   static HashMap ContainerTypes = new HashMap();
   int insanityScareCount = 5;
   boolean MDebounce = false;
   static int nSaveCycle = 1800;
   static boolean bDoChars = false;
   int insanitypic = -1;
   int timesincelastinsanity = 10000000;
   AStarPathFinder finder;
   boolean dbgChunkKeyDown = false;
   GameState RedirectState = null;
   boolean bDidServerDisconnectState = false;
   boolean fpsKeyDown = false;
   ArrayList debugTimes = new ArrayList();
   int tickCount = 0;
   public static ArrayList Frames = new ArrayList();
   OnceEvery replayUpdate = new OnceEvery(0.1F, false);
   int nReplay = 0;

   public IngameState() {
      instance = this;
   }

   public static void renderDebugOverhead(IsoCell var0, int var1, int var2, int var3, int var4) {
      Mouse.update();
      int var5 = Mouse.getX();
      int var6 = Mouse.getY();
      var5 -= var3;
      var6 -= var4;
      var5 /= var2;
      var6 /= var2;
      SpriteRenderer.instance.render((Texture)null, var3, var4, var2 * var0.getWidthInTiles(), var2 * var0.getHeightInTiles(), 0.7F, 0.7F, 0.7F, 1.0F);
      IsoGridSquare var7 = var0.getGridSquare(var5 + var0.ChunkMap[0].getWorldXMinTiles(), var6 + var0.ChunkMap[0].getWorldYMinTiles(), 0);
      int var13;
      if (var7 != null) {
         EnumSet var8 = var7.getProperties().getFlags();
         byte var9 = 48;
         byte var10 = 48;
         TextManager.instance.DrawString((double)var10, (double)var9, "SQUARE FLAGS", 1.0D, 1.0D, 1.0D, 1.0D);
         var13 = var9 + 20;
         int var14 = var10 + 8;

         int var11;
         for(var11 = 0; var11 < 64; ++var11) {
            if (var8.contains(var11)) {
               TextManager.instance.DrawString((double)var14, (double)var13, IsoFlagType.fromIndex(var11).toString(), 0.6D, 0.6D, 0.8D, 1.0D);
               var13 += 18;
            }
         }

         var10 = 48;
         var13 += 16;
         TextManager.instance.DrawString((double)var10, (double)var13, "SQUARE OBJECT TYPES", 1.0D, 1.0D, 1.0D, 1.0D);
         var13 += 20;
         var14 = var10 + 8;

         for(var11 = 0; var11 < 64; ++var11) {
            if (var7.getHasTypes().isSet(var11)) {
               TextManager.instance.DrawString((double)var14, (double)var13, IsoObjectType.fromIndex(var11).toString(), 0.6D, 0.6D, 0.8D, 1.0D);
               var13 += 18;
            }
         }
      }

      for(int var12 = 0; var12 < var0.getWidthInTiles(); ++var12) {
         for(var13 = 0; var13 < var0.getHeightInTiles(); ++var13) {
            IsoGridSquare var15 = var0.getGridSquare(var12 + var0.ChunkMap[0].getWorldXMinTiles(), var13 + var0.ChunkMap[0].getWorldYMinTiles(), var1);
            if (var15 != null) {
               if (!var15.getProperties().Is(IsoFlagType.solid) && !var15.getProperties().Is(IsoFlagType.solidtrans)) {
                  if (!var15.getProperties().Is(IsoFlagType.exterior)) {
                     SpriteRenderer.instance.render((Texture)null, var3 + var12 * var2, var4 + var13 * var2, var2, var2, 0.8F, 0.8F, 0.8F, 1.0F);
                  }
               } else {
                  SpriteRenderer.instance.render((Texture)null, var3 + var12 * var2, var4 + var13 * var2, var2, var2, 0.5F, 0.5F, 0.5F, 255.0F);
               }

               if (var15.Has(IsoObjectType.tree)) {
                  SpriteRenderer.instance.render((Texture)null, var3 + var12 * var2, var4 + var13 * var2, var2, var2, 0.4F, 0.8F, 0.4F, 1.0F);
               }

               if (var15.getProperties().Is(IsoFlagType.collideN)) {
                  SpriteRenderer.instance.render((Texture)null, var3 + var12 * var2, var4 + var13 * var2, var2, 1, 0.2F, 0.2F, 0.2F, 1.0F);
               }

               if (var15.getProperties().Is(IsoFlagType.collideW)) {
                  SpriteRenderer.instance.render((Texture)null, var3 + var12 * var2, var4 + var13 * var2, 1, var2, 0.2F, 0.2F, 0.2F, 1.0F);
               }
            }
         }
      }

   }

   public static float translatePointX(float var0, float var1, float var2, float var3) {
      var0 -= var1;
      var0 *= var2;
      var0 += var3;
      var0 += draww / 2.0F;
      return var0;
   }

   public static float invTranslatePointX(float var0, float var1, float var2, float var3) {
      var0 -= draww / 2.0F;
      var0 -= var3;
      var0 /= var2;
      var0 += var1;
      return var0;
   }

   public static float invTranslatePointY(float var0, float var1, float var2, float var3) {
      var0 -= drawh / 2.0F;
      var0 -= var3;
      var0 /= var2;
      var0 += var1;
      return var0;
   }

   public static float translatePointY(float var0, float var1, float var2, float var3) {
      var0 -= var1;
      var0 *= var2;
      var0 += var3;
      var0 += drawh / 2.0F;
      return var0;
   }

   public static float translatePointX(float var0) {
      return translatePointX(var0, xPos, zoom, offx);
   }

   public static float translatePointY(float var0) {
      return translatePointY(var0, yPos, zoom, offy);
   }

   public static void renderRect(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = translatePointX(var0, xPos, zoom, offx);
      float var9 = translatePointY(var1, yPos, zoom, offy);
      float var10 = translatePointX(var0 + var2, xPos, zoom, offx);
      float var11 = translatePointY(var1 + var3, yPos, zoom, offy);
      var2 = var10 - var8;
      var3 = var11 - var9;
      if (!(var8 >= (float)Core.getInstance().getScreenWidth()) && !(var10 < 0.0F) && !(var9 >= (float)Core.getInstance().getScreenHeight()) && !(var11 < 0.0F)) {
         SpriteRenderer.instance.render((Texture)null, var8, var9, var2, var3, var4, var5, var6, var7);
      }
   }

   public static void renderLine(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = translatePointX(var0, xPos, zoom, offx);
      float var9 = translatePointY(var1, yPos, zoom, offy);
      float var10 = translatePointX(var2, xPos, zoom, offx);
      float var11 = translatePointY(var3, yPos, zoom, offy);
      if ((!(var8 >= (float)Core.getInstance().getScreenWidth()) || !(var10 >= (float)Core.getInstance().getScreenWidth())) && (!(var9 >= (float)Core.getInstance().getScreenHeight()) || !(var11 >= (float)Core.getInstance().getScreenHeight())) && (!(var8 < 0.0F) || !(var10 < 0.0F)) && (!(var9 < 0.0F) || !(var11 < 0.0F))) {
         SpriteRenderer.instance.renderline((Texture)null, (int)var8, (int)var9, (int)var10, (int)var11, var4, var5, var6, var7);
      }
   }

   public static void renderDebugOverhead2(IsoCell var0, int var1, float var2, int var3, int var4, float var5, float var6, int var7, int var8) {
      draww = (float)var7;
      drawh = (float)var8;
      xPos = var5;
      yPos = var6;
      offx = (float)var3;
      offy = (float)var4;
      zoom = var2;
      float var9 = (float)var0.ChunkMap[0].getWorldXMinTiles();
      float var10 = (float)var0.ChunkMap[0].getWorldYMinTiles();
      float var11 = (float)var0.ChunkMap[0].getWorldXMaxTiles();
      float var12 = (float)var0.ChunkMap[0].getWorldYMaxTiles();
      renderRect(var9, var10, (float)var0.getWidthInTiles(), (float)var0.getWidthInTiles(), 0.7F, 0.7F, 0.7F, 1.0F);

      int var14;
      for(int var13 = 0; var13 < var0.getWidthInTiles(); ++var13) {
         for(var14 = 0; var14 < var0.getHeightInTiles(); ++var14) {
            IsoGridSquare var15 = var0.getGridSquare(var13 + var0.ChunkMap[0].getWorldXMinTiles(), var14 + var0.ChunkMap[0].getWorldYMinTiles(), var1);
            float var16 = (float)var13 + var9;
            float var17 = (float)var14 + var10;
            if (var15 != null) {
               if (!var15.getProperties().Is(IsoFlagType.solid) && !var15.getProperties().Is(IsoFlagType.solidtrans)) {
                  if (!var15.getProperties().Is(IsoFlagType.exterior)) {
                     renderRect(var16, var17, 1.0F, 1.0F, 0.8F, 0.8F, 0.8F, 1.0F);
                  }
               } else {
                  renderRect(var16, var17, 1.0F, 1.0F, 0.5F, 0.5F, 0.5F, 1.0F);
               }

               if (var15.Has(IsoObjectType.tree)) {
                  renderRect(var16, var17, 1.0F, 1.0F, 0.4F, 0.8F, 0.4F, 1.0F);
               }

               if (var15.getProperties().Is(IsoFlagType.collideN)) {
                  renderRect(var16, var17, 1.0F, 0.2F, 0.2F, 0.2F, 0.2F, 1.0F);
               }

               if (var15.getProperties().Is(IsoFlagType.collideW)) {
                  renderRect(var16, var17, 0.2F, 1.0F, 0.2F, 0.2F, 0.2F, 1.0F);
               }
            }
         }
      }

      IsoMetaGrid var20 = IsoWorld.instance.MetaGrid;
      renderRect((float)(var20.minX * 300), (float)(var20.minY * 300), (float)(var20.getWidth() * 300), (float)(var20.getHeight() * 300), 1.0F, 1.0F, 1.0F, 0.05F);
      if ((double)var2 > 0.1D) {
         for(var14 = var20.minY; var14 <= var20.maxY; ++var14) {
            renderLine((float)(var20.minX * 300), (float)(var14 * 300), (float)((var20.maxX + 1) * 300), (float)(var14 * 300), 1.0F, 1.0F, 1.0F, 0.15F);
         }

         for(var14 = var20.minX; var14 <= var20.maxX; ++var14) {
            renderLine((float)(var14 * 300), (float)(var20.minY * 300), (float)(var14 * 300), (float)((var20.maxY + 1) * 300), 1.0F, 1.0F, 1.0F, 0.15F);
         }
      }

      IsoMetaCell[][] var22 = IsoWorld.instance.MetaGrid.Grid;

      for(int var21 = 0; var21 < var22.length; ++var21) {
         for(int var23 = 0; var23 < var22[0].length; ++var23) {
            LotHeader var24 = var22[var21][var23].info;
            if (var24 != null) {
               for(int var18 = 0; var18 < var24.Buildings.size(); ++var18) {
                  BuildingDef var19 = (BuildingDef)var24.Buildings.get(var18);
                  if (!((BuildingDef)var24.Buildings.get(var18)).isAllExplored() && var19.bAlarmed) {
                     renderRect((float)var19.getX(), (float)var19.getY(), (float)var19.getW(), (float)var19.getH(), 0.8F, 0.8F, 0.5F, 0.3F);
                  } else {
                     renderRect((float)var19.getX(), (float)var19.getY(), (float)var19.getW(), (float)var19.getH(), 0.5F, 0.5F, 0.8F, 0.3F);
                  }
               }
            }
         }
      }

   }

   public void debugFullyStreamedIn(int var1, int var2) {
      IsoGridSquare var3 = IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, 0);
      if (var3 != null) {
         if (var3.getBuilding() != null) {
            BuildingDef var4 = var3.getBuilding().getDef();
            if (var4 != null) {
               boolean var5 = var4.isFullyStreamedIn();

               for(int var7 = 0; var7 < var4.overlappedChunks.size(); var7 += 2) {
                  short var8 = var4.overlappedChunks.get(var7);
                  short var9 = var4.overlappedChunks.get(var7 + 1);
                  if (var5) {
                     renderRect((float)(var8 * 10), (float)(var9 * 10), 10.0F, 10.0F, 0.0F, 1.0F, 0.0F, 0.5F);
                  } else {
                     renderRect((float)(var8 * 10), (float)(var9 * 10), 10.0F, 10.0F, 1.0F, 0.0F, 0.0F, 0.5F);
                  }
               }

            }
         }
      }
   }

   public void UpdateStuff() {
      GameClient.bIngame = true;
      OnceEvery.FPS = PerformanceSettings.LockFPS;
      this.SaveDelay += GameTime.instance.getMultiplier();
      if (this.SaveDelay / 60.0F > 30.0F) {
         this.SaveDelay = 0.0F;
      }

      GameTime.instance.LastLastTimeOfDay = GameTime.instance.getLastTimeOfDay();
      GameTime.instance.setLastTimeOfDay(GameTime.getInstance().getTimeOfDay());
      boolean var1 = false;
      if (!GameServer.bServer && IsoPlayer.getInstance() != null) {
         var1 = IsoPlayer.allPlayersAsleep();
      }

      GameTime.getInstance().update(var1 && UIManager.getFadeAlpha() == 1.0D);
      if (!this.Paused) {
         ScriptManager.instance.update();
      }

      if (!this.Paused) {
         long var2 = System.nanoTime();

         try {
            WorldSoundManager.instance.update();
         } catch (Exception var15) {
            ExceptionLogger.logException(var15);
         }

         try {
            IsoFireManager.Update();
         } catch (Exception var14) {
            ExceptionLogger.logException(var14);
         }

         try {
            if (!FrameLoader.bClient) {
               RainManager.Update();
            }
         } catch (Exception var13) {
            ExceptionLogger.logException(var13);
         }

         try {
            QuestManager.instance.Update();
         } catch (Exception var12) {
            ExceptionLogger.logException(var12);
         }

         Meta.instance.update();

         try {
            VirtualZombieManager.instance.update();
            MapCollisionData.instance.updateMain();
            ZombiePopulationManager.instance.updateMain();
            PolygonalMap2.instance.updateMain();
         } catch (Exception var10) {
            ExceptionLogger.logException(var10);
         } catch (Error var11) {
            var11.printStackTrace();
         }

         try {
            LootRespawn.update();
         } catch (Exception var9) {
            ExceptionLogger.logException(var9);
         }

         if (GameServer.bServer) {
            try {
               AmbientStreamManager.instance.update();
            } catch (Exception var8) {
               ExceptionLogger.logException(var8);
            }
         }

         if (GameClient.bClient) {
            try {
               BodyDamageSync.instance.update();
            } catch (Exception var7) {
               ExceptionLogger.logException(var7);
            }
         }

         if (!GameServer.bServer) {
            try {
               ItemSoundManager.update();
               FliesSound.instance.update();
               this.SadisticMusicDirectorTime += GameTime.getInstance().getGameWorldSecondsSinceLastUpdate();
               if (this.SadisticMusicDirectorTime > 20.0F) {
                  LuaManager.call("SadisticMusicDirectorTick", (Object)null);
                  this.SadisticMusicDirectorTime = 0.0F;
               }
            } catch (Exception var6) {
               ExceptionLogger.logException(var6);
            }
         }

         RenderSettings.getInstance().update();
         long var4 = System.nanoTime();
      }

   }

   public void enter() {
      boolean var10000;
      label63: {
         if (Core.getInstance().supportsFBO()) {
            Core.getInstance();
            if (Core.OptionUIFBO) {
               var10000 = true;
               break label63;
            }
         }

         var10000 = false;
      }

      UIManager.useUIFBO = var10000;
      if (!Core.getInstance().getUseShaders()) {
         Core.getInstance().RenderShader = null;
      }

      GameSounds.fix3DListenerPosition(false);
      IsoPlayer.instance.setModel(IsoPlayer.instance.isFemale() ? "kate" : "male");
      IsoPlayer.instance.updateUsername();
      IsoPlayer.instance.getInventory().addItemsToProcessItems();
      Core.getInstance().CalcCircle();
      GameID = (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);
      GameID = GameID + (long)Rand.Next(10000000);

      try {
         MapCollisionData.instance.updateMain();
         ZombiePopulationManager.instance.updateMain();
      } catch (Exception var3) {
         ExceptionLogger.logException(var3);
      } catch (Error var4) {
         ExceptionLogger.logException(var4);
      }

      if (!GameServer.bServer) {
         IsoWorld.instance.CurrentCell.ChunkMap[0].processAllLoadGridSquare();
         IsoWorld.instance.CurrentCell.ChunkMap[0].update();
         if (!GameClient.bClient) {
            LightingThread.instance.GameLoadingUpdate();
         }
      }

      IsoWorld.instance.CurrentCell.putInVehicle(IsoPlayer.instance);
      ClimateManager.getInstance().update();
      LuaEventManager.triggerEvent("OnGameStart");
      LuaEventManager.triggerEvent("OnLoad");
      if (GameClient.bClient) {
         GameClient.instance.sendPlayerConnect(IsoPlayer.instance);
         DebugLog.log("Waiting for player-connect response from server");

         for(; IsoPlayer.instance.OnlineID == -1; GameClient.instance.update()) {
            try {
               Thread.sleep(10L);
            } catch (InterruptedException var2) {
               var2.printStackTrace();
            }
         }

         ClimateManager.getInstance().update();
         LightingThread.instance.GameLoadingUpdate();
      }

      if (GameClient.bClient && SteamUtils.isSteamModeEnabled()) {
         SteamFriends.UpdateRichPresenceConnectionInfo("In game", "+connect " + GameClient.ip + ":" + GameClient.port);
      }

   }

   public void exit() {
      if (SteamUtils.isSteamModeEnabled()) {
         SteamFriends.UpdateRichPresenceConnectionInfo("", "");
      }

      UIManager.useUIFBO = false;
      if (ServerPulseGraph.instance != null) {
         ServerPulseGraph.instance.setVisible(false);
      }

      if (FPSGraph.instance != null) {
         FPSGraph.instance.setVisible(false);
      }

      UIManager.updateBeforeFadeOut();
      long var1 = Calendar.getInstance().getTimeInMillis();
      boolean var3 = UIManager.useUIFBO;
      UIManager.useUIFBO = false;

      while(true) {
         float var4 = Math.min(1.0F, (float)(Calendar.getInstance().getTimeInMillis() - var1) / 500.0F);
         boolean var5 = true;

         int var6;
         for(var6 = 0; var6 < IsoPlayer.numPlayers; ++var6) {
            if (IsoPlayer.players[var6] != null) {
               IsoPlayer.instance = IsoPlayer.players[var6];
               IsoCamera.CamCharacter = IsoPlayer.players[var6];
               IsoSprite.globalOffsetX = -1;
               Core.getInstance().StartFrame(var6, var5);
               IsoCamera.frameState.set(var6);
               IsoWorld.instance.render();
               Core.getInstance().EndFrame(var6);
               var5 = false;
            }
         }

         Core.getInstance().RenderOffScreenBuffer();
         Core.getInstance().StartFrameUI();
         UIManager.render();
         UIManager.DrawTexture(UIManager.getBlack(), 0.0D, 0.0D, (double)Core.getInstance().getScreenWidth(), (double)Core.getInstance().getScreenHeight(), (double)var4);
         Core.getInstance().EndFrameUI();
         if (var4 >= 1.0F) {
            UIManager.useUIFBO = var3;

            while(WorldStreamer.instance.isBusy()) {
               try {
                  Thread.sleep(1L);
               } catch (InterruptedException var9) {
                  var9.printStackTrace();
               }
            }

            WorldStreamer.instance.stop();
            LightingThread.instance.stop();
            MapCollisionData.instance.stop();
            ZombiePopulationManager.instance.stop();
            PolygonalMap2.instance.stop();

            int var11;
            for(var11 = 0; var11 < IsoWorld.instance.CurrentCell.ChunkMap.length; ++var11) {
               IsoChunkMap var12 = IsoWorld.instance.CurrentCell.ChunkMap[var11];

               for(var6 = 0; var6 < IsoChunkMap.ChunkGridWidth * IsoChunkMap.ChunkGridWidth; ++var6) {
                  IsoChunk var7 = var12.getChunk(var6 % IsoChunkMap.ChunkGridWidth, var6 / IsoChunkMap.ChunkGridWidth);
                  if (var7 != null && var7.refs.contains(var12)) {
                     var7.refs.remove(var12);
                     if (var7.refs.isEmpty()) {
                        var7.removeFromWorld();
                        var7.doReuseGridsquares();
                     }
                  }
               }
            }

            ModelManager.instance.Reset();

            for(var11 = 0; var11 < 4; ++var11) {
               IsoPlayer.players[var11] = null;
            }

            IsoPlayer.numPlayers = 1;
            Core.getInstance().OffscreenBuffer.destroy();
            WeatherFxMask.destroy();
            IsoRegion.reset();
            Temperature.reset();
            ZomboidRadio.getInstance().Reset();
            ErosionGlobals.Reset();
            IsoGenerator.Reset();
            StashSystem.Reset();
            LootRespawn.Reset();
            VehicleCache.Reset();
            VehicleIDMap.instance.Reset();
            IsoWorld.instance.KillCell();
            ItemSoundManager.Reset();
            IsoChunk.Reset();
            ChunkChecksum.Reset();
            ClientServerMap.Reset();
            SinglePlayerClient.Reset();
            SinglePlayerServer.Reset();
            PassengerMap.Reset();
            WorldStreamer.instance = new WorldStreamer();
            WorldSimulation.instance.destroy();
            WorldSimulation.instance = new WorldSimulation();
            VirtualZombieManager.instance = new VirtualZombieManager();
            ReanimatedPlayers.instance = new ReanimatedPlayers();
            ScriptManager.instance.Reset();
            GameSounds.Reset();
            LuaEventManager.Reset();
            MapObjects.Reset();
            CGlobalObjects.Reset();
            SGlobalObjects.Reset();
            AmbientStreamManager.instance.stop();
            SoundManager.instance.stop();
            IsoPlayer.instance = null;
            IsoCamera.CamCharacter = null;
            TutorialManager.instance.StealControl = false;
            UIManager.init();
            ScriptManager.instance.Reset();
            GameSounds.Reset();
            SurvivorFactory.Reset();
            ProfessionFactory.Reset();
            TraitFactory.Reset();
            ChooseGameInfo.Reset();
            LuaHookManager.Reset();
            LuaManager.init();
            JoypadManager.instance.Reset();
            GameKeyboard.doLuaKeyPressed = true;
            GameWindow.ActivatedJoyPad = null;
            GameWindow.OkToSaveOnExit = false;
            GameWindow.bLoadedAsClient = false;
            Core.bLastStand = false;
            Core.bTutorial = false;
            Core.getInstance().setChallenge(false);
            Core.getInstance().setForceSnow(false);
            Core.getInstance().setZombieGroupSound(true);
            SystemDisabler.Reset();
            Texture.nullTextures.clear();
            ZomboidFileSystem.instance.Reset();
            ZomboidFileSystem.instance.init();
            Core.OptionModsEnabled = true;
            ZomboidFileSystem.instance.loadMods();
            ScriptManager.instance.Load();

            try {
               LuaManager.LoadDirBase();
            } catch (Exception var8) {
               ExceptionLogger.logException(var8);
            }

            ZomboidGlobals.Load();
            LuaEventManager.triggerEvent("OnGameBoot");
            LOSThread.instance.finished = true;
            SoundManager.instance.resumeSoundAndMusic();
            IsoPlayer[] var13 = IsoPlayer.players;
            int var14 = var13.length;

            for(var6 = 0; var6 < var14; ++var6) {
               IsoPlayer var15 = var13[var6];
               if (var15 != null) {
                  var15.dirtyRecalcGridStack = true;
               }
            }

            return;
         }

         try {
            Thread.sleep(33L);
         } catch (Exception var10) {
         }
      }
   }

   public GameState redirectState() {
      if (this.RedirectState != null) {
         GameState var1 = this.RedirectState;
         this.RedirectState = null;
         return var1;
      } else {
         return new MainScreenState();
      }
   }

   public void reenter() {
   }

   public void FadeIn(int var1) {
      UIManager.FadeIn((double)var1);
   }

   public void FadeOut(int var1) {
      UIManager.FadeOut((double)var1);
   }

   public void renderframetext(int var1) {
      IndieGL.disableAlphaTest();
      IndieGL.glDisable(2929);
      ActionProgressBar var2 = UIManager.getProgressBar((double)var1);
      if (var2 != null && var2.isVisible()) {
         var2.render();
      }

      TextDrawObject.RenderBatch(var1);
      ChatElement.RenderBatch(var1);

      try {
         Core.getInstance().EndFrameText(var1);
      } catch (Exception var4) {
      }

   }

   public void renderframe(int var1) {
      if (IsoPlayer.instance == null) {
         IsoPlayer.instance = IsoPlayer.players[0];
         IsoCamera.CamCharacter = IsoPlayer.instance;
      }

      RenderSettings.getInstance().applyRenderSettings(var1);
      ActionProgressBar var2 = UIManager.getProgressBar((double)var1);
      if (var2 != null) {
         if (UIManager.getProgressBar((double)var1).getValue() > 0.0F && UIManager.getProgressBar((double)var1).getValue() < 1.0F) {
            float var3 = IsoUtils.XToScreen(IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY(), IsoPlayer.getInstance().getZ(), 0);
            float var4 = IsoUtils.YToScreen(IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY(), IsoPlayer.getInstance().getZ(), 0);
            var3 = var3 - IsoCamera.getOffX() - IsoPlayer.instance.offsetX;
            var4 = var4 - IsoCamera.getOffY() - IsoPlayer.instance.offsetY;
            var4 -= (float)(128 / (2 / Core.TileScale));
            var3 /= Core.getInstance().getZoom(var1);
            var4 /= Core.getInstance().getZoom(var1);
            var3 -= UIManager.getProgressBar((double)var1).width / 2.0F;
            var4 -= UIManager.getProgressBar((double)var1).height;
            IsoPlayer var5 = IsoPlayer.players[var1];
            if (var5 != null && var5.getUserNameHeight() > 0) {
               var4 -= (float)(var5.getUserNameHeight() + 2);
            }

            UIManager.getProgressBar((double)var1).setX((double)var3);
            UIManager.getProgressBar((double)var1).setY((double)var4);
            UIManager.getProgressBar((double)var1).setVisible(true);
            var2.delayHide = 2;
         } else if (var2.isVisible() && var2.delayHide > 0 && --var2.delayHide == 0) {
            var2.setVisible(false);
         }
      }

      IndieGL.disableAlphaTest();
      IndieGL.glDisable(2929);
      if (IsoPlayer.instance != null && !IsoPlayer.instance.isAsleep() || UIManager.getFadeAlpha((double)var1) < 1.0F) {
         long var7 = System.nanoTime();
         IsoWorld.instance.render();
         RenderSettings.getInstance().legacyPostRender(var1);
         LuaEventManager.triggerEvent("OnPostRender");
      }

      WorldSoundManager.instance.render();
      if (GameClient.bClient) {
         ClientServerMap.render(var1);
         PassengerMap.render(var1);
      }

      LineDrawer.drawLines();

      try {
         Core.getInstance().EndFrame(var1);
      } catch (Exception var6) {
      }

   }

   public void renderframeui() {
      if (Core.getInstance().StartFrameUI()) {
         TextManager.instance.DrawTextFromGameWorld();
         UIManager.render();
         ZomboidRadio.getInstance().render();
         int var11;
         if (FrameLoader.bClient) {
            short var1 = 150;
            int var2 = Core.getInstance().getOffscreenWidth(0) - 20;
            TextManager.instance.DrawStringRight((double)var2, (double)var1, IsoPlayer.getInstance().getDescriptor().getForename() + " " + IsoPlayer.getInstance().getDescriptor().getSurname() + " - " + IsoPlayer.getInstance().getPing(), 1.0D, 1.0D, 1.0D, 1.0D);
            var11 = var1 + 12;

            for(int var3 = 0; var3 < IsoWorld.instance.CurrentCell.getRemoteSurvivorList().size(); ++var3) {
               IsoSurvivor var4 = (IsoSurvivor)IsoWorld.instance.CurrentCell.getRemoteSurvivorList().get(var3);
               TextManager.instance.DrawStringRight((double)var2, (double)var11, var4.getDescriptor().getForename() + " " + var4.getDescriptor().getSurname() + " - " + var4.ping, 1.0D, 1.0D, 1.0D, 1.0D);
               var11 += 12;
            }
         }

         if (IsoWorld.instance.TotalSurvivorsDead > 0) {
         }

         if (Core.bDebug && IsoPlayer.instance != null && IsoPlayer.instance.GhostMode) {
            IsoWorld.instance.CurrentCell.ChunkMap[0].drawDebugChunkMap();
         }

         DeadBodyAtlas.instance.renderUI();
         if (GameClient.bClient && GameClient.accessLevel.equals("admin")) {
            if (ServerPulseGraph.instance == null) {
               ServerPulseGraph.instance = new ServerPulseGraph();
            }

            ServerPulseGraph.instance.update();
            ServerPulseGraph.instance.render();
         }

         if (Core.bDebug) {
            if (GameKeyboard.isKeyDown(Core.getInstance().getKey("Display FPS"))) {
               if (!this.fpsKeyDown) {
                  this.fpsKeyDown = true;
                  if (FPSGraph.instance == null) {
                     FPSGraph.instance = new FPSGraph();
                  }

                  FPSGraph.instance.setVisible(!FPSGraph.instance.isVisible());
               }
            } else {
               this.fpsKeyDown = false;
            }

            if (FPSGraph.instance != null) {
               FPSGraph.instance.render();
            }
         }

         if (!GameServer.bServer) {
            for(var11 = 0; var11 < IsoPlayer.numPlayers; ++var11) {
               IsoPlayer var12 = IsoPlayer.players[var11];
               if (var12 != null && !var12.isDead() && var12.isAsleep()) {
                  float var13 = GameClient.bFastForward ? GameTime.getInstance().ServerTimeOfDay : GameTime.getInstance().getTimeOfDay();
                  float var14 = (var13 - (float)((int)var13)) * 60.0F;
                  String var5 = "media/ui/SleepClock" + (int)var14 / 10 + ".png";
                  Texture var6 = Texture.getSharedTexture(var5);
                  if (var6 == null) {
                     break;
                  }

                  int var7 = IsoCamera.getScreenLeft(var11);
                  int var8 = IsoCamera.getScreenTop(var11);
                  int var9 = IsoCamera.getScreenWidth(var11);
                  int var10 = IsoCamera.getScreenHeight(var11);
                  SpriteRenderer.instance.render(var6, var7 + var9 / 2 - var6.getWidth() / 2, var8 + var10 / 2 - var6.getHeight() / 2, var6.getWidth(), var6.getHeight(), 1.0F, 1.0F, 1.0F, 1.0F);
               }
            }
         }
      }

      Core.getInstance().EndFrameUI();
   }

   public void render() {
      IsoZombie.HighQualityZombiesDrawnThisFrame = 0;
      if (!AlwaysDebugPathfinding) {
         boolean var1 = true;

         int var2;
         for(var2 = 0; var2 < IsoPlayer.numPlayers; ++var2) {
            if (IsoPlayer.players[var2] == null) {
               if (var2 == 0) {
                  SpriteRenderer.instance.preRender();
               }
            } else {
               IsoPlayer.instance = IsoPlayer.players[var2];
               IsoCamera.CamCharacter = IsoPlayer.players[var2];
               Core.getInstance().StartFrame(var2, var1);
               IsoCamera.frameState.set(var2);
               var1 = false;
               IsoSprite.globalOffsetX = -1;
               this.renderframe(var2);
            }
         }

         Core.getInstance().RenderOffScreenBuffer();

         for(var2 = 0; var2 < IsoPlayer.numPlayers; ++var2) {
            if (IsoPlayer.players[var2] != null) {
               Core.getInstance().StartFrameText(var2);
               this.renderframetext(var2);
            }
         }

         UIManager.resize();
         this.renderframeui();
      }
   }

   public void StartMusic() {
   }

   public void StartMusic(String var1) {
   }

   public GameStateMachine.StateAction update() {
      ++this.tickCount;
      if (this.tickCount < 60) {
         for(int var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
            if (IsoPlayer.players[var1] != null) {
               IsoPlayer.players[var1].dirtyRecalcGridStackTime = 20.0F;
            }
         }
      }

      long var10 = System.nanoTime();
      LuaEventManager.triggerEvent("OnTickEvenPaused", (double)this.numberTicks);
      if (Core.bDebug) {
         this.debugTimes.clear();
         this.debugTimes.add(System.nanoTime());
      }

      if (Core.bExiting) {
         Core.bExiting = false;
         if (GameClient.bClient) {
            WorldStreamer.instance.stop();
            GameClient.instance.doDisconnect("Quitting");
         }

         try {
            GameWindow.save(true);
         } catch (FileNotFoundException var6) {
            ExceptionLogger.logException(var6);
         } catch (IOException var7) {
            ExceptionLogger.logException(var7);
         }

         try {
            LuaEventManager.triggerEvent("OnPostSave");
         } catch (Exception var5) {
            var5.printStackTrace();
         }

         return GameStateMachine.StateAction.Continue;
      } else if (GameWindow.bServerDisconnected) {
         TutorialManager.instance.StealControl = true;
         if (!this.bDidServerDisconnectState) {
            this.bDidServerDisconnectState = true;
            this.RedirectState = new ServerDisconnectState();
            return GameStateMachine.StateAction.Yield;
         } else {
            GameClient.connection = null;
            GameClient.instance.bConnected = false;
            GameClient.bClient = false;
            GameWindow.bServerDisconnected = false;
            return GameStateMachine.StateAction.Continue;
         }
      } else {
         if (Core.bDebug) {
            if (GameKeyboard.isKeyDown(60)) {
               if (!this.dbgChunkKeyDown) {
                  this.dbgChunkKeyDown = true;
                  this.RedirectState = new DebugChunkState();
                  return GameStateMachine.StateAction.Yield;
               }
            } else {
               this.dbgChunkKeyDown = false;
            }
         }

         if (Core.bDebug) {
            this.debugTimes.add(System.nanoTime());
         }

         if (this.finder == null && AlwaysDebugPathfinding) {
            this.finder = new AStarPathFinder((IsoGameCharacter)null, IsoWorld.instance.CurrentCell.getPathMap(), 800, true, new ManhattanHeuristic(1));
         }

         if (AlwaysDebugPathfinding) {
            this.finder.maxSearchDistance = 1000;
            Path var11 = this.finder.findPath(0, (Mover)null, 170, 110, 0, 85, 110, 0);
            return GameStateMachine.StateAction.Remain;
         } else {
            if (FrameLoader.bClient) {
            }

            if (IsoPlayer.DemoMode) {
               IsoCamera.updateDemo();
            }

            ++this.timesincelastinsanity;
            if (!GameServer.bServer && GameKeyboard.isKeyDown(Core.getInstance().getKey("Toggle Music")) && !this.MDebounce) {
               this.MDebounce = true;
               SoundManager.instance.AllowMusic = !SoundManager.instance.AllowMusic;
               if (!SoundManager.instance.AllowMusic) {
                  SoundManager.instance.StopMusic();
                  TutorialManager.instance.PrefMusic = null;
               }
            } else if (!GameServer.bServer && !GameKeyboard.isKeyDown(Core.getInstance().getKey("Toggle Music"))) {
               this.MDebounce = false;
            }

            if (Core.bDebug) {
               this.debugTimes.add(System.nanoTime());
            }

            try {
               if (!GameServer.bServer && IsoPlayer.getInstance() != null && IsoPlayer.allPlayersDead()) {
                  if (IsoPlayer.getInstance() != null) {
                     UIManager.getSpeedControls().SetCurrentGameSpeed(1);
                  }

                  IsoCamera.update();
               }

               this.alt = !this.alt;
               if (!GameServer.bServer) {
                  WaitMul = 1;
                  if (UIManager.getSpeedControls() != null) {
                     if (UIManager.getSpeedControls().getCurrentGameSpeed() == 2) {
                        WaitMul = 15;
                     }

                     if (UIManager.getSpeedControls().getCurrentGameSpeed() == 3) {
                        WaitMul = 30;
                     }
                  }
               }

               if (Core.bDebug) {
                  this.debugTimes.add(System.nanoTime());
               }

               if (GameServer.bServer) {
                  if (GameServer.Players.isEmpty() && ServerOptions.instance.PauseEmpty.getValue()) {
                     this.Paused = true;
                  } else {
                     this.Paused = false;
                  }
               }

               if (!this.Paused || GameClient.bClient) {
                  try {
                     if (IsoCamera.CamCharacter != null && IsoWorld.instance.bDoChunkMapUpdate) {
                        for(int var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
                           if (IsoPlayer.players[var3] != null && !IsoWorld.instance.CurrentCell.ChunkMap[var3].ignore) {
                              if (!GameServer.bServer) {
                                 IsoCamera.CamCharacter = IsoPlayer.players[var3];
                                 IsoPlayer.instance = IsoPlayer.players[var3];
                              }

                              if (!GameServer.bServer) {
                                 IsoWorld.instance.CurrentCell.ChunkMap[var3].ProcessChunkPos(IsoCamera.CamCharacter);
                              }
                           }
                        }
                     }

                     if (PerformanceSettings.LightingThread && !LightingThread.instance.newLightingMethod && LightingThread.instance.UpdateDone) {
                        IsoCell.bReadAltLight = !IsoCell.bReadAltLight;
                        LightingThread.instance.UpdateDone = false;
                     }

                     if (Core.bDebug) {
                        this.debugTimes.add(System.nanoTime());
                     }

                     IsoWorld.instance.update();
                     if (Core.bDebug) {
                        this.debugTimes.add(System.nanoTime());
                     }

                     ZomboidRadio.getInstance().update();
                     this.UpdateStuff();
                     LuaEventManager.triggerEvent("OnTick", (double)this.numberTicks);
                     ++this.numberTicks;
                     ScriptManager.instance.Trigger("OnTick");
                  } catch (Exception var8) {
                     ExceptionLogger.logException(var8);
                     if (!GameServer.bServer) {
                        if (GameClient.bClient) {
                           WorldStreamer.instance.stop();
                        }

                        String var4 = Core.GameSaveWorld;
                        createWorld(Core.GameSaveWorld + "_crash");
                        copyWorld(var4, Core.GameSaveWorld);
                        GameWindow.save(true, false);
                     }

                     if (GameClient.bClient) {
                        GameClient.instance.doDisconnect("Quitting");
                     }

                     return GameStateMachine.StateAction.Continue;
                  }
               }
            } catch (Exception var9) {
               ExceptionLogger.logException(var9);
            }

            if (Core.bDebug) {
               this.debugTimes.add(System.nanoTime());
            }

            if (!GameServer.bServer || ServerGUI.isCreated()) {
               ModelManager.instance.update();
            }

            if (Core.bDebug && FPSGraph.instance != null) {
               FPSGraph.instance.addUpdate(System.currentTimeMillis());
               FPSGraph.instance.update();
            }

            return GameStateMachine.StateAction.Remain;
         }
      }
   }

   public static void copyWorld(String var0, String var1) {
      String var2 = GameWindow.getGameModeCacheDir() + File.separator + var0 + File.separator;
      var2 = var2.replace("/", File.separator);
      var2 = var2.replace("\\", File.separator);
      String var3 = var2.substring(0, var2.lastIndexOf(File.separator));
      var3 = var3.replace("\\", "/");
      File var4 = new File(var3);
      var2 = GameWindow.getGameModeCacheDir() + File.separator + var1 + File.separator;
      var2 = var2.replace("/", File.separator);
      var2 = var2.replace("\\", File.separator);
      String var5 = var2.substring(0, var2.lastIndexOf(File.separator));
      var5 = var5.replace("\\", "/");
      File var6 = new File(var5);

      try {
         copyDirectory(var4, var6);
      } catch (IOException var8) {
         var8.printStackTrace();
      }

   }

   public static void copyDirectory(File var0, File var1) throws IOException {
      if (var0.isDirectory()) {
         if (!var1.exists()) {
            var1.mkdir();
         }

         String[] var2 = var0.list();
         boolean var3 = GameLoadingState.convertingFileMax == -1;
         if (var3) {
            GameLoadingState.convertingFileMax = var2.length;
         }

         for(int var4 = 0; var4 < var2.length; ++var4) {
            if (var3) {
               ++GameLoadingState.convertingFileCount;
            }

            copyDirectory(new File(var0, var2[var4]), new File(var1, var2[var4]));
         }
      } else {
         FileInputStream var5 = new FileInputStream(var0);
         FileOutputStream var6 = new FileOutputStream(var1);
         var6.getChannel().transferFrom(var5.getChannel(), 0L, var0.length());
         var5.close();
         var6.close();
      }

   }

   public static void createWorld(String var0) {
      var0 = var0.replace(" ", "_").trim();
      String var1 = GameWindow.getGameModeCacheDir() + File.separator + var0 + File.separator;
      var1 = var1.replace("/", File.separator);
      var1 = var1.replace("\\", File.separator);
      String var2 = var1.substring(0, var1.lastIndexOf(File.separator));
      var2 = var2.replace("\\", "/");
      File var3 = new File(var2);
      if (!var3.exists()) {
         var3.mkdirs();
      }

      Core.GameSaveWorld = var0;
   }

   private void renderOverhead() {
      if (Core.bDebug) {
         if (Keyboard.isKeyDown(15)) {
            TextureID.UseFiltering = true;
            Texture.getSharedTexture("media/ui/white.png");
            IsoCell var1 = IsoWorld.instance.CurrentCell;
            Texture var2 = Texture.getSharedTexture("media/ui/white.png");
            byte var3 = 0;
            byte var4 = 2;
            int var5 = Core.getInstance().getOffscreenWidth(IsoPlayer.getPlayerIndex()) - var1.getWidthInTiles() * var4;
            int var6 = Core.getInstance().getOffscreenHeight(IsoPlayer.getPlayerIndex()) - var1.getHeightInTiles() * var4;
            var2.render(var5, var6, var4 * var1.getWidthInTiles(), var4 * var1.getHeightInTiles(), 0.7F, 0.7F, 0.7F, 1.0F);

            int var7;
            for(var7 = 0; var7 < var1.getWidthInTiles(); ++var7) {
               for(int var8 = 0; var8 < var1.getHeightInTiles(); ++var8) {
                  IsoChunkMap var9 = IsoWorld.instance.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()];
                  IsoGridSquare var10 = var1.getGridSquare(var7 + var9.getWorldXMinTiles(), var8 + var9.getWorldYMinTiles(), var3);
                  if (var10 != null) {
                     if (var10.getProperties().Is(IsoFlagType.exterior)) {
                        var2.render(var5 + var7 * var4, var6 + var8 * var4, var4, var4, 0.8F, 0.8F, 0.8F, 1.0F);
                     }

                     if (var10.getProperties().Is(IsoFlagType.collideN)) {
                     }

                     if (var10.getProperties().Is(IsoFlagType.collideW)) {
                     }

                     if (TileAccessibilityWorker.instance.current.getValue(var7, var8)) {
                        var2.render(var5 + var7 * var4, var6 + var8 * var4, var4, var4, 0.0F, 1.0F, 0.0F, 0.3F);
                     }
                  }
               }
            }

            for(var7 = 0; var7 < IsoWorld.instance.CurrentCell.getObjectList().size(); ++var7) {
               IsoMovingObject var11 = (IsoMovingObject)IsoWorld.instance.CurrentCell.getObjectList().get(var7);
               if (var11.getZ() == (float)var3) {
                  if (var11 instanceof IsoZombie) {
                     var2.render(var5 + ((int)var11.getX() - IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldXMinTiles()) * var4, var6 + ((int)var11.getY() - IsoWorld.instance.CurrentCell.ChunkMap[0].getWorldYMinTiles()) * var4, var4, var4, 1.0F, 0.0F, 0.0F, 1.0F);
                     IsoZombie var12 = (IsoZombie)var11;
                  }

                  if (var11 instanceof IsoSurvivor) {
                     var2.render(var5 + (int)var11.getX() * var4 + 1, var6 + (int)var11.getY() * var4 + 1, var4, var4, 0.0F, 0.0F, 0.0F, 1.0F);
                     var2.render(var5 + (int)var11.getX() * var4 - 1, var6 + (int)var11.getY() * var4 - 1, var4, var4, 0.0F, 0.0F, 0.0F, 1.0F);
                     var2.render(var5 + (int)var11.getX() * var4 - 1, var6 + (int)var11.getY() * var4 + 1, var4, var4, 0.0F, 0.0F, 0.0F, 1.0F);
                     var2.render(var5 + (int)var11.getX() * var4 + 1, var6 + (int)var11.getY() * var4 - 1, var4, var4, 0.0F, 0.0F, 0.0F, 1.0F);
                     var2.render(var5 + (int)var11.getX() * var4, var6 + (int)var11.getY() * var4, var4, var4, 1.0F, 1.0F, 1.0F, 1.0F);
                     IsoSurvivor var13 = (IsoSurvivor)var11;
                     if (var13.getPath() != null) {
                        for(int var14 = 0; var14 < var13.getPath().getLength(); ++var14) {
                        }
                     }
                  }
               }
            }

            TextureID.UseFiltering = false;
         }
      }
   }

   private void updateOverheadReplay() {
      ++this.nReplay;
      if (this.nReplay >= Frames.size()) {
         this.nReplay = 0;
      }

      if (!Keyboard.isKeyDown(15)) {
         this.nReplay = 0;
      }

   }

   private void renderOverheadReplay() {
      if (Keyboard.isKeyDown(15)) {
         if (!Frames.isEmpty()) {
            IsoWorld.Frame var1 = (IsoWorld.Frame)Frames.get(this.nReplay);
            TextureID.UseFiltering = true;
            Texture.getSharedTexture("media/ui/white.png");
            IsoCell var2 = IsoWorld.instance.CurrentCell;
            Texture var3 = Texture.getSharedTexture("media/ui/white.png");
            byte var4 = 0;
            byte var5 = 4;

            for(int var6 = 0; var6 < var2.getWidthInTiles(); ++var6) {
               for(int var7 = 0; var7 < var2.getHeightInTiles(); ++var7) {
                  IsoGridSquare var8 = var2.getGridSquare(var6, var7, var4);
                  if (var8.getProperties().Is(IsoFlagType.exterior)) {
                     var3.render(var6 * var5, var7 * var5, var5, var5, 0.8F, 0.8F, 0.8F, 1.0F);
                  } else {
                     var3.render(var6 * var5, var7 * var5, var5, var5, 0.7F, 0.7F, 0.7F, 1.0F);
                  }

                  if (var8.getProperties().Is(IsoFlagType.solid) || var8.getProperties().Is(IsoFlagType.solidtrans)) {
                     var3.render(var6 * var5, var7 * var5, var5, var5, 0.5F, 0.5F, 0.5F, 255.0F);
                  }

                  if (var8.getProperties().Is(IsoFlagType.collideN)) {
                     var3.render(var6 * var5, var7 * var5, var5, 1, 0.2F, 0.2F, 0.2F, 1.0F);
                  }

                  if (var8.getProperties().Is(IsoFlagType.collideW)) {
                     var3.render(var6 * var5, var7 * var5, 1, var5, 0.2F, 0.2F, 0.2F, 1.0F);
                  }
               }
            }

            Iterator var12 = var1.xPos.iterator();
            Iterator var13 = var1.yPos.iterator();
            Iterator var14 = var1.Type.iterator();

            while(var12 != null && var12.hasNext()) {
               int var9 = (Integer)var12.next();
               int var10 = (Integer)var13.next();
               int var11 = (Integer)var14.next();
               if (var11 == 0) {
                  var3.render(var9 * var5 + 1, var10 * var5 + 1, var5, var5, 0.0F, 0.0F, 0.0F, 1.0F);
                  var3.render(var9 * var5 - 1, var10 * var5 - 1, var5, var5, 0.0F, 0.0F, 0.0F, 1.0F);
                  var3.render(var9 * var5 - 1, var10 * var5 + 1, var5, var5, 0.0F, 0.0F, 0.0F, 1.0F);
                  var3.render(var9 * var5 + 1, var10 * var5 - 1, var5, var5, 0.0F, 0.0F, 0.0F, 1.0F);
                  var3.render(var9 * var5, var10 * var5, var5, var5, 0.5F, 0.5F, 1.0F, 1.0F);
               }

               if (var11 == 1) {
                  var3.render(var9 * var5 + 1, var10 * var5 + 1, var5, var5, 0.0F, 0.0F, 0.0F, 1.0F);
                  var3.render(var9 * var5 - 1, var10 * var5 - 1, var5, var5, 0.0F, 0.0F, 0.0F, 1.0F);
                  var3.render(var9 * var5 - 1, var10 * var5 + 1, var5, var5, 0.0F, 0.0F, 0.0F, 1.0F);
                  var3.render(var9 * var5 + 1, var10 * var5 - 1, var5, var5, 0.0F, 0.0F, 0.0F, 1.0F);
                  var3.render(var9 * var5, var10 * var5, var5, var5, 1.0F, 1.0F, 1.0F, 1.0F);
               }

               if (var11 == 2) {
                  var3.render(var9 * var5, var10 * var5, var5, var5, 1.0F, 0.0F, 0.0F, 1.0F);
               }
            }

            TextureID.UseFiltering = false;
         }
      }
   }
}
