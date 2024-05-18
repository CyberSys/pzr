package zombie.iso;

import gnu.trove.set.hash.TIntHashSet;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.MapCollisionData;
import zombie.PathfindManager;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SoundManager;
import zombie.WorldSoundManager;
import zombie.ZomboidBitFlag;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.astar.INode;
import zombie.ai.astar.SearchData;
import zombie.ai.states.ZombieStandState;
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
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.RenderSettings;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.utils.OnceEvery;
import zombie.debug.DebugLog;
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
import zombie.iso.areas.isoregion.IsoRegion;
import zombie.iso.areas.isoregion.MasterRegion;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoLightSwitch;
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
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.meta.Meta;
import zombie.network.ChunkRevisions;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerLOS;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.list.PZArrayList;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;


public class IsoGridSquare implements Comparable,INode {
	private boolean hasTree;
	private ArrayList LightInfluenceB;
	private ArrayList LightInfluenceG;
	private ArrayList LightInfluenceR;
	public boolean bDirty = true;
	public IsoGridSquare[] nav = new IsoGridSquare[8];
	public boolean[][][] collideMatrix = new boolean[3][3][3];
	public boolean[][][] pathMatrix = new boolean[3][3][3];
	public boolean[][][] visionMatrix = new boolean[3][3][3];
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
	private MasterRegion masterRegion;
	private boolean hasSetMasterRegion = false;
	public int ObjectsSyncCount = 0;
	public IsoBuilding roofHideBuilding;
	public IsoGridSquare.ILighting[] lighting = new IsoGridSquare.ILighting[4];
	public int x;
	public int y;
	public int z;
	public int CachedScreenValue = -1;
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
	private ArrayList DeferedCharacters = new ArrayList();
	private int DeferredCharacterTick = -1;
	private ArrayList StaticMovingObjects = new ArrayList(0);
	private ArrayList MovingObjects = new ArrayList(0);
	protected PZArrayList Objects = new PZArrayList(IsoObject.class, 2);
	private ArrayList WorldObjects = new ArrayList();
	ZomboidBitFlag hasTypes;
	private PropertyContainer Properties;
	private ArrayList SpecialObjects;
	public boolean haveRoof;
	private boolean burntOut;
	private IsoGridOcclusionData OcclusionDataCache;
	public static final ArrayDeque isoGridSquareCache = new ArrayDeque(20000);
	public static final TIntHashSet isoGridSquareSet = new TIntHashSet(20000);
	private boolean overlayDone;
	private KahluaTable table;
	private int trapPositionX;
	private int trapPositionY;
	private int trapPositionZ;
	private boolean haveElectricity;
	public static int gridSquareCacheEmptyTimer = 0;
	private static int FireRecalc = 30;
	private static float darkStep = 0.06F;
	private static BlockInfo blockInfo = new BlockInfo();
	public static int RecalcLightTime = 0;
	private static int lightcache = 0;
	private static IsoSprite ZombieHall;
	public static ArrayList choices = new ArrayList();
	public boolean isSolidFloorCache;
	public boolean isExteriorCache;
	public boolean isVegitationCache;
	public static boolean DEBUG_SAVE = true;
	public int hourLastSeen;
	static IsoGridSquare lastLoaded = null;
	public SearchData[] searchData;
	public static int IDMax = -1;
	static int col = -1;
	static int path = -1;
	static int pathdoor = -1;
	static int vision = -1;
	public long hashCodeObjects;
	static Color tr = new Color(1, 1, 1, 1);
	static Color tl = new Color(1, 1, 1, 1);
	static Color br = new Color(1, 1, 1, 1);
	static Color bl = new Color(1, 1, 1, 1);
	static Color interp1 = new Color(1, 1, 1, 1);
	static Color interp2 = new Color(1, 1, 1, 1);
	static Color finalCol = new Color(1, 1, 1, 1);
	public static IsoGridSquare.CellGetSquare cellGetSquare = new IsoGridSquare.CellGetSquare();
	public boolean propertiesDirty;
	public static boolean UseSlowCollision = false;
	private static boolean bDoSlowPathfinding = false;
	private static Comparator comp = new Comparator(){
    
    public int compare(IsoMovingObject var1, IsoMovingObject var2) {
        return var1.compareToY(var2);
    }
};
	public static boolean isOnScreenLast = false;
	private float splashX;
	private float splashY;
	private float splashFrame;
	private int splashFrameNum;
	private final ColorInfo[] lightInfo;
	private static Texture texWhite;
	private static ColorInfo defColorInfo = new ColorInfo();
	private static ColorInfo blackColorInfo = new ColorInfo();
	static int colu = 0;
	static int coll = 0;
	static int colr = 0;
	static int colu2 = 0;
	static int coll2 = 0;
	static int colr2 = 0;
	public static boolean CircleStencil = false;
	static OnceEvery every = new OnceEvery(0.025F);
	public static float rmod = 0.0F;
	public static float gmod = 0.0F;
	public static float bmod = 0.0F;
	static float rmodLT = 0.0F;
	static float gmodLT = 0.0F;
	static float bmodLT = 0.0F;
	static ArrayList POVCharacters = new ArrayList();
	static Vector2 tempo = new Vector2();
	static Vector2 tempo2 = new Vector2();
	public static long Checksum = 0L;
	public static long TotalChecksum = 0L;
	private IsoRaindrop RainDrop;
	private IsoRainSplash RainSplash;
	private ErosionData.Square erosion;
	public long revision;

	public static boolean DoChecksumCheck(String string, String string2) {
		String string3 = "";
		try {
			string3 = IsoObject.getMD5Checksum(string);
			if (!string3.equals(string2)) {
				return false;
			}
		} catch (Exception exception) {
			string3 = "";
			try {
				string3 = IsoObject.getMD5Checksum("D:/Dropbox/Zomboid/zombie/build/classes/" + string);
			} catch (Exception exception2) {
				return false;
			}
		}

		return string3.equals(string2);
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
		this.masterRegion = null;
		this.hasSetMasterRegion = false;
		for (int int1 = 0; int1 < 8; ++int1) {
			this.nav[int1] = null;
		}
	}

	public boolean isSomethingTo(IsoGridSquare square) {
		return this.isWallTo(square) || this.isWindowTo(square) || this.isDoorTo(square);
	}

	public boolean isWallTo(IsoGridSquare square) {
		if (square == null) {
			return false;
		} else if (square.x > this.x && square.Properties.Is(IsoFlagType.collideW) && !square.Properties.Is(IsoFlagType.WindowW)) {
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
	}

	public boolean isWindowTo(IsoGridSquare square) {
		if (square == null) {
			return false;
		} else if (square.x > this.x && square.Properties.Is(IsoFlagType.windowW)) {
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
		if (square == null) {
			return false;
		} else if (square.x > this.x && square.Properties.Is(IsoFlagType.doorW)) {
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
					return !window.open || window.isBarricaded();
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
		this.bDirty = true;
		this.room = null;
		this.w = null;
		this.nw = null;
		this.sw = null;
		this.s = null;
		this.n = null;
		this.ne = null;
		this.se = null;
		this.e = null;
		this.masterRegion = null;
		this.hasSetMasterRegion = false;
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

		this.revision = 0L;
		if (this.OcclusionDataCache != null) {
			this.OcclusionDataCache.Reset();
		}

		this.roofHideBuilding = null;
		synchronized (isoGridSquareCache) {
			if (!isoGridSquareSet.contains(this.ID) && isoGridSquareCache.size() < 20000) {
				isoGridSquareCache.push(this);
				isoGridSquareSet.add(this.ID);
			} else if (isoGridSquareCache.size() >= 20000) {
				boolean boolean1 = false;
			}
		}
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

	private void renderAttachedSpritesWithNoWallLighting(int int1, IsoObject object, ColorInfo colorInfo, int int2) {
		if (object.AttachedAnimSprite != null && !object.AttachedAnimSprite.isEmpty()) {
			boolean boolean1 = false;
			for (int int3 = 0; int3 < object.AttachedAnimSprite.size(); ++int3) {
				IsoSpriteInstance spriteInstance = (IsoSpriteInstance)object.AttachedAnimSprite.get(int3);
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
				IsoSpriteInstance spriteInstance2;
				int int4;
				if (CircleStencil) {
					IndieGL.enableStencilTest();
					IndieGL.enableAlphaTest();
					IndieGL.glAlphaFunc(516, 0.02F);
					IndieGL.glStencilFunc(517, 128, 128);
					for (int4 = 0; int4 < object.AttachedAnimSprite.size(); ++int4) {
						spriteInstance2 = (IsoSpriteInstance)object.AttachedAnimSprite.get(int4);
						if (spriteInstance2.parentSprite != null && spriteInstance2.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
							defColorInfo.a = spriteInstance2.alpha;
							spriteInstance2.render(object, (float)this.x, (float)this.y, (float)this.z, object.dir, object.offsetX, object.offsetY + object.getRenderYOffset() * (float)Core.TileScale, defColorInfo);
						}
					}

					IndieGL.glStencilFunc(519, 255, 255);
				} else {
					for (int4 = 0; int4 < object.AttachedAnimSprite.size(); ++int4) {
						spriteInstance2 = (IsoSpriteInstance)object.AttachedAnimSprite.get(int4);
						if (spriteInstance2.parentSprite != null && spriteInstance2.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
							defColorInfo.a = spriteInstance2.alpha;
							spriteInstance2.render(object, (float)this.x, (float)this.y, (float)this.z, object.dir, object.offsetX, object.offsetY + object.getRenderYOffset() * (float)Core.TileScale, defColorInfo);
							spriteInstance2.update();
						}
					}
				}

				defColorInfo.r = 1.0F;
				defColorInfo.g = 1.0F;
				defColorInfo.b = 1.0F;
				defColorInfo.a = float1;
			}
		}
	}

	public int DoWallLightingN(IsoObject object, int int1, int int2, int int3, int int4) {
		if (this.z != (int)IsoCamera.CamCharacter.z) {
			CircleStencil = false;
		}

		if (IsoCamera.CamCharacter.current != null && this.room == IsoCamera.CamCharacter.current.getRoom() && IsoCamera.CamCharacter.current.getRoom() != null && this.room != null) {
		}

		if (object.sprite.getType() == IsoObjectType.doorFrN || object instanceof IsoWindow) {
			CircleStencil = false;
		}

		boolean boolean1 = object.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
		if (int1 == 0 && !boolean1) {
			int1 = this.getCell().getStencilValue(this.x, this.y, this.z);
		}

		int int5 = IsoCamera.frameState.playerIndex;
		ColorInfo colorInfo = this.lightInfo[int5];
		colu = this.getVertLight(0, int5);
		coll = this.getVertLight(1, int5);
		colu2 = this.getVertLight(4, int5);
		coll2 = this.getVertLight(5, int5);
		IndieGL.End();
		if (CircleStencil) {
			IndieGL.enableStencilTest();
			IndieGL.enableAlphaTest();
			IndieGL.glAlphaFunc(516, 0.02F);
			IndieGL.glStencilFunc(517, 128, 128);
			IndieGL.glStencilOp(7680, 7680, 7680);
			object.render((float)this.x, (float)this.y, (float)this.z, boolean1 ? colorInfo : defColorInfo, true, !boolean1);
			IndieGL.glStencilFunc(519, int1, 255);
			object.alpha[int5] = 0.02F;
			if (object.getProperties() != null && object.getProperties().Is(IsoFlagType.HoppableN)) {
				object.alpha[int5] = 0.25F;
			}
		} else {
			IndieGL.enableAlphaTest();
			IndieGL.glAlphaFunc(516, 0.0F);
			IndieGL.glStencilFunc(519, int1, 127);
		}

		if (!boolean1) {
			IndieGL.glStencilOp(7680, 7680, 7681);
		}

		if (CircleStencil) {
			float float1 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex());
			if (!this.lighting[int5].bSeen()) {
				float1 = 0.0F;
			}

			defColorInfo.r = float1 * rmod;
			defColorInfo.g = float1 * gmod;
			defColorInfo.b = float1 * bmod;
			object.render((float)this.x, (float)this.y, (float)this.z, boolean1 ? colorInfo : defColorInfo, true, !boolean1);
			defColorInfo.r = 1.0F;
			defColorInfo.g = 1.0F;
			defColorInfo.b = 1.0F;
		} else {
			object.render((float)this.x, (float)this.y, (float)this.z, boolean1 ? colorInfo : defColorInfo, true, !boolean1);
		}

		object.alpha[int5] = 1.0F;
		IndieGL.End();
		if (boolean1) {
			IndieGL.glStencilFunc(519, 1, 255);
			IndieGL.glStencilOp(7680, 7680, 7680);
			return int1;
		} else {
			IndieGL.glColorMask(true, true, true, true);
			IndieGL.glAlphaFunc(516, 0.0F);
			if (CircleStencil) {
				IndieGL.glStencilFunc(514, int1, 255);
			} else {
				IndieGL.glStencilFunc(514, int1, 127);
			}

			IndieGL.glStencilOp(7680, 7680, 7680);
			if (texWhite == null) {
				texWhite = Texture.getSharedTexture("media/ui/white.png");
			}

			Texture texture = texWhite;
			float float2 = 0.0F;
			float float3 = 0.0F;
			float float4 = 0.0F;
			float float5 = IsoUtils.XToScreenInt(this.x + (int)float2, this.y + (int)float3, this.z + (int)float4, 0);
			float float6 = IsoUtils.YToScreenInt(this.x + (int)float2, this.y + (int)float3, this.z + (int)float4, 0);
			float5 = (float)((int)float5);
			float6 = (float)((int)float6);
			float5 -= (float)((int)IsoCamera.frameState.OffX);
			float6 -= (float)((int)IsoCamera.frameState.OffY);
			if ((object.highlightFlags & 1) == 0 && texture != null) {
				texture.renderwalln((int)float5, (int)float6, 64 * Core.TileScale, 32 * Core.TileScale, colu, coll, colu2, coll2);
			}

			IndieGL.End();
			IndieGL.glStencilFunc(519, 1, 255);
			IndieGL.glStencilOp(7680, 7680, 7680);
			this.renderAttachedSpritesWithNoWallLighting(int5, object, colorInfo, int1);
			this.getCell().setStencilValue(this.x, this.y, this.z, int1);
			return int1 + 1;
		}
	}

	public int DoWallLightingNW(int int1, int int2, IsoObject object, int int3, boolean boolean1, int int4) {
		if (this.z != (int)IsoCamera.CamCharacter.z) {
			boolean1 = false;
		}

		if (IsoCamera.CamCharacter.current != null && this.room == IsoCamera.CamCharacter.current.getRoom() && IsoCamera.CamCharacter.current.getRoom() != null && this.room != null) {
		}

		boolean boolean2 = object.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
		if (int3 == 0 && !boolean2) {
			int3 = this.getCell().getStencilValue(this.x, this.y, this.z);
		}

		int int5 = IsoCamera.frameState.playerIndex;
		ColorInfo colorInfo = this.lightInfo[int5];
		colu = this.getVertLight(0, int5);
		coll = this.getVertLight(3, int5);
		colr = this.getVertLight(1, int5);
		colu2 = this.getVertLight(4, int5);
		coll2 = this.getVertLight(7, int5);
		colr2 = this.getVertLight(5, int5);
		IndieGL.End();
		if (boolean1) {
			IndieGL.enableStencilTest();
			IndieGL.enableAlphaTest();
			IndieGL.glAlphaFunc(516, 0.02F);
			IndieGL.glStencilFunc(517, 128, 128);
			IndieGL.glStencilOp(7680, 7680, 7680);
			object.render((float)this.x, (float)this.y, (float)this.z, boolean2 ? colorInfo : defColorInfo, true, !boolean2);
			object.alpha[int5] = 0.02F;
			if (object.getProperties() != null && object.getProperties().Is(IsoFlagType.HoppableN)) {
				object.alpha[int5] = 0.25F;
			}

			IndieGL.glStencilFunc(519, int3, 255);
		} else {
			IndieGL.enableAlphaTest();
			IndieGL.glAlphaFunc(516, 0.02F);
			IndieGL.glStencilFunc(519, int3, 127);
		}

		if (!boolean2) {
			IndieGL.glStencilOp(7680, 7680, 7681);
		}

		if (boolean1) {
			float float1 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex());
			if (!this.lighting[int5].bSeen()) {
				float1 = 0.0F;
			}

			defColorInfo.r = float1 * rmod;
			defColorInfo.g = float1 * gmod;
			defColorInfo.b = float1 * bmod;
			object.render((float)this.x, (float)this.y, (float)this.z, boolean2 ? colorInfo : defColorInfo, true, !boolean2);
			defColorInfo.r = 1.0F;
			defColorInfo.g = 1.0F;
			defColorInfo.b = 1.0F;
		} else {
			object.render((float)this.x, (float)this.y, (float)this.z, boolean2 ? colorInfo : defColorInfo, true, !boolean2);
		}

		object.alpha[int5] = 1.0F;
		IndieGL.End();
		if (boolean2) {
			IndieGL.glStencilFunc(519, 1, 255);
			IndieGL.glStencilOp(7680, 7680, 7680);
			return int3;
		} else {
			IndieGL.glColorMask(true, true, true, true);
			IndieGL.glAlphaFunc(516, 0.0F);
			if (boolean1) {
				IndieGL.glStencilFunc(514, int3, 255);
			} else {
				IndieGL.glStencilFunc(514, int3, 127);
			}

			IndieGL.glStencilOp(7680, 7680, 7680);
			if (texWhite == null) {
				texWhite = Texture.getSharedTexture("media/ui/white.png");
			}

			Texture texture = texWhite;
			float float2 = 0.0F;
			float float3 = 0.0F;
			float float4 = 0.0F;
			float float5 = IsoUtils.XToScreenInt(this.x + (int)float2, this.y + (int)float3, this.z + (int)float4, 0);
			float float6 = IsoUtils.YToScreenInt(this.x + (int)float2, this.y + (int)float3, this.z + (int)float4, 0);
			float5 = (float)((int)float5);
			float6 = (float)((int)float6);
			float5 -= (float)((int)IsoCamera.frameState.OffX);
			float6 -= (float)((int)IsoCamera.frameState.OffY);
			if ((object.highlightFlags & 1) == 0 && texture != null) {
				texture.renderwallnw((int)float5, (int)float6, 64 * Core.TileScale, 32 * Core.TileScale, colu, coll, colu2, coll2, colr, colr2);
			}

			IndieGL.End();
			IndieGL.glStencilFunc(519, 1, 255);
			IndieGL.glStencilOp(7680, 7680, 7680);
			this.renderAttachedSpritesWithNoWallLighting(int5, object, colorInfo, int3);
			this.getCell().setStencilValue(this.x, this.y, this.z, int3);
			return int3 + 1;
		}
	}

	public int DoWallLightingW(IsoObject object, int int1, int int2, int int3, int int4) {
		if (this.z != (int)IsoCamera.CamCharacter.z) {
			CircleStencil = false;
		}

		if (IsoCamera.CamCharacter.current != null && this.room == IsoCamera.CamCharacter.current.getRoom() && IsoCamera.CamCharacter.current.getRoom() != null && this.room != null) {
		}

		if (object.sprite.getType() == IsoObjectType.doorFrW || object instanceof IsoWindow) {
			CircleStencil = false;
		}

		boolean boolean1 = object.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
		if (int1 == 0 && !boolean1) {
			int1 = this.getCell().getStencilValue(this.x, this.y, this.z);
		}

		int int5 = IsoCamera.frameState.playerIndex;
		ColorInfo colorInfo = this.lightInfo[int5];
		colu = this.getVertLight(0, int5);
		coll = this.getVertLight(3, int5);
		colu2 = this.getVertLight(4, int5);
		coll2 = this.getVertLight(7, int5);
		if (CircleStencil) {
			IndieGL.enableStencilTest();
			IndieGL.enableAlphaTest();
			IndieGL.glAlphaFunc(516, 0.02F);
			IndieGL.glStencilFunc(517, 128, 128);
			IndieGL.glStencilOp(7680, 7680, 7680);
			object.render((float)this.x, (float)this.y, (float)this.z, boolean1 ? colorInfo : defColorInfo, true, !boolean1);
			IndieGL.glStencilFunc(519, int1, 255);
			object.alpha[int5] = 0.02F;
			if (object.getProperties() != null && object.getProperties().Is(IsoFlagType.HoppableW)) {
				object.alpha[int5] = 0.25F;
			}
		} else {
			IndieGL.enableAlphaTest();
			IndieGL.glAlphaFunc(516, 0.0F);
			IndieGL.glStencilFunc(519, int1, 127);
		}

		if (!boolean1) {
			IndieGL.glStencilOp(7680, 7680, 7681);
		}

		if (CircleStencil) {
			float float1 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex());
			if (!this.lighting[int5].bSeen()) {
				float1 = 0.0F;
			}

			defColorInfo.r = float1 * rmod;
			defColorInfo.g = float1 * gmod;
			defColorInfo.b = float1 * bmod;
			object.render((float)this.x, (float)this.y, (float)this.z, boolean1 ? colorInfo : defColorInfo, true, !boolean1);
			defColorInfo.r = 1.0F;
			defColorInfo.g = 1.0F;
			defColorInfo.b = 1.0F;
		} else {
			object.render((float)this.x, (float)this.y, (float)this.z, boolean1 ? colorInfo : defColorInfo, true, !boolean1);
		}

		object.alpha[int5] = 1.0F;
		IndieGL.End();
		if (boolean1) {
			IndieGL.glStencilFunc(519, 1, 255);
			IndieGL.glStencilOp(7680, 7680, 7680);
			return int1;
		} else {
			IndieGL.glColorMask(true, true, true, true);
			IndieGL.glAlphaFunc(516, 0.0F);
			if (CircleStencil) {
				IndieGL.glStencilFunc(514, int1, 255);
			} else {
				IndieGL.glStencilFunc(514, int1, 127);
			}

			IndieGL.glStencilOp(7680, 7680, 7680);
			if (texWhite == null) {
				texWhite = Texture.getSharedTexture("media/ui/white.png");
			}

			Texture texture = texWhite;
			float float2 = 0.0F;
			float float3 = 0.0F;
			float float4 = 0.0F;
			float float5 = IsoUtils.XToScreenInt(this.x + (int)float2, this.y + (int)float3, this.z + (int)float4, 0);
			float float6 = IsoUtils.YToScreenInt(this.x + (int)float2, this.y + (int)float3, this.z + (int)float4, 0);
			float5 = (float)((int)float5);
			float6 = (float)((int)float6);
			float5 -= (float)((int)IsoCamera.frameState.OffX);
			float6 -= (float)((int)IsoCamera.frameState.OffY);
			if ((object.highlightFlags & 1) == 0 && texture != null) {
				texture.renderwallw((int)float5, (int)float6, 64 * Core.TileScale, 32 * Core.TileScale, colu, coll, colu2, coll2);
			}

			IndieGL.End();
			IndieGL.glStencilFunc(519, 1, 255);
			IndieGL.glStencilOp(7680, 7680, 7680);
			this.renderAttachedSpritesWithNoWallLighting(int5, object, colorInfo, int1);
			this.getCell().setStencilValue(this.x, this.y, this.z, int1);
			return int1 + 1;
		}
	}

	public int DoRoofLighting(IsoObject object, int int1, int int2, int int3, int int4) {
		IndieGL.End();
		++int1;
		if (CircleStencil) {
			IndieGL.enableStencilTest();
			IndieGL.enableAlphaTest();
			IndieGL.glAlphaFunc(516, 0.1F);
			IndieGL.glStencilFunc(517, 128, 128);
			IndieGL.glStencilOp(7680, 7680, 7680);
			object.render((float)this.x, (float)this.y, (float)this.z, defColorInfo, false);
			IndieGL.glStencilFunc(519, int1, 255);
			object.alpha[IsoPlayer.getPlayerIndex()] = 0.02F;
		} else {
			IndieGL.enableAlphaTest();
			IndieGL.glAlphaFunc(516, 0.02F);
			IndieGL.glStencilFunc(519, int1, 127);
		}

		IndieGL.glStencilOp(7680, 7680, 7681);
		float float1;
		if (CircleStencil) {
			float1 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex());
			defColorInfo.r = float1 * rmod;
			defColorInfo.g = float1 * gmod;
			defColorInfo.b = float1 * bmod;
			object.render((float)this.x, (float)this.y, (float)this.z, defColorInfo, true);
			defColorInfo.r = 1.0F;
			defColorInfo.g = 1.0F;
			defColorInfo.b = 1.0F;
		} else {
			object.render((float)this.x, (float)this.y, (float)this.z, defColorInfo, true);
		}

		object.alpha[IsoPlayer.getPlayerIndex()] = 1.0F;
		IndieGL.End();
		IndieGL.glColorMask(true, true, true, true);
		IndieGL.glAlphaFunc(516, 0.0F);
		if (CircleStencil) {
			IndieGL.glStencilFunc(514, int1, 255);
		} else {
			IndieGL.glStencilFunc(514, int1, 127);
		}

		IndieGL.glStencilOp(7680, 7680, 7680);
		IndieGL.glBlendFunc(0, 768);
		if (texWhite == null) {
			texWhite = Texture.getSharedTexture("media/ui/white.png");
		}

		float1 = 0.0F;
		float float2 = 0.0F;
		float float3 = 0.0F;
		float float4 = IsoUtils.XToScreenInt(this.x + (int)float1, this.y + (int)float2, this.z + (int)float3, 0);
		float float5 = IsoUtils.YToScreenInt(this.x + (int)float1, this.y + (int)float2, this.z + (int)float3, 0);
		float4 = (float)((int)float4);
		float5 = (float)((int)float5);
		float4 -= (float)((int)IsoCamera.getOffX());
		float5 -= (float)((int)IsoCamera.getOffY());
		float5 += 128.0F;
		float5 += 128.0F;
		float5 += 128.0F;
		IndieGL.End();
		IndieGL.glStencilFunc(519, 1, 255);
		IndieGL.glStencilOp(7680, 7680, 7680);
		IndieGL.glBlendFunc(770, 771);
		return int1;
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

	public boolean isZone(String string) {
		return IsoWorld.instance.CurrentCell.IsZone(string, this.x, this.y);
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

	public boolean HasTree() {
		return this.hasTree;
	}

	private void fudgeShadowsToAlpha(IsoObject object, Color color) {
		float float1 = 1.0F - object.alpha[IsoPlayer.getPlayerIndex()];
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

	private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
		objectOutputStream.defaultWriteObject();
	}

	private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		objectInputStream.defaultReadObject();
	}

	public boolean shouldSave() {
		return !this.Objects.isEmpty();
	}

	public void setDirty() {
		this.bDirty = true;
	}

	public void save(ByteBuffer byteBuffer, ObjectOutputStream objectOutputStream) throws IOException {
		if (GameWindow.DEBUG_SAVE) {
			GameWindow.WriteString(byteBuffer, "Number of objects");
		}

		byteBuffer.putShort((short)0);
		byteBuffer.putInt(this.Objects.size());
		int int1;
		int int2;
		for (int1 = 0; int1 < this.Objects.size(); ++int1) {
			int2 = byteBuffer.position();
			if (DEBUG_SAVE) {
				byteBuffer.putInt(0);
			}

			if (this.SpecialObjects.contains(this.Objects.get(int1))) {
				byteBuffer.put((byte)1);
			} else {
				byteBuffer.put((byte)0);
			}

			if (this.WorldObjects.contains(this.Objects.get(int1))) {
				byteBuffer.put((byte)1);
			} else {
				byteBuffer.put((byte)0);
			}

			if (GameWindow.DEBUG_SAVE) {
				GameWindow.WriteStringUTF(byteBuffer, ((IsoObject)this.Objects.get(int1)).getClass().getName());
			}

			((IsoObject)this.Objects.get(int1)).save(byteBuffer);
			if (DEBUG_SAVE) {
				int int3 = byteBuffer.position();
				byteBuffer.position(int2);
				byteBuffer.putInt(int3 - int2);
				byteBuffer.position(int3);
			}
		}

		if (DEBUG_SAVE) {
			byteBuffer.put((byte)67);
			byteBuffer.put((byte)82);
			byteBuffer.put((byte)80);
			byteBuffer.put((byte)83);
		}

		int1 = 0;
		for (int2 = 0; int2 < this.StaticMovingObjects.size(); ++int2) {
			if (this.StaticMovingObjects.get(int2) instanceof IsoDeadBody) {
				++int1;
			}
		}

		if (GameWindow.DEBUG_SAVE) {
			GameWindow.WriteString(byteBuffer, "Number of bodies");
		}

		byteBuffer.putInt(int1);
		for (int2 = 0; int2 < this.StaticMovingObjects.size(); ++int2) {
			IsoMovingObject movingObject = (IsoMovingObject)this.StaticMovingObjects.get(int2);
			if (movingObject instanceof IsoDeadBody) {
				if (GameWindow.DEBUG_SAVE) {
					GameWindow.WriteStringUTF(byteBuffer, movingObject.getClass().getName());
				}

				movingObject.save(byteBuffer);
			}
		}

		if (this.table != null && !this.table.isEmpty()) {
			byteBuffer.put((byte)1);
			this.table.save(byteBuffer);
		} else {
			byteBuffer.put((byte)0);
		}

		this.bDirty = false;
		byte byte1 = 0;
		if (this.isOverlayDone()) {
			byte1 = (byte)(byte1 | 1);
		}

		if (this.haveRoof) {
			byte1 = (byte)(byte1 | 2);
		}

		if (this.burntOut) {
			byte1 = (byte)(byte1 | 4);
		}

		byteBuffer.put(byte1);
		this.getErosionData().save(byteBuffer);
		if (this.getTrapPositionX() > 0) {
			byteBuffer.put((byte)1);
			byteBuffer.putInt(this.getTrapPositionX());
			byteBuffer.putInt(this.getTrapPositionY());
			byteBuffer.putInt(this.getTrapPositionZ());
		} else {
			byteBuffer.put((byte)0);
		}

		byteBuffer.put((byte)(this.haveElectricity() ? 1 : 0));
		byteBuffer.put((byte)(this.haveSheetRope ? 1 : 0));
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
			GameClient.connection.endPacketImmediate();
			this.clientModify();
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

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		this.hourLastSeen = byteBuffer.getShort();
		int int2 = byteBuffer.getInt();
		int int3;
		for (int3 = 0; int3 < int2; ++int3) {
			int int4 = byteBuffer.position();
			int int5 = 0;
			if (DEBUG_SAVE) {
				if (int1 >= 126) {
					int5 = byteBuffer.getInt();
				} else {
					int5 = byteBuffer.getShort();
				}
			}

			boolean boolean1 = byteBuffer.get() == 1;
			boolean boolean2 = byteBuffer.get() == 1;
			IsoObject object = null;
			String string;
			if (GameWindow.DEBUG_SAVE) {
				string = GameWindow.ReadStringUTF(byteBuffer);
				DebugLog.log(string);
			}

			object = IsoObject.factoryFromFileInput(this.getCell(), byteBuffer);
			int int6;
			if (object == null) {
				if (DEBUG_SAVE) {
					int6 = byteBuffer.position();
					if (int6 - int4 != int5) {
						DebugLog.log("***** Object loaded size " + (int6 - int4) + " != saved size " + int5);
					}
				}
			} else {
				object.square = this;
				try {
					object.load(byteBuffer, int1);
				} catch (Exception exception) {
					this.debugPrintGridSquare();
					if (lastLoaded != null) {
						lastLoaded.debugPrintGridSquare();
					}

					throw new RuntimeException(exception);
				}

				if (DEBUG_SAVE) {
					int6 = byteBuffer.position();
					if (int6 - int4 != int5) {
						DebugLog.log("***** Object loaded size " + (int6 - int4) + " != saved size " + int5);
					}
				}

				if (object instanceof IsoWorldInventoryObject) {
					if (((IsoWorldInventoryObject)object).getItem() == null) {
						continue;
					}

					string = ((IsoWorldInventoryObject)object).getItem().getFullType();
					Item item = ScriptManager.instance.FindItem(string);
					if (item != null && item.getObsolete() || (GameServer.bServer || GameClient.bClient) && ((IsoWorldInventoryObject)object).dropTime > -1.0 && ServerOptions.instance.HoursForWorldItemRemoval.getValue() > 0.0 && (ServerOptions.instance.WorldItemRemovalList.getValue().contains(string) && !ServerOptions.instance.ItemRemovalListBlacklistToggle.getValue() || !ServerOptions.instance.WorldItemRemovalList.getValue().contains(string) && ServerOptions.instance.ItemRemovalListBlacklistToggle.getValue()) && GameTime.instance.getWorldAgeHours() > ((IsoWorldInventoryObject)object).dropTime + ServerOptions.instance.HoursForWorldItemRemoval.getValue()) {
						continue;
					}
				}

				if (!(object instanceof IsoWindow) || object.getSprite() == null || !"walls_special_01_8".equals(object.getSprite().getName()) && !"walls_special_01_9".equals(object.getSprite().getName())) {
					this.Objects.add(object);
					if (boolean1) {
						this.SpecialObjects.add(object);
					}

					if (boolean2) {
						this.WorldObjects.add((IsoWorldInventoryObject)object);
						object.square.chunk.recalcHashCodeObjects();
					}
				}
			}
		}

		byte byte1;
		if (DEBUG_SAVE) {
			byte1 = byteBuffer.get();
			byte byte2 = byteBuffer.get();
			byte byte3 = byteBuffer.get();
			byte byte4 = byteBuffer.get();
			if (byte1 != 67 || byte2 != 82 || byte3 != 80 || byte4 != 83) {
				DebugLog.log("***** Expected CRPS here");
			}
		}

		int2 = byteBuffer.getInt();
		for (int3 = 0; int3 < int2; ++int3) {
			IsoMovingObject movingObject = null;
			if (GameWindow.DEBUG_SAVE) {
				String string2 = GameWindow.ReadStringUTF(byteBuffer);
				DebugLog.log(string2);
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
					movingObject.load(byteBuffer, int1);
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

		if (byteBuffer.get() != 0) {
			if (this.table == null) {
				this.table = LuaManager.platform.newTable();
			}

			this.table.load(byteBuffer, int1);
		}

		if (int1 >= 34) {
			if (int1 < 39) {
				this.setOverlayDone(byteBuffer.get() == 1);
				this.haveRoof = byteBuffer.get() == 1;
			} else {
				byte1 = byteBuffer.get();
				this.setOverlayDone((byte1 & 1) != 0);
				this.haveRoof = (byte1 & 2) != 0;
				this.burntOut = (byte1 & 4) != 0;
			}
		}

		if (int1 >= 45) {
			this.getErosionData().load(byteBuffer, int1);
		}

		if (int1 >= 62) {
			if (byteBuffer.get() == 1) {
				this.setTrapPositionX(byteBuffer.getInt());
				this.setTrapPositionY(byteBuffer.getInt());
				this.setTrapPositionZ(byteBuffer.getInt());
			}

			if (GameClient.bClient) {
				byteBuffer.get();
			} else {
				this.haveElectricity = byteBuffer.get() == 1;
			}
		}

		if (int1 >= 108) {
			this.haveSheetRope = byteBuffer.get() == 1;
		}

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
		} else {
			IsoGridSquare square = this.nav[IsoDirections.S.index()];
			if (square != null && square.getWindow(true) != null) {
				return true;
			} else {
				IsoGridSquare square2 = this.nav[IsoDirections.E.index()];
				return square2 != null && square2.getWindow(false) != null;
			}
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

	private IsoObject getSpecialWall(boolean boolean1) {
		for (int int1 = this.SpecialObjects.size() - 1; int1 >= 0; --int1) {
			IsoObject object = (IsoObject)this.SpecialObjects.get(int1);
			if (object instanceof IsoThumpable) {
				IsoThumpable thumpable = (IsoThumpable)object;
				if (thumpable.isStairs() || !thumpable.isThumpable() && !thumpable.isWindow() || thumpable.isCanPassThrough() || thumpable.isDoor() && thumpable.open || thumpable.isBlockAllTheSquare()) {
					continue;
				}

				if (boolean1 == thumpable.north) {
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

					String string = object.getSprite().getName().split("_")[0] + "_" + object.getSprite().getName().split("_")[1];
					int2 += 40;
					object.setSprite(IsoWorld.instance.spriteManager.getSprite(string + "_" + int2));
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
				if (!thumpable.isStairs() && thumpable.isThumpable() && !thumpable.isCanPassThrough() && thumpable.isBlockAllTheSquare()) {
					if (thumpable.getProperties().Is(IsoFlagType.solidtrans)) {
						if (this.getWindow(true) != null || this.getWindow(false) != null) {
							return null;
						}

						IsoGridSquare square = this.nav[IsoDirections.S.index()];
						if (square != null && square.getWindow(true) != null) {
							return null;
						}

						IsoGridSquare square2 = this.nav[IsoDirections.E.index()];
						if (square2 != null && square2.getWindow(false) != null) {
							return null;
						}
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

	public static IsoGridSquare getNew(IsoCell cell, SliceY sliceY, int int1, int int2, int int3) {
		IsoGridSquare square = null;
		synchronized (isoGridSquareCache) {
			if (isoGridSquareCache.isEmpty()) {
				return new IsoGridSquare(cell, sliceY, int1, int2, int3);
			}

			square = (IsoGridSquare)isoGridSquareCache.pop();
			isoGridSquareSet.remove(square.ID);
		}
		square.x = int1;
		square.y = int2;
		square.z = int3;
		square.CachedScreenValue = -1;
		int int4;
		for (int4 = 0; int4 < 4; ++int4) {
			if (square.lighting[int4] != null) {
				square.lighting[int4].setPos(int1, int2, int3);
			}
		}

		col = 0;
		path = 0;
		pathdoor = 0;
		vision = 0;
		for (int4 = 0; int4 < 3; ++int4) {
			for (int int5 = 0; int5 < 3; ++int5) {
				for (int int6 = 0; int6 < 3; ++int6) {
					square.collideMatrix[int4][int5][int6] = true;
					square.pathMatrix[int4][int5][int6] = true;
					square.visionMatrix[int4][int5][int6] = false;
				}
			}
		}

		return square;
	}

	public long getHashCodeObjects() {
		this.recalcHashCodeObjects();
		return this.hashCodeObjects;
	}

	public int getHashCodeObjectsInt() {
		this.recalcHashCodeObjects();
		return (int)this.hashCodeObjects;
	}

	public void recalcHashCodeObjects() {
		long long1 = 0L;
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			long1 += ((IsoObject)this.Objects.get(int1)).customHashCode();
		}

		this.hashCodeObjects = long1;
	}

	public int hashCode() {
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

		this.bDirty = false;
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
		if (ZombieHall == null) {
			ZombieHall = new IsoSprite(this.getCell().SpriteManager);
			ZombieHall.LoadFramesPalette("Zombie", "walk", 4, (String)"Zombie_palette10");
		}

		this.ID = ++IDMax;
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.CachedScreenValue = -1;
		col = 0;
		path = 0;
		pathdoor = 0;
		vision = 0;
		int int4;
		for (int4 = 0; int4 < 3; ++int4) {
			for (int int5 = 0; int5 < 3; ++int5) {
				for (int int6 = 0; int6 < 3; ++int6) {
					this.collideMatrix[int4][int5][int6] = true;
					this.pathMatrix[int4][int5][int6] = true;
					this.visionMatrix[int4][int5][int6] = false;
				}
			}
		}

		for (int4 = 0; int4 < 4; ++int4) {
			if (GameServer.bServer) {
				if (int4 == 0) {
					this.lighting[int4] = new ServerLOS.ServerLighting();
				}
			} else if (LightingJNI.init && LightingThread.instance.jniLighting) {
				this.lighting[int4] = new LightingJNI.JNILighting(int4, int1, int2, int3);
			} else {
				this.lighting[int4] = new IsoGridSquare.Lighting();
			}
		}
	}

	public void init(IsoCell cell, SliceY sliceY, int int1, int int2, int int3) {
		if (ZombieHall == null) {
			ZombieHall = new IsoSprite(this.getCell().SpriteManager);
			ZombieHall.LoadFramesPalette("Zombie", "walk", 4, (String)"Zombie_palette10");
		}

		sliceY.Squares.setValue(int1, int3, this);
		this.x = int1;
		this.y = int2;
		this.z = int3;
		this.CachedScreenValue = -1;
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

	public int compareTo(Object object) {
		Thread thread = Thread.currentThread();
		IsoGridSquare square = (IsoGridSquare)object;
		int int1 = 0;
		if (thread instanceof PathfindManager.PathfindThread) {
			int1 = ((PathfindManager.PathfindThread)thread).ID;
		}

		float float1 = this.searchData[int1].heuristic + this.searchData[int1].cost;
		float float2 = square.searchData[int1].heuristic + square.searchData[int1].cost;
		if (float1 < float2) {
			return -1;
		} else {
			return float1 > float2 ? 1 : 0;
		}
	}

	public int setParent(int int1, int int2, IsoGridSquare square) {
		this.getSearchData(int1).parent = square;
		short short1 = this.searchData[int1].depth = (short)(square.getSearchData(int1).depth + 1);
		return short1;
	}

	public SearchData getSearchData(int int1) {
		return this.searchData[int1];
	}

	IsoObject getWall() {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			if (((IsoObject)this.Objects.get(int1)).sprite.cutW || ((IsoObject)this.Objects.get(int1)).sprite.cutN) {
				return (IsoObject)this.Objects.get(int1);
			}
		}

		return null;
	}

	public IsoObject getWall(boolean boolean1) {
		for (int int1 = 0; int1 < this.Objects.size(); ++int1) {
			if (((IsoObject)this.Objects.get(int1)).sprite.cutN && boolean1 || ((IsoObject)this.Objects.get(int1)).sprite.cutW && !boolean1) {
				return (IsoObject)this.Objects.get(int1);
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

	void AddWoodWall(boolean boolean1, String string) {
		int int1 = 48;
		if (boolean1) {
			++int1;
		}

		if (string.equals("DoorFrame")) {
			int1 += 10;
		}

		if (string.equals("WindowFrame")) {
			int1 += 8;
		}

		String string2 = "TileWalls_" + int1;
		boolean boolean2 = false;
		int int2 = 0;
		int int3 = 0;
		IsoGridSquare square;
		if (boolean1) {
			square = this.getCell().getGridSquare(this.x + 1, this.y - 1, this.z);
			if (square != null && square.Properties.Is(IsoFlagType.cutW)) {
				boolean2 = true;
				int2 = this.x + 1;
				int3 = this.y;
			}
		} else {
			square = this.getCell().getGridSquare(this.x - 1, this.y + 1, this.z);
			if (square != null && square.Properties.Is(IsoFlagType.cutN)) {
				boolean2 = true;
				int2 = this.x;
				int3 = this.y + 1;
			}
		}

		IsoObject object = new IsoObject(this.getCell(), this, string2);
		if (boolean1) {
			if (!string.equals("DoorFrame") && !string.equals("WindowFrame")) {
				object.sprite.getProperties().Set(IsoFlagType.collideN, "");
			}

			object.sprite.getProperties().Set(IsoFlagType.cutN, "");
			if (string.equals("WindowFrame")) {
				object.sprite.getProperties().Set(IsoFlagType.transparentN, "");
			}
		}

		if (!boolean1) {
			if (!string.equals("DoorFrame") && !string.equals("WindowFrame")) {
				object.sprite.getProperties().Set(IsoFlagType.collideW, "");
			}

			object.sprite.getProperties().Set(IsoFlagType.cutW, "");
			if (string.equals("WindowFrame")) {
				object.sprite.getProperties().Set(IsoFlagType.transparentW, "");
			}
		}

		if (boolean2) {
			IsoGridSquare square2 = this.getCell().getGridSquare(int2, int3, this.z);
			if (square2 == null) {
				square2 = new IsoGridSquare(this.getCell(), (SliceY)null, int2, int3, this.z);
				this.getCell().ConnectNewSquare(square2, true);
			}

			IsoObject object2 = new IsoObject(this.getCell(), square2, "TileWalls_51");
			object2.sprite.getProperties().Set(IsoFlagType.cutN, "");
			object2.sprite.getProperties().Set(IsoFlagType.cutW, "");
			square2.Objects.add(0, object2);
			square2.RecalcProperties();
		}

		this.Objects.add(0, object);
		if (string.equals("DoorFrame")) {
			if (!boolean1) {
				string2 = "TileFrames_14";
			} else {
				string2 = "TileFrames_15";
			}

			object = new IsoObject(this.getCell(), this, string2);
			if (boolean1) {
				object.setType(IsoObjectType.doorFrN);
			}

			if (!boolean1) {
				object.setType(IsoObjectType.doorFrW);
			}

			this.Objects.add(1, object);
		}

		this.RecalcAllWithNeighbours(true);
		if (!boolean1 && string.equals("Wall")) {
			this.Properties.UnSet(IsoFlagType.transparentW);
		} else if (string.equals("Wall")) {
			this.Properties.UnSet(IsoFlagType.transparentN);
		}
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
		IsoRegion.setPreviousFlags(this);
		IsoObject object = new IsoObject(this.getCell(), this, string);
		int int1;
		for (int1 = 0; int1 < this.getObjects().size(); ++int1) {
			IsoObject object2 = (IsoObject)this.getObjects().get(int1);
			IsoSprite sprite = object2.sprite;
			if (sprite != null && (sprite.getProperties().Is(IsoFlagType.solidfloor) || sprite.getProperties().Is(IsoFlagType.noStart) || sprite.getProperties().Is(IsoFlagType.vegitation) && object2.getType() != IsoObjectType.tree || sprite.getName() != null && sprite.getName().startsWith("blends_grassoverlays"))) {
				this.transmitRemoveItemFromSquare(object2);
				--int1;
			}
		}

		object.sprite.getProperties().Set(IsoFlagType.solidfloor, "");
		this.getObjects().add(object);
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
		if (this.z > 0) {
			IsoGridSquare square = this.getCell().getGridSquare(this.x, this.y, this.z - 1);
			if (square != null) {
				square.RecalcAllWithNeighbours(true);
			}
		}

		this.setCachedIsFree(false);
		PolygonalMap2.instance.squareChanged(this);
		IsoGridOcclusionData.SquareChanged();
		IsoRegion.squareChanged(this);
		return object;
	}

	public IsoThumpable AddStairs(boolean boolean1, int int1, String string, String string2, KahluaTable kahluaTable) {
		IsoRegion.setPreviousFlags(this);
		this.EnsureSurroundNotNull();
		boolean boolean2 = !this.TreatAsSolidFloor();
		IsoGridSquare square = this.getCell().getGridSquare(this.x, this.y, this.z - 1);
		if (square != null && square.HasStairs()) {
			boolean2 = false;
		}

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
				thumpable.sprite.getProperties().Set(boolean1 ? IsoFlagType.cutN : IsoFlagType.cutW, "");
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
				thumpable.sprite.getProperties().Set(boolean1 ? IsoFlagType.cutN : IsoFlagType.cutW, "");
			}
		}

		this.Objects.add(thumpable);
		this.SpecialObjects.add(thumpable);
		this.Properties.Clear();
		int int2;
		for (int2 = 0; int2 < this.Objects.size(); ++int2) {
			IsoObject object = (IsoObject)this.Objects.get(int2);
			if (object.sprite != null) {
				this.Properties.AddProperties(object.sprite.getProperties());
			}
		}

		if (boolean2 && int1 == 2) {
			int2 = this.z - 1;
			IsoGridSquare square2 = this.getCell().getGridSquare(this.x, this.y, int2);
			if (square2 == null) {
				square2 = new IsoGridSquare(this.getCell(), (SliceY)null, this.x, this.y, int2);
				this.getCell().ConnectNewSquare(square2, true);
			}

			while (int2 >= 0) {
				IsoThumpable thumpable2 = new IsoThumpable(this.getCell(), square2, string2, boolean1, kahluaTable);
				thumpable2.sprite.getProperties().Set(IsoFlagType.solidtrans, "");
				thumpable2.sprite.getProperties().Set(boolean1 ? IsoFlagType.cutN : IsoFlagType.cutW, "");
				square2.Objects.add(thumpable2);
				square2.SpecialObjects.add(thumpable2);
				square2.RecalcAllWithNeighbours(true);
				if (square2.TreatAsSolidFloor()) {
					break;
				}

				--int2;
				if (this.getCell().getGridSquare(square2.x, square2.y, int2) == null) {
					square2 = new IsoGridSquare(this.getCell(), (SliceY)null, square2.x, square2.y, int2);
					this.getCell().ConnectNewSquare(square2, true);
				} else {
					square2 = this.getCell().getGridSquare(square2.x, square2.y, int2);
				}
			}
		}

		if (int1 == 2) {
			IsoGridSquare square3 = null;
			this.getCell().getStairsNodes().add(this.ID);
			if (boolean1) {
				if (IsoWorld.instance.isValidSquare(this.x, this.y - 1, this.z + 1)) {
					square3 = this.getCell().getGridSquare(this.x, this.y - 1, this.z + 1);
					if (square3 == null) {
						square3 = new IsoGridSquare(this.getCell(), (SliceY)null, this.x, this.y - 1, this.z + 1);
						this.getCell().ConnectNewSquare(square3, false);
					}

					if (!square3.Properties.Is(IsoFlagType.solidfloor)) {
						square3.addFloor("carpentry_02_57");
					}
				}
			} else if (IsoWorld.instance.isValidSquare(this.x - 1, this.y, this.z + 1)) {
				square3 = this.getCell().getGridSquare(this.x - 1, this.y, this.z + 1);
				if (square3 == null) {
					square3 = new IsoGridSquare(this.getCell(), (SliceY)null, this.x - 1, this.y, this.z + 1);
					this.getCell().ConnectNewSquare(square3, false);
				}

				if (!square3.Properties.Is(IsoFlagType.solidfloor)) {
					square3.addFloor("carpentry_02_57");
				}
			}

			square3.getModData().rawset("ConnectedToStairs" + boolean1, true);
			square3 = this.getCell().getGridSquare(this.x, this.y, this.z + 1);
			if (square3 == null) {
				square3 = new IsoGridSquare(this.getCell(), (SliceY)null, this.x, this.y, this.z + 1);
				this.getCell().ConnectNewSquare(square3, false);
			}
		}

		for (int2 = this.getX() - 1; int2 <= this.getX() + 1; ++int2) {
			for (int int3 = this.getY() - 1; int3 <= this.getY() + 1; ++int3) {
				for (int int4 = this.getZ() - 1; int4 <= this.getZ() + 1; ++int4) {
					if (IsoWorld.instance.isValidSquare(int2, int3, int4)) {
						IsoGridSquare square4 = this.getCell().getGridSquare(int2, int3, int4);
						if (square4 == null) {
							square4 = new IsoGridSquare(this.getCell(), (SliceY)null, int2, int3, int4);
							this.getCell().ConnectNewSquare(square4, false);
						}

						square4.ReCalculateCollide(this);
						square4.ReCalculateVisionBlocked(this);
						square4.ReCalculatePathFind(this);
						this.ReCalculateCollide(square4);
						this.ReCalculatePathFind(square4);
						this.ReCalculateVisionBlocked(square4);
						square4.CachedIsFree = false;
					}
				}
			}
		}

		this.RecalcAllWithNeighbours(true);
		MapCollisionData.instance.squareChanged(this);
		PolygonalMap2.instance.squareChanged(this);
		IsoRegion.squareChanged(this);
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
								this.ReCalculateAll(square, getSquare);
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

	public IsoGridSquare getNode() {
		return this.getCell().getPathMap().getNode(this.x, this.y, this.z);
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
				IsoSprite sprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)string, 0);
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
		IsoRegion.setPreviousFlags(this);
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
						object2.sx = 0;
						object2.sy = 0;
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
		IsoRegion.squareChanged(this, true);
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
			IsoRegion.setPreviousFlags(this);
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
			IsoRegion.squareChanged(this);
		}
	}

	public void AddTileObject(IsoObject object) {
		this.AddTileObject(object, -1);
	}

	public void AddTileObject(IsoObject object, int int1) {
		if (object != null) {
			IsoRegion.setPreviousFlags(this);
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
			IsoRegion.squareChanged(this);
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
				this.clientModify();
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
			SharedDescriptors.Descriptor descriptor = SharedDescriptors.pickRandomDescriptor();
			IsoZombie zombie;
			if (descriptor == null) {
				zombie = new IsoZombie(IsoWorld.instance.CurrentCell);
			} else {
				zombie = new IsoZombie(IsoWorld.instance.CurrentCell, descriptor.desc, descriptor.palette);
			}

			zombie.setDir(IsoDirections.fromIndex(Rand.Next(8)));
			zombie.angle.set(zombie.dir.ToVector());
			zombie.setFakeDead(false);
			zombie.setHealth(0.0F);
			zombie.upKillCount = false;
			zombie.setX((float)this.x + float1);
			zombie.setY((float)this.y + float2);
			zombie.setZ((float)this.z);
			zombie.square = this;
			zombie.current = this;
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
			}

			deadBody2.setX((float)this.x + float1);
			deadBody2.setY((float)this.y + float2);
			deadBody2.setZ((float)this.z);
			deadBody2.square = this;
			if (GameClient.bClient && deadBody2.sprite == null) {
				IsoZombie zombie2 = new IsoZombie((IsoCell)null);
				zombie2.dir = deadBody2.dir;
				zombie2.current = this;
				zombie2.x = deadBody2.x;
				zombie2.y = deadBody2.y;
				zombie2.z = deadBody2.z;
				if (zombie2.current != null) {
					deadBody2 = new IsoDeadBody(zombie2);
				}
			} else {
				this.StaticMovingObjects.add(deadBody2);
			}

			this.addCorpse(deadBody2, false);
			if (GameServer.bServer) {
				GameServer.sendCorpse(deadBody2);
				this.revisionUp();
			}

			if (GameClient.bClient) {
				this.clientModify();
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
						if (object2.getSprite().burntTile != null) {
							object2.sprite = this.getCell().SpriteManager.getSprite(object2.getSprite().burntTile);
							object2.RemoveAttachedAnims();
							if (object2.Children != null) {
								object2.Children.clear();
							}

							object2.transmitUpdatedSpriteToClients();
							object2.setOverlaySprite((String)null);
						} else if (object2.getType() == IsoObjectType.tree) {
							object2.sprite = this.getCell().SpriteManager.getSprite("fencing_burnt_01_" + (Rand.Next(15, 19) + 1));
							object2.RemoveAttachedAnims();
							if (object2.Children != null) {
								object2.Children.clear();
							}

							object2.transmitUpdatedSpriteToClients();
							object2.setOverlaySprite((String)null);
						} else if (!(object2 instanceof IsoTrap)) {
							if (object2 instanceof IsoBarricade) {
								if (GameServer.bServer) {
									GameServer.RemoveItemFromMap(object2);
								} else {
									this.Objects.remove(object2);
								}

								--int3;
							} else if (object2 instanceof IsoGenerator) {
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
								if (object2.getType() == IsoObjectType.wall && !object2.getProperties().Is("DoorWallW") && !object2.getProperties().Is("DoorWallN") && !object2.getProperties().Is("WindowN") && !object2.getProperties().Is(IsoFlagType.WindowW) && !object2.getSprite().getName().startsWith("walls_exterior_roofs_") && !object2.getSprite().getName().startsWith("fencing_") && !object2.getSprite().getName().startsWith("fixtures_railings_")) {
									if (object2.getSprite().getProperties().Is(IsoFlagType.collideW) && !object2.getSprite().getProperties().Is(IsoFlagType.collideN)) {
										object2.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "0" : "4"));
									} else if (object2.getSprite().getProperties().Is(IsoFlagType.collideN) && !object2.getSprite().getProperties().Is(IsoFlagType.collideW)) {
										object2.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "1" : "5"));
									} else if (object2.getSprite().getProperties().Is(IsoFlagType.collideW) && object2.getSprite().getProperties().Is(IsoFlagType.collideN)) {
										object2.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "2" : "6"));
									} else if (object2.getProperties().Is(IsoFlagType.WallSE)) {
										object2.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "3" : "7"));
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
										object2.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "8" : "12"));
									} else if (object2.getProperties().Is("WindowN")) {
										object2.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "9" : "13"));
									} else if (object2.getProperties().Is("DoorWallW")) {
										object2.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "10" : "14"));
									} else if (object2.getProperties().Is("DoorWallN")) {
										object2.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "11" : "15"));
									} else if (object2.getSprite().getProperties().Is(IsoFlagType.solidfloor) && !object2.getSprite().getProperties().Is(IsoFlagType.exterior)) {
										object2.sprite = this.getCell().SpriteManager.getSprite("floors_burnt_01_0");
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
											int int4;
											for (int4 = 0; int4 < object2.getContainer().getItems().size(); ++int4) {
												inventoryItem = (InventoryItem)object2.getContainer().getItems().get(int4);
												if (inventoryItem instanceof Food && ((Food)inventoryItem).isAlcoholic() || inventoryItem.getType().equals("PetrolCan") || inventoryItem.getType().equals("Bleach")) {
													int2 += 20;
													if (int2 > 100) {
														int2 = 100;
														break;
													}
												}
											}

											object2.sprite = this.getCell().SpriteManager.getSprite("floors_burnt_01_" + Rand.Next(1, 2));
											for (int4 = 0; int4 < object2.getContainerCount(); ++int4) {
												ItemContainer itemContainer = object2.getContainerByIndex(int4);
												itemContainer.removeItemsFromProcessItems();
												itemContainer.removeAllItems();
											}

											object2.removeAllContainers();
											boolean3 = true;
										} else if (!object2.getSprite().getProperties().Is(IsoFlagType.solidtrans) && !object2.getSprite().getProperties().Is(IsoFlagType.bed) && !object2.getSprite().getProperties().Is(IsoFlagType.waterPiped)) {
											if (object2.getSprite().getName().startsWith("walls_exterior_roofs_")) {
												object2.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_roofs_01_" + object2.getSprite().getName().substring(object2.getSprite().getName().lastIndexOf("_") + 1, object2.getSprite().getName().length()));
											} else if (object2.getSprite().getName().startsWith("roofs_")) {
												object2.sprite = this.getCell().SpriteManager.getSprite("roofs_burnt_01_" + object2.getSprite().getName().substring(object2.getSprite().getName().lastIndexOf("_") + 1, object2.getSprite().getName().length()));
											} else if ((object2.getSprite().getName().startsWith("fencing_") || object2.getSprite().getName().startsWith("fixtures_railings_")) && (object2.getSprite().getProperties().Is(IsoFlagType.HoppableN) || object2.getSprite().getProperties().Is(IsoFlagType.HoppableW))) {
												if (object2.getSprite().getProperties().Is(IsoFlagType.transparentW) && !object2.getSprite().getProperties().Is(IsoFlagType.transparentN)) {
													object2.sprite = this.getCell().SpriteManager.getSprite("fencing_burnt_01_0");
												} else if (object2.getSprite().getProperties().Is(IsoFlagType.transparentN) && !object2.getSprite().getProperties().Is(IsoFlagType.transparentW)) {
													object2.sprite = this.getCell().SpriteManager.getSprite("fencing_burnt_01_1");
												} else {
													object2.sprite = this.getCell().SpriteManager.getSprite("fencing_burnt_01_2");
												}
											}
										} else {
											object2.sprite = this.getCell().SpriteManager.getSprite("floors_burnt_01_" + Rand.Next(1, 2));
											if (object2.getOverlaySprite() != null) {
												object2.setOverlaySprite((String)null);
											}
										}
									}
								}

								if (!boolean3 && !(object2 instanceof IsoThumpable)) {
									object2.transmitUpdatedSpriteToClients();
									object2.setOverlaySprite((String)null);
								} else {
									IsoObject object3 = IsoObject.getNew();
									object3.setSprite(object2.getSprite());
									object3.setSquare(this);
									if (GameServer.bServer) {
										object2.sendObjectChange("replaceWith", "object", object3);
									}

									this.Objects.set(int3, object3);
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
						label188: {
							if (square.SolidFloorCached) {
								if (!square.SolidFloor) {
									break label188;
								}
							} else if (!square.TreatAsSolidFloor()) {
								break label188;
							}

							return true;
						}

						if (this.Properties.Is(IsoFlagType.noStart)) {
							return true;
						}

						square2 = getSquare.getGridSquare(this.x, this.y, square.z);
						if (square2 == null) {
							return false;
						}

						if (square2.SolidFloorCached) {
							if (square2.SolidFloor) {
								return true;
							}
						} else if (square2.TreatAsSolidFloor()) {
							return true;
						}
					} else {
						label236: {
							if (this.SolidFloorCached) {
								if (this.SolidFloor) {
									return true;
								}
							} else if (this.TreatAsSolidFloor()) {
								return true;
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
									break label236;
								}
							} else if (!square2.TreatAsSolidFloor()) {
								break label236;
							}

							return true;
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
		this.bDirty = true;
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
					if (propertyContainer.water) {
						boolean2 = false;
					} else {
						if (!boolean2 && propertyContainer.solidfloor) {
							boolean2 = true;
						}

						if (!boolean1 && propertyContainer.solidtrans) {
							boolean1 = true;
						}
					}
				}
			}
		}

		if (this.roomID == -1 && !this.haveRoof) {
			this.getProperties().Set(IsoFlagType.exterior);
		} else {
			this.getProperties().UnSet(IsoFlagType.exterior);
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

		if (!boolean1 && boolean2 && this.Properties.water) {
			this.Properties.UnSet(IsoFlagType.solidtrans);
		}

		this.propertiesDirty = this.chunk == null || this.chunk.bLoaded;
		if (this.chunk != null) {
			this.chunk.lightCheck[0] = this.chunk.lightCheck[1] = this.chunk.lightCheck[2] = this.chunk.lightCheck[3] = true;
		}

		if (this.chunk != null) {
			this.chunk.physicsCheck = true;
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
		boolean boolean1 = this.CalculateCollide(square, false, false, false, false, getSquare);
		this.collideMatrix[1 + (square.x - this.x)][1 + (square.y - this.y)][1 + (square.z - this.z)] = boolean1;
	}

	public void ReCalculatePathFind(IsoGridSquare square) {
		this.ReCalculatePathFind(square, cellGetSquare);
	}

	public void ReCalculatePathFind(IsoGridSquare square, IsoGridSquare.GetSquare getSquare) {
		boolean boolean1 = this.CalculateCollide(square, false, true, false, false, getSquare);
		this.pathMatrix[1 + (square.x - this.x)][1 + (square.y - this.y)][1 + (square.z - this.z)] = boolean1;
	}

	public void ReCalculateVisionBlocked(IsoGridSquare square) {
		this.ReCalculateVisionBlocked(square, cellGetSquare);
	}

	public void ReCalculateVisionBlocked(IsoGridSquare square, IsoGridSquare.GetSquare getSquare) {
		boolean boolean1 = this.CalculateVisionBlocked(square, getSquare);
		this.visionMatrix[1 + (square.x - this.x)][1 + (square.y - this.y)][1 + (square.z - this.z)] = boolean1;
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
		} else if (this.collideMatrix == null) {
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
						if (movingObject instanceof IsoPlayer && this.collideMatrix[int1 + 1][int2 + 1][int3 + 1]) {
							this.RecalcAllWithNeighbours(true);
						}

						return this.collideMatrix[int1 + 1][int2 + 1][int3 + 1];
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
		if (this.collideMatrix == null) {
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

			return UseSlowCollision ? this.CalculateCollide(square, false, false, false) : this.collideMatrix[int1 + 1][int2 + 1][int3 + 1];
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
				return this.pathMatrix[int1 + 1][int2 + 1][int3 + 1];
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

				return !this.visionMatrix[int1 + 1][int2 + 1][int3 + 1] ? testResults2 : LosUtil.TestResults.Blocked;
			}
		} else {
			return LosUtil.TestResults.Blocked;
		}
	}

	public boolean TreatAsSolidFloor() {
		if (this.SolidFloorCached) {
			return this.SolidFloor;
		} else {
			if (!this.Properties.Is(IsoFlagType.solidfloor) && !this.Has(IsoObjectType.stairsBN) && !this.Has(IsoObjectType.stairsTN) && !this.Has(IsoObjectType.stairsMN) && !this.Has(IsoObjectType.stairsBW) && !this.Has(IsoObjectType.stairsMW) && !this.Has(IsoObjectType.stairsTW)) {
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

	public static void bubbleSort3(ArrayList arrayList) {
		int int1 = arrayList.size();
		boolean boolean1 = true;
		while (boolean1) {
			--int1;
			boolean1 = false;
			for (int int2 = 0; int2 < int1; ++int2) {
				IsoMovingObject movingObject = (IsoMovingObject)arrayList.get(int2);
				IsoMovingObject movingObject2 = (IsoMovingObject)arrayList.get(int2 + 1);
				if (movingObject.compareToY(movingObject2) == 1) {
					arrayList.set(int2, movingObject2);
					arrayList.set(int2 + 1, movingObject);
					boolean1 = true;
				}
			}
		}
	}

	public void RenderCharacters(int int1, boolean boolean1) {
		this.renderCharacters(int1, boolean1, false);
	}

	void renderCharacters(int int1, boolean boolean1) {
		this.renderCharacters(int1, boolean1, true);
	}

	void renderCharacters(int int1, boolean boolean1, boolean boolean2) {
		if (this.z < int1) {
			if (!isOnScreenLast) {
			}

			if (boolean2) {
				IndieGL.glBlendFunc(770, 771);
			}

			if (!this.MovingObjects.isEmpty() || !this.StaticMovingObjects.isEmpty()) {
				IndieGL.End();
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
				if (movingObject.sprite != null && (!boolean1 || movingObject instanceof IsoDeadBody) && (boolean1 || !(movingObject instanceof IsoDeadBody))) {
					movingObject.render(movingObject.getX(), movingObject.getY(), movingObject.getZ(), colorInfo, true);
				}
			}

			int3 = this.MovingObjects.size();
			for (int4 = 0; int4 < int3; ++int4) {
				movingObject = (IsoMovingObject)this.MovingObjects.get(int4);
				if (movingObject != null && movingObject.sprite != null) {
					boolean boolean3 = movingObject.bOnFloor;
					if (boolean3 && movingObject instanceof IsoZombie) {
						IsoZombie zombie = (IsoZombie)movingObject;
						boolean3 = zombie.bCrawling || zombie.legsSprite.CurrentAnim != null && zombie.legsSprite.CurrentAnim.name.equals("ZombieDeath") && zombie.def.isFinished();
					}

					if ((!boolean1 || boolean3) && (boolean1 || !boolean3)) {
						if (movingObject instanceof IsoPlayer) {
							IsoPlayer player = (IsoPlayer)movingObject;
							if (player.bRemote) {
								player.netHistory.render(player);
							}
						}

						if (GameClient.bClient && movingObject != IsoPlayer.getInstance() && !(movingObject instanceof IsoLuaMover)) {
							movingObject.render(movingObject.bx, movingObject.by, movingObject.getZ(), colorInfo, true);
						} else {
							movingObject.render(movingObject.getX(), movingObject.getY(), movingObject.getZ(), colorInfo, true);
						}
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
				if (texWhite == null) {
					texWhite = Texture.getSharedTexture("media/ui/white.png");
				}

				Texture texture = texWhite;
				int int2 = (int)IsoUtils.XToScreenInt(this.x, this.y, this.z, 0);
				int int3 = (int)IsoUtils.YToScreenInt(this.x, this.y, this.z, 0);
				int2 -= (int)IsoCamera.frameState.OffX;
				int3 -= (int)IsoCamera.frameState.OffY;
				IndieGL.glColorMask(false, false, false, false);
				texture.renderwallnw(int2, int3, 64 * Core.TileScale, 32 * Core.TileScale, -1, -1, -1, -1, -1, -1);
				IndieGL.glColorMask(true, true, true, true);
				IndieGL.enableAlphaTest();
				IndieGL.glAlphaFunc(516, 0.0F);
				IndieGL.glStencilFunc(514, short1, 127);
				IndieGL.glStencilOp(7680, 7680, 7680);
				ColorInfo colorInfo = this.lightInfo[IsoCamera.frameState.playerIndex];
				Collections.sort(this.DeferedCharacters, comp);
				for (int int4 = 0; int4 < this.DeferedCharacters.size(); ++int4) {
					IsoGameCharacter gameCharacter = (IsoGameCharacter)this.DeferedCharacters.get(int4);
					if (gameCharacter.sprite != null) {
						gameCharacter.setbDoDefer(false);
						if (GameClient.bClient && gameCharacter != IsoPlayer.getInstance() && !(gameCharacter instanceof IsoLuaMover)) {
							gameCharacter.render(gameCharacter.bx, gameCharacter.by, gameCharacter.getZ(), colorInfo, true);
						} else {
							gameCharacter.render(gameCharacter.getX(), gameCharacter.getY(), gameCharacter.getZ(), colorInfo, true);
						}

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
		if (this.CachedScreenValue != Core.TileScale) {
			this.CachedScreenX = (float)((int)IsoUtils.XToScreenInt(this.x, this.y, this.z, 0));
			this.CachedScreenY = (float)((int)IsoUtils.YToScreenInt(this.x, this.y, this.z, 0));
			this.CachedScreenValue = Core.TileScale;
		}

		float float1 = this.CachedScreenX;
		float float2 = this.CachedScreenY;
		float1 -= (float)((int)IsoCamera.frameState.OffX);
		float2 -= (float)((int)IsoCamera.frameState.OffY);
		if (this.hasTree) {
			int int1 = 384 * Core.TileScale / 2 - 96 * Core.TileScale;
			int int2 = 256 * Core.TileScale - 32 * Core.TileScale;
			if (float1 + (float)int1 <= 0.0F) {
				return false;
			} else if (float2 + (float)(32 * Core.TileScale) <= 0.0F) {
				return false;
			} else if (float1 - (float)int1 >= (float)IsoCamera.frameState.OffscreenWidth) {
				return false;
			} else {
				return !(float2 - (float)int2 >= (float)IsoCamera.frameState.OffscreenHeight);
			}
		} else if (float1 + (float)(32 * Core.TileScale) <= 0.0F) {
			return false;
		} else if (float2 + (float)(32 * Core.TileScale) <= 0.0F) {
			return false;
		} else if (float1 - (float)(32 * Core.TileScale) >= (float)IsoCamera.frameState.OffscreenWidth) {
			return false;
		} else {
			return !(float2 - (float)(96 * Core.TileScale) >= (float)IsoCamera.frameState.OffscreenHeight);
		}
	}

	void cacheLightInfo() {
		int int1 = IsoCamera.frameState.playerIndex;
		this.lightInfo[int1] = this.lighting[int1].lightInfo();
	}

	public void setLightInfoServerGUIOnly(ColorInfo colorInfo) {
		this.lightInfo[0] = colorInfo;
	}

	int renderFloor(int int1, int int2) {
		int int3 = IsoCamera.frameState.playerIndex;
		ColorInfo colorInfo = this.lightInfo[int3];
		boolean boolean1 = IsoCamera.frameState.CamCharacter.getVehicle() != null;
		int int4 = 0;
		try {
			int int5 = this.Objects.size();
			IsoObject[] objectArray = (IsoObject[])this.Objects.getElements();
			int int6;
			for (int6 = 0; int6 < int5; ++int6) {
				IsoObject object = objectArray[int6];
				if (GameClient.bClient && IsoPlayer.players[int3] != null && IsoPlayer.players[int3].isSeeNonPvpZone() && (object.highlightFlags & 1) == 0) {
					object.setHighlighted(true);
					if (NonPvpZone.getNonPvpZone(this.x, this.y) != null) {
						object.setHighlightColor(0.6F, 0.6F, 1.0F, 0.5F);
					} else {
						object.setHighlightColor(1.0F, 0.6F, 0.6F, 0.5F);
					}
				}

				if (Core.bDebug && GameClient.bClient && SafeHouse.isSafeHouse(this, (String)null, true) != null) {
					object.setHighlighted(true);
					object.setHighlightColor(1.0F, 0.0F, 0.0F, 1.0F);
				}

				boolean boolean2 = true;
				if (object.sprite != null && !object.sprite.solidfloor) {
					boolean2 = false;
					int4 |= 4;
				}

				if (object instanceof IsoFire) {
					boolean2 = false;
					int4 |= 4;
				}

				if (boolean2) {
					IndieGL.glAlphaFunc(516, 0.0F);
					object.alpha[int3] = 1.0F;
					object.targetAlpha[int3] = 1.0F;
					object.render((float)this.x, (float)this.y, (float)this.z, PerformanceSettings.LightingFrameSkip < 3 ? defColorInfo : colorInfo, true);
					int4 |= 1;
					if ((object.highlightFlags & 1) == 0) {
						int4 |= 8;
					}

					if ((object.highlightFlags & 2) != 0) {
						object.highlightFlags &= -4;
					}
				}

				if (!boolean2 && object.sprite != null && (boolean1 || !object.sprite.isBush) && (object.sprite.canBeRemoved || object.sprite.attachedFloor)) {
					int4 |= 2;
				}
			}

			if ((this.getCell().rainIntensity > 0 || RainManager.isRaining() && RainManager.RainIntensity > 0.0F) && this.isExteriorCache && !this.isVegitationCache && this.isSolidFloorCache && this.isCouldSee(int3)) {
				if (!IsoCamera.frameState.Paused) {
					int6 = this.getCell().rainIntensity == 0 ? (int)Math.min(Math.floor((double)(RainManager.RainIntensity / 0.2F)) + 1.0, 5.0) : this.getCell().rainIntensity;
					if (this.splashFrame < 0.0F && Rand.Next(Rand.AdjustForFramerate((int)(5.0F / (float)int6) * 100)) == 0) {
						this.splashFrame = 0.0F;
					}
				}

				if (this.splashFrame >= 0.0F) {
					int6 = (int)(this.splashFrame * 4.0F);
					Texture texture = Texture.getSharedTexture("RainSplash_00_" + int6);
					if (texture != null) {
						float float1 = IsoUtils.XToScreen((float)this.x + this.splashX, (float)this.y + this.splashY, (float)this.z, 0) - IsoCamera.frameState.OffX;
						float float2 = IsoUtils.YToScreen((float)this.x + this.splashX, (float)this.y + this.splashY, (float)this.z, 0) - IsoCamera.frameState.OffY;
						float1 -= (float)(texture.getWidth() / 2 * Core.TileScale);
						float2 -= (float)(texture.getHeight() / 2 * Core.TileScale);
						float float3 = 0.6F * (this.getCell().rainIntensity > 0 ? 1.0F : RainManager.RainIntensity);
						float float4 = Core.getInstance().RenderShader != null ? 0.6F : 1.0F;
						SpriteRenderer.instance.render(texture, float1, float2, (float)(texture.getWidth() * Core.TileScale), (float)(texture.getHeight() * Core.TileScale), 0.8F * colorInfo.r, 0.9F * colorInfo.g, 1.0F * colorInfo.b, float3 * float4);
					}

					if (!IsoCamera.frameState.Paused && this.splashFrameNum != IsoCamera.frameState.frameCount) {
						this.splashFrame += 0.08F * (30.0F / (float)PerformanceSettings.LockFPS);
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
		} catch (Exception exception) {
			IndieGL.End();
			ExceptionLogger.logException(exception);
		}

		return int4;
	}

	private boolean isSpriteOnSouthOrEastWall(IsoObject object) {
		if (object instanceof IsoBarricade) {
			return object.getDir() == IsoDirections.S || object.getDir() == IsoDirections.E;
		} else if (!(object instanceof IsoCurtain)) {
			return false;
		} else {
			IsoCurtain curtain = (IsoCurtain)object;
			return curtain.getType() == IsoObjectType.curtainS || curtain.getType() == IsoObjectType.curtainE;
		}
	}

	public boolean RenderMinusFloorFxMask(int int1, int int2, int int3, int int4, boolean boolean1, boolean boolean2, boolean boolean3) {
		boolean boolean4 = false;
		int int5 = this.Objects.size();
		IsoObject[] objectArray = (IsoObject[])this.Objects.getElements();
		boolean boolean5 = IsoCamera.frameState.CamCharacter.getVehicle() != null;
		try {
			int int6 = boolean1 ? int5 - 1 : 0;
			int int7 = boolean1 ? 0 : int5 - 1;
			int int8 = int6;
			while (true) {
				if (boolean1) {
					if (int8 < int7) {
						break;
					}
				} else if (int8 > int7) {
					break;
				}

				IsoObject object = objectArray[int8];
				boolean boolean6 = true;
				IsoObjectType objectType = IsoObjectType.MAX;
				if (object.sprite != null) {
					objectType = object.sprite.getType();
				}

				if (object.sprite != null && object.sprite.solidfloor) {
					boolean6 = false;
				}

				if (this.z >= int3 && (object.sprite == null || !object.sprite.alwaysDraw)) {
					boolean6 = false;
				}

				if ((!boolean2 || object.sprite == null || !boolean5 && object.sprite.isBush || object.sprite.canBeRemoved || object.sprite.attachedFloor) && (boolean2 || object.sprite == null || !boolean5 && object.sprite.isBush || !object.sprite.canBeRemoved && !object.sprite.attachedFloor)) {
					if (object.sprite != null && (objectType == IsoObjectType.WestRoofB || objectType == IsoObjectType.WestRoofM || objectType == IsoObjectType.WestRoofT) && this.z == int3 - 1 && this.z == (int)IsoCamera.CamCharacter.getZ()) {
						boolean6 = false;
					}

					if (this.isSpriteOnSouthOrEastWall(object)) {
						if (!boolean1) {
							boolean6 = false;
						}

						boolean4 = true;
					} else if (boolean1) {
						boolean6 = false;
					}

					if (boolean6) {
						object.renderFxMask((float)this.x, (float)this.y, (float)this.z, false);
					}
				}

				int8 += boolean1 ? -1 : 1;
			}
		} catch (Exception exception) {
			IndieGL.End();
			ExceptionLogger.logException(exception);
		}

		return boolean4;
	}

	boolean renderMinusFloor(int int1, int int2, int int3, int int4, boolean boolean1, boolean boolean2) {
		IndieGL.glBlendFunc(770, 771);
		int int5 = 0;
		isOnScreenLast = this.IsOnScreen();
		int int6 = IsoCamera.frameState.playerIndex;
		IsoGridSquare square = IsoCamera.frameState.CamCharacterSquare;
		IsoRoom room = IsoCamera.frameState.CamCharacterRoom;
		ColorInfo colorInfo = this.lightInfo[int6];
		boolean boolean3 = this.lighting[int6].bCouldSee();
		float float1 = this.lighting[int6].darkMulti();
		boolean boolean4 = IsoCamera.frameState.CamCharacter.getVehicle() != null;
		colorInfo.a = 1.0F;
		defColorInfo.r = 1.0F;
		defColorInfo.g = 1.0F;
		defColorInfo.b = 1.0F;
		defColorInfo.a = 1.0F;
		int int7 = (int)(this.CachedScreenX - IsoCamera.frameState.OffX);
		int int8 = (int)(this.CachedScreenY - IsoCamera.frameState.OffY);
		boolean boolean5 = true;
		IsoCell cell = this.getCell();
		if (int7 + 32 * Core.TileScale <= cell.StencilX1 || int7 - 32 * Core.TileScale >= cell.StencilX2 || int8 + 32 * Core.TileScale <= cell.StencilY1 || int8 - 96 * Core.TileScale >= cell.StencilY2) {
			boolean5 = false;
		}

		boolean boolean6 = false;
		int int9 = this.Objects.size();
		IsoObject[] objectArray = (IsoObject[])this.Objects.getElements();
		try {
			int int10 = boolean1 ? int9 - 1 : 0;
			int int11 = boolean1 ? 0 : int9 - 1;
			int int12 = int10;
			while (true) {
				if (boolean1) {
					if (int12 < int11) {
						break;
					}
				} else if (int12 > int11) {
					break;
				}

				IsoObject object = objectArray[int12];
				boolean boolean7 = true;
				IsoObjectType objectType = IsoObjectType.MAX;
				if (object.sprite != null) {
					objectType = object.sprite.getType();
				}

				CircleStencil = false;
				if (object.sprite != null && object.sprite.solidfloor) {
					boolean7 = false;
				}

				if (object instanceof IsoFire) {
					boolean7 = !boolean2;
				}

				if (this.z >= int3 && (object.sprite == null || !object.sprite.alwaysDraw)) {
					boolean7 = false;
				}

				if (this.z == int3 - 1 && object.sprite != null) {
				}

				if ((!boolean2 || object.sprite == null || !boolean4 && object.sprite.isBush || object.sprite.canBeRemoved || object.sprite.attachedFloor || object instanceof IsoWorldInventoryObject) && (boolean2 || object.sprite == null || !boolean4 && object.sprite.isBush || !object.sprite.canBeRemoved && !object.sprite.attachedFloor && !(object instanceof IsoWorldInventoryObject))) {
					if (object.sprite != null && (objectType == IsoObjectType.WestRoofB || objectType == IsoObjectType.WestRoofM || objectType == IsoObjectType.WestRoofT) && this.z == int3 - 1 && this.z == (int)IsoCamera.CamCharacter.getZ()) {
						boolean7 = false;
					}

					if (object.sprite != null && !object.sprite.solidfloor && IsoPlayer.instance.isClimbing()) {
						boolean7 = true;
					}

					if (this.isSpriteOnSouthOrEastWall(object)) {
						if (!boolean1) {
							boolean7 = false;
						}

						boolean6 = true;
					} else if (boolean1) {
						boolean7 = false;
					}

					if (boolean7) {
						IndieGL.glAlphaFunc(516, 0.0F);
						IsoGridSquare square2;
						if (object.sprite != null && (objectType == IsoObjectType.doorFrW || objectType == IsoObjectType.doorFrN || objectType == IsoObjectType.doorW || objectType == IsoObjectType.doorN || object.sprite.cutW || object.sprite.cutN) && PerformanceSettings.LightingFrameSkip < 3) {
							if (object.targetAlpha[int6] < 1.0F) {
								boolean boolean8 = PerformanceSettings.NewRoofHiding && !IsoWorld.instance.CurrentCell.CanBuildingSquareOccludePlayer(this, int6);
								if (boolean8) {
									if (object.sprite.cutW && this.getProperties().Is(IsoFlagType.WallSE)) {
										square2 = this.nav[IsoDirections.NW.index()];
										if (square2 == null || square2.getRoom() == null) {
											boolean8 = false;
										}
									} else if (objectType != IsoObjectType.doorFrW && objectType != IsoObjectType.doorW && !object.sprite.cutW) {
										if (objectType == IsoObjectType.doorFrN || objectType == IsoObjectType.doorN || object.sprite.cutN) {
											square2 = this.nav[IsoDirections.N.index()];
											if (square2 == null || square2.getRoom() == null) {
												boolean8 = false;
											}
										}
									} else {
										square2 = this.nav[IsoDirections.W.index()];
										if (square2 == null || square2.getRoom() == null) {
											boolean8 = false;
										}
									}
								}

								if (!boolean8) {
									CircleStencil = boolean5;
								}

								object.targetAlpha[int6] = 1.0F;
								object.alpha[int6] = 1.0F;
							}

							if (object.sprite.cutW && object.sprite.cutN) {
								int5 = this.DoWallLightingNW(int2, int1, object, int5, CircleStencil, int4);
							} else if (object.sprite.getType() != IsoObjectType.doorFrW && objectType != IsoObjectType.doorW && !object.sprite.cutW) {
								if (objectType == IsoObjectType.doorFrN || objectType == IsoObjectType.doorN || object.sprite.cutN) {
									int5 = this.DoWallLightingN(object, int5, int2, int1, int4);
								}
							} else {
								int5 = this.DoWallLightingW(object, int5, int2, int1, int4);
							}
						} else if (object.sprite != null && (objectType == IsoObjectType.doorFrW || objectType == IsoObjectType.doorFrN || objectType == IsoObjectType.doorW || objectType == IsoObjectType.doorN || object.sprite.cutW || object.sprite.cutN) && PerformanceSettings.LightingFrameSkip == 3) {
							if (this.z != (int)IsoCamera.frameState.CamCharacterZ || objectType == IsoObjectType.doorFrW || objectType == IsoObjectType.doorFrN || object instanceof IsoWindow) {
								boolean5 = false;
							}

							if (object.targetAlpha[int6] < 1.0F) {
								object.targetAlpha[int6] = boolean5 ? object.targetAlpha[int6] : 1.0F;
								object.alpha[int6] = object.targetAlpha[int6];
								IsoObject.LowLightingQualityHack = false;
								object.render((float)this.x, (float)this.y, (float)this.z, colorInfo, true);
								if (!IsoObject.LowLightingQualityHack) {
									object.targetAlpha[int6] = 1.0F;
								}
							} else {
								object.render((float)this.x, (float)this.y, (float)this.z, colorInfo, true);
							}
						} else {
							if (square != null && !boolean3 && this.room != room && float1 < 0.5F) {
								object.targetAlpha[int6] = float1 * 2.0F;
							} else {
								object.targetAlpha[int6] = 1.0F;
								if (IsoPlayer.instance != null && object.getProperties() != null && (object.getProperties().Is(IsoFlagType.solid) || object.getProperties().Is(IsoFlagType.solidtrans))) {
									int int13 = this.getX() - (int)IsoPlayer.instance.getX();
									int int14 = this.getY() - (int)IsoPlayer.instance.getY();
									if (int13 > 0 && int13 < 3 && int14 >= 0 && int14 < 3 || int14 > 0 && int14 < 3 && int13 >= 0 && int13 < 3) {
										object.targetAlpha[int6] = 0.99F;
									}
								}
							}

							if (object instanceof IsoWindow && object.targetAlpha[int6] < 1.0E-4F) {
								IsoWindow window = (IsoWindow)object;
								square2 = window.getOppositeSquare();
								if (square2 != null && square2 != this && square2.lighting[int6].bSeen()) {
									object.targetAlpha[int6] = square2.lighting[int6].darkMulti() * 2.0F;
								}
							}

							if (object instanceof IsoTree) {
								if (boolean5 && this.x >= (int)IsoCamera.frameState.CamCharacterX && this.y >= (int)IsoCamera.frameState.CamCharacterY && square != null && square.Is(IsoFlagType.exterior)) {
									((IsoTree)object).bRenderFlag = true;
								} else {
									((IsoTree)object).bRenderFlag = false;
								}
							}

							object.render((float)this.x, (float)this.y, (float)this.z, colorInfo, true);
						}

						if ((object.highlightFlags & 2) != 0) {
							object.highlightFlags &= -4;
						}
					}
				}

				int12 += boolean1 ? -1 : 1;
			}
		} catch (Exception exception) {
			IndieGL.End();
			ExceptionLogger.logException(exception);
		}

		return boolean6;
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

	BlockInfo testAdjacentRoomTransition(int int1, int int2, int int3) {
		blockInfo.ThroughDoor = false;
		blockInfo.ThroughWindow = false;
		blockInfo.ThroughStairs = false;
		if (int1 >= -1 && int1 <= 1 && int2 >= -1 && int2 <= 1 && int3 >= -1 && int3 <= 1) {
			int int4 = this.x;
			int int5 = this.y;
			int int6 = this.z;
			int1 += this.x;
			int2 += this.y;
			int3 += this.z;
			IsoGridSquare square = this.getCell().getGridSquare(int4, int5, int6);
			IsoGridSquare square2 = this.getCell().getGridSquare(int1, int2, int3);
			if (square2 == null) {
				return blockInfo;
			} else {
				if (int4 < int1) {
					if (square2.Has(IsoObjectType.doorFrW) || square2.Properties.Is(IsoFlagType.doorW)) {
						blockInfo.ThroughDoor = true;
					}

					if (square2.Properties.Is(IsoFlagType.windowW)) {
						blockInfo.ThroughWindow = true;
					}
				}

				if (int4 > int1) {
					if (square.Has(IsoObjectType.doorFrW) || square.Properties.Is(IsoFlagType.doorW)) {
						blockInfo.ThroughDoor = true;
					}

					if (square.Properties.Is(IsoFlagType.windowW)) {
						blockInfo.ThroughWindow = true;
					}
				}

				if (int5 < int2) {
					if (square2.Has(IsoObjectType.doorFrN) || square2.Properties.Is(IsoFlagType.doorN)) {
						blockInfo.ThroughDoor = true;
					}

					if (square2.Properties.Is(IsoFlagType.windowN)) {
						blockInfo.ThroughWindow = true;
					}
				}

				if (int5 > int2) {
					if (square.Has(IsoObjectType.doorFrN) || square.Properties.Is(IsoFlagType.doorN)) {
						blockInfo.ThroughDoor = true;
					}

					if (square.Properties.Is(IsoFlagType.windowN)) {
						blockInfo.ThroughWindow = true;
					}
				}

				if (int6 > int3 && square2.Has(IsoObjectType.stairsTN)) {
					blockInfo.ThroughStairs = true;
				}

				if (int6 < int3 && square.Has(IsoObjectType.stairsTN)) {
					blockInfo.ThroughStairs = true;
				}

				return blockInfo;
			}
		} else {
			return blockInfo;
		}
	}

	public void CalcLightInfo(int int1) {
		IsoChunk chunk = this.getChunk();
		if (chunk != null) {
			IsoGridSquare.ILighting iLighting = this.lighting[int1];
			IsoPlayer player = IsoPlayer.players[int1];
			POVCharacters.clear();
			POVCharacters.add(player);
			float float1 = 0.0F;
			float float2 = darkStep;
			float float3 = 1.0E9F;
			int int2 = POVCharacters.size();
			for (int int3 = 0; int3 < int2; ++int3) {
				IsoGameCharacter gameCharacter = (IsoGameCharacter)POVCharacters.get(int3);
				IsoGameCharacter.LightInfo lightInfo = gameCharacter.getLightInfo2();
				float float4 = IsoUtils.DistanceManhatten(lightInfo.x, lightInfo.y, (float)this.x, (float)this.y, lightInfo.z, (float)this.z);
				if (this == lightInfo.square) {
					float4 = 0.0F;
				}

				if (IsoPlayer.DemoMode) {
				}

				float float5 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex());
				if (this.getRoom() != null) {
					float5 *= 0.5F;
				}

				float float6 = float5;
				if (gameCharacter.HasTrait("ShortSighted")) {
					float6 = float5 * 1.5F;
				}

				if (float4 > GameTime.getInstance().getViewDistMax()) {
					float4 = GameTime.getInstance().getViewDistMax();
				}

				float4 = 1.0F - float6 / GameTime.getInstance().getViewDist();
				if (float4 < 0.0F) {
					float4 = 0.0F;
				}

				if (float4 > 1.0F) {
					float4 = 1.0F;
				}

				float6 *= float4;
				if (float4 < float3) {
					float3 = float4;
				}

				if (float6 < float5) {
					float6 = float5;
				}

				if (IsoPlayer.getInstance() != null) {
					if (iLighting.darkMulti() < iLighting.targetDarkMulti()) {
						float2 *= 2.0F;
						if (player.getVehicle() != null) {
							float2 = darkStep * 20.0F;
						}
					} else if (iLighting.darkMulti() > iLighting.targetDarkMulti()) {
						float2 *= 1.5F;
						if (lightInfo.time - torchTimer < 2000L) {
							float2 *= 10.0F;
						}

						if (this.room == null) {
							float2 /= 4.0F;
						}

						if (player.getVehicle() != null) {
							float2 = darkStep * 20.0F;
						}
					}

					float2 *= (float)(PerformanceSettings.LightingFrameSkip + 1);
					float2 *= GameTime.instance.FPSMultiplier;
				}

				if (player.HasTrait("ShortSighted")) {
					float2 *= 0.7F;
				}

				float2 *= 0.8F;
				float1 += float6;
			}

			float1 /= (float)POVCharacters.size();
			if (float1 > 1.0F) {
				float1 = 1.0F;
			}

			if (iLighting.darkMulti() < iLighting.targetDarkMulti()) {
				iLighting.darkMulti(iLighting.darkMulti() + float2);
				if (iLighting.darkMulti() > iLighting.targetDarkMulti()) {
					iLighting.darkMulti(iLighting.targetDarkMulti());
				}
			} else if (iLighting.darkMulti() > iLighting.targetDarkMulti()) {
				iLighting.darkMulti(iLighting.darkMulti() - float2);
				if (iLighting.darkMulti() < iLighting.targetDarkMulti()) {
					iLighting.darkMulti(iLighting.targetDarkMulti());
				}
			}

			float float7 = iLighting.darkMulti();
			if (float3 < 576.0F) {
				int int4 = 1;
				if (this.w != null && this.w.room == this.room && this.w.lighting[int1].darkMulti() > iLighting.darkMulti()) {
					if (this.w.lighting[int1].bSeen()) {
						float7 += this.w.lighting[int1].darkMulti();
					}

					++int4;
				}

				if (this.n != null && this.n.room == this.room && this.n.lighting[int1].darkMulti() > iLighting.darkMulti()) {
					if (this.n.lighting[int1].bSeen()) {
						float7 += this.n.lighting[int1].darkMulti();
					}

					++int4;
				}

				if (this.e != null && this.e.room == this.room && this.e.lighting[int1].darkMulti() > iLighting.darkMulti()) {
					if (this.e.lighting[int1].bSeen()) {
						float7 += this.e.lighting[int1].darkMulti();
					}

					++int4;
				}

				if (this.s != null && this.s.room == this.room && this.s.lighting[int1].darkMulti() > iLighting.darkMulti()) {
					if (this.s.lighting[int1].bSeen()) {
						float7 += this.s.lighting[int1].darkMulti();
					}

					++int4;
				}

				float7 /= (float)int4;
			}

			ColorInfo colorInfo = iLighting.lightInfo();
			colorInfo.r = float7;
			colorInfo.g = float7;
			colorInfo.b = float7;
			if (rmodLT < 0.0F) {
				rmodLT = 0.0F;
			}

			if (gmodLT < 0.0F) {
				gmodLT = 0.0F;
			}

			if (bmodLT < 0.0F) {
				bmodLT = 0.0F;
			}

			colorInfo.r *= float1 * rmodLT;
			colorInfo.g *= float1 * gmodLT;
			colorInfo.b *= float1 * bmodLT;
			if (iLighting.lampostTotalR() > 0.0F || iLighting.lampostTotalG() > 0.0F || iLighting.lampostTotalB() > 0.0F) {
				colorInfo.r += iLighting.lampostTotalR() * float7;
				colorInfo.g += iLighting.lampostTotalG() * float7;
				colorInfo.b += iLighting.lampostTotalB() * float7;
			}

			if (colorInfo.r > 1.0F) {
				colorInfo.r = 1.0F;
			}

			if (colorInfo.g > 1.0F) {
				colorInfo.g = 1.0F;
			}

			if (colorInfo.b > 1.0F) {
				colorInfo.b = 1.0F;
			}

			if (iLighting.bSeen()) {
				IsoFireManager.LightTileWithFire(this);
			}
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
							player.angle.x = vector22.x;
							player.angle.y = vector22.y;
							lightInfo.angleX = player.angle.x;
							lightInfo.angleY = player.angle.y;
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
							if (player.HasTrait("HardOfHearing") && float4 < 0.7F) {
								float4 = 0.7F;
							}

							float3 = 2.0F;
							if (player.HasTrait("KeenHearing")) {
								float3 += 3.0F;
							}

							if (float1 < float3 * (1.0F - float4) && !player.HasTrait("Deaf")) {
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

						if (player.HasTrait("EagleEyed")) {
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

								if ((!GameServer.bServer || !(player instanceof IsoPlayer) || !((IsoPlayer)player).GhostMode) && IsoUtils.DistanceManhatten(lightInfo.x, lightInfo.y, (float)this.x, (float)this.y) < (float)byte1 && this.z == (int)lightInfo.z) {
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

	public static int getFireRecalc() {
		return FireRecalc;
	}

	public static void setFireRecalc(int int1) {
		FireRecalc = int1;
	}

	public static float getDarkStep() {
		return darkStep;
	}

	public static void setDarkStep(float float1) {
		darkStep = float1;
	}

	public static BlockInfo getBlockInfo() {
		return blockInfo;
	}

	public static void setBlockInfo(BlockInfo blockInfo) {
		blockInfo = blockInfo;
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

	public void setStaticMovingObjects(ArrayList arrayList) {
		this.StaticMovingObjects = arrayList;
	}

	public ArrayList getMovingObjects() {
		return this.MovingObjects;
	}

	public void setMovingObjects(ArrayList arrayList) {
		this.MovingObjects = arrayList;
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

	public void setObjects(PZArrayList pZArrayList) {
		this.Objects = pZArrayList;
	}

	public PropertyContainer getProperties() {
		return this.Properties;
	}

	public void setProperties(PropertyContainer propertyContainer) {
		this.Properties = propertyContainer;
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

	public void setSpecialObjects(ArrayList arrayList) {
		this.SpecialObjects = arrayList;
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

	public void setDeferedCharacters(ArrayList arrayList) {
		this.DeferedCharacters = arrayList;
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

	public static Comparator getComp() {
		return comp;
	}

	public static void setComp(Comparator comparator) {
		comp = comparator;
	}

	public static ColorInfo getDefColorInfo() {
		return defColorInfo;
	}

	public static void setDefColorInfo(ColorInfo colorInfo) {
		defColorInfo = colorInfo;
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

	public IsoObject getOpenDoor(IsoDirections directions) {
		boolean boolean1;
		String string;
		String string2;
		switch (directions) {
		case N: 
			boolean1 = false;
			string = "7";
			string2 = "8";
			break;
		
		case S: 
			boolean1 = false;
			string = "5";
			string2 = "6";
			break;
		
		case W: 
			boolean1 = true;
			string = "5";
			string2 = "6";
			break;
		
		case E: 
			boolean1 = true;
			string = "7";
			string2 = "8";
			break;
		
		default: 
			return null;
		
		}
		IsoObject object = this.getDoor(boolean1);
		if (object instanceof IsoDoor && ((IsoDoor)object).open || object instanceof IsoThumpable && ((IsoThumpable)object).open) {
			PropertyContainer propertyContainer = object.getProperties();
			String string3 = propertyContainer == null ? null : propertyContainer.Val("DoubleDoor");
			if (string3 == null) {
				if (boolean1 && directions == IsoDirections.W) {
					return object;
				}

				if (!boolean1 && directions == IsoDirections.N) {
					return object;
				}

				return null;
			}

			if (string.equals(string3) || string2.equals(string3)) {
				return object;
			}
		}

		return null;
	}

	public void setWorldObjects(ArrayList arrayList) {
		this.WorldObjects = arrayList;
	}

	public void removeWorldObject(IsoWorldInventoryObject worldInventoryObject) {
		if (worldInventoryObject != null) {
			worldInventoryObject.removeFromWorld();
			worldInventoryObject.removeFromSquare();
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

	public void drawCircleExplosion(int int1, HandWeapon handWeapon, boolean boolean1) {
		if (GameClient.bClient) {
			this.syncIsoTrap(handWeapon, boolean1, true);
		} else {
			IsoTrap trap = new IsoTrap(handWeapon, this.getCell(), this);
			this.drawCircleExplosion(int1, trap, boolean1);
			trap.removeFromWorld();
		}
	}

	public void syncIsoTrap(HandWeapon handWeapon, boolean boolean1, boolean boolean2) {
		ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
		PacketTypes.doPacket((short)110, byteBufferWriter);
		byteBufferWriter.putInt(this.getX());
		byteBufferWriter.putInt(this.getY());
		byteBufferWriter.putInt(this.getZ());
		int int1 = 0;
		if (handWeapon.getExplosionRange() > 0) {
			int1 = handWeapon.getExplosionRange();
		}

		if (handWeapon.getFireRange() > 0) {
			int1 = handWeapon.getFireRange();
		}

		if (handWeapon.getSmokeRange() > 0) {
			int1 = handWeapon.getSmokeRange();
		}

		try {
			handWeapon.save(byteBufferWriter.bb, false);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		byteBufferWriter.putInt(int1);
		byteBufferWriter.putBoolean(boolean1);
		byteBufferWriter.putBoolean(boolean2);
		GameClient.connection.endPacketImmediate();
	}

	public void drawCircleExplosion(int int1, IsoTrap trap, boolean boolean1) {
		if (int1 > 15) {
			int1 = 15;
		}

		IsoGridSquare square = null;
		if (trap.getExplosionSound() != null && !boolean1) {
			if (GameServer.bServer) {
				GameServer.PlayWorldSoundServer(trap.getExplosionSound(), false, this, 0.0F, 50.0F, 1.0F, false);
			} else {
				SoundManager.instance.PlayWorldSound(trap.getExplosionSound(), this, 0.0F, 50.0F, 1.0F, false);
			}

			WorldSoundManager.instance.addSound((IsoObject)null, this.x, this.y, 0, 50, 1);
		}

		if (trap.getNoiseRange() > 0 && !boolean1) {
			WorldSoundManager.instance.addSound((IsoObject)null, this.x, this.y, 0, trap.getNoiseRange(), 1);
		}

		for (int int2 = this.getX() - int1; int2 <= this.getX() + int1; ++int2) {
			for (int int3 = this.getY() - int1; int3 <= this.getY() + int1; ++int3) {
				if (IsoUtils.DistanceTo((float)int2 + 0.5F, (float)int3 + 0.5F, (float)this.getX() + 0.5F, (float)this.getY() + 0.5F) <= (float)int1) {
					LosUtil.TestResults testResults = LosUtil.lineClear(this.getCell(), (int)trap.getX(), (int)trap.getY(), (int)trap.getZ(), int2, int3, this.z, false);
					if (testResults != LosUtil.TestResults.Blocked && testResults != LosUtil.TestResults.ClearThroughClosedDoor) {
						square = this.getCell().getGridSquare(int2, int3, this.getZ());
						if (square != null) {
							if (trap.getSmokeRange() > 0 && !boolean1) {
								if (Rand.Next(2) == 0) {
									IsoFireManager.StartSmoke(this.getCell(), square, true, 40, 0);
								}

								square.smoke();
							}

							if (trap.getExplosionRange() > 0 && !boolean1) {
								if (trap.getExplosionPower() > 0 && Rand.Next(80 - trap.getExplosionPower()) <= 0) {
									square.Burn();
								}

								square.explosion(trap);
								if (trap.getExplosionPower() > 0 && Rand.Next(100 - trap.getExplosionPower()) == 0) {
									IsoFireManager.StartFire(this.getCell(), square, true, 20);
								}
							}

							if (trap.getFireRange() > 0 && !boolean1 && Rand.Next(100 - trap.getFirePower()) == 0) {
								IsoFireManager.StartFire(this.getCell(), square, true, 40);
							}

							if (trap.getSensorRange() > 0) {
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
				((IsoZombie)movingObject).target = null;
				((IsoZombie)movingObject).getStateMachine().changeState(ZombieStandState.instance());
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
					object2.sprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, object2.sprite.ID + propertyContainer.getStackReplaceTileOffset());
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
			}
		}
	}

	public void FixBarricades(int int1) {
		if (int1 < 87) {
			for (int int2 = 0; int2 < this.Objects.size(); ++int2) {
				IsoObject object = (IsoObject)this.Objects.get(int2);
				int int3 = 0;
				if (object instanceof IsoDoor) {
					IsoDoor door = (IsoDoor)object;
					int3 = door.OldNumPlanks;
				} else if (object instanceof IsoThumpable) {
					IsoThumpable thumpable = (IsoThumpable)object;
					int3 = thumpable.OldNumPlanks;
				} else if (object instanceof IsoWindow) {
					IsoWindow window = (IsoWindow)object;
					int3 = window.OldNumPlanks;
				}

				if (int3 != 0) {
					if (object.AttachedAnimSpriteActual != null) {
						object.AttachedAnimSpriteActual.clear();
					}

					if (object.AttachedAnimSprite != null) {
						object.AttachedAnimSprite.clear();
					}

					IsoBarricade barricade = IsoBarricade.AddBarricadeToObject((BarricadeAble)object, false);
					if (barricade != null) {
						for (int int4 = 0; int4 < int3; ++int4) {
							barricade.addPlank((IsoGameCharacter)null, (InventoryItem)null);
						}
					}
				}
			}
		}
	}

	public void revisionUp() {
		if (ChunkRevisions.USE_CHUNK_REVISIONS) {
			ChunkRevisions.instance.revisionUp(this);
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

	public void clientModify() {
		assert GameClient.bClient;
		if (this.chunk != null) {
			this.chunk.modificationTime = System.currentTimeMillis();
		}
	}

	public void setMasterRegion(MasterRegion masterRegion) {
		this.hasSetMasterRegion = masterRegion != null;
		this.masterRegion = masterRegion;
	}

	public MasterRegion getMasterRegion() {
		if (GameServer.bServer) {
			return IsoRegion.getMasterRegion(this.x, this.y, this.z);
		} else {
			if (!this.hasSetMasterRegion) {
				this.masterRegion = IsoRegion.getMasterRegion(this.x, this.y, this.z);
				this.hasSetMasterRegion = true;
			}

			return this.masterRegion;
		}
	}

	public void ResetMasterRegion() {
		this.masterRegion = null;
		this.hasSetMasterRegion = false;
	}

	public boolean isInARoom() {
		return this.getRoom() != null || this.getMasterRegion() != null && this.getMasterRegion().isPlayerRoom();
	}

	public static class CellGetSquare implements IsoGridSquare.GetSquare {

		public IsoGridSquare getGridSquare(int int1, int int2, int int3) {
			return IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
		}
	}

	public interface GetSquare {

		IsoGridSquare getGridSquare(int int1, int int2, int int3);
	}

	public static final class Lighting implements IsoGridSquare.ILighting {
		private int[] lightverts = new int[8];
		private float lampostTotalR = 0.0F;
		private float lampostTotalG = 0.0F;
		private float lampostTotalB = 0.0F;
		private boolean bSeen;
		private boolean bCanSee;
		private boolean bCouldSee;
		private float darkMulti;
		private float targetDarkMulti;
		private ColorInfo lightInfo = new ColorInfo();

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

		public void setPos(int int1, int int2, int int3) {
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

		void setPos(int int1, int int2, int int3);

		void reset();
	}
}
