package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.bucket.BucketManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.properties.PropertyContainer;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.ColorInfo;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.Key;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;
import zombie.vehicles.BaseVehicle;

public class IsoDoor extends IsoObject implements BarricadeAble, Thumpable {
   public int Health = 500;
   private boolean lockedByKey = false;
   private boolean haveKey = false;
   public boolean Locked = false;
   public int MaxHealth = 500;
   public int PushedMaxStrength = 0;
   public int PushedStrength = 0;
   public IsoDoor.DoorType type;
   IsoSpriteInstance barricadeSprite;
   IsoSprite closedSprite;
   public boolean north;
   int gid;
   public boolean open;
   IsoSprite openSprite;
   private boolean destroyed;
   private boolean bHasCurtain;
   private boolean bCurtainInside;
   private boolean bCurtainOpen;
   public int OldNumPlanks;
   KahluaTable table;
   public static Vector2 tempo = new Vector2();
   private IsoSprite curtainN;
   private IsoSprite curtainS;
   private IsoSprite curtainW;
   private IsoSprite curtainE;
   private IsoSprite curtainNopen;
   private IsoSprite curtainSopen;
   private IsoSprite curtainWopen;
   private IsoSprite curtainEopen;
   private static final int[] DoubleDoorNorthSpriteOffset = new int[]{5, 3, 4, 4};
   private static final int[] DoubleDoorWestSpriteOffset = new int[]{4, 4, 5, 3};
   private static final int[] DoubleDoorNorthClosedXOffset = new int[]{0, 1, 2, 3};
   private static final int[] DoubleDoorNorthOpenXOffset = new int[]{0, 0, 3, 3};
   private static final int[] DoubleDoorNorthClosedYOffset = new int[]{0, 0, 0, 0};
   private static final int[] DoubleDoorNorthOpenYOffset = new int[]{0, 1, 1, 0};
   private static final int[] DoubleDoorWestClosedXOffset = new int[]{0, 0, 0, 0};
   private static final int[] DoubleDoorWestOpenXOffset = new int[]{0, 1, 1, 0};
   private static final int[] DoubleDoorWestClosedYOffset = new int[]{0, -1, -2, -3};
   private static final int[] DoubleDoorWestOpenYOffset = new int[]{0, 0, -3, -3};

   public IsoDoor(IsoCell var1) {
      super(var1);
      this.type = IsoDoor.DoorType.WeakWooden;
      this.north = false;
      this.gid = -1;
      this.open = false;
      this.destroyed = false;
      this.OldNumPlanks = 0;
   }

   public String getObjectName() {
      return "Door";
   }

   public void render(float var1, float var2, float var3, ColorInfo var4, boolean var5, boolean var6) {
      int var7 = IsoCamera.frameState.playerIndex;
      IsoGameCharacter var8 = IsoCamera.frameState.CamCharacter;
      Key var9 = Key.highlightDoor[var7];
      if (var9 != null && var1 >= var8.getX() - 20.0F && var2 >= var8.getY() - 20.0F && var1 < var8.getX() + 20.0F && var2 < var8.getY() + 20.0F) {
         boolean var10 = this.square.isSeen(var7);
         if (!var10) {
            IsoGridSquare var11 = this.getOppositeSquare();
            var10 = var11 != null && var11.isSeen(var7);
         }

         if (var10) {
            this.checkKeyId();
            if (this.getKeyId() == var9.getKeyId()) {
               this.setHighlighted(true);
            }
         }
      }

      if (Core.TileScale == 1) {
         if (this.bHasCurtain && this.bCurtainInside) {
            this.initCurtainSprites();
            if (this.north && this.open) {
               (this.bCurtainOpen ? this.curtainEopen : this.curtainE).render((IsoObject)null, var1 - 1.0F, var2, var3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -16 : -18), this.offsetY + (float)(this.bCurtainOpen ? -14 : -15), var4);
            }

            if (!this.north && this.open) {
               (this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, var1, var2 - 1.0F, var3, this.dir, this.offsetX + 3.0F, this.offsetY + (float)(this.bCurtainOpen ? -14 : -14), var4);
            }
         }

         if (this.bHasCurtain && !this.bCurtainInside) {
            this.initCurtainSprites();
            if (this.north && !this.open) {
               (this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, var1, var2 - 1.0F, var3, this.dir, this.offsetX - 1.0F - 1.0F, this.offsetY + -15.0F, var4);
            }

            if (!this.north && !this.open) {
               (this.bCurtainOpen ? this.curtainEopen : this.curtainE).render((IsoObject)null, var1 - 1.0F, var2, var3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -12 : -14), this.offsetY + (float)(this.bCurtainOpen ? -14 : -15), var4);
            }
         }

         super.render(var1, var2, var3, var4, var5, var6);
         if (this.bHasCurtain && this.bCurtainInside) {
            this.initCurtainSprites();
            if (this.north && !this.open) {
               (this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, var1, var2, var3, this.dir, this.offsetX - 10.0F - 1.0F, this.offsetY + -10.0F, var4);
            }

            if (!this.north && !this.open) {
               (this.bCurtainOpen ? this.curtainWopen : this.curtainW).render((IsoObject)null, var1, var2, var3, this.dir, this.offsetX - 2.0F - 1.0F, this.offsetY + -10.0F, var4);
            }
         }

         if (this.bHasCurtain && !this.bCurtainInside) {
            this.initCurtainSprites();
            if (this.north && this.open) {
               (this.bCurtainOpen ? this.curtainWopen : this.curtainW).render((IsoObject)null, var1, var2, var3, this.dir, this.offsetX - 9.0F, this.offsetY + -10.0F, var4);
            }

            if (!this.north && this.open) {
               (this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, var1, var2, var3, this.dir, this.offsetX - 4.0F, this.offsetY + (float)(this.bCurtainOpen ? -10 : -10), var4);
            }
         }

      } else {
         if (this.bHasCurtain && this.bCurtainInside) {
            this.initCurtainSprites();
            if (this.north && this.open) {
               (this.bCurtainOpen ? this.curtainEopen : this.curtainE).render((IsoObject)null, var1 - 1.0F, var2, var3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -32 : -37), this.offsetY + (float)(this.bCurtainOpen ? -28 : -31), var4);
            }

            if (!this.north && this.open) {
               (this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, var1, var2 - 1.0F, var3, this.dir, this.offsetX + 7.0F, this.offsetY + (float)(this.bCurtainOpen ? -28 : -28), var4);
            }
         }

         if (this.bHasCurtain && !this.bCurtainInside) {
            this.initCurtainSprites();
            if (this.north && !this.open) {
               (this.bCurtainOpen ? this.curtainSopen : this.curtainS).render((IsoObject)null, var1, var2 - 1.0F, var3, this.dir, this.offsetX - 3.0F, this.offsetY + (float)(this.bCurtainOpen ? -30 : -30), var4);
            }

            if (!this.north && !this.open) {
               (this.bCurtainOpen ? this.curtainEopen : this.curtainE).render((IsoObject)null, var1 - 1.0F, var2, var3, this.dir, this.offsetX + (float)(this.bCurtainOpen ? -22 : -26), this.offsetY + (float)(this.bCurtainOpen ? -28 : -31), var4);
            }
         }

         super.render(var1, var2, var3, var4, var5, var6);
         if (this.bHasCurtain && this.bCurtainInside) {
            this.initCurtainSprites();
            if (this.north && !this.open) {
               (this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, var1, var2, var3, this.dir, this.offsetX - 20.0F, this.offsetY + (float)(this.bCurtainOpen ? -20 : -20), var4);
            }

            if (!this.north && !this.open) {
               (this.bCurtainOpen ? this.curtainWopen : this.curtainW).render((IsoObject)null, var1, var2, var3, this.dir, this.offsetX - 5.0F, this.offsetY + (float)(this.bCurtainOpen ? -20 : -20), var4);
            }
         }

         if (this.bHasCurtain && !this.bCurtainInside) {
            this.initCurtainSprites();
            if (this.north && this.open) {
               (this.bCurtainOpen ? this.curtainWopen : this.curtainW).render((IsoObject)null, var1, var2, var3, this.dir, this.offsetX - 19.0F, this.offsetY + (float)(this.bCurtainOpen ? -20 : -20), var4);
            }

            if (!this.north && this.open) {
               (this.bCurtainOpen ? this.curtainNopen : this.curtainN).render((IsoObject)null, var1, var2, var3, this.dir, this.offsetX - 8.0F, this.offsetY + (float)(this.bCurtainOpen ? -20 : -20), var4);
            }
         }

      }
   }

   public IsoDoor(IsoCell var1, IsoGridSquare var2, IsoSprite var3, boolean var4) {
      this.type = IsoDoor.DoorType.WeakWooden;
      this.north = false;
      this.gid = -1;
      this.open = false;
      this.destroyed = false;
      this.OldNumPlanks = 0;
      this.OutlineOnMouseover = true;
      this.PushedMaxStrength = this.PushedStrength = 2500;
      this.closedSprite = var3;
      this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (IsoSprite)var3, 2);
      this.sprite = this.closedSprite;
      this.square = var2;
      this.north = var4;
      switch(this.type) {
      case WeakWooden:
         this.MaxHealth = this.Health = 500;
         break;
      case StrongWooden:
         this.MaxHealth = this.Health = 800;
      }

      byte var5 = 69;
      if (SandboxOptions.instance.LockedHouses.getValue() == 1) {
         var5 = -1;
      } else if (SandboxOptions.instance.LockedHouses.getValue() == 2) {
         var5 = 5;
      } else if (SandboxOptions.instance.LockedHouses.getValue() == 3) {
         var5 = 10;
      } else if (SandboxOptions.instance.LockedHouses.getValue() == 4) {
         var5 = 50;
      } else if (SandboxOptions.instance.LockedHouses.getValue() == 5) {
         var5 = 60;
      } else if (SandboxOptions.instance.LockedHouses.getValue() == 6) {
         var5 = 70;
      }

      if (var5 > -1) {
         this.Locked = Rand.Next(100) < var5;
         if (this.Locked && Rand.Next(3) == 0) {
            this.lockedByKey = true;
         }
      }

      if (this.getProperties().Is("forceLocked")) {
         this.Locked = true;
         this.lockedByKey = true;
      }

   }

   public IsoDoor(IsoCell var1, IsoGridSquare var2, String var3, boolean var4) {
      this.type = IsoDoor.DoorType.WeakWooden;
      this.north = false;
      this.gid = -1;
      this.open = false;
      this.destroyed = false;
      this.OldNumPlanks = 0;
      this.OutlineOnMouseover = true;
      this.PushedMaxStrength = this.PushedStrength = 2500;
      this.closedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)var3, 0);
      this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)var3, 2);
      this.sprite = this.closedSprite;
      this.square = var2;
      this.north = var4;
      switch(this.type) {
      case WeakWooden:
         this.MaxHealth = this.Health = 500;
         break;
      case StrongWooden:
         this.MaxHealth = this.Health = 800;
      }

   }

   public IsoDoor(IsoCell var1, IsoGridSquare var2, String var3, boolean var4, KahluaTable var5) {
      this.type = IsoDoor.DoorType.WeakWooden;
      this.north = false;
      this.gid = -1;
      this.open = false;
      this.destroyed = false;
      this.OldNumPlanks = 0;
      this.OutlineOnMouseover = true;
      this.PushedMaxStrength = this.PushedStrength = 2500;
      this.closedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)var3, 0);
      this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, (String)var3, 2);
      this.table = var5;
      this.sprite = this.closedSprite;
      this.square = var2;
      this.north = var4;
      switch(this.type) {
      case WeakWooden:
         this.MaxHealth = this.Health = 500;
         break;
      case StrongWooden:
         this.MaxHealth = this.Health = 800;
      }

   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      super.load(var1, var2);
      this.open = var1.get() == 1;
      this.Locked = var1.get() == 1;
      this.north = var1.get() == 1;
      if (var2 >= 87) {
         this.Health = var1.getInt();
         this.MaxHealth = var1.getInt();
      } else {
         int var3 = var1.getInt();
         this.Health = var1.getInt();
         this.MaxHealth = var1.getInt();
         int var4 = var1.getInt();
         if (var2 >= 49) {
            short var5 = var1.getShort();
         } else {
            Math.max(var4, var3 * 1000);
         }

         this.OldNumPlanks = var3;
      }

      this.closedSprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, var1.getInt());
      this.openSprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, var1.getInt());
      this.OutlineOnMouseover = true;
      this.PushedMaxStrength = this.PushedStrength = 2500;
      if (var2 >= 57) {
         this.keyId = var1.getInt();
         this.lockedByKey = var1.get() == 1;
      }

      if (var2 >= 80) {
         byte var6 = var1.get();
         if ((var6 & 1) != 0) {
            this.bHasCurtain = true;
            this.bCurtainOpen = (var6 & 2) != 0;
            this.bCurtainInside = (var6 & 4) != 0;
         }
      }

      if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
         GameClient.instance.objectSyncReq.putRequestLoad(this.square);
      }

   }

   public void save(ByteBuffer var1) throws IOException {
      super.save(var1);
      var1.put((byte)(this.open ? 1 : 0));
      var1.put((byte)(this.Locked ? 1 : 0));
      var1.put((byte)(this.north ? 1 : 0));
      var1.putInt(this.Health);
      var1.putInt(this.MaxHealth);
      var1.putInt(this.closedSprite.ID);
      var1.putInt(this.openSprite.ID);
      var1.putInt(this.getKeyId());
      var1.put((byte)(this.isLockedByKey() ? 1 : 0));
      byte var2 = 0;
      if (this.bHasCurtain) {
         var2 = (byte)(var2 | 1);
         if (this.bCurtainOpen) {
            var2 = (byte)(var2 | 2);
         }

         if (this.bCurtainInside) {
            var2 = (byte)(var2 | 4);
         }
      }

      var1.put(var2);
   }

   public void saveState(ByteBuffer var1) {
      var1.put((byte)(this.open ? 1 : 0));
      var1.put((byte)(this.Locked ? 1 : 0));
      var1.put((byte)(this.lockedByKey ? 1 : 0));
   }

   public void loadState(ByteBuffer var1) {
      boolean var2 = var1.get() == 1;
      boolean var3 = var1.get() == 1;
      boolean var4 = var1.get() == 1;
      if (var2 != this.open) {
         this.open = var2;
         this.sprite = var2 ? this.openSprite : this.closedSprite;
      }

      if (var3 != this.Locked) {
         this.Locked = var3;
      }

      if (var4 != this.lockedByKey) {
         this.lockedByKey = var4;
      }

   }

   public boolean isDestroyed() {
      return this.destroyed;
   }

   public boolean IsOpen() {
      return this.open;
   }

   public boolean IsStrengthenedByPushedItems() {
      return false;
   }

   public boolean onMouseLeftClick(int var1, int var2) {
      this.DirtySlice();
      IsoGridSquare var3 = IsoPlayer.getInstance().getCurrentSquare();
      if (!(IsoUtils.DistanceTo((float)this.square.getX(), (float)this.square.getY(), IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY()) < 2.0F) || this.square.getZ() != var3.getZ() || this.getOppositeSquare() != var3 && this.square.isSomethingTo(var3)) {
         return false;
      } else if (Keyboard.isKeyDown(42)) {
         if (this.bHasCurtain && var3 == this.getSheetSquare()) {
            this.toggleCurtain();
         }

         return true;
      } else {
         this.ToggleDoor(IsoPlayer.getInstance());
         return true;
      }
   }

   public boolean TestPathfindCollide(IsoMovingObject var1, IsoGridSquare var2, IsoGridSquare var3) {
      boolean var4 = this.north;
      if (!this.isBarricaded()) {
         return false;
      } else if (var1 instanceof IsoSurvivor && ((IsoSurvivor)var1).getInventory().contains("Hammer")) {
         return false;
      } else {
         if (this.open) {
            var4 = !var4;
         }

         if (var2 == this.square) {
            if (var4 && var3.getY() < var2.getY()) {
               return true;
            }

            if (!var4 && var3.getX() < var2.getX()) {
               return true;
            }
         } else {
            if (var4 && var3.getY() > var2.getY()) {
               return true;
            }

            if (!var4 && var3.getX() > var2.getX()) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean TestCollide(IsoMovingObject var1, IsoGridSquare var2, IsoGridSquare var3) {
      boolean var4 = this.north;
      if (this.open) {
         return false;
      } else {
         if (var2 == this.square) {
            if (var4 && var3.getY() < var2.getY()) {
               if (var1 != null) {
                  var1.collideWith(this);
               }

               return true;
            }

            if (!var4 && var3.getX() < var2.getX()) {
               if (var1 != null) {
                  var1.collideWith(this);
               }

               return true;
            }
         } else {
            if (var4 && var3.getY() > var2.getY()) {
               if (var1 != null) {
                  var1.collideWith(this);
               }

               return true;
            }

            if (!var4 && var3.getX() > var2.getX()) {
               if (var1 != null) {
                  var1.collideWith(this);
               }

               return true;
            }
         }

         return false;
      }
   }

   public IsoObject.VisionResult TestVision(IsoGridSquare var1, IsoGridSquare var2) {
      boolean var3 = this.sprite != null && this.sprite.getProperties().Is("doorTrans");
      boolean var4 = this.north;
      if (this.open) {
         var4 = !var4;
      }

      if (var2.getZ() != var1.getZ()) {
         return IsoObject.VisionResult.NoEffect;
      } else {
         if (var1 == this.square) {
            if (var4 && var2.getY() < var1.getY()) {
               if (!var3 || this.bHasCurtain && !this.bCurtainOpen) {
                  return IsoObject.VisionResult.Blocked;
               }

               return IsoObject.VisionResult.Unblocked;
            }

            if (!var4 && var2.getX() < var1.getX()) {
               if (!var3 || this.bHasCurtain && !this.bCurtainOpen) {
                  return IsoObject.VisionResult.Blocked;
               }

               return IsoObject.VisionResult.Unblocked;
            }
         } else {
            if (var4 && var2.getY() > var1.getY()) {
               if (!var3 || this.bHasCurtain && !this.bCurtainOpen) {
                  return IsoObject.VisionResult.Blocked;
               }

               return IsoObject.VisionResult.Unblocked;
            }

            if (!var4 && var2.getX() > var1.getX()) {
               if (!var3 || this.bHasCurtain && !this.bCurtainOpen) {
                  return IsoObject.VisionResult.Blocked;
               }

               return IsoObject.VisionResult.Unblocked;
            }
         }

         return IsoObject.VisionResult.NoEffect;
      }
   }

   public void Thump(IsoMovingObject var1) {
      if (!this.isDestroyed()) {
         if (var1 instanceof IsoZombie) {
            IsoBarricade var2 = this.getBarricadeForCharacter((IsoZombie)var1);
            if (var2 != null) {
               var2.Thump(var1);
               return;
            }

            var2 = this.getBarricadeOppositeCharacter((IsoZombie)var1);
            if (var2 != null) {
               var2.Thump(var1);
               return;
            }

            if (((IsoZombie)var1).cognition == 1 && !this.open && (!this.Locked || var1.getCurrentSquare() != null && !var1.getCurrentSquare().Is(IsoFlagType.exterior))) {
               this.ToggleDoor((IsoGameCharacter)var1);
               if (this.open) {
                  return;
               }
            }

            int var3 = var1.getCurrentSquare().getMovingObjects().size();
            if (var1.getCurrentSquare().getW() != null) {
               var3 += var1.getCurrentSquare().getW().getMovingObjects().size();
            }

            if (var1.getCurrentSquare().getE() != null) {
               var3 += var1.getCurrentSquare().getE().getMovingObjects().size();
            }

            if (var1.getCurrentSquare().getS() != null) {
               var3 += var1.getCurrentSquare().getS().getMovingObjects().size();
            }

            if (var1.getCurrentSquare().getN() != null) {
               var3 += var1.getCurrentSquare().getN().getMovingObjects().size();
            }

            int var4 = ThumpState.getFastForwardDamageMultiplier();
            int var5 = ((IsoZombie)var1).strength;
            if (var3 >= var5) {
               this.DirtySlice();
               this.Damage(1 * var4);
               if (SandboxOptions.instance.Lore.Strength.getValue() == 1) {
                  this.Damage(2 * var4);
               }
            }

            if (Core.GameMode.equals("LastStand")) {
               this.Damage(1 * var4);
            }

            WorldSoundManager.instance.addSound(var1, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
            this.setRenderEffect(RenderEffectType.Hit_Door, true);
         }

         if (this.Health <= 0) {
            ((IsoGameCharacter)((IsoGameCharacter)var1)).getEmitter().playSound("BreakDoor");
            if (GameServer.bServer) {
               GameServer.PlayWorldSoundServer("BreakDoor", false, var1.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
            }

            WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
            var1.setThumpTarget((Thumpable)null);
            int var6 = getDoubleDoorIndex(this);
            if (var6 == 1 || var6 == 4) {
               IsoObject var7 = getDoubleDoorObject(this, var6 == 1 ? 2 : 3);
               if (var7 != null) {
                  ((IsoDoor)var7).destroy();
               }
            }

            this.destroy();
         }

      }
   }

   public void WeaponHit(IsoGameCharacter var1, HandWeapon var2) {
      if (GameClient.bClient) {
         if (var1 instanceof IsoPlayer) {
            GameClient.instance.sendWeaponHit((IsoPlayer)var1, var2, this);
         }

         this.setRenderEffect(RenderEffectType.Hit_Door, true);
      } else {
         IsoBarricade var3 = this.getBarricadeForCharacter(var1);
         if (var3 != null) {
            var3.WeaponHit(var1, var2);
         } else {
            var3 = this.getBarricadeOppositeCharacter(var1);
            if (var3 != null) {
               var3.WeaponHit(var1, var2);
            } else if (!this.open) {
               if (!this.isDestroyed()) {
                  int var4 = var1.getPerkLevel(PerkFactory.Perks.Strength);
                  float var5 = 1.0F;
                  if (var4 == 0) {
                     var5 = 0.5F;
                  } else if (var4 == 1) {
                     var5 = 0.63F;
                  } else if (var4 == 2) {
                     var5 = 0.76F;
                  } else if (var4 == 3) {
                     var5 = 0.89F;
                  } else if (var4 == 4) {
                     var5 = 1.02F;
                  }

                  if (var4 == 6) {
                     var5 = 1.15F;
                  } else if (var4 == 7) {
                     var5 = 1.27F;
                  } else if (var4 == 8) {
                     var5 = 1.3F;
                  } else if (var4 == 9) {
                     var5 = 1.45F;
                  } else if (var4 == 10) {
                     var5 = 1.7F;
                  }

                  this.Damage((int)((float)var2.getDoorDamage() * 2.0F * var5));
                  this.setRenderEffect(RenderEffectType.Hit_Door, true);
                  if (Rand.Next(10) == 0) {
                     this.Damage((int)((float)var2.getDoorDamage() * 6.0F * var5));
                  }

                  float var6 = GameTime.getInstance().getMultiplier() / 1.6F;
                  switch(var1.getPerkLevel(PerkFactory.Perks.Fitness)) {
                  case 0:
                     var1.exert(0.01F * var6);
                     break;
                  case 1:
                     var1.exert(0.007F * var6);
                     break;
                  case 2:
                     var1.exert(0.0065F * var6);
                     break;
                  case 3:
                     var1.exert(0.006F * var6);
                     break;
                  case 4:
                     var1.exert(0.005F * var6);
                     break;
                  case 5:
                     var1.exert(0.004F * var6);
                     break;
                  case 6:
                     var1.exert(0.0035F * var6);
                     break;
                  case 7:
                     var1.exert(0.003F * var6);
                     break;
                  case 8:
                     var1.exert(0.0025F * var6);
                     break;
                  case 9:
                     var1.exert(0.002F * var6);
                  }

                  this.DirtySlice();
                  if (var2.getDoorHitSound() != null) {
                     var1.getEmitter().playSound(var2.getDoorHitSound(), this);
                     if (GameServer.bServer) {
                        GameServer.PlayWorldSoundServer(var2.getDoorHitSound(), false, this.getSquare(), 1.0F, 20.0F, 2.0F, false);
                     }
                  }

                  WorldSoundManager.instance.addSound(var1, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
                  if (!this.IsStrengthenedByPushedItems() && this.Health <= 0 || this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength) {
                     var1.getEmitter().playSound("BreakDoor", this);
                     if (GameServer.bServer) {
                        GameServer.PlayWorldSoundServer("BreakDoor", false, this.getSquare(), 0.2F, 20.0F, 1.1F, true);
                     }

                     WorldSoundManager.instance.addSound(var1, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
                     var4 = getDoubleDoorIndex(this);
                     if (var4 == 1 || var4 == 4) {
                        IsoObject var7 = getDoubleDoorObject(this, var4 == 1 ? 2 : 3);
                        if (var7 != null) {
                           ((IsoDoor)var7).destroy();
                        }
                     }

                     this.destroy();
                     LuaEventManager.triggerEvent("OnContainerUpdate");
                  }

               }
            }
         }
      }
   }

   public void destroy() {
      int var1 = Rand.Next(2) + 1;

      for(int var2 = 0; var2 < var1; ++var2) {
         this.square.AddWorldInventoryItem("Base.Plank", 0.0F, 0.0F, 0.0F);
      }

      InventoryItem var5 = InventoryItemFactory.CreateItem("Base.Doorknob");
      var5.setKeyId(this.checkKeyId());
      this.square.AddWorldInventoryItem(var5, 0.0F, 0.0F, 0.0F);
      int var3 = Rand.Next(3);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.square.AddWorldInventoryItem("Base.Hinge", 0.0F, 0.0F, 0.0F);
      }

      if (this.bHasCurtain) {
         this.square.AddWorldInventoryItem("Base.Sheet", 0.0F, 0.0F, 0.0F);
      }

      this.destroyed = true;
      this.square.transmitRemoveItemFromSquare(this);
   }

   public IsoGridSquare getOtherSideOfDoor(IsoGameCharacter var1) {
      if (this.north) {
         return var1.getCurrentSquare().getRoom() == this.square.getRoom() ? IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ()) : IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ());
      } else {
         return var1.getCurrentSquare().getRoom() == this.square.getRoom() ? IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ()) : IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ());
      }
   }

   public boolean isExteriorDoor(IsoGameCharacter var1) {
      IsoGridSquare var2 = this.getSquare();
      IsoGridSquare var3 = this.getOppositeSquare();
      if (var3 == null) {
         return false;
      } else if (var2.Is(IsoFlagType.exterior) && var3.getBuilding() != null && var3.getBuilding().getDef() != null) {
         return true;
      } else {
         return var2.getBuilding() != null && var2.getBuilding().getDef() != null && var3.Is(IsoFlagType.exterior);
      }
   }

   public void ToggleDoorActual(IsoGameCharacter var1) {
      if (this.isBarricaded()) {
         if (var1 != null) {
            var1.getEmitter().playSound("DoorIsBlocked", this);
            var1.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBarricaded"), 255, 255, 255, 256.0F);
         }

      } else {
         this.checkKeyId();
         if (this.Locked && !this.lockedByKey && this.getKeyId() != -1) {
            this.lockedByKey = true;
         }

         if (!this.open && var1 instanceof IsoPlayer) {
            ((IsoPlayer)var1).TimeSinceOpenDoor = 0.0F;
         }

         this.DirtySlice();
         IsoGridSquare.RecalcLightTime = -1;
         GameTime.instance.lightSourceUpdate = 100.0F;
         this.square.InvalidateSpecialObjectPaths();
         if (this.isLockedByKey() && var1 != null && var1 instanceof IsoPlayer && (var1.getCurrentSquare().Is(IsoFlagType.exterior) || this.getProperties().Is("forceLocked")) && !this.open) {
            if (var1.getInventory().haveThisKeyId(this.getKeyId()) == null) {
               var1.getEmitter().playSound("DoorIsLocked", this);
               if (var1 instanceof IsoSurvivor) {
                  var1.getMasterBehaviorList().reset();
               }

               this.setRenderEffect(RenderEffectType.Hit_Door, true);
               return;
            }

            var1.getEmitter().playSound("UnlockDoor", this);
            var1.getEmitter().playSound("OpenDoor", this);
            this.Locked = false;
            this.setLockedByKey(false);
         }

         if (this.Locked && var1 != null && var1 instanceof IsoPlayer && var1.getCurrentSquare().Is(IsoFlagType.exterior) && !this.open) {
            var1.getEmitter().playSound("DoorIsLocked", this);
            if (var1 instanceof IsoSurvivor) {
               var1.getMasterBehaviorList().reset();
            }

            this.setRenderEffect(RenderEffectType.Hit_Door, true);
         } else if (this.getSprite().getProperties().Is("DoubleDoor")) {
            if (isDoubleDoorObstructed(this)) {
               if (var1 != null) {
                  var1.getEmitter().playSound("DoorIsBlocked", this);
                  var1.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0F);
               }

            } else {
               boolean var3 = this.open;
               toggleDoubleDoor(this, true);
               if (var3 != this.open) {
                  var1.getEmitter().playSound(this.open ? "OpenDoor" : "CloseDoor", this);
               }

            }
         } else if (this.isObstructed()) {
            if (var1 != null) {
               var1.getEmitter().playSound("DoorIsBlocked", this);
               var1.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0F);
            }

         } else {
            this.Locked = false;
            this.setLockedByKey(false);
            if (var1 instanceof IsoPlayer) {
               for(int var2 = 0; var2 < IsoPlayer.numPlayers; ++var2) {
                  LosUtil.cachecleared[var2] = true;
               }

               IsoGridSquare.setRecalcLightTime(-1);
            }

            this.open = !this.open;
            this.sprite = this.closedSprite;
            if (this.open) {
               var1.getEmitter().playSound("OpenDoor");
               this.sprite = this.openSprite;
            } else if (var1 != null) {
               var1.getEmitter().playSound("CloseDoor");
            }

            this.square.RecalcProperties();
            this.syncIsoObject(false, (byte)(this.open ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
            LuaEventManager.triggerEvent("OnContainerUpdate");
         }
      }
   }

   public void syncIsoObjectSend(ByteBufferWriter var1) {
      var1.putInt(this.square.getX());
      var1.putInt(this.square.getY());
      var1.putInt(this.square.getZ());
      byte var2 = (byte)this.square.getObjects().indexOf(this);
      var1.putByte(var2);
      var1.putByte((byte)1);
      if (this.open) {
         var1.putByte((byte)1);
      } else if (this.lockedByKey) {
         var1.putByte((byte)3);
      } else {
         var1.putByte((byte)4);
      }

   }

   public void syncIsoObject(boolean var1, byte var2, UdpConnection var3, ByteBuffer var4) {
      if (this.square == null) {
         System.out.println("ERROR: " + this.getClass().getSimpleName() + " square is null");
      } else if (this.getObjectIndex() == -1) {
         System.out.println("ERROR: " + this.getClass().getSimpleName() + " not found on square " + this.square.getX() + "," + this.square.getY() + "," + this.square.getZ());
      } else {
         if (GameClient.bClient && !var1) {
            ByteBufferWriter var8 = GameClient.connection.startPacket();
            PacketTypes.doPacket((short)12, var8);
            this.syncIsoObjectSend(var8);
            GameClient.connection.endPacketImmediate();
            this.square.clientModify();
         } else {
            Iterator var5;
            UdpConnection var6;
            ByteBufferWriter var7;
            if (GameServer.bServer && !var1) {
               var5 = GameServer.udpEngine.connections.iterator();

               while(var5.hasNext()) {
                  var6 = (UdpConnection)var5.next();
                  var7 = var6.startPacket();
                  PacketTypes.doPacket((short)12, var7);
                  this.syncIsoObjectSend(var7);
                  var6.endPacketImmediate();
               }
            } else if (var1) {
               if (var2 == 1) {
                  this.open = true;
                  this.sprite = this.openSprite;
                  this.Locked = false;
               } else if (var2 == 0) {
                  this.open = false;
                  this.sprite = this.closedSprite;
               } else if (var2 == 3) {
                  this.lockedByKey = true;
                  this.open = false;
                  this.sprite = this.closedSprite;
               } else if (var2 == 4) {
                  this.lockedByKey = false;
                  this.open = false;
                  this.sprite = this.closedSprite;
               }

               if (GameServer.bServer) {
                  var5 = GameServer.udpEngine.connections.iterator();

                  label60:
                  while(true) {
                     do {
                        if (!var5.hasNext()) {
                           this.square.revisionUp();
                           break label60;
                        }

                        var6 = (UdpConnection)var5.next();
                     } while((var3 == null || var6.getConnectedGUID() == var3.getConnectedGUID()) && var3 != null);

                     var7 = var6.startPacket();
                     PacketTypes.doPacket((short)12, var7);
                     this.syncIsoObjectSend(var7);
                     var6.endPacketImmediate();
                  }
               }
            }
         }

         this.square.InvalidateSpecialObjectPaths();
         this.square.RecalcProperties();

         for(int var9 = 0; var9 < IsoPlayer.numPlayers; ++var9) {
            LosUtil.cachecleared[var9] = true;
         }

         IsoGridSquare.setRecalcLightTime(-1);
         GameTime.instance.lightSourceUpdate = 100.0F;
         LuaEventManager.triggerEvent("OnContainerUpdate");
      }
   }

   public void ToggleDoor(IsoGameCharacter var1) {
      this.ToggleDoorActual(var1);
   }

   public void ToggleDoorSilent() {
      if (!this.isBarricaded()) {
         this.square.InvalidateSpecialObjectPaths();

         for(int var1 = 0; var1 < IsoPlayer.numPlayers; ++var1) {
            LosUtil.cachecleared[var1] = true;
         }

         IsoGridSquare.setRecalcLightTime(-1);
         this.open = !this.open;
         this.sprite = this.closedSprite;
         if (this.open) {
            this.sprite = this.openSprite;
         }

      }
   }

   void Damage(int var1) {
      this.DirtySlice();
      this.Health -= var1;
   }

   public IsoBarricade getBarricadeOnSameSquare() {
      return IsoBarricade.GetBarricadeOnSquare(this.square, this.north ? IsoDirections.N : IsoDirections.W);
   }

   public IsoBarricade getBarricadeOnOppositeSquare() {
      return IsoBarricade.GetBarricadeOnSquare(this.getOppositeSquare(), this.north ? IsoDirections.S : IsoDirections.E);
   }

   public boolean isBarricaded() {
      IsoBarricade var1 = this.getBarricadeOnSameSquare();
      if (var1 == null) {
         var1 = this.getBarricadeOnOppositeSquare();
      }

      return var1 != null;
   }

   public IsoBarricade getBarricadeForCharacter(IsoGameCharacter var1) {
      return IsoBarricade.GetBarricadeForCharacter(this, var1);
   }

   public IsoBarricade getBarricadeOppositeCharacter(IsoGameCharacter var1) {
      return IsoBarricade.GetBarricadeOppositeCharacter(this, var1);
   }

   public boolean isLocked() {
      return this.Locked;
   }

   public boolean getNorth() {
      return this.north;
   }

   public Vector2 getFacingPosition(Vector2 var1) {
      if (this.square == null) {
         return var1.set(0.0F, 0.0F);
      } else {
         return this.north ? var1.set(this.getX() + 0.5F, this.getY()) : var1.set(this.getX(), this.getY() + 0.5F);
      }
   }

   public Vector2 getFacingPositionAlt(Vector2 var1) {
      if (this.square == null) {
         return var1.set(0.0F, 0.0F);
      } else if (this.open) {
         return this.north ? var1.set(this.getX(), this.getY() + 0.5F) : var1.set(this.getX() + 0.5F, this.getY());
      } else {
         return this.north ? var1.set(this.getX() + 0.5F, this.getY()) : var1.set(this.getX(), this.getY() + 0.5F);
      }
   }

   public void setIsLocked(boolean var1) {
      this.Locked = var1;
   }

   public IsoSprite getOpenSprite() {
      return this.openSprite;
   }

   public void setOpenSprite(IsoSprite var1) {
      this.openSprite = var1;
   }

   public int getKeyId() {
      return this.keyId;
   }

   public void syncDoorKey() {
      ByteBufferWriter var1 = GameClient.connection.startPacket();
      PacketTypes.doPacket((short)106, var1);
      var1.putInt(this.square.getX());
      var1.putInt(this.square.getY());
      var1.putInt(this.square.getZ());
      byte var2 = (byte)this.square.getObjects().indexOf(this);
      if (var2 == -1) {
         System.out.println("ERROR: Door not found on square " + this.square.getX() + ", " + this.square.getY() + ", " + this.square.getZ());
         GameClient.connection.cancelPacket();
      } else {
         var1.putByte(var2);
         var1.putInt(this.getKeyId());
         GameClient.connection.endPacketImmediate();
      }
   }

   public void setKeyId(int var1) {
      if (this.keyId != var1 && GameClient.bClient) {
         this.keyId = var1;
         this.syncDoorKey();
      } else {
         this.keyId = var1;
      }

   }

   public boolean isLockedByKey() {
      return this.lockedByKey;
   }

   public void setLockedByKey(boolean var1) {
      boolean var2 = var1 != this.lockedByKey;
      this.lockedByKey = var1;
      this.Locked = var1;
      if (!GameServer.bServer && var2) {
         if (var1) {
            this.syncIsoObject(false, (byte)3, (UdpConnection)null, (ByteBuffer)null);
         } else {
            this.syncIsoObject(false, (byte)4, (UdpConnection)null, (ByteBuffer)null);
         }
      }

   }

   public boolean haveKey() {
      return this.haveKey;
   }

   public void setHaveKey(boolean var1) {
      this.haveKey = var1;
      if (!GameServer.bServer) {
         if (var1) {
            this.syncIsoObject(false, (byte)-1, (UdpConnection)null, (ByteBuffer)null);
         } else {
            this.syncIsoObject(false, (byte)-2, (UdpConnection)null, (ByteBuffer)null);
         }

      }
   }

   public IsoGridSquare getOppositeSquare() {
      return this.getNorth() ? this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() - 1.0F), (double)this.getZ()) : this.getCell().getGridSquare((double)(this.getX() - 1.0F), (double)this.getY(), (double)this.getZ());
   }

   public int checkKeyId() {
      if (this.getKeyId() != -1) {
         return this.getKeyId();
      } else {
         IsoGridSquare var1 = this.getSquare();
         IsoGridSquare var2 = this.getOppositeSquare();
         if (var1 != null && var2 != null) {
            BuildingDef var3 = var1.getBuilding() == null ? null : var1.getBuilding().getDef();
            BuildingDef var4 = var2.getBuilding() == null ? null : var2.getBuilding().getDef();
            if (var3 == null && var4 != null) {
               this.setKeyId(var4.getKeyId());
            } else if (var3 != null && var4 == null) {
               this.setKeyId(var3.getKeyId());
            } else if (this.getProperties().Is("forceLocked") && var3 != null) {
               this.setKeyId(var3.getKeyId());
            }

            if (this.Locked && !this.lockedByKey && this.getKeyId() != -1) {
               this.lockedByKey = true;
            }

            return this.getKeyId();
         } else {
            return -1;
         }
      }
   }

   public void setHealth(int var1) {
      this.Health = var1;
   }

   private void initCurtainSprites() {
      if (this.curtainN == null) {
         this.curtainW = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.curtainW.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_16");
         this.curtainW.def.setScale(0.8F, 0.8F);
         this.curtainWopen = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.curtainWopen.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_20");
         this.curtainWopen.def.setScale(0.8F, 0.8F);
         this.curtainE = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.curtainE.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_17");
         this.curtainE.def.setScale(0.8F, 0.8F);
         this.curtainEopen = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.curtainEopen.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_21");
         this.curtainEopen.def.setScale(0.8F, 0.8F);
         this.curtainN = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.curtainN.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_18");
         this.curtainN.def.setScale(0.8F, 0.8F);
         this.curtainNopen = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.curtainNopen.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_22");
         this.curtainNopen.def.setScale(0.8F, 0.8F);
         this.curtainS = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.curtainS.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_19");
         this.curtainS.def.setScale(0.8F, 0.8F);
         this.curtainSopen = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
         this.curtainSopen.LoadFramesNoDirPageSimple("fixtures_windows_curtains_01_23");
         this.curtainSopen.def.setScale(0.8F, 0.8F);
      }
   }

   public IsoDoor HasCurtains() {
      return this.bHasCurtain ? this : null;
   }

   public boolean isCurtainOpen() {
      return this.bHasCurtain && this.bCurtainOpen;
   }

   public void setCurtainOpen(boolean var1) {
      if (this.bHasCurtain) {
         this.bCurtainOpen = var1;
         if (!GameServer.bServer) {
            for(int var2 = 0; var2 < IsoPlayer.numPlayers; ++var2) {
               LosUtil.cachecleared[var2] = true;
            }

            GameTime.instance.lightSourceUpdate = 100.0F;
            IsoGridSquare.setRecalcLightTime(-1);
            if (this.square != null) {
               this.square.RecalcProperties();
            }
         }

      }
   }

   public void transmitSetCurtainOpen(boolean var1) {
      if (this.bHasCurtain) {
         if (GameServer.bServer) {
            this.sendObjectChange("setCurtainOpen", new Object[]{"open", var1});
         }

         if (GameClient.bClient) {
            GameClient.instance.sendClientCommandV((IsoPlayer)null, "object", "openCloseCurtain", "x", this.getX(), "y", this.getY(), "z", this.getZ(), "index", this.getObjectIndex(), "open", !this.bCurtainOpen);
         }

      }
   }

   public void toggleCurtain() {
      if (this.bHasCurtain) {
         if (GameClient.bClient) {
            this.transmitSetCurtainOpen(!this.isCurtainOpen());
         } else {
            this.setCurtainOpen(!this.isCurtainOpen());
            if (GameServer.bServer) {
               this.transmitSetCurtainOpen(this.isCurtainOpen());
            }
         }

      }
   }

   public void addSheet(IsoGameCharacter var1) {
      if (!this.bHasCurtain && var1 != null && var1.getCurrentSquare() != null) {
         IsoGridSquare var2 = var1.getCurrentSquare();
         IsoGridSquare var3 = this.getSquare();
         boolean var4;
         if (this.open) {
            if (this.north) {
               var4 = var2.getX() < var3.getX();
            } else {
               var4 = var2.getY() < var3.getY();
            }
         } else if (this.north) {
            var4 = var2.getY() >= var3.getY();
         } else {
            var4 = var2.getX() >= var3.getX();
         }

         this.addSheet(var4, var1);
      }
   }

   public void addSheet(boolean var1, IsoGameCharacter var2) {
      if (!this.bHasCurtain) {
         this.bHasCurtain = true;
         this.bCurtainInside = var1;
         this.bCurtainOpen = true;
         if (GameServer.bServer) {
            this.sendObjectChange("addSheet", new Object[]{"inside", var1});
            if (var2 != null) {
               var2.sendObjectChange("removeOneOf", new Object[]{"type", "Sheet"});
            }
         } else if (var2 != null) {
            var2.getInventory().RemoveOneOf("Sheet");

            for(int var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
               LosUtil.cachecleared[var3] = true;
            }

            GameTime.instance.lightSourceUpdate = 100.0F;
            IsoGridSquare.setRecalcLightTime(-1);
            if (this.square != null) {
               this.square.RecalcProperties();
            }
         }

      }
   }

   public void removeSheet(IsoGameCharacter var1) {
      if (this.bHasCurtain) {
         this.bHasCurtain = false;
         if (GameServer.bServer) {
            this.sendObjectChange("removeSheet");
            if (var1 != null) {
               var1.sendObjectChange("addItemOfType", new Object[]{"type", "Base.Sheet"});
            }
         } else if (var1 != null) {
            var1.getInventory().AddItem("Base.Sheet");

            for(int var2 = 0; var2 < IsoPlayer.numPlayers; ++var2) {
               LosUtil.cachecleared[var2] = true;
            }

            GameTime.instance.lightSourceUpdate = 100.0F;
            IsoGridSquare.setRecalcLightTime(-1);
            if (this.square != null) {
               this.square.RecalcProperties();
            }
         }

      }
   }

   public IsoGridSquare getAddSheetSquare(IsoGameCharacter var1) {
      if (var1 != null && var1.getCurrentSquare() != null) {
         IsoGridSquare var2 = var1.getCurrentSquare();
         IsoGridSquare var3 = this.getSquare();
         if (this.open) {
            if (this.north) {
               return var2.getX() < var3.getX() ? this.getCell().getGridSquare(var3.x - 1, var3.y, var3.z) : var3;
            } else {
               return var2.getY() < var3.getY() ? this.getCell().getGridSquare(var3.x, var3.y - 1, var3.z) : var3;
            }
         } else if (this.north) {
            return var2.getY() >= var3.getY() ? var3 : this.getOppositeSquare();
         } else {
            return var2.getX() >= var3.getX() ? var3 : this.getOppositeSquare();
         }
      } else {
         return null;
      }
   }

   public IsoGridSquare getSheetSquare() {
      if (!this.bHasCurtain) {
         return null;
      } else if (this.bCurtainInside) {
         if (this.open) {
            return this.north ? this.getCell().getGridSquare((double)(this.getX() - 1.0F), (double)this.getY(), (double)this.getZ()) : this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() - 1.0F), (double)this.getZ());
         } else {
            return this.getSquare();
         }
      } else if (this.open) {
         return this.north ? this.getSquare() : this.getCell().getGridSquare((double)this.getX(), (double)this.getY(), (double)this.getZ());
      } else {
         return this.getOppositeSquare();
      }
   }

   public int getHealth() {
      return this.Health;
   }

   public int getMaxHealth() {
      return this.MaxHealth;
   }

   public boolean isFacingSheet(IsoGameCharacter var1) {
      if (this.bHasCurtain && var1 != null && var1.getCurrentSquare() == this.getSheetSquare()) {
         IsoDirections var2;
         if (this.bCurtainInside) {
            if (this.open) {
               if (this.north) {
                  var2 = IsoDirections.E;
               } else {
                  var2 = IsoDirections.S;
               }
            } else if (this.north) {
               var2 = IsoDirections.N;
            } else {
               var2 = IsoDirections.W;
            }
         } else if (this.open) {
            if (this.north) {
               var2 = IsoDirections.W;
            } else {
               var2 = IsoDirections.N;
            }
         } else if (this.north) {
            var2 = IsoDirections.S;
         } else {
            var2 = IsoDirections.E;
         }

         return var1.getDir() == var2 || var1.getDir() == IsoDirections.RotLeft(var2) || var1.getDir() == IsoDirections.RotRight(var2);
      } else {
         return false;
      }
   }

   public void saveChange(String var1, KahluaTable var2, ByteBuffer var3) {
      if ("addSheet".equals(var1)) {
         if (var2 != null && var2.rawget("inside") instanceof Boolean) {
            var3.put((byte)((Boolean)var2.rawget("inside") ? 1 : 0));
         }
      } else if (!"removeSheet".equals(var1)) {
         if ("setCurtainOpen".equals(var1)) {
            if (var2 != null && var2.rawget("open") instanceof Boolean) {
               var3.put((byte)((Boolean)var2.rawget("open") ? 1 : 0));
            }
         } else {
            super.saveChange(var1, var2, var3);
         }
      }

   }

   public void loadChange(String var1, ByteBuffer var2) {
      if ("addSheet".equals(var1)) {
         this.addSheet(var2.get() == 1, (IsoGameCharacter)null);
      } else if ("removeSheet".equals(var1)) {
         this.removeSheet((IsoGameCharacter)null);
      } else if ("setCurtainOpen".equals(var1)) {
         this.setCurtainOpen(var2.get() == 1);
      } else {
         super.loadChange(var1, var2);
      }

   }

   public void addRandomBarricades() {
      IsoGridSquare var1 = this.square.getRoom() == null ? this.square : this.getOppositeSquare();
      if (var1 != null && var1.getRoom() == null) {
         boolean var2 = var1 != this.square;
         IsoBarricade var3 = IsoBarricade.AddBarricadeToObject(this, var2);
         if (var3 != null) {
            int var4 = Rand.Next(1, 4);

            for(int var5 = 0; var5 < var4; ++var5) {
               var3.addPlank((IsoGameCharacter)null, (InventoryItem)null);
            }
         }
      }

   }

   public boolean isObstructed() {
      return isDoorObstructed(this);
   }

   public static boolean isDoorObstructed(IsoObject var0) {
      IsoDoor var1 = var0 instanceof IsoDoor ? (IsoDoor)var0 : null;
      IsoThumpable var2 = var0 instanceof IsoThumpable ? (IsoThumpable)var0 : null;
      if (var1 == null && var2 == null) {
         return false;
      } else {
         IsoGridSquare var3 = var0.getSquare();
         if (var3 == null) {
            return false;
         } else if (!var3.isSolid() && !var3.isSolidTrans() && !var3.Has(IsoObjectType.tree)) {
            int var4 = (var3.x - 1) / 10;
            int var5 = (var3.y - 1) / 10;
            int var6 = (int)Math.ceil((double)(((float)var3.x + 1.0F) / 10.0F));
            int var7 = (int)Math.ceil((double)(((float)var3.y + 1.0F) / 10.0F));

            for(int var8 = var5; var8 <= var7; ++var8) {
               for(int var9 = var4; var9 <= var6; ++var9) {
                  IsoChunk var10 = GameServer.bServer ? ServerMap.instance.getChunk(var9, var8) : IsoWorld.instance.CurrentCell.getChunk(var9, var8);
                  if (var10 != null) {
                     for(int var11 = 0; var11 < var10.vehicles.size(); ++var11) {
                        BaseVehicle var12 = (BaseVehicle)var10.vehicles.get(var11);
                        if (var12.isIntersectingSquareWithShadow(var3.x, var3.y, var3.z)) {
                           return true;
                        }
                     }
                  }
               }
            }

            return false;
         } else {
            return true;
         }
      }
   }

   public static void toggleDoubleDoor(IsoObject var0, boolean var1) {
      int var2 = getDoubleDoorIndex(var0);
      if (var2 != -1) {
         IsoDoor var3 = var0 instanceof IsoDoor ? (IsoDoor)var0 : null;
         IsoThumpable var4 = var0 instanceof IsoThumpable ? (IsoThumpable)var0 : null;
         boolean var10000;
         if (var3 == null) {
            var10000 = var4.north;
         } else {
            var10000 = var3.north;
         }

         if (var3 == null) {
            var10000 = var4.open;
         } else {
            var10000 = var3.open;
         }

         if (var1 && var4 != null) {
            var4.syncIsoObject(false, (byte)(var4.open ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
         }

         IsoObject var7 = getDoubleDoorObject(var0, 1);
         IsoObject var8 = getDoubleDoorObject(var0, 2);
         IsoObject var9 = getDoubleDoorObject(var0, 3);
         IsoObject var10 = getDoubleDoorObject(var0, 4);
         if (var7 != null) {
            toggleDoubleDoorObject(var7);
         }

         if (var8 != null) {
            toggleDoubleDoorObject(var8);
         }

         if (var9 != null) {
            toggleDoubleDoorObject(var9);
         }

         if (var10 != null) {
            toggleDoubleDoorObject(var10);
         }

         LuaEventManager.triggerEvent("OnContainerUpdate");
      }
   }

   private static void toggleDoubleDoorObject(IsoObject var0) {
      int var1 = getDoubleDoorIndex(var0);
      if (var1 != -1) {
         IsoDoor var2 = var0 instanceof IsoDoor ? (IsoDoor)var0 : null;
         IsoThumpable var3 = var0 instanceof IsoThumpable ? (IsoThumpable)var0 : null;
         boolean var4 = var2 == null ? var3.north : var2.north;
         boolean var5 = var2 == null ? var3.open : var2.open;
         if (var2 != null) {
            var2.open = !var5;
            var2.setLockedByKey(false);
         }

         if (var3 != null) {
            var3.open = !var5;
            var3.setLockedByKey(false);
         }

         IsoSprite var6 = var0.getSprite();
         int var7 = var4 ? DoubleDoorNorthSpriteOffset[var1 - 1] : DoubleDoorWestSpriteOffset[var1 - 1];
         if (var5) {
            var7 *= -1;
         }

         var0.sprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, var6.getName(), var7);
         var0.getSquare().RecalcAllWithNeighbours(true);
         if (var1 == 2 || var1 == 3) {
            IsoGridSquare var8 = var0.getSquare();
            int[] var9;
            int[] var10;
            int[] var11;
            int[] var12;
            if (var4) {
               if (var5) {
                  var9 = DoubleDoorNorthOpenXOffset;
                  var10 = DoubleDoorNorthOpenYOffset;
                  var11 = DoubleDoorNorthClosedXOffset;
                  var12 = DoubleDoorNorthClosedYOffset;
               } else {
                  var9 = DoubleDoorNorthClosedXOffset;
                  var10 = DoubleDoorNorthClosedYOffset;
                  var11 = DoubleDoorNorthOpenXOffset;
                  var12 = DoubleDoorNorthOpenYOffset;
               }
            } else if (var5) {
               var9 = DoubleDoorWestOpenXOffset;
               var10 = DoubleDoorWestOpenYOffset;
               var11 = DoubleDoorWestClosedXOffset;
               var12 = DoubleDoorWestClosedYOffset;
            } else {
               var9 = DoubleDoorWestClosedXOffset;
               var10 = DoubleDoorWestClosedYOffset;
               var11 = DoubleDoorWestOpenXOffset;
               var12 = DoubleDoorWestOpenYOffset;
            }

            int var13 = var8.getX() - var9[var1 - 1];
            int var14 = var8.getY() - var10[var1 - 1];
            int var15 = var13 + var11[var1 - 1];
            int var16 = var14 + var12[var1 - 1];
            var8.RemoveTileObject(var0);
            var8 = IsoWorld.instance.CurrentCell.getGridSquare(var15, var16, var8.getZ());
            if (var8 == null) {
               return;
            }

            if (var3 != null) {
               IsoThumpable var17 = new IsoThumpable(var8.getCell(), var8, var0.getSprite().getName(), var4, var3.getTable());
               var17.setModData(var3.getModData());
               var17.setIsDoor(true);
               var17.open = !var5;
               var8.AddSpecialObject(var17);
            } else {
               IsoDoor var18 = new IsoDoor(var8.getCell(), var8, var0.getSprite().getName(), var4);
               var18.open = !var5;
               var8.getObjects().add(var18);
               var8.getSpecialObjects().add(var18);
               var8.RecalcProperties();
            }

            if (!GameClient.bClient) {
               var8.restackSheetRope();
            }
         }

      }
   }

   public static int getDoubleDoorIndex(IsoObject var0) {
      if (var0 != null && var0.getSquare() != null) {
         PropertyContainer var1 = var0.getProperties();
         if (var1 != null && var1.Is("DoubleDoor")) {
            int var2 = Integer.parseInt(var1.Val("DoubleDoor"));
            if (var2 >= 1 && var2 <= 8) {
               IsoDoor var3 = var0 instanceof IsoDoor ? (IsoDoor)var0 : null;
               IsoThumpable var4 = var0 instanceof IsoThumpable ? (IsoThumpable)var0 : null;
               if (var3 == null && var4 == null) {
                  return -1;
               } else {
                  boolean var5 = var3 == null ? var4.open : var3.open;
                  if (var5) {
                     return var2 >= 5 ? var2 - 4 : -1;
                  } else {
                     return var2;
                  }
               }
            } else {
               return -1;
            }
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static IsoObject getDoubleDoorObject(IsoObject var0, int var1) {
      int var2 = getDoubleDoorIndex(var0);
      if (var2 == -1) {
         return null;
      } else {
         IsoDoor var3 = var0 instanceof IsoDoor ? (IsoDoor)var0 : null;
         IsoThumpable var4 = var0 instanceof IsoThumpable ? (IsoThumpable)var0 : null;
         boolean var5 = var3 == null ? var4.north : var3.north;
         boolean var6 = var3 == null ? var4.open : var3.open;
         IsoGridSquare var7 = var0.getSquare();
         int[] var8;
         int[] var9;
         if (var5) {
            if (var6) {
               var8 = DoubleDoorNorthOpenXOffset;
               var9 = DoubleDoorNorthOpenYOffset;
            } else {
               var8 = DoubleDoorNorthClosedXOffset;
               var9 = DoubleDoorNorthClosedYOffset;
            }
         } else if (var6) {
            var8 = DoubleDoorWestOpenXOffset;
            var9 = DoubleDoorWestOpenYOffset;
         } else {
            var8 = DoubleDoorWestClosedXOffset;
            var9 = DoubleDoorWestClosedYOffset;
         }

         int var10 = var7.getX() - var8[var2 - 1];
         int var11 = var7.getY() - var9[var2 - 1];
         int var12 = var10 + var8[var1 - 1];
         int var13 = var11 + var9[var1 - 1];
         var7 = IsoWorld.instance.CurrentCell.getGridSquare(var12, var13, var7.getZ());
         if (var7 == null) {
            return null;
         } else {
            ArrayList var14 = var7.getSpecialObjects();
            int var15;
            IsoObject var16;
            if (var3 != null) {
               for(var15 = 0; var15 < var14.size(); ++var15) {
                  var16 = (IsoObject)var14.get(var15);
                  if (var16 instanceof IsoDoor && ((IsoDoor)var16).north == var5 && getDoubleDoorIndex(var16) == var1) {
                     return var16;
                  }
               }
            }

            if (var4 != null) {
               for(var15 = 0; var15 < var14.size(); ++var15) {
                  var16 = (IsoObject)var14.get(var15);
                  if (var16 instanceof IsoThumpable && ((IsoThumpable)var16).north == var5 && getDoubleDoorIndex(var16) == var1) {
                     return var16;
                  }
               }
            }

            return null;
         }
      }
   }

   public static boolean isDoubleDoorObstructed(IsoObject var0) {
      int var1 = getDoubleDoorIndex(var0);
      if (var1 == -1) {
         return false;
      } else {
         IsoDoor var2 = var0 instanceof IsoDoor ? (IsoDoor)var0 : null;
         IsoThumpable var3 = var0 instanceof IsoThumpable ? (IsoThumpable)var0 : null;
         boolean var4 = var2 == null ? var3.north : var2.north;
         boolean var5 = var2 == null ? var3.open : var2.open;
         IsoGridSquare var6 = var0.getSquare();
         int[] var7;
         int[] var8;
         if (var4) {
            if (var5) {
               var7 = DoubleDoorNorthOpenXOffset;
               var8 = DoubleDoorNorthOpenYOffset;
            } else {
               var7 = DoubleDoorNorthClosedXOffset;
               var8 = DoubleDoorNorthClosedYOffset;
            }
         } else if (var5) {
            var7 = DoubleDoorWestOpenXOffset;
            var8 = DoubleDoorWestOpenYOffset;
         } else {
            var7 = DoubleDoorWestClosedXOffset;
            var8 = DoubleDoorWestClosedYOffset;
         }

         int var9 = var6.getX() - var7[var1 - 1];
         int var10 = var6.getY() - var8[var1 - 1];
         int var11 = var9;
         int var12 = var10 + (var4 ? 0 : -3);
         int var13 = var9 + (var4 ? 4 : 2);
         int var14 = var12 + (var4 ? 2 : 4);
         int var15 = var6.getZ();

         int var16;
         int var17;
         for(var16 = var12; var16 < var14; ++var16) {
            for(var17 = var11; var17 < var13; ++var17) {
               IsoGridSquare var18 = IsoWorld.instance.CurrentCell.getGridSquare(var17, var16, var15);
               if (var18 != null && (var18.isSolid() || var18.isSolidTrans() || var18.Has(IsoObjectType.tree))) {
                  return true;
               }
            }
         }

         var16 = (var11 - 4) / 10;
         var17 = (var12 - 4) / 10;
         int var27 = (int)Math.ceil((double)((var13 + 4) / 10));
         int var19 = (int)Math.ceil((double)((var14 + 4) / 10));

         for(int var20 = var17; var20 <= var19; ++var20) {
            for(int var21 = var16; var21 <= var27; ++var21) {
               IsoChunk var22 = GameServer.bServer ? ServerMap.instance.getChunk(var21, var20) : IsoWorld.instance.CurrentCell.getChunk(var21, var20);
               if (var22 != null) {
                  for(int var23 = 0; var23 < var22.vehicles.size(); ++var23) {
                     BaseVehicle var24 = (BaseVehicle)var22.vehicles.get(var23);

                     for(int var25 = var12; var25 < var14; ++var25) {
                        for(int var26 = var11; var26 < var13; ++var26) {
                           if (var24.isIntersectingSquare(var26, var25, var15)) {
                              return true;
                           }
                        }
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   public static enum DoorType {
      WeakWooden,
      StrongWooden;
   }
}
