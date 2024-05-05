package zombie.iso;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.GL20;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.MapCollisionData;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.ZomboidBitFlag;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.states.ZombieIdleState;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.Moodles.MoodleType;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.RenderSettings;
import zombie.core.opengl.Shader;
import zombie.core.opengl.ShaderProgram;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.erosion.ErosionData;
import zombie.erosion.categories.ErosionCategory;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.areas.SafeHouse;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.areas.isoregion.regions.IWorldRegion;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoBrokenGlass;
import zombie.iso.objects.IsoCarBatteryCharger;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoRainSplash;
import zombie.iso.objects.IsoRaindrop;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWaveSignal;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.RainManager;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.iso.sprite.shapers.FloorShaper;
import zombie.iso.sprite.shapers.FloorShaperAttachedSprites;
import zombie.iso.sprite.shapers.FloorShaperDeDiamond;
import zombie.iso.sprite.shapers.FloorShaperDiamond;
import zombie.iso.sprite.shapers.WallShaperN;
import zombie.iso.sprite.shapers.WallShaperW;
import zombie.iso.sprite.shapers.WallShaperWhole;
import zombie.iso.weather.fx.WeatherFxMask;
import zombie.meta.Meta;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerLOS;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;
import zombie.util.list.PZArrayList;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;


public final class IsoGridSquare {
	private boolean hasTree;
	private ArrayList LightInfluenceB;
	private ArrayList LightInfluenceG;
	private ArrayList LightInfluenceR;
	public final IsoGridSquare[] nav = new IsoGridSquare[8];
	public int collideMatrix = -1;
	public int pathMatrix = -1;
	public int visionMatrix = -1;
	public IsoRoom room = null;
	public IsoGridSquare w;
	public IsoGridSquare nw;
	public IsoGridSquare sw;
	public IsoGridSquare s;
	public IsoGridSquare n;
	public IsoGridSquare ne;
	public IsoGridSquare se;
	public IsoGridSquare e;
	public boolean haveSheetRope = false;
	private IWorldRegion isoWorldRegion;
	private boolean hasSetIsoWorldRegion = false;
	public int ObjectsSyncCount = 0;
	public IsoBuilding roofHideBuilding;
	public boolean bFlattenGrassEtc;
	private static final long VisiFlagTimerPeriod_ms = 750L;
	private final boolean[] playerCutawayFlags = new boolean[4];
	private final long[] playerCutawayFlagLockUntilTimes = new long[4];
	private final boolean[] targetPlayerCutawayFlags = new boolean[4];
	private final boolean[] playerIsDissolvedFlags = new boolean[4];
	private final long[] playerIsDissolvedFlagLockUntilTimes = new long[4];
	private final boolean[] targetPlayerIsDissolvedFlags = new boolean[4];
	private IsoWaterGeometry water = null;
	private IsoPuddlesGeometry puddles = null;
	private float puddlesCacheSize = -1.0F;
	private float puddlesCacheLevel = -1.0F;
	public final IsoGridSquare.ILighting[] lighting = new IsoGridSquare.ILighting[4];
	public int x;
	public int y;
	public int z;
	private int CachedScreenValue = -1;
	public float CachedScreenX;
	public float CachedScreenY;
	private static long torchTimer = 0L;
	public boolean SolidFloorCached = false;
	public boolean SolidFloor = false;
	private boolean CacheIsFree = false;
	private boolean CachedIsFree = false;
	public IsoChunk chunk;
	public int roomID = -1;
	public Integer ID = -999;
	public IsoMetaGrid.Zone zone;
	private final ArrayList DeferedCharacters = new ArrayList();
	private int DeferredCharacterTick = -1;
	private final ArrayList StaticMovingObjects = new ArrayList(0);
	private final ArrayList MovingObjects = new ArrayList(0);
	protected final PZArrayList Objects = new PZArrayList(IsoObject.class, 2);
	private final ArrayList WorldObjects = new ArrayList();
	final ZomboidBitFlag hasTypes;
	private final PropertyContainer Properties;
	private final ArrayList SpecialObjects;
	public boolean haveRoof;
	private boolean burntOut;
	private boolean bHasFlies;
	private IsoGridOcclusionData OcclusionDataCache;
	public static final ConcurrentLinkedQueue isoGridSquareCache = new ConcurrentLinkedQueue();
	public static ArrayDeque loadGridSquareCache;
	private boolean overlayDone;
	private KahluaTable table;
	private int trapPositionX;
	private int trapPositionY;
	private int trapPositionZ;
	private boolean haveElectricity;
	public static int gridSquareCacheEmptyTimer = 0;
	private static float darkStep = 0.06F;
	public static int RecalcLightTime = 0;
	private static int lightcache = 0;
	public static final ArrayList choices = new ArrayList();
	public static boolean USE_WALL_SHADER = true;
	private static final int cutawayY = 0;
	private static final int cutawayNWWidth = 66;
	private static final int cutawayNWHeight = 226;
	private static final int cutawaySEXCut = 1084;
	private static final int cutawaySEXUncut = 1212;
	private static final int cutawaySEWidth = 6;
	private static final int cutawaySEHeight = 196;
	private static final int cutawayNXFullyCut = 700;
	private static final int cutawayNXCutW = 444;
	private static final int cutawayNXUncut = 828;
	private static final int cutawayNXCutE = 956;
	private static final int cutawayWXFullyCut = 512;
	private static final int cutawayWXCutS = 768;
	private static final int cutawayWXUncut = 896;
	private static final int cutawayWXCutN = 256;
	private static final int cutawayFenceXOffset = 1;
	private static final int cutawayLogWallXOffset = 1;
	private static final int cutawaySpiffoWindowXOffset = -24;
	private static final int cutawayRoof4XOffset = -60;
	private static final int cutawayRoof17XOffset = -46;
	private static final int cutawayRoof28XOffset = -60;
	private static final int cutawayRoof41XOffset = -46;
	private static final ColorInfo lightInfoTemp = new ColorInfo();
	private static final float doorWindowCutawayLightMin = 0.3F;
	private static boolean bWallCutawayW;
	private static boolean bWallCutawayN;
	public boolean isSolidFloorCache;
	public boolean isExteriorCache;
	public boolean isVegitationCache;
	public int hourLastSeen;
	static IsoGridSquare lastLoaded = null;
	public static int IDMax = -1;
	static int col = -1;
	static int path = -1;
	static int pathdoor = -1;
	static int vision = -1;
	public long hashCodeObjects;
	static final Color tr = new Color(1, 1, 1, 1);
	static final Color tl = new Color(1, 1, 1, 1);
	static final Color br = new Color(1, 1, 1, 1);
	static final Color bl = new Color(1, 1, 1, 1);
	static final Color interp1 = new Color(1, 1, 1, 1);
	static final Color interp2 = new Color(1, 1, 1, 1);
	static final Color finalCol = new Color(1, 1, 1, 1);
	public static final IsoGridSquare.CellGetSquare cellGetSquare = new IsoGridSquare.CellGetSquare();
	public boolean propertiesDirty;
	public static boolean UseSlowCollision = false;
	private static boolean bDoSlowPathfinding = false;
	private static final Comparator comp = (var0,var1)->{
    return var0.compareToY(var1);
};
	public static boolean isOnScreenLast = false;
	private float splashX;
	private float splashY;
	private float splashFrame;
	private int splashFrameNum;
	private final ColorInfo[] lightInfo;
	static String[] rainsplashCache = new String[50];
	private static final ColorInfo defColorInfo = new ColorInfo();
	private static final ColorInfo blackColorInfo = new ColorInfo();
	static int colu = 0;
	static int coll = 0;
	static int colr = 0;
	static int colu2 = 0;
	static int coll2 = 0;
	static int colr2 = 0;
	public static boolean CircleStencil = false;
	public static float rmod = 0.0F;
	public static float gmod = 0.0F;
	public static float bmod = 0.0F;
	static final Vector2 tempo = new Vector2();
	static final Vector2 tempo2 = new Vector2();
	private IsoRaindrop RainDrop;
	private IsoRainSplash RainSplash;
	private ErosionData.Square erosion;
	public static final int WALL_TYPE_N = 1;
	public static final int WALL_TYPE_S = 2;
	public static final int WALL_TYPE_W = 4;
	public static final int WALL_TYPE_E = 8;

	public static boolean getMatrixBit(int int1, int int2, int int3, int int4) {
		return getMatrixBit(int1, (byte)int2, (byte)int3, (byte)int4);
	}

	public static boolean getMatrixBit(int int1, byte byte1, byte byte2, byte byte3) {
		return (int1 >> byte1 + byte2 * 3 + byte3 * 9 & 1) != 0;
	}

	public static int setMatrixBit(int int1, int int2, int int3, int int4, boolean boolean1) {
		return setMatrixBit(int1, (byte)int2, (byte)int3, (byte)int4, boolean1);
	}

	public static int setMatrixBit(int int1, byte byte1, byte byte2, byte byte3, boolean boolean1) {
		return boolean1 ? int1 | 1 << byte1 + byte2 * 3 + byte3 * 9 : int1 & ~(1 << byte1 + byte2 * 3 + byte3 * 9);
	}

	public void setPlayerCutawayFlag(int int1, boolean boolean1, long long1) {
		this.targetPlayerCutawayFlags[int1] = boolean1;
		if (long1 > this.playerCutawayFlagLockUntilTimes[int1] && this.playerCutawayFlags[int1] != this.targetPlayerCutawayFlags[int1]) {
			this.playerCutawayFlags[int1] = this.targetPlayerCutawayFlags[int1];
			this.playerCutawayFlagLockUntilTimes[int1] = long1 + 750L;
		}
	}

	public boolean getPlayerCutawayFlag(int int1, long long1) {
		return long1 > this.playerCutawayFlagLockUntilTimes[int1] ? this.targetPlayerCutawayFlags[int1] : this.playerCutawayFlags[int1];
	}

	public void setIsDissolved(int int1, boolean boolean1, long long1) {
		this.targetPlayerIsDissolvedFlags[int1] = boolean1;
		if (long1 > this.playerIsDissolvedFlagLockUntilTimes[int1] && this.playerIsDissolvedFlags[int1] != this.targetPlayerIsDissolvedFlags[int1]) {
			this.playerIsDissolvedFlags[int1] = this.targetPlayerIsDissolvedFlags[int1];
			this.playerIsDissolvedFlagLockUntilTimes[int1] = long1 + 750L;
		}
	}

	public boolean getIsDissolved(int int1, long long1) {
		return long1 > this.playerIsDissolvedFlagLockUntilTimes[int1] ? this.targetPlayerIsDissolvedFlags[int1] : this.playerIsDissolvedFlags[int1];
	}

	public IsoWaterGeometry getWater() {
		if (this.water != null && this.water.m_adjacentChunkLoadedCounter != this.chunk.m_adjacentChunkLoadedCounter) {
			this.water.m_adjacentChunkLoadedCounter = this.chunk.m_adjacentChunkLoadedCounter;
			if (this.water.hasWater || this.water.bShore) {
				this.clearWater();
			}
		}

		if (this.water == null) {
			try {
				this.water = (IsoWaterGeometry)IsoWaterGeometry.pool.alloc();
				this.water.m_adjacentChunkLoadedCounter = this.chunk.m_adjacentChunkLoadedCounter;
				if (this.water.init(this) == null) {
					IsoWaterGeometry.pool.release((Object)this.water);
					this.water = null;
				}
			} catch (Exception exception) {
				this.clearWater();
			}
		}

		return this.water;
	}

	public void clearWater() {
		if (this.water != null) {
			IsoWaterGeometry.pool.release((Object)this.water);
			this.water = null;
		}
	}

	public IsoPuddlesGeometry getPuddles() {
		if (this.puddles == null) {
			try {
				synchronized (IsoPuddlesGeometry.pool) {
					this.puddles = (IsoPuddlesGeometry)IsoPuddlesGeometry.pool.alloc();
				}

				this.puddles.square = this;
				this.puddles.bRecalc = true;
			} catch (Exception exception) {
				this.clearPuddles();
			}
		}

		return this.puddles;
	}

	public void clearPuddles() {
		if (this.puddles != null) {
			this.puddles.square = null;
			synchronized (IsoPuddlesGeometry.pool) {
				IsoPuddlesGeometry.pool.release((Object)this.puddles);
			}

			this.puddles = null;
		}
	}

	public float getPuddlesInGround() {
		if (this.isInARoom()) {
			return -1.0F;
		} else {
			if ((double)Math.abs(IsoPuddles.getInstance().getPuddlesSize() + (float)Core.getInstance().getPerfPuddles() + (float)IsoCamera.frameState.OffscreenWidth - this.puddlesCacheSize) > 0.01) {
				this.puddlesCacheSize = IsoPuddles.getInstance().getPuddlesSize() + (float)Core.getInstance().getPerfPuddles() + (float)IsoCamera.frameState.OffscreenWidth;
				this.puddlesCacheLevel = IsoPuddlesCompute.computePuddle(this);
			}

			return this.puddlesCacheLevel;
		}
	}

	public IsoGridOcclusionData getOcclusionData() {
		return this.OcclusionDataCache;
	}

	public IsoGridOcclusionData getOrCreateOcclusionData() {
		assert !GameServer.bServer;
		if (this.OcclusionDataCache == null) {
			this.OcclusionDataCache = new IsoGridOcclusionData(this);
		}

		return this.OcclusionDataCache;
	}

	public void softClear() {
		this.zone = null;
		this.room = null;
		this.w = null;
		this.nw = null;
		this.sw = null;
		this.s = null;
		this.n = null;
		this.ne = null;
		this.se = null;
		this.e = null;
		this.isoWorldRegion = null;
		this.hasSetIsoWorldRegion = false;
		for (int int1 = 0; int1 < 8; ++int1) {
			this.nav[int1] = null;
		}
	}

	public float getGridSneakModifier(boolean boolean1) {
		if (!boolean1) {
			if (this.Properties.Is("CloseSneakBonus")) {
				return (float)Integer.parseInt(this.Properties.Val("CloseSneakBonus")) / 100.0F;
			}

			if (this.Properties.Is(IsoFlagType.collideN) || this.Properties.Is(IsoFlagType.collideW) || this.Properties.Is(IsoFlagType.WindowN) || this.Properties.Is(IsoFlagType.WindowW) || this.Properties.Is(IsoFlagType.doorN) || this.Properties.Is(IsoFlagType.doorW)) {
				return 8.0F;
			}
		} else if (this.Properties.Is(IsoFlagType.solidtrans)) {
			return 4.0F;
		}

		return 1.0F;
	}

	public boolean isSomethingTo(IsoGridSquare square) {
		return this.isWallTo(square) || this.isWindowTo(square) || this.isDoorTo(square);
	}

	public IsoObject getTransparentWallTo(IsoGridSquare square) {
		if (square != null && square != this && this.isWallTo(square)) {
			if (square.x > this.x && square.Properties.Is(IsoFlagType.SpearOnlyAttackThrough) && !square.Properties.Is(IsoFlagType.WindowW)) {
				return square.getWall();
			} else if (this.x > square.x && this.Properties.Is(IsoFlagType.SpearOnlyAttackThrough) && !this.Properties.Is(IsoFlagType.WindowW)) {
				return this.getWall();
			} else if (square.y > this.y && square.Properties.Is(IsoFlagType.SpearOnlyAttackThrough) && !square.Properties.Is(IsoFlagType.WindowN)) {
				return square.getWall();
			} else if (this.y > square.y && this.Properties.Is(IsoFlagType.SpearOnlyAttackThrough) && !this.Properties.Is(IsoFlagType.WindowN)) {
				return this.getWall();
			} else {
				if (square.x != this.x && square.y != this.y) {
					IsoObject object = this.getTransparentWallTo(IsoWorld.instance.CurrentCell.getGridSquare(square.x, this.y, this.z));
					IsoObject object2 = this.getTransparentWallTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, square.y, this.z));
					if (object != null) {
						return object;
					}

					if (object2 != null) {
						return object2;
					}

					object = square.getTransparentWallTo(IsoWorld.instance.CurrentCell.getGridSquare(square.x, this.y, this.z));
					object2 = square.getTransparentWallTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, square.y, this.z));
					if (object != null) {
						return object;
					}

					if (object2 != null) {
						return object2;
					}
				}

				return null;
			}
		} else {
			return null;
		}
	}

	public boolean isWallTo(IsoGridSquare square) {
		if (square != null && square != this) {
			if (square.x > this.x && square.Properties.Is(IsoFlagType.collideW) && !square.Properties.Is(IsoFlagType.WindowW)) {
				return true;
			} else if (this.x > square.x && this.Properties.Is(IsoFlagType.collideW) && !this.Properties.Is(IsoFlagType.WindowW)) {
				return true;
			} else if (square.y > this.y && square.Properties.Is(IsoFlagType.collideN) && !square.Properties.Is(IsoFlagType.WindowN)) {
				return true;
			} else if (this.y > square.y && this.Properties.Is(IsoFlagType.collideN) && !this.Properties.Is(IsoFlagType.WindowN)) {
				return true;
			} else {
				if (square.x != this.x && square.y != this.y) {
					if (this.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(square.x, this.y, this.z)) || this.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, square.y, this.z))) {
						return true;
					}

					if (square.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(square.x, this.y, this.z)) || square.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, square.y, this.z))) {
						return true;
					}
				}

				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isWindowTo(IsoGridSquare square) {
		if (square != null && square != this) {
			if (square.x > this.x && square.Properties.Is(IsoFlagType.windowW)) {
				return true;
			} else if (this.x > square.x && this.Properties.Is(IsoFlagType.windowW)) {
				return true;
			} else if (square.y > this.y && square.Properties.Is(IsoFlagType.windowN)) {
				return true;
			} else if (this.y > square.y && this.Properties.Is(IsoFlagType.windowN)) {
				return true;
			} else {
				if (square.x != this.x && square.y != this.y) {
					if (this.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(square.x, this.y, this.z)) || this.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, square.y, this.z))) {
						return true;
					}

					if (square.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(square.x, this.y, this.z)) || square.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, square.y, this.z))) {
						return true;
					}
				}

				return false;
			}
		} else {
			return false;
		}
	}

	public boolean haveDoor() {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			if (this.Objects.get(int1) instanceof IsoDoor) {
				return true;
			}
		}

		return false;
	}

	public boolean isDoorTo(IsoGridSquare square) {
		if (square != null && square != this) {
			if (square.x > this.x && square.Properties.Is(IsoFlagType.doorW)) {
				return true;
			} else if (this.x > square.x && this.Properties.Is(IsoFlagType.doorW)) {
				return true;
			} else if (square.y > this.y && square.Properties.Is(IsoFlagType.doorN)) {
				return true;
			} else if (this.y > square.y && this.Properties.Is(IsoFlagType.doorN)) {
				return true;
			} else {
				if (square.x != this.x && square.y != this.y) {
					if (this.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(square.x, this.y, this.z)) || this.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, square.y, this.z))) {
						return true;
					}

					if (square.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(square.x, this.y, this.z)) || square.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, square.y, this.z))) {
						return true;
					}
				}

				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isBlockedTo(IsoGridSquare square) {
		return this.isWallTo(square) || this.isWindowBlockedTo(square) || this.isDoorBlockedTo(square);
	}

	public boolean isWindowBlockedTo(IsoGridSquare square) {
		if (square == null) {
			return false;
		} else if (square.x > this.x && square.hasBlockedWindow(false)) {
			return true;
		} else if (this.x > square.x && this.hasBlockedWindow(false)) {
			return true;
		} else if (square.y > this.y && square.hasBlockedWindow(true)) {
			return true;
		} else if (this.y > square.y && this.hasBlockedWindow(true)) {
			return true;
		} else {
			if (square.x != this.x && square.y != this.y) {
				if (this.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(square.x, this.y, this.z)) || this.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, square.y, this.z))) {
					return true;
				}

				if (square.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(square.x, this.y, this.z)) || square.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, square.y, this.z))) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean hasBlockedWindow(boolean boolean1) {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (object instanceof IsoWindow) {
				IsoWindow window = (IsoWindow)object;
				if (window.getNorth() == boolean1) {
					return !window.isDestroyed() && !window.open || window.isBarricaded();
				}
			}
		}

		return false;
	}

	public boolean isDoorBlockedTo(IsoGridSquare square) {
		if (square == null) {
			return false;
		} else if (square.x > this.x && square.hasBlockedDoor(false)) {
			return true;
		} else if (this.x > square.x && this.hasBlockedDoor(false)) {
			return true;
		} else if (square.y > this.y && square.hasBlockedDoor(true)) {
			return true;
		} else if (this.y > square.y && this.hasBlockedDoor(true)) {
			return true;
		} else {
			if (square.x != this.x && square.y != this.y) {
				if (this.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(square.x, this.y, this.z)) || this.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, square.y, this.z))) {
					return true;
				}

				if (square.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(square.x, this.y, this.z)) || square.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, square.y, this.z))) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean hasBlockedDoor(boolean boolean1) {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (object instanceof IsoDoor) {
				IsoDoor door = (IsoDoor)object;
				if (door.getNorth() == boolean1) {
					return !door.open || door.isBarricaded();
				}
			}

			if (object instanceof IsoThumpable) {
				IsoThumpable thumpable = (IsoThumpable)object;
				if (thumpable.isDoor() && thumpable.getNorth() == boolean1) {
					return !thumpable.open || thumpable.isBarricaded();
				}
			}
		}

		return false;
	}

	public IsoObject getHoppable(boolean boolean1) {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			PropertyContainer propertyContainer = object.getProperties();
			if (propertyContainer != null && propertyContainer.Is(boolean1 ? IsoFlagType.HoppableN : IsoFlagType.HoppableW)) {
				return object;
			}

			if (propertyContainer != null && propertyContainer.Is(boolean1 ? IsoFlagType.WindowN : IsoFlagType.WindowW)) {
				return object;
			}
		}

		return null;
	}

	public IsoObject getHoppableTo(IsoGridSquare square) {
		if (square != null && square != this) {
			IsoObject object;
			if (square.x < this.x && square.y == this.y) {
				object = this.getHoppable(false);
				if (object != null) {
					return object;
				}
			}

			if (square.x == this.x && square.y < this.y) {
				object = this.getHoppable(true);
				if (object != null) {
					return object;
				}
			}

			if (square.x > this.x && square.y == this.y) {
				object = square.getHoppable(false);
				if (object != null) {
					return object;
				}
			}

			if (square.x == this.x && square.y > this.y) {
				object = square.getHoppable(true);
				if (object != null) {
					return object;
				}
			}

			if (square.x != this.x && square.y != this.y) {
				IsoGridSquare square2 = this.getCell().getGridSquare(this.x, square.y, this.z);
				IsoGridSquare square3 = this.getCell().getGridSquare(square.x, this.y, this.z);
				object = this.getHoppableTo(square2);
				if (object != null) {
					return object;
				}

				object = this.getHoppableTo(square3);
				if (object != null) {
					return object;
				}

				object = square.getHoppableTo(square2);
				if (object != null) {
					return object;
				}

				object = square.getHoppableTo(square3);
				if (object != null) {
					return object;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	public boolean isHoppableTo(IsoGridSquare square) {
		if (square == null) {
			return false;
		} else if (square.x != this.x && square.y != this.y) {
			return false;
		} else if (square.x > this.x && square.Properties.Is(IsoFlagType.HoppableW)) {
			return true;
		} else if (this.x > square.x && this.Properties.Is(IsoFlagType.HoppableW)) {
			return true;
		} else if (square.y > this.y && square.Properties.Is(IsoFlagType.HoppableN)) {
			return true;
		} else {
			return this.y > square.y && this.Properties.Is(IsoFlagType.HoppableN);
		}
	}

	public void discard() {
		this.hourLastSeen = -32768;
		this.chunk = null;
		this.zone = null;
		this.LightInfluenceB = null;
		this.LightInfluenceG = null;
		this.LightInfluenceR = null;
		this.room = null;
		this.w = null;
		this.nw = null;
		this.sw = null;
		this.s = null;
		this.n = null;
		this.ne = null;
		this.se = null;
		this.e = null;
		this.isoWorldRegion = null;
		this.hasSetIsoWorldRegion = false;
		this.nav[0] = null;
		this.nav[1] = null;
		this.nav[2] = null;
		this.nav[3] = null;
		this.nav[4] = null;
		this.nav[5] = null;
		this.nav[6] = null;
		this.nav[7] = null;
		for (int int1 = 0; int1 < 4; ++int1) {
			if (this.lighting[int1] != null) {
				this.lighting[int1].reset();
			}
		}

		this.SolidFloorCached = false;
		this.SolidFloor = false;
		this.CacheIsFree = false;
		this.CachedIsFree = false;
		this.chunk = null;
		this.roomID = -1;
		this.DeferedCharacters.clear();
		this.DeferredCharacterTick = -1;
		this.StaticMovingObjects.clear();
		this.MovingObjects.clear();
		this.Objects.clear();
		this.WorldObjects.clear();
		this.hasTypes.clear();
		this.table = null;
		this.Properties.Clear();
		this.SpecialObjects.clear();
		this.RainDrop = null;
		this.RainSplash = null;
		this.overlayDone = false;
		this.haveRoof = false;
		this.burntOut = false;
		this.trapPositionX = this.trapPositionY = this.trapPositionZ = -1;
		this.haveElectricity = false;
		this.haveSheetRope = false;
		if (this.erosion != null) {
			this.erosion.reset();
		}

		if (this.OcclusionDataCache != null) {
			this.OcclusionDataCache.Reset();
		}

		this.roofHideBuilding = null;
		this.bHasFlies = false;
		isoGridSquareCache.add(this);
	}

	private static boolean validateUser(String string, String string2) throws MalformedURLException, IOException {
		URL url = new URL("http://www.projectzomboid.com/scripts/auth.php?username=" + string + "&password=" + string2);
		URLConnection urlConnection = url.openConnection();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		String string3;
		do {
			if ((string3 = bufferedReader.readLine()) == null) {
				return false;
			}
		} while (!string3.contains("success"));

		return true;
	}

	public float DistTo(int int1, int int2) {
		return IsoUtils.DistanceManhatten((float)int1 + 0.5F, (float)int2 + 0.5F, (float)this.x, (float)this.y);
	}

	public float DistTo(IsoGridSquare square) {
		return IsoUtils.DistanceManhatten((float)this.x + 0.5F, (float)this.y + 0.5F, (float)square.x + 0.5F, (float)square.y + 0.5F);
	}

	public float DistToProper(IsoGridSquare square) {
		return IsoUtils.DistanceTo((float)this.x + 0.5F, (float)this.y + 0.5F, (float)square.x + 0.5F, (float)square.y + 0.5F);
	}

	public float DistTo(IsoMovingObject movingObject) {
		return IsoUtils.DistanceManhatten((float)this.x + 0.5F, (float)this.y + 0.5F, movingObject.getX(), movingObject.getY());
	}

	public float DistToProper(IsoMovingObject movingObject) {
		return IsoUtils.DistanceTo((float)this.x + 0.5F, (float)this.y + 0.5F, movingObject.getX(), movingObject.getY());
	}

	public boolean isSafeToSpawn() {
		choices.clear();
		this.isSafeToSpawn(this, 0);
		if (choices.size() > 7) {
			choices.clear();
			return true;
		} else {
			choices.clear();
			return false;
		}
	}

	public void isSafeToSpawn(IsoGridSquare square, int int1) {
		if (int1 <= 5) {
			choices.add(square);
			if (square.n != null && !choices.contains(square.n)) {
				this.isSafeToSpawn(square.n, int1 + 1);
			}

			if (square.s != null && !choices.contains(square.s)) {
				this.isSafeToSpawn(square.s, int1 + 1);
			}

			if (square.e != null && !choices.contains(square.e)) {
				this.isSafeToSpawn(square.e, int1 + 1);
			}

			if (square.w != null && !choices.contains(square.w)) {
				this.isSafeToSpawn(square.w, int1 + 1);
			}
		}
	}

	public static boolean auth(String string, char[] charArray) {
		if (string.length() > 64) {
			return false;
		} else {
			String string2 = charArray.toString();
			if (string2.length() > 64) {
				return false;
			} else {
				try {
					return validateUser(string, string2);
				} catch (MalformedURLException malformedURLException) {
					Logger.getLogger(IsoGridSquare.class.getName()).log(Level.SEVERE, (String)null, malformedURLException);
				} catch (IOException ioException) {
					Logger.getLogger(IsoGridSquare.class.getName()).log(Level.SEVERE, (String)null, ioException);
				}

				return false;
			}
		}
	}

	private void renderAttachedSpritesWithNoWallLighting(IsoObject object, ColorInfo colorInfo) {
		if (object.AttachedAnimSprite != null && !object.AttachedAnimSprite.isEmpty()) {
			boolean boolean1 = false;
			for (int int1 = 0; int1 < object.AttachedAnimSprite.size(); ++int1) {
				IsoSpriteInstance spriteInstance = (IsoSpriteInstance)object.AttachedAnimSprite.get(int1);
				if (spriteInstance.parentSprite != null && spriteInstance.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
					boolean1 = true;
					break;
				}
			}

			if (boolean1) {
				defColorInfo.r = colorInfo.r;
				defColorInfo.g = colorInfo.g;
				defColorInfo.b = colorInfo.b;
				float float1 = defColorInfo.a;
				if (CircleStencil) {
				}

				for (int int2 = 0; int2 < object.AttachedAnimSprite.size(); ++int2) {
					IsoSpriteInstance spriteInstance2 = (IsoSpriteInstance)object.AttachedAnimSprite.get(int2);
					if (spriteInstance2.parentSprite != null && spriteInstance2.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
						defColorInfo.a = spriteInstance2.alpha;
						spriteInstance2.render(object, (float)this.x, (float)this.y, (float)this.z, object.dir, object.offsetX, object.offsetY + object.getRenderYOffset() * (float)Core.TileScale, defColorInfo);
						spriteInstance2.update();
					}
				}

				defColorInfo.r = 1.0F;
				defColorInfo.g = 1.0F;
				defColorInfo.b = 1.0F;
				defColorInfo.a = float1;
			}
		}
	}

	public void DoCutawayShader(IsoObject object, IsoDirections directions, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, boolean boolean5, boolean boolean6, boolean boolean7, WallShaperWhole wallShaperWhole) {
		Texture texture = Texture.getSharedTexture("media/wallcutaways.png");
		if (texture != null && texture.getID() != -1) {
			boolean boolean8 = object.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
			int int1 = IsoCamera.frameState.playerIndex;
			ColorInfo colorInfo = this.lightInfo[int1];
			try {
				float float1 = 0.0F;
				float float2 = object.getCurrentFrameTex().getOffsetY();
				int int2 = 0;
				int int3 = 226 - object.getCurrentFrameTex().getHeight();
				if (directions != IsoDirections.NW) {
					int2 = 66 - object.getCurrentFrameTex().getWidth();
				}

				if (object.sprite.getProperties().Is(IsoFlagType.WallSE)) {
					int2 = 6 - object.getCurrentFrameTex().getWidth();
					int3 = 196 - object.getCurrentFrameTex().getHeight();
				}

				if (object.sprite.name.contains("fencing_01_11")) {
					float1 = 1.0F;
				} else if (object.sprite.name.contains("carpentry_02_80")) {
					float1 = 1.0F;
				} else if (object.sprite.name.contains("spiffos_01_71")) {
					float1 = -24.0F;
				} else if (object.sprite.name.contains("walls_exterior_roofs")) {
					String string = object.sprite.name.replaceAll("(.*)_", "");
					int int4 = Integer.parseInt(string);
					if (int4 == 4) {
						float1 = -60.0F;
					} else if (int4 == 17) {
						float1 = -46.0F;
					} else if (int4 == 28 && !object.sprite.name.contains("03")) {
						float1 = -60.0F;
					} else if (int4 == 41) {
						float1 = -46.0F;
					}
				}

				IsoGridSquare.CircleStencilShader circleStencilShader = IsoGridSquare.CircleStencilShader.instance;
				short short1;
				short short2;
				int int5;
				short short3;
				if (directions == IsoDirections.N || directions == IsoDirections.NW) {
					short3 = 700;
					short1 = 1084;
					if (boolean2) {
						short1 = 1212;
						if (!boolean3) {
							short3 = 444;
						}
					} else if (!boolean3) {
						short3 = 828;
					} else {
						short3 = 956;
					}

					short2 = 0;
					if (boolean4) {
						short2 = 904;
						if (object.sprite.name.contains("garage") || object.sprite.name.contains("industry_trucks")) {
							int5 = object.sprite.tileSheetIndex;
							if (int5 % 8 == 5) {
								short2 = 1356;
							} else if (int5 % 8 == 4) {
								short2 = 1582;
							} else if (int5 % 8 == 3) {
								short2 = 1130;
							}
						}

						if (object.sprite.name.contains("community_church")) {
							int5 = object.sprite.tileSheetIndex;
							if (int5 == 19) {
								short2 = 1356;
							} else if (int5 == 18) {
								short2 = 1130;
							}
						}
					} else if (boolean6) {
						short2 = 226;
						if (object.sprite.name.contains("trailer")) {
							int5 = object.sprite.tileSheetIndex;
							if (int5 != 14 && int5 != 38) {
								if (int5 == 15 || int5 == 39) {
									short2 = 452;
								}
							} else {
								short2 = 678;
							}
						}

						if (object.sprite.name.contains("sunstarmotel")) {
							int5 = object.sprite.tileSheetIndex;
							if (int5 != 22 && int5 != 18) {
								if (int5 == 23 || int5 == 19) {
									short2 = 452;
								}
							} else {
								short2 = 678;
							}
						}
					}

					colu = this.getVertLight(0, int1);
					coll = this.getVertLight(1, int1);
					colu2 = this.getVertLight(4, int1);
					coll2 = this.getVertLight(5, int1);
					if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
						coll2 = -1;
						colu2 = -1;
						coll = -1;
						colu = -1;
						colorInfo = defColorInfo;
					}

					if (object.sprite.getProperties().Is(IsoFlagType.WallSE)) {
						SpriteRenderer.instance.setCutawayTexture(texture, short1 + (int)float1, short2 + (int)float2, 6 - int2, 196 - int3);
					} else {
						SpriteRenderer.instance.setCutawayTexture(texture, short3 + (int)float1, short2 + (int)float2, 66 - int2, 226 - int3);
					}

					if (directions == IsoDirections.N) {
						SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.All);
					} else {
						SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.RightOnly);
					}

					wallShaperWhole.col[0] = colu2;
					wallShaperWhole.col[1] = coll2;
					wallShaperWhole.col[2] = coll;
					wallShaperWhole.col[3] = colu;
					object.renderWallTileOnly((float)this.x, (float)this.y, (float)this.z, boolean8 ? colorInfo : defColorInfo, circleStencilShader, wallShaperWhole);
				}

				if (directions == IsoDirections.W || directions == IsoDirections.NW) {
					short3 = 512;
					short1 = 1084;
					if (boolean1) {
						if (!boolean2) {
							short3 = 768;
							short1 = 1212;
						}
					} else if (!boolean2) {
						short3 = 896;
						short1 = 1212;
					} else {
						short3 = 256;
					}

					short2 = 0;
					if (boolean5) {
						short2 = 904;
						if (object.sprite.name.contains("garage") || object.sprite.name.contains("industry_trucks")) {
							int5 = object.sprite.tileSheetIndex;
							if (int5 % 8 == 0) {
								short2 = 1356;
							} else if (int5 % 8 == 1) {
								short2 = 1582;
							} else if (int5 % 8 == 2) {
								short2 = 1130;
							}
						}

						if (object.sprite.name.contains("community_church")) {
							int5 = object.sprite.tileSheetIndex;
							if (int5 == 16) {
								short2 = 1356;
							} else if (int5 == 17) {
								short2 = 1130;
							}
						}
					} else if (boolean7) {
						short2 = 226;
						if (object.sprite.name.contains("trailer")) {
							int5 = object.sprite.tileSheetIndex;
							if (int5 != 13 && int5 != 37) {
								if (int5 == 12 || int5 == 36) {
									short2 = 452;
								}
							} else {
								short2 = 678;
							}
						}

						if (object.sprite.name.contains("sunstarmotel")) {
							int5 = object.sprite.tileSheetIndex;
							if (int5 == 17) {
								short2 = 678;
							} else if (int5 == 16) {
								short2 = 452;
							}
						}
					}

					colu = this.getVertLight(0, int1);
					coll = this.getVertLight(3, int1);
					colu2 = this.getVertLight(4, int1);
					coll2 = this.getVertLight(7, int1);
					if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
						coll2 = -1;
						colu2 = -1;
						coll = -1;
						colu = -1;
						colorInfo = defColorInfo;
					}

					if (object.sprite.getProperties().Is(IsoFlagType.WallSE)) {
						SpriteRenderer.instance.setCutawayTexture(texture, short1 + (int)float1, short2 + (int)float2, 6 - int2, 196 - int3);
					} else {
						SpriteRenderer.instance.setCutawayTexture(texture, short3 + (int)float1, short2 + (int)float2, 66 - int2, 226 - int3);
					}

					if (directions == IsoDirections.W) {
						SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.All);
					} else {
						SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.LeftOnly);
					}

					wallShaperWhole.col[0] = coll2;
					wallShaperWhole.col[1] = colu2;
					wallShaperWhole.col[2] = colu;
					wallShaperWhole.col[3] = coll;
					object.renderWallTileOnly((float)this.x, (float)this.y, (float)this.z, boolean8 ? colorInfo : defColorInfo, circleStencilShader, wallShaperWhole);
				}
			} finally {
				SpriteRenderer.instance.setExtraWallShaderParams((SpriteRenderer.WallShaderTexRender)null);
				SpriteRenderer.instance.clearCutawayTexture();
				SpriteRenderer.instance.clearUseVertColorsArray();
			}

			object.renderAttachedAndOverlaySprites((float)this.x, (float)this.y, (float)this.z, boolean8 ? colorInfo : defColorInfo, false, !boolean8, (Shader)null, wallShaperWhole);
		}
	}

	public void DoCutawayShaderSprite(IsoSprite sprite, IsoDirections directions, boolean boolean1, boolean boolean2, boolean boolean3) {
		IsoGridSquare.CircleStencilShader circleStencilShader = IsoGridSquare.CircleStencilShader.instance;
		WallShaperWhole wallShaperWhole = WallShaperWhole.instance;
		int int1 = IsoCamera.frameState.playerIndex;
		Texture texture = Texture.getSharedTexture("media/wallcutaways.png");
		if (texture != null && texture.getID() != -1) {
			try {
				Texture texture2 = ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get((int)sprite.def.Frame)).getTexture(directions);
				float float1 = 0.0F;
				float float2 = ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get((int)sprite.def.Frame)).getTexture(directions).getOffsetY();
				int int2 = 0;
				int int3 = 226 - texture2.getHeight();
				if (directions != IsoDirections.NW) {
					int2 = 66 - ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get((int)sprite.def.Frame)).getTexture(directions).getWidth();
				}

				if (sprite.getProperties().Is(IsoFlagType.WallSE)) {
					int2 = 6 - ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get((int)sprite.def.Frame)).getTexture(directions).getWidth();
					int3 = 196 - ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get((int)sprite.def.Frame)).getTexture(directions).getHeight();
				}

				if (sprite.name.contains("fencing_01_11")) {
					float1 = 1.0F;
				} else if (sprite.name.contains("carpentry_02_80")) {
					float1 = 1.0F;
				} else if (sprite.name.contains("spiffos_01_71")) {
					float1 = -24.0F;
				} else if (sprite.name.contains("walls_exterior_roofs")) {
					String string = sprite.name.replaceAll("(.*)_", "");
					int int4 = Integer.parseInt(string);
					if (int4 == 4) {
						float1 = -60.0F;
					} else if (int4 == 17) {
						float1 = -46.0F;
					} else if (int4 == 28 && !sprite.name.contains("03")) {
						float1 = -60.0F;
					} else if (int4 == 41) {
						float1 = -46.0F;
					}
				}

				short short1;
				short short2;
				if (directions == IsoDirections.N || directions == IsoDirections.NW) {
					short1 = 700;
					short2 = 1084;
					if (boolean2) {
						short2 = 1212;
						if (!boolean3) {
							short1 = 444;
						}
					} else if (!boolean3) {
						short1 = 828;
					} else {
						short1 = 956;
					}

					colu = this.getVertLight(0, int1);
					coll = this.getVertLight(1, int1);
					colu2 = this.getVertLight(4, int1);
					coll2 = this.getVertLight(5, int1);
					if (sprite.getProperties().Is(IsoFlagType.WallSE)) {
						SpriteRenderer.instance.setCutawayTexture(texture, short2 + (int)float1, 0 + (int)float2, 6 - int2, 196 - int3);
					} else {
						SpriteRenderer.instance.setCutawayTexture(texture, short1 + (int)float1, 0 + (int)float2, 66 - int2, 226 - int3);
					}

					if (directions == IsoDirections.N) {
						SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.All);
					} else {
						SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.RightOnly);
					}

					wallShaperWhole.col[0] = colu2;
					wallShaperWhole.col[1] = coll2;
					wallShaperWhole.col[2] = coll;
					wallShaperWhole.col[3] = colu;
					IndieGL.bindShader(circleStencilShader, sprite, directions, wallShaperWhole, (spritex,directionsx,boolean1x)->{
						spritex.render((IsoObject)null, (float)this.x, (float)this.y, (float)this.z, directionsx, WeatherFxMask.offsetX, WeatherFxMask.offsetY, defColorInfo, false, boolean1x);
					});
				}

				if (directions == IsoDirections.W || directions == IsoDirections.NW) {
					short1 = 512;
					short2 = 1084;
					if (boolean1) {
						if (!boolean2) {
							short1 = 768;
							short2 = 1212;
						}
					} else if (!boolean2) {
						short1 = 896;
						short2 = 1212;
					} else {
						short1 = 256;
					}

					colu = this.getVertLight(0, int1);
					coll = this.getVertLight(3, int1);
					colu2 = this.getVertLight(4, int1);
					coll2 = this.getVertLight(7, int1);
					if (sprite.getProperties().Is(IsoFlagType.WallSE)) {
						SpriteRenderer.instance.setCutawayTexture(texture, short2 + (int)float1, 0 + (int)float2, 6 - int2, 196 - int3);
					} else {
						SpriteRenderer.instance.setCutawayTexture(texture, short1 + (int)float1, 0 + (int)float2, 66 - int2, 226 - int3);
					}

					if (directions == IsoDirections.W) {
						SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.All);
					} else {
						SpriteRenderer.instance.setExtraWallShaderParams(SpriteRenderer.WallShaderTexRender.LeftOnly);
					}

					wallShaperWhole.col[0] = coll2;
					wallShaperWhole.col[1] = colu2;
					wallShaperWhole.col[2] = colu;
					wallShaperWhole.col[3] = coll;
					IndieGL.bindShader(circleStencilShader, sprite, directions, wallShaperWhole, (spritex,directionsx,boolean1x)->{
						spritex.render((IsoObject)null, (float)this.x, (float)this.y, (float)this.z, directionsx, WeatherFxMask.offsetX, WeatherFxMask.offsetY, defColorInfo, false, boolean1x);
					});
				}
			} finally {
				SpriteRenderer.instance.setExtraWallShaderParams((SpriteRenderer.WallShaderTexRender)null);
				SpriteRenderer.instance.clearCutawayTexture();
				SpriteRenderer.instance.clearUseVertColorsArray();
			}
		}
	}

	public int DoWallLightingNW(IsoObject object, int int1, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, boolean boolean5, boolean boolean6, boolean boolean7, Shader shader) {
		if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.NW.getValue()) {
			return int1;
		} else {
			boolean boolean8 = boolean1 || boolean2 || boolean3;
			IsoDirections directions = IsoDirections.NW;
			int int2 = IsoCamera.frameState.playerIndex;
			colu = this.getVertLight(0, int2);
			coll = this.getVertLight(3, int2);
			colr = this.getVertLight(1, int2);
			colu2 = this.getVertLight(4, int2);
			coll2 = this.getVertLight(7, int2);
			colr2 = this.getVertLight(5, int2);
			if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingDebug.getValue()) {
				colu = -65536;
				coll = -16711936;
				colr = -16711681;
				colu2 = -16776961;
				coll2 = -65281;
				colr2 = -256;
			}

			boolean boolean9 = CircleStencil;
			if (this.z != (int)IsoCamera.CamCharacter.z) {
				boolean9 = false;
			}

			boolean boolean10 = object.sprite.getType() == IsoObjectType.doorFrN || object.sprite.getType() == IsoObjectType.doorN;
			boolean boolean11 = object.sprite.getType() == IsoObjectType.doorFrW || object.sprite.getType() == IsoObjectType.doorW;
			boolean boolean12 = false;
			boolean boolean13 = false;
			boolean boolean14 = (boolean10 || boolean12 || boolean11 || boolean12) && boolean8 || object.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
			boolean9 = this.calculateWallAlphaAndCircleStencilCorner(object, boolean1, boolean2, boolean3, boolean4, boolean5, boolean6, boolean7, boolean9, int2, boolean10, boolean11, boolean12, boolean13);
			if (USE_WALL_SHADER && boolean9 && boolean8) {
				this.DoCutawayShader(object, directions, boolean1, boolean2, boolean3, boolean4, boolean5, boolean6, boolean7, WallShaperWhole.instance);
				bWallCutawayN = true;
				bWallCutawayW = true;
				return int1;
			} else {
				WallShaperWhole.instance.col[0] = colu2;
				WallShaperWhole.instance.col[1] = colr2;
				WallShaperWhole.instance.col[2] = colr;
				WallShaperWhole.instance.col[3] = colu;
				WallShaperN wallShaperN = WallShaperN.instance;
				wallShaperN.col[0] = colu2;
				wallShaperN.col[1] = colr2;
				wallShaperN.col[2] = colr;
				wallShaperN.col[3] = colu;
				int1 = this.performDrawWall(object, int1, int2, boolean14, wallShaperN, shader);
				WallShaperWhole.instance.col[0] = coll2;
				WallShaperWhole.instance.col[1] = colu2;
				WallShaperWhole.instance.col[2] = colu;
				WallShaperWhole.instance.col[3] = coll;
				WallShaperW wallShaperW = WallShaperW.instance;
				wallShaperW.col[0] = coll2;
				wallShaperW.col[1] = colu2;
				wallShaperW.col[2] = colu;
				wallShaperW.col[3] = coll;
				int1 = this.performDrawWall(object, int1, int2, boolean14, wallShaperW, shader);
				return int1;
			}
		}
	}

	public int DoWallLightingN(IsoObject object, int int1, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, Shader shader) {
		if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.N.getValue()) {
			return int1;
		} else {
			boolean boolean5 = !boolean3;
			boolean boolean6 = !boolean4;
			IsoObjectType objectType = IsoObjectType.doorFrN;
			IsoObjectType objectType2 = IsoObjectType.doorN;
			boolean boolean7 = boolean1 || boolean2;
			IsoFlagType flagType = IsoFlagType.transparentN;
			IsoFlagType flagType2 = IsoFlagType.WindowN;
			IsoFlagType flagType3 = IsoFlagType.HoppableN;
			IsoDirections directions = IsoDirections.N;
			boolean boolean8 = CircleStencil;
			int int2 = IsoCamera.frameState.playerIndex;
			colu = this.getVertLight(0, int2);
			coll = this.getVertLight(1, int2);
			colu2 = this.getVertLight(4, int2);
			coll2 = this.getVertLight(5, int2);
			if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingDebug.getValue()) {
				colu = -65536;
				coll = -16711936;
				colu2 = -16776961;
				coll2 = -65281;
			}

			WallShaperWhole wallShaperWhole = WallShaperWhole.instance;
			wallShaperWhole.col[0] = colu2;
			wallShaperWhole.col[1] = coll2;
			wallShaperWhole.col[2] = coll;
			wallShaperWhole.col[3] = colu;
			return this.performDrawWallSegmentSingle(object, int1, false, boolean1, false, false, boolean2, boolean3, boolean4, boolean5, boolean6, objectType, objectType2, boolean7, flagType, flagType2, flagType3, directions, boolean8, wallShaperWhole, shader);
		}
	}

	public int DoWallLightingW(IsoObject object, int int1, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, Shader shader) {
		if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.W.getValue()) {
			return int1;
		} else {
			boolean boolean5 = !boolean3;
			boolean boolean6 = !boolean4;
			IsoObjectType objectType = IsoObjectType.doorFrW;
			IsoObjectType objectType2 = IsoObjectType.doorW;
			boolean boolean7 = boolean1 || boolean2;
			IsoFlagType flagType = IsoFlagType.transparentW;
			IsoFlagType flagType2 = IsoFlagType.WindowW;
			IsoFlagType flagType3 = IsoFlagType.HoppableW;
			IsoDirections directions = IsoDirections.W;
			boolean boolean8 = CircleStencil;
			int int2 = IsoCamera.frameState.playerIndex;
			colu = this.getVertLight(0, int2);
			coll = this.getVertLight(3, int2);
			colu2 = this.getVertLight(4, int2);
			coll2 = this.getVertLight(7, int2);
			if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingDebug.getValue()) {
				colu = -65536;
				coll = -16711936;
				colu2 = -16776961;
				coll2 = -65281;
			}

			WallShaperWhole wallShaperWhole = WallShaperWhole.instance;
			wallShaperWhole.col[0] = coll2;
			wallShaperWhole.col[1] = colu2;
			wallShaperWhole.col[2] = colu;
			wallShaperWhole.col[3] = coll;
			return this.performDrawWallSegmentSingle(object, int1, boolean1, boolean2, boolean3, boolean4, false, false, false, boolean5, boolean6, objectType, objectType2, boolean7, flagType, flagType2, flagType3, directions, boolean8, wallShaperWhole, shader);
		}
	}

	private int performDrawWallSegmentSingle(IsoObject object, int int1, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, boolean boolean5, boolean boolean6, boolean boolean7, boolean boolean8, boolean boolean9, IsoObjectType objectType, IsoObjectType objectType2, boolean boolean10, IsoFlagType flagType, IsoFlagType flagType2, IsoFlagType flagType3, IsoDirections directions, boolean boolean11, WallShaperWhole wallShaperWhole, Shader shader) {
		int int2 = IsoCamera.frameState.playerIndex;
		if (this.z != (int)IsoCamera.CamCharacter.z) {
			boolean11 = false;
		}

		boolean boolean12 = object.sprite.getType() == objectType || object.sprite.getType() == objectType2;
		boolean boolean13 = object instanceof IsoWindow;
		boolean boolean14 = (boolean12 || boolean13) && boolean10 || object.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
		boolean11 = this.calculateWallAlphaAndCircleStencilEdge(object, boolean8, boolean9, boolean10, flagType, flagType2, flagType3, boolean11, int2, boolean12, boolean13);
		if (USE_WALL_SHADER && boolean11 && boolean10) {
			this.DoCutawayShader(object, directions, boolean1, boolean2, boolean5, boolean6, boolean3, boolean7, boolean4, wallShaperWhole);
			bWallCutawayN |= directions == IsoDirections.N;
			bWallCutawayW |= directions == IsoDirections.W;
			return int1;
		} else {
			return this.performDrawWall(object, int1, int2, boolean14, wallShaperWhole, shader);
		}
	}

	private int performDrawWallOnly(IsoObject object, int int1, int int2, boolean boolean1, Consumer consumer, Shader shader) {
		if (int1 == 0 && !boolean1) {
			int1 = this.getCell().getStencilValue(this.x, this.y, this.z);
		}

		IndieGL.enableAlphaTest();
		IndieGL.glAlphaFunc(516, 0.0F);
		IndieGL.glStencilFunc(519, int1, 127);
		if (!boolean1) {
			IndieGL.glStencilOp(7680, 7680, 7681);
		}

		if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.Render.getValue()) {
			object.renderWallTile((float)this.x, (float)this.y, (float)this.z, boolean1 ? lightInfoTemp : defColorInfo, true, !boolean1, shader, consumer);
		}

		object.setAlpha(int2, 1.0F);
		if (boolean1) {
			IndieGL.glStencilFunc(519, 1, 255);
			IndieGL.glStencilOp(7680, 7680, 7680);
			return int1;
		} else {
			this.getCell().setStencilValue(this.x, this.y, this.z, int1);
			return int1 + 1;
		}
	}

	private int performDrawWall(IsoObject object, int int1, int int2, boolean boolean1, Consumer consumer, Shader shader) {
		lightInfoTemp.set(this.lightInfo[int2]);
		if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
			object.render((float)this.x, (float)this.y, (float)this.z, defColorInfo, true, !boolean1, (Shader)null);
			return int1;
		} else {
			int int3 = this.performDrawWallOnly(object, int1, int2, boolean1, consumer, shader);
			if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.AttachedSprites.getValue()) {
				this.renderAttachedSpritesWithNoWallLighting(object, lightInfoTemp);
			}

			return int3;
		}
	}

	private void calculateWallAlphaCommon(IsoObject object, boolean boolean1, boolean boolean2, boolean boolean3, int int1, boolean boolean4, boolean boolean5) {
		if (boolean4 || boolean5) {
			if (boolean1) {
				object.setAlpha(int1, 0.4F);
				object.setTargetAlpha(int1, 0.4F);
				lightInfoTemp.r = Math.max(0.3F, lightInfoTemp.r);
				lightInfoTemp.g = Math.max(0.3F, lightInfoTemp.g);
				lightInfoTemp.b = Math.max(0.3F, lightInfoTemp.b);
				if (boolean4 && !boolean2) {
					object.setAlpha(int1, 0.0F);
					object.setTargetAlpha(int1, 0.0F);
				}

				if (boolean5 && !boolean3) {
					object.setAlpha(int1, 0.0F);
					object.setTargetAlpha(int1, 0.0F);
				}
			}
		}
	}

	private boolean calculateWallAlphaAndCircleStencilEdge(IsoObject object, boolean boolean1, boolean boolean2, boolean boolean3, IsoFlagType flagType, IsoFlagType flagType2, IsoFlagType flagType3, boolean boolean4, int int1, boolean boolean5, boolean boolean6) {
		if (boolean5 || boolean6) {
			if (!object.sprite.getProperties().Is("GarageDoor")) {
				boolean4 = false;
			}

			this.calculateWallAlphaCommon(object, boolean3, !boolean1, !boolean2, int1, boolean5, boolean6);
		}

		if (boolean4 && object.sprite.getType() == IsoObjectType.wall && object.sprite.getProperties().Is(flagType) && !object.getSprite().getProperties().Is(IsoFlagType.exterior) && !object.sprite.getProperties().Is(flagType2)) {
			boolean4 = false;
		}

		return boolean4;
	}

	private boolean calculateWallAlphaAndCircleStencilCorner(IsoObject object, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, boolean boolean5, boolean boolean6, boolean boolean7, boolean boolean8, int int1, boolean boolean9, boolean boolean10, boolean boolean11, boolean boolean12) {
		this.calculateWallAlphaCommon(object, boolean2 || boolean3, boolean4, boolean6, int1, boolean9, boolean11);
		this.calculateWallAlphaCommon(object, boolean2 || boolean1, boolean5, boolean7, int1, boolean10, boolean12);
		boolean8 = boolean8 && !boolean9 && !boolean11;
		if (boolean8 && object.sprite.getType() == IsoObjectType.wall && (object.sprite.getProperties().Is(IsoFlagType.transparentN) || object.sprite.getProperties().Is(IsoFlagType.transparentW)) && !object.getSprite().getProperties().Is(IsoFlagType.exterior) && !object.sprite.getProperties().Is(IsoFlagType.WindowN) && !object.sprite.getProperties().Is(IsoFlagType.WindowW)) {
			boolean8 = false;
		}

		return boolean8;
	}

	public KahluaTable getLuaMovingObjectList() {
		KahluaTable kahluaTable = LuaManager.platform.newTable();
		LuaManager.env.rawset("Objects", kahluaTable);
		for (int int1 = 0; int1 < this.MovingObjects.size(); ++int1) {
			kahluaTable.rawset(int1 + 1, this.MovingObjects.get(int1));
		}

		return kahluaTable;
	}

	public boolean Is(IsoFlagType flagType) {
		return this.Properties.Is(flagType);
	}

	public boolean Is(String string) {
		return this.Properties.Is(string);
	}

	public boolean Has(IsoObjectType objectType) {
		return this.hasTypes.isSet(objectType);
	}

	public void DeleteTileObject(IsoObject object) {
		this.Objects.remove(object);
		this.RecalcAllWithNeighbours(true);
	}

	public KahluaTable getLuaTileObjectList() {
		KahluaTable kahluaTable = LuaManager.platform.newTable();
		LuaManager.env.rawset("Objects", kahluaTable);
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			kahluaTable.rawset(int1 + 1, this.Objects.get(int1));
		}

		return kahluaTable;
	}

	boolean HasDoor(boolean boolean1) {
		for (int int1 = 0; int1 < this.SpecialObjects.size(); ++int1) {
			if (this.SpecialObjects.get(int1) instanceof IsoDoor && ((IsoDoor)this.SpecialObjects.get(int1)).north == boolean1) {
				return true;
			}

			if (this.SpecialObjects.get(int1) instanceof IsoThumpable && ((IsoThumpable)this.SpecialObjects.get(int1)).isDoor && ((IsoThumpable)this.SpecialObjects.get(int1)).north == boolean1) {
				return true;
			}
		}

		return false;
	}

	public boolean HasStairs() {
		return this.HasStairsNorth() || this.HasStairsWest();
	}

	public boolean HasStairsNorth() {
		return this.Has(IsoObjectType.stairsTN) || this.Has(IsoObjectType.stairsMN) || this.Has(IsoObjectType.stairsBN);
	}

	public boolean HasStairsWest() {
		return this.Has(IsoObjectType.stairsTW) || this.Has(IsoObjectType.stairsMW) || this.Has(IsoObjectType.stairsBW);
	}

	public boolean HasStairsBelow() {
		if (this.z == 0) {
			return false;
		} else {
			IsoGridSquare square = this.getCell().getGridSquare(this.x, this.y, this.z - 1);
			return square != null && square.HasStairs();
		}
	}

	public boolean HasElevatedFloor() {
		return this.Has(IsoObjectType.stairsTN) || this.Has(IsoObjectType.stairsMN) || this.Has(IsoObjectType.stairsTW) || this.Has(IsoObjectType.stairsMW);
	}

	public boolean isSameStaircase(int int1, int int2, int int3) {
		if (int3 != this.getZ()) {
			return false;
		} else {
			int int4 = this.getX();
			int int5 = this.getY();
			int int6 = int4;
			int int7 = int5;
			if (this.Has(IsoObjectType.stairsTN)) {
				int7 = int5 + 2;
			} else if (this.Has(IsoObjectType.stairsMN)) {
				--int5;
				++int7;
			} else if (this.Has(IsoObjectType.stairsBN)) {
				int5 -= 2;
			} else if (this.Has(IsoObjectType.stairsTW)) {
				int6 = int4 + 2;
			} else if (this.Has(IsoObjectType.stairsMW)) {
				--int4;
				++int6;
			} else {
				if (!this.Has(IsoObjectType.stairsBW)) {
					return false;
				}

				int4 -= 2;
			}

			if (int1 >= int4 && int2 >= int5 && int1 <= int6 && int2 <= int7) {
				IsoGridSquare square = this.getCell().getGridSquare(int1, int2, int3);
				return square != null && square.HasStairs();
			} else {
				return false;
			}
		}
	}

	public boolean HasSlopedRoof() {
		return this.HasSlopedRoofWest() || this.HasSlopedRoofNorth();
	}

	public boolean HasSlopedRoofWest() {
		return this.Has(IsoObjectType.WestRoofB) || this.Has(IsoObjectType.WestRoofM) || this.Has(IsoObjectType.WestRoofT);
	}

	public boolean HasSlopedRoofNorth() {
		return this.Has(IsoObjectType.WestRoofB) || this.Has(IsoObjectType.WestRoofM) || this.Has(IsoObjectType.WestRoofT);
	}

	public boolean HasTree() {
		return this.hasTree;
	}

	public IsoTree getTree() {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoTree tree = (IsoTree)Type.tryCastTo((IsoObject)this.Objects.get(int1), IsoTree.class);
			if (tree != null) {
				return tree;
			}
		}

		return null;
	}

	private void fudgeShadowsToAlpha(IsoObject object, Color color) {
		float float1 = 1.0F - object.getAlpha();
		if (color.r < float1) {
			color.r = float1;
		}

		if (color.g < float1) {
			color.g = float1;
		}

		if (color.b < float1) {
			color.b = float1;
		}
	}

	public boolean shouldSave() {
		return !this.Objects.isEmpty();
	}

	public void save(ByteBuffer byteBuffer, ObjectOutputStream objectOutputStream) throws IOException {
		this.save(byteBuffer, objectOutputStream, false);
	}

	public void save(ByteBuffer byteBuffer, ObjectOutputStream objectOutputStream, boolean boolean1) throws IOException {
		this.getErosionData().save(byteBuffer);
		BitHeaderWrite bitHeaderWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, byteBuffer);
		int int1 = this.Objects.size();
		int int2;
		if (this.Objects.size() > 0) {
			bitHeaderWrite.addFlags(1);
			if (int1 == 2) {
				bitHeaderWrite.addFlags(2);
			} else if (int1 == 3) {
				bitHeaderWrite.addFlags(4);
			} else if (int1 >= 4) {
				bitHeaderWrite.addFlags(8);
			}

			if (boolean1) {
				GameWindow.WriteString(byteBuffer, "Number of objects (" + int1 + ")");
			}

			if (int1 >= 4) {
				byteBuffer.putShort((short)this.Objects.size());
			}

			for (int int3 = 0; int3 < this.Objects.size(); ++int3) {
				int2 = byteBuffer.position();
				if (boolean1) {
					byteBuffer.putInt(0);
				}

				byte byte1 = 0;
				if (this.SpecialObjects.contains(this.Objects.get(int3))) {
					byte1 = (byte)(byte1 | 2);
				}

				if (this.WorldObjects.contains(this.Objects.get(int3))) {
					byte1 = (byte)(byte1 | 4);
				}

				byteBuffer.put(byte1);
				if (boolean1) {
					GameWindow.WriteStringUTF(byteBuffer, ((IsoObject)this.Objects.get(int3)).getClass().getName());
				}

				((IsoObject)this.Objects.get(int3)).save(byteBuffer, boolean1);
				if (boolean1) {
					int int4 = byteBuffer.position();
					byteBuffer.position(int2);
					byteBuffer.putInt(int4 - int2);
					byteBuffer.position(int4);
				}
			}

			if (boolean1) {
				byteBuffer.put((byte)67);
				byteBuffer.put((byte)82);
				byteBuffer.put((byte)80);
				byteBuffer.put((byte)83);
			}
		}

		if (this.isOverlayDone()) {
			bitHeaderWrite.addFlags(16);
		}

		if (this.haveRoof) {
			bitHeaderWrite.addFlags(32);
		}

		BitHeaderWrite bitHeaderWrite2 = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, byteBuffer);
		int2 = 0;
		int int5;
		for (int5 = 0; int5 < this.StaticMovingObjects.size(); ++int5) {
			if (this.StaticMovingObjects.get(int5) instanceof IsoDeadBody) {
				++int2;
			}
		}

		if (int2 > 0) {
			bitHeaderWrite2.addFlags(1);
			if (boolean1) {
				GameWindow.WriteString(byteBuffer, "Number of bodies");
			}

			byteBuffer.putShort((short)int2);
			for (int5 = 0; int5 < this.StaticMovingObjects.size(); ++int5) {
				IsoMovingObject movingObject = (IsoMovingObject)this.StaticMovingObjects.get(int5);
				if (movingObject instanceof IsoDeadBody) {
					if (boolean1) {
						GameWindow.WriteStringUTF(byteBuffer, movingObject.getClass().getName());
					}

					movingObject.save(byteBuffer, boolean1);
				}
			}
		}

		if (this.table != null && !this.table.isEmpty()) {
			bitHeaderWrite2.addFlags(2);
			this.table.save(byteBuffer);
		}

		if (this.burntOut) {
			bitHeaderWrite2.addFlags(4);
		}

		if (this.getTrapPositionX() > 0) {
			bitHeaderWrite2.addFlags(8);
			byteBuffer.putInt(this.getTrapPositionX());
			byteBuffer.putInt(this.getTrapPositionY());
			byteBuffer.putInt(this.getTrapPositionZ());
		}

		if (this.haveSheetRope) {
			bitHeaderWrite2.addFlags(16);
		}

		if (!bitHeaderWrite2.equals(0)) {
			bitHeaderWrite.addFlags(64);
			bitHeaderWrite2.write();
		} else {
			byteBuffer.position(bitHeaderWrite2.getStartPosition());
		}

		bitHeaderWrite.write();
		bitHeaderWrite.release();
		bitHeaderWrite2.release();
	}

	static void loadmatrix(boolean[][][] booleanArrayArrayArray, DataInputStream dataInputStream) throws IOException {
	}

	static void savematrix(boolean[][][] booleanArrayArrayArray, DataOutputStream dataOutputStream) throws IOException {
		for (int int1 = 0; int1 < 3; ++int1) {
			for (int int2 = 0; int2 < 3; ++int2) {
				for (int int3 = 0; int3 < 3; ++int3) {
					dataOutputStream.writeBoolean(booleanArrayArrayArray[int1][int2][int3]);
				}
			}
		}
	}

	public boolean isCommonGrass() {
		if (this.Objects.isEmpty()) {
			return false;
		} else {
			IsoObject object = (IsoObject)this.Objects.get(0);
			return object.sprite.getProperties().Is(IsoFlagType.solidfloor) && ("TileFloorExt_3".equals(object.tile) || "TileFloorExt_4".equals(object.tile));
		}
	}

	public static boolean toBoolean(byte[] byteArray) {
		return byteArray != null && byteArray.length != 0 ? byteArray[0] != 0 : false;
	}

	public void removeCorpse(IsoDeadBody deadBody, boolean boolean1) {
		if (GameClient.bClient && !boolean1) {
			try {
				GameClient.instance.checkAddedRemovedItems(deadBody);
			} catch (Exception exception) {
				GameClient.connection.cancelPacket();
				ExceptionLogger.logException(exception);
			}

			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.doPacket((short)68, byteBufferWriter);
			byteBufferWriter.putInt(this.x);
			byteBufferWriter.putInt(this.y);
			byteBufferWriter.putInt(this.z);
			byteBufferWriter.putInt(this.StaticMovingObjects.indexOf(deadBody));
			byteBufferWriter.putInt(deadBody.getOnlineId());
			GameClient.connection.endPacketImmediate();
		}

		deadBody.removeFromWorld();
		deadBody.removeFromSquare();
		if (!GameServer.bServer) {
			LuaEventManager.triggerEvent("OnContainerUpdate", this);
		}
	}

	public IsoDeadBody getDeadBody() {
		for (int int1 = 0; int1 < this.StaticMovingObjects.size(); ++int1) {
			if (this.StaticMovingObjects.get(int1) instanceof IsoDeadBody) {
				return (IsoDeadBody)this.StaticMovingObjects.get(int1);
			}
		}

		return null;
	}

	public List getDeadBodys() {
		ArrayList arrayList = new ArrayList();
		for (int int1 = 0; int1 < this.StaticMovingObjects.size(); ++int1) {
			if (this.StaticMovingObjects.get(int1) instanceof IsoDeadBody) {
				arrayList.add((IsoDeadBody)this.StaticMovingObjects.get(int1));
			}
		}

		return arrayList;
	}

	public void addCorpse(IsoDeadBody deadBody, boolean boolean1) {
		if (GameClient.bClient && !boolean1) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.doPacket((short)69, byteBufferWriter);
			byteBufferWriter.putInt(this.x);
			byteBufferWriter.putInt(this.y);
			byteBufferWriter.putInt(this.z);
			deadBody.writeToRemoteBuffer(byteBufferWriter);
			GameClient.connection.endPacketImmediate();
		}

		if (!this.StaticMovingObjects.contains(deadBody)) {
			this.StaticMovingObjects.add(deadBody);
		}

		deadBody.addToWorld();
		this.burntOut = false;
		this.Properties.UnSet(IsoFlagType.burntOut);
	}

	public IsoBrokenGlass getBrokenGlass() {
		for (int int1 = 0; int1 < this.SpecialObjects.size(); ++int1) {
			IsoObject object = (IsoObject)this.SpecialObjects.get(int1);
			if (object instanceof IsoBrokenGlass) {
				return (IsoBrokenGlass)object;
			}
		}

		return null;
	}

	public IsoBrokenGlass addBrokenGlass() {
		if (!this.isFree(false)) {
			return this.getBrokenGlass();
		} else {
			IsoBrokenGlass brokenGlass = this.getBrokenGlass();
			if (brokenGlass == null) {
				brokenGlass = new IsoBrokenGlass(this.getCell());
				brokenGlass.setSquare(this);
				this.AddSpecialObject(brokenGlass);
				if (GameServer.bServer) {
					GameServer.transmitBrokenGlass(this);
				}
			}

			return brokenGlass;
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.load(byteBuffer, int1, false);
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		this.getErosionData().load(byteBuffer, int1);
		BitHeaderRead bitHeaderRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
		if (!bitHeaderRead.equals(0)) {
			int int2;
			if (bitHeaderRead.hasFlags(1)) {
				if (boolean1) {
					String string = GameWindow.ReadStringUTF(byteBuffer);
					DebugLog.log(string);
				}

				short short1 = 1;
				if (bitHeaderRead.hasFlags(2)) {
					short1 = 2;
				} else if (bitHeaderRead.hasFlags(4)) {
					short1 = 3;
				} else if (bitHeaderRead.hasFlags(8)) {
					short1 = byteBuffer.getShort();
				}

				int int3 = 0;
				while (true) {
					byte byte1;
					if (int3 >= short1) {
						if (!boolean1) {
							break;
						}

						byte byte2 = byteBuffer.get();
						byte byte3 = byteBuffer.get();
						byte byte4 = byteBuffer.get();
						byte1 = byteBuffer.get();
						if (byte2 != 67 || byte3 != 82 || byte4 != 80 || byte1 != 83) {
							DebugLog.log("***** Expected CRPS here");
						}

						break;
					}

					int2 = byteBuffer.position();
					int int4 = 0;
					if (boolean1) {
						int4 = byteBuffer.getInt();
					}

					byte1 = byteBuffer.get();
					boolean boolean2 = (byte1 & 2) != 0;
					boolean boolean3 = (byte1 & 4) != 0;
					IsoObject object = null;
					String string2;
					if (boolean1) {
						string2 = GameWindow.ReadStringUTF(byteBuffer);
						DebugLog.log(string2);
					}

					object = IsoObject.factoryFromFileInput(this.getCell(), byteBuffer);
					int int5;
					if (object == null) {
						if (boolean1) {
							int5 = byteBuffer.position();
							if (int5 - int2 != int4) {
								DebugLog.log("***** Object loaded size " + (int5 - int2) + " != saved size " + int4 + ", reading obj size: " + short1 + ", Object == null");
								if (object.getSprite() != null && object.getSprite().getName() != null) {
									DebugLog.log("Obj sprite = " + object.getSprite().getName());
								}
							}
						}
					} else {
						label252: {
							object.square = this;
							try {
								object.load(byteBuffer, int1, boolean1);
							} catch (Exception exception) {
								this.debugPrintGridSquare();
								if (lastLoaded != null) {
									lastLoaded.debugPrintGridSquare();
								}

								throw new RuntimeException(exception);
							}

							if (boolean1) {
								int5 = byteBuffer.position();
								if (int5 - int2 != int4) {
									DebugLog.log("***** Object loaded size " + (int5 - int2) + " != saved size " + int4 + ", reading obj size: " + short1);
									if (object.getSprite() != null && object.getSprite().getName() != null) {
										DebugLog.log("Obj sprite = " + object.getSprite().getName());
									}
								}
							}

							if (object instanceof IsoWorldInventoryObject) {
								if (((IsoWorldInventoryObject)object).getItem() == null) {
									break label252;
								}

								string2 = ((IsoWorldInventoryObject)object).getItem().getFullType();
								Item item = ScriptManager.instance.FindItem(string2);
								if (item != null && item.getObsolete()) {
									break label252;
								}

								String[] stringArray = string2.split("_");
								if (((IsoWorldInventoryObject)object).dropTime > -1.0 && SandboxOptions.instance.HoursForWorldItemRemoval.getValue() > 0.0 && (SandboxOptions.instance.WorldItemRemovalList.getValue().contains(stringArray[0]) && !SandboxOptions.instance.ItemRemovalListBlacklistToggle.getValue() || !SandboxOptions.instance.WorldItemRemovalList.getValue().contains(stringArray[0]) && SandboxOptions.instance.ItemRemovalListBlacklistToggle.getValue()) && !((IsoWorldInventoryObject)object).isIgnoreRemoveSandbox() && GameTime.instance.getWorldAgeHours() > ((IsoWorldInventoryObject)object).dropTime + SandboxOptions.instance.HoursForWorldItemRemoval.getValue()) {
									break label252;
								}
							}

							if (!(object instanceof IsoWindow) || object.getSprite() == null || !"walls_special_01_8".equals(object.getSprite().getName()) && !"walls_special_01_9".equals(object.getSprite().getName())) {
								this.Objects.add(object);
								if (boolean2) {
									this.SpecialObjects.add(object);
								}

								if (boolean3) {
									if (Core.bDebug && !(object instanceof IsoWorldInventoryObject)) {
										DebugLog.log("Bitflags = " + byte1 + ", obj name = " + object.getObjectName() + ", sprite = " + (object.getSprite() != null ? object.getSprite().getName() : "unknown"));
									}

									this.WorldObjects.add((IsoWorldInventoryObject)object);
									object.square.chunk.recalcHashCodeObjects();
								}
							}
						}
					}

					++int3;
				}
			}

			this.setOverlayDone(bitHeaderRead.hasFlags(16));
			this.haveRoof = bitHeaderRead.hasFlags(32);
			if (bitHeaderRead.hasFlags(64)) {
				BitHeaderRead bitHeaderRead2 = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
				if (bitHeaderRead2.hasFlags(1)) {
					if (boolean1) {
						String string3 = GameWindow.ReadStringUTF(byteBuffer);
						DebugLog.log(string3);
					}

					short short2 = byteBuffer.getShort();
					for (int2 = 0; int2 < short2; ++int2) {
						IsoMovingObject movingObject = null;
						if (boolean1) {
							String string4 = GameWindow.ReadStringUTF(byteBuffer);
							DebugLog.log(string4);
						}

						try {
							movingObject = (IsoMovingObject)IsoObject.factoryFromFileInput(this.getCell(), byteBuffer);
						} catch (Exception exception2) {
							this.debugPrintGridSquare();
							if (lastLoaded != null) {
								lastLoaded.debugPrintGridSquare();
							}

							throw new RuntimeException(exception2);
						}

						if (movingObject != null) {
							movingObject.square = this;
							movingObject.current = this;
							try {
								movingObject.load(byteBuffer, int1, boolean1);
							} catch (Exception exception3) {
								this.debugPrintGridSquare();
								if (lastLoaded != null) {
									lastLoaded.debugPrintGridSquare();
								}

								throw new RuntimeException(exception3);
							}

							this.StaticMovingObjects.add(movingObject);
							this.recalcHashCodeObjects();
						}
					}
				}

				if (bitHeaderRead2.hasFlags(2)) {
					if (this.table == null) {
						this.table = LuaManager.platform.newTable();
					}

					this.table.load(byteBuffer, int1);
				}

				this.burntOut = bitHeaderRead2.hasFlags(4);
				if (bitHeaderRead2.hasFlags(8)) {
					this.setTrapPositionX(byteBuffer.getInt());
					this.setTrapPositionY(byteBuffer.getInt());
					this.setTrapPositionZ(byteBuffer.getInt());
				}

				this.haveSheetRope = bitHeaderRead2.hasFlags(16);
				bitHeaderRead2.release();
			}
		}

		bitHeaderRead.release();
		lastLoaded = this;
	}

	private void debugPrintGridSquare() {
		System.out.println("x=" + this.x + " y=" + this.y + " z=" + this.z);
		System.out.println("objects");
		int int1;
		for (int1 = 0; int1 < this.Objects.size(); ++int1) {
			((IsoObject)this.Objects.get(int1)).debugPrintout();
		}

		System.out.println("staticmovingobjects");
		for (int1 = 0; int1 < this.StaticMovingObjects.size(); ++int1) {
			((IsoObject)this.Objects.get(int1)).debugPrintout();
		}
	}

	public float scoreAsWaypoint(int int1, int int2) {
		float float1 = 2.0F;
		float1 -= IsoUtils.DistanceManhatten((float)int1, (float)int2, (float)this.getX(), (float)this.getY()) * 5.0F;
		return float1;
	}

	public void InvalidateSpecialObjectPaths() {
	}

	public boolean isSolid() {
		return this.Properties.Is(IsoFlagType.solid);
	}

	public boolean isSolidTrans() {
		return this.Properties.Is(IsoFlagType.solidtrans);
	}

	public boolean isFree(boolean boolean1) {
		if (boolean1 && this.MovingObjects.size() > 0) {
			return false;
		} else if (this.CachedIsFree) {
			return this.CacheIsFree;
		} else {
			this.CachedIsFree = true;
			this.CacheIsFree = true;
			if (this.Properties.Is(IsoFlagType.solid) || this.Properties.Is(IsoFlagType.solidtrans) || this.Has(IsoObjectType.tree)) {
				this.CacheIsFree = false;
			}

			if (!this.Properties.Is(IsoFlagType.solidfloor)) {
				this.CacheIsFree = false;
			}

			if (!this.Has(IsoObjectType.stairsBN) && !this.Has(IsoObjectType.stairsMN) && !this.Has(IsoObjectType.stairsTN)) {
				if (this.Has(IsoObjectType.stairsBW) || this.Has(IsoObjectType.stairsMW) || this.Has(IsoObjectType.stairsTW)) {
					this.CacheIsFree = true;
				}
			} else {
				this.CacheIsFree = true;
			}

			return this.CacheIsFree;
		}
	}

	public boolean isFreeOrMidair(boolean boolean1) {
		if (boolean1 && this.MovingObjects.size() > 0) {
			return false;
		} else {
			boolean boolean2 = true;
			if (this.Properties.Is(IsoFlagType.solid) || this.Properties.Is(IsoFlagType.solidtrans) || this.Has(IsoObjectType.tree)) {
				boolean2 = false;
			}

			if (!this.Has(IsoObjectType.stairsBN) && !this.Has(IsoObjectType.stairsMN) && !this.Has(IsoObjectType.stairsTN)) {
				if (this.Has(IsoObjectType.stairsBW) || this.Has(IsoObjectType.stairsMW) || this.Has(IsoObjectType.stairsTW)) {
					boolean2 = true;
				}
			} else {
				boolean2 = true;
			}

			return boolean2;
		}
	}

	public boolean isFreeOrMidair(boolean boolean1, boolean boolean2) {
		if (boolean1 && this.MovingObjects.size() > 0) {
			if (!boolean2) {
				return false;
			}

			for (int int1 = 0; int1 < this.MovingObjects.size(); ++int1) {
				IsoMovingObject movingObject = (IsoMovingObject)this.MovingObjects.get(int1);
				if (!(movingObject instanceof IsoDeadBody)) {
					return false;
				}
			}
		}

		boolean boolean3 = true;
		if (this.Properties.Is(IsoFlagType.solid) || this.Properties.Is(IsoFlagType.solidtrans) || this.Has(IsoObjectType.tree)) {
			boolean3 = false;
		}

		if (!this.Has(IsoObjectType.stairsBN) && !this.Has(IsoObjectType.stairsMN) && !this.Has(IsoObjectType.stairsTN)) {
			if (this.Has(IsoObjectType.stairsBW) || this.Has(IsoObjectType.stairsMW) || this.Has(IsoObjectType.stairsTW)) {
				boolean3 = true;
			}
		} else {
			boolean3 = true;
		}

		return boolean3;
	}

	public boolean connectedWithFloor() {
		if (this.getZ() == 0) {
			return true;
		} else {
			IsoGridSquare square = null;
			square = this.getCell().getGridSquare(this.getX() - 1, this.getY(), this.getZ());
			if (square != null && square.Properties.Is(IsoFlagType.solidfloor)) {
				return true;
			} else {
				square = this.getCell().getGridSquare(this.getX() + 1, this.getY(), this.getZ());
				if (square != null && square.Properties.Is(IsoFlagType.solidfloor)) {
					return true;
				} else {
					square = this.getCell().getGridSquare(this.getX(), this.getY() - 1, this.getZ());
					if (square != null && square.Properties.Is(IsoFlagType.solidfloor)) {
						return true;
					} else {
						square = this.getCell().getGridSquare(this.getX(), this.getY() + 1, this.getZ());
						return square != null && square.Properties.Is(IsoFlagType.solidfloor);
					}
				}
			}
		}
	}

	public boolean hasFloor(boolean boolean1) {
		if (this.Properties.Is(IsoFlagType.solidfloor)) {
			return true;
		} else {
			IsoGridSquare square = null;
			if (boolean1) {
				square = this.getCell().getGridSquare(this.getX(), this.getY() - 1, this.getZ());
			} else {
				square = this.getCell().getGridSquare(this.getX() - 1, this.getY(), this.getZ());
			}

			return square != null && square.Properties.Is(IsoFlagType.solidfloor);
		}
	}

	public boolean isNotBlocked(boolean boolean1) {
		if (!this.CachedIsFree) {
			this.CacheIsFree = true;
			this.CachedIsFree = true;
			if (this.Properties.Is(IsoFlagType.solid) || this.Properties.Is(IsoFlagType.solidtrans)) {
				this.CacheIsFree = false;
			}

			if (!this.Properties.Is(IsoFlagType.solidfloor)) {
				this.CacheIsFree = false;
			}
		} else if (!this.CacheIsFree) {
			return false;
		}

		return !boolean1 || this.MovingObjects.size() <= 0;
	}

	public IsoObject getDoor(boolean boolean1) {
		for (int int1 = 0; int1 < this.SpecialObjects.size(); ++int1) {
			IsoObject object = (IsoObject)this.SpecialObjects.get(int1);
			if (object instanceof IsoThumpable) {
				IsoThumpable thumpable = (IsoThumpable)object;
				if (thumpable.isDoor() && boolean1 == thumpable.north) {
					return thumpable;
				}
			}

			if (object instanceof IsoDoor) {
				IsoDoor door = (IsoDoor)object;
				if (boolean1 == door.north) {
					return door;
				}
			}
		}

		return null;
	}

	public IsoDoor getIsoDoor() {
		for (int int1 = 0; int1 < this.SpecialObjects.size(); ++int1) {
			IsoObject object = (IsoObject)this.SpecialObjects.get(int1);
			if (object instanceof IsoDoor) {
				return (IsoDoor)object;
			}
		}

		return null;
	}

	public IsoObject getDoorTo(IsoGridSquare square) {
		if (square != null && square != this) {
			IsoObject object = null;
			if (square.x < this.x) {
				object = this.getDoor(false);
				if (object != null) {
					return object;
				}
			}

			if (square.y < this.y) {
				object = this.getDoor(true);
				if (object != null) {
					return object;
				}
			}

			if (square.x > this.x) {
				object = square.getDoor(false);
				if (object != null) {
					return object;
				}
			}

			if (square.y > this.y) {
				object = square.getDoor(true);
				if (object != null) {
					return object;
				}
			}

			if (square.x != this.x && square.y != this.y) {
				IsoGridSquare square2 = this.getCell().getGridSquare(this.x, square.y, this.z);
				IsoGridSquare square3 = this.getCell().getGridSquare(square.x, this.y, this.z);
				object = this.getDoorTo(square2);
				if (object != null) {
					return object;
				}

				object = this.getDoorTo(square3);
				if (object != null) {
					return object;
				}

				object = square.getDoorTo(square2);
				if (object != null) {
					return object;
				}

				object = square.getDoorTo(square3);
				if (object != null) {
					return object;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	public IsoWindow getWindow(boolean boolean1) {
		for (int int1 = 0; int1 < this.SpecialObjects.size(); ++int1) {
			IsoObject object = (IsoObject)this.SpecialObjects.get(int1);
			if (object instanceof IsoWindow) {
				IsoWindow window = (IsoWindow)object;
				if (boolean1 == window.north) {
					return window;
				}
			}
		}

		return null;
	}

	public IsoWindow getWindow() {
		for (int int1 = 0; int1 < this.SpecialObjects.size(); ++int1) {
			IsoObject object = (IsoObject)this.SpecialObjects.get(int1);
			if (object instanceof IsoWindow) {
				return (IsoWindow)object;
			}
		}

		return null;
	}

	public IsoWindow getWindowTo(IsoGridSquare square) {
		if (square != null && square != this) {
			IsoWindow window = null;
			if (square.x < this.x) {
				window = this.getWindow(false);
				if (window != null) {
					return window;
				}
			}

			if (square.y < this.y) {
				window = this.getWindow(true);
				if (window != null) {
					return window;
				}
			}

			if (square.x > this.x) {
				window = square.getWindow(false);
				if (window != null) {
					return window;
				}
			}

			if (square.y > this.y) {
				window = square.getWindow(true);
				if (window != null) {
					return window;
				}
			}

			if (square.x != this.x && square.y != this.y) {
				IsoGridSquare square2 = this.getCell().getGridSquare(this.x, square.y, this.z);
				IsoGridSquare square3 = this.getCell().getGridSquare(square.x, this.y, this.z);
				window = this.getWindowTo(square2);
				if (window != null) {
					return window;
				}

				window = this.getWindowTo(square3);
				if (window != null) {
					return window;
				}

				window = square.getWindowTo(square2);
				if (window != null) {
					return window;
				}

				window = square.getWindowTo(square3);
				if (window != null) {
					return window;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	public boolean isAdjacentToWindow() {
		if (this.getWindow() != null) {
			return true;
		} else if (this.hasWindowFrame()) {
			return true;
		} else if (this.getThumpableWindow(false) == null && this.getThumpableWindow(true) == null) {
			IsoGridSquare square = this.nav[IsoDirections.S.index()];
			if (square != null && (square.getWindow(true) != null || square.getWindowFrame(true) != null || square.getThumpableWindow(true) != null)) {
				return true;
			} else {
				IsoGridSquare square2 = this.nav[IsoDirections.E.index()];
				return square2 != null && (square2.getWindow(false) != null || square2.getWindowFrame(false) != null || square2.getThumpableWindow(false) != null);
			}
		} else {
			return true;
		}
	}

	public IsoThumpable getThumpableWindow(boolean boolean1) {
		for (int int1 = 0; int1 < this.SpecialObjects.size(); ++int1) {
			IsoObject object = (IsoObject)this.SpecialObjects.get(int1);
			if (object instanceof IsoThumpable) {
				IsoThumpable thumpable = (IsoThumpable)object;
				if (thumpable.isWindow() && boolean1 == thumpable.north) {
					return thumpable;
				}
			}
		}

		return null;
	}

	public IsoThumpable getWindowThumpableTo(IsoGridSquare square) {
		if (square != null && square != this) {
			IsoThumpable thumpable = null;
			if (square.x < this.x) {
				thumpable = this.getThumpableWindow(false);
				if (thumpable != null) {
					return thumpable;
				}
			}

			if (square.y < this.y) {
				thumpable = this.getThumpableWindow(true);
				if (thumpable != null) {
					return thumpable;
				}
			}

			if (square.x > this.x) {
				thumpable = square.getThumpableWindow(false);
				if (thumpable != null) {
					return thumpable;
				}
			}

			if (square.y > this.y) {
				thumpable = square.getThumpableWindow(true);
				if (thumpable != null) {
					return thumpable;
				}
			}

			if (square.x != this.x && square.y != this.y) {
				IsoGridSquare square2 = this.getCell().getGridSquare(this.x, square.y, this.z);
				IsoGridSquare square3 = this.getCell().getGridSquare(square.x, this.y, this.z);
				thumpable = this.getWindowThumpableTo(square2);
				if (thumpable != null) {
					return thumpable;
				}

				thumpable = this.getWindowThumpableTo(square3);
				if (thumpable != null) {
					return thumpable;
				}

				thumpable = square.getWindowThumpableTo(square2);
				if (thumpable != null) {
					return thumpable;
				}

				thumpable = square.getWindowThumpableTo(square3);
				if (thumpable != null) {
					return thumpable;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	public IsoThumpable getHoppableThumpable(boolean boolean1) {
		for (int int1 = 0; int1 < this.SpecialObjects.size(); ++int1) {
			IsoObject object = (IsoObject)this.SpecialObjects.get(int1);
			if (object instanceof IsoThumpable) {
				IsoThumpable thumpable = (IsoThumpable)object;
				if (thumpable.isHoppable() && boolean1 == thumpable.north) {
					return thumpable;
				}
			}
		}

		return null;
	}

	public IsoThumpable getHoppableThumpableTo(IsoGridSquare square) {
		if (square != null && square != this) {
			IsoThumpable thumpable = null;
			if (square.x < this.x) {
				thumpable = this.getHoppableThumpable(false);
				if (thumpable != null) {
					return thumpable;
				}
			}

			if (square.y < this.y) {
				thumpable = this.getHoppableThumpable(true);
				if (thumpable != null) {
					return thumpable;
				}
			}

			if (square.x > this.x) {
				thumpable = square.getHoppableThumpable(false);
				if (thumpable != null) {
					return thumpable;
				}
			}

			if (square.y > this.y) {
				thumpable = square.getHoppableThumpable(true);
				if (thumpable != null) {
					return thumpable;
				}
			}

			if (square.x != this.x && square.y != this.y) {
				IsoGridSquare square2 = this.getCell().getGridSquare(this.x, square.y, this.z);
				IsoGridSquare square3 = this.getCell().getGridSquare(square.x, this.y, this.z);
				thumpable = this.getHoppableThumpableTo(square2);
				if (thumpable != null) {
					return thumpable;
				}

				thumpable = this.getHoppableThumpableTo(square3);
				if (thumpable != null) {
					return thumpable;
				}

				thumpable = square.getHoppableThumpableTo(square2);
				if (thumpable != null) {
					return thumpable;
				}

				thumpable = square.getHoppableThumpableTo(square3);
				if (thumpable != null) {
					return thumpable;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	public IsoObject getWallHoppable(boolean boolean1) {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			if (((IsoObject)this.Objects.get(int1)).isHoppable() && boolean1 == ((IsoObject)this.Objects.get(int1)).isNorthHoppable()) {
				return (IsoObject)this.Objects.get(int1);
			}
		}

		return null;
	}

	public IsoObject getWallHoppableTo(IsoGridSquare square) {
		if (square != null && square != this) {
			IsoObject object = null;
			if (square.x < this.x) {
				object = this.getWallHoppable(false);
				if (object != null) {
					return object;
				}
			}

			if (square.y < this.y) {
				object = this.getWallHoppable(true);
				if (object != null) {
					return object;
				}
			}

			if (square.x > this.x) {
				object = square.getWallHoppable(false);
				if (object != null) {
					return object;
				}
			}

			if (square.y > this.y) {
				object = square.getWallHoppable(true);
				if (object != null) {
					return object;
				}
			}

			if (square.x != this.x && square.y != this.y) {
				IsoGridSquare square2 = this.getCell().getGridSquare(this.x, square.y, this.z);
				IsoGridSquare square3 = this.getCell().getGridSquare(square.x, this.y, this.z);
				object = this.getWallHoppableTo(square2);
				if (object != null) {
					return object;
				}

				object = this.getWallHoppableTo(square3);
				if (object != null) {
					return object;
				}

				object = square.getWallHoppableTo(square2);
				if (object != null) {
					return object;
				}

				object = square.getWallHoppableTo(square3);
				if (object != null) {
					return object;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	public IsoObject getBedTo(IsoGridSquare square) {
		ArrayList arrayList = null;
		if (square.y >= this.y && square.x >= this.x) {
			arrayList = square.SpecialObjects;
		} else {
			arrayList = this.SpecialObjects;
		}

		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoObject object = (IsoObject)arrayList.get(int1);
			if (object.getProperties().Is(IsoFlagType.bed)) {
				return object;
			}
		}

		return null;
	}

	public IsoObject getWindowFrame(boolean boolean1) {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (!(object instanceof IsoWorldInventoryObject) && IsoWindowFrame.isWindowFrame(object, boolean1)) {
				return object;
			}
		}

		return null;
	}

	public IsoObject getWindowFrameTo(IsoGridSquare square) {
		if (square != null && square != this) {
			IsoObject object = null;
			if (square.x < this.x) {
				object = this.getWindowFrame(false);
				if (object != null) {
					return object;
				}
			}

			if (square.y < this.y) {
				object = this.getWindowFrame(true);
				if (object != null) {
					return object;
				}
			}

			if (square.x > this.x) {
				object = square.getWindowFrame(false);
				if (object != null) {
					return object;
				}
			}

			if (square.y > this.y) {
				object = square.getWindowFrame(true);
				if (object != null) {
					return object;
				}
			}

			if (square.x != this.x && square.y != this.y) {
				IsoGridSquare square2 = this.getCell().getGridSquare(this.x, square.y, this.z);
				IsoGridSquare square3 = this.getCell().getGridSquare(square.x, this.y, this.z);
				object = this.getWindowFrameTo(square2);
				if (object != null) {
					return object;
				}

				object = this.getWindowFrameTo(square3);
				if (object != null) {
					return object;
				}

				object = square.getWindowFrameTo(square2);
				if (object != null) {
					return object;
				}

				object = square.getWindowFrameTo(square3);
				if (object != null) {
					return object;
				}
			}

			return null;
		} else {
			return null;
		}
	}

	public boolean hasWindowFrame() {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (!(object instanceof IsoWorldInventoryObject) && IsoWindowFrame.isWindowFrame(object)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasWindowOrWindowFrame() {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (!(object instanceof IsoWorldInventoryObject) && (this.isWindowOrWindowFrame(object, true) || this.isWindowOrWindowFrame(object, false))) {
				return true;
			}
		}

		return false;
	}

	private IsoObject getSpecialWall(boolean boolean1) {
		for (int int1 = this.SpecialObjects.size() - 1; int1 >= 0; --int1) {
			IsoObject object = (IsoObject)this.SpecialObjects.get(int1);
			if (object instanceof IsoThumpable) {
				IsoThumpable thumpable = (IsoThumpable)object;
				if (thumpable.isStairs() || !thumpable.isThumpable() && !thumpable.isWindow() && !thumpable.isDoor() || thumpable.isDoor() && thumpable.open || thumpable.isBlockAllTheSquare()) {
					continue;
				}

				if (boolean1 == thumpable.north && !thumpable.isCorner()) {
					return thumpable;
				}
			}

			if (object instanceof IsoWindow) {
				IsoWindow window = (IsoWindow)object;
				if (boolean1 == window.north) {
					return window;
				}
			}

			if (object instanceof IsoDoor) {
				IsoDoor door = (IsoDoor)object;
				if (boolean1 == door.north && !door.open) {
					return door;
				}
			}
		}

		if (boolean1 && !this.Is(IsoFlagType.WindowN) || !boolean1 && !this.Is(IsoFlagType.WindowW)) {
			return null;
		} else {
			IsoObject object2 = this.getWindowFrame(boolean1);
			if (object2 != null) {
				return object2;
			} else {
				return null;
			}
		}
	}

	public IsoObject getSheetRope() {
		for (int int1 = 0; int1 < this.getObjects().size(); ++int1) {
			IsoObject object = (IsoObject)this.getObjects().get(int1);
			if (object.sheetRope) {
				return object;
			}
		}

		return null;
	}

	public boolean damageSpriteSheetRopeFromBottom(IsoPlayer player, boolean boolean1) {
		IsoGridSquare square = this;
		IsoFlagType flagType;
		if (boolean1) {
			if (this.Is(IsoFlagType.climbSheetN)) {
				flagType = IsoFlagType.climbSheetN;
			} else {
				if (!this.Is(IsoFlagType.climbSheetS)) {
					return false;
				}

				flagType = IsoFlagType.climbSheetS;
			}
		} else if (this.Is(IsoFlagType.climbSheetW)) {
			flagType = IsoFlagType.climbSheetW;
		} else {
			if (!this.Is(IsoFlagType.climbSheetE)) {
				return false;
			}

			flagType = IsoFlagType.climbSheetE;
		}

		while (square != null) {
			for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
				IsoObject object = (IsoObject)square.getObjects().get(int1);
				if (object.getProperties() != null && object.getProperties().Is(flagType)) {
					int int2 = Integer.parseInt(object.getSprite().getName().split("_")[2]);
					if (int2 > 14) {
						return false;
					}

					String string = object.getSprite().getName().split("_")[0];
					String string2 = string + "_" + object.getSprite().getName().split("_")[1];
					int2 += 40;
					object.setSprite(IsoSpriteManager.instance.getSprite(string2 + "_" + int2));
					object.transmitUpdatedSpriteToClients();
					break;
				}
			}

			if (square.getZ() == 7) {
				break;
			}

			square = square.getCell().getGridSquare(square.getX(), square.getY(), square.getZ() + 1);
		}

		return true;
	}

	public boolean removeSheetRopeFromBottom(IsoPlayer player, boolean boolean1) {
		IsoGridSquare square = this;
		IsoFlagType flagType;
		IsoFlagType flagType2;
		String string;
		int int1;
		IsoObject object;
		if (boolean1) {
			if (this.Is(IsoFlagType.climbSheetN)) {
				flagType = IsoFlagType.climbSheetTopN;
				flagType2 = IsoFlagType.climbSheetN;
			} else {
				if (!this.Is(IsoFlagType.climbSheetS)) {
					return false;
				}

				flagType = IsoFlagType.climbSheetTopS;
				flagType2 = IsoFlagType.climbSheetS;
				string = "crafted_01_4";
				for (int1 = 0; int1 < square.getObjects().size(); ++int1) {
					object = (IsoObject)square.getObjects().get(int1);
					if (object.sprite != null && object.sprite.getName() != null && object.sprite.getName().equals(string)) {
						if (GameServer.bServer) {
							square.transmitRemoveItemFromSquare(object);
						}

						square.RemoveTileObject(object);
						break;
					}
				}
			}
		} else if (this.Is(IsoFlagType.climbSheetW)) {
			flagType = IsoFlagType.climbSheetTopW;
			flagType2 = IsoFlagType.climbSheetW;
		} else {
			if (!this.Is(IsoFlagType.climbSheetE)) {
				return false;
			}

			flagType = IsoFlagType.climbSheetTopE;
			flagType2 = IsoFlagType.climbSheetE;
			string = "crafted_01_3";
			for (int1 = 0; int1 < square.getObjects().size(); ++int1) {
				object = (IsoObject)square.getObjects().get(int1);
				if (object.sprite != null && object.sprite.getName() != null && object.sprite.getName().equals(string)) {
					if (GameServer.bServer) {
						square.transmitRemoveItemFromSquare(object);
					}

					square.RemoveTileObject(object);
					break;
				}
			}
		}

		boolean boolean2 = false;
		IsoGridSquare square2;
		for (square2 = null; square != null; boolean2 = false) {
			for (int int2 = 0; int2 < square.getObjects().size(); ++int2) {
				IsoObject object2 = (IsoObject)square.getObjects().get(int2);
				if (object2.getProperties() != null && (object2.getProperties().Is(flagType) || object2.getProperties().Is(flagType2))) {
					square2 = square;
					boolean2 = true;
					if (GameServer.bServer) {
						square.transmitRemoveItemFromSquare(object2);
						square.RemoveTileObject(object2);
						if (player != null) {
							player.sendObjectChange("addItemOfType", new Object[]{"type", object2.getName()});
						}
					} else {
						square.RemoveTileObject(object2);
						if (player != null) {
							player.getInventory().AddItem(object2.getName());
						}
					}

					break;
				}
			}

			if (square.getZ() == 7) {
				break;
			}

			square = square.getCell().getGridSquare(square.getX(), square.getY(), square.getZ() + 1);
		}

		if (!boolean2) {
			square = square2.getCell().getGridSquare(square2.getX(), square2.getY(), square2.getZ());
			IsoGridSquare square3 = boolean1 ? square.nav[IsoDirections.S.index()] : square.nav[IsoDirections.E.index()];
			if (square3 == null) {
				return true;
			}

			for (int int3 = 0; int3 < square3.getObjects().size(); ++int3) {
				IsoObject object3 = (IsoObject)square3.getObjects().get(int3);
				if (object3.getProperties() != null && (object3.getProperties().Is(flagType) || object3.getProperties().Is(flagType2))) {
					if (GameServer.bServer) {
						square3.transmitRemoveItemFromSquare(object3);
						square3.RemoveTileObject(object3);
					} else {
						square3.RemoveTileObject(object3);
					}

					break;
				}
			}
		}

		return true;
	}

	private IsoObject getSpecialSolid() {
		for (int int1 = 0; int1 < this.SpecialObjects.size(); ++int1) {
			IsoObject object = (IsoObject)this.SpecialObjects.get(int1);
			if (object instanceof IsoThumpable) {
				IsoThumpable thumpable = (IsoThumpable)object;
				if (!thumpable.isStairs() && thumpable.isThumpable() && thumpable.isBlockAllTheSquare()) {
					if (thumpable.getProperties().Is(IsoFlagType.solidtrans) && this.isAdjacentToWindow()) {
						return null;
					}

					return thumpable;
				}
			}
		}

		return null;
	}

	public IsoObject testCollideSpecialObjects(IsoGridSquare square) {
		if (square != null && square != this) {
			IsoObject object;
			if (square.x < this.x && square.y == this.y) {
				if (square.z == this.z && this.Has(IsoObjectType.stairsTW)) {
					return null;
				} else {
					object = this.getSpecialWall(false);
					if (object != null) {
						return object;
					} else if (this.isBlockedTo(square)) {
						return null;
					} else {
						object = square.getSpecialSolid();
						return object != null ? object : null;
					}
				}
			} else if (square.x == this.x && square.y < this.y) {
				if (square.z == this.z && this.Has(IsoObjectType.stairsTN)) {
					return null;
				} else {
					object = this.getSpecialWall(true);
					if (object != null) {
						return object;
					} else if (this.isBlockedTo(square)) {
						return null;
					} else {
						object = square.getSpecialSolid();
						return object != null ? object : null;
					}
				}
			} else if (square.x > this.x && square.y == this.y) {
				object = square.getSpecialWall(false);
				if (object != null) {
					return object;
				} else if (this.isBlockedTo(square)) {
					return null;
				} else {
					object = square.getSpecialSolid();
					return object != null ? object : null;
				}
			} else if (square.x == this.x && square.y > this.y) {
				object = square.getSpecialWall(true);
				if (object != null) {
					return object;
				} else if (this.isBlockedTo(square)) {
					return null;
				} else {
					object = square.getSpecialSolid();
					return object != null ? object : null;
				}
			} else {
				IsoGridSquare square2;
				IsoGridSquare square3;
				if (square.x < this.x && square.y < this.y) {
					object = this.getSpecialWall(true);
					if (object != null) {
						return object;
					} else {
						object = this.getSpecialWall(false);
						if (object != null) {
							return object;
						} else {
							square2 = this.getCell().getGridSquare(this.x, this.y - 1, this.z);
							if (square2 != null && !this.isBlockedTo(square2)) {
								object = square2.getSpecialSolid();
								if (object != null) {
									return object;
								}

								object = square2.getSpecialWall(false);
								if (object != null) {
									return object;
								}
							}

							square3 = this.getCell().getGridSquare(this.x - 1, this.y, this.z);
							if (square3 != null && !this.isBlockedTo(square3)) {
								object = square3.getSpecialSolid();
								if (object != null) {
									return object;
								}

								object = square3.getSpecialWall(true);
								if (object != null) {
									return object;
								}
							}

							if (square2 != null && !this.isBlockedTo(square2) && square3 != null && !this.isBlockedTo(square3)) {
								if (!square2.isBlockedTo(square) && !square3.isBlockedTo(square)) {
									object = square.getSpecialSolid();
									return object != null ? object : null;
								} else {
									return null;
								}
							} else {
								return null;
							}
						}
					}
				} else if (square.x > this.x && square.y < this.y) {
					object = this.getSpecialWall(true);
					if (object != null) {
						return object;
					} else {
						square2 = this.getCell().getGridSquare(this.x, this.y - 1, this.z);
						if (square2 != null && !this.isBlockedTo(square2)) {
							object = square2.getSpecialSolid();
							if (object != null) {
								return object;
							}
						}

						square3 = this.getCell().getGridSquare(this.x + 1, this.y, this.z);
						if (square3 != null) {
							object = square3.getSpecialWall(false);
							if (object != null) {
								return object;
							}

							if (!this.isBlockedTo(square3)) {
								object = square3.getSpecialSolid();
								if (object != null) {
									return object;
								}

								object = square3.getSpecialWall(true);
								if (object != null) {
									return object;
								}
							}
						}

						if (square2 != null && !this.isBlockedTo(square2) && square3 != null && !this.isBlockedTo(square3)) {
							object = square.getSpecialWall(false);
							if (object != null) {
								return object;
							} else if (!square2.isBlockedTo(square) && !square3.isBlockedTo(square)) {
								object = square.getSpecialSolid();
								return object != null ? object : null;
							} else {
								return null;
							}
						} else {
							return null;
						}
					}
				} else if (square.x > this.x && square.y > this.y) {
					square2 = this.getCell().getGridSquare(this.x, this.y + 1, this.z);
					if (square2 != null) {
						object = square2.getSpecialWall(true);
						if (object != null) {
							return object;
						}

						if (!this.isBlockedTo(square2)) {
							object = square2.getSpecialSolid();
							if (object != null) {
								return object;
							}
						}
					}

					square3 = this.getCell().getGridSquare(this.x + 1, this.y, this.z);
					if (square3 != null) {
						object = square3.getSpecialWall(false);
						if (object != null) {
							return object;
						}

						if (!this.isBlockedTo(square3)) {
							object = square3.getSpecialSolid();
							if (object != null) {
								return object;
							}
						}
					}

					if (square2 != null && !this.isBlockedTo(square2) && square3 != null && !this.isBlockedTo(square3)) {
						object = square.getSpecialWall(false);
						if (object != null) {
							return object;
						} else {
							object = square.getSpecialWall(true);
							if (object != null) {
								return object;
							} else if (!square2.isBlockedTo(square) && !square3.isBlockedTo(square)) {
								object = square.getSpecialSolid();
								return object != null ? object : null;
							} else {
								return null;
							}
						}
					} else {
						return null;
					}
				} else if (square.x < this.x && square.y > this.y) {
					object = this.getSpecialWall(false);
					if (object != null) {
						return object;
					} else {
						square2 = this.getCell().getGridSquare(this.x, this.y + 1, this.z);
						if (square2 != null) {
							object = square2.getSpecialWall(true);
							if (object != null) {
								return object;
							}

							if (!this.isBlockedTo(square2)) {
								object = square2.getSpecialSolid();
								if (object != null) {
									return object;
								}
							}
						}

						square3 = this.getCell().getGridSquare(this.x - 1, this.y, this.z);
						if (square3 != null && !this.isBlockedTo(square3)) {
							object = square3.getSpecialSolid();
							if (object != null) {
								return object;
							}
						}

						if (square2 != null && !this.isBlockedTo(square2) && square3 != null && !this.isBlockedTo(square3)) {
							object = square.getSpecialWall(true);
							if (object != null) {
								return object;
							} else if (!square2.isBlockedTo(square) && !square3.isBlockedTo(square)) {
								object = square.getSpecialSolid();
								return object != null ? object : null;
							} else {
								return null;
							}
						} else {
							return null;
						}
					}
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}

	public IsoObject getDoorFrameTo(IsoGridSquare square) {
		ArrayList arrayList = null;
		if (square.y >= this.y && square.x >= this.x) {
			arrayList = square.SpecialObjects;
		} else {
			arrayList = this.SpecialObjects;
		}

		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			boolean boolean1;
			if (arrayList.get(int1) instanceof IsoDoor) {
				IsoDoor door = (IsoDoor)arrayList.get(int1);
				boolean1 = door.north;
				if (boolean1 && square.y != this.y) {
					return door;
				}

				if (!boolean1 && square.x != this.x) {
					return door;
				}
			} else if (arrayList.get(int1) instanceof IsoThumpable && ((IsoThumpable)arrayList.get(int1)).isDoor) {
				IsoThumpable thumpable = (IsoThumpable)arrayList.get(int1);
				boolean1 = thumpable.north;
				if (boolean1 && square.y != this.y) {
					return thumpable;
				}

				if (!boolean1 && square.x != this.x) {
					return thumpable;
				}
			}
		}

		return null;
	}

	public static void getSquaresForThread(ArrayDeque arrayDeque, int int1) {
		for (int int2 = 0; int2 < int1; ++int2) {
			IsoGridSquare square = (IsoGridSquare)isoGridSquareCache.poll();
			if (square == null) {
				arrayDeque.add(new IsoGridSquare((IsoCell)null, (SliceY)null, 0, 0, 0));
			} else {
				arrayDeque.add(square);
			}
		}
	}

	public static IsoGridSquare getNew(IsoCell cell, SliceY sliceY, int int1, int int2, int int3) {
		IsoGridSquare square = (IsoGridSquare)isoGridSquareCache.poll();
		if (square == null) {
			return new IsoGridSquare(cell, sliceY, int1, int2, int3);
		} else {
			square.x = int1;
			square.y = int2;
			square.z = int3;
			square.CachedScreenValue = -1;
			col = 0;
			path = 0;
			pathdoor = 0;
			vision = 0;
			square.collideMatrix = 134217727;
			square.pathMatrix = 134217727;
			square.visionMatrix = 0;
			return square;
		}
	}

	public static IsoGridSquare getNew(ArrayDeque arrayDeque, IsoCell cell, SliceY sliceY, int int1, int int2, int int3) {
		IsoGridSquare square = null;
		if (arrayDeque.isEmpty()) {
			return new IsoGridSquare(cell, sliceY, int1, int2, int3);
		} else {
			square = (IsoGridSquare)arrayDeque.pop();
			square.x = int1;
			square.y = int2;
			square.z = int3;
			square.CachedScreenValue = -1;
			col = 0;
			path = 0;
			pathdoor = 0;
			vision = 0;
			square.collideMatrix = 134217727;
			square.pathMatrix = 134217727;
			square.visionMatrix = 0;
			return square;
		}
	}

	@Deprecated
	public long getHashCodeObjects() {
		this.recalcHashCodeObjects();
		return this.hashCodeObjects;
	}

	@Deprecated
	public int getHashCodeObjectsInt() {
		this.recalcHashCodeObjects();
		return (int)this.hashCodeObjects;
	}

	@Deprecated
	public void recalcHashCodeObjects() {
		long long1 = 0L;
		this.hashCodeObjects = long1;
	}

	@Deprecated
	public int hashCodeNoOverride() {
		byte byte1 = 0;
		this.recalcHashCodeObjects();
		int int1 = byte1 * 2 + this.Objects.size();
		int1 = (int)((long)int1 + this.getHashCodeObjects());
		int int2;
		for (int2 = 0; int2 < this.Objects.size(); ++int2) {
			int1 = int1 * 2 + ((IsoObject)this.Objects.get(int2)).hashCode();
		}

		int2 = 0;
		int int3;
		for (int3 = 0; int3 < this.StaticMovingObjects.size(); ++int3) {
			if (this.StaticMovingObjects.get(int3) instanceof IsoDeadBody) {
				++int2;
			}
		}

		int1 = int1 * 2 + int2;
		for (int3 = 0; int3 < this.StaticMovingObjects.size(); ++int3) {
			IsoMovingObject movingObject = (IsoMovingObject)this.StaticMovingObjects.get(int3);
			if (movingObject instanceof IsoDeadBody) {
				int1 = int1 * 2 + movingObject.hashCode();
			}
		}

		if (this.table != null && !this.table.isEmpty()) {
			int1 = int1 * 2 + this.table.hashCode();
		}

		byte byte2 = 0;
		if (this.isOverlayDone()) {
			byte2 = (byte)(byte2 | 1);
		}

		if (this.haveRoof) {
			byte2 = (byte)(byte2 | 2);
		}

		if (this.burntOut) {
			byte2 = (byte)(byte2 | 4);
		}

		int1 = int1 * 2 + byte2;
		int1 = int1 * 2 + this.getErosionData().hashCode();
		if (this.getTrapPositionX() > 0) {
			int1 = int1 * 2 + this.getTrapPositionX();
			int1 = int1 * 2 + this.getTrapPositionY();
			int1 = int1 * 2 + this.getTrapPositionZ();
		}

		int1 = int1 * 2 + (this.haveElectricity() ? 1 : 0);
		int1 = int1 * 2 + (this.haveSheetRope ? 1 : 0);
		return int1;
	}

	public IsoGridSquare(IsoCell cell, SliceY sliceY, int int1, int int2, int int3) {
		this.hasTypes = new ZomboidBitFlag(IsoObjectType.MAX.index());
		this.Properties = new PropertyContainer();
		this.SpecialObjects = new ArrayList(0);
		this.haveRoof = false;
		this.burntOut = false;
		this.bHasFlies = false;
		this.OcclusionDataCache = null;
		this.overlayDone = false;
		this.table = null;
		this.trapPositionX = -1;
		this.trapPositionY = -1;
		this.trapPositionZ = -1;
		this.haveElectricity = false;
		this.hourLastSeen = Integer.MIN_VALUE;
		this.propertiesDirty = true;
		this.splashFrame = -1.0F;
		this.lightInfo = new ColorInfo[4];
		this.RainDrop = null;
		this.RainSplash = null;
		this.ID = ++IDMax;
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.CachedScreenValue = -1;
		col = 0;
		path = 0;
		pathdoor = 0;
		vision = 0;
		this.collideMatrix = 134217727;
		this.pathMatrix = 134217727;
		this.visionMatrix = 0;
		for (int int4 = 0; int4 < 4; ++int4) {
			if (GameServer.bServer) {
				if (int4 == 0) {
					this.lighting[int4] = new ServerLOS.ServerLighting();
				}
			} else if (LightingJNI.init) {
				this.lighting[int4] = new LightingJNI.JNILighting(int4, this);
			} else {
				this.lighting[int4] = new IsoGridSquare.Lighting();
			}
		}
	}

	public IsoGridSquare getTileInDirection(IsoDirections directions) {
		if (directions == IsoDirections.N) {
			return this.getCell().getGridSquare(this.x, this.y - 1, this.z);
		} else if (directions == IsoDirections.NE) {
			return this.getCell().getGridSquare(this.x + 1, this.y - 1, this.z);
		} else if (directions == IsoDirections.NW) {
			return this.getCell().getGridSquare(this.x - 1, this.y - 1, this.z);
		} else if (directions == IsoDirections.E) {
			return this.getCell().getGridSquare(this.x + 1, this.y, this.z);
		} else if (directions == IsoDirections.W) {
			return this.getCell().getGridSquare(this.x - 1, this.y, this.z);
		} else if (directions == IsoDirections.SE) {
			return this.getCell().getGridSquare(this.x + 1, this.y + 1, this.z);
		} else if (directions == IsoDirections.SW) {
			return this.getCell().getGridSquare(this.x - 1, this.y + 1, this.z);
		} else {
			return directions == IsoDirections.S ? this.getCell().getGridSquare(this.x, this.y + 1, this.z) : null;
		}
	}

	IsoObject getWall() {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (object != null && object.sprite != null && (object.sprite.cutW || object.sprite.cutN)) {
				return object;
			}
		}

		return null;
	}

	public IsoObject getThumpableWall(boolean boolean1) {
		IsoObject object = this.getWall(boolean1);
		return object != null && object instanceof IsoThumpable ? object : null;
	}

	public IsoObject getHoppableWall(boolean boolean1) {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (object != null && object.sprite != null) {
				PropertyContainer propertyContainer = object.getProperties();
				boolean boolean2 = propertyContainer.Is(IsoFlagType.TallHoppableW) && !propertyContainer.Is(IsoFlagType.WallWTrans);
				boolean boolean3 = propertyContainer.Is(IsoFlagType.TallHoppableN) && !propertyContainer.Is(IsoFlagType.WallNTrans);
				if (boolean2 && !boolean1 || boolean3 && boolean1) {
					return object;
				}
			}
		}

		return null;
	}

	public IsoObject getThumpableWallOrHoppable(boolean boolean1) {
		IsoObject object = this.getThumpableWall(boolean1);
		IsoObject object2 = this.getHoppableWall(boolean1);
		if (object != null && object2 != null && object == object2) {
			return object;
		} else if (object == null && object2 != null) {
			return object2;
		} else {
			return object != null && object2 == null ? object : null;
		}
	}

	public IsoObject getWall(boolean boolean1) {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (object != null && object.sprite != null && (object.sprite.cutN && boolean1 || object.sprite.cutW && !boolean1)) {
				return object;
			}
		}

		return null;
	}

	public IsoObject getWallSE() {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (object != null && object.sprite != null && object.sprite.getProperties().Is(IsoFlagType.WallSE)) {
				return object;
			}
		}

		return null;
	}

	public IsoObject getFloor() {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (object.sprite != null && object.sprite.getProperties().Is(IsoFlagType.solidfloor)) {
				return object;
			}
		}

		return null;
	}

	public IsoObject getPlayerBuiltFloor() {
		return this.getBuilding() == null && this.roofHideBuilding == null ? this.getFloor() : null;
	}

	public void interpolateLight(ColorInfo colorInfo, float float1, float float2) {
		IsoCell cell = this.getCell();
		if (float1 < 0.0F) {
			float1 = 0.0F;
		}

		if (float1 > 1.0F) {
			float1 = 1.0F;
		}

		if (float2 < 0.0F) {
			float2 = 0.0F;
		}

		if (float2 > 1.0F) {
			float2 = 1.0F;
		}

		int int1 = IsoCamera.frameState.playerIndex;
		int int2 = this.getVertLight(0, int1);
		int int3 = this.getVertLight(1, int1);
		int int4 = this.getVertLight(2, int1);
		int int5 = this.getVertLight(3, int1);
		tl.fromColor(int2);
		bl.fromColor(int5);
		tr.fromColor(int3);
		br.fromColor(int4);
		tl.interp(tr, float1, interp1);
		bl.interp(br, float1, interp2);
		interp1.interp(interp2, float2, finalCol);
		colorInfo.r = finalCol.r;
		colorInfo.g = finalCol.g;
		colorInfo.b = finalCol.b;
		colorInfo.a = finalCol.a;
	}

	public void EnsureSurroundNotNull() {
		assert !GameServer.bServer;
		for (int int1 = -1; int1 <= 1; ++int1) {
			for (int int2 = -1; int2 <= 1; ++int2) {
				if ((int1 != 0 || int2 != 0) && IsoWorld.instance.isValidSquare(this.x + int1, this.y + int2, this.z) && this.getCell().getChunkForGridSquare(this.x + int1, this.y + int2, this.z) != null) {
					IsoGridSquare square = this.getCell().getGridSquare(this.x + int1, this.y + int2, this.z);
					if (square == null) {
						square = getNew(this.getCell(), (SliceY)null, this.x + int1, this.y + int2, this.z);
						IsoGridSquare square2 = this.getCell().ConnectNewSquare(square, false);
					}
				}
			}
		}
	}

	public IsoObject addFloor(String string) {
		IsoRegions.setPreviousFlags(this);
		IsoObject object = new IsoObject(this.getCell(), this, string);
		boolean boolean1 = false;
		int int1;
		for (int1 = 0; int1 < this.getObjects().size(); ++int1) {
			IsoObject object2 = (IsoObject)this.getObjects().get(int1);
			IsoSprite sprite = object2.sprite;
			if (sprite != null && (sprite.getProperties().Is(IsoFlagType.solidfloor) || sprite.getProperties().Is(IsoFlagType.noStart) || sprite.getProperties().Is(IsoFlagType.vegitation) && object2.getType() != IsoObjectType.tree || sprite.getName() != null && sprite.getName().startsWith("blends_grassoverlays"))) {
				if (sprite.getName() != null && sprite.getName().startsWith("floors_rugs")) {
					boolean1 = true;
				} else {
					this.transmitRemoveItemFromSquare(object2);
					--int1;
				}
			}
		}

		object.sprite.getProperties().Set(IsoFlagType.solidfloor);
		if (boolean1) {
			this.getObjects().add(0, object);
		} else {
			this.getObjects().add(object);
		}

		this.EnsureSurroundNotNull();
		this.RecalcProperties();
		this.getCell().checkHaveRoof(this.x, this.y);
		for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			LosUtil.cachecleared[int1] = true;
		}

		setRecalcLightTime(-1);
		GameTime.getInstance().lightSourceUpdate = 100.0F;
		object.transmitCompleteItemToServer();
		this.RecalcAllWithNeighbours(true);
		for (int1 = this.z - 1; int1 > 0; --int1) {
			IsoGridSquare square = this.getCell().getGridSquare(this.x, this.y, int1);
			if (square == null) {
				square = getNew(this.getCell(), (SliceY)null, this.x, this.y, int1);
				this.getCell().ConnectNewSquare(square, false);
			}

			square.EnsureSurroundNotNull();
			square.RecalcAllWithNeighbours(true);
		}

		this.setCachedIsFree(false);
		PolygonalMap2.instance.squareChanged(this);
		IsoGridOcclusionData.SquareChanged();
		IsoRegions.squareChanged(this);
		this.clearWater();
		return object;
	}

	public IsoThumpable AddStairs(boolean boolean1, int int1, String string, String string2, KahluaTable kahluaTable) {
		IsoRegions.setPreviousFlags(this);
		this.EnsureSurroundNotNull();
		boolean boolean2 = !this.TreatAsSolidFloor() && !this.HasStairsBelow();
		this.CachedIsFree = false;
		IsoThumpable thumpable = new IsoThumpable(this.getCell(), this, string, boolean1, kahluaTable);
		if (boolean1) {
			if (int1 == 0) {
				thumpable.setType(IsoObjectType.stairsBN);
			}

			if (int1 == 1) {
				thumpable.setType(IsoObjectType.stairsMN);
			}

			if (int1 == 2) {
				thumpable.setType(IsoObjectType.stairsTN);
				thumpable.sprite.getProperties().Set(boolean1 ? IsoFlagType.cutN : IsoFlagType.cutW);
			}
		}

		if (!boolean1) {
			if (int1 == 0) {
				thumpable.setType(IsoObjectType.stairsBW);
			}

			if (int1 == 1) {
				thumpable.setType(IsoObjectType.stairsMW);
			}

			if (int1 == 2) {
				thumpable.setType(IsoObjectType.stairsTW);
				thumpable.sprite.getProperties().Set(boolean1 ? IsoFlagType.cutN : IsoFlagType.cutW);
			}
		}

		this.AddSpecialObject(thumpable);
		int int2;
		if (boolean2 && int1 == 2) {
			int2 = this.z - 1;
			IsoGridSquare square = this.getCell().getGridSquare(this.x, this.y, int2);
			if (square == null) {
				square = new IsoGridSquare(this.getCell(), (SliceY)null, this.x, this.y, int2);
				this.getCell().ConnectNewSquare(square, true);
			}

			while (int2 >= 0) {
				IsoThumpable thumpable2 = new IsoThumpable(this.getCell(), square, string2, boolean1, kahluaTable);
				square.AddSpecialObject(thumpable2);
				if (square.TreatAsSolidFloor()) {
					break;
				}

				--int2;
				if (this.getCell().getGridSquare(square.x, square.y, int2) == null) {
					square = new IsoGridSquare(this.getCell(), (SliceY)null, square.x, square.y, int2);
					this.getCell().ConnectNewSquare(square, true);
				} else {
					square = this.getCell().getGridSquare(square.x, square.y, int2);
				}
			}
		}

		if (int1 == 2) {
			IsoGridSquare square2 = null;
			if (boolean1) {
				if (IsoWorld.instance.isValidSquare(this.x, this.y - 1, this.z + 1)) {
					square2 = this.getCell().getGridSquare(this.x, this.y - 1, this.z + 1);
					if (square2 == null) {
						square2 = new IsoGridSquare(this.getCell(), (SliceY)null, this.x, this.y - 1, this.z + 1);
						this.getCell().ConnectNewSquare(square2, false);
					}

					if (!square2.Properties.Is(IsoFlagType.solidfloor)) {
						square2.addFloor("carpentry_02_57");
					}
				}
			} else if (IsoWorld.instance.isValidSquare(this.x - 1, this.y, this.z + 1)) {
				square2 = this.getCell().getGridSquare(this.x - 1, this.y, this.z + 1);
				if (square2 == null) {
					square2 = new IsoGridSquare(this.getCell(), (SliceY)null, this.x - 1, this.y, this.z + 1);
					this.getCell().ConnectNewSquare(square2, false);
				}

				if (!square2.Properties.Is(IsoFlagType.solidfloor)) {
					square2.addFloor("carpentry_02_57");
				}
			}

			square2.getModData().rawset("ConnectedToStairs" + boolean1, true);
			square2 = this.getCell().getGridSquare(this.x, this.y, this.z + 1);
			if (square2 == null) {
				square2 = new IsoGridSquare(this.getCell(), (SliceY)null, this.x, this.y, this.z + 1);
				this.getCell().ConnectNewSquare(square2, false);
			}
		}

		for (int2 = this.getX() - 1; int2 <= this.getX() + 1; ++int2) {
			for (int int3 = this.getY() - 1; int3 <= this.getY() + 1; ++int3) {
				for (int int4 = this.getZ() - 1; int4 <= this.getZ() + 1; ++int4) {
					if (IsoWorld.instance.isValidSquare(int2, int3, int4)) {
						IsoGridSquare square3 = this.getCell().getGridSquare(int2, int3, int4);
						if (square3 == null) {
							square3 = new IsoGridSquare(this.getCell(), (SliceY)null, int2, int3, int4);
							this.getCell().ConnectNewSquare(square3, false);
						}

						square3.ReCalculateCollide(this);
						square3.ReCalculateVisionBlocked(this);
						square3.ReCalculatePathFind(this);
						this.ReCalculateCollide(square3);
						this.ReCalculatePathFind(square3);
						this.ReCalculateVisionBlocked(square3);
						square3.CachedIsFree = false;
					}
				}
			}
		}

		return thumpable;
	}

	void ReCalculateAll(IsoGridSquare square) {
		this.ReCalculateAll(square, cellGetSquare);
	}

	void ReCalculateAll(IsoGridSquare square, IsoGridSquare.GetSquare getSquare) {
		if (square != null && square != this) {
			this.SolidFloorCached = false;
			square.SolidFloorCached = false;
			this.RecalcPropertiesIfNeeded();
			square.RecalcPropertiesIfNeeded();
			this.ReCalculateCollide(square, getSquare);
			square.ReCalculateCollide(this, getSquare);
			this.ReCalculatePathFind(square, getSquare);
			square.ReCalculatePathFind(this, getSquare);
			this.ReCalculateVisionBlocked(square, getSquare);
			square.ReCalculateVisionBlocked(this, getSquare);
			this.setBlockedGridPointers(getSquare);
			square.setBlockedGridPointers(getSquare);
		}
	}

	void ReCalculateAll(boolean boolean1, IsoGridSquare square, IsoGridSquare.GetSquare getSquare) {
		if (square != null && square != this) {
			this.SolidFloorCached = false;
			square.SolidFloorCached = false;
			this.RecalcPropertiesIfNeeded();
			if (boolean1) {
				square.RecalcPropertiesIfNeeded();
			}

			this.ReCalculateCollide(square, getSquare);
			if (boolean1) {
				square.ReCalculateCollide(this, getSquare);
			}

			this.ReCalculatePathFind(square, getSquare);
			if (boolean1) {
				square.ReCalculatePathFind(this, getSquare);
			}

			this.ReCalculateVisionBlocked(square, getSquare);
			if (boolean1) {
				square.ReCalculateVisionBlocked(this, getSquare);
			}

			this.setBlockedGridPointers(getSquare);
			if (boolean1) {
				square.setBlockedGridPointers(getSquare);
			}
		}
	}

	void ReCalculateMineOnly(IsoGridSquare square) {
		this.SolidFloorCached = false;
		this.RecalcProperties();
		this.ReCalculateCollide(square);
		this.ReCalculatePathFind(square);
		this.ReCalculateVisionBlocked(square);
		this.setBlockedGridPointers(cellGetSquare);
	}

	public void RecalcAllWithNeighbours(boolean boolean1) {
		this.RecalcAllWithNeighbours(boolean1, cellGetSquare);
	}

	public void RecalcAllWithNeighbours(boolean boolean1, IsoGridSquare.GetSquare getSquare) {
		this.SolidFloorCached = false;
		this.RecalcPropertiesIfNeeded();
		for (int int1 = this.getX() - 1; int1 <= this.getX() + 1; ++int1) {
			for (int int2 = this.getY() - 1; int2 <= this.getY() + 1; ++int2) {
				for (int int3 = this.getZ() - 1; int3 <= this.getZ() + 1; ++int3) {
					if (IsoWorld.instance.isValidSquare(int1, int2, int3)) {
						int int4 = int1 - this.getX();
						int int5 = int2 - this.getY();
						int int6 = int3 - this.getZ();
						if (int4 != 0 || int5 != 0 || int6 != 0) {
							IsoGridSquare square = getSquare.getGridSquare(int1, int2, int3);
							if (square != null) {
								square.DirtySlice();
								this.ReCalculateAll(boolean1, square, getSquare);
							}
						}
					}
				}
			}
		}

		IsoWorld.instance.CurrentCell.DoGridNav(this, getSquare);
		IsoGridSquare square2 = this.nav[IsoDirections.N.index()];
		IsoGridSquare square3 = this.nav[IsoDirections.S.index()];
		IsoGridSquare square4 = this.nav[IsoDirections.W.index()];
		IsoGridSquare square5 = this.nav[IsoDirections.E.index()];
		if (square2 != null && square4 != null) {
			square2.ReCalculateAll(square4, getSquare);
		}

		if (square2 != null && square5 != null) {
			square2.ReCalculateAll(square5, getSquare);
		}

		if (square3 != null && square4 != null) {
			square3.ReCalculateAll(square4, getSquare);
		}

		if (square3 != null && square5 != null) {
			square3.ReCalculateAll(square5, getSquare);
		}
	}

	public void RecalcAllWithNeighboursMineOnly() {
		this.SolidFloorCached = false;
		this.RecalcProperties();
		for (int int1 = this.getX() - 1; int1 <= this.getX() + 1; ++int1) {
			for (int int2 = this.getY() - 1; int2 <= this.getY() + 1; ++int2) {
				for (int int3 = this.getZ() - 1; int3 <= this.getZ() + 1; ++int3) {
					if (int3 >= 0) {
						int int4 = int1 - this.getX();
						int int5 = int2 - this.getY();
						int int6 = int3 - this.getZ();
						if (int4 != 0 || int5 != 0 || int6 != 0) {
							IsoGridSquare square = this.getCell().getGridSquare(int1, int2, int3);
							if (square != null) {
								square.DirtySlice();
								this.ReCalculateMineOnly(square);
							}
						}
					}
				}
			}
		}
	}

	boolean IsWindow(int int1, int int2, int int3) {
		IsoGridSquare square = this.getCell().getGridSquare(this.x + int1, this.y + int2, this.z + int3);
		return this.getWindowTo(square) != null || this.getWindowThumpableTo(square) != null;
	}

	void RemoveAllWith(IsoFlagType flagType) {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (object.sprite != null && object.sprite.getProperties().Is(flagType)) {
				this.Objects.remove(object);
				this.SpecialObjects.remove(object);
				--int1;
			}
		}

		this.RecalcAllWithNeighbours(true);
	}

	public boolean hasSupport() {
		IsoGridSquare square = this.getCell().getGridSquare(this.x, this.y + 1, this.z);
		IsoGridSquare square2 = this.getCell().getGridSquare(this.x + 1, this.y, this.z);
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (object.sprite != null && (object.sprite.getProperties().Is(IsoFlagType.solid) || (object.sprite.getProperties().Is(IsoFlagType.cutW) || object.sprite.getProperties().Is(IsoFlagType.cutN)) && !object.sprite.Properties.Is(IsoFlagType.halfheight))) {
				return true;
			}
		}

		if (square != null && square.Properties.Is(IsoFlagType.cutN) && !square.Properties.Is(IsoFlagType.halfheight)) {
			return true;
		} else if (square2 != null && square2.Properties.Is(IsoFlagType.cutW) && !square.Properties.Is(IsoFlagType.halfheight)) {
			return true;
		} else {
			return false;
		}
	}

	public Integer getID() {
		return this.ID;
	}

	public void setID(int int1) {
		this.ID = int1;
	}

	private int savematrix(boolean[][][] booleanArrayArrayArray, byte[] byteArray, int int1) {
		for (int int2 = 0; int2 <= 2; ++int2) {
			for (int int3 = 0; int3 <= 2; ++int3) {
				for (int int4 = 0; int4 <= 2; ++int4) {
					byteArray[int1] = (byte)(booleanArrayArrayArray[int2][int3][int4] ? 1 : 0);
					++int1;
				}
			}
		}

		return int1;
	}

	private int loadmatrix(boolean[][][] booleanArrayArrayArray, byte[] byteArray, int int1) {
		for (int int2 = 0; int2 <= 2; ++int2) {
			for (int int3 = 0; int3 <= 2; ++int3) {
				for (int int4 = 0; int4 <= 2; ++int4) {
					booleanArrayArrayArray[int2][int3][int4] = byteArray[int1] != 0;
					++int1;
				}
			}
		}

		return int1;
	}

	private void savematrix(boolean[][][] booleanArrayArrayArray, ByteBuffer byteBuffer) {
		for (int int1 = 0; int1 <= 2; ++int1) {
			for (int int2 = 0; int2 <= 2; ++int2) {
				for (int int3 = 0; int3 <= 2; ++int3) {
					byteBuffer.put((byte)(booleanArrayArrayArray[int1][int2][int3] ? 1 : 0));
				}
			}
		}
	}

	private void loadmatrix(boolean[][][] booleanArrayArrayArray, ByteBuffer byteBuffer) {
		for (int int1 = 0; int1 <= 2; ++int1) {
			for (int int2 = 0; int2 <= 2; ++int2) {
				for (int int3 = 0; int3 <= 2; ++int3) {
					booleanArrayArrayArray[int1][int2][int3] = byteBuffer.get() != 0;
				}
			}
		}
	}

	public void DirtySlice() {
	}

	public void setHourSeenToCurrent() {
		this.hourLastSeen = (int)GameTime.instance.getWorldAgeHours();
	}

	public void splatBlood(int int1, float float1) {
		float1 *= 2.0F;
		float1 *= 3.0F;
		if (float1 > 1.0F) {
			float1 = 1.0F;
		}

		IsoGridSquare square = this;
		IsoGridSquare square2 = this;
		for (int int2 = 0; int2 < int1; ++int2) {
			if (square != null) {
				square = this.getCell().getGridSquare(this.getX(), this.getY() - int2, this.getZ());
			}

			if (square2 != null) {
				square2 = this.getCell().getGridSquare(this.getX() - int2, this.getY(), this.getZ());
			}

			float float2 = 0.0F;
			boolean boolean1;
			boolean boolean2;
			byte byte1;
			byte byte2;
			int int3;
			boolean boolean3;
			byte byte3;
			byte byte4;
			float float3;
			IsoGridSquare square3;
			IsoGridSquare square4;
			int int4;
			if (square2 != null && square2.testCollideAdjacent((IsoMovingObject)null, -1, 0, 0)) {
				boolean1 = false;
				boolean2 = false;
				byte1 = 0;
				byte2 = 0;
				if (square2.getS() != null && square2.getS().testCollideAdjacent((IsoMovingObject)null, -1, 0, 0)) {
					boolean1 = true;
				}

				if (square2.getN() != null && square2.getN().testCollideAdjacent((IsoMovingObject)null, -1, 0, 0)) {
					boolean2 = true;
				}

				if (boolean1) {
					byte1 = -1;
				}

				if (boolean2) {
					byte2 = 1;
				}

				int3 = byte2 - byte1;
				boolean3 = false;
				byte3 = 0;
				byte4 = 0;
				if (int3 > 0 && Rand.Next(2) == 0) {
					boolean3 = true;
					if (int3 > 1) {
						if (Rand.Next(2) == 0) {
							byte3 = -1;
							byte4 = 0;
						} else {
							byte3 = 0;
							byte4 = 1;
						}
					} else {
						byte3 = byte1;
						byte4 = byte2;
					}
				}

				float3 = (float)Rand.Next(100) / 300.0F;
				square3 = this.getCell().getGridSquare(square2.getX(), square2.getY() + byte3, square2.getZ());
				square4 = this.getCell().getGridSquare(square2.getX(), square2.getY() + byte4, square2.getZ());
				if (square3 == null || square4 == null || !square3.Is(IsoFlagType.cutW) || !square4.Is(IsoFlagType.cutW) || square3.getProperties().Is(IsoFlagType.WallSE) || square4.getProperties().Is(IsoFlagType.WallSE) || square3.Is(IsoFlagType.HoppableW) || square4.Is(IsoFlagType.HoppableW)) {
					boolean3 = false;
				}

				if (boolean3) {
					int4 = 24 + Rand.Next(2) * 2;
					if (Rand.Next(2) == 0) {
						int4 += 8;
					}

					square3.DoSplat("overlay_blood_wall_01_" + (int4 + 1), false, IsoFlagType.cutW, float2, float3, float1);
					square4.DoSplat("overlay_blood_wall_01_" + (int4 + 0), false, IsoFlagType.cutW, float2, float3, float1);
				} else {
					int4 = 0;
					switch (Rand.Next(3)) {
					case 0: 
						int4 = 0 + Rand.Next(4);
						break;
					
					case 1: 
						int4 = 8 + Rand.Next(4);
						break;
					
					case 2: 
						int4 = 16 + Rand.Next(4);
					
					}

					if (int4 == 17 || int4 == 19) {
						float3 = 0.0F;
					}

					if (square2.Is(IsoFlagType.HoppableW)) {
						square2.DoSplat("overlay_blood_fence_01_" + int4, false, IsoFlagType.HoppableW, float2, 0.0F, float1);
					} else {
						square2.DoSplat("overlay_blood_wall_01_" + int4, false, IsoFlagType.cutW, float2, float3, float1);
					}
				}

				square2 = null;
			}

			if (square != null && square.testCollideAdjacent((IsoMovingObject)null, 0, -1, 0)) {
				boolean1 = false;
				boolean2 = false;
				byte1 = 0;
				byte2 = 0;
				if (square.getW() != null && square.getW().testCollideAdjacent((IsoMovingObject)null, 0, -1, 0)) {
					boolean1 = true;
				}

				if (square.getE() != null && square.getE().testCollideAdjacent((IsoMovingObject)null, 0, -1, 0)) {
					boolean2 = true;
				}

				if (boolean1) {
					byte1 = -1;
				}

				if (boolean2) {
					byte2 = 1;
				}

				int3 = byte2 - byte1;
				boolean3 = false;
				byte3 = 0;
				byte4 = 0;
				if (int3 > 0 && Rand.Next(2) == 0) {
					boolean3 = true;
					if (int3 > 1) {
						if (Rand.Next(2) == 0) {
							byte3 = -1;
							byte4 = 0;
						} else {
							byte3 = 0;
							byte4 = 1;
						}
					} else {
						byte3 = byte1;
						byte4 = byte2;
					}
				}

				float3 = (float)Rand.Next(100) / 300.0F;
				square3 = this.getCell().getGridSquare(square.getX() + byte3, square.getY(), square.getZ());
				square4 = this.getCell().getGridSquare(square.getX() + byte4, square.getY(), square.getZ());
				if (square3 == null || square4 == null || !square3.Is(IsoFlagType.cutN) || !square4.Is(IsoFlagType.cutN) || square3.getProperties().Is(IsoFlagType.WallSE) || square4.getProperties().Is(IsoFlagType.WallSE) || square3.Is(IsoFlagType.HoppableN) || square4.Is(IsoFlagType.HoppableN)) {
					boolean3 = false;
				}

				if (boolean3) {
					int4 = 28 + Rand.Next(2) * 2;
					if (Rand.Next(2) == 0) {
						int4 += 8;
					}

					square3.DoSplat("overlay_blood_wall_01_" + (int4 + 0), false, IsoFlagType.cutN, float2, float3, float1);
					square4.DoSplat("overlay_blood_wall_01_" + (int4 + 1), false, IsoFlagType.cutN, float2, float3, float1);
				} else {
					int4 = 0;
					switch (Rand.Next(3)) {
					case 0: 
						int4 = 4 + Rand.Next(4);
						break;
					
					case 1: 
						int4 = 12 + Rand.Next(4);
						break;
					
					case 2: 
						int4 = 20 + Rand.Next(4);
					
					}

					if (int4 == 20 || int4 == 22) {
						float3 = 0.0F;
					}

					if (square.Is(IsoFlagType.HoppableN)) {
						square.DoSplat("overlay_blood_fence_01_" + int4, false, IsoFlagType.HoppableN, float2, float3, float1);
					} else {
						square.DoSplat("overlay_blood_wall_01_" + int4, false, IsoFlagType.cutN, float2, float3, float1);
					}
				}

				square = null;
			}
		}
	}

	public boolean haveBlood() {
		if (Core.OptionBloodDecals == 0) {
			return false;
		} else {
			int int1;
			for (int1 = 0; int1 < this.getObjects().size(); ++int1) {
				IsoObject object = (IsoObject)this.getObjects().get(int1);
				if (object.wallBloodSplats != null && !object.wallBloodSplats.isEmpty()) {
					return true;
				}
			}

			for (int1 = 0; int1 < this.getChunk().FloorBloodSplats.size(); ++int1) {
				IsoFloorBloodSplat floorBloodSplat = (IsoFloorBloodSplat)this.getChunk().FloorBloodSplats.get(int1);
				float float1 = floorBloodSplat.x + (float)(this.getChunk().wx * 10);
				float float2 = floorBloodSplat.y + (float)(this.getChunk().wy * 10);
				if ((int)float1 - 1 <= this.x && (int)float1 + 1 >= this.x && (int)float2 - 1 <= this.y && (int)float2 + 1 >= this.y) {
					return true;
				}
			}

			return false;
		}
	}

	public void removeBlood(boolean boolean1, boolean boolean2) {
		int int1;
		for (int1 = 0; int1 < this.getObjects().size(); ++int1) {
			IsoObject object = (IsoObject)this.getObjects().get(int1);
			if (object.wallBloodSplats != null) {
				object.wallBloodSplats.clear();
			}
		}

		if (!boolean2) {
			for (int1 = 0; int1 < this.getChunk().FloorBloodSplats.size(); ++int1) {
				IsoFloorBloodSplat floorBloodSplat = (IsoFloorBloodSplat)this.getChunk().FloorBloodSplats.get(int1);
				int int2 = (int)((float)(this.getChunk().wx * 10) + floorBloodSplat.x);
				int int3 = (int)((float)(this.getChunk().wy * 10) + floorBloodSplat.y);
				if (int2 >= this.getX() - 1 && int2 <= this.getX() + 1 && int3 >= this.getY() - 1 && int3 <= this.getY() + 1) {
					this.getChunk().FloorBloodSplats.remove(int1);
					--int1;
				}
			}
		}

		if (GameClient.bClient && !boolean1) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.doPacket((short)109, byteBufferWriter);
			byteBufferWriter.putInt(this.x);
			byteBufferWriter.putInt(this.y);
			byteBufferWriter.putInt(this.z);
			byteBufferWriter.putBoolean(boolean2);
			GameClient.connection.endPacketImmediate();
		}
	}

	public void DoSplat(String string, boolean boolean1, IsoFlagType flagType, float float1, float float2, float float3) {
		for (int int1 = 0; int1 < this.getObjects().size(); ++int1) {
			IsoObject object = (IsoObject)this.getObjects().get(int1);
			if (object.sprite != null && object.sprite.getProperties().Is(flagType)) {
				IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, (String)string, 0);
				if (sprite == null) {
					return;
				}

				if (object.wallBloodSplats == null) {
					object.wallBloodSplats = new ArrayList();
				}

				IsoWallBloodSplat wallBloodSplat = new IsoWallBloodSplat((float)GameTime.getInstance().getWorldAgeHours(), sprite);
				object.wallBloodSplats.add(wallBloodSplat);
			}
		}
	}

	public void ClearTileObjects() {
		this.Objects.clear();
		this.RecalcProperties();
	}

	public void ClearTileObjectsExceptFloor() {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (object.sprite == null || !object.sprite.getProperties().Is(IsoFlagType.solidfloor)) {
				this.Objects.remove(object);
				--int1;
			}
		}

		this.RecalcProperties();
	}

	public int RemoveTileObject(IsoObject object) {
		IsoRegions.setPreviousFlags(this);
		int int1 = this.Objects.indexOf(object);
		if (!this.Objects.contains(object)) {
			int1 = this.SpecialObjects.indexOf(object);
		}

		if (object != null && this.Objects.contains(object)) {
			if (object.isTableSurface()) {
				for (int int2 = this.Objects.indexOf(object) + 1; int2 < this.Objects.size(); ++int2) {
					IsoObject object2 = (IsoObject)this.Objects.get(int2);
					if (object2.isTableTopObject() || object2.isTableSurface()) {
						object2.setRenderYOffset(object2.getRenderYOffset() - object.getSurfaceOffset());
						object2.sx = 0.0F;
						object2.sy = 0.0F;
					}
				}
			}

			IsoObject object3 = this.getPlayerBuiltFloor();
			if (object == object3) {
				IsoGridOcclusionData.SquareChanged();
			}

			LuaEventManager.triggerEvent("OnObjectAboutToBeRemoved", object);
			if (!this.Objects.contains(object)) {
				throw new IllegalArgumentException("OnObjectAboutToBeRemoved not allowed to remove the object");
			}

			int1 = this.Objects.indexOf(object);
			object.removeFromWorld();
			object.removeFromSquare();
			assert !this.Objects.contains(object);
			assert !this.SpecialObjects.contains(object);
			if (!(object instanceof IsoWorldInventoryObject)) {
				this.RecalcAllWithNeighbours(true);
				this.getCell().checkHaveRoof(this.getX(), this.getY());
				for (int int3 = 0; int3 < IsoPlayer.numPlayers; ++int3) {
					LosUtil.cachecleared[int3] = true;
				}

				setRecalcLightTime(-1);
				GameTime.instance.lightSourceUpdate = 100.0F;
			}
		}

		MapCollisionData.instance.squareChanged(this);
		LuaEventManager.triggerEvent("OnTileRemoved", object);
		PolygonalMap2.instance.squareChanged(this);
		IsoRegions.squareChanged(this, true);
		return int1;
	}

	public int RemoveTileObjectErosionNoRecalc(IsoObject object) {
		int int1 = this.Objects.indexOf(object);
		IsoGridSquare square = object.square;
		object.removeFromWorld();
		object.removeFromSquare();
		square.RecalcPropertiesIfNeeded();
		assert !this.Objects.contains(object);
		assert !this.SpecialObjects.contains(object);
		return int1;
	}

	public void AddSpecialObject(IsoObject object) {
		this.AddSpecialObject(object, -1);
	}

	public void AddSpecialObject(IsoObject object, int int1) {
		if (object != null) {
			IsoRegions.setPreviousFlags(this);
			int1 = this.placeWallAndDoorCheck(object, int1);
			if (int1 != -1 && int1 >= 0 && int1 <= this.Objects.size()) {
				this.Objects.add(int1, object);
			} else {
				this.Objects.add(object);
			}

			this.SpecialObjects.add(object);
			this.burntOut = false;
			object.addToWorld();
			if (!GameServer.bServer && !GameClient.bClient) {
				this.restackSheetRope();
			}

			this.RecalcAllWithNeighbours(true);
			if (!(object instanceof IsoWorldInventoryObject)) {
				for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
					LosUtil.cachecleared[int2] = true;
				}

				setRecalcLightTime(-1);
				GameTime.instance.lightSourceUpdate = 100.0F;
				if (object == this.getPlayerBuiltFloor()) {
					IsoGridOcclusionData.SquareChanged();
				}
			}

			MapCollisionData.instance.squareChanged(this);
			PolygonalMap2.instance.squareChanged(this);
			IsoRegions.squareChanged(this);
		}
	}

	public void AddTileObject(IsoObject object) {
		this.AddTileObject(object, -1);
	}

	public void AddTileObject(IsoObject object, int int1) {
		if (object != null) {
			IsoRegions.setPreviousFlags(this);
			int1 = this.placeWallAndDoorCheck(object, int1);
			if (int1 != -1 && int1 >= 0 && int1 <= this.Objects.size()) {
				this.Objects.add(int1, object);
			} else {
				this.Objects.add(object);
			}

			this.burntOut = false;
			object.addToWorld();
			this.RecalcAllWithNeighbours(true);
			if (!(object instanceof IsoWorldInventoryObject)) {
				for (int int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
					LosUtil.cachecleared[int2] = true;
				}

				setRecalcLightTime(-1);
				GameTime.instance.lightSourceUpdate = 100.0F;
				if (object == this.getPlayerBuiltFloor()) {
					IsoGridOcclusionData.SquareChanged();
				}
			}

			MapCollisionData.instance.squareChanged(this);
			PolygonalMap2.instance.squareChanged(this);
			IsoRegions.squareChanged(this);
		}
	}

	public int placeWallAndDoorCheck(IsoObject object, int int1) {
		int int2 = -1;
		if (object.sprite != null) {
			IsoObjectType objectType = object.sprite.getType();
			boolean boolean1 = objectType == IsoObjectType.doorN || objectType == IsoObjectType.doorW;
			boolean boolean2 = !boolean1 && (object.sprite.cutW || object.sprite.cutN || objectType == IsoObjectType.doorFrN || objectType == IsoObjectType.doorFrW || object.sprite.treatAsWallOrder);
			if (boolean2 || boolean1) {
				int int3 = 0;
				while (true) {
					if (int3 >= this.Objects.size()) {
						if (boolean1 && int2 > int1) {
							int1 = int2 + 1;
							return int1;
						}

						if (boolean2 && int2 >= 0 && (int2 < int1 || int1 < 0)) {
							return int2;
						}

						break;
					}

					IsoObject object2 = (IsoObject)this.Objects.get(int3);
					objectType = IsoObjectType.MAX;
					if (object2.sprite != null) {
						objectType = object2.sprite.getType();
						if (boolean2 && (objectType == IsoObjectType.doorN || objectType == IsoObjectType.doorW)) {
							int2 = int3;
						}

						if (boolean1 && (objectType == IsoObjectType.doorFrN || objectType == IsoObjectType.doorFrW || object2.sprite.cutW || object2.sprite.cutN || object2.sprite.treatAsWallOrder)) {
							int2 = int3;
						}
					}

					++int3;
				}
			}
		}

		return int1;
	}

	public void transmitAddObjectToSquare(IsoObject object, int int1) {
		if (object != null && !this.Objects.contains(object)) {
			this.AddTileObject(object, int1);
			if (GameClient.bClient) {
				object.transmitCompleteItemToServer();
			}

			if (GameServer.bServer) {
				object.transmitCompleteItemToClients();
			}
		}
	}

	public int transmitRemoveItemFromSquare(IsoObject object) {
		if (object != null && this.Objects.contains(object)) {
			if (GameClient.bClient) {
				try {
					GameClient.instance.checkAddedRemovedItems(object);
				} catch (Exception exception) {
					GameClient.connection.cancelPacket();
					ExceptionLogger.logException(exception);
				}

				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)23, byteBufferWriter);
				byteBufferWriter.putInt(this.getX());
				byteBufferWriter.putInt(this.getY());
				byteBufferWriter.putInt(this.getZ());
				byteBufferWriter.putInt(this.Objects.indexOf(object));
				GameClient.connection.endPacket();
			}

			return GameServer.bServer ? GameServer.RemoveItemFromMap(object) : this.RemoveTileObject(object);
		} else {
			return -1;
		}
	}

	public void transmitRemoveItemFromSquareOnServer(IsoObject object) {
		if (object != null && this.Objects.contains(object)) {
			if (GameServer.bServer) {
				GameServer.RemoveItemFromMap(object);
			}
		}
	}

	public void transmitModdata() {
		if (GameClient.bClient) {
			ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
			PacketTypes.doPacket((short)48, byteBufferWriter);
			byteBufferWriter.putInt(this.getX());
			byteBufferWriter.putInt(this.getY());
			byteBufferWriter.putInt(this.getZ());
			try {
				this.getModData().save(byteBufferWriter.bb);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			GameClient.connection.endPacketImmediate();
		} else if (GameServer.bServer) {
			GameServer.loadModData(this);
		}
	}

	public InventoryItem AddWorldInventoryItem(String string, float float1, float float2, float float3) {
		InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
		if (inventoryItem == null) {
			return null;
		} else {
			IsoWorldInventoryObject worldInventoryObject = new IsoWorldInventoryObject(inventoryItem, this, float1, float2, float3);
			inventoryItem.setWorldItem(worldInventoryObject);
			worldInventoryObject.setKeyId(inventoryItem.getKeyId());
			worldInventoryObject.setName(inventoryItem.getName());
			this.Objects.add(worldInventoryObject);
			this.WorldObjects.add(worldInventoryObject);
			worldInventoryObject.square.chunk.recalcHashCodeObjects();
			if (GameClient.bClient) {
				worldInventoryObject.transmitCompleteItemToServer();
			}

			if (GameServer.bServer) {
				worldInventoryObject.transmitCompleteItemToClients();
			}

			return inventoryItem;
		}
	}

	public InventoryItem AddWorldInventoryItem(InventoryItem inventoryItem, float float1, float float2, float float3) {
		return this.AddWorldInventoryItem(inventoryItem, float1, float2, float3, true);
	}

	public InventoryItem AddWorldInventoryItem(InventoryItem inventoryItem, float float1, float float2, float float3, boolean boolean1) {
		if (!inventoryItem.getFullType().contains(".Corpse")) {
			if (inventoryItem.getFullType().contains(".Generator")) {
				new IsoGenerator(inventoryItem, IsoWorld.instance.CurrentCell, this);
				return inventoryItem;
			} else {
				IsoWorldInventoryObject worldInventoryObject = new IsoWorldInventoryObject(inventoryItem, this, float1, float2, float3);
				worldInventoryObject.setName(inventoryItem.getName());
				worldInventoryObject.setKeyId(inventoryItem.getKeyId());
				this.Objects.add(worldInventoryObject);
				this.WorldObjects.add(worldInventoryObject);
				worldInventoryObject.square.chunk.recalcHashCodeObjects();
				inventoryItem.setWorldItem(worldInventoryObject);
				worldInventoryObject.addToWorld();
				if (boolean1) {
					if (GameClient.bClient) {
						worldInventoryObject.transmitCompleteItemToServer();
					}

					if (GameServer.bServer) {
						worldInventoryObject.transmitCompleteItemToClients();
					}
				}

				return inventoryItem;
			}
		} else if (inventoryItem.byteData == null) {
			IsoZombie zombie = new IsoZombie(IsoWorld.instance.CurrentCell);
			zombie.setDir(IsoDirections.fromIndex(Rand.Next(8)));
			zombie.getForwardDirection().set(zombie.dir.ToVector());
			zombie.setFakeDead(false);
			zombie.setHealth(0.0F);
			zombie.upKillCount = false;
			zombie.setX((float)this.x + float1);
			zombie.setY((float)this.y + float2);
			zombie.setZ((float)this.z);
			zombie.square = this;
			zombie.current = this;
			zombie.dressInRandomOutfit();
			zombie.DoZombieInventory();
			IsoDeadBody deadBody = new IsoDeadBody(zombie, true);
			this.addCorpse(deadBody, false);
			if (GameServer.bServer) {
				GameServer.sendCorpse(deadBody);
			}

			return inventoryItem;
		} else {
			IsoDeadBody deadBody2 = new IsoDeadBody(IsoWorld.instance.CurrentCell);
			try {
				byte byte1 = inventoryItem.byteData.get();
				byte byte2 = inventoryItem.byteData.get();
				byte byte3 = inventoryItem.byteData.get();
				byte byte4 = inventoryItem.byteData.get();
				int int1 = 56;
				if (byte1 == 87 && byte2 == 86 && byte3 == 69 && byte4 == 82) {
					int1 = inventoryItem.byteData.getInt();
				} else {
					inventoryItem.byteData.rewind();
				}

				deadBody2.load(inventoryItem.byteData, int1);
			} catch (IOException ioException) {
				ioException.printStackTrace();
				IsoZombie zombie2 = new IsoZombie((IsoCell)null);
				zombie2.dir = deadBody2.dir;
				zombie2.current = this;
				zombie2.x = deadBody2.x;
				zombie2.y = deadBody2.y;
				zombie2.z = deadBody2.z;
				deadBody2 = new IsoDeadBody(zombie2);
			}

			deadBody2.setX((float)this.x + float1);
			deadBody2.setY((float)this.y + float2);
			deadBody2.setZ((float)this.z);
			deadBody2.square = this;
			this.addCorpse(deadBody2, false);
			if (GameServer.bServer) {
				GameServer.sendCorpse(deadBody2);
			}

			return inventoryItem;
		}
	}

	public void restackSheetRope() {
		if (this.Is(IsoFlagType.climbSheetW) || this.Is(IsoFlagType.climbSheetN) || this.Is(IsoFlagType.climbSheetE) || this.Is(IsoFlagType.climbSheetS)) {
			for (int int1 = 0; int1 < this.getObjects().size() - 1; ++int1) {
				IsoObject object = (IsoObject)this.getObjects().get(int1);
				if (object.getProperties() != null && (object.getProperties().Is(IsoFlagType.climbSheetW) || object.getProperties().Is(IsoFlagType.climbSheetN) || object.getProperties().Is(IsoFlagType.climbSheetE) || object.getProperties().Is(IsoFlagType.climbSheetS))) {
					if (GameServer.bServer) {
						this.transmitRemoveItemFromSquare(object);
						this.Objects.add(object);
						object.transmitCompleteItemToClients();
					} else if (!GameClient.bClient) {
						this.Objects.remove(object);
						this.Objects.add(object);
					}

					break;
				}
			}
		}
	}

	public void Burn() {
		if (!GameServer.bServer && !GameClient.bClient || !ServerOptions.instance.NoFire.getValue()) {
			if (this.getCell() != null) {
				this.BurnWalls(true);
				LuaEventManager.triggerEvent("OnGridBurnt", this);
			}
		}
	}

	public void Burn(boolean boolean1) {
		if (!GameServer.bServer && !GameClient.bClient || !ServerOptions.instance.NoFire.getValue()) {
			if (this.getCell() != null) {
				this.BurnWalls(boolean1);
			}
		}
	}

	public void BurnWalls(boolean boolean1) {
		if (!GameClient.bClient) {
			if (GameServer.bServer && SafeHouse.isSafeHouse(this, (String)null, false) != null) {
				if (ServerOptions.instance.NoFire.getValue()) {
					return;
				}

				if (!ServerOptions.instance.SafehouseAllowFire.getValue()) {
					return;
				}
			}

			for (int int1 = 0; int1 < this.SpecialObjects.size(); ++int1) {
				IsoObject object = (IsoObject)this.SpecialObjects.get(int1);
				if (object instanceof IsoThumpable && ((IsoThumpable)object).haveSheetRope()) {
					((IsoThumpable)object).removeSheetRope((IsoPlayer)null);
				}

				if (object instanceof IsoWindow) {
					if (((IsoWindow)object).haveSheetRope()) {
						((IsoWindow)object).removeSheetRope((IsoPlayer)null);
					}

					((IsoWindow)object).removeSheet((IsoGameCharacter)null);
				}

				if (IsoWindowFrame.isWindowFrame(object) && IsoWindowFrame.haveSheetRope(object)) {
					IsoWindowFrame.removeSheetRope(object, (IsoPlayer)null);
				}

				if (object instanceof BarricadeAble) {
					IsoBarricade barricade = ((BarricadeAble)object).getBarricadeOnSameSquare();
					IsoBarricade barricade2 = ((BarricadeAble)object).getBarricadeOnOppositeSquare();
					if (barricade != null) {
						if (GameServer.bServer) {
							GameServer.RemoveItemFromMap(barricade);
						} else {
							this.RemoveTileObject(barricade);
						}
					}

					if (barricade2 != null) {
						if (GameServer.bServer) {
							GameServer.RemoveItemFromMap(barricade2);
						} else {
							barricade2.getSquare().RemoveTileObject(barricade2);
						}
					}
				}
			}

			this.SpecialObjects.clear();
			boolean boolean2 = false;
			if (!this.getProperties().Is(IsoFlagType.burntOut)) {
				int int2 = 0;
				for (int int3 = 0; int3 < this.Objects.size(); ++int3) {
					IsoObject object2 = (IsoObject)this.Objects.get(int3);
					boolean boolean3 = false;
					if (object2.getSprite() != null && object2.getSprite().getName() != null && !object2.getSprite().getProperties().Is(IsoFlagType.water) && !object2.getSprite().getName().contains("_burnt_")) {
						IsoObject object3;
						if (object2 instanceof IsoThumpable && object2.getSprite().burntTile != null) {
							object3 = IsoObject.getNew();
							object3.setSprite(IsoSpriteManager.instance.getSprite(object2.getSprite().burntTile));
							object3.setSquare(this);
							if (GameServer.bServer) {
								object2.sendObjectChange("replaceWith", "object", object3);
							}

							object2.removeFromWorld();
							this.Objects.set(int3, object3);
						} else if (object2.getSprite().burntTile != null) {
							object2.sprite = IsoSpriteManager.instance.getSprite(object2.getSprite().burntTile);
							object2.RemoveAttachedAnims();
							if (object2.Children != null) {
								object2.Children.clear();
							}

							object2.transmitUpdatedSpriteToClients();
							object2.setOverlaySprite((String)null);
						} else {
							IsoSpriteManager spriteManager;
							if (object2.getType() == IsoObjectType.tree) {
								spriteManager = IsoSpriteManager.instance;
								int int4 = Rand.Next(15, 19);
								object2.sprite = spriteManager.getSprite("fencing_burnt_01_" + (int4 + 1));
								object2.RemoveAttachedAnims();
								if (object2.Children != null) {
									object2.Children.clear();
								}

								object2.transmitUpdatedSpriteToClients();
								object2.setOverlaySprite((String)null);
							} else if (!(object2 instanceof IsoTrap)) {
								if (!(object2 instanceof IsoBarricade) && !(object2 instanceof IsoMannequin)) {
									if (object2 instanceof IsoGenerator) {
										IsoGenerator generator = (IsoGenerator)object2;
										if (generator.getFuel() > 0.0F) {
											int2 += 20;
										}

										if (generator.isActivated()) {
											generator.activated = false;
											generator.setSurroundingElectricity();
											if (GameServer.bServer) {
												generator.syncIsoObject(false, (byte)0, (UdpConnection)null, (ByteBuffer)null);
											}
										}

										if (GameServer.bServer) {
											GameServer.RemoveItemFromMap(object2);
										} else {
											this.RemoveTileObject(object2);
										}

										--int3;
									} else {
										if (object2.getType() == IsoObjectType.wall && !object2.getProperties().Is(IsoFlagType.DoorWallW) && !object2.getProperties().Is(IsoFlagType.DoorWallN) && !object2.getProperties().Is("WindowN") && !object2.getProperties().Is(IsoFlagType.WindowW) && !object2.getSprite().getName().startsWith("walls_exterior_roofs_") && !object2.getSprite().getName().startsWith("fencing_") && !object2.getSprite().getName().startsWith("fixtures_railings_")) {
											if (object2.getSprite().getProperties().Is(IsoFlagType.collideW) && !object2.getSprite().getProperties().Is(IsoFlagType.collideN)) {
												object2.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "0" : "4"));
											} else if (object2.getSprite().getProperties().Is(IsoFlagType.collideN) && !object2.getSprite().getProperties().Is(IsoFlagType.collideW)) {
												object2.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "1" : "5"));
											} else if (object2.getSprite().getProperties().Is(IsoFlagType.collideW) && object2.getSprite().getProperties().Is(IsoFlagType.collideN)) {
												object2.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "2" : "6"));
											} else if (object2.getProperties().Is(IsoFlagType.WallSE)) {
												object2.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "3" : "7"));
											}
										} else {
											if (object2 instanceof IsoDoor || object2 instanceof IsoWindow || object2 instanceof IsoCurtain) {
												if (GameServer.bServer) {
													GameServer.RemoveItemFromMap(object2);
												} else {
													this.RemoveTileObject(object2);
													boolean2 = true;
												}

												--int3;
												continue;
											}

											if (object2.getProperties().Is(IsoFlagType.WindowW)) {
												object2.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "8" : "12"));
											} else if (object2.getProperties().Is("WindowN")) {
												object2.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "9" : "13"));
											} else if (object2.getProperties().Is(IsoFlagType.DoorWallW)) {
												object2.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "10" : "14"));
											} else if (object2.getProperties().Is(IsoFlagType.DoorWallN)) {
												object2.sprite = IsoSpriteManager.instance.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "11" : "15"));
											} else if (object2.getSprite().getProperties().Is(IsoFlagType.solidfloor) && !object2.getSprite().getProperties().Is(IsoFlagType.exterior)) {
												object2.sprite = IsoSpriteManager.instance.getSprite("floors_burnt_01_0");
											} else {
												if (object2 instanceof IsoWaveSignal) {
													if (GameServer.bServer) {
														GameServer.RemoveItemFromMap(object2);
													} else {
														this.RemoveTileObject(object2);
														boolean2 = true;
													}

													--int3;
													continue;
												}

												if (object2.getContainer() != null && object2.getContainer().getItems() != null) {
													InventoryItem inventoryItem = null;
													int int5;
													for (int5 = 0; int5 < object2.getContainer().getItems().size(); ++int5) {
														inventoryItem = (InventoryItem)object2.getContainer().getItems().get(int5);
														if (inventoryItem instanceof Food && ((Food)inventoryItem).isAlcoholic() || inventoryItem.getType().equals("PetrolCan") || inventoryItem.getType().equals("Bleach")) {
															int2 += 20;
															if (int2 > 100) {
																int2 = 100;
																break;
															}
														}
													}

													object2.sprite = IsoSpriteManager.instance.getSprite("floors_burnt_01_" + Rand.Next(1, 2));
													for (int5 = 0; int5 < object2.getContainerCount(); ++int5) {
														ItemContainer itemContainer = object2.getContainerByIndex(int5);
														itemContainer.removeItemsFromProcessItems();
														itemContainer.removeAllItems();
													}

													object2.removeAllContainers();
													if (object2.getOverlaySprite() != null) {
														object2.setOverlaySprite((String)null);
													}

													boolean3 = true;
												} else if (!object2.getSprite().getProperties().Is(IsoFlagType.solidtrans) && !object2.getSprite().getProperties().Is(IsoFlagType.bed) && !object2.getSprite().getProperties().Is(IsoFlagType.waterPiped)) {
													String string;
													if (object2.getSprite().getName().startsWith("walls_exterior_roofs_")) {
														spriteManager = IsoSpriteManager.instance;
														string = object2.getSprite().getName();
														object2.sprite = spriteManager.getSprite("walls_burnt_roofs_01_" + string.substring(object2.getSprite().getName().lastIndexOf("_") + 1, object2.getSprite().getName().length()));
													} else if (!object2.getSprite().getName().startsWith("roofs_accents")) {
														if (object2.getSprite().getName().startsWith("roofs_")) {
															spriteManager = IsoSpriteManager.instance;
															string = object2.getSprite().getName();
															object2.sprite = spriteManager.getSprite("roofs_burnt_01_" + string.substring(object2.getSprite().getName().lastIndexOf("_") + 1, object2.getSprite().getName().length()));
														} else if ((object2.getSprite().getName().startsWith("fencing_") || object2.getSprite().getName().startsWith("fixtures_railings_")) && (object2.getSprite().getProperties().Is(IsoFlagType.HoppableN) || object2.getSprite().getProperties().Is(IsoFlagType.HoppableW))) {
															if (object2.getSprite().getProperties().Is(IsoFlagType.transparentW) && !object2.getSprite().getProperties().Is(IsoFlagType.transparentN)) {
																object2.sprite = IsoSpriteManager.instance.getSprite("fencing_burnt_01_0");
															} else if (object2.getSprite().getProperties().Is(IsoFlagType.transparentN) && !object2.getSprite().getProperties().Is(IsoFlagType.transparentW)) {
																object2.sprite = IsoSpriteManager.instance.getSprite("fencing_burnt_01_1");
															} else {
																object2.sprite = IsoSpriteManager.instance.getSprite("fencing_burnt_01_2");
															}
														}
													}
												} else {
													object2.sprite = IsoSpriteManager.instance.getSprite("floors_burnt_01_" + Rand.Next(1, 2));
													if (object2.getOverlaySprite() != null) {
														object2.setOverlaySprite((String)null);
													}
												}
											}
										}

										if (!boolean3 && !(object2 instanceof IsoThumpable)) {
											object2.RemoveAttachedAnims();
											object2.transmitUpdatedSpriteToClients();
											object2.setOverlaySprite((String)null);
										} else {
											object3 = IsoObject.getNew();
											object3.setSprite(object2.getSprite());
											object3.setSquare(this);
											if (GameServer.bServer) {
												object2.sendObjectChange("replaceWith", "object", object3);
											}

											this.Objects.set(int3, object3);
										}

										if (object2.emitter != null) {
											object2.emitter.stopAll();
											object2.emitter = null;
										}
									}
								} else {
									if (GameServer.bServer) {
										GameServer.RemoveItemFromMap(object2);
									} else {
										this.Objects.remove(object2);
									}

									--int3;
								}
							}
						}
					}
				}

				if (int2 > 0 && boolean1) {
					if (GameServer.bServer) {
						GameServer.PlayWorldSoundServer("BurnedObjectExploded", false, this, 0.0F, 50.0F, 1.0F, false);
					} else {
						SoundManager.instance.PlayWorldSound("BurnedObjectExploded", this, 0.0F, 50.0F, 1.0F, false);
					}

					IsoFireManager.explode(this.getCell(), this, int2);
				}
			}

			if (!boolean2) {
				this.RecalcProperties();
			}

			this.getProperties().Set(IsoFlagType.burntOut);
			this.burntOut = true;
			MapCollisionData.instance.squareChanged(this);
			PolygonalMap2.instance.squareChanged(this);
		}
	}

	public void BurnWallsTCOnly() {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (object.sprite == null) {
			}
		}
	}

	public void BurnTick() {
		if (!GameClient.bClient) {
			for (int int1 = 0; int1 < this.StaticMovingObjects.size(); ++int1) {
				IsoMovingObject movingObject = (IsoMovingObject)this.StaticMovingObjects.get(int1);
				if (movingObject instanceof IsoDeadBody) {
					((IsoDeadBody)movingObject).Burn();
					if (!this.StaticMovingObjects.contains(movingObject)) {
						--int1;
					}
				}
			}
		}
	}

	public boolean CalculateCollide(IsoGridSquare square, boolean boolean1, boolean boolean2, boolean boolean3) {
		return this.CalculateCollide(square, boolean1, boolean2, boolean3, false);
	}

	public boolean CalculateCollide(IsoGridSquare square, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4) {
		return this.CalculateCollide(square, boolean1, boolean2, boolean3, boolean4, cellGetSquare);
	}

	public boolean CalculateCollide(IsoGridSquare square, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, IsoGridSquare.GetSquare getSquare) {
		if (square == null && boolean2) {
			return true;
		} else if (square == null) {
			return false;
		} else {
			if (boolean1 && square.Properties.Is(IsoFlagType.trans)) {
			}

			boolean boolean5 = false;
			boolean boolean6 = false;
			boolean boolean7 = false;
			boolean boolean8 = false;
			if (square.x < this.x) {
				boolean5 = true;
			}

			if (square.y < this.y) {
				boolean7 = true;
			}

			if (square.x > this.x) {
				boolean6 = true;
			}

			if (square.y > this.y) {
				boolean8 = true;
			}

			if (!boolean4 && square.Properties.Is(IsoFlagType.solid)) {
				if (this.Has(IsoObjectType.stairsTW) && !boolean2 && square.x < this.x && square.y == this.y && square.z == this.z) {
					return false;
				} else {
					return !this.Has(IsoObjectType.stairsTN) || boolean2 || square.x != this.x || square.y >= this.y || square.z != this.z;
				}
			} else {
				if (!boolean3 && square.Properties.Is(IsoFlagType.solidtrans)) {
					if (this.Has(IsoObjectType.stairsTW) && !boolean2 && square.x < this.x && square.y == this.y && square.z == this.z) {
						return false;
					}

					if (this.Has(IsoObjectType.stairsTN) && !boolean2 && square.x == this.x && square.y < this.y && square.z == this.z) {
						return false;
					}

					boolean boolean9 = false;
					if (square.Properties.Is(IsoFlagType.windowW) || square.Properties.Is(IsoFlagType.windowN)) {
						boolean9 = true;
					}

					if (!boolean9 && (square.Properties.Is(IsoFlagType.WindowW) || square.Properties.Is(IsoFlagType.WindowN))) {
						boolean9 = true;
					}

					IsoGridSquare square2;
					if (!boolean9) {
						square2 = getSquare.getGridSquare(square.x, square.y + 1, this.z);
						if (square2 != null && (square2.Is(IsoFlagType.windowN) || square2.Is(IsoFlagType.WindowN))) {
							boolean9 = true;
						}
					}

					if (!boolean9) {
						square2 = getSquare.getGridSquare(square.x + 1, square.y, this.z);
						if (square2 != null && (square2.Is(IsoFlagType.windowW) || square2.Is(IsoFlagType.WindowW))) {
							boolean9 = true;
						}
					}

					if (!boolean9) {
						return true;
					}
				}

				if (square.x != this.x && square.y != this.y && this.z != square.z && boolean2) {
					return true;
				} else {
					if (boolean2 && square.z < this.z) {
						label695: {
							if (this.SolidFloorCached) {
								if (this.SolidFloor) {
									break label695;
								}
							} else if (this.TreatAsSolidFloor()) {
								break label695;
							}

							if (!square.Has(IsoObjectType.stairsTN) && !square.Has(IsoObjectType.stairsTW)) {
								return false;
							}

							return true;
						}
					}

					if (boolean2 && square.z == this.z) {
						if (square.x > this.x && square.y == this.y && square.Properties.Is(IsoFlagType.windowW)) {
							return false;
						}

						if (square.y > this.y && square.x == this.x && square.Properties.Is(IsoFlagType.windowN)) {
							return false;
						}

						if (square.x < this.x && square.y == this.y && this.Properties.Is(IsoFlagType.windowW)) {
							return false;
						}

						if (square.y < this.y && square.x == this.x && this.Properties.Is(IsoFlagType.windowN)) {
							return false;
						}
					}

					if (square.x > this.x && square.z < this.z && square.Has(IsoObjectType.stairsTW)) {
						return false;
					} else if (square.y > this.y && square.z < this.z && square.Has(IsoObjectType.stairsTN)) {
						return false;
					} else {
						IsoGridSquare square3 = getSquare.getGridSquare(square.x, square.y, square.z - 1);
						if (square.x != this.x && square.z == this.z && square.Has(IsoObjectType.stairsTN) && (square3 == null || !square3.Has(IsoObjectType.stairsTN) || boolean2)) {
							return true;
						} else if (square.y > this.y && square.x == this.x && square.z == this.z && square.Has(IsoObjectType.stairsTN) && (square3 == null || !square3.Has(IsoObjectType.stairsTN) || boolean2)) {
							return true;
						} else if (square.x > this.x && square.y == this.y && square.z == this.z && square.Has(IsoObjectType.stairsTW) && (square3 == null || !square3.Has(IsoObjectType.stairsTW) || boolean2)) {
							return true;
						} else if (square.y == this.y || square.z != this.z || !square.Has(IsoObjectType.stairsTW) || square3 != null && square3.Has(IsoObjectType.stairsTW) && !boolean2) {
							if (square.x != this.x && square.z == this.z && square.Has(IsoObjectType.stairsMN)) {
								return true;
							} else if (square.y != this.y && square.z == this.z && square.Has(IsoObjectType.stairsMW)) {
								return true;
							} else if (square.x != this.x && square.z == this.z && square.Has(IsoObjectType.stairsBN)) {
								return true;
							} else if (square.y != this.y && square.z == this.z && square.Has(IsoObjectType.stairsBW)) {
								return true;
							} else if (square.x != this.x && square.z == this.z && this.Has(IsoObjectType.stairsTN)) {
								return true;
							} else if (square.y != this.y && square.z == this.z && this.Has(IsoObjectType.stairsTW)) {
								return true;
							} else if (square.x != this.x && square.z == this.z && this.Has(IsoObjectType.stairsMN)) {
								return true;
							} else if (square.y != this.y && square.z == this.z && this.Has(IsoObjectType.stairsMW)) {
								return true;
							} else if (square.x != this.x && square.z == this.z && this.Has(IsoObjectType.stairsBN)) {
								return true;
							} else if (square.y != this.y && square.z == this.z && this.Has(IsoObjectType.stairsBW)) {
								return true;
							} else if (square.y < this.y && square.x == this.x && square.z > this.z && this.Has(IsoObjectType.stairsTN)) {
								return false;
							} else if (square.x < this.x && square.y == this.y && square.z > this.z && this.Has(IsoObjectType.stairsTW)) {
								return false;
							} else if (square.y > this.y && square.x == this.x && square.z < this.z && square.Has(IsoObjectType.stairsTN)) {
								return false;
							} else if (square.x > this.x && square.y == this.y && square.z < this.z && square.Has(IsoObjectType.stairsTW)) {
								return false;
							} else {
								if (square.z == this.z) {
									label529: {
										if (square.SolidFloorCached) {
											if (square.SolidFloor) {
												break label529;
											}
										} else if (square.TreatAsSolidFloor()) {
											break label529;
										}

										if (boolean2) {
											return true;
										}
									}
								}

								if (square.z == this.z) {
									label522: {
										if (square.SolidFloorCached) {
											if (square.SolidFloor) {
												break label522;
											}
										} else if (square.TreatAsSolidFloor()) {
											break label522;
										}

										if (square.z > 0) {
											square3 = getSquare.getGridSquare(square.x, square.y, square.z - 1);
											if (square3 == null) {
												return true;
											}
										}
									}
								}

								if (this.z != square.z) {
									if (square.z < this.z && square.x == this.x && square.y == this.y) {
										if (this.SolidFloorCached) {
											if (!this.SolidFloor) {
												return false;
											}
										} else if (!this.TreatAsSolidFloor()) {
											return false;
										}
									}

									return true;
								} else {
									boolean boolean10 = boolean7 && this.Properties.Is(IsoFlagType.collideN);
									boolean boolean11 = boolean5 && this.Properties.Is(IsoFlagType.collideW);
									boolean boolean12 = boolean8 && square.Properties.Is(IsoFlagType.collideN);
									boolean boolean13 = boolean6 && square.Properties.Is(IsoFlagType.collideW);
									if (boolean10 && boolean2 && this.Properties.Is(IsoFlagType.canPathN)) {
										boolean10 = false;
									}

									if (boolean11 && boolean2 && this.Properties.Is(IsoFlagType.canPathW)) {
										boolean11 = false;
									}

									if (boolean12 && boolean2 && square.Properties.Is(IsoFlagType.canPathN)) {
										boolean12 = false;
									}

									if (boolean13 && boolean2 && square.Properties.Is(IsoFlagType.canPathW)) {
										boolean13 = false;
									}

									if (boolean11 && this.Has(IsoObjectType.stairsTW) && !boolean2) {
										boolean11 = false;
									}

									if (boolean10 && this.Has(IsoObjectType.stairsTN) && !boolean2) {
										boolean10 = false;
									}

									if (!boolean10 && !boolean11 && !boolean12 && !boolean13) {
										boolean boolean14 = square.x != this.x && square.y != this.y;
										if (boolean14) {
											IsoGridSquare square4 = getSquare.getGridSquare(this.x, square.y, this.z);
											IsoGridSquare square5 = getSquare.getGridSquare(square.x, this.y, this.z);
											if (square4 != null && square4 != this && square4 != square) {
												square4.RecalcPropertiesIfNeeded();
											}

											if (square5 != null && square5 != this && square5 != square) {
												square5.RecalcPropertiesIfNeeded();
											}

											if (square == this || square4 == square5 || square4 == this || square5 == this || square4 == square || square5 == square) {
												return true;
											}

											if (square.x == this.x + 1 && square.y == this.y + 1 && square4 != null && square5 != null && square4.Is(IsoFlagType.windowN) && square5.Is(IsoFlagType.windowW)) {
												return true;
											}

											if (square.x == this.x - 1 && square.y == this.y - 1 && square4 != null && square5 != null && square4.Is(IsoFlagType.windowW) && square5.Is(IsoFlagType.windowN)) {
												return true;
											}

											if (this.CalculateCollide(square4, boolean1, boolean2, boolean3, false, getSquare)) {
												return true;
											}

											if (this.CalculateCollide(square5, boolean1, boolean2, boolean3, false, getSquare)) {
												return true;
											}

											if (square.CalculateCollide(square4, boolean1, boolean2, boolean3, false, getSquare)) {
												return true;
											}

											if (square.CalculateCollide(square5, boolean1, boolean2, boolean3, false, getSquare)) {
												return true;
											}
										}

										return false;
									} else {
										return true;
									}
								}
							}
						} else {
							return true;
						}
					}
				}
			}
		}
	}

	public boolean CalculateVisionBlocked(IsoGridSquare square) {
		return this.CalculateVisionBlocked(square, cellGetSquare);
	}

	public boolean CalculateVisionBlocked(IsoGridSquare square, IsoGridSquare.GetSquare getSquare) {
		if (square == null) {
			return false;
		} else if (Math.abs(square.getX() - this.getX()) <= 1 && Math.abs(square.getY() - this.getY()) <= 1) {
			boolean boolean1 = false;
			boolean boolean2 = false;
			boolean boolean3 = false;
			boolean boolean4 = false;
			if (square.x < this.x) {
				boolean1 = true;
			}

			if (square.y < this.y) {
				boolean3 = true;
			}

			if (square.x > this.x) {
				boolean2 = true;
			}

			if (square.y > this.y) {
				boolean4 = true;
			}

			if (!square.Properties.Is(IsoFlagType.trans) && !this.Properties.Is(IsoFlagType.trans)) {
				if (this.z != square.z) {
					IsoGridSquare square2;
					if (square.z > this.z) {
						label255: {
							label234: {
								if (square.SolidFloorCached) {
									if (!square.SolidFloor) {
										break label234;
									}
								} else if (!square.TreatAsSolidFloor()) {
									break label234;
								}

								if (!square.getProperties().Is(IsoFlagType.transparentFloor)) {
									return true;
								}
							}

							if (this.Properties.Is(IsoFlagType.noStart)) {
								return true;
							}

							square2 = getSquare.getGridSquare(this.x, this.y, square.z);
							if (square2 == null) {
								return false;
							}

							if (square2.SolidFloorCached) {
								if (!square2.SolidFloor) {
									break label255;
								}
							} else if (!square2.TreatAsSolidFloor()) {
								break label255;
							}

							if (!square2.getProperties().Is(IsoFlagType.transparentFloor)) {
								return true;
							}
						}
					} else {
						label256: {
							label220: {
								if (this.SolidFloorCached) {
									if (!this.SolidFloor) {
										break label220;
									}
								} else if (!this.TreatAsSolidFloor()) {
									break label220;
								}

								if (!this.getProperties().Is(IsoFlagType.transparentFloor)) {
									return true;
								}
							}

							if (this.Properties.Is(IsoFlagType.noStart)) {
								return true;
							}

							square2 = getSquare.getGridSquare(square.x, square.y, this.z);
							if (square2 == null) {
								return false;
							}

							if (square2.SolidFloorCached) {
								if (!square2.SolidFloor) {
									break label256;
								}
							} else if (!square2.TreatAsSolidFloor()) {
								break label256;
							}

							if (!square2.getProperties().Is(IsoFlagType.transparentFloor)) {
								return true;
							}
						}
					}
				}

				if (square.x > this.x && square.Properties.Is(IsoFlagType.transparentW)) {
					return false;
				} else if (square.y > this.y && square.Properties.Is(IsoFlagType.transparentN)) {
					return false;
				} else if (square.x < this.x && this.Properties.Is(IsoFlagType.transparentW)) {
					return false;
				} else if (square.y < this.y && this.Properties.Is(IsoFlagType.transparentN)) {
					return false;
				} else if (square.x > this.x && square.Properties.Is(IsoFlagType.doorW)) {
					return false;
				} else if (square.y > this.y && square.Properties.Is(IsoFlagType.doorN)) {
					return false;
				} else if (square.x < this.x && this.Properties.Is(IsoFlagType.doorW)) {
					return false;
				} else if (square.y < this.y && this.Properties.Is(IsoFlagType.doorN)) {
					return false;
				} else {
					boolean boolean5 = boolean3 && this.Properties.Is(IsoFlagType.collideN);
					boolean boolean6 = boolean1 && this.Properties.Is(IsoFlagType.collideW);
					boolean boolean7 = boolean4 && square.Properties.Is(IsoFlagType.collideN);
					boolean boolean8 = boolean2 && square.Properties.Is(IsoFlagType.collideW);
					if (!boolean5 && !boolean6 && !boolean7 && !boolean8) {
						boolean boolean9 = square.x != this.x && square.y != this.y;
						if (!square.Properties.Is(IsoFlagType.solid) && !square.Properties.Is(IsoFlagType.blocksight)) {
							if (boolean9) {
								IsoGridSquare square3 = getSquare.getGridSquare(this.x, square.y, this.z);
								IsoGridSquare square4 = getSquare.getGridSquare(square.x, this.y, this.z);
								if (square3 != null && square3 != this && square3 != square) {
									square3.RecalcPropertiesIfNeeded();
								}

								if (square4 != null && square4 != this && square4 != square) {
									square4.RecalcPropertiesIfNeeded();
								}

								if (this.CalculateVisionBlocked(square3)) {
									return true;
								}

								if (this.CalculateVisionBlocked(square4)) {
									return true;
								}

								if (square.CalculateVisionBlocked(square3)) {
									return true;
								}

								if (square.CalculateVisionBlocked(square4)) {
									return true;
								}
							}

							return false;
						} else {
							return true;
						}
					} else {
						return true;
					}
				}
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public IsoGameCharacter FindFriend(IsoGameCharacter gameCharacter, int int1, Stack stack) {
		Stack stack2 = new Stack();
		for (int int2 = 0; int2 < gameCharacter.getLocalList().size(); ++int2) {
			IsoMovingObject movingObject = (IsoMovingObject)gameCharacter.getLocalList().get(int2);
			if (movingObject != gameCharacter && movingObject != gameCharacter.getFollowingTarget() && movingObject instanceof IsoGameCharacter && !(movingObject instanceof IsoZombie) && !stack.contains(movingObject)) {
				stack2.add((IsoGameCharacter)movingObject);
			}
		}

		float float1 = 1000000.0F;
		IsoGameCharacter gameCharacter2 = null;
		Iterator iterator = stack2.iterator();
		while (iterator.hasNext()) {
			IsoGameCharacter gameCharacter3 = (IsoGameCharacter)iterator.next();
			float float2 = 0.0F;
			float2 += Math.abs((float)this.getX() - gameCharacter3.getX());
			float2 += Math.abs((float)this.getY() - gameCharacter3.getY());
			float2 += Math.abs((float)this.getZ() - gameCharacter3.getZ());
			if (float2 < float1) {
				gameCharacter2 = gameCharacter3;
				float1 = float2;
			}

			if (gameCharacter3 == IsoPlayer.getInstance()) {
				gameCharacter2 = gameCharacter3;
				float2 = 0.0F;
			}
		}

		if (float1 > (float)int1) {
			return null;
		} else {
			return gameCharacter2;
		}
	}

	public IsoGameCharacter FindEnemy(IsoGameCharacter gameCharacter, int int1, ArrayList arrayList, IsoGameCharacter gameCharacter2, int int2) {
		float float1 = 1000000.0F;
		IsoGameCharacter gameCharacter3 = null;
		for (int int3 = 0; int3 < arrayList.size(); ++int3) {
			IsoGameCharacter gameCharacter4 = (IsoGameCharacter)arrayList.get(int3);
			float float2 = 0.0F;
			float2 += Math.abs((float)this.getX() - gameCharacter4.getX());
			float2 += Math.abs((float)this.getY() - gameCharacter4.getY());
			float2 += Math.abs((float)this.getZ() - gameCharacter4.getZ());
			if (float2 < (float)int1 && float2 < float1 && gameCharacter4.DistTo(gameCharacter2) < (float)int2) {
				gameCharacter3 = gameCharacter4;
				float1 = float2;
			}
		}

		if (float1 > (float)int1) {
			return null;
		} else {
			return gameCharacter3;
		}
	}

	public IsoGameCharacter FindEnemy(IsoGameCharacter gameCharacter, int int1, ArrayList arrayList) {
		float float1 = 1000000.0F;
		IsoGameCharacter gameCharacter2 = null;
		for (int int2 = 0; int2 < arrayList.size(); ++int2) {
			IsoGameCharacter gameCharacter3 = (IsoGameCharacter)arrayList.get(int2);
			float float2 = 0.0F;
			float2 += Math.abs((float)this.getX() - gameCharacter3.getX());
			float2 += Math.abs((float)this.getY() - gameCharacter3.getY());
			float2 += Math.abs((float)this.getZ() - gameCharacter3.getZ());
			if (float2 < float1) {
				gameCharacter2 = gameCharacter3;
				float1 = float2;
			}
		}

		if (float1 > (float)int1) {
			return null;
		} else {
			return gameCharacter2;
		}
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public void RecalcProperties() {
		this.CachedIsFree = false;
		String string = null;
		if (this.Properties.Is("waterAmount")) {
			string = this.Properties.Val("waterAmount");
		}

		String string2 = null;
		if (this.Properties.Is("fuelAmount")) {
			string2 = this.Properties.Val("fuelAmount");
		}

		if (this.zone == null) {
			this.zone = IsoWorld.instance.MetaGrid.getZoneAt(this.x, this.y, this.z);
		}

		this.Properties.Clear();
		this.hasTypes.clear();
		this.hasTree = false;
		boolean boolean1 = false;
		boolean boolean2 = false;
		boolean boolean3 = false;
		boolean boolean4 = false;
		boolean boolean5 = false;
		boolean boolean6 = false;
		boolean boolean7 = false;
		int int1 = this.Objects.size();
		IsoObject[] objectArray = (IsoObject[])this.Objects.getElements();
		for (int int2 = 0; int2 < int1; ++int2) {
			IsoObject object = objectArray[int2];
			if (object != null) {
				PropertyContainer propertyContainer = object.getProperties();
				if (propertyContainer != null && !propertyContainer.Is(IsoFlagType.blueprint)) {
					if (object.getType() == IsoObjectType.tree) {
						this.hasTree = true;
					}

					this.hasTypes.set(object.getType(), true);
					this.Properties.AddProperties(propertyContainer);
					if (propertyContainer.Is(IsoFlagType.water)) {
						boolean2 = false;
					} else {
						if (!boolean2 && propertyContainer.Is(IsoFlagType.solidfloor)) {
							boolean2 = true;
						}

						if (!boolean1 && propertyContainer.Is(IsoFlagType.solidtrans)) {
							boolean1 = true;
						}

						if (!boolean3 && propertyContainer.Is(IsoFlagType.solidfloor) && !propertyContainer.Is(IsoFlagType.transparentFloor)) {
							boolean3 = true;
						}
					}

					if (!boolean4 && propertyContainer.Is(IsoFlagType.collideN) && !propertyContainer.Is(IsoFlagType.HoppableN)) {
						boolean4 = true;
					}

					if (!boolean5 && propertyContainer.Is(IsoFlagType.collideW) && !propertyContainer.Is(IsoFlagType.HoppableW)) {
						boolean5 = true;
					}

					if (!boolean6 && propertyContainer.Is(IsoFlagType.cutN) && !propertyContainer.Is(IsoFlagType.transparentN)) {
						boolean6 = true;
					}

					if (!boolean7 && propertyContainer.Is(IsoFlagType.cutW) && !propertyContainer.Is(IsoFlagType.transparentW)) {
						boolean7 = true;
					}
				}
			}
		}

		if (this.roomID == -1 && !this.haveRoof) {
			this.getProperties().Set(IsoFlagType.exterior);
			try {
				this.getPuddles().bRecalc = true;
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else {
			this.getProperties().UnSet(IsoFlagType.exterior);
			try {
				this.getPuddles().bRecalc = true;
			} catch (Exception exception2) {
				exception2.printStackTrace();
			}
		}

		if (string != null) {
			this.getProperties().Set("waterAmount", string, false);
		}

		if (string2 != null) {
			this.getProperties().Set("fuelAmount", string2, false);
		}

		if (this.RainDrop != null) {
			this.Properties.Set(IsoFlagType.HasRaindrop);
		}

		if (this.RainSplash != null) {
			this.Properties.Set(IsoFlagType.HasRainSplashes);
		}

		if (this.burntOut) {
			this.Properties.Set(IsoFlagType.burntOut);
		}

		if (!boolean1 && boolean2 && this.Properties.Is(IsoFlagType.water)) {
			this.Properties.UnSet(IsoFlagType.solidtrans);
		}

		if (boolean3 && this.Properties.Is(IsoFlagType.transparentFloor)) {
			this.Properties.UnSet(IsoFlagType.transparentFloor);
		}

		if (boolean4 && this.Properties.Is(IsoFlagType.HoppableN)) {
			this.Properties.UnSet(IsoFlagType.HoppableN);
		}

		if (boolean5 && this.Properties.Is(IsoFlagType.HoppableW)) {
			this.Properties.UnSet(IsoFlagType.HoppableW);
		}

		if (boolean6 && this.Properties.Is(IsoFlagType.transparentN)) {
			this.Properties.UnSet(IsoFlagType.transparentN);
		}

		if (boolean7 && this.Properties.Is(IsoFlagType.transparentW)) {
			this.Properties.UnSet(IsoFlagType.transparentW);
		}

		this.propertiesDirty = this.chunk == null || this.chunk.bLoaded;
		if (this.chunk != null) {
			this.chunk.lightCheck[0] = this.chunk.lightCheck[1] = this.chunk.lightCheck[2] = this.chunk.lightCheck[3] = true;
		}

		if (this.chunk != null) {
			this.chunk.physicsCheck = true;
			this.chunk.collision.clear();
		}

		this.isExteriorCache = this.Is(IsoFlagType.exterior);
		this.isSolidFloorCache = this.Is(IsoFlagType.solidfloor);
		this.isVegitationCache = this.Is(IsoFlagType.vegitation);
	}

	public void RecalcPropertiesIfNeeded() {
		if (this.propertiesDirty) {
			this.RecalcProperties();
		}
	}

	public void ReCalculateCollide(IsoGridSquare square) {
		this.ReCalculateCollide(square, cellGetSquare);
	}

	public void ReCalculateCollide(IsoGridSquare square, IsoGridSquare.GetSquare getSquare) {
		if (1 + (square.x - this.x) < 0 || 1 + (square.y - this.y) < 0 || 1 + (square.z - this.z) < 0) {
			DebugLog.log("ERROR");
		}

		boolean boolean1 = this.CalculateCollide(square, false, false, false, false, getSquare);
		this.collideMatrix = setMatrixBit(this.collideMatrix, 1 + (square.x - this.x), 1 + (square.y - this.y), 1 + (square.z - this.z), boolean1);
	}

	public void ReCalculatePathFind(IsoGridSquare square) {
		this.ReCalculatePathFind(square, cellGetSquare);
	}

	public void ReCalculatePathFind(IsoGridSquare square, IsoGridSquare.GetSquare getSquare) {
		boolean boolean1 = this.CalculateCollide(square, false, true, false, false, getSquare);
		this.pathMatrix = setMatrixBit(this.pathMatrix, 1 + (square.x - this.x), 1 + (square.y - this.y), 1 + (square.z - this.z), boolean1);
	}

	public void ReCalculateVisionBlocked(IsoGridSquare square) {
		this.ReCalculateVisionBlocked(square, cellGetSquare);
	}

	public void ReCalculateVisionBlocked(IsoGridSquare square, IsoGridSquare.GetSquare getSquare) {
		boolean boolean1 = this.CalculateVisionBlocked(square, getSquare);
		this.visionMatrix = setMatrixBit(this.visionMatrix, 1 + (square.x - this.x), 1 + (square.y - this.y), 1 + (square.z - this.z), boolean1);
	}

	private static boolean testCollideSpecialObjects(IsoMovingObject movingObject, IsoGridSquare square, IsoGridSquare square2) {
		for (int int1 = 0; int1 < square2.SpecialObjects.size(); ++int1) {
			IsoObject object = (IsoObject)square2.SpecialObjects.get(int1);
			if (object.TestCollide(movingObject, square, square2)) {
				if (object instanceof IsoDoor) {
					movingObject.setCollidedWithDoor(true);
				} else if (object instanceof IsoThumpable && ((IsoThumpable)object).isDoor) {
					movingObject.setCollidedWithDoor(true);
				}

				movingObject.setCollidedObject(object);
				return true;
			}
		}

		return false;
	}

	public boolean testCollideAdjacent(IsoMovingObject movingObject, int int1, int int2, int int3) {
		if (movingObject instanceof IsoPlayer && ((IsoPlayer)movingObject).isNoClip()) {
			return false;
		} else if (this.collideMatrix == -1) {
			return true;
		} else if (int1 >= -1 && int1 <= 1 && int2 >= -1 && int2 <= 1 && int3 >= -1 && int3 <= 1) {
			if (this.x + int1 >= 0 && this.y + int2 >= 0 && IsoWorld.instance.MetaGrid.isValidChunk((this.x + int1) / 10, (this.y + int2) / 10)) {
				IsoGridSquare square = this.getCell().getGridSquare(this.x + int1, this.y + int2, this.z + int3);
				SafeHouse safeHouse = null;
				if ((GameServer.bServer || GameClient.bClient) && movingObject instanceof IsoPlayer && !ServerOptions.instance.SafehouseAllowTrepass.getValue()) {
					IsoGridSquare square2 = this.getCell().getGridSquare(this.x + int1, this.y + int2, 0);
					safeHouse = SafeHouse.isSafeHouse(square2, ((IsoPlayer)movingObject).getUsername(), true);
				}

				if (safeHouse != null) {
					return true;
				} else {
					if (square != null && movingObject != null) {
						IsoObject object = this.testCollideSpecialObjects(square);
						if (object != null) {
							movingObject.collideWith(object);
							if (object instanceof IsoDoor) {
								movingObject.setCollidedWithDoor(true);
							} else if (object instanceof IsoThumpable && ((IsoThumpable)object).isDoor) {
								movingObject.setCollidedWithDoor(true);
							}

							movingObject.setCollidedObject(object);
							return true;
						}
					}

					if (UseSlowCollision) {
						return this.CalculateCollide(square, false, false, false);
					} else {
						if (movingObject instanceof IsoPlayer && getMatrixBit(this.collideMatrix, int1 + 1, int2 + 1, int3 + 1)) {
							this.RecalcAllWithNeighbours(true);
						}

						return getMatrixBit(this.collideMatrix, int1 + 1, int2 + 1, int3 + 1);
					}
				}
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public boolean testCollideAdjacentAdvanced(int int1, int int2, int int3, boolean boolean1) {
		if (this.collideMatrix == -1) {
			return true;
		} else if (int1 >= -1 && int1 <= 1 && int2 >= -1 && int2 <= 1 && int3 >= -1 && int3 <= 1) {
			IsoGridSquare square = this.getCell().getGridSquare(this.x + int1, this.y + int2, this.z + int3);
			if (square != null) {
				int int4;
				IsoObject object;
				if (!square.SpecialObjects.isEmpty()) {
					for (int4 = 0; int4 < square.SpecialObjects.size(); ++int4) {
						object = (IsoObject)square.SpecialObjects.get(int4);
						if (object.TestCollide((IsoMovingObject)null, this, square)) {
							return true;
						}
					}
				}

				if (!this.SpecialObjects.isEmpty()) {
					for (int4 = 0; int4 < this.SpecialObjects.size(); ++int4) {
						object = (IsoObject)this.SpecialObjects.get(int4);
						if (object.TestCollide((IsoMovingObject)null, this, square)) {
							return true;
						}
					}
				}
			}

			return UseSlowCollision ? this.CalculateCollide(square, false, false, false) : getMatrixBit(this.collideMatrix, int1 + 1, int2 + 1, int3 + 1);
		} else {
			return true;
		}
	}

	public static void setCollisionMode() {
		UseSlowCollision = !UseSlowCollision;
	}

	public boolean testPathFindAdjacent(IsoMovingObject movingObject, int int1, int int2, int int3) {
		return this.testPathFindAdjacent(movingObject, int1, int2, int3, cellGetSquare);
	}

	public boolean testPathFindAdjacent(IsoMovingObject movingObject, int int1, int int2, int int3, IsoGridSquare.GetSquare getSquare) {
		if (int1 >= -1 && int1 <= 1 && int2 >= -1 && int2 <= 1 && int3 >= -1 && int3 <= 1) {
			IsoGridSquare square;
			if (this.Has(IsoObjectType.stairsTN) || this.Has(IsoObjectType.stairsTW)) {
				square = getSquare.getGridSquare(int1 + this.x, int2 + this.y, int3 + this.z);
				if (square == null) {
					return true;
				}

				if (this.Has(IsoObjectType.stairsTN) && square.y < this.y && square.z == this.z) {
					return true;
				}

				if (this.Has(IsoObjectType.stairsTW) && square.x < this.x && square.z == this.z) {
					return true;
				}
			}

			if (bDoSlowPathfinding) {
				square = getSquare.getGridSquare(int1 + this.x, int2 + this.y, int3 + this.z);
				return this.CalculateCollide(square, false, true, false, false, getSquare);
			} else {
				return getMatrixBit(this.pathMatrix, int1 + 1, int2 + 1, int3 + 1);
			}
		} else {
			return true;
		}
	}

	public LosUtil.TestResults testVisionAdjacent(int int1, int int2, int int3, boolean boolean1, boolean boolean2) {
		if (int1 >= -1 && int1 <= 1 && int2 >= -1 && int2 <= 1 && int3 >= -1 && int3 <= 1) {
			IsoGridSquare square;
			if (int3 == 1 && (int1 != 0 || int2 != 0) && this.HasElevatedFloor()) {
				square = this.getCell().getGridSquare(this.x, this.y, this.z + int3);
				if (square != null) {
					return square.testVisionAdjacent(int1, int2, 0, boolean1, boolean2);
				}
			}

			if (int3 == -1 && (int1 != 0 || int2 != 0)) {
				square = this.getCell().getGridSquare(this.x + int1, this.y + int2, this.z + int3);
				if (square != null && square.HasElevatedFloor()) {
					return this.testVisionAdjacent(int1, int2, 0, boolean1, boolean2);
				}
			}

			LosUtil.TestResults testResults = LosUtil.TestResults.Clear;
			IsoGridSquare square2;
			if (int1 != 0 && int2 != 0 && boolean1) {
				testResults = this.DoDiagnalCheck(int1, int2, int3, boolean2);
				if (testResults == LosUtil.TestResults.Clear || testResults == LosUtil.TestResults.ClearThroughWindow || testResults == LosUtil.TestResults.ClearThroughOpenDoor || testResults == LosUtil.TestResults.ClearThroughClosedDoor) {
					square2 = this.getCell().getGridSquare(this.x + int1, this.y + int2, this.z + int3);
					if (square2 != null) {
						testResults = square2.DoDiagnalCheck(-int1, -int2, -int3, boolean2);
					}
				}

				return testResults;
			} else {
				square2 = this.getCell().getGridSquare(this.x + int1, this.y + int2, this.z + int3);
				LosUtil.TestResults testResults2 = LosUtil.TestResults.Clear;
				if (square2 != null && square2.z == this.z) {
					int int4;
					IsoObject object;
					IsoObject.VisionResult visionResult;
					if (!this.SpecialObjects.isEmpty()) {
						for (int4 = 0; int4 < this.SpecialObjects.size(); ++int4) {
							object = (IsoObject)this.SpecialObjects.get(int4);
							if (object == null) {
								return LosUtil.TestResults.Clear;
							}

							visionResult = object.TestVision(this, square2);
							if (visionResult != IsoObject.VisionResult.NoEffect) {
								if (visionResult == IsoObject.VisionResult.Unblocked && object instanceof IsoDoor) {
									testResults2 = ((IsoDoor)object).IsOpen() ? LosUtil.TestResults.ClearThroughOpenDoor : LosUtil.TestResults.ClearThroughClosedDoor;
								} else if (visionResult == IsoObject.VisionResult.Unblocked && object instanceof IsoThumpable && ((IsoThumpable)object).isDoor) {
									testResults2 = LosUtil.TestResults.ClearThroughOpenDoor;
								} else if (visionResult == IsoObject.VisionResult.Unblocked && object instanceof IsoWindow) {
									testResults2 = LosUtil.TestResults.ClearThroughWindow;
								} else {
									if (visionResult == IsoObject.VisionResult.Blocked && object instanceof IsoDoor && !boolean2) {
										return LosUtil.TestResults.Blocked;
									}

									if (visionResult == IsoObject.VisionResult.Blocked && object instanceof IsoThumpable && ((IsoThumpable)object).isDoor && !boolean2) {
										return LosUtil.TestResults.Blocked;
									}

									if (visionResult == IsoObject.VisionResult.Blocked && object instanceof IsoThumpable && ((IsoThumpable)object).isWindow()) {
										return LosUtil.TestResults.Blocked;
									}

									if (visionResult == IsoObject.VisionResult.Blocked && object instanceof IsoCurtain) {
										return LosUtil.TestResults.Blocked;
									}

									if (visionResult == IsoObject.VisionResult.Blocked && object instanceof IsoWindow) {
										return LosUtil.TestResults.Blocked;
									}

									if (visionResult == IsoObject.VisionResult.Blocked && object instanceof IsoBarricade) {
										return LosUtil.TestResults.Blocked;
									}
								}
							}
						}
					}

					if (!square2.SpecialObjects.isEmpty()) {
						for (int4 = 0; int4 < square2.SpecialObjects.size(); ++int4) {
							object = (IsoObject)square2.SpecialObjects.get(int4);
							if (object == null) {
								return LosUtil.TestResults.Clear;
							}

							visionResult = object.TestVision(this, square2);
							if (visionResult != IsoObject.VisionResult.NoEffect) {
								if (visionResult == IsoObject.VisionResult.Unblocked && object instanceof IsoDoor) {
									testResults2 = ((IsoDoor)object).IsOpen() ? LosUtil.TestResults.ClearThroughOpenDoor : LosUtil.TestResults.ClearThroughClosedDoor;
								} else if (visionResult == IsoObject.VisionResult.Unblocked && object instanceof IsoThumpable && ((IsoThumpable)object).isDoor) {
									testResults2 = LosUtil.TestResults.ClearThroughOpenDoor;
								} else if (visionResult == IsoObject.VisionResult.Unblocked && object instanceof IsoWindow) {
									testResults2 = LosUtil.TestResults.ClearThroughWindow;
								} else {
									if (visionResult == IsoObject.VisionResult.Blocked && object instanceof IsoDoor && !boolean2) {
										return LosUtil.TestResults.Blocked;
									}

									if (visionResult == IsoObject.VisionResult.Blocked && object instanceof IsoThumpable && ((IsoThumpable)object).isDoor && !boolean2) {
										return LosUtil.TestResults.Blocked;
									}

									if (visionResult == IsoObject.VisionResult.Blocked && object instanceof IsoThumpable && ((IsoThumpable)object).isWindow()) {
										return LosUtil.TestResults.Blocked;
									}

									if (visionResult == IsoObject.VisionResult.Blocked && object instanceof IsoCurtain) {
										return LosUtil.TestResults.Blocked;
									}

									if (visionResult == IsoObject.VisionResult.Blocked && object instanceof IsoWindow) {
										return LosUtil.TestResults.Blocked;
									}

									if (visionResult == IsoObject.VisionResult.Blocked && object instanceof IsoBarricade) {
										return LosUtil.TestResults.Blocked;
									}
								}
							}
						}
					}
				}

				return !getMatrixBit(this.visionMatrix, int1 + 1, int2 + 1, int3 + 1) ? testResults2 : LosUtil.TestResults.Blocked;
			}
		} else {
			return LosUtil.TestResults.Blocked;
		}
	}

	public boolean TreatAsSolidFloor() {
		if (this.SolidFloorCached) {
			return this.SolidFloor;
		} else {
			if (!this.Properties.Is(IsoFlagType.solidfloor) && !this.HasStairs()) {
				this.SolidFloor = false;
			} else {
				this.SolidFloor = true;
			}

			this.SolidFloorCached = true;
			return this.SolidFloor;
		}
	}

	public void AddSpecialTileObject(IsoObject object) {
		this.AddSpecialObject(object);
	}

	public void renderCharacters(int int1, boolean boolean1, boolean boolean2) {
		if (this.z < int1) {
			if (!isOnScreenLast) {
			}

			if (boolean2) {
				IndieGL.glBlendFunc(770, 771);
			}

			if (this.MovingObjects.size() > 1) {
				Collections.sort(this.MovingObjects, comp);
			}

			int int2 = IsoCamera.frameState.playerIndex;
			ColorInfo colorInfo = this.lightInfo[int2];
			int int3 = this.StaticMovingObjects.size();
			int int4;
			IsoMovingObject movingObject;
			for (int4 = 0; int4 < int3; ++int4) {
				movingObject = (IsoMovingObject)this.StaticMovingObjects.get(int4);
				if ((movingObject.sprite != null || movingObject instanceof IsoDeadBody) && (!boolean1 || movingObject instanceof IsoDeadBody && !this.HasStairs()) && (boolean1 || !(movingObject instanceof IsoDeadBody) || this.HasStairs())) {
					movingObject.render(movingObject.getX(), movingObject.getY(), movingObject.getZ(), colorInfo, true, false, (Shader)null);
				}
			}

			int3 = this.MovingObjects.size();
			for (int4 = 0; int4 < int3; ++int4) {
				movingObject = (IsoMovingObject)this.MovingObjects.get(int4);
				if (movingObject != null && movingObject.sprite != null) {
					boolean boolean3 = movingObject.bOnFloor;
					if (boolean3 && movingObject instanceof IsoZombie) {
						IsoZombie zombie = (IsoZombie)movingObject;
						boolean3 = zombie.isProne();
						if (!BaseVehicle.RENDER_TO_TEXTURE) {
							boolean3 = false;
						}
					}

					if ((!boolean1 || boolean3) && (boolean1 || !boolean3)) {
						if (movingObject instanceof IsoPlayer) {
							IsoPlayer player = (IsoPlayer)movingObject;
							if (player.bRemote) {
								player.netHistory.render(player);
							}
						}

						movingObject.render(movingObject.getX(), movingObject.getY(), movingObject.getZ(), colorInfo, true, false, (Shader)null);
					}
				}
			}
		}
	}

	public void renderDeferredCharacters(int int1) {
		if (!this.DeferedCharacters.isEmpty()) {
			if (this.DeferredCharacterTick != this.getCell().DeferredCharacterTick) {
				this.DeferedCharacters.clear();
			} else if (this.z >= int1) {
				this.DeferedCharacters.clear();
			} else if (PerformanceSettings.LightingFrameSkip != 3) {
				short short1 = this.getCell().getStencilValue2z(this.x, this.y, this.z - 1);
				this.getCell().setStencilValue2z(this.x, this.y, this.z - 1, short1);
				IndieGL.enableAlphaTest();
				IndieGL.glAlphaFunc(516, 0.0F);
				IndieGL.glStencilFunc(519, short1, 127);
				IndieGL.glStencilOp(7680, 7680, 7681);
				float float1 = IsoUtils.XToScreen((float)this.x, (float)this.y, (float)this.z, 0);
				float float2 = IsoUtils.YToScreen((float)this.x, (float)this.y, (float)this.z, 0);
				float1 -= IsoCamera.frameState.OffX;
				float2 -= IsoCamera.frameState.OffY;
				IndieGL.glColorMask(false, false, false, false);
				Texture.getWhite().renderwallnw(float1, float2, (float)(64 * Core.TileScale), (float)(32 * Core.TileScale), -1, -1, -1, -1, -1, -1);
				IndieGL.glColorMask(true, true, true, true);
				IndieGL.enableAlphaTest();
				IndieGL.glAlphaFunc(516, 0.0F);
				IndieGL.glStencilFunc(514, short1, 127);
				IndieGL.glStencilOp(7680, 7680, 7680);
				ColorInfo colorInfo = this.lightInfo[IsoCamera.frameState.playerIndex];
				Collections.sort(this.DeferedCharacters, comp);
				for (int int2 = 0; int2 < this.DeferedCharacters.size(); ++int2) {
					IsoGameCharacter gameCharacter = (IsoGameCharacter)this.DeferedCharacters.get(int2);
					if (gameCharacter.sprite != null) {
						gameCharacter.setbDoDefer(false);
						gameCharacter.render(gameCharacter.getX(), gameCharacter.getY(), gameCharacter.getZ(), colorInfo, true, false, (Shader)null);
						gameCharacter.renderObjectPicker(gameCharacter.getX(), gameCharacter.getY(), gameCharacter.getZ(), colorInfo);
						gameCharacter.setbDoDefer(true);
					}
				}

				this.DeferedCharacters.clear();
				IndieGL.glAlphaFunc(516, 0.0F);
				IndieGL.glStencilFunc(519, 1, 255);
				IndieGL.glStencilOp(7680, 7680, 7680);
			}
		}
	}

	public void switchLight(boolean boolean1) {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			if (object instanceof IsoLightSwitch) {
				((IsoLightSwitch)object).setActive(boolean1);
			}
		}
	}

	public boolean IsOnScreen() {
		return this.IsOnScreen(false);
	}

	public boolean IsOnScreen(boolean boolean1) {
		if (this.CachedScreenValue != Core.TileScale) {
			this.CachedScreenX = IsoUtils.XToScreen((float)this.x, (float)this.y, (float)this.z, 0);
			this.CachedScreenY = IsoUtils.YToScreen((float)this.x, (float)this.y, (float)this.z, 0);
			this.CachedScreenValue = Core.TileScale;
		}

		float float1 = this.CachedScreenX;
		float float2 = this.CachedScreenY;
		float1 -= IsoCamera.frameState.OffX;
		float2 -= IsoCamera.frameState.OffY;
		int int1 = boolean1 ? 32 * Core.TileScale : 0;
		if (this.hasTree) {
			int int2 = 384 * Core.TileScale / 2 - 96 * Core.TileScale;
			int int3 = 256 * Core.TileScale - 32 * Core.TileScale;
			if (float1 + (float)int2 <= (float)(0 - int1)) {
				return false;
			} else if (float2 + (float)(32 * Core.TileScale) <= (float)(0 - int1)) {
				return false;
			} else if (float1 - (float)int2 >= (float)(IsoCamera.frameState.OffscreenWidth + int1)) {
				return false;
			} else {
				return !(float2 - (float)int3 >= (float)(IsoCamera.frameState.OffscreenHeight + int1));
			}
		} else if (float1 + (float)(32 * Core.TileScale) <= (float)(0 - int1)) {
			return false;
		} else if (float2 + (float)(32 * Core.TileScale) <= (float)(0 - int1)) {
			return false;
		} else if (float1 - (float)(32 * Core.TileScale) >= (float)(IsoCamera.frameState.OffscreenWidth + int1)) {
			return false;
		} else {
			return !(float2 - (float)(96 * Core.TileScale) >= (float)(IsoCamera.frameState.OffscreenHeight + int1));
		}
	}

	void cacheLightInfo() {
		int int1 = IsoCamera.frameState.playerIndex;
		this.lightInfo[int1] = this.lighting[int1].lightInfo();
	}

	public void setLightInfoServerGUIOnly(ColorInfo colorInfo) {
		this.lightInfo[0] = colorInfo;
	}

	int renderFloor(Shader shader) {
		int int1;
		try {
			IsoGridSquare.s_performance.renderFloor.start();
			int1 = this.renderFloorInternal(shader);
		} finally {
			IsoGridSquare.s_performance.renderFloor.end();
		}

		return int1;
	}

	private int renderFloorInternal(Shader shader) {
		int int1 = IsoCamera.frameState.playerIndex;
		ColorInfo colorInfo = this.lightInfo[int1];
		IsoGridSquare square = IsoCamera.frameState.CamCharacterSquare;
		boolean boolean1 = this.lighting[int1].bCouldSee();
		float float1 = this.lighting[int1].darkMulti();
		boolean boolean2 = GameClient.bClient && IsoPlayer.players[int1] != null && IsoPlayer.players[int1].isSeeNonPvpZone();
		boolean boolean3 = Core.bDebug && GameClient.bClient && SafeHouse.isSafeHouse(this, (String)null, true) != null;
		boolean boolean4 = true;
		float float2 = 1.0F;
		float float3 = 1.0F;
		if (square != null) {
			int int2 = this.getRoomID();
			if (int2 != -1) {
				int int3 = IsoWorld.instance.CurrentCell.GetEffectivePlayerRoomId();
				if (int3 == -1 && IsoWorld.instance.CurrentCell.CanBuildingSquareOccludePlayer(this, int1)) {
					boolean4 = false;
					float2 = 1.0F;
					float3 = 1.0F;
				} else if (!boolean1 && int2 != int3 && float1 < 0.5F) {
					boolean4 = false;
					float2 = 0.0F;
					float3 = float1 * 2.0F;
				}
			}
		}

		IsoWaterGeometry waterGeometry = this.z == 0 ? this.getWater() : null;
		boolean boolean5 = waterGeometry != null && waterGeometry.bShore;
		float float4 = waterGeometry == null ? 0.0F : waterGeometry.depth[0];
		float float5 = waterGeometry == null ? 0.0F : waterGeometry.depth[3];
		float float6 = waterGeometry == null ? 0.0F : waterGeometry.depth[2];
		float float7 = waterGeometry == null ? 0.0F : waterGeometry.depth[1];
		int int4 = 0;
		int int5 = this.Objects.size();
		IsoObject[] objectArray = (IsoObject[])this.Objects.getElements();
		int int6;
		for (int6 = 0; int6 < int5; ++int6) {
			IsoObject object = objectArray[int6];
			if (boolean2 && (object.highlightFlags & 1) == 0) {
				object.setHighlighted(true);
				if (NonPvpZone.getNonPvpZone(this.x, this.y) != null) {
					object.setHighlightColor(0.6F, 0.6F, 1.0F, 0.5F);
				} else {
					object.setHighlightColor(1.0F, 0.6F, 0.6F, 0.5F);
				}
			}

			if (boolean3) {
				object.setHighlighted(true);
				object.setHighlightColor(1.0F, 0.0F, 0.0F, 1.0F);
			}

			boolean boolean6 = true;
			if (object.sprite != null && !object.sprite.solidfloor && object.sprite.renderLayer != 1) {
				boolean6 = false;
				int4 |= 4;
			}

			if (object instanceof IsoFire || object instanceof IsoCarBatteryCharger) {
				boolean6 = false;
				int4 |= 4;
			}

			if (!boolean6) {
				boolean boolean7 = object.sprite != null && (object.sprite.isBush || object.sprite.canBeRemoved || object.sprite.attachedFloor);
				if (this.bFlattenGrassEtc && boolean7) {
					int4 |= 2;
				}
			} else {
				IndieGL.glAlphaFunc(516, 0.0F);
				object.setTargetAlpha(int1, float3);
				if (boolean4) {
					object.setAlpha(int1, float2);
				}

				if (DebugOptions.instance.Terrain.RenderTiles.RenderGridSquares.getValue()) {
					IndieGL.StartShader(shader, int1);
					FloorShaperAttachedSprites floorShaperAttachedSprites = FloorShaperAttachedSprites.instance;
					Object object2;
					if (!object.getProperties().Is(IsoFlagType.diamondFloor) && !object.getProperties().Is(IsoFlagType.water)) {
						object2 = FloorShaperDeDiamond.instance;
					} else {
						object2 = FloorShaperDiamond.instance;
					}

					int int7 = this.getVertLight(0, int1);
					int int8 = this.getVertLight(1, int1);
					int int9 = this.getVertLight(2, int1);
					int int10 = this.getVertLight(3, int1);
					if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Floor.LightingDebug.getValue()) {
						int7 = -65536;
						int8 = -65536;
						int9 = -16776961;
						int10 = -16776961;
					}

					floorShaperAttachedSprites.setShore(boolean5);
					floorShaperAttachedSprites.setWaterDepth(float4, float5, float6, float7);
					floorShaperAttachedSprites.setVertColors(int7, int8, int9, int10);
					((FloorShaper)object2).setShore(boolean5);
					((FloorShaper)object2).setWaterDepth(float4, float5, float6, float7);
					((FloorShaper)object2).setVertColors(int7, int8, int9, int10);
					object.renderFloorTile((float)this.x, (float)this.y, (float)this.z, PerformanceSettings.LightingFrameSkip < 3 ? defColorInfo : colorInfo, true, false, shader, (Consumer)object2, floorShaperAttachedSprites);
					IndieGL.StartShader((Shader)null);
				}

				int4 |= 1;
				if ((object.highlightFlags & 1) == 0) {
					int4 |= 8;
				}

				if ((object.highlightFlags & 2) != 0) {
					object.highlightFlags &= -4;
				}
			}
		}

		if ((this.getCell().rainIntensity > 0 || RainManager.isRaining() && RainManager.RainIntensity > 0.0F) && this.isExteriorCache && !this.isVegitationCache && this.isSolidFloorCache && this.isCouldSee(int1)) {
			if (!IsoCamera.frameState.Paused) {
				int6 = this.getCell().rainIntensity == 0 ? (int)Math.min(Math.floor((double)(RainManager.RainIntensity / 0.2F)) + 1.0, 5.0) : this.getCell().rainIntensity;
				if (this.splashFrame < 0.0F && Rand.Next(Rand.AdjustForFramerate((int)(5.0F / (float)int6) * 100)) == 0) {
					this.splashFrame = 0.0F;
				}
			}

			if (this.splashFrame >= 0.0F) {
				int6 = (int)(this.splashFrame * 4.0F);
				if (rainsplashCache[int6] == null) {
					rainsplashCache[int6] = "RainSplash_00_" + int6;
				}

				Texture texture = Texture.getSharedTexture(rainsplashCache[int6]);
				if (texture != null) {
					float float8 = IsoUtils.XToScreen((float)this.x + this.splashX, (float)this.y + this.splashY, (float)this.z, 0) - IsoCamera.frameState.OffX;
					float float9 = IsoUtils.YToScreen((float)this.x + this.splashX, (float)this.y + this.splashY, (float)this.z, 0) - IsoCamera.frameState.OffY;
					float8 -= (float)(texture.getWidth() / 2 * Core.TileScale);
					float9 -= (float)(texture.getHeight() / 2 * Core.TileScale);
					float float10 = 0.6F * (this.getCell().rainIntensity > 0 ? 1.0F : RainManager.RainIntensity);
					float float11 = Core.getInstance().RenderShader != null ? 0.6F : 1.0F;
					SpriteRenderer.instance.render(texture, float8, float9, (float)(texture.getWidth() * Core.TileScale), (float)(texture.getHeight() * Core.TileScale), 0.8F * colorInfo.r, 0.9F * colorInfo.g, 1.0F * colorInfo.b, float10 * float11, (Consumer)null);
				}

				if (!IsoCamera.frameState.Paused && this.splashFrameNum != IsoCamera.frameState.frameCount) {
					this.splashFrame += 0.08F * (30.0F / (float)PerformanceSettings.getLockFPS());
					if (this.splashFrame >= 1.0F) {
						this.splashX = Rand.Next(0.1F, 0.9F);
						this.splashY = Rand.Next(0.1F, 0.9F);
						this.splashFrame = -1.0F;
					}

					this.splashFrameNum = IsoCamera.frameState.frameCount;
				}
			}
		} else {
			this.splashFrame = -1.0F;
		}

		return int4;
	}

	private boolean isSpriteOnSouthOrEastWall(IsoObject object) {
		if (object instanceof IsoBarricade) {
			return object.getDir() == IsoDirections.S || object.getDir() == IsoDirections.E;
		} else if (object instanceof IsoCurtain) {
			IsoCurtain curtain = (IsoCurtain)object;
			return curtain.getType() == IsoObjectType.curtainS || curtain.getType() == IsoObjectType.curtainE;
		} else {
			PropertyContainer propertyContainer = object.getProperties();
			return propertyContainer != null && (propertyContainer.Is(IsoFlagType.attachedE) || propertyContainer.Is(IsoFlagType.attachedS));
		}
	}

	public void RenderOpenDoorOnly() {
		int int1 = this.Objects.size();
		IsoObject[] objectArray = (IsoObject[])this.Objects.getElements();
		try {
			byte byte1 = 0;
			int int2 = int1 - 1;
			for (int int3 = byte1; int3 <= int2; ++int3) {
				IsoObject object = objectArray[int3];
				if (object.sprite != null && (object.sprite.getProperties().Is(IsoFlagType.attachedN) || object.sprite.getProperties().Is(IsoFlagType.attachedW))) {
					object.renderFxMask((float)this.x, (float)this.y, (float)this.z, false);
				}
			}
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}
	}

	public boolean RenderMinusFloorFxMask(int int1, boolean boolean1, boolean boolean2) {
		boolean boolean3 = false;
		int int2 = this.Objects.size();
		IsoObject[] objectArray = (IsoObject[])this.Objects.getElements();
		long long1 = System.currentTimeMillis();
		try {
			int int3 = boolean1 ? int2 - 1 : 0;
			int int4 = boolean1 ? 0 : int2 - 1;
			int int5 = int3;
			while (true) {
				if (boolean1) {
					if (int5 < int4) {
						break;
					}
				} else if (int5 > int4) {
					break;
				}

				IsoObject object = objectArray[int5];
				if (object.sprite != null) {
					boolean boolean4 = true;
					IsoObjectType objectType = object.sprite.getType();
					if (object.sprite.solidfloor || object.sprite.renderLayer == 1) {
						boolean4 = false;
					}

					if (this.z >= int1 && !object.sprite.alwaysDraw) {
						boolean4 = false;
					}

					boolean boolean5 = object.sprite.isBush || object.sprite.canBeRemoved || object.sprite.attachedFloor;
					if ((!boolean2 || boolean5 && this.bFlattenGrassEtc) && (boolean2 || !boolean5 || !this.bFlattenGrassEtc)) {
						if ((objectType == IsoObjectType.WestRoofB || objectType == IsoObjectType.WestRoofM || objectType == IsoObjectType.WestRoofT) && this.z == int1 - 1 && this.z == (int)IsoCamera.CamCharacter.getZ()) {
							boolean4 = false;
						}

						if (this.isSpriteOnSouthOrEastWall(object)) {
							if (!boolean1) {
								boolean4 = false;
							}

							boolean3 = true;
						} else if (boolean1) {
							boolean4 = false;
						}

						if (boolean4) {
							if (!object.sprite.cutW && !object.sprite.cutN) {
								object.renderFxMask((float)this.x, (float)this.y, (float)this.z, false);
							} else {
								int int6 = IsoCamera.frameState.playerIndex;
								boolean boolean6 = object.sprite.cutN;
								boolean boolean7 = object.sprite.cutW;
								IsoGridSquare square = this.nav[IsoDirections.S.index()];
								IsoGridSquare square2 = this.nav[IsoDirections.E.index()];
								boolean boolean8 = square != null && square.getPlayerCutawayFlag(int6, long1);
								boolean boolean9 = this.getPlayerCutawayFlag(int6, long1);
								boolean boolean10 = square2 != null && square2.getPlayerCutawayFlag(int6, long1);
								IsoDirections directions;
								if (boolean6 && boolean7) {
									directions = IsoDirections.NW;
								} else if (boolean6) {
									directions = IsoDirections.N;
								} else if (boolean7) {
									directions = IsoDirections.W;
								} else {
									directions = IsoDirections.W;
								}

								this.DoCutawayShaderSprite(object.sprite, directions, boolean8, boolean9, boolean10);
							}
						}
					}
				}

				int5 += boolean1 ? -1 : 1;
			}
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}

		return boolean3;
	}

	private boolean isWindowOrWindowFrame(IsoObject object, boolean boolean1) {
		if (object != null && object.sprite != null) {
			if (boolean1 && object.sprite.getProperties().Is(IsoFlagType.windowN)) {
				return true;
			} else if (!boolean1 && object.sprite.getProperties().Is(IsoFlagType.windowW)) {
				return true;
			} else {
				IsoThumpable thumpable = (IsoThumpable)Type.tryCastTo(object, IsoThumpable.class);
				if (thumpable != null && thumpable.isWindow()) {
					return boolean1 == thumpable.getNorth();
				} else {
					return IsoWindowFrame.isWindowFrame(object, boolean1);
				}
			}
		} else {
			return false;
		}
	}

	boolean renderMinusFloor(int int1, boolean boolean1, boolean boolean2, boolean boolean3, boolean boolean4, boolean boolean5, Shader shader) {
		if (!DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.RenderMinusFloor.getValue()) {
			return false;
		} else {
			IndieGL.glBlendFunc(770, 771);
			int int2 = 0;
			isOnScreenLast = this.IsOnScreen();
			int int3 = IsoCamera.frameState.playerIndex;
			IsoGridSquare square = IsoCamera.frameState.CamCharacterSquare;
			ColorInfo colorInfo = this.lightInfo[int3];
			boolean boolean6 = this.lighting[int3].bCouldSee();
			float float1 = this.lighting[int3].darkMulti();
			boolean boolean7 = IsoWorld.instance.CurrentCell.CanBuildingSquareOccludePlayer(this, int3);
			colorInfo.a = 1.0F;
			defColorInfo.r = 1.0F;
			defColorInfo.g = 1.0F;
			defColorInfo.b = 1.0F;
			defColorInfo.a = 1.0F;
			if (Core.bDebug && DebugOptions.instance.DebugDraw_SkipWorldShading.getValue()) {
				colorInfo = defColorInfo;
			}

			float float2 = this.CachedScreenX - IsoCamera.frameState.OffX;
			float float3 = this.CachedScreenY - IsoCamera.frameState.OffY;
			boolean boolean8 = true;
			IsoCell cell = this.getCell();
			if (float2 + (float)(32 * Core.TileScale) <= (float)cell.StencilX1 || float2 - (float)(32 * Core.TileScale) >= (float)cell.StencilX2 || float3 + (float)(32 * Core.TileScale) <= (float)cell.StencilY1 || float3 - (float)(96 * Core.TileScale) >= (float)cell.StencilY2) {
				boolean8 = false;
			}

			boolean boolean9 = false;
			int int4 = this.Objects.size();
			IsoObject[] objectArray = (IsoObject[])this.Objects.getElements();
			int int5 = boolean1 ? int4 - 1 : 0;
			int int6 = boolean1 ? 0 : int4 - 1;
			boolean boolean10 = false;
			boolean boolean11 = false;
			boolean boolean12 = false;
			boolean boolean13 = false;
			int int7;
			if (!boolean1) {
				for (int7 = int5; int7 <= int6; ++int7) {
					IsoObject object = objectArray[int7];
					IsoGridSquare square2;
					if (this.isWindowOrWindowFrame(object, true) && (boolean4 || boolean5)) {
						square2 = this.nav[IsoDirections.N.index()];
						boolean12 = boolean6 || square2 != null && square2.isCouldSee(int3);
					}

					if (this.isWindowOrWindowFrame(object, false) && (boolean4 || boolean3)) {
						square2 = this.nav[IsoDirections.W.index()];
						boolean13 = boolean6 || square2 != null && square2.isCouldSee(int3);
					}

					if (object.sprite != null && (object.sprite.getType() == IsoObjectType.doorFrN || object.sprite.getType() == IsoObjectType.doorN) && (boolean4 || boolean5)) {
						square2 = this.nav[IsoDirections.N.index()];
						boolean10 = boolean6 || square2 != null && square2.isCouldSee(int3);
					}

					if (object.sprite != null && (object.sprite.getType() == IsoObjectType.doorFrW || object.sprite.getType() == IsoObjectType.doorW) && (boolean4 || boolean3)) {
						square2 = this.nav[IsoDirections.W.index()];
						boolean11 = boolean6 || square2 != null && square2.isCouldSee(int3);
					}
				}
			}

			int7 = IsoWorld.instance.CurrentCell.GetEffectivePlayerRoomId();
			bWallCutawayN = false;
			bWallCutawayW = false;
			int int8 = int5;
			while (true) {
				if (boolean1) {
					if (int8 < int6) {
						break;
					}
				} else if (int8 > int6) {
					break;
				}

				IsoObject object2 = objectArray[int8];
				boolean boolean14 = true;
				IsoObjectType objectType = IsoObjectType.MAX;
				if (object2.sprite != null) {
					objectType = object2.sprite.getType();
				}

				CircleStencil = false;
				if (object2.sprite != null && (object2.sprite.solidfloor || object2.sprite.renderLayer == 1)) {
					boolean14 = false;
				}

				if (object2 instanceof IsoFire) {
					boolean14 = !boolean2;
				}

				if (this.z >= int1 && (object2.sprite == null || !object2.sprite.alwaysDraw)) {
					boolean14 = false;
				}

				boolean boolean15 = object2.sprite != null && (object2.sprite.isBush || object2.sprite.canBeRemoved || object2.sprite.attachedFloor);
				if ((!boolean2 || boolean15 && this.bFlattenGrassEtc) && (boolean2 || !boolean15 || !this.bFlattenGrassEtc)) {
					if (object2.sprite != null && (objectType == IsoObjectType.WestRoofB || objectType == IsoObjectType.WestRoofM || objectType == IsoObjectType.WestRoofT) && this.z == int1 - 1 && this.z == (int)IsoCamera.CamCharacter.getZ()) {
						boolean14 = false;
					}

					boolean boolean16 = objectType == IsoObjectType.doorFrW || objectType == IsoObjectType.doorW || object2.sprite != null && object2.sprite.cutW;
					boolean boolean17 = objectType == IsoObjectType.doorFrN || objectType == IsoObjectType.doorN || object2.sprite != null && object2.sprite.cutN;
					boolean boolean18 = object2 instanceof IsoDoor && ((IsoDoor)object2).open || object2 instanceof IsoThumpable && ((IsoThumpable)object2).open;
					boolean boolean19 = object2.container != null;
					boolean boolean20 = object2.sprite != null && object2.sprite.getProperties().Is(IsoFlagType.waterPiped);
					if (object2.sprite != null && objectType == IsoObjectType.MAX && !(object2 instanceof IsoDoor) && !(object2 instanceof IsoWindow) && !boolean19 && !boolean20) {
						if (!boolean16 && object2.sprite.getProperties().Is(IsoFlagType.attachedW) && (boolean7 || boolean3 || boolean4)) {
							boolean14 = !bWallCutawayW;
						} else if (!boolean17 && object2.sprite.getProperties().Is(IsoFlagType.attachedN) && (boolean7 || boolean4 || boolean5)) {
							boolean14 = !bWallCutawayN;
						}
					}

					if (object2.sprite != null && !object2.sprite.solidfloor && IsoPlayer.getInstance().isClimbing()) {
						boolean14 = true;
					}

					if (this.isSpriteOnSouthOrEastWall(object2)) {
						if (!boolean1) {
							boolean14 = false;
						}

						boolean9 = true;
					} else if (boolean1) {
						boolean14 = false;
					}

					if (boolean14) {
						IndieGL.glAlphaFunc(516, 0.0F);
						object2.bAlphaForced = false;
						if (boolean18) {
							object2.setTargetAlpha(int3, 0.6F);
							object2.setAlpha(int3, 0.6F);
						}

						if (object2.sprite == null || !boolean16 && !boolean17) {
							if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Objects.getValue()) {
								if (this.getRoomID() != -1 && this.getRoomID() != int7 && IsoPlayer.players[int3].isSeatedInVehicle() && IsoPlayer.players[int3].getVehicle().getCurrentSpeedKmHour() >= 50.0F) {
									break;
								}

								if (this.getRoomID() != -1 || !boolean5 && !boolean3 || objectType != IsoObjectType.WestRoofB && objectType != IsoObjectType.WestRoofM && objectType != IsoObjectType.WestRoofT) {
									if (square != null && !boolean6 && this.getRoomID() != int7 && float1 < 0.5F) {
										object2.setTargetAlpha(int3, float1 * 2.0F);
									} else {
										if (!boolean18) {
											object2.setTargetAlpha(int3, 1.0F);
										}

										if (IsoPlayer.getInstance() != null && object2.getProperties() != null && (object2.getProperties().Is(IsoFlagType.solid) || object2.getProperties().Is(IsoFlagType.solidtrans) || object2.getProperties().Is(IsoFlagType.attachedCeiling)) || objectType.index() > 2 && objectType.index() < 9 && IsoCamera.frameState.CamCharacterZ <= object2.getZ()) {
											byte byte1 = 2;
											float float4 = 0.75F;
											if (objectType.index() > 2 && objectType.index() < 9) {
												byte1 = 4;
												float4 = 0.5F;
											}

											int int9 = this.getX() - (int)IsoPlayer.getInstance().getX();
											int int10 = this.getY() - (int)IsoPlayer.getInstance().getY();
											if (int9 > 0 && int9 < byte1 && int10 >= 0 && int10 < byte1 || int10 > 0 && int10 < byte1 && int9 >= 0 && int9 < byte1) {
												object2.setTargetAlpha(int3, float4);
											}

											IsoZombie zombie = IsoCell.getInstance().getNearestVisibleZombie(int3);
											if (zombie != null && zombie.getCurrentSquare() != null && zombie.getCurrentSquare().isCanSee(int3)) {
												int int11 = this.getX() - (int)zombie.x;
												int int12 = this.getY() - (int)zombie.y;
												if (int11 > 0 && int11 < byte1 && int12 >= 0 && int12 < byte1 || int12 > 0 && int12 < byte1 && int11 >= 0 && int11 < byte1) {
													object2.setTargetAlpha(int3, float4);
												}
											}
										}
									}
								} else {
									object2.setTargetAlpha(int3, 0.0F);
								}

								if (object2 instanceof IsoWindow) {
									IsoWindow window = (IsoWindow)object2;
									if (object2.getTargetAlpha(int3) < 1.0E-4F) {
										IsoGridSquare square3 = window.getOppositeSquare();
										if (square3 != null && square3 != this && square3.lighting[int3].bSeen()) {
											object2.setTargetAlpha(int3, square3.lighting[int3].darkMulti() * 2.0F);
										}
									}

									if (object2.getTargetAlpha(int3) > 0.4F) {
										if ((boolean4 || boolean5) && object2.sprite.getProperties().Is(IsoFlagType.windowN)) {
											object2.setTargetAlpha(int3, 0.4F);
											bWallCutawayN = true;
										} else if ((boolean4 || boolean3) && object2.sprite.getProperties().Is(IsoFlagType.windowW)) {
											object2.setTargetAlpha(int3, 0.4F);
											bWallCutawayW = true;
										}
									}
								}

								if (object2 instanceof IsoTree) {
									if (boolean8 && this.x >= (int)IsoCamera.frameState.CamCharacterX && this.y >= (int)IsoCamera.frameState.CamCharacterY && square != null && square.Is(IsoFlagType.exterior)) {
										((IsoTree)object2).bRenderFlag = true;
										object2.setTargetAlpha(int3, Math.min(0.99F, object2.getTargetAlpha(int3)));
									} else {
										((IsoTree)object2).bRenderFlag = false;
									}
								}

								object2.render((float)this.x, (float)this.y, (float)this.z, colorInfo, true, false, (Shader)null);
							}
						} else if (PerformanceSettings.LightingFrameSkip < 3) {
							if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.DoorsAndWalls.getValue()) {
								CircleStencil = true;
								if (square != null && this.getRoomID() != -1 && int7 == -1 && boolean7) {
									object2.setTargetAlpha(int3, 0.5F);
									object2.setAlpha(int3, 0.5F);
								} else if (!boolean18) {
									object2.setTargetAlpha(int3, 1.0F);
									object2.setAlpha(int3, 1.0F);
								}

								object2.bAlphaForced = true;
								if (object2.sprite.cutW && object2.sprite.cutN) {
									int2 = this.DoWallLightingNW(object2, int2, boolean3, boolean4, boolean5, boolean10, boolean11, boolean12, boolean13, shader);
								} else if (object2.sprite.getType() != IsoObjectType.doorFrW && objectType != IsoObjectType.doorW && !object2.sprite.cutW) {
									if (objectType == IsoObjectType.doorFrN || objectType == IsoObjectType.doorN || object2.sprite.cutN) {
										int2 = this.DoWallLightingN(object2, int2, boolean4, boolean5, boolean10, boolean12, shader);
									}
								} else {
									int2 = this.DoWallLightingW(object2, int2, boolean3, boolean4, boolean11, boolean13, shader);
								}

								if (object2 instanceof IsoWindow && object2.getTargetAlpha(int3) < 1.0F) {
									bWallCutawayN |= object2.sprite.cutN;
									bWallCutawayW |= object2.sprite.cutW;
								}
							}
						} else if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.DoorsAndWalls_SimpleLighting.getValue()) {
							if (this.z != (int)IsoCamera.frameState.CamCharacterZ || objectType == IsoObjectType.doorFrW || objectType == IsoObjectType.doorFrN || object2 instanceof IsoWindow) {
								boolean8 = false;
							}

							if (object2.getTargetAlpha(int3) < 1.0F) {
								if (!boolean8) {
									object2.setTargetAlpha(int3, 1.0F);
								}

								object2.setAlphaToTarget(int3);
								IsoObject.LowLightingQualityHack = false;
								object2.render((float)this.x, (float)this.y, (float)this.z, colorInfo, true, false, (Shader)null);
								if (!IsoObject.LowLightingQualityHack) {
									object2.setTargetAlpha(int3, 1.0F);
								}
							} else {
								object2.render((float)this.x, (float)this.y, (float)this.z, colorInfo, true, false, (Shader)null);
							}
						}

						if ((object2.highlightFlags & 2) != 0) {
							object2.highlightFlags &= -4;
						}
					}
				}

				int8 += boolean1 ? -1 : 1;
			}

			return boolean9;
		}
	}

	void RereouteWallMaskTo(IsoObject object) {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object2 = (IsoObject)this.Objects.get(int1);
			if (object2.sprite.getProperties().Is(IsoFlagType.collideW) || object2.sprite.getProperties().Is(IsoFlagType.collideN)) {
				object2.rerouteMask = object;
			}
		}
	}

	void setBlockedGridPointers(IsoGridSquare.GetSquare getSquare) {
		this.w = getSquare.getGridSquare(this.x - 1, this.y, this.z);
		this.e = getSquare.getGridSquare(this.x + 1, this.y, this.z);
		this.s = getSquare.getGridSquare(this.x, this.y + 1, this.z);
		this.n = getSquare.getGridSquare(this.x, this.y - 1, this.z);
		this.ne = getSquare.getGridSquare(this.x + 1, this.y - 1, this.z);
		this.nw = getSquare.getGridSquare(this.x - 1, this.y - 1, this.z);
		this.se = getSquare.getGridSquare(this.x + 1, this.y + 1, this.z);
		this.sw = getSquare.getGridSquare(this.x - 1, this.y + 1, this.z);
		if (this.s != null && this.testPathFindAdjacent((IsoMovingObject)null, this.s.x - this.x, this.s.y - this.y, this.s.z - this.z, getSquare)) {
			this.s = null;
		}

		if (this.w != null && this.testPathFindAdjacent((IsoMovingObject)null, this.w.x - this.x, this.w.y - this.y, this.w.z - this.z, getSquare)) {
			this.w = null;
		}

		if (this.n != null && this.testPathFindAdjacent((IsoMovingObject)null, this.n.x - this.x, this.n.y - this.y, this.n.z - this.z, getSquare)) {
			this.n = null;
		}

		if (this.e != null && this.testPathFindAdjacent((IsoMovingObject)null, this.e.x - this.x, this.e.y - this.y, this.e.z - this.z, getSquare)) {
			this.e = null;
		}

		if (this.sw != null && this.testPathFindAdjacent((IsoMovingObject)null, this.sw.x - this.x, this.sw.y - this.y, this.sw.z - this.z, getSquare)) {
			this.sw = null;
		}

		if (this.se != null && this.testPathFindAdjacent((IsoMovingObject)null, this.se.x - this.x, this.se.y - this.y, this.se.z - this.z, getSquare)) {
			this.se = null;
		}

		if (this.nw != null && this.testPathFindAdjacent((IsoMovingObject)null, this.nw.x - this.x, this.nw.y - this.y, this.nw.z - this.z, getSquare)) {
			this.nw = null;
		}

		if (this.ne != null && this.testPathFindAdjacent((IsoMovingObject)null, this.ne.x - this.x, this.ne.y - this.y, this.ne.z - this.z, getSquare)) {
			this.ne = null;
		}
	}

	public IsoObject getContainerItem(String string) {
		int int1 = this.getObjects().size();
		IsoObject[] objectArray = (IsoObject[])this.getObjects().getElements();
		for (int int2 = 0; int2 < int1; ++int2) {
			IsoObject object = objectArray[int2];
			if (object.getContainer() != null && string.equals(object.getContainer().getType())) {
				return object;
			}
		}

		return null;
	}

	public void StartFire() {
		IsoFireManager.StartFire(this.getCell(), this, true, 100000);
	}

	public void explode() {
		IsoFireManager.explode(this.getCell(), this, 100000);
	}

	public int getHourLastSeen() {
		return this.hourLastSeen;
	}

	public float getHoursSinceLastSeen() {
		return (float)GameTime.instance.getWorldAgeHours() - (float)this.hourLastSeen;
	}

	public void CalcVisibility(int int1) {
		IsoPlayer player = IsoPlayer.players[int1];
		IsoGridSquare.ILighting iLighting = this.lighting[int1];
		iLighting.bCanSee(false);
		iLighting.bCouldSee(false);
		if (GameServer.bServer || player != null && (!player.isDead() || player.ReanimatedCorpse != null)) {
			if (player != null) {
				IsoGameCharacter.LightInfo lightInfo = player.getLightInfo2();
				IsoGridSquare square = lightInfo.square;
				if (square != null) {
					IsoChunk chunk = this.getChunk();
					if (chunk != null) {
						tempo.x = (float)this.x + 0.5F;
						tempo.y = (float)this.y + 0.5F;
						tempo2.x = lightInfo.x;
						tempo2.y = lightInfo.y;
						Vector2 vector2 = tempo2;
						vector2.x -= tempo.x;
						vector2 = tempo2;
						vector2.y -= tempo.y;
						Vector2 vector22 = tempo;
						float float1 = tempo2.getLength();
						tempo2.normalize();
						if (player instanceof IsoSurvivor) {
							player.setForwardDirection(vector22);
							lightInfo.angleX = vector22.x;
							lightInfo.angleY = vector22.y;
						}

						vector22.x = lightInfo.angleX;
						vector22.y = lightInfo.angleY;
						vector22.normalize();
						float float2 = tempo2.dot(vector22);
						if (square == this) {
							float2 = -1.0F;
						}

						float float3;
						if (!GameServer.bServer) {
							float float4 = player.getStats().fatigue - 0.6F;
							if (float4 < 0.0F) {
								float4 = 0.0F;
							}

							float4 *= 2.5F;
							if (player.Traits.HardOfHearing.isSet() && float4 < 0.7F) {
								float4 = 0.7F;
							}

							float3 = 2.0F;
							if (player.Traits.KeenHearing.isSet()) {
								float3 += 3.0F;
							}

							if (float1 < float3 * (1.0F - float4) && !player.Traits.Deaf.isSet()) {
								float2 = -1.0F;
							}
						}

						LosUtil.TestResults testResults = LosUtil.lineClearCached(this.getCell(), this.x, this.y, this.z, (int)lightInfo.x, (int)lightInfo.y, (int)lightInfo.z, false, int1);
						float3 = -0.2F;
						float3 -= player.getStats().fatigue - 0.6F;
						if (float3 > -0.2F) {
							float3 = -0.2F;
						}

						if (player.getStats().fatigue >= 1.0F) {
							float3 -= 0.2F;
						}

						if (player.getMoodles().getMoodleLevel(MoodleType.Panic) == 4) {
							float3 -= 0.2F;
						}

						if (float3 < -0.9F) {
							float3 = -0.9F;
						}

						if (player.Traits.EagleEyed.isSet()) {
							float3 += 0.2F;
						}

						if (player instanceof IsoPlayer && player.getVehicle() != null) {
							float3 = 1.0F;
						}

						if (!(float2 > float3) && testResults != LosUtil.TestResults.Blocked) {
							iLighting.bCouldSee(true);
							if (this.room != null && this.room.def != null && !this.room.def.bExplored) {
								byte byte1 = 10;
								if (lightInfo.square != null && lightInfo.square.getBuilding() == this.room.building) {
									byte1 = 50;
								}

								if ((!GameServer.bServer || !(player instanceof IsoPlayer) || !((IsoPlayer)player).isGhostMode()) && IsoUtils.DistanceManhatten(lightInfo.x, lightInfo.y, (float)this.x, (float)this.y) < (float)byte1 && this.z == (int)lightInfo.z) {
									if (GameServer.bServer) {
										DebugLog.log(DebugType.Zombie, "bExplored room=" + this.room.def.ID);
									}

									this.room.def.bExplored = true;
									this.room.onSee();
									this.room.seen = 0;
								}
							}

							if (!GameClient.bClient) {
								Meta.instance.dealWithSquareSeen(this);
							}

							iLighting.bCanSee(true);
							iLighting.bSeen(true);
							iLighting.targetDarkMulti(1.0F);
						} else {
							if (testResults == LosUtil.TestResults.Blocked) {
								iLighting.bCouldSee(false);
							} else {
								iLighting.bCouldSee(true);
							}

							if (!GameServer.bServer) {
								if (iLighting.bSeen()) {
									float float5 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex());
									if (!iLighting.bCouldSee()) {
										float5 *= 0.5F;
									} else {
										float5 *= 0.94F;
									}

									if (this.room == null && square.getRoom() == null) {
										iLighting.targetDarkMulti(float5);
									} else if (this.room != null && square.getRoom() != null && this.room.building == square.getRoom().building) {
										if (this.room != square.getRoom() && !iLighting.bCouldSee()) {
											iLighting.targetDarkMulti(0.0F);
										} else {
											iLighting.targetDarkMulti(float5);
										}
									} else if (this.room == null) {
										iLighting.targetDarkMulti(float5 / 2.0F);
									} else if (iLighting.lampostTotalR() + iLighting.lampostTotalG() + iLighting.lampostTotalB() == 0.0F) {
										iLighting.targetDarkMulti(0.0F);
									}

									if (this.room != null) {
										iLighting.targetDarkMulti(iLighting.targetDarkMulti() * 0.7F);
									}
								} else {
									iLighting.targetDarkMulti(0.0F);
									iLighting.darkMulti(0.0F);
								}
							}
						}

						if (float2 > float3) {
							iLighting.targetDarkMulti(iLighting.targetDarkMulti() * 0.85F);
						}

						if (!GameServer.bServer) {
							for (int int2 = 0; int2 < lightInfo.torches.size(); ++int2) {
								IsoGameCharacter.TorchInfo torchInfo = (IsoGameCharacter.TorchInfo)lightInfo.torches.get(int2);
								tempo2.x = torchInfo.x;
								tempo2.y = torchInfo.y;
								vector2 = tempo2;
								vector2.x -= (float)this.x + 0.5F;
								vector2 = tempo2;
								vector2.y -= (float)this.y + 0.5F;
								float1 = tempo2.getLength();
								tempo2.normalize();
								vector22.x = torchInfo.angleX;
								vector22.y = torchInfo.angleY;
								vector22.normalize();
								float2 = tempo2.dot(vector22);
								if ((int)torchInfo.x == this.getX() && (int)torchInfo.y == this.getY() && (int)torchInfo.z == this.getZ()) {
									float2 = -1.0F;
								}

								boolean boolean1 = false;
								if (IsoUtils.DistanceManhatten((float)this.getX(), (float)this.getY(), torchInfo.x, torchInfo.y) < torchInfo.dist && (torchInfo.bCone && float2 < -torchInfo.dot || float2 == -1.0F || !torchInfo.bCone && float2 < 0.8F)) {
									boolean1 = true;
								}

								if ((torchInfo.bCone && float1 < torchInfo.dist || !torchInfo.bCone && float1 < torchInfo.dist) && iLighting.bCanSee() && boolean1 && this.z == (int)player.getZ()) {
									float float6 = float1 / torchInfo.dist;
									if (float6 > 1.0F) {
										float6 = 1.0F;
									}

									if (float6 < 0.0F) {
										float6 = 0.0F;
									}

									iLighting.targetDarkMulti(iLighting.targetDarkMulti() + torchInfo.strength * (1.0F - float6) * 3.0F);
									if (iLighting.targetDarkMulti() > 2.5F) {
										iLighting.targetDarkMulti(2.5F);
									}

									torchTimer = lightInfo.time;
								}
							}
						}
					}
				}
			}
		} else {
			iLighting.bSeen(true);
			iLighting.bCanSee(true);
			iLighting.bCouldSee(true);
		}
	}

	private LosUtil.TestResults DoDiagnalCheck(int int1, int int2, int int3, boolean boolean1) {
		LosUtil.TestResults testResults = this.testVisionAdjacent(int1, 0, int3, false, boolean1);
		if (testResults == LosUtil.TestResults.Blocked) {
			return LosUtil.TestResults.Blocked;
		} else {
			LosUtil.TestResults testResults2 = this.testVisionAdjacent(0, int2, int3, false, boolean1);
			if (testResults2 == LosUtil.TestResults.Blocked) {
				return LosUtil.TestResults.Blocked;
			} else {
				return testResults != LosUtil.TestResults.ClearThroughWindow && testResults2 != LosUtil.TestResults.ClearThroughWindow ? this.testVisionAdjacent(int1, int2, int3, false, boolean1) : LosUtil.TestResults.ClearThroughWindow;
			}
		}
	}

	boolean HasNoCharacters() {
		int int1;
		for (int1 = 0; int1 < this.MovingObjects.size(); ++int1) {
			if (this.MovingObjects.get(int1) instanceof IsoGameCharacter) {
				return false;
			}
		}

		for (int1 = 0; int1 < this.SpecialObjects.size(); ++int1) {
			if (this.SpecialObjects.get(int1) instanceof IsoBarricade) {
				return false;
			}
		}

		return true;
	}

	public IsoZombie getZombie() {
		for (int int1 = 0; int1 < this.MovingObjects.size(); ++int1) {
			if (this.MovingObjects.get(int1) instanceof IsoZombie) {
				return (IsoZombie)this.MovingObjects.get(int1);
			}
		}

		return null;
	}

	public IsoPlayer getPlayer() {
		for (int int1 = 0; int1 < this.MovingObjects.size(); ++int1) {
			if (this.MovingObjects.get(int1) instanceof IsoPlayer) {
				return (IsoPlayer)this.MovingObjects.get(int1);
			}
		}

		return null;
	}

	public static float getDarkStep() {
		return darkStep;
	}

	public static void setDarkStep(float float1) {
		darkStep = float1;
	}

	public static int getRecalcLightTime() {
		return RecalcLightTime;
	}

	public static void setRecalcLightTime(int int1) {
		RecalcLightTime = int1;
	}

	public static int getLightcache() {
		return lightcache;
	}

	public static void setLightcache(int int1) {
		lightcache = int1;
	}

	public boolean isCouldSee(int int1) {
		return this.lighting[int1].bCouldSee();
	}

	public void setCouldSee(int int1, boolean boolean1) {
		this.lighting[int1].bCouldSee(boolean1);
	}

	public boolean isCanSee(int int1) {
		return this.lighting[int1].bCanSee();
	}

	public void setCanSee(int int1, boolean boolean1) {
		this.lighting[int1].bCanSee(boolean1);
	}

	public IsoCell getCell() {
		return IsoWorld.instance.CurrentCell;
	}

	public IsoGridSquare getE() {
		return this.e;
	}

	public void setE(IsoGridSquare square) {
		this.e = square;
	}

	public ArrayList getLightInfluenceB() {
		return this.LightInfluenceB;
	}

	public void setLightInfluenceB(ArrayList arrayList) {
		this.LightInfluenceB = arrayList;
	}

	public ArrayList getLightInfluenceG() {
		return this.LightInfluenceG;
	}

	public void setLightInfluenceG(ArrayList arrayList) {
		this.LightInfluenceG = arrayList;
	}

	public ArrayList getLightInfluenceR() {
		return this.LightInfluenceR;
	}

	public void setLightInfluenceR(ArrayList arrayList) {
		this.LightInfluenceR = arrayList;
	}

	public ArrayList getStaticMovingObjects() {
		return this.StaticMovingObjects;
	}

	public ArrayList getMovingObjects() {
		return this.MovingObjects;
	}

	public IsoGridSquare getN() {
		return this.n;
	}

	public void setN(IsoGridSquare square) {
		this.n = square;
	}

	public PZArrayList getObjects() {
		return this.Objects;
	}

	public PropertyContainer getProperties() {
		return this.Properties;
	}

	public IsoRoom getRoom() {
		return this.roomID == -1 ? null : this.room;
	}

	public void setRoom(IsoRoom room) {
		this.room = room;
	}

	public IsoBuilding getBuilding() {
		IsoRoom room = this.getRoom();
		return room != null ? room.getBuilding() : null;
	}

	public IsoGridSquare getS() {
		return this.s;
	}

	public void setS(IsoGridSquare square) {
		this.s = square;
	}

	public ArrayList getSpecialObjects() {
		return this.SpecialObjects;
	}

	public IsoGridSquare getW() {
		return this.w;
	}

	public void setW(IsoGridSquare square) {
		this.w = square;
	}

	public float getLampostTotalR() {
		return this.lighting[0].lampostTotalR();
	}

	public void setLampostTotalR(float float1) {
		this.lighting[0].lampostTotalR(float1);
	}

	public float getLampostTotalG() {
		return this.lighting[0].lampostTotalG();
	}

	public void setLampostTotalG(float float1) {
		this.lighting[0].lampostTotalG(float1);
	}

	public float getLampostTotalB() {
		return this.lighting[0].lampostTotalB();
	}

	public void setLampostTotalB(float float1) {
		this.lighting[0].lampostTotalB(float1);
	}

	public boolean isSeen(int int1) {
		return this.lighting[int1].bSeen();
	}

	public void setIsSeen(int int1, boolean boolean1) {
		this.lighting[int1].bSeen(boolean1);
	}

	public float getDarkMulti(int int1) {
		return this.lighting[int1].darkMulti();
	}

	public void setDarkMulti(int int1, float float1) {
		this.lighting[int1].darkMulti(float1);
	}

	public float getTargetDarkMulti(int int1) {
		return this.lighting[int1].targetDarkMulti();
	}

	public void setTargetDarkMulti(int int1, float float1) {
		this.lighting[int1].targetDarkMulti(float1);
	}

	public void setX(int int1) {
		this.x = int1;
		this.CachedScreenValue = -1;
	}

	public void setY(int int1) {
		this.y = int1;
		this.CachedScreenValue = -1;
	}

	public void setZ(int int1) {
		this.z = int1;
		this.CachedScreenValue = -1;
	}

	public ArrayList getDeferedCharacters() {
		return this.DeferedCharacters;
	}

	public void addDeferredCharacter(IsoGameCharacter gameCharacter) {
		if (this.DeferredCharacterTick != this.getCell().DeferredCharacterTick) {
			if (!this.DeferedCharacters.isEmpty()) {
				this.DeferedCharacters.clear();
			}

			this.DeferredCharacterTick = this.getCell().DeferredCharacterTick;
		}

		this.DeferedCharacters.add(gameCharacter);
	}

	public boolean isCacheIsFree() {
		return this.CacheIsFree;
	}

	public void setCacheIsFree(boolean boolean1) {
		this.CacheIsFree = boolean1;
	}

	public boolean isCachedIsFree() {
		return this.CachedIsFree;
	}

	public void setCachedIsFree(boolean boolean1) {
		this.CachedIsFree = boolean1;
	}

	public static boolean isbDoSlowPathfinding() {
		return bDoSlowPathfinding;
	}

	public static void setbDoSlowPathfinding(boolean boolean1) {
		bDoSlowPathfinding = boolean1;
	}

	public boolean isSolidFloorCached() {
		return this.SolidFloorCached;
	}

	public void setSolidFloorCached(boolean boolean1) {
		this.SolidFloorCached = boolean1;
	}

	public boolean isSolidFloor() {
		return this.SolidFloor;
	}

	public void setSolidFloor(boolean boolean1) {
		this.SolidFloor = boolean1;
	}

	public static ColorInfo getDefColorInfo() {
		return defColorInfo;
	}

	public boolean isOutside() {
		return this.Properties.Is(IsoFlagType.exterior);
	}

	public boolean HasPushable() {
		int int1 = this.MovingObjects.size();
		for (int int2 = 0; int2 < int1; ++int2) {
			if (this.MovingObjects.get(int2) instanceof IsoPushableObject) {
				return true;
			}
		}

		return false;
	}

	public void setRoomID(int int1) {
		this.roomID = int1;
		if (int1 != -1) {
			this.getProperties().UnSet(IsoFlagType.exterior);
			this.room = this.chunk.getRoom(int1);
		}
	}

	public int getRoomID() {
		return this.roomID;
	}

	public boolean getCanSee(int int1) {
		return this.lighting[int1].bCanSee();
	}

	public boolean getSeen(int int1) {
		return this.lighting[int1].bSeen();
	}

	public IsoChunk getChunk() {
		return this.chunk;
	}

	public IsoObject getDoorOrWindow(boolean boolean1) {
		for (int int1 = this.SpecialObjects.size() - 1; int1 >= 0; --int1) {
			IsoObject object = (IsoObject)this.SpecialObjects.get(int1);
			if (object instanceof IsoDoor && ((IsoDoor)object).north == boolean1) {
				return object;
			}

			if (object instanceof IsoThumpable && ((IsoThumpable)object).north == boolean1 && (((IsoThumpable)object).isDoor() || ((IsoThumpable)object).isWindow())) {
				return object;
			}

			if (object instanceof IsoWindow && ((IsoWindow)object).north == boolean1) {
				return object;
			}
		}

		return null;
	}

	public IsoObject getDoorOrWindowOrWindowFrame(IsoDirections directions, boolean boolean1) {
		for (int int1 = this.Objects.size() - 1; int1 >= 0; --int1) {
			IsoObject object = (IsoObject)this.Objects.get(int1);
			IsoDoor door = (IsoDoor)Type.tryCastTo(object, IsoDoor.class);
			IsoThumpable thumpable = (IsoThumpable)Type.tryCastTo(object, IsoThumpable.class);
			IsoWindow window = (IsoWindow)Type.tryCastTo(object, IsoWindow.class);
			if (door != null && door.getSpriteEdge(boolean1) == directions) {
				return object;
			}

			if (thumpable != null && thumpable.getSpriteEdge(boolean1) == directions) {
				return object;
			}

			if (window != null) {
				if (window.north && directions == IsoDirections.N) {
					return object;
				}

				if (!window.north && directions == IsoDirections.W) {
					return object;
				}
			}

			if (IsoWindowFrame.isWindowFrame(object)) {
				if (IsoWindowFrame.isWindowFrame(object, true) && directions == IsoDirections.N) {
					return object;
				}

				if (IsoWindowFrame.isWindowFrame(object, false) && directions == IsoDirections.W) {
					return object;
				}
			}
		}

		return null;
	}

	public IsoObject getOpenDoor(IsoDirections directions) {
		for (int int1 = 0; int1 < this.SpecialObjects.size(); ++int1) {
			IsoObject object = (IsoObject)this.SpecialObjects.get(int1);
			IsoDoor door = (IsoDoor)Type.tryCastTo(object, IsoDoor.class);
			IsoThumpable thumpable = (IsoThumpable)Type.tryCastTo(object, IsoThumpable.class);
			if (door != null && door.open && door.getSpriteEdge(false) == directions) {
				return door;
			}

			if (thumpable != null && thumpable.open && thumpable.getSpriteEdge(false) == directions) {
				return thumpable;
			}
		}

		return null;
	}

	public void removeWorldObject(IsoWorldInventoryObject worldInventoryObject) {
		if (worldInventoryObject != null) {
			worldInventoryObject.removeFromWorld();
			worldInventoryObject.removeFromSquare();
		}
	}

	public void removeAllWorldObjects() {
		for (int int1 = 0; int1 < this.getWorldObjects().size(); ++int1) {
			IsoObject object = (IsoObject)this.getWorldObjects().get(int1);
			object.removeFromWorld();
			object.removeFromSquare();
			--int1;
		}
	}

	public ArrayList getWorldObjects() {
		return this.WorldObjects;
	}

	public KahluaTable getModData() {
		if (this.table == null) {
			this.table = LuaManager.platform.newTable();
		}

		return this.table;
	}

	public boolean hasModData() {
		return this.table != null && !this.table.isEmpty();
	}

	public ZomboidBitFlag getHasTypes() {
		return this.hasTypes;
	}

	public void setVertLight(int int1, int int2, int int3) {
		this.lighting[int3].lightverts(int1, int2);
	}

	public int getVertLight(int int1, int int2) {
		return this.lighting[int2].lightverts(int1);
	}

	public void setRainDrop(IsoRaindrop raindrop) {
		this.RainDrop = raindrop;
	}

	public IsoRaindrop getRainDrop() {
		return this.RainDrop;
	}

	public void setRainSplash(IsoRainSplash rainSplash) {
		this.RainSplash = rainSplash;
	}

	public IsoRainSplash getRainSplash() {
		return this.RainSplash;
	}

	public IsoMetaGrid.Zone getZone() {
		return this.zone;
	}

	public String getZoneType() {
		return this.zone != null ? this.zone.getType() : null;
	}

	public boolean isOverlayDone() {
		return this.overlayDone;
	}

	public void setOverlayDone(boolean boolean1) {
		this.overlayDone = boolean1;
	}

	public ErosionData.Square getErosionData() {
		if (this.erosion == null) {
			this.erosion = new ErosionData.Square();
		}

		return this.erosion;
	}

	public void disableErosion() {
		ErosionData.Square square = this.getErosionData();
		if (square != null && !square.doNothing) {
			square.doNothing = true;
		}
	}

	public void removeErosionObject(String string) {
		if (this.erosion != null) {
			if ("WallVines".equals(string)) {
				for (int int1 = 0; int1 < this.erosion.regions.size(); ++int1) {
					ErosionCategory.Data data = (ErosionCategory.Data)this.erosion.regions.get(int1);
					if (data.regionID == 2 && data.categoryID == 0) {
						this.erosion.regions.remove(int1);
						break;
					}
				}
			}
		}
	}

	public void syncIsoTrap(HandWeapon handWeapon) {
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.doPacket((short)110, byteBufferWriter);
		byteBufferWriter.putInt(this.getX());
		byteBufferWriter.putInt(this.getY());
		byteBufferWriter.putInt(this.getZ());
		try {
			handWeapon.saveWithSize(byteBufferWriter.bb, false);
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}

		GameClient.connection.endPacketImmediate();
	}

	public void drawCircleExplosion(int int1, IsoTrap trap, IsoTrap.ExplosionMode explosionMode) {
		if (int1 > 15) {
			int1 = 15;
		}

		for (int int2 = this.getX() - int1; int2 <= this.getX() + int1; ++int2) {
			for (int int3 = this.getY() - int1; int3 <= this.getY() + int1; ++int3) {
				if (!(IsoUtils.DistanceTo((float)int2 + 0.5F, (float)int3 + 0.5F, (float)this.getX() + 0.5F, (float)this.getY() + 0.5F) > (float)int1)) {
					LosUtil.TestResults testResults = LosUtil.lineClear(this.getCell(), (int)trap.getX(), (int)trap.getY(), (int)trap.getZ(), int2, int3, this.z, false);
					if (testResults != LosUtil.TestResults.Blocked && testResults != LosUtil.TestResults.ClearThroughClosedDoor) {
						IsoGridSquare square = this.getCell().getGridSquare(int2, int3, this.getZ());
						if (square != null) {
							if (explosionMode == IsoTrap.ExplosionMode.Smoke) {
								if (Rand.Next(2) == 0) {
									IsoFireManager.StartSmoke(this.getCell(), square, true, 40, 0);
								}

								square.smoke();
							}

							if (explosionMode == IsoTrap.ExplosionMode.Explosion) {
								if (trap.getExplosionPower() > 0 && Rand.Next(80 - trap.getExplosionPower()) <= 0) {
									square.Burn();
								}

								square.explosion(trap);
								if (trap.getExplosionPower() > 0 && Rand.Next(100 - trap.getExplosionPower()) == 0) {
									IsoFireManager.StartFire(this.getCell(), square, true, 20);
								}
							}

							if (explosionMode == IsoTrap.ExplosionMode.Fire && Rand.Next(100 - trap.getFirePower()) == 0) {
								IsoFireManager.StartFire(this.getCell(), square, true, 40);
							}

							if (explosionMode == IsoTrap.ExplosionMode.Sensor) {
								square.setTrapPositionX(this.getX());
								square.setTrapPositionY(this.getY());
								square.setTrapPositionZ(this.getZ());
							}
						}
					}
				}
			}
		}
	}

	public void explosion(IsoTrap trap) {
		for (int int1 = 0; int1 < this.getMovingObjects().size(); ++int1) {
			IsoMovingObject movingObject = (IsoMovingObject)this.getMovingObjects().get(int1);
			if (movingObject instanceof IsoGameCharacter) {
				int int2 = Math.min(trap.getExplosionPower(), 80);
				movingObject.Hit((HandWeapon)InventoryItemFactory.CreateItem("Base.Axe"), IsoWorld.instance.CurrentCell.getFakeZombieForHit(), Rand.Next((float)int2 / 30.0F, (float)int2 / 30.0F * 2.0F) + trap.getExtraDamage(), false, 1.0F);
				if (trap.getExplosionPower() > 0) {
					boolean boolean1 = !(movingObject instanceof IsoZombie);
					while (boolean1) {
						boolean1 = false;
						BodyPart bodyPart = ((IsoGameCharacter)movingObject).getBodyDamage().getBodyPart(BodyPartType.FromIndex(Rand.Next(15)));
						bodyPart.setBurned();
						if (Rand.Next((100 - int2) / 2) == 0) {
							boolean1 = true;
						}
					}
				}
			}
		}
	}

	public void smoke() {
		for (int int1 = 0; int1 < this.getMovingObjects().size(); ++int1) {
			IsoMovingObject movingObject = (IsoMovingObject)this.getMovingObjects().get(int1);
			if (movingObject instanceof IsoZombie) {
				((IsoZombie)movingObject).setTarget((IsoMovingObject)null);
				((IsoZombie)movingObject).changeState(ZombieIdleState.instance());
			}
		}
	}

	public void explodeTrap() {
		IsoGridSquare square = this.getCell().getGridSquare(this.getTrapPositionX(), this.getTrapPositionY(), this.getTrapPositionZ());
		if (square != null) {
			for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
				IsoObject object = (IsoObject)square.getObjects().get(int1);
				if (object instanceof IsoTrap) {
					IsoTrap trap = (IsoTrap)object;
					trap.triggerExplosion(false);
					IsoGridSquare square2 = null;
					int int2 = trap.getSensorRange();
					for (int int3 = square.getX() - int2; int3 <= square.getX() + int2; ++int3) {
						for (int int4 = square.getY() - int2; int4 <= square.getY() + int2; ++int4) {
							if (IsoUtils.DistanceTo((float)int3 + 0.5F, (float)int4 + 0.5F, (float)square.getX() + 0.5F, (float)square.getY() + 0.5F) <= (float)int2) {
								square2 = this.getCell().getGridSquare(int3, int4, this.getZ());
								if (square2 != null) {
									square2.setTrapPositionX(-1);
									square2.setTrapPositionY(-1);
									square2.setTrapPositionZ(-1);
								}
							}
						}
					}

					return;
				}
			}
		}
	}

	public int getTrapPositionX() {
		return this.trapPositionX;
	}

	public void setTrapPositionX(int int1) {
		this.trapPositionX = int1;
	}

	public int getTrapPositionY() {
		return this.trapPositionY;
	}

	public void setTrapPositionY(int int1) {
		this.trapPositionY = int1;
	}

	public int getTrapPositionZ() {
		return this.trapPositionZ;
	}

	public void setTrapPositionZ(int int1) {
		this.trapPositionZ = int1;
	}

	public boolean haveElectricity() {
		if ((this.chunk == null || !this.chunk.bLoaded) && this.haveElectricity) {
			return true;
		} else if (!SandboxOptions.getInstance().AllowExteriorGenerator.getValue() && this.Is(IsoFlagType.exterior)) {
			return false;
		} else {
			return this.chunk != null && this.chunk.isGeneratorPoweringSquare(this.x, this.y, this.z);
		}
	}

	public void setHaveElectricity(boolean boolean1) {
		if (!boolean1) {
			this.haveElectricity = false;
		}

		if (this.getObjects() != null) {
			for (int int1 = 0; int1 < this.getObjects().size(); ++int1) {
				if (this.getObjects().get(int1) instanceof IsoLightSwitch) {
					((IsoLightSwitch)this.getObjects().get(int1)).update();
				}
			}
		}
	}

	public IsoGenerator getGenerator() {
		if (this.getSpecialObjects() != null) {
			for (int int1 = 0; int1 < this.getSpecialObjects().size(); ++int1) {
				if (this.getSpecialObjects().get(int1) instanceof IsoGenerator) {
					return (IsoGenerator)this.getSpecialObjects().get(int1);
				}
			}
		}

		return null;
	}

	public void stopFire() {
		IsoFireManager.RemoveAllOn(this);
		this.getProperties().Set(IsoFlagType.burntOut);
		this.getProperties().UnSet(IsoFlagType.burning);
		this.burntOut = true;
	}

	public void transmitStopFire() {
		if (GameClient.bClient) {
			GameClient.sendStopFire(this);
		}
	}

	public long playSound(String string) {
		BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter((float)this.x + 0.5F, (float)this.y + 0.5F, (float)this.z);
		return baseSoundEmitter.playSound(string);
	}

	@Deprecated
	public long playSound(String string, boolean boolean1) {
		BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter((float)this.x + 0.5F, (float)this.y + 0.5F, (float)this.z);
		return baseSoundEmitter.playSound(string, boolean1);
	}

	public void FixStackableObjects() {
		IsoObject object = null;
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			IsoObject object2 = (IsoObject)this.Objects.get(int1);
			if (!(object2 instanceof IsoWorldInventoryObject) && object2.sprite != null) {
				PropertyContainer propertyContainer = object2.sprite.getProperties();
				if (propertyContainer.getStackReplaceTileOffset() != 0) {
					object2.sprite = IsoSprite.getSprite(IsoSpriteManager.instance, object2.sprite.ID + propertyContainer.getStackReplaceTileOffset());
					if (object2.sprite == null) {
						continue;
					}

					propertyContainer = object2.sprite.getProperties();
				}

				if (propertyContainer.isTable() || propertyContainer.isTableTop()) {
					float float1 = propertyContainer.isSurfaceOffset() ? (float)propertyContainer.getSurface() : 0.0F;
					if (object != null) {
						object2.setRenderYOffset(object.getRenderYOffset() + object.getSurfaceOffset() - float1);
					} else {
						object2.setRenderYOffset(0.0F - float1);
					}
				}

				if (propertyContainer.isTable()) {
					object = object2;
				}

				if (object2 instanceof IsoLightSwitch && propertyContainer.isTableTop() && object != null && !propertyContainer.Is("IgnoreSurfaceSnap")) {
					int int2 = PZMath.tryParseInt(propertyContainer.Val("Noffset"), 0);
					int int3 = PZMath.tryParseInt(propertyContainer.Val("Soffset"), 0);
					int int4 = PZMath.tryParseInt(propertyContainer.Val("Woffset"), 0);
					int int5 = PZMath.tryParseInt(propertyContainer.Val("Eoffset"), 0);
					String string = propertyContainer.Val("Facing");
					PropertyContainer propertyContainer2 = object.getProperties();
					String string2 = propertyContainer2.Val("Facing");
					if (!StringUtils.isNullOrWhitespace(string2) && !string2.equals(string)) {
						int int6 = 0;
						if ("N".equals(string2)) {
							if (int2 != 0) {
								int6 = int2;
							} else if (int3 != 0) {
								int6 = int3;
							}
						} else if ("S".equals(string2)) {
							if (int3 != 0) {
								int6 = int3;
							} else if (int2 != 0) {
								int6 = int2;
							}
						} else if ("W".equals(string2)) {
							if (int4 != 0) {
								int6 = int4;
							} else if (int5 != 0) {
								int6 = int5;
							}
						} else if ("E".equals(string2)) {
							if (int5 != 0) {
								int6 = int5;
							} else if (int4 != 0) {
								int6 = int4;
							}
						}

						if (int6 != 0) {
							IsoSprite sprite = IsoSpriteManager.instance.getSprite(object2.sprite.ID + int6);
							if (sprite != null) {
								object2.setSprite(sprite);
							}
						}
					}
				}
			}
		}
	}

	public BaseVehicle getVehicleContainer() {
		int int1 = (int)(((float)this.x - 4.0F) / 10.0F);
		int int2 = (int)(((float)this.y - 4.0F) / 10.0F);
		int int3 = (int)Math.ceil((double)(((float)this.x + 4.0F) / 10.0F));
		int int4 = (int)Math.ceil((double)(((float)this.y + 4.0F) / 10.0F));
		for (int int5 = int2; int5 < int4; ++int5) {
			for (int int6 = int1; int6 < int3; ++int6) {
				IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int6, int5) : IsoWorld.instance.CurrentCell.getChunk(int6, int5);
				if (chunk != null) {
					for (int int7 = 0; int7 < chunk.vehicles.size(); ++int7) {
						BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int7);
						if (baseVehicle.isIntersectingSquare(this.x, this.y, this.z)) {
							return baseVehicle;
						}
					}
				}
			}
		}

		return null;
	}

	public boolean isVehicleIntersecting() {
		int int1 = (int)(((float)this.x - 4.0F) / 10.0F);
		int int2 = (int)(((float)this.y - 4.0F) / 10.0F);
		int int3 = (int)Math.ceil((double)(((float)this.x + 4.0F) / 10.0F));
		int int4 = (int)Math.ceil((double)(((float)this.y + 4.0F) / 10.0F));
		for (int int5 = int2; int5 < int4; ++int5) {
			for (int int6 = int1; int6 < int3; ++int6) {
				IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int6, int5) : IsoWorld.instance.CurrentCell.getChunk(int6, int5);
				if (chunk != null) {
					for (int int7 = 0; int7 < chunk.vehicles.size(); ++int7) {
						BaseVehicle baseVehicle = (BaseVehicle)chunk.vehicles.get(int7);
						if (baseVehicle.isIntersectingSquare(this.x, this.y, this.z)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public IsoCompost getCompost() {
		if (this.getSpecialObjects() != null) {
			for (int int1 = 0; int1 < this.getSpecialObjects().size(); ++int1) {
				if (this.getSpecialObjects().get(int1) instanceof IsoCompost) {
					return (IsoCompost)this.getSpecialObjects().get(int1);
				}
			}
		}

		return null;
	}

	public void setIsoWorldRegion(IsoWorldRegion worldRegion) {
		this.hasSetIsoWorldRegion = worldRegion != null;
		this.isoWorldRegion = worldRegion;
	}

	public IWorldRegion getIsoWorldRegion() {
		if (GameServer.bServer) {
			return IsoRegions.getIsoWorldRegion(this.x, this.y, this.z);
		} else {
			if (!this.hasSetIsoWorldRegion) {
				this.isoWorldRegion = IsoRegions.getIsoWorldRegion(this.x, this.y, this.z);
				this.hasSetIsoWorldRegion = true;
			}

			return this.isoWorldRegion;
		}
	}

	public void ResetIsoWorldRegion() {
		this.isoWorldRegion = null;
		this.hasSetIsoWorldRegion = false;
	}

	public boolean isInARoom() {
		return this.getRoom() != null || this.getIsoWorldRegion() != null && this.getIsoWorldRegion().isPlayerRoom();
	}

	public int getWallType() {
		int int1 = 0;
		if (this.getProperties().Is(IsoFlagType.WallN)) {
			int1 |= 1;
		}

		if (this.getProperties().Is(IsoFlagType.WallW)) {
			int1 |= 4;
		}

		if (this.getProperties().Is(IsoFlagType.WallNW)) {
			int1 |= 5;
		}

		IsoGridSquare square = this.nav[IsoDirections.E.index()];
		if (square != null && (square.getProperties().Is(IsoFlagType.WallW) || square.getProperties().Is(IsoFlagType.WallNW))) {
			int1 |= 8;
		}

		IsoGridSquare square2 = this.nav[IsoDirections.S.index()];
		if (square2 != null && (square2.getProperties().Is(IsoFlagType.WallN) || square2.getProperties().Is(IsoFlagType.WallNW))) {
			int1 |= 2;
		}

		return int1;
	}

	public int getPuddlesDir() {
		byte byte1 = IsoGridSquare.PuddlesDirection.PUDDLES_DIR_ALL;
		if (this.isInARoom()) {
			return IsoGridSquare.PuddlesDirection.PUDDLES_DIR_NONE;
		} else {
			for (int int1 = 0; int1 < this.getObjects().size(); ++int1) {
				IsoObject object = (IsoObject)this.getObjects().get(int1);
				if (object.AttachedAnimSprite != null) {
					for (int int2 = 0; int2 < object.AttachedAnimSprite.size(); ++int2) {
						IsoSprite sprite = ((IsoSpriteInstance)object.AttachedAnimSprite.get(int2)).parentSprite;
						if (sprite.name != null) {
							if (sprite.name.equals("street_trafficlines_01_2") || sprite.name.equals("street_trafficlines_01_6") || sprite.name.equals("street_trafficlines_01_22") || sprite.name.equals("street_trafficlines_01_32")) {
								byte1 = IsoGridSquare.PuddlesDirection.PUDDLES_DIR_NW;
							}

							if (sprite.name.equals("street_trafficlines_01_4") || sprite.name.equals("street_trafficlines_01_0") || sprite.name.equals("street_trafficlines_01_16")) {
								byte1 = IsoGridSquare.PuddlesDirection.PUDDLES_DIR_NE;
							}
						}
					}
				}
			}

			return byte1;
		}
	}

	public boolean haveFire() {
		int int1 = this.Objects.size();
		IsoObject[] objectArray = (IsoObject[])this.Objects.getElements();
		for (int int2 = 0; int2 < int1; ++int2) {
			IsoObject object = objectArray[int2];
			if (object instanceof IsoFire) {
				return true;
			}
		}

		return false;
	}

	public IsoBuilding getRoofHideBuilding() {
		return this.roofHideBuilding;
	}

	public IsoGridSquare getAdjacentSquare(IsoDirections directions) {
		return this.nav[directions.index()];
	}

	public IsoGridSquare getAdjacentPathSquare(IsoDirections directions) {
		switch (directions) {
		case NW: 
			return this.nw;
		
		case N: 
			return this.n;
		
		case NE: 
			return this.ne;
		
		case W: 
			return this.w;
		
		case E: 
			return this.e;
		
		case SW: 
			return this.sw;
		
		case S: 
			return this.s;
		
		case SE: 
			return this.se;
		
		default: 
			return null;
		
		}
	}

	public float getApparentZ(float float1, float float2) {
		float1 = PZMath.clamp(float1, 0.0F, 1.0F);
		float2 = PZMath.clamp(float2, 0.0F, 1.0F);
		if (this.Has(IsoObjectType.stairsTN)) {
			return (float)this.getZ() + PZMath.lerp(0.6666F, 1.0F, 1.0F - float2);
		} else if (this.Has(IsoObjectType.stairsTW)) {
			return (float)this.getZ() + PZMath.lerp(0.6666F, 1.0F, 1.0F - float1);
		} else if (this.Has(IsoObjectType.stairsMN)) {
			return (float)this.getZ() + PZMath.lerp(0.3333F, 0.6666F, 1.0F - float2);
		} else if (this.Has(IsoObjectType.stairsMW)) {
			return (float)this.getZ() + PZMath.lerp(0.3333F, 0.6666F, 1.0F - float1);
		} else if (this.Has(IsoObjectType.stairsBN)) {
			return (float)this.getZ() + PZMath.lerp(0.01F, 0.3333F, 1.0F - float2);
		} else {
			return this.Has(IsoObjectType.stairsBW) ? (float)this.getZ() + PZMath.lerp(0.01F, 0.3333F, 1.0F - float1) : (float)this.getZ();
		}
	}

	public float getTotalWeightOfItemsOnFloor() {
		float float1 = 0.0F;
		for (int int1 = 0; int1 < this.WorldObjects.size(); ++int1) {
			InventoryItem inventoryItem = ((IsoWorldInventoryObject)this.WorldObjects.get(int1)).getItem();
			if (inventoryItem != null) {
				float1 += inventoryItem.getUnequippedWeight();
			}
		}

		return float1;
	}

	public boolean getCollideMatrix(int int1, int int2, int int3) {
		return getMatrixBit(this.collideMatrix, int1 + 1, int2 + 1, int3 + 1);
	}

	public boolean getPathMatrix(int int1, int int2, int int3) {
		return getMatrixBit(this.pathMatrix, int1 + 1, int2 + 1, int3 + 1);
	}

	public boolean getVisionMatrix(int int1, int int2, int int3) {
		return getMatrixBit(this.visionMatrix, int1 + 1, int2 + 1, int3 + 1);
	}

	public boolean hasFlies() {
		return this.bHasFlies;
	}

	public void setHasFlies(boolean boolean1) {
		this.bHasFlies = boolean1;
	}

	public interface ILighting {

		int lightverts(int int1);

		float lampostTotalR();

		float lampostTotalG();

		float lampostTotalB();

		boolean bSeen();

		boolean bCanSee();

		boolean bCouldSee();

		float darkMulti();

		float targetDarkMulti();

		ColorInfo lightInfo();

		void lightverts(int int1, int int2);

		void lampostTotalR(float float1);

		void lampostTotalG(float float1);

		void lampostTotalB(float float1);

		void bSeen(boolean boolean1);

		void bCanSee(boolean boolean1);

		void bCouldSee(boolean boolean1);

		void darkMulti(float float1);

		void targetDarkMulti(float float1);

		int resultLightCount();

		IsoGridSquare.ResultLight getResultLight(int int1);

		void reset();
	}

	public static final class CircleStencilShader extends Shader {
		public static final IsoGridSquare.CircleStencilShader instance = new IsoGridSquare.CircleStencilShader();
		public int a_wallShadeColor = -1;

		public CircleStencilShader() {
			super("CircleStencil");
		}

		protected void onCompileSuccess(ShaderProgram shaderProgram) {
			this.Start();
			this.a_wallShadeColor = GL20.glGetAttribLocation(this.getID(), "a_wallShadeColor");
			shaderProgram.setSamplerUnit("texture", 0);
			shaderProgram.setSamplerUnit("CutawayStencil", 1);
			this.End();
		}
	}

	public static final class Lighting implements IsoGridSquare.ILighting {
		private final int[] lightverts = new int[8];
		private float lampostTotalR = 0.0F;
		private float lampostTotalG = 0.0F;
		private float lampostTotalB = 0.0F;
		private boolean bSeen;
		private boolean bCanSee;
		private boolean bCouldSee;
		private float darkMulti;
		private float targetDarkMulti;
		private final ColorInfo lightInfo = new ColorInfo();

		public int lightverts(int int1) {
			return this.lightverts[int1];
		}

		public float lampostTotalR() {
			return this.lampostTotalR;
		}

		public float lampostTotalG() {
			return this.lampostTotalG;
		}

		public float lampostTotalB() {
			return this.lampostTotalB;
		}

		public boolean bSeen() {
			return this.bSeen;
		}

		public boolean bCanSee() {
			return this.bCanSee;
		}

		public boolean bCouldSee() {
			return this.bCouldSee;
		}

		public float darkMulti() {
			return this.darkMulti;
		}

		public float targetDarkMulti() {
			return this.targetDarkMulti;
		}

		public ColorInfo lightInfo() {
			return this.lightInfo;
		}

		public void lightverts(int int1, int int2) {
			this.lightverts[int1] = int2;
		}

		public void lampostTotalR(float float1) {
			this.lampostTotalR = float1;
		}

		public void lampostTotalG(float float1) {
			this.lampostTotalG = float1;
		}

		public void lampostTotalB(float float1) {
			this.lampostTotalB = float1;
		}

		public void bSeen(boolean boolean1) {
			this.bSeen = boolean1;
		}

		public void bCanSee(boolean boolean1) {
			this.bCanSee = boolean1;
		}

		public void bCouldSee(boolean boolean1) {
			this.bCouldSee = boolean1;
		}

		public void darkMulti(float float1) {
			this.darkMulti = float1;
		}

		public void targetDarkMulti(float float1) {
			this.targetDarkMulti = float1;
		}

		public int resultLightCount() {
			return 0;
		}

		public IsoGridSquare.ResultLight getResultLight(int int1) {
			return null;
		}

		public void reset() {
			this.lampostTotalR = 0.0F;
			this.lampostTotalG = 0.0F;
			this.lampostTotalB = 0.0F;
			this.bSeen = false;
			this.bCouldSee = false;
			this.bCanSee = false;
			this.targetDarkMulti = 0.0F;
			this.darkMulti = 0.0F;
			this.lightInfo.r = 0.0F;
			this.lightInfo.g = 0.0F;
			this.lightInfo.b = 0.0F;
			this.lightInfo.a = 1.0F;
		}
	}

	public static class CellGetSquare implements IsoGridSquare.GetSquare {

		public IsoGridSquare getGridSquare(int int1, int int2, int int3) {
			return IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		}
	}

	public interface GetSquare {

		IsoGridSquare getGridSquare(int int1, int int2, int int3);
	}

	private static final class s_performance {
		static final PerformanceProfileProbe renderFloor = new PerformanceProfileProbe("IsoGridSquare.renderFloor", false);
	}

	public static class PuddlesDirection {
		public static byte PUDDLES_DIR_NONE = 1;
		public static byte PUDDLES_DIR_NE = 2;
		public static byte PUDDLES_DIR_NW = 4;
		public static byte PUDDLES_DIR_ALL = 8;
	}

	public static final class NoCircleStencilShader {
		public static final IsoGridSquare.NoCircleStencilShader instance = new IsoGridSquare.NoCircleStencilShader();
		private ShaderProgram shaderProgram;
		public int ShaderID = -1;
		public int a_wallShadeColor = -1;

		private void initShader() {
			this.shaderProgram = ShaderProgram.createShaderProgram("NoCircleStencil", false, true);
			if (this.shaderProgram.isCompiled()) {
				this.ShaderID = this.shaderProgram.getShaderID();
				this.a_wallShadeColor = GL20.glGetAttribLocation(this.ShaderID, "a_wallShadeColor");
			}
		}
	}

	private interface RenderWallCallback {

		void invoke(Texture texture, float float1, float float2);
	}

	public static final class ResultLight {
		public int id;
		public int x;
		public int y;
		public int z;
		public int radius;
		public float r;
		public float g;
		public float b;
		public static final int RLF_NONE = 0;
		public static final int RLF_ROOMLIGHT = 1;
		public static final int RLF_TORCH = 2;
		public int flags;

		public IsoGridSquare.ResultLight copyFrom(IsoGridSquare.ResultLight resultLight) {
			this.id = resultLight.id;
			this.x = resultLight.x;
			this.y = resultLight.y;
			this.z = resultLight.z;
			this.radius = resultLight.radius;
			this.r = resultLight.r;
			this.g = resultLight.g;
			this.b = resultLight.b;
			this.flags = resultLight.flags;
			return this;
		}
	}
}
