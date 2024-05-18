package zombie.iso;

import fmod.fmod.FMODSoundEmitter;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.krka.kahlua.vm.KahluaTable;
import zombie.CollisionManager;
import zombie.FliesSound;
import zombie.FrameLoader;
import zombie.GameApplet;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.MapCollisionData;
import zombie.ReanimatedPlayers;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ZomboidFileSystem;
import zombie.ZomboidGlobals;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.ZombieGroupManager;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.DummySoundEmitter;
import zombie.behaviors.survivor.orders.FollowOrder;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.characters.SurvivorFactory;
import zombie.characters.SurvivorGroup;
import zombie.characters.SurvivorPersonality;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.traits.TraitFactory;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.TilePropertyAliasMap;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.physics.WorldSimulation;
import zombie.core.skinnedmodel.AutoZombieManager;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.stash.StashSystem;
import zombie.core.utils.OnceEvery;
import zombie.debug.DebugLog;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionGlobals;
import zombie.gameStates.GameLoadingState;
import zombie.globalObjects.GlobalObjectLookup;
import zombie.inventory.ItemContainerFiller;
import zombie.inventory.ItemPickerJava;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegion;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.ObjectRenderEffects;
import zombie.iso.objects.RainManager;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteGrid;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.sprite.SkyBox;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.network.BodyDamageSync;
import zombie.network.ChunkRevisions;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetChecksum;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.popman.ZombiePopulationManager;
import zombie.radio.ZomboidRadio;
import zombie.randomizedWorld.RBBasic;
import zombie.randomizedWorld.RBBurnt;
import zombie.randomizedWorld.RBLooted;
import zombie.randomizedWorld.RBSafehouse;
import zombie.randomizedWorld.RandomizedBuildingBase;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.ScriptCharacter;
import zombie.ui.TutorialManager;
import zombie.util.AddCoopPlayer;
import zombie.util.SharedStrings;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleIDMap;
import zombie.vehicles.VehicleManager;

public class IsoWorld {
   private float globalTemperature = 0.0F;
   private String weather = "sunny";
   public IsoMetaGrid MetaGrid = new IsoMetaGrid();
   private ArrayList randomizedBuildingList = new ArrayList();
   private RandomizedBuildingBase RBBasic = new RBBasic();
   public SkyBox sky = null;
   public Helicopter helicopter = new Helicopter();
   public ArrayDeque freeEmitters = new ArrayDeque();
   public ArrayList currentEmitters = new ArrayList();
   int movex = 0;
   int movey = 0;
   public int x = 50;
   public int y = 50;
   public String playerCell = "suburbs1";
   public IsoCell CurrentCell;
   public static IsoWorld instance = new IsoWorld();
   public Stack Groups = new Stack();
   public int TotalSurvivorsDead = 0;
   public int TotalSurvivorNights = 0;
   public int SurvivorSurvivalRecord = 0;
   public HashMap SurvivorDescriptors = new HashMap();
   private int cellSurvivorSpawns;
   private int cellRemoteness;
   public ArrayList AddCoopPlayers = new ArrayList();
   boolean caboltoo = false;
   static IsoWorld.CompDistToPlayer compDistToPlayer = new IsoWorld.CompDistToPlayer();
   public static String mapPath = "media/";
   public static boolean mapUseJar = true;
   boolean bLoaded = false;
   public static HashMap PropertyValueMap = new HashMap();
   private static int WorldX = 0;
   private static int WorldY = 0;
   public IsoSpriteManager spriteManager;
   private SurvivorDesc luaDesc;
   private ArrayList luatraits;
   private int luaSpawnCellX = -1;
   private int luaSpawnCellY = -1;
   private int luaPosX = -1;
   private int luaPosY = -1;
   private int luaPosZ = -1;
   public static final int WorldVersion = 143;
   public static final int WorldVersion_Barricade = 87;
   public static final int WorldVersion_SandboxOptions = 88;
   public static final int WorldVersion_FliesSound = 121;
   public static final int WorldVersion_LootRespawn = 125;
   public static final int WorldVersion_OverlappingGenerators = 127;
   public static final int WorldVersion_ItemContainerIdenticalItems = 128;
   public static final int WorldVersion_VehicleSirenStartTime = 129;
   public static final int WorldVersion_CompostLastUpdated = 130;
   public static final int WorldVersion_DayLengthHours = 131;
   public static final int WorldVersion_LampOnPillar = 132;
   public static final int WorldVersion_AlarmClockRingSince = 134;
   public static final int WorldVersion_ClimateAdded = 135;
   public static final int WorldVersion_VehicleLightFocusing = 135;
   public static final int WorldVersion_GeneratorFuelFloat = 138;
   public static final int WorldVersion_InfectionTime = 142;
   public static final int WorldVersion_ClimateColors = 143;
   public static final int WorldVersion_ChunkVehicles = 91;
   public static final int WorldVersion_PlayerVehicleSeat = 91;
   public static int SavedWorldVersion = -1;
   public String[][] cellMap = new String[10][10];
   OnceEvery spriteChange = new OnceEvery(0.3F);
   public boolean bDrawWorld = true;
   int savePlayerCount = 0;
   private ArrayList zombieWithModel = new ArrayList();
   static OnceEvery e = new OnceEvery(0.4F, false);
   int worldX = 0;
   int worldY = 0;
   static SurvivorGroup TestGroup = null;
   public static boolean NoZombies = false;
   public static int TotalWorldVersion = -1;
   public static int saveoffsetx;
   public static int saveoffsety;
   public boolean bDoChunkMapUpdate = true;
   private long emitterUpdateMS;
   public boolean emitterUpdate;

   public IsoMetaGrid getMetaGrid() {
      return this.MetaGrid;
   }

   public IsoMetaGrid.Zone registerZone(String var1, String var2, int var3, int var4, int var5, int var6, int var7) {
      return this.MetaGrid.registerZone(var1, var2, var3, var4, var5, var6, var7);
   }

   public void removeZone(IsoMetaGrid.Zone var1) {
      this.MetaGrid.removeZone(var1);
   }

   public IsoMetaGrid.Zone registerZoneNoOverlap(String var1, String var2, int var3, int var4, int var5, int var6, int var7) {
      return this.MetaGrid.registerZoneNoOverlap(var1, var2, var3, var4, var5, var6, var7);
   }

   public void removeZonesForLotDirectory(String var1) {
      this.MetaGrid.removeZonesForLotDirectory(var1);
   }

   public BaseSoundEmitter getFreeEmitter() {
      Object var1 = null;
      if (this.freeEmitters.isEmpty()) {
         var1 = Core.SoundDisabled ? new DummySoundEmitter() : new FMODSoundEmitter();
      } else {
         var1 = (BaseSoundEmitter)this.freeEmitters.pop();
      }

      this.currentEmitters.add(var1);
      return (BaseSoundEmitter)var1;
   }

   public BaseSoundEmitter getFreeEmitter(float var1, float var2, float var3) {
      BaseSoundEmitter var4 = this.getFreeEmitter();
      var4.setPos(var1, var2, var3);
      return var4;
   }

   public IsoMetaGrid.Zone registerVehiclesZone(String var1, String var2, int var3, int var4, int var5, int var6, int var7, KahluaTable var8) {
      return this.MetaGrid.registerVehiclesZone(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void checkVehiclesZones() {
      this.MetaGrid.checkVehiclesZones();
   }

   public static byte[] createChecksum(String var0) throws Exception {
      FileInputStream var1 = new FileInputStream(var0);
      byte[] var2 = new byte[1024];
      MessageDigest var3 = MessageDigest.getInstance("MD5");

      int var4;
      do {
         var4 = var1.read(var2);
         if (var4 > 0) {
            var3.update(var2, 0, var4);
         }
      } while(var4 != -1);

      var1.close();
      return var3.digest();
   }

   public void setGameMode(String var1) {
      Core.GameMode = var1;
      Core.bLastStand = "LastStand".equals(var1);
      Core.getInstance().setChallenge(false);
   }

   public String getGameMode() {
      return Core.GameMode;
   }

   public void setWorld(String var1) {
      Core.GameSaveWorld = var1.trim();
   }

   public void setMap(String var1) {
      Core.GameMap = var1;
   }

   public String getMap() {
      return Core.GameMap;
   }

   public static String getMD5Checksum(String var0) throws Exception {
      byte[] var1 = createChecksum(var0);
      String var2 = "";

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2 = var2 + Integer.toString((var1[var3] & 255) + 256, 16).substring(1);
      }

      return var2;
   }

   public static boolean DoChecksumCheck(String var0, String var1) {
      String var2 = "";

      try {
         var2 = getMD5Checksum(var0);
         if (!var2.equals(var1)) {
            return false;
         }
      } catch (Exception var6) {
         var2 = "";

         try {
            var2 = getMD5Checksum("D:/Dropbox/Zomboid/zombie/build/classes/" + var0);
         } catch (Exception var5) {
            return false;
         }
      }

      return var2.equals(var1);
   }

   public static boolean DoChecksumCheck() {
      if (!DoChecksumCheck("zombie/GameWindow.class", "c4a62b8857f0fb6b9c103ff6ef127a9b")) {
         return false;
      } else if (!DoChecksumCheck("zombie/GameWindow$1.class", "5d93dc446b2dc49092fe4ecb5edf5f17")) {
         return false;
      } else if (!DoChecksumCheck("zombie/GameWindow$2.class", "a3e3d2c8cf6f0efaa1bf7f6ceb572073")) {
         return false;
      } else if (!DoChecksumCheck("zombie/gameStates/MainScreenState.class", "206848ba7cb764293dd2c19780263854")) {
         return false;
      } else if (!DoChecksumCheck("zombie/FrameLoader$1.class", "0ebfcc9557cc28d53aa982a71616bf5b")) {
         return false;
      } else {
         return DoChecksumCheck("zombie/FrameLoader.class", "d5b1f7b2886a499d848c204f6a815776");
      }
   }

   private void LoadRemotenessVars() {
   }

   public IsoObject getItemFromXYZIndexBuffer(ByteBuffer var1) {
      int var2 = var1.getInt();
      int var3 = var1.getInt();
      int var4 = var1.getInt();
      IsoGridSquare var5 = this.CurrentCell.getGridSquare(var2, var3, var4);
      if (var5 == null) {
         return null;
      } else {
         byte var6 = var1.get();
         return var6 >= 0 && var6 < var5.getObjects().size() ? (IsoObject)var5.getObjects().get(var6) : null;
      }
   }

   public IsoWorld() {
      if (!GameServer.bServer) {
         this.sky = SkyBox.getInstance();
      }

   }

   public void CreateSurvivorGroup(IsoGridSquare var1, IsoPlayer var2) {
      int var3 = Rand.Next(4);
      SurvivorDesc var4 = SurvivorFactory.CreateSurvivor();
      IsoSurvivor var5 = this.CreateRandomSurvivor(var4, var1, var2);
      if (var5 != null) {
         if (IsoPlayer.DemoMode) {
            var3 = 0;
         }

         for(int var6 = 0; var6 < var3; ++var6) {
            SurvivorDesc var7 = SurvivorFactory.CreateSurvivor();
            IsoGridSquare var8 = var1;

            do {
               var1 = var8.getCell().getGridSquare(var8.getX() + (Rand.Next(10) - 5), var8.getY() + (Rand.Next(10) - 5), var8.getZ());
               if (var1 != null) {
                  var1.setCachedIsFree(false);
               }
            } while(var1 == null || !var1.isFree(true));

            IsoSurvivor var9 = this.CreateRandomSurvivor(var7, var1, var2);
            if (var9 != null) {
               var7.AddToGroup(var4.getGroup());
               var7.getMetCount().put(var4.getID(), 100);
               var4.getMetCount().put(var7.getID(), 100);
               var7.getInstance().GiveOrder(new FollowOrder(var7.getInstance(), var4.getInstance(), 3), true);
            }
         }

      }
   }

   public IsoSurvivor CreateRandomSurvivor(SurvivorDesc var1, IsoGridSquare var2, IsoPlayer var3) {
      int var4 = 0;
      if (var2.getW() != null) {
         ++var4;
      }

      if (var2.getS() != null) {
         ++var4;
      }

      if (var2.getN() != null) {
         ++var4;
      }

      if (var2.getE() != null) {
         ++var4;
      }

      if (var4 <= 1) {
         return null;
      } else {
         IsoSurvivor var5 = null;
         var5 = new IsoSurvivor(SurvivorPersonality.Personality.GunNut, var1, instance.CurrentCell, var2.getX(), var2.getY(), var2.getZ());
         if (Rand.Next(4) == 0) {
            var5.getInventory().AddItem("Base.Plank");
         }

         if (Rand.Next(4) == 0) {
            var5.getInventory().AddItem("Base.Plank");
         }

         if (Rand.Next(4) == 0) {
            var5.getInventory().AddItem("Base.Nails");
         }

         if (Rand.Next(4) == 0) {
            var5.getInventory().AddItem("Base.Nails");
         }

         if (Rand.Next(4) == 0) {
            var5.getInventory().AddItem("Base.Nails");
         }

         if (Rand.Next(4) == 0) {
            var5.getInventory().AddItem("Base.Hammer");
         }

         if (Rand.Next(4) == 0) {
            var5.getInventory().AddItem("Base.Sheet");
         }

         if (Rand.Next(4) == 0) {
            var5.getInventory().AddItem("Base.Sheet");
         }

         if (Rand.Next(4) == 0) {
            var5.getInventory().AddItem("Base.Sheet");
         }

         if (Rand.Next(4) == 0) {
            var5.getInventory().AddItem("Base.Torch");
         }

         var5.getInventory().AddItem("Base.WaterBottleFull");
         var1.setInstance(var5);
         switch(Rand.Next(11)) {
         case 0:
         case 1:
            var5.getInventory().AddItem("Base.Hammer");
            break;
         case 2:
         case 3:
            var5.getInventory().AddItem("Base.Plank");
            break;
         case 4:
            var5.getInventory().AddItem("Base.BaseballBatNails");
            break;
         case 5:
         case 6:
            var5.getInventory().AddItem("Base.Axe");
            break;
         case 7:
         case 8:
            var5.getInventory().AddItem("Base.BaseballBat");
            break;
         case 9:
         case 10:
            var5.getInventory().AddItem("Base.Shotgun");
            var5.getInventory().AddItem("Base.ShotgunShells");
            var5.getInventory().AddItem("Base.ShotgunShells");
            var5.getInventory().AddItem("Base.ShotgunShells");
         }

         var5.setAllowBehaviours(true);
         return var5;
      }
   }

   public void CreateSwarm(int var1, int var2, int var3, int var4, int var5) {
   }

   public void ForceKillAllZombies() {
      GameTime.getInstance().RemoveZombiesIndiscriminate(1000);
   }

   public static int readInt(RandomAccessFile var0) throws EOFException, IOException {
      int var1 = var0.read();
      int var2 = var0.read();
      int var3 = var0.read();
      int var4 = var0.read();
      if ((var1 | var2 | var3 | var4) < 0) {
         throw new EOFException();
      } else {
         return (var1 << 0) + (var2 << 8) + (var3 << 16) + (var4 << 24);
      }
   }

   public static String readString(RandomAccessFile var0) throws EOFException, IOException {
      String var1 = var0.readLine();
      return var1;
   }

   public void LoadTileDefinitions(IsoSpriteManager var1, String var2, int var3) {
      DebugLog.log("tiledef: loading " + var2);
      RandomAccessFile var4 = null;

      try {
         File var5 = new File(var2);
         var4 = new RandomAccessFile(var5.getAbsolutePath(), "r");
         int var6 = readInt(var4);
         int var7 = readInt(var4);
         int var8 = readInt(var4);
         SharedStrings var9 = new SharedStrings();
         boolean var10 = false;
         boolean var11 = false;
         ArrayList var12 = new ArrayList();
         HashMap var13 = new HashMap();
         HashMap var14 = new HashMap();
         String[] var15 = new String[]{"N", "E", "S", "W"};

         for(int var16 = 0; var16 < var15.length; ++var16) {
            var14.put(var15[var16], new ArrayList());
         }

         ArrayList var61 = new ArrayList();
         HashMap var17 = new HashMap();
         int var18 = 0;
         int var19 = 0;
         int var20 = 0;
         int var21 = 0;
         HashSet var22 = new HashSet();

         String var25;
         label1317:
         for(int var23 = 0; var23 < var8; ++var23) {
            String var24 = readString(var4);
            var25 = var24.trim();
            String var26 = readString(var4);
            int var27 = readInt(var4);
            int var28 = readInt(var4);
            int var29 = readInt(var4);
            int var30 = readInt(var4);

            int var31;
            IsoSprite var32;
            for(var31 = 0; var31 < var30; ++var31) {
               if (var3 < 2) {
                  var32 = var1.AddSprite(var25 + "_" + var31, var3 * 100 * 1000 + 10000 + var29 * 1000 + var31);
               } else {
                  var32 = var1.AddSprite(var25 + "_" + var31, var3 * 512 * 512 + var29 * 512 + var31);
               }

               var12.add(var32);
               var32.setName(var25 + "_" + var31);
               if (var32.name.contains("damaged") || var32.name.contains("trash_")) {
                  var32.attachedFloor = true;
                  var32.getProperties().Set("attachedFloor", "true");
               }

               if (var32.name.startsWith("f_bushes") && var31 <= 31) {
                  var32.isBush = true;
                  var32.attachedFloor = true;
                  var32.getProperties().isBush = true;
               }

               int var33 = readInt(var4);

               for(int var34 = 0; var34 < var33; ++var34) {
                  var24 = readString(var4);
                  String var35 = var24.trim();
                  var24 = readString(var4);
                  String var36 = var24.trim();
                  IsoObjectType var37 = IsoObjectType.FromString(var35);
                  if (var37 == IsoObjectType.MAX) {
                     var35 = var9.get(var35);
                     if (var35.equals("firerequirement")) {
                        var32.firerequirement = Integer.parseInt(var36);
                     } else if (var35.equals("fireRequirement")) {
                        var32.firerequirement = Integer.parseInt(var36);
                     } else if (var35.equals("BurntTile")) {
                        var32.burntTile = var36;
                     } else if (var35.equals("ForceAmbient")) {
                        var32.forceAmbient = true;
                        var32.getProperties().Set(var35, var36);
                     } else if (var35.equals("solidfloor")) {
                        var32.solidfloor = true;
                        var32.getProperties().Set(var35, var36);
                     } else if (var35.equals("canBeRemoved")) {
                        var32.canBeRemoved = true;
                        var32.getProperties().Set(var35, var36);
                     } else if (var35.equals("attachedFloor")) {
                        var32.attachedFloor = true;
                        var32.getProperties().Set(var35, var36);
                     } else if (var35.equals("cutW")) {
                        var32.cutW = true;
                        var32.getProperties().Set(var35, var36);
                     } else if (var35.equals("cutN")) {
                        var32.cutN = true;
                        var32.getProperties().Set(var35, var36);
                     } else if (var35.equals("solid")) {
                        var32.solid = true;
                        var32.getProperties().Set(var35, var36);
                     } else if (var35.equals("solidTrans")) {
                        var32.solidTrans = true;
                        var32.getProperties().Set(var35, var36);
                     } else if (var35.equals("invisible")) {
                        var32.invisible = true;
                        var32.getProperties().Set(var35, var36);
                     } else if (var35.equals("alwaysDraw")) {
                        var32.alwaysDraw = true;
                        var32.getProperties().Set(var35, var36);
                     } else if ("FloorHeight".equals(var35)) {
                        if ("OneThird".equals(var36)) {
                           var32.getProperties().Set(IsoFlagType.FloorHeightOneThird);
                        } else if ("TwoThirds".equals(var36)) {
                           var32.getProperties().Set(IsoFlagType.FloorHeightTwoThirds);
                        }
                     } else if (var35.equals("MoveWithWind")) {
                        var32.moveWithWind = true;
                        var32.getProperties().Set(var35, var36);
                     } else if (var35.equals("WindType")) {
                        var32.windType = Integer.parseInt(var36);
                        var32.getProperties().Set(var35, var36);
                     } else {
                        var32.getProperties().Set(var35, var36);
                        if ("WindowN".equals(var35) || "WindowW".equals(var35)) {
                           var32.getProperties().Set(var35, var36, false);
                        }
                     }
                  } else {
                     if (var32.getType() != IsoObjectType.doorW && var32.getType() != IsoObjectType.doorN || var37 != IsoObjectType.wall) {
                        var32.setType(var37);
                     }

                     if (var37 == IsoObjectType.doorW) {
                        var32.getProperties().Set(IsoFlagType.doorW);
                     } else if (var37 == IsoObjectType.doorN) {
                        var32.getProperties().Set(IsoFlagType.doorN);
                     }
                  }

                  if (var37 == IsoObjectType.tree) {
                     if (var32.name.equals("e_riverbirch_1_1")) {
                        var36 = "1";
                     }

                     var32.getProperties().Set("tree", var36);
                     var32.getProperties().UnSet(IsoFlagType.solid);
                     var32.getProperties().Set(IsoFlagType.blocksight);
                     int var38 = Integer.parseInt(var36);
                     if (var25.startsWith("vegetation_trees")) {
                        var38 = 4;
                     }

                     if (var38 < 1) {
                        var38 = 1;
                     }

                     if (var38 > 4) {
                        var38 = 4;
                     }

                     if (var38 == 1 || var38 == 2) {
                        var32.getProperties().UnSet(IsoFlagType.blocksight);
                     }
                  }

                  if (var35.equals("interior") && var36.equals("false")) {
                     var32.getProperties().Set(IsoFlagType.exterior);
                  }

                  if (var35.equals("HoppableN")) {
                     var32.getProperties().Set(IsoFlagType.collideN);
                     var32.getProperties().Set(IsoFlagType.canPathN);
                     var32.getProperties().Set(IsoFlagType.transparentN, "");
                  }

                  if (var35.equals("HoppableW")) {
                     var32.getProperties().Set(IsoFlagType.collideW);
                     var32.getProperties().Set(IsoFlagType.canPathW);
                     var32.getProperties().Set(IsoFlagType.transparentW, "");
                  }

                  if (var35.equals("WallN")) {
                     var32.getProperties().Set(IsoFlagType.collideN);
                     var32.getProperties().Set(IsoFlagType.cutN);
                     var32.setType(IsoObjectType.wall);
                     var32.cutN = true;
                  } else if (var35.equals("WallNTrans")) {
                     var32.getProperties().Set(IsoFlagType.collideN, "");
                     var32.getProperties().Set(IsoFlagType.cutN, "");
                     var32.getProperties().Set(IsoFlagType.transparentN, "");
                     var32.setType(IsoObjectType.wall);
                     var32.cutN = true;
                  } else if (var35.equals("WallW")) {
                     var32.getProperties().Set(IsoFlagType.collideW);
                     var32.getProperties().Set(IsoFlagType.cutW);
                     var32.setType(IsoObjectType.wall);
                     var32.cutW = true;
                  } else if (var35.equals("windowN")) {
                     var32.getProperties().Set("WindowN", "WindowN");
                     var32.getProperties().Set("WindowN", "WindowN", false);
                  } else if (var35.equals("windowW")) {
                     var32.getProperties().Set("WindowW", "WindowW");
                     var32.getProperties().Set("WindowW", "WindowW", false);
                  } else if (var35.equals("WallWTrans")) {
                     var32.getProperties().Set(IsoFlagType.collideW, "");
                     var32.getProperties().Set(IsoFlagType.transparentW, "");
                     var32.getProperties().Set(IsoFlagType.cutW, "");
                     var32.setType(IsoObjectType.wall);
                     var32.cutW = true;
                  } else if (var35.equals("DoorWallN")) {
                     var32.getProperties().Set(IsoFlagType.cutN);
                     var32.getProperties().Set("DoorWallN", "DoorWallN");
                     var32.cutN = true;
                  } else if (var35.equals("DoorWallW")) {
                     var32.getProperties().Set(IsoFlagType.cutW);
                     var32.getProperties().Set("DoorWallW", "DoorWallW");
                     var32.cutW = true;
                  } else if (var35.equals("WallNW")) {
                     var32.getProperties().Set(IsoFlagType.collideN, "");
                     var32.getProperties().Set(IsoFlagType.cutN, "");
                     var32.getProperties().Set(IsoFlagType.collideW, "");
                     var32.getProperties().Set(IsoFlagType.cutW, "");
                     var32.setType(IsoObjectType.wall);
                     var32.cutW = true;
                     var32.cutN = true;
                  } else if (var35.equals("WallNWTrans")) {
                     var32.getProperties().Set(IsoFlagType.collideN, "");
                     var32.getProperties().Set(IsoFlagType.cutN, "");
                     var32.getProperties().Set(IsoFlagType.collideW, "");
                     var32.getProperties().Set(IsoFlagType.transparentN, "");
                     var32.getProperties().Set(IsoFlagType.transparentW, "");
                     var32.getProperties().Set(IsoFlagType.cutW, "");
                     var32.setType(IsoObjectType.wall);
                     var32.cutW = true;
                     var32.cutN = true;
                  } else if (var35.equals("WallSE")) {
                     var32.getProperties().Set(IsoFlagType.cutW, "");
                     var32.getProperties().Set(IsoFlagType.WallSE);
                     var32.getProperties().Set("WallSE", "WallSE");
                     var32.cutW = true;
                  } else if (var35.equals("WindowW")) {
                     var32.getProperties().Set(IsoFlagType.canPathW, "");
                     var32.getProperties().Set(IsoFlagType.collideW, "");
                     var32.getProperties().Set(IsoFlagType.cutW, "");
                     var32.getProperties().Set(IsoFlagType.transparentW, "");
                     var32.setType(IsoObjectType.windowFW);
                     if (var32.getProperties().Is(IsoFlagType.HoppableW)) {
                        if (Core.bDebug) {
                           DebugLog.log("ERROR: WindowW sprite shouldn't have HoppableW (" + var32.getName() + ")");
                        }

                        var32.getProperties().UnSet(IsoFlagType.HoppableW);
                     }

                     var32.cutW = true;
                  } else if (var35.equals("WindowN")) {
                     var32.getProperties().Set(IsoFlagType.canPathN, "");
                     var32.getProperties().Set(IsoFlagType.collideN, "");
                     var32.getProperties().Set(IsoFlagType.cutN, "");
                     var32.getProperties().Set(IsoFlagType.transparentN, "");
                     var32.setType(IsoObjectType.windowFN);
                     if (var32.getProperties().Is(IsoFlagType.HoppableN)) {
                        if (Core.bDebug) {
                           DebugLog.log("ERROR: WindowN sprite shouldn't have HoppableN (" + var32.getName() + ")");
                        }

                        var32.getProperties().UnSet(IsoFlagType.HoppableN);
                     }

                     var32.cutN = true;
                  } else if (var35.equals("UnbreakableWindowW")) {
                     var32.getProperties().Set(IsoFlagType.canPathW, "");
                     var32.getProperties().Set(IsoFlagType.collideW, "");
                     var32.getProperties().Set(IsoFlagType.cutW, "");
                     var32.getProperties().Set(IsoFlagType.transparentW, "");
                     var32.getProperties().Set(IsoFlagType.collideW, "");
                     var32.setType(IsoObjectType.wall);
                     var32.cutW = true;
                  } else if (var35.equals("UnbreakableWindowN")) {
                     var32.getProperties().Set(IsoFlagType.canPathN, "");
                     var32.getProperties().Set(IsoFlagType.collideN, "");
                     var32.getProperties().Set(IsoFlagType.cutN, "");
                     var32.getProperties().Set(IsoFlagType.transparentN, "");
                     var32.getProperties().Set(IsoFlagType.collideN, "");
                     var32.setType(IsoObjectType.wall);
                     var32.cutN = true;
                  } else if (var35.equals("UnbreakableWindowNW")) {
                     var32.getProperties().Set(IsoFlagType.cutN, "");
                     var32.getProperties().Set(IsoFlagType.transparentN, "");
                     var32.getProperties().Set(IsoFlagType.collideN, "");
                     var32.getProperties().Set(IsoFlagType.cutN, "");
                     var32.getProperties().Set(IsoFlagType.collideW, "");
                     var32.getProperties().Set(IsoFlagType.cutW, "");
                     var32.setType(IsoObjectType.wall);
                     var32.cutW = true;
                     var32.cutN = true;
                  } else if ("NoWallLighting".equals(var35)) {
                     var32.getProperties().Set(IsoFlagType.NoWallLighting);
                  } else if ("ForceAmbient".equals(var35)) {
                     var32.getProperties().Set(IsoFlagType.ForceAmbient);
                  }

                  if (var35.equals("name")) {
                     var32.setParentObjectName(var36);
                  }
               }

               if (var32.getProperties().Is("lightR") || var32.getProperties().Is("lightG") || var32.getProperties().Is("lightB")) {
                  if (!var32.getProperties().Is("lightR")) {
                     var32.getProperties().Set("lightR", "0");
                  }

                  if (!var32.getProperties().Is("lightG")) {
                     var32.getProperties().Set("lightG", "0");
                  }

                  if (!var32.getProperties().Is("lightB")) {
                     var32.getProperties().Set("lightB", "0");
                  }
               }

               var32.getProperties().CreateKeySet();
               if (Core.bDebug && var32.getProperties().Is("SmashedTileOffset") && !var32.getProperties().Is("GlassRemovedOffset")) {
                  DebugLog.log("ERROR: Window sprite has SmashedTileOffset but no GlassRemovedOffset (" + var32.getName() + ")");
               }
            }

            var13.clear();

            String var66;
            for(var31 = 0; var31 < var12.size(); ++var31) {
               var32 = (IsoSprite)var12.get(var31);
               if (var32.getProperties().Is("StopCar")) {
                  var32.setType(IsoObjectType.isMoveAbleObject);
               }

               if (var32.getProperties().Is("IsMoveAble")) {
                  if (var32.getProperties().Is("CustomName") && !var32.getProperties().Val("CustomName").equals("")) {
                     ++var18;
                     if (var32.getProperties().Is("GroupName")) {
                        var66 = var32.getProperties().Val("GroupName") + " " + var32.getProperties().Val("CustomName");
                        if (!var13.containsKey(var66)) {
                           var13.put(var66, new ArrayList());
                        }

                        ((ArrayList)var13.get(var66)).add(var32);
                        var22.add(var66);
                     } else {
                        if (!var17.containsKey(var25)) {
                           var17.put(var25, new ArrayList());
                        }

                        if (!((ArrayList)var17.get(var25)).contains(var32.getProperties().Val("CustomName"))) {
                           ((ArrayList)var17.get(var25)).add(var32.getProperties().Val("CustomName"));
                        }

                        ++var19;
                        var22.add(var32.getProperties().Val("CustomName"));
                     }
                  } else {
                     DebugLog.log("[IMPORTANT] MOVABLES: Object has no custom name defined: sheet = " + var25);
                  }
               }
            }

            Iterator var64 = var13.entrySet().iterator();

            while(true) {
               while(true) {
                  while(true) {
                     ArrayList var67;
                     boolean var69;
                     int var70;
                     boolean var71;
                     IsoSprite var73;
                     do {
                        if (!var64.hasNext()) {
                           var12.clear();
                           continue label1317;
                        }

                        Entry var65 = (Entry)var64.next();
                        var66 = (String)var65.getKey();
                        if (!var17.containsKey(var25)) {
                           var17.put(var25, new ArrayList());
                        }

                        if (!((ArrayList)var17.get(var25)).contains(var66)) {
                           ((ArrayList)var17.get(var25)).add(var66);
                        }

                        var67 = (ArrayList)var65.getValue();
                        if (var67.size() == 1) {
                           DebugLog.log("MOVABLES: Object has only one face defined for group: (" + var66 + ") sheet = " + var25);
                        }

                        if (var67.size() == 3) {
                           DebugLog.log("MOVABLES: Object only has 3 sprites, _might_ have a error in settings, group: (" + var66 + ") sheet = " + var25);
                        }

                        for(int var68 = 0; var68 < var15.length; ++var68) {
                           ((ArrayList)var14.get(var15[var68])).clear();
                        }

                        var69 = ((IsoSprite)var67.get(0)).getProperties().Is("SpriteGridPos") && !((IsoSprite)var67.get(0)).getProperties().Val("SpriteGridPos").equals("None");
                        var71 = true;

                        for(var70 = 0; var70 < var67.size(); ++var70) {
                           var73 = (IsoSprite)var67.get(var70);
                           boolean var39 = var73.getProperties().Is("SpriteGridPos") && !var73.getProperties().Val("SpriteGridPos").equals("None");
                           if (var69 != var39) {
                              var71 = false;
                              DebugLog.log("MOVABLES: Difference in SpriteGrid settings for members of group: (" + var66 + ") sheet = " + var25);
                              break;
                           }

                           if (!var73.getProperties().Is("Facing")) {
                              var71 = false;
                           } else {
                              String var40 = var73.getProperties().Val("Facing");
                              byte var41 = -1;
                              switch(var40.hashCode()) {
                              case 69:
                                 if (var40.equals("E")) {
                                    var41 = 1;
                                 }
                                 break;
                              case 78:
                                 if (var40.equals("N")) {
                                    var41 = 0;
                                 }
                                 break;
                              case 83:
                                 if (var40.equals("S")) {
                                    var41 = 2;
                                 }
                                 break;
                              case 87:
                                 if (var40.equals("W")) {
                                    var41 = 3;
                                 }
                              }

                              switch(var41) {
                              case 0:
                                 ((ArrayList)var14.get("N")).add(var73);
                                 break;
                              case 1:
                                 ((ArrayList)var14.get("E")).add(var73);
                                 break;
                              case 2:
                                 ((ArrayList)var14.get("S")).add(var73);
                                 break;
                              case 3:
                                 ((ArrayList)var14.get("W")).add(var73);
                                 break;
                              default:
                                 DebugLog.log("MOVABLES: Invalid face (" + var73.getProperties().Val("Facing") + ") for group: (" + var66 + ") sheet = " + var25);
                                 var71 = false;
                              }
                           }

                           if (!var71) {
                              DebugLog.log("MOVABLES: Not all members have a valid face defined for group: (" + var66 + ") sheet = " + var25);
                              break;
                           }
                        }
                     } while(!var71);

                     int var72;
                     ArrayList var74;
                     if (!var69) {
                        if (var67.size() > 4) {
                           DebugLog.log("MOVABLES: Object has too many faces defined for group: (" + var66 + ") sheet = " + var25);
                        } else {
                           for(var70 = 0; var70 < var15.length; ++var70) {
                              if (((ArrayList)var14.get(var15[var70])).size() > 1) {
                                 DebugLog.log("MOVABLES: " + var15[var70] + " face defined more than once for group: (" + var66 + ") sheet = " + var25);
                                 var71 = false;
                              }
                           }

                           if (var71) {
                              ++var20;

                              for(var70 = 0; var70 < var67.size(); ++var70) {
                                 var73 = (IsoSprite)var67.get(var70);

                                 for(var72 = 0; var72 < var15.length; ++var72) {
                                    var74 = (ArrayList)var14.get(var15[var72]);
                                    if (var74.size() > 0 && var74.get(0) != var73) {
                                       var73.getProperties().Set(var15[var72] + "offset", Integer.toString(var12.indexOf(var74.get(0)) - var12.indexOf(var73)));
                                    }
                                 }
                              }
                           }
                        }
                     } else {
                        var70 = 0;
                        IsoSpriteGrid[] var75 = new IsoSpriteGrid[var15.length];

                        int var42;
                        IsoSprite var44;
                        label1286:
                        for(var72 = 0; var72 < var15.length; ++var72) {
                           var74 = (ArrayList)var14.get(var15[var72]);
                           if (var74.size() > 0) {
                              if (var70 == 0) {
                                 var70 = var74.size();
                              }

                              if (var70 != var74.size()) {
                                 DebugLog.log("MOVABLES: Sprite count mismatch for multi sprite movable, group: (" + var66 + ") sheet = " + var25);
                                 var71 = false;
                                 break;
                              }

                              var61.clear();
                              int var77 = -1;
                              var42 = -1;
                              Iterator var43 = var74.iterator();

                              while(true) {
                                 String var45;
                                 String[] var46;
                                 int var47;
                                 int var48;
                                 if (var43.hasNext()) {
                                    var44 = (IsoSprite)var43.next();
                                    var45 = var44.getProperties().Val("SpriteGridPos");
                                    if (!var61.contains(var45)) {
                                       var61.add(var45);
                                       var46 = var45.split(",");
                                       if (var46.length == 2) {
                                          var47 = Integer.parseInt(var46[0]);
                                          var48 = Integer.parseInt(var46[1]);
                                          if (var47 > var77) {
                                             var77 = var47;
                                          }

                                          if (var48 > var42) {
                                             var42 = var48;
                                          }
                                          continue;
                                       }

                                       DebugLog.log("MOVABLES: SpriteGrid position error for multi sprite movable, group: (" + var66 + ") sheet = " + var25);
                                       var71 = false;
                                    } else {
                                       DebugLog.log("MOVABLES: double SpriteGrid position (" + var45 + ") for multi sprite movable, group: (" + var66 + ") sheet = " + var25);
                                       var71 = false;
                                    }
                                 }

                                 if (var77 == -1 || var42 == -1 || (var77 + 1) * (var42 + 1) != var74.size()) {
                                    DebugLog.log("MOVABLES: SpriteGrid dimensions error for multi sprite movable, group: (" + var66 + ") sheet = " + var25);
                                    var71 = false;
                                    break label1286;
                                 }

                                 if (!var71) {
                                    break label1286;
                                 }

                                 var75[var72] = new IsoSpriteGrid(var77 + 1, var42 + 1);
                                 var43 = var74.iterator();

                                 while(var43.hasNext()) {
                                    var44 = (IsoSprite)var43.next();
                                    var45 = var44.getProperties().Val("SpriteGridPos");
                                    var46 = var45.split(",");
                                    var47 = Integer.parseInt(var46[0]);
                                    var48 = Integer.parseInt(var46[1]);
                                    var75[var72].setSprite(var47, var48, var44);
                                 }

                                 if (!var75[var72].validate()) {
                                    DebugLog.log("MOVABLES: SpriteGrid didn't validate for multi sprite movable, group: (" + var66 + ") sheet = " + var25);
                                    var71 = false;
                                    break label1286;
                                 }
                                 break;
                              }
                           }
                        }

                        if (var71 && var70 != 0) {
                           ++var21;

                           for(var72 = 0; var72 < var15.length; ++var72) {
                              IsoSpriteGrid var76 = var75[var72];
                              if (var76 != null) {
                                 IsoSprite[] var78 = var76.getSprites();
                                 var42 = var78.length;

                                 for(int var79 = 0; var79 < var42; ++var79) {
                                    var44 = var78[var79];
                                    var44.setSpriteGrid(var76);

                                    for(int var80 = 0; var80 < var15.length; ++var80) {
                                       if (var80 != var72 && var75[var80] != null) {
                                          var44.getProperties().Set(var15[var80] + "offset", Integer.toString(var12.indexOf(var75[var80].getAnchorSprite()) - var12.indexOf(var44)));
                                       }
                                    }
                                 }
                              }
                           }
                        } else {
                           DebugLog.log("MOVABLES: Error in multi sprite movable, group: (" + var66 + ") sheet = " + var25);
                        }
                     }
                  }
               }
            }
         }

         if (var11) {
            ArrayList var62 = new ArrayList(var22);
            Collections.sort(var62);
            Iterator var63 = var62.iterator();

            while(var63.hasNext()) {
               var25 = (String)var63.next();
               System.out.println(var25.replaceAll(" ", "_").replaceAll("-", "_").replaceAll("'", "").replaceAll("\\.", "") + " = \"" + var25 + "\",");
            }
         }

         if (var10) {
            try {
               this.saveMovableStats(var17, var3, var19, var20, var21, var18);
            } catch (Exception var58) {
            }
         }
      } catch (Exception var59) {
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var59);
      } finally {
         try {
            var4.close();
         } catch (Exception var57) {
         }

      }

   }

   private void GenerateTilePropertyLookupTables() {
      TilePropertyAliasMap.instance.Generate(PropertyValueMap);
      PropertyValueMap.clear();
   }

   public void LoadTileDefinitionsPropertyStrings(IsoSpriteManager var1, String var2, int var3) {
      DebugLog.log("tiledef: loading " + var2);
      if (!GameServer.bServer) {
         Thread.yield();
         Core.getInstance().DoFrameReady();
      }

      RandomAccessFile var4 = null;

      try {
         File var5 = new File(var2);
         var4 = new RandomAccessFile(var5.getAbsolutePath(), "r");
         int var6 = readInt(var4);
         int var7 = readInt(var4);
         int var8 = readInt(var4);
         SharedStrings var9 = new SharedStrings();

         for(int var10 = 0; var10 < var8; ++var10) {
            if (!GameServer.bServer) {
               Thread.yield();
               Core.getInstance().DoFrameReady();
            }

            String var11 = readString(var4);
            String var12 = var11.trim();
            String var13 = readString(var4);
            int var14 = readInt(var4);
            int var15 = readInt(var4);
            int var16 = readInt(var4);
            int var17 = readInt(var4);

            for(int var18 = 0; var18 < var17; ++var18) {
               int var19 = readInt(var4);

               for(int var20 = 0; var20 < var19; ++var20) {
                  var11 = readString(var4);
                  String var21 = var11.trim();
                  var11 = readString(var4);
                  String var22 = var11.trim();
                  IsoObjectType var23 = IsoObjectType.FromString(var21);
                  var21 = var9.get(var21);
                  ArrayList var24 = null;
                  if (PropertyValueMap.containsKey(var21)) {
                     var24 = (ArrayList)PropertyValueMap.get(var21);
                  } else {
                     var24 = new ArrayList();
                     PropertyValueMap.put(var21, var24);
                  }

                  if (!var24.contains(var22)) {
                     var24.add(var22);
                  }
               }
            }
         }
      } catch (Exception var33) {
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var33);
      } finally {
         try {
            var4.close();
         } catch (Exception var32) {
         }

      }

   }

   private void SetCustomPropertyValues() {
      ((ArrayList)PropertyValueMap.get("WindowN")).add("WindowN");
      ((ArrayList)PropertyValueMap.get("WindowW")).add("WindowW");
      ((ArrayList)PropertyValueMap.get("DoorWallN")).add("DoorWallN");
      ((ArrayList)PropertyValueMap.get("DoorWallW")).add("DoorWallW");
      ((ArrayList)PropertyValueMap.get("WallSE")).add("WallSE");
      ArrayList var1 = new ArrayList();

      for(int var2 = -96; var2 <= 96; ++var2) {
         String var3 = Integer.toString(var2);
         var1.add(var3);
      }

      PropertyValueMap.put("Noffset", var1);
      PropertyValueMap.put("Soffset", var1);
      PropertyValueMap.put("Woffset", var1);
      PropertyValueMap.put("Eoffset", var1);
      ((ArrayList)PropertyValueMap.get("tree")).add("5");
      ((ArrayList)PropertyValueMap.get("tree")).add("6");
      ((ArrayList)PropertyValueMap.get("lightR")).add("0");
      ((ArrayList)PropertyValueMap.get("lightG")).add("0");
      ((ArrayList)PropertyValueMap.get("lightB")).add("0");
   }

   private void saveMovableStats(Map var1, int var2, int var3, int var4, int var5, int var6) throws FileNotFoundException, IOException {
      File var7 = new File(GameWindow.getCacheDir());
      if (var7.exists() && var7.isDirectory()) {
         File var8 = new File(GameWindow.getCacheDir() + File.separator + "movables_stats_" + var2 + ".txt");

         try {
            FileWriter var9 = new FileWriter(var8, false);
            Throwable var10 = null;

            try {
               var9.write("### Movable objects ###" + System.lineSeparator());
               var9.write("Single Face: " + var3 + System.lineSeparator());
               var9.write("Multi Face: " + var4 + System.lineSeparator());
               var9.write("Multi Face & Multi Sprite: " + var5 + System.lineSeparator());
               var9.write("Total objects : " + (var3 + var4 + var5) + System.lineSeparator());
               var9.write(" " + System.lineSeparator());
               var9.write("Total sprites : " + var6 + System.lineSeparator());
               var9.write(" " + System.lineSeparator());
               Iterator var11 = var1.entrySet().iterator();

               while(var11.hasNext()) {
                  Entry var12 = (Entry)var11.next();
                  var9.write((String)var12.getKey() + System.lineSeparator());
                  Iterator var13 = ((ArrayList)var12.getValue()).iterator();

                  while(var13.hasNext()) {
                     String var14 = (String)var13.next();
                     var9.write("\t" + var14 + System.lineSeparator());
                  }
               }
            } catch (Throwable var23) {
               var10 = var23;
               throw var23;
            } finally {
               if (var9 != null) {
                  if (var10 != null) {
                     try {
                        var9.close();
                     } catch (Throwable var22) {
                        var10.addSuppressed(var22);
                     }
                  } else {
                     var9.close();
                  }
               }

            }
         } catch (Exception var25) {
            var25.printStackTrace();
         }
      }

   }

   private void addJumboTreeTileset(IsoSpriteManager var1, int var2, String var3, int var4, int var5, int var6) {
      byte var7 = 2;

      for(int var8 = 0; var8 < var5; ++var8) {
         for(int var9 = 0; var9 < var7; ++var9) {
            String var10 = "e_" + var3 + "JUMBO_1";
            int var11 = var8 * var7 + var9;
            IsoSprite var12 = var1.AddSprite(var10 + "_" + var11, var2 * 512 * 512 + var4 * 512 + var11);

            assert GameServer.bServer || !var12.CurrentAnim.Frames.isEmpty() && ((IsoDirectionFrame)var12.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) != null;

            var12.setName(var10 + "_" + var11);
            var12.setType(IsoObjectType.tree);
            var12.getProperties().Set("tree", var9 == 0 ? "5" : "6");
            var12.getProperties().UnSet(IsoFlagType.solid);
            var12.getProperties().Set(IsoFlagType.blocksight);
            var12.getProperties().CreateKeySet();
            var12.moveWithWind = true;
            var12.windType = var6;
         }
      }

   }

   private void JumboTreeDefinitions(IsoSpriteManager var1, int var2) {
      this.addJumboTreeTileset(var1, var2, "americanholly", 1, 2, 3);
      this.addJumboTreeTileset(var1, var2, "americanlinden", 2, 6, 2);
      this.addJumboTreeTileset(var1, var2, "canadianhemlock", 3, 2, 3);
      this.addJumboTreeTileset(var1, var2, "carolinasilverbell", 4, 6, 1);
      this.addJumboTreeTileset(var1, var2, "cockspurhawthorn", 5, 6, 2);
      this.addJumboTreeTileset(var1, var2, "dogwood", 6, 6, 2);
      this.addJumboTreeTileset(var1, var2, "easternredbud", 7, 6, 2);
      this.addJumboTreeTileset(var1, var2, "redmaple", 8, 6, 2);
      this.addJumboTreeTileset(var1, var2, "riverbirch", 9, 6, 1);
      this.addJumboTreeTileset(var1, var2, "virginiapine", 10, 2, 1);
      this.addJumboTreeTileset(var1, var2, "yellowwood", 11, 6, 2);
      byte var8 = 12;
      byte var9 = 0;
      IsoSprite var10 = var1.AddSprite("jumbo_tree_01_" + var9, var2 * 512 * 512 + var8 * 512 + var9);
      var10.setName("jumbo_tree_01_" + var9);
      var10.setType(IsoObjectType.tree);
      var10.getProperties().Set("tree", "4");
      var10.getProperties().UnSet(IsoFlagType.solid);
      var10.getProperties().Set(IsoFlagType.blocksight);
   }

   public boolean LoadPlayerForInfo() throws FileNotFoundException, IOException {
      File var1 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_p.bin");
      if (!var1.exists()) {
         return false;
      } else {
         FileInputStream var2 = new FileInputStream(var1);
         BufferedInputStream var3 = new BufferedInputStream(var2);
         synchronized(SliceY.SliceBuffer) {
            SliceY.SliceBuffer.rewind();
            byte[] var5 = SliceY.SliceBuffer.array();
            var3.read(SliceY.SliceBuffer.array());
            SliceY.SliceBuffer.rewind();
            var3.close();
            byte var6 = SliceY.SliceBuffer.get();
            byte var7 = SliceY.SliceBuffer.get();
            byte var8 = SliceY.SliceBuffer.get();
            byte var9 = SliceY.SliceBuffer.get();
            int var10 = -1;
            if (var6 == 80 && var7 == 76 && var8 == 89 && var9 == 82) {
               var10 = SliceY.SliceBuffer.getInt();
            } else {
               SliceY.SliceBuffer.rewind();
            }

            if (var10 >= 69) {
               String var11 = GameWindow.ReadString(SliceY.SliceBuffer);
               if (GameClient.bClient && var10 < 71) {
                  var11 = ServerOptions.instance.ServerPlayerID.getValue();
               }

               if (GameClient.bClient && !IsoPlayer.isServerPlayerIDValid(var11)) {
                  GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_ServerPlayerIDMismatch");
                  GameLoadingState.playerWrongIP = true;
                  return false;
               }
            } else if (GameClient.bClient && ServerOptions.instance.ServerPlayerID.getValue().isEmpty()) {
               GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_ServerPlayerIDMissing");
               GameLoadingState.playerWrongIP = true;
               return false;
            }

            WorldX = SliceY.SliceBuffer.getInt();
            WorldY = SliceY.SliceBuffer.getInt();
            IsoChunkMap.WorldXA = SliceY.SliceBuffer.getInt();
            IsoChunkMap.WorldYA = SliceY.SliceBuffer.getInt();
            IsoChunkMap.WorldZA = SliceY.SliceBuffer.getInt();
            IsoChunkMap.WorldXA += 300 * saveoffsetx;
            IsoChunkMap.WorldYA += 300 * saveoffsety;
            IsoChunkMap.SWorldX[0] = WorldX;
            IsoChunkMap.SWorldY[0] = WorldY;
            int[] var10000 = IsoChunkMap.SWorldX;
            var10000[0] += 30 * saveoffsetx;
            var10000 = IsoChunkMap.SWorldY;
            var10000[0] += 30 * saveoffsety;
            return true;
         }
      }
   }

   public void init() throws FileNotFoundException, IOException {
      if (!Core.bTutorial) {
         this.randomizedBuildingList.add(new RBSafehouse());
         this.randomizedBuildingList.add(new RBBurnt());
         this.randomizedBuildingList.add(new RBLooted());
      }

      if (!GameClient.bClient && !GameServer.bServer) {
         BodyDamageSync.instance = null;
      } else {
         BodyDamageSync.instance = new BodyDamageSync();
      }

      if (GameServer.bServer) {
         Core.GameSaveWorld = GameServer.ServerName;
         LuaManager.GlobalObject.createWorld(Core.GameSaveWorld);
      }

      SavedWorldVersion = -1;
      File var1 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_ver.bin");
      FileInputStream var2;
      DataInputStream var3;
      if (var1.exists()) {
         var2 = new FileInputStream(var1);
         var3 = new DataInputStream(var2);
         SavedWorldVersion = var3.readInt();
         if (SavedWorldVersion >= 25) {
            String var4 = GameWindow.ReadString(var3);
            if (!GameClient.bClient) {
               Core.GameMap = var4;
            }
         }

         if (SavedWorldVersion >= 74) {
            this.setDifficulty(GameWindow.ReadString(var3));
         }

         var3.close();
      }

      if (!GameServer.bServer || System.getProperty("softreset") == null) {
         this.MetaGrid.CreateStep1();
      }

      LuaEventManager.triggerEvent("OnPreDistributionMerge");
      LuaEventManager.triggerEvent("OnDistributionMerge");
      LuaEventManager.triggerEvent("OnPostDistributionMerge");
      ItemPickerJava.Parse();
      LuaEventManager.triggerEvent("OnInitWorld");
      if (!GameClient.bClient) {
         var1 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_sand.bin");
         if (var1.exists()) {
            var2 = new FileInputStream(var1);
            BufferedInputStream var40 = new BufferedInputStream(var2);
            if (SliceY.SliceBuffer == null) {
               SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
            }

            synchronized(SliceY.SliceBuffer) {
               var40.read(SliceY.SliceBuffer.array());
               var40.close();
               SliceY.SliceBuffer.rewind();
               SandboxOptions.instance.load(SliceY.SliceBuffer);
               SandboxOptions.instance.handleOldZombiesFile1();
               SandboxOptions.instance.applySettings();
               SandboxOptions.instance.toLua();
            }
         } else {
            SandboxOptions.instance = new SandboxOptions();
            SandboxOptions.instance.updateFromLua();
         }
      }

      ZomboidGlobals.toLua();
      this.SurvivorDescriptors.clear();
      this.spriteManager = new IsoSpriteManager();
      if (GameClient.bClient && ServerOptions.instance.DoLuaChecksum.getValue()) {
         try {
            NetChecksum.comparer.beginCompare();
            GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_Checksum");
            long var37 = System.currentTimeMillis();
            long var41 = var37;

            while(!GameClient.checksumValid) {
               if (GameWindow.bServerDisconnected) {
                  return;
               }

               if (System.currentTimeMillis() > var37 + 8000L) {
                  DebugLog.log("checksum: timed out waiting for the server to respond");
                  GameClient.connection.forceDisconnect();
                  GameWindow.bServerDisconnected = true;
                  GameWindow.kickReason = Translator.getText("UI_GameLoad_TimedOut");
                  return;
               }

               if (System.currentTimeMillis() > var41 + 1000L) {
                  DebugLog.log("checksum: waited one second");
                  var41 += 1000L;
               }

               NetChecksum.comparer.update();
               if (GameClient.checksumValid) {
                  break;
               }

               Thread.sleep(100L);
            }
         } catch (Exception var36) {
            var36.printStackTrace();
         }
      }

      GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_LoadTileDef");
      this.LoadTileDefinitionsPropertyStrings(this.spriteManager, "media/tiledefinitions.tiles", 0);
      this.LoadTileDefinitionsPropertyStrings(this.spriteManager, "media/newtiledefinitions.tiles", 1);
      this.LoadTileDefinitionsPropertyStrings(this.spriteManager, "media/tiledefinitions_erosion.tiles", 2);
      this.LoadTileDefinitionsPropertyStrings(this.spriteManager, "media/tiledefinitions_apcom.tiles", 3);
      ZomboidFileSystem.instance.loadModTileDefPropertyStrings();
      this.SetCustomPropertyValues();
      this.GenerateTilePropertyLookupTables();
      this.LoadTileDefinitions(this.spriteManager, "media/tiledefinitions.tiles", 0);
      this.LoadTileDefinitions(this.spriteManager, "media/newtiledefinitions.tiles", 1);
      this.LoadTileDefinitions(this.spriteManager, "media/tiledefinitions_erosion.tiles", 2);
      this.LoadTileDefinitions(this.spriteManager, "media/tiledefinitions_apcom.tiles", 3);
      this.JumboTreeDefinitions(this.spriteManager, 4);
      ZomboidFileSystem.instance.loadModTileDefs();
      GameLoadingState.GameLoadingString = "";
      this.spriteManager.AddSprite("media/ui/missing-tile.png");
      LuaEventManager.triggerEvent("OnLoadedTileDefinitions", this.spriteManager);
      String var38 = "media/newtiledefinitions_143.tiles";
      File var39 = new File(var38);
      if (!var39.exists()) {
         var38 = "media/newtiledefinitions.tiles";
      }

      if (GameServer.bServer && System.getProperty("softreset") != null) {
         WorldConverter.instance.softreset(this.spriteManager);
      }

      try {
         WeatherFxMask.init();
      } catch (Exception var33) {
         System.out.print(var33.getStackTrace());
      }

      IsoRegion.init();
      ObjectRenderEffects.init();
      WorldConverter.instance.convert(Core.GameSaveWorld, this.spriteManager);
      if (!GameLoadingState.build23Stop) {
         SandboxOptions.instance.handleOldZombiesFile2();
         GameTime.getInstance().init();
         GameTime.getInstance().load();
         ZomboidRadio.getInstance().Init(SavedWorldVersion);
         if (GameServer.bServer && Core.getInstance().getPoisonousBerry() == null) {
            Core.getInstance().initPoisonousBerry();
         }

         if (GameServer.bServer && Core.getInstance().getPoisonousMushroom() == null) {
            Core.getInstance().initPoisonousMushroom();
         }

         ErosionGlobals.Boot(this.spriteManager);
         if (GameServer.bServer) {
            SharedDescriptors.initSharedDescriptors();
         }

         VirtualZombieManager.instance.init();
         VehicleIDMap.instance.Reset();
         VehicleManager.instance = new VehicleManager();
         String var42 = this.playerCell;
         this.playerCell = this.x + "_" + this.y;
         KahluaTable var43 = LuaManager.env;
         Object[] var5 = LuaManager.caller.pcall(LuaManager.thread, var43.rawget("getMainCellLot"), instance.x, instance.y);
         if (var5.length > 1) {
            var42 = (String)var5[1];
         }

         this.LoadRemotenessVars();
         GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_InitMap");
         this.MetaGrid.CreateStep2();
         ClimateManager.getInstance().init(this.MetaGrid);
         SafeHouse.init();
         LuaEventManager.triggerEvent("OnLoadMapZones");
         if (ChunkRevisions.USE_CHUNK_REVISIONS) {
            ChunkRevisions.instance = new ChunkRevisions();
         }

         File var6 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_meta.bin");
         FileInputStream var7;
         BufferedInputStream var8;
         byte[] var10;
         if (var6.exists()) {
            var7 = new FileInputStream(var6);
            var8 = new BufferedInputStream(var7);
            if (SliceY.SliceBuffer == null) {
               SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
            }

            synchronized(SliceY.SliceBuffer) {
               SliceY.SliceBuffer.rewind();
               var10 = SliceY.SliceBuffer.array();
               var8.read(SliceY.SliceBuffer.array());
               SliceY.SliceBuffer.rewind();
               instance.MetaGrid.load(SliceY.SliceBuffer);
               SliceY.SliceBuffer.rewind();
            }

            try {
               var8.close();
            } catch (IOException var31) {
               var31.printStackTrace();
            }
         }

         var6 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_zone.bin");
         if (var6.exists()) {
            var7 = new FileInputStream(var6);
            var8 = new BufferedInputStream(var7);
            if (SliceY.SliceBuffer == null) {
               SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
            }

            synchronized(SliceY.SliceBuffer) {
               SliceY.SliceBuffer.rewind();
               var10 = SliceY.SliceBuffer.array();
               var8.read(SliceY.SliceBuffer.array());
               SliceY.SliceBuffer.rewind();
               instance.MetaGrid.loadZone(SliceY.SliceBuffer, -1);
               SliceY.SliceBuffer.rewind();
            }

            try {
               var8.close();
            } catch (IOException var29) {
               var29.printStackTrace();
            }
         }

         this.MetaGrid.processZones();
         if (GameServer.bServer) {
            ServerMap.instance.init(this.MetaGrid);
         }

         boolean var51 = false;
         File var52 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_p.bin");
         int var9 = 0;
         int var54 = 0;
         int var11 = 0;
         SafeHouse var13;
         if (var52.exists()) {
            var51 = true;
            if (!this.LoadPlayerForInfo()) {
               return;
            }

            WorldX = IsoChunkMap.SWorldX[IsoPlayer.getPlayerIndex()];
            WorldY = IsoChunkMap.SWorldY[IsoPlayer.getPlayerIndex()];
            var9 = IsoChunkMap.WorldXA;
            var54 = IsoChunkMap.WorldYA;
            var11 = IsoChunkMap.WorldZA;
         } else {
            var51 = false;
            if (GameClient.bClient && !ServerOptions.instance.SpawnPoint.getValue().isEmpty()) {
               String[] var12 = ServerOptions.instance.SpawnPoint.getValue().split(",");
               if (var12.length == 3) {
                  try {
                     IsoChunkMap.MPWorldXA = new Integer(var12[0].trim());
                     IsoChunkMap.MPWorldYA = new Integer(var12[1].trim());
                     IsoChunkMap.MPWorldZA = new Integer(var12[2].trim());
                  } catch (NumberFormatException var28) {
                     DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
                     IsoChunkMap.MPWorldXA = 0;
                     IsoChunkMap.MPWorldYA = 0;
                     IsoChunkMap.MPWorldZA = 0;
                  }
               } else {
                  DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
               }
            }

            if (this.getLuaSpawnCellX() < 0 || GameClient.bClient && (IsoChunkMap.MPWorldXA != 0 || IsoChunkMap.MPWorldYA != 0)) {
               if (GameClient.bClient) {
                  IsoChunkMap.WorldXA = IsoChunkMap.MPWorldXA;
                  IsoChunkMap.WorldYA = IsoChunkMap.MPWorldYA;
                  IsoChunkMap.WorldZA = IsoChunkMap.MPWorldZA;
                  WorldX = IsoChunkMap.WorldXA / 10;
                  WorldY = IsoChunkMap.WorldYA / 10;
               }
            } else {
               IsoChunkMap.WorldXA = this.getLuaPosX() + 300 * this.getLuaSpawnCellX();
               IsoChunkMap.WorldYA = this.getLuaPosY() + 300 * this.getLuaSpawnCellY();
               IsoChunkMap.WorldZA = this.getLuaPosZ();
               if (GameClient.bClient && ServerOptions.instance.SafehouseAllowRespawn.getValue()) {
                  for(int var55 = 0; var55 < SafeHouse.getSafehouseList().size(); ++var55) {
                     var13 = (SafeHouse)SafeHouse.getSafehouseList().get(var55);
                     if (var13.getPlayers().contains(GameClient.username)) {
                        IsoChunkMap.WorldXA = var13.getX() + var13.getH() / 2;
                        IsoChunkMap.WorldYA = var13.getY() + var13.getW() / 2;
                        IsoChunkMap.WorldZA = 0;
                     }
                  }
               }

               WorldX = IsoChunkMap.WorldXA / 10;
               WorldY = IsoChunkMap.WorldYA / 10;
            }
         }

         Core.getInstance();
         KahluaTable var56 = (KahluaTable)LuaManager.env.rawget("selectedDebugScenario");
         int var16;
         if (var56 != null) {
            KahluaTable var57 = (KahluaTable)var56.rawget("startLoc");
            int var14 = ((Double)var57.rawget("x")).intValue();
            int var15 = ((Double)var57.rawget("y")).intValue();
            var16 = ((Double)var57.rawget("z")).intValue();
            IsoChunkMap.WorldXA = var14;
            IsoChunkMap.WorldYA = var15;
            IsoChunkMap.WorldZA = var16;
            WorldX = IsoChunkMap.WorldXA / 10;
            WorldY = IsoChunkMap.WorldYA / 10;
         }

         MapCollisionData.instance.init(instance.getMetaGrid());
         ZombiePopulationManager.instance.init(instance.getMetaGrid());
         PolygonalMap2.instance.init(instance.getMetaGrid());
         GlobalObjectLookup.init(instance.getMetaGrid());
         WorldStreamer.instance.create();
         this.CurrentCell = CellLoader.LoadCellBinaryChunk(this.spriteManager, WorldX, WorldY);
         ClimateManager.getInstance().postCellLoadSetSnow();
         GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_LoadWorld");
         MapCollisionData.instance.start();

         while(WorldStreamer.instance.isBusy()) {
            try {
               Thread.sleep(100L);
            } catch (InterruptedException var27) {
               var27.printStackTrace();
            }
         }

         ArrayList var58 = new ArrayList();
         var58.addAll(IsoChunk.loadGridSquare);
         Iterator var59 = var58.iterator();

         while(var59.hasNext()) {
            IsoChunk var61 = (IsoChunk)var59.next();
            this.CurrentCell.ChunkMap[0].setChunkDirect(var61, false);
         }

         IsoChunk.bDoServerRequests = true;
         if (var51 && SystemDisabler.doPlayerCreation && !FrameLoader.bDedicated) {
            var13 = null;
            this.CurrentCell.getGridSquare(var9, var54, var11);
            this.CurrentCell.LoadPlayer(SavedWorldVersion);
            if (GameClient.bClient) {
               IsoPlayer.instance.setUsername(GameClient.username);
            }
         } else if (!FrameLoader.bDedicated) {
            ScriptCharacter var60 = ScriptManager.instance.FindCharacter("Player");
            if (var60 == null) {
               SurvivorDesc var62 = SurvivorFactory.CreateSurvivor();
               IsoGridSquare var63 = null;
               if (IsoPlayer.numPlayers == 0) {
                  IsoPlayer.numPlayers = 1;
               }

               IsoChunkMap var10000 = this.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()];
               var16 = IsoChunkMap.WorldXA;
               var10000 = this.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()];
               int var17 = IsoChunkMap.WorldYA;
               var10000 = this.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()];
               int var18 = IsoChunkMap.WorldZA;
               if (GameClient.bClient && !ServerOptions.instance.SpawnPoint.getValue().isEmpty()) {
                  String[] var19 = ServerOptions.instance.SpawnPoint.getValue().split(",");
                  if (var19.length != 3) {
                     DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
                  } else {
                     try {
                        int var20 = new Integer(var19[0].trim());
                        int var21 = new Integer(var19[1].trim());
                        int var22 = new Integer(var19[2].trim());
                        if (GameClient.bClient && ServerOptions.instance.SafehouseAllowRespawn.getValue()) {
                           for(int var23 = 0; var23 < SafeHouse.getSafehouseList().size(); ++var23) {
                              SafeHouse var24 = (SafeHouse)SafeHouse.getSafehouseList().get(var23);
                              if (var24.getPlayers().contains(GameClient.username)) {
                                 var20 = var24.getX() + var24.getH() / 2;
                                 var21 = var24.getY() + var24.getW() / 2;
                                 var22 = 0;
                              }
                           }
                        }

                        if (this.CurrentCell.getGridSquare(var20, var21, var22) != null) {
                           var16 = var20;
                           var17 = var21;
                           var18 = var22;
                        }
                     } catch (NumberFormatException var35) {
                        DebugLog.log("ERROR: SpawnPoint must be x,y,z, got \"" + ServerOptions.instance.SpawnPoint.getValue() + "\"");
                     }
                  }
               }

               var63 = this.CurrentCell.getGridSquare(var16, var17, var18);
               if (SystemDisabler.doPlayerCreation && !GameServer.bServer) {
                  if (var63 != null && var63.isFree(false) && var63.getRoom() != null) {
                     IsoGridSquare var64 = var63;
                     var63 = var63.getRoom().getFreeTile();
                     if (var63 == null) {
                        var63 = var64;
                     }
                  }

                  IsoPlayer var65 = null;
                  Core.getInstance();
                  if (this.getLuaPlayerDesc() != null) {
                     if (GameClient.bClient && ServerOptions.instance.SafehouseAllowRespawn.getValue()) {
                        var63 = this.CurrentCell.getGridSquare(IsoChunkMap.WorldXA, IsoChunkMap.WorldYA, IsoChunkMap.WorldZA);
                        if (var63 != null && var63.isFree(false) && var63.getRoom() != null) {
                           IsoGridSquare var66 = var63;
                           var63 = var63.getRoom().getFreeTile();
                           if (var63 == null) {
                              var63 = var66;
                           }
                        }
                     }

                     if (var63 == null) {
                        throw new RuntimeException("can't create player at x,y,z=" + var16 + "," + var17 + "," + var18 + " because the square is null");
                     }

                     WorldSimulation.instance.create();
                     var65 = new IsoPlayer(instance.CurrentCell, this.getLuaPlayerDesc(), var63.getX(), var63.getY(), var63.getZ());
                     if (GameClient.bClient) {
                        var65.setUsername(GameClient.username);
                     }

                     var65.setDir(IsoDirections.SE);
                     IsoPlayer.players[0] = var65;
                     IsoPlayer.instance = var65;
                     IsoCamera.CamCharacter = var65;
                  }

                  IsoPlayer var67 = IsoPlayer.getInstance();
                  var67.applyTraits(this.getLuaTraits());
                  ProfessionFactory.Profession var68 = ProfessionFactory.getProfession(var67.getDescriptor().getProfession());
                  Iterator var69;
                  String var70;
                  if (var68 != null && !var68.getFreeRecipes().isEmpty()) {
                     var69 = var68.getFreeRecipes().iterator();

                     while(var69.hasNext()) {
                        var70 = (String)var69.next();
                        var67.getKnownRecipes().add(var70);
                     }
                  }

                  var69 = this.getLuaTraits().iterator();

                  label362:
                  while(true) {
                     TraitFactory.Trait var71;
                     do {
                        do {
                           if (!var69.hasNext()) {
                              if (!GameClient.bClient) {
                                 StashSystem.init();
                              }

                              if (var63 != null && var63.getRoom() != null) {
                                 var63.getRoom().def.setExplored(true);
                                 var63.getRoom().building.setAllExplored(true);
                                 if (!GameServer.bServer && !GameClient.bClient) {
                                    ZombiePopulationManager.instance.playerSpawnedAt(var63.getX(), var63.getY(), var63.getZ());
                                 }
                              }

                              var67.createKeyRing();
                              if (!GameClient.bClient) {
                                 Core.getInstance().initPoisonousBerry();
                                 Core.getInstance().initPoisonousMushroom();
                              }

                              var67.addSmallInjuries();
                              LuaEventManager.triggerEvent("OnNewGame", var65, var63);
                              break label362;
                           }

                           var70 = (String)var69.next();
                           var71 = TraitFactory.getTrait(var70);
                        } while(var71 == null);
                     } while(var71.getFreeRecipes().isEmpty());

                     Iterator var25 = var71.getFreeRecipes().iterator();

                     while(var25.hasNext()) {
                        String var26 = (String)var25.next();
                        var67.getKnownRecipes().add(var26);
                     }
                  }
               }
            }
         }

         TutorialManager.instance.ActiveControlZombies = false;
         var3 = null;
         ReanimatedPlayers.instance.loadReanimatedPlayers();
         ChunkSaveWorker.instance.LoadContainers();
         ScriptCharacter var44;
         if (!this.bLoaded && !GameServer.bServer && !FrameLoader.bClient && SystemDisabler.doPlayerCreation) {
            var44 = ScriptManager.instance.FindCharacter("Player");
         }

         int var45;
         if (IsoPlayer.getInstance() != null) {
            if (GameClient.bClient) {
               var45 = (int)IsoPlayer.getInstance().getX();
               int var46 = (int)IsoPlayer.getInstance().getY();
               int var49 = (int)IsoPlayer.getInstance().getZ();

               while(var49 > 0) {
                  IsoGridSquare var53 = this.CurrentCell.getGridSquare(var45, var46, var49);
                  if (var53 != null && var53.TreatAsSolidFloor()) {
                     break;
                  }

                  --var49;
                  IsoPlayer.getInstance().setZ((float)var49);
               }
            }

            IsoPlayer.getInstance().setCurrent(this.CurrentCell.getGridSquare((int)IsoPlayer.getInstance().getX(), (int)IsoPlayer.getInstance().getY(), (int)IsoPlayer.getInstance().getZ()));
         }

         if (!this.bLoaded) {
            if (!this.CurrentCell.getBuildingList().isEmpty()) {
               boolean var47 = true;
               KahluaTable var48 = LuaManager.env;
               Object[] var50 = LuaManager.caller.pcall(LuaManager.thread, var48.rawget("getStartIndoorZombiesByGrid"), this.x, this.y);
               if (var50.length > 1) {
                  var45 = ((Double)var50[1]).intValue();
               }
            }

            var44 = ScriptManager.instance.getCharacter("KateAndBaldspot.Kate");
            if (var44 != null) {
               TutorialManager.instance.wife = (IsoSurvivor)var44.Actual;
            }

            if (!this.bLoaded) {
               this.PopulateCellWithSurvivors();
            }
         }

         if (IsoPlayer.players[0] != null && !this.CurrentCell.getObjectList().contains(IsoPlayer.players[0])) {
            this.CurrentCell.getObjectList().add(IsoPlayer.players[0]);
         }

         LightingThread.instance.create();
         GameLoadingState.GameLoadingString = "";
      }
   }

   public ArrayList getLuaTraits() {
      if (this.luatraits == null) {
         this.luatraits = new ArrayList();
      }

      return this.luatraits;
   }

   public void addLuaTrait(String var1) {
      this.getLuaTraits().add(var1);
   }

   public SurvivorDesc getLuaPlayerDesc() {
      return this.luaDesc;
   }

   public void setLuaPlayerDesc(SurvivorDesc var1) {
      this.luaDesc = var1;
   }

   public void KillCell() {
      this.helicopter.deactivate();
      CollisionManager.instance.ContactMap.clear();
      IsoDeadBody.Reset();
      FliesSound.instance.Reset();
      IsoObjectPicker.Instance.Init();
      IsoChunkMap.SharedChunks.clear();
      SoundManager.instance.StopMusic();
      WorldSoundManager.instance.KillCell();
      ZombieGroupManager.instance.Reset();
      this.CurrentCell.Dispose();
      this.CurrentCell = null;
      CellLoader.wanderRoom = null;
      IsoLot.Dispose();
      IsoGameCharacter.getSurvivorMap().clear();
      IsoPlayer.getInstance().setCurrent((IsoGridSquare)null);
      IsoPlayer.getInstance().setLast((IsoGridSquare)null);
      IsoPlayer.getInstance().square = null;
      ItemContainerFiller.Containers.clear();
      ItemContainerFiller.DistributionTarget.clear();
      instance.Groups.clear();
      RainManager.reset();
      IsoFireManager.Reset();
      this.MetaGrid.Dispose();
      this.MetaGrid = null;
      this.spriteManager = null;
      instance = new IsoWorld();
   }

   public void setDrawWorld(boolean var1) {
      this.bDrawWorld = var1;
   }

   public void render() {
      if (this.bDrawWorld) {
         if (IsoCamera.CamCharacter != null) {
            ++this.savePlayerCount;
            if (this.savePlayerCount > PerformanceSettings.LockFPS * 60) {
               GameWindow.savePlayer();
               this.savePlayerCount = 0;
            }

            int var1 = PerformanceSettings.numberOf3D;
            switch(PerformanceSettings.numberOf3D) {
            case 1:
               var1 = 1;
               break;
            case 2:
               var1 = 2;
               break;
            case 3:
               var1 = 3;
               break;
            case 4:
               var1 = 4;
               break;
            case 5:
               var1 = 5;
               break;
            case 6:
               var1 = 8;
               break;
            case 7:
               var1 = 10;
               break;
            case 8:
               var1 = 20;
               break;
            case 9:
               var1 = 20000;
            }

            var1 += PerformanceSettings.numberOf3DAlt;
            ModelManager.instance.returnContext = false;

            try {
               synchronized(this.CurrentCell.getZombieList()) {
                  this.zombieWithModel.clear();
                  int var3;
                  int var5;
                  IsoZombie var19;
                  if (var1 >= this.CurrentCell.getZombieList().size()) {
                     for(var3 = 0; var3 < this.CurrentCell.getZombieList().size(); ++var3) {
                        var19 = (IsoZombie)this.CurrentCell.getZombieList().get(var3);

                        for(var5 = 0; var5 < IsoPlayer.numPlayers; ++var5) {
                           IsoPlayer var20 = IsoPlayer.players[var5];
                           if (var20 != null && var19.current != null && var19.alpha[var5] > 0.0F) {
                              if (!this.zombieWithModel.contains(var19)) {
                                 this.zombieWithModel.add(var19);
                              }
                              break;
                           }
                        }
                     }
                  } else {
                     for(var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
                        IsoPlayer var4 = IsoPlayer.players[var3];
                        if (var4 != null) {
                           compDistToPlayer.px = var4.getX();
                           compDistToPlayer.py = var4.getY();
                           Collections.sort(this.CurrentCell.getZombieList(), compDistToPlayer);
                           var5 = 0;
                           int var6 = 0;

                           for(int var7 = 0; var7 < this.CurrentCell.getZombieList().size(); ++var7) {
                              IsoZombie var8 = (IsoZombie)this.CurrentCell.getZombieList().get(var7);
                              if (var1 > var5 && var8.current != null && var8.alpha[var3] > 0.0F) {
                                 if (!this.zombieWithModel.contains(var8)) {
                                    this.zombieWithModel.add(var8);
                                 }

                                 ++var5;
                              }

                              ++var6;
                           }
                        }
                     }
                  }

                  for(var3 = 0; var3 < this.CurrentCell.getZombieList().size(); ++var3) {
                     var19 = (IsoZombie)this.CurrentCell.getZombieList().get(var3);
                     if (this.zombieWithModel.contains(var19)) {
                        var19.setModel(var19.isFemale() ? "kate" : "male");
                     } else {
                        var19.setModel((String)null);
                     }
                  }
               }
            } catch (Exception var17) {
               ExceptionLogger.logException(var17);
            } finally {
               ModelManager.instance.returnContext = true;
            }

            try {
               long var2 = System.nanoTime();
               WeatherFxMask.initMask();
               this.CurrentCell.render();
               PolygonalMap2.instance.render();
               LineDrawer.render();
               this.sky.draw();
               WeatherFxMask.renderFxMask(IsoCamera.frameState.playerIndex);
               SkyBox.getInstance().render();
            } catch (Throwable var15) {
               ExceptionLogger.logException(var15);
            }

         }
      }
   }

   public void primUpdate() {
   }

   public void update() {
      try {
         if (GameServer.bServer) {
            VehicleManager.instance.serverUpdate();
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }

      WorldSimulation.instance.update();
      this.helicopter.update();
      long var1 = System.currentTimeMillis();
      if (var1 - this.emitterUpdateMS >= 30L) {
         this.emitterUpdateMS = var1;
         this.emitterUpdate = true;
      } else {
         this.emitterUpdate = false;
      }

      for(int var3 = 0; var3 < this.currentEmitters.size(); ++var3) {
         BaseSoundEmitter var4 = (BaseSoundEmitter)this.currentEmitters.get(var3);
         if (this.emitterUpdate || var4.hasSoundsToStart()) {
            var4.tick();
         }

         if (var4.isEmpty()) {
            this.currentEmitters.remove(var3);
            this.freeEmitters.push(var4);
            --var3;
         }
      }

      AutoZombieManager.instance.update();
      if (!GameClient.bClient && !GameServer.bServer) {
         IsoMetaCell var9 = this.MetaGrid.getCurrentCellData();
         if (var9 != null) {
            var9.checkTriggers();
         }
      }

      WorldSoundManager.instance.initFrame();
      ZombieGroupManager.instance.preupdate();
      OnceEvery.update();
      CollisionManager.instance.initUpdate();
      boolean var11 = false;
      int var10 = this.cellSurvivorSpawns;
      if (IsoPlayer.DemoMode) {
         boolean var12 = true;
      }

      for(var10 = 0; var10 < this.CurrentCell.getBuildingList().size(); ++var10) {
         ((IsoBuilding)this.CurrentCell.getBuildingList().get(var10)).update();
      }

      long var13 = System.nanoTime();
      ClimateManager.getInstance().update();
      ObjectRenderEffects.updateStatic();
      this.CurrentCell.update();
      IsoRegion.update();
      CollisionManager.instance.ResolveContacts();

      for(int var6 = 0; var6 < this.AddCoopPlayers.size(); ++var6) {
         AddCoopPlayer var7 = (AddCoopPlayer)this.AddCoopPlayers.get(var6);
         var7.update();
         if (var7.isFinished()) {
            this.AddCoopPlayers.remove(var6--);
         }
      }

   }

   public IsoCell getCell() {
      return this.CurrentCell;
   }

   private void PopulateCellWithSurvivors() {
   }

   public int getWorldSquareY() {
      return this.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY * 10;
   }

   public int getWorldSquareX() {
      return this.CurrentCell.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX * 10;
   }

   public IsoMetaChunk getMetaChunk(int var1, int var2) {
      return this.MetaGrid.getChunkData(var1, var2);
   }

   public IsoMetaChunk getMetaChunkFromTile(int var1, int var2) {
      return this.MetaGrid.getChunkDataFromTile(var1, var2);
   }

   public float getGlobalTemperature() {
      return ClimateManager.getInstance().getTemperature();
   }

   public void setGlobalTemperature(float var1) {
   }

   public String getWeather() {
      return this.weather;
   }

   public void setWeather(String var1) {
      this.weather = var1;
   }

   public int getLuaSpawnCellX() {
      return this.luaSpawnCellX;
   }

   public void setLuaSpawnCellX(int var1) {
      this.luaSpawnCellX = var1;
   }

   public int getLuaSpawnCellY() {
      return this.luaSpawnCellY;
   }

   public void setLuaSpawnCellY(int var1) {
      this.luaSpawnCellY = var1;
   }

   public int getLuaPosX() {
      return this.luaPosX;
   }

   public void setLuaPosX(int var1) {
      this.luaPosX = var1;
   }

   public int getLuaPosY() {
      return this.luaPosY;
   }

   public void setLuaPosY(int var1) {
      this.luaPosY = var1;
   }

   public int getLuaPosZ() {
      return this.luaPosZ;
   }

   public void setLuaPosZ(int var1) {
      this.luaPosZ = var1;
   }

   public String getWorld() {
      return Core.GameSaveWorld;
   }

   public void transmitWeather() {
      if (GameServer.bServer) {
         GameServer.sendWeather();
      }
   }

   public boolean isValidSquare(int var1, int var2, int var3) {
      return var3 >= 0 && var3 < 8 ? this.MetaGrid.isValidSquare(var1, var2) : false;
   }

   public ArrayList getRandomizedBuildingList() {
      return this.randomizedBuildingList;
   }

   public void setRandomizedBuildingList(ArrayList var1) {
      this.randomizedBuildingList = var1;
   }

   public RandomizedBuildingBase getRBBasic() {
      return this.RBBasic;
   }

   public void setRBBasic(RandomizedBuildingBase var1) {
      this.RBBasic = var1;
   }

   public String getDifficulty() {
      return Core.getDifficulty();
   }

   public void setDifficulty(String var1) {
      Core.setDifficulty(var1);
   }

   public static boolean getZombiesDisabled() {
      return NoZombies || !SystemDisabler.doZombieCreation || SandboxOptions.instance.Zombies.getValue() == 5;
   }

   public static boolean getZombiesEnabled() {
      return !getZombiesDisabled();
   }

   public ClimateManager getClimateManager() {
      return ClimateManager.getInstance();
   }

   public static int getWorldVersion() {
      return 143;
   }

   public class Frame {
      public ArrayList xPos = new ArrayList();
      public ArrayList yPos = new ArrayList();
      public ArrayList Type = new ArrayList();

      public Frame() {
         Iterator var2 = IsoWorld.instance.CurrentCell.getObjectList().iterator();

         while(var2 != null && var2.hasNext()) {
            IsoMovingObject var3 = (IsoMovingObject)var2.next();
            boolean var4 = true;
            byte var5;
            if (var3 instanceof IsoPlayer) {
               var5 = 0;
            } else if (var3 instanceof IsoSurvivor) {
               var5 = 1;
            } else {
               if (!(var3 instanceof IsoZombie) || ((IsoZombie)var3).Ghost) {
                  continue;
               }

               var5 = 2;
            }

            this.xPos.add((int)var3.getX());
            this.yPos.add((int)var3.getY());
            this.Type.add(Integer.valueOf(var5));
         }

      }
   }

   public static class MetaCell {
      public int x;
      public int y;
      public int zombieCount;
      public IsoDirections zombieMigrateDirection;
      public int[][] from = new int[3][3];
   }

   private static class CompDistToPlayer implements Comparator {
      public float px;
      public float py;

      private CompDistToPlayer() {
      }

      public int compare(IsoZombie var1, IsoZombie var2) {
         float var3 = IsoUtils.DistanceManhatten((float)((int)var1.x), (float)((int)var1.y), this.px, this.py);
         float var4 = IsoUtils.DistanceManhatten((float)((int)var2.x), (float)((int)var2.y), this.px, this.py);
         if (var3 < var4) {
            return -1;
         } else {
            return var3 > var4 ? 1 : 0;
         }
      }

      // $FF: synthetic method
      CompDistToPlayer(Object var1) {
         this();
      }
   }
}
