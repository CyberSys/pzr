package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import zombie.AmbientStreamManager;
import zombie.SandboxOptions;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.OpenWindowState;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoRoom;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;

public class IsoWindow extends IsoObject implements BarricadeAble, Thumpable {
   public int Health = 75;
   public int MaxHealth = 75;
   public IsoWindow.WindowType type;
   IsoSprite closedSprite;
   IsoSprite smashedSprite;
   public boolean north;
   public boolean Locked;
   public boolean PermaLocked;
   public boolean open;
   IsoSprite openSprite;
   private boolean destroyed;
   private boolean glassRemoved;
   private IsoSprite glassRemovedSprite;
   public int OldNumPlanks;

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

   public IsoGridSquare getIndoorSquare() {
      if (this.square.getRoom() != null) {
         return this.square;
      } else {
         IsoGridSquare var1;
         if (this.north) {
            var1 = IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ());
            if (var1 != null && var1.getRoom() != null) {
               return var1;
            }
         } else {
            var1 = IsoWorld.instance.CurrentCell.getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
            if (var1 != null && var1.getRoom() != null) {
               return var1;
            }
         }

         return null;
      }
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

   public void AttackObject(IsoGameCharacter var1) {
      super.AttackObject(var1);
      IsoObject var2 = this.square.getWall(this.north);
      if (var2 != null) {
         var2.AttackObject(var1);
      }

   }

   public IsoGridSquare getInsideSquare() {
      IsoGridSquare var1 = this.square;
      return this.north ? this.square.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ()) : this.square.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
   }

   public IsoGridSquare getOppositeSquare() {
      return this.getInsideSquare();
   }

   public IsoWindow(IsoCell var1) {
      super(var1);
      this.type = IsoWindow.WindowType.SinglePane;
      this.north = false;
      this.Locked = false;
      this.PermaLocked = false;
      this.open = false;
      this.destroyed = false;
      this.glassRemoved = false;
   }

   public String getObjectName() {
      return "Window";
   }

   public void WeaponHit(IsoGameCharacter var1, HandWeapon var2) {
      if (GameClient.bClient) {
         if (var1 instanceof IsoPlayer) {
            GameClient.instance.sendWeaponHit((IsoPlayer)var1, var2, this);
         }

      } else {
         IsoBarricade var3 = this.getBarricadeForCharacter(var1);
         if (var3 != null) {
            var3.WeaponHit(var1, var2);
         } else if (!this.isDestroyed() && !this.open) {
            if (var2 != ((IsoPlayer)var1).bareHands) {
               if (var2 != null) {
                  this.Damage((float)(var2.getDoorDamage() * 5));
               } else {
                  this.Damage(100.0F);
               }

               this.DirtySlice();
               WorldSoundManager.instance.addSound(var1, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
               if (!this.isDestroyed() && this.Health <= 0) {
                  this.smashWindow();
               }

            }
         } else {
            var3 = this.getBarricadeOppositeCharacter(var1);
            if (var3 != null) {
               var3.WeaponHit(var1, var2);
            }
         }
      }
   }

   public void smashWindow(boolean var1, boolean var2) {
      if (!this.destroyed) {
         if (GameClient.bClient && !var1) {
            GameClient.instance.smashWindow(this, 1);
            this.square.clientModify();
         }

         if (!var1) {
            if (GameServer.bServer) {
               GameServer.PlayWorldSoundServer("SmashWindow", false, this.square, 0.2F, 20.0F, 1.1F, true);
            } else {
               SoundManager.instance.PlayWorldSound("SmashWindow", this.square, 0.2F, 20.0F, 1.0F, true);
            }

            WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
         }

         this.destroyed = true;
         this.sprite = this.smashedSprite;
         if (var2) {
            this.handleAlarm();
         }

         if (GameServer.bServer && !var1) {
            GameServer.smashWindow(this, 1);
         }

         this.square.InvalidateSpecialObjectPaths();
         if (GameServer.bServer && !var1) {
            this.square.revisionUp();
         }

      }
   }

   public void smashWindow(boolean var1) {
      this.smashWindow(var1, true);
   }

   public void smashWindow() {
      this.smashWindow(false, true);
   }

   private void handleAlarm() {
      if (!GameClient.bClient) {
         IsoGridSquare var1 = this.getIndoorSquare();
         if (var1 != null) {
            IsoRoom var2 = var1.getRoom();
            RoomDef var3 = var2.def;
            if (var3.building.bAlarmed && !GameClient.bClient) {
               AmbientStreamManager.instance.doAlarm(var3);
            }

         }
      }
   }

   public IsoWindow(IsoCell var1, IsoGridSquare var2, IsoSprite var3, boolean var4) {
      this.type = IsoWindow.WindowType.SinglePane;
      this.north = false;
      this.Locked = false;
      this.PermaLocked = false;
      this.open = false;
      this.destroyed = false;
      this.glassRemoved = false;
      var3.getProperties().UnSet(IsoFlagType.cutN);
      var3.getProperties().UnSet(IsoFlagType.cutW);
      int var5 = 0;
      if (var3.getProperties().Is("OpenTileOffset")) {
         var5 = Integer.parseInt(var3.getProperties().Val("OpenTileOffset"));
      }

      int var6 = 0;
      this.PermaLocked = var3.getProperties().Is("WindowLocked");
      if (var3.getProperties().Is("SmashedTileOffset")) {
         var6 = Integer.parseInt(var3.getProperties().Val("SmashedTileOffset"));
      }

      this.closedSprite = var3;
      if (var4) {
         this.closedSprite.getProperties().Set(IsoFlagType.cutN);
         this.closedSprite.getProperties().Set(IsoFlagType.windowN);
      } else {
         this.closedSprite.getProperties().Set(IsoFlagType.cutW);
         this.closedSprite.getProperties().Set(IsoFlagType.windowW);
      }

      this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, var3, var5);
      this.smashedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, var3, var6);
      if (this.closedSprite.getProperties().Is("GlassRemovedOffset")) {
         int var7 = Integer.parseInt(this.closedSprite.getProperties().Val("GlassRemovedOffset"));
         this.glassRemovedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, this.closedSprite, var7);
      } else {
         this.glassRemovedSprite = this.smashedSprite;
      }

      if (this.smashedSprite != this.closedSprite && this.smashedSprite != null) {
         this.smashedSprite.AddProperties(this.closedSprite);
         this.smashedSprite.setType(this.closedSprite.getType());
      }

      if (this.openSprite != this.closedSprite && this.openSprite != null) {
         this.openSprite.AddProperties(this.closedSprite);
         this.openSprite.setType(this.closedSprite.getType());
      }

      if (this.glassRemovedSprite != this.closedSprite && this.glassRemovedSprite != null) {
         this.glassRemovedSprite.AddProperties(this.closedSprite);
         this.glassRemovedSprite.setType(this.closedSprite.getType());
      }

      this.sprite = this.closedSprite;
      IsoObject var9 = var2.getWall(var4);
      if (var9 != null) {
         var9.rerouteCollide = this;
         var9.rerouteMask = this;
      }

      this.square = var2;
      this.north = var4;
      switch(this.type) {
      case SinglePane:
         this.MaxHealth = this.Health = 50;
         break;
      case DoublePane:
         this.MaxHealth = this.Health = 150;
      }

      byte var8 = 69;
      if (SandboxOptions.instance.LockedHouses.getValue() == 1) {
         var8 = -1;
      } else if (SandboxOptions.instance.LockedHouses.getValue() == 2) {
         var8 = 5;
      } else if (SandboxOptions.instance.LockedHouses.getValue() == 3) {
         var8 = 10;
      } else if (SandboxOptions.instance.LockedHouses.getValue() == 4) {
         var8 = 50;
      } else if (SandboxOptions.instance.LockedHouses.getValue() == 5) {
         var8 = 60;
      } else if (SandboxOptions.instance.LockedHouses.getValue() == 6) {
         var8 = 70;
      }

      if (var8 > -1) {
         this.Locked = Rand.Next(100) < var8;
      }

   }

   public boolean isDestroyed() {
      return this.destroyed;
   }

   public boolean IsOpen() {
      return this.open;
   }

   public boolean onMouseLeftClick(int var1, int var2) {
      if (super.onMouseLeftClick(var1, var2)) {
         return true;
      } else {
         float var3 = IsoUtils.DistanceTo((float)this.square.getX(), (float)this.square.getY(), IsoPlayer.getInstance().getX(), IsoPlayer.getInstance().getY());
         if (Keyboard.isKeyDown(42)) {
            IsoCurtain var4 = this.HasCurtains();
            if (var4 != null && var3 < 2.0F && this.getZ() == IsoPlayer.getInstance().getZ() && !var4.square.isBlockedTo(IsoPlayer.getInstance().getCurrentSquare())) {
               var4.ToggleDoorSilent();
               return true;
            } else {
               return false;
            }
         } else if (var3 <= 1.0F && (float)this.square.getZ() == IsoPlayer.getInstance().getZ()) {
            if (this.getBarricadeForCharacter(IsoPlayer.getInstance()) != null) {
               return true;
            } else {
               IsoPlayer.instance.StateMachineParams.clear();
               if (IsoPlayer.instance.timePressedContext >= 0.5F) {
                  if (!this.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
                     if ((this.open || this.Health <= 0) && !this.isBarricaded()) {
                        IsoPlayer.instance.StateMachineParams.put(0, this);
                        IsoPlayer.instance.changeState(ClimbThroughWindowState.instance());
                     } else {
                        IsoPlayer.instance.StateMachineParams.put(0, this);
                        IsoPlayer.instance.getStateMachine().changeState(OpenWindowState.instance());
                     }
                  }
               } else if (!this.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
                  if (this.Health > 0) {
                     if (!this.open) {
                        IsoPlayer.instance.StateMachineParams.put(0, this);
                        IsoPlayer.instance.getStateMachine().changeState(OpenWindowState.instance());
                     } else {
                        this.ToggleWindow(IsoPlayer.instance);
                     }
                  } else if (!this.isBarricaded()) {
                     IsoPlayer.instance.StateMachineParams.put(0, this);
                     IsoPlayer.instance.changeState(ClimbThroughWindowState.instance());
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   public boolean TestCollide(IsoMovingObject var1, IsoGridSquare var2, IsoGridSquare var3) {
      if (var2 == this.square) {
         if (this.north && var3.getY() < var2.getY()) {
            if (var1 != null) {
               var1.collideWith(this);
            }

            return true;
         }

         if (!this.north && var3.getX() < var2.getX()) {
            if (var1 != null) {
               var1.collideWith(this);
            }

            return true;
         }
      } else {
         if (this.north && var3.getY() > var2.getY()) {
            if (var1 != null) {
               var1.collideWith(this);
            }

            return true;
         }

         if (!this.north && var3.getX() > var2.getX()) {
            if (var1 != null) {
               var1.collideWith(this);
            }

            return true;
         }
      }

      return false;
   }

   public IsoObject.VisionResult TestVision(IsoGridSquare var1, IsoGridSquare var2) {
      if (var2.getZ() != var1.getZ()) {
         return IsoObject.VisionResult.NoEffect;
      } else {
         if (var1 == this.square) {
            if (this.north && var2.getY() < var1.getY()) {
               return IsoObject.VisionResult.Unblocked;
            }

            if (!this.north && var2.getX() < var1.getX()) {
               return IsoObject.VisionResult.Unblocked;
            }
         } else {
            if (this.north && var2.getY() > var1.getY()) {
               return IsoObject.VisionResult.Unblocked;
            }

            if (!this.north && var2.getX() > var1.getX()) {
               return IsoObject.VisionResult.Unblocked;
            }
         }

         return IsoObject.VisionResult.NoEffect;
      }
   }

   public void Thump(IsoMovingObject var1) {
      if (var1 instanceof IsoZombie) {
         IsoBarricade var2 = this.getBarricadeForCharacter((IsoZombie)var1);
         if (var2 != null) {
            var2.Thump((IsoZombie)var1);
            return;
         }

         if (this.isDestroyed() || this.open) {
            var2 = this.getBarricadeOppositeCharacter((IsoZombie)var1);
            if (var2 != null) {
               var2.Thump((IsoZombie)var1);
               return;
            }

            return;
         }

         if (((IsoZombie)var1).cognition == 1 && !this.canClimbThrough((IsoZombie)var1) && (this.sprite == null || !this.sprite.getProperties().Is(IsoFlagType.makeWindowInvincible)) && (!this.Locked || var1.getCurrentSquare() != null && !var1.getCurrentSquare().Is(IsoFlagType.exterior))) {
            this.ToggleWindow((IsoGameCharacter)var1);
            if (this.canClimbThrough((IsoZombie)var1)) {
               return;
            }
         }

         int var3 = ThumpState.getFastForwardDamageMultiplier();
         this.DirtySlice();
         this.Damage((float)(((IsoZombie)var1).strength * var3), true);
         WorldSoundManager.instance.addSound(var1, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
      }

      if (!this.isDestroyed() && this.Health <= 0) {
         if (GameServer.bServer) {
            GameServer.smashWindow(this, 1);
            GameServer.PlayWorldSoundServer("SmashWindow", false, var1.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
         }

         ((IsoGameCharacter)var1).getEmitter().playSound("SmashWindow", this);
         WorldSoundManager.instance.addSound((IsoObject)null, this.square.getX(), this.square.getY(), this.square.getZ(), 10, 20, true, 4.0F, 15.0F);
         var1.setThumpTarget((Thumpable)null);
         this.destroyed = true;
         this.sprite = this.smashedSprite;
         this.square.InvalidateSpecialObjectPaths();
         if (GameServer.bServer) {
            this.square.revisionUp();
         }
      }

   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      super.load(var1, var2);
      this.open = var1.get() == 1;
      this.north = var1.get() == 1;
      int var3;
      if (var2 >= 87) {
         this.Health = var1.getInt();
      } else {
         var3 = var1.getInt();
         this.Health = var1.getInt();
         int var4 = var1.getInt();
         if (var2 >= 49) {
            short var5 = var1.getShort();
         } else {
            Math.max(var4, var3 * 1000);
         }

         this.OldNumPlanks = var3;
      }

      this.Locked = var1.get() == 1;
      this.PermaLocked = var1.get() == 1;
      this.destroyed = var1.get() == 1;
      if (var2 >= 64) {
         this.glassRemoved = var1.get() == 1;
         if (var1.get() == 1) {
            this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, var1.getInt());
         }

         if (var1.get() == 1) {
            this.closedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, var1.getInt());
         }

         if (var1.get() == 1) {
            this.smashedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, var1.getInt());
         }

         if (var1.get() == 1) {
            this.glassRemovedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, var1.getInt());
         }
      } else {
         if (var1.getInt() == 1) {
            this.openSprite = IsoSprite.getSprite(this.getCell().SpriteManager, var1.getInt());
         }

         if (var1.getInt() == 1) {
            this.closedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, var1.getInt());
         }

         if (var1.getInt() == 1) {
            this.smashedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, var1.getInt());
         }

         if (this.closedSprite != null) {
            if (this.destroyed && this.closedSprite.getProperties().Is("SmashedTileOffset")) {
               var3 = Integer.parseInt(this.closedSprite.getProperties().Val("SmashedTileOffset"));
               this.closedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, this.closedSprite, -var3);
            }

            if (this.closedSprite.getProperties().Is("GlassRemovedOffset")) {
               var3 = Integer.parseInt(this.closedSprite.getProperties().Val("GlassRemovedOffset"));
               this.glassRemovedSprite = IsoSprite.getSprite(this.getCell().SpriteManager, this.closedSprite, var3);
            }
         }

         if (this.glassRemovedSprite == null) {
            this.glassRemovedSprite = this.smashedSprite != null ? this.smashedSprite : this.closedSprite;
         }
      }

      this.MaxHealth = var1.getInt();
      if (this.closedSprite != null) {
         if (this.north) {
            this.closedSprite.getProperties().Set(IsoFlagType.cutN);
            this.closedSprite.getProperties().Set(IsoFlagType.windowN);
         } else {
            this.closedSprite.getProperties().Set(IsoFlagType.cutW);
            this.closedSprite.getProperties().Set(IsoFlagType.windowW);
         }

         if (this.smashedSprite != this.closedSprite && this.smashedSprite != null) {
            this.smashedSprite.AddProperties(this.closedSprite);
            this.smashedSprite.setType(this.closedSprite.getType());
         }

         if (this.openSprite != this.closedSprite && this.openSprite != null) {
            this.openSprite.AddProperties(this.closedSprite);
            this.openSprite.setType(this.closedSprite.getType());
         }

         if (this.glassRemovedSprite != this.closedSprite && this.glassRemovedSprite != null) {
            this.glassRemovedSprite.AddProperties(this.closedSprite);
            this.glassRemovedSprite.setType(this.closedSprite.getType());
         }
      }

      if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
         GameClient.instance.objectSyncReq.putRequestLoad(this.square);
      }

   }

   public void save(ByteBuffer var1) throws IOException {
      super.save(var1);
      var1.put((byte)(this.open ? 1 : 0));
      var1.put((byte)(this.north ? 1 : 0));
      var1.putInt(this.Health);
      var1.put((byte)(this.Locked ? 1 : 0));
      var1.put((byte)(this.PermaLocked ? 1 : 0));
      var1.put((byte)(this.destroyed ? 1 : 0));
      var1.put((byte)(this.glassRemoved ? 1 : 0));
      if (this.openSprite != null) {
         var1.put((byte)1);
         var1.putInt(this.openSprite.ID);
      } else {
         var1.put((byte)0);
      }

      if (this.closedSprite != null) {
         var1.put((byte)1);
         var1.putInt(this.closedSprite.ID);
      } else {
         var1.put((byte)0);
      }

      if (this.smashedSprite != null) {
         var1.put((byte)1);
         var1.putInt(this.smashedSprite.ID);
      } else {
         var1.put((byte)0);
      }

      if (this.glassRemovedSprite != null) {
         var1.put((byte)1);
         var1.putInt(this.glassRemovedSprite.ID);
      } else {
         var1.put((byte)0);
      }

      var1.putInt(this.MaxHealth);
   }

   public void saveState(ByteBuffer var1) {
      var1.put((byte)(this.Locked ? 1 : 0));
   }

   public void loadState(ByteBuffer var1) {
      boolean var2 = var1.get() == 1;
      if (var2 != this.Locked) {
         this.Locked = var2;
      }

   }

   public void openCloseCurtain(IsoGameCharacter var1) {
      if (var1 == IsoPlayer.instance) {
         IsoGridSquare var2 = null;
         Object var3 = null;
         IsoDirections var4 = IsoDirections.N;
         IsoGridSquare var5;
         if (this.north) {
            var5 = this.square;
            var4 = IsoDirections.N;
            if (var5.getRoom() == null) {
               var5 = this.getCell().getGridSquare(var5.getX(), var5.getY() - 1, var5.getZ());
               var4 = IsoDirections.S;
            }

            var3 = var2;
            var2 = var5;
         } else {
            var5 = this.square;
            var4 = IsoDirections.W;
            if (var5.getRoom() == null) {
               var5 = this.getCell().getGridSquare(var5.getX() - 1, var5.getY(), var5.getZ());
               var4 = IsoDirections.E;
            }

            var3 = var2;
            var2 = var5;
         }

         int var6;
         if (var2 != null) {
            for(var6 = 0; var6 < var2.getSpecialObjects().size(); ++var6) {
               if (var2.getSpecialObjects().get(var6) instanceof IsoCurtain) {
                  ((IsoCurtain)var2.getSpecialObjects().get(var6)).ToggleDoorSilent();
                  return;
               }
            }
         }

         if (var3 != null) {
            for(var6 = 0; var6 < ((IsoGridSquare)var3).getSpecialObjects().size(); ++var6) {
               if (((IsoGridSquare)var3).getSpecialObjects().get(var6) instanceof IsoCurtain) {
                  ((IsoCurtain)((IsoGridSquare)var3).getSpecialObjects().get(var6)).ToggleDoorSilent();
                  return;
               }
            }
         }
      }

   }

   public void removeSheet(IsoGameCharacter var1) {
      IsoGridSquare var2 = null;
      IsoDirections var3 = IsoDirections.N;
      IsoGridSquare var4;
      if (this.north) {
         var4 = this.square;
         var3 = IsoDirections.N;
         if (var4.getRoom() == null) {
            var4 = this.getCell().getGridSquare(var4.getX(), var4.getY() - 1, var4.getZ());
            var3 = IsoDirections.S;
         }

         var2 = var4;
      } else {
         var4 = this.square;
         var3 = IsoDirections.W;
         if (var4.getRoom() == null) {
            var4 = this.getCell().getGridSquare(var4.getX() - 1, var4.getY(), var4.getZ());
            var3 = IsoDirections.E;
         }

         var2 = var4;
      }

      for(int var6 = 0; var6 < var2.getSpecialObjects().size(); ++var6) {
         IsoObject var5 = (IsoObject)var2.getSpecialObjects().get(var6);
         if (var5 instanceof IsoCurtain) {
            var2.transmitRemoveItemFromSquare(var5);
            if (var1 != null) {
               if (GameServer.bServer) {
                  var1.sendObjectChange("addItemOfType", new Object[]{"type", var5.getName()});
               } else {
                  var1.getInventory().AddItem(var5.getName());
               }
            }
            break;
         }
      }

   }

   public void addSheet(IsoGameCharacter var1) {
      IsoGridSquare var2 = null;
      IsoDirections var3 = IsoDirections.N;
      IsoGridSquare var4;
      if (this.north) {
         var4 = this.square;
         var3 = IsoDirections.N;
         if (var1 != null) {
            if (var1.getY() < this.getY()) {
               var4 = this.getCell().getGridSquare(var4.getX(), var4.getY() - 1, var4.getZ());
               var3 = IsoDirections.S;
            }
         } else if (var4.getRoom() == null) {
            var4 = this.getCell().getGridSquare(var4.getX(), var4.getY() - 1, var4.getZ());
            var3 = IsoDirections.S;
         }

         var2 = var4;
      } else {
         var4 = this.square;
         var3 = IsoDirections.W;
         if (var1 != null) {
            if (var1.getX() < this.getX()) {
               var4 = this.getCell().getGridSquare(var4.getX() - 1, var4.getY(), var4.getZ());
               var3 = IsoDirections.E;
            }
         } else if (var4.getRoom() == null) {
            var4 = this.getCell().getGridSquare(var4.getX() - 1, var4.getY(), var4.getZ());
            var3 = IsoDirections.E;
         }

         var2 = var4;
      }

      boolean var7 = true;

      int var5;
      for(var5 = 0; var5 < var2.getSpecialObjects().size(); ++var5) {
         if (var2.getSpecialObjects().get(var5) instanceof IsoCurtain) {
            var7 = false;
         }
      }

      if (var2 != null && var7) {
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
         if (!var6.open) {
            var6.ToggleDoorSilent();
         }

         if (GameServer.bServer) {
            var6.transmitCompleteItemToClients();
            if (var1 != null) {
               var1.sendObjectChange("removeOneOf", new Object[]{"type", "Sheet"});
            }
         } else if (var1 != null) {
            var1.getInventory().RemoveOneOf("Sheet");
         }

      }
   }

   public void ToggleWindow(IsoGameCharacter var1) {
      this.DirtySlice();
      IsoGridSquare.setRecalcLightTime(-1);
      if (!this.PermaLocked) {
         if (!this.destroyed) {
            if (var1 == null || this.getBarricadeForCharacter(var1) == null) {
               this.Locked = false;
               this.open = !this.open;
               this.sprite = this.closedSprite;
               this.square.InvalidateSpecialObjectPaths();
               if (this.open) {
                  this.handleAlarm();
                  this.sprite = this.openSprite;
               } else {
                  var1.getEmitter().playSound("CloseWindow");
               }

               this.square.RecalcProperties();
               this.syncIsoObject(false, (byte)(this.open ? 1 : 0), (UdpConnection)null, (ByteBuffer)null);
               LuaEventManager.triggerEvent("OnContainerUpdate");
            }
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
      var1.putByte((byte)(this.open ? 1 : 0));
      var1.putByte((byte)(this.destroyed ? 1 : 0));
      var1.putByte((byte)(this.Locked ? 1 : 0));
      var1.putByte((byte)(this.PermaLocked ? 1 : 0));
   }

   public void syncIsoObject(boolean var1, byte var2, UdpConnection var3, ByteBuffer var4) {
      if (this.square == null) {
         System.out.println("ERROR: " + this.getClass().getSimpleName() + " square is null");
      } else if (this.getObjectIndex() == -1) {
         System.out.println("ERROR: " + this.getClass().getSimpleName() + " not found on square " + this.square.getX() + "," + this.square.getY() + "," + this.square.getZ());
      } else {
         if (GameClient.bClient && !var1) {
            ByteBufferWriter var12 = GameClient.connection.startPacket();
            PacketTypes.doPacket((short)12, var12);
            this.syncIsoObjectSend(var12);
            GameClient.connection.endPacketImmediate();
            this.square.clientModify();
         } else if (GameServer.bServer && !var1) {
            Iterator var11 = GameServer.udpEngine.connections.iterator();

            while(var11.hasNext()) {
               UdpConnection var13 = (UdpConnection)var11.next();
               ByteBufferWriter var14 = var13.startPacket();
               PacketTypes.doPacket((short)12, var14);
               this.syncIsoObjectSend(var14);
               var13.endPacketImmediate();
            }
         } else if (var1) {
            boolean var5 = var4.get() == 1;
            boolean var6 = var4.get() == 1;
            boolean var7 = var4.get() == 1;
            if (var2 == 1) {
               this.open = true;
               this.sprite = this.openSprite;
            } else if (var2 == 0) {
               this.open = false;
               this.sprite = this.closedSprite;
            }

            if (var5) {
               this.destroyed = true;
               this.sprite = this.smashedSprite;
            }

            this.Locked = var6;
            this.PermaLocked = var7;
            if (GameServer.bServer) {
               Iterator var8 = GameServer.udpEngine.connections.iterator();

               while(var8.hasNext()) {
                  UdpConnection var9 = (UdpConnection)var8.next();
                  if (var3 != null && var9.getConnectedGUID() != var3.getConnectedGUID()) {
                     ByteBufferWriter var10 = var9.startPacket();
                     PacketTypes.doPacket((short)12, var10);
                     this.syncIsoObjectSend(var10);
                     var9.endPacketImmediate();
                  }
               }

               this.square.revisionUp();
            }

            this.square.RecalcProperties();
            LuaEventManager.triggerEvent("OnContainerUpdate");
         }

      }
   }

   public static boolean isTopOfSheetRopeHere(IsoGridSquare var0) {
      if (var0 == null) {
         return false;
      } else {
         return var0.Is(IsoFlagType.climbSheetTopN) || var0.Is(IsoFlagType.climbSheetTopS) || var0.Is(IsoFlagType.climbSheetTopW) || var0.Is(IsoFlagType.climbSheetTopE);
      }
   }

   public static boolean isTopOfSheetRopeHere(IsoGridSquare var0, boolean var1) {
      if (var0 == null) {
         return false;
      } else {
         if (var1) {
            if (var0.Is(IsoFlagType.climbSheetTopN)) {
               return true;
            }

            if (var0.nav[IsoDirections.N.index()] != null && var0.nav[IsoDirections.N.index()].Is(IsoFlagType.climbSheetTopS)) {
               return true;
            }
         } else {
            if (var0.Is(IsoFlagType.climbSheetTopW)) {
               return true;
            }

            if (var0.nav[IsoDirections.W.index()] != null && var0.nav[IsoDirections.W.index()].Is(IsoFlagType.climbSheetTopE)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean haveSheetRope() {
      return isTopOfSheetRopeHere(this.square, this.north);
   }

   public static boolean isSheetRopeHere(IsoGridSquare var0) {
      if (var0 == null) {
         return false;
      } else {
         return var0.Is(IsoFlagType.climbSheetTopW) || var0.Is(IsoFlagType.climbSheetTopN) || var0.Is(IsoFlagType.climbSheetTopE) || var0.Is(IsoFlagType.climbSheetTopS) || var0.Is(IsoFlagType.climbSheetW) || var0.Is(IsoFlagType.climbSheetN) || var0.Is(IsoFlagType.climbSheetE) || var0.Is(IsoFlagType.climbSheetS);
      }
   }

   public static boolean canClimbHere(IsoGridSquare var0) {
      if (var0 == null) {
         return false;
      } else if (var0.getProperties().Is(IsoFlagType.solid)) {
         return false;
      } else if (!var0.Has(IsoObjectType.stairsBN) && !var0.Has(IsoObjectType.stairsMN) && !var0.Has(IsoObjectType.stairsTN)) {
         return !var0.Has(IsoObjectType.stairsBW) && !var0.Has(IsoObjectType.stairsMW) && !var0.Has(IsoObjectType.stairsTW);
      } else {
         return false;
      }
   }

   public static int countAddSheetRope(IsoGridSquare var0, boolean var1) {
      if (isTopOfSheetRopeHere(var0, var1)) {
         return 0;
      } else {
         IsoCell var2 = IsoWorld.instance.CurrentCell;
         if (var0.TreatAsSolidFloor()) {
            IsoGridSquare var3;
            if (var1) {
               var3 = var2.getOrCreateGridSquare((double)var0.getX(), (double)(var0.getY() - 1), (double)var0.getZ());
               if (var3 == null || var3.TreatAsSolidFloor() || isSheetRopeHere(var3) || !canClimbHere(var3)) {
                  return 0;
               }

               var0 = var3;
            } else {
               var3 = var2.getOrCreateGridSquare((double)(var0.getX() - 1), (double)var0.getY(), (double)var0.getZ());
               if (var3 == null || var3.TreatAsSolidFloor() || isSheetRopeHere(var3) || !canClimbHere(var3)) {
                  return 0;
               }

               var0 = var3;
            }
         }

         for(int var4 = 1; var0 != null; ++var4) {
            if (!canClimbHere(var0)) {
               return 0;
            }

            if (var0.TreatAsSolidFloor()) {
               return var4;
            }

            if (var0.getZ() == 0) {
               return var4;
            }

            var0 = var2.getOrCreateGridSquare((double)var0.getX(), (double)var0.getY(), (double)(var0.getZ() - 1));
         }

         return 0;
      }
   }

   public int countAddSheetRope() {
      return countAddSheetRope(this.square, this.north);
   }

   public static boolean canAddSheetRope(IsoGridSquare var0, boolean var1) {
      return countAddSheetRope(var0, var1) != 0;
   }

   public boolean canAddSheetRope() {
      return !this.canClimbThrough((IsoGameCharacter)null) ? false : canAddSheetRope(this.square, this.north);
   }

   public boolean addSheetRope(IsoPlayer var1, String var2) {
      return !this.canAddSheetRope() ? false : addSheetRope(var1, this.square, this.north, var2);
   }

   public static boolean addSheetRope(IsoPlayer var0, IsoGridSquare var1, boolean var2, String var3) {
      boolean var4 = false;
      int var5 = 0;
      byte var6 = 0;
      if (var2) {
         var6 = 1;
      }

      boolean var7 = false;
      boolean var8 = false;
      IsoGridSquare var9 = null;
      IsoGridSquare var10 = null;
      IsoCell var11 = IsoWorld.instance.CurrentCell;
      if (var1.TreatAsSolidFloor()) {
         if (!var2) {
            var9 = var11.getGridSquare(var1.getX() - 1, var1.getY(), var1.getZ());
            if (var9 != null) {
               var8 = true;
               var6 = 3;
            }
         } else {
            var10 = var11.getGridSquare(var1.getX(), var1.getY() - 1, var1.getZ());
            if (var10 != null) {
               var7 = true;
               var6 = 4;
            }
         }
      }

      if (var1.getProperties().Is(IsoFlagType.solidfloor)) {
      }

      while(var1 != null && (GameServer.bServer || var0.getInventory().contains(var3))) {
         String var12 = "crafted_01_" + var6;
         if (var5 > 0) {
            if (var8) {
               var12 = "crafted_01_10";
            } else if (var7) {
               var12 = "crafted_01_13";
            } else {
               var12 = "crafted_01_" + (var6 + 8);
            }
         }

         IsoObject var13 = new IsoObject(var11, var1, var12);
         var13.setName(var3);
         var13.sheetRope = true;
         var1.getObjects().add(var13);
         var13.transmitCompleteItemToClients();
         var1.haveSheetRope = true;
         if (var7 && var5 == 0) {
            var1 = var10;
            var13 = new IsoObject(var11, var10, "crafted_01_5");
            var13.setName(var3);
            var13.sheetRope = true;
            var10.getObjects().add(var13);
            var13.transmitCompleteItemToClients();
         }

         if (var8 && var5 == 0) {
            var1 = var9;
            var13 = new IsoObject(var11, var9, "crafted_01_2");
            var13.setName(var3);
            var13.sheetRope = true;
            var9.getObjects().add(var13);
            var13.transmitCompleteItemToClients();
         }

         var1.RecalcProperties();
         var1.getProperties().UnSet(IsoFlagType.solidtrans);
         if (GameServer.bServer) {
            if (var5 == 0) {
               var0.sendObjectChange("removeOneOf", new Object[]{"type", "Nails"});
            }

            var0.sendObjectChange("removeOneOf", new Object[]{"type", var3});
         } else {
            if (var5 == 0) {
               var0.getInventory().RemoveOneOf("Nails");
            }

            var0.getInventory().RemoveOneOf(var3);
         }

         ++var5;
         if (var4) {
            break;
         }

         var1 = var11.getOrCreateGridSquare((double)var1.getX(), (double)var1.getY(), (double)(var1.getZ() - 1));
         if (var1 != null && var1.TreatAsSolidFloor()) {
            var4 = true;
         }
      }

      return true;
   }

   public boolean removeSheetRope(IsoPlayer var1) {
      return !this.haveSheetRope() ? false : removeSheetRope(var1, this.square, this.north);
   }

   public static boolean removeSheetRope(IsoPlayer var0, IsoGridSquare var1, boolean var2) {
      if (var1 == null) {
         return false;
      } else {
         IsoGridSquare var6 = var1;
         var1.haveSheetRope = false;
         IsoFlagType var3;
         IsoFlagType var4;
         String var5;
         int var7;
         IsoObject var8;
         if (var2) {
            if (var1.Is(IsoFlagType.climbSheetTopN)) {
               var3 = IsoFlagType.climbSheetTopN;
               var4 = IsoFlagType.climbSheetN;
            } else {
               if (var1.nav[IsoDirections.N.index()] == null || !var1.nav[IsoDirections.N.index()].Is(IsoFlagType.climbSheetTopS)) {
                  return false;
               }

               var3 = IsoFlagType.climbSheetTopS;
               var4 = IsoFlagType.climbSheetS;
               var5 = "crafted_01_4";

               for(var7 = 0; var7 < var6.getObjects().size(); ++var7) {
                  var8 = (IsoObject)var6.getObjects().get(var7);
                  if (var8.sprite != null && var8.sprite.getName() != null && var8.sprite.getName().equals(var5)) {
                     var6.transmitRemoveItemFromSquare(var8);
                     break;
                  }
               }

               var6 = var1.nav[IsoDirections.N.index()];
            }
         } else if (var1.Is(IsoFlagType.climbSheetTopW)) {
            var3 = IsoFlagType.climbSheetTopW;
            var4 = IsoFlagType.climbSheetW;
         } else {
            if (var1.nav[IsoDirections.W.index()] == null || !var1.nav[IsoDirections.W.index()].Is(IsoFlagType.climbSheetTopE)) {
               return false;
            }

            var3 = IsoFlagType.climbSheetTopE;
            var4 = IsoFlagType.climbSheetE;
            var5 = "crafted_01_3";

            for(var7 = 0; var7 < var6.getObjects().size(); ++var7) {
               var8 = (IsoObject)var6.getObjects().get(var7);
               if (var8.sprite != null && var8.sprite.getName() != null && var8.sprite.getName().equals(var5)) {
                  var6.transmitRemoveItemFromSquare(var8);
                  break;
               }
            }

            var6 = var1.nav[IsoDirections.W.index()];
         }

         while(var6 != null) {
            boolean var10 = false;

            for(int var11 = 0; var11 < var6.getObjects().size(); ++var11) {
               IsoObject var9 = (IsoObject)var6.getObjects().get(var11);
               if (var9.getProperties() != null && (var9.getProperties().Is(var3) || var9.getProperties().Is(var4))) {
                  var6.transmitRemoveItemFromSquare(var9);
                  if (GameServer.bServer) {
                     if (var0 != null) {
                        var0.sendObjectChange("addItemOfType", new Object[]{"type", var9.getName()});
                     }
                  } else if (var0 != null) {
                     var0.getInventory().AddItem(var9.getName());
                  }

                  var10 = true;
                  break;
               }
            }

            if (!var10 || var6.getZ() == 0) {
               break;
            }

            var6 = var6.getCell().getGridSquare(var6.getX(), var6.getY(), var6.getZ() - 1);
         }

         return true;
      }
   }

   public void Damage(float var1) {
      this.Damage(var1, false);
   }

   public void Damage(float var1, boolean var2) {
      if (!this.getSquare().getProperties().Is(IsoFlagType.makeWindowInvincible)) {
         this.DirtySlice();
         this.Health = (int)((float)this.Health - var1);
         if (this.Health < 0) {
            this.Health = 0;
         }

         if (!this.isDestroyed() && this.Health == 0) {
            this.smashWindow(false, !var2 || SandboxOptions.getInstance().Lore.TriggerHouseAlarm.getValue());
         }

      }
   }

   public boolean isLocked() {
      return this.Locked;
   }

   public boolean isSmashed() {
      return this.destroyed;
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

   public void setIsLocked(boolean var1) {
      this.Locked = var1;
   }

   public IsoSprite getOpenSprite() {
      return this.openSprite;
   }

   public void setOpenSprite(IsoSprite var1) {
      this.openSprite = var1;
   }

   public void setSmashed(boolean var1) {
      if (var1) {
         this.destroyed = true;
         this.sprite = this.smashedSprite;
      } else {
         this.destroyed = false;
         this.sprite = this.open ? this.openSprite : this.closedSprite;
         this.Health = this.MaxHealth;
      }

      this.glassRemoved = false;
   }

   public IsoSprite getSmashedSprite() {
      return this.smashedSprite;
   }

   public void setSmashedSprite(IsoSprite var1) {
      this.smashedSprite = var1;
   }

   public void setPermaLocked(Boolean var1) {
      this.PermaLocked = var1;
   }

   public boolean isPermaLocked() {
      return this.PermaLocked;
   }

   public boolean canClimbThrough(IsoGameCharacter var1) {
      if (this.square != null && !this.square.Is(IsoFlagType.makeWindowInvincible)) {
         if (this.isBarricaded()) {
            return false;
         } else {
            return this.Health > 0 && !this.destroyed ? this.open : true;
         }
      } else {
         return false;
      }
   }

   public boolean isGlassRemoved() {
      return this.glassRemoved;
   }

   public void setGlassRemoved(boolean var1) {
      if (this.destroyed) {
         if (var1) {
            this.sprite = this.glassRemovedSprite;
            this.glassRemoved = true;
         } else {
            this.sprite = this.smashedSprite;
            this.glassRemoved = false;
         }

      }
   }

   public void removeBrokenGlass() {
      if (GameClient.bClient) {
         GameClient.instance.smashWindow(this, 2);
      } else {
         this.setGlassRemoved(true);
      }

   }

   public void addRandomBarricades() {
      IsoGridSquare var1 = this.square.getRoom() == null ? this.square : this.getOppositeSquare();
      if (this.getZ() == 0.0F && var1 != null && var1.getRoom() == null) {
         boolean var2 = var1 != this.square;
         IsoBarricade var3 = IsoBarricade.AddBarricadeToObject(this, var2);
         if (var3 != null) {
            int var4 = Rand.Next(1, 4);

            for(int var5 = 0; var5 < var4; ++var5) {
               var3.addPlank((IsoGameCharacter)null, (InventoryItem)null);
            }

            if (GameServer.bServer) {
               var3.transmitCompleteItemToClients();
            }
         }
      } else {
         this.addSheet((IsoGameCharacter)null);
         this.HasCurtains().ToggleDoor((IsoGameCharacter)null);
      }

   }

   public static enum WindowType {
      SinglePane,
      DoublePane;
   }
}
