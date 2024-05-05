package zombie.iso;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Vector2i;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaClosure;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.MovingObjectUpdateScheduler;
import zombie.ReanimatedPlayers;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.Lua.LuaManager;
import zombie.ai.astar.Mover;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.physics.WorldSimulation;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.profiling.PerformanceProfileProbeList;
import zombie.core.textures.ColorInfo;
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
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.sprite.CorpseFlies;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.sprite.shapers.FloorShaper;
import zombie.iso.sprite.shapers.FloorShaperAttachedSprites;
import zombie.iso.sprite.shapers.FloorShaperDiamond;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.fog.ImprovedFog;
import zombie.iso.weather.fx.IsoWeatherFX;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.popman.NetworkZombieSimulator;
import zombie.savefile.ClientPlayerDB;
import zombie.savefile.PlayerDB;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.UIManager;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;


public final class IsoCell {
	public static int MaxHeight = 8;
	private static Shader m_floorRenderShader;
	private static Shader m_wallRenderShader;
	public ArrayList Trees = new ArrayList();
	static final ArrayList stchoices = new ArrayList();
	public final IsoChunkMap[] ChunkMap = new IsoChunkMap[4];
	public final ArrayList BuildingList = new ArrayList();
	private final ArrayList WindowList = new ArrayList();
	private final ArrayList ObjectList = new ArrayList();
	private final ArrayList PushableObjectList = new ArrayList();
	private final HashMap BuildingScores = new HashMap();
	private final ArrayList RoomList = new ArrayList();
	private final ArrayList StaticUpdaterObjectList = new ArrayList();
	private final ArrayList ZombieList = new ArrayList();
	private final ArrayList RemoteSurvivorList = new ArrayList();
	private final ArrayList removeList = new ArrayList();
	private final ArrayList addList = new ArrayList();
	private final ArrayList ProcessIsoObject = new ArrayList();
	private final ArrayList ProcessIsoObjectRemove = new ArrayList();
	private final ArrayList ProcessItems = new ArrayList();
	private final ArrayList ProcessItemsRemove = new ArrayList();
	private final ArrayList ProcessWorldItems = new ArrayList();
	public final ArrayList ProcessWorldItemsRemove = new ArrayList();
	private final IsoGridSquare[][] gridSquares;
	public static final boolean ENABLE_SQUARE_CACHE = true;
	private int height;
	private int width;
	private int worldX;
	private int worldY;
	public IntGrid DangerScore;
	private boolean safeToAdd;
	private final Stack LamppostPositions;
	public final ArrayList roomLights;
	private final ArrayList heatSources;
	public final ArrayList addVehicles;
	public final ArrayList vehicles;
	public static final int ISOANGLEFACTOR = 3;
	private static final int ZOMBIESCANBUDGET = 10;
	private static final float NEARESTZOMBIEDISTSQRMAX = 150.0F;
	private int zombieScanCursor;
	private final IsoZombie[] nearestVisibleZombie;
	private final float[] nearestVisibleZombieDistSqr;
	private static Stack buildingscores = new Stack();
	static ArrayList GridStack = null;
	public static final int RTF_SolidFloor = 1;
	public static final int RTF_VegetationCorpses = 2;
	public static final int RTF_MinusFloorCharacters = 4;
	public static final int RTF_ShadedFloor = 8;
	public static final int RTF_Shadows = 16;
	private static final ArrayList ShadowSquares = new ArrayList(1000);
	private static final ArrayList MinusFloorCharacters = new ArrayList(1000);
	private static final ArrayList SolidFloor = new ArrayList(5000);
	private static final ArrayList ShadedFloor = new ArrayList(5000);
	private static final ArrayList VegetationCorpses = new ArrayList(5000);
	public static final IsoCell.PerPlayerRender[] perPlayerRender = new IsoCell.PerPlayerRender[4];
	private final int[] StencilXY;
	private final int[] StencilXY2z;
	public int StencilX1;
	public int StencilY1;
	public int StencilX2;
	public int StencilY2;
	private Texture m_stencilTexture;
	private final DiamondMatrixIterator diamondMatrixIterator;
	private final Vector2i diamondMatrixPos;
	public int DeferredCharacterTick;
	private boolean hasSetupSnowGrid;
	private IsoCell.SnowGridTiles snowGridTiles_Square;
	private IsoCell.SnowGridTiles[] snowGridTiles_Strip;
	private IsoCell.SnowGridTiles[] snowGridTiles_Edge;
	private IsoCell.SnowGridTiles[] snowGridTiles_Cove;
	private IsoCell.SnowGridTiles snowGridTiles_Enclosed;
	private int m_snowFirstNonSquare;
	private Noise2D snowNoise2D;
	private IsoCell.SnowGrid snowGridCur;
	private IsoCell.SnowGrid snowGridPrev;
	private int snowFracTarget;
	private long snowFadeTime;
	private float snowTransitionTime;
	private int raport;
	private static final int SNOWSHORE_NONE = 0;
	private static final int SNOWSHORE_N = 1;
	private static final int SNOWSHORE_E = 2;
	private static final int SNOWSHORE_S = 4;
	private static final int SNOWSHORE_W = 8;
	public boolean recalcFloors;
	static int wx;
	static int wy;
	final KahluaTable[] drag;
	final ArrayList SurvivorList;
	private static Texture texWhite;
	private static IsoCell instance;
	private int currentLX;
	private int currentLY;
	private int currentLZ;
	int recalcShading;
	int lastMinX;
	int lastMinY;
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
	public boolean bRendering;
	final boolean[] bHideFloors;
	final int[] unhideFloorsCounter;
	boolean bOccludedByOrphanStructureFlag;
	int playerPeekedRoomId;
	final ArrayList playerOccluderBuildings;
	final IsoBuilding[][] playerOccluderBuildingsArr;
	final int[] playerWindowPeekingRoomId;
	final boolean[] playerHidesOrphanStructures;
	final boolean[] playerCutawaysDirty;
	final Vector2 tempCutawaySqrVector;
	ArrayList tempPrevPlayerCutawayRoomIDs;
	ArrayList tempPlayerCutawayRoomIDs;
	final IsoGridSquare[] lastPlayerSquare;
	final boolean[] lastPlayerSquareHalf;
	final IsoDirections[] lastPlayerDir;
	final Vector2[] lastPlayerAngle;
	int hidesOrphanStructuresAbove;
	final Rectangle buildingRectTemp;
	final ArrayList zombieOccluderBuildings;
	final IsoBuilding[][] zombieOccluderBuildingsArr;
	final IsoGridSquare[] lastZombieSquare;
	final boolean[] lastZombieSquareHalf;
	final ArrayList otherOccluderBuildings;
	final IsoBuilding[][] otherOccluderBuildingsArr;
	final int mustSeeSquaresRadius;
	final int mustSeeSquaresGridSize;
	final ArrayList gridSquaresTempLeft;
	final ArrayList gridSquaresTempRight;
	private IsoWeatherFX weatherFX;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int minZ;
	private int maxZ;
	private OnceEvery dangerUpdate;
	private Thread LightInfoUpdate;
	private final Stack SpottedRooms;
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

	public IsoZombie getNearestVisibleZombie(int int1) {
		return this.nearestVisibleZombie[int1];
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
		this.gridSquares = new IsoGridSquare[4][IsoChunkMap.ChunkWidthInTiles * IsoChunkMap.ChunkWidthInTiles * 8];
		this.safeToAdd = true;
		this.LamppostPositions = new Stack();
		this.roomLights = new ArrayList();
		this.heatSources = new ArrayList();
		this.addVehicles = new ArrayList();
		this.vehicles = new ArrayList();
		this.zombieScanCursor = 0;
		this.nearestVisibleZombie = new IsoZombie[4];
		this.nearestVisibleZombieDistSqr = new float[4];
		this.StencilXY = new int[]{0, 0, -1, 0, 0, -1, -1, -1, -2, -1, -1, -2, -2, -2, -3, -2, -2, -3, -3, -3};
		this.StencilXY2z = new int[]{0, 0, -1, 0, 0, -1, -1, -1, -2, -1, -1, -2, -2, -2, -3, -2, -2, -3, -3, -3, -4, -3, -3, -4, -4, -4, -5, -4, -4, -5, -5, -5, -6, -5, -5, -6, -6, -6};
		this.m_stencilTexture = null;
		this.diamondMatrixIterator = new DiamondMatrixIterator(123);
		this.diamondMatrixPos = new Vector2i();
		this.DeferredCharacterTick = 0;
		this.hasSetupSnowGrid = false;
		this.m_snowFirstNonSquare = -1;
		this.snowNoise2D = new Noise2D();
		this.snowFracTarget = 0;
		this.snowFadeTime = 0L;
		this.snowTransitionTime = 5000.0F;
		this.raport = 0;
		this.recalcFloors = false;
		this.drag = new KahluaTable[4];
		this.SurvivorList = new ArrayList();
		this.currentLX = 0;
		this.currentLY = 0;
		this.currentLZ = 0;
		this.recalcShading = 30;
		this.lastMinX = -1234567;
		this.lastMinY = -1234567;
		this.rainX = new int[4];
		this.rainY = new int[4];
		this.rainTextures = new Texture[5];
		this.rainFileTime = new long[5];
		this.rainAlphaMax = 0.6F;
		this.rainAlpha = new float[4];
		this.rainIntensity = 0;
		this.rainSpeed = 6;
		this.lightUpdateCount = 11;
		this.bRendering = false;
		this.bHideFloors = new boolean[4];
		this.unhideFloorsCounter = new int[4];
		this.bOccludedByOrphanStructureFlag = false;
		this.playerPeekedRoomId = -1;
		this.playerOccluderBuildings = new ArrayList(4);
		this.playerOccluderBuildingsArr = new IsoBuilding[4][];
		this.playerWindowPeekingRoomId = new int[4];
		this.playerHidesOrphanStructures = new boolean[4];
		this.playerCutawaysDirty = new boolean[4];
		this.tempCutawaySqrVector = new Vector2();
		this.tempPrevPlayerCutawayRoomIDs = new ArrayList();
		this.tempPlayerCutawayRoomIDs = new ArrayList();
		this.lastPlayerSquare = new IsoGridSquare[4];
		this.lastPlayerSquareHalf = new boolean[4];
		this.lastPlayerDir = new IsoDirections[4];
		this.lastPlayerAngle = new Vector2[4];
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
		IsoGridSquare square2 = !IsoGridSquare.getMatrixBit(square.visionMatrix, (int)0, (int)0, (int)1) ? square.nav[IsoDirections.NW.index()] : null;
		IsoGridSquare square3 = !IsoGridSquare.getMatrixBit(square.visionMatrix, (int)1, (int)0, (int)1) ? square.nav[IsoDirections.N.index()] : null;
		IsoGridSquare square4 = !IsoGridSquare.getMatrixBit(square.visionMatrix, (int)2, (int)0, (int)1) ? square.nav[IsoDirections.NE.index()] : null;
		IsoGridSquare square5 = !IsoGridSquare.getMatrixBit(square.visionMatrix, (int)2, (int)1, (int)1) ? square.nav[IsoDirections.E.index()] : null;
		IsoGridSquare square6 = !IsoGridSquare.getMatrixBit(square.visionMatrix, (int)2, (int)2, (int)1) ? square.nav[IsoDirections.SE.index()] : null;
		IsoGridSquare square7 = !IsoGridSquare.getMatrixBit(square.visionMatrix, (int)1, (int)2, (int)1) ? square.nav[IsoDirections.S.index()] : null;
		IsoGridSquare square8 = !IsoGridSquare.getMatrixBit(square.visionMatrix, (int)0, (int)2, (int)1) ? square.nav[IsoDirections.SW.index()] : null;
		IsoGridSquare square9 = !IsoGridSquare.getMatrixBit(square.visionMatrix, (int)0, (int)1, (int)1) ? square.nav[IsoDirections.W.index()] : null;
		this.CalculateColor(square2, square3, square9, square, 0, int4);
		this.CalculateColor(square3, square4, square5, square, 1, int4);
		this.CalculateColor(square6, square7, square5, square, 2, int4);
		this.CalculateColor(square8, square7, square9, square, 3, int4);
	}

	private Texture getStencilTexture() {
		if (this.m_stencilTexture == null) {
			this.m_stencilTexture = Texture.getSharedTexture("media/mask_circledithernew.png");
		}

		return this.m_stencilTexture;
	}

	public void DrawStencilMask() {
		Texture texture = this.getStencilTexture();
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
			IndieGL.glColorMask(false, false, false, false);
			texture.renderstrip(int1 - (int)IsoCamera.getRightClickOffX(), int2 - (int)IsoCamera.getRightClickOffY(), texture.getWidth() * Core.TileScale, texture.getHeight() * Core.TileScale, 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
			IndieGL.glColorMask(true, true, true, true);
			IndieGL.glStencilFunc(519, 0, 255);
			IndieGL.glStencilOp(7680, 7680, 7680);
			IndieGL.glStencilMask(127);
			IndieGL.glAlphaFunc(519, 0.0F);
			this.StencilX1 = int1 - (int)IsoCamera.getRightClickOffX();
			this.StencilY1 = int2 - (int)IsoCamera.getRightClickOffY();
			this.StencilX2 = this.StencilX1 + texture.getWidth() * Core.TileScale;
			this.StencilY2 = this.StencilY1 + texture.getHeight() * Core.TileScale;
		}
	}

	public void RenderTiles(int int1) {
		IsoCell.s_performance.isoCellRenderTiles.invokeAndMeasure(this, int1, IsoCell::renderTilesInternal);
	}

	private void renderTilesInternal(int int1) {
		if (DebugOptions.instance.Terrain.RenderTiles.Enable.getValue()) {
			if (m_floorRenderShader == null) {
				RenderThread.invokeOnRenderContext(this::initTileShaders);
			}

			int int2 = IsoCamera.frameState.playerIndex;
			IsoPlayer player = IsoPlayer.players[int2];
			player.dirtyRecalcGridStackTime -= GameTime.getInstance().getMultiplier() / 4.0F;
			IsoCell.PerPlayerRender perPlayerRender = this.getPerPlayerRenderAt(int2);
			perPlayerRender.setSize(this.maxX - this.minX + 1, this.maxY - this.minY + 1);
			long long1 = System.currentTimeMillis();
			if (this.minX != perPlayerRender.minX || this.minY != perPlayerRender.minY || this.maxX != perPlayerRender.maxX || this.maxY != perPlayerRender.maxY) {
				perPlayerRender.minX = this.minX;
				perPlayerRender.minY = this.minY;
				perPlayerRender.maxX = this.maxX;
				perPlayerRender.maxY = this.maxY;
				player.dirtyRecalcGridStack = true;
				WeatherFxMask.forceMaskUpdate(int2);
			}

			IsoCell.s_performance.renderTiles.recalculateAnyGridStacks.start();
			boolean boolean1 = player.dirtyRecalcGridStack;
			this.recalculateAnyGridStacks(perPlayerRender, int1, int2, long1);
			IsoCell.s_performance.renderTiles.recalculateAnyGridStacks.end();
			++this.DeferredCharacterTick;
			IsoCell.s_performance.renderTiles.flattenAnyFoliage.start();
			this.flattenAnyFoliage(perPlayerRender, int2);
			IsoCell.s_performance.renderTiles.flattenAnyFoliage.end();
			if (this.SetCutawayRoomsForPlayer() || boolean1) {
				IsoGridStack gridStack = perPlayerRender.GridStacks;
				for (int int3 = 0; int3 < int1 + 1; ++int3) {
					GridStack = (ArrayList)gridStack.Squares.get(int3);
					for (int int4 = 0; int4 < GridStack.size(); ++int4) {
						IsoGridSquare square = (IsoGridSquare)GridStack.get(int4);
						square.setPlayerCutawayFlag(int2, this.IsCutawaySquare(square, long1), long1);
					}
				}
			}

			IsoCell.s_performance.renderTiles.performRenderTiles.start();
			this.performRenderTiles(perPlayerRender, int1, int2, long1);
			IsoCell.s_performance.renderTiles.performRenderTiles.end();
			this.playerCutawaysDirty[int2] = false;
			ShadowSquares.clear();
			MinusFloorCharacters.clear();
			ShadedFloor.clear();
			SolidFloor.clear();
			VegetationCorpses.clear();
			IsoCell.s_performance.renderTiles.renderDebugPhysics.start();
			this.renderDebugPhysics(int2);
			IsoCell.s_performance.renderTiles.renderDebugPhysics.end();
			IsoCell.s_performance.renderTiles.renderDebugLighting.start();
			this.renderDebugLighting(perPlayerRender, int1);
			IsoCell.s_performance.renderTiles.renderDebugLighting.end();
		}
	}

	private void initTileShaders() {
		if (DebugLog.isEnabled(DebugType.Shader)) {
			DebugLog.Shader.debugln("Loading shader: \"floorTile\"");
		}

		m_floorRenderShader = new Shader("floorTile");
		if (DebugLog.isEnabled(DebugType.Shader)) {
			DebugLog.Shader.debugln("Loading shader: \"wallTile\"");
		}

		m_wallRenderShader = new Shader("wallTile");
	}

	private IsoCell.PerPlayerRender getPerPlayerRenderAt(int int1) {
		if (perPlayerRender[int1] == null) {
			perPlayerRender[int1] = new IsoCell.PerPlayerRender();
		}

		return perPlayerRender[int1];
	}

	private void recalculateAnyGridStacks(IsoCell.PerPlayerRender perPlayerRender, int int1, int int2, long long1) {
		IsoPlayer player = IsoPlayer.players[int2];
		if (player.dirtyRecalcGridStack) {
			player.dirtyRecalcGridStack = false;
			IsoGridStack gridStack = perPlayerRender.GridStacks;
			boolean[][][] booleanArrayArrayArray = perPlayerRender.VisiOccludedFlags;
			boolean[][] booleanArrayArray = perPlayerRender.VisiCulledFlags;
			IsoChunk chunk = -1;
			int int3 = -1;
			int int4 = -1;
			WeatherFxMask.setDiamondIterDone(int2);
			for (int int5 = int1; int5 >= 0; --int5) {
				GridStack = (ArrayList)gridStack.Squares.get(int5);
				GridStack.clear();
				if (int5 < this.maxZ) {
					boolean boolean1;
					if (DebugOptions.instance.Terrain.RenderTiles.NewRender.getValue()) {
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
									if (chunk2 != null && square.IsOnScreen(true)) {
										WeatherFxMask.addMaskLocation(square, vector2i.x + this.minX, vector2i.y + this.minY, int5);
										boolean1 = this.IsDissolvedSquare(square, int2);
										square.setIsDissolved(int2, boolean1, long1);
										if (!square.getIsDissolved(int2, long1)) {
											square.cacheLightInfo();
											GridStack.add(square);
										}
									}
								}
							}
						}
					} else {
						label106: for (int int6 = this.minY; int6 < this.maxY; ++int6) {
							int int7 = this.minX;
							IsoGridSquare square2 = this.ChunkMap[int2].getGridSquare(int7, int6, int5);
							int int8 = IsoDirections.E.index();
							while (true) {
								while (true) {
									if (int7 >= this.maxX) {
										continue label106;
									}

									if (int5 == 0) {
										booleanArrayArrayArray[int7 - this.minX][int6 - this.minY][0] = false;
										booleanArrayArrayArray[int7 - this.minX][int6 - this.minY][1] = false;
										booleanArrayArray[int7 - this.minX][int6 - this.minY] = false;
									}

									if (square2 != null && square2.getY() != int6) {
										square2 = null;
									}

									boolean1 = true;
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
										if (chunk3 != null && square2.IsOnScreen(true)) {
											WeatherFxMask.addMaskLocation(square2, square2.x, square2.y, int5);
											boolean boolean3 = this.IsDissolvedSquare(square2, int2);
											square2.setIsDissolved(int2, boolean3, long1);
											if (!square2.getIsDissolved(int2, long1)) {
												square2.cacheLightInfo();
												GridStack.add(square2);
											}
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
	}

	private void flattenAnyFoliage(IsoCell.PerPlayerRender perPlayerRender, int int1) {
		short[][][] shortArrayArrayArray = perPlayerRender.StencilValues;
		boolean[][] booleanArrayArray = perPlayerRender.FlattenGrassEtc;
		int int2;
		for (int2 = this.minY; int2 <= this.maxY; ++int2) {
			for (int int3 = this.minX; int3 <= this.maxX; ++int3) {
				shortArrayArrayArray[int3 - this.minX][int2 - this.minY][0] = 0;
				shortArrayArrayArray[int3 - this.minX][int2 - this.minY][1] = 0;
				booleanArrayArray[int3 - this.minX][int2 - this.minY] = false;
			}
		}

		for (int2 = 0; int2 < this.vehicles.size(); ++int2) {
			BaseVehicle baseVehicle = (BaseVehicle)this.vehicles.get(int2);
			if (!(baseVehicle.getAlpha(int1) <= 0.0F)) {
				for (int int4 = -2; int4 < 5; ++int4) {
					for (int int5 = -2; int5 < 5; ++int5) {
						int int6 = (int)baseVehicle.x + int5;
						int int7 = (int)baseVehicle.y + int4;
						if (int6 >= this.minX && int6 <= this.maxX && int7 >= this.minY && int7 <= this.maxY) {
							booleanArrayArray[int6 - this.minX][int7 - this.minY] = true;
						}
					}
				}
			}
		}
	}

	private void performRenderTiles(IsoCell.PerPlayerRender perPlayerRender, int int1, int int2, long long1) {
		IsoGridStack gridStack = perPlayerRender.GridStacks;
		boolean[][] booleanArrayArray = perPlayerRender.FlattenGrassEtc;
		Shader shader;
		Shader shader2;
		if (Core.bDebug && !DebugOptions.instance.Terrain.RenderTiles.UseShaders.getValue()) {
			shader = null;
			shader2 = null;
		} else {
			shader = m_floorRenderShader;
			shader2 = m_wallRenderShader;
		}

		for (int int3 = 0; int3 < int1 + 1; ++int3) {
			IsoCell.s_performance.renderTiles.PperformRenderTilesLayer pperformRenderTilesLayer = (IsoCell.s_performance.renderTiles.PperformRenderTilesLayer)IsoCell.s_performance.renderTiles.performRenderTilesLayers.start(int3);
			GridStack = (ArrayList)gridStack.Squares.get(int3);
			ShadowSquares.clear();
			SolidFloor.clear();
			ShadedFloor.clear();
			VegetationCorpses.clear();
			MinusFloorCharacters.clear();
			IndieGL.glClear(256);
			if (int3 == 0 && DebugOptions.instance.Terrain.RenderTiles.Water.getValue() && DebugOptions.instance.Terrain.RenderTiles.WaterBody.getValue()) {
				pperformRenderTilesLayer.renderIsoWater.start();
				IsoWater.getInstance().render(GridStack, false);
				pperformRenderTilesLayer.renderIsoWater.end();
			}

			pperformRenderTilesLayer.renderFloor.start();
			int int4;
			IsoGridSquare square;
			boolean boolean1;
			for (int4 = 0; int4 < GridStack.size(); ++int4) {
				square = (IsoGridSquare)GridStack.get(int4);
				if (square.chunk == null || !square.chunk.bLightingNeverDone[int2]) {
					square.bFlattenGrassEtc = int3 == 0 && booleanArrayArray[square.x - this.minX][square.y - this.minY];
					int int5 = square.renderFloor(shader);
					if (!square.getStaticMovingObjects().isEmpty()) {
						int5 |= 2;
						int5 |= 16;
						if (square.HasStairs()) {
							int5 |= 4;
						}
					}

					if (!square.getWorldObjects().isEmpty()) {
						int5 |= 2;
					}

					if (!square.getLocalTemporaryObjects().isEmpty()) {
						int5 |= 4;
					}

					for (int int6 = 0; int6 < square.getMovingObjects().size(); ++int6) {
						IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int6);
						boolean1 = movingObject.bOnFloor;
						if (boolean1 && movingObject instanceof IsoZombie) {
							IsoZombie zombie = (IsoZombie)movingObject;
							boolean1 = zombie.isProne();
							if (!BaseVehicle.RENDER_TO_TEXTURE) {
								boolean1 = false;
							}
						}

						if (boolean1) {
							int5 |= 2;
						} else {
							int5 |= 4;
						}

						int5 |= 16;
					}

					if (!square.getDeferedCharacters().isEmpty()) {
						int5 |= 4;
					}

					if (square.hasFlies()) {
						int5 |= 4;
					}

					if ((int5 & 1) != 0) {
						SolidFloor.add(square);
					}

					if ((int5 & 8) != 0) {
						ShadedFloor.add(square);
					}

					if ((int5 & 2) != 0) {
						VegetationCorpses.add(square);
					}

					if ((int5 & 4) != 0) {
						MinusFloorCharacters.add(square);
					}

					if ((int5 & 16) != 0) {
						ShadowSquares.add(square);
					}
				}
			}

			pperformRenderTilesLayer.renderFloor.end();
			pperformRenderTilesLayer.renderPuddles.start();
			IsoPuddles.getInstance().render(SolidFloor, int3);
			pperformRenderTilesLayer.renderPuddles.end();
			if (int3 == 0 && DebugOptions.instance.Terrain.RenderTiles.Water.getValue() && DebugOptions.instance.Terrain.RenderTiles.WaterShore.getValue()) {
				pperformRenderTilesLayer.renderShore.start();
				IsoWater.getInstance().render((ArrayList)null, true);
				pperformRenderTilesLayer.renderShore.end();
			}

			if (!SolidFloor.isEmpty()) {
				pperformRenderTilesLayer.renderSnow.start();
				this.RenderSnow(int3);
				pperformRenderTilesLayer.renderSnow.end();
			}

			if (!GridStack.isEmpty()) {
				pperformRenderTilesLayer.renderBlood.start();
				this.ChunkMap[int2].renderBloodForChunks(int3);
				pperformRenderTilesLayer.renderBlood.end();
			}

			if (!ShadedFloor.isEmpty()) {
				pperformRenderTilesLayer.renderFloorShading.start();
				this.RenderFloorShading(int3);
				pperformRenderTilesLayer.renderFloorShading.end();
			}

			WorldMarkers.instance.renderGridSquareMarkers(perPlayerRender, int3, int2);
			if (DebugOptions.instance.Terrain.RenderTiles.Shadows.getValue()) {
				pperformRenderTilesLayer.renderShadows.start();
				this.renderShadows();
				pperformRenderTilesLayer.renderShadows.end();
			}

			if (DebugOptions.instance.Terrain.RenderTiles.Lua.getValue()) {
				pperformRenderTilesLayer.luaOnPostFloorLayerDraw.start();
				LuaEventManager.triggerEvent("OnPostFloorLayerDraw", int3);
				pperformRenderTilesLayer.luaOnPostFloorLayerDraw.end();
			}

			IsoMarkers.instance.renderIsoMarkers(perPlayerRender, int3, int2);
			IsoMarkers.instance.renderCircleIsoMarkers(perPlayerRender, int3, int2);
			if (DebugOptions.instance.Terrain.RenderTiles.VegetationCorpses.getValue()) {
				pperformRenderTilesLayer.vegetationCorpses.start();
				for (int4 = 0; int4 < VegetationCorpses.size(); ++int4) {
					square = (IsoGridSquare)VegetationCorpses.get(int4);
					square.renderMinusFloor(this.maxZ, false, true, false, false, false, shader2);
					square.renderCharacters(this.maxZ, true, true);
				}

				pperformRenderTilesLayer.vegetationCorpses.end();
			}

			ImprovedFog.startRender(int2, int3);
			if (DebugOptions.instance.Terrain.RenderTiles.MinusFloorCharacters.getValue()) {
				pperformRenderTilesLayer.minusFloorCharacters.start();
				int4 = 0;
				while (true) {
					if (int4 >= MinusFloorCharacters.size()) {
						pperformRenderTilesLayer.minusFloorCharacters.end();
						break;
					}

					square = (IsoGridSquare)MinusFloorCharacters.get(int4);
					IsoGridSquare square2 = square.nav[IsoDirections.S.index()];
					IsoGridSquare square3 = square.nav[IsoDirections.E.index()];
					boolean boolean2 = square2 != null && square2.getPlayerCutawayFlag(int2, long1);
					boolean1 = square.getPlayerCutawayFlag(int2, long1);
					boolean boolean3 = square3 != null && square3.getPlayerCutawayFlag(int2, long1);
					this.currentLY = square.getY() - this.minY;
					this.currentLZ = int3;
					ImprovedFog.renderRowsBehind(square);
					boolean boolean4 = square.renderMinusFloor(this.maxZ, false, false, boolean2, boolean1, boolean3, shader2);
					square.renderDeferredCharacters(this.maxZ);
					square.renderCharacters(this.maxZ, false, true);
					if (square.hasFlies()) {
						CorpseFlies.render(square.x, square.y, square.z);
					}

					if (boolean4) {
						square.renderMinusFloor(this.maxZ, true, false, boolean2, boolean1, boolean3, shader2);
					}

					++int4;
				}
			}

			IsoMarkers.instance.renderIsoMarkersDeferred(perPlayerRender, int3, int2);
			ImprovedFog.endRender();
			pperformRenderTilesLayer.end();
		}
	}

	private void renderShadows() {
		boolean boolean1 = Core.getInstance().getOptionCorpseShadows();
		for (int int1 = 0; int1 < ShadowSquares.size(); ++int1) {
			IsoGridSquare square = (IsoGridSquare)ShadowSquares.get(int1);
			int int2;
			IsoMovingObject movingObject;
			for (int2 = 0; int2 < square.getMovingObjects().size(); ++int2) {
				movingObject = (IsoMovingObject)square.getMovingObjects().get(int2);
				IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(movingObject, IsoGameCharacter.class);
				if (gameCharacter != null) {
					gameCharacter.renderShadow(gameCharacter.getX(), gameCharacter.getY(), gameCharacter.getZ());
				} else {
					BaseVehicle baseVehicle = (BaseVehicle)Type.tryCastTo(movingObject, BaseVehicle.class);
					if (baseVehicle != null) {
						baseVehicle.renderShadow();
					}
				}
			}

			if (boolean1) {
				for (int2 = 0; int2 < square.getStaticMovingObjects().size(); ++int2) {
					movingObject = (IsoMovingObject)square.getStaticMovingObjects().get(int2);
					IsoDeadBody deadBody = (IsoDeadBody)Type.tryCastTo(movingObject, IsoDeadBody.class);
					if (deadBody != null) {
						deadBody.renderShadow();
					}
				}
			}
		}
	}

	private void renderDebugPhysics(int int1) {
		if (Core.bDebug && DebugOptions.instance.PhysicsRender.getValue()) {
			TextureDraw.GenericDrawer genericDrawer = WorldSimulation.getDrawer(int1);
			SpriteRenderer.instance.drawGeneric(genericDrawer);
		}
	}

	private void renderDebugLighting(IsoCell.PerPlayerRender perPlayerRender, int int1) {
		if (Core.bDebug && DebugOptions.instance.LightingRender.getValue()) {
			IsoGridStack gridStack = perPlayerRender.GridStacks;
			byte byte1 = 1;
			for (int int2 = 0; int2 < int1 + 1; ++int2) {
				GridStack = (ArrayList)gridStack.Squares.get(int2);
				for (int int3 = 0; int3 < GridStack.size(); ++int3) {
					IsoGridSquare square = (IsoGridSquare)GridStack.get(int3);
					float float1 = IsoUtils.XToScreenExact((float)square.x + 0.3F, (float)square.y, 0.0F, 0);
					float float2 = IsoUtils.YToScreenExact((float)square.x + 0.3F, (float)square.y, 0.0F, 0);
					float float3 = IsoUtils.XToScreenExact((float)square.x + 0.6F, (float)square.y, 0.0F, 0);
					float float4 = IsoUtils.YToScreenExact((float)square.x + 0.6F, (float)square.y, 0.0F, 0);
					float float5 = IsoUtils.XToScreenExact((float)(square.x + 1), (float)square.y + 0.3F, 0.0F, 0);
					float float6 = IsoUtils.YToScreenExact((float)(square.x + 1), (float)square.y + 0.3F, 0.0F, 0);
					float float7 = IsoUtils.XToScreenExact((float)(square.x + 1), (float)square.y + 0.6F, 0.0F, 0);
					float float8 = IsoUtils.YToScreenExact((float)(square.x + 1), (float)square.y + 0.6F, 0.0F, 0);
					float float9 = IsoUtils.XToScreenExact((float)square.x + 0.6F, (float)(square.y + 1), 0.0F, 0);
					float float10 = IsoUtils.YToScreenExact((float)square.x + 0.6F, (float)(square.y + 1), 0.0F, 0);
					float float11 = IsoUtils.XToScreenExact((float)square.x + 0.3F, (float)(square.y + 1), 0.0F, 0);
					float float12 = IsoUtils.YToScreenExact((float)square.x + 0.3F, (float)(square.y + 1), 0.0F, 0);
					float float13 = IsoUtils.XToScreenExact((float)square.x, (float)square.y + 0.6F, 0.0F, 0);
					float float14 = IsoUtils.YToScreenExact((float)square.x, (float)square.y + 0.6F, 0.0F, 0);
					float float15 = IsoUtils.XToScreenExact((float)square.x, (float)square.y + 0.3F, 0.0F, 0);
					float float16 = IsoUtils.YToScreenExact((float)square.x, (float)square.y + 0.3F, 0.0F, 0);
					if (IsoGridSquare.getMatrixBit(square.visionMatrix, (int)0, (int)0, (int)byte1)) {
						LineDrawer.drawLine(float1, float2, float3, float4, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}

					if (IsoGridSquare.getMatrixBit(square.visionMatrix, (int)0, (int)1, (int)byte1)) {
						LineDrawer.drawLine(float3, float4, float5, float6, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}

					if (IsoGridSquare.getMatrixBit(square.visionMatrix, (int)0, (int)2, (int)byte1)) {
						LineDrawer.drawLine(float5, float6, float7, float8, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}

					if (IsoGridSquare.getMatrixBit(square.visionMatrix, (int)1, (int)2, (int)byte1)) {
						LineDrawer.drawLine(float7, float8, float9, float10, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}

					if (IsoGridSquare.getMatrixBit(square.visionMatrix, (int)2, (int)2, (int)byte1)) {
						LineDrawer.drawLine(float9, float10, float11, float12, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}

					if (IsoGridSquare.getMatrixBit(square.visionMatrix, (int)2, (int)1, (int)byte1)) {
						LineDrawer.drawLine(float11, float12, float13, float14, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}

					if (IsoGridSquare.getMatrixBit(square.visionMatrix, (int)2, (int)0, (int)byte1)) {
						LineDrawer.drawLine(float13, float14, float15, float16, 1.0F, 0.0F, 0.0F, 1.0F, 0);
					}

					if (IsoGridSquare.getMatrixBit(square.visionMatrix, (int)1, (int)0, (int)byte1)) {
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
					if (int5 >= 0 && int5 < booleanArrayArray.length) {
						if (int6 >= 0 && int6 < booleanArrayArray[0].length) {
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
							boolean1 = IsoGridSquare.getMatrixBit(square.visionMatrix, (int)0, (int)1, (int)1) && square.getProperties().Is(IsoFlagType.cutW);
							boolean boolean2 = IsoGridSquare.getMatrixBit(square.visionMatrix, (int)1, (int)0, (int)1) && square.getProperties().Is(IsoFlagType.cutN);
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

							if (IsoGridSquare.getMatrixBit(square.visionMatrix, (int)1, (int)1, (int)0)) {
								++int7;
								booleanArrayArrayArray[int5][int6][0] = true;
								booleanArrayArrayArray[int5][int6][1] = true;
							}

							if (int7 == 3) {
								booleanArrayArray[int5][int6] = true;
							}
						} else {
							GridStack.remove(int4);
						}
					} else {
						GridStack.remove(int4);
					}
				}
			}
		}
	}

	public void RenderFloorShading(int int1) {
		if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.LightingOld.getValue() && !DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.Lighting.getValue()) {
			if (int1 < this.maxZ && PerformanceSettings.LightingFrameSkip < 3) {
				if (!Core.bDebug || !DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
					if (texWhite == null) {
						texWhite = Texture.getWhite();
					}

					Texture texture = texWhite;
					if (texture != null) {
						int int2 = IsoCamera.frameState.playerIndex;
						int int3 = (int)IsoCamera.frameState.OffX;
						int int4 = (int)IsoCamera.frameState.OffY;
						for (int int5 = 0; int5 < ShadedFloor.size(); ++int5) {
							IsoGridSquare square = (IsoGridSquare)ShadedFloor.get(int5);
							if (square.getProperties().Is(IsoFlagType.solidfloor)) {
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
								float4 -= (float)int3;
								float5 -= (float)int4;
								int int6 = square.getVertLight(0, int2);
								int int7 = square.getVertLight(1, int2);
								int int8 = square.getVertLight(2, int2);
								int int9 = square.getVertLight(3, int2);
								if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.LightingDebug.getValue()) {
									int6 = -65536;
									int7 = -65536;
									int8 = -16776961;
									int9 = -16776961;
								}

								texture.renderdiamond(float4 - (float)(32 * Core.TileScale), float5 + (float)(16 * Core.TileScale), (float)(64 * Core.TileScale), (float)(32 * Core.TileScale), int9, int6, int7, int8);
							}
						}
					}
				}
			}
		}
	}

	public boolean IsPlayerWindowPeeking(int int1) {
		return this.playerWindowPeekingRoomId[int1] != -1;
	}

	public boolean CanBuildingSquareOccludePlayer(IsoGridSquare square, int int1) {
		ArrayList arrayList = (ArrayList)this.playerOccluderBuildings.get(int1);
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			IsoBuilding building = (IsoBuilding)arrayList.get(int2);
			int int3 = building.getDef().getX();
			int int4 = building.getDef().getY();
			int int5 = building.getDef().getX2() - int3;
			int int6 = building.getDef().getY2() - int4;
			this.buildingRectTemp.setBounds(int3 - 1, int4 - 1, int5 + 2, int6 + 2);
			if (this.buildingRectTemp.contains(square.getX(), square.getY())) {
				return true;
			}
		}

		return false;
	}

	public int GetEffectivePlayerRoomId() {
		int int1 = IsoCamera.frameState.playerIndex;
		int int2 = this.playerWindowPeekingRoomId[int1];
		if (IsoPlayer.players[int1] != null && IsoPlayer.players[int1].isClimbing()) {
			int2 = -1;
		}

		if (int2 != -1) {
			return int2;
		} else {
			IsoGridSquare square = IsoPlayer.players[int1].current;
			return square != null ? square.getRoomID() : -1;
		}
	}

	private boolean SetCutawayRoomsForPlayer() {
		int int1 = IsoCamera.frameState.playerIndex;
		IsoPlayer player = IsoPlayer.players[int1];
		ArrayList arrayList = this.tempPrevPlayerCutawayRoomIDs;
		this.tempPrevPlayerCutawayRoomIDs = this.tempPlayerCutawayRoomIDs;
		this.tempPlayerCutawayRoomIDs = arrayList;
		this.tempPlayerCutawayRoomIDs.clear();
		IsoGridSquare square = player.getSquare();
		if (square == null) {
			return false;
		} else {
			IsoBuilding building = square.getBuilding();
			int int2 = square.getRoomID();
			boolean boolean1 = false;
			if (int2 == -1) {
				if (this.playerWindowPeekingRoomId[int1] != -1) {
					this.tempPlayerCutawayRoomIDs.add(this.playerWindowPeekingRoomId[int1]);
				} else {
					boolean1 = this.playerCutawaysDirty[int1];
				}
			} else {
				int int3 = (int)(player.getX() - 1.5F);
				int int4 = (int)(player.getY() - 1.5F);
				int int5 = (int)(player.getX() + 1.5F);
				int int6 = (int)(player.getY() + 1.5F);
				for (int int7 = int3; int7 <= int5; ++int7) {
					for (int int8 = int4; int8 <= int6; ++int8) {
						IsoGridSquare square2 = this.getGridSquare(int7, int8, square.getZ());
						if (square2 != null) {
							int int9 = square2.getRoomID();
							if (square2.getCanSee(int1) && int9 != -1 && !this.tempPlayerCutawayRoomIDs.contains(int9)) {
								this.tempCutawaySqrVector.set((float)square2.getX() + 0.5F - player.getX(), (float)square2.getY() + 0.5F - player.getY());
								if (square == square2 || player.getForwardDirection().dot(this.tempCutawaySqrVector) > 0.0F) {
									this.tempPlayerCutawayRoomIDs.add(int9);
								}
							}
						}
					}
				}

				Collections.sort(this.tempPlayerCutawayRoomIDs);
			}

			return boolean1 || !this.tempPlayerCutawayRoomIDs.equals(this.tempPrevPlayerCutawayRoomIDs);
		}
	}

	private boolean IsCutawaySquare(IsoGridSquare square, long long1) {
		int int1 = IsoCamera.frameState.playerIndex;
		IsoPlayer player = IsoPlayer.players[int1];
		if (player.current == null) {
			return false;
		} else if (square == null) {
			return false;
		} else {
			IsoGridSquare square2 = player.current;
			if (square2.getZ() != square.getZ()) {
				return false;
			} else {
				IsoGridSquare square3;
				IsoGridSquare square4;
				IsoGridSquare square5;
				if (!this.tempPlayerCutawayRoomIDs.isEmpty()) {
					square3 = square.nav[IsoDirections.N.index()];
					square4 = square.nav[IsoDirections.E.index()];
					square5 = square.nav[IsoDirections.S.index()];
					IsoGridSquare square6 = square.nav[IsoDirections.W.index()];
					IsoGridSquare square7 = square2.nav[IsoDirections.N.index()];
					IsoGridSquare square8 = square2.nav[IsoDirections.E.index()];
					IsoGridSquare square9 = square2.nav[IsoDirections.S.index()];
					IsoGridSquare square10 = square2.nav[IsoDirections.W.index()];
					boolean boolean1 = false;
					boolean boolean2 = false;
					int int2;
					for (int2 = 0; int2 < 8; ++int2) {
						if (square.nav[int2] != null && square.nav[int2].getRoomID() != square.getRoomID()) {
							boolean1 = true;
							break;
						}
					}

					if (!this.tempPlayerCutawayRoomIDs.contains(square.getRoomID())) {
						boolean2 = true;
					}

					int int3;
					if (boolean1 || boolean2 || square.getWall() != null) {
						IsoGridSquare square11 = square;
						for (int3 = 0; int3 < 3; ++int3) {
							square11 = square11.nav[IsoDirections.NW.index()];
							if (square11 == null) {
								break;
							}

							if (square11.getRoomID() != -1 && this.tempPlayerCutawayRoomIDs.contains(square11.getRoomID())) {
								if ((boolean1 || boolean2) && square11.getCanSee(int1)) {
									return true;
								}

								if (square.getWall() != null && square11.isCouldSee(int1)) {
									return true;
								}
							}
						}
					}

					if (square3 != null && square6 != null && (square3.getThumpableWallOrHoppable(false) != null || square6.getThumpableWallOrHoppable(true) != null || square.getThumpableWallOrHoppable(true) != null || square.getThumpableWallOrHoppable(false) != null)) {
						return this.DoesSquareHaveValidCutaways(square2, square, int1, long1);
					}

					if (square2.getRoomID() == -1 && (square7 != null && square7.getRoomID() != -1 || square8 != null && square8.getRoomID() != -1 || square9 != null && square9.getRoomID() != -1 || square10 != null && square10.getRoomID() != -1)) {
						int2 = square2.x - square.x;
						int3 = square2.y - square.y;
						if (int2 < 0 && int3 < 0) {
							if (int2 >= -3) {
								if (int3 >= -3) {
									return true;
								}

								if (square3 != null && square5 != null && square.getWall(false) != null && square3.getWall(false) != null && square5.getWall(false) != null && square5.getPlayerCutawayFlag(int1, long1)) {
									return true;
								}
							} else if (square4 != null && square6 != null) {
								if (square.getWall(true) != null && square6.getWall(true) != null && square4.getWall(true) != null && square4.getPlayerCutawayFlag(int1, long1)) {
									return true;
								}

								if (square.getWall(true) != null && square6.getWall(true) != null && square4.getWall(true) != null && square4.getPlayerCutawayFlag(int1, long1)) {
									return true;
								}
							}
						}
					}
				} else {
					square3 = square.nav[IsoDirections.N.index()];
					square4 = square.nav[IsoDirections.W.index()];
					if (this.IsCollapsibleBuildingSquare(square)) {
						if (player.getZ() == 0.0F) {
							return true;
						}

						if (square.getBuilding() != null && (square2.getX() < square.getBuilding().def.x || square2.getY() < square.getBuilding().def.y)) {
							return true;
						}

						square5 = square;
						for (int int4 = 0; int4 < 3; ++int4) {
							square5 = square5.nav[IsoDirections.NW.index()];
							if (square5 == null) {
								break;
							}

							if (square5.isCanSee(int1)) {
								return true;
							}
						}
					}

					if (square3 != null && square3.getRoomID() == -1 && square4 != null && square4.getRoomID() == -1) {
						return this.DoesSquareHaveValidCutaways(square2, square, int1, long1);
					}
				}

				return false;
			}
		}
	}

	private boolean DoesSquareHaveValidCutaways(IsoGridSquare square, IsoGridSquare square2, int int1, long long1) {
		IsoGridSquare square3 = square2.nav[IsoDirections.N.index()];
		IsoGridSquare square4 = square2.nav[IsoDirections.E.index()];
		IsoGridSquare square5 = square2.nav[IsoDirections.S.index()];
		IsoGridSquare square6 = square2.nav[IsoDirections.W.index()];
		IsoObject object = square2.getWall(true);
		IsoObject object2 = square2.getWall(false);
		IsoObject object3 = null;
		IsoObject object4 = null;
		if (square3 != null && square3.nav[IsoDirections.W.index()] != null && square3.nav[IsoDirections.W.index()].getRoomID() == square3.getRoomID()) {
			object4 = square3.getWall(false);
		}

		if (square6 != null && square6.nav[IsoDirections.N.index()] != null && square6.nav[IsoDirections.N.index()].getRoomID() == square6.getRoomID()) {
			object3 = square6.getWall(true);
		}

		int int2;
		if (object2 != null || object != null || object4 != null || object3 != null) {
			IsoGridSquare square7 = square2.nav[IsoDirections.NW.index()];
			for (int2 = 0; int2 < 2 && square7 != null && square7.getRoomID() == square.getRoomID(); ++int2) {
				IsoGridSquare square8 = square7.nav[IsoDirections.S.index()];
				IsoGridSquare square9 = square7.nav[IsoDirections.E.index()];
				if (square8 != null && square8.getBuilding() != null || square9 != null && square9.getBuilding() != null) {
					break;
				}

				if (square7.isCanSee(int1) && square7.isCouldSee(int1) && square7.DistTo(square) <= (float)(6 - (int2 + 1))) {
					return true;
				}

				if (square7.getBuilding() == null) {
					square7 = square7.nav[IsoDirections.NW.index()];
				}
			}
		}

		int int3 = square.x - square2.x;
		int2 = square.y - square2.y;
		if (object != null && object.sprite.name.contains("fencing") || object2 != null && object2.sprite.name.contains("fencing")) {
			if (object != null && object3 != null && int2 >= -6 && int2 < 0) {
				return true;
			}

			if (object2 != null && object4 != null && int3 >= -6 && int3 < 0) {
				return true;
			}
		} else if (square2.DistTo(square) <= 6.0F && square2.nav[IsoDirections.NW.index()] != null && square2.nav[IsoDirections.NW.index()].getRoomID() == square2.getRoomID() && (square2.getWall(true) == null || square2.getWall(true) == object) && (square2.getWall(false) == null || square2.getWall(false) == object2)) {
			if (square5 != null && square3 != null && int2 != 0) {
				if (int2 > 0 && object2 != null && square5.getWall(false) != null && square3.getWall(false) != null && square5.getPlayerCutawayFlag(int1, long1)) {
					return true;
				}

				if (int2 < 0 && object2 != null && square3.getWall(false) != null && square3.getPlayerCutawayFlag(int1, long1)) {
					return true;
				}
			}

			if (square4 != null && square6 != null && int3 != 0) {
				if (int3 > 0 && object != null && square4.getWall(true) != null && square6.getWall(true) != null && square4.getPlayerCutawayFlag(int1, long1)) {
					return true;
				}

				if (int3 < 0 && object != null && square6.getWall(true) != null && square6.getPlayerCutawayFlag(int1, long1)) {
					return true;
				}
			}
		}

		if (square2 == square && square2.nav[IsoDirections.NW.index()] != null && square2.nav[IsoDirections.NW.index()].getRoomID() == square2.getRoomID()) {
			if (object != null && square3 != null && square3.getWall(false) == null && square3.isCanSee(int1) && square3.isCouldSee(int1)) {
				return true;
			}

			if (object2 != null && square6 != null && square6.getWall(true) != null && square6.isCanSee(int1) && square6.isCouldSee(int1)) {
				return true;
			}
		}

		if (square3 != null && square6 != null && int3 != 0 && int2 != 0 && object4 != null && object3 != null && square3.getPlayerCutawayFlag(int1, long1) && square6.getPlayerCutawayFlag(int1, long1)) {
			return true;
		} else {
			return int3 < 0 && int3 >= -6 && int2 < 0 && int2 >= -6 && (object2 != null && square2.getWall(true) == null || object != null && square2.getWall(false) == null);
		}
	}

	private boolean IsCollapsibleBuildingSquare(IsoGridSquare square) {
		if (square.getProperties().Is(IsoFlagType.forceRender)) {
			return false;
		} else {
			int int1;
			int int2;
			IsoBuilding building;
			BuildingDef buildingDef;
			for (int1 = 0; int1 < 4; ++int1) {
				short short1 = 500;
				for (int2 = 0; int2 < short1 && this.playerOccluderBuildingsArr[int1] != null; ++int2) {
					building = this.playerOccluderBuildingsArr[int1][int2];
					if (building == null) {
						break;
					}

					buildingDef = building.getDef();
					if (this.collapsibleBuildingSquareAlgorithm(buildingDef, square, IsoPlayer.players[int1].getSquare())) {
						return true;
					}

					if (square.getY() - buildingDef.getY2() == 1 && square.getWall(true) != null) {
						return true;
					}

					if (square.getX() - buildingDef.getX2() == 1 && square.getWall(false) != null) {
						return true;
					}
				}
			}

			int1 = IsoCamera.frameState.playerIndex;
			IsoPlayer player = IsoPlayer.players[int1];
			if (player.getVehicle() != null) {
				return false;
			} else {
				for (int2 = 0; int2 < 500 && this.zombieOccluderBuildingsArr[int1] != null; ++int2) {
					building = this.zombieOccluderBuildingsArr[int1][int2];
					if (building == null) {
						break;
					}

					buildingDef = building.getDef();
					if (this.collapsibleBuildingSquareAlgorithm(buildingDef, square, player.getSquare())) {
						return true;
					}
				}

				for (int2 = 0; int2 < 500 && this.otherOccluderBuildingsArr[int1] != null; ++int2) {
					building = this.otherOccluderBuildingsArr[int1][int2];
					if (building == null) {
						break;
					}

					buildingDef = building.getDef();
					if (this.collapsibleBuildingSquareAlgorithm(buildingDef, square, player.getSquare())) {
						return true;
					}
				}

				return false;
			}
		}
	}

	private boolean collapsibleBuildingSquareAlgorithm(BuildingDef buildingDef, IsoGridSquare square, IsoGridSquare square2) {
		int int1 = buildingDef.getX();
		int int2 = buildingDef.getY();
		int int3 = buildingDef.getX2() - int1;
		int int4 = buildingDef.getY2() - int2;
		this.buildingRectTemp.setBounds(int1, int2, int3, int4);
		if (square2.getRoomID() == -1 && this.buildingRectTemp.contains(square2.getX(), square2.getY())) {
			this.buildingRectTemp.setBounds(int1 - 1, int2 - 1, int3 + 2, int4 + 2);
			IsoGridSquare square3 = square.nav[IsoDirections.N.index()];
			IsoGridSquare square4 = square.nav[IsoDirections.W.index()];
			IsoGridSquare square5 = square.nav[IsoDirections.NW.index()];
			if (square5 != null && square3 != null && square4 != null) {
				boolean boolean1 = square.getRoomID() == -1;
				boolean boolean2 = square3.getRoomID() == -1;
				boolean boolean3 = square4.getRoomID() == -1;
				boolean boolean4 = square5.getRoomID() == -1;
				boolean boolean5 = square2.getY() < square.getY();
				boolean boolean6 = square2.getX() < square.getX();
				return this.buildingRectTemp.contains(square.getX(), square.getY()) && (square2.getZ() < square.getZ() || boolean1 && (!boolean2 && boolean5 || !boolean3 && boolean6) || boolean1 && boolean2 && boolean3 && !boolean4 || !boolean1 && (boolean4 || boolean2 == boolean3 || boolean2 && boolean6 || boolean3 && boolean5));
			} else {
				return false;
			}
		} else {
			this.buildingRectTemp.setBounds(int1 - 1, int2 - 1, int3 + 2, int4 + 2);
			return this.buildingRectTemp.contains(square.getX(), square.getY());
		}
	}

	private boolean IsDissolvedSquare(IsoGridSquare square, int int1) {
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

							if (square4.HasStairsBelow()) {
								return true;
							}
						}

						square3 = square.nav[IsoDirections.W.index()];
						if (square3 != null && square3.getBuilding() == null) {
							if (square3.getPlayerBuiltFloor() != null) {
								return true;
							}

							if (square3.HasStairsBelow()) {
								return true;
							}
						}

						if (square.Is(IsoFlagType.WallSE)) {
							IsoGridSquare square5 = square.nav[IsoDirections.NW.index()];
							if (square5 != null && square5.getBuilding() == null) {
								if (square5.getPlayerBuiltFloor() != null) {
									return true;
								}

								if (square5.HasStairsBelow()) {
									return true;
								}
							}
						}
					}
				}

				return this.IsCollapsibleBuildingSquare(square);
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
				this.snowFadeTime = System.currentTimeMillis();
				DebugLog.log("snow from " + this.snowGridPrev.frac + " to " + this.snowGridCur.frac);
			}
		}
	}

	public void setSnowTarget(int int1) {
		if (!SandboxOptions.instance.EnableSnowOnGround.getValue()) {
			int1 = 0;
		}

		this.snowFracTarget = int1;
	}

	public boolean gridSquareIsSnow(int int1, int int2, int int3) {
		IsoGridSquare square = this.getGridSquare(int1, int2, int3);
		if (square != null) {
			if (!square.getProperties().Is(IsoFlagType.solidfloor)) {
				return false;
			} else if (square.getProperties().Is(IsoFlagType.water)) {
				return false;
			} else if (square.getProperties().Is(IsoFlagType.exterior) && square.room == null && !square.isInARoom()) {
				int int4 = square.getX() % this.snowGridCur.w;
				int int5 = square.getY() % this.snowGridCur.h;
				return this.snowGridCur.check(int4, int5);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void RenderSnow(int int1) {
		if (DebugOptions.instance.Weather.Snow.getValue()) {
			this.updateSnow(this.snowFracTarget);
			IsoCell.SnowGrid snowGrid = this.snowGridCur;
			if (snowGrid != null) {
				IsoCell.SnowGrid snowGrid2 = this.snowGridPrev;
				if (snowGrid.frac > 0 || snowGrid2.frac > 0) {
					float float1 = 1.0F;
					float float2 = 0.0F;
					long long1 = System.currentTimeMillis();
					long long2 = long1 - this.snowFadeTime;
					if ((float)long2 < this.snowTransitionTime) {
						float float3 = (float)long2 / this.snowTransitionTime;
						float1 = float3;
						float2 = 1.0F - float3;
					}

					Shader shader = null;
					if (DebugOptions.instance.Terrain.RenderTiles.UseShaders.getValue()) {
						shader = m_floorRenderShader;
					}

					FloorShaperAttachedSprites.instance.setShore(false);
					FloorShaperDiamond.instance.setShore(false);
					IndieGL.StartShader(shader, IsoCamera.frameState.playerIndex);
					int int2 = (int)IsoCamera.frameState.OffX;
					int int3 = (int)IsoCamera.frameState.OffY;
					for (int int4 = 0; int4 < SolidFloor.size(); ++int4) {
						IsoGridSquare square = (IsoGridSquare)SolidFloor.get(int4);
						if (square.room == null && square.getProperties().Is(IsoFlagType.exterior) && square.getProperties().Is(IsoFlagType.solidfloor)) {
							int int5;
							if (square.getProperties().Is(IsoFlagType.water)) {
								int5 = getShoreInt(square);
								if (int5 == 0) {
									continue;
								}
							} else {
								int5 = 0;
							}

							int int6 = square.getX() % snowGrid.w;
							int int7 = square.getY() % snowGrid.h;
							float float4 = IsoUtils.XToScreen((float)square.getX(), (float)square.getY(), (float)int1, 0);
							float float5 = IsoUtils.YToScreen((float)square.getX(), (float)square.getY(), (float)int1, 0);
							float4 -= (float)int2;
							float5 -= (float)int3;
							float float6 = (float)(32 * Core.TileScale);
							float float7 = (float)(96 * Core.TileScale);
							float4 -= float6;
							float5 -= float7;
							int int8 = IsoCamera.frameState.playerIndex;
							int int9 = square.getVertLight(0, int8);
							int int10 = square.getVertLight(1, int8);
							int int11 = square.getVertLight(2, int8);
							int int12 = square.getVertLight(3, int8);
							if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.LightingDebug.getValue()) {
								int9 = -65536;
								int10 = -65536;
								int11 = -16776961;
								int12 = -16776961;
							}

							FloorShaperAttachedSprites.instance.setVertColors(int9, int10, int11, int12);
							FloorShaperDiamond.instance.setVertColors(int9, int10, int11, int12);
							for (int8 = 0; int8 < 2; ++int8) {
								if (float2 > float1) {
									this.renderSnowTileGeneral(snowGrid, float1, square, int5, int6, int7, (int)float4, (int)float5, int8);
									this.renderSnowTileGeneral(snowGrid2, float2, square, int5, int6, int7, (int)float4, (int)float5, int8);
								} else {
									this.renderSnowTileGeneral(snowGrid2, float2, square, int5, int6, int7, (int)float4, (int)float5, int8);
									this.renderSnowTileGeneral(snowGrid, float1, square, int5, int6, int7, (int)float4, (int)float5, int8);
								}
							}
						}
					}

					IndieGL.StartShader((Shader)null);
				}
			}
		}
	}

	private void renderSnowTileGeneral(IsoCell.SnowGrid snowGrid, float float1, IsoGridSquare square, int int1, int int2, int int3, int int4, int int5, int int6) {
		if (!(float1 <= 0.0F)) {
			Texture texture = snowGrid.grid[int2][int3][int6];
			if (texture != null) {
				if (int6 == 0) {
					this.renderSnowTile(snowGrid, int2, int3, int6, square, int1, texture, int4, int5, float1);
				} else if (int1 == 0) {
					byte byte1 = snowGrid.gridType[int2][int3][int6];
					this.renderSnowTileBase(texture, int4, int5, float1, byte1 < this.m_snowFirstNonSquare);
				}
			}
		}
	}

	private void renderSnowTileBase(Texture texture, int int1, int int2, float float1, boolean boolean1) {
		Object object = boolean1 ? FloorShaperDiamond.instance : FloorShaperAttachedSprites.instance;
		((FloorShaper)object).setAlpha4(float1);
		texture.render((float)int1, (float)int2, (float)texture.getWidth(), (float)texture.getHeight(), 1.0F, 1.0F, 1.0F, float1, (Consumer)object);
	}

	private void renderSnowTile(IsoCell.SnowGrid snowGrid, int int1, int int2, int int3, IsoGridSquare square, int int4, Texture texture, int int5, int int6, float float1) {
		if (int4 == 0) {
			byte byte1 = snowGrid.gridType[int1][int2][int3];
			this.renderSnowTileBase(texture, int5, int6, float1, byte1 < this.m_snowFirstNonSquare);
		} else {
			int int7 = 0;
			boolean boolean1 = snowGrid.check(int1, int2);
			boolean boolean2 = (int4 & 1) == 1 && (boolean1 || snowGrid.check(int1, int2 - 1));
			boolean boolean3 = (int4 & 2) == 2 && (boolean1 || snowGrid.check(int1 + 1, int2));
			boolean boolean4 = (int4 & 4) == 4 && (boolean1 || snowGrid.check(int1, int2 + 1));
			boolean boolean5 = (int4 & 8) == 8 && (boolean1 || snowGrid.check(int1 - 1, int2));
			if (boolean2) {
				++int7;
			}

			if (boolean4) {
				++int7;
			}

			if (boolean3) {
				++int7;
			}

			if (boolean5) {
				++int7;
			}

			IsoCell.SnowGridTiles snowGridTiles = null;
			IsoCell.SnowGridTiles snowGridTiles2 = null;
			boolean boolean6 = false;
			if (int7 != 0) {
				if (int7 == 1) {
					if (boolean2) {
						snowGridTiles = this.snowGridTiles_Strip[0];
					} else if (boolean4) {
						snowGridTiles = this.snowGridTiles_Strip[1];
					} else if (boolean3) {
						snowGridTiles = this.snowGridTiles_Strip[3];
					} else if (boolean5) {
						snowGridTiles = this.snowGridTiles_Strip[2];
					}
				} else if (int7 == 2) {
					if (boolean2 && boolean4) {
						snowGridTiles = this.snowGridTiles_Strip[0];
						snowGridTiles2 = this.snowGridTiles_Strip[1];
					} else if (boolean3 && boolean5) {
						snowGridTiles = this.snowGridTiles_Strip[2];
						snowGridTiles2 = this.snowGridTiles_Strip[3];
					} else if (boolean2) {
						snowGridTiles = this.snowGridTiles_Edge[boolean5 ? 0 : 3];
					} else if (boolean4) {
						snowGridTiles = this.snowGridTiles_Edge[boolean5 ? 2 : 1];
					} else if (boolean5) {
						snowGridTiles = this.snowGridTiles_Edge[boolean2 ? 0 : 2];
					} else if (boolean3) {
						snowGridTiles = this.snowGridTiles_Edge[boolean2 ? 3 : 1];
					}
				} else if (int7 == 3) {
					if (!boolean2) {
						snowGridTiles = this.snowGridTiles_Cove[1];
					} else if (!boolean4) {
						snowGridTiles = this.snowGridTiles_Cove[0];
					} else if (!boolean3) {
						snowGridTiles = this.snowGridTiles_Cove[2];
					} else if (!boolean5) {
						snowGridTiles = this.snowGridTiles_Cove[3];
					}

					boolean6 = true;
				} else if (int7 == 4) {
					snowGridTiles = this.snowGridTiles_Enclosed;
					boolean6 = true;
				}

				if (snowGridTiles != null) {
					int int8 = (square.getX() + square.getY()) % snowGridTiles.size();
					texture = snowGridTiles.get(int8);
					if (texture != null) {
						this.renderSnowTileBase(texture, int5, int6, float1, boolean6);
					}

					if (snowGridTiles2 != null) {
						texture = snowGridTiles2.get(int8);
						if (texture != null) {
							this.renderSnowTileBase(texture, int5, int6, float1, false);
						}
					}
				}
			}
		}
	}

	private static int getShoreInt(IsoGridSquare square) {
		int int1 = 0;
		if (isSnowShore(square, 0, -1)) {
			int1 |= 1;
		}

		if (isSnowShore(square, 1, 0)) {
			int1 |= 2;
		}

		if (isSnowShore(square, 0, 1)) {
			int1 |= 4;
		}

		if (isSnowShore(square, -1, 0)) {
			int1 |= 8;
		}

		return int1;
	}

	private static boolean isSnowShore(IsoGridSquare square, int int1, int int2) {
		IsoGridSquare square2 = IsoWorld.instance.getCell().getGridSquare(square.getX() + int1, square.getY() + int2, 0);
		return square2 != null && !square2.getProperties().Is(IsoFlagType.water);
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
				MovingObjectUpdateScheduler.instance.removeObject(movingObject);
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
		MovingObjectUpdateScheduler.instance.update();
		for (int int1 = 0; int1 < this.ZombieList.size(); ++int1) {
			IsoZombie zombie = (IsoZombie)this.ZombieList.get(int1);
			zombie.updateVocalProperties();
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
				Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, (String)null, exception);
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

	public void addToProcessIsoObjectRemove(IsoObject object) {
		if (object != null) {
			if (this.ProcessIsoObject.contains(object)) {
				if (!this.ProcessIsoObjectRemove.contains(object)) {
					this.ProcessIsoObjectRemove.add(object);
				}
			}
		}
	}

	public void addToStaticUpdaterObjectList(IsoObject object) {
		if (object != null) {
			if (!this.StaticUpdaterObjectList.contains(object)) {
				this.StaticUpdaterObjectList.add(object);
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

	public void addToProcessWorldItems(IsoWorldInventoryObject worldInventoryObject) {
		if (worldInventoryObject != null) {
			this.ProcessWorldItemsRemove.remove(worldInventoryObject);
			if (!this.ProcessWorldItems.contains(worldInventoryObject)) {
				this.ProcessWorldItems.add(worldInventoryObject);
			}
		}
	}

	public void addToProcessWorldItemsRemove(IsoWorldInventoryObject worldInventoryObject) {
		if (worldInventoryObject != null) {
			if (!this.ProcessWorldItemsRemove.contains(worldInventoryObject)) {
				this.ProcessWorldItemsRemove.add(worldInventoryObject);
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
		int int4 = Math.min(int3 + lot.info.levels, int3 + 8);
		for (int int5 = int1; int5 < int1 + lot.info.width; ++int5) {
			for (int int6 = int2; int6 < int2 + lot.info.height; ++int6) {
				for (int int7 = int3; int7 < int4; ++int7) {
					int int8 = int5 - int1;
					int int9 = int6 - int2;
					int int10 = int7 - int3;
					if (int5 < this.width && int6 < this.height && int5 >= 0 && int6 >= 0 && int7 >= 0) {
						int int11 = int8 + int9 * 10 + int10 * 100;
						int int12 = lot.m_offsetInData[int11];
						if (int12 != -1) {
							int int13 = lot.m_data.getQuick(int12);
							if (int13 > 0) {
								boolean boolean2 = false;
								for (int int14 = 0; int14 < int13; ++int14) {
									String string = (String)lot.info.tilesUsed.get(lot.m_data.getQuick(int12 + 1 + int14));
									IsoSprite sprite = (IsoSprite)IsoSpriteManager.instance.NamedMap.get(string);
									if (sprite == null) {
										Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, "Missing tile definition: " + string);
									} else {
										IsoGridSquare square = this.getGridSquare(int5, int6, int7);
										if (square == null) {
											if (IsoGridSquare.loadGridSquareCache != null) {
												square = IsoGridSquare.getNew(IsoGridSquare.loadGridSquareCache, this, (SliceY)null, int5, int6, int7);
											} else {
												square = IsoGridSquare.getNew(this, (SliceY)null, int5, int6, int7);
											}

											this.ChunkMap[IsoPlayer.getPlayerIndex()].setGridSquare(square, int5, int6, int7);
										} else {
											if (boolean1 && int14 == 0 && sprite.getProperties().Is(IsoFlagType.solidfloor) && (!sprite.Properties.Is(IsoFlagType.hidewalls) || int13 > 1)) {
												boolean2 = true;
											}

											if (boolean2 && int14 == 0) {
												square.getObjects().clear();
											}
										}

										CellLoader.DoTileObjectCreation(sprite, sprite.getType(), square, this, int5, int6, int7, string);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void PlaceLot(IsoLot lot, int int1, int int2, int int3, IsoChunk chunk, int int4, int int5) {
		int4 *= 10;
		int5 *= 10;
		IsoMetaGrid metaGrid = IsoWorld.instance.getMetaGrid();
		int int6 = Math.min(int3 + lot.info.levels, int3 + 8);
		int int7;
		int int8;
		int int9;
		try {
			for (int int10 = int4 + int1; int10 < int4 + int1 + 10; ++int10) {
				for (int7 = int5 + int2; int7 < int5 + int2 + 10; ++int7) {
					for (int8 = int3; int8 < int6; ++int8) {
						int9 = int10 - int4 - int1;
						int int11 = int7 - int5 - int2;
						int int12 = int8 - int3;
						if (int10 < int4 + 10 && int7 < int5 + 10 && int10 >= int4 && int7 >= int5 && int8 >= 0) {
							int int13 = int9 + int11 * 10 + int12 * 100;
							int int14 = lot.m_offsetInData[int13];
							if (int14 != -1) {
								int int15 = lot.m_data.getQuick(int14);
								if (int15 > 0) {
									IsoGridSquare square = chunk.getGridSquare(int10 - int4, int7 - int5, int8);
									if (square == null) {
										if (IsoGridSquare.loadGridSquareCache != null) {
											square = IsoGridSquare.getNew(IsoGridSquare.loadGridSquareCache, this, (SliceY)null, int10, int7, int8);
										} else {
											square = IsoGridSquare.getNew(this, (SliceY)null, int10, int7, int8);
										}

										square.setX(int10);
										square.setY(int7);
										square.setZ(int8);
										chunk.setSquare(int10 - int4, int7 - int5, int8, square);
									}

									int int16;
									for (int int17 = -1; int17 <= 1; ++int17) {
										for (int16 = -1; int16 <= 1; ++int16) {
											if ((int17 != 0 || int16 != 0) && int17 + int10 - int4 >= 0 && int17 + int10 - int4 < 10 && int16 + int7 - int5 >= 0 && int16 + int7 - int5 < 10) {
												IsoGridSquare square2 = chunk.getGridSquare(int10 + int17 - int4, int7 + int16 - int5, int8);
												if (square2 == null) {
													square2 = IsoGridSquare.getNew(this, (SliceY)null, int10 + int17, int7 + int16, int8);
													chunk.setSquare(int10 + int17 - int4, int7 + int16 - int5, int8, square2);
												}
											}
										}
									}

									RoomDef roomDef = metaGrid.getRoomAt(int10, int7, int8);
									int16 = roomDef != null ? roomDef.ID : -1;
									square.setRoomID(int16);
									square.ResetIsoWorldRegion();
									roomDef = metaGrid.getEmptyOutsideAt(int10, int7, int8);
									if (roomDef != null) {
										IsoRoom room = chunk.getRoom(roomDef.ID);
										square.roofHideBuilding = room == null ? null : room.building;
									}

									boolean boolean1 = true;
									for (int int18 = 0; int18 < int15; ++int18) {
										String string = (String)lot.info.tilesUsed.get(lot.m_data.get(int14 + 1 + int18));
										if (!lot.info.bFixed2x) {
											string = IsoChunk.Fix2x(string);
										}

										IsoSprite sprite = (IsoSprite)IsoSpriteManager.instance.NamedMap.get(string);
										if (sprite == null) {
											Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, "Missing tile definition: " + string);
										} else {
											if (int18 == 0 && sprite.getProperties().Is(IsoFlagType.solidfloor) && (!sprite.Properties.Is(IsoFlagType.hidewalls) || int15 > 1)) {
												boolean1 = true;
											}

											if (boolean1 && int18 == 0) {
												square.getObjects().clear();
											}

											CellLoader.DoTileObjectCreation(sprite, sprite.getType(), square, this, int10, int7, int8, string);
										}
									}

									square.FixStackableObjects();
								}
							}
						}
					}
				}
			}
		} catch (Exception exception) {
			DebugLog.log("Failed to load chunk, blocking out area");
			ExceptionLogger.logException(exception);
			for (int7 = int4 + int1; int7 < int4 + int1 + 10; ++int7) {
				for (int8 = int5 + int2; int8 < int5 + int2 + 10; ++int8) {
					for (int9 = int3; int9 < int6; ++int9) {
						chunk.setSquare(int7 - int4 - int1, int8 - int5 - int2, int9 - int3, (IsoGridSquare)null);
						this.setCacheGridSquare(int7, int8, int9, (IsoGridSquare)null);
					}
				}
			}
		}
	}

	public void setDrag(KahluaTable kahluaTable, int int1) {
		if (int1 >= 0 && int1 < 4) {
			if (this.drag[int1] != null && this.drag[int1] != kahluaTable) {
				Object object = this.drag[int1].rawget("deactivate");
				if (object instanceof JavaFunction || object instanceof LuaClosure) {
					LuaManager.caller.pcallvoid(LuaManager.thread, object, (Object)this.drag[int1]);
				}
			}

			this.drag[int1] = kahluaTable;
		}
	}

	public KahluaTable getDrag(int int1) {
		return int1 >= 0 && int1 < 4 ? this.drag[int1] : null;
	}

	public boolean DoBuilding(int int1, boolean boolean1) {
		boolean boolean2;
		try {
			IsoCell.s_performance.isoCellDoBuilding.start();
			boolean2 = this.doBuildingInternal(int1, boolean1);
		} finally {
			IsoCell.s_performance.isoCellDoBuilding.end();
		}

		return boolean2;
	}

	private boolean doBuildingInternal(int int1, boolean boolean1) {
		if (UIManager.getPickedTile() != null && this.drag[int1] != null && JoypadManager.instance.getFromPlayer(int1) == null) {
			if (!IsoWorld.instance.isValidSquare((int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ())) {
				return false;
			}

			IsoGridSquare square = this.getGridSquare((int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ());
			if (!boolean1) {
				if (square == null) {
					square = this.createNewGridSquare((int)UIManager.getPickedTile().x, (int)UIManager.getPickedTile().y, (int)IsoCamera.CamCharacter.getZ(), true);
					if (square == null) {
						return false;
					}
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

	public ArrayList getWindowList() {
		return this.WindowList;
	}

	public void addToWindowList(IsoWindow window) {
		if (!GameServer.bServer) {
			if (window != null) {
				if (!this.WindowList.contains(window)) {
					this.WindowList.add(window);
				}
			}
		}
	}

	public void removeFromWindowList(IsoWindow window) {
		this.WindowList.remove(window);
	}

	public ArrayList getObjectList() {
		return this.ObjectList;
	}

	public IsoRoom getRoom(int int1) {
		IsoRoom room = this.ChunkMap[IsoPlayer.getPlayerIndex()].getRoom(int1);
		return room;
	}

	public ArrayList getPushableObjectList() {
		return this.PushableObjectList;
	}

	public HashMap getBuildingScores() {
		return this.BuildingScores;
	}

	public ArrayList getRoomList() {
		return this.RoomList;
	}

	public ArrayList getStaticUpdaterObjectList() {
		return this.StaticUpdaterObjectList;
	}

	public ArrayList getZombieList() {
		return this.ZombieList;
	}

	public ArrayList getRemoteSurvivorList() {
		return this.RemoteSurvivorList;
	}

	public ArrayList getRemoveList() {
		return this.removeList;
	}

	public ArrayList getAddList() {
		return this.addList;
	}

	public void addMovingObject(IsoMovingObject movingObject) {
		this.addList.add(movingObject);
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

	public ArrayList getProcessItemsRemove() {
		return this.ProcessItemsRemove;
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

	public boolean isSafeToAdd() {
		return this.safeToAdd;
	}

	public void setSafeToAdd(boolean boolean1) {
		this.safeToAdd = boolean1;
	}

	public Stack getLamppostPositions() {
		return this.LamppostPositions;
	}

	public IsoLightSource getLightSourceAt(int int1, int int2, int int3) {
		for (int int4 = 0; int4 < this.LamppostPositions.size(); ++int4) {
			IsoLightSource lightSource = (IsoLightSource)this.LamppostPositions.get(int4);
			if (lightSource.getX() == int1 && lightSource.getY() == int2 && lightSource.getZ() == int3) {
				return lightSource;
			}
		}

		return null;
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
		this.LamppostPositions.clear();
		this.ProcessItems.clear();
		this.ProcessItemsRemove.clear();
		this.ProcessWorldItems.clear();
		this.ProcessWorldItemsRemove.clear();
		this.BuildingScores.clear();
		this.BuildingList.clear();
		this.WindowList.clear();
		this.PushableObjectList.clear();
		this.RoomList.clear();
		this.SurvivorList.clear();
		this.ObjectList.clear();
		this.ZombieList.clear();
		for (int1 = 0; int1 < this.ChunkMap.length; ++int1) {
			this.ChunkMap[int1].Dispose();
			this.ChunkMap[int1] = null;
		}

		for (int1 = 0; int1 < this.gridSquares.length; ++int1) {
			if (this.gridSquares[int1] != null) {
				Arrays.fill(this.gridSquares[int1], (Object)null);
				this.gridSquares[int1] = null;
			}
		}
	}

	@LuaMethod(name = "getGridSquare")
	public IsoGridSquare getGridSquare(double double1, double double2, double double3) {
		return GameServer.bServer ? ServerMap.instance.getGridSquare((int)double1, (int)double2, (int)double3) : this.getGridSquare((int)double1, (int)double2, (int)double3);
	}

	@LuaMethod(name = "getOrCreateGridSquare")
	public IsoGridSquare getOrCreateGridSquare(double double1, double double2, double double3) {
		IsoGridSquare square;
		if (GameServer.bServer) {
			square = ServerMap.instance.getGridSquare((int)double1, (int)double2, (int)double3);
			if (square == null) {
				square = IsoGridSquare.getNew(this, (SliceY)null, (int)double1, (int)double2, (int)double3);
				ServerMap.instance.setGridSquare((int)double1, (int)double2, (int)double3, square);
				this.ConnectNewSquare(square, true);
			}

			return square;
		} else {
			square = this.getGridSquare((int)double1, (int)double2, (int)double3);
			if (square == null) {
				square = IsoGridSquare.getNew(this, (SliceY)null, (int)double1, (int)double2, (int)double3);
				this.ConnectNewSquare(square, true);
			}

			return square;
		}
	}

	public void setCacheGridSquare(int int1, int int2, int int3, IsoGridSquare square) {
		assert square == null || int1 == square.getX() && int2 == square.getY() && int3 == square.getZ();
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

	public void setCacheChunk(IsoChunk chunk) {
		if (!GameServer.bServer) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				this.setCacheChunk(chunk, int1);
			}
		}
	}

	public void setCacheChunk(IsoChunk chunk, int int1) {
		if (!GameServer.bServer) {
			int int2 = IsoChunkMap.ChunkWidthInTiles;
			IsoChunkMap chunkMap = this.ChunkMap[int1];
			if (!chunkMap.ignore) {
				int int3 = chunk.wx - chunkMap.getWorldXMin();
				int int4 = chunk.wy - chunkMap.getWorldYMin();
				if (int3 >= 0 && int3 < IsoChunkMap.ChunkGridWidth && int4 >= 0 && int4 < IsoChunkMap.ChunkGridWidth) {
					IsoGridSquare[] gridSquareArray = this.gridSquares[int1];
					for (int int5 = 0; int5 < 8; ++int5) {
						for (int int6 = 0; int6 < 10; ++int6) {
							for (int int7 = 0; int7 < 10; ++int7) {
								IsoGridSquare square = chunk.squares[int5][int7 + int6 * 10];
								int int8 = int3 * 10 + int7;
								int int9 = int4 * 10 + int6;
								gridSquareArray[int8 + int9 * int2 + int5 * int2 * int2] = square;
							}
						}
					}
				}
			}
		}
	}

	public void clearCacheGridSquare(int int1) {
		if (!GameServer.bServer) {
			int int2 = IsoChunkMap.ChunkWidthInTiles;
			this.gridSquares[int1] = new IsoGridSquare[int2 * int2 * 8];
		}
	}

	public void setCacheGridSquareLocal(int int1, int int2, int int3, IsoGridSquare square, int int4) {
		if (!GameServer.bServer) {
			int int5 = IsoChunkMap.ChunkWidthInTiles;
			if (int3 < 8 && int3 >= 0 && int1 >= 0 && int1 < int5 && int2 >= 0 && int2 < int5) {
				this.gridSquares[int4][int1 + int2 * int5 + int3 * int5 * int5] = square;
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
						IsoGridSquare square = this.gridSquares[int5][int6 + int7 * int4 + int3 * int4 * int4];
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

	public static IsoCell getInstance() {
		return instance;
	}

	public void render() {
		IsoCell.s_performance.isoCellRender.invokeAndMeasure(this, IsoCell::renderInternal);
	}

	private void renderInternal() {
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
			this.StencilX1 = int6 - (int)IsoCamera.cameras[int1].RightClickX;
			this.StencilY1 = int7 - (int)IsoCamera.cameras[int1].RightClickY;
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
					if (this.nearestVisibleZombie[int1].isDead()) {
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
						if (this.lastPlayerAngle[int9] == null) {
							this.lastPlayerAngle[int9] = new Vector2(player2.getForwardDirection());
							this.playerCutawaysDirty[int9] = true;
						} else if (player2.getForwardDirection().dot(this.lastPlayerAngle[int9]) < 0.98F) {
							this.lastPlayerAngle[int9].set(player2.getForwardDirection());
							this.playerCutawaysDirty[int9] = true;
						}

						IsoDirections directions = IsoDirections.fromAngle(player2.getForwardDirection());
						if (this.lastPlayerSquare[int9] != square3 || this.lastPlayerSquareHalf[int9] != boolean2 || this.lastPlayerDir[int9] != directions) {
							this.playerCutawaysDirty[int9] = true;
							this.lastPlayerSquare[int9] = square3;
							this.lastPlayerSquareHalf[int9] = boolean2;
							this.lastPlayerDir[int9] = directions;
							building = square3.getBuilding();
							this.playerWindowPeekingRoomId[int9] = -1;
							this.GetBuildingsInFrontOfCharacter((ArrayList)this.playerOccluderBuildings.get(int9), square3, boolean2);
							if (this.playerOccluderBuildingsArr[int1] == null) {
								this.playerOccluderBuildingsArr[int1] = new IsoBuilding[500];
							}

							this.playerHidesOrphanStructures[int9] = this.bOccludedByOrphanStructureFlag;
							if (building == null && !player2.bRemote) {
								building = this.GetPeekedInBuilding(square3, directions);
								if (building != null) {
									this.playerWindowPeekingRoomId[int9] = this.playerPeekedRoomId;
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
							boolean[] booleanArray;
							int int11;
							IsoGridSquare square4;
							ArrayList arrayList2;
							int int12;
							for (int11 = 0; int11 < this.gridSquaresTempLeft.size(); ++int11) {
								square4 = (IsoGridSquare)this.gridSquaresTempLeft.get(int11);
								if (square4.getCanSee(int1) && (square4.getBuilding() == null || square4.getBuilding() == square.getBuilding())) {
									arrayList2 = this.GetBuildingsInFrontOfMustSeeSquare(square4, IsoGridOcclusionData.OcclusionFilter.Right);
									for (int12 = 0; int12 < arrayList2.size(); ++int12) {
										this.AddUniqueToBuildingList((ArrayList)this.otherOccluderBuildings.get(int1), (IsoBuilding)arrayList2.get(int12));
									}

									booleanArray = this.playerHidesOrphanStructures;
									booleanArray[int1] |= this.bOccludedByOrphanStructureFlag;
								}
							}

							for (int11 = 0; int11 < this.gridSquaresTempRight.size(); ++int11) {
								square4 = (IsoGridSquare)this.gridSquaresTempRight.get(int11);
								if (square4.getCanSee(int1) && (square4.getBuilding() == null || square4.getBuilding() == square.getBuilding())) {
									arrayList2 = this.GetBuildingsInFrontOfMustSeeSquare(square4, IsoGridOcclusionData.OcclusionFilter.Left);
									for (int12 = 0; int12 < arrayList2.size(); ++int12) {
										this.AddUniqueToBuildingList((ArrayList)this.otherOccluderBuildings.get(int1), (IsoBuilding)arrayList2.get(int12));
									}

									booleanArray = this.playerHidesOrphanStructures;
									booleanArray[int1] |= this.bOccludedByOrphanStructureFlag;
								}
							}

							ArrayList arrayList3 = (ArrayList)this.otherOccluderBuildings.get(int1);
							if (this.otherOccluderBuildingsArr[int1] == null) {
								this.otherOccluderBuildingsArr[int1] = new IsoBuilding[500];
							}

							for (int int13 = 0; int13 < arrayList3.size(); ++int13) {
								IsoBuilding building3 = (IsoBuilding)arrayList3.get(int13);
								this.otherOccluderBuildingsArr[int1][int13] = building3;
							}

							this.otherOccluderBuildingsArr[int1][arrayList3.size()] = null;
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

							for (int int14 = 0; int14 < arrayList4.size(); ++int14) {
								building = (IsoBuilding)arrayList4.get(int14);
								this.zombieOccluderBuildingsArr[int1][int14] = building;
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
					this.playerCutawaysDirty[int7] = true;
				}

				this.playerWindowPeekingRoomId[int1] = -1;
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
				this.playerWindowPeekingRoomId[int6] = -1;
				IsoPlayer player3 = IsoPlayer.players[int6];
				if (player3 != null) {
					IsoBuilding building4 = player3.getCurrentBuilding();
					if (building4 == null) {
						IsoDirections directions2 = IsoDirections.fromAngle(player3.getForwardDirection());
						building4 = this.GetPeekedInBuilding(player3.getCurrentSquare(), directions2);
						if (building4 != null) {
							this.playerWindowPeekingRoomId[int6] = this.playerPeekedRoomId;
						}
					}
				}
			}
		}

		if (IsoCamera.CamCharacter != null && IsoCamera.CamCharacter.getCurrentSquare() != null && IsoCamera.CamCharacter.getCurrentSquare().getProperties().Is(IsoFlagType.hidewalls)) {
			this.maxZ = (int)IsoCamera.CamCharacter.getZ() + 1;
		}

		this.bRendering = true;
		try {
			this.RenderTiles(int3);
		} catch (Exception exception) {
			this.bRendering = false;
			Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, (String)null, exception);
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

		IsoTree.renderChopTreeIndicators();
		if (Core.bDebug) {
		}

		this.lastMinX = this.minX;
		this.lastMinY = this.minY;
		this.DoBuilding(IsoPlayer.getPlayerIndex(), true);
		this.renderRain();
	}

	public void invalidatePeekedRoom(int int1) {
		this.lastPlayerDir[int1] = IsoDirections.Max;
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
	}

	private void GetBuildingsInFrontOfCharacter(ArrayList arrayList, IsoGridSquare square, boolean boolean1) {
		arrayList.clear();
		this.bOccludedByOrphanStructureFlag = false;
		if (square != null) {
			int int1 = square.getX();
			int int2 = square.getY();
			int int3 = square.getZ();
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

	private IsoBuilding GetPeekedInBuilding(IsoGridSquare square, IsoDirections directions) {
		this.playerPeekedRoomId = -1;
		if (square == null) {
			return null;
		} else {
			IsoGridSquare square2;
			IsoBuilding building;
			if ((directions == IsoDirections.NW || directions == IsoDirections.N || directions == IsoDirections.NE) && LosUtil.lineClear(this, square.x, square.y, square.z, square.x, square.y - 1, square.z, false) != LosUtil.TestResults.Blocked) {
				square2 = square.nav[IsoDirections.N.index()];
				if (square2 != null) {
					building = square2.getBuilding();
					if (building != null) {
						this.playerPeekedRoomId = square2.getRoomID();
						return building;
					}
				}
			}

			if ((directions == IsoDirections.SW || directions == IsoDirections.W || directions == IsoDirections.NW) && LosUtil.lineClear(this, square.x, square.y, square.z, square.x - 1, square.y, square.z, false) != LosUtil.TestResults.Blocked) {
				square2 = square.nav[IsoDirections.W.index()];
				if (square2 != null) {
					building = square2.getBuilding();
					if (building != null) {
						this.playerPeekedRoomId = square2.getRoomID();
						return building;
					}
				}
			}

			if ((directions == IsoDirections.SE || directions == IsoDirections.S || directions == IsoDirections.SW) && LosUtil.lineClear(this, square.x, square.y, square.z, square.x, square.y + 1, square.z, false) != LosUtil.TestResults.Blocked) {
				square2 = square.nav[IsoDirections.S.index()];
				if (square2 != null) {
					building = square2.getBuilding();
					if (building != null) {
						this.playerPeekedRoomId = square2.getRoomID();
						return building;
					}
				}
			}

			if ((directions == IsoDirections.NE || directions == IsoDirections.E || directions == IsoDirections.SE) && LosUtil.lineClear(this, square.x, square.y, square.z, square.x + 1, square.y, square.z, false) != LosUtil.TestResults.Blocked) {
				square2 = square.nav[IsoDirections.E.index()];
				if (square2 != null) {
					building = square2.getBuilding();
					if (building != null) {
						this.playerPeekedRoomId = square2.getRoomID();
						return building;
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

	public void update() {
		IsoCell.s_performance.isoCellUpdate.invokeAndMeasure(this, IsoCell::updateInternal);
	}

	private void updateInternal() {
		MovingObjectUpdateScheduler.instance.startFrame();
		IsoSprite.alphaStep = 0.075F * (GameTime.getInstance().getMultiplier() / 1.6F);
		++IsoGridSquare.gridSquareCacheEmptyTimer;
		this.ProcessSpottedRooms();
		if (!GameServer.bServer) {
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				if (IsoPlayer.players[int1] != null && (!IsoPlayer.players[int1].isDead() || IsoPlayer.players[int1].ReanimatedCorpse != null)) {
					IsoPlayer.setInstance(IsoPlayer.players[int1]);
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
		if (GameClient.bClient && (NetworkZombieSimulator.getInstance().anyUnknownZombies() && GameClient.instance.sendZombieRequestsTimer.Check() || GameClient.instance.sendZombieTimer.Check())) {
			NetworkZombieSimulator.getInstance().send();
			GameClient.instance.sendZombieTimer.Reset();
			GameClient.instance.sendZombieRequestsTimer.Reset();
		}

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

		if (!GameTime.isGamePaused()) {
			this.rainScroll += (float)this.rainSpeed / 10.0F * 0.075F * (30.0F / (float)PerformanceSettings.getLockFPS());
			if (this.rainScroll > 1.0F) {
				this.rainScroll = 0.0F;
			}
		}

		if (!GameServer.bServer) {
			this.updateWeatherFx();
		}
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
				stack.add((IsoRoom)this.RoomList.get(int1));
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
				if (!room.def.bDoneSpawn) {
					room.def.bDoneSpawn = true;
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

		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			this.ChunkMap[int1].Save();
		}

		dataOutputStream.writeInt(this.width);
		dataOutputStream.writeInt(this.height);
		dataOutputStream.writeInt(MaxHeight);
		File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_t.bin");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		dataOutputStream = new DataOutputStream(new BufferedOutputStream(fileOutputStream));
		GameTime.instance.save(dataOutputStream);
		dataOutputStream.flush();
		dataOutputStream.close();
		IsoWorld.instance.MetaGrid.save();
		if (PlayerDB.isAllow()) {
			PlayerDB.getInstance().savePlayers();
		}

		ReanimatedPlayers.instance.saveReanimatedPlayers();
	}

	public boolean LoadPlayer(int int1) throws FileNotFoundException, IOException {
		if (GameClient.bClient) {
			return ClientPlayerDB.getInstance().loadNetworkPlayer();
		} else {
			File file = ZomboidFileSystem.instance.getFileInCurrentSave("map_p.bin");
			if (!file.exists()) {
				PlayerDB.getInstance().importPlayersFromVehiclesDB();
				return PlayerDB.getInstance().loadLocalPlayer(1);
			} else {
				FileInputStream fileInputStream = new FileInputStream(file);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				synchronized (SliceY.SliceBufferLock) {
					SliceY.SliceBuffer.clear();
					int int2 = bufferedInputStream.read(SliceY.SliceBuffer.array());
					SliceY.SliceBuffer.limit(int2);
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
						IsoPlayer.setInstance(new IsoPlayer(instance));
						IsoPlayer.players[0] = IsoPlayer.getInstance();
					}

					IsoPlayer.getInstance().load(SliceY.SliceBuffer, int1);
					fileInputStream.close();
				}

				PlayerDB.getInstance().saveLocalPlayersForce();
				file.delete();
				PlayerDB.getInstance().uploadLocalPlayers2DB();
				return true;
			}
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
		return this.gridSquares[int4][int1 + int2 * int5 + int3 * int5 * int5];
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

	public IsoSpriteManager getSpriteManager() {
		return IsoSpriteManager.instance;
	}

	public static final class PerPlayerRender {
		public final IsoGridStack GridStacks = new IsoGridStack(9);
		public boolean[][][] VisiOccludedFlags;
		public boolean[][] VisiCulledFlags;
		public short[][][] StencilValues;
		public boolean[][] FlattenGrassEtc;
		public int minX;
		public int minY;
		public int maxX;
		public int maxY;

		public void setSize(int int1, int int2) {
			if (this.VisiOccludedFlags == null || this.VisiOccludedFlags.length < int1 || this.VisiOccludedFlags[0].length < int2) {
				this.VisiOccludedFlags = new boolean[int1][int2][2];
				this.VisiCulledFlags = new boolean[int1][int2];
				this.StencilValues = new short[int1][int2][2];
				this.FlattenGrassEtc = new boolean[int1][int2];
			}
		}
	}

	private static class s_performance {
		static final PerformanceProfileProbe isoCellUpdate = new PerformanceProfileProbe("IsoCell.update");
		static final PerformanceProfileProbe isoCellRender = new PerformanceProfileProbe("IsoCell.render");
		static final PerformanceProfileProbe isoCellRenderTiles = new PerformanceProfileProbe("IsoCell.renderTiles");
		static final PerformanceProfileProbe isoCellDoBuilding = new PerformanceProfileProbe("IsoCell.doBuilding");

		static class renderTiles {
			static final PerformanceProfileProbe performRenderTiles = new PerformanceProfileProbe("performRenderTiles");
			static final PerformanceProfileProbe recalculateAnyGridStacks = new PerformanceProfileProbe("recalculateAnyGridStacks");
			static final PerformanceProfileProbe flattenAnyFoliage = new PerformanceProfileProbe("flattenAnyFoliage");
			static final PerformanceProfileProbe renderDebugPhysics = new PerformanceProfileProbe("renderDebugPhysics");
			static final PerformanceProfileProbe renderDebugLighting = new PerformanceProfileProbe("renderDebugLighting");
			static PerformanceProfileProbeList performRenderTilesLayers = PerformanceProfileProbeList.construct("performRenderTiles", 8, IsoCell.s_performance.renderTiles.PperformRenderTilesLayer.class, IsoCell.s_performance.renderTiles.PperformRenderTilesLayer::new);

			static class PperformRenderTilesLayer extends PerformanceProfileProbe {
				final PerformanceProfileProbe renderIsoWater = new PerformanceProfileProbe("renderIsoWater");
				final PerformanceProfileProbe renderFloor = new PerformanceProfileProbe("renderFloor");
				final PerformanceProfileProbe renderPuddles = new PerformanceProfileProbe("renderPuddles");
				final PerformanceProfileProbe renderShore = new PerformanceProfileProbe("renderShore");
				final PerformanceProfileProbe renderSnow = new PerformanceProfileProbe("renderSnow");
				final PerformanceProfileProbe renderBlood = new PerformanceProfileProbe("renderBlood");
				final PerformanceProfileProbe vegetationCorpses = new PerformanceProfileProbe("vegetationCorpses");
				final PerformanceProfileProbe renderFloorShading = new PerformanceProfileProbe("renderFloorShading");
				final PerformanceProfileProbe renderShadows = new PerformanceProfileProbe("renderShadows");
				final PerformanceProfileProbe luaOnPostFloorLayerDraw = new PerformanceProfileProbe("luaOnPostFloorLayerDraw");
				final PerformanceProfileProbe minusFloorCharacters = new PerformanceProfileProbe("minusFloorCharacters");

				PperformRenderTilesLayer(String string) {
					super(string);
				}
			}
		}
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
		public final Texture[][][] grid;
		public final byte[][][] gridType;

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

				IsoCell.this.m_snowFirstNonSquare = byte2;
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

		public boolean checkAny(int int1, int int2) {
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
					return this.grid[int1][int2][0] != null;
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
		private final ArrayList textures = new ArrayList();

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

		protected Texture get(int int1) {
			return (Texture)this.textures.get(int1);
		}

		protected int size() {
			return this.textures.size();
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

	public static enum BuildingSearchCriteria {

		Food,
		Defense,
		Wood,
		Weapons,
		General;

		private static IsoCell.BuildingSearchCriteria[] $values() {
			return new IsoCell.BuildingSearchCriteria[]{Food, Defense, Wood, Weapons, General};
		}
	}
}
