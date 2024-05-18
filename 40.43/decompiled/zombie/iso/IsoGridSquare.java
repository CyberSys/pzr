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

public class IsoGridSquare implements Comparable, INode {
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
   private static Comparator comp = new Comparator() {
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

   public static boolean DoChecksumCheck(String var0, String var1) {
      String var2 = "";

      try {
         var2 = IsoObject.getMD5Checksum(var0);
         if (!var2.equals(var1)) {
            return false;
         }
      } catch (Exception var6) {
         var2 = "";

         try {
            var2 = IsoObject.getMD5Checksum("D:/Dropbox/Zomboid/zombie/build/classes/" + var0);
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

      for(int var1 = 0; var1 < 8; ++var1) {
         this.nav[var1] = null;
      }

   }

   public boolean isSomethingTo(IsoGridSquare var1) {
      return this.isWallTo(var1) || this.isWindowTo(var1) || this.isDoorTo(var1);
   }

   public boolean isWallTo(IsoGridSquare var1) {
      if (var1 == null) {
         return false;
      } else if (var1.x > this.x && var1.Properties.Is(IsoFlagType.collideW) && !var1.Properties.Is(IsoFlagType.WindowW)) {
         return true;
      } else if (this.x > var1.x && this.Properties.Is(IsoFlagType.collideW) && !this.Properties.Is(IsoFlagType.WindowW)) {
         return true;
      } else if (var1.y > this.y && var1.Properties.Is(IsoFlagType.collideN) && !var1.Properties.Is(IsoFlagType.WindowN)) {
         return true;
      } else if (this.y > var1.y && this.Properties.Is(IsoFlagType.collideN) && !this.Properties.Is(IsoFlagType.WindowN)) {
         return true;
      } else {
         if (var1.x != this.x && var1.y != this.y) {
            if (this.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(var1.x, this.y, this.z)) || this.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, var1.y, this.z))) {
               return true;
            }

            if (var1.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(var1.x, this.y, this.z)) || var1.isWallTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, var1.y, this.z))) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isWindowTo(IsoGridSquare var1) {
      if (var1 == null) {
         return false;
      } else if (var1.x > this.x && var1.Properties.Is(IsoFlagType.windowW)) {
         return true;
      } else if (this.x > var1.x && this.Properties.Is(IsoFlagType.windowW)) {
         return true;
      } else if (var1.y > this.y && var1.Properties.Is(IsoFlagType.windowN)) {
         return true;
      } else if (this.y > var1.y && this.Properties.Is(IsoFlagType.windowN)) {
         return true;
      } else {
         if (var1.x != this.x && var1.y != this.y) {
            if (this.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(var1.x, this.y, this.z)) || this.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, var1.y, this.z))) {
               return true;
            }

            if (var1.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(var1.x, this.y, this.z)) || var1.isWindowTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, var1.y, this.z))) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean haveDoor() {
      for(int var1 = 0; var1 < this.Objects.size(); ++var1) {
         if (this.Objects.get(var1) instanceof IsoDoor) {
            return true;
         }
      }

      return false;
   }

   public boolean isDoorTo(IsoGridSquare var1) {
      if (var1 == null) {
         return false;
      } else if (var1.x > this.x && var1.Properties.Is(IsoFlagType.doorW)) {
         return true;
      } else if (this.x > var1.x && this.Properties.Is(IsoFlagType.doorW)) {
         return true;
      } else if (var1.y > this.y && var1.Properties.Is(IsoFlagType.doorN)) {
         return true;
      } else if (this.y > var1.y && this.Properties.Is(IsoFlagType.doorN)) {
         return true;
      } else {
         if (var1.x != this.x && var1.y != this.y) {
            if (this.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(var1.x, this.y, this.z)) || this.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, var1.y, this.z))) {
               return true;
            }

            if (var1.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(var1.x, this.y, this.z)) || var1.isDoorTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, var1.y, this.z))) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isBlockedTo(IsoGridSquare var1) {
      return this.isWallTo(var1) || this.isWindowBlockedTo(var1) || this.isDoorBlockedTo(var1);
   }

   public boolean isWindowBlockedTo(IsoGridSquare var1) {
      if (var1 == null) {
         return false;
      } else if (var1.x > this.x && var1.hasBlockedWindow(false)) {
         return true;
      } else if (this.x > var1.x && this.hasBlockedWindow(false)) {
         return true;
      } else if (var1.y > this.y && var1.hasBlockedWindow(true)) {
         return true;
      } else if (this.y > var1.y && this.hasBlockedWindow(true)) {
         return true;
      } else {
         if (var1.x != this.x && var1.y != this.y) {
            if (this.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(var1.x, this.y, this.z)) || this.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, var1.y, this.z))) {
               return true;
            }

            if (var1.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(var1.x, this.y, this.z)) || var1.isWindowBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, var1.y, this.z))) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean hasBlockedWindow(boolean var1) {
      for(int var2 = 0; var2 < this.Objects.size(); ++var2) {
         IsoObject var3 = (IsoObject)this.Objects.get(var2);
         if (var3 instanceof IsoWindow) {
            IsoWindow var4 = (IsoWindow)var3;
            if (var4.getNorth() == var1) {
               return !var4.open || var4.isBarricaded();
            }
         }
      }

      return false;
   }

   public boolean isDoorBlockedTo(IsoGridSquare var1) {
      if (var1 == null) {
         return false;
      } else if (var1.x > this.x && var1.hasBlockedDoor(false)) {
         return true;
      } else if (this.x > var1.x && this.hasBlockedDoor(false)) {
         return true;
      } else if (var1.y > this.y && var1.hasBlockedDoor(true)) {
         return true;
      } else if (this.y > var1.y && this.hasBlockedDoor(true)) {
         return true;
      } else {
         if (var1.x != this.x && var1.y != this.y) {
            if (this.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(var1.x, this.y, this.z)) || this.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, var1.y, this.z))) {
               return true;
            }

            if (var1.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(var1.x, this.y, this.z)) || var1.isDoorBlockedTo(IsoWorld.instance.CurrentCell.getGridSquare(this.x, var1.y, this.z))) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean hasBlockedDoor(boolean var1) {
      for(int var2 = 0; var2 < this.Objects.size(); ++var2) {
         IsoObject var3 = (IsoObject)this.Objects.get(var2);
         if (var3 instanceof IsoDoor) {
            IsoDoor var4 = (IsoDoor)var3;
            if (var4.getNorth() == var1) {
               return !var4.open || var4.isBarricaded();
            }
         }

         if (var3 instanceof IsoThumpable) {
            IsoThumpable var5 = (IsoThumpable)var3;
            if (var5.isDoor() && var5.getNorth() == var1) {
               return !var5.open || var5.isBarricaded();
            }
         }
      }

      return false;
   }

   public boolean isHoppableTo(IsoGridSquare var1) {
      if (var1 == null) {
         return false;
      } else if (var1.x != this.x && var1.y != this.y) {
         return false;
      } else if (var1.x > this.x && var1.Properties.Is(IsoFlagType.HoppableW)) {
         return true;
      } else if (this.x > var1.x && this.Properties.Is(IsoFlagType.HoppableW)) {
         return true;
      } else if (var1.y > this.y && var1.Properties.Is(IsoFlagType.HoppableN)) {
         return true;
      } else {
         return this.y > var1.y && this.Properties.Is(IsoFlagType.HoppableN);
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

      for(int var1 = 0; var1 < 4; ++var1) {
         if (this.lighting[var1] != null) {
            this.lighting[var1].reset();
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
      synchronized(isoGridSquareCache) {
         if (!isoGridSquareSet.contains(this.ID) && isoGridSquareCache.size() < 20000) {
            isoGridSquareCache.push(this);
            isoGridSquareSet.add(this.ID);
         } else if (isoGridSquareCache.size() >= 20000) {
            boolean var2 = false;
         }

      }
   }

   private static boolean validateUser(String var0, String var1) throws MalformedURLException, IOException {
      URL var2 = new URL("http://www.projectzomboid.com/scripts/auth.php?username=" + var0 + "&password=" + var1);
      URLConnection var3 = var2.openConnection();
      BufferedReader var4 = new BufferedReader(new InputStreamReader(var3.getInputStream()));

      String var5;
      do {
         if ((var5 = var4.readLine()) == null) {
            return false;
         }
      } while(!var5.contains("success"));

      return true;
   }

   public float DistTo(int var1, int var2) {
      return IsoUtils.DistanceManhatten((float)var1 + 0.5F, (float)var2 + 0.5F, (float)this.x, (float)this.y);
   }

   public float DistTo(IsoGridSquare var1) {
      return IsoUtils.DistanceManhatten((float)this.x + 0.5F, (float)this.y + 0.5F, (float)var1.x + 0.5F, (float)var1.y + 0.5F);
   }

   public float DistToProper(IsoGridSquare var1) {
      return IsoUtils.DistanceTo((float)this.x + 0.5F, (float)this.y + 0.5F, (float)var1.x + 0.5F, (float)var1.y + 0.5F);
   }

   public float DistTo(IsoMovingObject var1) {
      return IsoUtils.DistanceManhatten((float)this.x + 0.5F, (float)this.y + 0.5F, var1.getX(), var1.getY());
   }

   public float DistToProper(IsoMovingObject var1) {
      return IsoUtils.DistanceTo((float)this.x + 0.5F, (float)this.y + 0.5F, var1.getX(), var1.getY());
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

   public void isSafeToSpawn(IsoGridSquare var1, int var2) {
      if (var2 <= 5) {
         choices.add(var1);
         if (var1.n != null && !choices.contains(var1.n)) {
            this.isSafeToSpawn(var1.n, var2 + 1);
         }

         if (var1.s != null && !choices.contains(var1.s)) {
            this.isSafeToSpawn(var1.s, var2 + 1);
         }

         if (var1.e != null && !choices.contains(var1.e)) {
            this.isSafeToSpawn(var1.e, var2 + 1);
         }

         if (var1.w != null && !choices.contains(var1.w)) {
            this.isSafeToSpawn(var1.w, var2 + 1);
         }

      }
   }

   public static boolean auth(String var0, char[] var1) {
      if (var0.length() > 64) {
         return false;
      } else {
         String var2 = var1.toString();
         if (var2.length() > 64) {
            return false;
         } else {
            try {
               return validateUser(var0, var2);
            } catch (MalformedURLException var4) {
               Logger.getLogger(IsoGridSquare.class.getName()).log(Level.SEVERE, (String)null, var4);
            } catch (IOException var5) {
               Logger.getLogger(IsoGridSquare.class.getName()).log(Level.SEVERE, (String)null, var5);
            }

            return false;
         }
      }
   }

   private void renderAttachedSpritesWithNoWallLighting(int var1, IsoObject var2, ColorInfo var3, int var4) {
      if (var2.AttachedAnimSprite != null && !var2.AttachedAnimSprite.isEmpty()) {
         boolean var5 = false;

         for(int var6 = 0; var6 < var2.AttachedAnimSprite.size(); ++var6) {
            IsoSpriteInstance var7 = (IsoSpriteInstance)var2.AttachedAnimSprite.get(var6);
            if (var7.parentSprite != null && var7.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
               var5 = true;
               break;
            }
         }

         if (var5) {
            defColorInfo.r = var3.r;
            defColorInfo.g = var3.g;
            defColorInfo.b = var3.b;
            float var9 = defColorInfo.a;
            IsoSpriteInstance var8;
            int var10;
            if (CircleStencil) {
               IndieGL.enableStencilTest();
               IndieGL.enableAlphaTest();
               IndieGL.glAlphaFunc(516, 0.02F);
               IndieGL.glStencilFunc(517, 128, 128);

               for(var10 = 0; var10 < var2.AttachedAnimSprite.size(); ++var10) {
                  var8 = (IsoSpriteInstance)var2.AttachedAnimSprite.get(var10);
                  if (var8.parentSprite != null && var8.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
                     defColorInfo.a = var8.alpha;
                     var8.render(var2, (float)this.x, (float)this.y, (float)this.z, var2.dir, var2.offsetX, var2.offsetY + var2.getRenderYOffset() * (float)Core.TileScale, defColorInfo);
                  }
               }

               IndieGL.glStencilFunc(519, 255, 255);
            } else {
               for(var10 = 0; var10 < var2.AttachedAnimSprite.size(); ++var10) {
                  var8 = (IsoSpriteInstance)var2.AttachedAnimSprite.get(var10);
                  if (var8.parentSprite != null && var8.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
                     defColorInfo.a = var8.alpha;
                     var8.render(var2, (float)this.x, (float)this.y, (float)this.z, var2.dir, var2.offsetX, var2.offsetY + var2.getRenderYOffset() * (float)Core.TileScale, defColorInfo);
                     var8.update();
                  }
               }
            }

            defColorInfo.r = 1.0F;
            defColorInfo.g = 1.0F;
            defColorInfo.b = 1.0F;
            defColorInfo.a = var9;
         }
      }
   }

   public int DoWallLightingN(IsoObject var1, int var2, int var3, int var4, int var5) {
      if (this.z != (int)IsoCamera.CamCharacter.z) {
         CircleStencil = false;
      }

      if (IsoCamera.CamCharacter.current != null && this.room == IsoCamera.CamCharacter.current.getRoom() && IsoCamera.CamCharacter.current.getRoom() != null && this.room != null) {
      }

      if (var1.sprite.getType() == IsoObjectType.doorFrN || var1 instanceof IsoWindow) {
         CircleStencil = false;
      }

      boolean var6 = var1.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
      if (var2 == 0 && !var6) {
         var2 = this.getCell().getStencilValue(this.x, this.y, this.z);
      }

      int var7 = IsoCamera.frameState.playerIndex;
      ColorInfo var8 = this.lightInfo[var7];
      colu = this.getVertLight(0, var7);
      coll = this.getVertLight(1, var7);
      colu2 = this.getVertLight(4, var7);
      coll2 = this.getVertLight(5, var7);
      IndieGL.End();
      if (CircleStencil) {
         IndieGL.enableStencilTest();
         IndieGL.enableAlphaTest();
         IndieGL.glAlphaFunc(516, 0.02F);
         IndieGL.glStencilFunc(517, 128, 128);
         IndieGL.glStencilOp(7680, 7680, 7680);
         var1.render((float)this.x, (float)this.y, (float)this.z, var6 ? var8 : defColorInfo, true, !var6);
         IndieGL.glStencilFunc(519, var2, 255);
         var1.alpha[var7] = 0.02F;
         if (var1.getProperties() != null && var1.getProperties().Is(IsoFlagType.HoppableN)) {
            var1.alpha[var7] = 0.25F;
         }
      } else {
         IndieGL.enableAlphaTest();
         IndieGL.glAlphaFunc(516, 0.0F);
         IndieGL.glStencilFunc(519, var2, 127);
      }

      if (!var6) {
         IndieGL.glStencilOp(7680, 7680, 7681);
      }

      if (CircleStencil) {
         float var9 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex());
         if (!this.lighting[var7].bSeen()) {
            var9 = 0.0F;
         }

         defColorInfo.r = var9 * rmod;
         defColorInfo.g = var9 * gmod;
         defColorInfo.b = var9 * bmod;
         var1.render((float)this.x, (float)this.y, (float)this.z, var6 ? var8 : defColorInfo, true, !var6);
         defColorInfo.r = 1.0F;
         defColorInfo.g = 1.0F;
         defColorInfo.b = 1.0F;
      } else {
         var1.render((float)this.x, (float)this.y, (float)this.z, var6 ? var8 : defColorInfo, true, !var6);
      }

      var1.alpha[var7] = 1.0F;
      IndieGL.End();
      if (var6) {
         IndieGL.glStencilFunc(519, 1, 255);
         IndieGL.glStencilOp(7680, 7680, 7680);
         return var2;
      } else {
         IndieGL.glColorMask(true, true, true, true);
         IndieGL.glAlphaFunc(516, 0.0F);
         if (CircleStencil) {
            IndieGL.glStencilFunc(514, var2, 255);
         } else {
            IndieGL.glStencilFunc(514, var2, 127);
         }

         IndieGL.glStencilOp(7680, 7680, 7680);
         if (texWhite == null) {
            texWhite = Texture.getSharedTexture("media/ui/white.png");
         }

         Texture var15 = texWhite;
         float var10 = 0.0F;
         float var11 = 0.0F;
         float var12 = 0.0F;
         float var13 = IsoUtils.XToScreenInt(this.x + (int)var10, this.y + (int)var11, this.z + (int)var12, 0);
         float var14 = IsoUtils.YToScreenInt(this.x + (int)var10, this.y + (int)var11, this.z + (int)var12, 0);
         var13 = (float)((int)var13);
         var14 = (float)((int)var14);
         var13 -= (float)((int)IsoCamera.frameState.OffX);
         var14 -= (float)((int)IsoCamera.frameState.OffY);
         if ((var1.highlightFlags & 1) == 0 && var15 != null) {
            var15.renderwalln((int)var13, (int)var14, 64 * Core.TileScale, 32 * Core.TileScale, colu, coll, colu2, coll2);
         }

         IndieGL.End();
         IndieGL.glStencilFunc(519, 1, 255);
         IndieGL.glStencilOp(7680, 7680, 7680);
         this.renderAttachedSpritesWithNoWallLighting(var7, var1, var8, var2);
         this.getCell().setStencilValue(this.x, this.y, this.z, var2);
         return var2 + 1;
      }
   }

   public int DoWallLightingNW(int var1, int var2, IsoObject var3, int var4, boolean var5, int var6) {
      if (this.z != (int)IsoCamera.CamCharacter.z) {
         var5 = false;
      }

      if (IsoCamera.CamCharacter.current != null && this.room == IsoCamera.CamCharacter.current.getRoom() && IsoCamera.CamCharacter.current.getRoom() != null && this.room != null) {
      }

      boolean var7 = var3.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
      if (var4 == 0 && !var7) {
         var4 = this.getCell().getStencilValue(this.x, this.y, this.z);
      }

      int var8 = IsoCamera.frameState.playerIndex;
      ColorInfo var9 = this.lightInfo[var8];
      colu = this.getVertLight(0, var8);
      coll = this.getVertLight(3, var8);
      colr = this.getVertLight(1, var8);
      colu2 = this.getVertLight(4, var8);
      coll2 = this.getVertLight(7, var8);
      colr2 = this.getVertLight(5, var8);
      IndieGL.End();
      if (var5) {
         IndieGL.enableStencilTest();
         IndieGL.enableAlphaTest();
         IndieGL.glAlphaFunc(516, 0.02F);
         IndieGL.glStencilFunc(517, 128, 128);
         IndieGL.glStencilOp(7680, 7680, 7680);
         var3.render((float)this.x, (float)this.y, (float)this.z, var7 ? var9 : defColorInfo, true, !var7);
         var3.alpha[var8] = 0.02F;
         if (var3.getProperties() != null && var3.getProperties().Is(IsoFlagType.HoppableN)) {
            var3.alpha[var8] = 0.25F;
         }

         IndieGL.glStencilFunc(519, var4, 255);
      } else {
         IndieGL.enableAlphaTest();
         IndieGL.glAlphaFunc(516, 0.02F);
         IndieGL.glStencilFunc(519, var4, 127);
      }

      if (!var7) {
         IndieGL.glStencilOp(7680, 7680, 7681);
      }

      if (var5) {
         float var10 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex());
         if (!this.lighting[var8].bSeen()) {
            var10 = 0.0F;
         }

         defColorInfo.r = var10 * rmod;
         defColorInfo.g = var10 * gmod;
         defColorInfo.b = var10 * bmod;
         var3.render((float)this.x, (float)this.y, (float)this.z, var7 ? var9 : defColorInfo, true, !var7);
         defColorInfo.r = 1.0F;
         defColorInfo.g = 1.0F;
         defColorInfo.b = 1.0F;
      } else {
         var3.render((float)this.x, (float)this.y, (float)this.z, var7 ? var9 : defColorInfo, true, !var7);
      }

      var3.alpha[var8] = 1.0F;
      IndieGL.End();
      if (var7) {
         IndieGL.glStencilFunc(519, 1, 255);
         IndieGL.glStencilOp(7680, 7680, 7680);
         return var4;
      } else {
         IndieGL.glColorMask(true, true, true, true);
         IndieGL.glAlphaFunc(516, 0.0F);
         if (var5) {
            IndieGL.glStencilFunc(514, var4, 255);
         } else {
            IndieGL.glStencilFunc(514, var4, 127);
         }

         IndieGL.glStencilOp(7680, 7680, 7680);
         if (texWhite == null) {
            texWhite = Texture.getSharedTexture("media/ui/white.png");
         }

         Texture var16 = texWhite;
         float var11 = 0.0F;
         float var12 = 0.0F;
         float var13 = 0.0F;
         float var14 = IsoUtils.XToScreenInt(this.x + (int)var11, this.y + (int)var12, this.z + (int)var13, 0);
         float var15 = IsoUtils.YToScreenInt(this.x + (int)var11, this.y + (int)var12, this.z + (int)var13, 0);
         var14 = (float)((int)var14);
         var15 = (float)((int)var15);
         var14 -= (float)((int)IsoCamera.frameState.OffX);
         var15 -= (float)((int)IsoCamera.frameState.OffY);
         if ((var3.highlightFlags & 1) == 0 && var16 != null) {
            var16.renderwallnw((int)var14, (int)var15, 64 * Core.TileScale, 32 * Core.TileScale, colu, coll, colu2, coll2, colr, colr2);
         }

         IndieGL.End();
         IndieGL.glStencilFunc(519, 1, 255);
         IndieGL.glStencilOp(7680, 7680, 7680);
         this.renderAttachedSpritesWithNoWallLighting(var8, var3, var9, var4);
         this.getCell().setStencilValue(this.x, this.y, this.z, var4);
         return var4 + 1;
      }
   }

   public int DoWallLightingW(IsoObject var1, int var2, int var3, int var4, int var5) {
      if (this.z != (int)IsoCamera.CamCharacter.z) {
         CircleStencil = false;
      }

      if (IsoCamera.CamCharacter.current != null && this.room == IsoCamera.CamCharacter.current.getRoom() && IsoCamera.CamCharacter.current.getRoom() != null && this.room != null) {
      }

      if (var1.sprite.getType() == IsoObjectType.doorFrW || var1 instanceof IsoWindow) {
         CircleStencil = false;
      }

      boolean var6 = var1.sprite.getProperties().Is(IsoFlagType.NoWallLighting);
      if (var2 == 0 && !var6) {
         var2 = this.getCell().getStencilValue(this.x, this.y, this.z);
      }

      int var7 = IsoCamera.frameState.playerIndex;
      ColorInfo var8 = this.lightInfo[var7];
      colu = this.getVertLight(0, var7);
      coll = this.getVertLight(3, var7);
      colu2 = this.getVertLight(4, var7);
      coll2 = this.getVertLight(7, var7);
      if (CircleStencil) {
         IndieGL.enableStencilTest();
         IndieGL.enableAlphaTest();
         IndieGL.glAlphaFunc(516, 0.02F);
         IndieGL.glStencilFunc(517, 128, 128);
         IndieGL.glStencilOp(7680, 7680, 7680);
         var1.render((float)this.x, (float)this.y, (float)this.z, var6 ? var8 : defColorInfo, true, !var6);
         IndieGL.glStencilFunc(519, var2, 255);
         var1.alpha[var7] = 0.02F;
         if (var1.getProperties() != null && var1.getProperties().Is(IsoFlagType.HoppableW)) {
            var1.alpha[var7] = 0.25F;
         }
      } else {
         IndieGL.enableAlphaTest();
         IndieGL.glAlphaFunc(516, 0.0F);
         IndieGL.glStencilFunc(519, var2, 127);
      }

      if (!var6) {
         IndieGL.glStencilOp(7680, 7680, 7681);
      }

      if (CircleStencil) {
         float var9 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex());
         if (!this.lighting[var7].bSeen()) {
            var9 = 0.0F;
         }

         defColorInfo.r = var9 * rmod;
         defColorInfo.g = var9 * gmod;
         defColorInfo.b = var9 * bmod;
         var1.render((float)this.x, (float)this.y, (float)this.z, var6 ? var8 : defColorInfo, true, !var6);
         defColorInfo.r = 1.0F;
         defColorInfo.g = 1.0F;
         defColorInfo.b = 1.0F;
      } else {
         var1.render((float)this.x, (float)this.y, (float)this.z, var6 ? var8 : defColorInfo, true, !var6);
      }

      var1.alpha[var7] = 1.0F;
      IndieGL.End();
      if (var6) {
         IndieGL.glStencilFunc(519, 1, 255);
         IndieGL.glStencilOp(7680, 7680, 7680);
         return var2;
      } else {
         IndieGL.glColorMask(true, true, true, true);
         IndieGL.glAlphaFunc(516, 0.0F);
         if (CircleStencil) {
            IndieGL.glStencilFunc(514, var2, 255);
         } else {
            IndieGL.glStencilFunc(514, var2, 127);
         }

         IndieGL.glStencilOp(7680, 7680, 7680);
         if (texWhite == null) {
            texWhite = Texture.getSharedTexture("media/ui/white.png");
         }

         Texture var15 = texWhite;
         float var10 = 0.0F;
         float var11 = 0.0F;
         float var12 = 0.0F;
         float var13 = IsoUtils.XToScreenInt(this.x + (int)var10, this.y + (int)var11, this.z + (int)var12, 0);
         float var14 = IsoUtils.YToScreenInt(this.x + (int)var10, this.y + (int)var11, this.z + (int)var12, 0);
         var13 = (float)((int)var13);
         var14 = (float)((int)var14);
         var13 -= (float)((int)IsoCamera.frameState.OffX);
         var14 -= (float)((int)IsoCamera.frameState.OffY);
         if ((var1.highlightFlags & 1) == 0 && var15 != null) {
            var15.renderwallw((int)var13, (int)var14, 64 * Core.TileScale, 32 * Core.TileScale, colu, coll, colu2, coll2);
         }

         IndieGL.End();
         IndieGL.glStencilFunc(519, 1, 255);
         IndieGL.glStencilOp(7680, 7680, 7680);
         this.renderAttachedSpritesWithNoWallLighting(var7, var1, var8, var2);
         this.getCell().setStencilValue(this.x, this.y, this.z, var2);
         return var2 + 1;
      }
   }

   public int DoRoofLighting(IsoObject var1, int var2, int var3, int var4, int var5) {
      IndieGL.End();
      ++var2;
      if (CircleStencil) {
         IndieGL.enableStencilTest();
         IndieGL.enableAlphaTest();
         IndieGL.glAlphaFunc(516, 0.1F);
         IndieGL.glStencilFunc(517, 128, 128);
         IndieGL.glStencilOp(7680, 7680, 7680);
         var1.render((float)this.x, (float)this.y, (float)this.z, defColorInfo, false);
         IndieGL.glStencilFunc(519, var2, 255);
         var1.alpha[IsoPlayer.getPlayerIndex()] = 0.02F;
      } else {
         IndieGL.enableAlphaTest();
         IndieGL.glAlphaFunc(516, 0.02F);
         IndieGL.glStencilFunc(519, var2, 127);
      }

      IndieGL.glStencilOp(7680, 7680, 7681);
      float var6;
      if (CircleStencil) {
         var6 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex());
         defColorInfo.r = var6 * rmod;
         defColorInfo.g = var6 * gmod;
         defColorInfo.b = var6 * bmod;
         var1.render((float)this.x, (float)this.y, (float)this.z, defColorInfo, true);
         defColorInfo.r = 1.0F;
         defColorInfo.g = 1.0F;
         defColorInfo.b = 1.0F;
      } else {
         var1.render((float)this.x, (float)this.y, (float)this.z, defColorInfo, true);
      }

      var1.alpha[IsoPlayer.getPlayerIndex()] = 1.0F;
      IndieGL.End();
      IndieGL.glColorMask(true, true, true, true);
      IndieGL.glAlphaFunc(516, 0.0F);
      if (CircleStencil) {
         IndieGL.glStencilFunc(514, var2, 255);
      } else {
         IndieGL.glStencilFunc(514, var2, 127);
      }

      IndieGL.glStencilOp(7680, 7680, 7680);
      IndieGL.glBlendFunc(0, 768);
      if (texWhite == null) {
         texWhite = Texture.getSharedTexture("media/ui/white.png");
      }

      var6 = 0.0F;
      float var7 = 0.0F;
      float var8 = 0.0F;
      float var9 = IsoUtils.XToScreenInt(this.x + (int)var6, this.y + (int)var7, this.z + (int)var8, 0);
      float var10 = IsoUtils.YToScreenInt(this.x + (int)var6, this.y + (int)var7, this.z + (int)var8, 0);
      var9 = (float)((int)var9);
      var10 = (float)((int)var10);
      var9 -= (float)((int)IsoCamera.getOffX());
      var10 -= (float)((int)IsoCamera.getOffY());
      var10 += 128.0F;
      var10 += 128.0F;
      var10 += 128.0F;
      IndieGL.End();
      IndieGL.glStencilFunc(519, 1, 255);
      IndieGL.glStencilOp(7680, 7680, 7680);
      IndieGL.glBlendFunc(770, 771);
      return var2;
   }

   public KahluaTable getLuaMovingObjectList() {
      KahluaTable var1 = LuaManager.platform.newTable();
      LuaManager.env.rawset("Objects", var1);

      for(int var2 = 0; var2 < this.MovingObjects.size(); ++var2) {
         var1.rawset(var2 + 1, this.MovingObjects.get(var2));
      }

      return var1;
   }

   public boolean Is(IsoFlagType var1) {
      return this.Properties.Is(var1);
   }

   public boolean Is(String var1) {
      return this.Properties.Is(var1);
   }

   public boolean Has(IsoObjectType var1) {
      return this.hasTypes.isSet(var1);
   }

   public boolean isZone(String var1) {
      return IsoWorld.instance.CurrentCell.IsZone(var1, this.x, this.y);
   }

   public void DeleteTileObject(IsoObject var1) {
      this.Objects.remove(var1);
      this.RecalcAllWithNeighbours(true);
   }

   public KahluaTable getLuaTileObjectList() {
      KahluaTable var1 = LuaManager.platform.newTable();
      LuaManager.env.rawset("Objects", var1);

      for(int var2 = 0; var2 < this.Objects.size(); ++var2) {
         var1.rawset(var2 + 1, this.Objects.get(var2));
      }

      return var1;
   }

   boolean HasDoor(boolean var1) {
      for(int var2 = 0; var2 < this.SpecialObjects.size(); ++var2) {
         if (this.SpecialObjects.get(var2) instanceof IsoDoor && ((IsoDoor)this.SpecialObjects.get(var2)).north == var1) {
            return true;
         }

         if (this.SpecialObjects.get(var2) instanceof IsoThumpable && ((IsoThumpable)this.SpecialObjects.get(var2)).isDoor && ((IsoThumpable)this.SpecialObjects.get(var2)).north == var1) {
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

   public boolean isSameStaircase(int var1, int var2, int var3) {
      if (var3 != this.getZ()) {
         return false;
      } else {
         int var4 = this.getX();
         int var5 = this.getY();
         int var6 = var4;
         int var7 = var5;
         if (this.Has(IsoObjectType.stairsTN)) {
            var7 = var5 + 2;
         } else if (this.Has(IsoObjectType.stairsMN)) {
            --var5;
            ++var7;
         } else if (this.Has(IsoObjectType.stairsBN)) {
            var5 -= 2;
         } else if (this.Has(IsoObjectType.stairsTW)) {
            var6 = var4 + 2;
         } else if (this.Has(IsoObjectType.stairsMW)) {
            --var4;
            ++var6;
         } else {
            if (!this.Has(IsoObjectType.stairsBW)) {
               return false;
            }

            var4 -= 2;
         }

         if (var1 >= var4 && var2 >= var5 && var1 <= var6 && var2 <= var7) {
            IsoGridSquare var8 = this.getCell().getGridSquare(var1, var2, var3);
            return var8 != null && var8.HasStairs();
         } else {
            return false;
         }
      }
   }

   public boolean HasTree() {
      return this.hasTree;
   }

   private void fudgeShadowsToAlpha(IsoObject var1, Color var2) {
      float var3 = 1.0F - var1.alpha[IsoPlayer.getPlayerIndex()];
      if (var2.r < var3) {
         var2.r = var3;
      }

      if (var2.g < var3) {
         var2.g = var3;
      }

      if (var2.b < var3) {
         var2.b = var3;
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
   }

   public boolean shouldSave() {
      return !this.Objects.isEmpty();
   }

   public void setDirty() {
      this.bDirty = true;
   }

   public void save(ByteBuffer var1, ObjectOutputStream var2) throws IOException {
      if (GameWindow.DEBUG_SAVE) {
         GameWindow.WriteString(var1, "Number of objects");
      }

      var1.putShort((short)0);
      var1.putInt(this.Objects.size());

      int var3;
      int var4;
      for(var3 = 0; var3 < this.Objects.size(); ++var3) {
         var4 = var1.position();
         if (DEBUG_SAVE) {
            var1.putInt(0);
         }

         if (this.SpecialObjects.contains(this.Objects.get(var3))) {
            var1.put((byte)1);
         } else {
            var1.put((byte)0);
         }

         if (this.WorldObjects.contains(this.Objects.get(var3))) {
            var1.put((byte)1);
         } else {
            var1.put((byte)0);
         }

         if (GameWindow.DEBUG_SAVE) {
            GameWindow.WriteStringUTF(var1, ((IsoObject)this.Objects.get(var3)).getClass().getName());
         }

         ((IsoObject)this.Objects.get(var3)).save(var1);
         if (DEBUG_SAVE) {
            int var5 = var1.position();
            var1.position(var4);
            var1.putInt(var5 - var4);
            var1.position(var5);
         }
      }

      if (DEBUG_SAVE) {
         var1.put((byte)67);
         var1.put((byte)82);
         var1.put((byte)80);
         var1.put((byte)83);
      }

      var3 = 0;

      for(var4 = 0; var4 < this.StaticMovingObjects.size(); ++var4) {
         if (this.StaticMovingObjects.get(var4) instanceof IsoDeadBody) {
            ++var3;
         }
      }

      if (GameWindow.DEBUG_SAVE) {
         GameWindow.WriteString(var1, "Number of bodies");
      }

      var1.putInt(var3);

      for(var4 = 0; var4 < this.StaticMovingObjects.size(); ++var4) {
         IsoMovingObject var6 = (IsoMovingObject)this.StaticMovingObjects.get(var4);
         if (var6 instanceof IsoDeadBody) {
            if (GameWindow.DEBUG_SAVE) {
               GameWindow.WriteStringUTF(var1, var6.getClass().getName());
            }

            var6.save(var1);
         }
      }

      if (this.table != null && !this.table.isEmpty()) {
         var1.put((byte)1);
         this.table.save(var1);
      } else {
         var1.put((byte)0);
      }

      this.bDirty = false;
      byte var7 = 0;
      if (this.isOverlayDone()) {
         var7 = (byte)(var7 | 1);
      }

      if (this.haveRoof) {
         var7 = (byte)(var7 | 2);
      }

      if (this.burntOut) {
         var7 = (byte)(var7 | 4);
      }

      var1.put(var7);
      this.getErosionData().save(var1);
      if (this.getTrapPositionX() > 0) {
         var1.put((byte)1);
         var1.putInt(this.getTrapPositionX());
         var1.putInt(this.getTrapPositionY());
         var1.putInt(this.getTrapPositionZ());
      } else {
         var1.put((byte)0);
      }

      var1.put((byte)(this.haveElectricity() ? 1 : 0));
      var1.put((byte)(this.haveSheetRope ? 1 : 0));
   }

   static void loadmatrix(boolean[][][] var0, DataInputStream var1) throws IOException {
   }

   static void savematrix(boolean[][][] var0, DataOutputStream var1) throws IOException {
      for(int var2 = 0; var2 < 3; ++var2) {
         for(int var3 = 0; var3 < 3; ++var3) {
            for(int var4 = 0; var4 < 3; ++var4) {
               var1.writeBoolean(var0[var2][var3][var4]);
            }
         }
      }

   }

   public boolean isCommonGrass() {
      if (this.Objects.isEmpty()) {
         return false;
      } else {
         IsoObject var1 = (IsoObject)this.Objects.get(0);
         return var1.sprite.getProperties().Is(IsoFlagType.solidfloor) && ("TileFloorExt_3".equals(var1.tile) || "TileFloorExt_4".equals(var1.tile));
      }
   }

   public static boolean toBoolean(byte[] var0) {
      return var0 != null && var0.length != 0 ? var0[0] != 0 : false;
   }

   public void removeCorpse(IsoDeadBody var1, boolean var2) {
      if (GameClient.bClient && !var2) {
         try {
            GameClient.instance.checkAddedRemovedItems(var1);
         } catch (Exception var4) {
            GameClient.connection.cancelPacket();
            ExceptionLogger.logException(var4);
         }

         ByteBufferWriter var3 = GameClient.connection.startPacket();
         PacketTypes.doPacket((short)68, var3);
         var3.putInt(this.x);
         var3.putInt(this.y);
         var3.putInt(this.z);
         var3.putInt(this.StaticMovingObjects.indexOf(var1));
         GameClient.connection.endPacketImmediate();
         this.clientModify();
      }

      var1.removeFromWorld();
      var1.removeFromSquare();
      if (!GameServer.bServer) {
         LuaEventManager.triggerEvent("OnContainerUpdate", this);
      }

   }

   public IsoDeadBody getDeadBody() {
      for(int var1 = 0; var1 < this.StaticMovingObjects.size(); ++var1) {
         if (this.StaticMovingObjects.get(var1) instanceof IsoDeadBody) {
            return (IsoDeadBody)this.StaticMovingObjects.get(var1);
         }
      }

      return null;
   }

   public List getDeadBodys() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < this.StaticMovingObjects.size(); ++var2) {
         if (this.StaticMovingObjects.get(var2) instanceof IsoDeadBody) {
            var1.add((IsoDeadBody)this.StaticMovingObjects.get(var2));
         }
      }

      return var1;
   }

   public void addCorpse(IsoDeadBody var1, boolean var2) {
      if (GameClient.bClient && !var2) {
         ByteBufferWriter var3 = GameClient.connection.startPacket();
         PacketTypes.doPacket((short)69, var3);
         var3.putInt(this.x);
         var3.putInt(this.y);
         var3.putInt(this.z);
         var1.writeToRemoteBuffer(var3);
         GameClient.connection.endPacketImmediate();
      }

      if (!this.StaticMovingObjects.contains(var1)) {
         this.StaticMovingObjects.add(var1);
      }

      var1.addToWorld();
      this.burntOut = false;
      this.Properties.UnSet(IsoFlagType.burntOut);
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      this.hourLastSeen = var1.getShort();
      int var3 = var1.getInt();

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         int var5 = var1.position();
         int var6 = 0;
         if (DEBUG_SAVE) {
            if (var2 >= 126) {
               var6 = var1.getInt();
            } else {
               var6 = var1.getShort();
            }
         }

         boolean var7 = var1.get() == 1;
         boolean var8 = var1.get() == 1;
         IsoObject var9 = null;
         String var10;
         if (GameWindow.DEBUG_SAVE) {
            var10 = GameWindow.ReadStringUTF(var1);
            DebugLog.log(var10);
         }

         var9 = IsoObject.factoryFromFileInput(this.getCell(), var1);
         int var21;
         if (var9 == null) {
            if (DEBUG_SAVE) {
               var21 = var1.position();
               if (var21 - var5 != var6) {
                  DebugLog.log("***** Object loaded size " + (var21 - var5) + " != saved size " + var6);
               }
            }
         } else {
            var9.square = this;

            try {
               var9.load(var1, var2);
            } catch (Exception var14) {
               this.debugPrintGridSquare();
               if (lastLoaded != null) {
                  lastLoaded.debugPrintGridSquare();
               }

               throw new RuntimeException(var14);
            }

            if (DEBUG_SAVE) {
               var21 = var1.position();
               if (var21 - var5 != var6) {
                  DebugLog.log("***** Object loaded size " + (var21 - var5) + " != saved size " + var6);
               }
            }

            if (var9 instanceof IsoWorldInventoryObject) {
               if (((IsoWorldInventoryObject)var9).getItem() == null) {
                  continue;
               }

               var10 = ((IsoWorldInventoryObject)var9).getItem().getFullType();
               Item var11 = ScriptManager.instance.FindItem(var10);
               if (var11 != null && var11.getObsolete() || (GameServer.bServer || GameClient.bClient) && ((IsoWorldInventoryObject)var9).dropTime > -1.0D && ServerOptions.instance.HoursForWorldItemRemoval.getValue() > 0.0D && (ServerOptions.instance.WorldItemRemovalList.getValue().contains(var10) && !ServerOptions.instance.ItemRemovalListBlacklistToggle.getValue() || !ServerOptions.instance.WorldItemRemovalList.getValue().contains(var10) && ServerOptions.instance.ItemRemovalListBlacklistToggle.getValue()) && GameTime.instance.getWorldAgeHours() > ((IsoWorldInventoryObject)var9).dropTime + ServerOptions.instance.HoursForWorldItemRemoval.getValue()) {
                  continue;
               }
            }

            if (!(var9 instanceof IsoWindow) || var9.getSprite() == null || !"walls_special_01_8".equals(var9.getSprite().getName()) && !"walls_special_01_9".equals(var9.getSprite().getName())) {
               this.Objects.add(var9);
               if (var7) {
                  this.SpecialObjects.add(var9);
               }

               if (var8) {
                  this.WorldObjects.add((IsoWorldInventoryObject)var9);
                  var9.square.chunk.recalcHashCodeObjects();
               }
            }
         }
      }

      byte var15;
      if (DEBUG_SAVE) {
         var15 = var1.get();
         byte var16 = var1.get();
         byte var19 = var1.get();
         byte var18 = var1.get();
         if (var15 != 67 || var16 != 82 || var19 != 80 || var18 != 83) {
            DebugLog.log("***** Expected CRPS here");
         }
      }

      var3 = var1.getInt();

      for(var4 = 0; var4 < var3; ++var4) {
         IsoMovingObject var17 = null;
         if (GameWindow.DEBUG_SAVE) {
            String var20 = GameWindow.ReadStringUTF(var1);
            DebugLog.log(var20);
         }

         try {
            var17 = (IsoMovingObject)IsoObject.factoryFromFileInput(this.getCell(), var1);
         } catch (Exception var12) {
            this.debugPrintGridSquare();
            if (lastLoaded != null) {
               lastLoaded.debugPrintGridSquare();
            }

            throw new RuntimeException(var12);
         }

         if (var17 != null) {
            var17.square = this;
            var17.current = this;

            try {
               var17.load(var1, var2);
            } catch (Exception var13) {
               this.debugPrintGridSquare();
               if (lastLoaded != null) {
                  lastLoaded.debugPrintGridSquare();
               }

               throw new RuntimeException(var13);
            }

            this.StaticMovingObjects.add(var17);
            this.recalcHashCodeObjects();
         }
      }

      if (var1.get() != 0) {
         if (this.table == null) {
            this.table = LuaManager.platform.newTable();
         }

         this.table.load(var1, var2);
      }

      if (var2 >= 34) {
         if (var2 < 39) {
            this.setOverlayDone(var1.get() == 1);
            this.haveRoof = var1.get() == 1;
         } else {
            var15 = var1.get();
            this.setOverlayDone((var15 & 1) != 0);
            this.haveRoof = (var15 & 2) != 0;
            this.burntOut = (var15 & 4) != 0;
         }
      }

      if (var2 >= 45) {
         this.getErosionData().load(var1, var2);
      }

      if (var2 >= 62) {
         if (var1.get() == 1) {
            this.setTrapPositionX(var1.getInt());
            this.setTrapPositionY(var1.getInt());
            this.setTrapPositionZ(var1.getInt());
         }

         if (GameClient.bClient) {
            var1.get();
         } else {
            this.haveElectricity = var1.get() == 1;
         }
      }

      if (var2 >= 108) {
         this.haveSheetRope = var1.get() == 1;
      }

      lastLoaded = this;
   }

   private void debugPrintGridSquare() {
      System.out.println("x=" + this.x + " y=" + this.y + " z=" + this.z);
      System.out.println("objects");

      int var1;
      for(var1 = 0; var1 < this.Objects.size(); ++var1) {
         ((IsoObject)this.Objects.get(var1)).debugPrintout();
      }

      System.out.println("staticmovingobjects");

      for(var1 = 0; var1 < this.StaticMovingObjects.size(); ++var1) {
         ((IsoObject)this.Objects.get(var1)).debugPrintout();
      }

   }

   public float scoreAsWaypoint(int var1, int var2) {
      float var3 = 2.0F;
      var3 -= IsoUtils.DistanceManhatten((float)var1, (float)var2, (float)this.getX(), (float)this.getY()) * 5.0F;
      return var3;
   }

   public void InvalidateSpecialObjectPaths() {
   }

   public boolean isSolid() {
      return this.Properties.Is(IsoFlagType.solid);
   }

   public boolean isSolidTrans() {
      return this.Properties.Is(IsoFlagType.solidtrans);
   }

   public boolean isFree(boolean var1) {
      if (var1 && this.MovingObjects.size() > 0) {
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

   public boolean isFreeOrMidair(boolean var1) {
      if (var1 && this.MovingObjects.size() > 0) {
         return false;
      } else {
         boolean var2 = true;
         if (this.Properties.Is(IsoFlagType.solid) || this.Properties.Is(IsoFlagType.solidtrans) || this.Has(IsoObjectType.tree)) {
            var2 = false;
         }

         if (!this.Has(IsoObjectType.stairsBN) && !this.Has(IsoObjectType.stairsMN) && !this.Has(IsoObjectType.stairsTN)) {
            if (this.Has(IsoObjectType.stairsBW) || this.Has(IsoObjectType.stairsMW) || this.Has(IsoObjectType.stairsTW)) {
               var2 = true;
            }
         } else {
            var2 = true;
         }

         return var2;
      }
   }

   public boolean isFreeOrMidair(boolean var1, boolean var2) {
      if (var1 && this.MovingObjects.size() > 0) {
         if (!var2) {
            return false;
         }

         for(int var3 = 0; var3 < this.MovingObjects.size(); ++var3) {
            IsoMovingObject var4 = (IsoMovingObject)this.MovingObjects.get(var3);
            if (!(var4 instanceof IsoDeadBody)) {
               return false;
            }
         }
      }

      boolean var5 = true;
      if (this.Properties.Is(IsoFlagType.solid) || this.Properties.Is(IsoFlagType.solidtrans) || this.Has(IsoObjectType.tree)) {
         var5 = false;
      }

      if (!this.Has(IsoObjectType.stairsBN) && !this.Has(IsoObjectType.stairsMN) && !this.Has(IsoObjectType.stairsTN)) {
         if (this.Has(IsoObjectType.stairsBW) || this.Has(IsoObjectType.stairsMW) || this.Has(IsoObjectType.stairsTW)) {
            var5 = true;
         }
      } else {
         var5 = true;
      }

      return var5;
   }

   public boolean connectedWithFloor() {
      if (this.getZ() == 0) {
         return true;
      } else {
         IsoGridSquare var1 = null;
         var1 = this.getCell().getGridSquare(this.getX() - 1, this.getY(), this.getZ());
         if (var1 != null && var1.Properties.Is(IsoFlagType.solidfloor)) {
            return true;
         } else {
            var1 = this.getCell().getGridSquare(this.getX() + 1, this.getY(), this.getZ());
            if (var1 != null && var1.Properties.Is(IsoFlagType.solidfloor)) {
               return true;
            } else {
               var1 = this.getCell().getGridSquare(this.getX(), this.getY() - 1, this.getZ());
               if (var1 != null && var1.Properties.Is(IsoFlagType.solidfloor)) {
                  return true;
               } else {
                  var1 = this.getCell().getGridSquare(this.getX(), this.getY() + 1, this.getZ());
                  return var1 != null && var1.Properties.Is(IsoFlagType.solidfloor);
               }
            }
         }
      }
   }

   public boolean hasFloor(boolean var1) {
      if (this.Properties.Is(IsoFlagType.solidfloor)) {
         return true;
      } else {
         IsoGridSquare var2 = null;
         if (var1) {
            var2 = this.getCell().getGridSquare(this.getX(), this.getY() - 1, this.getZ());
         } else {
            var2 = this.getCell().getGridSquare(this.getX() - 1, this.getY(), this.getZ());
         }

         return var2 != null && var2.Properties.Is(IsoFlagType.solidfloor);
      }
   }

   public boolean isNotBlocked(boolean var1) {
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

      return !var1 || this.MovingObjects.size() <= 0;
   }

   public IsoObject getDoor(boolean var1) {
      for(int var2 = 0; var2 < this.SpecialObjects.size(); ++var2) {
         IsoObject var3 = (IsoObject)this.SpecialObjects.get(var2);
         if (var3 instanceof IsoThumpable) {
            IsoThumpable var4 = (IsoThumpable)var3;
            if (var4.isDoor() && var1 == var4.north) {
               return var4;
            }
         }

         if (var3 instanceof IsoDoor) {
            IsoDoor var5 = (IsoDoor)var3;
            if (var1 == var5.north) {
               return var5;
            }
         }
      }

      return null;
   }

   public IsoDoor getIsoDoor() {
      for(int var1 = 0; var1 < this.SpecialObjects.size(); ++var1) {
         IsoObject var2 = (IsoObject)this.SpecialObjects.get(var1);
         if (var2 instanceof IsoDoor) {
            return (IsoDoor)var2;
         }
      }

      return null;
   }

   public IsoObject getDoorTo(IsoGridSquare var1) {
      if (var1 != null && var1 != this) {
         IsoObject var2 = null;
         if (var1.x < this.x) {
            var2 = this.getDoor(false);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.y < this.y) {
            var2 = this.getDoor(true);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.x > this.x) {
            var2 = var1.getDoor(false);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.y > this.y) {
            var2 = var1.getDoor(true);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.x != this.x && var1.y != this.y) {
            IsoGridSquare var3 = this.getCell().getGridSquare(this.x, var1.y, this.z);
            IsoGridSquare var4 = this.getCell().getGridSquare(var1.x, this.y, this.z);
            var2 = this.getDoorTo(var3);
            if (var2 != null) {
               return var2;
            }

            var2 = this.getDoorTo(var4);
            if (var2 != null) {
               return var2;
            }

            var2 = var1.getDoorTo(var3);
            if (var2 != null) {
               return var2;
            }

            var2 = var1.getDoorTo(var4);
            if (var2 != null) {
               return var2;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public IsoWindow getWindow(boolean var1) {
      for(int var2 = 0; var2 < this.SpecialObjects.size(); ++var2) {
         IsoObject var3 = (IsoObject)this.SpecialObjects.get(var2);
         if (var3 instanceof IsoWindow) {
            IsoWindow var4 = (IsoWindow)var3;
            if (var1 == var4.north) {
               return var4;
            }
         }
      }

      return null;
   }

   public IsoWindow getWindow() {
      for(int var1 = 0; var1 < this.SpecialObjects.size(); ++var1) {
         IsoObject var2 = (IsoObject)this.SpecialObjects.get(var1);
         if (var2 instanceof IsoWindow) {
            return (IsoWindow)var2;
         }
      }

      return null;
   }

   public IsoWindow getWindowTo(IsoGridSquare var1) {
      if (var1 != null && var1 != this) {
         IsoWindow var2 = null;
         if (var1.x < this.x) {
            var2 = this.getWindow(false);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.y < this.y) {
            var2 = this.getWindow(true);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.x > this.x) {
            var2 = var1.getWindow(false);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.y > this.y) {
            var2 = var1.getWindow(true);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.x != this.x && var1.y != this.y) {
            IsoGridSquare var3 = this.getCell().getGridSquare(this.x, var1.y, this.z);
            IsoGridSquare var4 = this.getCell().getGridSquare(var1.x, this.y, this.z);
            var2 = this.getWindowTo(var3);
            if (var2 != null) {
               return var2;
            }

            var2 = this.getWindowTo(var4);
            if (var2 != null) {
               return var2;
            }

            var2 = var1.getWindowTo(var3);
            if (var2 != null) {
               return var2;
            }

            var2 = var1.getWindowTo(var4);
            if (var2 != null) {
               return var2;
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
         IsoGridSquare var1 = this.nav[IsoDirections.S.index()];
         if (var1 != null && var1.getWindow(true) != null) {
            return true;
         } else {
            IsoGridSquare var2 = this.nav[IsoDirections.E.index()];
            return var2 != null && var2.getWindow(false) != null;
         }
      }
   }

   public IsoThumpable getThumpableWindow(boolean var1) {
      for(int var2 = 0; var2 < this.SpecialObjects.size(); ++var2) {
         IsoObject var3 = (IsoObject)this.SpecialObjects.get(var2);
         if (var3 instanceof IsoThumpable) {
            IsoThumpable var4 = (IsoThumpable)var3;
            if (var4.isWindow() && var1 == var4.north) {
               return var4;
            }
         }
      }

      return null;
   }

   public IsoThumpable getWindowThumpableTo(IsoGridSquare var1) {
      if (var1 != null && var1 != this) {
         IsoThumpable var2 = null;
         if (var1.x < this.x) {
            var2 = this.getThumpableWindow(false);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.y < this.y) {
            var2 = this.getThumpableWindow(true);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.x > this.x) {
            var2 = var1.getThumpableWindow(false);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.y > this.y) {
            var2 = var1.getThumpableWindow(true);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.x != this.x && var1.y != this.y) {
            IsoGridSquare var3 = this.getCell().getGridSquare(this.x, var1.y, this.z);
            IsoGridSquare var4 = this.getCell().getGridSquare(var1.x, this.y, this.z);
            var2 = this.getWindowThumpableTo(var3);
            if (var2 != null) {
               return var2;
            }

            var2 = this.getWindowThumpableTo(var4);
            if (var2 != null) {
               return var2;
            }

            var2 = var1.getWindowThumpableTo(var3);
            if (var2 != null) {
               return var2;
            }

            var2 = var1.getWindowThumpableTo(var4);
            if (var2 != null) {
               return var2;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public IsoThumpable getHoppableThumpable(boolean var1) {
      for(int var2 = 0; var2 < this.SpecialObjects.size(); ++var2) {
         IsoObject var3 = (IsoObject)this.SpecialObjects.get(var2);
         if (var3 instanceof IsoThumpable) {
            IsoThumpable var4 = (IsoThumpable)var3;
            if (var4.isHoppable() && var1 == var4.north) {
               return var4;
            }
         }
      }

      return null;
   }

   public IsoThumpable getHoppableThumpableTo(IsoGridSquare var1) {
      if (var1 != null && var1 != this) {
         IsoThumpable var2 = null;
         if (var1.x < this.x) {
            var2 = this.getHoppableThumpable(false);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.y < this.y) {
            var2 = this.getHoppableThumpable(true);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.x > this.x) {
            var2 = var1.getHoppableThumpable(false);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.y > this.y) {
            var2 = var1.getHoppableThumpable(true);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.x != this.x && var1.y != this.y) {
            IsoGridSquare var3 = this.getCell().getGridSquare(this.x, var1.y, this.z);
            IsoGridSquare var4 = this.getCell().getGridSquare(var1.x, this.y, this.z);
            var2 = this.getHoppableThumpableTo(var3);
            if (var2 != null) {
               return var2;
            }

            var2 = this.getHoppableThumpableTo(var4);
            if (var2 != null) {
               return var2;
            }

            var2 = var1.getHoppableThumpableTo(var3);
            if (var2 != null) {
               return var2;
            }

            var2 = var1.getHoppableThumpableTo(var4);
            if (var2 != null) {
               return var2;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public IsoObject getBedTo(IsoGridSquare var1) {
      ArrayList var2 = null;
      if (var1.y >= this.y && var1.x >= this.x) {
         var2 = var1.SpecialObjects;
      } else {
         var2 = this.SpecialObjects;
      }

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         IsoObject var4 = (IsoObject)var2.get(var3);
         if (var4.getProperties().Is(IsoFlagType.bed)) {
            return var4;
         }
      }

      return null;
   }

   public IsoObject getWindowFrame(boolean var1) {
      for(int var2 = 0; var2 < this.Objects.size(); ++var2) {
         IsoObject var3 = (IsoObject)this.Objects.get(var2);
         if (!(var3 instanceof IsoWorldInventoryObject) && IsoWindowFrame.isWindowFrame(var3, var1)) {
            return var3;
         }
      }

      return null;
   }

   public IsoObject getWindowFrameTo(IsoGridSquare var1) {
      if (var1 != null && var1 != this) {
         IsoObject var2 = null;
         if (var1.x < this.x) {
            var2 = this.getWindowFrame(false);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.y < this.y) {
            var2 = this.getWindowFrame(true);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.x > this.x) {
            var2 = var1.getWindowFrame(false);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.y > this.y) {
            var2 = var1.getWindowFrame(true);
            if (var2 != null) {
               return var2;
            }
         }

         if (var1.x != this.x && var1.y != this.y) {
            IsoGridSquare var3 = this.getCell().getGridSquare(this.x, var1.y, this.z);
            IsoGridSquare var4 = this.getCell().getGridSquare(var1.x, this.y, this.z);
            var2 = this.getWindowFrameTo(var3);
            if (var2 != null) {
               return var2;
            }

            var2 = this.getWindowFrameTo(var4);
            if (var2 != null) {
               return var2;
            }

            var2 = var1.getWindowFrameTo(var3);
            if (var2 != null) {
               return var2;
            }

            var2 = var1.getWindowFrameTo(var4);
            if (var2 != null) {
               return var2;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private IsoObject getSpecialWall(boolean var1) {
      for(int var2 = this.SpecialObjects.size() - 1; var2 >= 0; --var2) {
         IsoObject var3 = (IsoObject)this.SpecialObjects.get(var2);
         if (var3 instanceof IsoThumpable) {
            IsoThumpable var4 = (IsoThumpable)var3;
            if (var4.isStairs() || !var4.isThumpable() && !var4.isWindow() || var4.isCanPassThrough() || var4.isDoor() && var4.open || var4.isBlockAllTheSquare()) {
               continue;
            }

            if (var1 == var4.north) {
               return var4;
            }
         }

         if (var3 instanceof IsoWindow) {
            IsoWindow var6 = (IsoWindow)var3;
            if (var1 == var6.north) {
               return var6;
            }
         }

         if (var3 instanceof IsoDoor) {
            IsoDoor var7 = (IsoDoor)var3;
            if (var1 == var7.north && !var7.open) {
               return var7;
            }
         }
      }

      if (var1 && !this.Is(IsoFlagType.WindowN) || !var1 && !this.Is(IsoFlagType.WindowW)) {
         return null;
      } else {
         IsoObject var5 = this.getWindowFrame(var1);
         if (var5 != null) {
            return var5;
         } else {
            return null;
         }
      }
   }

   public IsoObject getSheetRope() {
      for(int var1 = 0; var1 < this.getObjects().size(); ++var1) {
         IsoObject var2 = (IsoObject)this.getObjects().get(var1);
         if (var2.sheetRope) {
            return var2;
         }
      }

      return null;
   }

   public boolean damageSpriteSheetRopeFromBottom(IsoPlayer var1, boolean var2) {
      IsoGridSquare var4 = this;
      IsoFlagType var3;
      if (var2) {
         if (this.Is(IsoFlagType.climbSheetN)) {
            var3 = IsoFlagType.climbSheetN;
         } else {
            if (!this.Is(IsoFlagType.climbSheetS)) {
               return false;
            }

            var3 = IsoFlagType.climbSheetS;
         }
      } else if (this.Is(IsoFlagType.climbSheetW)) {
         var3 = IsoFlagType.climbSheetW;
      } else {
         if (!this.Is(IsoFlagType.climbSheetE)) {
            return false;
         }

         var3 = IsoFlagType.climbSheetE;
      }

      while(var4 != null) {
         for(int var5 = 0; var5 < var4.getObjects().size(); ++var5) {
            IsoObject var6 = (IsoObject)var4.getObjects().get(var5);
            if (var6.getProperties() != null && var6.getProperties().Is(var3)) {
               int var7 = Integer.parseInt(var6.getSprite().getName().split("_")[2]);
               if (var7 > 14) {
                  return false;
               }

               String var8 = var6.getSprite().getName().split("_")[0] + "_" + var6.getSprite().getName().split("_")[1];
               var7 += 40;
               var6.setSprite(IsoWorld.instance.spriteManager.getSprite(var8 + "_" + var7));
               var6.transmitUpdatedSpriteToClients();
               break;
            }
         }

         if (var4.getZ() == 7) {
            break;
         }

         var4 = var4.getCell().getGridSquare(var4.getX(), var4.getY(), var4.getZ() + 1);
      }

      return true;
   }

   public boolean removeSheetRopeFromBottom(IsoPlayer var1, boolean var2) {
      IsoGridSquare var6 = this;
      IsoFlagType var3;
      IsoFlagType var4;
      String var5;
      int var7;
      IsoObject var8;
      if (var2) {
         if (this.Is(IsoFlagType.climbSheetN)) {
            var3 = IsoFlagType.climbSheetTopN;
            var4 = IsoFlagType.climbSheetN;
         } else {
            if (!this.Is(IsoFlagType.climbSheetS)) {
               return false;
            }

            var3 = IsoFlagType.climbSheetTopS;
            var4 = IsoFlagType.climbSheetS;
            var5 = "crafted_01_4";

            for(var7 = 0; var7 < var6.getObjects().size(); ++var7) {
               var8 = (IsoObject)var6.getObjects().get(var7);
               if (var8.sprite != null && var8.sprite.getName() != null && var8.sprite.getName().equals(var5)) {
                  if (GameServer.bServer) {
                     var6.transmitRemoveItemFromSquare(var8);
                  }

                  var6.RemoveTileObject(var8);
                  break;
               }
            }
         }
      } else if (this.Is(IsoFlagType.climbSheetW)) {
         var3 = IsoFlagType.climbSheetTopW;
         var4 = IsoFlagType.climbSheetW;
      } else {
         if (!this.Is(IsoFlagType.climbSheetE)) {
            return false;
         }

         var3 = IsoFlagType.climbSheetTopE;
         var4 = IsoFlagType.climbSheetE;
         var5 = "crafted_01_3";

         for(var7 = 0; var7 < var6.getObjects().size(); ++var7) {
            var8 = (IsoObject)var6.getObjects().get(var7);
            if (var8.sprite != null && var8.sprite.getName() != null && var8.sprite.getName().equals(var5)) {
               if (GameServer.bServer) {
                  var6.transmitRemoveItemFromSquare(var8);
               }

               var6.RemoveTileObject(var8);
               break;
            }
         }
      }

      boolean var12 = false;

      IsoGridSquare var13;
      for(var13 = null; var6 != null; var12 = false) {
         for(int var9 = 0; var9 < var6.getObjects().size(); ++var9) {
            IsoObject var10 = (IsoObject)var6.getObjects().get(var9);
            if (var10.getProperties() != null && (var10.getProperties().Is(var3) || var10.getProperties().Is(var4))) {
               var13 = var6;
               var12 = true;
               if (GameServer.bServer) {
                  var6.transmitRemoveItemFromSquare(var10);
                  var6.RemoveTileObject(var10);
                  if (var1 != null) {
                     var1.sendObjectChange("addItemOfType", new Object[]{"type", var10.getName()});
                  }
               } else {
                  var6.RemoveTileObject(var10);
                  if (var1 != null) {
                     var1.getInventory().AddItem(var10.getName());
                  }
               }
               break;
            }
         }

         if (var6.getZ() == 7) {
            break;
         }

         var6 = var6.getCell().getGridSquare(var6.getX(), var6.getY(), var6.getZ() + 1);
      }

      if (!var12) {
         var6 = var13.getCell().getGridSquare(var13.getX(), var13.getY(), var13.getZ());
         IsoGridSquare var14 = var2 ? var6.nav[IsoDirections.S.index()] : var6.nav[IsoDirections.E.index()];
         if (var14 == null) {
            return true;
         }

         for(int var15 = 0; var15 < var14.getObjects().size(); ++var15) {
            IsoObject var11 = (IsoObject)var14.getObjects().get(var15);
            if (var11.getProperties() != null && (var11.getProperties().Is(var3) || var11.getProperties().Is(var4))) {
               if (GameServer.bServer) {
                  var14.transmitRemoveItemFromSquare(var11);
                  var14.RemoveTileObject(var11);
               } else {
                  var14.RemoveTileObject(var11);
               }
               break;
            }
         }
      }

      return true;
   }

   private IsoObject getSpecialSolid() {
      for(int var1 = 0; var1 < this.SpecialObjects.size(); ++var1) {
         IsoObject var2 = (IsoObject)this.SpecialObjects.get(var1);
         if (var2 instanceof IsoThumpable) {
            IsoThumpable var3 = (IsoThumpable)var2;
            if (!var3.isStairs() && var3.isThumpable() && !var3.isCanPassThrough() && var3.isBlockAllTheSquare()) {
               if (var3.getProperties().Is(IsoFlagType.solidtrans)) {
                  if (this.getWindow(true) != null || this.getWindow(false) != null) {
                     return null;
                  }

                  IsoGridSquare var4 = this.nav[IsoDirections.S.index()];
                  if (var4 != null && var4.getWindow(true) != null) {
                     return null;
                  }

                  IsoGridSquare var5 = this.nav[IsoDirections.E.index()];
                  if (var5 != null && var5.getWindow(false) != null) {
                     return null;
                  }
               }

               return var3;
            }
         }
      }

      return null;
   }

   public IsoObject testCollideSpecialObjects(IsoGridSquare var1) {
      if (var1 != null && var1 != this) {
         IsoObject var2;
         if (var1.x < this.x && var1.y == this.y) {
            if (var1.z == this.z && this.Has(IsoObjectType.stairsTW)) {
               return null;
            } else {
               var2 = this.getSpecialWall(false);
               if (var2 != null) {
                  return var2;
               } else if (this.isBlockedTo(var1)) {
                  return null;
               } else {
                  var2 = var1.getSpecialSolid();
                  return var2 != null ? var2 : null;
               }
            }
         } else if (var1.x == this.x && var1.y < this.y) {
            if (var1.z == this.z && this.Has(IsoObjectType.stairsTN)) {
               return null;
            } else {
               var2 = this.getSpecialWall(true);
               if (var2 != null) {
                  return var2;
               } else if (this.isBlockedTo(var1)) {
                  return null;
               } else {
                  var2 = var1.getSpecialSolid();
                  return var2 != null ? var2 : null;
               }
            }
         } else if (var1.x > this.x && var1.y == this.y) {
            var2 = var1.getSpecialWall(false);
            if (var2 != null) {
               return var2;
            } else if (this.isBlockedTo(var1)) {
               return null;
            } else {
               var2 = var1.getSpecialSolid();
               return var2 != null ? var2 : null;
            }
         } else if (var1.x == this.x && var1.y > this.y) {
            var2 = var1.getSpecialWall(true);
            if (var2 != null) {
               return var2;
            } else if (this.isBlockedTo(var1)) {
               return null;
            } else {
               var2 = var1.getSpecialSolid();
               return var2 != null ? var2 : null;
            }
         } else {
            IsoGridSquare var3;
            IsoGridSquare var4;
            if (var1.x < this.x && var1.y < this.y) {
               var2 = this.getSpecialWall(true);
               if (var2 != null) {
                  return var2;
               } else {
                  var2 = this.getSpecialWall(false);
                  if (var2 != null) {
                     return var2;
                  } else {
                     var3 = this.getCell().getGridSquare(this.x, this.y - 1, this.z);
                     if (var3 != null && !this.isBlockedTo(var3)) {
                        var2 = var3.getSpecialSolid();
                        if (var2 != null) {
                           return var2;
                        }

                        var2 = var3.getSpecialWall(false);
                        if (var2 != null) {
                           return var2;
                        }
                     }

                     var4 = this.getCell().getGridSquare(this.x - 1, this.y, this.z);
                     if (var4 != null && !this.isBlockedTo(var4)) {
                        var2 = var4.getSpecialSolid();
                        if (var2 != null) {
                           return var2;
                        }

                        var2 = var4.getSpecialWall(true);
                        if (var2 != null) {
                           return var2;
                        }
                     }

                     if (var3 != null && !this.isBlockedTo(var3) && var4 != null && !this.isBlockedTo(var4)) {
                        if (!var3.isBlockedTo(var1) && !var4.isBlockedTo(var1)) {
                           var2 = var1.getSpecialSolid();
                           return var2 != null ? var2 : null;
                        } else {
                           return null;
                        }
                     } else {
                        return null;
                     }
                  }
               }
            } else if (var1.x > this.x && var1.y < this.y) {
               var2 = this.getSpecialWall(true);
               if (var2 != null) {
                  return var2;
               } else {
                  var3 = this.getCell().getGridSquare(this.x, this.y - 1, this.z);
                  if (var3 != null && !this.isBlockedTo(var3)) {
                     var2 = var3.getSpecialSolid();
                     if (var2 != null) {
                        return var2;
                     }
                  }

                  var4 = this.getCell().getGridSquare(this.x + 1, this.y, this.z);
                  if (var4 != null) {
                     var2 = var4.getSpecialWall(false);
                     if (var2 != null) {
                        return var2;
                     }

                     if (!this.isBlockedTo(var4)) {
                        var2 = var4.getSpecialSolid();
                        if (var2 != null) {
                           return var2;
                        }

                        var2 = var4.getSpecialWall(true);
                        if (var2 != null) {
                           return var2;
                        }
                     }
                  }

                  if (var3 != null && !this.isBlockedTo(var3) && var4 != null && !this.isBlockedTo(var4)) {
                     var2 = var1.getSpecialWall(false);
                     if (var2 != null) {
                        return var2;
                     } else if (!var3.isBlockedTo(var1) && !var4.isBlockedTo(var1)) {
                        var2 = var1.getSpecialSolid();
                        return var2 != null ? var2 : null;
                     } else {
                        return null;
                     }
                  } else {
                     return null;
                  }
               }
            } else if (var1.x > this.x && var1.y > this.y) {
               var3 = this.getCell().getGridSquare(this.x, this.y + 1, this.z);
               if (var3 != null) {
                  var2 = var3.getSpecialWall(true);
                  if (var2 != null) {
                     return var2;
                  }

                  if (!this.isBlockedTo(var3)) {
                     var2 = var3.getSpecialSolid();
                     if (var2 != null) {
                        return var2;
                     }
                  }
               }

               var4 = this.getCell().getGridSquare(this.x + 1, this.y, this.z);
               if (var4 != null) {
                  var2 = var4.getSpecialWall(false);
                  if (var2 != null) {
                     return var2;
                  }

                  if (!this.isBlockedTo(var4)) {
                     var2 = var4.getSpecialSolid();
                     if (var2 != null) {
                        return var2;
                     }
                  }
               }

               if (var3 != null && !this.isBlockedTo(var3) && var4 != null && !this.isBlockedTo(var4)) {
                  var2 = var1.getSpecialWall(false);
                  if (var2 != null) {
                     return var2;
                  } else {
                     var2 = var1.getSpecialWall(true);
                     if (var2 != null) {
                        return var2;
                     } else if (!var3.isBlockedTo(var1) && !var4.isBlockedTo(var1)) {
                        var2 = var1.getSpecialSolid();
                        return var2 != null ? var2 : null;
                     } else {
                        return null;
                     }
                  }
               } else {
                  return null;
               }
            } else if (var1.x < this.x && var1.y > this.y) {
               var2 = this.getSpecialWall(false);
               if (var2 != null) {
                  return var2;
               } else {
                  var3 = this.getCell().getGridSquare(this.x, this.y + 1, this.z);
                  if (var3 != null) {
                     var2 = var3.getSpecialWall(true);
                     if (var2 != null) {
                        return var2;
                     }

                     if (!this.isBlockedTo(var3)) {
                        var2 = var3.getSpecialSolid();
                        if (var2 != null) {
                           return var2;
                        }
                     }
                  }

                  var4 = this.getCell().getGridSquare(this.x - 1, this.y, this.z);
                  if (var4 != null && !this.isBlockedTo(var4)) {
                     var2 = var4.getSpecialSolid();
                     if (var2 != null) {
                        return var2;
                     }
                  }

                  if (var3 != null && !this.isBlockedTo(var3) && var4 != null && !this.isBlockedTo(var4)) {
                     var2 = var1.getSpecialWall(true);
                     if (var2 != null) {
                        return var2;
                     } else if (!var3.isBlockedTo(var1) && !var4.isBlockedTo(var1)) {
                        var2 = var1.getSpecialSolid();
                        return var2 != null ? var2 : null;
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

   public IsoObject getDoorFrameTo(IsoGridSquare var1) {
      ArrayList var2 = null;
      if (var1.y >= this.y && var1.x >= this.x) {
         var2 = var1.SpecialObjects;
      } else {
         var2 = this.SpecialObjects;
      }

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         boolean var5;
         if (var2.get(var3) instanceof IsoDoor) {
            IsoDoor var4 = (IsoDoor)var2.get(var3);
            var5 = var4.north;
            if (var5 && var1.y != this.y) {
               return var4;
            }

            if (!var5 && var1.x != this.x) {
               return var4;
            }
         } else if (var2.get(var3) instanceof IsoThumpable && ((IsoThumpable)var2.get(var3)).isDoor) {
            IsoThumpable var6 = (IsoThumpable)var2.get(var3);
            var5 = var6.north;
            if (var5 && var1.y != this.y) {
               return var6;
            }

            if (!var5 && var1.x != this.x) {
               return var6;
            }
         }
      }

      return null;
   }

   public static IsoGridSquare getNew(IsoCell var0, SliceY var1, int var2, int var3, int var4) {
      IsoGridSquare var5 = null;
      synchronized(isoGridSquareCache) {
         if (isoGridSquareCache.isEmpty()) {
            return new IsoGridSquare(var0, var1, var2, var3, var4);
         }

         var5 = (IsoGridSquare)isoGridSquareCache.pop();
         isoGridSquareSet.remove(var5.ID);
      }

      var5.x = var2;
      var5.y = var3;
      var5.z = var4;
      var5.CachedScreenValue = -1;

      int var6;
      for(var6 = 0; var6 < 4; ++var6) {
         if (var5.lighting[var6] != null) {
            var5.lighting[var6].setPos(var2, var3, var4);
         }
      }

      col = 0;
      path = 0;
      pathdoor = 0;
      vision = 0;

      for(var6 = 0; var6 < 3; ++var6) {
         for(int var7 = 0; var7 < 3; ++var7) {
            for(int var8 = 0; var8 < 3; ++var8) {
               var5.collideMatrix[var6][var7][var8] = true;
               var5.pathMatrix[var6][var7][var8] = true;
               var5.visionMatrix[var6][var7][var8] = false;
            }
         }
      }

      return var5;
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
      long var1 = 0L;

      for(int var3 = 0; var3 < this.Objects.size(); ++var3) {
         var1 += ((IsoObject)this.Objects.get(var3)).customHashCode();
      }

      this.hashCodeObjects = var1;
   }

   public int hashCode() {
      byte var1 = 0;
      this.recalcHashCodeObjects();
      int var5 = var1 * 2 + this.Objects.size();
      var5 = (int)((long)var5 + this.getHashCodeObjects());

      int var2;
      for(var2 = 0; var2 < this.Objects.size(); ++var2) {
         var5 = var5 * 2 + ((IsoObject)this.Objects.get(var2)).hashCode();
      }

      var2 = 0;

      int var3;
      for(var3 = 0; var3 < this.StaticMovingObjects.size(); ++var3) {
         if (this.StaticMovingObjects.get(var3) instanceof IsoDeadBody) {
            ++var2;
         }
      }

      var5 = var5 * 2 + var2;

      for(var3 = 0; var3 < this.StaticMovingObjects.size(); ++var3) {
         IsoMovingObject var4 = (IsoMovingObject)this.StaticMovingObjects.get(var3);
         if (var4 instanceof IsoDeadBody) {
            var5 = var5 * 2 + var4.hashCode();
         }
      }

      if (this.table != null && !this.table.isEmpty()) {
         var5 = var5 * 2 + this.table.hashCode();
      }

      this.bDirty = false;
      byte var6 = 0;
      if (this.isOverlayDone()) {
         var6 = (byte)(var6 | 1);
      }

      if (this.haveRoof) {
         var6 = (byte)(var6 | 2);
      }

      if (this.burntOut) {
         var6 = (byte)(var6 | 4);
      }

      var5 = var5 * 2 + var6;
      var5 = var5 * 2 + this.getErosionData().hashCode();
      if (this.getTrapPositionX() > 0) {
         var5 = var5 * 2 + this.getTrapPositionX();
         var5 = var5 * 2 + this.getTrapPositionY();
         var5 = var5 * 2 + this.getTrapPositionZ();
      }

      var5 = var5 * 2 + (this.haveElectricity() ? 1 : 0);
      var5 = var5 * 2 + (this.haveSheetRope ? 1 : 0);
      return var5;
   }

   public IsoGridSquare(IsoCell var1, SliceY var2, int var3, int var4, int var5) {
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
      this.x = var3;
      this.y = var4;
      this.z = var5;
      this.CachedScreenValue = -1;
      col = 0;
      path = 0;
      pathdoor = 0;
      vision = 0;

      int var6;
      for(var6 = 0; var6 < 3; ++var6) {
         for(int var7 = 0; var7 < 3; ++var7) {
            for(int var8 = 0; var8 < 3; ++var8) {
               this.collideMatrix[var6][var7][var8] = true;
               this.pathMatrix[var6][var7][var8] = true;
               this.visionMatrix[var6][var7][var8] = false;
            }
         }
      }

      for(var6 = 0; var6 < 4; ++var6) {
         if (GameServer.bServer) {
            if (var6 == 0) {
               this.lighting[var6] = new ServerLOS.ServerLighting();
            }
         } else if (LightingJNI.init && LightingThread.instance.jniLighting) {
            this.lighting[var6] = new LightingJNI.JNILighting(var6, var3, var4, var5);
         } else {
            this.lighting[var6] = new IsoGridSquare.Lighting();
         }
      }

   }

   public void init(IsoCell var1, SliceY var2, int var3, int var4, int var5) {
      if (ZombieHall == null) {
         ZombieHall = new IsoSprite(this.getCell().SpriteManager);
         ZombieHall.LoadFramesPalette("Zombie", "walk", 4, (String)"Zombie_palette10");
      }

      var2.Squares.setValue(var3, var5, this);
      this.x = var3;
      this.y = var4;
      this.z = var5;
      this.CachedScreenValue = -1;
   }

   public IsoGridSquare getTileInDirection(IsoDirections var1) {
      if (var1 == IsoDirections.N) {
         return this.getCell().getGridSquare(this.x, this.y - 1, this.z);
      } else if (var1 == IsoDirections.NE) {
         return this.getCell().getGridSquare(this.x + 1, this.y - 1, this.z);
      } else if (var1 == IsoDirections.NW) {
         return this.getCell().getGridSquare(this.x - 1, this.y - 1, this.z);
      } else if (var1 == IsoDirections.E) {
         return this.getCell().getGridSquare(this.x + 1, this.y, this.z);
      } else if (var1 == IsoDirections.W) {
         return this.getCell().getGridSquare(this.x - 1, this.y, this.z);
      } else if (var1 == IsoDirections.SE) {
         return this.getCell().getGridSquare(this.x + 1, this.y + 1, this.z);
      } else if (var1 == IsoDirections.SW) {
         return this.getCell().getGridSquare(this.x - 1, this.y + 1, this.z);
      } else {
         return var1 == IsoDirections.S ? this.getCell().getGridSquare(this.x, this.y + 1, this.z) : null;
      }
   }

   public int compareTo(Object var1) {
      Thread var2 = Thread.currentThread();
      IsoGridSquare var3 = (IsoGridSquare)var1;
      int var4 = 0;
      if (var2 instanceof PathfindManager.PathfindThread) {
         var4 = ((PathfindManager.PathfindThread)var2).ID;
      }

      float var5 = this.searchData[var4].heuristic + this.searchData[var4].cost;
      float var6 = var3.searchData[var4].heuristic + var3.searchData[var4].cost;
      if (var5 < var6) {
         return -1;
      } else {
         return var5 > var6 ? 1 : 0;
      }
   }

   public int setParent(int var1, int var2, IsoGridSquare var3) {
      this.getSearchData(var1).parent = var3;
      short var4 = this.searchData[var1].depth = (short)(var3.getSearchData(var1).depth + 1);
      return var4;
   }

   public SearchData getSearchData(int var1) {
      return this.searchData[var1];
   }

   IsoObject getWall() {
      for(int var1 = 0; var1 < this.Objects.size(); ++var1) {
         if (((IsoObject)this.Objects.get(var1)).sprite.cutW || ((IsoObject)this.Objects.get(var1)).sprite.cutN) {
            return (IsoObject)this.Objects.get(var1);
         }
      }

      return null;
   }

   public IsoObject getWall(boolean var1) {
      for(int var2 = 0; var2 < this.Objects.size(); ++var2) {
         if (((IsoObject)this.Objects.get(var2)).sprite.cutN && var1 || ((IsoObject)this.Objects.get(var2)).sprite.cutW && !var1) {
            return (IsoObject)this.Objects.get(var2);
         }
      }

      return null;
   }

   public IsoObject getFloor() {
      for(int var1 = 0; var1 < this.Objects.size(); ++var1) {
         IsoObject var2 = (IsoObject)this.Objects.get(var1);
         if (var2.sprite != null && var2.sprite.getProperties().Is(IsoFlagType.solidfloor)) {
            return var2;
         }
      }

      return null;
   }

   public IsoObject getPlayerBuiltFloor() {
      return this.getBuilding() == null && this.roofHideBuilding == null ? this.getFloor() : null;
   }

   public void interpolateLight(ColorInfo var1, float var2, float var3) {
      IsoCell var4 = this.getCell();
      if (var2 < 0.0F) {
         var2 = 0.0F;
      }

      if (var2 > 1.0F) {
         var2 = 1.0F;
      }

      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      int var5 = IsoCamera.frameState.playerIndex;
      int var6 = this.getVertLight(0, var5);
      int var7 = this.getVertLight(1, var5);
      int var8 = this.getVertLight(2, var5);
      int var9 = this.getVertLight(3, var5);
      tl.fromColor(var6);
      bl.fromColor(var9);
      tr.fromColor(var7);
      br.fromColor(var8);
      tl.interp(tr, var2, interp1);
      bl.interp(br, var2, interp2);
      interp1.interp(interp2, var3, finalCol);
      var1.r = finalCol.r;
      var1.g = finalCol.g;
      var1.b = finalCol.b;
      var1.a = finalCol.a;
   }

   void AddWoodWall(boolean var1, String var2) {
      int var3 = 48;
      if (var1) {
         ++var3;
      }

      if (var2.equals("DoorFrame")) {
         var3 += 10;
      }

      if (var2.equals("WindowFrame")) {
         var3 += 8;
      }

      String var4 = "TileWalls_" + var3;
      boolean var5 = false;
      int var6 = 0;
      int var7 = 0;
      IsoGridSquare var8;
      if (var1) {
         var8 = this.getCell().getGridSquare(this.x + 1, this.y - 1, this.z);
         if (var8 != null && var8.Properties.Is(IsoFlagType.cutW)) {
            var5 = true;
            var6 = this.x + 1;
            var7 = this.y;
         }
      } else {
         var8 = this.getCell().getGridSquare(this.x - 1, this.y + 1, this.z);
         if (var8 != null && var8.Properties.Is(IsoFlagType.cutN)) {
            var5 = true;
            var6 = this.x;
            var7 = this.y + 1;
         }
      }

      IsoObject var11 = new IsoObject(this.getCell(), this, var4);
      if (var1) {
         if (!var2.equals("DoorFrame") && !var2.equals("WindowFrame")) {
            var11.sprite.getProperties().Set(IsoFlagType.collideN, "");
         }

         var11.sprite.getProperties().Set(IsoFlagType.cutN, "");
         if (var2.equals("WindowFrame")) {
            var11.sprite.getProperties().Set(IsoFlagType.transparentN, "");
         }
      }

      if (!var1) {
         if (!var2.equals("DoorFrame") && !var2.equals("WindowFrame")) {
            var11.sprite.getProperties().Set(IsoFlagType.collideW, "");
         }

         var11.sprite.getProperties().Set(IsoFlagType.cutW, "");
         if (var2.equals("WindowFrame")) {
            var11.sprite.getProperties().Set(IsoFlagType.transparentW, "");
         }
      }

      if (var5) {
         IsoGridSquare var9 = this.getCell().getGridSquare(var6, var7, this.z);
         if (var9 == null) {
            var9 = new IsoGridSquare(this.getCell(), (SliceY)null, var6, var7, this.z);
            this.getCell().ConnectNewSquare(var9, true);
         }

         IsoObject var10 = new IsoObject(this.getCell(), var9, "TileWalls_51");
         var10.sprite.getProperties().Set(IsoFlagType.cutN, "");
         var10.sprite.getProperties().Set(IsoFlagType.cutW, "");
         var9.Objects.add(0, var10);
         var9.RecalcProperties();
      }

      this.Objects.add(0, var11);
      if (var2.equals("DoorFrame")) {
         if (!var1) {
            var4 = "TileFrames_14";
         } else {
            var4 = "TileFrames_15";
         }

         var11 = new IsoObject(this.getCell(), this, var4);
         if (var1) {
            var11.setType(IsoObjectType.doorFrN);
         }

         if (!var1) {
            var11.setType(IsoObjectType.doorFrW);
         }

         this.Objects.add(1, var11);
      }

      this.RecalcAllWithNeighbours(true);
      if (!var1 && var2.equals("Wall")) {
         this.Properties.UnSet(IsoFlagType.transparentW);
      } else if (var2.equals("Wall")) {
         this.Properties.UnSet(IsoFlagType.transparentN);
      }

   }

   public void EnsureSurroundNotNull() {
      assert !GameServer.bServer;

      for(int var1 = -1; var1 <= 1; ++var1) {
         for(int var2 = -1; var2 <= 1; ++var2) {
            if ((var1 != 0 || var2 != 0) && IsoWorld.instance.isValidSquare(this.x + var1, this.y + var2, this.z) && this.getCell().getChunkForGridSquare(this.x + var1, this.y + var2, this.z) != null) {
               IsoGridSquare var3 = this.getCell().getGridSquare(this.x + var1, this.y + var2, this.z);
               if (var3 == null) {
                  var3 = getNew(this.getCell(), (SliceY)null, this.x + var1, this.y + var2, this.z);
                  IsoGridSquare var4 = this.getCell().ConnectNewSquare(var3, false);
               }
            }
         }
      }

   }

   public IsoObject addFloor(String var1) {
      IsoRegion.setPreviousFlags(this);
      IsoObject var2 = new IsoObject(this.getCell(), this, var1);

      int var3;
      for(var3 = 0; var3 < this.getObjects().size(); ++var3) {
         IsoObject var4 = (IsoObject)this.getObjects().get(var3);
         IsoSprite var5 = var4.sprite;
         if (var5 != null && (var5.getProperties().Is(IsoFlagType.solidfloor) || var5.getProperties().Is(IsoFlagType.noStart) || var5.getProperties().Is(IsoFlagType.vegitation) && var4.getType() != IsoObjectType.tree || var5.getName() != null && var5.getName().startsWith("blends_grassoverlays"))) {
            this.transmitRemoveItemFromSquare(var4);
            --var3;
         }
      }

      var2.sprite.getProperties().Set(IsoFlagType.solidfloor, "");
      this.getObjects().add(var2);
      this.EnsureSurroundNotNull();
      this.RecalcProperties();
      this.getCell().checkHaveRoof(this.x, this.y);

      for(var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
         LosUtil.cachecleared[var3] = true;
      }

      setRecalcLightTime(-1);
      GameTime.getInstance().lightSourceUpdate = 100.0F;
      var2.transmitCompleteItemToServer();
      this.RecalcAllWithNeighbours(true);
      if (this.z > 0) {
         IsoGridSquare var6 = this.getCell().getGridSquare(this.x, this.y, this.z - 1);
         if (var6 != null) {
            var6.RecalcAllWithNeighbours(true);
         }
      }

      this.setCachedIsFree(false);
      PolygonalMap2.instance.squareChanged(this);
      IsoGridOcclusionData.SquareChanged();
      IsoRegion.squareChanged(this);
      return var2;
   }

   public IsoThumpable AddStairs(boolean var1, int var2, String var3, String var4, KahluaTable var5) {
      IsoRegion.setPreviousFlags(this);
      this.EnsureSurroundNotNull();
      boolean var6 = !this.TreatAsSolidFloor();
      IsoGridSquare var7 = this.getCell().getGridSquare(this.x, this.y, this.z - 1);
      if (var7 != null && var7.HasStairs()) {
         var6 = false;
      }

      this.CachedIsFree = false;
      IsoThumpable var8 = new IsoThumpable(this.getCell(), this, var3, var1, var5);
      if (var1) {
         if (var2 == 0) {
            var8.setType(IsoObjectType.stairsBN);
         }

         if (var2 == 1) {
            var8.setType(IsoObjectType.stairsMN);
         }

         if (var2 == 2) {
            var8.setType(IsoObjectType.stairsTN);
            var8.sprite.getProperties().Set(var1 ? IsoFlagType.cutN : IsoFlagType.cutW, "");
         }
      }

      if (!var1) {
         if (var2 == 0) {
            var8.setType(IsoObjectType.stairsBW);
         }

         if (var2 == 1) {
            var8.setType(IsoObjectType.stairsMW);
         }

         if (var2 == 2) {
            var8.setType(IsoObjectType.stairsTW);
            var8.sprite.getProperties().Set(var1 ? IsoFlagType.cutN : IsoFlagType.cutW, "");
         }
      }

      this.Objects.add(var8);
      this.SpecialObjects.add(var8);
      this.Properties.Clear();

      int var9;
      for(var9 = 0; var9 < this.Objects.size(); ++var9) {
         IsoObject var10 = (IsoObject)this.Objects.get(var9);
         if (var10.sprite != null) {
            this.Properties.AddProperties(var10.sprite.getProperties());
         }
      }

      if (var6 && var2 == 2) {
         var9 = this.z - 1;
         IsoGridSquare var13 = this.getCell().getGridSquare(this.x, this.y, var9);
         if (var13 == null) {
            var13 = new IsoGridSquare(this.getCell(), (SliceY)null, this.x, this.y, var9);
            this.getCell().ConnectNewSquare(var13, true);
         }

         while(var9 >= 0) {
            IsoThumpable var11 = new IsoThumpable(this.getCell(), var13, var4, var1, var5);
            var11.sprite.getProperties().Set(IsoFlagType.solidtrans, "");
            var11.sprite.getProperties().Set(var1 ? IsoFlagType.cutN : IsoFlagType.cutW, "");
            var13.Objects.add(var11);
            var13.SpecialObjects.add(var11);
            var13.RecalcAllWithNeighbours(true);
            if (var13.TreatAsSolidFloor()) {
               break;
            }

            --var9;
            if (this.getCell().getGridSquare(var13.x, var13.y, var9) == null) {
               var13 = new IsoGridSquare(this.getCell(), (SliceY)null, var13.x, var13.y, var9);
               this.getCell().ConnectNewSquare(var13, true);
            } else {
               var13 = this.getCell().getGridSquare(var13.x, var13.y, var9);
            }
         }
      }

      if (var2 == 2) {
         IsoGridSquare var14 = null;
         this.getCell().getStairsNodes().add(this.ID);
         if (var1) {
            if (IsoWorld.instance.isValidSquare(this.x, this.y - 1, this.z + 1)) {
               var14 = this.getCell().getGridSquare(this.x, this.y - 1, this.z + 1);
               if (var14 == null) {
                  var14 = new IsoGridSquare(this.getCell(), (SliceY)null, this.x, this.y - 1, this.z + 1);
                  this.getCell().ConnectNewSquare(var14, false);
               }

               if (!var14.Properties.Is(IsoFlagType.solidfloor)) {
                  var14.addFloor("carpentry_02_57");
               }
            }
         } else if (IsoWorld.instance.isValidSquare(this.x - 1, this.y, this.z + 1)) {
            var14 = this.getCell().getGridSquare(this.x - 1, this.y, this.z + 1);
            if (var14 == null) {
               var14 = new IsoGridSquare(this.getCell(), (SliceY)null, this.x - 1, this.y, this.z + 1);
               this.getCell().ConnectNewSquare(var14, false);
            }

            if (!var14.Properties.Is(IsoFlagType.solidfloor)) {
               var14.addFloor("carpentry_02_57");
            }
         }

         var14.getModData().rawset("ConnectedToStairs" + var1, true);
         var14 = this.getCell().getGridSquare(this.x, this.y, this.z + 1);
         if (var14 == null) {
            var14 = new IsoGridSquare(this.getCell(), (SliceY)null, this.x, this.y, this.z + 1);
            this.getCell().ConnectNewSquare(var14, false);
         }
      }

      for(var9 = this.getX() - 1; var9 <= this.getX() + 1; ++var9) {
         for(int var16 = this.getY() - 1; var16 <= this.getY() + 1; ++var16) {
            for(int var15 = this.getZ() - 1; var15 <= this.getZ() + 1; ++var15) {
               if (IsoWorld.instance.isValidSquare(var9, var16, var15)) {
                  IsoGridSquare var12 = this.getCell().getGridSquare(var9, var16, var15);
                  if (var12 == null) {
                     var12 = new IsoGridSquare(this.getCell(), (SliceY)null, var9, var16, var15);
                     this.getCell().ConnectNewSquare(var12, false);
                  }

                  var12.ReCalculateCollide(this);
                  var12.ReCalculateVisionBlocked(this);
                  var12.ReCalculatePathFind(this);
                  this.ReCalculateCollide(var12);
                  this.ReCalculatePathFind(var12);
                  this.ReCalculateVisionBlocked(var12);
                  var12.CachedIsFree = false;
               }
            }
         }
      }

      this.RecalcAllWithNeighbours(true);
      MapCollisionData.instance.squareChanged(this);
      PolygonalMap2.instance.squareChanged(this);
      IsoRegion.squareChanged(this);
      return var8;
   }

   void ReCalculateAll(IsoGridSquare var1) {
      this.ReCalculateAll(var1, cellGetSquare);
   }

   void ReCalculateAll(IsoGridSquare var1, IsoGridSquare.GetSquare var2) {
      if (var1 != null && var1 != this) {
         this.SolidFloorCached = false;
         var1.SolidFloorCached = false;
         this.RecalcPropertiesIfNeeded();
         var1.RecalcPropertiesIfNeeded();
         this.ReCalculateCollide(var1, var2);
         var1.ReCalculateCollide(this, var2);
         this.ReCalculatePathFind(var1, var2);
         var1.ReCalculatePathFind(this, var2);
         this.ReCalculateVisionBlocked(var1, var2);
         var1.ReCalculateVisionBlocked(this, var2);
         this.setBlockedGridPointers(var2);
         var1.setBlockedGridPointers(var2);
      }
   }

   void ReCalculateMineOnly(IsoGridSquare var1) {
      this.SolidFloorCached = false;
      this.RecalcProperties();
      this.ReCalculateCollide(var1);
      this.ReCalculatePathFind(var1);
      this.ReCalculateVisionBlocked(var1);
      this.setBlockedGridPointers(cellGetSquare);
   }

   public void RecalcAllWithNeighbours(boolean var1) {
      this.RecalcAllWithNeighbours(var1, cellGetSquare);
   }

   public void RecalcAllWithNeighbours(boolean var1, IsoGridSquare.GetSquare var2) {
      this.SolidFloorCached = false;
      this.RecalcPropertiesIfNeeded();

      for(int var3 = this.getX() - 1; var3 <= this.getX() + 1; ++var3) {
         for(int var4 = this.getY() - 1; var4 <= this.getY() + 1; ++var4) {
            for(int var5 = this.getZ() - 1; var5 <= this.getZ() + 1; ++var5) {
               if (IsoWorld.instance.isValidSquare(var3, var4, var5)) {
                  int var6 = var3 - this.getX();
                  int var7 = var4 - this.getY();
                  int var8 = var5 - this.getZ();
                  if (var6 != 0 || var7 != 0 || var8 != 0) {
                     IsoGridSquare var9 = var2.getGridSquare(var3, var4, var5);
                     if (var9 != null) {
                        var9.DirtySlice();
                        this.ReCalculateAll(var9, var2);
                     }
                  }
               }
            }
         }
      }

      IsoWorld.instance.CurrentCell.DoGridNav(this, var2);
      IsoGridSquare var10 = this.nav[IsoDirections.N.index()];
      IsoGridSquare var11 = this.nav[IsoDirections.S.index()];
      IsoGridSquare var12 = this.nav[IsoDirections.W.index()];
      IsoGridSquare var13 = this.nav[IsoDirections.E.index()];
      if (var10 != null && var12 != null) {
         var10.ReCalculateAll(var12, var2);
      }

      if (var10 != null && var13 != null) {
         var10.ReCalculateAll(var13, var2);
      }

      if (var11 != null && var12 != null) {
         var11.ReCalculateAll(var12, var2);
      }

      if (var11 != null && var13 != null) {
         var11.ReCalculateAll(var13, var2);
      }

   }

   public void RecalcAllWithNeighboursMineOnly() {
      this.SolidFloorCached = false;
      this.RecalcProperties();

      for(int var1 = this.getX() - 1; var1 <= this.getX() + 1; ++var1) {
         for(int var2 = this.getY() - 1; var2 <= this.getY() + 1; ++var2) {
            for(int var3 = this.getZ() - 1; var3 <= this.getZ() + 1; ++var3) {
               if (var3 >= 0) {
                  int var4 = var1 - this.getX();
                  int var5 = var2 - this.getY();
                  int var6 = var3 - this.getZ();
                  if (var4 != 0 || var5 != 0 || var6 != 0) {
                     IsoGridSquare var7 = this.getCell().getGridSquare(var1, var2, var3);
                     if (var7 != null) {
                        var7.DirtySlice();
                        this.ReCalculateMineOnly(var7);
                     }
                  }
               }
            }
         }
      }

   }

   boolean IsWindow(int var1, int var2, int var3) {
      IsoGridSquare var4 = this.getCell().getGridSquare(this.x + var1, this.y + var2, this.z + var3);
      return this.getWindowTo(var4) != null || this.getWindowThumpableTo(var4) != null;
   }

   void RemoveAllWith(IsoFlagType var1) {
      for(int var2 = 0; var2 < this.Objects.size(); ++var2) {
         IsoObject var3 = (IsoObject)this.Objects.get(var2);
         if (var3.sprite != null && var3.sprite.getProperties().Is(var1)) {
            this.Objects.remove(var3);
            this.SpecialObjects.remove(var3);
            --var2;
         }
      }

      this.RecalcAllWithNeighbours(true);
   }

   public boolean hasSupport() {
      IsoGridSquare var1 = this.getCell().getGridSquare(this.x, this.y + 1, this.z);
      IsoGridSquare var2 = this.getCell().getGridSquare(this.x + 1, this.y, this.z);

      for(int var3 = 0; var3 < this.Objects.size(); ++var3) {
         IsoObject var4 = (IsoObject)this.Objects.get(var3);
         if (var4.sprite != null && (var4.sprite.getProperties().Is(IsoFlagType.solid) || (var4.sprite.getProperties().Is(IsoFlagType.cutW) || var4.sprite.getProperties().Is(IsoFlagType.cutN)) && !var4.sprite.Properties.Is(IsoFlagType.halfheight))) {
            return true;
         }
      }

      if (var1 != null && var1.Properties.Is(IsoFlagType.cutN) && !var1.Properties.Is(IsoFlagType.halfheight)) {
         return true;
      } else if (var2 != null && var2.Properties.Is(IsoFlagType.cutW) && !var1.Properties.Is(IsoFlagType.halfheight)) {
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

   public void setID(int var1) {
      this.ID = var1;
   }

   private int savematrix(boolean[][][] var1, byte[] var2, int var3) {
      for(int var4 = 0; var4 <= 2; ++var4) {
         for(int var5 = 0; var5 <= 2; ++var5) {
            for(int var6 = 0; var6 <= 2; ++var6) {
               var2[var3] = (byte)(var1[var4][var5][var6] ? 1 : 0);
               ++var3;
            }
         }
      }

      return var3;
   }

   private int loadmatrix(boolean[][][] var1, byte[] var2, int var3) {
      for(int var4 = 0; var4 <= 2; ++var4) {
         for(int var5 = 0; var5 <= 2; ++var5) {
            for(int var6 = 0; var6 <= 2; ++var6) {
               var1[var4][var5][var6] = var2[var3] != 0;
               ++var3;
            }
         }
      }

      return var3;
   }

   private void savematrix(boolean[][][] var1, ByteBuffer var2) {
      for(int var3 = 0; var3 <= 2; ++var3) {
         for(int var4 = 0; var4 <= 2; ++var4) {
            for(int var5 = 0; var5 <= 2; ++var5) {
               var2.put((byte)(var1[var3][var4][var5] ? 1 : 0));
            }
         }
      }

   }

   private void loadmatrix(boolean[][][] var1, ByteBuffer var2) {
      for(int var3 = 0; var3 <= 2; ++var3) {
         for(int var4 = 0; var4 <= 2; ++var4) {
            for(int var5 = 0; var5 <= 2; ++var5) {
               var1[var3][var4][var5] = var2.get() != 0;
            }
         }
      }

   }

   public void DirtySlice() {
   }

   public void setHourSeenToCurrent() {
      this.hourLastSeen = (int)GameTime.instance.getWorldAgeHours();
   }

   public void splatBlood(int var1, float var2) {
      var2 *= 2.0F;
      var2 *= 3.0F;
      if (var2 > 1.0F) {
         var2 = 1.0F;
      }

      IsoGridSquare var3 = this;
      IsoGridSquare var4 = this;

      for(int var5 = 0; var5 < var1; ++var5) {
         if (var3 != null) {
            var3 = this.getCell().getGridSquare(this.getX(), this.getY() - var5, this.getZ());
         }

         if (var4 != null) {
            var4 = this.getCell().getGridSquare(this.getX() - var5, this.getY(), this.getZ());
         }

         float var6 = 0.0F;
         boolean var7;
         boolean var8;
         byte var9;
         byte var10;
         int var11;
         boolean var12;
         byte var13;
         byte var14;
         float var15;
         IsoGridSquare var16;
         IsoGridSquare var17;
         int var18;
         if (var4 != null && var4.testCollideAdjacent((IsoMovingObject)null, -1, 0, 0)) {
            var7 = false;
            var8 = false;
            var9 = 0;
            var10 = 0;
            if (var4.getS() != null && var4.getS().testCollideAdjacent((IsoMovingObject)null, -1, 0, 0)) {
               var7 = true;
            }

            if (var4.getN() != null && var4.getN().testCollideAdjacent((IsoMovingObject)null, -1, 0, 0)) {
               var8 = true;
            }

            if (var7) {
               var9 = -1;
            }

            if (var8) {
               var10 = 1;
            }

            var11 = var10 - var9;
            var12 = false;
            var13 = 0;
            var14 = 0;
            if (var11 > 0 && Rand.Next(2) == 0) {
               var12 = true;
               if (var11 > 1) {
                  if (Rand.Next(2) == 0) {
                     var13 = -1;
                     var14 = 0;
                  } else {
                     var13 = 0;
                     var14 = 1;
                  }
               } else {
                  var13 = var9;
                  var14 = var10;
               }
            }

            var15 = (float)Rand.Next(100) / 300.0F;
            var16 = this.getCell().getGridSquare(var4.getX(), var4.getY() + var13, var4.getZ());
            var17 = this.getCell().getGridSquare(var4.getX(), var4.getY() + var14, var4.getZ());
            if (var16 == null || var17 == null || !var16.Is(IsoFlagType.cutW) || !var17.Is(IsoFlagType.cutW) || var16.getProperties().Is(IsoFlagType.WallSE) || var17.getProperties().Is(IsoFlagType.WallSE) || var16.Is(IsoFlagType.HoppableW) || var17.Is(IsoFlagType.HoppableW)) {
               var12 = false;
            }

            if (var12) {
               var18 = 24 + Rand.Next(2) * 2;
               if (Rand.Next(2) == 0) {
                  var18 += 8;
               }

               var16.DoSplat("overlay_blood_wall_01_" + (var18 + 1), false, IsoFlagType.cutW, var6, var15, var2);
               var17.DoSplat("overlay_blood_wall_01_" + (var18 + 0), false, IsoFlagType.cutW, var6, var15, var2);
            } else {
               var18 = 0;
               switch(Rand.Next(3)) {
               case 0:
                  var18 = 0 + Rand.Next(4);
                  break;
               case 1:
                  var18 = 8 + Rand.Next(4);
                  break;
               case 2:
                  var18 = 16 + Rand.Next(4);
               }

               if (var18 == 17 || var18 == 19) {
                  var15 = 0.0F;
               }

               if (var4.Is(IsoFlagType.HoppableW)) {
                  var4.DoSplat("overlay_blood_fence_01_" + var18, false, IsoFlagType.HoppableW, var6, 0.0F, var2);
               } else {
                  var4.DoSplat("overlay_blood_wall_01_" + var18, false, IsoFlagType.cutW, var6, var15, var2);
               }
            }

            var4 = null;
         }

         if (var3 != null && var3.testCollideAdjacent((IsoMovingObject)null, 0, -1, 0)) {
            var7 = false;
            var8 = false;
            var9 = 0;
            var10 = 0;
            if (var3.getW() != null && var3.getW().testCollideAdjacent((IsoMovingObject)null, 0, -1, 0)) {
               var7 = true;
            }

            if (var3.getE() != null && var3.getE().testCollideAdjacent((IsoMovingObject)null, 0, -1, 0)) {
               var8 = true;
            }

            if (var7) {
               var9 = -1;
            }

            if (var8) {
               var10 = 1;
            }

            var11 = var10 - var9;
            var12 = false;
            var13 = 0;
            var14 = 0;
            if (var11 > 0 && Rand.Next(2) == 0) {
               var12 = true;
               if (var11 > 1) {
                  if (Rand.Next(2) == 0) {
                     var13 = -1;
                     var14 = 0;
                  } else {
                     var13 = 0;
                     var14 = 1;
                  }
               } else {
                  var13 = var9;
                  var14 = var10;
               }
            }

            var15 = (float)Rand.Next(100) / 300.0F;
            var16 = this.getCell().getGridSquare(var3.getX() + var13, var3.getY(), var3.getZ());
            var17 = this.getCell().getGridSquare(var3.getX() + var14, var3.getY(), var3.getZ());
            if (var16 == null || var17 == null || !var16.Is(IsoFlagType.cutN) || !var17.Is(IsoFlagType.cutN) || var16.getProperties().Is(IsoFlagType.WallSE) || var17.getProperties().Is(IsoFlagType.WallSE) || var16.Is(IsoFlagType.HoppableN) || var17.Is(IsoFlagType.HoppableN)) {
               var12 = false;
            }

            if (var12) {
               var18 = 28 + Rand.Next(2) * 2;
               if (Rand.Next(2) == 0) {
                  var18 += 8;
               }

               var16.DoSplat("overlay_blood_wall_01_" + (var18 + 0), false, IsoFlagType.cutN, var6, var15, var2);
               var17.DoSplat("overlay_blood_wall_01_" + (var18 + 1), false, IsoFlagType.cutN, var6, var15, var2);
            } else {
               var18 = 0;
               switch(Rand.Next(3)) {
               case 0:
                  var18 = 4 + Rand.Next(4);
                  break;
               case 1:
                  var18 = 12 + Rand.Next(4);
                  break;
               case 2:
                  var18 = 20 + Rand.Next(4);
               }

               if (var18 == 20 || var18 == 22) {
                  var15 = 0.0F;
               }

               if (var3.Is(IsoFlagType.HoppableN)) {
                  var3.DoSplat("overlay_blood_fence_01_" + var18, false, IsoFlagType.HoppableN, var6, var15, var2);
               } else {
                  var3.DoSplat("overlay_blood_wall_01_" + var18, false, IsoFlagType.cutN, var6, var15, var2);
               }
            }

            var3 = null;
         }
      }

   }

   public boolean haveBlood() {
      if (Core.OptionBloodDecals == 0) {
         return false;
      } else {
         int var1;
         for(var1 = 0; var1 < this.getObjects().size(); ++var1) {
            IsoObject var2 = (IsoObject)this.getObjects().get(var1);
            if (var2.wallBloodSplats != null && !var2.wallBloodSplats.isEmpty()) {
               return true;
            }
         }

         for(var1 = 0; var1 < this.getChunk().FloorBloodSplats.size(); ++var1) {
            IsoFloorBloodSplat var5 = (IsoFloorBloodSplat)this.getChunk().FloorBloodSplats.get(var1);
            float var3 = var5.x + (float)(this.getChunk().wx * 10);
            float var4 = var5.y + (float)(this.getChunk().wy * 10);
            if ((int)var3 - 1 <= this.x && (int)var3 + 1 >= this.x && (int)var4 - 1 <= this.y && (int)var4 + 1 >= this.y) {
               return true;
            }
         }

         return false;
      }
   }

   public void removeBlood(boolean var1, boolean var2) {
      int var3;
      for(var3 = 0; var3 < this.getObjects().size(); ++var3) {
         IsoObject var4 = (IsoObject)this.getObjects().get(var3);
         if (var4.wallBloodSplats != null) {
            var4.wallBloodSplats.clear();
         }
      }

      if (!var2) {
         for(var3 = 0; var3 < this.getChunk().FloorBloodSplats.size(); ++var3) {
            IsoFloorBloodSplat var7 = (IsoFloorBloodSplat)this.getChunk().FloorBloodSplats.get(var3);
            int var5 = (int)((float)(this.getChunk().wx * 10) + var7.x);
            int var6 = (int)((float)(this.getChunk().wy * 10) + var7.y);
            if (var5 >= this.getX() - 1 && var5 <= this.getX() + 1 && var6 >= this.getY() - 1 && var6 <= this.getY() + 1) {
               this.getChunk().FloorBloodSplats.remove(var3);
               --var3;
            }
         }
      }

      if (GameClient.bClient && !var1) {
         ByteBufferWriter var8 = GameClient.connection.startPacket();
         PacketTypes.doPacket((short)109, var8);
         var8.putInt(this.x);
         var8.putInt(this.y);
         var8.putInt(this.z);
         var8.putBoolean(var2);
         GameClient.connection.endPacketImmediate();
      }

   }

   public void DoSplat(String var1, boolean var2, IsoFlagType var3, float var4, float var5, float var6) {
      for(int var7 = 0; var7 < this.getObjects().size(); ++var7) {
         IsoObject var8 = (IsoObject)this.getObjects().get(var7);
         if (var8.sprite != null && var8.sprite.getProperties().Is(var3)) {
            IsoSprite var9 = IsoSprite.getSprite(this.getCell().SpriteManager, (String)var1, 0);
            if (var9 == null) {
               return;
            }

            if (var8.wallBloodSplats == null) {
               var8.wallBloodSplats = new ArrayList();
            }

            IsoWallBloodSplat var10 = new IsoWallBloodSplat((float)GameTime.getInstance().getWorldAgeHours(), var9);
            var8.wallBloodSplats.add(var10);
         }
      }

   }

   public void ClearTileObjects() {
      this.Objects.clear();
      this.RecalcProperties();
   }

   public void ClearTileObjectsExceptFloor() {
      for(int var1 = 0; var1 < this.Objects.size(); ++var1) {
         IsoObject var2 = (IsoObject)this.Objects.get(var1);
         if (var2.sprite == null || !var2.sprite.getProperties().Is(IsoFlagType.solidfloor)) {
            this.Objects.remove(var2);
            --var1;
         }
      }

      this.RecalcProperties();
   }

   public int RemoveTileObject(IsoObject var1) {
      IsoRegion.setPreviousFlags(this);
      int var2 = this.Objects.indexOf(var1);
      if (!this.Objects.contains(var1)) {
         var2 = this.SpecialObjects.indexOf(var1);
      }

      if (var1 != null && this.Objects.contains(var1)) {
         if (var1.isTableSurface()) {
            for(int var3 = this.Objects.indexOf(var1) + 1; var3 < this.Objects.size(); ++var3) {
               IsoObject var4 = (IsoObject)this.Objects.get(var3);
               if (var4.isTableTopObject() || var4.isTableSurface()) {
                  var4.setRenderYOffset(var4.getRenderYOffset() - var1.getSurfaceOffset());
                  var4.sx = 0;
                  var4.sy = 0;
               }
            }
         }

         IsoObject var5 = this.getPlayerBuiltFloor();
         if (var1 == var5) {
            IsoGridOcclusionData.SquareChanged();
         }

         LuaEventManager.triggerEvent("OnObjectAboutToBeRemoved", var1);
         if (!this.Objects.contains(var1)) {
            throw new IllegalArgumentException("OnObjectAboutToBeRemoved not allowed to remove the object");
         }

         var2 = this.Objects.indexOf(var1);
         var1.removeFromWorld();
         var1.removeFromSquare();

         assert !this.Objects.contains(var1);

         assert !this.SpecialObjects.contains(var1);

         if (!(var1 instanceof IsoWorldInventoryObject)) {
            this.RecalcAllWithNeighbours(true);
            this.getCell().checkHaveRoof(this.getX(), this.getY());

            for(int var6 = 0; var6 < IsoPlayer.numPlayers; ++var6) {
               LosUtil.cachecleared[var6] = true;
            }

            setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0F;
         }
      }

      MapCollisionData.instance.squareChanged(this);
      LuaEventManager.triggerEvent("OnTileRemoved", var1);
      PolygonalMap2.instance.squareChanged(this);
      IsoRegion.squareChanged(this, true);
      return var2;
   }

   public int RemoveTileObjectErosionNoRecalc(IsoObject var1) {
      int var2 = this.Objects.indexOf(var1);
      IsoGridSquare var3 = var1.square;
      var1.removeFromWorld();
      var1.removeFromSquare();
      var3.RecalcPropertiesIfNeeded();

      assert !this.Objects.contains(var1);

      assert !this.SpecialObjects.contains(var1);

      return var2;
   }

   public void AddSpecialObject(IsoObject var1) {
      this.AddSpecialObject(var1, -1);
   }

   public void AddSpecialObject(IsoObject var1, int var2) {
      if (var1 != null) {
         IsoRegion.setPreviousFlags(this);
         if (var2 != -1 && var2 >= 0 && var2 <= this.Objects.size()) {
            this.Objects.add(var2, var1);
         } else {
            this.Objects.add(var1);
         }

         this.SpecialObjects.add(var1);
         this.burntOut = false;
         var1.addToWorld();
         if (!GameServer.bServer && !GameClient.bClient) {
            this.restackSheetRope();
         }

         this.RecalcAllWithNeighbours(true);
         if (!(var1 instanceof IsoWorldInventoryObject)) {
            for(int var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
               LosUtil.cachecleared[var3] = true;
            }

            setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0F;
            if (var1 == this.getPlayerBuiltFloor()) {
               IsoGridOcclusionData.SquareChanged();
            }
         }

         MapCollisionData.instance.squareChanged(this);
         PolygonalMap2.instance.squareChanged(this);
         IsoRegion.squareChanged(this);
      }
   }

   public void AddTileObject(IsoObject var1) {
      this.AddTileObject(var1, -1);
   }

   public void AddTileObject(IsoObject var1, int var2) {
      if (var1 != null) {
         IsoRegion.setPreviousFlags(this);
         if (var2 != -1 && var2 >= 0 && var2 <= this.Objects.size()) {
            this.Objects.add(var2, var1);
         } else {
            this.Objects.add(var1);
         }

         this.burntOut = false;
         var1.addToWorld();
         this.RecalcAllWithNeighbours(true);
         if (!(var1 instanceof IsoWorldInventoryObject)) {
            for(int var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
               LosUtil.cachecleared[var3] = true;
            }

            setRecalcLightTime(-1);
            GameTime.instance.lightSourceUpdate = 100.0F;
            if (var1 == this.getPlayerBuiltFloor()) {
               IsoGridOcclusionData.SquareChanged();
            }
         }

         MapCollisionData.instance.squareChanged(this);
         PolygonalMap2.instance.squareChanged(this);
         IsoRegion.squareChanged(this);
      }
   }

   public int transmitRemoveItemFromSquare(IsoObject var1) {
      if (var1 != null && this.Objects.contains(var1)) {
         if (GameClient.bClient) {
            try {
               GameClient.instance.checkAddedRemovedItems(var1);
            } catch (Exception var3) {
               GameClient.connection.cancelPacket();
               ExceptionLogger.logException(var3);
            }

            ByteBufferWriter var2 = GameClient.connection.startPacket();
            PacketTypes.doPacket((short)23, var2);
            var2.putInt(this.getX());
            var2.putInt(this.getY());
            var2.putInt(this.getZ());
            var2.putInt(this.Objects.indexOf(var1));
            GameClient.connection.endPacket();
            this.clientModify();
         }

         return GameServer.bServer ? GameServer.RemoveItemFromMap(var1) : this.RemoveTileObject(var1);
      } else {
         return -1;
      }
   }

   public void transmitRemoveItemFromSquareOnServer(IsoObject var1) {
      if (var1 != null && this.Objects.contains(var1)) {
         if (GameServer.bServer) {
            GameServer.RemoveItemFromMap(var1);
         }

      }
   }

   public void transmitModdata() {
      if (GameClient.bClient) {
         ByteBufferWriter var1 = GameClient.connection.startPacket();
         PacketTypes.doPacket((short)48, var1);
         var1.putInt(this.getX());
         var1.putInt(this.getY());
         var1.putInt(this.getZ());

         try {
            this.getModData().save(var1.bb);
         } catch (IOException var3) {
            var3.printStackTrace();
         }

         GameClient.connection.endPacketImmediate();
      } else if (GameServer.bServer) {
         GameServer.loadModData(this);
      }

   }

   public InventoryItem AddWorldInventoryItem(String var1, float var2, float var3, float var4) {
      InventoryItem var5 = InventoryItemFactory.CreateItem(var1);
      IsoWorldInventoryObject var6 = new IsoWorldInventoryObject(var5, this, var2, var3, var4);
      var5.setWorldItem(var6);
      var6.setKeyId(var5.getKeyId());
      var6.setName(var5.getName());
      this.Objects.add(var6);
      this.WorldObjects.add(var6);
      var6.square.chunk.recalcHashCodeObjects();
      if (GameClient.bClient) {
         var6.transmitCompleteItemToServer();
      }

      if (GameServer.bServer) {
         var6.transmitCompleteItemToClients();
      }

      return var5;
   }

   public InventoryItem AddWorldInventoryItem(InventoryItem var1, float var2, float var3, float var4) {
      return this.AddWorldInventoryItem(var1, var2, var3, var4, true);
   }

   public InventoryItem AddWorldInventoryItem(InventoryItem var1, float var2, float var3, float var4, boolean var5) {
      if (!var1.getFullType().contains(".Corpse")) {
         if (var1.getFullType().contains(".Generator")) {
            new IsoGenerator(var1, IsoWorld.instance.CurrentCell, this);
            return var1;
         } else {
            IsoWorldInventoryObject var16 = new IsoWorldInventoryObject(var1, this, var2, var3, var4);
            var16.setName(var1.getName());
            var16.setKeyId(var1.getKeyId());
            this.Objects.add(var16);
            this.WorldObjects.add(var16);
            var16.square.chunk.recalcHashCodeObjects();
            var1.setWorldItem(var16);
            var16.addToWorld();
            if (var5) {
               if (GameClient.bClient) {
                  var16.transmitCompleteItemToServer();
               }

               if (GameServer.bServer) {
                  var16.transmitCompleteItemToClients();
               }
            }

            return var1;
         }
      } else if (var1.byteData == null) {
         SharedDescriptors.Descriptor var15 = SharedDescriptors.pickRandomDescriptor();
         IsoZombie var13;
         if (var15 == null) {
            var13 = new IsoZombie(IsoWorld.instance.CurrentCell);
         } else {
            var13 = new IsoZombie(IsoWorld.instance.CurrentCell, var15.desc, var15.palette);
         }

         var13.setDir(IsoDirections.fromIndex(Rand.Next(8)));
         var13.angle.set(var13.dir.ToVector());
         var13.setFakeDead(false);
         var13.setHealth(0.0F);
         var13.upKillCount = false;
         var13.setX((float)this.x + var2);
         var13.setY((float)this.y + var3);
         var13.setZ((float)this.z);
         var13.square = this;
         var13.current = this;
         var13.DoZombieInventory();
         IsoDeadBody var17 = new IsoDeadBody(var13, true);
         this.addCorpse(var17, false);
         if (GameServer.bServer) {
            GameServer.sendCorpse(var17);
         }

         return var1;
      } else {
         IsoDeadBody var6 = new IsoDeadBody(IsoWorld.instance.CurrentCell);

         try {
            byte var7 = var1.byteData.get();
            byte var8 = var1.byteData.get();
            byte var9 = var1.byteData.get();
            byte var10 = var1.byteData.get();
            int var11 = 56;
            if (var7 == 87 && var8 == 86 && var9 == 69 && var10 == 82) {
               var11 = var1.byteData.getInt();
            } else {
               var1.byteData.rewind();
            }

            var6.load(var1.byteData, var11);
         } catch (IOException var12) {
            var12.printStackTrace();
         }

         var6.setX((float)this.x + var2);
         var6.setY((float)this.y + var3);
         var6.setZ((float)this.z);
         var6.square = this;
         if (GameClient.bClient && var6.sprite == null) {
            IsoZombie var14 = new IsoZombie((IsoCell)null);
            var14.dir = var6.dir;
            var14.current = this;
            var14.x = var6.x;
            var14.y = var6.y;
            var14.z = var6.z;
            if (var14.current != null) {
               var6 = new IsoDeadBody(var14);
            }
         } else {
            this.StaticMovingObjects.add(var6);
         }

         this.addCorpse(var6, false);
         if (GameServer.bServer) {
            GameServer.sendCorpse(var6);
            this.revisionUp();
         }

         if (GameClient.bClient) {
            this.clientModify();
         }

         return var1;
      }
   }

   public void restackSheetRope() {
      if (this.Is(IsoFlagType.climbSheetW) || this.Is(IsoFlagType.climbSheetN) || this.Is(IsoFlagType.climbSheetE) || this.Is(IsoFlagType.climbSheetS)) {
         for(int var1 = 0; var1 < this.getObjects().size() - 1; ++var1) {
            IsoObject var2 = (IsoObject)this.getObjects().get(var1);
            if (var2.getProperties() != null && (var2.getProperties().Is(IsoFlagType.climbSheetW) || var2.getProperties().Is(IsoFlagType.climbSheetN) || var2.getProperties().Is(IsoFlagType.climbSheetE) || var2.getProperties().Is(IsoFlagType.climbSheetS))) {
               if (GameServer.bServer) {
                  this.transmitRemoveItemFromSquare(var2);
                  this.Objects.add(var2);
                  var2.transmitCompleteItemToClients();
               } else if (!GameClient.bClient) {
                  this.Objects.remove(var2);
                  this.Objects.add(var2);
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

   public void Burn(boolean var1) {
      if (!GameServer.bServer && !GameClient.bClient || !ServerOptions.instance.NoFire.getValue()) {
         if (this.getCell() != null) {
            this.BurnWalls(var1);
         }
      }
   }

   public void BurnWalls(boolean var1) {
      if (!GameClient.bClient) {
         if (GameServer.bServer && SafeHouse.isSafeHouse(this, (String)null, false) != null) {
            if (ServerOptions.instance.NoFire.getValue()) {
               return;
            }

            if (!ServerOptions.instance.SafehouseAllowFire.getValue()) {
               return;
            }
         }

         for(int var2 = 0; var2 < this.SpecialObjects.size(); ++var2) {
            IsoObject var3 = (IsoObject)this.SpecialObjects.get(var2);
            if (var3 instanceof IsoThumpable && ((IsoThumpable)var3).haveSheetRope()) {
               ((IsoThumpable)var3).removeSheetRope((IsoPlayer)null);
            }

            if (var3 instanceof IsoWindow) {
               if (((IsoWindow)var3).haveSheetRope()) {
                  ((IsoWindow)var3).removeSheetRope((IsoPlayer)null);
               }

               ((IsoWindow)var3).removeSheet((IsoGameCharacter)null);
            }

            if (IsoWindowFrame.isWindowFrame(var3) && IsoWindowFrame.haveSheetRope(var3)) {
               IsoWindowFrame.removeSheetRope(var3, (IsoPlayer)null);
            }

            if (var3 instanceof BarricadeAble) {
               IsoBarricade var4 = ((BarricadeAble)var3).getBarricadeOnSameSquare();
               IsoBarricade var5 = ((BarricadeAble)var3).getBarricadeOnOppositeSquare();
               if (var4 != null) {
                  if (GameServer.bServer) {
                     GameServer.RemoveItemFromMap(var4);
                  } else {
                     this.RemoveTileObject(var4);
                  }
               }

               if (var5 != null) {
                  if (GameServer.bServer) {
                     GameServer.RemoveItemFromMap(var5);
                  } else {
                     var5.getSquare().RemoveTileObject(var5);
                  }
               }
            }
         }

         this.SpecialObjects.clear();
         boolean var10 = false;
         if (!this.getProperties().Is(IsoFlagType.burntOut)) {
            int var11 = 0;

            for(int var12 = 0; var12 < this.Objects.size(); ++var12) {
               IsoObject var13 = (IsoObject)this.Objects.get(var12);
               boolean var6 = false;
               if (var13.getSprite() != null && var13.getSprite().getName() != null && !var13.getSprite().getProperties().Is(IsoFlagType.water) && !var13.getSprite().getName().contains("_burnt_")) {
                  if (var13.getSprite().burntTile != null) {
                     var13.sprite = this.getCell().SpriteManager.getSprite(var13.getSprite().burntTile);
                     var13.RemoveAttachedAnims();
                     if (var13.Children != null) {
                        var13.Children.clear();
                     }

                     var13.transmitUpdatedSpriteToClients();
                     var13.setOverlaySprite((String)null);
                  } else if (var13.getType() == IsoObjectType.tree) {
                     var13.sprite = this.getCell().SpriteManager.getSprite("fencing_burnt_01_" + (Rand.Next(15, 19) + 1));
                     var13.RemoveAttachedAnims();
                     if (var13.Children != null) {
                        var13.Children.clear();
                     }

                     var13.transmitUpdatedSpriteToClients();
                     var13.setOverlaySprite((String)null);
                  } else if (!(var13 instanceof IsoTrap)) {
                     if (var13 instanceof IsoBarricade) {
                        if (GameServer.bServer) {
                           GameServer.RemoveItemFromMap(var13);
                        } else {
                           this.Objects.remove(var13);
                        }

                        --var12;
                     } else if (var13 instanceof IsoGenerator) {
                        IsoGenerator var15 = (IsoGenerator)var13;
                        if (var15.getFuel() > 0.0F) {
                           var11 += 20;
                        }

                        if (var15.isActivated()) {
                           var15.activated = false;
                           var15.setSurroundingElectricity();
                           if (GameServer.bServer) {
                              var15.syncIsoObject(false, (byte)0, (UdpConnection)null, (ByteBuffer)null);
                           }
                        }

                        if (GameServer.bServer) {
                           GameServer.RemoveItemFromMap(var13);
                        } else {
                           this.RemoveTileObject(var13);
                        }

                        --var12;
                     } else {
                        if (var13.getType() == IsoObjectType.wall && !var13.getProperties().Is("DoorWallW") && !var13.getProperties().Is("DoorWallN") && !var13.getProperties().Is("WindowN") && !var13.getProperties().Is(IsoFlagType.WindowW) && !var13.getSprite().getName().startsWith("walls_exterior_roofs_") && !var13.getSprite().getName().startsWith("fencing_") && !var13.getSprite().getName().startsWith("fixtures_railings_")) {
                           if (var13.getSprite().getProperties().Is(IsoFlagType.collideW) && !var13.getSprite().getProperties().Is(IsoFlagType.collideN)) {
                              var13.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "0" : "4"));
                           } else if (var13.getSprite().getProperties().Is(IsoFlagType.collideN) && !var13.getSprite().getProperties().Is(IsoFlagType.collideW)) {
                              var13.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "1" : "5"));
                           } else if (var13.getSprite().getProperties().Is(IsoFlagType.collideW) && var13.getSprite().getProperties().Is(IsoFlagType.collideN)) {
                              var13.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "2" : "6"));
                           } else if (var13.getProperties().Is(IsoFlagType.WallSE)) {
                              var13.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "3" : "7"));
                           }
                        } else {
                           if (var13 instanceof IsoDoor || var13 instanceof IsoWindow || var13 instanceof IsoCurtain) {
                              if (GameServer.bServer) {
                                 GameServer.RemoveItemFromMap(var13);
                              } else {
                                 this.RemoveTileObject(var13);
                                 var10 = true;
                              }

                              --var12;
                              continue;
                           }

                           if (var13.getProperties().Is(IsoFlagType.WindowW)) {
                              var13.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "8" : "12"));
                           } else if (var13.getProperties().Is("WindowN")) {
                              var13.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "9" : "13"));
                           } else if (var13.getProperties().Is("DoorWallW")) {
                              var13.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "10" : "14"));
                           } else if (var13.getProperties().Is("DoorWallN")) {
                              var13.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_01_" + (Rand.Next(2) == 0 ? "11" : "15"));
                           } else if (var13.getSprite().getProperties().Is(IsoFlagType.solidfloor) && !var13.getSprite().getProperties().Is(IsoFlagType.exterior)) {
                              var13.sprite = this.getCell().SpriteManager.getSprite("floors_burnt_01_0");
                           } else {
                              if (var13 instanceof IsoWaveSignal) {
                                 if (GameServer.bServer) {
                                    GameServer.RemoveItemFromMap(var13);
                                 } else {
                                    this.RemoveTileObject(var13);
                                    var10 = true;
                                 }

                                 --var12;
                                 continue;
                              }

                              if (var13.getContainer() != null && var13.getContainer().getItems() != null) {
                                 InventoryItem var7 = null;

                                 int var8;
                                 for(var8 = 0; var8 < var13.getContainer().getItems().size(); ++var8) {
                                    var7 = (InventoryItem)var13.getContainer().getItems().get(var8);
                                    if (var7 instanceof Food && ((Food)var7).isAlcoholic() || var7.getType().equals("PetrolCan") || var7.getType().equals("Bleach")) {
                                       var11 += 20;
                                       if (var11 > 100) {
                                          var11 = 100;
                                          break;
                                       }
                                    }
                                 }

                                 var13.sprite = this.getCell().SpriteManager.getSprite("floors_burnt_01_" + Rand.Next(1, 2));

                                 for(var8 = 0; var8 < var13.getContainerCount(); ++var8) {
                                    ItemContainer var9 = var13.getContainerByIndex(var8);
                                    var9.removeItemsFromProcessItems();
                                    var9.removeAllItems();
                                 }

                                 var13.removeAllContainers();
                                 var6 = true;
                              } else if (!var13.getSprite().getProperties().Is(IsoFlagType.solidtrans) && !var13.getSprite().getProperties().Is(IsoFlagType.bed) && !var13.getSprite().getProperties().Is(IsoFlagType.waterPiped)) {
                                 if (var13.getSprite().getName().startsWith("walls_exterior_roofs_")) {
                                    var13.sprite = this.getCell().SpriteManager.getSprite("walls_burnt_roofs_01_" + var13.getSprite().getName().substring(var13.getSprite().getName().lastIndexOf("_") + 1, var13.getSprite().getName().length()));
                                 } else if (var13.getSprite().getName().startsWith("roofs_")) {
                                    var13.sprite = this.getCell().SpriteManager.getSprite("roofs_burnt_01_" + var13.getSprite().getName().substring(var13.getSprite().getName().lastIndexOf("_") + 1, var13.getSprite().getName().length()));
                                 } else if ((var13.getSprite().getName().startsWith("fencing_") || var13.getSprite().getName().startsWith("fixtures_railings_")) && (var13.getSprite().getProperties().Is(IsoFlagType.HoppableN) || var13.getSprite().getProperties().Is(IsoFlagType.HoppableW))) {
                                    if (var13.getSprite().getProperties().Is(IsoFlagType.transparentW) && !var13.getSprite().getProperties().Is(IsoFlagType.transparentN)) {
                                       var13.sprite = this.getCell().SpriteManager.getSprite("fencing_burnt_01_0");
                                    } else if (var13.getSprite().getProperties().Is(IsoFlagType.transparentN) && !var13.getSprite().getProperties().Is(IsoFlagType.transparentW)) {
                                       var13.sprite = this.getCell().SpriteManager.getSprite("fencing_burnt_01_1");
                                    } else {
                                       var13.sprite = this.getCell().SpriteManager.getSprite("fencing_burnt_01_2");
                                    }
                                 }
                              } else {
                                 var13.sprite = this.getCell().SpriteManager.getSprite("floors_burnt_01_" + Rand.Next(1, 2));
                                 if (var13.getOverlaySprite() != null) {
                                    var13.setOverlaySprite((String)null);
                                 }
                              }
                           }
                        }

                        if (!var6 && !(var13 instanceof IsoThumpable)) {
                           var13.transmitUpdatedSpriteToClients();
                           var13.setOverlaySprite((String)null);
                        } else {
                           IsoObject var14 = IsoObject.getNew();
                           var14.setSprite(var13.getSprite());
                           var14.setSquare(this);
                           if (GameServer.bServer) {
                              var13.sendObjectChange("replaceWith", "object", var14);
                           }

                           this.Objects.set(var12, var14);
                        }
                     }
                  }
               }
            }

            if (var11 > 0 && var1) {
               if (GameServer.bServer) {
                  GameServer.PlayWorldSoundServer("BurnedObjectExploded", false, this, 0.0F, 50.0F, 1.0F, false);
               } else {
                  SoundManager.instance.PlayWorldSound("BurnedObjectExploded", this, 0.0F, 50.0F, 1.0F, false);
               }

               IsoFireManager.explode(this.getCell(), this, var11);
            }
         }

         if (!var10) {
            this.RecalcProperties();
         }

         this.getProperties().Set(IsoFlagType.burntOut);
         this.burntOut = true;
         MapCollisionData.instance.squareChanged(this);
         PolygonalMap2.instance.squareChanged(this);
      }
   }

   public void BurnWallsTCOnly() {
      for(int var1 = 0; var1 < this.Objects.size(); ++var1) {
         IsoObject var2 = (IsoObject)this.Objects.get(var1);
         if (var2.sprite == null) {
         }
      }

   }

   public void BurnTick() {
      if (!GameClient.bClient) {
         for(int var1 = 0; var1 < this.StaticMovingObjects.size(); ++var1) {
            IsoMovingObject var2 = (IsoMovingObject)this.StaticMovingObjects.get(var1);
            if (var2 instanceof IsoDeadBody) {
               ((IsoDeadBody)var2).Burn();
               if (!this.StaticMovingObjects.contains(var2)) {
                  --var1;
               }
            }
         }

      }
   }

   public boolean CalculateCollide(IsoGridSquare var1, boolean var2, boolean var3, boolean var4) {
      return this.CalculateCollide(var1, var2, var3, var4, false);
   }

   public boolean CalculateCollide(IsoGridSquare var1, boolean var2, boolean var3, boolean var4, boolean var5) {
      return this.CalculateCollide(var1, var2, var3, var4, var5, cellGetSquare);
   }

   public boolean CalculateCollide(IsoGridSquare var1, boolean var2, boolean var3, boolean var4, boolean var5, IsoGridSquare.GetSquare var6) {
      if (var1 == null && var3) {
         return true;
      } else if (var1 == null) {
         return false;
      } else {
         if (var2 && var1.Properties.Is(IsoFlagType.trans)) {
         }

         boolean var7 = false;
         boolean var8 = false;
         boolean var9 = false;
         boolean var10 = false;
         if (var1.x < this.x) {
            var7 = true;
         }

         if (var1.y < this.y) {
            var9 = true;
         }

         if (var1.x > this.x) {
            var8 = true;
         }

         if (var1.y > this.y) {
            var10 = true;
         }

         if (!var5 && var1.Properties.Is(IsoFlagType.solid)) {
            if (this.Has(IsoObjectType.stairsTW) && !var3 && var1.x < this.x && var1.y == this.y && var1.z == this.z) {
               return false;
            } else {
               return !this.Has(IsoObjectType.stairsTN) || var3 || var1.x != this.x || var1.y >= this.y || var1.z != this.z;
            }
         } else {
            if (!var4 && var1.Properties.Is(IsoFlagType.solidtrans)) {
               if (this.Has(IsoObjectType.stairsTW) && !var3 && var1.x < this.x && var1.y == this.y && var1.z == this.z) {
                  return false;
               }

               if (this.Has(IsoObjectType.stairsTN) && !var3 && var1.x == this.x && var1.y < this.y && var1.z == this.z) {
                  return false;
               }

               boolean var11 = false;
               if (var1.Properties.Is(IsoFlagType.windowW) || var1.Properties.Is(IsoFlagType.windowN)) {
                  var11 = true;
               }

               if (!var11 && (var1.Properties.Is(IsoFlagType.WindowW) || var1.Properties.Is(IsoFlagType.WindowN))) {
                  var11 = true;
               }

               IsoGridSquare var12;
               if (!var11) {
                  var12 = var6.getGridSquare(var1.x, var1.y + 1, this.z);
                  if (var12 != null && (var12.Is(IsoFlagType.windowN) || var12.Is(IsoFlagType.WindowN))) {
                     var11 = true;
                  }
               }

               if (!var11) {
                  var12 = var6.getGridSquare(var1.x + 1, var1.y, this.z);
                  if (var12 != null && (var12.Is(IsoFlagType.windowW) || var12.Is(IsoFlagType.WindowW))) {
                     var11 = true;
                  }
               }

               if (!var11) {
                  return true;
               }
            }

            if (var1.x != this.x && var1.y != this.y && this.z != var1.z && var3) {
               return true;
            } else {
               if (var3 && var1.z < this.z) {
                  label695: {
                     if (this.SolidFloorCached) {
                        if (this.SolidFloor) {
                           break label695;
                        }
                     } else if (this.TreatAsSolidFloor()) {
                        break label695;
                     }

                     if (!var1.Has(IsoObjectType.stairsTN) && !var1.Has(IsoObjectType.stairsTW)) {
                        return false;
                     }

                     return true;
                  }
               }

               if (var3 && var1.z == this.z) {
                  if (var1.x > this.x && var1.y == this.y && var1.Properties.Is(IsoFlagType.windowW)) {
                     return false;
                  }

                  if (var1.y > this.y && var1.x == this.x && var1.Properties.Is(IsoFlagType.windowN)) {
                     return false;
                  }

                  if (var1.x < this.x && var1.y == this.y && this.Properties.Is(IsoFlagType.windowW)) {
                     return false;
                  }

                  if (var1.y < this.y && var1.x == this.x && this.Properties.Is(IsoFlagType.windowN)) {
                     return false;
                  }
               }

               if (var1.x > this.x && var1.z < this.z && var1.Has(IsoObjectType.stairsTW)) {
                  return false;
               } else if (var1.y > this.y && var1.z < this.z && var1.Has(IsoObjectType.stairsTN)) {
                  return false;
               } else {
                  IsoGridSquare var20 = var6.getGridSquare(var1.x, var1.y, var1.z - 1);
                  if (var1.x != this.x && var1.z == this.z && var1.Has(IsoObjectType.stairsTN) && (var20 == null || !var20.Has(IsoObjectType.stairsTN) || var3)) {
                     return true;
                  } else if (var1.y > this.y && var1.x == this.x && var1.z == this.z && var1.Has(IsoObjectType.stairsTN) && (var20 == null || !var20.Has(IsoObjectType.stairsTN) || var3)) {
                     return true;
                  } else if (var1.x > this.x && var1.y == this.y && var1.z == this.z && var1.Has(IsoObjectType.stairsTW) && (var20 == null || !var20.Has(IsoObjectType.stairsTW) || var3)) {
                     return true;
                  } else if (var1.y == this.y || var1.z != this.z || !var1.Has(IsoObjectType.stairsTW) || var20 != null && var20.Has(IsoObjectType.stairsTW) && !var3) {
                     if (var1.x != this.x && var1.z == this.z && var1.Has(IsoObjectType.stairsMN)) {
                        return true;
                     } else if (var1.y != this.y && var1.z == this.z && var1.Has(IsoObjectType.stairsMW)) {
                        return true;
                     } else if (var1.x != this.x && var1.z == this.z && var1.Has(IsoObjectType.stairsBN)) {
                        return true;
                     } else if (var1.y != this.y && var1.z == this.z && var1.Has(IsoObjectType.stairsBW)) {
                        return true;
                     } else if (var1.x != this.x && var1.z == this.z && this.Has(IsoObjectType.stairsTN)) {
                        return true;
                     } else if (var1.y != this.y && var1.z == this.z && this.Has(IsoObjectType.stairsTW)) {
                        return true;
                     } else if (var1.x != this.x && var1.z == this.z && this.Has(IsoObjectType.stairsMN)) {
                        return true;
                     } else if (var1.y != this.y && var1.z == this.z && this.Has(IsoObjectType.stairsMW)) {
                        return true;
                     } else if (var1.x != this.x && var1.z == this.z && this.Has(IsoObjectType.stairsBN)) {
                        return true;
                     } else if (var1.y != this.y && var1.z == this.z && this.Has(IsoObjectType.stairsBW)) {
                        return true;
                     } else if (var1.y < this.y && var1.x == this.x && var1.z > this.z && this.Has(IsoObjectType.stairsTN)) {
                        return false;
                     } else if (var1.x < this.x && var1.y == this.y && var1.z > this.z && this.Has(IsoObjectType.stairsTW)) {
                        return false;
                     } else if (var1.y > this.y && var1.x == this.x && var1.z < this.z && var1.Has(IsoObjectType.stairsTN)) {
                        return false;
                     } else if (var1.x > this.x && var1.y == this.y && var1.z < this.z && var1.Has(IsoObjectType.stairsTW)) {
                        return false;
                     } else {
                        if (var1.z == this.z) {
                           label529: {
                              if (var1.SolidFloorCached) {
                                 if (var1.SolidFloor) {
                                    break label529;
                                 }
                              } else if (var1.TreatAsSolidFloor()) {
                                 break label529;
                              }

                              if (var3) {
                                 return true;
                              }
                           }
                        }

                        if (var1.z == this.z) {
                           label522: {
                              if (var1.SolidFloorCached) {
                                 if (var1.SolidFloor) {
                                    break label522;
                                 }
                              } else if (var1.TreatAsSolidFloor()) {
                                 break label522;
                              }

                              if (var1.z > 0) {
                                 var20 = var6.getGridSquare(var1.x, var1.y, var1.z - 1);
                                 if (var20 == null) {
                                    return true;
                                 }
                              }
                           }
                        }

                        if (this.z != var1.z) {
                           if (var1.z < this.z && var1.x == this.x && var1.y == this.y) {
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
                           boolean var19 = var9 && this.Properties.Is(IsoFlagType.collideN);
                           boolean var13 = var7 && this.Properties.Is(IsoFlagType.collideW);
                           boolean var14 = var10 && var1.Properties.Is(IsoFlagType.collideN);
                           boolean var15 = var8 && var1.Properties.Is(IsoFlagType.collideW);
                           if (var19 && var3 && this.Properties.Is(IsoFlagType.canPathN)) {
                              var19 = false;
                           }

                           if (var13 && var3 && this.Properties.Is(IsoFlagType.canPathW)) {
                              var13 = false;
                           }

                           if (var14 && var3 && var1.Properties.Is(IsoFlagType.canPathN)) {
                              var14 = false;
                           }

                           if (var15 && var3 && var1.Properties.Is(IsoFlagType.canPathW)) {
                              var15 = false;
                           }

                           if (var13 && this.Has(IsoObjectType.stairsTW) && !var3) {
                              var13 = false;
                           }

                           if (var19 && this.Has(IsoObjectType.stairsTN) && !var3) {
                              var19 = false;
                           }

                           if (!var19 && !var13 && !var14 && !var15) {
                              boolean var16 = var1.x != this.x && var1.y != this.y;
                              if (var16) {
                                 IsoGridSquare var17 = var6.getGridSquare(this.x, var1.y, this.z);
                                 IsoGridSquare var18 = var6.getGridSquare(var1.x, this.y, this.z);
                                 if (var17 != null && var17 != this && var17 != var1) {
                                    var17.RecalcPropertiesIfNeeded();
                                 }

                                 if (var18 != null && var18 != this && var18 != var1) {
                                    var18.RecalcPropertiesIfNeeded();
                                 }

                                 if (var1 == this || var17 == var18 || var17 == this || var18 == this || var17 == var1 || var18 == var1) {
                                    return true;
                                 }

                                 if (var1.x == this.x + 1 && var1.y == this.y + 1 && var17 != null && var18 != null && var17.Is(IsoFlagType.windowN) && var18.Is(IsoFlagType.windowW)) {
                                    return true;
                                 }

                                 if (var1.x == this.x - 1 && var1.y == this.y - 1 && var17 != null && var18 != null && var17.Is(IsoFlagType.windowW) && var18.Is(IsoFlagType.windowN)) {
                                    return true;
                                 }

                                 if (this.CalculateCollide(var17, var2, var3, var4, false, var6)) {
                                    return true;
                                 }

                                 if (this.CalculateCollide(var18, var2, var3, var4, false, var6)) {
                                    return true;
                                 }

                                 if (var1.CalculateCollide(var17, var2, var3, var4, false, var6)) {
                                    return true;
                                 }

                                 if (var1.CalculateCollide(var18, var2, var3, var4, false, var6)) {
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

   public boolean CalculateVisionBlocked(IsoGridSquare var1) {
      return this.CalculateVisionBlocked(var1, cellGetSquare);
   }

   public boolean CalculateVisionBlocked(IsoGridSquare var1, IsoGridSquare.GetSquare var2) {
      if (var1 == null) {
         return false;
      } else if (Math.abs(var1.getX() - this.getX()) <= 1 && Math.abs(var1.getY() - this.getY()) <= 1) {
         boolean var3 = false;
         boolean var4 = false;
         boolean var5 = false;
         boolean var6 = false;
         if (var1.x < this.x) {
            var3 = true;
         }

         if (var1.y < this.y) {
            var5 = true;
         }

         if (var1.x > this.x) {
            var4 = true;
         }

         if (var1.y > this.y) {
            var6 = true;
         }

         if (!var1.Properties.Is(IsoFlagType.trans) && !this.Properties.Is(IsoFlagType.trans)) {
            if (this.z != var1.z) {
               IsoGridSquare var7;
               if (var1.z > this.z) {
                  label188: {
                     if (var1.SolidFloorCached) {
                        if (!var1.SolidFloor) {
                           break label188;
                        }
                     } else if (!var1.TreatAsSolidFloor()) {
                        break label188;
                     }

                     return true;
                  }

                  if (this.Properties.Is(IsoFlagType.noStart)) {
                     return true;
                  }

                  var7 = var2.getGridSquare(this.x, this.y, var1.z);
                  if (var7 == null) {
                     return false;
                  }

                  if (var7.SolidFloorCached) {
                     if (var7.SolidFloor) {
                        return true;
                     }
                  } else if (var7.TreatAsSolidFloor()) {
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

                     var7 = var2.getGridSquare(var1.x, var1.y, this.z);
                     if (var7 == null) {
                        return false;
                     }

                     if (var7.SolidFloorCached) {
                        if (!var7.SolidFloor) {
                           break label236;
                        }
                     } else if (!var7.TreatAsSolidFloor()) {
                        break label236;
                     }

                     return true;
                  }
               }
            }

            if (var1.x > this.x && var1.Properties.Is(IsoFlagType.transparentW)) {
               return false;
            } else if (var1.y > this.y && var1.Properties.Is(IsoFlagType.transparentN)) {
               return false;
            } else if (var1.x < this.x && this.Properties.Is(IsoFlagType.transparentW)) {
               return false;
            } else if (var1.y < this.y && this.Properties.Is(IsoFlagType.transparentN)) {
               return false;
            } else {
               boolean var14 = var5 && this.Properties.Is(IsoFlagType.collideN);
               boolean var8 = var3 && this.Properties.Is(IsoFlagType.collideW);
               boolean var9 = var6 && var1.Properties.Is(IsoFlagType.collideN);
               boolean var10 = var4 && var1.Properties.Is(IsoFlagType.collideW);
               if (!var14 && !var8 && !var9 && !var10) {
                  boolean var11 = var1.x != this.x && var1.y != this.y;
                  if (!var1.Properties.Is(IsoFlagType.solid) && !var1.Properties.Is(IsoFlagType.blocksight)) {
                     if (var11) {
                        IsoGridSquare var12 = var2.getGridSquare(this.x, var1.y, this.z);
                        IsoGridSquare var13 = var2.getGridSquare(var1.x, this.y, this.z);
                        if (var12 != null && var12 != this && var12 != var1) {
                           var12.RecalcPropertiesIfNeeded();
                        }

                        if (var13 != null && var13 != this && var13 != var1) {
                           var13.RecalcPropertiesIfNeeded();
                        }

                        if (this.CalculateVisionBlocked(var12)) {
                           return true;
                        }

                        if (this.CalculateVisionBlocked(var13)) {
                           return true;
                        }

                        if (var1.CalculateVisionBlocked(var12)) {
                           return true;
                        }

                        if (var1.CalculateVisionBlocked(var13)) {
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

   public IsoGameCharacter FindFriend(IsoGameCharacter var1, int var2, Stack var3) {
      Stack var4 = new Stack();

      for(int var5 = 0; var5 < var1.getLocalList().size(); ++var5) {
         IsoMovingObject var6 = (IsoMovingObject)var1.getLocalList().get(var5);
         if (var6 != var1 && var6 != var1.getFollowingTarget() && var6 instanceof IsoGameCharacter && !(var6 instanceof IsoZombie) && !var3.contains(var6)) {
            var4.add((IsoGameCharacter)var6);
         }
      }

      float var10 = 1000000.0F;
      IsoGameCharacter var11 = null;
      Iterator var7 = var4.iterator();

      while(var7.hasNext()) {
         IsoGameCharacter var8 = (IsoGameCharacter)var7.next();
         float var9 = 0.0F;
         var9 += Math.abs((float)this.getX() - var8.getX());
         var9 += Math.abs((float)this.getY() - var8.getY());
         var9 += Math.abs((float)this.getZ() - var8.getZ());
         if (var9 < var10) {
            var11 = var8;
            var10 = var9;
         }

         if (var8 == IsoPlayer.getInstance()) {
            var11 = var8;
            var9 = 0.0F;
         }
      }

      if (var10 > (float)var2) {
         return null;
      } else {
         return var11;
      }
   }

   public IsoGameCharacter FindEnemy(IsoGameCharacter var1, int var2, ArrayList var3, IsoGameCharacter var4, int var5) {
      float var6 = 1000000.0F;
      IsoGameCharacter var7 = null;

      for(int var8 = 0; var8 < var3.size(); ++var8) {
         IsoGameCharacter var9 = (IsoGameCharacter)var3.get(var8);
         float var10 = 0.0F;
         var10 += Math.abs((float)this.getX() - var9.getX());
         var10 += Math.abs((float)this.getY() - var9.getY());
         var10 += Math.abs((float)this.getZ() - var9.getZ());
         if (var10 < (float)var2 && var10 < var6 && var9.DistTo(var4) < (float)var5) {
            var7 = var9;
            var6 = var10;
         }
      }

      if (var6 > (float)var2) {
         return null;
      } else {
         return var7;
      }
   }

   public IsoGameCharacter FindEnemy(IsoGameCharacter var1, int var2, ArrayList var3) {
      float var4 = 1000000.0F;
      IsoGameCharacter var5 = null;

      for(int var6 = 0; var6 < var3.size(); ++var6) {
         IsoGameCharacter var7 = (IsoGameCharacter)var3.get(var6);
         float var8 = 0.0F;
         var8 += Math.abs((float)this.getX() - var7.getX());
         var8 += Math.abs((float)this.getY() - var7.getY());
         var8 += Math.abs((float)this.getZ() - var7.getZ());
         if (var8 < var4) {
            var5 = var7;
            var4 = var8;
         }
      }

      if (var4 > (float)var2) {
         return null;
      } else {
         return var5;
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
      String var1 = null;
      if (this.Properties.Is("waterAmount")) {
         var1 = this.Properties.Val("waterAmount");
      }

      String var2 = null;
      if (this.Properties.Is("fuelAmount")) {
         var2 = this.Properties.Val("fuelAmount");
      }

      if (this.zone == null) {
         this.zone = IsoWorld.instance.MetaGrid.getZoneAt(this.x, this.y, this.z);
      }

      this.Properties.Clear();
      this.hasTypes.clear();
      this.hasTree = false;
      boolean var3 = false;
      boolean var4 = false;
      int var5 = this.Objects.size();
      IsoObject[] var6 = (IsoObject[])this.Objects.getElements();

      for(int var7 = 0; var7 < var5; ++var7) {
         IsoObject var8 = var6[var7];
         if (var8 != null) {
            PropertyContainer var9 = var8.getProperties();
            if (var9 != null && !var9.Is(IsoFlagType.blueprint)) {
               if (var8.getType() == IsoObjectType.tree) {
                  this.hasTree = true;
               }

               this.hasTypes.set(var8.getType(), true);
               this.Properties.AddProperties(var9);
               if (var9.water) {
                  var4 = false;
               } else {
                  if (!var4 && var9.solidfloor) {
                     var4 = true;
                  }

                  if (!var3 && var9.solidtrans) {
                     var3 = true;
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

      if (var1 != null) {
         this.getProperties().Set("waterAmount", var1, false);
      }

      if (var2 != null) {
         this.getProperties().Set("fuelAmount", var2, false);
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

      if (!var3 && var4 && this.Properties.water) {
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

   public void ReCalculateCollide(IsoGridSquare var1) {
      this.ReCalculateCollide(var1, cellGetSquare);
   }

   public void ReCalculateCollide(IsoGridSquare var1, IsoGridSquare.GetSquare var2) {
      boolean var3 = this.CalculateCollide(var1, false, false, false, false, var2);
      this.collideMatrix[1 + (var1.x - this.x)][1 + (var1.y - this.y)][1 + (var1.z - this.z)] = var3;
   }

   public void ReCalculatePathFind(IsoGridSquare var1) {
      this.ReCalculatePathFind(var1, cellGetSquare);
   }

   public void ReCalculatePathFind(IsoGridSquare var1, IsoGridSquare.GetSquare var2) {
      boolean var3 = this.CalculateCollide(var1, false, true, false, false, var2);
      this.pathMatrix[1 + (var1.x - this.x)][1 + (var1.y - this.y)][1 + (var1.z - this.z)] = var3;
   }

   public void ReCalculateVisionBlocked(IsoGridSquare var1) {
      this.ReCalculateVisionBlocked(var1, cellGetSquare);
   }

   public void ReCalculateVisionBlocked(IsoGridSquare var1, IsoGridSquare.GetSquare var2) {
      boolean var3 = this.CalculateVisionBlocked(var1, var2);
      this.visionMatrix[1 + (var1.x - this.x)][1 + (var1.y - this.y)][1 + (var1.z - this.z)] = var3;
   }

   private static boolean testCollideSpecialObjects(IsoMovingObject var0, IsoGridSquare var1, IsoGridSquare var2) {
      for(int var3 = 0; var3 < var2.SpecialObjects.size(); ++var3) {
         IsoObject var4 = (IsoObject)var2.SpecialObjects.get(var3);
         if (var4.TestCollide(var0, var1, var2)) {
            if (var4 instanceof IsoDoor) {
               var0.setCollidedWithDoor(true);
            } else if (var4 instanceof IsoThumpable && ((IsoThumpable)var4).isDoor) {
               var0.setCollidedWithDoor(true);
            }

            var0.setCollidedObject(var4);
            return true;
         }
      }

      return false;
   }

   public boolean testCollideAdjacent(IsoMovingObject var1, int var2, int var3, int var4) {
      if (var1 instanceof IsoPlayer && ((IsoPlayer)var1).isNoClip()) {
         return false;
      } else if (this.collideMatrix == null) {
         return true;
      } else if (var2 >= -1 && var2 <= 1 && var3 >= -1 && var3 <= 1 && var4 >= -1 && var4 <= 1) {
         if (this.x + var2 >= 0 && this.y + var3 >= 0 && IsoWorld.instance.MetaGrid.isValidChunk((this.x + var2) / 10, (this.y + var3) / 10)) {
            IsoGridSquare var5 = this.getCell().getGridSquare(this.x + var2, this.y + var3, this.z + var4);
            SafeHouse var6 = null;
            if ((GameServer.bServer || GameClient.bClient) && var1 instanceof IsoPlayer && !ServerOptions.instance.SafehouseAllowTrepass.getValue()) {
               IsoGridSquare var7 = this.getCell().getGridSquare(this.x + var2, this.y + var3, 0);
               var6 = SafeHouse.isSafeHouse(var7, ((IsoPlayer)var1).getUsername(), true);
            }

            if (var6 != null) {
               return true;
            } else {
               if (var5 != null && var1 != null) {
                  IsoObject var8 = this.testCollideSpecialObjects(var5);
                  if (var8 != null) {
                     var1.collideWith(var8);
                     if (var8 instanceof IsoDoor) {
                        var1.setCollidedWithDoor(true);
                     } else if (var8 instanceof IsoThumpable && ((IsoThumpable)var8).isDoor) {
                        var1.setCollidedWithDoor(true);
                     }

                     var1.setCollidedObject(var8);
                     return true;
                  }
               }

               if (UseSlowCollision) {
                  return this.CalculateCollide(var5, false, false, false);
               } else {
                  if (var1 instanceof IsoPlayer && this.collideMatrix[var2 + 1][var3 + 1][var4 + 1]) {
                     this.RecalcAllWithNeighbours(true);
                  }

                  return this.collideMatrix[var2 + 1][var3 + 1][var4 + 1];
               }
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public boolean testCollideAdjacentAdvanced(int var1, int var2, int var3, boolean var4) {
      if (this.collideMatrix == null) {
         return true;
      } else if (var1 >= -1 && var1 <= 1 && var2 >= -1 && var2 <= 1 && var3 >= -1 && var3 <= 1) {
         IsoGridSquare var5 = this.getCell().getGridSquare(this.x + var1, this.y + var2, this.z + var3);
         if (var5 != null) {
            int var6;
            IsoObject var7;
            if (!var5.SpecialObjects.isEmpty()) {
               for(var6 = 0; var6 < var5.SpecialObjects.size(); ++var6) {
                  var7 = (IsoObject)var5.SpecialObjects.get(var6);
                  if (var7.TestCollide((IsoMovingObject)null, this, var5)) {
                     return true;
                  }
               }
            }

            if (!this.SpecialObjects.isEmpty()) {
               for(var6 = 0; var6 < this.SpecialObjects.size(); ++var6) {
                  var7 = (IsoObject)this.SpecialObjects.get(var6);
                  if (var7.TestCollide((IsoMovingObject)null, this, var5)) {
                     return true;
                  }
               }
            }
         }

         return UseSlowCollision ? this.CalculateCollide(var5, false, false, false) : this.collideMatrix[var1 + 1][var2 + 1][var3 + 1];
      } else {
         return true;
      }
   }

   public static void setCollisionMode() {
      UseSlowCollision = !UseSlowCollision;
   }

   public boolean testPathFindAdjacent(IsoMovingObject var1, int var2, int var3, int var4) {
      return this.testPathFindAdjacent(var1, var2, var3, var4, cellGetSquare);
   }

   public boolean testPathFindAdjacent(IsoMovingObject var1, int var2, int var3, int var4, IsoGridSquare.GetSquare var5) {
      if (var2 >= -1 && var2 <= 1 && var3 >= -1 && var3 <= 1 && var4 >= -1 && var4 <= 1) {
         IsoGridSquare var6;
         if (this.Has(IsoObjectType.stairsTN) || this.Has(IsoObjectType.stairsTW)) {
            var6 = var5.getGridSquare(var2 + this.x, var3 + this.y, var4 + this.z);
            if (var6 == null) {
               return true;
            }

            if (this.Has(IsoObjectType.stairsTN) && var6.y < this.y && var6.z == this.z) {
               return true;
            }

            if (this.Has(IsoObjectType.stairsTW) && var6.x < this.x && var6.z == this.z) {
               return true;
            }
         }

         if (bDoSlowPathfinding) {
            var6 = var5.getGridSquare(var2 + this.x, var3 + this.y, var4 + this.z);
            return this.CalculateCollide(var6, false, true, false, false, var5);
         } else {
            return this.pathMatrix[var2 + 1][var3 + 1][var4 + 1];
         }
      } else {
         return true;
      }
   }

   public LosUtil.TestResults testVisionAdjacent(int var1, int var2, int var3, boolean var4, boolean var5) {
      if (var1 >= -1 && var1 <= 1 && var2 >= -1 && var2 <= 1 && var3 >= -1 && var3 <= 1) {
         IsoGridSquare var6;
         if (var3 == 1 && (var1 != 0 || var2 != 0) && this.HasElevatedFloor()) {
            var6 = this.getCell().getGridSquare(this.x, this.y, this.z + var3);
            if (var6 != null) {
               return var6.testVisionAdjacent(var1, var2, 0, var4, var5);
            }
         }

         if (var3 == -1 && (var1 != 0 || var2 != 0)) {
            var6 = this.getCell().getGridSquare(this.x + var1, this.y + var2, this.z + var3);
            if (var6 != null && var6.HasElevatedFloor()) {
               return this.testVisionAdjacent(var1, var2, 0, var4, var5);
            }
         }

         LosUtil.TestResults var12 = LosUtil.TestResults.Clear;
         IsoGridSquare var7;
         if (var1 != 0 && var2 != 0 && var4) {
            var12 = this.DoDiagnalCheck(var1, var2, var3, var5);
            if (var12 == LosUtil.TestResults.Clear || var12 == LosUtil.TestResults.ClearThroughWindow || var12 == LosUtil.TestResults.ClearThroughOpenDoor || var12 == LosUtil.TestResults.ClearThroughClosedDoor) {
               var7 = this.getCell().getGridSquare(this.x + var1, this.y + var2, this.z + var3);
               if (var7 != null) {
                  var12 = var7.DoDiagnalCheck(-var1, -var2, -var3, var5);
               }
            }

            return var12;
         } else {
            var7 = this.getCell().getGridSquare(this.x + var1, this.y + var2, this.z + var3);
            LosUtil.TestResults var8 = LosUtil.TestResults.Clear;
            if (var7 != null && var7.z == this.z) {
               int var9;
               IsoObject var10;
               IsoObject.VisionResult var11;
               if (!this.SpecialObjects.isEmpty()) {
                  for(var9 = 0; var9 < this.SpecialObjects.size(); ++var9) {
                     var10 = (IsoObject)this.SpecialObjects.get(var9);
                     if (var10 == null) {
                        return LosUtil.TestResults.Clear;
                     }

                     var11 = var10.TestVision(this, var7);
                     if (var11 != IsoObject.VisionResult.NoEffect) {
                        if (var11 == IsoObject.VisionResult.Unblocked && var10 instanceof IsoDoor) {
                           var8 = ((IsoDoor)var10).IsOpen() ? LosUtil.TestResults.ClearThroughOpenDoor : LosUtil.TestResults.ClearThroughClosedDoor;
                        } else if (var11 == IsoObject.VisionResult.Unblocked && var10 instanceof IsoThumpable && ((IsoThumpable)var10).isDoor) {
                           var8 = LosUtil.TestResults.ClearThroughOpenDoor;
                        } else if (var11 == IsoObject.VisionResult.Unblocked && var10 instanceof IsoWindow) {
                           var8 = LosUtil.TestResults.ClearThroughWindow;
                        } else {
                           if (var11 == IsoObject.VisionResult.Blocked && var10 instanceof IsoDoor && !var5) {
                              return LosUtil.TestResults.Blocked;
                           }

                           if (var11 == IsoObject.VisionResult.Blocked && var10 instanceof IsoThumpable && ((IsoThumpable)var10).isDoor && !var5) {
                              return LosUtil.TestResults.Blocked;
                           }

                           if (var11 == IsoObject.VisionResult.Blocked && var10 instanceof IsoThumpable && ((IsoThumpable)var10).isWindow()) {
                              return LosUtil.TestResults.Blocked;
                           }

                           if (var11 == IsoObject.VisionResult.Blocked && var10 instanceof IsoCurtain) {
                              return LosUtil.TestResults.Blocked;
                           }

                           if (var11 == IsoObject.VisionResult.Blocked && var10 instanceof IsoWindow) {
                              return LosUtil.TestResults.Blocked;
                           }

                           if (var11 == IsoObject.VisionResult.Blocked && var10 instanceof IsoBarricade) {
                              return LosUtil.TestResults.Blocked;
                           }
                        }
                     }
                  }
               }

               if (!var7.SpecialObjects.isEmpty()) {
                  for(var9 = 0; var9 < var7.SpecialObjects.size(); ++var9) {
                     var10 = (IsoObject)var7.SpecialObjects.get(var9);
                     if (var10 == null) {
                        return LosUtil.TestResults.Clear;
                     }

                     var11 = var10.TestVision(this, var7);
                     if (var11 != IsoObject.VisionResult.NoEffect) {
                        if (var11 == IsoObject.VisionResult.Unblocked && var10 instanceof IsoDoor) {
                           var8 = ((IsoDoor)var10).IsOpen() ? LosUtil.TestResults.ClearThroughOpenDoor : LosUtil.TestResults.ClearThroughClosedDoor;
                        } else if (var11 == IsoObject.VisionResult.Unblocked && var10 instanceof IsoThumpable && ((IsoThumpable)var10).isDoor) {
                           var8 = LosUtil.TestResults.ClearThroughOpenDoor;
                        } else if (var11 == IsoObject.VisionResult.Unblocked && var10 instanceof IsoWindow) {
                           var8 = LosUtil.TestResults.ClearThroughWindow;
                        } else {
                           if (var11 == IsoObject.VisionResult.Blocked && var10 instanceof IsoDoor && !var5) {
                              return LosUtil.TestResults.Blocked;
                           }

                           if (var11 == IsoObject.VisionResult.Blocked && var10 instanceof IsoThumpable && ((IsoThumpable)var10).isDoor && !var5) {
                              return LosUtil.TestResults.Blocked;
                           }

                           if (var11 == IsoObject.VisionResult.Blocked && var10 instanceof IsoThumpable && ((IsoThumpable)var10).isWindow()) {
                              return LosUtil.TestResults.Blocked;
                           }

                           if (var11 == IsoObject.VisionResult.Blocked && var10 instanceof IsoCurtain) {
                              return LosUtil.TestResults.Blocked;
                           }

                           if (var11 == IsoObject.VisionResult.Blocked && var10 instanceof IsoWindow) {
                              return LosUtil.TestResults.Blocked;
                           }

                           if (var11 == IsoObject.VisionResult.Blocked && var10 instanceof IsoBarricade) {
                              return LosUtil.TestResults.Blocked;
                           }
                        }
                     }
                  }
               }
            }

            return !this.visionMatrix[var1 + 1][var2 + 1][var3 + 1] ? var8 : LosUtil.TestResults.Blocked;
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

   public void AddSpecialTileObject(IsoObject var1) {
      this.AddSpecialObject(var1);
   }

   public static void bubbleSort3(ArrayList var0) {
      int var1 = var0.size();
      boolean var2 = true;

      while(var2) {
         --var1;
         var2 = false;

         for(int var3 = 0; var3 < var1; ++var3) {
            IsoMovingObject var4 = (IsoMovingObject)var0.get(var3);
            IsoMovingObject var5 = (IsoMovingObject)var0.get(var3 + 1);
            if (var4.compareToY(var5) == 1) {
               var0.set(var3, var5);
               var0.set(var3 + 1, var4);
               var2 = true;
            }
         }
      }

   }

   public void RenderCharacters(int var1, boolean var2) {
      this.renderCharacters(var1, var2, false);
   }

   void renderCharacters(int var1, boolean var2) {
      this.renderCharacters(var1, var2, true);
   }

   void renderCharacters(int var1, boolean var2, boolean var3) {
      if (this.z < var1) {
         if (!isOnScreenLast) {
         }

         if (var3) {
            IndieGL.glBlendFunc(770, 771);
         }

         if (!this.MovingObjects.isEmpty() || !this.StaticMovingObjects.isEmpty()) {
            IndieGL.End();
         }

         if (this.MovingObjects.size() > 1) {
            Collections.sort(this.MovingObjects, comp);
         }

         int var4 = IsoCamera.frameState.playerIndex;
         ColorInfo var5 = this.lightInfo[var4];
         int var6 = this.StaticMovingObjects.size();

         int var7;
         IsoMovingObject var8;
         for(var7 = 0; var7 < var6; ++var7) {
            var8 = (IsoMovingObject)this.StaticMovingObjects.get(var7);
            if (var8.sprite != null && (!var2 || var8 instanceof IsoDeadBody) && (var2 || !(var8 instanceof IsoDeadBody))) {
               var8.render(var8.getX(), var8.getY(), var8.getZ(), var5, true);
            }
         }

         var6 = this.MovingObjects.size();

         for(var7 = 0; var7 < var6; ++var7) {
            var8 = (IsoMovingObject)this.MovingObjects.get(var7);
            if (var8 != null && var8.sprite != null) {
               boolean var9 = var8.bOnFloor;
               if (var9 && var8 instanceof IsoZombie) {
                  IsoZombie var10 = (IsoZombie)var8;
                  var9 = var10.bCrawling || var10.legsSprite.CurrentAnim != null && var10.legsSprite.CurrentAnim.name.equals("ZombieDeath") && var10.def.isFinished();
               }

               if ((!var2 || var9) && (var2 || !var9)) {
                  if (var8 instanceof IsoPlayer) {
                     IsoPlayer var11 = (IsoPlayer)var8;
                     if (var11.bRemote) {
                        var11.netHistory.render(var11);
                     }
                  }

                  if (GameClient.bClient && var8 != IsoPlayer.getInstance() && !(var8 instanceof IsoLuaMover)) {
                     var8.render(var8.bx, var8.by, var8.getZ(), var5, true);
                  } else {
                     var8.render(var8.getX(), var8.getY(), var8.getZ(), var5, true);
                  }
               }
            }
         }

      }
   }

   public void renderDeferredCharacters(int var1) {
      if (!this.DeferedCharacters.isEmpty()) {
         if (this.DeferredCharacterTick != this.getCell().DeferredCharacterTick) {
            this.DeferedCharacters.clear();
         } else if (this.z >= var1) {
            this.DeferedCharacters.clear();
         } else if (PerformanceSettings.LightingFrameSkip != 3) {
            short var2 = this.getCell().getStencilValue2z(this.x, this.y, this.z - 1);
            this.getCell().setStencilValue2z(this.x, this.y, this.z - 1, var2);
            IndieGL.enableAlphaTest();
            IndieGL.glAlphaFunc(516, 0.0F);
            IndieGL.glStencilFunc(519, var2, 127);
            IndieGL.glStencilOp(7680, 7680, 7681);
            if (texWhite == null) {
               texWhite = Texture.getSharedTexture("media/ui/white.png");
            }

            Texture var3 = texWhite;
            int var4 = (int)IsoUtils.XToScreenInt(this.x, this.y, this.z, 0);
            int var5 = (int)IsoUtils.YToScreenInt(this.x, this.y, this.z, 0);
            var4 -= (int)IsoCamera.frameState.OffX;
            var5 -= (int)IsoCamera.frameState.OffY;
            IndieGL.glColorMask(false, false, false, false);
            var3.renderwallnw(var4, var5, 64 * Core.TileScale, 32 * Core.TileScale, -1, -1, -1, -1, -1, -1);
            IndieGL.glColorMask(true, true, true, true);
            IndieGL.enableAlphaTest();
            IndieGL.glAlphaFunc(516, 0.0F);
            IndieGL.glStencilFunc(514, var2, 127);
            IndieGL.glStencilOp(7680, 7680, 7680);
            ColorInfo var6 = this.lightInfo[IsoCamera.frameState.playerIndex];
            Collections.sort(this.DeferedCharacters, comp);

            for(int var7 = 0; var7 < this.DeferedCharacters.size(); ++var7) {
               IsoGameCharacter var8 = (IsoGameCharacter)this.DeferedCharacters.get(var7);
               if (var8.sprite != null) {
                  var8.setbDoDefer(false);
                  if (GameClient.bClient && var8 != IsoPlayer.getInstance() && !(var8 instanceof IsoLuaMover)) {
                     var8.render(var8.bx, var8.by, var8.getZ(), var6, true);
                  } else {
                     var8.render(var8.getX(), var8.getY(), var8.getZ(), var6, true);
                  }

                  var8.renderObjectPicker(var8.getX(), var8.getY(), var8.getZ(), var6);
                  var8.setbDoDefer(true);
               }
            }

            this.DeferedCharacters.clear();
            IndieGL.glAlphaFunc(516, 0.0F);
            IndieGL.glStencilFunc(519, 1, 255);
            IndieGL.glStencilOp(7680, 7680, 7680);
         }
      }
   }

   public void switchLight(boolean var1) {
      for(int var2 = 0; var2 < this.Objects.size(); ++var2) {
         IsoObject var3 = (IsoObject)this.Objects.get(var2);
         if (var3 instanceof IsoLightSwitch) {
            ((IsoLightSwitch)var3).setActive(var1);
         }
      }

   }

   public boolean IsOnScreen() {
      if (this.CachedScreenValue != Core.TileScale) {
         this.CachedScreenX = (float)((int)IsoUtils.XToScreenInt(this.x, this.y, this.z, 0));
         this.CachedScreenY = (float)((int)IsoUtils.YToScreenInt(this.x, this.y, this.z, 0));
         this.CachedScreenValue = Core.TileScale;
      }

      float var1 = this.CachedScreenX;
      float var2 = this.CachedScreenY;
      var1 -= (float)((int)IsoCamera.frameState.OffX);
      var2 -= (float)((int)IsoCamera.frameState.OffY);
      if (this.hasTree) {
         int var3 = 384 * Core.TileScale / 2 - 96 * Core.TileScale;
         int var4 = 256 * Core.TileScale - 32 * Core.TileScale;
         if (var1 + (float)var3 <= 0.0F) {
            return false;
         } else if (var2 + (float)(32 * Core.TileScale) <= 0.0F) {
            return false;
         } else if (var1 - (float)var3 >= (float)IsoCamera.frameState.OffscreenWidth) {
            return false;
         } else {
            return !(var2 - (float)var4 >= (float)IsoCamera.frameState.OffscreenHeight);
         }
      } else if (var1 + (float)(32 * Core.TileScale) <= 0.0F) {
         return false;
      } else if (var2 + (float)(32 * Core.TileScale) <= 0.0F) {
         return false;
      } else if (var1 - (float)(32 * Core.TileScale) >= (float)IsoCamera.frameState.OffscreenWidth) {
         return false;
      } else {
         return !(var2 - (float)(96 * Core.TileScale) >= (float)IsoCamera.frameState.OffscreenHeight);
      }
   }

   void cacheLightInfo() {
      int var1 = IsoCamera.frameState.playerIndex;
      this.lightInfo[var1] = this.lighting[var1].lightInfo();
   }

   public void setLightInfoServerGUIOnly(ColorInfo var1) {
      this.lightInfo[0] = var1;
   }

   int renderFloor(int var1, int var2) {
      int var3 = IsoCamera.frameState.playerIndex;
      ColorInfo var4 = this.lightInfo[var3];
      boolean var5 = IsoCamera.frameState.CamCharacter.getVehicle() != null;
      int var6 = 0;

      try {
         int var7 = this.Objects.size();
         IsoObject[] var8 = (IsoObject[])this.Objects.getElements();

         int var9;
         for(var9 = 0; var9 < var7; ++var9) {
            IsoObject var10 = var8[var9];
            if (GameClient.bClient && IsoPlayer.players[var3] != null && IsoPlayer.players[var3].isSeeNonPvpZone() && (var10.highlightFlags & 1) == 0) {
               var10.setHighlighted(true);
               if (NonPvpZone.getNonPvpZone(this.x, this.y) != null) {
                  var10.setHighlightColor(0.6F, 0.6F, 1.0F, 0.5F);
               } else {
                  var10.setHighlightColor(1.0F, 0.6F, 0.6F, 0.5F);
               }
            }

            if (Core.bDebug && GameClient.bClient && SafeHouse.isSafeHouse(this, (String)null, true) != null) {
               var10.setHighlighted(true);
               var10.setHighlightColor(1.0F, 0.0F, 0.0F, 1.0F);
            }

            boolean var11 = true;
            if (var10.sprite != null && !var10.sprite.solidfloor) {
               var11 = false;
               var6 |= 4;
            }

            if (var10 instanceof IsoFire) {
               var11 = false;
               var6 |= 4;
            }

            if (var11) {
               IndieGL.glAlphaFunc(516, 0.0F);
               var10.alpha[var3] = 1.0F;
               var10.targetAlpha[var3] = 1.0F;
               var10.render((float)this.x, (float)this.y, (float)this.z, PerformanceSettings.LightingFrameSkip < 3 ? defColorInfo : var4, true);
               var6 |= 1;
               if ((var10.highlightFlags & 1) == 0) {
                  var6 |= 8;
               }

               if ((var10.highlightFlags & 2) != 0) {
                  var10.highlightFlags &= -4;
               }
            }

            if (!var11 && var10.sprite != null && (var5 || !var10.sprite.isBush) && (var10.sprite.canBeRemoved || var10.sprite.attachedFloor)) {
               var6 |= 2;
            }
         }

         if ((this.getCell().rainIntensity > 0 || RainManager.isRaining() && RainManager.RainIntensity > 0.0F) && this.isExteriorCache && !this.isVegitationCache && this.isSolidFloorCache && this.isCouldSee(var3)) {
            if (!IsoCamera.frameState.Paused) {
               var9 = this.getCell().rainIntensity == 0 ? (int)Math.min(Math.floor((double)(RainManager.RainIntensity / 0.2F)) + 1.0D, 5.0D) : this.getCell().rainIntensity;
               if (this.splashFrame < 0.0F && Rand.Next(Rand.AdjustForFramerate((int)(5.0F / (float)var9) * 100)) == 0) {
                  this.splashFrame = 0.0F;
               }
            }

            if (this.splashFrame >= 0.0F) {
               var9 = (int)(this.splashFrame * 4.0F);
               Texture var16 = Texture.getSharedTexture("RainSplash_00_" + var9);
               if (var16 != null) {
                  float var17 = IsoUtils.XToScreen((float)this.x + this.splashX, (float)this.y + this.splashY, (float)this.z, 0) - IsoCamera.frameState.OffX;
                  float var12 = IsoUtils.YToScreen((float)this.x + this.splashX, (float)this.y + this.splashY, (float)this.z, 0) - IsoCamera.frameState.OffY;
                  var17 -= (float)(var16.getWidth() / 2 * Core.TileScale);
                  var12 -= (float)(var16.getHeight() / 2 * Core.TileScale);
                  float var13 = 0.6F * (this.getCell().rainIntensity > 0 ? 1.0F : RainManager.RainIntensity);
                  float var14 = Core.getInstance().RenderShader != null ? 0.6F : 1.0F;
                  SpriteRenderer.instance.render(var16, var17, var12, (float)(var16.getWidth() * Core.TileScale), (float)(var16.getHeight() * Core.TileScale), 0.8F * var4.r, 0.9F * var4.g, 1.0F * var4.b, var13 * var14);
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
      } catch (Exception var15) {
         IndieGL.End();
         ExceptionLogger.logException(var15);
      }

      return var6;
   }

   private boolean isSpriteOnSouthOrEastWall(IsoObject var1) {
      if (var1 instanceof IsoBarricade) {
         return var1.getDir() == IsoDirections.S || var1.getDir() == IsoDirections.E;
      } else if (!(var1 instanceof IsoCurtain)) {
         return false;
      } else {
         IsoCurtain var2 = (IsoCurtain)var1;
         return var2.getType() == IsoObjectType.curtainS || var2.getType() == IsoObjectType.curtainE;
      }
   }

   public boolean RenderMinusFloorFxMask(int var1, int var2, int var3, int var4, boolean var5, boolean var6, boolean var7) {
      boolean var8 = false;
      int var9 = this.Objects.size();
      IsoObject[] var10 = (IsoObject[])this.Objects.getElements();
      boolean var11 = IsoCamera.frameState.CamCharacter.getVehicle() != null;

      try {
         int var12 = var5 ? var9 - 1 : 0;
         int var13 = var5 ? 0 : var9 - 1;
         int var14 = var12;

         while(true) {
            if (var5) {
               if (var14 < var13) {
                  break;
               }
            } else if (var14 > var13) {
               break;
            }

            IsoObject var15 = var10[var14];
            boolean var16 = true;
            IsoObjectType var17 = IsoObjectType.MAX;
            if (var15.sprite != null) {
               var17 = var15.sprite.getType();
            }

            if (var15.sprite != null && var15.sprite.solidfloor) {
               var16 = false;
            }

            if (this.z >= var3 && (var15.sprite == null || !var15.sprite.alwaysDraw)) {
               var16 = false;
            }

            if ((!var6 || var15.sprite == null || !var11 && var15.sprite.isBush || var15.sprite.canBeRemoved || var15.sprite.attachedFloor) && (var6 || var15.sprite == null || !var11 && var15.sprite.isBush || !var15.sprite.canBeRemoved && !var15.sprite.attachedFloor)) {
               if (var15.sprite != null && (var17 == IsoObjectType.WestRoofB || var17 == IsoObjectType.WestRoofM || var17 == IsoObjectType.WestRoofT) && this.z == var3 - 1 && this.z == (int)IsoCamera.CamCharacter.getZ()) {
                  var16 = false;
               }

               if (this.isSpriteOnSouthOrEastWall(var15)) {
                  if (!var5) {
                     var16 = false;
                  }

                  var8 = true;
               } else if (var5) {
                  var16 = false;
               }

               if (var16) {
                  var15.renderFxMask((float)this.x, (float)this.y, (float)this.z, false);
               }
            }

            var14 += var5 ? -1 : 1;
         }
      } catch (Exception var18) {
         IndieGL.End();
         ExceptionLogger.logException(var18);
      }

      return var8;
   }

   boolean renderMinusFloor(int var1, int var2, int var3, int var4, boolean var5, boolean var6) {
      IndieGL.glBlendFunc(770, 771);
      int var7 = 0;
      isOnScreenLast = this.IsOnScreen();
      int var8 = IsoCamera.frameState.playerIndex;
      IsoGridSquare var9 = IsoCamera.frameState.CamCharacterSquare;
      IsoRoom var10 = IsoCamera.frameState.CamCharacterRoom;
      ColorInfo var11 = this.lightInfo[var8];
      boolean var12 = this.lighting[var8].bCouldSee();
      float var13 = this.lighting[var8].darkMulti();
      boolean var14 = IsoCamera.frameState.CamCharacter.getVehicle() != null;
      var11.a = 1.0F;
      defColorInfo.r = 1.0F;
      defColorInfo.g = 1.0F;
      defColorInfo.b = 1.0F;
      defColorInfo.a = 1.0F;
      int var15 = (int)(this.CachedScreenX - IsoCamera.frameState.OffX);
      int var16 = (int)(this.CachedScreenY - IsoCamera.frameState.OffY);
      boolean var17 = true;
      IsoCell var18 = this.getCell();
      if (var15 + 32 * Core.TileScale <= var18.StencilX1 || var15 - 32 * Core.TileScale >= var18.StencilX2 || var16 + 32 * Core.TileScale <= var18.StencilY1 || var16 - 96 * Core.TileScale >= var18.StencilY2) {
         var17 = false;
      }

      boolean var19 = false;
      int var20 = this.Objects.size();
      IsoObject[] var21 = (IsoObject[])this.Objects.getElements();

      try {
         int var22 = var5 ? var20 - 1 : 0;
         int var23 = var5 ? 0 : var20 - 1;
         int var24 = var22;

         while(true) {
            if (var5) {
               if (var24 < var23) {
                  break;
               }
            } else if (var24 > var23) {
               break;
            }

            IsoObject var25 = var21[var24];
            boolean var26 = true;
            IsoObjectType var27 = IsoObjectType.MAX;
            if (var25.sprite != null) {
               var27 = var25.sprite.getType();
            }

            CircleStencil = false;
            if (var25.sprite != null && var25.sprite.solidfloor) {
               var26 = false;
            }

            if (var25 instanceof IsoFire) {
               var26 = !var6;
            }

            if (this.z >= var3 && (var25.sprite == null || !var25.sprite.alwaysDraw)) {
               var26 = false;
            }

            if (this.z == var3 - 1 && var25.sprite != null) {
            }

            if ((!var6 || var25.sprite == null || !var14 && var25.sprite.isBush || var25.sprite.canBeRemoved || var25.sprite.attachedFloor || var25 instanceof IsoWorldInventoryObject) && (var6 || var25.sprite == null || !var14 && var25.sprite.isBush || !var25.sprite.canBeRemoved && !var25.sprite.attachedFloor && !(var25 instanceof IsoWorldInventoryObject))) {
               if (var25.sprite != null && (var27 == IsoObjectType.WestRoofB || var27 == IsoObjectType.WestRoofM || var27 == IsoObjectType.WestRoofT) && this.z == var3 - 1 && this.z == (int)IsoCamera.CamCharacter.getZ()) {
                  var26 = false;
               }

               if (var25.sprite != null && !var25.sprite.solidfloor && IsoPlayer.instance.isClimbing()) {
                  var26 = true;
               }

               if (this.isSpriteOnSouthOrEastWall(var25)) {
                  if (!var5) {
                     var26 = false;
                  }

                  var19 = true;
               } else if (var5) {
                  var26 = false;
               }

               if (var26) {
                  IndieGL.glAlphaFunc(516, 0.0F);
                  IsoGridSquare var33;
                  if (var25.sprite != null && (var27 == IsoObjectType.doorFrW || var27 == IsoObjectType.doorFrN || var27 == IsoObjectType.doorW || var27 == IsoObjectType.doorN || var25.sprite.cutW || var25.sprite.cutN) && PerformanceSettings.LightingFrameSkip < 3) {
                     if (var25.targetAlpha[var8] < 1.0F) {
                        boolean var32 = PerformanceSettings.NewRoofHiding && !IsoWorld.instance.CurrentCell.CanBuildingSquareOccludePlayer(this, var8);
                        if (var32) {
                           if (var25.sprite.cutW && this.getProperties().Is(IsoFlagType.WallSE)) {
                              var33 = this.nav[IsoDirections.NW.index()];
                              if (var33 == null || var33.getRoom() == null) {
                                 var32 = false;
                              }
                           } else if (var27 != IsoObjectType.doorFrW && var27 != IsoObjectType.doorW && !var25.sprite.cutW) {
                              if (var27 == IsoObjectType.doorFrN || var27 == IsoObjectType.doorN || var25.sprite.cutN) {
                                 var33 = this.nav[IsoDirections.N.index()];
                                 if (var33 == null || var33.getRoom() == null) {
                                    var32 = false;
                                 }
                              }
                           } else {
                              var33 = this.nav[IsoDirections.W.index()];
                              if (var33 == null || var33.getRoom() == null) {
                                 var32 = false;
                              }
                           }
                        }

                        if (!var32) {
                           CircleStencil = var17;
                        }

                        var25.targetAlpha[var8] = 1.0F;
                        var25.alpha[var8] = 1.0F;
                     }

                     if (var25.sprite.cutW && var25.sprite.cutN) {
                        var7 = this.DoWallLightingNW(var2, var1, var25, var7, CircleStencil, var4);
                     } else if (var25.sprite.getType() != IsoObjectType.doorFrW && var27 != IsoObjectType.doorW && !var25.sprite.cutW) {
                        if (var27 == IsoObjectType.doorFrN || var27 == IsoObjectType.doorN || var25.sprite.cutN) {
                           var7 = this.DoWallLightingN(var25, var7, var2, var1, var4);
                        }
                     } else {
                        var7 = this.DoWallLightingW(var25, var7, var2, var1, var4);
                     }
                  } else if (var25.sprite != null && (var27 == IsoObjectType.doorFrW || var27 == IsoObjectType.doorFrN || var27 == IsoObjectType.doorW || var27 == IsoObjectType.doorN || var25.sprite.cutW || var25.sprite.cutN) && PerformanceSettings.LightingFrameSkip == 3) {
                     if (this.z != (int)IsoCamera.frameState.CamCharacterZ || var27 == IsoObjectType.doorFrW || var27 == IsoObjectType.doorFrN || var25 instanceof IsoWindow) {
                        var17 = false;
                     }

                     if (var25.targetAlpha[var8] < 1.0F) {
                        var25.targetAlpha[var8] = var17 ? var25.targetAlpha[var8] : 1.0F;
                        var25.alpha[var8] = var25.targetAlpha[var8];
                        IsoObject.LowLightingQualityHack = false;
                        var25.render((float)this.x, (float)this.y, (float)this.z, var11, true);
                        if (!IsoObject.LowLightingQualityHack) {
                           var25.targetAlpha[var8] = 1.0F;
                        }
                     } else {
                        var25.render((float)this.x, (float)this.y, (float)this.z, var11, true);
                     }
                  } else {
                     if (var9 != null && !var12 && this.room != var10 && var13 < 0.5F) {
                        var25.targetAlpha[var8] = var13 * 2.0F;
                     } else {
                        var25.targetAlpha[var8] = 1.0F;
                        if (IsoPlayer.instance != null && var25.getProperties() != null && (var25.getProperties().Is(IsoFlagType.solid) || var25.getProperties().Is(IsoFlagType.solidtrans))) {
                           int var28 = this.getX() - (int)IsoPlayer.instance.getX();
                           int var29 = this.getY() - (int)IsoPlayer.instance.getY();
                           if (var28 > 0 && var28 < 3 && var29 >= 0 && var29 < 3 || var29 > 0 && var29 < 3 && var28 >= 0 && var28 < 3) {
                              var25.targetAlpha[var8] = 0.99F;
                           }
                        }
                     }

                     if (var25 instanceof IsoWindow && var25.targetAlpha[var8] < 1.0E-4F) {
                        IsoWindow var31 = (IsoWindow)var25;
                        var33 = var31.getOppositeSquare();
                        if (var33 != null && var33 != this && var33.lighting[var8].bSeen()) {
                           var25.targetAlpha[var8] = var33.lighting[var8].darkMulti() * 2.0F;
                        }
                     }

                     if (var25 instanceof IsoTree) {
                        if (var17 && this.x >= (int)IsoCamera.frameState.CamCharacterX && this.y >= (int)IsoCamera.frameState.CamCharacterY && var9 != null && var9.Is(IsoFlagType.exterior)) {
                           ((IsoTree)var25).bRenderFlag = true;
                        } else {
                           ((IsoTree)var25).bRenderFlag = false;
                        }
                     }

                     var25.render((float)this.x, (float)this.y, (float)this.z, var11, true);
                  }

                  if ((var25.highlightFlags & 2) != 0) {
                     var25.highlightFlags &= -4;
                  }
               }
            }

            var24 += var5 ? -1 : 1;
         }
      } catch (Exception var30) {
         IndieGL.End();
         ExceptionLogger.logException(var30);
      }

      return var19;
   }

   void RereouteWallMaskTo(IsoObject var1) {
      for(int var2 = 0; var2 < this.Objects.size(); ++var2) {
         IsoObject var3 = (IsoObject)this.Objects.get(var2);
         if (var3.sprite.getProperties().Is(IsoFlagType.collideW) || var3.sprite.getProperties().Is(IsoFlagType.collideN)) {
            var3.rerouteMask = var1;
         }
      }

   }

   void setBlockedGridPointers(IsoGridSquare.GetSquare var1) {
      this.w = var1.getGridSquare(this.x - 1, this.y, this.z);
      this.e = var1.getGridSquare(this.x + 1, this.y, this.z);
      this.s = var1.getGridSquare(this.x, this.y + 1, this.z);
      this.n = var1.getGridSquare(this.x, this.y - 1, this.z);
      this.ne = var1.getGridSquare(this.x + 1, this.y - 1, this.z);
      this.nw = var1.getGridSquare(this.x - 1, this.y - 1, this.z);
      this.se = var1.getGridSquare(this.x + 1, this.y + 1, this.z);
      this.sw = var1.getGridSquare(this.x - 1, this.y + 1, this.z);
      if (this.s != null && this.testPathFindAdjacent((IsoMovingObject)null, this.s.x - this.x, this.s.y - this.y, this.s.z - this.z, var1)) {
         this.s = null;
      }

      if (this.w != null && this.testPathFindAdjacent((IsoMovingObject)null, this.w.x - this.x, this.w.y - this.y, this.w.z - this.z, var1)) {
         this.w = null;
      }

      if (this.n != null && this.testPathFindAdjacent((IsoMovingObject)null, this.n.x - this.x, this.n.y - this.y, this.n.z - this.z, var1)) {
         this.n = null;
      }

      if (this.e != null && this.testPathFindAdjacent((IsoMovingObject)null, this.e.x - this.x, this.e.y - this.y, this.e.z - this.z, var1)) {
         this.e = null;
      }

      if (this.sw != null && this.testPathFindAdjacent((IsoMovingObject)null, this.sw.x - this.x, this.sw.y - this.y, this.sw.z - this.z, var1)) {
         this.sw = null;
      }

      if (this.se != null && this.testPathFindAdjacent((IsoMovingObject)null, this.se.x - this.x, this.se.y - this.y, this.se.z - this.z, var1)) {
         this.se = null;
      }

      if (this.nw != null && this.testPathFindAdjacent((IsoMovingObject)null, this.nw.x - this.x, this.nw.y - this.y, this.nw.z - this.z, var1)) {
         this.nw = null;
      }

      if (this.ne != null && this.testPathFindAdjacent((IsoMovingObject)null, this.ne.x - this.x, this.ne.y - this.y, this.ne.z - this.z, var1)) {
         this.ne = null;
      }

   }

   BlockInfo testAdjacentRoomTransition(int var1, int var2, int var3) {
      blockInfo.ThroughDoor = false;
      blockInfo.ThroughWindow = false;
      blockInfo.ThroughStairs = false;
      if (var1 >= -1 && var1 <= 1 && var2 >= -1 && var2 <= 1 && var3 >= -1 && var3 <= 1) {
         int var4 = this.x;
         int var5 = this.y;
         int var6 = this.z;
         var1 += this.x;
         var2 += this.y;
         var3 += this.z;
         IsoGridSquare var7 = this.getCell().getGridSquare(var4, var5, var6);
         IsoGridSquare var8 = this.getCell().getGridSquare(var1, var2, var3);
         if (var8 == null) {
            return blockInfo;
         } else {
            if (var4 < var1) {
               if (var8.Has(IsoObjectType.doorFrW) || var8.Properties.Is(IsoFlagType.doorW)) {
                  blockInfo.ThroughDoor = true;
               }

               if (var8.Properties.Is(IsoFlagType.windowW)) {
                  blockInfo.ThroughWindow = true;
               }
            }

            if (var4 > var1) {
               if (var7.Has(IsoObjectType.doorFrW) || var7.Properties.Is(IsoFlagType.doorW)) {
                  blockInfo.ThroughDoor = true;
               }

               if (var7.Properties.Is(IsoFlagType.windowW)) {
                  blockInfo.ThroughWindow = true;
               }
            }

            if (var5 < var2) {
               if (var8.Has(IsoObjectType.doorFrN) || var8.Properties.Is(IsoFlagType.doorN)) {
                  blockInfo.ThroughDoor = true;
               }

               if (var8.Properties.Is(IsoFlagType.windowN)) {
                  blockInfo.ThroughWindow = true;
               }
            }

            if (var5 > var2) {
               if (var7.Has(IsoObjectType.doorFrN) || var7.Properties.Is(IsoFlagType.doorN)) {
                  blockInfo.ThroughDoor = true;
               }

               if (var7.Properties.Is(IsoFlagType.windowN)) {
                  blockInfo.ThroughWindow = true;
               }
            }

            if (var6 > var3 && var8.Has(IsoObjectType.stairsTN)) {
               blockInfo.ThroughStairs = true;
            }

            if (var6 < var3 && var7.Has(IsoObjectType.stairsTN)) {
               blockInfo.ThroughStairs = true;
            }

            return blockInfo;
         }
      } else {
         return blockInfo;
      }
   }

   public void CalcLightInfo(int var1) {
      IsoChunk var2 = this.getChunk();
      if (var2 != null) {
         IsoGridSquare.ILighting var3 = this.lighting[var1];
         IsoPlayer var4 = IsoPlayer.players[var1];
         POVCharacters.clear();
         POVCharacters.add(var4);
         float var5 = 0.0F;
         float var6 = darkStep;
         float var7 = 1.0E9F;
         int var8 = POVCharacters.size();

         for(int var9 = 0; var9 < var8; ++var9) {
            IsoGameCharacter var10 = (IsoGameCharacter)POVCharacters.get(var9);
            IsoGameCharacter.LightInfo var11 = var10.getLightInfo2();
            float var12 = IsoUtils.DistanceManhatten(var11.x, var11.y, (float)this.x, (float)this.y, var11.z, (float)this.z);
            if (this == var11.square) {
               var12 = 0.0F;
            }

            if (IsoPlayer.DemoMode) {
            }

            float var13 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex());
            if (this.getRoom() != null) {
               var13 *= 0.5F;
            }

            float var14 = var13;
            if (var10.HasTrait("ShortSighted")) {
               var14 = var13 * 1.5F;
            }

            if (var12 > GameTime.getInstance().getViewDistMax()) {
               var12 = GameTime.getInstance().getViewDistMax();
            }

            var12 = 1.0F - var14 / GameTime.getInstance().getViewDist();
            if (var12 < 0.0F) {
               var12 = 0.0F;
            }

            if (var12 > 1.0F) {
               var12 = 1.0F;
            }

            var14 *= var12;
            if (var12 < var7) {
               var7 = var12;
            }

            if (var14 < var13) {
               var14 = var13;
            }

            if (IsoPlayer.getInstance() != null) {
               if (var3.darkMulti() < var3.targetDarkMulti()) {
                  var6 *= 2.0F;
                  if (var4.getVehicle() != null) {
                     var6 = darkStep * 20.0F;
                  }
               } else if (var3.darkMulti() > var3.targetDarkMulti()) {
                  var6 *= 1.5F;
                  if (var11.time - torchTimer < 2000L) {
                     var6 *= 10.0F;
                  }

                  if (this.room == null) {
                     var6 /= 4.0F;
                  }

                  if (var4.getVehicle() != null) {
                     var6 = darkStep * 20.0F;
                  }
               }

               var6 *= (float)(PerformanceSettings.LightingFrameSkip + 1);
               var6 *= GameTime.instance.FPSMultiplier;
            }

            if (var4.HasTrait("ShortSighted")) {
               var6 *= 0.7F;
            }

            var6 *= 0.8F;
            var5 += var14;
         }

         var5 /= (float)POVCharacters.size();
         if (var5 > 1.0F) {
            var5 = 1.0F;
         }

         if (var3.darkMulti() < var3.targetDarkMulti()) {
            var3.darkMulti(var3.darkMulti() + var6);
            if (var3.darkMulti() > var3.targetDarkMulti()) {
               var3.darkMulti(var3.targetDarkMulti());
            }
         } else if (var3.darkMulti() > var3.targetDarkMulti()) {
            var3.darkMulti(var3.darkMulti() - var6);
            if (var3.darkMulti() < var3.targetDarkMulti()) {
               var3.darkMulti(var3.targetDarkMulti());
            }
         }

         float var15 = var3.darkMulti();
         if (var7 < 576.0F) {
            int var16 = 1;
            if (this.w != null && this.w.room == this.room && this.w.lighting[var1].darkMulti() > var3.darkMulti()) {
               if (this.w.lighting[var1].bSeen()) {
                  var15 += this.w.lighting[var1].darkMulti();
               }

               ++var16;
            }

            if (this.n != null && this.n.room == this.room && this.n.lighting[var1].darkMulti() > var3.darkMulti()) {
               if (this.n.lighting[var1].bSeen()) {
                  var15 += this.n.lighting[var1].darkMulti();
               }

               ++var16;
            }

            if (this.e != null && this.e.room == this.room && this.e.lighting[var1].darkMulti() > var3.darkMulti()) {
               if (this.e.lighting[var1].bSeen()) {
                  var15 += this.e.lighting[var1].darkMulti();
               }

               ++var16;
            }

            if (this.s != null && this.s.room == this.room && this.s.lighting[var1].darkMulti() > var3.darkMulti()) {
               if (this.s.lighting[var1].bSeen()) {
                  var15 += this.s.lighting[var1].darkMulti();
               }

               ++var16;
            }

            var15 /= (float)var16;
         }

         ColorInfo var17 = var3.lightInfo();
         var17.r = var15;
         var17.g = var15;
         var17.b = var15;
         if (rmodLT < 0.0F) {
            rmodLT = 0.0F;
         }

         if (gmodLT < 0.0F) {
            gmodLT = 0.0F;
         }

         if (bmodLT < 0.0F) {
            bmodLT = 0.0F;
         }

         var17.r *= var5 * rmodLT;
         var17.g *= var5 * gmodLT;
         var17.b *= var5 * bmodLT;
         if (var3.lampostTotalR() > 0.0F || var3.lampostTotalG() > 0.0F || var3.lampostTotalB() > 0.0F) {
            var17.r += var3.lampostTotalR() * var15;
            var17.g += var3.lampostTotalG() * var15;
            var17.b += var3.lampostTotalB() * var15;
         }

         if (var17.r > 1.0F) {
            var17.r = 1.0F;
         }

         if (var17.g > 1.0F) {
            var17.g = 1.0F;
         }

         if (var17.b > 1.0F) {
            var17.b = 1.0F;
         }

         if (var3.bSeen()) {
            IsoFireManager.LightTileWithFire(this);
         }

      }
   }

   public IsoObject getContainerItem(String var1) {
      int var2 = this.getObjects().size();
      IsoObject[] var3 = (IsoObject[])this.getObjects().getElements();

      for(int var4 = 0; var4 < var2; ++var4) {
         IsoObject var5 = var3[var4];
         if (var5.getContainer() != null && var1.equals(var5.getContainer().getType())) {
            return var5;
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

   public void CalcVisibility(int var1) {
      IsoPlayer var2 = IsoPlayer.players[var1];
      IsoGridSquare.ILighting var3 = this.lighting[var1];
      var3.bCanSee(false);
      var3.bCouldSee(false);
      if (GameServer.bServer || var2 != null && (!var2.isDead() || var2.ReanimatedCorpse != null)) {
         if (var2 != null) {
            IsoGameCharacter.LightInfo var4 = var2.getLightInfo2();
            IsoGridSquare var5 = var4.square;
            if (var5 != null) {
               IsoChunk var6 = this.getChunk();
               if (var6 != null) {
                  tempo.x = (float)this.x + 0.5F;
                  tempo.y = (float)this.y + 0.5F;
                  tempo2.x = var4.x;
                  tempo2.y = var4.y;
                  Vector2 var10000 = tempo2;
                  var10000.x -= tempo.x;
                  var10000 = tempo2;
                  var10000.y -= tempo.y;
                  Vector2 var7 = tempo;
                  float var8 = tempo2.getLength();
                  tempo2.normalize();
                  if (var2 instanceof IsoSurvivor) {
                     var2.angle.x = var7.x;
                     var2.angle.y = var7.y;
                     var4.angleX = var2.angle.x;
                     var4.angleY = var2.angle.y;
                  }

                  var7.x = var4.angleX;
                  var7.y = var4.angleY;
                  var7.normalize();
                  float var9 = tempo2.dot(var7);
                  if (var5 == this) {
                     var9 = -1.0F;
                  }

                  float var11;
                  if (!GameServer.bServer) {
                     float var10 = var2.getStats().fatigue - 0.6F;
                     if (var10 < 0.0F) {
                        var10 = 0.0F;
                     }

                     var10 *= 2.5F;
                     if (var2.HasTrait("HardOfHearing") && var10 < 0.7F) {
                        var10 = 0.7F;
                     }

                     var11 = 2.0F;
                     if (var2.HasTrait("KeenHearing")) {
                        var11 += 3.0F;
                     }

                     if (var8 < var11 * (1.0F - var10) && !var2.HasTrait("Deaf")) {
                        var9 = -1.0F;
                     }
                  }

                  LosUtil.TestResults var16 = LosUtil.lineClearCached(this.getCell(), this.x, this.y, this.z, (int)var4.x, (int)var4.y, (int)var4.z, false, var1);
                  var11 = -0.2F;
                  var11 -= var2.getStats().fatigue - 0.6F;
                  if (var11 > -0.2F) {
                     var11 = -0.2F;
                  }

                  if (var2.getStats().fatigue >= 1.0F) {
                     var11 -= 0.2F;
                  }

                  if (var2.getMoodles().getMoodleLevel(MoodleType.Panic) == 4) {
                     var11 -= 0.2F;
                  }

                  if (var11 < -0.9F) {
                     var11 = -0.9F;
                  }

                  if (var2.HasTrait("EagleEyed")) {
                     var11 += 0.2F;
                  }

                  if (var2 instanceof IsoPlayer && var2.getVehicle() != null) {
                     var11 = 1.0F;
                  }

                  if (!(var9 > var11) && var16 != LosUtil.TestResults.Blocked) {
                     var3.bCouldSee(true);
                     if (this.room != null && this.room.def != null && !this.room.def.bExplored) {
                        byte var17 = 10;
                        if (var4.square != null && var4.square.getBuilding() == this.room.building) {
                           var17 = 50;
                        }

                        if ((!GameServer.bServer || !(var2 instanceof IsoPlayer) || !((IsoPlayer)var2).GhostMode) && IsoUtils.DistanceManhatten(var4.x, var4.y, (float)this.x, (float)this.y) < (float)var17 && this.z == (int)var4.z) {
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

                     var3.bCanSee(true);
                     var3.bSeen(true);
                     var3.targetDarkMulti(1.0F);
                  } else {
                     if (var16 == LosUtil.TestResults.Blocked) {
                        var3.bCouldSee(false);
                     } else {
                        var3.bCouldSee(true);
                     }

                     if (!GameServer.bServer) {
                        if (var3.bSeen()) {
                           float var12 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex());
                           if (!var3.bCouldSee()) {
                              var12 *= 0.5F;
                           } else {
                              var12 *= 0.94F;
                           }

                           if (this.room == null && var5.getRoom() == null) {
                              var3.targetDarkMulti(var12);
                           } else if (this.room != null && var5.getRoom() != null && this.room.building == var5.getRoom().building) {
                              if (this.room != var5.getRoom() && !var3.bCouldSee()) {
                                 var3.targetDarkMulti(0.0F);
                              } else {
                                 var3.targetDarkMulti(var12);
                              }
                           } else if (this.room == null) {
                              var3.targetDarkMulti(var12 / 2.0F);
                           } else if (var3.lampostTotalR() + var3.lampostTotalG() + var3.lampostTotalB() == 0.0F) {
                              var3.targetDarkMulti(0.0F);
                           }

                           if (this.room != null) {
                              var3.targetDarkMulti(var3.targetDarkMulti() * 0.7F);
                           }
                        } else {
                           var3.targetDarkMulti(0.0F);
                           var3.darkMulti(0.0F);
                        }
                     }
                  }

                  if (var9 > var11) {
                     var3.targetDarkMulti(var3.targetDarkMulti() * 0.85F);
                  }

                  if (!GameServer.bServer) {
                     for(int var18 = 0; var18 < var4.torches.size(); ++var18) {
                        IsoGameCharacter.TorchInfo var13 = (IsoGameCharacter.TorchInfo)var4.torches.get(var18);
                        tempo2.x = var13.x;
                        tempo2.y = var13.y;
                        var10000 = tempo2;
                        var10000.x -= (float)this.x + 0.5F;
                        var10000 = tempo2;
                        var10000.y -= (float)this.y + 0.5F;
                        var8 = tempo2.getLength();
                        tempo2.normalize();
                        var7.x = var13.angleX;
                        var7.y = var13.angleY;
                        var7.normalize();
                        var9 = tempo2.dot(var7);
                        if ((int)var13.x == this.getX() && (int)var13.y == this.getY() && (int)var13.z == this.getZ()) {
                           var9 = -1.0F;
                        }

                        boolean var14 = false;
                        if (IsoUtils.DistanceManhatten((float)this.getX(), (float)this.getY(), var13.x, var13.y) < var13.dist && (var13.bCone && var9 < -var13.dot || var9 == -1.0F || !var13.bCone && var9 < 0.8F)) {
                           var14 = true;
                        }

                        if ((var13.bCone && var8 < var13.dist || !var13.bCone && var8 < var13.dist) && var3.bCanSee() && var14 && this.z == (int)var2.getZ()) {
                           float var15 = var8 / var13.dist;
                           if (var15 > 1.0F) {
                              var15 = 1.0F;
                           }

                           if (var15 < 0.0F) {
                              var15 = 0.0F;
                           }

                           var3.targetDarkMulti(var3.targetDarkMulti() + var13.strength * (1.0F - var15) * 3.0F);
                           if (var3.targetDarkMulti() > 2.5F) {
                              var3.targetDarkMulti(2.5F);
                           }

                           torchTimer = var4.time;
                        }
                     }

                  }
               }
            }
         }
      } else {
         var3.bSeen(true);
         var3.bCanSee(true);
         var3.bCouldSee(true);
      }
   }

   private LosUtil.TestResults DoDiagnalCheck(int var1, int var2, int var3, boolean var4) {
      LosUtil.TestResults var5 = this.testVisionAdjacent(var1, 0, var3, false, var4);
      if (var5 == LosUtil.TestResults.Blocked) {
         return LosUtil.TestResults.Blocked;
      } else {
         LosUtil.TestResults var6 = this.testVisionAdjacent(0, var2, var3, false, var4);
         if (var6 == LosUtil.TestResults.Blocked) {
            return LosUtil.TestResults.Blocked;
         } else {
            return var5 != LosUtil.TestResults.ClearThroughWindow && var6 != LosUtil.TestResults.ClearThroughWindow ? this.testVisionAdjacent(var1, var2, var3, false, var4) : LosUtil.TestResults.ClearThroughWindow;
         }
      }
   }

   boolean HasNoCharacters() {
      int var1;
      for(var1 = 0; var1 < this.MovingObjects.size(); ++var1) {
         if (this.MovingObjects.get(var1) instanceof IsoGameCharacter) {
            return false;
         }
      }

      for(var1 = 0; var1 < this.SpecialObjects.size(); ++var1) {
         if (this.SpecialObjects.get(var1) instanceof IsoBarricade) {
            return false;
         }
      }

      return true;
   }

   public static int getFireRecalc() {
      return FireRecalc;
   }

   public static void setFireRecalc(int var0) {
      FireRecalc = var0;
   }

   public static float getDarkStep() {
      return darkStep;
   }

   public static void setDarkStep(float var0) {
      darkStep = var0;
   }

   public static BlockInfo getBlockInfo() {
      return blockInfo;
   }

   public static void setBlockInfo(BlockInfo var0) {
      blockInfo = var0;
   }

   public static int getRecalcLightTime() {
      return RecalcLightTime;
   }

   public static void setRecalcLightTime(int var0) {
      RecalcLightTime = var0;
   }

   public static int getLightcache() {
      return lightcache;
   }

   public static void setLightcache(int var0) {
      lightcache = var0;
   }

   public boolean isCouldSee(int var1) {
      return this.lighting[var1].bCouldSee();
   }

   public void setCouldSee(int var1, boolean var2) {
      this.lighting[var1].bCouldSee(var2);
   }

   public boolean isCanSee(int var1) {
      return this.lighting[var1].bCanSee();
   }

   public void setCanSee(int var1, boolean var2) {
      this.lighting[var1].bCanSee(var2);
   }

   public IsoCell getCell() {
      return IsoWorld.instance.CurrentCell;
   }

   public IsoGridSquare getE() {
      return this.e;
   }

   public void setE(IsoGridSquare var1) {
      this.e = var1;
   }

   public ArrayList getLightInfluenceB() {
      return this.LightInfluenceB;
   }

   public void setLightInfluenceB(ArrayList var1) {
      this.LightInfluenceB = var1;
   }

   public ArrayList getLightInfluenceG() {
      return this.LightInfluenceG;
   }

   public void setLightInfluenceG(ArrayList var1) {
      this.LightInfluenceG = var1;
   }

   public ArrayList getLightInfluenceR() {
      return this.LightInfluenceR;
   }

   public void setLightInfluenceR(ArrayList var1) {
      this.LightInfluenceR = var1;
   }

   public ArrayList getStaticMovingObjects() {
      return this.StaticMovingObjects;
   }

   public void setStaticMovingObjects(ArrayList var1) {
      this.StaticMovingObjects = var1;
   }

   public ArrayList getMovingObjects() {
      return this.MovingObjects;
   }

   public void setMovingObjects(ArrayList var1) {
      this.MovingObjects = var1;
   }

   public IsoGridSquare getN() {
      return this.n;
   }

   public void setN(IsoGridSquare var1) {
      this.n = var1;
   }

   public PZArrayList getObjects() {
      return this.Objects;
   }

   public void setObjects(PZArrayList var1) {
      this.Objects = var1;
   }

   public PropertyContainer getProperties() {
      return this.Properties;
   }

   public void setProperties(PropertyContainer var1) {
      this.Properties = var1;
   }

   public IsoRoom getRoom() {
      return this.roomID == -1 ? null : this.room;
   }

   public void setRoom(IsoRoom var1) {
      this.room = var1;
   }

   public IsoBuilding getBuilding() {
      IsoRoom var1 = this.getRoom();
      return var1 != null ? var1.getBuilding() : null;
   }

   public IsoGridSquare getS() {
      return this.s;
   }

   public void setS(IsoGridSquare var1) {
      this.s = var1;
   }

   public ArrayList getSpecialObjects() {
      return this.SpecialObjects;
   }

   public void setSpecialObjects(ArrayList var1) {
      this.SpecialObjects = var1;
   }

   public IsoGridSquare getW() {
      return this.w;
   }

   public void setW(IsoGridSquare var1) {
      this.w = var1;
   }

   public float getLampostTotalR() {
      return this.lighting[0].lampostTotalR();
   }

   public void setLampostTotalR(float var1) {
      this.lighting[0].lampostTotalR(var1);
   }

   public float getLampostTotalG() {
      return this.lighting[0].lampostTotalG();
   }

   public void setLampostTotalG(float var1) {
      this.lighting[0].lampostTotalG(var1);
   }

   public float getLampostTotalB() {
      return this.lighting[0].lampostTotalB();
   }

   public void setLampostTotalB(float var1) {
      this.lighting[0].lampostTotalB(var1);
   }

   public boolean isSeen(int var1) {
      return this.lighting[var1].bSeen();
   }

   public void setIsSeen(int var1, boolean var2) {
      this.lighting[var1].bSeen(var2);
   }

   public float getDarkMulti(int var1) {
      return this.lighting[var1].darkMulti();
   }

   public void setDarkMulti(int var1, float var2) {
      this.lighting[var1].darkMulti(var2);
   }

   public float getTargetDarkMulti(int var1) {
      return this.lighting[var1].targetDarkMulti();
   }

   public void setTargetDarkMulti(int var1, float var2) {
      this.lighting[var1].targetDarkMulti(var2);
   }

   public void setX(int var1) {
      this.x = var1;
      this.CachedScreenValue = -1;
   }

   public void setY(int var1) {
      this.y = var1;
      this.CachedScreenValue = -1;
   }

   public void setZ(int var1) {
      this.z = var1;
      this.CachedScreenValue = -1;
   }

   public ArrayList getDeferedCharacters() {
      return this.DeferedCharacters;
   }

   public void setDeferedCharacters(ArrayList var1) {
      this.DeferedCharacters = var1;
   }

   public void addDeferredCharacter(IsoGameCharacter var1) {
      if (this.DeferredCharacterTick != this.getCell().DeferredCharacterTick) {
         if (!this.DeferedCharacters.isEmpty()) {
            this.DeferedCharacters.clear();
         }

         this.DeferredCharacterTick = this.getCell().DeferredCharacterTick;
      }

      this.DeferedCharacters.add(var1);
   }

   public boolean isCacheIsFree() {
      return this.CacheIsFree;
   }

   public void setCacheIsFree(boolean var1) {
      this.CacheIsFree = var1;
   }

   public boolean isCachedIsFree() {
      return this.CachedIsFree;
   }

   public void setCachedIsFree(boolean var1) {
      this.CachedIsFree = var1;
   }

   public static boolean isbDoSlowPathfinding() {
      return bDoSlowPathfinding;
   }

   public static void setbDoSlowPathfinding(boolean var0) {
      bDoSlowPathfinding = var0;
   }

   public boolean isSolidFloorCached() {
      return this.SolidFloorCached;
   }

   public void setSolidFloorCached(boolean var1) {
      this.SolidFloorCached = var1;
   }

   public boolean isSolidFloor() {
      return this.SolidFloor;
   }

   public void setSolidFloor(boolean var1) {
      this.SolidFloor = var1;
   }

   public static Comparator getComp() {
      return comp;
   }

   public static void setComp(Comparator var0) {
      comp = var0;
   }

   public static ColorInfo getDefColorInfo() {
      return defColorInfo;
   }

   public static void setDefColorInfo(ColorInfo var0) {
      defColorInfo = var0;
   }

   public boolean isOutside() {
      return this.Properties.Is(IsoFlagType.exterior);
   }

   public boolean HasPushable() {
      int var1 = this.MovingObjects.size();

      for(int var2 = 0; var2 < var1; ++var2) {
         if (this.MovingObjects.get(var2) instanceof IsoPushableObject) {
            return true;
         }
      }

      return false;
   }

   public void setRoomID(int var1) {
      this.roomID = var1;
      if (var1 != -1) {
         this.getProperties().UnSet(IsoFlagType.exterior);
         this.room = this.chunk.getRoom(var1);
      }

   }

   public int getRoomID() {
      return this.roomID;
   }

   public boolean getCanSee(int var1) {
      return this.lighting[var1].bCanSee();
   }

   public boolean getSeen(int var1) {
      return this.lighting[var1].bSeen();
   }

   public IsoChunk getChunk() {
      return this.chunk;
   }

   public IsoObject getDoorOrWindow(boolean var1) {
      for(int var2 = this.SpecialObjects.size() - 1; var2 >= 0; --var2) {
         IsoObject var3 = (IsoObject)this.SpecialObjects.get(var2);
         if (var3 instanceof IsoDoor && ((IsoDoor)var3).north == var1) {
            return var3;
         }

         if (var3 instanceof IsoThumpable && ((IsoThumpable)var3).north == var1 && (((IsoThumpable)var3).isDoor() || ((IsoThumpable)var3).isWindow())) {
            return var3;
         }

         if (var3 instanceof IsoWindow && ((IsoWindow)var3).north == var1) {
            return var3;
         }
      }

      return null;
   }

   public IsoObject getOpenDoor(IsoDirections var1) {
      boolean var2;
      String var3;
      String var4;
      switch(var1) {
      case N:
         var2 = false;
         var3 = "7";
         var4 = "8";
         break;
      case S:
         var2 = false;
         var3 = "5";
         var4 = "6";
         break;
      case W:
         var2 = true;
         var3 = "5";
         var4 = "6";
         break;
      case E:
         var2 = true;
         var3 = "7";
         var4 = "8";
         break;
      default:
         return null;
      }

      IsoObject var5 = this.getDoor(var2);
      if (var5 instanceof IsoDoor && ((IsoDoor)var5).open || var5 instanceof IsoThumpable && ((IsoThumpable)var5).open) {
         PropertyContainer var6 = var5.getProperties();
         String var7 = var6 == null ? null : var6.Val("DoubleDoor");
         if (var7 == null) {
            if (var2 && var1 == IsoDirections.W) {
               return var5;
            }

            if (!var2 && var1 == IsoDirections.N) {
               return var5;
            }

            return null;
         }

         if (var3.equals(var7) || var4.equals(var7)) {
            return var5;
         }
      }

      return null;
   }

   public void setWorldObjects(ArrayList var1) {
      this.WorldObjects = var1;
   }

   public void removeWorldObject(IsoWorldInventoryObject var1) {
      if (var1 != null) {
         var1.removeFromWorld();
         var1.removeFromSquare();
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

   public void setVertLight(int var1, int var2, int var3) {
      this.lighting[var3].lightverts(var1, var2);
   }

   public int getVertLight(int var1, int var2) {
      return this.lighting[var2].lightverts(var1);
   }

   public void setRainDrop(IsoRaindrop var1) {
      this.RainDrop = var1;
   }

   public IsoRaindrop getRainDrop() {
      return this.RainDrop;
   }

   public void setRainSplash(IsoRainSplash var1) {
      this.RainSplash = var1;
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

   public void setOverlayDone(boolean var1) {
      this.overlayDone = var1;
   }

   public ErosionData.Square getErosionData() {
      if (this.erosion == null) {
         this.erosion = new ErosionData.Square();
      }

      return this.erosion;
   }

   public void disableErosion() {
      ErosionData.Square var1 = this.getErosionData();
      if (var1 != null && !var1.doNothing) {
         var1.doNothing = true;
      }

   }

   public void removeErosionObject(String var1) {
      if (this.erosion != null) {
         if ("WallVines".equals(var1)) {
            for(int var2 = 0; var2 < this.erosion.regions.size(); ++var2) {
               ErosionCategory.Data var3 = (ErosionCategory.Data)this.erosion.regions.get(var2);
               if (var3.regionID == 2 && var3.categoryID == 0) {
                  this.erosion.regions.remove(var2);
                  break;
               }
            }
         }

      }
   }

   public void drawCircleExplosion(int var1, HandWeapon var2, boolean var3) {
      if (GameClient.bClient) {
         this.syncIsoTrap(var2, var3, true);
      } else {
         IsoTrap var4 = new IsoTrap(var2, this.getCell(), this);
         this.drawCircleExplosion(var1, var4, var3);
         var4.removeFromWorld();
      }
   }

   public void syncIsoTrap(HandWeapon var1, boolean var2, boolean var3) {
      ByteBufferWriter var4 = GameClient.connection.startPacket();
      PacketTypes.doPacket((short)110, var4);
      var4.putInt(this.getX());
      var4.putInt(this.getY());
      var4.putInt(this.getZ());
      int var5 = 0;
      if (var1.getExplosionRange() > 0) {
         var5 = var1.getExplosionRange();
      }

      if (var1.getFireRange() > 0) {
         var5 = var1.getFireRange();
      }

      if (var1.getSmokeRange() > 0) {
         var5 = var1.getSmokeRange();
      }

      try {
         var1.save(var4.bb, false);
      } catch (IOException var7) {
         var7.printStackTrace();
      }

      var4.putInt(var5);
      var4.putBoolean(var2);
      var4.putBoolean(var3);
      GameClient.connection.endPacketImmediate();
   }

   public void drawCircleExplosion(int var1, IsoTrap var2, boolean var3) {
      if (var1 > 15) {
         var1 = 15;
      }

      IsoGridSquare var4 = null;
      if (var2.getExplosionSound() != null && !var3) {
         if (GameServer.bServer) {
            GameServer.PlayWorldSoundServer(var2.getExplosionSound(), false, this, 0.0F, 50.0F, 1.0F, false);
         } else {
            SoundManager.instance.PlayWorldSound(var2.getExplosionSound(), this, 0.0F, 50.0F, 1.0F, false);
         }

         WorldSoundManager.instance.addSound((IsoObject)null, this.x, this.y, 0, 50, 1);
      }

      if (var2.getNoiseRange() > 0 && !var3) {
         WorldSoundManager.instance.addSound((IsoObject)null, this.x, this.y, 0, var2.getNoiseRange(), 1);
      }

      for(int var5 = this.getX() - var1; var5 <= this.getX() + var1; ++var5) {
         for(int var6 = this.getY() - var1; var6 <= this.getY() + var1; ++var6) {
            if (IsoUtils.DistanceTo((float)var5 + 0.5F, (float)var6 + 0.5F, (float)this.getX() + 0.5F, (float)this.getY() + 0.5F) <= (float)var1) {
               LosUtil.TestResults var7 = LosUtil.lineClear(this.getCell(), (int)var2.getX(), (int)var2.getY(), (int)var2.getZ(), var5, var6, this.z, false);
               if (var7 != LosUtil.TestResults.Blocked && var7 != LosUtil.TestResults.ClearThroughClosedDoor) {
                  var4 = this.getCell().getGridSquare(var5, var6, this.getZ());
                  if (var4 != null) {
                     if (var2.getSmokeRange() > 0 && !var3) {
                        if (Rand.Next(2) == 0) {
                           IsoFireManager.StartSmoke(this.getCell(), var4, true, 40, 0);
                        }

                        var4.smoke();
                     }

                     if (var2.getExplosionRange() > 0 && !var3) {
                        if (var2.getExplosionPower() > 0 && Rand.Next(80 - var2.getExplosionPower()) <= 0) {
                           var4.Burn();
                        }

                        var4.explosion(var2);
                        if (var2.getExplosionPower() > 0 && Rand.Next(100 - var2.getExplosionPower()) == 0) {
                           IsoFireManager.StartFire(this.getCell(), var4, true, 20);
                        }
                     }

                     if (var2.getFireRange() > 0 && !var3 && Rand.Next(100 - var2.getFirePower()) == 0) {
                        IsoFireManager.StartFire(this.getCell(), var4, true, 40);
                     }

                     if (var2.getSensorRange() > 0) {
                        var4.setTrapPositionX(this.getX());
                        var4.setTrapPositionY(this.getY());
                        var4.setTrapPositionZ(this.getZ());
                     }
                  }
               }
            }
         }
      }

   }

   public void explosion(IsoTrap var1) {
      for(int var2 = 0; var2 < this.getMovingObjects().size(); ++var2) {
         IsoMovingObject var3 = (IsoMovingObject)this.getMovingObjects().get(var2);
         if (var3 instanceof IsoGameCharacter) {
            int var4 = Math.min(var1.getExplosionPower(), 80);
            var3.Hit((HandWeapon)InventoryItemFactory.CreateItem("Base.Axe"), IsoWorld.instance.CurrentCell.getFakeZombieForHit(), Rand.Next((float)var4 / 30.0F, (float)var4 / 30.0F * 2.0F) + var1.getExtraDamage(), false, 1.0F);
            if (var1.getExplosionPower() > 0) {
               boolean var5 = !(var3 instanceof IsoZombie);

               while(var5) {
                  var5 = false;
                  BodyPart var6 = ((IsoGameCharacter)var3).getBodyDamage().getBodyPart(BodyPartType.FromIndex(Rand.Next(15)));
                  var6.setBurned();
                  if (Rand.Next((100 - var4) / 2) == 0) {
                     var5 = true;
                  }
               }
            }
         }
      }

   }

   public void smoke() {
      for(int var1 = 0; var1 < this.getMovingObjects().size(); ++var1) {
         IsoMovingObject var2 = (IsoMovingObject)this.getMovingObjects().get(var1);
         if (var2 instanceof IsoZombie) {
            ((IsoZombie)var2).target = null;
            ((IsoZombie)var2).getStateMachine().changeState(ZombieStandState.instance());
         }
      }

   }

   public void explodeTrap() {
      IsoGridSquare var1 = this.getCell().getGridSquare(this.getTrapPositionX(), this.getTrapPositionY(), this.getTrapPositionZ());
      if (var1 != null) {
         for(int var2 = 0; var2 < var1.getObjects().size(); ++var2) {
            IsoObject var3 = (IsoObject)var1.getObjects().get(var2);
            if (var3 instanceof IsoTrap) {
               IsoTrap var4 = (IsoTrap)var3;
               var4.triggerExplosion(false);
               IsoGridSquare var5 = null;
               int var6 = var4.getSensorRange();

               for(int var7 = var1.getX() - var6; var7 <= var1.getX() + var6; ++var7) {
                  for(int var8 = var1.getY() - var6; var8 <= var1.getY() + var6; ++var8) {
                     if (IsoUtils.DistanceTo((float)var7 + 0.5F, (float)var8 + 0.5F, (float)var1.getX() + 0.5F, (float)var1.getY() + 0.5F) <= (float)var6) {
                        var5 = this.getCell().getGridSquare(var7, var8, this.getZ());
                        if (var5 != null) {
                           var5.setTrapPositionX(-1);
                           var5.setTrapPositionY(-1);
                           var5.setTrapPositionZ(-1);
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

   public void setTrapPositionX(int var1) {
      this.trapPositionX = var1;
   }

   public int getTrapPositionY() {
      return this.trapPositionY;
   }

   public void setTrapPositionY(int var1) {
      this.trapPositionY = var1;
   }

   public int getTrapPositionZ() {
      return this.trapPositionZ;
   }

   public void setTrapPositionZ(int var1) {
      this.trapPositionZ = var1;
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

   public void setHaveElectricity(boolean var1) {
      if (!var1) {
         this.haveElectricity = false;
      }

      if (this.getObjects() != null) {
         for(int var2 = 0; var2 < this.getObjects().size(); ++var2) {
            if (this.getObjects().get(var2) instanceof IsoLightSwitch) {
               ((IsoLightSwitch)this.getObjects().get(var2)).update();
            }
         }
      }

   }

   public IsoGenerator getGenerator() {
      if (this.getSpecialObjects() != null) {
         for(int var1 = 0; var1 < this.getSpecialObjects().size(); ++var1) {
            if (this.getSpecialObjects().get(var1) instanceof IsoGenerator) {
               return (IsoGenerator)this.getSpecialObjects().get(var1);
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

   public long playSound(String var1) {
      BaseSoundEmitter var2 = IsoWorld.instance.getFreeEmitter((float)this.x + 0.5F, (float)this.y + 0.5F, (float)this.z);
      return var2.playSound(var1);
   }

   /** @deprecated */
   @Deprecated
   public long playSound(String var1, boolean var2) {
      BaseSoundEmitter var3 = IsoWorld.instance.getFreeEmitter((float)this.x + 0.5F, (float)this.y + 0.5F, (float)this.z);
      return var3.playSound(var1, var2);
   }

   public void FixStackableObjects() {
      IsoObject var1 = null;

      for(int var2 = 0; var2 < this.Objects.size(); ++var2) {
         IsoObject var3 = (IsoObject)this.Objects.get(var2);
         if (!(var3 instanceof IsoWorldInventoryObject) && var3.sprite != null) {
            PropertyContainer var4 = var3.sprite.getProperties();
            if (var4.getStackReplaceTileOffset() != 0) {
               var3.sprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, var3.sprite.ID + var4.getStackReplaceTileOffset());
               if (var3.sprite == null) {
                  continue;
               }

               var4 = var3.sprite.getProperties();
            }

            if (var4.isTable() || var4.isTableTop()) {
               float var5 = var4.isSurfaceOffset() ? (float)var4.getSurface() : 0.0F;
               if (var1 != null) {
                  var3.setRenderYOffset(var1.getRenderYOffset() + var1.getSurfaceOffset() - var5);
               } else {
                  var3.setRenderYOffset(0.0F - var5);
               }
            }

            if (var4.isTable()) {
               var1 = var3;
            }
         }
      }

   }

   public void FixBarricades(int var1) {
      if (var1 < 87) {
         for(int var2 = 0; var2 < this.Objects.size(); ++var2) {
            IsoObject var3 = (IsoObject)this.Objects.get(var2);
            int var4 = 0;
            if (var3 instanceof IsoDoor) {
               IsoDoor var5 = (IsoDoor)var3;
               var4 = var5.OldNumPlanks;
            } else if (var3 instanceof IsoThumpable) {
               IsoThumpable var7 = (IsoThumpable)var3;
               var4 = var7.OldNumPlanks;
            } else if (var3 instanceof IsoWindow) {
               IsoWindow var8 = (IsoWindow)var3;
               var4 = var8.OldNumPlanks;
            }

            if (var4 != 0) {
               if (var3.AttachedAnimSpriteActual != null) {
                  var3.AttachedAnimSpriteActual.clear();
               }

               if (var3.AttachedAnimSprite != null) {
                  var3.AttachedAnimSprite.clear();
               }

               IsoBarricade var9 = IsoBarricade.AddBarricadeToObject((BarricadeAble)var3, false);
               if (var9 != null) {
                  for(int var6 = 0; var6 < var4; ++var6) {
                     var9.addPlank((IsoGameCharacter)null, (InventoryItem)null);
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
      int var1 = (int)(((float)this.x - 4.0F) / 10.0F);
      int var2 = (int)(((float)this.y - 4.0F) / 10.0F);
      int var3 = (int)Math.ceil((double)(((float)this.x + 4.0F) / 10.0F));
      int var4 = (int)Math.ceil((double)(((float)this.y + 4.0F) / 10.0F));

      for(int var5 = var2; var5 < var4; ++var5) {
         for(int var6 = var1; var6 < var3; ++var6) {
            IsoChunk var7 = GameServer.bServer ? ServerMap.instance.getChunk(var6, var5) : IsoWorld.instance.CurrentCell.getChunk(var6, var5);
            if (var7 != null) {
               for(int var8 = 0; var8 < var7.vehicles.size(); ++var8) {
                  BaseVehicle var9 = (BaseVehicle)var7.vehicles.get(var8);
                  if (var9.isIntersectingSquare(this.x, this.y, this.z)) {
                     return var9;
                  }
               }
            }
         }
      }

      return null;
   }

   public boolean isVehicleIntersecting() {
      int var1 = (int)(((float)this.x - 4.0F) / 10.0F);
      int var2 = (int)(((float)this.y - 4.0F) / 10.0F);
      int var3 = (int)Math.ceil((double)(((float)this.x + 4.0F) / 10.0F));
      int var4 = (int)Math.ceil((double)(((float)this.y + 4.0F) / 10.0F));

      for(int var5 = var2; var5 < var4; ++var5) {
         for(int var6 = var1; var6 < var3; ++var6) {
            IsoChunk var7 = GameServer.bServer ? ServerMap.instance.getChunk(var6, var5) : IsoWorld.instance.CurrentCell.getChunk(var6, var5);
            if (var7 != null) {
               for(int var8 = 0; var8 < var7.vehicles.size(); ++var8) {
                  BaseVehicle var9 = (BaseVehicle)var7.vehicles.get(var8);
                  if (var9.isIntersectingSquare(this.x, this.y, this.z)) {
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
         for(int var1 = 0; var1 < this.getSpecialObjects().size(); ++var1) {
            if (this.getSpecialObjects().get(var1) instanceof IsoCompost) {
               return (IsoCompost)this.getSpecialObjects().get(var1);
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

   public void setMasterRegion(MasterRegion var1) {
      this.hasSetMasterRegion = var1 != null;
      this.masterRegion = var1;
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
      public IsoGridSquare getGridSquare(int var1, int var2, int var3) {
         return IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, var3);
      }
   }

   public interface GetSquare {
      IsoGridSquare getGridSquare(int var1, int var2, int var3);
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

      public int lightverts(int var1) {
         return this.lightverts[var1];
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

      public void lightverts(int var1, int var2) {
         this.lightverts[var1] = var2;
      }

      public void lampostTotalR(float var1) {
         this.lampostTotalR = var1;
      }

      public void lampostTotalG(float var1) {
         this.lampostTotalG = var1;
      }

      public void lampostTotalB(float var1) {
         this.lampostTotalB = var1;
      }

      public void bSeen(boolean var1) {
         this.bSeen = var1;
      }

      public void bCanSee(boolean var1) {
         this.bCanSee = var1;
      }

      public void bCouldSee(boolean var1) {
         this.bCouldSee = var1;
      }

      public void darkMulti(float var1) {
         this.darkMulti = var1;
      }

      public void targetDarkMulti(float var1) {
         this.targetDarkMulti = var1;
      }

      public void setPos(int var1, int var2, int var3) {
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
      int lightverts(int var1);

      float lampostTotalR();

      float lampostTotalG();

      float lampostTotalB();

      boolean bSeen();

      boolean bCanSee();

      boolean bCouldSee();

      float darkMulti();

      float targetDarkMulti();

      ColorInfo lightInfo();

      void lightverts(int var1, int var2);

      void lampostTotalR(float var1);

      void lampostTotalG(float var1);

      void lampostTotalB(float var1);

      void bSeen(boolean var1);

      void bCanSee(boolean var1);

      void bCouldSee(boolean var1);

      void darkMulti(float var1);

      void targetDarkMulti(float var1);

      void setPos(int var1, int var2, int var3);

      void reset();
   }
}
