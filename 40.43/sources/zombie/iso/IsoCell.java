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
		IsoChunk chunk = this.getChunkForGridSquare((int)IsoCamera.CamCharacter.x, (int)IsoCamera.CamCharacter.y, (int)IsoCamera.CamCharacter.z);
		return chunk.lotheader;
	}

	public IsoChunkMap getChunkMap(int int1) {
		return this.ChunkMap[int1];
	}

	public static int getBarricadeDoorFrame() {
		return BarricadeDoorFrame;
	}

	public static void setBarricadeDoorFrame(int int1) {
		BarricadeDoorFrame = int1;
	}

	public static int getSheetCurtains() {
		return SheetCurtains;
	}

	public static void setSheetCurtains(int int1) {
		SheetCurtains = int1;
	}

	public IsoGridSquare getFreeTile(RoomDef roomDef) {
		stchoices.clear();
		for (int int1 = 0; int1 < roomDef.rects.size(); ++int1) {
			RoomDef.RoomRect roomRect = (RoomDef.RoomRect)roomDef.rects.get(int1);
			for (int int2 = roomRect.x; int2 < roomRect.x + roomRect.w; ++int2) {
				for (int int3 = roomRect.y; int3 < roomRect.y + roomRect.h; ++int3) {
					IsoGridSquare square = this.getGridSquare(int2, int3, roomDef.level);
					if (square != null) {
						square.setCachedIsFree(false);
						square.setCacheIsFree(false);
						if (square.isFree(false)) {
							stchoices.add(square);
						}
					}
				}
			}
		}

		if (stchoices.isEmpty()) {
			return null;
		} else {
			IsoGridSquare square2 = (IsoGridSquare)stchoices.get(Rand.Next(stchoices.size()));
			stchoices.clear();
			return square2;
		}
	}

	public static Stack getBuildings() {
		return buildingscores;
	}

	public static void setBuildings(Stack stack) {
		buildingscores = stack;
	}

	public IsoChunk getChunkForGridSquare(int int1, int int2, int int3) {
		int int4 = int1;
		int int5 = int2;
		for (int int6 = 0; int6 < IsoPlayer.numPlayers; ++int6) {
			if (!this.ChunkMap[int6].ignore) {
				int1 = int4 - this.ChunkMap[int6].getWorldXMinTiles();
				int2 = int5 - this.ChunkMap[int6].getWorldYMinTiles();
				if (int1 >= 0 && int2 >= 0) {
					IsoChunkMap chunkMap = this.ChunkMap[int6];
					int1 /= 10;
					chunkMap = this.ChunkMap[int6];
					int2 /= 10;
					IsoChunk chunk = null;
					chunk = this.ChunkMap[int6].getChunk(int1, int2);
					if (chunk != null) {
						return chunk;
					}
				}
			}
		}

		return null;
	}

	public IsoChunk getChunk(int int1, int int2) {
		for (int int3 = 0; int3 < IsoPlayer.numPlayers; ++int3) {
			IsoChunkMap chunkMap = this.ChunkMap[int3];
			if (!chunkMap.ignore) {
				IsoChunk chunk = chunkMap.getChunk(int1 - chunkMap.getWorldXMin(), int2 - chunkMap.getWorldYMin());
				if (chunk != null) {
					return chunk;
				}
			}
		}

		return null;
	}

	public IsoCell(int int1, int int2) {
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
		this.width = int1;
		this.height = int2;
		int int3;
		for (int3 = 0; int3 < 4; ++int3) {
			this.ChunkMap[int3] = new IsoChunkMap(this);
			this.ChunkMap[int3].PlayerID = int3;
			this.ChunkMap[int3].ignore = int3 > 0;
			this.playerOccluderBuildings.add(new ArrayList(5));
			this.zombieOccluderBuildings.add(new ArrayList(5));
			this.otherOccluderBuildings.add(new ArrayList(5));
		}

		WorldReuserThread.instance.run();
		for (int3 = 0; int3 < int2; ++int3) {
		}
	}

	public IsoCell(IsoSpriteManager spriteManager, int int1, int int2) {
		super(spriteManager);
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
		this.width = int1;
		this.height = int2;
		for (int int3 = 0; int3 < 4; ++int3) {
			this.ChunkMap[int3] = new IsoChunkMap(this);
			this.ChunkMap[int3].PlayerID = int3;
			this.ChunkMap[int3].ignore = int3 > 0;
			this.playerOccluderBuildings.add(new ArrayList(5));
			this.zombieOccluderBuildings.add(new ArrayList(5));
			this.otherOccluderBuildings.add(new ArrayList(5));
		}

		WorldReuserThread.instance.run();
	}

	public IsoCell(IsoSpriteManager spriteManager, int int1, int int2, boolean boolean1) {
		super(spriteManager);
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
		this.width = int1;
		this.height = int2;
		for (int int3 = 0; int3 < 4; ++int3) {
			this.ChunkMap[int3] = new IsoChunkMap(this);
			this.ChunkMap[int3].PlayerID = int3;
			this.ChunkMap[int3].ignore = int3 > 0;
			this.playerOccluderBuildings.add(new ArrayList(5));
			this.zombieOccluderBuildings.add(new ArrayList(5));
			this.otherOccluderBuildings.add(new ArrayList(5));
		}

		WorldReuserThread.instance.run();
	}

	public short getStencilValue(int int1, int int2, int int3) {
		short[][][] shortArrayArrayArray = perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;
		int int4 = 0;
		int int5 = 0;
		for (int int6 = 0; int6 < this.StencilXY.length; int6 += 2) {
			int int7 = -int3 * 3;
			int int8 = int1 + int7 + this.StencilXY[int6];
			int int9 = int2 + int7 + this.StencilXY[int6 + 1];
			if (int8 >= this.minX && int8 < this.maxX && int9 >= this.minY && int9 < this.maxY) {
				short[] shortArray = shortArrayArrayArray[int8 - this.minX][int9 - this.minY];
				if (shortArray[0] != 0) {
					if (int4 == 0) {
						int4 = shortArray[0];
						int5 = shortArray[1];
					} else {
						int4 = Math.min(shortArray[0], int4);
						int5 = Math.max(shortArray[1], int5);
					}
				}
			}
		}

		if (int4 == 0) {
			return 1;
		} else if (int4 > 10) {
			return (short)(int4 - 10);
		} else {
			return (short)(int5 + 1);
		}
	}

	public void setStencilValue(int int1, int int2, int int3, int int4) {
		short[][][] shortArrayArrayArray = perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;
		for (int int5 = 0; int5 < this.StencilXY.length; int5 += 2) {
			int int6 = -int3 * 3;
			int int7 = int1 + int6 + this.StencilXY[int5];
			int int8 = int2 + int6 + this.StencilXY[int5 + 1];
			if (int7 >= this.minX && int7 < this.maxX && int8 >= this.minY && int8 < this.maxY) {
				short[] shortArray = shortArrayArrayArray[int7 - this.minX][int8 - this.minY];
				if (shortArray[0] == 0) {
					shortArray[0] = (short)int4;
					shortArray[1] = (short)int4;
				} else {
					shortArray[0] = (short)Math.min(shortArray[0], int4);
					shortArray[1] = (short)Math.max(shortArray[1], int4);
				}
			}
		}
	}

	public short getStencilValue2z(int int1, int int2, int int3) {
		short[][][] shortArrayArrayArray = perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;
		int int4 = 0;
		int int5 = 0;
		int int6 = -int3 * 3;
		for (int int7 = 0; int7 < this.StencilXY2z.length; int7 += 2) {
			int int8 = int1 + int6 + this.StencilXY2z[int7];
			int int9 = int2 + int6 + this.StencilXY2z[int7 + 1];
			if (int8 >= this.minX && int8 < this.maxX && int9 >= this.minY && int9 < this.maxY) {
				short[] shortArray = shortArrayArrayArray[int8 - this.minX][int9 - this.minY];
				if (shortArray[0] != 0) {
					if (int4 == 0) {
						int4 = shortArray[0];
						int5 = shortArray[1];
					} else {
						int4 = Math.min(shortArray[0], int4);
						int5 = Math.max(shortArray[1], int5);
					}
				}
			}
		}

		if (int4 == 0) {
			return 1;
		} else if (int4 > 10) {
			return (short)(int4 - 10);
		} else {
			return (short)(int5 + 1);
		}
	}

	public void setStencilValue2z(int int1, int int2, int int3, int int4) {
		short[][][] shortArrayArrayArray = perPlayerRender[IsoCamera.frameState.playerIndex].StencilValues;
		int int5 = -int3 * 3;
		for (int int6 = 0; int6 < this.StencilXY2z.length; int6 += 2) {
			int int7 = int1 + int5 + this.StencilXY2z[int6];
			int int8 = int2 + int5 + this.StencilXY2z[int6 + 1];
			if (int7 >= this.minX && int7 < this.maxX && int8 >= this.minY && int8 < this.maxY) {
				short[] shortArray = shortArrayArrayArray[int7 - this.minX][int8 - this.minY];
				if (shortArray[0] == 0) {
					shortArray[0] = (short)int4;
					shortArray[1] = (short)int4;
				} else {
					shortArray[0] = (short)Math.min(shortArray[0], int4);
					shortArray[1] = (short)Math.max(shortArray[1], int4);
				}
			}
		}
	}

	public void CalculateVertColoursForTile(IsoGridSquare square, int int1, int int2, int int3, int int4) {
		IsoGridSquare square2 = !square.visionMatrix[0][0][1] ? square.nav[IsoDirections.NW.index()] : null;
		IsoGridSquare square3 = !square.visionMatrix[1][0][1] ? square.nav[IsoDirections.N.index()] : null;
		IsoGridSquare square4 = !square.visionMatrix[2][0][1] ? square.nav[IsoDirections.NE.index()] : null;
		IsoGridSquare square5 = !square.visionMatrix[2][1][1] ? square.nav[IsoDirections.E.index()] : null;
		IsoGridSquare square6 = !square.visionMatrix[2][2][1] ? square.nav[IsoDirections.SE.index()] : null;
		IsoGridSquare square7 = !square.visionMatrix[1][2][1] ? square.nav[IsoDirections.S.index()] : null;
		IsoGridSquare square8 = !square.visionMatrix[0][2][1] ? square.nav[IsoDirections.SW.index()] : null;
		IsoGridSquare square9 = !square.visionMatrix[0][1][1] ? square.nav[IsoDirections.W.index()] : null;
		this.CalculateColor(square2, square3, square9, square, 0, int4);
		this.CalculateColor(square3, square4, square5, square, 1, int4);
		this.CalculateColor(square6, square7, square5, square, 2, int4);
		this.CalculateColor(square8, square7, square9, square, 3, int4);
	}

	public void ClearVertArrays() {
		this.everDone = true;
		int int1 = MaxHeight;
	}

	public void DrawStencilMask() {
		Texture texture = Texture.getSharedTexture("media/mask_circledithernew.png");
		if (texture != null) {
			IndieGL.glStencilMask(255);
			IndieGL.glClear(1280);
			int int1 = IsoCamera.getOffscreenWidth(IsoPlayer.getPlayerIndex()) / 2;
			int int2 = IsoCamera.getOffscreenHeight(IsoPlayer.getPlayerIndex()) / 2;
			int1 -= texture.getWidth() / (2 / Core.TileScale);
			int2 -= texture.getHeight() / (2 / Core.TileScale);
			IndieGL.enableStencilTest();
			IndieGL.enableAlphaTest();
			IndieGL.glAlphaFunc(516, 0.1F);
			IndieGL.glStencilFunc(519, 128, 255);
			IndieGL.glStencilOp(7680, 7680, 7681);
			texture.renderstrip(int1 - (int)IsoCamera.RightClickX[IsoPlayer.getPlayerIndex()], int2 - (int)IsoCamera.RightClickY[IsoPlayer.getPlayerIndex()], texture.getWidth() * Core.TileScale, texture.getHeight() * Core.TileScale, 1.0F, 1.0F, 1.0F, 1.0F);
			IndieGL.glStencilFunc(519, 0, 255);
			IndieGL.glStencilOp(7680, 7680, 7680);
			IndieGL.glStencilMask(127);
			IndieGL.glAlphaFunc(519, 0.0F);
			this.StencilX1 = int1 - (int)IsoCamera.RightClickX[IsoPlayer.getPlayerIndex()];
			this.StencilY1 = int2 - (int)IsoCamera.RightClickY[IsoPlayer.getPlayerIndex()];
			this.StencilX2 = this.StencilX1 + texture.getWidth() * Core.TileScale;
			this.StencilY2 = this.StencilY1 + texture.getHeight() * Core.TileScale;
		}
	}

	public void RenderTiles(int int1) {
		int int2 = IsoCamera.frameState.playerIndex;
		IsoPlayer player = IsoPlayer.players[int2];
		player.dirtyRecalcGridStackTime -= GameTime.getInstance().getMultiplier() / 4.0F;
		if (perPlayerRender[int2] == null) {
			perPlayerRender[int2] = new IsoCell.PerPlayerRender();
		}

		IsoCell.PerPlayerRender perPlayerRender = perPlayerRender[int2];
		perPlayerRender.setSize(this.maxX - this.minX + 1, this.maxY - this.minY + 1);
		IsoGridStack gridStack = perPlayerRender.GridStacks;
		boolean[][][] booleanArrayArrayArray = perPlayerRender.VisiOccludedFlags;
		boolean[][] booleanArrayArray = perPlayerRender.VisiCulledFlags;
		short[][][] shortArrayArrayArray = perPlayerRender.StencilValues;
		int int3;
		int int4;
		int int5;
		int int6;
		if (player.dirtyRecalcGridStack) {
			player.dirtyRecalcGridStack = false;
			IsoChunk chunk = -1;
			int3 = -1;
			int4 = -1;
			WeatherFxMask.DIAMOND_ITER_DONE = true;
			for (int5 = int1; int5 >= 0; --int5) {
				GridStack = (ArrayList)gridStack.Squares.get(int5);
				GridStack.clear();
				if (int5 < this.maxZ) {
					if (newRender) {
						DiamondMatrixIterator diamondMatrixIterator = this.diamondMatrixIterator.reset(this.maxX - this.minX);
						IsoGridSquare square = null;
						Vector2i vector2i = this.diamondMatrixPos;
						while (diamondMatrixIterator.next(vector2i)) {
							if (vector2i.y < this.maxY - this.minY + 1) {
								square = this.ChunkMap[int2].getGridSquare(vector2i.x + this.minX, vector2i.y + this.minY, int5);
								if (int5 == 0) {
									booleanArrayArrayArray[vector2i.x][vector2i.y][0] = false;
									booleanArrayArrayArray[vector2i.x][vector2i.y][1] = false;
									booleanArrayArray[vector2i.x][vector2i.y] = false;
								}

								if (square == null) {
									WeatherFxMask.addMaskLocation((IsoGridSquare)null, vector2i.x + this.minX, vector2i.y + this.minY, int5);
								} else {
									IsoChunk chunk2 = square.getChunk();
									if (chunk2 != null && square.IsOnScreen()) {
										WeatherFxMask.addMaskLocation(square, vector2i.x + this.minX, vector2i.y + this.minY, int5);
										if (!this.IsDissolvedSquare(square)) {
											square.cacheLightInfo();
											((ArrayList)gridStack.Squares.get(int5)).add(square);
										}
									}
								}
							}
						}
					} else {
						label302: for (int6 = this.minY; int6 < this.maxY; ++int6) {
							int int7 = this.minX;
							IsoGridSquare square2 = this.ChunkMap[int2].getGridSquare(int7, int6, int5);
							int int8 = IsoDirections.E.index();
							while (true) {
								while (true) {
									if (int7 >= this.maxX) {
										continue label302;
									}

									if (int5 == 0) {
										booleanArrayArrayArray[int7 - this.minX][int6 - this.minY][0] = false;
										booleanArrayArrayArray[int7 - this.minX][int6 - this.minY][1] = false;
										booleanArrayArray[int7 - this.minX][int6 - this.minY] = false;
									}

									if (square2 != null && square2.getY() != int6) {
										square2 = null;
									}

									boolean boolean1 = true;
									boolean boolean2 = true;
									IsoChunkMap chunkMap = this.ChunkMap[int2];
									int int9 = this.ChunkMap[int2].WorldX - IsoChunkMap.ChunkGridWidth / 2;
									chunkMap = this.ChunkMap[int2];
									int int10 = int7 - int9 * 10;
									chunkMap = this.ChunkMap[int2];
									int9 = this.ChunkMap[int2].WorldY - IsoChunkMap.ChunkGridWidth / 2;
									chunkMap = this.ChunkMap[int2];
									int int11 = int6 - int9 * 10;
									IsoChunkMap chunkMap2 = this.ChunkMap[int2];
									IsoChunk chunk3 = int10 / 10;
									chunkMap2 = this.ChunkMap[int2];
									int11 /= 10;
									if (chunk3 != chunk || int11 != int3) {
										chunk3 = this.ChunkMap[int2].getChunkForGridSquare(int7, int6);
										if (chunk3 != null) {
											int4 = chunk3.maxLevel;
										}
									}

									chunk = chunk3;
									int3 = int11;
									if (int4 < int5) {
										++int7;
									} else {
										if (square2 == null) {
											square2 = this.getGridSquare(int7, int6, int5);
											if (square2 == null) {
												square2 = this.ChunkMap[int2].getGridSquare(int7, int6, int5);
												if (square2 == null) {
													++int7;
													continue;
												}
											}
										}

										chunk3 = square2.getChunk();
										if (chunk3 != null && !chunk3.bLightingNeverDone[int2] && square2.IsOnScreen() && !this.IsDissolvedSquare(square2)) {
											square2.cacheLightInfo();
											GridStack.add(square2);
										}

										square2 = square2.nav[int8];
										++int7;
									}
								}
							}
						}
					}
				}
			}

			this.CullFullyOccludedSquares(gridStack, booleanArrayArrayArray, booleanArrayArray);
		}

		++this.DeferredCharacterTick;
		int int12;
		for (int12 = this.minY; int12 <= this.maxY; ++int12) {
			for (int3 = this.minX; int3 <= this.maxX; ++int3) {
				shortArrayArrayArray[int3 - this.minX][int12 - this.minY][0] = 0;
				shortArrayArrayArray[int3 - this.minX][int12 - this.minY][1] = 0;
			}
		}

		IsoGridSquare square3;
		for (int12 = 0; int12 < int1 + 1; ++int12) {
			GridStack = (ArrayList)gridStack.Squares.get(int12);
			SolidFloor.clear();
			ShadedFloor.clear();
			VegetationCorpses.clear();
			MinusFloorCharacters.clear();
			for (int3 = 0; int3 < GridStack.size(); ++int3) {
				IsoGridSquare square4 = (IsoGridSquare)GridStack.get(int3);
				int5 = square4.renderFloor(square4.getX() - this.minX, square4.getY());
				if (!square4.getStaticMovingObjects().isEmpty()) {
					int5 |= 2;
				}

				if (!square4.getWorldObjects().isEmpty()) {
					int5 |= 2;
				}

				for (int6 = 0; int6 < square4.getMovingObjects().size(); ++int6) {
					IsoMovingObject movingObject = (IsoMovingObject)square4.getMovingObjects().get(int6);
					boolean boolean3 = movingObject.bOnFloor;
					if (boolean3 && movingObject instanceof IsoZombie) {
						IsoZombie zombie = (IsoZombie)movingObject;
						boolean3 = zombie.bCrawling || zombie.legsSprite.CurrentAnim != null && zombie.legsSprite.CurrentAnim.name.equals("ZombieDeath") && zombie.def.isFinished();
					}

					if (boolean3) {
						int5 |= 2;
					} else {
						int5 |= 4;
					}
				}

				if (!square4.getDeferedCharacters().isEmpty()) {
					int5 |= 4;
				}

				if ((int5 & 1) != 0) {
					SolidFloor.add(square4);
				}

				if ((int5 & 8) != 0) {
					ShadedFloor.add(square4);
				}

				if ((int5 & 2) != 0) {
					VegetationCorpses.add(square4);
				}

				if ((int5 & 4) != 0) {
					MinusFloorCharacters.add(square4);
				}
			}

			if (!SolidFloor.isEmpty()) {
				this.RenderSnow(int12);
			}

			if (!GridStack.isEmpty()) {
				this.ChunkMap[int2].renderBloodForChunks(int12);
			}

			if (!ShadedFloor.isEmpty()) {
				this.RenderFloorShading(int12);
			}

			LuaEventManager.triggerEvent("OnPostFloorLayerDraw", int12);
			for (int4 = 0; int4 < VegetationCorpses.size(); ++int4) {
				square3 = (IsoGridSquare)VegetationCorpses.get(int4);
				square3.renderMinusFloor(this.minY, square3.getX() - this.minX, this.maxZ, this.currentLY, false, true);
				square3.renderCharacters(this.maxZ, true);
			}

			for (int4 = 0; int4 < MinusFloorCharacters.size(); ++int4) {
				square3 = (IsoGridSquare)MinusFloorCharacters.get(int4);
				this.currentLY = square3.getY() - this.minY;
				this.currentLZ = int12;
				boolean boolean4 = square3.renderMinusFloor(this.minY, square3.getX() - this.minX, this.maxZ, this.currentLY, false, false);
				square3.renderDeferredCharacters(this.maxZ);
				square3.renderCharacters(this.maxZ, false);
				if (boolean4) {
					square3.renderMinusFloor(this.minY, square3.getX() - this.minX, this.maxZ, this.currentLY, true, false);
				}
			}
		}

		MinusFloorCharacters.clear();
		ShadedFloor.clear();
		SolidFloor.clear();
		VegetationCorpses.clear();
		if (Core.bDebug && DebugOptions.instance.PhysicsRender.getValue()) {
			TextureDraw.GenericDrawer genericDrawer = WorldSimulation.getDrawer(int2);
			SpriteRenderer.instance.drawGeneric(genericDrawer);
		}

		if (Core.bDebug && DebugOptions.instance.LightingRender.getValue()) {
			byte byte1 = 1;
			for (int3 = 0; int3 < int1 + 1; ++int3) {
				GridStack = (ArrayList)gridStack.Squares.get(int3);
				for (int4 = 0; int4 < GridStack.size(); ++int4) {
					square3 = (IsoGridSquare)GridStack.get(int4);
					float float1 = IsoUtils.XToScreenExact((float)square3.x + 0.3F, (float)square3.y, 0.0F, 0);
					float float2 = IsoUtils.YToScreenExact((float)square3.x + 0.3F, (float)square3.y, 0.0F, 0);
					float float3 = IsoUtils.XToScreenExact((float)square3.x + 0.6F, (float)square3.y, 0.0F, 0);
					float float4 = IsoUtils.YToScreenExact((float)square3.x + 0.6F, (float)square3.y, 0.0F, 0);
					float float5 = IsoUtils.XToScreenExact((float)(square3.x + 1), (float)square3.y + 0.3F, 0.0F, 0);
					float float6 = IsoUtils.YToScreenExact((float)(square3.x + 1), (float)square3.y + 0.3F, 0.0F, 0);
					float float7 = IsoUtils.XToScreenExact((float)(square3.x + 1), (float)square3.y + 0.6F, 0.0F, 0);
					float float8 = IsoUtils.YToScreenExact((float)(square3.x + 1), (float)square3.y + 0.6F, 0.0F, 0);
					float float9 = IsoUtils.XToScreenExact((float)square3.x + 0.6F, (float)(square3.y + 1), 0.0F, 0);
					float float10 = IsoUtils.YToScreenExact((float)square3.x + 0.6F, (float)(square3.y + 1), 0.0F, 0);
					float float11 = IsoUtils.XToScreenExact((float)square3.x + 0.3F, (float)(square3.y + 1), 0.0F, 0);
					float float12 = IsoUtils.YToScreenExact((float)square3.x + 0.3F, (float)(square3.y + 1), 0.0F, 0);
					float float13 = IsoUtils.XToScreenExact((float)square3.x, (float)square3.y + 0.6F, 0.0F, 0);
					float float14 = IsoUtils.YToScreenExact((float)square3.x, (float)square3.y + 0.6F, 0.0F, 0);
					float float15 = IsoUtils.XToScreenExact((float)square3.x, (float)square3.y + 0.3F, 0.0F, 0);
					float float16 = IsoUtils.YToScreenExact((float)square3.x, (float)square3.y + 0.3F, 0.0F, 0);
					if (square3.visionMatrix[0][0][byte1]) {
						LineDrawer.drawLine(float1, float2, float3, float4, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}

					if (square3.visionMatrix[0][1][byte1]) {
						LineDrawer.drawLine(float3, float4, float5, float6, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}

					if (square3.visionMatrix[0][2][byte1]) {
						LineDrawer.drawLine(float5, float6, float7, float8, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}

					if (square3.visionMatrix[1][2][byte1]) {
						LineDrawer.drawLine(float7, float8, float9, float10, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}

					if (square3.visionMatrix[2][2][byte1]) {
						LineDrawer.drawLine(float9, float10, float11, float12, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}

					if (square3.visionMatrix[2][1][byte1]) {
						LineDrawer.drawLine(float11, float12, float13, float14, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}

					if (square3.visionMatrix[2][0][byte1]) {
						LineDrawer.drawLine(float13, float14, float15, float16, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}

					if (square3.visionMatrix[1][0][byte1]) {
						LineDrawer.drawLine(float15, float16, float1, float2, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}
				}
			}
		}
	}

	private void CullFullyOccludedSquares(IsoGridStack gridStack, boolean[][][] booleanArrayArrayArray, boolean[][] booleanArrayArray) {
		int int1 = 0;
		int int2;
		for (int2 = 1; int2 < MaxHeight + 1; ++int2) {
			int1 += ((ArrayList)gridStack.Squares.get(int2)).size();
		}

		if (int1 >= 500) {
			int2 = 0;
			for (int int3 = MaxHeight; int3 >= 0; --int3) {
				GridStack = (ArrayList)gridStack.Squares.get(int3);
				for (int int4 = GridStack.size() - 1; int4 >= 0; --int4) {
					IsoGridSquare square = (IsoGridSquare)GridStack.get(int4);
					int int5 = square.getX() - int3 * 3 - this.minX;
					int int6 = square.getY() - int3 * 3 - this.minY;
					boolean boolean1;
					if (int3 < MaxHeight) {
						boolean1 = !booleanArrayArray[int5][int6];
						if (boolean1) {
							boolean1 = false;
							if (int5 > 2) {
								if (int6 > 2) {
									boolean1 = !booleanArrayArrayArray[int5 - 3][int6 - 3][0] || !booleanArrayArrayArray[int5 - 3][int6 - 3][1] || !booleanArrayArrayArray[int5 - 3][int6 - 2][0] || !booleanArrayArrayArray[int5 - 2][int6 - 3][1] || !booleanArrayArrayArray[int5 - 2][int6 - 2][0] || !booleanArrayArrayArray[int5 - 2][int6 - 2][1] || !booleanArrayArrayArray[int5 - 2][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6 - 2][0] || !booleanArrayArrayArray[int5 - 1][int6 - 1][1] || !booleanArrayArrayArray[int5 - 1][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6][0] || !booleanArrayArrayArray[int5][int6 - 1][1] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
								} else if (int6 > 1) {
									boolean1 = !booleanArrayArrayArray[int5 - 3][int6 - 2][0] || !booleanArrayArrayArray[int5 - 2][int6 - 2][0] || !booleanArrayArrayArray[int5 - 2][int6 - 2][1] || !booleanArrayArrayArray[int5 - 2][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6 - 2][0] || !booleanArrayArrayArray[int5 - 1][int6 - 1][1] || !booleanArrayArrayArray[int5 - 1][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6][0] || !booleanArrayArrayArray[int5][int6 - 1][1] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
								} else if (int6 > 0) {
									boolean1 = !booleanArrayArrayArray[int5 - 2][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6 - 1][1] || !booleanArrayArrayArray[int5 - 1][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6][0] || !booleanArrayArrayArray[int5][int6 - 1][1] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
								} else {
									boolean1 = !booleanArrayArrayArray[int5 - 1][int6][0] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
								}
							} else if (int5 > 1) {
								if (int6 > 2) {
									boolean1 = !booleanArrayArrayArray[int5 - 2][int6 - 3][1] || !booleanArrayArrayArray[int5 - 2][int6 - 2][0] || !booleanArrayArrayArray[int5 - 2][int6 - 2][1] || !booleanArrayArrayArray[int5 - 2][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6 - 2][0] || !booleanArrayArrayArray[int5 - 1][int6 - 1][1] || !booleanArrayArrayArray[int5 - 1][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6][0] || !booleanArrayArrayArray[int5][int6 - 1][1] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
								} else if (int6 > 1) {
									boolean1 = !booleanArrayArrayArray[int5 - 2][int6 - 2][0] || !booleanArrayArrayArray[int5 - 2][int6 - 2][1] || !booleanArrayArrayArray[int5 - 2][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6 - 2][0] || !booleanArrayArrayArray[int5 - 1][int6 - 1][1] || !booleanArrayArrayArray[int5 - 1][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6][0] || !booleanArrayArrayArray[int5][int6 - 1][1] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
								} else if (int6 > 0) {
									boolean1 = !booleanArrayArrayArray[int5 - 2][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6 - 1][1] || !booleanArrayArrayArray[int5 - 1][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6][0] || !booleanArrayArrayArray[int5][int6 - 1][1] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
								} else {
									boolean1 = !booleanArrayArrayArray[int5 - 1][int6][0] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
								}
							} else if (int5 > 0) {
								if (int6 > 2) {
									boolean1 = !booleanArrayArrayArray[int5 - 1][int6 - 2][0] || !booleanArrayArrayArray[int5 - 1][int6 - 1][1] || !booleanArrayArrayArray[int5 - 1][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6][0] || !booleanArrayArrayArray[int5][int6 - 1][1] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
								} else if (int6 > 1) {
									boolean1 = !booleanArrayArrayArray[int5 - 1][int6 - 2][0] || !booleanArrayArrayArray[int5 - 1][int6 - 1][1] || !booleanArrayArrayArray[int5 - 1][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6][0] || !booleanArrayArrayArray[int5][int6 - 1][1] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
								} else if (int6 > 0) {
									boolean1 = !booleanArrayArrayArray[int5 - 1][int6 - 1][1] || !booleanArrayArrayArray[int5 - 1][int6 - 1][0] || !booleanArrayArrayArray[int5 - 1][int6][0] || !booleanArrayArrayArray[int5][int6 - 1][1] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
								} else {
									boolean1 = !booleanArrayArrayArray[int5 - 1][int6][0] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
								}
							} else if (int6 > 2) {
								boolean1 = !booleanArrayArrayArray[int5][int6 - 1][1] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
							} else if (int6 > 1) {
								boolean1 = !booleanArrayArrayArray[int5][int6 - 1][1] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
							} else if (int6 > 0) {
								boolean1 = !booleanArrayArrayArray[int5][int6 - 1][1] || !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
							} else {
								boolean1 = !booleanArrayArrayArray[int5][int6][0] || !booleanArrayArrayArray[int5][int6][1];
							}
						}

						if (!boolean1) {
							GridStack.remove(int4);
							booleanArrayArray[int5][int6] = true;
							continue;
						}
					}

					++int2;
					boolean1 = square.visionMatrix[0][1][1] && square.getProperties().Is(IsoFlagType.cutW);
					boolean boolean2 = square.visionMatrix[1][0][1] && square.getProperties().Is(IsoFlagType.cutN);
					boolean boolean3 = false;
					int int7;
					if (boolean1 || boolean2) {
						boolean3 = ((float)square.x > IsoCamera.frameState.CamCharacterX || (float)square.y > IsoCamera.frameState.CamCharacterY) && square.z >= (int)IsoCamera.frameState.CamCharacterZ;
						if (boolean3) {
							int7 = (int)(square.CachedScreenX - IsoCamera.frameState.OffX);
							int int8 = (int)(square.CachedScreenY - IsoCamera.frameState.OffY);
							if (int7 + 32 * Core.TileScale <= this.StencilX1 || int7 - 32 * Core.TileScale >= this.StencilX2 || int8 + 32 * Core.TileScale <= this.StencilY1 || int8 - 96 * Core.TileScale >= this.StencilY2) {
								boolean3 = false;
							}
						}
					}

					int7 = 0;
					if (boolean1 && !boolean3) {
						++int7;
						if (int5 > 0) {
							booleanArrayArrayArray[int5 - 1][int6][0] = true;
							if (int6 > 0) {
								booleanArrayArrayArray[int5 - 1][int6 - 1][1] = true;
							}
						}

						if (int5 > 1 && int6 > 0) {
							booleanArrayArrayArray[int5 - 2][int6 - 1][0] = true;
							if (int6 > 1) {
								booleanArrayArrayArray[int5 - 2][int6 - 2][1] = true;
							}
						}

						if (int5 > 2 && int6 > 1) {
							booleanArrayArrayArray[int5 - 3][int6 - 2][0] = true;
							if (int6 > 2) {
								booleanArrayArrayArray[int5 - 3][int6 - 3][1] = true;
							}
						}
					}

					if (boolean2 && !boolean3) {
						++int7;
						if (int6 > 0) {
							booleanArrayArrayArray[int5][int6 - 1][1] = true;
							if (int5 > 0) {
								booleanArrayArrayArray[int5 - 1][int6 - 1][0] = true;
							}
						}

						if (int6 > 1 && int5 > 0) {
							booleanArrayArrayArray[int5 - 1][int6 - 2][1] = true;
							if (int5 > 1) {
								booleanArrayArrayArray[int5 - 2][int6 - 2][0] = true;
							}
						}

						if (int6 > 2 && int5 > 1) {
							booleanArrayArrayArray[int5 - 2][int6 - 3][1] = true;
							if (int5 > 2) {
								booleanArrayArrayArray[int5 - 3][int6 - 3][0] = true;
							}
						}
					}

					if (square.visionMatrix[1][1][0]) {
						++int7;
						booleanArrayArrayArray[int5][int6][0] = true;
						booleanArrayArrayArray[int5][int6][1] = true;
					}

					if (int7 == 3) {
						booleanArrayArray[int5][int6] = true;
					}
				}
			}
		}
	}

	public void RenderFloorShading(int int1) {
		if (int1 < this.maxZ && PerformanceSettings.LightingFrameSkip < 3) {
			if (texWhite == null) {
				texWhite = Texture.getSharedTexture("media/ui/white.png");
			}

			Texture texture = texWhite;
			if (texture != null) {
				int int2 = IsoCamera.frameState.playerIndex;
				int int3 = (int)IsoCamera.frameState.OffX;
				int int4 = (int)IsoCamera.frameState.OffY;
				for (int int5 = 0; int5 < ShadedFloor.size(); ++int5) {
					IsoGridSquare square = (IsoGridSquare)ShadedFloor.get(int5);
					if (square.getProperties().solidfloor) {
						float float1 = 0.0F;
						float float2 = 0.0F;
						float float3 = 0.0F;
						if (square.getProperties().Is(IsoFlagType.FloorHeightOneThird)) {
							float2 = -1.0F;
							float1 = -1.0F;
						} else if (square.getProperties().Is(IsoFlagType.FloorHeightTwoThirds)) {
							float2 = -2.0F;
							float1 = -2.0F;
						}

						float float4 = IsoUtils.XToScreen((float)square.getX() + float1, (float)square.getY() + float2, (float)int1 + float3, 0);
						float float5 = IsoUtils.YToScreen((float)square.getX() + float1, (float)square.getY() + float2, (float)int1 + float3, 0);
						float4 = (float)((int)float4);
						float5 = (float)((int)float5);
						float4 -= (float)int3;
						float5 -= (float)int4;
						colu = square.getVertLight(0, int2);
						colr = square.getVertLight(1, int2);
						cold = square.getVertLight(2, int2);
						coll = square.getVertLight(3, int2);
						texture.renderdiamond((int)float4 - 32 * Core.TileScale, (int)float5 + 16 * Core.TileScale, 64 * Core.TileScale, 32 * Core.TileScale, colu, cold, coll, colr);
					}
				}
			}
		}
	}

	public boolean IsPlayerWindowPeeking(int int1) {
		return this.playerWindowPeeking[int1];
	}

	public boolean CanBuildingSquareOccludePlayer(IsoGridSquare square, int int1) {
		ArrayList arrayList = (ArrayList)this.playerOccluderBuildings.get(int1);
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			IsoBuilding building = (IsoBuilding)arrayList.get(int2);
			int int3 = building.getDef().getX();
			int int4 = building.getDef().getY();
			int int5 = building.getDef().getX2() - int3;
			int int6 = building.getDef().getY2() - int4;
			this.buildingRectTemp.setBounds(int3, int4, int5 + 1, int6 + 1);
			if (this.buildingRectTemp.contains(square.getX(), square.getY())) {
				return true;
			}
		}

		return false;
	}

	private boolean IsDissolvedSquare(IsoGridSquare square) {
		int int1 = IsoCamera.frameState.playerIndex;
		IsoPlayer player = IsoPlayer.players[int1];
		if (player.current == null) {
			return false;
		} else {
			IsoGridSquare square2 = player.current;
			if (square2.getZ() >= square.getZ()) {
				return false;
			} else if (!PerformanceSettings.NewRoofHiding) {
				return this.bHideFloors[int1] && square.getZ() >= this.maxZ;
			} else {
				if (square.getZ() > this.hidesOrphanStructuresAbove) {
					IsoBuilding building = square.getBuilding();
					if (building == null) {
						building = square.roofHideBuilding;
					}

					IsoGridSquare square3;
					for (int int2 = square.getZ() - 1; int2 >= 0 && building == null; --int2) {
						square3 = this.getGridSquare(square.x, square.y, int2);
						if (square3 != null) {
							building = square3.getBuilding();
							if (building == null) {
								building = square3.roofHideBuilding;
							}
						}
					}

					if (building == null) {
						if (square.isSolidFloor()) {
							return true;
						}

						IsoGridSquare square4 = square.nav[IsoDirections.N.index()];
						if (square4 != null && square4.getBuilding() == null) {
							if (square4.getPlayerBuiltFloor() != null) {
								return true;
							}

							square3 = this.getGridSquare(square4.x, square4.y, square4.z - 1);
							if (square3 != null && square3.HasStairs()) {
								return true;
							}
						}

						square3 = square.nav[IsoDirections.W.index()];
						IsoGridSquare square5;
						if (square3 != null && square3.getBuilding() == null) {
							if (square3.getPlayerBuiltFloor() != null) {
								return true;
							}

							square5 = this.getGridSquare(square3.x, square3.y, square3.z - 1);
							if (square5 != null && square5.HasStairs()) {
								return true;
							}
						}

						if (square.Is(IsoFlagType.WallSE)) {
							square5 = square.nav[IsoDirections.NW.index()];
							if (square5 != null && square5.getBuilding() == null) {
								if (square5.getPlayerBuiltFloor() != null) {
									return true;
								}

								IsoGridSquare square6 = this.getGridSquare(square5.x, square5.y, square5.z - 1);
								if (square6 != null && square6.HasStairs()) {
									return true;
								}
							}
						}
					}
				}

				int int3;
				int int4;
				int int5;
				for (int5 = 0; int5 < 4; ++int5) {
					short short1 = 500;
					for (int int6 = 0; int6 < short1 && this.playerOccluderBuildingsArr[int1] != null; ++int6) {
						IsoBuilding building2 = this.playerOccluderBuildingsArr[int1][int6];
						if (building2 == null) {
							break;
						}

						BuildingDef buildingDef = building2.getDef();
						int3 = buildingDef.getX();
						int4 = buildingDef.getY();
						int int7 = buildingDef.getX2() - int3;
						int int8 = buildingDef.getY2() - int4;
						this.buildingRectTemp.setBounds(int3 - 1, int4 - 1, int7 + 2, int8 + 2);
						if (this.buildingRectTemp.contains(square.getX(), square.getY())) {
							return true;
						}
					}
				}

				IsoBuilding building3;
				BuildingDef buildingDef2;
				int int9;
				int int10;
				for (int5 = 0; int5 < 500; ++int5) {
					building3 = this.zombieOccluderBuildingsArr[int1][int5];
					if (building3 == null) {
						break;
					}

					buildingDef2 = building3.getDef();
					int9 = buildingDef2.getX();
					int10 = buildingDef2.getY();
					int3 = buildingDef2.getX2() - int9;
					int4 = buildingDef2.getY2() - int10;
					this.buildingRectTemp.setBounds(int9 - 1, int10 - 1, int3 + 2, int4 + 2);
					if (this.buildingRectTemp.contains(square.getX(), square.getY())) {
						return true;
					}
				}

				for (int5 = 0; int5 < 500; ++int5) {
					building3 = this.otherOccluderBuildingsArr[int1][int5];
					if (building3 == null) {
						break;
					}

					buildingDef2 = building3.getDef();
					int9 = buildingDef2.getX();
					int10 = buildingDef2.getY();
					int3 = buildingDef2.getX2() - int9;
					int4 = buildingDef2.getY2() - int10;
					this.buildingRectTemp.setBounds(int9 - 1, int10 - 1, int3 + 2, int4 + 2);
					if (this.buildingRectTemp.contains(square.getX(), square.getY())) {
						return true;
					}
				}

				return false;
			}
		}
	}

	private int GetBuildingHeightAt(IsoBuilding building, int int1, int int2, int int3) {
		for (int int4 = MaxHeight; int4 > int3; --int4) {
			IsoGridSquare square = this.getGridSquare(int1, int2, int4);
			if (square != null && square.getBuilding() == building) {
				return int4;
			}
		}

		return int3;
	}

	private void updateSnow(int int1) {
		if (this.snowGridCur == null) {
			this.snowGridCur = new IsoCell.SnowGrid(int1);
			this.snowGridPrev = new IsoCell.SnowGrid(0);
		} else {
			if (int1 != this.snowGridCur.frac) {
				this.snowGridPrev.init(this.snowGridCur.frac);
				this.snowGridCur.init(int1);
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

	public void setSnowParams(int int1, int int2, float float1) {
		DebugLog.log("Warning: method is redundant! use method \'setSnowTarget\' instead.");
	}

	public void setSnowTarget(int int1) {
		if (!SandboxOptions.instance.EnableSnowOnGround.getValue()) {
			int1 = 0;
		}

		this.snowFracTarget = int1;
	}

	public void RenderSnow(int int1) {
		this.updateSnow(this.snowFracTarget);
		if (this.snowGridCur != null) {
			float float1 = 1.0F;
			float float2 = 1.0F;
			if (this.snowGridPrev.frac > this.snowGridCur.frac) {
				float2 = 0.0F;
			}

			long long1 = System.currentTimeMillis();
			if ((float)(long1 - this.snowFadeTime) < this.snowTransitionTime) {
				float float3 = (float)(long1 - this.snowFadeTime) / this.snowTransitionTime;
				if (this.snowGridPrev.frac < this.snowGridCur.frac) {
					float1 = float3;
					float2 = 1.0F;
				} else {
					float1 = 1.0F;
					float2 = 1.0F - float3 * float3;
				}
			}

			if (this.snowGridCur.frac > 0 || !(float2 <= 0.0F) && this.snowGridPrev.frac > 0) {
				int int2 = (int)IsoCamera.frameState.OffX;
				int int3 = (int)IsoCamera.frameState.OffY;
				for (int int4 = 0; int4 < SolidFloor.size(); ++int4) {
					IsoGridSquare square = (IsoGridSquare)SolidFloor.get(int4);
					if (square.getProperties().Is(IsoFlagType.solidfloor) && !square.getProperties().Is(IsoFlagType.water) && square.getProperties().Is(IsoFlagType.exterior) && square.room == null) {
						int int5 = square.getX() % this.snowGridCur.w;
						int int6 = square.getY() % this.snowGridCur.h;
						float float4 = IsoUtils.XToScreen((float)square.getX(), (float)square.getY(), (float)int1, 0);
						float float5 = IsoUtils.YToScreen((float)square.getX(), (float)square.getY(), (float)int1, 0);
						float4 = (float)((int)float4);
						float5 = (float)((int)float5);
						float4 -= (float)int2;
						float5 -= (float)int3;
						float float6 = (float)(32 * Core.TileScale);
						float float7 = (float)(96 * Core.TileScale);
						float4 -= float6;
						float5 -= float7;
						float float8 = 1.0F;
						float float9 = 1.0F;
						float float10 = 1.0F;
						for (int int7 = 0; int7 < 2; ++int7) {
							Texture texture;
							if (float2 > float1) {
								if (float1 > 0.0F) {
									texture = this.snowGridCur.grid[int5][int6][int7];
									if (texture != null) {
										texture.render((int)float4, (int)float5, texture.getWidth(), texture.getHeight(), float8, float9, float10, float1);
									}
								}

								if (float2 > 0.0F) {
									texture = this.snowGridPrev.grid[int5][int6][int7];
									if (texture != null) {
										texture.render((int)float4, (int)float5, texture.getWidth(), texture.getHeight(), float8, float9, float10, float2);
									}
								}
							} else {
								if (float2 > 0.0F) {
									texture = this.snowGridPrev.grid[int5][int6][int7];
									if (texture != null) {
										texture.render((int)float4, (int)float5, texture.getWidth(), texture.getHeight(), float8, float9, float10, float2);
									}
								}

								if (float1 > 0.0F) {
									texture = this.snowGridCur.grid[int5][int6][int7];
									if (texture != null) {
										texture.render((int)float4, (int)float5, texture.getWidth(), texture.getHeight(), float8, float9, float10, float1);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public IsoBuilding getClosestBuildingExcept(IsoGameCharacter gameCharacter, IsoRoom room) {
		IsoBuilding building = null;
		float float1 = 1000000.0F;
		for (int int1 = 0; int1 < this.BuildingList.size(); ++int1) {
			IsoBuilding building2 = (IsoBuilding)this.BuildingList.get(int1);
			for (int int2 = 0; int2 < building2.Exits.size(); ++int2) {
				float float2 = gameCharacter.DistTo(((IsoRoomExit)building2.Exits.get(int2)).x, ((IsoRoomExit)building2.Exits.get(int2)).y);
				if (float2 < float1 && (room == null || room.building != building2)) {
					building = building2;
					float1 = float2;
				}
			}
		}

		return building;
	}

	public int getDangerScore(int int1, int int2) {
		return int1 >= 0 && int2 >= 0 && int1 < this.width && int2 < this.height ? this.DangerScore.getValue(int1, int2) : 1000000;
	}

	private void ObjectDeletionAddition() {
		int int1;
		IsoMovingObject movingObject;
		for (int1 = 0; int1 < this.removeList.size(); ++int1) {
			movingObject = (IsoMovingObject)this.removeList.get(int1);
			if (movingObject instanceof IsoZombie) {
				VirtualZombieManager.instance.RemoveZombie((IsoZombie)movingObject);
			}

			if (!(movingObject instanceof IsoPlayer) || ((IsoPlayer)movingObject).isDead()) {
				this.ObjectList.remove(movingObject);
				if (movingObject.getCurrentSquare() != null) {
					movingObject.getCurrentSquare().getMovingObjects().remove(movingObject);
				}

				if (movingObject.getLastSquare() != null) {
					movingObject.getLastSquare().getMovingObjects().remove(movingObject);
				}
			}
		}

		this.removeList.clear();
		for (int1 = 0; int1 < this.addList.size(); ++int1) {
			movingObject = (IsoMovingObject)this.addList.get(int1);
			this.ObjectList.add(movingObject);
		}

		this.addList.clear();
		for (int1 = 0; int1 < this.addVehicles.size(); ++int1) {
			BaseVehicle baseVehicle = (BaseVehicle)this.addVehicles.get(int1);
			if (!this.ObjectList.contains(baseVehicle)) {
				this.ObjectList.add(baseVehicle);
			}

			if (!this.vehicles.contains(baseVehicle)) {
				this.vehicles.add(baseVehicle);
			}
		}

		this.addVehicles.clear();
	}

	private void ProcessItems(Iterator iterator) {
		int int1 = this.ProcessItems.size();
		int int2;
		for (int2 = 0; int2 < int1; ++int2) {
			InventoryItem inventoryItem = (InventoryItem)this.ProcessItems.get(int2);
			inventoryItem.update();
			if (inventoryItem.finishupdate()) {
				this.ProcessItemsRemove.add(inventoryItem);
			}
		}

		int1 = this.ProcessWorldItems.size();
		for (int2 = 0; int2 < int1; ++int2) {
			IsoWorldInventoryObject worldInventoryObject = (IsoWorldInventoryObject)this.ProcessWorldItems.get(int2);
			worldInventoryObject.update();
			if (worldInventoryObject.finishupdate()) {
				this.ProcessWorldItemsRemove.add(worldInventoryObject);
			}
		}
	}

	private void ProcessIsoObject() {
		this.ProcessIsoObject.removeAll(this.ProcessIsoObjectRemove);
		this.ProcessIsoObjectRemove.clear();
		int int1 = this.ProcessIsoObject.size();
		for (int int2 = 0; int2 < int1; ++int2) {
			IsoObject object = (IsoObject)this.ProcessIsoObject.get(int2);
			if (object != null) {
				object.update();
				if (int1 > this.ProcessIsoObject.size()) {
					--int2;
					--int1;
				}
			}
		}
	}

	private void ProcessObjects(Iterator iterator) {
		for (int int1 = 0; int1 < this.ObjectList.size(); ++int1) {
			IsoMovingObject movingObject = (IsoMovingObject)this.ObjectList.get(int1);
			if (movingObject instanceof IsoDeadBody) {
				this.removeList.add(movingObject);
			} else if (GameServer.bServer && movingObject instanceof IsoPlayer && !GameServer.Players.contains(movingObject)) {
				this.removeList.add(movingObject);
			} else if (movingObject instanceof IsoZombie && VirtualZombieManager.instance.isReused((IsoZombie)movingObject)) {
				DebugLog.log(DebugType.Zombie, "REUSABLE ZOMBIE IN IsoCell.ObjectList IGNORED " + movingObject);
			} else {
				movingObject.preupdate();
				movingObject.update();
			}
		}
	}

	private void ProcessRemoveItems(Iterator iterator) {
		this.ProcessItems.removeAll(this.ProcessItemsRemove);
		this.ProcessWorldItems.removeAll(this.ProcessWorldItemsRemove);
		this.ProcessItemsRemove.clear();
		this.ProcessWorldItemsRemove.clear();
	}

	private void ProcessStaticUpdaters() {
		int int1 = this.StaticUpdaterObjectList.size();
		for (int int2 = 0; int2 < int1; ++int2) {
			try {
				((IsoObject)this.StaticUpdaterObjectList.get(int2)).update();
			} catch (Exception exception) {
				Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
			}

			if (int1 > this.StaticUpdaterObjectList.size()) {
				--int2;
				--int1;
			}
		}
	}

	public void addToProcessIsoObject(IsoObject object) {
		if (object != null) {
			this.ProcessIsoObjectRemove.remove(object);
			if (!this.ProcessIsoObject.contains(object)) {
				this.ProcessIsoObject.add(object);
			}
		}
	}

	public void addToProcessItems(InventoryItem inventoryItem) {
		if (inventoryItem != null) {
			this.ProcessItemsRemove.remove(inventoryItem);
			if (!this.ProcessItems.contains(inventoryItem)) {
				this.ProcessItems.add(inventoryItem);
			}
		}
	}

	public void addToProcessItems(ArrayList arrayList) {
		if (arrayList != null) {
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				InventoryItem inventoryItem = (InventoryItem)arrayList.get(int1);
				if (inventoryItem != null) {
					this.ProcessItemsRemove.remove(inventoryItem);
					if (!this.ProcessItems.contains(inventoryItem)) {
						this.ProcessItems.add(inventoryItem);
					}
				}
			}
		}
	}

	public void addToProcessItemsRemove(InventoryItem inventoryItem) {
		if (inventoryItem != null) {
			if (!this.ProcessItemsRemove.contains(inventoryItem)) {
				this.ProcessItemsRemove.add(inventoryItem);
			}
		}
	}

	public void addToProcessItemsRemove(ArrayList arrayList) {
		if (arrayList != null) {
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				InventoryItem inventoryItem = (InventoryItem)arrayList.get(int1);
				if (inventoryItem != null && !this.ProcessItemsRemove.contains(inventoryItem)) {
					this.ProcessItemsRemove.add(inventoryItem);
				}
			}
		}
	}

	public IsoSurvivor getNetworkPlayer(int int1) {
		int int2 = this.RemoteSurvivorList.size();
		for (int int3 = 0; int3 < int2; ++int3) {
			if (((IsoGameCharacter)this.RemoteSurvivorList.get(int3)).getRemoteID() == int1) {
				return (IsoSurvivor)this.RemoteSurvivorList.get(int3);
			}
		}

		return null;
	}

	public boolean IsStairsNode(IsoGridSquare square, IsoGridSquare square2, IsoDirections directions) {
		if (square.Has(IsoObjectType.stairsTN)) {
			return directions == IsoDirections.N;
		} else if (square.Has(IsoObjectType.stairsTW)) {
			return directions == IsoDirections.W;
		} else {
			return square2.getZ() == square.getZ();
		}
	}

	public void InitNodeMap(int int1) {
	}

	IsoGridSquare ConnectNewSquare(IsoGridSquare square, boolean boolean1, boolean boolean2) {
		int int1 = square.getX();
		int int2 = square.getY();
		int int3 = square.getZ();
		this.setCacheGridSquare(int1, int2, int3, square);
		this.DoGridNav(square, IsoGridSquare.cellGetSquare);
		return square;
	}

	public void DoGridNav(IsoGridSquare square, IsoGridSquare.GetSquare getSquare) {
		int int1 = square.getX();
		int int2 = square.getY();
		int int3 = square.getZ();
		square.nav[IsoDirections.N.index()] = getSquare.getGridSquare(int1, int2 - 1, int3);
		square.nav[IsoDirections.NW.index()] = getSquare.getGridSquare(int1 - 1, int2 - 1, int3);
		square.nav[IsoDirections.W.index()] = getSquare.getGridSquare(int1 - 1, int2, int3);
		square.nav[IsoDirections.SW.index()] = getSquare.getGridSquare(int1 - 1, int2 + 1, int3);
		square.nav[IsoDirections.S.index()] = getSquare.getGridSquare(int1, int2 + 1, int3);
		square.nav[IsoDirections.SE.index()] = getSquare.getGridSquare(int1 + 1, int2 + 1, int3);
		square.nav[IsoDirections.E.index()] = getSquare.getGridSquare(int1 + 1, int2, int3);
		square.nav[IsoDirections.NE.index()] = getSquare.getGridSquare(int1 + 1, int2 - 1, int3);
		if (square.nav[IsoDirections.N.index()] != null) {
			square.nav[IsoDirections.N.index()].nav[IsoDirections.S.index()] = square;
		}

		if (square.nav[IsoDirections.NW.index()] != null) {
			square.nav[IsoDirections.NW.index()].nav[IsoDirections.SE.index()] = square;
		}

		if (square.nav[IsoDirections.W.index()] != null) {
			square.nav[IsoDirections.W.index()].nav[IsoDirections.E.index()] = square;
		}

		if (square.nav[IsoDirections.SW.index()] != null) {
			square.nav[IsoDirections.SW.index()].nav[IsoDirections.NE.index()] = square;
		}

		if (square.nav[IsoDirections.S.index()] != null) {
			square.nav[IsoDirections.S.index()].nav[IsoDirections.N.index()] = square;
		}

		if (square.nav[IsoDirections.SE.index()] != null) {
			square.nav[IsoDirections.SE.index()].nav[IsoDirections.NW.index()] = square;
		}

		if (square.nav[IsoDirections.E.index()] != null) {
			square.nav[IsoDirections.E.index()].nav[IsoDirections.W.index()] = square;
		}

		if (square.nav[IsoDirections.NE.index()] != null) {
			square.nav[IsoDirections.NE.index()].nav[IsoDirections.SW.index()] = square;
		}
	}

	public IsoGridSquare ConnectNewSquare(IsoGridSquare square, boolean boolean1) {
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			if (!this.ChunkMap[int1].ignore) {
				this.ChunkMap[int1].setGridSquare(square, square.getX(), square.getY(), square.getZ());
			}
		}

		IsoGridSquare square2 = this.ConnectNewSquare(square, boolean1, false);
		return square2;
	}

	public void PlaceLot(String string, int int1, int int2, int int3, boolean boolean1) {
	}

	public void PlaceLot(IsoLot lot, int int1, int int2, int int3, boolean boolean1) {
		boolean boolean2 = true;
		Stack stack = new Stack();
		for (int int4 = int1; int4 < int1 + lot.info.width; ++int4) {
			for (int int5 = int2; int5 < int2 + lot.info.height; ++int5) {
				boolean boolean3 = true;
				for (int int6 = int3; int6 < int3 + lot.info.levels; ++int6) {
					boolean3 = false;
					if (int4 < this.width && int5 < this.height && int4 >= 0 && int5 >= 0 && int6 >= 0) {
						Integer[] integerArray = lot.data[int4 - int1][int5 - int2][int6 - int3];
						if (integerArray != null) {
							int int7 = integerArray.length;
							boolean boolean4 = false;
							IsoGridSquare square = this.getGridSquare(int4, int5, int6);
							if (square == null) {
								square = IsoGridSquare.getNew(this, (SliceY)null, int4, int5, int6);
								this.ChunkMap[IsoPlayer.getPlayerIndex()].setGridSquare(square, int4, int5, int6);
								if (this.bDoLotConnect) {
								}
							}

							if (int6 < 8) {
								square = IsoGridSquare.getNew(this, (SliceY)null, int4, int5, int6 + 1);
								this.ChunkMap[IsoPlayer.getPlayerIndex()].setGridSquare(square, int4, int5, int6 + 1);
								if (this.bDoLotConnect) {
								}
							}

							if (int7 > 0 && int6 > MaxHeight) {
								MaxHeight = int6;
							}

							for (int int8 = 0; int8 < int7; ++int8) {
								String string = (String)lot.info.tilesUsed.get(integerArray[int8]);
								IsoSprite sprite = (IsoSprite)this.SpriteManager.NamedMap.get(string);
								if (sprite == null) {
									Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, "Missing tile definition: " + string);
								} else {
									IsoGridSquare square2 = this.getGridSquare(int4, int5, int6);
									if (square2 == null) {
										square2 = IsoGridSquare.getNew(this, (SliceY)null, int4, int5, int6);
										this.ChunkMap[IsoPlayer.getPlayerIndex()].setGridSquare(square2, int4, int5, int6);
										if (this.bDoLotConnect) {
										}
									} else {
										if (boolean1 && int8 == 0 && sprite.getProperties().Is(IsoFlagType.solidfloor) && (!sprite.Properties.Is(IsoFlagType.hidewalls) || integerArray.length > 1)) {
											boolean3 = true;
										}

										if (boolean3 && int8 == 0) {
											square2.getObjects().clear();
										}
									}

									CellLoader.DoTileObjectCreation(sprite, sprite.getType(), square2, this, int4, int5, int6, stack, false, string);
								}
							}
						}
					}
				}
			}
		}

		boolean boolean5 = false;
	}

	public void PlaceLot(IsoLot lot, int int1, int int2, int int3, IsoChunk chunk, int int4, int int5, boolean boolean1) {
		boolean boolean2 = true;
		Stack stack = new Stack();
		int4 *= 10;
		int5 *= 10;
		try {
			int int6;
			int int7;
			try {
				for (int int8 = int4 + int1; int8 < int4 + int1 + 10; ++int8) {
					for (int6 = int5 + int2; int6 < int5 + int2 + 10; ++int6) {
						boolean boolean3 = true;
						for (int7 = int3; int7 < int3 + 8; ++int7) {
							boolean3 = false;
							if (int8 < int4 + 10 && int6 < int5 + 10 && int8 >= int4 && int6 >= int5 && int7 >= 0) {
								Integer[] integerArray = lot.data[int8 - (int4 + int1)][int6 - (int5 + int2)][int7 - int3];
								IsoGridSquare square = null;
								if (integerArray != null && integerArray.length > 0) {
									int int9 = integerArray.length;
									boolean boolean4 = false;
									int int10;
									if (square == null) {
										square = chunk.getGridSquare(int8 - int4, int6 - int5, int7);
										if (square == null) {
											square = IsoGridSquare.getNew(this, (SliceY)null, int8, int6, int7);
											square.setX(int8);
											square.setY(int6);
											square.setZ(int7);
											chunk.setSquare(int8 - int4, int6 - int5, int7, square);
										}

										for (int int11 = -1; int11 <= 1; ++int11) {
											for (int10 = -1; int10 <= 1; ++int10) {
												if ((int11 != 0 || int10 != 0) && int11 + int8 - int4 >= 0 && int11 + int8 - int4 < 10 && int10 + int6 - int5 >= 0 && int10 + int6 - int5 < 10) {
													IsoGridSquare square2 = chunk.getGridSquare(int8 + int11 - int4, int6 + int10 - int5, int7);
													if (square2 == null) {
														square2 = IsoGridSquare.getNew(this, (SliceY)null, int8 + int11, int6 + int10, int7);
														chunk.setSquare(int8 + int11 - int4, int6 + int10 - int5, int7, square2);
													}
												}
											}
										}
									}

									if (int9 > 1 && int7 > MaxHeight) {
										MaxHeight = int7;
									}

									RoomDef roomDef = IsoWorld.instance.getMetaChunkFromTile(int8, int6).getRoomAt(int8, int6, int7);
									int10 = roomDef != null ? roomDef.ID : -1;
									square.setRoomID(int10);
									square.ResetMasterRegion();
									roomDef = IsoWorld.instance.getMetaChunkFromTile(int8, int6).getEmptyOutsideAt(int8, int6, int7);
									if (roomDef != null) {
										IsoRoom room = chunk.getRoom(roomDef.ID);
										square.roofHideBuilding = room == null ? null : room.building;
									}

									for (int int12 = 0; int12 < int9; ++int12) {
										String string = (String)lot.info.tilesUsed.get(integerArray[int12]);
										if (!lot.info.bFixed2x) {
											string = IsoChunk.Fix2x(string);
										}

										IsoSprite sprite = (IsoSprite)this.SpriteManager.NamedMap.get(string);
										if (sprite == null) {
											Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, "Missing tile definition: " + string);
										} else {
											if (int12 == 0 && sprite.getProperties().Is(IsoFlagType.solidfloor) && (!sprite.Properties.Is(IsoFlagType.hidewalls) || integerArray.length > 1)) {
												boolean3 = true;
											}

											if (boolean3 && int12 == 0) {
												square.getObjects().clear();
											}

											CellLoader.DoTileObjectCreation(sprite, sprite.getType(), square, this, int8, int6, int7, stack, false, string);
										}
									}

									square.FixStackableObjects();
								}
							}
						}
					}
				}
			} catch (Exception exception) {
				DebugLog.log("Failed to load chunk, blocking out area");
				ExceptionLogger.logException(exception);
				for (int6 = int4 + int1; int6 < int4 + int1 + 10; ++int6) {
					for (int int13 = int5 + int2; int13 < int5 + int2 + 10; ++int13) {
						for (int7 = int3; int7 < int3 + lot.info.levels; ++int7) {
							chunk.setSquare(int6 - int4, int13 - int5, int7, (IsoGridSquare)null);
							this.setCacheGridSquare(int6, int13, int7, (IsoGridSquare)null);
						}
					}
				}
			}
		} finally {
			;
		}

		boolean boolean5 = false;
	}

	public void setDrag(KahluaTable kahluaTable, int int1) {
		if (int1 >= 0 && int1 < 4) {
			this.drag[int1] = kahluaTable;
		}
	}

	public KahluaTable getDrag(int int1) {
		return int1 >= 0 && int1 < 4 ? this.drag[int1] : null;
	}

	public boolean DoBuilding(int int1, boolean boolean1) {
		if (UIManager.getPickedTile() != null && this.drag[int1] != null && JoypadManager.instance.getFromPlayer(int1) == null) {
			if (!IsoWorld.instance.isValidSquare((int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ())) {
				return false;
			}

			IsoGridSquare square = this.getGridSquare((int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ());
			if (!boolean1) {
				if (square == null) {
					square = this.createNewGridSquare((int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ(), true);
				}

				square.EnsureSurroundNotNull();
			}

			LuaEventManager.triggerEvent("OnDoTileBuilding2", this.drag[int1], boolean1, (int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ(), square);
		}

		if (this.drag[int1] != null && JoypadManager.instance.getFromPlayer(int1) != null) {
			LuaEventManager.triggerEvent("OnDoTileBuilding3", this.drag[int1], boolean1, (int)IsoPlayer.players[int1].getX(), (int)IsoPlayer.players[int1].getY(), (int)IsoCamera.CamCharacter.getZ());
		}

		if (boolean1) {
			IndieGL.glBlendFunc(770, 771);
		}

		return false;
	}

	public float DistanceFromSupport(int int1, int int2, int int3) {
		return 0.0F;
	}

	public ArrayList getBuildingList() {
		return this.BuildingList;
	}

	public void setBuildingList(ArrayList arrayList) {
		this.BuildingList = arrayList;
	}

	public ArrayList getObjectList() {
		return this.ObjectList;
	}

	public void setObjectList(ArrayList arrayList) {
		this.ObjectList = arrayList;
	}

	public IsoRoom getRoom(int int1) {
		IsoRoom room = this.ChunkMap[IsoPlayer.getPlayerIndex()].getRoom(int1);
		return room;
	}

	public ArrayList getPushableObjectList() {
		return this.PushableObjectList;
	}

	public void setPushableObjectList(ArrayList arrayList) {
		this.PushableObjectList = arrayList;
	}

	public HashMap getBuildingScores() {
		return this.BuildingScores;
	}

	public void setBuildingScores(HashMap hashMap) {
		this.BuildingScores = hashMap;
	}

	public AStarPathMap getPathMap() {
		return this.PathMap;
	}

	public void setPathMap(AStarPathMap aStarPathMap) {
		this.PathMap = aStarPathMap;
	}

	public ArrayList getRoomList() {
		return this.RoomList;
	}

	public void setRoomList(ArrayList arrayList) {
		this.RoomList = arrayList;
	}

	public ArrayList getStaticUpdaterObjectList() {
		return this.StaticUpdaterObjectList;
	}

	public void setStaticUpdaterObjectList(ArrayList arrayList) {
		this.StaticUpdaterObjectList = arrayList;
	}

	public ArrayList getWallArray() {
		return this.wallArray;
	}

	public void setWallArray(ArrayList arrayList) {
		this.wallArray = arrayList;
	}

	public ArrayList getZombieList() {
		return this.ZombieList;
	}

	public void setZombieList(ArrayList arrayList) {
		this.ZombieList = arrayList;
	}

	public ArrayList getRemoteSurvivorList() {
		return this.RemoteSurvivorList;
	}

	public void setRemoteSurvivorList(ArrayList arrayList) {
		this.RemoteSurvivorList = arrayList;
	}

	public ArrayList getGhostList() {
		return this.GhostList;
	}

	public void setGhostList(ArrayList arrayList) {
		this.GhostList = arrayList;
	}

	public ArrayList getZoneStack() {
		return this.ZoneStack;
	}

	public void setZoneStack(ArrayList arrayList) {
		this.ZoneStack = arrayList;
	}

	public ArrayList getRemoveList() {
		return this.removeList;
	}

	public void setRemoveList(ArrayList arrayList) {
		this.removeList = arrayList;
	}

	public ArrayList getAddList() {
		return this.addList;
	}

	public void addMovingObject(IsoMovingObject movingObject) {
		this.addList.add(movingObject);
	}

	public void setAddList(ArrayList arrayList) {
		this.addList = arrayList;
	}

	public ArrayList getRenderJobsArray() {
		return this.RenderJobsArray;
	}

	public void setRenderJobsArray(ArrayList arrayList) {
		this.RenderJobsArray = arrayList;
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

	public void setProcessItems(ArrayList arrayList) {
		this.ProcessItems = arrayList;
	}

	public ArrayList getProcessItemsRemove() {
		return this.ProcessItemsRemove;
	}

	public void setProcessItemsRemove(ArrayList arrayList) {
		this.ProcessItemsRemove = arrayList;
	}

	public HashMap getRenderJobsMapArray() {
		return this.RenderJobsMapArray;
	}

	public void setRenderJobsMapArray(HashMap hashMap) {
		this.RenderJobsMapArray = hashMap;
	}

	public ArrayList getVehicles() {
		return this.vehicles;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int int1) {
		this.height = int1;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int int1) {
		this.width = int1;
	}

	public int getWorldX() {
		return this.worldX;
	}

	public void setWorldX(int int1) {
		this.worldX = int1;
	}

	public int getWorldY() {
		return this.worldY;
	}

	public void setWorldY(int int1) {
		this.worldY = int1;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String string) {
		this.filename = string;
	}

	public boolean isSafeToAdd() {
		return this.safeToAdd;
	}

	public void setSafeToAdd(boolean boolean1) {
		this.safeToAdd = boolean1;
	}

	public Stack getLamppostPositions() {
		return this.LamppostPositions;
	}

	public void addLamppost(IsoLightSource lightSource) {
		if (lightSource != null && !this.LamppostPositions.contains(lightSource)) {
			this.LamppostPositions.add(lightSource);
			IsoGridSquare.RecalcLightTime = -1;
			GameTime.instance.lightSourceUpdate = 100.0F;
		}
	}

	public IsoLightSource addLamppost(int int1, int int2, int int3, float float1, float float2, float float3, int int4) {
		IsoLightSource lightSource = new IsoLightSource(int1, int2, int3, float1, float2, float3, int4);
		this.LamppostPositions.add(lightSource);
		IsoGridSquare.RecalcLightTime = -1;
		GameTime.instance.lightSourceUpdate = 100.0F;
		return lightSource;
	}

	public void removeLamppost(int int1, int int2, int int3) {
		for (int int4 = 0; int4 < this.LamppostPositions.size(); ++int4) {
			IsoLightSource lightSource = (IsoLightSource)this.LamppostPositions.get(int4);
			if (lightSource.getX() == int1 && lightSource.getY() == int2 && lightSource.getZ() == int3) {
				lightSource.clearInfluence();
				this.LamppostPositions.remove(lightSource);
				IsoGridSquare.RecalcLightTime = -1;
				GameTime.instance.lightSourceUpdate = 100.0F;
				return;
			}
		}
	}

	public void removeLamppost(IsoLightSource lightSource) {
		lightSource.life = 0;
		IsoGridSquare.RecalcLightTime = -1;
		GameTime.instance.lightSourceUpdate = 100.0F;
	}

	public void setLamppostPositions(Stack stack) {
		this.LamppostPositions = stack;
	}

	public HashSet getStairsNodes() {
		return this.stairsNodes;
	}

	public void setStairsNodes(HashSet hashSet) {
		this.stairsNodes = hashSet;
	}

	public Stack getTempZoneStack() {
		return this.tempZoneStack;
	}

	public void setTempZoneStack(Stack stack) {
		this.tempZoneStack = stack;
	}

	public int getCurrentLightX() {
		return this.currentLX;
	}

	public void setCurrentLightX(int int1) {
		this.currentLX = int1;
	}

	public int getCurrentLightY() {
		return this.currentLY;
	}

	public void setCurrentLightY(int int1) {
		this.currentLY = int1;
	}

	public int getCurrentLightZ() {
		return this.currentLZ;
	}

	public void setCurrentLightZ(int int1) {
		this.currentLZ = int1;
	}

	public IsoSprite getWoodWallN() {
		return this.woodWallN;
	}

	public void setWoodWallN(IsoSprite sprite) {
		this.woodWallN = sprite;
	}

	public IsoSprite getWoodWallW() {
		return this.woodWallW;
	}

	public void setWoodWallW(IsoSprite sprite) {
		this.woodWallW = sprite;
	}

	public IsoSprite getWoodDWallN() {
		return this.woodDWallN;
	}

	public void setWoodDWallN(IsoSprite sprite) {
		this.woodDWallN = sprite;
	}

	public IsoSprite getWoodDWallW() {
		return this.woodDWallW;
	}

	public void setWoodDWallW(IsoSprite sprite) {
		this.woodDWallW = sprite;
	}

	public IsoSprite getWoodWWallN() {
		return this.woodWWallN;
	}

	public void setWoodWWallN(IsoSprite sprite) {
		this.woodWWallN = sprite;
	}

	public IsoSprite getWoodWWallW() {
		return this.woodWWallW;
	}

	public void setWoodWWallW(IsoSprite sprite) {
		this.woodWWallW = sprite;
	}

	public IsoSprite getWoodDoorW() {
		return this.woodDoorW;
	}

	public void setWoodDoorW(IsoSprite sprite) {
		this.woodDoorW = sprite;
	}

	public IsoSprite getWoodDoorN() {
		return this.woodDoorN;
	}

	public void setWoodDoorN(IsoSprite sprite) {
		this.woodDoorN = sprite;
	}

	public IsoSprite getWoodFloor() {
		return this.woodFloor;
	}

	public void setWoodFloor(IsoSprite sprite) {
		this.woodFloor = sprite;
	}

	public IsoSprite getWoodBarricade() {
		return this.woodBarricade;
	}

	public void setWoodBarricade(IsoSprite sprite) {
		this.woodBarricade = sprite;
	}

	public IsoSprite getWoodCrate() {
		return this.woodCrate;
	}

	public void setWoodCrate(IsoSprite sprite) {
		this.woodCrate = sprite;
	}

	public IsoSprite getWoodStairsNB() {
		return this.woodStairsNB;
	}

	public void setWoodStairsNB(IsoSprite sprite) {
		this.woodStairsNB = sprite;
	}

	public IsoSprite getWoodStairsNM() {
		return this.woodStairsNM;
	}

	public void setWoodStairsNM(IsoSprite sprite) {
		this.woodStairsNM = sprite;
	}

	public IsoSprite getWoodStairsNT() {
		return this.woodStairsNT;
	}

	public void setWoodStairsNT(IsoSprite sprite) {
		this.woodStairsNT = sprite;
	}

	public IsoSprite getWoodStairsWB() {
		return this.woodStairsWB;
	}

	public void setWoodStairsWB(IsoSprite sprite) {
		this.woodStairsWB = sprite;
	}

	public IsoSprite getWoodStairsWM() {
		return this.woodStairsWM;
	}

	public void setWoodStairsWM(IsoSprite sprite) {
		this.woodStairsWM = sprite;
	}

	public IsoSprite getWoodStairsWT() {
		return this.woodStairsWT;
	}

	public void setWoodStairsWT(IsoSprite sprite) {
		this.woodStairsWT = sprite;
	}

	public int getMinX() {
		return this.minX;
	}

	public void setMinX(int int1) {
		this.minX = int1;
	}

	public int getMaxX() {
		return this.maxX;
	}

	public void setMaxX(int int1) {
		this.maxX = int1;
	}

	public int getMinY() {
		return this.minY;
	}

	public void setMinY(int int1) {
		this.minY = int1;
	}

	public int getMaxY() {
		return this.maxY;
	}

	public void setMaxY(int int1) {
		this.maxY = int1;
	}

	public int getMinZ() {
		return this.minZ;
	}

	public void setMinZ(int int1) {
		this.minZ = int1;
	}

	public int getMaxZ() {
		return this.maxZ;
	}

	public void setMaxZ(int int1) {
		this.maxZ = int1;
	}

	public OnceEvery getDangerUpdate() {
		return this.dangerUpdate;
	}

	public void setDangerUpdate(OnceEvery onceEvery) {
		this.dangerUpdate = onceEvery;
	}

	public Thread getLightInfoUpdate() {
		return this.LightInfoUpdate;
	}

	public void setLightInfoUpdate(Thread thread) {
		this.LightInfoUpdate = thread;
	}

	public ArrayList getSurvivorList() {
		return this.SurvivorList;
	}

	public static int getRComponent(int int1) {
		return int1 & 255;
	}

	public static int getGComponent(int int1) {
		return (int1 & '＀') >> 8;
	}

	public static int getBComponent(int int1) {
		return (int1 & 16711680) >> 16;
	}

	public static int toIntColor(float float1, float float2, float float3, float float4) {
		return (int)(float1 * 255.0F) << 0 | (int)(float2 * 255.0F) << 8 | (int)(float3 * 255.0F) << 16 | (int)(float4 * 255.0F) << 24;
	}

	public IsoGridSquare getRandomOutdoorTile() {
		IsoGridSquare square = null;
		do {
			square = this.getGridSquare(this.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldXMin() * 10 + Rand.Next(this.width), this.ChunkMap[IsoPlayer.getPlayerIndex()].getWorldYMin() * 10 + Rand.Next(this.height), 0);
			if (square != null) {
				square.setCachedIsFree(false);
			}
		} while (square == null || !square.isFree(false) || square.getRoom() != null);

		return square;
	}

	private static void InsertAt(int int1, BuildingScore buildingScore, BuildingScore[] buildingScoreArray) {
		for (int int2 = buildingScoreArray.length - 1; int2 > int1; --int2) {
			buildingScoreArray[int2] = buildingScoreArray[int2 - 1];
		}

		buildingScoreArray[int1] = buildingScore;
	}

	static void Place(BuildingScore buildingScore, BuildingScore[] buildingScoreArray, IsoCell.BuildingSearchCriteria buildingSearchCriteria) {
		for (int int1 = 0; int1 < buildingScoreArray.length; ++int1) {
			if (buildingScoreArray[int1] != null) {
				boolean boolean1 = false;
				if (buildingScoreArray[int1] == null) {
					boolean1 = true;
				} else {
					switch (buildingSearchCriteria) {
					case General: 
						if (buildingScoreArray[int1].food + buildingScoreArray[int1].defense + (float)buildingScoreArray[int1].size + buildingScoreArray[int1].weapons < buildingScore.food + buildingScore.defense + (float)buildingScore.size + buildingScore.weapons) {
							boolean1 = true;
						}

						break;
					
					case Food: 
						if (buildingScoreArray[int1].food < buildingScore.food) {
							boolean1 = true;
						}

						break;
					
					case Wood: 
						if (buildingScoreArray[int1].wood < buildingScore.wood) {
							boolean1 = true;
						}

						break;
					
					case Weapons: 
						if (buildingScoreArray[int1].weapons < buildingScore.weapons) {
							boolean1 = true;
						}

						break;
					
					case Defense: 
						if (buildingScoreArray[int1].defense < buildingScore.defense) {
							boolean1 = true;
						}

					
					}
				}

				if (boolean1) {
					InsertAt(int1, buildingScore, buildingScoreArray);
					return;
				}
			}
		}
	}

	public Stack getBestBuildings(IsoCell.BuildingSearchCriteria buildingSearchCriteria, int int1) {
		BuildingScore[] buildingScoreArray = new BuildingScore[int1];
		int int2;
		int int3;
		if (this.BuildingScores.isEmpty()) {
			int2 = this.BuildingList.size();
			for (int3 = 0; int3 < int2; ++int3) {
				((IsoBuilding)this.BuildingList.get(int3)).update();
			}
		}

		int2 = this.BuildingScores.size();
		for (int3 = 0; int3 < int2; ++int3) {
			BuildingScore buildingScore = (BuildingScore)this.BuildingScores.get(int3);
			Place(buildingScore, buildingScoreArray, buildingSearchCriteria);
		}

		buildingscores.clear();
		buildingscores.addAll(Arrays.asList(buildingScoreArray));
		return buildingscores;
	}

	public void AddZone(String string, int int1, int int2, int int3, int int4, int int5) {
		this.ZoneStack.add(new IsoCell.Zone(string, int1, int2, int3, int4, int5));
	}

	public boolean blocked(Mover mover, int int1, int int2, int int3, int int4, int int5, int int6) {
		IsoGridSquare square = this.getGridSquare(int4, int5, int6);
		if (square == null) {
			return true;
		} else {
			if (mover instanceof IsoMovingObject) {
				if (square.testPathFindAdjacent((IsoMovingObject)mover, int1 - int4, int2 - int5, int3 - int6)) {
					return true;
				}
			} else if (square.testPathFindAdjacent((IsoMovingObject)null, int1 - int4, int2 - int5, int3 - int6)) {
				return true;
			}

			return false;
		}
	}

	public void Dispose() {
		int int1;
		for (int1 = 0; int1 < this.ObjectList.size(); ++int1) {
			IsoMovingObject movingObject = (IsoMovingObject)this.ObjectList.get(int1);
			if (movingObject instanceof IsoZombie) {
				movingObject.setCurrent((IsoGridSquare)null);
				movingObject.setLast((IsoGridSquare)null);
				VirtualZombieManager.instance.addToReusable((IsoZombie)movingObject);
			}
		}

		this.stopLightingThread();
		super.Dispose();
		for (int1 = 0; int1 < this.RoomList.size(); ++int1) {
			((IsoRoom)this.RoomList.get(int1)).TileList.clear();
			((IsoRoom)this.RoomList.get(int1)).Exits.clear();
			((IsoRoom)this.RoomList.get(int1)).WaterSources.clear();
			((IsoRoom)this.RoomList.get(int1)).lightSwitches.clear();
			((IsoRoom)this.RoomList.get(int1)).Beds.clear();
		}

		for (int1 = 0; int1 < this.BuildingList.size(); ++int1) {
			((IsoBuilding)this.BuildingList.get(int1)).Exits.clear();
			((IsoBuilding)this.BuildingList.get(int1)).Rooms.clear();
			((IsoBuilding)this.BuildingList.get(int1)).container.clear();
			((IsoBuilding)this.BuildingList.get(int1)).Windows.clear();
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
		for (int1 = 0; int1 < this.ChunkMap.length; ++int1) {
			this.ChunkMap[int1].Dispose();
			this.ChunkMap[int1] = null;
		}
	}

	public float getCost(Mover mover, int int1, int int2, int int3, int int4, int int5, int int6) {
		float float1 = 0.0F;
		if (int1 != int4 && int2 != int5) {
			++float1;
		} else {
			++float1;
		}

		IsoGridSquare square;
		if (mover instanceof IsoZombie) {
			square = this.getGridSquare(int1, int2, int3);
			if (square == null) {
				return 100000.0F;
			} else {
				float1 *= (float)((square.getMovingObjects().size() + 1) * 3);
				return square.Has(IsoObjectType.tree) ? float1 * 100.0F : float1;
			}
		} else {
			if (mover instanceof IsoLivingCharacter) {
				square = this.getGridSquare(int1, int2, int3);
				IsoGridSquare square2 = this.getGridSquare(int4, int5, int6);
				if (square == null) {
					return 1000000.0F;
				}

				if (square2 != null) {
					if (square.IsWindow(square2.getX() - square.getX(), square2.getY() - square.getY(), 0)) {
						float1 += 200.0F;
					}

					label53: {
						if (square2.SolidFloorCached) {
							if (square2.SolidFloor) {
								break label53;
							}
						} else if (square2.TreatAsSolidFloor()) {
							break label53;
						}

						float1 += 10000.0F;
					}

					int int7 = square2.getMovingObjects().size();
					for (int int8 = 0; int8 < int7; ++int8) {
						IsoMovingObject movingObject = (IsoMovingObject)square2.getMovingObjects().get(int8);
						if (movingObject instanceof IsoPushableObject) {
							float1 += 10000.0F;
						}

						if (movingObject instanceof IsoZombie) {
							float1 += 10.0F;
						}
					}
				}
			}

			return float1;
		}
	}

	public int getElevInTiles() {
		return 8;
	}

	public IsoGridSquare getFreeTile(String string) {
		Iterator iterator = this.ZoneStack.iterator();
		IsoCell.Zone zone;
		while (iterator != null && iterator.hasNext()) {
			zone = (IsoCell.Zone)iterator.next();
			if (zone.Name.equals(string)) {
				this.tempZoneStack.add(zone);
			}
		}

		if (this.tempZoneStack.isEmpty()) {
			return null;
		} else {
			zone = (IsoCell.Zone)this.tempZoneStack.get(Rand.Next(this.tempZoneStack.size()));
			this.tempZoneStack.clear();
			return this.getFreeTile(zone);
		}
	}

	public IsoGridSquare getFreeTile(IsoCell.Zone zone) {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < zone.W; ++int1) {
			for (int int2 = 0; int2 < zone.H; ++int2) {
				IsoGridSquare square = this.getGridSquare(int1 + zone.X, int2 + zone.Y, zone.Z);
				if (square != null && !square.getProperties().Is(IsoFlagType.solid) && !square.getProperties().Is(IsoFlagType.solidtrans)) {
					arrayList.add(square);
				}
			}
		}

		if (!arrayList.isEmpty()) {
			return (IsoGridSquare)arrayList.get(Rand.Next(arrayList.size()));
		} else {
			return null;
		}
	}

	@LuaMethod(name = "getGridSquare")
	public IsoGridSquare getGridSquare(double double1, double double2, double double3) {
		return GameServer.bServer ? ServerMap.instance.getGridSquare((int)double1, (int)double2, (int)double3) : this.getGridSquare((int)double1, (int)double2, (int)double3);
	}

	@LuaMethod(name = "getOrCreateGridSquare")
	public IsoGridSquare getOrCreateGridSquare(double double1, double double2, double double3) {
		if (GameServer.bServer) {
			IsoGridSquare square = ServerMap.instance.getGridSquare((int)double1, (int)double2, (int)double3);
			if (square == null) {
				square = IsoGridSquare.getNew(this, (SliceY)null, (int)double1, (int)double2, (int)double3);
				ServerMap.instance.setGridSquare((int)double1, (int)double2, (int)double3, square);
				this.ConnectNewSquare(square, true);
			}

			return square;
		} else {
			IsoChunkMap chunkMap = this.ChunkMap[IsoPlayer.getPlayerIndex()];
			if (!(double3 >= 8.0) && !(double3 < 0.0) && !(double1 < (double)chunkMap.getWorldXMinTiles()) && !(double1 >= (double)chunkMap.getWorldXMaxTiles()) && !(double2 < (double)chunkMap.getWorldYMinTiles()) && !(double2 >= (double)chunkMap.getWorldYMaxTiles())) {
				IsoGridSquare square2 = this.getGridSquare((int)double1, (int)double2, (int)double3);
				if (square2 == null) {
					square2 = IsoGridSquare.getNew(this, (SliceY)null, (int)double1, (int)double2, (int)double3);
					this.ConnectNewSquare(square2, true);
				}

				return square2;
			} else {
				return null;
			}
		}
	}

	public void setCacheGridSquare(int int1, int int2, int int3, IsoGridSquare square) {
		assert square == null || int1 == square.getX() && int2 == square.getY() && int3 == square.getZ();
		if (ENABLE_SQUARE_CACHE) {
			if (!GameServer.bServer) {
				assert this.getChunkForGridSquare(int1, int2, int3) != null;
				int int4 = IsoChunkMap.ChunkWidthInTiles;
				for (int int5 = 0; int5 < IsoPlayer.numPlayers; ++int5) {
					if (!this.ChunkMap[int5].ignore) {
						this.ChunkMap[int5].YMinTiles = -1;
						this.ChunkMap[int5].XMinTiles = -1;
						this.ChunkMap[int5].YMaxTiles = -1;
						this.ChunkMap[int5].XMaxTiles = -1;
						int int6 = int1 - this.ChunkMap[int5].getWorldXMinTiles();
						int int7 = int2 - this.ChunkMap[int5].getWorldYMinTiles();
						if (int3 < 8 && int3 >= 0 && int6 >= 0 && int6 < int4 && int7 >= 0 && int7 < int4) {
							this.gridSquares[int5][int6 + int7 * int4 + int3 * int4 * int4] = square;
						}
					}
				}
			}
		}
	}

	public void setCacheChunk(IsoChunk chunk) {
		if (!GameServer.bServer) {
			if (ENABLE_SQUARE_CACHE) {
				int int1 = IsoChunkMap.ChunkWidthInTiles;
				for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
					IsoChunkMap chunkMap = this.ChunkMap[int2];
					if (!chunkMap.ignore) {
						int int3 = chunk.wx - chunkMap.getWorldXMin();
						int int4 = chunk.wy - chunkMap.getWorldYMin();
						if (int3 >= 0 && int3 < IsoChunkMap.ChunkGridWidth && int4 >= 0 && int4 < IsoChunkMap.ChunkGridWidth) {
							for (int int5 = 0; int5 < 8; ++int5) {
								for (int int6 = 0; int6 < 10; ++int6) {
									for (int int7 = 0; int7 < 10; ++int7) {
										IsoGridSquare square = chunk.squares[int5][int7 + int6 * 10];
										int int8 = int3 * 10 + int7;
										int int9 = int4 * 10 + int6;
										this.gridSquares[int2][int8 + int9 * int1 + int5 * int1 * int1] = square;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void clearCacheGridSquare(int int1) {
		if (ENABLE_SQUARE_CACHE) {
			if (!GameServer.bServer) {
				int int2 = IsoChunkMap.ChunkWidthInTiles;
				this.gridSquares[int1] = new IsoGridSquare[int2 * int2 * 8];
			}
		}
	}

	public void setCacheGridSquareLocal(int int1, int int2, int int3, IsoGridSquare square, int int4) {
		if (ENABLE_SQUARE_CACHE) {
			if (!GameServer.bServer) {
				int int5 = IsoChunkMap.ChunkWidthInTiles;
				if (int3 < 8 && int3 >= 0 && int1 >= 0 && int1 < int5 && int2 >= 0 && int2 < int5) {
					this.gridSquares[int4][int1 + int2 * int5 + int3 * int5 * int5] = square;
				}
			}
		}
	}

	public IsoGridSquare getGridSquare(Double Double1, Double Double2, Double Double3) {
		return this.getGridSquare(Double1.intValue(), Double2.intValue(), Double3.intValue());
	}

	public IsoGridSquare getGridSquare(int int1, int int2, int int3) {
		if (GameServer.bServer) {
			return ServerMap.instance.getGridSquare(int1, int2, int3);
		} else {
			int int4 = IsoChunkMap.ChunkWidthInTiles;
			for (int int5 = 0; int5 < IsoPlayer.numPlayers; ++int5) {
				if (!this.ChunkMap[int5].ignore) {
					if (int3 == 0) {
						boolean boolean1 = false;
					}

					int int6 = int1 - this.ChunkMap[int5].getWorldXMinTiles();
					int int7 = int2 - this.ChunkMap[int5].getWorldYMinTiles();
					if (int3 < 8 && int3 >= 0 && int6 >= 0 && int6 < int4 && int7 >= 0 && int7 < int4) {
						IsoGridSquare square = ENABLE_SQUARE_CACHE ? this.gridSquares[int5][int6 + int7 * int4 + int3 * int4 * int4] : this.ChunkMap[int5].getGridSquareDirect(int6, int7, int3);
						if (square != null) {
							return square;
						}
					}
				}
			}

			return null;
		}
	}

	public void EnsureSurroundNotNull(int int1, int int2, int int3) {
		for (int int4 = -1; int4 <= 1; ++int4) {
			for (int int5 = -1; int5 <= 1; ++int5) {
				this.createNewGridSquare(int1 + int4, int2 + int5, int3, false);
			}
		}
	}

	public void DeleteAllMovingObjects() {
		this.ObjectList.clear();
	}

	@LuaMethod(name = "getMaxFloors")
	public int getMaxFloors() {
		return 8;
	}

	public KahluaTable getLuaObjectList() {
		KahluaTable kahluaTable = LuaManager.platform.newTable();
		LuaManager.env.rawset("Objects", kahluaTable);
		for (int int1 = 0; int1 < this.ObjectList.size(); ++int1) {
			kahluaTable.rawset(int1 + 1, this.ObjectList.get(int1));
		}

		return kahluaTable;
	}

	public int getHeightInTiles() {
		return this.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
	}

	public int getWidthInTiles() {
		return this.ChunkMap[IsoPlayer.getPlayerIndex()].getWidthInTiles();
	}

	public boolean isNull(int int1, int int2, int int3) {
		IsoGridSquare square = this.getGridSquare(int1, int2, int3);
		return square == null || !square.isFree(false);
	}

	public boolean IsZone(String string, int int1, int int2) {
		for (int int3 = 0; int3 < this.ZoneStack.size(); ++int3) {
			IsoCell.Zone zone = (IsoCell.Zone)this.ZoneStack.get(int3);
			if (zone.Name.equals(string) && int1 >= zone.X && int1 < zone.X + zone.W && int2 >= zone.Y && int2 < zone.Y + zone.H) {
				return true;
			}
		}

		return false;
	}

	public void pathFinderVisited(int int1, int int2, int int3) {
	}

	public void Remove(IsoMovingObject movingObject) {
		if (!(movingObject instanceof IsoPlayer) || ((IsoPlayer)movingObject).isDead()) {
			this.removeList.add(movingObject);
		}
	}

	boolean isBlocked(IsoGridSquare square, IsoGridSquare square2) {
		return square.room != square2.room;
	}

	private int CalculateColor(IsoGridSquare square, IsoGridSquare square2, IsoGridSquare square3, IsoGridSquare square4, int int1, int int2) {
		float float1 = 0.0F;
		float float2 = 0.0F;
		float float3 = 0.0F;
		float float4 = 1.0F;
		if (square4 == null) {
			return 0;
		} else {
			float float5 = 0.0F;
			boolean boolean1 = true;
			ColorInfo colorInfo;
			if (square != null && square4.room == square.room && square.getChunk() != null) {
				++float5;
				colorInfo = square.lighting[int2].lightInfo();
				float1 += colorInfo.r;
				float2 += colorInfo.g;
				float3 += colorInfo.b;
			}

			if (square2 != null && square4.room == square2.room && square2.getChunk() != null) {
				++float5;
				colorInfo = square2.lighting[int2].lightInfo();
				float1 += colorInfo.r;
				float2 += colorInfo.g;
				float3 += colorInfo.b;
			}

			if (square3 != null && square4.room == square3.room && square3.getChunk() != null) {
				++float5;
				colorInfo = square3.lighting[int2].lightInfo();
				float1 += colorInfo.r;
				float2 += colorInfo.g;
				float3 += colorInfo.b;
			}

			if (square4 != null) {
				++float5;
				colorInfo = square4.lighting[int2].lightInfo();
				float1 += colorInfo.r;
				float2 += colorInfo.g;
				float3 += colorInfo.b;
			}

			if (float5 != 0.0F) {
				float1 /= float5;
				float2 /= float5;
				float3 /= float5;
			}

			if (float1 > 1.0F) {
				float1 = 1.0F;
			}

			if (float2 > 1.0F) {
				float2 = 1.0F;
			}

			if (float3 > 1.0F) {
				float3 = 1.0F;
			}

			if (float1 < 0.0F) {
				float1 = 0.0F;
			}

			if (float2 < 0.0F) {
				float2 = 0.0F;
			}

			if (float3 < 0.0F) {
				float3 = 0.0F;
			}

			if (square4 != null) {
				square4.setVertLight(int1, (int)(float1 * 255.0F) << 0 | (int)(float2 * 255.0F) << 8 | (int)(float3 * 255.0F) << 16 | -16777216, int2);
				square4.setVertLight(int1 + 4, (int)(float1 * 255.0F) << 0 | (int)(float2 * 255.0F) << 8 | (int)(float3 * 255.0F) << 16 | -16777216, int2);
			}

			return int1;
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
			this.lightingThread = new Thread(new Runnable(){
				
				public void run() {
				}
			});
		}
	}

	public void render() {
		int int1 = IsoCamera.frameState.playerIndex;
		IsoPlayer player = IsoPlayer.players[int1];
		if (player.dirtyRecalcGridStackTime > 0.0F) {
			player.dirtyRecalcGridStack = true;
		} else {
			player.dirtyRecalcGridStack = false;
		}

		if (!PerformanceSettings.NewRoofHiding) {
			if (this.bHideFloors[int1] && this.unhideFloorsCounter[int1] > 0) {
				int int2 = this.unhideFloorsCounter[int1]--;
			}

			if (this.unhideFloorsCounter[int1] <= 0) {
				this.bHideFloors[int1] = false;
				this.unhideFloorsCounter[int1] = 60;
			}
		}

		int int3 = 8;
		if (int3 < 8) {
			++int3;
		}

		--this.recalcShading;
		byte byte1 = 0;
		byte byte2 = 0;
		int int4 = byte1 + IsoCamera.getOffscreenWidth(int1);
		int int5 = byte2 + IsoCamera.getOffscreenHeight(int1);
		float float1 = IsoUtils.XToIso((float)byte1, (float)byte2, 0.0F);
		float float2 = IsoUtils.YToIso((float)int4, (float)byte2, 0.0F);
		float float3 = IsoUtils.XToIso((float)int4, (float)int5, 6.0F);
		float float4 = IsoUtils.YToIso((float)byte1, (float)int5, 6.0F);
		this.minY = (int)float2;
		this.maxY = (int)float4;
		this.minX = (int)float1;
		this.maxX = (int)float3;
		this.minX -= 2;
		this.minY -= 2;
		this.maxZ = MaxHeight;
		if (IsoCamera.CamCharacter == null) {
			this.maxZ = 1;
		}

		boolean boolean1 = false;
		boolean1 = true;
		if (GameTime.instance.FPSMultiplier > 1.5F) {
			boolean1 = true;
		}

		if (this.minX != this.lastMinX || this.minY != this.lastMinY) {
			this.lightUpdateCount = 10;
		}

		++this.alt;
		if (this.alt == 3) {
			this.alt = 0;
		}

		IsoGridSquare square;
		IsoGridSquare square2;
		if (!PerformanceSettings.NewRoofHiding) {
			square = IsoCamera.CamCharacter == null ? null : IsoCamera.CamCharacter.getCurrentSquare();
			if (square != null) {
				square2 = this.getGridSquare((double)Math.round(IsoCamera.CamCharacter.getX()), (double)Math.round(IsoCamera.CamCharacter.getY()), (double)IsoCamera.CamCharacter.getZ());
				if (square2 != null && this.IsBehindStuff(square2)) {
					this.bHideFloors[int1] = true;
				}

				if (!this.bHideFloors[int1] && square.getProperties().Is(IsoFlagType.hidewalls) || !square.getProperties().Is(IsoFlagType.exterior)) {
					this.bHideFloors[int1] = true;
				}
			}

			if (this.bHideFloors[int1]) {
				this.maxZ = (int)IsoCamera.CamCharacter.getZ() + 1;
			}
		}

		if (PerformanceSettings.LightingFrameSkip < 3) {
			this.DrawStencilMask();
		}

		int int6;
		int int7;
		if (PerformanceSettings.LightingFrameSkip == 3) {
			int6 = IsoCamera.getOffscreenWidth(int1) / 2;
			int7 = IsoCamera.getOffscreenHeight(int1) / 2;
			short short1 = 409;
			int6 -= short1 / (2 / Core.TileScale);
			int7 -= short1 / (2 / Core.TileScale);
			this.StencilX1 = int6 - (int)IsoCamera.RightClickX[int1];
			this.StencilY1 = int7 - (int)IsoCamera.RightClickY[int1];
			this.StencilX2 = this.StencilX1 + short1 * Core.TileScale;
			this.StencilY2 = this.StencilY1 + short1 * Core.TileScale;
		}

		if (PerformanceSettings.NewRoofHiding && player.dirtyRecalcGridStack) {
			this.hidesOrphanStructuresAbove = int3;
			square = null;
			((ArrayList)this.otherOccluderBuildings.get(int1)).clear();
			if (this.otherOccluderBuildingsArr[int1] != null) {
				this.otherOccluderBuildingsArr[int1][0] = null;
			} else {
				this.otherOccluderBuildingsArr[int1] = new IsoBuilding[500];
			}

			if (IsoCamera.CamCharacter != null && IsoCamera.CamCharacter.getCurrentSquare() != null) {
				square2 = IsoCamera.CamCharacter.getCurrentSquare();
				int int8 = 10;
				if (this.ZombieList.size() < 10) {
					int8 = this.ZombieList.size();
				}

				if (this.nearestVisibleZombie[int1] != null) {
					if (this.nearestVisibleZombie[int1].bDead) {
						this.nearestVisibleZombie[int1] = null;
					} else {
						float float5 = this.nearestVisibleZombie[int1].x - IsoCamera.CamCharacter.x;
						float float6 = this.nearestVisibleZombie[int1].y - IsoCamera.CamCharacter.y;
						this.nearestVisibleZombieDistSqr[int1] = float5 * float5 + float6 * float6;
					}
				}

				IsoGridSquare square3;
				int int9;
				for (int9 = 0; int9 < int8; ++this.zombieScanCursor) {
					if (this.zombieScanCursor >= this.ZombieList.size()) {
						this.zombieScanCursor = 0;
					}

					IsoZombie zombie = (IsoZombie)this.ZombieList.get(this.zombieScanCursor);
					if (zombie != null) {
						square3 = zombie.getCurrentSquare();
						if (square3 != null && square2.z == square3.z && square3.getCanSee(int1)) {
							float float7;
							float float8;
							if (this.nearestVisibleZombie[int1] == null) {
								this.nearestVisibleZombie[int1] = zombie;
								float7 = this.nearestVisibleZombie[int1].x - IsoCamera.CamCharacter.x;
								float8 = this.nearestVisibleZombie[int1].y - IsoCamera.CamCharacter.y;
								this.nearestVisibleZombieDistSqr[int1] = float7 * float7 + float8 * float8;
							} else {
								float7 = zombie.x - IsoCamera.CamCharacter.x;
								float8 = zombie.y - IsoCamera.CamCharacter.y;
								float float9 = float7 * float7 + float8 * float8;
								if (float9 < this.nearestVisibleZombieDistSqr[int1]) {
									this.nearestVisibleZombie[int1] = zombie;
									this.nearestVisibleZombieDistSqr[int1] = float9;
								}
							}
						}
					}

					++int9;
				}

				IsoBuilding building;
				for (int9 = 0; int9 < 4; ++int9) {
					IsoPlayer player2 = IsoPlayer.players[int9];
					if (player2 != null && player2.getCurrentSquare() != null) {
						square3 = player2.getCurrentSquare();
						if (int9 == int1) {
							square = square3;
						}

						double double1 = (double)player2.x - Math.floor((double)player2.x);
						double double2 = (double)player2.y - Math.floor((double)player2.y);
						boolean boolean2 = double1 > double2;
						IsoDirections directions = IsoDirections.cardinalFromAngle(player2.angle);
						if (this.lastPlayerSquare[int9] != square3 || this.lastPlayerSquareHalf[int9] != boolean2 || this.lastPlayerCardinalDir[int9] != directions) {
							this.lastPlayerSquare[int9] = square3;
							this.lastPlayerSquareHalf[int9] = boolean2;
							this.lastPlayerCardinalDir[int9] = directions;
							building = square3.getBuilding();
							this.playerWindowPeeking[int9] = false;
							this.GetBuildingsInFrontOfCharacter((ArrayList)this.playerOccluderBuildings.get(int9), square3, boolean2);
							if (this.playerOccluderBuildingsArr[int1] == null) {
								this.playerOccluderBuildingsArr[int1] = new IsoBuilding[500];
							}

							this.playerHidesOrphanStructures[int9] = this.bOccludedByOrphanStructureFlag;
							if (building == null && !player2.bRemote) {
								building = this.GetPeekedInBuilding(square3, directions, 2);
								if (building != null) {
									this.playerWindowPeeking[int9] = true;
								}
							}

							if (building != null) {
								this.AddUniqueToBuildingList((ArrayList)this.playerOccluderBuildings.get(int9), building);
							}

							ArrayList arrayList = (ArrayList)this.playerOccluderBuildings.get(int9);
							for (int int10 = 0; int10 < arrayList.size(); ++int10) {
								IsoBuilding building2 = (IsoBuilding)arrayList.get(int10);
								this.playerOccluderBuildingsArr[int1][int10] = building2;
							}

							this.playerOccluderBuildingsArr[int1][arrayList.size()] = null;
						}

						if (int9 == int1 && square != null) {
							this.gridSquaresTempLeft.clear();
							this.gridSquaresTempRight.clear();
							this.GetSquaresAroundPlayerSquare(player2, square, this.gridSquaresTempLeft, this.gridSquaresTempRight);
							Iterator iterator = this.gridSquaresTempLeft.iterator();
							label425: while (true) {
								boolean[] booleanArray;
								IsoGridSquare square4;
								ArrayList arrayList2;
								int int11;
								do {
									do {
										if (!iterator.hasNext()) {
											iterator = this.gridSquaresTempRight.iterator();
											while (true) {
												do {
													do {
														if (!iterator.hasNext()) {
															ArrayList arrayList3 = (ArrayList)this.otherOccluderBuildings.get(int1);
															if (this.otherOccluderBuildingsArr[int1] == null) {
																this.otherOccluderBuildingsArr[int1] = new IsoBuilding[500];
															}

															for (int int12 = 0; int12 < arrayList3.size(); ++int12) {
																IsoBuilding building3 = (IsoBuilding)arrayList3.get(int12);
																this.otherOccluderBuildingsArr[int1][int12] = building3;
															}

															this.otherOccluderBuildingsArr[int1][arrayList3.size()] = null;
															break label425;
														}

														square4 = (IsoGridSquare)iterator.next();
													}											 while (!square4.getCanSee(int1));
												}										 while (square4.getBuilding() != null && square4.getBuilding() != square.getBuilding());

												arrayList2 = this.GetBuildingsInFrontOfMustSeeSquare(square4, IsoGridOcclusionData.OcclusionFilter.Left);
												for (int11 = 0; int11 < arrayList2.size(); ++int11) {
													this.AddUniqueToBuildingList((ArrayList)this.otherOccluderBuildings.get(int1), (IsoBuilding)arrayList2.get(int11));
												}

												booleanArray = this.playerHidesOrphanStructures;
												booleanArray[int1] |= this.bOccludedByOrphanStructureFlag;
											}
										}

										square4 = (IsoGridSquare)iterator.next();
									}							 while (!square4.getCanSee(int1));
								}						 while (square4.getBuilding() != null && square4.getBuilding() != square.getBuilding());

								arrayList2 = this.GetBuildingsInFrontOfMustSeeSquare(square4, IsoGridOcclusionData.OcclusionFilter.Right);
								for (int11 = 0; int11 < arrayList2.size(); ++int11) {
									this.AddUniqueToBuildingList((ArrayList)this.otherOccluderBuildings.get(int1), (IsoBuilding)arrayList2.get(int11));
								}

								booleanArray = this.playerHidesOrphanStructures;
								booleanArray[int1] |= this.bOccludedByOrphanStructureFlag;
							}
						}

						if (this.playerHidesOrphanStructures[int9] && this.hidesOrphanStructuresAbove > square3.getZ()) {
							this.hidesOrphanStructuresAbove = square3.getZ();
						}
					}
				}

				if (square != null && this.hidesOrphanStructuresAbove < square.getZ()) {
					this.hidesOrphanStructuresAbove = square.getZ();
				}

				boolean boolean3 = false;
				if (this.nearestVisibleZombie[int1] != null && this.nearestVisibleZombieDistSqr[int1] < 150.0F) {
					IsoGridSquare square5 = this.nearestVisibleZombie[int1].getCurrentSquare();
					if (square5 != null && square5.getCanSee(int1)) {
						double double3 = (double)this.nearestVisibleZombie[int1].x - Math.floor((double)this.nearestVisibleZombie[int1].x);
						double double4 = (double)this.nearestVisibleZombie[int1].y - Math.floor((double)this.nearestVisibleZombie[int1].y);
						boolean boolean4 = double3 > double4;
						boolean3 = true;
						if (this.lastZombieSquare[int1] != square5 || this.lastZombieSquareHalf[int1] != boolean4) {
							this.lastZombieSquare[int1] = square5;
							this.lastZombieSquareHalf[int1] = boolean4;
							this.GetBuildingsInFrontOfCharacter((ArrayList)this.zombieOccluderBuildings.get(int1), square5, boolean4);
							ArrayList arrayList4 = (ArrayList)this.zombieOccluderBuildings.get(int1);
							if (this.zombieOccluderBuildingsArr[int1] == null) {
								this.zombieOccluderBuildingsArr[int1] = new IsoBuilding[500];
							}

							for (int int13 = 0; int13 < arrayList4.size(); ++int13) {
								building = (IsoBuilding)arrayList4.get(int13);
								this.zombieOccluderBuildingsArr[int1][int13] = building;
							}

							this.zombieOccluderBuildingsArr[int1][arrayList4.size()] = null;
						}
					}
				}

				if (!boolean3) {
					((ArrayList)this.zombieOccluderBuildings.get(int1)).clear();
					if (this.zombieOccluderBuildingsArr[int1] != null) {
						this.zombieOccluderBuildingsArr[int1][0] = null;
					} else {
						this.zombieOccluderBuildingsArr[int1] = new IsoBuilding[500];
					}
				}
			} else {
				for (int7 = 0; int7 < 4; ++int7) {
					((ArrayList)this.playerOccluderBuildings.get(int7)).clear();
					if (this.playerOccluderBuildingsArr[int7] != null) {
						this.playerOccluderBuildingsArr[int7][0] = null;
					} else {
						this.playerOccluderBuildingsArr[int7] = new IsoBuilding[500];
					}

					this.lastPlayerSquare[int7] = null;
				}

				this.playerWindowPeeking[int1] = false;
				((ArrayList)this.zombieOccluderBuildings.get(int1)).clear();
				if (this.zombieOccluderBuildingsArr[int1] != null) {
					this.zombieOccluderBuildingsArr[int1][0] = null;
				} else {
					this.zombieOccluderBuildingsArr[int1] = new IsoBuilding[500];
				}

				this.lastZombieSquare[int1] = null;
			}
		}

		if (!PerformanceSettings.NewRoofHiding) {
			for (int6 = 0; int6 < IsoPlayer.numPlayers; ++int6) {
				this.playerWindowPeeking[int6] = false;
				IsoPlayer player3 = IsoPlayer.players[int6];
				if (player3 != null) {
					IsoBuilding building4 = player3.getCurrentBuilding();
					if (building4 == null) {
						IsoDirections directions2 = IsoDirections.cardinalFromAngle(player3.angle);
						building4 = this.GetPeekedInBuilding(player3.getCurrentSquare(), directions2, 2);
						if (building4 != null) {
							this.playerWindowPeeking[int6] = true;
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
			this.RenderTiles(int3);
		} catch (Exception exception) {
			this.bRendering = false;
			Logger.getLogger(GameApplet.class.getName()).log(Level.SEVERE, (String)null, exception);
		}

		this.bRendering = false;
		if (IsoGridSquare.getRecalcLightTime() < 0) {
			IsoGridSquare.setRecalcLightTime(60);
		}

		if (IsoGridSquare.getLightcache() <= 0) {
			IsoGridSquare.setLightcache(90);
		}

		for (int6 = 0; int6 < this.ObjectList.size(); ++int6) {
			IsoMovingObject movingObject = (IsoMovingObject)this.ObjectList.get(int6);
			movingObject.renderlast();
		}

		for (int6 = 0; int6 < this.StaticUpdaterObjectList.size(); ++int6) {
			IsoObject object = (IsoObject)this.StaticUpdaterObjectList.get(int6);
			object.renderlast();
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

	public void setRainAlpha(int int1) {
		this.rainAlphaMax = (float)int1 / 100.0F;
	}

	public void setRainIntensity(int int1) {
		this.rainIntensity = int1;
	}

	public void setRainSpeed(int int1) {
		this.rainSpeed = int1;
	}

	public void reloadRainTextures() {
		for (int int1 = 1; int1 <= 5; ++int1) {
			File file = new File("media/ui/rain" + int1 + ".png");
			if (this.rainFileTime[int1 - 1] != file.lastModified()) {
				this.rainFileTime[int1 - 1] = file.lastModified();
				try {
					RenderThread.borrowContext();
					ImageData imageData = new ImageData(file.getAbsolutePath());
					GL11.glBindTexture(3553, Texture.lastTextureID = this.rainTextures[int1 - 1].getID());
					GL11.glTexParameteri(3553, 10241, 9728);
					GL11.glTexParameteri(3553, 10240, 9728);
					short short1 = 6408;
					GL11.glTexImage2D(3553, 0, short1, this.rainTextures[int1 - 1].getWidthHW(), this.rainTextures[int1 - 1].getHeightHW(), 0, 6408, 5121, imageData.getData().getBuffer());
					RenderThread.returnContext();
				} catch (Exception exception) {
				}
			}
		}
	}

	private void GetBuildingsInFrontOfCharacter(ArrayList arrayList, IsoGridSquare square, boolean boolean1) {
		arrayList.clear();
		this.bOccludedByOrphanStructureFlag = false;
		if (square != null) {
			int int1 = square.getX();
			int int2 = square.getY();
			int int3 = square.getZ();
			if (!square.Has(IsoObjectType.stairsTN) && !square.Has(IsoObjectType.stairsTW)) {
				if (!square.Has(IsoObjectType.stairsMN) && !square.Has(IsoObjectType.stairsMW)) {
					if (square.Has(IsoObjectType.stairsBN) || square.Has(IsoObjectType.stairsBW)) {
						int1 -= 2;
						int2 -= 2;
					}
				} else {
					int1 -= 2;
					int2 -= 2;
				}
			} else {
				int1 -= 3;
				int2 -= 3;
			}

			this.GetBuildingsInFrontOfCharacterSquare(int1, int2, int3, boolean1, arrayList);
			if (int3 < MaxHeight) {
				this.GetBuildingsInFrontOfCharacterSquare(int1 - 1 + 3, int2 - 1 + 3, int3 + 1, boolean1, arrayList);
				this.GetBuildingsInFrontOfCharacterSquare(int1 - 2 + 3, int2 - 2 + 3, int3 + 1, boolean1, arrayList);
				if (boolean1) {
					this.GetBuildingsInFrontOfCharacterSquare(int1 + 3, int2 - 1 + 3, int3 + 1, !boolean1, arrayList);
					this.GetBuildingsInFrontOfCharacterSquare(int1 - 1 + 3, int2 - 2 + 3, int3 + 1, !boolean1, arrayList);
				} else {
					this.GetBuildingsInFrontOfCharacterSquare(int1 - 1 + 3, int2 + 3, int3 + 1, !boolean1, arrayList);
					this.GetBuildingsInFrontOfCharacterSquare(int1 - 2 + 3, int2 - 1 + 3, int3 + 1, !boolean1, arrayList);
				}
			}
		}
	}

	private void GetBuildingsInFrontOfCharacterSquare(int int1, int int2, int int3, boolean boolean1, ArrayList arrayList) {
		IsoGridSquare square = this.getGridSquare(int1, int2, int3);
		if (square == null) {
			if (int3 < MaxHeight) {
				this.GetBuildingsInFrontOfCharacterSquare(int1 + 3, int2 + 3, int3 + 1, boolean1, arrayList);
			}
		} else {
			IsoGridOcclusionData gridOcclusionData = square.getOrCreateOcclusionData();
			IsoGridOcclusionData.OcclusionFilter occlusionFilter = boolean1 ? IsoGridOcclusionData.OcclusionFilter.Right : IsoGridOcclusionData.OcclusionFilter.Left;
			this.bOccludedByOrphanStructureFlag |= gridOcclusionData.getCouldBeOccludedByOrphanStructures(occlusionFilter);
			ArrayList arrayList2 = gridOcclusionData.getBuildingsCouldBeOccluders(occlusionFilter);
			for (int int4 = 0; int4 < arrayList2.size(); ++int4) {
				this.AddUniqueToBuildingList(arrayList, (IsoBuilding)arrayList2.get(int4));
			}
		}
	}

	private ArrayList GetBuildingsInFrontOfMustSeeSquare(IsoGridSquare square, IsoGridOcclusionData.OcclusionFilter occlusionFilter) {
		IsoGridOcclusionData gridOcclusionData = square.getOrCreateOcclusionData();
		this.bOccludedByOrphanStructureFlag = gridOcclusionData.getCouldBeOccludedByOrphanStructures(IsoGridOcclusionData.OcclusionFilter.All);
		return gridOcclusionData.getBuildingsCouldBeOccluders(occlusionFilter);
	}

	private IsoBuilding GetPeekedInBuilding(IsoGridSquare square, IsoDirections directions, int int1) {
		if (square == null) {
			return null;
		} else {
			IsoGridSquare square2;
			IsoBuilding building;
			if (directions == IsoDirections.N) {
				if (!square.visionMatrix[1][0][1]) {
					square2 = square.nav[IsoDirections.N.index()];
					if (square2 != null) {
						building = square2.getBuilding();
						if (building != null) {
							return building;
						}

						if (int1 > 1) {
							return this.GetPeekedInBuilding(square2, directions, int1 - 1);
						}
					}
				}
			} else if (directions == IsoDirections.W) {
				if (!square.visionMatrix[0][1][1]) {
					square2 = square.nav[IsoDirections.W.index()];
					if (square2 != null) {
						building = square2.getBuilding();
						if (building != null) {
							return building;
						}

						if (int1 > 1) {
							return this.GetPeekedInBuilding(square2, directions, int1 - 1);
						}
					}
				}
			} else if (directions == IsoDirections.S) {
				if (!square.visionMatrix[1][2][1]) {
					square2 = square.nav[IsoDirections.S.index()];
					if (square2 != null) {
						building = square2.getBuilding();
						if (building != null) {
							return building;
						}

						if (int1 > 1) {
							return this.GetPeekedInBuilding(square2, directions, int1 - 1);
						}
					}
				}
			} else if (directions == IsoDirections.E && !square.visionMatrix[2][1][1]) {
				square2 = square.nav[IsoDirections.E.index()];
				if (square2 != null) {
					building = square2.getBuilding();
					if (building != null) {
						return building;
					}

					if (int1 > 1) {
						return this.GetPeekedInBuilding(square2, directions, int1 - 1);
					}
				}
			}

			return null;
		}
	}

	void GetSquaresAroundPlayerSquare(IsoPlayer player, IsoGridSquare square, ArrayList arrayList, ArrayList arrayList2) {
		float float1 = player.x - 4.0F;
		float float2 = player.y - 4.0F;
		int int1 = (int)float1;
		int int2 = (int)float2;
		int int3 = square.getZ();
		for (int int4 = int2; int4 < int2 + 10; ++int4) {
			for (int int5 = int1; int5 < int1 + 10; ++int5) {
				if ((int5 >= (int)player.x || int4 >= (int)player.y) && (int5 != (int)player.x || int4 != (int)player.y)) {
					float float3 = (float)int5 - player.x;
					float float4 = (float)int4 - player.y;
					if ((double)float4 < (double)float3 + 4.5 && (double)float4 > (double)float3 - 4.5) {
						IsoGridSquare square2 = this.getGridSquare(int5, int4, int3);
						if (square2 != null) {
							if (float4 >= float3) {
								arrayList.add(square2);
							}

							if (float4 <= float3) {
								arrayList2.add(square2);
							}
						}
					}
				}
			}
		}
	}

	private boolean IsBehindStuff(IsoGridSquare square) {
		if (!square.getProperties().Is(IsoFlagType.exterior)) {
			return true;
		} else {
			for (int int1 = 1; int1 < 8 && square.getZ() + int1 < MaxHeight; ++int1) {
				for (int int2 = -5; int2 <= 6; ++int2) {
					for (int int3 = -5; int3 <= 6; ++int3) {
						if (int3 >= int2 - 5 && int3 <= int2 + 5) {
							IsoGridSquare square2 = this.getGridSquare(square.getX() + int3 + int1 * 3, square.getY() + int2 + int1 * 3, square.getZ() + int1);
							if (square2 != null && !square2.getObjects().isEmpty()) {
								if (int1 != 1 || square2.getObjects().size() != 1) {
									return true;
								}

								IsoObject object = (IsoObject)square2.getObjects().get(0);
								if (object.sprite == null || object.sprite.name == null || !object.sprite.name.startsWith("lighting_outdoor")) {
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
		IsoDirections directions = IsoDirections.N;
		float float1 = UIManager.getPickedTileLocal().x;
		float float2 = UIManager.getPickedTileLocal().y;
		float float3 = 0.5F - Math.abs(0.5F - float2);
		float float4 = 0.5F - Math.abs(0.5F - float1);
		if (float1 > 0.5F && float4 < float3) {
			directions = IsoDirections.E;
		} else if (float2 > 0.5F && float4 > float3) {
			directions = IsoDirections.S;
		} else if (float1 < 0.5F && float4 < float3) {
			directions = IsoDirections.W;
		} else if (float2 < 0.5F && float4 > float3) {
			directions = IsoDirections.N;
		}

		return directions;
	}

	public void renderListClear() {
		for (int int1 = 0; int1 < this.RenderJobsArray.size(); ++int1) {
			((ArrayList)this.RenderJobsArray.get(int1)).clear();
		}
	}

	public void update() {
		IsoObject.alphaStep = 0.044999998F * (30.0F / (float)PerformanceSettings.LockFPS);
		IsoSprite.alphaStep = 0.075F * (30.0F / (float)PerformanceSettings.LockFPS);
		++IsoGridSquare.gridSquareCacheEmptyTimer;
		this.ProcessSpottedRooms();
		if (!GameServer.bServer) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				if (IsoPlayer.players[int1] != null && (!IsoPlayer.players[int1].isDead() || IsoPlayer.players[int1].ReanimatedCorpse != null)) {
					IsoPlayer.instance = IsoPlayer.players[int1];
					IsoCamera.CamCharacter = IsoPlayer.players[int1];
					this.ChunkMap[int1].update();
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

	void renderListAdd(IsoObject object) {
		Texture texture = object.getCurrentFrameTex();
		boolean boolean1 = false;
		int int1;
		if (texture == null) {
			int1 = 4999;
		} else {
			int1 = texture.getID();
		}

		if (this.RenderJobsMapArray.containsKey(int1)) {
			int1 = (Integer)this.RenderJobsMapArray.get(int1);
		} else {
			this.RenderJobsMapArray.put(int1, this.RenderJobsArray.size());
			int1 = this.RenderJobsArray.size();
		}

		if (this.RenderJobsArray.size() <= int1) {
			this.RenderJobsArray.add(new ArrayList());
		}

		((ArrayList)this.RenderJobsArray.get(int1)).add(object);
	}

	IsoGridSquare getRandomFreeTile() {
		IsoGridSquare square = null;
		boolean boolean1 = true;
		do {
			boolean1 = true;
			square = this.getGridSquare(Rand.Next(this.width), Rand.Next(this.height), 0);
			if (square == null) {
				boolean1 = false;
			} else if (!square.isFree(false)) {
				boolean1 = false;
			} else if (!square.getProperties().Is(IsoFlagType.solid) && !square.getProperties().Is(IsoFlagType.solidtrans)) {
				if (square.getMovingObjects().size() > 0) {
					boolean1 = false;
				} else if (!square.Has(IsoObjectType.stairsBN) && !square.Has(IsoObjectType.stairsMN) && !square.Has(IsoObjectType.stairsTN)) {
					if (square.Has(IsoObjectType.stairsBW) || square.Has(IsoObjectType.stairsMW) || square.Has(IsoObjectType.stairsTW)) {
						boolean1 = false;
					}
				} else {
					boolean1 = false;
				}
			} else {
				boolean1 = false;
			}
		} while (!boolean1);

		return square;
	}

	IsoGridSquare getRandomOutdoorFreeTile() {
		IsoGridSquare square = null;
		boolean boolean1 = true;
		do {
			boolean1 = true;
			square = this.getGridSquare(Rand.Next(this.width), Rand.Next(this.height), 0);
			if (square == null) {
				boolean1 = false;
			} else if (!square.isFree(false)) {
				boolean1 = false;
			} else if (square.getRoom() != null) {
				boolean1 = false;
			} else if (!square.getProperties().Is(IsoFlagType.solid) && !square.getProperties().Is(IsoFlagType.solidtrans)) {
				if (square.getMovingObjects().size() > 0) {
					boolean1 = false;
				} else if (!square.Has(IsoObjectType.stairsBN) && !square.Has(IsoObjectType.stairsMN) && !square.Has(IsoObjectType.stairsTN)) {
					if (square.Has(IsoObjectType.stairsBW) || square.Has(IsoObjectType.stairsMW) || square.Has(IsoObjectType.stairsTW)) {
						boolean1 = false;
					}
				} else {
					boolean1 = false;
				}
			} else {
				boolean1 = false;
			}
		} while (!boolean1);

		return square;
	}

	public IsoGridSquare getRandomFreeTileInRoom() {
		Stack stack = new Stack();
		for (int int1 = 0; int1 < this.RoomList.size(); ++int1) {
			if (((IsoRoom)this.RoomList.get(int1)).TileList.size() > 9 && !((IsoRoom)this.RoomList.get(int1)).Exits.isEmpty() && ((IsoGridSquare)((IsoRoom)this.RoomList.get(int1)).TileList.get(0)).getProperties().Is(IsoFlagType.solidfloor)) {
				stack.add(this.RoomList.get(int1));
			}
		}

		if (stack.isEmpty()) {
			return null;
		} else {
			IsoRoom room = (IsoRoom)stack.get(Rand.Next(stack.size()));
			return room.getFreeTile();
		}
	}

	public void roomSpotted(IsoRoom room) {
		synchronized (this.SpottedRooms) {
			if (!this.SpottedRooms.contains(room)) {
				this.SpottedRooms.push(room);
			}
		}
	}

	public void ProcessSpottedRooms() {
		synchronized (this.SpottedRooms) {
			for (int int1 = 0; int1 < this.SpottedRooms.size(); ++int1) {
				IsoRoom room = (IsoRoom)this.SpottedRooms.get(int1);
				LuaEventManager.triggerEvent("OnSeeNewRoom", room);
				VirtualZombieManager.instance.roomSpotted(room);
				if (!GameClient.bClient && !Core.bLastStand && ("shed".equals(room.def.name) || "garagestorage".equals(room.def.name) || "storageunit".equals(room.def.name))) {
					int int2 = 7;
					if ("shed".equals(room.def.name) || "garagestorage".equals(room.def.name)) {
						int2 = 4;
					}

					switch (SandboxOptions.instance.GeneratorSpawning.getValue()) {
					case 1: 
						int2 += 3;
						break;
					
					case 2: 
						int2 += 2;
					
					case 3: 
					
					default: 
						break;
					
					case 4: 
						int2 -= 2;
						break;
					
					case 5: 
						int2 -= 3;
					
					}

					if (Rand.Next(int2) == 0) {
						IsoGridSquare square = room.getRandomFreeSquare();
						if (square != null) {
							IsoGenerator generator = new IsoGenerator(InventoryItemFactory.CreateItem("Base.Generator"), this, square);
							if (GameServer.bServer) {
								generator.transmitCompleteItemToClients();
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

	public void save(DataOutputStream dataOutputStream, boolean boolean1) throws IOException {
		while (ChunkSaveWorker.instance.bSaving) {
			try {
				Thread.sleep(30L);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}

		if (SliceY.SliceBuffer == null) {
			SliceY.SliceBuffer = ByteBuffer.allocate(10000000);
		}

		int int1;
		for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			this.ChunkMap[int1].Save();
		}

		IsoChunkMap.DoSave();
		dataOutputStream.writeInt(this.width);
		dataOutputStream.writeInt(this.height);
		dataOutputStream.writeInt(MaxHeight);
		dataOutputStream.writeInt(this.ZoneStack.size());
		for (int1 = 0; int1 < this.ZoneStack.size(); ++int1) {
			IsoCell.Zone zone = (IsoCell.Zone)this.ZoneStack.get(int1);
			dataOutputStream.writeInt(zone.X);
			dataOutputStream.writeInt(zone.Y);
			dataOutputStream.writeInt(zone.W);
			dataOutputStream.writeInt(zone.H);
			GameWindow.WriteString(dataOutputStream, zone.Name);
		}

		ArrayList arrayList = new ArrayList();
		IsoLightSource lightSource;
		int int2;
		for (int2 = 0; int2 < this.LamppostPositions.size(); ++int2) {
			lightSource = (IsoLightSource)this.LamppostPositions.get(int2);
			if (!lightSource.switches.isEmpty()) {
			}
		}

		dataOutputStream.writeInt(arrayList.size());
		for (int2 = 0; int2 < arrayList.size(); ++int2) {
			lightSource = (IsoLightSource)arrayList.get(int2);
			if (lightSource.switches.isEmpty()) {
				dataOutputStream.writeInt(lightSource.x);
			}

			dataOutputStream.writeInt(lightSource.y);
			dataOutputStream.writeInt(lightSource.z);
			dataOutputStream.writeInt(lightSource.radius);
			dataOutputStream.writeFloat(lightSource.r);
			dataOutputStream.writeFloat(lightSource.g);
			dataOutputStream.writeFloat(lightSource.b);
			dataOutputStream.writeBoolean(lightSource.bActive);
			dataOutputStream.writeBoolean(lightSource.bWasActive);
		}

		try {
			ChunkSaveWorker.instance.SaveContainers();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_t.bin");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		dataOutputStream = new DataOutputStream(new BufferedOutputStream(fileOutputStream));
		GameTime.instance.save(dataOutputStream);
		dataOutputStream.flush();
		dataOutputStream.close();
		IsoWorld.instance.MetaGrid.save();
		if (IsoPlayer.players[0] != null && !IsoPlayer.players[0].isDead()) {
			this.savePlayer();
		} else {
			File file2 = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_p.bin");
			if (file2.exists()) {
				file2.delete();
			}
		}

		for (int int3 = 1; int3 < IsoPlayer.numPlayers; ++int3) {
			IsoPlayer player = IsoPlayer.players[int3];
			if (player != null && !player.isDead()) {
				String string = player.SaveFileName;
				if (string == null) {
					string = IsoPlayer.getUniqueFileName();
				}

				player.save(string);
			}
		}

		try {
			ChunkSaveWorker.instance.SaveContainers();
		} catch (IOException ioException2) {
			ioException2.printStackTrace();
		}

		ReanimatedPlayers.instance.saveReanimatedPlayers();
	}

	public boolean LoadPlayer(int int1) throws FileNotFoundException, IOException {
		File file = new File(GameWindow.getGameModeCacheDir() + File.separator + Core.GameSaveWorld + File.separator + "map_p.bin");
		if (!file.exists()) {
			return false;
		} else {
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			synchronized (SliceY.SliceBuffer) {
				SliceY.SliceBuffer.rewind();
				bufferedInputStream.read(SliceY.SliceBuffer.array());
				SliceY.SliceBuffer.rewind();
				byte byte1 = SliceY.SliceBuffer.get();
				byte byte2 = SliceY.SliceBuffer.get();
				byte byte3 = SliceY.SliceBuffer.get();
				byte byte4 = SliceY.SliceBuffer.get();
				if (byte1 == 80 && byte2 == 76 && byte3 == 89 && byte4 == 82) {
					int1 = SliceY.SliceBuffer.getInt();
				} else {
					SliceY.SliceBuffer.rewind();
				}

				if (int1 >= 69) {
					String string = GameWindow.ReadString(SliceY.SliceBuffer);
					if (GameClient.bClient && int1 < 71) {
						string = ServerOptions.instance.ServerPlayerID.getValue();
					}

					if (GameClient.bClient && !IsoPlayer.isServerPlayerIDValid(string)) {
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

				IsoPlayer.getInstance().load(SliceY.SliceBuffer, int1);
				fileInputStream.close();
				ChunkSaveWorker.instance.LoadContainers();
				return true;
			}
		}
	}

	public void load(DataInputStream dataInputStream, boolean boolean1) throws FileNotFoundException, IOException, ClassNotFoundException {
		int int1 = dataInputStream.readInt();
		int int2;
		int int3;
		int int4;
		int int5;
		int int6;
		for (int2 = 0; int2 < int1; ++int2) {
			int3 = dataInputStream.readInt();
			int4 = dataInputStream.readInt();
			int5 = dataInputStream.readInt();
			int6 = dataInputStream.readInt();
			String string = GameWindow.ReadString(dataInputStream);
			this.ZoneStack.add(new IsoCell.Zone(string, int3, int4, int5, int6, 0));
		}

		int2 = dataInputStream.readInt();
		for (int3 = 0; int3 < int2; ++int3) {
			int4 = dataInputStream.readInt();
			int5 = dataInputStream.readInt();
			int6 = dataInputStream.readInt();
			int int7 = dataInputStream.readInt();
			float float1 = dataInputStream.readFloat();
			float float2 = dataInputStream.readFloat();
			float float3 = dataInputStream.readFloat();
			boolean boolean2 = dataInputStream.readBoolean();
			boolean boolean3 = dataInputStream.readBoolean();
			IsoLightSource lightSource = new IsoLightSource(int4, int5, int6, float1, float2, float3, int7);
			lightSource.bActive = boolean2;
			lightSource.bWasActive = boolean3;
			this.LamppostPositions.add(lightSource);
		}

		if (GameServer.bServer) {
			boolean1 = false;
		}
	}

	public IsoGridSquare getRelativeGridSquare(int int1, int int2, int int3) {
		int int4 = this.ChunkMap[0].getWorldXMin();
		IsoChunkMap chunkMap = this.ChunkMap[0];
		int int5 = int4 * 10;
		int4 = this.ChunkMap[0].getWorldYMin();
		chunkMap = this.ChunkMap[0];
		int int6 = int4 * 10;
		int1 += int5;
		int2 += int6;
		return this.getGridSquare(int1, int2, int3);
	}

	public IsoGridSquare createNewGridSquare(int int1, int int2, int int3, boolean boolean1) {
		if (!IsoWorld.instance.isValidSquare(int1, int2, int3)) {
			return null;
		} else {
			IsoGridSquare square = this.getGridSquare(int1, int2, int3);
			if (square != null) {
				return square;
			} else {
				if (GameServer.bServer) {
					int int4 = int1 / 10;
					int int5 = int2 / 10;
					if (ServerMap.instance.getChunk(int4, int5) != null) {
						square = IsoGridSquare.getNew(this, (SliceY)null, int1, int2, int3);
						ServerMap.instance.setGridSquare(int1, int2, int3, square);
					}
				} else if (this.getChunkForGridSquare(int1, int2, int3) != null) {
					square = IsoGridSquare.getNew(this, (SliceY)null, int1, int2, int3);
					this.ConnectNewSquare(square, true);
				}

				if (square != null && boolean1) {
					square.RecalcAllWithNeighbours(true);
				}

				return square;
			}
		}
	}

	public IsoGridSquare getGridSquareDirect(int int1, int int2, int int3, int int4) {
		int int5 = IsoChunkMap.ChunkWidthInTiles;
		return ENABLE_SQUARE_CACHE ? this.gridSquares[int4][int1 + int2 * int5 + int3 * int5 * int5] : this.ChunkMap[int4].getGridSquareDirect(int1, int2, int3);
	}

	public boolean isInChunkMap(int int1, int int2) {
		for (int int3 = 0; int3 < IsoPlayer.numPlayers; ++int3) {
			int int4 = this.ChunkMap[int3].getWorldXMinTiles();
			int int5 = this.ChunkMap[int3].getWorldXMaxTiles();
			int int6 = this.ChunkMap[int3].getWorldYMinTiles();
			int int7 = this.ChunkMap[int3].getWorldYMaxTiles();
			if (int1 >= int4 && int1 < int5 && int2 >= int6 && int2 < int7) {
				return true;
			}
		}

		return false;
	}

	public ArrayList getProcessIsoObjectRemove() {
		return this.ProcessIsoObjectRemove;
	}

	public void setProcessIsoObjectRemove(ArrayList arrayList) {
		this.ProcessIsoObjectRemove = arrayList;
	}

	public void checkHaveRoof(int int1, int int2) {
		boolean boolean1 = false;
		for (int int3 = 8; int3 >= 0; --int3) {
			IsoGridSquare square = this.getGridSquare(int1, int2, int3);
			if (square != null) {
				if (boolean1 != square.haveRoof) {
					square.haveRoof = boolean1;
					square.RecalcAllWithNeighbours(true);
				}

				if (square.Is(IsoFlagType.solidfloor)) {
					boolean1 = true;
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

	public void addHeatSource(IsoHeatSource heatSource) {
		if (!GameServer.bServer) {
			if (this.heatSources.contains(heatSource)) {
				DebugLog.log("ERROR addHeatSource called again with the same HeatSource");
			} else {
				this.heatSources.add(heatSource);
			}
		}
	}

	public void removeHeatSource(IsoHeatSource heatSource) {
		if (!GameServer.bServer) {
			this.heatSources.remove(heatSource);
		}
	}

	public void updateHeatSources() {
		if (!GameServer.bServer) {
			for (int int1 = this.heatSources.size() - 1; int1 >= 0; --int1) {
				IsoHeatSource heatSource = (IsoHeatSource)this.heatSources.get(int1);
				if (!heatSource.isInBounds()) {
					this.heatSources.remove(int1);
				}
			}
		}
	}

	public int getHeatSourceTemperature(int int1, int int2, int int3) {
		int int4 = 0;
		for (int int5 = 0; int5 < this.heatSources.size(); ++int5) {
			IsoHeatSource heatSource = (IsoHeatSource)this.heatSources.get(int5);
			if (heatSource.getZ() == int3) {
				float float1 = IsoUtils.DistanceToSquared((float)int1, (float)int2, (float)heatSource.getX(), (float)heatSource.getY());
				if (float1 < (float)(heatSource.getRadius() * heatSource.getRadius())) {
					LosUtil.TestResults testResults = LosUtil.lineClear(this, heatSource.getX(), heatSource.getY(), heatSource.getZ(), int1, int2, int3, false);
					if (testResults == LosUtil.TestResults.Clear || testResults == LosUtil.TestResults.ClearThroughOpenDoor) {
						int4 = (int)((double)int4 + (double)heatSource.getTemperature() * (1.0 - Math.sqrt((double)float1) / (double)heatSource.getRadius()));
					}
				}
			}
		}

		return int4;
	}

	public float getHeatSourceHighestTemperature(float float1, int int1, int int2, int int3) {
		float float2 = float1;
		float float3 = float1;
		float float4 = 0.0F;
		IsoGridSquare square = null;
		float float5 = 0.0F;
		for (int int4 = 0; int4 < this.heatSources.size(); ++int4) {
			IsoHeatSource heatSource = (IsoHeatSource)this.heatSources.get(int4);
			if (heatSource.getZ() == int3) {
				float float6 = IsoUtils.DistanceToSquared((float)int1, (float)int2, (float)heatSource.getX(), (float)heatSource.getY());
				square = this.getGridSquare(heatSource.getX(), heatSource.getY(), heatSource.getZ());
				float5 = 0.0F;
				if (square != null) {
					if (!square.isInARoom()) {
						float5 = float2 - 30.0F;
						if (float5 < -15.0F) {
							float5 = -15.0F;
						} else if (float5 > 5.0F) {
							float5 = 5.0F;
						}
					} else {
						float5 = float2 - 30.0F;
						if (float5 < -7.0F) {
							float5 = -7.0F;
						} else if (float5 > 7.0F) {
							float5 = 7.0F;
						}
					}
				}

				float4 = ClimateManager.lerp((float)(1.0 - Math.sqrt((double)float6) / (double)heatSource.getRadius()), float2, (float)heatSource.getTemperature() + float5);
				if (!(float4 <= float3) && float6 < (float)(heatSource.getRadius() * heatSource.getRadius())) {
					LosUtil.TestResults testResults = LosUtil.lineClear(this, heatSource.getX(), heatSource.getY(), heatSource.getZ(), int1, int2, int3, false);
					if (testResults == LosUtil.TestResults.Clear || testResults == LosUtil.TestResults.ClearThroughOpenDoor) {
						float3 = float4;
					}
				}
			}
		}

		return float3;
	}

	public void putInVehicle(IsoGameCharacter gameCharacter) {
		if (gameCharacter != null && gameCharacter.savedVehicleSeat != -1) {
			int int1 = ((int)gameCharacter.getX() - 4) / 10;
			int int2 = ((int)gameCharacter.getY() - 4) / 10;
			int int3 = ((int)gameCharacter.getX() + 4) / 10;
			int int4 = ((int)gameCharacter.getY() + 4) / 10;
			for (int int5 = int2; int5 <= int4; ++int5) {
				for (int int6 = int1; int6 <= int3; ++int6) {
					IsoChunk chunk = this.getChunkForGridSquare(int6 * 10, int5 * 10, (int)gameCharacter.getZ());
					if (chunk != null) {
						for (int int7 = 0; int7 < chunk.vehicles.size(); ++int7) {
							BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int7);
							if ((int)baseVehicle.getZ() == (int)gameCharacter.getZ() && IsoUtils.DistanceToSquared(baseVehicle.getX(), baseVehicle.getY(), gameCharacter.savedVehicleX, gameCharacter.savedVehicleY) < 0.010000001F) {
								if (baseVehicle.VehicleID == -1) {
									return;
								}

								VehicleScript.Position position = baseVehicle.getPassengerPosition(gameCharacter.savedVehicleSeat, "inside");
								if (position != null && !baseVehicle.isSeatOccupied(gameCharacter.savedVehicleSeat)) {
									baseVehicle.enter(gameCharacter.savedVehicleSeat, gameCharacter, position.offset);
									LuaEventManager.triggerEvent("OnEnterVehicle", gameCharacter);
									if (baseVehicle.getCharacter(gameCharacter.savedVehicleSeat) == gameCharacter && gameCharacter.savedVehicleRunning) {
										baseVehicle.resumeRunningAfterLoad();
									}

									gameCharacter.savedVehicleSeat = -1;
									gameCharacter.savedVehicleRunning = false;
								}

								return;
							}
						}
					}
				}
			}
		}
	}

	@Deprecated
	public void resumeVehicleSounds(IsoGameCharacter gameCharacter) {
		if (gameCharacter != null && gameCharacter.savedVehicleSeat != -1) {
			int int1 = ((int)gameCharacter.getX() - 4) / 10;
			int int2 = ((int)gameCharacter.getY() - 4) / 10;
			int int3 = ((int)gameCharacter.getX() + 4) / 10;
			int int4 = ((int)gameCharacter.getY() + 4) / 10;
			for (int int5 = int2; int5 <= int4; ++int5) {
				for (int int6 = int1; int6 <= int3; ++int6) {
					IsoChunk chunk = this.getChunkForGridSquare(int6 * 10, int5 * 10, (int)gameCharacter.getZ());
					if (chunk != null) {
						for (int int7 = 0; int7 < chunk.vehicles.size(); ++int7) {
							BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int7);
							if (baseVehicle.lightbarSirenMode.isEnable()) {
								baseVehicle.setLightbarSirenMode(baseVehicle.lightbarSirenMode.get());
							}
						}
					}
				}
			}
		}
	}

	private void AddUniqueToBuildingList(ArrayList arrayList, IsoBuilding building) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			if (arrayList.get(int1) == building) {
				return;
			}
		}

		arrayList.add(building);
	}

	public static class Zone {
		public int H;
		public boolean HasHeight = false;
		public String Name;
		public int W;
		public int X;
		public int Y;
		public int Z = 0;

		public Zone(String string, int int1, int int2, int int3, int int4, int int5) {
			this.Name = string;
			this.X = int1;
			this.Y = int2;
			this.W = int3;
			this.H = int4;
			this.Z = int5;
			this.HasHeight = true;
		}

		public int getH() {
			return this.H;
		}

		public void setH(int int1) {
			this.H = int1;
		}

		public boolean isHasHeight() {
			return this.HasHeight;
		}

		public void setHasHeight(boolean boolean1) {
			this.HasHeight = boolean1;
		}

		public String getName() {
			return this.Name;
		}

		public void setName(String string) {
			this.Name = string;
		}

		public int getW() {
			return this.W;
		}

		public void setW(int int1) {
			this.W = int1;
		}

		public int getX() {
			return this.X;
		}

		public void setX(int int1) {
			this.X = int1;
		}

		public int getY() {
			return this.Y;
		}

		public void setY(int int1) {
			this.Y = int1;
		}

		public int getZ() {
			return this.Z;
		}

		public void setZ(int int1) {
			this.Z = int1;
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

		public SnowGrid(int int1) {
			this.grid = new Texture[this.w][this.h][2];
			this.gridType = new byte[this.w][this.h][2];
			this.init(int1);
		}

		public IsoCell.SnowGrid init(int int1) {
			int int2;
			int int3;
			if (!IsoCell.this.hasSetupSnowGrid) {
				IsoCell.this.snowNoise2D = new Noise2D();
				IsoCell.this.snowNoise2D.addLayer(16, 0.5F, 3.0F);
				IsoCell.this.snowNoise2D.addLayer(32, 2.0F, 5.0F);
				IsoCell.this.snowNoise2D.addLayer(64, 5.0F, 8.0F);
				byte byte1 = 0;
				byte byte2 = (byte)(byte1 + 1);
				IsoCell.this.snowGridTiles_Square = IsoCell.this.new SnowGridTiles(byte1);
				byte byte3 = 40;
				for (int2 = 0; int2 < 4; ++int2) {
					IsoCell.this.snowGridTiles_Square.add(Texture.getSharedTexture("e_newsnow_ground_1_" + (byte3 + int2)));
				}

				IsoCell.this.snowGridTiles_Enclosed = IsoCell.this.new SnowGridTiles(byte2++);
				byte3 = 0;
				for (int2 = 0; int2 < 4; ++int2) {
					IsoCell.this.snowGridTiles_Enclosed.add(Texture.getSharedTexture("e_newsnow_ground_1_" + (byte3 + int2)));
				}

				IsoCell.this.snowGridTiles_Cove = new IsoCell.SnowGridTiles[4];
				for (int2 = 0; int2 < 4; ++int2) {
					IsoCell.this.snowGridTiles_Cove[int2] = IsoCell.this.new SnowGridTiles(byte2++);
					if (int2 == 0) {
						byte3 = 7;
					}

					if (int2 == 2) {
						byte3 = 4;
					}

					if (int2 == 1) {
						byte3 = 5;
					}

					if (int2 == 3) {
						byte3 = 6;
					}

					for (int3 = 0; int3 < 3; ++int3) {
						IsoCell.this.snowGridTiles_Cove[int2].add(Texture.getSharedTexture("e_newsnow_ground_1_" + (byte3 + int3 * 4)));
					}
				}

				IsoCell.this.snowGridTiles_Edge = new IsoCell.SnowGridTiles[4];
				for (int2 = 0; int2 < 4; ++int2) {
					IsoCell.this.snowGridTiles_Edge[int2] = IsoCell.this.new SnowGridTiles(byte2++);
					if (int2 == 0) {
						byte3 = 16;
					}

					if (int2 == 2) {
						byte3 = 18;
					}

					if (int2 == 1) {
						byte3 = 17;
					}

					if (int2 == 3) {
						byte3 = 19;
					}

					for (int3 = 0; int3 < 3; ++int3) {
						IsoCell.this.snowGridTiles_Edge[int2].add(Texture.getSharedTexture("e_newsnow_ground_1_" + (byte3 + int3 * 4)));
					}
				}

				IsoCell.this.snowGridTiles_Strip = new IsoCell.SnowGridTiles[4];
				for (int2 = 0; int2 < 4; ++int2) {
					IsoCell.this.snowGridTiles_Strip[int2] = IsoCell.this.new SnowGridTiles(byte2++);
					if (int2 == 0) {
						byte3 = 28;
					}

					if (int2 == 2) {
						byte3 = 29;
					}

					if (int2 == 1) {
						byte3 = 31;
					}

					if (int2 == 3) {
						byte3 = 30;
					}

					for (int3 = 0; int3 < 3; ++int3) {
						IsoCell.this.snowGridTiles_Strip[int2].add(Texture.getSharedTexture("e_newsnow_ground_1_" + (byte3 + int3 * 4)));
					}
				}

				IsoCell.this.hasSetupSnowGrid = true;
			}

			IsoCell.this.snowGridTiles_Square.resetCounter();
			IsoCell.this.snowGridTiles_Enclosed.resetCounter();
			for (int int4 = 0; int4 < 4; ++int4) {
				IsoCell.this.snowGridTiles_Cove[int4].resetCounter();
				IsoCell.this.snowGridTiles_Edge[int4].resetCounter();
				IsoCell.this.snowGridTiles_Strip[int4].resetCounter();
			}

			this.frac = int1;
			Noise2D noise2D = IsoCell.this.snowNoise2D;
			int int5;
			for (int5 = 0; int5 < this.h; ++int5) {
				for (int2 = 0; int2 < this.w; ++int2) {
					for (int3 = 0; int3 < 2; ++int3) {
						this.grid[int2][int5][int3] = null;
						this.gridType[int2][int5][int3] = -1;
					}

					if (noise2D.layeredNoise((float)int2 / 10.0F, (float)int5 / 10.0F) <= (float)int1 / 100.0F) {
						this.grid[int2][int5][0] = IsoCell.this.snowGridTiles_Square.getNext();
						this.gridType[int2][int5][0] = IsoCell.this.snowGridTiles_Square.ID;
					}
				}
			}

			for (int int6 = 0; int6 < this.h; ++int6) {
				for (int int7 = 0; int7 < this.w; ++int7) {
					Texture texture = this.grid[int7][int6][0];
					if (texture == null) {
						boolean boolean1 = this.check(int7, int6 - 1);
						boolean boolean2 = this.check(int7, int6 + 1);
						boolean boolean3 = this.check(int7 - 1, int6);
						boolean boolean4 = this.check(int7 + 1, int6);
						int5 = 0;
						if (boolean1) {
							++int5;
						}

						if (boolean2) {
							++int5;
						}

						if (boolean4) {
							++int5;
						}

						if (boolean3) {
							++int5;
						}

						if (int5 != 0) {
							if (int5 == 1) {
								if (boolean1) {
									this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Strip[0]);
								} else if (boolean2) {
									this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Strip[1]);
								} else if (boolean4) {
									this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Strip[3]);
								} else if (boolean3) {
									this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Strip[2]);
								}
							} else if (int5 == 2) {
								if (boolean1 && boolean2) {
									this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Strip[0]);
									this.set(int7, int6, 1, IsoCell.this.snowGridTiles_Strip[1]);
								} else if (boolean4 && boolean3) {
									this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Strip[2]);
									this.set(int7, int6, 1, IsoCell.this.snowGridTiles_Strip[3]);
								} else if (boolean1) {
									this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Edge[boolean3 ? 0 : 3]);
								} else if (boolean2) {
									this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Edge[boolean3 ? 2 : 1]);
								} else if (boolean3) {
									this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Edge[boolean1 ? 0 : 2]);
								} else if (boolean4) {
									this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Edge[boolean1 ? 3 : 1]);
								}
							} else if (int5 == 3) {
								if (!boolean1) {
									this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Cove[1]);
								} else if (!boolean2) {
									this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Cove[0]);
								} else if (!boolean4) {
									this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Cove[2]);
								} else if (!boolean3) {
									this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Cove[3]);
								}
							} else if (int5 == 4) {
								this.set(int7, int6, 0, IsoCell.this.snowGridTiles_Enclosed);
							}
						}
					}
				}
			}

			return this;
		}

		public boolean check(int int1, int int2) {
			if (int1 == this.w) {
				int1 = 0;
			}

			if (int1 == -1) {
				int1 = this.w - 1;
			}

			if (int2 == this.h) {
				int2 = 0;
			}

			if (int2 == -1) {
				int2 = this.h - 1;
			}

			if (int1 >= 0 && int1 < this.w) {
				if (int2 >= 0 && int2 < this.h) {
					Texture texture = this.grid[int1][int2][0];
					return IsoCell.this.snowGridTiles_Square.contains(texture);
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		public void set(int int1, int int2, int int3, IsoCell.SnowGridTiles snowGridTiles) {
			if (int1 == this.w) {
				int1 = 0;
			}

			if (int1 == -1) {
				int1 = this.w - 1;
			}

			if (int2 == this.h) {
				int2 = 0;
			}

			if (int2 == -1) {
				int2 = this.h - 1;
			}

			if (int1 >= 0 && int1 < this.w) {
				if (int2 >= 0 && int2 < this.h) {
					this.grid[int1][int2][int3] = snowGridTiles.getNext();
					this.gridType[int1][int2][int3] = snowGridTiles.ID;
				}
			}
		}

		public void subtract(IsoCell.SnowGrid snowGrid) {
			for (int int1 = 0; int1 < this.h; ++int1) {
				for (int int2 = 0; int2 < this.w; ++int2) {
					for (int int3 = 0; int3 < 2; ++int3) {
						if (snowGrid.gridType[int2][int1][int3] == this.gridType[int2][int1][int3]) {
							this.grid[int2][int1][int3] = null;
							this.gridType[int2][int1][int3] = -1;
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

		public SnowGridTiles(byte byte1) {
			this.ID = byte1;
		}

		protected void add(Texture texture) {
			this.textures.add(texture);
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

		protected boolean contains(Texture texture) {
			return this.textures.contains(texture);
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

		public void setSize(int int1, int int2) {
			if (this.VisiOccludedFlags == null || this.VisiOccludedFlags.length < int1 || this.VisiOccludedFlags[0].length < int2) {
				this.VisiOccludedFlags = new boolean[int1][int2][2];
				this.VisiCulledFlags = new boolean[int1][int2];
				this.StencilValues = new short[int1][int2][2];
			}
		}
	}
}
