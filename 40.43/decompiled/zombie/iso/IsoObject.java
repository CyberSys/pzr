package zombie.iso;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.AmbientStreamManager;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoLivingCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.bucket.BucketManager;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.RenderSettings;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.input.Mouse;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.HandWeapon;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.isoregion.IsoRegion;
import zombie.iso.objects.BSFurnace;
import zombie.iso.objects.IsoBarbecue;
import zombie.iso.objects.IsoBarricade;
import zombie.iso.objects.IsoCarBatteryCharger;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoCrate;
import zombie.iso.objects.IsoCurtain;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoJukebox;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoStove;
import zombie.iso.objects.IsoTelevision;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTrap;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWheelieBin;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.iso.objects.IsoWoodenWall;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.ObjectRenderEffects;
import zombie.iso.objects.RenderEffectType;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.spnetwork.SinglePlayerServer;
import zombie.ui.ObjectTooltip;
import zombie.ui.UIManager;
import zombie.util.list.PZArrayList;
import zombie.vehicles.BaseVehicle;

public class IsoObject implements Serializable {
   public static final byte OBF_Highlighted = 1;
   public static final byte OBF_HighlightRenderOnce = 2;
   public static final byte OBF_Blink = 4;
   public byte highlightFlags;
   private boolean specialTooltip = false;
   public int keyId = -1;
   private ColorInfo highlightColor = new ColorInfo(0.9F, 1.0F, 0.0F, 1.0F);
   public BaseSoundEmitter emitter;
   public float sheetRopeHealth = 100.0F;
   public boolean sheetRope = false;
   private static int DefaultCondition = 0;
   ArrayList Children;
   public float[] alpha = new float[4];
   public static float alphaStep = 0.03F;
   public boolean bNeverDoneAlpha = true;
   public ArrayList AttachedAnimSprite;
   public ArrayList AttachedAnimSpriteActual;
   public static final int MAX_WALL_SPLATS = 32;
   public ArrayList wallBloodSplats;
   public ItemContainer container = null;
   private ArrayList secondaryContainers;
   public IsoDirections dir;
   public short Damage;
   public boolean NoPicking;
   public float offsetX;
   public float offsetY;
   public boolean OutlineOnMouseover;
   public IsoObject rerouteMask;
   public IsoSprite sprite;
   public IsoSprite overlaySprite;
   public ColorInfo overlaySpriteColor;
   public IsoGridSquare square;
   public float[] targetAlpha;
   public IsoObject rerouteCollide;
   public KahluaTable table;
   public String name;
   String tile;
   public static IsoObject lastRendered = null;
   public static IsoObject lastRenderedRendered = null;
   public float tintr;
   public float tintg;
   public float tintb;
   public static ColorInfo stCol = new ColorInfo();
   private static ColorInfo stCol2 = new ColorInfo();
   protected ObjectRenderEffects windRenderEffects;
   protected ObjectRenderEffects objectRenderEffects;
   private static final String PropMoveWithWind = "MoveWithWind";
   public String spriteName;
   public int sx;
   public int sy;
   private ColorInfo customColor;
   public boolean doNotSync;
   protected IsoObject externalWaterSource;
   protected boolean usesExternalWaterSource;
   private float renderYOffset;
   public static float rmod;
   public static float gmod;
   public static float bmod;
   private float blinkAlpha;
   private boolean blinkAlphaIncrease;
   public static boolean LowLightingQualityHack = false;
   private static ColorInfo colFxMask = new ColorInfo(1.0F, 1.0F, 1.0F, 1.0F);

   public static IsoObject getNew(IsoGridSquare var0, String var1, String var2, boolean var3) {
      IsoObject var4 = null;
      if (CellLoader.isoObjectCache.isEmpty()) {
         var4 = new IsoObject(var0, var1, var2, var3);
      } else {
         var4 = (IsoObject)CellLoader.isoObjectCache.pop();
         var4.reset();
      }

      if (var3) {
         var4.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         var4.sprite.LoadFramesNoDirPageSimple(var4.tile);
      } else {
         var4.sprite = (IsoSprite)var0.getCell().SpriteManager.NamedMap.get(var4.tile);
      }

      var4.square = var0;
      var4.name = var2;
      return var4;
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

   public void syncIsoObject(boolean var1, byte var2, UdpConnection var3, ByteBuffer var4) {
   }

   public void syncIsoObjectSend(ByteBufferWriter var1) {
      var1.putInt(this.square.getX());
      var1.putInt(this.square.getY());
      var1.putInt(this.square.getZ());
      var1.putByte((byte)this.square.getObjects().indexOf(this));
      var1.putByte((byte)0);
      var1.putByte((byte)0);
   }

   public String getTextureName() {
      return this.sprite == null ? null : this.sprite.name;
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

   public static String getMD5Checksum(String var0) throws Exception {
      byte[] var1 = createChecksum(var0);
      String var2 = "";

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2 = var2 + Integer.toString((var1[var3] & 255) + 256, 16).substring(1);
      }

      return var2;
   }

   public static IsoObject getLastRendered() {
      return lastRendered;
   }

   public static void setLastRendered(IsoObject var0) {
      lastRendered = var0;
   }

   public static IsoObject getLastRenderedRendered() {
      return lastRenderedRendered;
   }

   public static void setLastRenderedRendered(IsoObject var0) {
      lastRenderedRendered = var0;
   }

   public static void setDefaultCondition(int var0) {
      DefaultCondition = var0;
   }

   public boolean Serialize() {
      return true;
   }

   public IsoObject(IsoCell var1) {
      this.dir = IsoDirections.N;
      this.Damage = 100;
      this.NoPicking = false;
      this.offsetX = (float)(32 * Core.TileScale);
      this.offsetY = (float)(96 * Core.TileScale);
      this.OutlineOnMouseover = false;
      this.rerouteMask = null;
      this.sprite = null;
      this.overlaySprite = null;
      this.overlaySpriteColor = null;
      this.targetAlpha = new float[4];
      this.rerouteCollide = null;
      this.table = null;
      this.name = null;
      this.tintr = 1.0F;
      this.tintg = 1.0F;
      this.tintb = 1.0F;
      this.spriteName = null;
      this.customColor = null;
      this.doNotSync = false;
      this.externalWaterSource = null;
      this.usesExternalWaterSource = false;
      this.renderYOffset = 0.0F;
      this.blinkAlpha = 1.0F;
      this.blinkAlphaIncrease = false;

      for(int var2 = 0; var2 < 4; ++var2) {
         this.alpha[var2] = 1.0F;
         this.targetAlpha[var2] = 1.0F;
      }

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

   public static IsoObject getNew() {
      return CellLoader.isoObjectCache.isEmpty() ? new IsoObject() : (IsoObject)CellLoader.isoObjectCache.pop();
   }

   public static IsoObject factoryFromFileInput(IsoCell var0, int var1) {
      IsoObject var2;
      if (var1 == "IsoObject".hashCode()) {
         if (CellLoader.isoObjectCache.isEmpty()) {
            return new IsoObject(var0);
         } else {
            var2 = (IsoObject)CellLoader.isoObjectCache.pop();
            var2.sx = 0;
            return var2;
         }
      } else if (var1 == "Player".hashCode()) {
         return new IsoPlayer(var0);
      } else if (var1 == "Survivor".hashCode()) {
         return new IsoSurvivor(var0);
      } else if (var1 == "Zombie".hashCode()) {
         return new IsoZombie(var0);
      } else if (var1 == "Pushable".hashCode()) {
         return new IsoPushableObject(var0);
      } else if (var1 == "WheelieBin".hashCode()) {
         return new IsoWheelieBin(var0);
      } else if (var1 == "WorldInventoryItem".hashCode()) {
         return new IsoWorldInventoryObject(var0);
      } else if (var1 == "Jukebox".hashCode()) {
         return new IsoJukebox(var0);
      } else if (var1 == "Curtain".hashCode()) {
         return new IsoCurtain(var0);
      } else if (var1 == "Radio".hashCode()) {
         return new IsoRadio(var0);
      } else if (var1 == "Television".hashCode()) {
         return new IsoTelevision(var0);
      } else if (var1 == "DeadBody".hashCode()) {
         return new IsoDeadBody(var0);
      } else if (var1 == "Barbecue".hashCode()) {
         return new IsoBarbecue(var0);
      } else if (var1 == "Fireplace".hashCode()) {
         return new IsoFireplace(var0);
      } else if (var1 == "Stove".hashCode()) {
         return new IsoStove(var0);
      } else if (var1 == "Door".hashCode()) {
         return new IsoDoor(var0);
      } else if (var1 == "Thumpable".hashCode()) {
         return new IsoThumpable(var0);
      } else if (var1 == "IsoTrap".hashCode()) {
         return new IsoTrap(var0);
      } else if (var1 == "IsoCarBatteryCharger".hashCode()) {
         return new IsoCarBatteryCharger(var0);
      } else if (var1 == "IsoGenerator".hashCode()) {
         return new IsoGenerator(var0);
      } else if (var1 == "IsoCompost".hashCode()) {
         return new IsoCompost(var0);
      } else if (var1 == "StoneFurnace".hashCode()) {
         return new BSFurnace(var0);
      } else if (var1 == "Window".hashCode()) {
         return new IsoWindow(var0);
      } else if (var1 == "Curtain".hashCode()) {
         return new IsoCurtain(var0);
      } else if (var1 == "Barricade".hashCode()) {
         return new IsoBarricade(var0);
      } else if (var1 == "Crate".hashCode()) {
         return new IsoCrate(var0);
      } else if (var1 == "Tree".hashCode()) {
         if (CellLoader.isoTreeCache.isEmpty()) {
            return new IsoTree(var0);
         } else {
            IsoTree var3 = (IsoTree)CellLoader.isoTreeCache.pop();
            var3.sx = 0;
            return var3;
         }
      } else if (var1 == "LightSwitch".hashCode()) {
         return new IsoLightSwitch(var0);
      } else if (var1 == "ZombieGiblets".hashCode()) {
         return new IsoZombieGiblets(var0);
      } else if (var1 == "MolotovCocktail".hashCode()) {
         return new IsoMolotovCocktail(var0);
      } else if (var1 == "WoodenWall".hashCode()) {
         return new IsoWoodenWall(var0);
      } else if (var1 == "Fire".hashCode()) {
         return new IsoFire(var0);
      } else if (var1 == "Vehicle".hashCode() && !GameClient.bClient) {
         return new BaseVehicle(var0);
      } else {
         var2 = new IsoObject(var0);
         return var2;
      }
   }

   public static Class factoryClassFromFileInput(IsoCell var0, int var1) {
      if (var1 == "IsoObject".hashCode()) {
         return IsoObject.class;
      } else if (var1 == "Player".hashCode()) {
         return IsoPlayer.class;
      } else if (var1 == "Survivor".hashCode()) {
         return IsoSurvivor.class;
      } else if (var1 == "Zombie".hashCode()) {
         return IsoZombie.class;
      } else if (var1 == "Pushable".hashCode()) {
         return IsoPushableObject.class;
      } else if (var1 == "WheelieBin".hashCode()) {
         return IsoWheelieBin.class;
      } else if (var1 == "WorldInventoryItem".hashCode()) {
         return IsoWorldInventoryObject.class;
      } else if (var1 == "Jukebox".hashCode()) {
         return IsoJukebox.class;
      } else if (var1 == "Curtain".hashCode()) {
         return IsoCurtain.class;
      } else if (var1 == "Radio".hashCode()) {
         return IsoRadio.class;
      } else if (var1 == "Television".hashCode()) {
         return IsoTelevision.class;
      } else if (var1 == "DeadBody".hashCode()) {
         return IsoDeadBody.class;
      } else if (var1 == "Barbecue".hashCode()) {
         return IsoBarbecue.class;
      } else if (var1 == "Fireplace".hashCode()) {
         return IsoFireplace.class;
      } else if (var1 == "Stove".hashCode()) {
         return IsoStove.class;
      } else if (var1 == "Door".hashCode()) {
         return IsoDoor.class;
      } else if (var1 == "Thumpable".hashCode()) {
         return IsoThumpable.class;
      } else if (var1 == "Window".hashCode()) {
         return IsoWindow.class;
      } else if (var1 == "Curtain".hashCode()) {
         return IsoCurtain.class;
      } else if (var1 == "Barricade".hashCode()) {
         return IsoBarricade.class;
      } else if (var1 == "Crate".hashCode()) {
         return IsoCrate.class;
      } else if (var1 == "Tree".hashCode()) {
         return IsoTree.class;
      } else if (var1 == "LightSwitch".hashCode()) {
         return IsoLightSwitch.class;
      } else if (var1 == "ZombieGiblets".hashCode()) {
         return IsoZombieGiblets.class;
      } else if (var1 == "MolotovCocktail".hashCode()) {
         return IsoMolotovCocktail.class;
      } else if (var1 == "WoodenWall".hashCode()) {
         return IsoWoodenWall.class;
      } else {
         return var1 == "Vehicle".hashCode() ? BaseVehicle.class : IsoObject.class;
      }
   }

   static IsoObject factoryFromFileInput(IsoCell var0, DataInputStream var1) throws IOException {
      boolean var2 = var1.readBoolean();
      if (!var2) {
         return null;
      } else {
         int var3 = var1.readInt();
         IsoObject var4 = factoryFromFileInput(var0, var3);
         return var4;
      }
   }

   public static IsoObject factoryFromFileInput(IsoCell var0, ByteBuffer var1) throws IOException {
      boolean var2 = var1.get() != 0;
      if (!var2) {
         return null;
      } else {
         int var3 = var1.getInt();
         IsoObject var4 = factoryFromFileInput(var0, var3);
         return var4;
      }
   }

   public IsoGridSquare getSquare() {
      return this.square;
   }

   public void update() {
      this.checkHaveElectricity();
   }

   public void renderlast() {
   }

   public void DirtySlice() {
   }

   public String getObjectName() {
      if (this.name != null) {
         return this.name;
      } else {
         return this.sprite != null && this.sprite.getParentObjectName() != null ? this.sprite.getParentObjectName() : "IsoObject";
      }
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      int var3 = var1.getInt();
      var3 = IsoChunk.Fix2x(this.square, var3);
      this.sprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, var3);
      this.spriteName = GameWindow.ReadString(var1);
      if (var3 == -1) {
         this.sprite = IsoWorld.instance.spriteManager.getSprite("");

         assert this.sprite != null;

         assert this.sprite.ID == -1;
      }

      if (this.sprite == null) {
         this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.sprite.LoadFramesNoDirPageSimple(this.spriteName);
      }

      int var4 = var1.get() & 255;

      IsoWallBloodSplat var11;
      for(int var5 = 0; var5 < var4; ++var5) {
         if (this.AttachedAnimSprite == null) {
            this.AttachedAnimSprite = new ArrayList();
         }

         if (var4 == 10000) {
            this.AttachedAnimSprite = null;
         }

         int var6 = var1.getInt();
         IsoSprite var7 = IsoSprite.getSprite(IsoWorld.instance.spriteManager, var6);
         if (var7 != null) {
            IsoSpriteInstance var17 = var7.newInstance();
            boolean var9 = var1.get() == 1;
            if (var9) {
               var17.offX = 0.0F;
               var17.offY = 0.0F;
               var17.offZ = 0.0F;
               var17.tintr = 1.0F;
               var17.tintg = 1.0F;
               var17.tintb = 1.0F;
               var17.alpha = 1.0F;
               var17.targetAlpha = 1.0F;
            } else {
               var17.offX = var1.getFloat();
               var17.offY = var1.getFloat();
               var17.offZ = var1.getFloat();
               var17.tintr = var1.getFloat();
               var17.tintg = var1.getFloat();
               var17.tintb = var1.getFloat();
            }

            var17.Flip = var1.get() != 0;
            var17.bCopyTargetAlpha = var1.get() != 0;
            if (var7.name != null && var7.name.startsWith("overlay_blood_")) {
               float var10 = (float)GameTime.getInstance().getWorldAgeHours();
               var11 = new IsoWallBloodSplat(var10, var7);
               if (this.wallBloodSplats == null) {
                  this.wallBloodSplats = new ArrayList();
               }

               this.wallBloodSplats.add(var11);
            } else {
               if (this.AttachedAnimSpriteActual == null) {
                  this.AttachedAnimSpriteActual = new ArrayList(4);
               }

               this.AttachedAnimSprite.add(var17);
               this.AttachedAnimSpriteActual.add(var7);
            }
         } else {
            boolean var8 = var1.get() == 1;
            if (!var8) {
               var1.getFloat();
               var1.getFloat();
               var1.getFloat();
               var1.getFloat();
               var1.getFloat();
               var1.getFloat();
            }

            var1.get();
            var1.get();
         }
      }

      boolean var13 = var1.get() != 0;
      if (var13) {
         this.name = GameWindow.ReadString(var1);
      }

      byte var14 = var1.get();

      for(int var15 = 0; var15 < var14; ++var15) {
         try {
            ItemContainer var19 = new ItemContainer();
            var19.ID = 0;
            var19.parent = this;
            var19.parent.square = this.square;
            var19.SourceGrid = this.square;
            var19.load(var1, var2, false);
            if (var15 == 0) {
               if (this instanceof IsoDeadBody) {
                  var19.Capacity = 8;
               }

               this.container = var19;
            } else {
               this.addSecondaryContainer(var19);
            }
         } catch (Exception var12) {
            if (this.container != null) {
               DebugLog.log("Failed to stream in container ID: " + this.container.ID);
            }

            throw new RuntimeException(var12);
         }
      }

      if (var1.get() != 0) {
         if (this.table == null) {
            this.table = LuaManager.platform.newTable();
         }

         this.table.load(var1, var2);
      }

      this.setOutlineOnMouseover(var1.get() == 1);
      if (var2 >= 34 && var1.get() == 1) {
         String var16 = GameWindow.ReadString(var1);
         if (!var16.isEmpty()) {
            this.overlaySprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(var16);
            this.overlaySprite.name = var16;
         }

         if (var2 >= 37 && var1.get() == 1) {
            this.setOverlaySpriteColor(var1.getFloat(), var1.getFloat(), var1.getFloat(), var1.getFloat());
         }
      }

      if (var2 >= 38) {
         this.setSpecialTooltip(var1.get() == 1);
      }

      if (var2 >= 57) {
         this.keyId = var1.getInt();
      }

      if (var2 >= 65) {
         byte var18 = var1.get();
         if (var18 > 0) {
            if (this.wallBloodSplats == null) {
               this.wallBloodSplats = new ArrayList();
            }

            int var20 = 0;
            if (GameClient.bClient || GameServer.bServer) {
               var20 = ServerOptions.getInstance().BloodSplatLifespanDays.getValue();
            }

            float var21 = (float)GameTime.getInstance().getWorldAgeHours();

            for(int var22 = 0; var22 < var18; ++var22) {
               var11 = new IsoWallBloodSplat();
               var11.load(var1, var2);
               if (var11.worldAge > var21) {
                  var11.worldAge = var21;
               }

               if (var20 <= 0 || !(var21 - var11.worldAge >= (float)(var20 * 24))) {
                  this.wallBloodSplats.add(var11);
               }
            }
         }
      }

      if (var2 >= 75) {
         this.usesExternalWaterSource = var1.get() == 1;
         this.renderYOffset = var1.getFloat();
      }

      if (var2 >= 92 && var1.get() == 1) {
         this.sheetRope = true;
         this.sheetRopeHealth = var1.getFloat();
      }

      if (var2 >= 95 && var1.get() == 1) {
         this.customColor = new ColorInfo(var1.getFloat(), var1.getFloat(), var1.getFloat(), 1.0F);
      }

      if (var2 >= 123) {
         this.doNotSync = var1.get() == 1;
      }

   }

   public void save(ByteBuffer var1) throws IOException {
      var1.put((byte)(this.Serialize() ? 1 : 0));
      if (this.Serialize()) {
         var1.putInt(this.getObjectName().hashCode());
         var1.putInt(this.sprite == null ? -1 : this.sprite.ID);
         GameWindow.WriteString(var1, this.spriteName);
         if (GameWindow.DEBUG_SAVE) {
            GameWindow.WriteString(var1, "Writing attached sprites");
         }

         int var2;
         int var3;
         if (this.AttachedAnimSprite == null) {
            var1.put((byte)0);
         } else {
            var2 = this.AttachedAnimSprite.size() > 255 ? 255 : this.AttachedAnimSprite.size();
            var1.put((byte)var2);

            for(var3 = 0; var3 < var2; ++var3) {
               IsoSpriteInstance var4 = (IsoSpriteInstance)this.AttachedAnimSprite.get(var3);
               var1.putInt(var4.getID());
               if (var4.offX == 0.0F && var4.offY == 0.0F && var4.offZ == 0.0F && var4.tintr == 1.0F && var4.tintg == 1.0F && var4.tintb == 1.0F) {
                  var1.put((byte)1);
               } else {
                  var1.put((byte)0);
                  var1.putFloat(var4.offX);
                  var1.putFloat(var4.offY);
                  var1.putFloat(var4.offZ);
                  var1.putFloat(var4.tintr);
                  var1.putFloat(var4.tintg);
                  var1.putFloat(var4.tintb);
               }

               var1.put((byte)(var4.Flip ? 1 : 0));
               var1.put((byte)(var4.bCopyTargetAlpha ? 1 : 0));
            }
         }

         if (GameWindow.DEBUG_SAVE) {
            GameWindow.WriteString(var1, "Writing name");
         }

         if (this.name != null) {
            var1.put((byte)1);
            GameWindow.WriteString(var1, this.name);
         } else {
            var1.put((byte)0);
         }

         if (GameWindow.DEBUG_SAVE) {
            GameWindow.WriteString(var1, "Writing container");
         }

         var1.put((byte)this.getContainerCount());

         for(var2 = 0; var2 < this.getContainerCount(); ++var2) {
            this.getContainerByIndex(var2).save(var1, false);
         }

         if (this.table != null && !this.table.isEmpty()) {
            var1.put((byte)1);
            this.table.save(var1);
         } else {
            var1.put((byte)0);
         }

         var1.put((byte)(this.isOutlineOnMouseover() ? 1 : 0));
         if (this.getOverlaySprite() != null) {
            var1.put((byte)1);
            GameWindow.WriteString(var1, this.getOverlaySprite().name);
            if (this.getOverlaySpriteColor() != null) {
               var1.put((byte)1);
               var1.putFloat(this.getOverlaySpriteColor().r);
               var1.putFloat(this.getOverlaySpriteColor().g);
               var1.putFloat(this.getOverlaySpriteColor().b);
               var1.putFloat(this.getOverlaySpriteColor().a);
            } else {
               var1.put((byte)0);
            }
         } else {
            var1.put((byte)0);
         }

         var1.put((byte)(this.haveSpecialTooltip() ? 1 : 0));
         var1.putInt(this.getKeyId());
         if (this.wallBloodSplats == null) {
            var1.put((byte)0);
         } else {
            var2 = Math.min(this.wallBloodSplats.size(), 32);
            var3 = this.wallBloodSplats.size() - var2;
            var1.put((byte)var2);

            for(int var5 = var3; var5 < this.wallBloodSplats.size(); ++var5) {
               ((IsoWallBloodSplat)this.wallBloodSplats.get(var5)).save(var1);
            }
         }

         var1.put((byte)(this.usesExternalWaterSource ? 1 : 0));
         var1.putFloat(this.renderYOffset);
         if (this.sheetRope) {
            var1.put((byte)1);
            var1.putFloat(this.sheetRopeHealth);
         } else {
            var1.put((byte)0);
         }

         if (this.customColor != null) {
            var1.put((byte)1);
            var1.putFloat(this.customColor.r);
            var1.putFloat(this.customColor.g);
            var1.putFloat(this.customColor.b);
         } else {
            var1.put((byte)0);
         }

         var1.put((byte)(this.doNotSync ? 1 : 0));
      }
   }

   public void saveState(ByteBuffer var1) {
   }

   public void loadState(ByteBuffer var1) {
   }

   public void softReset() {
      if (this.container != null) {
         this.container.Items.clear();
         this.container.bExplored = false;
         this.setOverlaySprite((String)null, -1.0F, -1.0F, -1.0F, -1.0F, false);
      }

      if (this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
         for(int var1 = 0; var1 < this.AttachedAnimSprite.size(); ++var1) {
            IsoSprite var2 = (IsoSprite)this.AttachedAnimSpriteActual.get(var1);
            if (var2.name != null && var2.name.contains("blood")) {
               this.AttachedAnimSprite.remove(var1);
               this.AttachedAnimSpriteActual.remove(var1);
            }
         }
      }

   }

   public void AttackObject(IsoGameCharacter var1) {
      this.Damage = (short)(this.Damage - 10);
      HandWeapon var2 = (HandWeapon)var1.getPrimaryHandItem();
      SoundManager.instance.PlaySound(var2.getDoorHitSound(), false, 2.0F);
      WorldSoundManager.instance.addSound(var1, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
      if (this.Damage <= 0) {
         this.square.getObjects().remove(this);
         this.square.RecalcAllWithNeighbours(true);
         if (this.getType() == IsoObjectType.stairsBN || this.getType() == IsoObjectType.stairsMN || this.getType() == IsoObjectType.stairsTN || this.getType() == IsoObjectType.stairsBW || this.getType() == IsoObjectType.stairsMW || this.getType() == IsoObjectType.stairsTW) {
            this.square.RemoveAllWith(IsoFlagType.attachtostairs);
         }

         byte var3 = 1;

         for(int var4 = 0; var4 < var3; ++var4) {
            InventoryItem var5 = this.square.AddWorldInventoryItem("Base.Plank", Rand.Next(-1.0F, 1.0F), Rand.Next(-1.0F, 1.0F), 0.0F);
            var5.setUses(1);
         }
      }

   }

   public void onMouseRightClick(int var1, int var2) {
   }

   public void onMouseRightReleased() {
   }

   public void Hit(Vector2 var1, IsoObject var2, float var3) {
      if (var2 instanceof BaseVehicle) {
         this.Damage(var3);
      }

   }

   public void Damage(float var1) {
      short var2 = this.Damage;
      this.Damage = (short)((int)((double)this.Damage - (double)var1 * 0.1D));
      SoundManager.instance.PlayWorldSound("VehicleHitObject", this.square, 1.0F, 20.0F, 1.0F, true);
      WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
      if (this.getProperties().Is("HitByCar") && this.getSprite().getProperties().Val("DamagedSprite") != null && !this.getSprite().getProperties().Val("DamagedSprite").equals("") && this.Damage <= 90 && var2 > 90) {
         this.setSprite(IsoWorld.instance.CurrentCell.SpriteManager.getSprite(this.getSprite().getProperties().Val("DamagedSprite")));
         if (this.getSprite().getProperties().Is("StopCar")) {
            this.getSprite().setType(IsoObjectType.isMoveAbleObject);
         } else {
            this.getSprite().setType(IsoObjectType.MAX);
         }

         if (this instanceof IsoThumpable) {
            ((IsoThumpable)this).setBlockAllTheSquare(false);
         }

         if (GameServer.bServer) {
            this.transmitUpdatedSpriteToClients();
         }

         this.getSquare().RecalcProperties();
         this.Damage = 50;
      }

      if (this.Damage <= 40 && this.getProperties().Is("HitByCar")) {
         this.getSquare().transmitRemoveItemFromSquare(this);
      }

   }

   public void Collision(Vector2 var1, IsoObject var2) {
      if (var2 instanceof BaseVehicle) {
         if (this.getProperties().Is("CarSlowFactor")) {
            int var3 = Integer.parseInt(this.getProperties().Val("CarSlowFactor"));
            BaseVehicle var4 = (BaseVehicle)var2;
            var4.ApplyImpulse(this, Math.abs(var4.getMass() * var4.getCurrentSpeedKmHour() * (float)var3 / 100.0F));
         }

         if (this.getProperties().Is("HitByCar")) {
            BaseVehicle var5 = (BaseVehicle)var2;
            String var6 = this.getSprite().getProperties().Val("MinimumCarSpeedDmg");
            if (var6 == null) {
               var6 = "150";
            }

            if (Math.abs(var5.getCurrentSpeedKmHour()) > (float)Integer.parseInt(var6)) {
               this.Damage(Math.abs(var5.getMass() * var5.getCurrentSpeedKmHour()) / 300.0F);
            } else {
               var5.ApplyImpulse(this, Math.abs(var5.getMass() * var5.getCurrentSpeedKmHour() * 10.0F / 200.0F));
               var5.jniSpeed = 0.0F;
            }

            if (var5.getCurrentSpeedKmHour() > 3.0F) {
               var5.ApplyImpulse(this, Math.abs(var5.getMass() * var5.getCurrentSpeedKmHour() * 10.0F / 150.0F));
            }
         }
      }

   }

   public void UnCollision(IsoObject var1) {
   }

   public float GetVehicleSlowFactor(BaseVehicle var1) {
      if (this.getProperties().Is("CarSlowFactor")) {
         int var2 = Integer.parseInt(this.getProperties().Val("CarSlowFactor"));
         return 33.0F - (float)(10 - var2);
      } else {
         return 0.0F;
      }
   }

   public IsoObject getRerouteCollide() {
      return this.rerouteCollide;
   }

   public void setRerouteCollide(IsoObject var1) {
      this.rerouteCollide = var1;
   }

   public KahluaTable getTable() {
      return this.table;
   }

   public void setTable(KahluaTable var1) {
      this.table = var1;
   }

   public float getAlpha() {
      return this.alpha[IsoPlayer.getPlayerIndex()];
   }

   public void setAlpha(float var1) {
      this.alpha[IsoPlayer.getPlayerIndex()] = var1;
   }

   public float getAlphaStep() {
      return alphaStep;
   }

   public void setAlphaStep(float var1) {
      alphaStep = var1;
   }

   public ArrayList getAttachedAnimSprite() {
      return this.AttachedAnimSprite;
   }

   public void setAttachedAnimSprite(ArrayList var1) {
      this.AttachedAnimSprite = var1;
   }

   public IsoCell getCell() {
      return IsoWorld.instance.CurrentCell;
   }

   public ArrayList getChildSprites() {
      return this.AttachedAnimSprite;
   }

   public void setChildSprites(ArrayList var1) {
      this.AttachedAnimSprite = var1;
   }

   public ItemContainer getContainer() {
      return this.container;
   }

   public void setContainer(ItemContainer var1) {
      this.container = var1;
   }

   public IsoDirections getDir() {
      return this.dir;
   }

   public void setDir(IsoDirections var1) {
      this.dir = var1;
   }

   public void setDir(int var1) {
      this.dir = IsoDirections.fromIndex(var1);
   }

   public short getDamage() {
      return this.Damage;
   }

   public void setDamage(short var1) {
      this.Damage = var1;
   }

   public boolean isNoPicking() {
      return this.NoPicking;
   }

   public void setNoPicking(boolean var1) {
      this.NoPicking = var1;
   }

   public void setOffsetX(float var1) {
      this.offsetX = var1;
   }

   public void setOffsetY(float var1) {
      this.offsetY = var1;
   }

   public boolean isOutlineOnMouseover() {
      return this.OutlineOnMouseover;
   }

   public void setOutlineOnMouseover(boolean var1) {
      this.OutlineOnMouseover = var1;
   }

   public IsoObject getRerouteMask() {
      return this.rerouteMask;
   }

   public void setRerouteMask(IsoObject var1) {
      this.rerouteMask = var1;
   }

   public IsoSprite getSprite() {
      return this.sprite;
   }

   public void setSprite(IsoSprite var1) {
      this.sprite = var1;
      this.windRenderEffects = null;
      this.checkMoveWithWind();
   }

   public void setSprite(String var1) {
      this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
      this.sprite.LoadFramesNoDirPageSimple(var1);
      this.tile = var1;
      this.spriteName = var1;
      this.windRenderEffects = null;
      this.checkMoveWithWind();
   }

   public void setSpriteFromName(String var1) {
      this.sprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(var1);
      this.windRenderEffects = null;
      this.checkMoveWithWind();
   }

   public void setSquare(IsoGridSquare var1) {
      this.square = var1;
   }

   public float getTargetAlpha() {
      return this.targetAlpha[IsoPlayer.getPlayerIndex()];
   }

   public void setTargetAlpha(float var1) {
      this.targetAlpha[IsoPlayer.getPlayerIndex()] = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public IsoObjectType getType() {
      return this.sprite == null ? IsoObjectType.MAX : this.sprite.getType();
   }

   public void setType(IsoObjectType var1) {
      if (this.sprite != null) {
         this.sprite.setType(var1);
      }

   }

   public void addChild(IsoObject var1) {
      if (this.Children == null) {
         this.Children = new ArrayList(4);
      }

      this.Children.add(var1);
   }

   public void debugPrintout() {
      System.out.println(this.getClass().toString());
      System.out.println(this.getObjectName());
   }

   protected void checkMoveWithWind() {
      this.checkMoveWithWind(this.sprite != null && this.sprite.isBush);
   }

   protected void checkMoveWithWind(boolean var1) {
      if (!GameServer.bServer) {
         if (this.sprite != null && this.windRenderEffects == null && this.sprite.moveWithWind) {
            this.windRenderEffects = ObjectRenderEffects.getNextWindEffect(this.sprite.windType, var1);
         } else {
            if (this.windRenderEffects != null && (this.sprite == null || !this.sprite.moveWithWind)) {
               this.windRenderEffects = null;
            }

         }
      }
   }

   public IsoObject() {
      this.dir = IsoDirections.N;
      this.Damage = 100;
      this.NoPicking = false;
      this.offsetX = (float)(32 * Core.TileScale);
      this.offsetY = (float)(96 * Core.TileScale);
      this.OutlineOnMouseover = false;
      this.rerouteMask = null;
      this.sprite = null;
      this.overlaySprite = null;
      this.overlaySpriteColor = null;
      this.targetAlpha = new float[4];
      this.rerouteCollide = null;
      this.table = null;
      this.name = null;
      this.tintr = 1.0F;
      this.tintg = 1.0F;
      this.tintb = 1.0F;
      this.spriteName = null;
      this.customColor = null;
      this.doNotSync = false;
      this.externalWaterSource = null;
      this.usesExternalWaterSource = false;
      this.renderYOffset = 0.0F;
      this.blinkAlpha = 1.0F;
      this.blinkAlphaIncrease = false;

      for(int var1 = 0; var1 < 4; ++var1) {
         this.alpha[var1] = 1.0F;
         this.targetAlpha[var1] = 1.0F;
      }

   }

   public void reset() {
      this.tintr = 1.0F;
      this.tintg = 1.0F;
      this.tintb = 1.0F;
      this.name = null;
      this.table = null;
      this.rerouteCollide = null;
      int var1;
      if (this.AttachedAnimSprite != null) {
         for(var1 = 0; var1 < this.AttachedAnimSprite.size(); ++var1) {
            IsoSpriteInstance var2 = (IsoSpriteInstance)this.AttachedAnimSprite.get(var1);
            IsoSpriteInstance.add(var2);
         }

         this.AttachedAnimSprite.clear();
         if (this.AttachedAnimSpriteActual == null) {
            this.AttachedAnimSpriteActual = new ArrayList();
         }

         this.AttachedAnimSpriteActual.clear();
      }

      if (this.wallBloodSplats != null) {
         this.wallBloodSplats.clear();
      }

      this.overlaySprite = null;
      this.overlaySpriteColor = null;
      if (this.container != null) {
         this.container.Items.clear();
         this.container.IncludingObsoleteItems.clear();
         this.container.setParent((IsoObject)null);
         this.container.setSourceGrid((IsoGridSquare)null);
         this.container.vehiclePart = null;
      }

      this.container = null;
      this.dir = IsoDirections.N;
      this.Damage = 100;
      this.NoPicking = false;
      this.offsetX = (float)(32 * Core.TileScale);
      this.offsetY = (float)(96 * Core.TileScale);
      this.OutlineOnMouseover = false;
      this.rerouteMask = null;
      this.sprite = null;
      this.square = null;

      for(var1 = 0; var1 < 4; ++var1) {
         this.alpha[var1] = 1.0F;
         this.targetAlpha[var1] = 1.0F;
      }

      this.bNeverDoneAlpha = true;
      this.highlightFlags = 0;
      this.tile = null;
      this.spriteName = null;
      this.specialTooltip = false;
      this.usesExternalWaterSource = false;
      this.externalWaterSource = null;
      if (this.secondaryContainers != null) {
         for(var1 = 0; var1 < this.secondaryContainers.size(); ++var1) {
            ItemContainer var3 = (ItemContainer)this.secondaryContainers.get(var1);
            var3.Items.clear();
            var3.IncludingObsoleteItems.clear();
            var3.setParent((IsoObject)null);
            var3.setSourceGrid((IsoGridSquare)null);
            var3.vehiclePart = null;
         }

         this.secondaryContainers.clear();
      }

      this.renderYOffset = 0.0F;
      this.sx = 0;
      this.windRenderEffects = null;
      this.objectRenderEffects = null;
   }

   public IsoObject(IsoCell var1, IsoGridSquare var2, IsoSprite var3) {
      this.dir = IsoDirections.N;
      this.Damage = 100;
      this.NoPicking = false;
      this.offsetX = (float)(32 * Core.TileScale);
      this.offsetY = (float)(96 * Core.TileScale);
      this.OutlineOnMouseover = false;
      this.rerouteMask = null;
      this.sprite = null;
      this.overlaySprite = null;
      this.overlaySpriteColor = null;
      this.targetAlpha = new float[4];
      this.rerouteCollide = null;
      this.table = null;
      this.name = null;
      this.tintr = 1.0F;
      this.tintg = 1.0F;
      this.tintb = 1.0F;
      this.spriteName = null;
      this.customColor = null;
      this.doNotSync = false;
      this.externalWaterSource = null;
      this.usesExternalWaterSource = false;
      this.renderYOffset = 0.0F;
      this.blinkAlpha = 1.0F;
      this.blinkAlphaIncrease = false;
      this.sprite = var3;
      this.square = var2;
   }

   public IsoObject(IsoCell var1, IsoGridSquare var2, String var3) {
      this.dir = IsoDirections.N;
      this.Damage = 100;
      this.NoPicking = false;
      this.offsetX = (float)(32 * Core.TileScale);
      this.offsetY = (float)(96 * Core.TileScale);
      this.OutlineOnMouseover = false;
      this.rerouteMask = null;
      this.sprite = null;
      this.overlaySprite = null;
      this.overlaySpriteColor = null;
      this.targetAlpha = new float[4];
      this.rerouteCollide = null;
      this.table = null;
      this.name = null;
      this.tintr = 1.0F;
      this.tintg = 1.0F;
      this.tintb = 1.0F;
      this.spriteName = null;
      this.customColor = null;
      this.doNotSync = false;
      this.externalWaterSource = null;
      this.usesExternalWaterSource = false;
      this.renderYOffset = 0.0F;
      this.blinkAlpha = 1.0F;
      this.blinkAlphaIncrease = false;
      this.sprite = IsoWorld.instance.spriteManager.getSprite(var3);
      this.square = var2;
      this.tile = var3;
   }

   public IsoObject(IsoGridSquare var1, String var2, String var3) {
      this.dir = IsoDirections.N;
      this.Damage = 100;
      this.NoPicking = false;
      this.offsetX = (float)(32 * Core.TileScale);
      this.offsetY = (float)(96 * Core.TileScale);
      this.OutlineOnMouseover = false;
      this.rerouteMask = null;
      this.sprite = null;
      this.overlaySprite = null;
      this.overlaySpriteColor = null;
      this.targetAlpha = new float[4];
      this.rerouteCollide = null;
      this.table = null;
      this.name = null;
      this.tintr = 1.0F;
      this.tintg = 1.0F;
      this.tintb = 1.0F;
      this.spriteName = null;
      this.customColor = null;
      this.doNotSync = false;
      this.externalWaterSource = null;
      this.usesExternalWaterSource = false;
      this.renderYOffset = 0.0F;
      this.blinkAlpha = 1.0F;
      this.blinkAlphaIncrease = false;
      this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
      this.sprite.LoadFramesNoDirPageSimple(var2);
      this.square = var1;
      this.tile = var2;
      this.spriteName = var2;
      this.name = var3;
   }

   public IsoObject(IsoGridSquare var1, String var2, String var3, boolean var4) {
      this.dir = IsoDirections.N;
      this.Damage = 100;
      this.NoPicking = false;
      this.offsetX = (float)(32 * Core.TileScale);
      this.offsetY = (float)(96 * Core.TileScale);
      this.OutlineOnMouseover = false;
      this.rerouteMask = null;
      this.sprite = null;
      this.overlaySprite = null;
      this.overlaySpriteColor = null;
      this.targetAlpha = new float[4];
      this.rerouteCollide = null;
      this.table = null;
      this.name = null;
      this.tintr = 1.0F;
      this.tintg = 1.0F;
      this.tintb = 1.0F;
      this.spriteName = null;
      this.customColor = null;
      this.doNotSync = false;
      this.externalWaterSource = null;
      this.usesExternalWaterSource = false;
      this.renderYOffset = 0.0F;
      this.blinkAlpha = 1.0F;
      this.blinkAlphaIncrease = false;
      if (var4) {
         this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.sprite.LoadFramesNoDirPageSimple(var2);
      } else {
         this.sprite = (IsoSprite)var1.getCell().SpriteManager.NamedMap.get(var2);
      }

      this.tile = var2;
      this.square = var1;
      this.name = var3;
   }

   public IsoObject(IsoGridSquare var1, String var2, boolean var3) {
      this.dir = IsoDirections.N;
      this.Damage = 100;
      this.NoPicking = false;
      this.offsetX = (float)(32 * Core.TileScale);
      this.offsetY = (float)(96 * Core.TileScale);
      this.OutlineOnMouseover = false;
      this.rerouteMask = null;
      this.sprite = null;
      this.overlaySprite = null;
      this.overlaySpriteColor = null;
      this.targetAlpha = new float[4];
      this.rerouteCollide = null;
      this.table = null;
      this.name = null;
      this.tintr = 1.0F;
      this.tintg = 1.0F;
      this.tintb = 1.0F;
      this.spriteName = null;
      this.customColor = null;
      this.doNotSync = false;
      this.externalWaterSource = null;
      this.usesExternalWaterSource = false;
      this.renderYOffset = 0.0F;
      this.blinkAlpha = 1.0F;
      this.blinkAlphaIncrease = false;
      if (var3) {
         this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.sprite.LoadFramesNoDirPageSimple(var2);
      } else {
         this.sprite = (IsoSprite)var1.getCell().SpriteManager.NamedMap.get(var2);
      }

      this.tile = var2;
      this.square = var1;
   }

   public IsoObject(IsoGridSquare var1, String var2) {
      this.dir = IsoDirections.N;
      this.Damage = 100;
      this.NoPicking = false;
      this.offsetX = (float)(32 * Core.TileScale);
      this.offsetY = (float)(96 * Core.TileScale);
      this.OutlineOnMouseover = false;
      this.rerouteMask = null;
      this.sprite = null;
      this.overlaySprite = null;
      this.overlaySpriteColor = null;
      this.targetAlpha = new float[4];
      this.rerouteCollide = null;
      this.table = null;
      this.name = null;
      this.tintr = 1.0F;
      this.tintg = 1.0F;
      this.tintb = 1.0F;
      this.spriteName = null;
      this.customColor = null;
      this.doNotSync = false;
      this.externalWaterSource = null;
      this.usesExternalWaterSource = false;
      this.renderYOffset = 0.0F;
      this.blinkAlpha = 1.0F;
      this.blinkAlphaIncrease = false;
      this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
      this.sprite.LoadFramesNoDirPageSimple(var2);
      this.square = var1;
   }

   public long customHashCode() {
      if (this.doNotSync) {
         return 0L;
      } else {
         try {
            long var1 = 1L;
            if (this.getObjectName() != null) {
               var1 = var1 * 3L + (long)this.getObjectName().hashCode();
            }

            if (this.name != null) {
               var1 = var1 * 2L + (long)this.name.hashCode();
            }

            if (this.container != null) {
               ++var1;
               var1 += (long)this.container.Items.size();

               for(int var3 = 0; var3 < this.container.Items.size(); ++var3) {
                  var1 += (long)(((InventoryItem)this.container.Items.get(var3)).getModule().hashCode() + ((InventoryItem)this.container.Items.get(var3)).getType().hashCode()) + ((InventoryItem)this.container.Items.get(var3)).id;
               }
            }

            var1 += (long)this.square.getObjects().indexOf(this);
            return var1;
         } catch (Throwable var4) {
            DebugLog.log("ERROR: " + var4.getMessage());
            return 0L;
         }
      }
   }

   public void SetName(String var1) {
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }

   public String getSpriteName() {
      return this.spriteName;
   }

   public String getTile() {
      return this.tile;
   }

   public boolean isCharacter() {
      return this instanceof IsoLivingCharacter;
   }

   public boolean isZombie() {
      return this instanceof IsoZombie;
   }

   public String getScriptName() {
      return "none";
   }

   public void AttachAnim(String var1, String var2, int var3, float var4, int var5, int var6, boolean var7, int var8, boolean var9, float var10, ColorInfo var11) {
      if (this.AttachedAnimSprite == null) {
         this.AttachedAnimSprite = new ArrayList(4);
      }

      if (this.AttachedAnimSpriteActual == null) {
         this.AttachedAnimSpriteActual = new ArrayList(4);
      }

      IsoSprite var12 = IsoSprite.CreateSprite(this.getCell().SpriteManager);
      if (IsoSprite.HasCache(var1 + var2)) {
         var12.LoadCache(var1 + var2);
      } else {
         var12.LoadFramesNoDirPage(var1, var2, var3);
         var12.CacheAnims(var1 + var2);
      }

      var12.TintMod.r = var11.r;
      var12.TintMod.g = var11.g;
      var12.TintMod.b = var11.b;
      var12.TintMod.a = var11.a;
      Integer var13 = var5;
      Integer var14 = var6;
      var12.soffX = (short)(-var13);
      var12.soffY = (short)(-var14);
      var12.Animate = true;
      var12.Loop = var7;
      var12.DeleteWhenFinished = var9;
      var12.PlayAnim(var2);
      IsoSpriteInstance var15 = IsoSpriteInstance.get(var12);
      var15.AnimFrameIncrease = var4;
      var15.Frame = 0.0F;
      this.AttachedAnimSprite.add(var15);
      this.AttachedAnimSpriteActual.add(var12);
   }

   public void AttachExistingAnim(IsoSprite var1, int var2, int var3, boolean var4, int var5, boolean var6, float var7, ColorInfo var8) {
      if (this.AttachedAnimSprite == null) {
         this.AttachedAnimSprite = new ArrayList(4);
      }

      if (this.AttachedAnimSpriteActual == null) {
         this.AttachedAnimSpriteActual = new ArrayList(4);
      }

      var1.TintMod.r = var8.r;
      var1.TintMod.g = var8.g;
      var1.TintMod.b = var8.b;
      var1.TintMod.a = var8.a;
      Integer var10 = var2;
      Integer var11 = var3;
      var1.soffX = (short)(-var10);
      var1.soffY = (short)(-var11);
      var1.Animate = true;
      var1.Loop = var4;
      var1.DeleteWhenFinished = var6;
      this.AttachedAnimSpriteActual.add(var1);
      IsoSpriteInstance var12 = IsoSpriteInstance.get(var1);
      this.AttachedAnimSprite.add(var12);
   }

   public void AttachExistingAnim(IsoSprite var1, int var2, int var3, boolean var4, int var5, boolean var6, float var7) {
      this.AttachExistingAnim(var1, var2, var3, var4, var5, var6, var7, new ColorInfo());
   }

   public void DoTooltip(ObjectTooltip var1) {
   }

   public void DoSpecialTooltip(ObjectTooltip var1, IsoGridSquare var2) {
      if (this.haveSpecialTooltip()) {
         var1.setHeight(0.0D);
         LuaEventManager.triggerEvent("DoSpecialTooltip", var1, var2);
         if (var1.getHeight() == 0.0D) {
            var1.hide();
         }
      }

   }

   public ItemContainer getItemContainer() {
      return this.container;
   }

   public float getOffsetX() {
      return this.offsetX;
   }

   public float getOffsetY() {
      return this.offsetY;
   }

   public IsoObject getRerouteMaskObject() {
      return this.rerouteMask;
   }

   public boolean HasTooltip() {
      return false;
   }

   public void setUsesExternalWaterSource(boolean var1) {
      this.usesExternalWaterSource = var1;
   }

   public boolean getUsesExternalWaterSource() {
      return this.usesExternalWaterSource;
   }

   public boolean hasExternalWaterSource() {
      return this.externalWaterSource != null;
   }

   public void doFindExternalWaterSource() {
      this.externalWaterSource = this.FindExternalWaterSource(this.getSquare(), true);
   }

   protected IsoObject FindExternalWaterSource(IsoGridSquare var1, boolean var2) {
      if (var1 != null) {
         var1 = this.getCell().getGridSquare(var1.getX(), var1.getY(), var1.getZ() + 1);
         if (var1 != null) {
            for(int var3 = 0; var3 < 8; ++var3) {
               IsoGridSquare var4 = var1.getTileInDirection(IsoDirections.fromIndex(var3));
               if (var4 != null) {
                  PZArrayList var5 = var4.getObjects();

                  for(int var6 = 0; var6 < var5.size(); ++var6) {
                     IsoObject var7 = (IsoObject)var5.get(var6);
                     if (!var7.getUsesExternalWaterSource() && var7.hasWater()) {
                        return var7;
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   public void setWaterAmount(int var1) {
      if (this.usesExternalWaterSource) {
         if (!this.sprite.getProperties().Is(IsoFlagType.waterPiped) || GameTime.getInstance().getNightsSurvived() >= SandboxOptions.instance.getWaterShutModifier() || this.square == null || this.square.getRoom() == null) {
            if (this.externalWaterSource != null) {
               this.externalWaterSource.setWaterAmount(var1);
            }

         }
      } else {
         var1 = Math.max(0, var1);
         int var2 = this.getWaterAmount();
         if (var1 != var2) {
            this.getModData().rawset("waterAmount", (double)var1);
            if (var1 <= 0) {
               this.setTaintedWater(false);
            }

            LuaEventManager.triggerEvent("OnWaterAmountChange", this, var2);
         }

      }
   }

   public int getWaterAmount() {
      if (this.sprite == null) {
         return 0;
      } else if (this.usesExternalWaterSource) {
         if (this.sprite.getProperties().Is(IsoFlagType.waterPiped) && GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getWaterShutModifier() && this.square != null && this.square.getRoom() != null) {
            return 10000;
         } else {
            if (this.externalWaterSource == null || !this.externalWaterSource.hasWater()) {
               this.doFindExternalWaterSource();
            }

            return this.externalWaterSource != null && this.externalWaterSource.hasWater() ? this.externalWaterSource.getWaterAmount() : 0;
         }
      } else if (this.sprite.getProperties().Is(IsoFlagType.waterPiped) && GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getWaterShutModifier()) {
         return this.hasModData() && this.getModData().rawget("canBeWaterPiped") instanceof Boolean && (Boolean)this.getModData().rawget("canBeWaterPiped") ? 0 : 10000;
      } else {
         if (this.hasModData() && !this.getModData().isEmpty()) {
            KahluaTableIterator var1 = this.getModData().iterator();

            while(var1.advance()) {
               if (var1.getKey().toString().equals("waterAmount")) {
                  Object var2 = var1.getValue();
                  if (var2 instanceof Double) {
                     return (int)Math.max(0.0D, (Double)var2);
                  }

                  if (var2 instanceof String) {
                     return Math.max(0, Integer.parseInt((String)var2));
                  }

                  return 0;
               }
            }
         }

         if (!this.sprite.Properties.Is("waterAmount")) {
            return 0;
         } else {
            int var3 = Integer.parseInt(this.sprite.getProperties().Val("waterAmount"));
            return var3;
         }
      }
   }

   public int useWater(int var1) {
      if (this.sprite == null) {
         return 0;
      } else {
         int var2 = this.getWaterAmount();
         boolean var3 = false;
         int var4;
         if (var2 >= var1) {
            var4 = var1;
         } else {
            var4 = var2;
         }

         if (!this.usesExternalWaterSource) {
            if (this.sprite.getProperties().Is(IsoFlagType.water)) {
               return var4;
            }

            if (this.sprite.getProperties().Is(IsoFlagType.waterPiped) && GameTime.getInstance().getNightsSurvived() < SandboxOptions.instance.getWaterShutModifier()) {
               return var4;
            }
         }

         this.setWaterAmount(var2 - var4);
         return var4;
      }
   }

   public boolean hasWater() {
      return this.getWaterAmount() > 0;
   }

   public void setTaintedWater(boolean var1) {
      this.getModData().rawset("taintedWater", var1);
   }

   public boolean isTaintedWater() {
      if (this.hasModData()) {
         Object var1 = this.getModData().rawget("taintedWater");
         if (var1 instanceof Boolean) {
            return (Boolean)var1;
         }
      }

      return this.sprite != null && this.sprite.getProperties().Is(IsoFlagType.taintedWater);
   }

   public InventoryItem replaceItem(InventoryItem var1) {
      String var2 = null;
      InventoryItem var3 = null;
      if (var1 != null && var1 != null) {
         var2 = var1.getReplaceOnUseOn();
         if (var2.split("-")[0].trim().contains(this.getObjectName())) {
            var2 = var2.split("-")[1];
            if (!var2.contains(".")) {
               var2 = var1.getModule() + "." + var2;
            }
         } else if (var2.split("-")[0].trim().contains("WaterSource")) {
            var2 = var2.split("-")[1];
            if (!var2.contains(".")) {
               var2 = var1.getModule() + "." + var2;
            }
         } else {
            var2 = null;
         }
      }

      if (var2 != null && var1 != null) {
         var3 = var1.getContainer().AddItem(InventoryItemFactory.CreateItem(var2));
         if (var1.getContainer().getParent() instanceof IsoGameCharacter) {
            IsoGameCharacter var4 = (IsoGameCharacter)var1.getContainer().getParent();
            if (var4.getPrimaryHandItem() == var1) {
               var4.setPrimaryHandItem(var3);
            }

            if (var4.getSecondaryHandItem() == var1) {
               var4.setSecondaryHandItem(var3);
            }
         }

         var1.getContainer().Remove(var1);
      }

      return var3;
   }

   public void useItemOn(InventoryItem var1) {
      String var2 = null;
      if (var1 != null && var1 != null) {
         var2 = var1.getReplaceOnUseOn();
         if (var2.split("-")[0].trim().contains(this.getObjectName())) {
            var2 = var2.split("-")[1];
            if (!var2.contains(".")) {
               var2 = var1.getModule() + "." + var2;
            }
         } else if (var2.split("-")[0].trim().contains("WaterSource")) {
            var2 = var2.split("-")[1];
            if (!var2.contains(".")) {
               var2 = var1.getModule() + "." + var2;
            }

            this.useWater(10);
         } else {
            var2 = null;
         }
      }

      if (var2 != null && var1 != null) {
         InventoryItem var3 = var1.getContainer().AddItem(InventoryItemFactory.CreateItem(var2));
         var1.setUses(var1.getUses() - 1);
         if (var1.getUses() <= 0 && var1.getContainer() != null) {
            var1.getContainer().Items.remove(var1);
         }
      }

   }

   public float getX() {
      return (float)this.square.getX();
   }

   public float getY() {
      return (float)this.square.getY();
   }

   public float getZ() {
      return (float)this.square.getZ();
   }

   public boolean onMouseLeftClick(int var1, int var2) {
      if (IsoPlayer.instance.getBodyDamage().getOverallBodyHealth() > 0.0F) {
         float var3 = IsoPlayer.instance.DistTo(this.square.getX(), this.square.getY());
         String var4 = null;
         if (UIManager.getDragInventory() != null && this.square.getZ() == (int)IsoPlayer.getInstance().getZ() && UIManager.getDragInventory().getReplaceOnUseOn() != null) {
            var4 = UIManager.getDragInventory().getReplaceOnUseOn();
            if (var4.split("-")[0].trim().contains(this.getObjectName())) {
               var4 = var4.split("-")[1];
               if (!var4.contains(".")) {
                  var4 = UIManager.getDragInventory().getModule() + "." + var4;
               }
            } else if (var4.split("-")[0].trim().contains("WaterSource") && this.hasWater()) {
               var4 = var4.split("-")[1];
               if (!var4.contains(".")) {
                  var4 = UIManager.getDragInventory().getModule() + "." + var4;
               }

               this.useWater(10);
            } else {
               var4 = null;
            }
         }

         boolean var5 = false;
         int var7;
         String var8;
         IsoObject var9;
         IsoGridSquare var13;
         if (UIManager.getDragInventory() != null && this.square.getZ() == (int)IsoPlayer.getInstance().getZ() && UIManager.getDragInventory().getType().contains("SheetRope") && this.sprite != null && this.sprite.Properties.Is(IsoFlagType.windowN)) {
            var13 = this.square;
            var7 = 0;
            if (var13.getProperties().Is(IsoFlagType.solidfloor)) {
               return false;
            }

            while(var13 != null && UIManager.getDragInventory() != null) {
               var8 = "TileRope_1";
               if (var7 > 0) {
                  var8 = "TileRope_9";
               }

               var9 = new IsoObject(this.getCell(), var13, var8);
               var9.sprite.getProperties().Set(IsoFlagType.climbSheetN);
               if (var7 == 0) {
                  var9.sprite.getProperties().Set(IsoFlagType.climbSheetTopN);
                  var13.getProperties().Set(IsoFlagType.climbSheetTopN);
               }

               if (!var13.getProperties().Is(IsoFlagType.climbSheetN)) {
                  var13.DirtySlice();
                  UIManager.getDragInventory().Use();
               }

               var13.getProperties().Set(IsoFlagType.climbSheetN);
               var13.getObjects().add(var9);
               ++var7;
               if (var5) {
                  break;
               }

               var13 = this.getCell().getGridSquare(var13.getX(), var13.getY(), var13.getZ() - 1);
               if (var13 != null && var13.getProperties().Is(IsoFlagType.solidfloor)) {
                  var5 = true;
               }
            }
         } else if (UIManager.getDragInventory() != null && this.square.getZ() == (int)IsoPlayer.getInstance().getZ() && UIManager.getDragInventory().getType().contains("SheetRope") && this.sprite != null && this.sprite.Properties.Is(IsoFlagType.windowN)) {
            var13 = this.square;
            var7 = 0;
            if (var13.getProperties().Is(IsoFlagType.solidfloor)) {
               return false;
            }

            while(var13 != null && UIManager.getDragInventory() != null) {
               var8 = "TileRope_0";
               if (var7 > 0) {
                  var8 = "TileRope_8";
               }

               var9 = new IsoObject(this.getCell(), var13, var8);
               var9.sprite.getProperties().Set(IsoFlagType.climbSheetW);
               if (var7 == 0) {
                  var9.sprite.getProperties().Set(IsoFlagType.climbSheetTopW);
                  var13.getProperties().Set(IsoFlagType.climbSheetTopW);
               }

               if (!var13.getProperties().Is(IsoFlagType.climbSheetW)) {
                  UIManager.getDragInventory().Use();
                  var13.DirtySlice();
               }

               var13.getProperties().Set(IsoFlagType.climbSheetW);
               var13.getObjects().add(var9);
               ++var7;
               if (var5) {
                  break;
               }

               var13 = this.getCell().getGridSquare(var13.getX(), var13.getY(), var13.getZ() - 1);
               if (var13 != null && var13.getProperties().Is(IsoFlagType.solidfloor)) {
                  var5 = true;
               }
            }
         } else {
            if (this.rerouteMask != this && this.rerouteMask != null) {
               return this.rerouteMask.onMouseLeftClick(var1, var2);
            }

            if (UIManager.getDragInventory() != null && UIManager.getDragInventory().getType().contains("Sledgehammer") && (this.sprite.getProperties().Is(IsoFlagType.sledgesmash) || this.getType() == IsoObjectType.stairsTW || this.getType() == IsoObjectType.stairsMW || this.getType() == IsoObjectType.stairsBW || this.getType() == IsoObjectType.stairsTN || this.getType() == IsoObjectType.stairsMN || this.getType() == IsoObjectType.stairsBN || this.sprite.getProperties().Is(IsoFlagType.cutW) || this.sprite.getProperties().Is(IsoFlagType.cutN) || this.sprite.getProperties().Is(IsoFlagType.windowW) || this.sprite.getProperties().Is(IsoFlagType.windowN))) {
               if (IsoUtils.DistanceTo((float)this.square.getX(), (float)this.square.getY(), IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY()) <= 2.0F && (float)this.square.getZ() == IsoPlayer.getInstance().getZ()) {
                  Vector2 var12 = new Vector2((float)this.square.getX() + 0.5F, (float)this.square.getY() + 0.5F);
                  var12.x -= IsoPlayer.getInstance().getX();
                  var12.y -= IsoPlayer.getInstance().getY();
                  var12.normalize();
                  IsoPlayer.getInstance().DirectionFromVector(var12);
                  IsoPlayer.getInstance().AttemptAttack();
                  IsoPlayer.instance.setFakeAttack(true);
                  IsoPlayer.instance.setFakeAttackTarget(this);
               }
            } else if (this.container != null) {
               if (IsoUtils.DistanceTo((float)this.square.getX(), (float)this.square.getY(), IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY()) < 2.5F && (float)this.square.getZ() == IsoPlayer.getInstance().getZ()) {
               }
            } else if (var4 != null && UIManager.getDragInventory() != null) {
               InventoryItem var6 = UIManager.getDragInventory().getContainer().AddItem(InventoryItemFactory.CreateItem(var4));
               UIManager.getDragInventory().setUses(UIManager.getDragInventory().getUses() - 1);
               if (UIManager.getDragInventory().getUses() <= 0 && UIManager.getDragInventory().getContainer() != null) {
                  UIManager.getDragInventory().getContainer().Items.remove(UIManager.getDragInventory());
                  UIManager.getDragInventory().getContainer().dirty = true;
                  UIManager.setDragInventory((InventoryItem)null);
               }

               UIManager.setDragInventory(var6);
            }
         }

         if (this.sprite.getProperties().Is(IsoFlagType.solidfloor) && this.square.getZ() == (int)IsoPlayer.getInstance().getZ() && var3 <= 3.0F && UIManager.getDragInventory() != null) {
            int var14 = Mouse.getX();
            var7 = Mouse.getY();
            float var16 = IsoUtils.XToIso((float)(var14 - 30), (float)var7 - 356.0F - 5.0F, IsoPlayer.instance.getZ());
            float var15 = IsoUtils.YToIso((float)(var14 - 30), (float)var7 - 356.0F - 5.0F, IsoPlayer.instance.getZ());
            var16 -= (float)this.square.getX();
            var15 -= (float)this.square.getY();
            InventoryItem var10 = null;
            if (UIManager.getDragInventory().getUses() > 1) {
               var10 = InventoryItemFactory.CreateItem(UIManager.getDragInventory().getModule() + "." + UIManager.getDragInventory().getType());
            } else {
               var10 = UIManager.getDragInventory();
            }

            IsoWorldInventoryObject var11 = new IsoWorldInventoryObject(var10, this.square, var16, var15, 0.05F);
            var10.setWorldItem(var11);
            var11.item.setUses(1);
            this.square.getObjects().add(var11);
            if (UIManager.getDragInventory().getUses() > 1) {
               UIManager.getDragInventory().Use(true);
            } else {
               IsoPlayer.instance.getInventory().Remove(UIManager.getDragInventory());
               UIManager.setDragInventory((InventoryItem)null);
            }
         }
      }

      return false;
   }

   public PropertyContainer getProperties() {
      return this.sprite == null ? null : this.sprite.getProperties();
   }

   public void RemoveAttachedAnims() {
      if (this.AttachedAnimSprite != null) {
         for(int var1 = 0; var1 < this.AttachedAnimSprite.size(); ++var1) {
            ((IsoSpriteInstance)this.AttachedAnimSprite.get(var1)).Dispose();
         }

         this.AttachedAnimSprite.clear();
         if (this.AttachedAnimSpriteActual != null) {
            this.AttachedAnimSpriteActual.clear();
         }

      }
   }

   public void RemoveAttachedAnim(int var1) {
      if (this.AttachedAnimSprite != null) {
         if (var1 >= 0 && var1 < this.AttachedAnimSprite.size()) {
            ((IsoSpriteInstance)this.AttachedAnimSprite.get(var1)).Dispose();
            this.AttachedAnimSprite.remove(var1);
            if (this.AttachedAnimSpriteActual != null && var1 < this.AttachedAnimSpriteActual.size()) {
               this.AttachedAnimSpriteActual.remove(var1);
            }

         }
      }
   }

   public Vector2 getFacingPosition(Vector2 var1) {
      if (this.square == null) {
         return var1.set(0.0F, 0.0F);
      } else {
         if (this.getType() == IsoObjectType.wall) {
            if (this.getProperties().Is(IsoFlagType.collideN) && this.getProperties().Is(IsoFlagType.collideW)) {
               return var1.set(this.getX(), this.getY());
            }

            if (this.getProperties().Is(IsoFlagType.collideN)) {
               return var1.set(this.getX() + 0.5F, this.getY());
            }

            if (this.getProperties().Is(IsoFlagType.collideW)) {
               return var1.set(this.getX(), this.getY() + 0.5F);
            }
         }

         return var1.set(this.getX() + 0.5F, this.getY() + 0.5F);
      }
   }

   public Vector2 getFacingPositionAlt(Vector2 var1) {
      return this.getFacingPosition(var1);
   }

   public float getRenderYOffset() {
      return this.renderYOffset;
   }

   public void setRenderYOffset(float var1) {
      this.renderYOffset = var1;
      this.sx = 0;
   }

   public boolean isTableSurface() {
      PropertyContainer var1 = this.getProperties();
      return var1 != null ? var1.isTable() : false;
   }

   public boolean isTableTopObject() {
      PropertyContainer var1 = this.getProperties();
      return var1 != null ? var1.isTableTop() : false;
   }

   public boolean getIsSurfaceNormalOffset() {
      PropertyContainer var1 = this.getProperties();
      return var1 != null ? var1.isSurfaceOffset() : false;
   }

   public float getSurfaceNormalOffset() {
      float var1 = 0.0F;
      PropertyContainer var2 = this.getProperties();
      if (var2.isSurfaceOffset()) {
         var1 = (float)var2.getSurface();
      }

      return var1;
   }

   public float getSurfaceOffset() {
      float var1 = 0.0F;
      if (this.isTableSurface()) {
         PropertyContainer var2 = this.getProperties();
         if (var2 != null) {
            var1 = (float)var2.getSurface();
         }
      }

      return var1;
   }

   public boolean isStairsNorth() {
      return this.getType() == IsoObjectType.stairsTN || this.getType() == IsoObjectType.stairsMN || this.getType() == IsoObjectType.stairsBN;
   }

   public boolean isStairsWest() {
      return this.getType() == IsoObjectType.stairsTW || this.getType() == IsoObjectType.stairsMW || this.getType() == IsoObjectType.stairsBW;
   }

   public boolean isStairsObject() {
      return this.isStairsNorth() || this.isStairsWest();
   }

   public void render(float var1, float var2, float var3, ColorInfo var4, boolean var5) {
      this.render(var1, var2, var3, var4, var5, false);
   }

   public void render(float var1, float var2, float var3, ColorInfo var4, boolean var5, boolean var6) {
      stCol.r = var4.r;
      stCol.g = var4.g;
      stCol.b = var4.b;
      stCol.a = var4.a;
      if (this.isHighlighted()) {
         stCol.r = this.getHighlightColor().r;
         stCol.g = this.getHighlightColor().g;
         stCol.b = this.getHighlightColor().b;
         stCol.a = this.getHighlightColor().a;
         if (this.isBlink()) {
            stCol.a = this.blinkAlpha;
            if (!this.blinkAlphaIncrease) {
               this.blinkAlpha -= 0.1F * (30.0F / (float)PerformanceSettings.LockFPS);
               if (this.blinkAlpha < 0.15F) {
                  this.blinkAlpha = 0.15F;
                  this.blinkAlphaIncrease = true;
               }
            } else {
               this.blinkAlpha += 0.1F * (30.0F / (float)PerformanceSettings.LockFPS);
               if (this.blinkAlpha > 1.0F) {
                  this.blinkAlpha = 1.0F;
                  this.blinkAlphaIncrease = false;
               }
            }
         } else {
            stCol.a = 1.0F;
         }
      }

      if (this.customColor != null) {
         stCol.r = this.customColor.r;
         stCol.g = this.customColor.g;
         stCol.b = this.customColor.b;
         stCol.a = this.customColor.a;
      }

      float var8;
      float var9;
      float var10;
      if (this.sprite != null && this.sprite.forceAmbient) {
         GameTime var7 = GameTime.getInstance();
         var8 = rmod * this.tintr;
         var9 = gmod * this.tintg;
         var10 = bmod * this.tintb;
         if (!this.isHighlighted()) {
            stCol.r = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * var8;
            stCol.g = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * var9;
            stCol.b = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * var10;
         }
      }

      int var17 = IsoPlayer.getPlayerIndex();
      var8 = IsoCamera.frameState.CamCharacterX;
      var9 = IsoCamera.frameState.CamCharacterY;
      var10 = IsoCamera.frameState.CamCharacterZ;
      if (IsoWorld.instance.CurrentCell.IsPlayerWindowPeeking(var17)) {
         IsoPlayer var11 = IsoPlayer.players[var17];
         IsoDirections var12 = IsoDirections.cardinalFromAngle(var11.angle);
         if (var12 == IsoDirections.N) {
            var9 = (float)((double)var9 - 1.0D);
         } else if (var12 == IsoDirections.W) {
            var8 = (float)((double)var8 - 1.0D);
         }
      }

      if (this == IsoCamera.CamCharacter && !IsoPlayer.DemoMode) {
         this.targetAlpha[var17] = 1.0F;
         this.alpha[var17] = 1.0F;
      }

      lastRenderedRendered = lastRendered;
      lastRendered = this;
      float var19;
      if (this.sprite != null) {
         if (this.sprite.getProperties().Is(IsoFlagType.invisible)) {
            return;
         }

         if (!(this instanceof IsoPhysicsObject) && IsoCamera.CamCharacter != null) {
            if (!(this instanceof IsoWindow) && this.sprite.getType() != IsoObjectType.doorW && this.sprite.getType() != IsoObjectType.doorN && ((float)this.square.getX() > var8 || (float)this.square.getY() > var9) && (int)var10 <= this.square.getZ()) {
               boolean var18 = false;
               var19 = 0.2F;
               boolean var13 = (this.sprite.cutW || this.sprite.getProperties().Is(IsoFlagType.doorW)) && (float)this.square.getX() > var8;
               boolean var14 = (this.sprite.cutN || this.sprite.getProperties().Is(IsoFlagType.doorN)) && (float)this.square.getY() > var9;
               if (var13 && this.square.getProperties().Is(IsoFlagType.WallSE) && (float)this.square.getY() <= var9) {
                  var13 = false;
               }

               if (!var13 && !var14) {
                  boolean var15 = this.getType() == IsoObjectType.WestRoofB || this.getType() == IsoObjectType.WestRoofM || this.getType() == IsoObjectType.WestRoofT;
                  boolean var16 = var15 && (int)var10 == this.square.getZ() && this.square.getBuilding() == null;
                  if (var16 && IsoWorld.instance.CurrentCell.CanBuildingSquareOccludePlayer(this.square, var17)) {
                     var18 = true;
                     var19 = 0.05F;
                  }
               } else {
                  var18 = true;
               }

               if (this.sprite.getProperties().Is(IsoFlagType.halfheight)) {
                  var18 = false;
               }

               if (var18) {
                  if (var14 && this.sprite.getProperties().Is(IsoFlagType.HoppableN)) {
                     var19 = 0.25F;
                  }

                  if (var13 && this.sprite.getProperties().Is(IsoFlagType.HoppableW)) {
                     var19 = 0.25F;
                  }

                  this.targetAlpha[var17] = var19;
                  LowLightingQualityHack = true;
                  if (this.rerouteMask == null && !(this instanceof IsoThumpable) && !IsoWindowFrame.isWindowFrame(this) && (UIManager.getDragInventory() == null || !UIManager.DragInventory.getType().contains("Sledgehammer") && !UIManager.DragInventory.getType().contains("Axe")) && !this.sprite.getProperties().Is(IsoFlagType.doorN) && !this.sprite.getProperties().Is(IsoFlagType.doorW)) {
                     this.NoPicking = true;
                  } else {
                     this.NoPicking = false;
                  }
               } else {
                  this.NoPicking = false;
               }
            } else {
               this.NoPicking = false;
            }
         }
      }

      float var20 = 2.0F;
      var19 = 1.5F;
      if (this.square != null && this.square.room != null) {
         var20 *= 2.0F;
      }

      if (this instanceof IsoGameCharacter && !((IsoGameCharacter)this).SpottedSinceAlphaZero[var17]) {
         this.targetAlpha[var17] = 0.0F;
      }

      if (this == IsoCamera.CamCharacter) {
         this.targetAlpha[var17] = 1.0F;
      }

      if (this.bNeverDoneAlpha) {
         this.alpha[var17] = this.targetAlpha[var17];
         this.bNeverDoneAlpha = false;
      }

      float[] var10000;
      if (this.alpha[var17] < this.targetAlpha[var17]) {
         var10000 = this.alpha;
         var10000[var17] += alphaStep * var20;
         if (this.alpha[var17] > this.targetAlpha[var17]) {
            this.alpha[var17] = this.targetAlpha[var17];
         }
      } else if (this.alpha[var17] > this.targetAlpha[var17]) {
         var10000 = this.alpha;
         var10000[var17] -= alphaStep / var19;
         if (this.alpha[var17] < this.targetAlpha[var17]) {
            this.alpha[var17] = this.targetAlpha[var17];
         }
      }

      if (this.alpha[var17] < 0.0F) {
         this.alpha[var17] = 0.0F;
      }

      if (this.alpha[var17] > 1.0F) {
         this.alpha[var17] = 1.0F;
      }

      if (this.sprite != null) {
         if (this.getType() == IsoObjectType.wall) {
         }

         this.sprite.render(this, var1, var2, var3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, stCol, !this.isBlink());
      }

      if (this.isHighlighted()) {
         var4 = stCol;
      }

      if (this.getOverlaySprite() != null) {
         ColorInfo var21 = stCol2;
         var21.r = var4.r;
         var21.g = var4.g;
         var21.b = var4.b;
         var21.a = var4.a;
         if (this.overlaySpriteColor != null) {
            var21.r *= this.overlaySpriteColor.r;
            var21.g *= this.overlaySpriteColor.g;
            var21.b *= this.overlaySpriteColor.b;
            var21.a *= this.overlaySpriteColor.a;
         }

         this.getOverlaySprite().render(this, var1, var2, var3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, var21);
      }

      if (var5) {
         int var22;
         int var23;
         if (this.AttachedAnimSprite != null) {
            var22 = this.AttachedAnimSprite.size();

            for(var23 = 0; var23 < var22; ++var23) {
               IsoSpriteInstance var24 = (IsoSpriteInstance)this.AttachedAnimSprite.get(var23);
               if (!var6 || !var24.parentSprite.Properties.Is(IsoFlagType.NoWallLighting)) {
                  float var26 = var4.a;
                  var4.a = var24.alpha;
                  var24.render(this, var1, var2, var3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, var4);
                  var4.a = var26;
                  var24.update();
               }
            }
         }

         if (this.Children != null) {
            var22 = this.Children.size();

            for(var23 = 0; var23 < var22; ++var23) {
               IsoObject var25 = (IsoObject)this.Children.get(var23);
               if (var25 instanceof IsoMovingObject) {
                  var25.render(((IsoMovingObject)var25).x, ((IsoMovingObject)var25).y, ((IsoMovingObject)var25).z, var4, var5);
               }
            }
         }

         if (this.wallBloodSplats != null) {
            if (Core.OptionBloodDecals == 0) {
               return;
            }

            for(var22 = 0; var22 < this.wallBloodSplats.size(); ++var22) {
               ((IsoWallBloodSplat)this.wallBloodSplats.get(var22)).render(var1, var2, var3, var4);
            }
         }
      }

   }

   public void renderFxMask(float var1, float var2, float var3, boolean var4) {
      if (this.sprite != null) {
         if (this.getType() == IsoObjectType.wall) {
         }

         this.sprite.render(this, var1, var2, var3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, colFxMask, false);
      }

      if (this.getOverlaySprite() != null) {
         this.getOverlaySprite().render(this, var1, var2, var3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, colFxMask, false);
      }

      if (var4) {
         int var5;
         int var6;
         if (this.AttachedAnimSprite != null) {
            var5 = this.AttachedAnimSprite.size();

            for(var6 = 0; var6 < var5; ++var6) {
               IsoSpriteInstance var7 = (IsoSpriteInstance)this.AttachedAnimSprite.get(var6);
               var7.render(this, var1, var2, var3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, colFxMask);
            }
         }

         if (this.Children != null) {
            var5 = this.Children.size();

            for(var6 = 0; var6 < var5; ++var6) {
               IsoObject var8 = (IsoObject)this.Children.get(var6);
               if (var8 instanceof IsoMovingObject) {
                  var8.render(((IsoMovingObject)var8).x, ((IsoMovingObject)var8).y, ((IsoMovingObject)var8).z, colFxMask, var4);
               }
            }
         }

         if (this.wallBloodSplats != null) {
            if (Core.OptionBloodDecals == 0) {
               return;
            }

            for(var5 = 0; var5 < this.wallBloodSplats.size(); ++var5) {
               ((IsoWallBloodSplat)this.wallBloodSplats.get(var5)).render(var1, var2, var3, colFxMask);
            }
         }
      }

   }

   public void renderObjectPicker(float var1, float var2, float var3, ColorInfo var4) {
      if (this.sprite != null) {
         if (!this.sprite.getProperties().Is(IsoFlagType.invisible)) {
            if (UIManager.getDragInventory() == null || !"Barricade".equals(UIManager.getDragInventory().getType()) || this.sprite.Properties.Is(IsoFlagType.solidfloor)) {
               this.sprite.renderObjectPicker(this.sprite.def, this, var1, var2, var3, this.dir, this.offsetX, this.offsetY + this.renderYOffset * (float)Core.TileScale, var4);
            }
         }
      }
   }

   public boolean TestPathfindCollide(IsoMovingObject var1, IsoGridSquare var2, IsoGridSquare var3) {
      return false;
   }

   public boolean TestCollide(IsoMovingObject var1, IsoGridSquare var2, IsoGridSquare var3) {
      return false;
   }

   public IsoObject.VisionResult TestVision(IsoGridSquare var1, IsoGridSquare var2) {
      return IsoObject.VisionResult.Unblocked;
   }

   Texture getCurrentFrameTex() {
      if (this.sprite == null) {
         return null;
      } else if (this.sprite.CurrentAnim == null) {
         return null;
      } else {
         return (float)this.sprite.CurrentAnim.Frames.size() <= this.sprite.def.Frame ? null : ((IsoDirectionFrame)this.sprite.CurrentAnim.Frames.get((int)this.sprite.def.Frame)).getTexture(this.dir);
      }
   }

   public boolean isMaskClicked(int var1, int var2) {
      return this.sprite == null ? false : this.sprite.isMaskClicked(this.dir, var1, var2);
   }

   public boolean isMaskClicked(int var1, int var2, boolean var3) {
      if (this.sprite == null) {
         return false;
      } else {
         return this.overlaySprite != null && this.overlaySprite.isMaskClicked(this.dir, var1, var2, var3) ? true : this.sprite.isMaskClicked(this.dir, var1, var2, var3);
      }
   }

   public float getMaskClickedY(int var1, int var2, boolean var3) {
      return this.sprite == null ? 10000.0F : this.sprite.getMaskClickedY(this.dir, var1, var2, var3);
   }

   public void setCustomColor(ColorInfo var1) {
      this.customColor = var1;
   }

   public ColorInfo getCustomColor() {
      return this.customColor;
   }

   public void setCustomColor(float var1, float var2, float var3, float var4) {
      ColorInfo var5 = new ColorInfo(var1, var2, var3, var4);
      this.customColor = var5;
   }

   public void loadFromRemoteBuffer(ByteBuffer var1) {
      this.loadFromRemoteBuffer(var1, true);
   }

   public void loadFromRemoteBuffer(ByteBuffer var1, boolean var2) {
      try {
         this.load(var1, 143);
      } catch (IOException var12) {
         var12.printStackTrace();
         return;
      }

      if (this instanceof IsoWorldInventoryObject && ((IsoWorldInventoryObject)this).getItem() == null) {
         DebugLog.log("loadFromRemoteBuffer() failed due to an unknown item type");
      } else {
         int var3 = var1.getInt();
         int var4 = var1.getInt();
         int var5 = var1.getInt();
         int var6 = var1.getInt();
         boolean var7 = var1.get() != 0;
         boolean var8 = var1.get() != 0;
         IsoWorld.instance.CurrentCell.EnsureSurroundNotNull(var3, var4, var5);
         this.square = IsoWorld.instance.CurrentCell.getGridSquare(var3, var4, var5);
         if (this.square != null) {
            if (GameServer.bServer && !(this instanceof IsoWorldInventoryObject)) {
               IsoRegion.setPreviousFlags(this.square);
            }

            if (var7) {
               this.square.getSpecialObjects().add(this);
            }

            if (var8 && this instanceof IsoWorldInventoryObject) {
               this.square.getWorldObjects().add((IsoWorldInventoryObject)this);
               this.square.chunk.recalcHashCodeObjects();
            }

            if (var2) {
               if (var6 != -1 && var6 >= 0 && var6 <= this.square.getObjects().size()) {
                  this.square.getObjects().add(var6, this);
               } else {
                  this.square.getObjects().add(this);
               }
            }

            int var9;
            for(var9 = 0; var9 < this.getContainerCount(); ++var9) {
               ItemContainer var10 = this.getContainerByIndex(var9);
               var10.parent = this;
               var10.parent.square = this.square;
               var10.SourceGrid = this.square;
            }

            for(var9 = -1; var9 <= 1; ++var9) {
               for(int var13 = -1; var13 <= 1; ++var13) {
                  IsoGridSquare var11 = IsoWorld.instance.CurrentCell.getGridSquare(var9 + var3, var13 + var4, var5);
                  if (var11 != null) {
                     var11.RecalcAllWithNeighbours(true);
                  }
               }
            }

         }
      }
   }

   public void addToWorld() {
      for(int var1 = 0; var1 < this.getContainerCount(); ++var1) {
         ItemContainer var2 = this.getContainerByIndex(var1);
         var2.addItemsToProcessItems();
      }

      if (!GameServer.bServer) {
         ItemContainer var3 = this.getContainerByEitherType("fridge", "freezer");
         if (var3 != null && var3.isPowered()) {
            this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (float)((int)this.getZ()));
            this.emitter.playAmbientLoopedImpl("FridgeHum");
            if (!IsoWorld.instance.getCell().getProcessIsoObjects().contains(this)) {
               IsoWorld.instance.getCell().getProcessIsoObjects().add(this);
            }
         }

         if ((this.sprite == null || this.sprite.name == null || !this.sprite.name.startsWith("blends_natural_02")) && this.hasWater() && Rand.Next(5) == 0) {
            AmbientStreamManager.instance.addAmbientEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ(), "WaterDrip");
         }

         if (this instanceof IsoWindow && Rand.Next(8) == 0) {
            AmbientStreamManager.instance.addAmbientEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ(), "WindowWind");
         }

         if (this instanceof IsoWindow && Rand.Next(8) == 0) {
            AmbientStreamManager.instance.addAmbientEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ(), "WindowRattle");
         }

         if (this instanceof IsoDoor && Rand.Next(6) == 0) {
            AmbientStreamManager.instance.addAmbientEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ(), "WoodDoorCreaks");
         }

         if (this instanceof IsoTree && Rand.Next(80) == 0) {
            AmbientStreamManager.instance.addDaytimeAmbientEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ(), "BirdInTree");
         }

         this.checkMoveWithWind();
      }
   }

   public void removeFromWorld() {
      IsoCell var1 = this.getCell();
      var1.getProcessIsoObjects().remove(this);
      var1.getStaticUpdaterObjectList().remove(this);

      for(int var2 = 0; var2 < this.getContainerCount(); ++var2) {
         ItemContainer var3 = this.getContainerByIndex(var2);
         var3.removeItemsFromProcessItems();
      }

      if (this.emitter != null) {
         this.emitter.stopAll();
         this.emitter = null;
      }

   }

   public void reuseGridSquare() {
   }

   public void removeFromSquare() {
      if (this.square != null) {
         this.square.getObjects().remove(this);
         this.square.getSpecialObjects().remove(this);
      }

   }

   public void transmitCustomColor() {
      if (GameClient.bClient && this.getCustomColor() != null) {
         GameClient.instance.sendCustomColor(this);
      }

   }

   public void transmitCompleteItemToClients() {
      if (GameServer.bServer) {
         if (GameServer.udpEngine == null) {
            return;
         }

         int var1;
         UdpConnection var2;
         if (SystemDisabler.doWorldSyncEnable) {
            for(var1 = 0; var1 < GameServer.udpEngine.connections.size(); ++var1) {
               var2 = (UdpConnection)GameServer.udpEngine.connections.get(var1);
               if (var2.ReleventTo((float)this.square.x, (float)this.square.y)) {
                  GameServer.SyncObjectChunkHashes(this.square.chunk, var2);
               }
            }

            return;
         }

         for(var1 = 0; var1 < GameServer.udpEngine.connections.size(); ++var1) {
            var2 = (UdpConnection)GameServer.udpEngine.connections.get(var1);
            if (var2 != null && this.square != null && var2.ReleventTo((float)this.square.x, (float)this.square.y)) {
               ByteBufferWriter var3 = var2.startPacket();
               PacketTypes.doPacket((short)17, var3);
               this.writeToRemoteBuffer(var3);
               var2.endPacketImmediate();
            }
         }
      }

   }

   public void transmitUpdatedSpriteToClients(UdpConnection var1) {
      if (GameServer.bServer) {
         this.revisionUp();

         for(int var2 = 0; var2 < GameServer.udpEngine.connections.size(); ++var2) {
            UdpConnection var3 = (UdpConnection)GameServer.udpEngine.connections.get(var2);
            if (var3 != null && this.square != null && (var1 != null && var3.getConnectedGUID() != var1.getConnectedGUID() || var1 == null) && var3.ReleventTo((float)this.square.x, (float)this.square.y)) {
               ByteBufferWriter var4 = var3.startPacket();
               PacketTypes.doPacket((short)76, var4);
               var4.putInt(this.getSprite().ID);
               GameWindow.WriteStringUTF(var4.bb, this.spriteName);
               var4.putInt(this.getSquare().getX());
               var4.putInt(this.getSquare().getY());
               var4.putInt(this.getSquare().getZ());
               var4.putInt(this.getSquare().getObjects().indexOf(this));
               if (this.AttachedAnimSpriteActual != null) {
                  var4.putByte((byte)this.AttachedAnimSpriteActual.size());
                  Iterator var5 = this.AttachedAnimSpriteActual.iterator();

                  while(var5.hasNext()) {
                     IsoSprite var6 = (IsoSprite)var5.next();
                     var4.putInt(var6.ID);
                  }
               } else {
                  var4.putByte((byte)0);
               }

               var3.endPacket();
            }
         }
      }

   }

   public void transmitUpdatedSpriteToClients() {
      this.transmitUpdatedSpriteToClients((UdpConnection)null);
   }

   public void sendObjectChange(String var1) {
      if (GameServer.bServer) {
         GameServer.sendObjectChange(this, var1, (KahluaTable)null);
      } else if (GameClient.bClient) {
         DebugLog.log("sendObjectChange() can only be called on the server");
      } else {
         SinglePlayerServer.sendObjectChange(this, var1, (KahluaTable)null);
      }

   }

   public void sendObjectChange(String var1, KahluaTable var2) {
      if (GameServer.bServer) {
         GameServer.sendObjectChange(this, var1, var2);
      } else if (GameClient.bClient) {
         DebugLog.log("sendObjectChange() can only be called on the server");
      } else {
         SinglePlayerServer.sendObjectChange(this, var1, var2);
      }

   }

   public void sendObjectChange(String var1, Object... var2) {
      if (GameServer.bServer) {
         GameServer.sendObjectChange(this, var1, var2);
      } else if (GameClient.bClient) {
         DebugLog.log("sendObjectChange() can only be called on the server");
      } else {
         SinglePlayerServer.sendObjectChange(this, var1, var2);
      }

   }

   public void saveChange(String var1, KahluaTable var2, ByteBuffer var3) {
      if ("containers".equals(var1)) {
         var3.put((byte)this.getContainerCount());

         for(int var4 = 0; var4 < this.getContainerCount(); ++var4) {
            ItemContainer var5 = this.getContainerByIndex(var4);

            try {
               var5.save(var3, false);
            } catch (Throwable var8) {
               ExceptionLogger.logException(var8);
            }
         }
      } else if ("container.customTemperature".equals(var1)) {
         if (this.getContainer() != null) {
            var3.putFloat(this.getContainer().getCustomTemperature());
         } else {
            var3.putFloat(0.0F);
         }
      } else if ("name".equals(var1)) {
         GameWindow.WriteStringUTF(var3, this.getName());
      } else if ("replaceWith".equals(var1)) {
         if (var2 != null && var2.rawget("object") instanceof IsoObject) {
            IsoObject var9 = (IsoObject)var2.rawget("object");

            try {
               var9.save(var3);
            } catch (IOException var7) {
               var7.printStackTrace();
            }
         }
      } else if ("usesExternalWaterSource".equals(var1)) {
         boolean var10 = var2 != null && Boolean.TRUE.equals(var2.rawget("value"));
         var3.put((byte)(var10 ? 1 : 0));
      } else if ("sprite".equals(var1)) {
         if (this.sprite == null) {
            var3.putInt(0);
         } else {
            var3.putInt(this.sprite.ID);
            GameWindow.WriteStringUTF(var3, this.spriteName);
         }
      }

   }

   public void loadChange(String var1, ByteBuffer var2) {
      int var3;
      if ("containers".equals(var1)) {
         for(var3 = 0; var3 < this.getContainerCount(); ++var3) {
            ItemContainer var4 = this.getContainerByIndex(var3);
            var4.removeItemsFromProcessItems();
            var4.removeAllItems();
         }

         this.removeAllContainers();
         byte var9 = var2.get();

         for(int var11 = 0; var11 < var9; ++var11) {
            ItemContainer var5 = new ItemContainer();
            var5.ID = 0;
            var5.parent = this;
            var5.SourceGrid = this.square;

            try {
               var5.load(var2, 143, false);
               if (var11 == 0) {
                  if (this instanceof IsoDeadBody) {
                     var5.Capacity = 8;
                  }

                  this.container = var5;
               } else {
                  this.addSecondaryContainer(var5);
               }
            } catch (Throwable var7) {
               ExceptionLogger.logException(var7);
            }
         }
      } else if ("container.customTemperature".equals(var1)) {
         float var10 = var2.getFloat();
         if (this.getContainer() != null) {
            this.getContainer().setCustomTemperature(var10);
         }
      } else if ("name".equals(var1)) {
         String var12 = GameWindow.ReadStringUTF(var2);
         this.setName(var12);
      } else if ("replaceWith".equals(var1)) {
         try {
            var3 = this.getObjectIndex();
            if (var3 >= 0) {
               IsoObject var13 = factoryFromFileInput(this.getCell(), var2);
               var13.load(var2, 143);
               var13.setSquare(this.square);
               this.square.getObjects().set(var3, var13);
               this.square.getSpecialObjects().remove(this);
               this.square.RecalcAllWithNeighbours(true);
               if (this.getContainerCount() > 0) {
                  for(int var14 = 0; var14 < this.getContainerCount(); ++var14) {
                     ItemContainer var6 = this.getContainerByIndex(var14);
                     var6.removeItemsFromProcessItems();
                  }

                  LuaEventManager.triggerEvent("OnContainerUpdate");
               }
            }
         } catch (IOException var8) {
            var8.printStackTrace();
         }
      } else if ("usesExternalWaterSource".equals(var1)) {
         this.usesExternalWaterSource = var2.get() == 1;
      } else if ("sprite".equals(var1)) {
         var3 = var2.getInt();
         if (var3 == 0) {
            this.sprite = null;
            this.spriteName = null;
            this.tile = null;
         } else {
            this.spriteName = GameWindow.ReadString(var2);
            this.sprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, var3);
            if (this.sprite == null) {
               this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
               this.sprite.LoadFramesNoDirPageSimple(this.spriteName);
            }
         }
      }

      this.checkMoveWithWind();
   }

   public void transmitUpdatedSpriteToServer() {
      if (GameClient.bClient) {
         ByteBufferWriter var1 = GameClient.connection.startPacket();
         PacketTypes.doPacket((short)76, var1);
         var1.putInt(this.getSprite().ID);
         GameWindow.WriteStringUTF(var1.bb, this.spriteName);
         var1.putInt(this.getSquare().getX());
         var1.putInt(this.getSquare().getY());
         var1.putInt(this.getSquare().getZ());
         var1.putInt(this.getSquare().getObjects().indexOf(this));
         if (this.AttachedAnimSpriteActual != null) {
            var1.putByte((byte)this.AttachedAnimSpriteActual.size());
            Iterator var2 = this.AttachedAnimSpriteActual.iterator();

            while(var2.hasNext()) {
               IsoSprite var3 = (IsoSprite)var2.next();
               var1.putInt(var3.ID);
            }
         } else {
            var1.putByte((byte)0);
         }

         GameClient.connection.endPacket();
      }

   }

   public void transmitCompleteItemToServer() {
      if (GameClient.bClient) {
         this.square.clientModify();
         ByteBufferWriter var1 = GameClient.connection.startPacket();
         PacketTypes.doPacket((short)17, var1);
         this.writeToRemoteBuffer(var1);
         GameClient.connection.endPacketImmediate();
      }

   }

   public void transmitModData() {
      if (this.square != null) {
         if (GameClient.bClient) {
            this.square.clientModify();
            ByteBufferWriter var1 = GameClient.connection.startPacket();
            PacketTypes.doPacket((short)58, var1);
            var1.putInt(this.getSquare().getX());
            var1.putInt(this.getSquare().getY());
            var1.putInt(this.getSquare().getZ());
            var1.putInt(this.getSquare().getObjects().indexOf(this));
            if (this.getModData().isEmpty()) {
               var1.putByte((byte)0);
            } else {
               var1.putByte((byte)1);

               try {
                  this.getModData().save(var1.bb);
               } catch (IOException var3) {
                  var3.printStackTrace();
               }
            }

            GameClient.connection.endPacketImmediate();
         } else if (GameServer.bServer) {
            GameServer.sendObjectModData(this);
            this.square.revisionUp();
         }

      }
   }

   public void writeToRemoteBuffer(ByteBufferWriter var1) {
      try {
         this.save(var1.bb);
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      var1.putInt(this.square.getX());
      var1.putInt(this.square.getY());
      var1.putInt(this.square.getZ());
      var1.putInt(this.getObjectIndex());
      var1.putBoolean(this.square.getSpecialObjects().contains(this));
      var1.putBoolean(this.square.getWorldObjects().contains(this));
   }

   public int getObjectIndex() {
      return this.square == null ? -1 : this.square.getObjects().indexOf(this);
   }

   public int getMovingObjectIndex() {
      return this.square == null ? -1 : this.square.getMovingObjects().indexOf(this);
   }

   public int getSpecialObjectIndex() {
      return this.square == null ? -1 : this.square.getSpecialObjects().indexOf(this);
   }

   public int getStaticMovingObjectIndex() {
      return this.square == null ? -1 : this.square.getStaticMovingObjects().indexOf(this);
   }

   public int getWorldObjectIndex() {
      return this.square == null ? -1 : this.square.getWorldObjects().indexOf(this);
   }

   public IsoSprite getOverlaySprite() {
      return this.overlaySprite;
   }

   public void setOverlaySpriteColor(float var1, float var2, float var3, float var4) {
      this.overlaySpriteColor = new ColorInfo(var1, var2, var3, var4);
   }

   public ColorInfo getOverlaySpriteColor() {
      return this.overlaySpriteColor;
   }

   public void setOverlaySprite(String var1) {
      this.setOverlaySprite(var1, -1.0F, -1.0F, -1.0F, -1.0F, true);
   }

   public void setOverlaySprite(String var1, float var2, float var3, float var4, float var5) {
      this.setOverlaySprite(var1, var2, var3, var4, var5, true);
   }

   public boolean setOverlaySprite(String var1, float var2, float var3, float var4, float var5, boolean var6) {
      if (var1 != null && !var1.isEmpty()) {
         boolean var7;
         if (!(var2 > -1.0F)) {
            var7 = this.overlaySpriteColor == null;
         } else {
            var7 = this.overlaySpriteColor != null && this.overlaySpriteColor.r == var2 && this.overlaySpriteColor.g == var3 && this.overlaySpriteColor.b == var4 && this.overlaySpriteColor.a == var5;
         }

         if (this.overlaySprite != null && var1.equals(this.overlaySprite.name) && var7) {
            return false;
         }

         this.overlaySprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(var1);
         this.overlaySprite.name = var1;
      } else {
         if (this.overlaySprite == null) {
            return false;
         }

         this.overlaySprite = null;
         var1 = "";
      }

      if (var2 > -1.0F) {
         this.overlaySpriteColor = new ColorInfo(var2, var3, var4, var5);
      } else {
         this.overlaySpriteColor = null;
      }

      if (!var6) {
         return true;
      } else {
         if (GameServer.bServer) {
            GameServer.updateOverlayForClients(this, var1, var2, var3, var4, var5, (UdpConnection)null);
         } else if (GameClient.bClient) {
            ByteBufferWriter var8 = GameClient.connection.startPacket();
            PacketTypes.doPacket((short)90, var8);
            GameWindow.WriteStringUTF(var8.bb, var1);
            var8.putInt(this.getSquare().getX());
            var8.putInt(this.getSquare().getY());
            var8.putInt(this.getSquare().getZ());
            var8.putFloat(var2);
            var8.putFloat(var3);
            var8.putFloat(var4);
            var8.putFloat(var5);
            var8.putInt(this.getSquare().getObjects().indexOf(this));
            GameClient.connection.endPacketImmediate();
         }

         return true;
      }
   }

   public boolean haveSpecialTooltip() {
      return this.specialTooltip;
   }

   public void setSpecialTooltip(boolean var1) {
      this.specialTooltip = var1;
   }

   public int getKeyId() {
      return this.keyId;
   }

   public void setKeyId(int var1) {
      this.keyId = var1;
   }

   public boolean isHighlighted() {
      return (this.highlightFlags & 1) != 0;
   }

   public void setHighlighted(boolean var1) {
      this.setHighlighted(var1, true);
   }

   public void setHighlighted(boolean var1, boolean var2) {
      if (var1) {
         this.highlightFlags = (byte)(this.highlightFlags | 1);
      } else {
         this.highlightFlags &= -2;
      }

      if (var2) {
         this.highlightFlags = (byte)(this.highlightFlags | 2);
      } else {
         this.highlightFlags &= -3;
      }

   }

   public ColorInfo getHighlightColor() {
      return this.highlightColor;
   }

   public void setHighlightColor(ColorInfo var1) {
      this.highlightColor = var1;
   }

   public void setHighlightColor(float var1, float var2, float var3, float var4) {
      if (this.highlightColor == null) {
         this.highlightColor = new ColorInfo(var1, var2, var3, var4);
      } else {
         this.highlightColor.set(var1, var2, var3, var4);
      }

   }

   public boolean isBlink() {
      return (this.highlightFlags & 4) != 0;
   }

   public void setBlink(boolean var1) {
      if (var1) {
         this.highlightFlags = (byte)(this.highlightFlags | 4);
      } else {
         this.highlightFlags &= -5;
      }

   }

   public void checkHaveElectricity() {
      if (!GameServer.bServer) {
         ItemContainer var1 = this.getContainerByEitherType("fridge", "freezer");
         if (var1 != null) {
            if (var1.isPowered()) {
               if (this.emitter == null) {
                  this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (float)((int)this.getZ()));
                  this.emitter.playAmbientLoopedImpl("FridgeHum");
                  if (!IsoWorld.instance.getCell().getProcessIsoObjects().contains(this)) {
                     IsoWorld.instance.getCell().getProcessIsoObjects().add(this);
                  }
               }
            } else if (this.emitter != null) {
               this.emitter.stopAll();
               this.emitter = null;
            }
         }

      }
   }

   public int getContainerCount() {
      int var1 = this.container == null ? 0 : 1;
      int var2 = this.secondaryContainers == null ? 0 : this.secondaryContainers.size();
      return var1 + var2;
   }

   public ItemContainer getContainerByIndex(int var1) {
      if (this.container != null) {
         if (var1 == 0) {
            return this.container;
         } else if (this.secondaryContainers == null) {
            return null;
         } else {
            return var1 >= 1 && var1 <= this.secondaryContainers.size() ? (ItemContainer)this.secondaryContainers.get(var1 - 1) : null;
         }
      } else if (this.secondaryContainers == null) {
         return null;
      } else {
         return var1 >= 0 && var1 < this.secondaryContainers.size() ? (ItemContainer)this.secondaryContainers.get(var1) : null;
      }
   }

   public ItemContainer getContainerByType(String var1) {
      for(int var2 = 0; var2 < this.getContainerCount(); ++var2) {
         ItemContainer var3 = this.getContainerByIndex(var2);
         if (var3.getType().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public ItemContainer getContainerByEitherType(String var1, String var2) {
      for(int var3 = 0; var3 < this.getContainerCount(); ++var3) {
         ItemContainer var4 = this.getContainerByIndex(var3);
         if (var4.getType().equals(var1) || var4.getType().equals(var2)) {
            return var4;
         }
      }

      return null;
   }

   public void addSecondaryContainer(ItemContainer var1) {
      if (this.secondaryContainers == null) {
         this.secondaryContainers = new ArrayList();
      }

      this.secondaryContainers.add(var1);
      var1.parent = this;
   }

   public int getContainerIndex(ItemContainer var1) {
      if (var1 == this.container) {
         return 0;
      } else if (this.secondaryContainers == null) {
         return -1;
      } else {
         for(int var2 = 0; var2 < this.secondaryContainers.size(); ++var2) {
            if (this.secondaryContainers.get(var2) == var1) {
               return (this.container == null ? 0 : 1) + var2;
            }
         }

         return -1;
      }
   }

   public void removeAllContainers() {
      this.container = null;
      if (this.secondaryContainers != null) {
         this.secondaryContainers.clear();
      }

   }

   public void createContainersFromSpriteProperties() {
      if (this.sprite != null) {
         if (this.container == null) {
            if (this.sprite.getProperties().Is("container") && this.container == null) {
               this.container = new ItemContainer(this.sprite.getProperties().Val("container"), this.square, this, 1, 1);
               this.container.parent = this;
               this.OutlineOnMouseover = true;
               if (this.sprite.getProperties().Is("ContainerCapacity")) {
                  this.container.Capacity = Integer.parseInt(this.sprite.getProperties().Val("ContainerCapacity"));
               }
            }

            if (this.getSprite().getProperties().Is("Freezer")) {
               ItemContainer var1 = new ItemContainer("freezer", this.square, this, 1, 1);
               if (this.getSprite().getProperties().Is("FreezerCapacity")) {
                  var1.Capacity = Integer.parseInt(this.sprite.getProperties().Val("FreezerCapacity"));
               } else {
                  var1.Capacity = 15;
               }

               if (this.container == null) {
                  this.container = var1;
                  this.container.parent = this;
               } else {
                  this.addSecondaryContainer(var1);
               }
            }

         }
      }
   }

   public void revisionUp() {
      if (this.square != null) {
         this.square.revisionUp();
      }

   }

   public void cleanWallBlood() {
      this.square.removeBlood(false, true);
   }

   public ObjectRenderEffects getWindRenderEffects() {
      return this.windRenderEffects;
   }

   public ObjectRenderEffects getObjectRenderEffects() {
      return this.objectRenderEffects;
   }

   public void setRenderEffect(RenderEffectType var1) {
      this.setRenderEffect(var1, false);
   }

   public void setRenderEffect(RenderEffectType var1, boolean var2) {
      if (!GameServer.bServer) {
         if (this.objectRenderEffects == null || var2) {
            this.objectRenderEffects = ObjectRenderEffects.getNew(this, var1, var2);
         }

      }
   }

   public void removeRenderEffect(ObjectRenderEffects var1) {
      if (this.objectRenderEffects != null && this.objectRenderEffects == var1) {
         this.objectRenderEffects = null;
      }

   }

   public ObjectRenderEffects getObjectRenderEffectsToApply() {
      if (this.objectRenderEffects != null) {
         return this.objectRenderEffects;
      } else {
         return Core.getInstance().getOptionDoWindSpriteEffects() && this.windRenderEffects != null ? this.windRenderEffects : null;
      }
   }

   public static enum VisionResult {
      NoEffect,
      Blocked,
      Unblocked;
   }
}
