package zombie.iso;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameApplet;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.ReanimatedPlayers;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaManager;
import zombie.ai.astar.AStarPathMap;
import zombie.ai.astar.Mover;
import zombie.ai.astar.TileBasedMap;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoLivingCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.bucket.Bucket;
import zombie.core.bucket.BucketManager;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.RenderThread;
import zombie.core.physics.WorldSimulation;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.ImageData;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.utils.IntGrid;
import zombie.core.utils.OnceEvery;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.debug.LineDrawer;
import zombie.erosion.utils.Noise2D;
import zombie.gameStates.GameLoadingState;
import zombie.input.JoypadManager;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.BuildingScore;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.IsoRoomExit;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.fx.IsoWeatherFX;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.UIManager;
import zombie.vehicles.BaseVehicle;

public class IsoCell extends Bucket implements TileBasedMap {
   public static int MaxHeight = 8;
   private static int BarricadeDoorFrame = -1;
   static ArrayList stchoices = new ArrayList();
   public IsoChunkMap[] ChunkMap = new IsoChunkMap[4];
   public ArrayList BuildingList = new ArrayList();
   private ArrayList ObjectList = new ArrayList();
   private ArrayList PushableObjectList = new ArrayList();
   private HashMap BuildingScores = new HashMap();
   public AStarPathMap PathMap;
   private ArrayList RoomList = new ArrayList();
   private ArrayList StaticUpdaterObjectList = new ArrayList();
   private ArrayList wallArray = new ArrayList();
   private ArrayList ZombieList = new ArrayList();
   private ArrayList RemoteSurvivorList = new ArrayList();
   private ArrayList GhostList = new ArrayList();
   private ArrayList ZoneStack = new ArrayList();
   private ArrayList removeList = new ArrayList();
   private ArrayList addList = new ArrayList();
   private ArrayList RenderJobsArray = new ArrayList();
   private ArrayList ProcessIsoObject = new ArrayList();
   private ArrayList ProcessIsoObjectRemove = new ArrayList();
   private ArrayList ProcessItems = new ArrayList();
   private ArrayList ProcessItemsRemove = new ArrayList();
   private ArrayList ProcessWorldItems = new ArrayList();
   public ArrayList ProcessWorldItemsRemove = new ArrayList();
   private IsoGridSquare[][] gridSquares;
   public static boolean ENABLE_SQUARE_CACHE = true;
   private HashMap RenderJobsMapArray;
   private int height;
   private int width;
   private int worldX;
   private int worldY;
   private static int SheetCurtains = -1;
   public String filename;
   public IntGrid DangerScore;
   private boolean safeToAdd;
   private Stack LamppostPositions;
   public ArrayList roomDefs;
   public ArrayList roomLights;
   private final ArrayList heatSources;
   public ArrayList addVehicles;
   public ArrayList vehicles;
   public static final int ISOANGLEFACTOR = 3;
   private static final int ZOMBIESCANBUDGET = 10;
   private static final float NEARESTZOMBIEDISTSQRMAX = 150.0F;
   private int zombieScanCursor;
   private IsoZombie[] nearestVisibleZombie;
   private float[] nearestVisibleZombieDistSqr;
   private static Stack buildingscores = new Stack();
   static ArrayList GridStack = null;
   public static final int RTF_MinusFloorCharacters = 4;
   public static final int RTF_SolidFloor = 1;
   public static final int RTF_VegetationCorpses = 2;
   public static final int RTF_ShadedFloor = 8;
   private static final ArrayList MinusFloorCharacters = new ArrayList(1000);
   private static final ArrayList SolidFloor = new ArrayList(5000);
   private static final ArrayList ShadedFloor = new ArrayList(5000);
   private static final ArrayList VegetationCorpses = new ArrayList(5000);
   public static final IsoCell.PerPlayerRender[] perPlayerRender = new IsoCell.PerPlayerRender[4];
   private final int[] StencilXY;
   private final int[] StencilXY2z;
   boolean everDone;
   public int StencilX1;
   public int StencilY1;
   public int StencilX2;
   public int StencilY2;
   public static boolean newRender = true;
   private final DiamondMatrixIterator diamondMatrixIterator;
   private final Vector2i diamondMatrixPos;
   public int DeferredCharacterTick;
   private boolean hasSetupSnowGrid;
   private IsoCell.SnowGridTiles snowGridTiles_Square;
   private IsoCell.SnowGridTiles[] snowGridTiles_Strip;
   private IsoCell.SnowGridTiles[] snowGridTiles_Edge;
   private IsoCell.SnowGridTiles[] snowGridTiles_Cove;
   private IsoCell.SnowGridTiles snowGridTiles_Enclosed;
   private Noise2D snowNoise2D;
   private IsoCell.SnowGrid snowGridCur;
   private IsoCell.SnowGrid snowGridPrev;
   private int snowFracTarget;
   private long snowFadeTime;
   private float snowTransitionTime;
   private int raport;
   public boolean recalcFloors;
   private HashSet stairsNodes;
   static int wx;
   static int wy;
   public boolean bDoLotConnect;
   KahluaTable[] drag;
   ArrayList SurvivorList;
   public static boolean bReadAltLight = false;
   int jumptot;
   int jumpcount;
   int jumpavr;
   private Stack tempZoneStack;
   static int colu = 0;
   static int coll = 0;
   static int colr = 0;
   static int cold = 0;
   private static Texture texWhite;
   private static IsoCell instance;
   private int currentLX;
   private int currentLY;
   private int currentLZ;
   int recalcShading;
   int lastMinX;
   int lastMinY;
   OnceEvery lightUpdate;
   int alt;
   public Color staticBlack;
   Thread lightingThread;
   private float rainScroll;
   private int[] rainX;
   private int[] rainY;
   private Texture[] rainTextures;
   private long[] rainFileTime;
   private float rainAlphaMax;
   private float[] rainAlpha;
   protected int rainIntensity;
   protected int rainSpeed;
   int lightUpdateCount;
   public boolean bSwappingLightBuffers;
   public boolean bRendering;
   boolean[] bHideFloors;
   int[] unhideFloorsCounter;
   boolean bOccludedByOrphanStructureFlag;
   ArrayList playerOccluderBuildings;
   IsoBuilding[][] playerOccluderBuildingsArr;
   boolean[] playerWindowPeeking;
   boolean[] playerHidesOrphanStructures;
   IsoGridSquare[] lastPlayerSquare;
   boolean[] lastPlayerSquareHalf;
   IsoDirections[] lastPlayerCardinalDir;
   int hidesOrphanStructuresAbove;
   Rectangle buildingRectTemp;
   ArrayList zombieOccluderBuildings;
   IsoBuilding[][] zombieOccluderBuildingsArr;
   IsoGridSquare[] lastZombieSquare;
   boolean[] lastZombieSquareHalf;
   ArrayList otherOccluderBuildings;
   IsoBuilding[][] otherOccluderBuildingsArr;
   final int mustSeeSquaresRadius;
   final int mustSeeSquaresGridSize;
   ArrayList gridSquaresTempLeft;
   ArrayList gridSquaresTempRight;
   private IsoWeatherFX weatherFX;
   private IsoSprite woodWallN;
   private IsoSprite woodWallW;
   private IsoSprite woodDWallN;
   private IsoSprite woodDWallW;
   private IsoSprite woodWWallN;
   private IsoSprite woodWWallW;
   private IsoSprite woodDoorW;
   private IsoSprite woodDoorN;
   private IsoSprite woodFloor;
   private IsoSprite woodBarricade;
   private IsoSprite woodCrate;
   private IsoSprite woodStairsNB;
   private IsoSprite woodStairsNM;
   private IsoSprite woodStairsNT;
   private IsoSprite woodStairsWB;
   private IsoSprite woodStairsWM;
   private IsoSprite woodStairsWT;
   private int minX;
   private int maxX;
   private int minY;
   private int maxY;
   private int minZ;
   private int maxZ;
   private OnceEvery dangerUpdate;
   private Thread LightInfoUpdate;
   private Stack SpottedRooms;
   private IsoZombie fakeZombieForHit;

   public static int getMaxHeight() {
      return MaxHeight;
   }

   public LotHeader getCurrentLotHeader() {
      IsoChunk var1 = this.getChunkForGridSquare((int)IsoCamera.CamCharacter.x, (int)IsoCamera.CamCharacter.y, (int)IsoCamera.CamCharacter.z);
      return var1.lotheader;
   }

   public IsoChunkMap getChunkMap(int var1) {
      return this.ChunkMap[var1];
   }

   public static int getBarricadeDoorFrame() {
      return BarricadeDoorFrame;
   }

   public static void setBarricadeDoorFrame(int var0) {
      BarricadeDoorFrame = var0;
   }

   public static int getSheetCurtains() {
      return SheetCurtains;
   }

   public static void setSheetCurtains(int var0) {
      SheetCurtains = var0;
   }

   public IsoGridSquare getFreeTile(RoomDef var1) {
      stchoices.clear();

      for(int var2 = 0; var2 < var1.rects.size(); ++var2) {
         RoomDef.RoomRect var3 = (RoomDef.RoomRect)var1.rects.get(var2);

         for(int var4 = var3.x; var4 < var3.x + var3.w; ++var4) {
            for(int var5 = var3.y; var5 < var3.y + var3.h; ++var5) {
               IsoGridSquare var6 = this.getGridSquare(var4, var5, var1.level);
               if (var6 != null) {
                  var6.setCachedIsFree(false);
                  var6.setCacheIsFree(false);
                  if (var6.isFree(false)) {
                     stchoices.add(var6);
                  }
               }
            }
         }
      }

      if (stchoices.isEmpty()) {
         return null;
      } else {
         IsoGridSquare var7 = (IsoGridSquare)stchoices.get(Rand.Next(stchoices.size()));
         stchoices.clear();
         return var7;
      }
   }

   public static Stack getBuildings() {
      return buildingscores;
   }

   public static void setBuildings(Stack var0) {
      buildingscores = var0;
   }

   public IsoChunk getChunkForGridSquare(int var1, int var2, int var3) {
      int var4 = var1;
      int var5 = var2;

      for(int var6 = 0; var6 < IsoPlayer.numPlayers; ++var6) {
         if (!this.ChunkMap[var6].ignore) {
            var1 = var4 - this.ChunkMap[var6].getWorldXMinTiles();
            var2 = var5 - this.ChunkMap[var6].getWorldYMinTiles();
            if (var1 >= 0 && var2 >= 0) {
               IsoChunkMap var10001 = this.ChunkMap[var6];
               var1 /= 10;
               var10001 = this.ChunkMap[var6];
               var2 /= 10;
               IsoChunk var7 = null;
               var7 = this.ChunkMap[var6].getChunk(var1, var2);
               if (var7 != null) {
                  return var7;
               }
            }
         }
      }

      return null;
   }

   public IsoChunk getChunk(int var1, int var2) {
      for(int var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
         IsoChunkMap var4 = this.ChunkMap[var3];
         if (!var4.ignore) {
            IsoChunk var5 = var4.getChunk(var1 - var4.getWorldXMin(), var2 - var4.getWorldYMin());
            if (var5 != null) {
               return var5;
            }
         }
      }

      return null;
   }

   public IsoCell(int var1, int var2) {
      this.gridSquares = new IsoGridSquare[4][10 * IsoChunkMap.ChunkGridWidth * 10 * IsoChunkMap.ChunkGridWidth * 8];
      this.RenderJobsMapArray = new HashMap();
      this.safeToAdd = true;
      this.LamppostPositions = new Stack();
      this.roomDefs = new ArrayList();
      this.roomLights = new ArrayList();
      this.heatSources = new ArrayList();
      this.addVehicles = new ArrayList();
      this.vehicles = new ArrayList();
      this.zombieScanCursor = 0;
      this.nearestVisibleZombie = new IsoZombie[4];
      this.nearestVisibleZombieDistSqr = new float[4];
      this.StencilXY = new int[]{0, 0, -1, 0, 0, -1, -1, -1, -2, -1, -1, -2, -2, -2, -3, -2, -2, -3, -3, -3};
      this.StencilXY2z = new int[]{0, 0, -1, 0, 0, -1, -1, -1, -2, -1, -1, -2, -2, -2, -3, -2, -2, -3, -3, -3, -4, -3, -3, -4, -4, -4, -5, -4, -4, -5, -5, -5, -6, -5, -5, -6, -6, -6};
      this.everDone = false;
      this.diamondMatrixIterator = new DiamondMatrixIterator(123);
      this.diamondMatrixPos = new Vector2i();
      this.DeferredCharacterTick = 0;
      this.hasSetupSnowGrid = false;
      this.snowNoise2D = new Noise2D();
      this.snowFracTarget = 0;
      this.snowFadeTime = 0L;
      this.snowTransitionTime = 5000.0F;
      this.raport = 0;
      this.recalcFloors = false;
      this.stairsNodes = new HashSet();
      this.bDoLotConnect = true;
      this.drag = new KahluaTable[4];
      this.SurvivorList = new ArrayList();
      this.jumptot = 0;
      this.jumpcount = 0;
      this.jumpavr = 0;
      this.tempZoneStack = new Stack();
      this.currentLX = 0;
      this.currentLY = 0;
      this.currentLZ = 0;
      this.recalcShading = 30;
      this.lastMinX = -1234567;
      this.lastMinY = -1234567;
      this.lightUpdate = new OnceEvery(0.05F);
      this.alt = 0;
      this.staticBlack = new Color(0, 0, 0, 0);
      this.lightingThread = null;
      this.rainX = new int[4];
      this.rainY = new int[4];
      this.rainTextures = new Texture[5];
      this.rainFileTime = new long[5];
      this.rainAlphaMax = 0.6F;
      this.rainAlpha = new float[4];
      this.rainIntensity = 0;
      this.rainSpeed = 6;
      this.lightUpdateCount = 11;
      this.bSwappingLightBuffers = false;
      this.bRendering = false;
      this.bHideFloors = new boolean[4];
      this.unhideFloorsCounter = new int[4];
      this.bOccludedByOrphanStructureFlag = false;
      this.playerOccluderBuildings = new ArrayList(4);
      this.playerOccluderBuildingsArr = new IsoBuilding[4][];
      this.playerWindowPeeking = new boolean[4];
      this.playerHidesOrphanStructures = new boolean[4];
      this.lastPlayerSquare = new IsoGridSquare[4];
      this.lastPlayerSquareHalf = new boolean[4];
      this.lastPlayerCardinalDir = new IsoDirections[4];
      this.hidesOrphanStructuresAbove = MaxHeight;
      this.buildingRectTemp = new Rectangle();
      this.zombieOccluderBuildings = new ArrayList(4);
      this.zombieOccluderBuildingsArr = new IsoBuilding[4][];
      this.lastZombieSquare = new IsoGridSquare[4];
      this.lastZombieSquareHalf = new boolean[4];
      this.otherOccluderBuildings = new ArrayList(4);
      this.otherOccluderBuildingsArr = new IsoBuilding[4][];
      this.mustSeeSquaresRadius = 4;
      this.mustSeeSquaresGridSize = 10;
      this.gridSquaresTempLeft = new ArrayList(100);
      this.gridSquaresTempRight = new ArrayList(100);
      this.woodWallN = null;
      this.woodWallW = null;
      this.woodDWallN = null;
      this.woodDWallW = null;
      this.woodWWallN = null;
      this.woodWWallW = null;
      this.woodDoorW = null;
      this.woodDoorN = null;
      this.woodFloor = null;
      this.woodBarricade = null;
      this.woodCrate = null;
      this.woodStairsNB = null;
      this.woodStairsNM = null;
      this.woodStairsNT = null;
      this.woodStairsWB = null;
      this.woodStairsWM = null;
      this.woodStairsWT = null;
      this.dangerUpdate = new OnceEvery(0.4F, false);
      this.LightInfoUpdate = null;
      this.SpottedRooms = new Stack();
      IsoWorld.instance.CurrentCell = this;
      instance = this;
      this.width = var1;
      this.height = var2;

      int var3;
      for(var3 = 0; var3 < 4; ++var3) {
         this.ChunkMap[var3] = new IsoChunkMap(this);
         this.ChunkMap[var3].PlayerID = var3;
         this.ChunkMap[var3].ignore = var3 > 0;
         this.playerOccluderBuildings.add(new ArrayList(5));
         this.zombieOccluderBuildings.add(new ArrayList(5));
         this.otherOccluderBuildings.add(new ArrayList(5));
      }

      WorldReuserThread.instance.run();

      for(var3 = 0; var3 < var2; ++var3) {
      }

   }

   public IsoCell(IsoSpriteManager var1, int var2, int var3) {
      super(var1);
      this.gridSquares = new IsoGridSquare[4][10 * IsoChunkMap.ChunkGridWidth * 10 * IsoChunkMap.ChunkGridWidth * 8];
      this.RenderJobsMapArray = new HashMap();
      this.safeToAdd = true;
      this.LamppostPositions = new Stack();
      this.roomDefs = new ArrayList();
      this.roomLights = new ArrayList();
      this.heatSources = new ArrayList();
      this.addVehicles = new ArrayList();
      this.vehicles = new ArrayList();
      this.zombieScanCursor = 0;
      this.nearestVisibleZombie = new IsoZombie[4];
      this.nearestVisibleZombieDistSqr = new float[4];
      this.StencilXY = new int[]{0, 0, -1, 0, 0, -1, -1, -1, -2, -1, -1, -2, -2, -2, -3, -2, -2, -3, -3, -3};
      this.StencilXY2z = new int[]{0, 0, -1, 0, 0, -1, -1, -1, -2, -1, -1, -2, -2, -2, -3, -2, -2, -3, -3, -3, -4, -3, -3, -4, -4, -4, -5, -4, -4, -5, -5, -5, -6, -5, -5, -6, -6, -6};
      this.everDone = false;
      this.diamondMatrixIterator = new DiamondMatrixIterator(123);
      this.diamondMatrixPos = new Vector2i();
      this.DeferredCharacterTick = 0;
      this.hasSetupSnowGrid = false;
      this.snowNoise2D = new Noise2D();
      this.snowFracTarget = 0;
      this.snowFadeTime = 0L;
      this.snowTransitionTime = 5000.0F;
      this.raport = 0;
      this.recalcFloors = false;
      this.stairsNodes = new HashSet();
      this.bDoLotConnect = true;
      this.drag = new KahluaTable[4];
      this.SurvivorList = new ArrayList();
      this.jumptot = 0;
      this.jumpcount = 0;
      this.jumpavr = 0;
      this.tempZoneStack = new Stack();
      this.currentLX = 0;
      this.currentLY = 0;
      this.currentLZ = 0;
      this.recalcShading = 30;
      this.lastMinX = -1234567;
      this.lastMinY = -1234567;
      this.lightUpdate = new OnceEvery(0.05F);
      this.alt = 0;
      this.staticBlack = new Color(0, 0, 0, 0);
      this.lightingThread = null;
      this.rainX = new int[4];
      this.rainY = new int[4];
      this.rainTextures = new Texture[5];
      this.rainFileTime = new long[5];
      this.rainAlphaMax = 0.6F;
      this.rainAlpha = new float[4];
      this.rainIntensity = 0;
      this.rainSpeed = 6;
      this.lightUpdateCount = 11;
      this.bSwappingLightBuffers = false;
      this.bRendering = false;
      this.bHideFloors = new boolean[4];
      this.unhideFloorsCounter = new int[4];
      this.bOccludedByOrphanStructureFlag = false;
      this.playerOccluderBuildings = new ArrayList(4);
      this.playerOccluderBuildingsArr = new IsoBuilding[4][];
      this.playerWindowPeeking = new boolean[4];
      this.playerHidesOrphanStructures = new boolean[4];
      this.lastPlayerSquare = new IsoGridSquare[4];
      this.lastPlayerSquareHalf = new boolean[4];
      this.lastPlayerCardinalDir = new IsoDirections[4];
      this.hidesOrphanStructuresAbove = MaxHeight;
      this.buildingRectTemp = new Rectangle();
      this.zombieOccluderBuildings = new ArrayList(4);
      this.zombieOccluderBuildingsArr = new IsoBuilding[4][];
      this.lastZombieSquare = new IsoGridSquare[4];
      this.lastZombieSquareHalf = new boolean[4];
      this.otherOccluderBuildings = new ArrayList(4);
      this.otherOccluderBuildingsArr = new IsoBuilding[4][];
      this.mustSeeSquaresRadius = 4;
      this.mustSeeSquaresGridSize = 10;
      this.gridSquaresTempLeft = new ArrayList(100);
      this.gridSquaresTempRight = new ArrayList(100);
      this.woodWallN = null;
      this.woodWallW = null;
      this.woodDWallN = null;
      this.woodDWallW = null;
      this.woodWWallN = null;
      this.woodWWallW = null;
      this.woodDoorW = null;
      this.woodDoorN = null;
      this.woodFloor = null;
      this.woodBarricade = null;
      this.woodCrate = null;
      this.woodStairsNB = null;
      this.woodStairsNM = null;
      this.woodStairsNT = null;
      this.woodStairsWB = null;
      this.woodStairsWM = null;
      this.woodStairsWT = null;
      this.dangerUpdate = new OnceEvery(0.4F, false);
      this.LightInfoUpdate = null;
      this.SpottedRooms = new Stack();
      IsoWorld.instance.CurrentCell = this;
      instance = this;
      this.width = var2;
      this.height = var3;

      for(int var4 = 0; var4 < 4; ++var4) {
         this.ChunkMap[var4] = new IsoChunkMap(this);
         this.ChunkMap[var4].PlayerID = var4;
         this.ChunkMap[var4].ignore = var4 > 0;
         this.playerOccluderBuildings.add(new ArrayList(5));
         this.zombieOccluderBuildings.add(new ArrayList(5));
         this.otherOccluderBuildings.add(new ArrayList(5));
      }

      WorldReuserThread.instance.run();
   }

   public IsoCell(IsoSpriteManager var1, int var2, int var3, boolean var4) {
      super(var1);
      this.gridSquares = new IsoGridSquare[4][10 * IsoChunkMap.ChunkGridWidth * 10 * IsoChunkMap.ChunkGridWidth * 8];
      this.RenderJobsMapArray = new HashMap();
      this.safeToAdd = true;
      this.LamppostPositions = new Stack();
      this.roomDefs = new ArrayList();
      this.roomLights = new ArrayList();
      this.heatSources = new ArrayList();
      this.addVehicles = new ArrayList();
      this.vehicles = new ArrayList();
      this.zombieScanCursor = 0;
      this.nearestVisibleZombie = new IsoZombie[4];
      this.nearestVisibleZombieDistSqr = new float[4];
      this.StencilXY = new int[]{0, 0, -1, 0, 0, -1, -1, -1, -2, -1, -1, -2, -2, -2, -3, -2, -2, -3, -3, -3};
      this.StencilXY2z = new int[]{0, 0, -1, 0, 0, -1, -1, -1, -2, -1, -1, -2, -2, -2, -3, -2, -2, -3, -3, -3, -4, -3, -3, -4, -4, -4, -5, -4, -4, -5, -5, -5, -6, -5, -5, -6, -6, -6};
      this.everDone = false;
      this.diamondMatrixIterator = new DiamondMatrixIterator(123);
      this.diamondMatrixPos = new Vector2i();
      this.DeferredCharacterTick = 0;
      this.hasSetupSnowGrid = false;
      this.snowNoise2D = new Noise2D();
      this.snowFracTarget = 0;
      this.snowFadeTime = 0L;
      this.snowTransitionTime = 5000.0F;
      this.raport = 0;
      this.recalcFloors = false;
      this.stairsNodes = new HashSet();
      this.bDoLotConnect = true;
      this.drag = new KahluaTable[4];
      this.SurvivorList = new ArrayList();
      this.jumptot = 0;
      this.jumpcount = 0;
      this.jumpavr = 0;
      this.tempZoneStack = new Stack();
      this.currentLX = 0;
      this.currentLY = 0;
      this.currentLZ = 0;
      this.recalcShading = 30;
      this.lastMinX = -1234567;
      this.lastMinY = -1234567;
      this.lightUpdate = new OnceEvery(0.05F);
      this.alt = 0;
      this.staticBlack = new Color(0, 0, 0, 0);
      this.lightingThread = null;
      this.rainX = new int[4];
      this.rainY = new int[4];
      this.rainTextures = new Texture[5];
      this.rainFileTime = new long[5];
      this.rainAlphaMax = 0.6F;
      this.rainAlpha = new float[4];
      this.rainIntensity = 0;
      this.rainSpeed = 6;
      this.lightUpdateCount = 11;
      this.bSwappingLightBuffers = false;
      this.bRendering = false;
      this.bHideFloors = new boolean[4];
      this.unhideFloorsCounter = new int[4];
      this.bOccludedByOrphanStructureFlag = false;
      this.playerOccluderBuildings = new ArrayList(4);
      this.playerOccluderBuildingsArr = new IsoBuilding[4][];
      this.playerWindowPeeking = new boolean[4];
      this.playerHidesOrphanStructures = new boolean[4];
      this.lastPlayerSquare = new IsoGridSquare[4];
      this.lastPlayerSquareHalf = new boolean[4];
      this.lastPlayerCardinalDir = new IsoDirections[4];
      this.hidesOrphanStructuresAbove = MaxHeight;
      this.buildingRectTemp = new Rectangle();
      this.zombieOccluderBuildings = new ArrayList(4);
      this.zombieOccluderBuildingsArr = new IsoBuilding[4][];
      this.lastZombieSquare = new IsoGridSquare[4];
      this.lastZombieSquareHalf = new boolean[4];
      this.otherOccluderBuildings = new ArrayList(4);
      this.otherOccluderBuildingsArr = new IsoBuilding[4][];
      this.mustSeeSquaresRadius = 4;
      this.mustSeeSquaresGridSize = 10;
      this.gridSquaresTempLeft = new ArrayList(100);
      this.gridSquaresTempRight = new ArrayList(100);
      this.woodWallN = null;
      this.woodWallW = null;
      this.woodDWallN = null;
      this.woodDWallW = null;
      this.woodWWallN = null;
      this.woodWWallW = null;
      this.woodDoorW = null;
      this.woodDoorN = null;
      this.woodFloor = null;
      this.woodBarricade = null;
      this.woodCrate = null;
      this.woodStairsNB = null;
      this.woodStairsNM = null;
      this.woodStairsNT = null;
      this.woodStairsWB = null;
      this.woodStairsWM = null;
      this.woodStairsWT = null;
      this.dangerUpdate = new OnceEvery(0.4F, false);
      this.LightInfoUpdate = null;
      this.SpottedRooms = new Stack();
      IsoWorld.instance.CurrentCell = this;
      instance = this;
      this.width = var2;
      this.height = var3;

      for(int var5 = 0; var5 < 4; ++var5) {
         this.ChunkMap[var5] = new IsoChunkMap(this);
         this.ChunkMap[var5].PlayerID = var5;
         this.ChunkMap[var5].ignore = var5 > 0;
         this.playerOccluderBuildings.add(new ArrayList(5));
         this.zombieOccluderBuildings.add(new ArrayList(5));
         this.otherOccluderBuildings.add(new ArrayList(5));
      }

      WorldReuserThread.instance.run();
   }

   public short getStencilValue(int var1, int var2, int var3) {
      short[][][] var4 = perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;
      int var5 = 0;
      int var6 = 0;

      for(int var7 = 0; var7 < this.StencilXY.length; var7 += 2) {
         int var8 = -var3 * 3;
         int var9 = var1 + var8 + this.StencilXY[var7];
         int var10 = var2 + var8 + this.StencilXY[var7 + 1];
         if (var9 >= this.minX && var9 < this.maxX && var10 >= this.minY && var10 < this.maxY) {
            short[] var11 = var4[var9 - this.minX][var10 - this.minY];
            if (var11[0] != 0) {
               if (var5 == 0) {
                  var5 = var11[0];
                  var6 = var11[1];
               } else {
                  var5 = Math.min(var11[0], var5);
                  var6 = Math.max(var11[1], var6);
               }
            }
         }
      }

      if (var5 == 0) {
         return 1;
      } else if (var5 > 10) {
         return (short)(var5 - 10);
      } else {
         return (short)(var6 + 1);
      }
   }

   public void setStencilValue(int var1, int var2, int var3, int var4) {
      short[][][] var5 = perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;

      for(int var6 = 0; var6 < this.StencilXY.length; var6 += 2) {
         int var7 = -var3 * 3;
         int var8 = var1 + var7 + this.StencilXY[var6];
         int var9 = var2 + var7 + this.StencilXY[var6 + 1];
         if (var8 >= this.minX && var8 < this.maxX && var9 >= this.minY && var9 < this.maxY) {
            short[] var10 = var5[var8 - this.minX][var9 - this.minY];
            if (var10[0] == 0) {
               var10[0] = (short)var4;
               var10[1] = (short)var4;
            } else {
               var10[0] = (short)Math.min(var10[0], var4);
               var10[1] = (short)Math.max(var10[1], var4);
            }
         }
      }

   }

   public short getStencilValue2z(int var1, int var2, int var3) {
      short[][][] var4 = perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;
      int var5 = 0;
      int var6 = 0;
      int var7 = -var3 * 3;

      for(int var8 = 0; var8 < this.StencilXY2z.length; var8 += 2) {
         int var9 = var1 + var7 + this.StencilXY2z[var8];
         int var10 = var2 + var7 + this.StencilXY2z[var8 + 1];
         if (var9 >= this.minX && var9 < this.maxX && var10 >= this.minY && var10 < this.maxY) {
            short[] var11 = var4[var9 - this.minX][var10 - this.minY];
            if (var11[0] != 0) {
               if (var5 == 0) {
                  var5 = var11[0];
                  var6 = var11[1];
               } else {
                  var5 = Math.min(var11[0], var5);
                  var6 = Math.max(var11[1], var6);
               }
            }
         }
      }

      if (var5 == 0) {
         return 1;
      } else if (var5 > 10) {
         return (short)(var5 - 10);
      } else {
         return (short)(var6 + 1);
      }
   }

   public void setStencilValue2z(int var1, int var2, int var3, int var4) {
      short[][][] var5 = perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;
      int var6 = -var3 * 3;

      for(int var7 = 0; var7 < this.StencilXY2z.length; var7 += 2) {
         int var8 = var1 + var6 + this.StencilXY2z[var7];
         int var9 = var2 + var6 + this.StencilXY2z[var7 + 1];
         if (var8 >= this.minX && var8 < this.maxX && var9 >= this.minY && var9 < this.maxY) {
            short[] var10 = var5[var8 - this.minX][var9 - this.minY];
            if (var10[0] == 0) {
               var10[0] = (short)var4;
               var10[1] = (short)var4;
            } else {
               var10[0] = (short)Math.min(var10[0], var4);
               var10[1] = (short)Math.max(var10[1], var4);
            }
         }
      }

   }

   public void CalculateVertColoursForTile(IsoGridSquare var1, int var2, int var3, int var4, int var5) {
      IsoGridSquare var6 = !var1.visionMatrix[0][0][1] ? var1.nav[IsoDirections.NW.index()] : null;
      IsoGridSquare var7 = !var1.visionMatrix[1][0][1] ? var1.nav[IsoDirections.N.index()] : null;
      IsoGridSquare var8 = !var1.visionMatrix[2][0][1] ? var1.nav[IsoDirections.NE.index()] : null;
      IsoGridSquare var9 = !var1.visionMatrix[2][1][1] ? var1.nav[IsoDirections.E.index()] : null;
      IsoGridSquare var10 = !var1.visionMatrix[2][2][1] ? var1.nav[IsoDirections.SE.index()] : null;
      IsoGridSquare var11 = !var1.visionMatrix[1][2][1] ? var1.nav[IsoDirections.S.index()] : null;
      IsoGridSquare var12 = !var1.visionMatrix[0][2][1] ? var1.nav[IsoDirections.SW.index()] : null;
      IsoGridSquare var13 = !var1.visionMatrix[0][1][1] ? var1.nav[IsoDirections.W.index()] : null;
      this.CalculateColor(var6, var7, var13, var1, 0, var5);
      this.CalculateColor(var7, var8, var9, var1, 1, var5);
      this.CalculateColor(var10, var11, var9, var1, 2, var5);
      this.CalculateColor(var12, var11, var13, var1, 3, var5);
   }

   public void ClearVertArrays() {
      this.everDone = true;
      int var1 = MaxHeight;
   }

   public void DrawStencilMask() {
      Texture var1 = Texture.getSharedTexture("media/mask_circledithernew.png");
      if (var1 != null) {
         IndieGL.glStencilMask(255);
         IndieGL.glClear(1280);
         int var2 = IsoCamera.getOffscreenWidth(IsoPlayer.getPlayerIndex()) / 2;
         int var3 = IsoCamera.getOffscreenHeight(IsoPlayer.getPlayerIndex()) / 2;
         var2 -= var1.getWidth() / (2 / Core.TileScale);
         var3 -= var1.getHeight() / (2 / Core.TileScale);
         IndieGL.enableStencilTest();
         IndieGL.enableAlphaTest();
         IndieGL.glAlphaFunc(516, 0.1F);
         IndieGL.glStencilFunc(519, 128, 255);
         IndieGL.glStencilOp(7680, 7680, 7681);
         var1.renderstrip(var2 - (int)IsoCamera.RightClickX[IsoPlayer.getPlayerIndex()], var3 - (int)IsoCamera.RightClickY[IsoPlayer.getPlayerIndex()], var1.getWidth() * Core.TileScale, var1.getHeight() * Core.TileScale, 1.0F, 1.0F, 1.0F, 1.0F);
         IndieGL.glStencilFunc(519, 0, 255);
         IndieGL.glStencilOp(7680, 7680, 7680);
         IndieGL.glStencilMask(127);
         IndieGL.glAlphaFunc(519, 0.0F);
         this.StencilX1 = var2 - (int)IsoCamera.RightClickX[IsoPlayer.getPlayerIndex()];
         this.StencilY1 = var3 - (int)IsoCamera.RightClickY[IsoPlayer.getPlayerIndex()];
         this.StencilX2 = this.StencilX1 + var1.getWidth() * Core.TileScale;
         this.StencilY2 = this.StencilY1 + var1.getHeight() * Core.TileScale;
      }
   }

   public void RenderTiles(int var1) {
      int var2 = IsoCamera.frameState.playerIndex;
      IsoPlayer var3 = IsoPlayer.players[var2];
      var3.dirtyRecalcGridStackTime -= GameTime.getInstance().getMultiplier() / 4.0F;
      if (perPlayerRender[var2] == null) {
         perPlayerRender[var2] = new IsoCell.PerPlayerRender();
      }

      IsoCell.PerPlayerRender var4 = perPlayerRender[var2];
      var4.setSize(this.maxX - this.minX + 1, this.maxY - this.minY + 1);
      IsoGridStack var5 = var4.GridStacks;
      boolean[][][] var6 = var4.VisiOccludedFlags;
      boolean[][] var7 = var4.VisiCulledFlags;
      short[][][] var8 = var4.StencilValues;
      int var10;
      int var11;
      int var12;
      int var13;
      if (var3.dirtyRecalcGridStack) {
         var3.dirtyRecalcGridStack = false;
         IsoChunk var9 = -1;
         var10 = -1;
         var11 = -1;
         WeatherFxMask.DIAMOND_ITER_DONE = true;

         for(var12 = var1; var12 >= 0; --var12) {
            GridStack = (ArrayList)var5.Squares.get(var12);
            GridStack.clear();
            if (var12 < this.maxZ) {
               if (newRender) {
                  DiamondMatrixIterator var33 = this.diamondMatrixIterator.reset(this.maxX - this.minX);
                  IsoGridSquare var36 = null;
                  Vector2i var43 = this.diamondMatrixPos;

                  while(var33.next(var43)) {
                     if (var43.y < this.maxY - this.minY + 1) {
                        var36 = this.ChunkMap[var2].getGridSquare(var43.x + this.minX, var43.y + this.minY, var12);
                        if (var12 == 0) {
                           var6[var43.x][var43.y][0] = false;
                           var6[var43.x][var43.y][1] = false;
                           var7[var43.x][var43.y] = false;
                        }

                        if (var36 == null) {
                           WeatherFxMask.addMaskLocation((IsoGridSquare)null, var43.x + this.minX, var43.y + this.minY, var12);
                        } else {
                           IsoChunk var38 = var36.getChunk();
                           if (var38 != null && var36.IsOnScreen()) {
                              WeatherFxMask.addMaskLocation(var36, var43.x + this.minX, var43.y + this.minY, var12);
                              if (!this.IsDissolvedSquare(var36)) {
                                 var36.cacheLightInfo();
                                 ((ArrayList)var5.Squares.get(var12)).add(var36);
                              }
                           }
                        }
                     }
                  }
               } else {
                  label302:
                  for(var13 = this.minY; var13 < this.maxY; ++var13) {
                     int var14 = this.minX;
                     IsoGridSquare var15 = this.ChunkMap[var2].getGridSquare(var14, var13, var12);
                     int var16 = IsoDirections.E.index();

                     while(true) {
                        while(true) {
                           if (var14 >= this.maxX) {
                              continue label302;
                           }

                           if (var12 == 0) {
                              var6[var14 - this.minX][var13 - this.minY][0] = false;
                              var6[var14 - this.minX][var13 - this.minY][1] = false;
                              var7[var14 - this.minX][var13 - this.minY] = false;
                           }

                           if (var15 != null && var15.getY() != var13) {
                              var15 = null;
                           }

                           boolean var17 = true;
                           boolean var18 = true;
                           IsoChunkMap var10002 = this.ChunkMap[var2];
                           int var10001 = this.ChunkMap[var2].WorldX - IsoChunkMap.ChunkGridWidth / 2;
                           var10002 = this.ChunkMap[var2];
                           int var19 = var14 - var10001 * 10;
                           var10002 = this.ChunkMap[var2];
                           var10001 = this.ChunkMap[var2].WorldY - IsoChunkMap.ChunkGridWidth / 2;
                           var10002 = this.ChunkMap[var2];
                           int var20 = var13 - var10001 * 10;
                           IsoChunkMap var51 = this.ChunkMap[var2];
                           IsoChunk var48 = var19 / 10;
                           var51 = this.ChunkMap[var2];
                           var20 /= 10;
                           if (var48 != var9 || var20 != var10) {
                              var48 = this.ChunkMap[var2].getChunkForGridSquare(var14, var13);
                              if (var48 != null) {
                                 var11 = var48.maxLevel;
                              }
                           }

                           var9 = var48;
                           var10 = var20;
                           if (var11 < var12) {
                              ++var14;
                           } else {
                              if (var15 == null) {
                                 var15 = this.getGridSquare(var14, var13, var12);
                                 if (var15 == null) {
                                    var15 = this.ChunkMap[var2].getGridSquare(var14, var13, var12);
                                    if (var15 == null) {
                                       ++var14;
                                       continue;
                                    }
                                 }
                              }

                              var48 = var15.getChunk();
                              if (var48 != null && !var48.bLightingNeverDone[var2] && var15.IsOnScreen() && !this.IsDissolvedSquare(var15)) {
                                 var15.cacheLightInfo();
                                 GridStack.add(var15);
                              }

                              var15 = var15.nav[var16];
                              ++var14;
                           }
                        }
                     }
                  }
               }
            }
         }

         this.CullFullyOccludedSquares(var5, var6, var7);
      }

      ++this.DeferredCharacterTick;

      int var29;
      for(var29 = this.minY; var29 <= this.maxY; ++var29) {
         for(var10 = this.minX; var10 <= this.maxX; ++var10) {
            var8[var10 - this.minX][var29 - this.minY][0] = 0;
            var8[var10 - this.minX][var29 - this.minY][1] = 0;
         }
      }

      IsoGridSquare var40;
      for(var29 = 0; var29 < var1 + 1; ++var29) {
         GridStack = (ArrayList)var5.Squares.get(var29);
         SolidFloor.clear();
         ShadedFloor.clear();
         VegetationCorpses.clear();
         MinusFloorCharacters.clear();

         for(var10 = 0; var10 < GridStack.size(); ++var10) {
            IsoGridSquare var32 = (IsoGridSquare)GridStack.get(var10);
            var12 = var32.renderFloor(var32.getX() - this.minX, var32.getY());
            if (!var32.getStaticMovingObjects().isEmpty()) {
               var12 |= 2;
            }

            if (!var32.getWorldObjects().isEmpty()) {
               var12 |= 2;
            }

            for(var13 = 0; var13 < var32.getMovingObjects().size(); ++var13) {
               IsoMovingObject var37 = (IsoMovingObject)var32.getMovingObjects().get(var13);
               boolean var45 = var37.bOnFloor;
               if (var45 && var37 instanceof IsoZombie) {
                  IsoZombie var41 = (IsoZombie)var37;
                  var45 = var41.bCrawling || var41.legsSprite.CurrentAnim != null && var41.legsSprite.CurrentAnim.name.equals("ZombieDeath") && var41.def.isFinished();
               }

               if (var45) {
                  var12 |= 2;
               } else {
                  var12 |= 4;
               }
            }

            if (!var32.getDeferedCharacters().isEmpty()) {
               var12 |= 4;
            }

            if ((var12 & 1) != 0) {
               SolidFloor.add(var32);
            }

            if ((var12 & 8) != 0) {
               ShadedFloor.add(var32);
            }

            if ((var12 & 2) != 0) {
               VegetationCorpses.add(var32);
            }

            if ((var12 & 4) != 0) {
               MinusFloorCharacters.add(var32);
            }
         }

         if (!SolidFloor.isEmpty()) {
            this.RenderSnow(var29);
         }

         if (!GridStack.isEmpty()) {
            this.ChunkMap[var2].renderBloodForChunks(var29);
         }

         if (!ShadedFloor.isEmpty()) {
            this.RenderFloorShading(var29);
         }

         LuaEventManager.triggerEvent("OnPostFloorLayerDraw", var29);

         for(var11 = 0; var11 < VegetationCorpses.size(); ++var11) {
            var40 = (IsoGridSquare)VegetationCorpses.get(var11);
            var40.renderMinusFloor(this.minY, var40.getX() - this.minX, this.maxZ, this.currentLY, false, true);
            var40.renderCharacters(this.maxZ, true);
         }

         for(var11 = 0; var11 < MinusFloorCharacters.size(); ++var11) {
            var40 = (IsoGridSquare)MinusFloorCharacters.get(var11);
            this.currentLY = var40.getY() - this.minY;
            this.currentLZ = var29;
            boolean var34 = var40.renderMinusFloor(this.minY, var40.getX() - this.minX, this.maxZ, this.currentLY, false, false);
            var40.renderDeferredCharacters(this.maxZ);
            var40.renderCharacters(this.maxZ, false);
            if (var34) {
               var40.renderMinusFloor(this.minY, var40.getX() - this.minX, this.maxZ, this.currentLY, true, false);
            }
         }
      }

      MinusFloorCharacters.clear();
      ShadedFloor.clear();
      SolidFloor.clear();
      VegetationCorpses.clear();
      if (Core.bDebug && DebugOptions.instance.PhysicsRender.getValue()) {
         TextureDraw.GenericDrawer var30 = WorldSimulation.getDrawer(var2);
         SpriteRenderer.instance.drawGeneric(var30);
      }

      if (Core.bDebug && DebugOptions.instance.LightingRender.getValue()) {
         byte var31 = 1;

         for(var10 = 0; var10 < var1 + 1; ++var10) {
            GridStack = (ArrayList)var5.Squares.get(var10);

            for(var11 = 0; var11 < GridStack.size(); ++var11) {
               var40 = (IsoGridSquare)GridStack.get(var11);
               float var35 = IsoUtils.XToScreenExact((float)var40.x + 0.3F, (float)var40.y, 0.0F, 0);
               float var39 = IsoUtils.YToScreenExact((float)var40.x + 0.3F, (float)var40.y, 0.0F, 0);
               float var47 = IsoUtils.XToScreenExact((float)var40.x + 0.6F, (float)var40.y, 0.0F, 0);
               float var42 = IsoUtils.YToScreenExact((float)var40.x + 0.6F, (float)var40.y, 0.0F, 0);
               float var44 = IsoUtils.XToScreenExact((float)(var40.x + 1), (float)var40.y + 0.3F, 0.0F, 0);
               float var46 = IsoUtils.YToScreenExact((float)(var40.x + 1), (float)var40.y + 0.3F, 0.0F, 0);
               float var49 = IsoUtils.XToScreenExact((float)(var40.x + 1), (float)var40.y + 0.6F, 0.0F, 0);
               float var50 = IsoUtils.YToScreenExact((float)(var40.x + 1), (float)var40.y + 0.6F, 0.0F, 0);
               float var21 = IsoUtils.XToScreenExact((float)var40.x + 0.6F, (float)(var40.y + 1), 0.0F, 0);
               float var22 = IsoUtils.YToScreenExact((float)var40.x + 0.6F, (float)(var40.y + 1), 0.0F, 0);
               float var23 = IsoUtils.XToScreenExact((float)var40.x + 0.3F, (float)(var40.y + 1), 0.0F, 0);
               float var24 = IsoUtils.YToScreenExact((float)var40.x + 0.3F, (float)(var40.y + 1), 0.0F, 0);
               float var25 = IsoUtils.XToScreenExact((float)var40.x, (float)var40.y + 0.6F, 0.0F, 0);
               float var26 = IsoUtils.YToScreenExact((float)var40.x, (float)var40.y + 0.6F, 0.0F, 0);
               float var27 = IsoUtils.XToScreenExact((float)var40.x, (float)var40.y + 0.3F, 0.0F, 0);
               float var28 = IsoUtils.YToScreenExact((float)var40.x, (float)var40.y + 0.3F, 0.0F, 0);
               if (var40.visionMatrix[0][0][var31]) {
                  LineDrawer.drawLine(var35, var39, var47, var42, 1.0F, 0.0F, 0.0F, 1.0F, 0);
               }

               if (var40.visionMatrix[0][1][var31]) {
                  LineDrawer.drawLine(var47, var42, var44, var46, 1.0F, 0.0F, 0.0F, 1.0F, 0);
               }

               if (var40.visionMatrix[0][2][var31]) {
                  LineDrawer.drawLine(var44, var46, var49, var50, 1.0F, 0.0F, 0.0F, 1.0F, 0);
               }

               if (var40.visionMatrix[1][2][var31]) {
                  LineDrawer.drawLine(var49, var50, var21, var22, 1.0F, 0.0F, 0.0F, 1.0F, 0);
               }

               if (var40.visionMatrix[2][2][var31]) {
                  LineDrawer.drawLine(var21, var22, var23, var24, 1.0F, 0.0F, 0.0F, 1.0F, 0);
               }

               if (var40.visionMatrix[2][1][var31]) {
                  LineDrawer.drawLine(var23, var24, var25, var26, 1.0F, 0.0F, 0.0F, 1.0F, 0);
               }

               if (var40.visionMatrix[2][0][var31]) {
                  LineDrawer.drawLine(var25, var26, var27, var28, 1.0F, 0.0F, 0.0F, 1.0F, 0);
               }

               if (var40.visionMatrix[1][0][var31]) {
                  LineDrawer.drawLine(var27, var28, var35, var39, 1.0F, 0.0F, 0.0F, 1.0F, 0);
               }
            }
         }
      }

   }

   private void CullFullyOccludedSquares(IsoGridStack var1, boolean[][][] var2, boolean[][] var3) {
      int var4 = 0;

      int var5;
      for(var5 = 1; var5 < MaxHeight + 1; ++var5) {
         var4 += ((ArrayList)var1.Squares.get(var5)).size();
      }

      if (var4 >= 500) {
         var5 = 0;

         for(int var6 = MaxHeight; var6 >= 0; --var6) {
            GridStack = (ArrayList)var1.Squares.get(var6);

            for(int var7 = GridStack.size() - 1; var7 >= 0; --var7) {
               IsoGridSquare var8 = (IsoGridSquare)GridStack.get(var7);
               int var9 = var8.getX() - var6 * 3 - this.minX;
               int var10 = var8.getY() - var6 * 3 - this.minY;
               boolean var11;
               if (var6 < MaxHeight) {
                  var11 = !var3[var9][var10];
                  if (var11) {
                     var11 = false;
                     if (var9 > 2) {
                        if (var10 > 2) {
                           var11 = !var2[var9 - 3][var10 - 3][0] || !var2[var9 - 3][var10 - 3][1] || !var2[var9 - 3][var10 - 2][0] || !var2[var9 - 2][var10 - 3][1] || !var2[var9 - 2][var10 - 2][0] || !var2[var9 - 2][var10 - 2][1] || !var2[var9 - 2][var10 - 1][0] || !var2[var9 - 1][var10 - 2][0] || !var2[var9 - 1][var10 - 1][1] || !var2[var9 - 1][var10 - 1][0] || !var2[var9 - 1][var10][0] || !var2[var9][var10 - 1][1] || !var2[var9][var10][0] || !var2[var9][var10][1];
                        } else if (var10 > 1) {
                           var11 = !var2[var9 - 3][var10 - 2][0] || !var2[var9 - 2][var10 - 2][0] || !var2[var9 - 2][var10 - 2][1] || !var2[var9 - 2][var10 - 1][0] || !var2[var9 - 1][var10 - 2][0] || !var2[var9 - 1][var10 - 1][1] || !var2[var9 - 1][var10 - 1][0] || !var2[var9 - 1][var10][0] || !var2[var9][var10 - 1][1] || !var2[var9][var10][0] || !var2[var9][var10][1];
                        } else if (var10 > 0) {
                           var11 = !var2[var9 - 2][var10 - 1][0] || !var2[var9 - 1][var10 - 1][1] || !var2[var9 - 1][var10 - 1][0] || !var2[var9 - 1][var10][0] || !var2[var9][var10 - 1][1] || !var2[var9][var10][0] || !var2[var9][var10][1];
                        } else {
                           var11 = !var2[var9 - 1][var10][0] || !var2[var9][var10][0] || !var2[var9][var10][1];
                        }
                     } else if (var9 > 1) {
                        if (var10 > 2) {
                           var11 = !var2[var9 - 2][var10 - 3][1] || !var2[var9 - 2][var10 - 2][0] || !var2[var9 - 2][var10 - 2][1] || !var2[var9 - 2][var10 - 1][0] || !var2[var9 - 1][var10 - 2][0] || !var2[var9 - 1][var10 - 1][1] || !var2[var9 - 1][var10 - 1][0] || !var2[var9 - 1][var10][0] || !var2[var9][var10 - 1][1] || !var2[var9][var10][0] || !var2[var9][var10][1];
                        } else if (var10 > 1) {
                           var11 = !var2[var9 - 2][var10 - 2][0] || !var2[var9 - 2][var10 - 2][1] || !var2[var9 - 2][var10 - 1][0] || !var2[var9 - 1][var10 - 2][0] || !var2[var9 - 1][var10 - 1][1] || !var2[var9 - 1][var10 - 1][0] || !var2[var9 - 1][var10][0] || !var2[var9][var10 - 1][1] || !var2[var9][var10][0] || !var2[var9][var10][1];
                        } else if (var10 > 0) {
                           var11 = !var2[var9 - 2][var10 - 1][0] || !var2[var9 - 1][var10 - 1][1] || !var2[var9 - 1][var10 - 1][0] || !var2[var9 - 1][var10][0] || !var2[var9][var10 - 1][1] || !var2[var9][var10][0] || !var2[var9][var10][1];
                        } else {
                           var11 = !var2[var9 - 1][var10][0] || !var2[var9][var10][0] || !var2[var9][var10][1];
                        }
                     } else if (var9 > 0) {
                        if (var10 > 2) {
                           var11 = !var2[var9 - 1][var10 - 2][0] || !var2[var9 - 1][var10 - 1][1] || !var2[var9 - 1][var10 - 1][0] || !var2[var9 - 1][var10][0] || !var2[var9][var10 - 1][1] || !var2[var9][var10][0] || !var2[var9][var10][1];
                        } else if (var10 > 1) {
                           var11 = !var2[var9 - 1][var10 - 2][0] || !var2[var9 - 1][var10 - 1][1] || !var2[var9 - 1][var10 - 1][0] || !var2[var9 - 1][var10][0] || !var2[var9][var10 - 1][1] || !var2[var9][var10][0] || !var2[var9][var10][1];
                        } else if (var10 > 0) {
                           var11 = !var2[var9 - 1][var10 - 1][1] || !var2[var9 - 1][var10 - 1][0] || !var2[var9 - 1][var10][0] || !var2[var9][var10 - 1][1] || !var2[var9][var10][0] || !var2[var9][var10][1];
                        } else {
                           var11 = !var2[var9 - 1][var10][0] || !var2[var9][var10][0] || !var2[var9][var10][1];
                        }
                     } else if (var10 > 2) {
                        var11 = !var2[var9][var10 - 1][1] || !var2[var9][var10][0] || !var2[var9][var10][1];
                     } else if (var10 > 1) {
                        var11 = !var2[var9][var10 - 1][1] || !var2[var9][var10][0] || !var2[var9][var10][1];
                     } else if (var10 > 0) {
                        var11 = !var2[var9][var10 - 1][1] || !var2[var9][var10][0] || !var2[var9][var10][1];
                     } else {
                        var11 = !var2[var9][var10][0] || !var2[var9][var10][1];
                     }
                  }

                  if (!var11) {
                     GridStack.remove(var7);
                     var3[var9][var10] = true;
                     continue;
                  }
               }

               ++var5;
               var11 = var8.visionMatrix[0][1][1] && var8.getProperties().Is(IsoFlagType.cutW);
               boolean var12 = var8.visionMatrix[1][0][1] && var8.getProperties().Is(IsoFlagType.cutN);
               boolean var13 = false;
               int var14;
               if (var11 || var12) {
                  var13 = ((float)var8.x > IsoCamera.frameState.CamCharacterX || (float)var8.y > IsoCamera.frameState.CamCharacterY) && var8.z >= (int)IsoCamera.frameState.CamCharacterZ;
                  if (var13) {
                     var14 = (int)(var8.CachedScreenX - IsoCamera.frameState.OffX);
                     int var15 = (int)(var8.CachedScreenY - IsoCamera.frameState.OffY);
                     if (var14 + 32 * Core.TileScale <= this.StencilX1 || var14 - 32 * Core.TileScale >= this.StencilX2 || var15 + 32 * Core.TileScale <= this.StencilY1 || var15 - 96 * Core.TileScale >= this.StencilY2) {
                        var13 = false;
                     }
                  }
               }

               var14 = 0;
               if (var11 && !var13) {
                  ++var14;
                  if (var9 > 0) {
                     var2[var9 - 1][var10][0] = true;
                     if (var10 > 0) {
                        var2[var9 - 1][var10 - 1][1] = true;
                     }
                  }

                  if (var9 > 1 && var10 > 0) {
                     var2[var9 - 2][var10 - 1][0] = true;
                     if (var10 > 1) {
                        var2[var9 - 2][var10 - 2][1] = true;
                     }
                  }

                  if (var9 > 2 && var10 > 1) {
                     var2[var9 - 3][var10 - 2][0] = true;
                     if (var10 > 2) {
                        var2[var9 - 3][var10 - 3][1] = true;
                     }
                  }
               }

               if (var12 && !var13) {
                  ++var14;
                  if (var10 > 0) {
                     var2[var9][var10 - 1][1] = true;
                     if (var9 > 0) {
                        var2[var9 - 1][var10 - 1][0] = true;
                     }
                  }

                  if (var10 > 1 && var9 > 0) {
                     var2[var9 - 1][var10 - 2][1] = true;
                     if (var9 > 1) {
                        var2[var9 - 2][var10 - 2][0] = true;
                     }
                  }

                  if (var10 > 2 && var9 > 1) {
                     var2[var9 - 2][var10 - 3][1] = true;
                     if (var9 > 2) {
                        var2[var9 - 3][var10 - 3][0] = true;
                     }
                  }
               }

               if (var8.visionMatrix[1][1][0]) {
                  ++var14;
                  var2[var9][var10][0] = true;
                  var2[var9][var10][1] = true;
               }

               if (var14 == 3) {
                  var3[var9][var10] = true;
               }
            }
         }

      }
   }

   public void RenderFloorShading(int var1) {
      if (var1 < this.maxZ && PerformanceSettings.LightingFrameSkip < 3) {
         if (texWhite == null) {
            texWhite = Texture.getSharedTexture("media/ui/white.png");
         }

         Texture var2 = texWhite;
         if (var2 != null) {
            int var3 = IsoCamera.frameState.playerIndex;
            int var4 = (int)IsoCamera.frameState.OffX;
            int var5 = (int)IsoCamera.frameState.OffY;

            for(int var6 = 0; var6 < ShadedFloor.size(); ++var6) {
               IsoGridSquare var7 = (IsoGridSquare)ShadedFloor.get(var6);
               if (var7.getProperties().solidfloor) {
                  float var8 = 0.0F;
                  float var9 = 0.0F;
                  float var10 = 0.0F;
                  if (var7.getProperties().Is(IsoFlagType.FloorHeightOneThird)) {
                     var9 = -1.0F;
                     var8 = -1.0F;
                  } else if (var7.getProperties().Is(IsoFlagType.FloorHeightTwoThirds)) {
                     var9 = -2.0F;
                     var8 = -2.0F;
                  }

                  float var11 = IsoUtils.XToScreen((float)var7.getX() + var8, (float)var7.getY() + var9, (float)var1 + var10, 0);
                  float var12 = IsoUtils.YToScreen((float)var7.getX() + var8, (float)var7.getY() + var9, (float)var1 + var10, 0);
                  var11 = (float)((int)var11);
                  var12 = (float)((int)var12);
                  var11 -= (float)var4;
                  var12 -= (float)var5;
                  colu = var7.getVertLight(0, var3);
                  colr = var7.getVertLight(1, var3);
                  cold = var7.getVertLight(2, var3);
                  coll = var7.getVertLight(3, var3);
                  var2.renderdiamond((int)var11 - 32 * Core.TileScale, (int)var12 + 16 * Core.TileScale, 64 * Core.TileScale, 32 * Core.TileScale, colu, cold, coll, colr);
               }
            }

         }
      }
   }

   public boolean IsPlayerWindowPeeking(int var1) {
      return this.playerWindowPeeking[var1];
   }

   public boolean CanBuildingSquareOccludePlayer(IsoGridSquare var1, int var2) {
      ArrayList var3 = (ArrayList)this.playerOccluderBuildings.get(var2);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         IsoBuilding var5 = (IsoBuilding)var3.get(var4);
         int var6 = var5.getDef().getX();
         int var7 = var5.getDef().getY();
         int var8 = var5.getDef().getX2() - var6;
         int var9 = var5.getDef().getY2() - var7;
         this.buildingRectTemp.setBounds(var6, var7, var8 + 1, var9 + 1);
         if (this.buildingRectTemp.contains(var1.getX(), var1.getY())) {
            return true;
         }
      }

      return false;
   }

   private boolean IsDissolvedSquare(IsoGridSquare var1) {
      int var2 = IsoCamera.frameState.playerIndex;
      IsoPlayer var3 = IsoPlayer.players[var2];
      if (var3.current == null) {
         return false;
      } else {
         IsoGridSquare var4 = var3.current;
         if (var4.getZ() >= var1.getZ()) {
            return false;
         } else if (!PerformanceSettings.NewRoofHiding) {
            return this.bHideFloors[var2] && var1.getZ() >= this.maxZ;
         } else {
            if (var1.getZ() > this.hidesOrphanStructuresAbove) {
               IsoBuilding var5 = var1.getBuilding();
               if (var5 == null) {
                  var5 = var1.roofHideBuilding;
               }

               IsoGridSquare var7;
               for(int var6 = var1.getZ() - 1; var6 >= 0 && var5 == null; --var6) {
                  var7 = this.getGridSquare(var1.x, var1.y, var6);
                  if (var7 != null) {
                     var5 = var7.getBuilding();
                     if (var5 == null) {
                        var5 = var7.roofHideBuilding;
                     }
                  }
               }

               if (var5 == null) {
                  if (var1.isSolidFloor()) {
                     return true;
                  }

                  IsoGridSquare var14 = var1.nav[IsoDirections.N.index()];
                  if (var14 != null && var14.getBuilding() == null) {
                     if (var14.getPlayerBuiltFloor() != null) {
                        return true;
                     }

                     var7 = this.getGridSquare(var14.x, var14.y, var14.z - 1);
                     if (var7 != null && var7.HasStairs()) {
                        return true;
                     }
                  }

                  var7 = var1.nav[IsoDirections.W.index()];
                  IsoGridSquare var8;
                  if (var7 != null && var7.getBuilding() == null) {
                     if (var7.getPlayerBuiltFloor() != null) {
                        return true;
                     }

                     var8 = this.getGridSquare(var7.x, var7.y, var7.z - 1);
                     if (var8 != null && var8.HasStairs()) {
                        return true;
                     }
                  }

                  if (var1.Is(IsoFlagType.WallSE)) {
                     var8 = var1.nav[IsoDirections.NW.index()];
                     if (var8 != null && var8.getBuilding() == null) {
                        if (var8.getPlayerBuiltFloor() != null) {
                           return true;
                        }

                        IsoGridSquare var9 = this.getGridSquare(var8.x, var8.y, var8.z - 1);
                        if (var9 != null && var9.HasStairs()) {
                           return true;
                        }
                     }
                  }
               }
            }

            int var10;
            int var11;
            int var15;
            for(var15 = 0; var15 < 4; ++var15) {
               short var16 = 500;

               for(int var18 = 0; var18 < var16 && this.playerOccluderBuildingsArr[var2] != null; ++var18) {
                  IsoBuilding var19 = this.playerOccluderBuildingsArr[var2][var18];
                  if (var19 == null) {
                     break;
                  }

                  BuildingDef var22 = var19.getDef();
                  var10 = var22.getX();
                  var11 = var22.getY();
                  int var12 = var22.getX2() - var10;
                  int var13 = var22.getY2() - var11;
                  this.buildingRectTemp.setBounds(var10 - 1, var11 - 1, var12 + 2, var13 + 2);
                  if (this.buildingRectTemp.contains(var1.getX(), var1.getY())) {
                     return true;
                  }
               }
            }

            IsoBuilding var17;
            BuildingDef var20;
            int var21;
            int var23;
            for(var15 = 0; var15 < 500; ++var15) {
               var17 = this.zombieOccluderBuildingsArr[var2][var15];
               if (var17 == null) {
                  break;
               }

               var20 = var17.getDef();
               var21 = var20.getX();
               var23 = var20.getY();
               var10 = var20.getX2() - var21;
               var11 = var20.getY2() - var23;
               this.buildingRectTemp.setBounds(var21 - 1, var23 - 1, var10 + 2, var11 + 2);
               if (this.buildingRectTemp.contains(var1.getX(), var1.getY())) {
                  return true;
               }
            }

            for(var15 = 0; var15 < 500; ++var15) {
               var17 = this.otherOccluderBuildingsArr[var2][var15];
               if (var17 == null) {
                  break;
               }

               var20 = var17.getDef();
               var21 = var20.getX();
               var23 = var20.getY();
               var10 = var20.getX2() - var21;
               var11 = var20.getY2() - var23;
               this.buildingRectTemp.setBounds(var21 - 1, var23 - 1, var10 + 2, var11 + 2);
               if (this.buildingRectTemp.contains(var1.getX(), var1.getY())) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private int GetBuildingHeightAt(IsoBuilding var1, int var2, int var3, int var4) {
      for(int var5 = MaxHeight; var5 > var4; --var5) {
         IsoGridSquare var6 = this.getGridSquare(var2, var3, var5);
         if (var6 != null && var6.getBuilding() == var1) {
            return var5;
         }
      }

      return var4;
   }

   private void updateSnow(int var1) {
      if (this.snowGridCur == null) {
         this.snowGridCur = new IsoCell.SnowGrid(var1);
         this.snowGridPrev = new IsoCell.SnowGrid(0);
      } else {
         if (var1 != this.snowGridCur.frac) {
            this.snowGridPrev.init(this.snowGridCur.frac);
            this.snowGridCur.init(var1);
            if (this.snowGridPrev.frac < this.snowGridCur.frac) {
               this.snowGridCur.subtract(this.snowGridPrev);
            } else {
               this.snowGridPrev.subtract(this.snowGridCur);
            }

            this.snowFadeTime = System.currentTimeMillis();
            DebugLog.log("snow from " + this.snowGridPrev.frac + " to " + this.snowGridCur.frac);
         }

      }
   }

   public void setSnowParams(int var1, int var2, float var3) {
      DebugLog.log("Warning: method is redundant! use method 'setSnowTarget' instead.");
   }

   public void setSnowTarget(int var1) {
      if (!SandboxOptions.instance.EnableSnowOnGround.getValue()) {
         var1 = 0;
      }

      this.snowFracTarget = var1;
   }

   public void RenderSnow(int var1) {
      this.updateSnow(this.snowFracTarget);
      if (this.snowGridCur != null) {
         float var2 = 1.0F;
         float var3 = 1.0F;
         if (this.snowGridPrev.frac > this.snowGridCur.frac) {
            var3 = 0.0F;
         }

         long var4 = System.currentTimeMillis();
         if ((float)(var4 - this.snowFadeTime) < this.snowTransitionTime) {
            float var6 = (float)(var4 - this.snowFadeTime) / this.snowTransitionTime;
            if (this.snowGridPrev.frac < this.snowGridCur.frac) {
               var2 = var6;
               var3 = 1.0F;
            } else {
               var2 = 1.0F;
               var3 = 1.0F - var6 * var6;
            }
         }

         if (this.snowGridCur.frac > 0 || !(var3 <= 0.0F) && this.snowGridPrev.frac > 0) {
            int var21 = (int)IsoCamera.frameState.OffX;
            int var7 = (int)IsoCamera.frameState.OffY;

            for(int var8 = 0; var8 < SolidFloor.size(); ++var8) {
               IsoGridSquare var9 = (IsoGridSquare)SolidFloor.get(var8);
               if (var9.getProperties().Is(IsoFlagType.solidfloor) && !var9.getProperties().Is(IsoFlagType.water) && var9.getProperties().Is(IsoFlagType.exterior) && var9.room == null) {
                  int var10 = var9.getX() % this.snowGridCur.w;
                  int var11 = var9.getY() % this.snowGridCur.h;
                  float var12 = IsoUtils.XToScreen((float)var9.getX(), (float)var9.getY(), (float)var1, 0);
                  float var13 = IsoUtils.YToScreen((float)var9.getX(), (float)var9.getY(), (float)var1, 0);
                  var12 = (float)((int)var12);
                  var13 = (float)((int)var13);
                  var12 -= (float)var21;
                  var13 -= (float)var7;
                  float var14 = (float)(32 * Core.TileScale);
                  float var15 = (float)(96 * Core.TileScale);
                  var12 -= var14;
                  var13 -= var15;
                  float var16 = 1.0F;
                  float var17 = 1.0F;
                  float var18 = 1.0F;

                  for(int var19 = 0; var19 < 2; ++var19) {
                     Texture var20;
                     if (var3 > var2) {
                        if (var2 > 0.0F) {
                           var20 = this.snowGridCur.grid[var10][var11][var19];
                           if (var20 != null) {
                              var20.render((int)var12, (int)var13, var20.getWidth(), var20.getHeight(), var16, var17, var18, var2);
                           }
                        }

                        if (var3 > 0.0F) {
                           var20 = this.snowGridPrev.grid[var10][var11][var19];
                           if (var20 != null) {
                              var20.render((int)var12, (int)var13, var20.getWidth(), var20.getHeight(), var16, var17, var18, var3);
                           }
                        }
                     } else {
                        if (var3 > 0.0F) {
                           var20 = this.snowGridPrev.grid[var10][var11][var19];
                           if (var20 != null) {
                              var20.render((int)var12, (int)var13, var20.getWidth(), var20.getHeight(), var16, var17, var18, var3);
                           }
                        }

                        if (var2 > 0.0F) {
                           var20 = this.snowGridCur.grid[var10][var11][var19];
                           if (var20 != null) {
                              var20.render((int)var12, (int)var13, var20.getWidth(), var20.getHeight(), var16, var17, var18, var2);
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   public IsoBuilding getClosestBuildingExcept(IsoGameCharacter var1, IsoRoom var2) {
      IsoBuilding var3 = null;
      float var4 = 1000000.0F;

      for(int var5 = 0; var5 < this.BuildingList.size(); ++var5) {
         IsoBuilding var6 = (IsoBuilding)this.BuildingList.get(var5);

         for(int var7 = 0; var7 < var6.Exits.size(); ++var7) {
            float var8 = var1.DistTo(((IsoRoomExit)var6.Exits.get(var7)).x, ((IsoRoomExit)var6.Exits.get(var7)).y);
            if (var8 < var4 && (var2 == null || var2.building != var6)) {
               var3 = var6;
               var4 = var8;
            }
         }
      }

      return var3;
   }

   public int getDangerScore(int var1, int var2) {
      return var1 >= 0 && var2 >= 0 && var1 < this.width && var2 < this.height ? this.DangerScore.getValue(var1, var2) : 1000000;
   }

   private void ObjectDeletionAddition() {
      int var1;
      IsoMovingObject var2;
      for(var1 = 0; var1 < this.removeList.size(); ++var1) {
         var2 = (IsoMovingObject)this.removeList.get(var1);
         if (var2 instanceof IsoZombie) {
            VirtualZombieManager.instance.RemoveZombie((IsoZombie)var2);
         }

         if (!(var2 instanceof IsoPlayer) || ((IsoPlayer)var2).isDead()) {
            this.ObjectList.remove(var2);
            if (var2.getCurrentSquare() != null) {
               var2.getCurrentSquare().getMovingObjects().remove(var2);
            }

            if (var2.getLastSquare() != null) {
               var2.getLastSquare().getMovingObjects().remove(var2);
            }
         }
      }

      this.removeList.clear();

      for(var1 = 0; var1 < this.addList.size(); ++var1) {
         var2 = (IsoMovingObject)this.addList.get(var1);
         this.ObjectList.add(var2);
      }

      this.addList.clear();

      for(var1 = 0; var1 < this.addVehicles.size(); ++var1) {
         BaseVehicle var3 = (BaseVehicle)this.addVehicles.get(var1);
         if (!this.ObjectList.contains(var3)) {
            this.ObjectList.add(var3);
         }

         if (!this.vehicles.contains(var3)) {
            this.vehicles.add(var3);
         }
      }

      this.addVehicles.clear();
   }

   private void ProcessItems(Iterator var1) {
      int var2 = this.ProcessItems.size();

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         InventoryItem var4 = (InventoryItem)this.ProcessItems.get(var3);
         var4.update();
         if (var4.finishupdate()) {
            this.ProcessItemsRemove.add(var4);
         }
      }

      var2 = this.ProcessWorldItems.size();

      for(var3 = 0; var3 < var2; ++var3) {
         IsoWorldInventoryObject var5 = (IsoWorldInventoryObject)this.ProcessWorldItems.get(var3);
         var5.update();
         if (var5.finishupdate()) {
            this.ProcessWorldItemsRemove.add(var5);
         }
      }

   }

   private void ProcessIsoObject() {
      this.ProcessIsoObject.removeAll(this.ProcessIsoObjectRemove);
      this.ProcessIsoObjectRemove.clear();
      int var1 = this.ProcessIsoObject.size();

      for(int var2 = 0; var2 < var1; ++var2) {
         IsoObject var3 = (IsoObject)this.ProcessIsoObject.get(var2);
         if (var3 != null) {
            var3.update();
            if (var1 > this.ProcessIsoObject.size()) {
               --var2;
               --var1;
            }
         }
      }

   }

   private void ProcessObjects(Iterator var1) {
      for(int var2 = 0; var2 < this.ObjectList.size(); ++var2) {
         IsoMovingObject var3 = (IsoMovingObject)this.ObjectList.get(var2);
         if (var3 instanceof IsoDeadBody) {
            this.removeList.add(var3);
         } else if (GameServer.bServer && var3 instanceof IsoPlayer && !GameServer.Players.contains(var3)) {
            this.removeList.add(var3);
         } else if (var3 instanceof IsoZombie && VirtualZombieManager.instance.isReused((IsoZombie)var3)) {
            DebugLog.log(DebugType.Zombie, "REUSABLE ZOMBIE IN IsoCell.ObjectList IGNORED " + var3);
         } else {
            var3.preupdate();
            var3.update();
         }
      }

   }

   private void ProcessRemoveItems(Iterator var1) {
      this.ProcessItems.removeAll(this.ProcessItemsRemove);
      this.ProcessWorldItems.removeAll(this.ProcessWorldItemsRemove);
      this.ProcessItemsRemove.clear();
      this.ProcessWorldItemsRemove.clear();
   }

   private void ProcessStaticUpdaters() {
      int var1 = this.StaticUpdaterObjectList.size();

      for(int var2 = 0; var2 < var1; ++var2) {
         try {
            ((IsoObject)this.StaticUpdaterObjectList.get(var2)).update();
         } catch (Exception var4) {
            Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var4);
         }

         if (var1 > this.StaticUpdaterObjectList.size()) {
            --var2;
            --var1;
         }
      }

   }

   public void addToProcessIsoObject(IsoObject var1) {
      if (var1 != null) {
         this.ProcessIsoObjectRemove.remove(var1);
         if (!this.ProcessIsoObject.contains(var1)) {
            this.ProcessIsoObject.add(var1);
         }

      }
   }

   public void addToProcessItems(InventoryItem var1) {
      if (var1 != null) {
         this.ProcessItemsRemove.remove(var1);
         if (!this.ProcessItems.contains(var1)) {
            this.ProcessItems.add(var1);
         }

      }
   }

   public void addToProcessItems(ArrayList var1) {
      if (var1 != null) {
         for(int var2 = 0; var2 < var1.size(); ++var2) {
            InventoryItem var3 = (InventoryItem)var1.get(var2);
            if (var3 != null) {
               this.ProcessItemsRemove.remove(var3);
               if (!this.ProcessItems.contains(var3)) {
                  this.ProcessItems.add(var3);
               }
            }
         }

      }
   }

   public void addToProcessItemsRemove(InventoryItem var1) {
      if (var1 != null) {
         if (!this.ProcessItemsRemove.contains(var1)) {
            this.ProcessItemsRemove.add(var1);
         }

      }
   }

   public void addToProcessItemsRemove(ArrayList var1) {
      if (var1 != null) {
         for(int var2 = 0; var2 < var1.size(); ++var2) {
            InventoryItem var3 = (InventoryItem)var1.get(var2);
            if (var3 != null && !this.ProcessItemsRemove.contains(var3)) {
               this.ProcessItemsRemove.add(var3);
            }
         }

      }
   }

   public IsoSurvivor getNetworkPlayer(int var1) {
      int var2 = this.RemoteSurvivorList.size();

      for(int var3 = 0; var3 < var2; ++var3) {
         if (((IsoGameCharacter)this.RemoteSurvivorList.get(var3)).getRemoteID() == var1) {
            return (IsoSurvivor)this.RemoteSurvivorList.get(var3);
         }
      }

      return null;
   }

   public boolean IsStairsNode(IsoGridSquare var1, IsoGridSquare var2, IsoDirections var3) {
      if (var1.Has(IsoObjectType.stairsTN)) {
         return var3 == IsoDirections.N;
      } else if (var1.Has(IsoObjectType.stairsTW)) {
         return var3 == IsoDirections.W;
      } else {
         return var2.getZ() == var1.getZ();
      }
   }

   public void InitNodeMap(int var1) {
   }

   IsoGridSquare ConnectNewSquare(IsoGridSquare var1, boolean var2, boolean var3) {
      int var4 = var1.getX();
      int var5 = var1.getY();
      int var6 = var1.getZ();
      this.setCacheGridSquare(var4, var5, var6, var1);
      this.DoGridNav(var1, IsoGridSquare.cellGetSquare);
      return var1;
   }

   public void DoGridNav(IsoGridSquare var1, IsoGridSquare.GetSquare var2) {
      int var3 = var1.getX();
      int var4 = var1.getY();
      int var5 = var1.getZ();
      var1.nav[IsoDirections.N.index()] = var2.getGridSquare(var3, var4 - 1, var5);
      var1.nav[IsoDirections.NW.index()] = var2.getGridSquare(var3 - 1, var4 - 1, var5);
      var1.nav[IsoDirections.W.index()] = var2.getGridSquare(var3 - 1, var4, var5);
      var1.nav[IsoDirections.SW.index()] = var2.getGridSquare(var3 - 1, var4 + 1, var5);
      var1.nav[IsoDirections.S.index()] = var2.getGridSquare(var3, var4 + 1, var5);
      var1.nav[IsoDirections.SE.index()] = var2.getGridSquare(var3 + 1, var4 + 1, var5);
      var1.nav[IsoDirections.E.index()] = var2.getGridSquare(var3 + 1, var4, var5);
      var1.nav[IsoDirections.NE.index()] = var2.getGridSquare(var3 + 1, var4 - 1, var5);
      if (var1.nav[IsoDirections.N.index()] != null) {
         var1.nav[IsoDirections.N.index()].nav[IsoDirections.S.index()] = var1;
      }

      if (var1.nav[IsoDirections.NW.index()] != null) {
         var1.nav[IsoDirections.NW.index()].nav[IsoDirections.SE.index()] = var1;
      }

      if (var1.nav[IsoDirections.W.index()] != null) {
         var1.nav[IsoDirections.W.index()].nav[IsoDirections.E.index()] = var1;
      }

      if (var1.nav[IsoDirections.SW.index()] != null) {
         var1.nav[IsoDirections.SW.index()].nav[IsoDirections.NE.index()] = var1;
      }

      if (var1.nav[IsoDirections.S.index()] != null) {
         var1.nav[IsoDirections.S.index()].nav[IsoDirections.N.index()] = var1;
      }

      if (var1.nav[IsoDirections.SE.index()] != null) {
         var1.nav[IsoDirections.SE.index()].nav[IsoDirections.NW.index()] = var1;
      }

      if (var1.nav[IsoDirections.E.index()] != null) {
         var1.nav[IsoDirections.E.index()].nav[IsoDirections.W.index()] = var1;
      }

      if (var1.nav[IsoDirections.NE.index()] != null) {
         var1.nav[IsoDirections.NE.index()].nav[IsoDirections.SW.index()] = var1;
      }

   }

   public IsoGridSquare ConnectNewSquare(IsoGridSquare var1, boolean var2) {
      for(int var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
         if (!this.ChunkMap[var3].ignore) {
            this.ChunkMap[var3].setGridSquare(var1, var1.getX(), var1.getY(), var1.getZ());
         }
      }

      IsoGridSquare var4 = this.ConnectNewSquare(var1, var2, false);
      return var4;
   }

   public void PlaceLot(String var1, int var2, int var3, int var4, boolean var5) {
   }

   public void PlaceLot(IsoLot var1, int var2, int var3, int var4, boolean var5) {
      boolean var6 = true;
      Stack var7 = new Stack();

      for(int var8 = var2; var8 < var2 + var1.info.width; ++var8) {
         for(int var9 = var3; var9 < var3 + var1.info.height; ++var9) {
            boolean var10 = true;

            for(int var11 = var4; var11 < var4 + var1.info.levels; ++var11) {
               var10 = false;
               if (var8 < this.width && var9 < this.height && var8 >= 0 && var9 >= 0 && var11 >= 0) {
                  Integer[] var12 = var1.data[var8 - var2][var9 - var3][var11 - var4];
                  if (var12 != null) {
                     int var13 = var12.length;
                     boolean var14 = false;
                     IsoGridSquare var15 = this.getGridSquare(var8, var9, var11);
                     if (var15 == null) {
                        var15 = IsoGridSquare.getNew(this, (SliceY)null, var8, var9, var11);
                        this.ChunkMap[IsoPlayer.getPlayerIndex()].setGridSquare(var15, var8, var9, var11);
                        if (this.bDoLotConnect) {
                        }
                     }

                     if (var11 < 8) {
                        var15 = IsoGridSquare.getNew(this, (SliceY)null, var8, var9, var11 + 1);
                        this.ChunkMap[IsoPlayer.getPlayerIndex()].setGridSquare(var15, var8, var9, var11 + 1);
                        if (this.bDoLotConnect) {
                        }
                     }

                     if (var13 > 0 && var11 > MaxHeight) {
                        MaxHeight = var11;
                     }

                     for(int var19 = 0; var19 < var13; ++var19) {
                        String var20 = (String)var1.info.tilesUsed.get(var12[var19]);
                        IsoSprite var16 = (IsoSprite)this.SpriteManager.NamedMap.get(var20);
                        if (var16 == null) {
                           Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, "Missing tile definition: " + var20);
                        } else {
                           IsoGridSquare var17 = this.getGridSquare(var8, var9, var11);
                           if (var17 == null) {
                              var17 = IsoGridSquare.getNew(this, (SliceY)null, var8, var9, var11);
                              this.ChunkMap[IsoPlayer.getPlayerIndex()].setGridSquare(var17, var8, var9, var11);
                              if (this.bDoLotConnect) {
                              }
                           } else {
                              if (var5 && var19 == 0 && var16.getProperties().Is(IsoFlagType.solidfloor) && (!var16.Properties.Is(IsoFlagType.hidewalls) || var12.length > 1)) {
                                 var10 = true;
                              }

                              if (var10 && var19 == 0) {
                                 var17.getObjects().clear();
                              }
                           }

                           CellLoader.DoTileObjectCreation(var16, var16.getType(), var17, this, var8, var9, var11, var7, false, var20);
                        }
                     }
                  }
               }
            }
         }
      }

      boolean var18 = false;
   }

   public void PlaceLot(IsoLot var1, int var2, int var3, int var4, IsoChunk var5, int var6, int var7, boolean var8) {
      boolean var9 = true;
      Stack var10 = new Stack();
      var6 *= 10;
      var7 *= 10;

      try {
         int var12;
         int var14;
         try {
            for(int var11 = var6 + var2; var11 < var6 + var2 + 10; ++var11) {
               for(var12 = var7 + var3; var12 < var7 + var3 + 10; ++var12) {
                  boolean var29 = true;

                  for(var14 = var4; var14 < var4 + 8; ++var14) {
                     var29 = false;
                     if (var11 < var6 + 10 && var12 < var7 + 10 && var11 >= var6 && var12 >= var7 && var14 >= 0) {
                        Integer[] var15 = var1.data[var11 - (var6 + var2)][var12 - (var7 + var3)][var14 - var4];
                        IsoGridSquare var16 = null;
                        if (var15 != null && var15.length > 0) {
                           int var17 = var15.length;
                           boolean var18 = false;
                           int var20;
                           if (var16 == null) {
                              var16 = var5.getGridSquare(var11 - var6, var12 - var7, var14);
                              if (var16 == null) {
                                 var16 = IsoGridSquare.getNew(this, (SliceY)null, var11, var12, var14);
                                 var16.setX(var11);
                                 var16.setY(var12);
                                 var16.setZ(var14);
                                 var5.setSquare(var11 - var6, var12 - var7, var14, var16);
                              }

                              for(int var19 = -1; var19 <= 1; ++var19) {
                                 for(var20 = -1; var20 <= 1; ++var20) {
                                    if ((var19 != 0 || var20 != 0) && var19 + var11 - var6 >= 0 && var19 + var11 - var6 < 10 && var20 + var12 - var7 >= 0 && var20 + var12 - var7 < 10) {
                                       IsoGridSquare var21 = var5.getGridSquare(var11 + var19 - var6, var12 + var20 - var7, var14);
                                       if (var21 == null) {
                                          var21 = IsoGridSquare.getNew(this, (SliceY)null, var11 + var19, var12 + var20, var14);
                                          var5.setSquare(var11 + var19 - var6, var12 + var20 - var7, var14, var21);
                                       }
                                    }
                                 }
                              }
                           }

                           if (var17 > 1 && var14 > MaxHeight) {
                              MaxHeight = var14;
                           }

                           RoomDef var31 = IsoWorld.instance.getMetaChunkFromTile(var11, var12).getRoomAt(var11, var12, var14);
                           var20 = var31 != null ? var31.ID : -1;
                           var16.setRoomID(var20);
                           var16.ResetMasterRegion();
                           var31 = IsoWorld.instance.getMetaChunkFromTile(var11, var12).getEmptyOutsideAt(var11, var12, var14);
                           if (var31 != null) {
                              IsoRoom var32 = var5.getRoom(var31.ID);
                              var16.roofHideBuilding = var32 == null ? null : var32.building;
                           }

                           for(int var30 = 0; var30 < var17; ++var30) {
                              String var33 = (String)var1.info.tilesUsed.get(var15[var30]);
                              if (!var1.info.bFixed2x) {
                                 var33 = IsoChunk.Fix2x(var33);
                              }

                              IsoSprite var22 = (IsoSprite)this.SpriteManager.NamedMap.get(var33);
                              if (var22 == null) {
                                 Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, "Missing tile definition: " + var33);
                              } else {
                                 if (var30 == 0 && var22.getProperties().Is(IsoFlagType.solidfloor) && (!var22.Properties.Is(IsoFlagType.hidewalls) || var15.length > 1)) {
                                    var29 = true;
                                 }

                                 if (var29 && var30 == 0) {
                                    var16.getObjects().clear();
                                 }

                                 CellLoader.DoTileObjectCreation(var22, var22.getType(), var16, this, var11, var12, var14, var10, false, var33);
                              }
                           }

                           var16.FixStackableObjects();
                        }
                     }
                  }
               }
            }
         } catch (Exception var26) {
            DebugLog.log("Failed to load chunk, blocking out area");
            ExceptionLogger.logException(var26);

            for(var12 = var6 + var2; var12 < var6 + var2 + 10; ++var12) {
               for(int var13 = var7 + var3; var13 < var7 + var3 + 10; ++var13) {
                  for(var14 = var4; var14 < var4 + var1.info.levels; ++var14) {
                     var5.setSquare(var12 - var6, var13 - var7, var14, (IsoGridSquare)null);
                     this.setCacheGridSquare(var12, var13, var14, (IsoGridSquare)null);
                  }
               }
            }
         }
      } finally {
         ;
      }

      boolean var28 = false;
   }

   public void setDrag(KahluaTable var1, int var2) {
      if (var2 >= 0 && var2 < 4) {
         this.drag[var2] = var1;
      }
   }

   public KahluaTable getDrag(int var1) {
      return var1 >= 0 && var1 < 4 ? this.drag[var1] : null;
   }

   public boolean DoBuilding(int var1, boolean var2) {
      if (UIManager.getPickedTile() != null && this.drag[var1] != null && JoypadManager.instance.getFromPlayer(var1) == null) {
         if (!IsoWorld.instance.isValidSquare((int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ())) {
            return false;
         }

         IsoGridSquare var3 = this.getGridSquare((int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ());
         if (!var2) {
            if (var3 == null) {
               var3 = this.createNewGridSquare((int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ(), true);
            }

            var3.EnsureSurroundNotNull();
         }

         LuaEventManager.triggerEvent("OnDoTileBuilding2", this.drag[var1], var2, (int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ(), var3);
      }

      if (this.drag[var1] != null && JoypadManager.instance.getFromPlayer(var1) != null) {
         LuaEventManager.triggerEvent("OnDoTileBuilding3", this.drag[var1], var2, (int)IsoPlayer.players[var1].getX(), (int)IsoPlayer.players[var1].getY(), (int)IsoCamera.CamCharacter.getZ());
      }

      if (var2) {
         IndieGL.glBlendFunc(770, 771);
      }

      return false;
   }

   public float DistanceFromSupport(int var1, int var2, int var3) {
      return 0.0F;
   }

   public ArrayList getBuildingList() {
      return this.BuildingList;
   }

   public void setBuildingList(ArrayList var1) {
      this.BuildingList = var1;
   }

   public ArrayList getObjectList() {
      return this.ObjectList;
   }

   public void setObjectList(ArrayList var1) {
      this.ObjectList = var1;
   }

   public IsoRoom getRoom(int var1) {
      IsoRoom var2 = this.ChunkMap[IsoPlayer.getPlayerIndex()].getRoom(var1);
      return var2;
   }

   public ArrayList getPushableObjectList() {
      return this.PushableObjectList;
   }

   public void setPushableObjectList(ArrayList var1) {
      this.PushableObjectList = var1;
   }

   public HashMap getBuildingScores() {
      return this.BuildingScores;
   }

   public void setBuildingScores(HashMap var1) {
      this.BuildingScores = var1;
   }

   public AStarPathMap getPathMap() {
      return this.PathMap;
   }

   public void setPathMap(AStarPathMap var1) {
      this.PathMap = var1;
   }

   public ArrayList getRoomList() {
      return this.RoomList;
   }

   public void setRoomList(ArrayList var1) {
      this.RoomList = var1;
   }

   public ArrayList getStaticUpdaterObjectList() {
      return this.StaticUpdaterObjectList;
   }

   public void setStaticUpdaterObjectList(ArrayList var1) {
      this.StaticUpdaterObjectList = var1;
   }

   public ArrayList getWallArray() {
      return this.wallArray;
   }

   public void setWallArray(ArrayList var1) {
      this.wallArray = var1;
   }

   public ArrayList getZombieList() {
      return this.ZombieList;
   }

   public void setZombieList(ArrayList var1) {
      this.ZombieList = var1;
   }

   public ArrayList getRemoteSurvivorList() {
      return this.RemoteSurvivorList;
   }

   public void setRemoteSurvivorList(ArrayList var1) {
      this.RemoteSurvivorList = var1;
   }

   public ArrayList getGhostList() {
      return this.GhostList;
   }

   public void setGhostList(ArrayList var1) {
      this.GhostList = var1;
   }

   public ArrayList getZoneStack() {
      return this.ZoneStack;
   }

   public void setZoneStack(ArrayList var1) {
      this.ZoneStack = var1;
   }

   public ArrayList getRemoveList() {
      return this.removeList;
   }

   public void setRemoveList(ArrayList var1) {
      this.removeList = var1;
   }

   public ArrayList getAddList() {
      return this.addList;
   }

   public void addMovingObject(IsoMovingObject var1) {
      this.addList.add(var1);
   }

   public void setAddList(ArrayList var1) {
      this.addList = var1;
   }

   public ArrayList getRenderJobsArray() {
      return this.RenderJobsArray;
   }

   public void setRenderJobsArray(ArrayList var1) {
      this.RenderJobsArray = var1;
   }

   public ArrayList getProcessItems() {
      return this.ProcessItems;
   }

   public ArrayList getProcessWorldItems() {
      return this.ProcessWorldItems;
   }

   public ArrayList getProcessIsoObjects() {
      return this.ProcessIsoObject;
   }

   public void setProcessItems(ArrayList var1) {
      this.ProcessItems = var1;
   }

   public ArrayList getProcessItemsRemove() {
      return this.ProcessItemsRemove;
   }

   public void setProcessItemsRemove(ArrayList var1) {
      this.ProcessItemsRemove = var1;
   }

   public HashMap getRenderJobsMapArray() {
      return this.RenderJobsMapArray;
   }

   public void setRenderJobsMapArray(HashMap var1) {
      this.RenderJobsMapArray = var1;
   }

   public ArrayList getVehicles() {
      return this.vehicles;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int var1) {
      this.height = var1;
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int var1) {
      this.width = var1;
   }

   public int getWorldX() {
      return this.worldX;
   }

   public void setWorldX(int var1) {
      this.worldX = var1;
   }

   public int getWorldY() {
      return this.worldY;
   }

   public void setWorldY(int var1) {
      this.worldY = var1;
   }

   public String getFilename() {
      return this.filename;
   }

   public void setFilename(String var1) {
      this.filename = var1;
   }

   public boolean isSafeToAdd() {
      return this.safeToAdd;
   }

   public void setSafeToAdd(boolean var1) {
      this.safeToAdd = var1;
   }

   public Stack getLamppostPositions() {
      return this.LamppostPositions;
   }

   public void addLamppost(IsoLightSource var1) {
      if (var1 != null && !this.LamppostPositions.contains(var1)) {
         this.LamppostPositions.add(var1);
         IsoGridSquare.RecalcLightTime = -1;
         GameTime.instance.lightSourceUpdate = 100.0F;
      }
   }

   public IsoLightSource addLamppost(int var1, int var2, int var3, float var4, float var5, float var6, int var7) {
      IsoLightSource var8 = new IsoLightSource(var1, var2, var3, var4, var5, var6, var7);
      this.LamppostPositions.add(var8);
      IsoGridSquare.RecalcLightTime = -1;
      GameTime.instance.lightSourceUpdate = 100.0F;
      return var8;
   }

   public void removeLamppost(int var1, int var2, int var3) {
      for(int var4 = 0; var4 < this.LamppostPositions.size(); ++var4) {
         IsoLightSource var5 = (IsoLightSource)this.LamppostPositions.get(var4);
         if (var5.getX() == var1 && var5.getY() == var2 && var5.getZ() == var3) {
            var5.clearInfluence();
            this.LamppostPositions.remove(var5);
            IsoGridSquare.RecalcLightTime = -1;
            GameTime.instance.lightSourceUpdate = 100.0F;
            return;
         }
      }

   }

   public void removeLamppost(IsoLightSource var1) {
      var1.life = 0;
      IsoGridSquare.RecalcLightTime = -1;
      GameTime.instance.lightSourceUpdate = 100.0F;
   }

   public void setLamppostPositions(Stack var1) {
      this.LamppostPositions = var1;
   }

   public HashSet getStairsNodes() {
      return this.stairsNodes;
   }

   public void setStairsNodes(HashSet var1) {
      this.stairsNodes = var1;
   }

   public Stack getTempZoneStack() {
      return this.tempZoneStack;
   }

   public void setTempZoneStack(Stack var1) {
      this.tempZoneStack = var1;
   }

   public int getCurrentLightX() {
      return this.currentLX;
   }

   public void setCurrentLightX(int var1) {
      this.currentLX = var1;
   }

   public int getCurrentLightY() {
      return this.currentLY;
   }

   public void setCurrentLightY(int var1) {
      this.currentLY = var1;
   }

   public int getCurrentLightZ() {
      return this.currentLZ;
   }

   public void setCurrentLightZ(int var1) {
      this.currentLZ = var1;
   }

   public IsoSprite getWoodWallN() {
      return this.woodWallN;
   }

   public void setWoodWallN(IsoSprite var1) {
      this.woodWallN = var1;
   }

   public IsoSprite getWoodWallW() {
      return this.woodWallW;
   }

   public void setWoodWallW(IsoSprite var1) {
      this.woodWallW = var1;
   }

   public IsoSprite getWoodDWallN() {
      return this.woodDWallN;
   }

   public void setWoodDWallN(IsoSprite var1) {
      this.woodDWallN = var1;
   }

   public IsoSprite getWoodDWallW() {
      return this.woodDWallW;
   }

   public void setWoodDWallW(IsoSprite var1) {
      this.woodDWallW = var1;
   }

   public IsoSprite getWoodWWallN() {
      return this.woodWWallN;
   }

   public void setWoodWWallN(IsoSprite var1) {
      this.woodWWallN = var1;
   }

   public IsoSprite getWoodWWallW() {
      return this.woodWWallW;
   }

   public void setWoodWWallW(IsoSprite var1) {
      this.woodWWallW = var1;
   }

   public IsoSprite getWoodDoorW() {
      return this.woodDoorW;
   }

   public void setWoodDoorW(IsoSprite var1) {
      this.woodDoorW = var1;
   }

   public IsoSprite getWoodDoorN() {
      return this.woodDoorN;
   }

   public void setWoodDoorN(IsoSprite var1) {
      this.woodDoorN = var1;
   }

   public IsoSprite getWoodFloor() {
      return this.woodFloor;
   }

   public void setWoodFloor(IsoSprite var1) {
      this.woodFloor = var1;
   }

   public IsoSprite getWoodBarricade() {
      return this.woodBarricade;
   }

   public void setWoodBarricade(IsoSprite var1) {
      this.woodBarricade = var1;
   }

   public IsoSprite getWoodCrate() {
      return this.woodCrate;
   }

   public void setWoodCrate(IsoSprite var1) {
      this.woodCrate = var1;
   }

   public IsoSprite getWoodStairsNB() {
      return this.woodStairsNB;
   }

   public void setWoodStairsNB(IsoSprite var1) {
      this.woodStairsNB = var1;
   }

   public IsoSprite getWoodStairsNM() {
      return this.woodStairsNM;
   }

   public void setWoodStairsNM(IsoSprite var1) {
      this.woodStairsNM = var1;
   }

   public IsoSprite getWoodStairsNT() {
      return this.woodStairsNT;
   }

   public void setWoodStairsNT(IsoSprite var1) {
      this.woodStairsNT = var1;
   }

   public IsoSprite getWoodStairsWB() {
      return this.woodStairsWB;
   }

   public void setWoodStairsWB(IsoSprite var1) {
      this.woodStairsWB = var1;
   }

   public IsoSprite getWoodStairsWM() {
      return this.woodStairsWM;
   }

   public void setWoodStairsWM(IsoSprite var1) {
      this.woodStairsWM = var1;
   }

   public IsoSprite getWoodStairsWT() {
      return this.woodStairsWT;
   }

   public void setWoodStairsWT(IsoSprite var1) {
      this.woodStairsWT = var1;
   }

   public int getMinX() {
      return this.minX;
   }

   public void setMinX(int var1) {
      this.minX = var1;
   }

   public int getMaxX() {
      return this.maxX;
   }

   public void setMaxX(int var1) {
      this.maxX = var1;
   }

   public int getMinY() {
      return this.minY;
   }

   public void setMinY(int var1) {
      this.minY = var1;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public void setMaxY(int var1) {
      this.maxY = var1;
   }

   public int getMinZ() {
      return this.minZ;
   }

   public void setMinZ(int var1) {
      this.minZ = var1;
   }

   public int getMaxZ() {
      return this.maxZ;
   }

   public void setMaxZ(int var1) {
      this.maxZ = var1;
   }

   public OnceEvery getDangerUpdate() {
      return this.dangerUpdate;
   }

   public void setDangerUpdate(OnceEvery var1) {
      this.dangerUpdate = var1;
   }

   public Thread getLightInfoUpdate() {
      return this.LightInfoUpdate;
   }

   public void setLightInfoUpdate(Thread var1) {
      this.LightInfoUpdate = var1;
   }

   public ArrayList getSurvivorList() {
      return this.SurvivorList;
   }

   public static int getRComponent(int var0) {
      return var0 & 255;
   }

   public static int getGComponent(int var0) {
      return (var0 & '\uff00') >> 8;
   }

   public static int getBComponent(int var0) {
      return (var0 & 16711680) >> 16;
   }

   public static int toIntColor(float var0, float var1, float var2, float var3) {
      return (int)(var0 * 255.0F) << 0 | (int)(var1 * 255.0F) << 8 | (int)(var2 * 255.0F) << 16 | (int)(var3 * 255.0F) << 24;
   }

   public IsoGridSquare getRandomOutdoorTile() {
      IsoGridSquare var1 = null;

      do {
         var1 = this.getGridSquare(this.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldXMin() * 10 + Rand.Next(this.width), this.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldYMin() * 10 + Rand.Next(this.height), 0);
         if (var1 != null) {
            var1.setCachedIsFree(false);
         }
      } while(var1 == null || !var1.isFree(false) || var1.getRoom() != null);

      return var1;
   }

   private static void InsertAt(int var0, BuildingScore var1, BuildingScore[] var2) {
      for(int var3 = var2.length - 1; var3 > var0; --var3) {
         var2[var3] = var2[var3 - 1];
      }

      var2[var0] = var1;
   }

   static void Place(BuildingScore var0, BuildingScore[] var1, IsoCell.BuildingSearchCriteria var2) {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var1[var3] != null) {
            boolean var4 = false;
            if (var1[var3] == null) {
               var4 = true;
            } else {
               switch(var2) {
               case General:
                  if (var1[var3].food + var1[var3].defense + (float)var1[var3].size + var1[var3].weapons < var0.food + var0.defense + (float)var0.size + var0.weapons) {
                     var4 = true;
                  }
                  break;
               case Food:
                  if (var1[var3].food < var0.food) {
                     var4 = true;
                  }
                  break;
               case Wood:
                  if (var1[var3].wood < var0.wood) {
                     var4 = true;
                  }
                  break;
               case Weapons:
                  if (var1[var3].weapons < var0.weapons) {
                     var4 = true;
                  }
                  break;
               case Defense:
                  if (var1[var3].defense < var0.defense) {
                     var4 = true;
                  }
               }
            }

            if (var4) {
               InsertAt(var3, var0, var1);
               return;
            }
         }
      }

   }

   public Stack getBestBuildings(IsoCell.BuildingSearchCriteria var1, int var2) {
      BuildingScore[] var3 = new BuildingScore[var2];
      int var4;
      int var5;
      if (this.BuildingScores.isEmpty()) {
         var4 = this.BuildingList.size();

         for(var5 = 0; var5 < var4; ++var5) {
            ((IsoBuilding)this.BuildingList.get(var5)).update();
         }
      }

      var4 = this.BuildingScores.size();

      for(var5 = 0; var5 < var4; ++var5) {
         BuildingScore var6 = (BuildingScore)this.BuildingScores.get(var5);
         Place(var6, var3, var1);
      }

      buildingscores.clear();
      buildingscores.addAll(Arrays.asList(var3));
      return buildingscores;
   }

   public void AddZone(String var1, int var2, int var3, int var4, int var5, int var6) {
      this.ZoneStack.add(new IsoCell.Zone(var1, var2, var3, var4, var5, var6));
   }

   public boolean blocked(Mover var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      IsoGridSquare var8 = this.getGridSquare(var5, var6, var7);
      if (var8 == null) {
         return true;
      } else {
         if (var1 instanceof IsoMovingObject) {
            if (var8.testPathFindAdjacent((IsoMovingObject)var1, var2 - var5, var3 - var6, var4 - var7)) {
               return true;
            }
         } else if (var8.testPathFindAdjacent((IsoMovingObject)null, var2 - var5, var3 - var6, var4 - var7)) {
            return true;
         }

         return false;
      }
   }

   public void Dispose() {
      int var1;
      for(var1 = 0; var1 < this.ObjectList.size(); ++var1) {
         IsoMovingObject var2 = (IsoMovingObject)this.ObjectList.get(var1);
         if (var2 instanceof IsoZombie) {
            var2.setCurrent((IsoGridSquare)null);
            var2.setLast((IsoGridSquare)null);
            VirtualZombieManager.instance.addToReusable((IsoZombie)var2);
         }
      }

      this.stopLightingThread();
      super.Dispose();

      for(var1 = 0; var1 < this.RoomList.size(); ++var1) {
         ((IsoRoom)this.RoomList.get(var1)).TileList.clear();
         ((IsoRoom)this.RoomList.get(var1)).Exits.clear();
         ((IsoRoom)this.RoomList.get(var1)).WaterSources.clear();
         ((IsoRoom)this.RoomList.get(var1)).lightSwitches.clear();
         ((IsoRoom)this.RoomList.get(var1)).Beds.clear();
      }

      for(var1 = 0; var1 < this.BuildingList.size(); ++var1) {
         ((IsoBuilding)this.BuildingList.get(var1)).Exits.clear();
         ((IsoBuilding)this.BuildingList.get(var1)).Rooms.clear();
         ((IsoBuilding)this.BuildingList.get(var1)).container.clear();
         ((IsoBuilding)this.BuildingList.get(var1)).Windows.clear();
      }

      LuaEventManager.clear();
      LuaHookManager.clear();
      this.ZoneStack.clear();
      this.LamppostPositions.clear();
      this.PathMap.map = null;
      this.PathMap = null;
      this.GhostList.clear();
      this.ProcessItems.clear();
      this.ProcessItemsRemove.clear();
      this.ProcessWorldItems.clear();
      this.ProcessWorldItemsRemove.clear();
      this.BuildingScores.clear();
      this.BuildingList.clear();
      this.PushableObjectList.clear();
      this.RoomList.clear();
      this.SurvivorList.clear();
      this.ObjectList.clear();
      this.ZombieList.clear();

      for(var1 = 0; var1 < this.ChunkMap.length; ++var1) {
         this.ChunkMap[var1].Dispose();
         this.ChunkMap[var1] = null;
      }

   }

   public float getCost(Mover var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      float var8 = 0.0F;
      if (var2 != var5 && var3 != var6) {
         ++var8;
      } else {
         ++var8;
      }

      IsoGridSquare var9;
      if (var1 instanceof IsoZombie) {
         var9 = this.getGridSquare(var2, var3, var4);
         if (var9 == null) {
            return 100000.0F;
         } else {
            var8 *= (float)((var9.getMovingObjects().size() + 1) * 3);
            return var9.Has(IsoObjectType.tree) ? var8 * 100.0F : var8;
         }
      } else {
         if (var1 instanceof IsoLivingCharacter) {
            var9 = this.getGridSquare(var2, var3, var4);
            IsoGridSquare var10 = this.getGridSquare(var5, var6, var7);
            if (var9 == null) {
               return 1000000.0F;
            }

            if (var10 != null) {
               if (var9.IsWindow(var10.getX() - var9.getX(), var10.getY() - var9.getY(), 0)) {
                  var8 += 200.0F;
               }

               label53: {
                  if (var10.SolidFloorCached) {
                     if (var10.SolidFloor) {
                        break label53;
                     }
                  } else if (var10.TreatAsSolidFloor()) {
                     break label53;
                  }

                  var8 += 10000.0F;
               }

               int var11 = var10.getMovingObjects().size();

               for(int var12 = 0; var12 < var11; ++var12) {
                  IsoMovingObject var13 = (IsoMovingObject)var10.getMovingObjects().get(var12);
                  if (var13 instanceof IsoPushableObject) {
                     var8 += 10000.0F;
                  }

                  if (var13 instanceof IsoZombie) {
                     var8 += 10.0F;
                  }
               }
            }
         }

         return var8;
      }
   }

   public int getElevInTiles() {
      return 8;
   }

   public IsoGridSquare getFreeTile(String var1) {
      Iterator var2 = this.ZoneStack.iterator();

      IsoCell.Zone var3;
      while(var2 != null && var2.hasNext()) {
         var3 = (IsoCell.Zone)var2.next();
         if (var3.Name.equals(var1)) {
            this.tempZoneStack.add(var3);
         }
      }

      if (this.tempZoneStack.isEmpty()) {
         return null;
      } else {
         var3 = (IsoCell.Zone)this.tempZoneStack.get(Rand.Next(this.tempZoneStack.size()));
         this.tempZoneStack.clear();
         return this.getFreeTile(var3);
      }
   }

   public IsoGridSquare getFreeTile(IsoCell.Zone var1) {
      ArrayList var2 = new ArrayList();

      for(int var3 = 0; var3 < var1.W; ++var3) {
         for(int var4 = 0; var4 < var1.H; ++var4) {
            IsoGridSquare var5 = this.getGridSquare(var3 + var1.X, var4 + var1.Y, var1.Z);
            if (var5 != null && !var5.getProperties().Is(IsoFlagType.solid) && !var5.getProperties().Is(IsoFlagType.solidtrans)) {
               var2.add(var5);
            }
         }
      }

      if (!var2.isEmpty()) {
         return (IsoGridSquare)var2.get(Rand.Next(var2.size()));
      } else {
         return null;
      }
   }

   @LuaMethod(
      name = "getGridSquare"
   )
   public IsoGridSquare getGridSquare(double var1, double var3, double var5) {
      return GameServer.bServer ? ServerMap.instance.getGridSquare((int)var1, (int)var3, (int)var5) : this.getGridSquare((int)var1, (int)var3, (int)var5);
   }

   @LuaMethod(
      name = "getOrCreateGridSquare"
   )
   public IsoGridSquare getOrCreateGridSquare(double var1, double var3, double var5) {
      if (GameServer.bServer) {
         IsoGridSquare var9 = ServerMap.instance.getGridSquare((int)var1, (int)var3, (int)var5);
         if (var9 == null) {
            var9 = IsoGridSquare.getNew(this, (SliceY)null, (int)var1, (int)var3, (int)var5);
            ServerMap.instance.setGridSquare((int)var1, (int)var3, (int)var5, var9);
            this.ConnectNewSquare(var9, true);
         }

         return var9;
      } else {
         IsoChunkMap var7 = this.ChunkMap[IsoPlayer.getPlayerIndex()];
         if (!(var5 >= 8.0D) && !(var5 < 0.0D) && !(var1 < (double)var7.getWorldXMinTiles()) && !(var1 >= (double)var7.getWorldXMaxTiles()) && !(var3 < (double)var7.getWorldYMinTiles()) && !(var3 >= (double)var7.getWorldYMaxTiles())) {
            IsoGridSquare var8 = this.getGridSquare((int)var1, (int)var3, (int)var5);
            if (var8 == null) {
               var8 = IsoGridSquare.getNew(this, (SliceY)null, (int)var1, (int)var3, (int)var5);
               this.ConnectNewSquare(var8, true);
            }

            return var8;
         } else {
            return null;
         }
      }
   }

   public void setCacheGridSquare(int var1, int var2, int var3, IsoGridSquare var4) {
      assert var4 == null || var1 == var4.getX() && var2 == var4.getY() && var3 == var4.getZ();

      if (ENABLE_SQUARE_CACHE) {
         if (!GameServer.bServer) {
            assert this.getChunkForGridSquare(var1, var2, var3) != null;

            int var5 = IsoChunkMap.ChunkWidthInTiles;

            for(int var6 = 0; var6 < IsoPlayer.numPlayers; ++var6) {
               if (!this.ChunkMap[var6].ignore) {
                  this.ChunkMap[var6].YMinTiles = -1;
                  this.ChunkMap[var6].XMinTiles = -1;
                  this.ChunkMap[var6].YMaxTiles = -1;
                  this.ChunkMap[var6].XMaxTiles = -1;
                  int var7 = var1 - this.ChunkMap[var6].getWorldXMinTiles();
                  int var8 = var2 - this.ChunkMap[var6].getWorldYMinTiles();
                  if (var3 < 8 && var3 >= 0 && var7 >= 0 && var7 < var5 && var8 >= 0 && var8 < var5) {
                     this.gridSquares[var6][var7 + var8 * var5 + var3 * var5 * var5] = var4;
                  }
               }
            }

         }
      }
   }

   public void setCacheChunk(IsoChunk var1) {
      if (!GameServer.bServer) {
         if (ENABLE_SQUARE_CACHE) {
            int var2 = IsoChunkMap.ChunkWidthInTiles;

            for(int var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
               IsoChunkMap var4 = this.ChunkMap[var3];
               if (!var4.ignore) {
                  int var5 = var1.wx - var4.getWorldXMin();
                  int var6 = var1.wy - var4.getWorldYMin();
                  if (var5 >= 0 && var5 < IsoChunkMap.ChunkGridWidth && var6 >= 0 && var6 < IsoChunkMap.ChunkGridWidth) {
                     for(int var7 = 0; var7 < 8; ++var7) {
                        for(int var8 = 0; var8 < 10; ++var8) {
                           for(int var9 = 0; var9 < 10; ++var9) {
                              IsoGridSquare var10 = var1.squares[var7][var9 + var8 * 10];
                              int var11 = var5 * 10 + var9;
                              int var12 = var6 * 10 + var8;
                              this.gridSquares[var3][var11 + var12 * var2 + var7 * var2 * var2] = var10;
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   public void clearCacheGridSquare(int var1) {
      if (ENABLE_SQUARE_CACHE) {
         if (!GameServer.bServer) {
            int var2 = IsoChunkMap.ChunkWidthInTiles;
            this.gridSquares[var1] = new IsoGridSquare[var2 * var2 * 8];
         }
      }
   }

   public void setCacheGridSquareLocal(int var1, int var2, int var3, IsoGridSquare var4, int var5) {
      if (ENABLE_SQUARE_CACHE) {
         if (!GameServer.bServer) {
            int var6 = IsoChunkMap.ChunkWidthInTiles;
            if (var3 < 8 && var3 >= 0 && var1 >= 0 && var1 < var6 && var2 >= 0 && var2 < var6) {
               this.gridSquares[var5][var1 + var2 * var6 + var3 * var6 * var6] = var4;
            }
         }
      }
   }

   public IsoGridSquare getGridSquare(Double var1, Double var2, Double var3) {
      return this.getGridSquare(var1.intValue(), var2.intValue(), var3.intValue());
   }

   public IsoGridSquare getGridSquare(int var1, int var2, int var3) {
      if (GameServer.bServer) {
         return ServerMap.instance.getGridSquare(var1, var2, var3);
      } else {
         int var4 = IsoChunkMap.ChunkWidthInTiles;

         for(int var5 = 0; var5 < IsoPlayer.numPlayers; ++var5) {
            if (!this.ChunkMap[var5].ignore) {
               if (var3 == 0) {
                  boolean var6 = false;
               }

               int var9 = var1 - this.ChunkMap[var5].getWorldXMinTiles();
               int var7 = var2 - this.ChunkMap[var5].getWorldYMinTiles();
               if (var3 < 8 && var3 >= 0 && var9 >= 0 && var9 < var4 && var7 >= 0 && var7 < var4) {
                  IsoGridSquare var8 = ENABLE_SQUARE_CACHE ? this.gridSquares[var5][var9 + var7 * var4 + var3 * var4 * var4] : this.ChunkMap[var5].getGridSquareDirect(var9, var7, var3);
                  if (var8 != null) {
                     return var8;
                  }
               }
            }
         }

         return null;
      }
   }

   public void EnsureSurroundNotNull(int var1, int var2, int var3) {
      for(int var4 = -1; var4 <= 1; ++var4) {
         for(int var5 = -1; var5 <= 1; ++var5) {
            this.createNewGridSquare(var1 + var4, var2 + var5, var3, false);
         }
      }

   }

   public void DeleteAllMovingObjects() {
      this.ObjectList.clear();
   }

   @LuaMethod(
      name = "getMaxFloors"
   )
   public int getMaxFloors() {
      return 8;
   }

   public KahluaTable getLuaObjectList() {
      KahluaTable var1 = LuaManager.platform.newTable();
      LuaManager.env.rawset("Objects", var1);

      for(int var2 = 0; var2 < this.ObjectList.size(); ++var2) {
         var1.rawset(var2 + 1, this.ObjectList.get(var2));
      }

      return var1;
   }

   public int getHeightInTiles() {
      return this.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
   }

   public int getWidthInTiles() {
      return this.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
   }

   public boolean isNull(int var1, int var2, int var3) {
      IsoGridSquare var4 = this.getGridSquare(var1, var2, var3);
      return var4 == null || !var4.isFree(false);
   }

   public boolean IsZone(String var1, int var2, int var3) {
      for(int var4 = 0; var4 < this.ZoneStack.size(); ++var4) {
         IsoCell.Zone var5 = (IsoCell.Zone)this.ZoneStack.get(var4);
         if (var5.Name.equals(var1) && var2 >= var5.X && var2 < var5.X + var5.W && var3 >= var5.Y && var3 < var5.Y + var5.H) {
            return true;
         }
      }

      return false;
   }

   public void pathFinderVisited(int var1, int var2, int var3) {
   }

   public void Remove(IsoMovingObject var1) {
      if (!(var1 instanceof IsoPlayer) || ((IsoPlayer)var1).isDead()) {
         this.removeList.add(var1);
      }
   }

   boolean isBlocked(IsoGridSquare var1, IsoGridSquare var2) {
      return var1.room != var2.room;
   }

   private int CalculateColor(IsoGridSquare var1, IsoGridSquare var2, IsoGridSquare var3, IsoGridSquare var4, int var5, int var6) {
      float var7 = 0.0F;
      float var8 = 0.0F;
      float var9 = 0.0F;
      float var10 = 1.0F;
      if (var4 == null) {
         return 0;
      } else {
         float var11 = 0.0F;
         boolean var12 = true;
         ColorInfo var13;
         if (var1 != null && var4.room == var1.room && var1.getChunk() != null) {
            ++var11;
            var13 = var1.lighting[var6].lightInfo();
            var7 += var13.r;
            var8 += var13.g;
            var9 += var13.b;
         }

         if (var2 != null && var4.room == var2.room && var2.getChunk() != null) {
            ++var11;
            var13 = var2.lighting[var6].lightInfo();
            var7 += var13.r;
            var8 += var13.g;
            var9 += var13.b;
         }

         if (var3 != null && var4.room == var3.room && var3.getChunk() != null) {
            ++var11;
            var13 = var3.lighting[var6].lightInfo();
            var7 += var13.r;
            var8 += var13.g;
            var9 += var13.b;
         }

         if (var4 != null) {
            ++var11;
            var13 = var4.lighting[var6].lightInfo();
            var7 += var13.r;
            var8 += var13.g;
            var9 += var13.b;
         }

         if (var11 != 0.0F) {
            var7 /= var11;
            var8 /= var11;
            var9 /= var11;
         }

         if (var7 > 1.0F) {
            var7 = 1.0F;
         }

         if (var8 > 1.0F) {
            var8 = 1.0F;
         }

         if (var9 > 1.0F) {
            var9 = 1.0F;
         }

         if (var7 < 0.0F) {
            var7 = 0.0F;
         }

         if (var8 < 0.0F) {
            var8 = 0.0F;
         }

         if (var9 < 0.0F) {
            var9 = 0.0F;
         }

         if (var4 != null) {
            var4.setVertLight(var5, (int)(var7 * 255.0F) << 0 | (int)(var8 * 255.0F) << 8 | (int)(var9 * 255.0F) << 16 | -16777216, var6);
            var4.setVertLight(var5 + 4, (int)(var7 * 255.0F) << 0 | (int)(var8 * 255.0F) << 8 | (int)(var9 * 255.0F) << 16 | -16777216, var6);
         }

         return var5;
      }
   }

   public void stopLightingThread() {
      if (this.lightingThread != null) {
         this.lightingThread.stop();
         this.lightingThread = null;
      }
   }

   public void initLightingThread() {
      if (this.lightingThread == null) {
         this.lightingThread = new Thread(new Runnable() {
            public void run() {
            }
         });
      }

   }

   public void render() {
      int var1 = IsoCamera.frameState.playerIndex;
      IsoPlayer var2 = IsoPlayer.players[var1];
      if (var2.dirtyRecalcGridStackTime > 0.0F) {
         var2.dirtyRecalcGridStack = true;
      } else {
         var2.dirtyRecalcGridStack = false;
      }

      if (!PerformanceSettings.NewRoofHiding) {
         if (this.bHideFloors[var1] && this.unhideFloorsCounter[var1] > 0) {
            int var10002 = this.unhideFloorsCounter[var1]--;
         }

         if (this.unhideFloorsCounter[var1] <= 0) {
            this.bHideFloors[var1] = false;
            this.unhideFloorsCounter[var1] = 60;
         }
      }

      int var3 = 8;
      if (var3 < 8) {
         ++var3;
      }

      --this.recalcShading;
      byte var4 = 0;
      byte var5 = 0;
      int var6 = var4 + IsoCamera.getOffscreenWidth(var1);
      int var7 = var5 + IsoCamera.getOffscreenHeight(var1);
      float var8 = IsoUtils.XToIso((float)var4, (float)var5, 0.0F);
      float var9 = IsoUtils.YToIso((float)var6, (float)var5, 0.0F);
      float var10 = IsoUtils.XToIso((float)var6, (float)var7, 6.0F);
      float var11 = IsoUtils.YToIso((float)var4, (float)var7, 6.0F);
      this.minY = (int)var9;
      this.maxY = (int)var11;
      this.minX = (int)var8;
      this.maxX = (int)var10;
      this.minX -= 2;
      this.minY -= 2;
      this.maxZ = MaxHeight;
      if (IsoCamera.CamCharacter == null) {
         this.maxZ = 1;
      }

      boolean var12 = false;
      var12 = true;
      if (GameTime.instance.FPSMultiplier > 1.5F) {
         var12 = true;
      }

      if (this.minX != this.lastMinX || this.minY != this.lastMinY) {
         this.lightUpdateCount = 10;
      }

      ++this.alt;
      if (this.alt == 3) {
         this.alt = 0;
      }

      IsoGridSquare var13;
      IsoGridSquare var14;
      if (!PerformanceSettings.NewRoofHiding) {
         var13 = IsoCamera.CamCharacter == null ? null : IsoCamera.CamCharacter.getCurrentSquare();
         if (var13 != null) {
            var14 = this.getGridSquare((double)Math.round(IsoCamera.CamCharacter.getX()), (double)Math.round(IsoCamera.CamCharacter.getY()), (double)IsoCamera.CamCharacter.getZ());
            if (var14 != null && this.IsBehindStuff(var14)) {
               this.bHideFloors[var1] = true;
            }

            if (!this.bHideFloors[var1] && var13.getProperties().Is(IsoFlagType.hidewalls) || !var13.getProperties().Is(IsoFlagType.exterior)) {
               this.bHideFloors[var1] = true;
            }
         }

         if (this.bHideFloors[var1]) {
            this.maxZ = (int)IsoCamera.CamCharacter.getZ() + 1;
         }
      }

      if (PerformanceSettings.LightingFrameSkip < 3) {
         this.DrawStencilMask();
      }

      int var30;
      int var31;
      if (PerformanceSettings.LightingFrameSkip == 3) {
         var30 = IsoCamera.getOffscreenWidth(var1) / 2;
         var31 = IsoCamera.getOffscreenHeight(var1) / 2;
         short var15 = 409;
         var30 -= var15 / (2 / Core.TileScale);
         var31 -= var15 / (2 / Core.TileScale);
         this.StencilX1 = var30 - (int)IsoCamera.RightClickX[var1];
         this.StencilY1 = var31 - (int)IsoCamera.RightClickY[var1];
         this.StencilX2 = this.StencilX1 + var15 * Core.TileScale;
         this.StencilY2 = this.StencilY1 + var15 * Core.TileScale;
      }

      if (PerformanceSettings.NewRoofHiding && var2.dirtyRecalcGridStack) {
         this.hidesOrphanStructuresAbove = var3;
         var13 = null;
         ((ArrayList)this.otherOccluderBuildings.get(var1)).clear();
         if (this.otherOccluderBuildingsArr[var1] != null) {
            this.otherOccluderBuildingsArr[var1][0] = null;
         } else {
            this.otherOccluderBuildingsArr[var1] = new IsoBuilding[500];
         }

         if (IsoCamera.CamCharacter != null && IsoCamera.CamCharacter.getCurrentSquare() != null) {
            var14 = IsoCamera.CamCharacter.getCurrentSquare();
            int var32 = 10;
            if (this.ZombieList.size() < 10) {
               var32 = this.ZombieList.size();
            }

            if (this.nearestVisibleZombie[var1] != null) {
               if (this.nearestVisibleZombie[var1].bDead) {
                  this.nearestVisibleZombie[var1] = null;
               } else {
                  float var16 = this.nearestVisibleZombie[var1].x - IsoCamera.CamCharacter.x;
                  float var17 = this.nearestVisibleZombie[var1].y - IsoCamera.CamCharacter.y;
                  this.nearestVisibleZombieDistSqr[var1] = var16 * var16 + var17 * var17;
               }
            }

            IsoGridSquare var18;
            int var35;
            for(var35 = 0; var35 < var32; ++this.zombieScanCursor) {
               if (this.zombieScanCursor >= this.ZombieList.size()) {
                  this.zombieScanCursor = 0;
               }

               IsoZombie var38 = (IsoZombie)this.ZombieList.get(this.zombieScanCursor);
               if (var38 != null) {
                  var18 = var38.getCurrentSquare();
                  if (var18 != null && var14.z == var18.z && var18.getCanSee(var1)) {
                     float var19;
                     float var20;
                     if (this.nearestVisibleZombie[var1] == null) {
                        this.nearestVisibleZombie[var1] = var38;
                        var19 = this.nearestVisibleZombie[var1].x - IsoCamera.CamCharacter.x;
                        var20 = this.nearestVisibleZombie[var1].y - IsoCamera.CamCharacter.y;
                        this.nearestVisibleZombieDistSqr[var1] = var19 * var19 + var20 * var20;
                     } else {
                        var19 = var38.x - IsoCamera.CamCharacter.x;
                        var20 = var38.y - IsoCamera.CamCharacter.y;
                        float var21 = var19 * var19 + var20 * var20;
                        if (var21 < this.nearestVisibleZombieDistSqr[var1]) {
                           this.nearestVisibleZombie[var1] = var38;
                           this.nearestVisibleZombieDistSqr[var1] = var21;
                        }
                     }
                  }
               }

               ++var35;
            }

            IsoBuilding var25;
            for(var35 = 0; var35 < 4; ++var35) {
               IsoPlayer var39 = IsoPlayer.players[var35];
               if (var39 != null && var39.getCurrentSquare() != null) {
                  var18 = var39.getCurrentSquare();
                  if (var35 == var1) {
                     var13 = var18;
                  }

                  double var44 = (double)var39.x - Math.floor((double)var39.x);
                  double var46 = (double)var39.y - Math.floor((double)var39.y);
                  boolean var23 = var44 > var46;
                  IsoDirections var24 = IsoDirections.cardinalFromAngle(var39.angle);
                  if (this.lastPlayerSquare[var35] != var18 || this.lastPlayerSquareHalf[var35] != var23 || this.lastPlayerCardinalDir[var35] != var24) {
                     this.lastPlayerSquare[var35] = var18;
                     this.lastPlayerSquareHalf[var35] = var23;
                     this.lastPlayerCardinalDir[var35] = var24;
                     var25 = var18.getBuilding();
                     this.playerWindowPeeking[var35] = false;
                     this.GetBuildingsInFrontOfCharacter((ArrayList)this.playerOccluderBuildings.get(var35), var18, var23);
                     if (this.playerOccluderBuildingsArr[var1] == null) {
                        this.playerOccluderBuildingsArr[var1] = new IsoBuilding[500];
                     }

                     this.playerHidesOrphanStructures[var35] = this.bOccludedByOrphanStructureFlag;
                     if (var25 == null && !var39.bRemote) {
                        var25 = this.GetPeekedInBuilding(var18, var24, 2);
                        if (var25 != null) {
                           this.playerWindowPeeking[var35] = true;
                        }
                     }

                     if (var25 != null) {
                        this.AddUniqueToBuildingList((ArrayList)this.playerOccluderBuildings.get(var35), var25);
                     }

                     ArrayList var26 = (ArrayList)this.playerOccluderBuildings.get(var35);

                     for(int var27 = 0; var27 < var26.size(); ++var27) {
                        IsoBuilding var28 = (IsoBuilding)var26.get(var27);
                        this.playerOccluderBuildingsArr[var1][var27] = var28;
                     }

                     this.playerOccluderBuildingsArr[var1][var26.size()] = null;
                  }

                  if (var35 == var1 && var13 != null) {
                     this.gridSquaresTempLeft.clear();
                     this.gridSquaresTempRight.clear();
                     this.GetSquaresAroundPlayerSquare(var39, var13, this.gridSquaresTempLeft, this.gridSquaresTempRight);
                     Iterator var49 = this.gridSquaresTempLeft.iterator();

                     label425:
                     while(true) {
                        boolean[] var10000;
                        IsoGridSquare var50;
                        ArrayList var53;
                        int var54;
                        do {
                           do {
                              if (!var49.hasNext()) {
                                 var49 = this.gridSquaresTempRight.iterator();

                                 while(true) {
                                    do {
                                       do {
                                          if (!var49.hasNext()) {
                                             ArrayList var51 = (ArrayList)this.otherOccluderBuildings.get(var1);
                                             if (this.otherOccluderBuildingsArr[var1] == null) {
                                                this.otherOccluderBuildingsArr[var1] = new IsoBuilding[500];
                                             }

                                             for(int var52 = 0; var52 < var51.size(); ++var52) {
                                                IsoBuilding var55 = (IsoBuilding)var51.get(var52);
                                                this.otherOccluderBuildingsArr[var1][var52] = var55;
                                             }

                                             this.otherOccluderBuildingsArr[var1][var51.size()] = null;
                                             break label425;
                                          }

                                          var50 = (IsoGridSquare)var49.next();
                                       } while(!var50.getCanSee(var1));
                                    } while(var50.getBuilding() != null && var50.getBuilding() != var13.getBuilding());

                                    var53 = this.GetBuildingsInFrontOfMustSeeSquare(var50, IsoGridOcclusionData.OcclusionFilter.Left);

                                    for(var54 = 0; var54 < var53.size(); ++var54) {
                                       this.AddUniqueToBuildingList((ArrayList)this.otherOccluderBuildings.get(var1), (IsoBuilding)var53.get(var54));
                                    }

                                    var10000 = this.playerHidesOrphanStructures;
                                    var10000[var1] |= this.bOccludedByOrphanStructureFlag;
                                 }
                              }

                              var50 = (IsoGridSquare)var49.next();
                           } while(!var50.getCanSee(var1));
                        } while(var50.getBuilding() != null && var50.getBuilding() != var13.getBuilding());

                        var53 = this.GetBuildingsInFrontOfMustSeeSquare(var50, IsoGridOcclusionData.OcclusionFilter.Right);

                        for(var54 = 0; var54 < var53.size(); ++var54) {
                           this.AddUniqueToBuildingList((ArrayList)this.otherOccluderBuildings.get(var1), (IsoBuilding)var53.get(var54));
                        }

                        var10000 = this.playerHidesOrphanStructures;
                        var10000[var1] |= this.bOccludedByOrphanStructureFlag;
                     }
                  }

                  if (this.playerHidesOrphanStructures[var35] && this.hidesOrphanStructuresAbove > var18.getZ()) {
                     this.hidesOrphanStructuresAbove = var18.getZ();
                  }
               }
            }

            if (var13 != null && this.hidesOrphanStructuresAbove < var13.getZ()) {
               this.hidesOrphanStructuresAbove = var13.getZ();
            }

            boolean var40 = false;
            if (this.nearestVisibleZombie[var1] != null && this.nearestVisibleZombieDistSqr[var1] < 150.0F) {
               IsoGridSquare var41 = this.nearestVisibleZombie[var1].getCurrentSquare();
               if (var41 != null && var41.getCanSee(var1)) {
                  double var43 = (double)this.nearestVisibleZombie[var1].x - Math.floor((double)this.nearestVisibleZombie[var1].x);
                  double var45 = (double)this.nearestVisibleZombie[var1].y - Math.floor((double)this.nearestVisibleZombie[var1].y);
                  boolean var22 = var43 > var45;
                  var40 = true;
                  if (this.lastZombieSquare[var1] != var41 || this.lastZombieSquareHalf[var1] != var22) {
                     this.lastZombieSquare[var1] = var41;
                     this.lastZombieSquareHalf[var1] = var22;
                     this.GetBuildingsInFrontOfCharacter((ArrayList)this.zombieOccluderBuildings.get(var1), var41, var22);
                     ArrayList var47 = (ArrayList)this.zombieOccluderBuildings.get(var1);
                     if (this.zombieOccluderBuildingsArr[var1] == null) {
                        this.zombieOccluderBuildingsArr[var1] = new IsoBuilding[500];
                     }

                     for(int var48 = 0; var48 < var47.size(); ++var48) {
                        var25 = (IsoBuilding)var47.get(var48);
                        this.zombieOccluderBuildingsArr[var1][var48] = var25;
                     }

                     this.zombieOccluderBuildingsArr[var1][var47.size()] = null;
                  }
               }
            }

            if (!var40) {
               ((ArrayList)this.zombieOccluderBuildings.get(var1)).clear();
               if (this.zombieOccluderBuildingsArr[var1] != null) {
                  this.zombieOccluderBuildingsArr[var1][0] = null;
               } else {
                  this.zombieOccluderBuildingsArr[var1] = new IsoBuilding[500];
               }
            }
         } else {
            for(var31 = 0; var31 < 4; ++var31) {
               ((ArrayList)this.playerOccluderBuildings.get(var31)).clear();
               if (this.playerOccluderBuildingsArr[var31] != null) {
                  this.playerOccluderBuildingsArr[var31][0] = null;
               } else {
                  this.playerOccluderBuildingsArr[var31] = new IsoBuilding[500];
               }

               this.lastPlayerSquare[var31] = null;
            }

            this.playerWindowPeeking[var1] = false;
            ((ArrayList)this.zombieOccluderBuildings.get(var1)).clear();
            if (this.zombieOccluderBuildingsArr[var1] != null) {
               this.zombieOccluderBuildingsArr[var1][0] = null;
            } else {
               this.zombieOccluderBuildingsArr[var1] = new IsoBuilding[500];
            }

            this.lastZombieSquare[var1] = null;
         }
      }

      if (!PerformanceSettings.NewRoofHiding) {
         for(var30 = 0; var30 < IsoPlayer.numPlayers; ++var30) {
            this.playerWindowPeeking[var30] = false;
            IsoPlayer var33 = IsoPlayer.players[var30];
            if (var33 != null) {
               IsoBuilding var34 = var33.getCurrentBuilding();
               if (var34 == null) {
                  IsoDirections var42 = IsoDirections.cardinalFromAngle(var33.angle);
                  var34 = this.GetPeekedInBuilding(var33.getCurrentSquare(), var42, 2);
                  if (var34 != null) {
                     this.playerWindowPeeking[var30] = true;
                  }
               }
            }
         }
      }

      if (IsoCamera.CamCharacter != null && IsoCamera.CamCharacter.getCurrentSquare() != null && IsoCamera.CamCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.hidewalls)) {
         this.maxZ = (int)IsoCamera.CamCharacter.getZ() + 1;
      }

      if (PerformanceSettings.LightingFrameSkip < 3 && IsoPlayer.DemoMode) {
         this.maxZ = (int)IsoCamera.CamCharacter.getZ() + 1;
      }

      this.bRendering = true;

      try {
         this.RenderTiles(var3);
      } catch (Exception var29) {
         this.bRendering = false;
         Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, var29);
      }

      this.bRendering = false;
      if (IsoGridSquare.getRecalcLightTime() < 0) {
         IsoGridSquare.setRecalcLightTime(60);
      }

      if (IsoGridSquare.getLightcache() <= 0) {
         IsoGridSquare.setLightcache(90);
      }

      for(var30 = 0; var30 < this.ObjectList.size(); ++var30) {
         IsoMovingObject var36 = (IsoMovingObject)this.ObjectList.get(var30);
         var36.renderlast();
      }

      for(var30 = 0; var30 < this.StaticUpdaterObjectList.size(); ++var30) {
         IsoObject var37 = (IsoObject)this.StaticUpdaterObjectList.get(var30);
         var37.renderlast();
      }

      if (Core.bDebug) {
      }

      this.lastMinX = this.minX;
      this.lastMinY = this.minY;
      if (this.woodDoorW == null) {
         this.woodDoorW = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodDoorW.LoadFramesNoDirPageSimple("TileDoors_8");
      }

      if (this.woodDoorN == null) {
         this.woodDoorN = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodDoorN.LoadFramesNoDirPageSimple("TileDoors_9");
      }

      if (this.woodStairsNB == null) {
         this.woodStairsNB = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodStairsNB.LoadFramesNoDirPageSimple("TileStairs_8");
      }

      if (this.woodStairsNM == null) {
         this.woodStairsNM = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodStairsNM.LoadFramesNoDirPageSimple("TileStairs_9");
      }

      if (this.woodStairsNT == null) {
         this.woodStairsNT = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodStairsNT.LoadFramesNoDirPageSimple("TileStairs_10");
      }

      if (this.woodStairsWB == null) {
         this.woodStairsWB = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodStairsWB.LoadFramesNoDirPageSimple("TileStairs_0");
      }

      if (this.woodStairsWM == null) {
         this.woodStairsWM = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodStairsWM.LoadFramesNoDirPageSimple("TileStairs_1");
      }

      if (this.woodStairsWT == null) {
         this.woodStairsWT = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodStairsWT.LoadFramesNoDirPageSimple("TileStairs_2");
      }

      if (this.woodCrate == null) {
         this.woodCrate = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodCrate.LoadFramesNoDirPageSimple("TileObjects2_0");
      }

      if (this.woodBarricade == null) {
         this.woodBarricade = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodBarricade.LoadFramesNoDirPageSimple("TileObjects2_26");
      }

      if (this.woodWallN == null) {
         this.woodWallN = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodWallN.LoadFramesNoDirPageSimple("TileWalls_49");
      }

      if (this.woodWallW == null) {
         this.woodWallW = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodWallW.LoadFramesNoDirPageSimple("TileWalls_48");
      }

      if (this.woodDWallN == null) {
         this.woodDWallN = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodDWallN.LoadFramesNoDirPageSimple("TileWalls_59");
      }

      if (this.woodDWallW == null) {
         this.woodDWallW = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodDWallW.LoadFramesNoDirPageSimple("TileWalls_58");
      }

      if (this.woodWWallN == null) {
         this.woodWWallN = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodWWallN.LoadFramesNoDirPageSimple("TileWalls_57");
      }

      if (this.woodWWallW == null) {
         this.woodWWallW = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodWWallW.LoadFramesNoDirPageSimple("TileWalls_56");
      }

      if (this.woodFloor == null) {
         this.woodFloor = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.woodFloor.LoadFramesNoDirPageSimple("TileFloorInt_15");
      }

      this.DoBuilding(IsoPlayer.getPlayerIndex(), true);
      this.renderRain();
   }

   private boolean initWeatherFx() {
      if (GameServer.bServer) {
         return false;
      } else {
         if (this.weatherFX == null) {
            this.weatherFX = new IsoWeatherFX();
            this.weatherFX.init();
         }

         return true;
      }
   }

   private void updateWeatherFx() {
      if (this.initWeatherFx()) {
         this.weatherFX.update();
      }

   }

   private void renderWeatherFx() {
      if (this.initWeatherFx()) {
         this.weatherFX.render();
      }

   }

   public IsoWeatherFX getWeatherFX() {
      return this.weatherFX;
   }

   private void renderRain() {
   }

   public void setRainAlpha(int var1) {
      this.rainAlphaMax = (float)var1 / 100.0F;
   }

   public void setRainIntensity(int var1) {
      this.rainIntensity = var1;
   }

   public void setRainSpeed(int var1) {
      this.rainSpeed = var1;
   }

   public void reloadRainTextures() {
      for(int var1 = 1; var1 <= 5; ++var1) {
         File var2 = new File("media/ui/rain" + var1 + ".png");
         if (this.rainFileTime[var1 - 1] != var2.lastModified()) {
            this.rainFileTime[var1 - 1] = var2.lastModified();

            try {
               RenderThread.borrowContext();
               ImageData var3 = new ImageData(var2.getAbsolutePath());
               GL11.glBindTexture(3553, Texture.lastTextureID = this.rainTextures[var1 - 1].getID());
               GL11.glTexParameteri(3553, 10241, 9728);
               GL11.glTexParameteri(3553, 10240, 9728);
               short var4 = 6408;
               GL11.glTexImage2D(3553, 0, var4, this.rainTextures[var1 - 1].getWidthHW(), this.rainTextures[var1 - 1].getHeightHW(), 0, 6408, 5121, var3.getData().getBuffer());
               RenderThread.returnContext();
            } catch (Exception var5) {
            }
         }
      }

   }

   private void GetBuildingsInFrontOfCharacter(ArrayList var1, IsoGridSquare var2, boolean var3) {
      var1.clear();
      this.bOccludedByOrphanStructureFlag = false;
      if (var2 != null) {
         int var4 = var2.getX();
         int var5 = var2.getY();
         int var6 = var2.getZ();
         if (!var2.Has(IsoObjectType.stairsTN) && !var2.Has(IsoObjectType.stairsTW)) {
            if (!var2.Has(IsoObjectType.stairsMN) && !var2.Has(IsoObjectType.stairsMW)) {
               if (var2.Has(IsoObjectType.stairsBN) || var2.Has(IsoObjectType.stairsBW)) {
                  var4 -= 2;
                  var5 -= 2;
               }
            } else {
               var4 -= 2;
               var5 -= 2;
            }
         } else {
            var4 -= 3;
            var5 -= 3;
         }

         this.GetBuildingsInFrontOfCharacterSquare(var4, var5, var6, var3, var1);
         if (var6 < MaxHeight) {
            this.GetBuildingsInFrontOfCharacterSquare(var4 - 1 + 3, var5 - 1 + 3, var6 + 1, var3, var1);
            this.GetBuildingsInFrontOfCharacterSquare(var4 - 2 + 3, var5 - 2 + 3, var6 + 1, var3, var1);
            if (var3) {
               this.GetBuildingsInFrontOfCharacterSquare(var4 + 3, var5 - 1 + 3, var6 + 1, !var3, var1);
               this.GetBuildingsInFrontOfCharacterSquare(var4 - 1 + 3, var5 - 2 + 3, var6 + 1, !var3, var1);
            } else {
               this.GetBuildingsInFrontOfCharacterSquare(var4 - 1 + 3, var5 + 3, var6 + 1, !var3, var1);
               this.GetBuildingsInFrontOfCharacterSquare(var4 - 2 + 3, var5 - 1 + 3, var6 + 1, !var3, var1);
            }
         }

      }
   }

   private void GetBuildingsInFrontOfCharacterSquare(int var1, int var2, int var3, boolean var4, ArrayList var5) {
      IsoGridSquare var6 = this.getGridSquare(var1, var2, var3);
      if (var6 == null) {
         if (var3 < MaxHeight) {
            this.GetBuildingsInFrontOfCharacterSquare(var1 + 3, var2 + 3, var3 + 1, var4, var5);
         }

      } else {
         IsoGridOcclusionData var7 = var6.getOrCreateOcclusionData();
         IsoGridOcclusionData.OcclusionFilter var8 = var4 ? IsoGridOcclusionData.OcclusionFilter.Right : IsoGridOcclusionData.OcclusionFilter.Left;
         this.bOccludedByOrphanStructureFlag |= var7.getCouldBeOccludedByOrphanStructures(var8);
         ArrayList var9 = var7.getBuildingsCouldBeOccluders(var8);

         for(int var10 = 0; var10 < var9.size(); ++var10) {
            this.AddUniqueToBuildingList(var5, (IsoBuilding)var9.get(var10));
         }

      }
   }

   private ArrayList GetBuildingsInFrontOfMustSeeSquare(IsoGridSquare var1, IsoGridOcclusionData.OcclusionFilter var2) {
      IsoGridOcclusionData var3 = var1.getOrCreateOcclusionData();
      this.bOccludedByOrphanStructureFlag = var3.getCouldBeOccludedByOrphanStructures(IsoGridOcclusionData.OcclusionFilter.All);
      return var3.getBuildingsCouldBeOccluders(var2);
   }

   private IsoBuilding GetPeekedInBuilding(IsoGridSquare var1, IsoDirections var2, int var3) {
      if (var1 == null) {
         return null;
      } else {
         IsoGridSquare var4;
         IsoBuilding var5;
         if (var2 == IsoDirections.N) {
            if (!var1.visionMatrix[1][0][1]) {
               var4 = var1.nav[IsoDirections.N.index()];
               if (var4 != null) {
                  var5 = var4.getBuilding();
                  if (var5 != null) {
                     return var5;
                  }

                  if (var3 > 1) {
                     return this.GetPeekedInBuilding(var4, var2, var3 - 1);
                  }
               }
            }
         } else if (var2 == IsoDirections.W) {
            if (!var1.visionMatrix[0][1][1]) {
               var4 = var1.nav[IsoDirections.W.index()];
               if (var4 != null) {
                  var5 = var4.getBuilding();
                  if (var5 != null) {
                     return var5;
                  }

                  if (var3 > 1) {
                     return this.GetPeekedInBuilding(var4, var2, var3 - 1);
                  }
               }
            }
         } else if (var2 == IsoDirections.S) {
            if (!var1.visionMatrix[1][2][1]) {
               var4 = var1.nav[IsoDirections.S.index()];
               if (var4 != null) {
                  var5 = var4.getBuilding();
                  if (var5 != null) {
                     return var5;
                  }

                  if (var3 > 1) {
                     return this.GetPeekedInBuilding(var4, var2, var3 - 1);
                  }
               }
            }
         } else if (var2 == IsoDirections.E && !var1.visionMatrix[2][1][1]) {
            var4 = var1.nav[IsoDirections.E.index()];
            if (var4 != null) {
               var5 = var4.getBuilding();
               if (var5 != null) {
                  return var5;
               }

               if (var3 > 1) {
                  return this.GetPeekedInBuilding(var4, var2, var3 - 1);
               }
            }
         }

         return null;
      }
   }

   void GetSquaresAroundPlayerSquare(IsoPlayer var1, IsoGridSquare var2, ArrayList var3, ArrayList var4) {
      float var5 = var1.x - 4.0F;
      float var6 = var1.y - 4.0F;
      int var7 = (int)var5;
      int var8 = (int)var6;
      int var9 = var2.getZ();

      for(int var10 = var8; var10 < var8 + 10; ++var10) {
         for(int var11 = var7; var11 < var7 + 10; ++var11) {
            if ((var11 >= (int)var1.x || var10 >= (int)var1.y) && (var11 != (int)var1.x || var10 != (int)var1.y)) {
               float var12 = (float)var11 - var1.x;
               float var13 = (float)var10 - var1.y;
               if ((double)var13 < (double)var12 + 4.5D && (double)var13 > (double)var12 - 4.5D) {
                  IsoGridSquare var14 = this.getGridSquare(var11, var10, var9);
                  if (var14 != null) {
                     if (var13 >= var12) {
                        var3.add(var14);
                     }

                     if (var13 <= var12) {
                        var4.add(var14);
                     }
                  }
               }
            }
         }
      }

   }

   private boolean IsBehindStuff(IsoGridSquare var1) {
      if (!var1.getProperties().Is(IsoFlagType.exterior)) {
         return true;
      } else {
         for(int var2 = 1; var2 < 8 && var1.getZ() + var2 < MaxHeight; ++var2) {
            for(int var3 = -5; var3 <= 6; ++var3) {
               for(int var4 = -5; var4 <= 6; ++var4) {
                  if (var4 >= var3 - 5 && var4 <= var3 + 5) {
                     IsoGridSquare var6 = this.getGridSquare(var1.getX() + var4 + var2 * 3, var1.getY() + var3 + var2 * 3, var1.getZ() + var2);
                     if (var6 != null && !var6.getObjects().isEmpty()) {
                        if (var2 != 1 || var6.getObjects().size() != 1) {
                           return true;
                        }

                        IsoObject var7 = (IsoObject)var6.getObjects().get(0);
                        if (var7.sprite == null || var7.sprite.name == null || !var7.sprite.name.startsWith("lighting_outdoor")) {
                           return true;
                        }
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   public static IsoDirections FromMouseTile() {
      IsoDirections var0 = IsoDirections.N;
      float var1 = UIManager.getPickedTileLocal().x;
      float var2 = UIManager.getPickedTileLocal().y;
      float var3 = 0.5F - Math.abs(0.5F - var2);
      float var4 = 0.5F - Math.abs(0.5F - var1);
      if (var1 > 0.5F && var4 < var3) {
         var0 = IsoDirections.E;
      } else if (var2 > 0.5F && var4 > var3) {
         var0 = IsoDirections.S;
      } else if (var1 < 0.5F && var4 < var3) {
         var0 = IsoDirections.W;
      } else if (var2 < 0.5F && var4 > var3) {
         var0 = IsoDirections.N;
      }

      return var0;
   }

   public void renderListClear() {
      for(int var1 = 0; var1 < this.RenderJobsArray.size(); ++var1) {
         ((ArrayList)this.RenderJobsArray.get(var1)).clear();
      }

   }

   public void update() {
      IsoObject.alphaStep = 0.044999998F * (30.0F / (float)PerformanceSettings.LockFPS);
      IsoSprite.alphaStep = 0.075F * (30.0F / (float)PerformanceSettings.LockFPS);
      ++IsoGridSquare.gridSquareCacheEmptyTimer;
      this.ProcessSpottedRooms();
      if (!GameServer.bServer) {
         for(int var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
            if (IsoPlayer.players[var1] != null && (!IsoPlayer.players[var1].isDead() || IsoPlayer.players[var1].ReanimatedCorpse != null)) {
               IsoPlayer.instance = IsoPlayer.players[var1];
               IsoCamera.CamCharacter = IsoPlayer.players[var1];
               this.ChunkMap[var1].update();
            }
         }
      }

      this.ProcessRemoveItems((Iterator)null);
      this.ProcessItems((Iterator)null);
      this.ProcessRemoveItems((Iterator)null);
      this.ProcessIsoObject();
      this.safeToAdd = false;
      this.ProcessObjects((Iterator)null);
      this.safeToAdd = true;
      this.ProcessStaticUpdaters();
      this.ObjectDeletionAddition();
      IsoDeadBody.updateBodies();
      IsoGridSquare.setLightcache(IsoGridSquare.getLightcache() - 1);
      IsoGridSquare.setRecalcLightTime(IsoGridSquare.getRecalcLightTime() - 1);
      if (GameServer.bServer) {
         this.LamppostPositions.clear();
         this.roomLights.clear();
      }

      if (UIManager.getSpeedControls() == null || UIManager.getSpeedControls().getCurrentGameSpeed() > 0) {
         this.rainScroll += (float)this.rainSpeed / 10.0F * 0.075F * (30.0F / (float)PerformanceSettings.LockFPS);
         if (this.rainScroll > 1.0F) {
            this.rainScroll = 0.0F;
         }
      }

      if (!GameServer.bServer) {
         this.updateWeatherFx();
      }

   }

   void renderAllJobs() {
   }

   void renderListAdd(IsoObject var1) {
      Texture var2 = var1.getCurrentFrameTex();
      boolean var3 = false;
      int var4;
      if (var2 == null) {
         var4 = 4999;
      } else {
         var4 = var2.getID();
      }

      if (this.RenderJobsMapArray.containsKey(var4)) {
         var4 = (Integer)this.RenderJobsMapArray.get(var4);
      } else {
         this.RenderJobsMapArray.put(var4, this.RenderJobsArray.size());
         var4 = this.RenderJobsArray.size();
      }

      if (this.RenderJobsArray.size() <= var4) {
         this.RenderJobsArray.add(new ArrayList());
      }

      ((ArrayList)this.RenderJobsArray.get(var4)).add(var1);
   }

   IsoGridSquare getRandomFreeTile() {
      IsoGridSquare var1 = null;
      boolean var2 = true;

      do {
         var2 = true;
         var1 = this.getGridSquare(Rand.Next(this.width), Rand.Next(this.height), 0);
         if (var1 == null) {
            var2 = false;
         } else if (!var1.isFree(false)) {
            var2 = false;
         } else if (!var1.getProperties().Is(IsoFlagType.solid) && !var1.getProperties().Is(IsoFlagType.solidtrans)) {
            if (var1.getMovingObjects().size() > 0) {
               var2 = false;
            } else if (!var1.Has(IsoObjectType.stairsBN) && !var1.Has(IsoObjectType.stairsMN) && !var1.Has(IsoObjectType.stairsTN)) {
               if (var1.Has(IsoObjectType.stairsBW) || var1.Has(IsoObjectType.stairsMW) || var1.Has(IsoObjectType.stairsTW)) {
                  var2 = false;
               }
            } else {
               var2 = false;
            }
         } else {
            var2 = false;
         }
      } while(!var2);

      return var1;
   }

   IsoGridSquare getRandomOutdoorFreeTile() {
      IsoGridSquare var1 = null;
      boolean var2 = true;

      do {
         var2 = true;
         var1 = this.getGridSquare(Rand.Next(this.width), Rand.Next(this.height), 0);
         if (var1 == null) {
            var2 = false;
         } else if (!var1.isFree(false)) {
            var2 = false;
         } else if (var1.getRoom() != null) {
            var2 = false;
         } else if (!var1.getProperties().Is(IsoFlagType.solid) && !var1.getProperties().Is(IsoFlagType.solidtrans)) {
            if (var1.getMovingObjects().size() > 0) {
               var2 = false;
            } else if (!var1.Has(IsoObjectType.stairsBN) && !var1.Has(IsoObjectType.stairsMN) && !var1.Has(IsoObjectType.stairsTN)) {
               if (var1.Has(IsoObjectType.stairsBW) || var1.Has(IsoObjectType.stairsMW) || var1.Has(IsoObjectType.stairsTW)) {
                  var2 = false;
               }
            } else {
               var2 = false;
            }
         } else {
            var2 = false;
         }
      } while(!var2);

      return var1;
   }

   public IsoGridSquare getRandomFreeTileInRoom() {
      Stack var1 = new Stack();

      for(int var2 = 0; var2 < this.RoomList.size(); ++var2) {
         if (((IsoRoom)this.RoomList.get(var2)).TileList.size() > 9 && !((IsoRoom)this.RoomList.get(var2)).Exits.isEmpty() && ((IsoGridSquare)((IsoRoom)this.RoomList.get(var2)).TileList.get(0)).getProperties().Is(IsoFlagType.solidfloor)) {
            var1.add(this.RoomList.get(var2));
         }
      }

      if (var1.isEmpty()) {
         return null;
      } else {
         IsoRoom var3 = (IsoRoom)var1.get(Rand.Next(var1.size()));
         return var3.getFreeTile();
      }
   }

   public void roomSpotted(IsoRoom var1) {
      synchronized(this.SpottedRooms) {
         if (!this.SpottedRooms.contains(var1)) {
            this.SpottedRooms.push(var1);
         }

      }
   }

   public void ProcessSpottedRooms() {
      synchronized(this.SpottedRooms) {
         for(int var2 = 0; var2 < this.SpottedRooms.size(); ++var2) {
            IsoRoom var3 = (IsoRoom)this.SpottedRooms.get(var2);
            LuaEventManager.triggerEvent("OnSeeNewRoom", var3);
            VirtualZombieManager.instance.roomSpotted(var3);
            if (!GameClient.bClient && !Core.bLastStand && ("shed".equals(var3.def.name) || "garagestorage".equals(var3.def.name) || "storageunit".equals(var3.def.name))) {
               int var4 = 7;
               if ("shed".equals(var3.def.name) || "garagestorage".equals(var3.def.name)) {
                  var4 = 4;
               }

               switch(SandboxOptions.instance.GeneratorSpawning.getValue()) {
               case 1:
                  var4 += 3;
                  break;
               case 2:
                  var4 += 2;
               case 3:
               default:
                  break;
               case 4:
                  var4 -= 2;
                  break;
               case 5:
                  var4 -= 3;
               }

               if (Rand.Next(var4) == 0) {
                  IsoGridSquare var5 = var3.getRandomFreeSquare();
                  if (var5 != null) {
                     IsoGenerator var6 = new IsoGenerator(InventoryItemFactory.CreateItem("Base.Generator"), this, var5);
                     if (GameServer.bServer) {
                        var6.transmitCompleteItemToClients();
                     }
                  }
               }
            }
         }

         this.SpottedRooms.clear();
      }
   }

   public void savePlayer() throws IOException {
      if (IsoPlayer.players[0] != null && !IsoPlayer.players[0].isDead()) {
         IsoPlayer.players[0].save();
      }

      GameClient.instance.sendPlayerSave(IsoPlayer.players[0]);
   }

   public void save(DataOutputStream var1, boolean var2) throws IOException {
      while(ChunkSaveWorker.instance.bSaving) {
         try {
            Thread.sleep(30L);
         } catch (InterruptedException var9) {
            var9.printStackTrace();
         }
      }

      if (SliceY.SliceBuffer == null) {
         SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
      }

      int var3;
      for(var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
         this.ChunkMap[var3].Save();
      }

      IsoChunkMap.DoSave();
      var1.writeInt(this.width);
      var1.writeInt(this.height);
      var1.writeInt(MaxHeight);
      var1.writeInt(this.ZoneStack.size());

      for(var3 = 0; var3 < this.ZoneStack.size(); ++var3) {
         IsoCell.Zone var4 = (IsoCell.Zone)this.ZoneStack.get(var3);
         var1.writeInt(var4.X);
         var1.writeInt(var4.Y);
         var1.writeInt(var4.W);
         var1.writeInt(var4.H);
         GameWindow.WriteString(var1, var4.Name);
      }

      ArrayList var12 = new ArrayList();

      IsoLightSource var5;
      int var13;
      for(var13 = 0; var13 < this.LamppostPositions.size(); ++var13) {
         var5 = (IsoLightSource)this.LamppostPositions.get(var13);
         if (!var5.switches.isEmpty()) {
         }
      }

      var1.writeInt(var12.size());

      for(var13 = 0; var13 < var12.size(); ++var13) {
         var5 = (IsoLightSource)var12.get(var13);
         if (var5.switches.isEmpty()) {
            var1.writeInt(var5.x);
         }

         var1.writeInt(var5.y);
         var1.writeInt(var5.z);
         var1.writeInt(var5.radius);
         var1.writeFloat(var5.r);
         var1.writeFloat(var5.g);
         var1.writeFloat(var5.b);
         var1.writeBoolean(var5.bActive);
         var1.writeBoolean(var5.bWasActive);
      }

      try {
         ChunkSaveWorker.instance.SaveContainers();
      } catch (IOException var11) {
         var11.printStackTrace();
      }

      File var14 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_t.bin");
      FileOutputStream var15 = new FileOutputStream(var14);
      var1 = new DataOutputStream(new BufferedOutputStream(var15));
      GameTime.instance.save(var1);
      var1.flush();
      var1.close();
      IsoWorld.instance.MetaGrid.save();
      if (IsoPlayer.players[0] != null && !IsoPlayer.players[0].isDead()) {
         this.savePlayer();
      } else {
         File var6 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_p.bin");
         if (var6.exists()) {
            var6.delete();
         }
      }

      for(int var16 = 1; var16 < IsoPlayer.numPlayers; ++var16) {
         IsoPlayer var7 = IsoPlayer.players[var16];
         if (var7 != null && !var7.isDead()) {
            String var8 = var7.SaveFileName;
            if (var8 == null) {
               var8 = IsoPlayer.getUniqueFileName();
            }

            var7.save(var8);
         }
      }

      try {
         ChunkSaveWorker.instance.SaveContainers();
      } catch (IOException var10) {
         var10.printStackTrace();
      }

      ReanimatedPlayers.instance.saveReanimatedPlayers();
   }

   public boolean LoadPlayer(int var1) throws FileNotFoundException, IOException {
      File var2 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_p.bin");
      if (!var2.exists()) {
         return false;
      } else {
         FileInputStream var3 = new FileInputStream(var2);
         BufferedInputStream var4 = new BufferedInputStream(var3);
         synchronized(SliceY.SliceBuffer) {
            SliceY.SliceBuffer.rewind();
            var4.read(SliceY.SliceBuffer.array());
            SliceY.SliceBuffer.rewind();
            byte var6 = SliceY.SliceBuffer.get();
            byte var7 = SliceY.SliceBuffer.get();
            byte var8 = SliceY.SliceBuffer.get();
            byte var9 = SliceY.SliceBuffer.get();
            if (var6 == 80 && var7 == 76 && var8 == 89 && var9 == 82) {
               var1 = SliceY.SliceBuffer.getInt();
            } else {
               SliceY.SliceBuffer.rewind();
            }

            if (var1 >= 69) {
               String var10 = GameWindow.ReadString(SliceY.SliceBuffer);
               if (GameClient.bClient && var1 < 71) {
                  var10 = ServerOptions.instance.ServerPlayerID.getValue();
               }

               if (GameClient.bClient && !IsoPlayer.isServerPlayerIDValid(var10)) {
                  GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_ServerPlayerIDMismatch");
                  GameLoadingState.playerWrongIP = true;
                  return false;
               }
            }

            instance.ChunkMap[IsoPlayer.getPlayerIndex()].WorldX = SliceY.SliceBuffer.getInt() + IsoWorld.saveoffsetx * 30;
            instance.ChunkMap[IsoPlayer.getPlayerIndex()].WorldY = SliceY.SliceBuffer.getInt() + IsoWorld.saveoffsety * 30;
            SliceY.SliceBuffer.getInt();
            SliceY.SliceBuffer.getInt();
            SliceY.SliceBuffer.getInt();
            if (IsoPlayer.getInstance() == null) {
               IsoPlayer.instance = new IsoPlayer(instance);
               IsoPlayer.players[0] = IsoPlayer.instance;
            }

            IsoPlayer.getInstance().load(SliceY.SliceBuffer, var1);
            var3.close();
            ChunkSaveWorker.instance.LoadContainers();
            return true;
         }
      }
   }

   public void load(DataInputStream var1, boolean var2) throws FileNotFoundException, IOException, ClassNotFoundException {
      int var3 = var1.readInt();

      int var4;
      int var5;
      int var6;
      int var7;
      int var8;
      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var1.readInt();
         var6 = var1.readInt();
         var7 = var1.readInt();
         var8 = var1.readInt();
         String var9 = GameWindow.ReadString(var1);
         this.ZoneStack.add(new IsoCell.Zone(var9, var5, var6, var7, var8, 0));
      }

      var4 = var1.readInt();

      for(var5 = 0; var5 < var4; ++var5) {
         var6 = var1.readInt();
         var7 = var1.readInt();
         var8 = var1.readInt();
         int var16 = var1.readInt();
         float var10 = var1.readFloat();
         float var11 = var1.readFloat();
         float var12 = var1.readFloat();
         boolean var13 = var1.readBoolean();
         boolean var14 = var1.readBoolean();
         IsoLightSource var15 = new IsoLightSource(var6, var7, var8, var10, var11, var12, var16);
         var15.bActive = var13;
         var15.bWasActive = var14;
         this.LamppostPositions.add(var15);
      }

      if (GameServer.bServer) {
         var2 = false;
      }

   }

   public IsoGridSquare getRelativeGridSquare(int var1, int var2, int var3) {
      int var10000 = this.ChunkMap[0].getWorldXMin();
      IsoChunkMap var10001 = this.ChunkMap[0];
      int var4 = var10000 * 10;
      var10000 = this.ChunkMap[0].getWorldYMin();
      var10001 = this.ChunkMap[0];
      int var5 = var10000 * 10;
      var1 += var4;
      var2 += var5;
      return this.getGridSquare(var1, var2, var3);
   }

   public IsoGridSquare createNewGridSquare(int var1, int var2, int var3, boolean var4) {
      if (!IsoWorld.instance.isValidSquare(var1, var2, var3)) {
         return null;
      } else {
         IsoGridSquare var5 = this.getGridSquare(var1, var2, var3);
         if (var5 != null) {
            return var5;
         } else {
            if (GameServer.bServer) {
               int var6 = var1 / 10;
               int var7 = var2 / 10;
               if (ServerMap.instance.getChunk(var6, var7) != null) {
                  var5 = IsoGridSquare.getNew(this, (SliceY)null, var1, var2, var3);
                  ServerMap.instance.setGridSquare(var1, var2, var3, var5);
               }
            } else if (this.getChunkForGridSquare(var1, var2, var3) != null) {
               var5 = IsoGridSquare.getNew(this, (SliceY)null, var1, var2, var3);
               this.ConnectNewSquare(var5, true);
            }

            if (var5 != null && var4) {
               var5.RecalcAllWithNeighbours(true);
            }

            return var5;
         }
      }
   }

   public IsoGridSquare getGridSquareDirect(int var1, int var2, int var3, int var4) {
      int var5 = IsoChunkMap.ChunkWidthInTiles;
      return ENABLE_SQUARE_CACHE ? this.gridSquares[var4][var1 + var2 * var5 + var3 * var5 * var5] : this.ChunkMap[var4].getGridSquareDirect(var1, var2, var3);
   }

   public boolean isInChunkMap(int var1, int var2) {
      for(int var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
         int var4 = this.ChunkMap[var3].getWorldXMinTiles();
         int var5 = this.ChunkMap[var3].getWorldXMaxTiles();
         int var6 = this.ChunkMap[var3].getWorldYMinTiles();
         int var7 = this.ChunkMap[var3].getWorldYMaxTiles();
         if (var1 >= var4 && var1 < var5 && var2 >= var6 && var2 < var7) {
            return true;
         }
      }

      return false;
   }

   public ArrayList getProcessIsoObjectRemove() {
      return this.ProcessIsoObjectRemove;
   }

   public void setProcessIsoObjectRemove(ArrayList var1) {
      this.ProcessIsoObjectRemove = var1;
   }

   public void checkHaveRoof(int var1, int var2) {
      boolean var3 = false;

      for(int var4 = 8; var4 >= 0; --var4) {
         IsoGridSquare var5 = this.getGridSquare(var1, var2, var4);
         if (var5 != null) {
            if (var3 != var5.haveRoof) {
               var5.haveRoof = var3;
               var5.RecalcAllWithNeighbours(true);
            }

            if (var5.Is(IsoFlagType.solidfloor)) {
               var3 = true;
            }
         }
      }

   }

   public IsoZombie getFakeZombieForHit() {
      if (this.fakeZombieForHit == null) {
         this.fakeZombieForHit = new IsoZombie(this);
      }

      return this.fakeZombieForHit;
   }

   public void addHeatSource(IsoHeatSource var1) {
      if (!GameServer.bServer) {
         if (this.heatSources.contains(var1)) {
            DebugLog.log("ERROR addHeatSource called again with the same HeatSource");
         } else {
            this.heatSources.add(var1);
         }
      }
   }

   public void removeHeatSource(IsoHeatSource var1) {
      if (!GameServer.bServer) {
         this.heatSources.remove(var1);
      }
   }

   public void updateHeatSources() {
      if (!GameServer.bServer) {
         for(int var1 = this.heatSources.size() - 1; var1 >= 0; --var1) {
            IsoHeatSource var2 = (IsoHeatSource)this.heatSources.get(var1);
            if (!var2.isInBounds()) {
               this.heatSources.remove(var1);
            }
         }

      }
   }

   public int getHeatSourceTemperature(int var1, int var2, int var3) {
      int var4 = 0;

      for(int var5 = 0; var5 < this.heatSources.size(); ++var5) {
         IsoHeatSource var6 = (IsoHeatSource)this.heatSources.get(var5);
         if (var6.getZ() == var3) {
            float var7 = IsoUtils.DistanceToSquared((float)var1, (float)var2, (float)var6.getX(), (float)var6.getY());
            if (var7 < (float)(var6.getRadius() * var6.getRadius())) {
               LosUtil.TestResults var8 = LosUtil.lineClear(this, var6.getX(), var6.getY(), var6.getZ(), var1, var2, var3, false);
               if (var8 == LosUtil.TestResults.Clear || var8 == LosUtil.TestResults.ClearThroughOpenDoor) {
                  var4 = (int)((double)var4 + (double)var6.getTemperature() * (1.0D - Math.sqrt((double)var7) / (double)var6.getRadius()));
               }
            }
         }
      }

      return var4;
   }

   public float getHeatSourceHighestTemperature(float var1, int var2, int var3, int var4) {
      float var5 = var1;
      float var6 = var1;
      float var7 = 0.0F;
      IsoGridSquare var8 = null;
      float var9 = 0.0F;

      for(int var10 = 0; var10 < this.heatSources.size(); ++var10) {
         IsoHeatSource var11 = (IsoHeatSource)this.heatSources.get(var10);
         if (var11.getZ() == var4) {
            float var12 = IsoUtils.DistanceToSquared((float)var2, (float)var3, (float)var11.getX(), (float)var11.getY());
            var8 = this.getGridSquare(var11.getX(), var11.getY(), var11.getZ());
            var9 = 0.0F;
            if (var8 != null) {
               if (!var8.isInARoom()) {
                  var9 = var5 - 30.0F;
                  if (var9 < -15.0F) {
                     var9 = -15.0F;
                  } else if (var9 > 5.0F) {
                     var9 = 5.0F;
                  }
               } else {
                  var9 = var5 - 30.0F;
                  if (var9 < -7.0F) {
                     var9 = -7.0F;
                  } else if (var9 > 7.0F) {
                     var9 = 7.0F;
                  }
               }
            }

            var7 = ClimateManager.lerp((float)(1.0D - Math.sqrt((double)var12) / (double)var11.getRadius()), var5, (float)var11.getTemperature() + var9);
            if (!(var7 <= var6) && var12 < (float)(var11.getRadius() * var11.getRadius())) {
               LosUtil.TestResults var13 = LosUtil.lineClear(this, var11.getX(), var11.getY(), var11.getZ(), var2, var3, var4, false);
               if (var13 == LosUtil.TestResults.Clear || var13 == LosUtil.TestResults.ClearThroughOpenDoor) {
                  var6 = var7;
               }
            }
         }
      }

      return var6;
   }

   public void putInVehicle(IsoGameCharacter var1) {
      if (var1 != null && var1.savedVehicleSeat != -1) {
         int var2 = ((int)var1.getX() - 4) / 10;
         int var3 = ((int)var1.getY() - 4) / 10;
         int var4 = ((int)var1.getX() + 4) / 10;
         int var5 = ((int)var1.getY() + 4) / 10;

         for(int var6 = var3; var6 <= var5; ++var6) {
            for(int var7 = var2; var7 <= var4; ++var7) {
               IsoChunk var8 = this.getChunkForGridSquare(var7 * 10, var6 * 10, (int)var1.getZ());
               if (var8 != null) {
                  for(int var9 = 0; var9 < var8.vehicles.size(); ++var9) {
                     BaseVehicle var10 = (BaseVehicle)var8.vehicles.get(var9);
                     if ((int)var10.getZ() == (int)var1.getZ() && IsoUtils.DistanceToSquared(var10.getX(), var10.getY(), var1.savedVehicleX, var1.savedVehicleY) < 0.010000001F) {
                        if (var10.VehicleID == -1) {
                           return;
                        }

                        VehicleScript.Position var11 = var10.getPassengerPosition(var1.savedVehicleSeat, "inside");
                        if (var11 != null && !var10.isSeatOccupied(var1.savedVehicleSeat)) {
                           var10.enter(var1.savedVehicleSeat, var1, var11.offset);
                           LuaEventManager.triggerEvent("OnEnterVehicle", var1);
                           if (var10.getCharacter(var1.savedVehicleSeat) == var1 && var1.savedVehicleRunning) {
                              var10.resumeRunningAfterLoad();
                           }

                           var1.savedVehicleSeat = -1;
                           var1.savedVehicleRunning = false;
                        }

                        return;
                     }
                  }
               }
            }
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public void resumeVehicleSounds(IsoGameCharacter var1) {
      if (var1 != null && var1.savedVehicleSeat != -1) {
         int var2 = ((int)var1.getX() - 4) / 10;
         int var3 = ((int)var1.getY() - 4) / 10;
         int var4 = ((int)var1.getX() + 4) / 10;
         int var5 = ((int)var1.getY() + 4) / 10;

         for(int var6 = var3; var6 <= var5; ++var6) {
            for(int var7 = var2; var7 <= var4; ++var7) {
               IsoChunk var8 = this.getChunkForGridSquare(var7 * 10, var6 * 10, (int)var1.getZ());
               if (var8 != null) {
                  for(int var9 = 0; var9 < var8.vehicles.size(); ++var9) {
                     BaseVehicle var10 = (BaseVehicle)var8.vehicles.get(var9);
                     if (var10.lightbarSirenMode.isEnable()) {
                        var10.setLightbarSirenMode(var10.lightbarSirenMode.get());
                     }
                  }
               }
            }
         }

      }
   }

   private void AddUniqueToBuildingList(ArrayList var1, IsoBuilding var2) {
      for(int var3 = 0; var3 < var1.size(); ++var3) {
         if (var1.get(var3) == var2) {
            return;
         }
      }

      var1.add(var2);
   }

   public static class Zone {
      public int H;
      public boolean HasHeight = false;
      public String Name;
      public int W;
      public int X;
      public int Y;
      public int Z = 0;

      public Zone(String var1, int var2, int var3, int var4, int var5, int var6) {
         this.Name = var1;
         this.X = var2;
         this.Y = var3;
         this.W = var4;
         this.H = var5;
         this.Z = var6;
         this.HasHeight = true;
      }

      public int getH() {
         return this.H;
      }

      public void setH(int var1) {
         this.H = var1;
      }

      public boolean isHasHeight() {
         return this.HasHeight;
      }

      public void setHasHeight(boolean var1) {
         this.HasHeight = var1;
      }

      public String getName() {
         return this.Name;
      }

      public void setName(String var1) {
         this.Name = var1;
      }

      public int getW() {
         return this.W;
      }

      public void setW(int var1) {
         this.W = var1;
      }

      public int getX() {
         return this.X;
      }

      public void setX(int var1) {
         this.X = var1;
      }

      public int getY() {
         return this.Y;
      }

      public void setY(int var1) {
         this.Y = var1;
      }

      public int getZ() {
         return this.Z;
      }

      public void setZ(int var1) {
         this.Z = var1;
      }
   }

   public static enum BuildingSearchCriteria {
      Food,
      Defense,
      Wood,
      Weapons,
      General;
   }

   private class SnowGrid {
      public int w = 256;
      public int h = 256;
      public int frac = 0;
      public static final int N = 0;
      public static final int S = 1;
      public static final int W = 2;
      public static final int E = 3;
      public static final int A = 0;
      public static final int B = 1;
      public Texture[][][] grid;
      public byte[][][] gridType;

      public SnowGrid(int var2) {
         this.grid = new Texture[this.w][this.h][2];
         this.gridType = new byte[this.w][this.h][2];
         this.init(var2);
      }

      public IsoCell.SnowGrid init(int var1) {
         int var4;
         int var5;
         if (!IsoCell.this.hasSetupSnowGrid) {
            IsoCell.this.snowNoise2D = new Noise2D();
            IsoCell.this.snowNoise2D.addLayer(16, 0.5F, 3.0F);
            IsoCell.this.snowNoise2D.addLayer(32, 2.0F, 5.0F);
            IsoCell.this.snowNoise2D.addLayer(64, 5.0F, 8.0F);
            byte var2 = 0;
            byte var11 = (byte)(var2 + 1);
            IsoCell.this.snowGridTiles_Square = IsoCell.this.new SnowGridTiles(var2);
            byte var3 = 40;

            for(var4 = 0; var4 < 4; ++var4) {
               IsoCell.this.snowGridTiles_Square.add(Texture.getSharedTexture("e_newsnow_ground_1_" + (var3 + var4)));
            }

            IsoCell.this.snowGridTiles_Enclosed = IsoCell.this.new SnowGridTiles(var11++);
            var3 = 0;

            for(var4 = 0; var4 < 4; ++var4) {
               IsoCell.this.snowGridTiles_Enclosed.add(Texture.getSharedTexture("e_newsnow_ground_1_" + (var3 + var4)));
            }

            IsoCell.this.snowGridTiles_Cove = new IsoCell.SnowGridTiles[4];

            for(var4 = 0; var4 < 4; ++var4) {
               IsoCell.this.snowGridTiles_Cove[var4] = IsoCell.this.new SnowGridTiles(var11++);
               if (var4 == 0) {
                  var3 = 7;
               }

               if (var4 == 2) {
                  var3 = 4;
               }

               if (var4 == 1) {
                  var3 = 5;
               }

               if (var4 == 3) {
                  var3 = 6;
               }

               for(var5 = 0; var5 < 3; ++var5) {
                  IsoCell.this.snowGridTiles_Cove[var4].add(Texture.getSharedTexture("e_newsnow_ground_1_" + (var3 + var5 * 4)));
               }
            }

            IsoCell.this.snowGridTiles_Edge = new IsoCell.SnowGridTiles[4];

            for(var4 = 0; var4 < 4; ++var4) {
               IsoCell.this.snowGridTiles_Edge[var4] = IsoCell.this.new SnowGridTiles(var11++);
               if (var4 == 0) {
                  var3 = 16;
               }

               if (var4 == 2) {
                  var3 = 18;
               }

               if (var4 == 1) {
                  var3 = 17;
               }

               if (var4 == 3) {
                  var3 = 19;
               }

               for(var5 = 0; var5 < 3; ++var5) {
                  IsoCell.this.snowGridTiles_Edge[var4].add(Texture.getSharedTexture("e_newsnow_ground_1_" + (var3 + var5 * 4)));
               }
            }

            IsoCell.this.snowGridTiles_Strip = new IsoCell.SnowGridTiles[4];

            for(var4 = 0; var4 < 4; ++var4) {
               IsoCell.this.snowGridTiles_Strip[var4] = IsoCell.this.new SnowGridTiles(var11++);
               if (var4 == 0) {
                  var3 = 28;
               }

               if (var4 == 2) {
                  var3 = 29;
               }

               if (var4 == 1) {
                  var3 = 31;
               }

               if (var4 == 3) {
                  var3 = 30;
               }

               for(var5 = 0; var5 < 3; ++var5) {
                  IsoCell.this.snowGridTiles_Strip[var4].add(Texture.getSharedTexture("e_newsnow_ground_1_" + (var3 + var5 * 4)));
               }
            }

            IsoCell.this.hasSetupSnowGrid = true;
         }

         IsoCell.this.snowGridTiles_Square.resetCounter();
         IsoCell.this.snowGridTiles_Enclosed.resetCounter();

         for(int var12 = 0; var12 < 4; ++var12) {
            IsoCell.this.snowGridTiles_Cove[var12].resetCounter();
            IsoCell.this.snowGridTiles_Edge[var12].resetCounter();
            IsoCell.this.snowGridTiles_Strip[var12].resetCounter();
         }

         this.frac = var1;
         Noise2D var13 = IsoCell.this.snowNoise2D;

         int var16;
         for(var16 = 0; var16 < this.h; ++var16) {
            for(var4 = 0; var4 < this.w; ++var4) {
               for(var5 = 0; var5 < 2; ++var5) {
                  this.grid[var4][var16][var5] = null;
                  this.gridType[var4][var16][var5] = -1;
               }

               if (var13.layeredNoise((float)var4 / 10.0F, (float)var16 / 10.0F) <= (float)var1 / 100.0F) {
                  this.grid[var4][var16][0] = IsoCell.this.snowGridTiles_Square.getNext();
                  this.gridType[var4][var16][0] = IsoCell.this.snowGridTiles_Square.ID;
               }
            }
         }

         for(int var8 = 0; var8 < this.h; ++var8) {
            for(int var9 = 0; var9 < this.w; ++var9) {
               Texture var10 = this.grid[var9][var8][0];
               if (var10 == null) {
                  boolean var15 = this.check(var9, var8 - 1);
                  boolean var14 = this.check(var9, var8 + 1);
                  boolean var6 = this.check(var9 - 1, var8);
                  boolean var7 = this.check(var9 + 1, var8);
                  var16 = 0;
                  if (var15) {
                     ++var16;
                  }

                  if (var14) {
                     ++var16;
                  }

                  if (var7) {
                     ++var16;
                  }

                  if (var6) {
                     ++var16;
                  }

                  if (var16 != 0) {
                     if (var16 == 1) {
                        if (var15) {
                           this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Strip[0]);
                        } else if (var14) {
                           this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Strip[1]);
                        } else if (var7) {
                           this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Strip[3]);
                        } else if (var6) {
                           this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Strip[2]);
                        }
                     } else if (var16 == 2) {
                        if (var15 && var14) {
                           this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Strip[0]);
                           this.set(var9, var8, 1, IsoCell.this.snowGridTiles_Strip[1]);
                        } else if (var7 && var6) {
                           this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Strip[2]);
                           this.set(var9, var8, 1, IsoCell.this.snowGridTiles_Strip[3]);
                        } else if (var15) {
                           this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Edge[var6 ? 0 : 3]);
                        } else if (var14) {
                           this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Edge[var6 ? 2 : 1]);
                        } else if (var6) {
                           this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Edge[var15 ? 0 : 2]);
                        } else if (var7) {
                           this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Edge[var15 ? 3 : 1]);
                        }
                     } else if (var16 == 3) {
                        if (!var15) {
                           this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Cove[1]);
                        } else if (!var14) {
                           this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Cove[0]);
                        } else if (!var7) {
                           this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Cove[2]);
                        } else if (!var6) {
                           this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Cove[3]);
                        }
                     } else if (var16 == 4) {
                        this.set(var9, var8, 0, IsoCell.this.snowGridTiles_Enclosed);
                     }
                  }
               }
            }
         }

         return this;
      }

      public boolean check(int var1, int var2) {
         if (var1 == this.w) {
            var1 = 0;
         }

         if (var1 == -1) {
            var1 = this.w - 1;
         }

         if (var2 == this.h) {
            var2 = 0;
         }

         if (var2 == -1) {
            var2 = this.h - 1;
         }

         if (var1 >= 0 && var1 < this.w) {
            if (var2 >= 0 && var2 < this.h) {
               Texture var3 = this.grid[var1][var2][0];
               return IsoCell.this.snowGridTiles_Square.contains(var3);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      public void set(int var1, int var2, int var3, IsoCell.SnowGridTiles var4) {
         if (var1 == this.w) {
            var1 = 0;
         }

         if (var1 == -1) {
            var1 = this.w - 1;
         }

         if (var2 == this.h) {
            var2 = 0;
         }

         if (var2 == -1) {
            var2 = this.h - 1;
         }

         if (var1 >= 0 && var1 < this.w) {
            if (var2 >= 0 && var2 < this.h) {
               this.grid[var1][var2][var3] = var4.getNext();
               this.gridType[var1][var2][var3] = var4.ID;
            }
         }
      }

      public void subtract(IsoCell.SnowGrid var1) {
         for(int var2 = 0; var2 < this.h; ++var2) {
            for(int var3 = 0; var3 < this.w; ++var3) {
               for(int var4 = 0; var4 < 2; ++var4) {
                  if (var1.gridType[var3][var2][var4] == this.gridType[var3][var2][var4]) {
                     this.grid[var3][var2][var4] = null;
                     this.gridType[var3][var2][var4] = -1;
                  }
               }
            }
         }

      }
   }

   protected class SnowGridTiles {
      protected byte ID = -1;
      private int counter = -1;
      private ArrayList textures = new ArrayList();

      public SnowGridTiles(byte var2) {
         this.ID = var2;
      }

      protected void add(Texture var1) {
         this.textures.add(var1);
      }

      protected Texture getNext() {
         ++this.counter;
         if (this.counter >= this.textures.size()) {
            this.counter = 0;
         }

         return (Texture)this.textures.get(this.counter);
      }

      protected Texture getRand() {
         return (Texture)this.textures.get(Rand.Next(4));
      }

      protected boolean contains(Texture var1) {
         return this.textures.contains(var1);
      }

      protected void resetCounter() {
         this.counter = 0;
      }
   }

   public static final class PerPlayerRender {
      public IsoGridStack GridStacks = new IsoGridStack(9);
      public boolean[][][] VisiOccludedFlags;
      public boolean[][] VisiCulledFlags;
      public short[][][] StencilValues;

      public void setSize(int var1, int var2) {
         if (this.VisiOccludedFlags == null || this.VisiOccludedFlags.length < var1 || this.VisiOccludedFlags[0].length < var2) {
            this.VisiOccludedFlags = new boolean[var1][var2][2];
            this.VisiCulledFlags = new boolean[var1][var2];
            this.StencilValues = new short[var1][var2][2];
         }

      }
   }
}
