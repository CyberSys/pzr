package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;

public class IsoThumpable extends IsoObject implements BarricadeAble, Thumpable {
   private KahluaTable table;
   private KahluaTable modData;
   public Boolean isDoor = false;
   public Boolean isDoorFrame = false;
   public String breakSound = "BreakObject";
   private boolean isCorner = false;
   private boolean isFloor = false;
   private boolean blockAllTheSquare = false;
   public boolean Locked = false;
   public int MaxHealth = 500;
   public int Health = 500;
   public int PushedMaxStrength = 0;
   public int PushedStrength = 0;
   IsoSprite closedSprite;
   public boolean north = false;
   public String name = "";
   private int thumpDmg = 8;
   private float crossSpeed = 1.0F;
   int gid = -1;
   public boolean open = false;
   IsoSprite openSprite;
   private boolean destroyed = false;
   private boolean canBarricade = false;
   public boolean canPassThrough = false;
   private boolean isStairs = false;
   private boolean isContainer = false;
   private boolean dismantable = false;
   private boolean canBePlastered = false;
   private boolean paintable = false;
   private boolean isThumpable = true;
   private boolean isHoppable = false;
   private int lightSourceRadius = -1;
   private int lightSourceLife = -1;
   private int lightSourceXOffset = 0;
   private int lightSourceYOffset = 0;
   private boolean lightSourceOn = false;
   private IsoLightSource lightSource = null;
   private String lightSourceFuel = null;
   private float lifeLeft = -1.0F;
   private float lifeDelta = 0.0F;
   private boolean haveFuel = false;
   private float updateAccumulator = 0.0F;
   private float lastUpdateHours = -1.0F;
   public int keyId = -1;
   private boolean lockedByKey = false;
   public boolean lockedByPadlock = false;
   private boolean canBeLockByPadlock = false;
   public int lockedByCode = 0;
   public int OldNumPlanks = 0;
   public String thumpSound = "ZombieThumpGeneric";
   public static Vector2 tempo = new Vector2();

   public KahluaTable getModData() {
      if (this.modData == null) {
         this.modData = LuaManager.platform.newTable();
      }

      return this.modData;
   }

   public void setModData(KahluaTable var1) {
      this.modData = var1;
   }

   public boolean hasModData() {
      return this.modData != null && !this.modData.isEmpty();
   }

   public boolean isCanPassThrough() {
      return this.canPassThrough;
   }

   public void setCanPassThrough(boolean var1) {
      this.canPassThrough = var1;
   }

   public boolean isBlockAllTheSquare() {
      return this.blockAllTheSquare;
   }

   public void setBlockAllTheSquare(boolean var1) {
      this.blockAllTheSquare = var1;
   }

   public void setIsDismantable(boolean var1) {
      this.dismantable = var1;
   }

   public boolean isDismantable() {
      return this.dismantable;
   }

   public float getCrossSpeed() {
      return this.crossSpeed;
   }

   public void setCrossSpeed(float var1) {
      this.crossSpeed = var1;
   }

   public void setIsFloor(boolean var1) {
      this.isFloor = var1;
   }

   public boolean isCorner() {
      return this.isCorner;
   }

   public boolean isFloor() {
      return this.isFloor;
   }

   public void setIsContainer(boolean var1) {
      this.isContainer = var1;
      if (var1) {
         this.container = new ItemContainer("crate", this.square, this, 6, 6);
         if (this.sprite.getProperties().Is("ContainerCapacity")) {
            this.container.Capacity = Integer.parseInt(this.sprite.getProperties().Val("ContainerCapacity"));
         }

         this.container.setExplored(true);
      }

   }

   public void setIsStairs(boolean var1) {
      this.isStairs = var1;
   }

   public boolean isStairs() {
      return this.isStairs;
   }

   public boolean isWindow() {
      return this.sprite != null && (this.sprite.getProperties().Is(IsoFlagType.WindowN) || this.sprite.getProperties().Is(IsoFlagType.WindowW));
   }

   public String getObjectName() {
      return "Thumpable";
   }

   public IsoThumpable(IsoCell var1) {
      super(var1);
   }

   public void setCorner(boolean var1) {
      this.isCorner = var1;
   }

   public void setCanBarricade(boolean var1) {
      this.canBarricade = var1;
   }

   public boolean getCanBarricade() {
      return this.canBarricade;
   }

   public void setHealth(int var1) {
      this.Health = var1;
   }

   public int getHealth() {
      return this.Health;
   }

   public void setMaxHealth(int var1) {
      this.MaxHealth = var1;
   }

   public int getMaxHealth() {
      return this.MaxHealth;
   }

   public void setThumpDmg(Integer var1) {
      this.thumpDmg = var1;
   }

   public void setBreakSound(String var1) {
      this.breakSound = var1;
   }

   public boolean isDoor() {
      return this.isDoor;
   }

   public boolean getNorth() {
      return this.north;
   }

   public Vector2 getFacingPosition(Vector2 var1) {
      if (this.square == null) {
         return var1.set(0.0F, 0.0F);
      } else if (!this.isDoor && !this.isDoorFrame && !this.isWindow() && !this.isHoppable && (this.getProperties() == null || !this.getProperties().Is(IsoFlagType.collideN) && !this.getProperties().Is(IsoFlagType.collideW))) {
         return var1.set(this.getX() + 0.5F, this.getY() + 0.5F);
      } else {
         return this.north ? var1.set(this.getX() + 0.5F, this.getY()) : var1.set(this.getX(), this.getY() + 0.5F);
      }
   }

   public boolean isDoorFrame() {
      return this.isDoorFrame;
   }

   public void setIsDoor(boolean var1) {
      this.isDoor = var1;
   }

   public void setIsDoorFrame(boolean var1) {
      this.isDoorFrame = var1;
   }

   public void setSprite(String var1) {
      this.closedSprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(var1);
      this.sprite = this.closedSprite;
   }

   public void setSpriteFromName(String var1) {
      this.sprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(var1);
   }

   public void setClosedSprite(IsoSprite var1) {
      this.closedSprite = var1;
      this.sprite = this.closedSprite;
   }

   public void setOpenSprite(IsoSprite var1) {
      this.openSprite = var1;
   }

   public IsoThumpable(IsoCell var1, IsoGridSquare var2, String var3, String var4, boolean var5, KahluaTable var6) {
      this.OutlineOnMouseover = true;
      this.PushedMaxStrength = this.PushedStrength = 2500;
      this.openSprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(var4);
      this.closedSprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(var3);
      this.table = var6;
      this.sprite = this.closedSprite;
      this.square = var2;
      this.north = var5;
   }

   public IsoThumpable(IsoCell var1, IsoGridSquare var2, String var3, boolean var4, KahluaTable var5) {
      this.OutlineOnMouseover = true;
      this.PushedMaxStrength = this.PushedStrength = 2500;
      this.closedSprite = IsoWorld.instance.CurrentCell.SpriteManager.getSprite(var3);
      this.table = var5;
      this.sprite = this.closedSprite;
      this.square = var2;
      this.north = var4;
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
      this.OutlineOnMouseover = true;
      this.PushedMaxStrength = this.PushedStrength = 2500;
      if (var1.getInt() == 1) {
         this.openSprite = IsoSprite.getSprite(IsoWorld.instance.spriteManager, var1.getInt());
      }

      this.thumpDmg = var1.getInt();
      this.name = GameWindow.ReadString(var1);
      this.isDoor = var1.get() == 1;
      this.isDoorFrame = var1.get() == 1;
      this.isCorner = var1.get() == 1;
      this.isStairs = var1.get() == 1;
      this.isContainer = var1.get() == 1;
      this.isFloor = var1.get() == 1;
      this.canBarricade = var1.get() == 1;
      this.canPassThrough = var1.get() == 1;
      this.dismantable = var1.get() == 1;
      this.canBePlastered = var1.get() == 1;
      this.paintable = var1.get() == 1;
      this.crossSpeed = var1.getFloat();
      if (var1.get() != 0) {
         if (this.table == null) {
            this.table = LuaManager.platform.newTable();
         }

         this.table.load(var1, var2);
      }

      if (var1.get() != 0) {
         if (this.modData == null) {
            this.modData = LuaManager.platform.newTable();
         }

         this.modData.load(var1, var2);
      }

      this.blockAllTheSquare = var1.get() == 1;
      this.isThumpable = var1.get() == 1;
      this.isHoppable = var1.get() == 1;
      if (var2 >= 26) {
         this.setLightSourceLife(var1.getInt());
         this.setLightSourceRadius(var1.getInt());
         this.setLightSourceXOffset(var1.getInt());
         this.setLightSourceYOffset(var1.getInt());
         this.setLightSourceFuel(GameWindow.ReadString(var1));
         this.setLifeDelta(var1.getFloat());
         this.setLifeLeft(var1.getFloat());
         this.setLightSourceOn(var1.get() == 1);
      }

      if (var2 >= 28) {
         this.haveFuel = var1.get() == 1;
      } else if (this.getLifeDelta() > 0.0F && this.getLifeLeft() > 0.0F) {
         this.setHaveFuel(true);
      }

      if (this.getLightSourceFuel() != null) {
         boolean var6 = this.isLightSourceOn();
         this.createLightSource(this.getLightSourceRadius(), this.getLightSourceXOffset(), this.getLightSourceYOffset(), 0, this.getLightSourceLife(), this.getLightSourceFuel(), (InventoryItem)null, (IsoGameCharacter)null);
         if (this.lightSource != null) {
            this.getLightSource().setActive(var6);
         }

         this.setLightSourceOn(var6);
      }

      if (var2 >= 57) {
         this.keyId = var1.getInt();
         this.lockedByKey = var1.get() == 1;
         this.lockedByPadlock = var1.get() == 1;
         this.canBeLockByPadlock = var1.get() == 1;
         this.lockedByCode = var1.getInt();
      }

      if (var2 >= 91) {
         this.thumpSound = GameWindow.ReadString(var1);
         if ("thumpa2".equals(this.thumpSound)) {
            this.thumpSound = "ZombieThumpGeneric";
         }

         if ("metalthump".equals(this.thumpSound)) {
            this.thumpSound = "ZombieThumpMetal";
         }
      }

      if (var2 >= 132) {
         this.lastUpdateHours = var1.getFloat();
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
      var1.putInt(this.closedSprite != null ? this.closedSprite.ID : 0);
      if (this.openSprite == null) {
         var1.putInt(0);
      } else {
         var1.putInt(1);
         var1.putInt(this.openSprite.ID);
      }

      var1.putInt(this.thumpDmg);
      GameWindow.WriteString(var1, this.name);
      var1.put((byte)(this.isDoor ? 1 : 0));
      var1.put((byte)(this.isDoorFrame ? 1 : 0));
      var1.put((byte)(this.isCorner ? 1 : 0));
      var1.put((byte)(this.isStairs ? 1 : 0));
      var1.put((byte)(this.isContainer ? 1 : 0));
      var1.put((byte)(this.isFloor ? 1 : 0));
      var1.put((byte)(this.canBarricade ? 1 : 0));
      var1.put((byte)(this.canPassThrough ? 1 : 0));
      var1.put((byte)(this.dismantable ? 1 : 0));
      var1.put((byte)(this.canBePlastered ? 1 : 0));
      var1.put((byte)(this.paintable ? 1 : 0));
      var1.putFloat(this.crossSpeed);
      if (this.table != null && !this.table.isEmpty()) {
         var1.put((byte)1);
         this.table.save(var1);
      } else {
         var1.put((byte)0);
      }

      if (this.modData != null && !this.modData.isEmpty()) {
         var1.put((byte)1);
         this.modData.save(var1);
      } else {
         var1.put((byte)0);
      }

      var1.put((byte)(this.blockAllTheSquare ? 1 : 0));
      var1.put((byte)(this.isThumpable ? 1 : 0));
      var1.put((byte)(this.isHoppable ? 1 : 0));
      var1.putInt(this.getLightSourceLife());
      var1.putInt(this.getLightSourceRadius());
      var1.putInt(this.getLightSourceXOffset());
      var1.putInt(this.getLightSourceYOffset());
      GameWindow.WriteString(var1, this.getLightSourceFuel() != null ? this.getLightSourceFuel() : "");
      var1.putFloat(this.getLifeDelta());
      var1.putFloat(this.getLifeLeft());
      var1.put((byte)(this.isLightSourceOn() ? 1 : 0));
      var1.put((byte)(this.haveFuel ? 1 : 0));
      var1.putInt(this.getKeyId());
      var1.put((byte)(this.isLockedByKey() ? 1 : 0));
      var1.put((byte)(this.isLockedByPadlock() ? 1 : 0));
      var1.put((byte)(this.canBeLockByPadlock() ? 1 : 0));
      var1.putInt(this.getLockedByCode());
      GameWindow.WriteString(var1, this.thumpSound);
      var1.putFloat(this.lastUpdateHours);
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
      if (this.isDoor) {
         IsoGridSquare var3 = IsoPlayer.getInstance().getCurrentSquare();
         if (IsoUtils.DistanceTo((float)this.square.getX(), (float)this.square.getY(), IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY()) < 2.0F && this.square.getZ() == var3.getZ() && (this.getOppositeSquare() == var3 || !this.square.isSomethingTo(var3))) {
            this.ToggleDoor(IsoPlayer.getInstance());
         }
      }

      return true;
   }

   public boolean TestPathfindCollide(IsoMovingObject var1, IsoGridSquare var2, IsoGridSquare var3) {
      boolean var4 = this.north;
      if (var1 instanceof IsoSurvivor && ((IsoSurvivor)var1).getInventory().contains("Hammer")) {
         return false;
      } else if (this.open) {
         return false;
      } else {
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
      if (var1 instanceof IsoPlayer && ((IsoPlayer)var1).isNoClip()) {
         return false;
      } else {
         boolean var4 = this.north;
         if (this.open) {
            return false;
         } else if (this.blockAllTheSquare) {
            if (var2 != this.square) {
               if (var1 != null) {
                  var1.collideWith(this);
               }

               return true;
            } else {
               return false;
            }
         } else {
            if (var2 == this.square) {
               if (var4 && var3.getY() < var2.getY()) {
                  if (var1 != null) {
                     var1.collideWith(this);
                  }

                  if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
                     return true;
                  }
               }

               if (!var4 && var3.getX() < var2.getX()) {
                  if (var1 != null) {
                     var1.collideWith(this);
                  }

                  if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
                     return true;
                  }
               }
            } else {
               if (var4 && var3.getY() > var2.getY()) {
                  if (var1 != null) {
                     var1.collideWith(this);
                  }

                  if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
                     return true;
                  }
               }

               if (!var4 && var3.getX() > var2.getX()) {
                  if (var1 != null) {
                     var1.collideWith(this);
                  }

                  if (!this.canPassThrough && !this.isStairs && !this.isCorner) {
                     return true;
                  }
               }
            }

            if (this.isCorner) {
               if (var3.getY() < var2.getY() && var3.getX() < var2.getX()) {
                  if (var1 != null) {
                     var1.collideWith(this);
                  }

                  if (!this.canPassThrough) {
                     return true;
                  }
               }

               if (var3.getY() > var2.getY() && var3.getX() > var2.getX()) {
                  if (var1 != null) {
                     var1.collideWith(this);
                  }

                  if (!this.canPassThrough) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   public IsoObject.VisionResult TestVision(IsoGridSquare var1, IsoGridSquare var2) {
      if (this.canPassThrough) {
         return IsoObject.VisionResult.NoEffect;
      } else {
         boolean var3 = this.north;
         if (this.open) {
            var3 = !var3;
         }

         if (var2.getZ() != var1.getZ()) {
            return IsoObject.VisionResult.NoEffect;
         } else {
            if (var1 == this.square) {
               if (var3 && var2.getY() < var1.getY()) {
                  if (this.isWindow()) {
                     return IsoObject.VisionResult.Unblocked;
                  }

                  return IsoObject.VisionResult.Blocked;
               }

               if (!var3 && var2.getX() < var1.getX()) {
                  if (this.isWindow()) {
                     return IsoObject.VisionResult.Unblocked;
                  }

                  return IsoObject.VisionResult.Blocked;
               }
            } else {
               if (var3 && var2.getY() > var1.getY()) {
                  if (this.isWindow()) {
                     return IsoObject.VisionResult.Unblocked;
                  }

                  return IsoObject.VisionResult.Blocked;
               }

               if (!var3 && var2.getX() > var1.getX()) {
                  if (this.isWindow()) {
                     return IsoObject.VisionResult.Unblocked;
                  }

                  return IsoObject.VisionResult.Blocked;
               }
            }

            return IsoObject.VisionResult.NoEffect;
         }
      }
   }

   public void Thump(IsoMovingObject var1) {
      if (SandboxOptions.instance.Lore.ThumpOnConstruction.getValue()) {
         if (this.isThumpable()) {
            if (!this.isDestroyed()) {
               int var4;
               if (var1 instanceof IsoZombie) {
                  if (this.isDoor() || this.isWindow()) {
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
                  }

                  if (((IsoZombie)var1).cognition == 1 && this.isDoor() && !this.IsOpen()) {
                     this.ToggleDoor((IsoGameCharacter)var1);
                     return;
                  }

                  var4 = var1.getCurrentSquare().getMovingObjects().size();
                  if (var1.getCurrentSquare().getW() != null) {
                     var4 += var1.getCurrentSquare().getW().getMovingObjects().size();
                  }

                  if (var1.getCurrentSquare().getE() != null) {
                     var4 += var1.getCurrentSquare().getE().getMovingObjects().size();
                  }

                  if (var1.getCurrentSquare().getS() != null) {
                     var4 += var1.getCurrentSquare().getS().getMovingObjects().size();
                  }

                  if (var1.getCurrentSquare().getN() != null) {
                     var4 += var1.getCurrentSquare().getN().getMovingObjects().size();
                  }

                  int var3 = this.thumpDmg;
                  if (var4 >= var3) {
                     this.Damage(1 * ThumpState.getFastForwardDamageMultiplier());
                  }

                  WorldSoundManager.instance.addSound(var1, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
                  if (this.isDoor()) {
                     this.setRenderEffect(RenderEffectType.Hit_Door, true);
                  }
               }

               if (this.Health <= 0) {
                  var4 = IsoDoor.getDoubleDoorIndex(this);
                  if (var4 == 1 || var4 == 4) {
                     IsoObject var5 = IsoDoor.getDoubleDoorObject(this, var4 == 1 ? 2 : 3);
                     if (var5 != null) {
                        ((IsoThumpable)var5).destroy();
                     }
                  }

                  ((IsoGameCharacter)var1).getEmitter().playSound(this.breakSound, this);
                  if (GameServer.bServer) {
                     GameServer.PlayWorldSoundServer(this.breakSound, false, var1.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
                  }

                  WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
                  var1.setThumpTarget((Thumpable)null);
                  this.destroy();
               }

            }
         }
      }
   }

   public void WeaponHit(IsoGameCharacter var1, HandWeapon var2) {
      if (GameClient.bClient) {
         if (var1 instanceof IsoPlayer) {
            GameClient.instance.sendWeaponHit((IsoPlayer)var1, var2, this);
         }

         if (this.isDoor()) {
            this.setRenderEffect(RenderEffectType.Hit_Door, true);
         }

      } else if (!this.open || !this.isDoor) {
         if (!this.isDestroyed()) {
            if (this.isDoor() || this.isWindow()) {
               IsoBarricade var3 = this.getBarricadeForCharacter(var1);
               if (var3 != null) {
                  var3.WeaponHit(var1, var2);
                  return;
               }

               var3 = this.getBarricadeOppositeCharacter(var1);
               if (var3 != null) {
                  var3.WeaponHit(var1, var2);
                  return;
               }
            }

            this.Damage(var2.getDoorDamage());
            if (var2.getDoorHitSound() != null) {
               var1.getEmitter().playSound(var2.getDoorHitSound(), this);
               if (GameServer.bServer) {
                  GameServer.PlayWorldSoundServer(var2.getDoorHitSound(), false, this.getSquare(), 0.2F, 20.0F, 1.0F, false);
               }
            }

            WorldSoundManager.instance.addSound(var1, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
            if (this.isDoor()) {
               this.setRenderEffect(RenderEffectType.Hit_Door, true);
            }

            if (!this.IsStrengthenedByPushedItems() && this.Health <= 0 || this.IsStrengthenedByPushedItems() && this.Health <= -this.PushedMaxStrength) {
               var1.getEmitter().playSound(this.breakSound, this);
               WorldSoundManager.instance.addSound(var1, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
               int var5 = IsoDoor.getDoubleDoorIndex(this);
               if (var5 == 1 || var5 == 4) {
                  IsoObject var4 = IsoDoor.getDoubleDoorObject(this, var5 == 1 ? 2 : 3);
                  if (var4 != null) {
                     ((IsoThumpable)var4).destroy();
                  }
               }

               if (GameClient.bClient) {
                  GameClient.instance.sendClientCommandV((IsoPlayer)null, "object", "OnDestroyIsoThumpable", "x", (int)this.getX(), "y", (int)this.getY(), "z", (int)this.getZ(), "index", this.getObjectIndex());
               }

               LuaEventManager.triggerEvent("OnDestroyIsoThumpable", this, (Object)null);
               this.destroyed = true;
               if (this.getObjectIndex() != -1) {
                  this.square.transmitRemoveItemFromSquare(this);
               }
            }

         }
      }
   }

   public IsoGridSquare getOtherSideOfDoor(IsoGameCharacter var1) {
      if (this.north) {
         return var1.getCurrentSquare().getRoom() == this.square.getRoom() ? IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ()) : IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ());
      } else {
         return var1.getCurrentSquare().getRoom() == this.square.getRoom() ? IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ()) : IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ());
      }
   }

   public void ToggleDoorActual(IsoGameCharacter var1) {
      if (this.isBarricaded()) {
         if (var1 != null) {
            var1.getEmitter().playSound("DoorIsBlocked", this);
            var1.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBarricaded"), 255, 255, 255, 256.0F);
         }

      } else if (this.isLockedByKey() && var1 != null && var1 instanceof IsoPlayer && var1.getInventory().haveThisKeyId(this.getKeyId()) == null) {
         var1.getEmitter().playSound("DoorIsLocked", this);
      } else {
         if (this.isLockedByKey()) {
            var1.getEmitter().playSound("UnlockDoor", this);
         }

         this.DirtySlice();
         this.square.InvalidateSpecialObjectPaths();
         if (this.Locked && var1 != null && var1 instanceof IsoPlayer && var1.getCurrentSquare().getRoom() == null && !this.open) {
            var1.getEmitter().playSound("DoorIsLocked", this);
            if (var1 instanceof IsoSurvivor) {
               var1.getMasterBehaviorList().reset();
            }

         } else {
            if (var1 instanceof IsoPlayer) {
               for(int var2 = 0; var2 < IsoPlayer.numPlayers; ++var2) {
                  LosUtil.cachecleared[var2] = true;
               }

               IsoGridSquare.setRecalcLightTime(-1);
               GameTime.instance.lightSourceUpdate = 100.0F;
            }

            if (this.getSprite().getProperties().Is("DoubleDoor")) {
               if (IsoDoor.isDoubleDoorObstructed(this)) {
                  if (var1 != null) {
                     var1.getEmitter().playSound("DoorIsBlocked", this);
                     var1.setHaloNote(Translator.getText("IGUI_PlayerText_DoorBlocked"), 255, 255, 255, 256.0F);
                  }

               } else {
                  boolean var3 = this.open;
                  IsoDoor.toggleDoubleDoor(this, true);
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
               this.sprite = this.closedSprite;
               this.open = !this.open;
               this.setLockedByKey(false);
               if (this.open) {
                  var1.getEmitter().playSound("OpenDoor", this);
                  this.sprite = this.openSprite;
               } else {
                  var1.getEmitter().playSound("CloseDoor", this);
               }

               this.square.RecalcProperties();
               this.syncIsoObject(false, (byte)(this.open ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
               LuaEventManager.triggerEvent("OnContainerUpdate");
            }
         }
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

   public boolean isObstructed() {
      return IsoDoor.isDoorObstructed(this);
   }

   public boolean haveSheetRope() {
      return IsoWindow.isTopOfSheetRopeHere(this.square, this.north);
   }

   public int countAddSheetRope() {
      return !this.isHoppable() && !this.isWindow() ? 0 : IsoWindow.countAddSheetRope(this.square, this.north);
   }

   public boolean canAddSheetRope() {
      return !this.isHoppable() && !this.isWindow() ? false : IsoWindow.canAddSheetRope(this.square, this.north);
   }

   public boolean addSheetRope(IsoPlayer var1, String var2) {
      return !this.canAddSheetRope() ? false : IsoWindow.addSheetRope(var1, this.square, this.north, var2);
   }

   public boolean removeSheetRope(IsoPlayer var1) {
      return this.haveSheetRope() ? IsoWindow.removeSheetRope(var1, this.square, this.north) : false;
   }

   public void createLightSource(int var1, int var2, int var3, int var4, int var5, String var6, InventoryItem var7, IsoGameCharacter var8) {
      this.setLightSourceXOffset(var2);
      this.setLightSourceYOffset(var3);
      this.setLightSourceRadius(var1);
      this.setLightSourceFuel(var6);
      if (var7 != null) {
         if (!(var7 instanceof DrainableComboItem)) {
            this.setLifeLeft(1.0F);
            this.setHaveFuel(true);
         } else {
            this.setLifeLeft(((DrainableComboItem)var7).getUsedDelta());
            this.setLifeDelta(((DrainableComboItem)var7).getUseDelta());
            this.setHaveFuel(!"Base.Torch".equals(var7.getFullType()) || ((DrainableComboItem)var7).getUsedDelta() > 0.0F);
         }

         if (var8.getPrimaryHandItem() == var7) {
            var8.setPrimaryHandItem((InventoryItem)null);
         }

         if (var8.getSecondaryHandItem() == var7) {
            var8.setSecondaryHandItem((InventoryItem)null);
         }

         var8.getInventory().Remove(var7);
      }

      this.setLightSourceOn(this.haveFuel);
      if (this.lightSource != null) {
         this.lightSource.setActive(this.isLightSourceOn());
      }

   }

   public InventoryItem insertNewFuel(InventoryItem var1, IsoGameCharacter var2) {
      if (var1 != null) {
         InventoryItem var3 = this.removeCurrentFuel(var2);
         if (var2 != null) {
            if (var2.getPrimaryHandItem() == var1) {
               var2.setPrimaryHandItem((InventoryItem)null);
            }

            if (var2.getSecondaryHandItem() == var1) {
               var2.setSecondaryHandItem((InventoryItem)null);
            }

            var2.getInventory().Remove(var1);
         }

         if (var1 instanceof DrainableComboItem) {
            this.setLifeLeft(((DrainableComboItem)var1).getUsedDelta());
            this.setLifeDelta(((DrainableComboItem)var1).getUseDelta());
         } else {
            this.setLifeLeft(1.0F);
         }

         this.setHaveFuel(true);
         this.toggleLightSource(true);
         return var3;
      } else {
         return null;
      }
   }

   public InventoryItem removeCurrentFuel(IsoGameCharacter var1) {
      if (this.haveFuel()) {
         InventoryItem var2 = InventoryItemFactory.CreateItem(this.getLightSourceFuel());
         if (var2 instanceof DrainableComboItem) {
            ((DrainableComboItem)var2).setUsedDelta(this.getLifeLeft());
         }

         if (var1 != null) {
            var1.getInventory().AddItem(var2);
         }

         this.setLifeLeft(0.0F);
         this.setLifeDelta(-1.0F);
         this.toggleLightSource(false);
         this.setHaveFuel(false);
         return var2;
      } else {
         return null;
      }
   }

   private int calcLightSourceX() {
      int var1 = (int)this.getX();
      int var2 = (int)this.getY();
      if (this.lightSourceXOffset != 0) {
         for(int var3 = 1; var3 <= Math.abs(this.lightSourceXOffset); ++var3) {
            int var4 = this.lightSourceXOffset > 0 ? 1 : -1;
            LosUtil.TestResults var5 = LosUtil.lineClear(this.getCell(), (int)this.getX(), (int)this.getY(), (int)this.getZ(), var1 + var4, var2, (int)this.getZ(), false);
            if (var5 == LosUtil.TestResults.Blocked || var5 == LosUtil.TestResults.ClearThroughWindow) {
               break;
            }

            var1 += var4;
         }
      }

      return var1;
   }

   private int calcLightSourceY() {
      int var1 = (int)this.getX();
      int var2 = (int)this.getY();
      if (this.lightSourceYOffset != 0) {
         for(int var3 = 1; var3 <= Math.abs(this.lightSourceYOffset); ++var3) {
            int var4 = this.lightSourceYOffset > 0 ? 1 : -1;
            LosUtil.TestResults var5 = LosUtil.lineClear(this.getCell(), (int)this.getX(), (int)this.getY(), (int)this.getZ(), var1, var2 + var4, (int)this.getZ(), false);
            if (var5 == LosUtil.TestResults.Blocked || var5 == LosUtil.TestResults.ClearThroughWindow) {
               break;
            }

            var2 += var4;
         }
      }

      return var2;
   }

   public void update() {
      if (this.getObjectIndex() != -1) {
         int var4;
         if (!GameServer.bServer) {
            if (this.lightSource != null && !this.lightSource.isInBounds()) {
               this.lightSource = null;
            }

            byte var1;
            int var2;
            int var3;
            if (this.lightSourceFuel != null && !this.lightSourceFuel.isEmpty() && this.lightSource == null && this.square != null) {
               var1 = 0;
               var2 = this.calcLightSourceX();
               var3 = this.calcLightSourceY();
               if (IsoWorld.instance.CurrentCell.isInChunkMap(var2, var3)) {
                  var4 = this.getLightSourceLife();
                  this.setLightSource(new IsoLightSource(var2, var3, (int)this.getZ() + var1, 1.0F, 1.0F, 1.0F, this.lightSourceRadius, var4 > 0 ? var4 : -1));
                  this.lightSource.setActive(this.isLightSourceOn());
                  IsoWorld.instance.getCell().getLamppostPositions().add(this.getLightSource());
               }
            }

            if (this.lightSource != null && this.lightSource.isActive()) {
               var1 = 0;
               var2 = this.calcLightSourceX();
               var3 = this.calcLightSourceY();
               if (var2 != this.lightSource.x || var3 != this.lightSource.y) {
                  this.getCell().removeLamppost(this.lightSource);
                  var4 = this.getLightSourceLife();
                  this.setLightSource(new IsoLightSource(var2, var3, (int)this.getZ() + var1, 1.0F, 1.0F, 1.0F, this.lightSourceRadius, var4 > 0 ? var4 : -1));
                  this.lightSource.setActive(this.isLightSourceOn());
                  IsoWorld.instance.getCell().getLamppostPositions().add(this.getLightSource());
               }
            }
         }

         if (this.getLifeLeft() > -1.0F) {
            float var5 = (float)GameTime.getInstance().getWorldAgeHours();
            if (this.lastUpdateHours == -1.0F) {
               this.lastUpdateHours = var5;
            } else if (this.lastUpdateHours > var5) {
               this.lastUpdateHours = var5;
            }

            float var6 = var5 - this.lastUpdateHours;
            this.lastUpdateHours = var5;
            if (this.isLightSourceOn()) {
               this.updateAccumulator += var6;
               var4 = (int)Math.floor((double)(this.updateAccumulator / 0.004166667F));
               if (var4 > 0) {
                  this.updateAccumulator -= 0.004166667F * (float)var4;
                  this.setLifeLeft(this.getLifeLeft() - this.getLifeDelta() * (float)var4);
                  if (this.getLifeLeft() <= 0.0F) {
                     this.setLifeLeft(0.0F);
                     this.toggleLightSource(false);
                  }
               }
            } else {
               this.updateAccumulator = 0.0F;
            }
         }

         this.checkHaveElectricity();
      }
   }

   void Damage(int var1) {
      if (this.isThumpable()) {
         this.DirtySlice();
         this.Health -= var1;
      }
   }

   public void destroy() {
      if (!this.destroyed) {
         if (this.getObjectIndex() != -1) {
            if (GameClient.bClient) {
               GameClient.instance.sendClientCommandV((IsoPlayer)null, "object", "OnDestroyIsoThumpable", "x", this.square.getX(), "y", this.square.getY(), "z", this.square.getZ(), "index", this.getObjectIndex());
            }

            LuaEventManager.triggerEvent("OnDestroyIsoThumpable", this, (Object)null);
            this.Health = 0;
            this.destroyed = true;
            if (this.getObjectIndex() != -1) {
               this.square.transmitRemoveItemFromSquare(this);
            }

         }
      }
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

   public void setName(String var1) {
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }

   public void setIsDoor(Boolean var1) {
      this.isDoor = var1;
   }

   public KahluaTable getTable() {
      return this.table;
   }

   public void setTable(KahluaTable var1) {
      this.table = var1;
   }

   public boolean canBePlastered() {
      return this.canBePlastered;
   }

   public void setCanBePlastered(boolean var1) {
      this.canBePlastered = var1;
   }

   public boolean isPaintable() {
      return this.paintable;
   }

   public void setPaintable(boolean var1) {
      this.paintable = var1;
   }

   public boolean isLocked() {
      return this.Locked;
   }

   public void setIsLocked(boolean var1) {
      this.Locked = var1;
   }

   public boolean isThumpable() {
      return this.isBarricaded() ? true : this.isThumpable;
   }

   public void setIsThumpable(boolean var1) {
      this.isThumpable = var1;
   }

   public void setIsHoppable(boolean var1) {
      this.setHoppable(var1);
   }

   public IsoSprite getOpenSprite() {
      return this.openSprite;
   }

   public boolean isHoppable() {
      return this.isHoppable;
   }

   public void setHoppable(boolean var1) {
      this.isHoppable = var1;
   }

   public int getLightSourceRadius() {
      return this.lightSourceRadius;
   }

   public void setLightSourceRadius(int var1) {
      this.lightSourceRadius = var1;
   }

   public int getLightSourceXOffset() {
      return this.lightSourceXOffset;
   }

   public void setLightSourceXOffset(int var1) {
      this.lightSourceXOffset = var1;
   }

   public int getLightSourceYOffset() {
      return this.lightSourceYOffset;
   }

   public void setLightSourceYOffset(int var1) {
      this.lightSourceYOffset = var1;
   }

   public int getLightSourceLife() {
      return this.lightSourceLife;
   }

   public void setLightSourceLife(int var1) {
      this.lightSourceLife = var1;
   }

   public boolean isLightSourceOn() {
      return this.lightSourceOn;
   }

   public void setLightSourceOn(boolean var1) {
      this.lightSourceOn = var1;
   }

   public IsoLightSource getLightSource() {
      return this.lightSource;
   }

   public void setLightSource(IsoLightSource var1) {
      this.lightSource = var1;
   }

   public void toggleLightSource(boolean var1) {
      this.setLightSourceOn(var1);
      if (this.lightSource != null) {
         this.getLightSource().setActive(var1);
         IsoGridSquare.setRecalcLightTime(-1);
         GameTime.instance.lightSourceUpdate = 100.0F;
      }
   }

   public String getLightSourceFuel() {
      return this.lightSourceFuel;
   }

   public void setLightSourceFuel(String var1) {
      if (var1 != null && var1.isEmpty()) {
         var1 = null;
      }

      this.lightSourceFuel = var1;
   }

   public float getLifeLeft() {
      return this.lifeLeft;
   }

   public void setLifeLeft(float var1) {
      this.lifeLeft = var1;
   }

   public float getLifeDelta() {
      return this.lifeDelta;
   }

   public void setLifeDelta(float var1) {
      this.lifeDelta = var1;
   }

   public boolean haveFuel() {
      return this.haveFuel;
   }

   public void setHaveFuel(boolean var1) {
      this.haveFuel = var1;
   }

   public void syncIsoObjectSend(ByteBufferWriter var1) {
      var1.putInt(this.square.getX());
      var1.putInt(this.square.getY());
      var1.putInt(this.square.getZ());
      byte var2 = (byte)this.square.getObjects().indexOf(this);
      var1.putByte(var2);
      var1.putByte((byte)1);
      if (this.getSprite() != null && this.getSprite().getProperties().Is("DoubleDoor")) {
         var1.putByte((byte)5);
      } else {
         if (this.open) {
            var1.putByte((byte)1);
         } else if (this.lockedByKey) {
            var1.putByte((byte)3);
         } else {
            var1.putByte((byte)4);
         }

      }
   }

   public void syncIsoObject(boolean var1, byte var2, UdpConnection var3, ByteBuffer var4) {
      if (this.square == null) {
         System.out.println("ERROR: " + this.getClass().getSimpleName() + " square is null");
      } else if (this.getObjectIndex() == -1) {
         System.out.println("ERROR: " + this.getClass().getSimpleName() + " not found on square " + this.square.getX() + "," + this.square.getY() + "," + this.square.getZ());
      } else if (this.isDoor()) {
         if (GameClient.bClient && !var1) {
            ByteBufferWriter var9 = GameClient.connection.startPacket();
            PacketTypes.doPacket((short)12, var9);
            this.syncIsoObjectSend(var9);
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

                  while(var5.hasNext()) {
                     var6 = (UdpConnection)var5.next();
                     if (var3 != null && var6.getConnectedGUID() != var3.getConnectedGUID()) {
                        var7 = var6.startPacket();
                        PacketTypes.doPacket((short)12, var7);
                        this.syncIsoObjectSend(var7);
                        var6.endPacketImmediate();
                     }
                  }

                  this.square.revisionUp();
               }

               if (var2 == 5) {
                  IsoDoor.toggleDoubleDoor(this, false);
                  if (GameServer.bServer) {
                     ServerMap.instance.physicsCheck(this.square.getX(), this.square.getY());
                  }
               }

               this.square.InvalidateSpecialObjectPaths();
               this.square.RecalcProperties();

               for(int var8 = 0; var8 < IsoPlayer.numPlayers; ++var8) {
                  LosUtil.cachecleared[var8] = true;
               }

               IsoGridSquare.setRecalcLightTime(-1);
               GameTime.instance.lightSourceUpdate = 100.0F;
               LuaEventManager.triggerEvent("OnContainerUpdate");
            }
         }

      }
   }

   public void addToWorld() {
      super.addToWorld();
      if (!IsoWorld.instance.getCell().getProcessIsoObjects().contains(this)) {
         IsoWorld.instance.getCell().getProcessIsoObjects().add(this);
      }

   }

   public void removeFromWorld() {
      if (this.lightSource != null) {
         IsoWorld.instance.CurrentCell.removeLamppost(this.lightSource);
      }

      super.removeFromWorld();
   }

   public void saveChange(String var1, KahluaTable var2, ByteBuffer var3) {
      super.saveChange(var1, var2, var3);
      if ("lightSource".equals(var1)) {
         var3.put((byte)(this.lightSourceOn ? 1 : 0));
         var3.put((byte)(this.haveFuel ? 1 : 0));
         var3.putFloat(this.lifeLeft);
         var3.putFloat(this.lifeDelta);
      } else if ("paintable".equals(var1)) {
         var3.put((byte)(this.isPaintable() ? 1 : 0));
      }

   }

   public void loadChange(String var1, ByteBuffer var2) {
      super.loadChange(var1, var2);
      if ("lightSource".equals(var1)) {
         boolean var3 = var2.get() == 1;
         this.haveFuel = var2.get() == 1;
         this.lifeLeft = var2.getFloat();
         this.lifeDelta = var2.getFloat();
         if (var3 != this.lightSourceOn) {
            this.toggleLightSource(var3);
         }
      } else if ("paintable".equals(var1)) {
         this.setPaintable(var2.get() == 1);
      }

   }

   public IsoCurtain HasCurtains() {
      IsoGridSquare var1 = this.getInsideSquare();
      int var2;
      if (var1 != null && !var1.getSpecialObjects().isEmpty()) {
         for(var2 = 0; var2 < var1.getSpecialObjects().size(); ++var2) {
            if (var1.getSpecialObjects().get(var2) instanceof IsoCurtain) {
               return (IsoCurtain)var1.getSpecialObjects().get(var2);
            }
         }
      }

      var1 = this.square;
      if (!var1.getSpecialObjects().isEmpty()) {
         for(var2 = 0; var2 < var1.getSpecialObjects().size(); ++var2) {
            if (var1.getSpecialObjects().get(var2) instanceof IsoCurtain) {
               return (IsoCurtain)var1.getSpecialObjects().get(var2);
            }
         }
      }

      return null;
   }

   public IsoGridSquare getInsideSquare() {
      return this.north ? this.square.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ()) : this.square.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
   }

   public IsoGridSquare getOppositeSquare() {
      return this.getInsideSquare();
   }

   public IsoGridSquare getAddSheetSquare(IsoGameCharacter var1) {
      if (var1 != null && var1.getCurrentSquare() != null) {
         IsoGridSquare var2 = var1.getCurrentSquare();
         IsoGridSquare var3 = this.getSquare();
         if (this.north) {
            return var2.getY() < var3.getY() ? this.getCell().getGridSquare(var3.x, var3.y - 1, var3.z) : var3;
         } else {
            return var2.getX() < var3.getX() ? this.getCell().getGridSquare(var3.x - 1, var3.y, var3.z) : var3;
         }
      } else {
         return null;
      }
   }

   public void addSheet(IsoGameCharacter var1) {
      IsoGridSquare var2 = this.getIndoorSquare();
      IsoDirections var3 = IsoDirections.N;
      if (this.north) {
         if (var2 != this.square) {
            var3 = IsoDirections.S;
         }
      } else {
         var3 = IsoDirections.W;
         if (var2 != this.square) {
            var3 = IsoDirections.E;
         }
      }

      if (var1 != null) {
         if (this.north) {
            if (var1.getY() < this.getY()) {
               var2 = this.getCell().getGridSquare((double)this.getX(), (double)(this.getY() - 1.0F), (double)this.getZ());
               var3 = IsoDirections.S;
            } else {
               var2 = this.getSquare();
               var3 = IsoDirections.N;
            }
         } else if (var1.getX() < this.getX()) {
            var2 = this.getCell().getGridSquare((double)(this.getX() - 1.0F), (double)this.getY(), (double)this.getZ());
            var3 = IsoDirections.E;
         } else {
            var2 = this.getSquare();
            var3 = IsoDirections.W;
         }
      }

      if (var2 != null) {
         boolean var4 = true;

         int var5;
         for(var5 = 0; var5 < var2.getSpecialObjects().size(); ++var5) {
            if (var2.getSpecialObjects().get(var5) instanceof IsoCurtain) {
               var4 = false;
            }
         }

         if (var2 != null && var4) {
            var5 = 16;
            if (var3 == IsoDirections.E) {
               ++var5;
            }

            if (var3 == IsoDirections.S) {
               var5 += 3;
            }

            if (var3 == IsoDirections.N) {
               var5 += 2;
            }

            var5 += 4;
            IsoCurtain var6 = new IsoCurtain(this.getCell(), var2, "fixtures_windows_curtains_01_" + var5, this.north);
            var2.AddSpecialTileObject(var6);
            if (GameServer.bServer) {
               var6.transmitCompleteItemToClients();
               var1.sendObjectChange("removeOneOf", new Object[]{"type", "Sheet"});
            } else {
               var1.getInventory().RemoveOneOf("Sheet");
            }

         }
      }
   }

   public IsoGridSquare getIndoorSquare() {
      if (this.square.getRoom() != null) {
         return this.square;
      } else {
         IsoGridSquare var1;
         if (this.north) {
            var1 = IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ());
         } else {
            var1 = IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
         }

         if (var1 != null && var1.getFloor() != null) {
            if (var1.getRoom() != null) {
               return var1;
            } else if (this.square.getFloor() == null) {
               return var1;
            } else {
               String var2 = var1.getFloor().getSprite().getName();
               return var2 != null && var2.startsWith("carpentry_02_") ? var1 : this.square;
            }
         } else {
            return this.square;
         }
      }
   }

   public int getKeyId() {
      return this.keyId;
   }

   public void setKeyId(int var1, boolean var2) {
      if (var2 && this.keyId != var1 && GameClient.bClient) {
         this.keyId = var1;
         this.syncIsoThumpable();
      } else {
         this.keyId = var1;
      }

   }

   public void setKeyId(int var1) {
      this.setKeyId(var1, true);
   }

   public boolean isLockedByKey() {
      return this.lockedByKey;
   }

   public void setLockedByKey(boolean var1) {
      boolean var2 = var1 != this.lockedByKey;
      this.lockedByKey = var1;
      if (!GameServer.bServer && var2) {
         if (var1) {
            this.syncIsoObject(false, (byte)3, (UdpConnection)null, (ByteBuffer)null);
         } else {
            this.syncIsoObject(false, (byte)4, (UdpConnection)null, (ByteBuffer)null);
         }
      }

   }

   public boolean isLockedByPadlock() {
      return this.lockedByPadlock;
   }

   public void syncIsoThumpable() {
      ByteBufferWriter var1 = GameClient.connection.startPacket();
      PacketTypes.doPacket((short)105, var1);
      var1.putInt(this.square.getX());
      var1.putInt(this.square.getY());
      var1.putInt(this.square.getZ());
      byte var2 = (byte)this.square.getObjects().indexOf(this);
      if (var2 == -1) {
         System.out.println("ERROR: Thumpable door not found on square " + this.square.getX() + ", " + this.square.getY() + ", " + this.square.getZ());
         GameClient.connection.cancelPacket();
      } else {
         var1.putByte(var2);
         var1.putInt(this.getLockedByCode());
         var1.putByte((byte)(this.lockedByPadlock ? 1 : 0));
         var1.putInt(this.getKeyId());
         GameClient.connection.endPacketImmediate();
      }
   }

   public void setLockedByPadlock(boolean var1) {
      if (this.lockedByPadlock != var1 && GameClient.bClient) {
         this.lockedByPadlock = var1;
         this.syncIsoThumpable();
      } else {
         this.lockedByPadlock = var1;
      }

   }

   public boolean canBeLockByPadlock() {
      return this.canBeLockByPadlock;
   }

   public void setCanBeLockByPadlock(boolean var1) {
      this.canBeLockByPadlock = var1;
   }

   public int getLockedByCode() {
      return this.lockedByCode;
   }

   public void setLockedByCode(int var1) {
      if (this.lockedByCode != var1 && GameClient.bClient) {
         this.lockedByCode = var1;
         this.syncIsoThumpable();
      } else {
         this.lockedByCode = var1;
      }

   }

   public boolean isLockedToCharacter(IsoGameCharacter var1) {
      if (GameClient.bClient && var1 instanceof IsoPlayer && !((IsoPlayer)var1).accessLevel.equals("")) {
         return false;
      } else if (this.getLockedByCode() > 0) {
         return true;
      } else {
         return this.isLockedByPadlock() && (var1.getInventory() == null || var1.getInventory().haveThisKeyId(this.getKeyId()) == null);
      }
   }

   public String getThumpSound() {
      return this.thumpSound;
   }

   public void setThumpSound(String var1) {
      this.thumpSound = var1;
   }
}
