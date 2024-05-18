package zombie.iso;

import fmod.fmod.Audio;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;
import zombie.CollisionManager;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.State;
import zombie.ai.astar.Mover;
import zombie.ai.states.AttackState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverFenceState2;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.ClimbThroughWindowState2;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.StaggerBackDieState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ZombieStandState;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.core.bucket.BucketManager;
import zombie.inventory.types.HandWeapon;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.isoregion.MasterRegion;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.RenderEffectType;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.popman.ZombiePopulationManager;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.ScriptCharacter;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;

public class IsoMovingObject extends IsoObject implements Mover {
   public static final long ExpectedChecksum = 270439210007L;
   private static int IDCount = 0;
   private boolean closeKilled = false;
   private float bspeed = 0.0F;
   public float bx;
   public float by;
   float bz;
   private int TimeSinceZombieAttack = 1000000;
   public boolean noDamage = false;
   private boolean collidedE = false;
   private boolean collidedN = false;
   private IsoObject CollidedObject = null;
   private boolean collidedS = false;
   private boolean collidedThisFrame = false;
   private boolean collidedW = false;
   private boolean CollidedWithDoor = false;
   private boolean collidedWithVehicle = false;
   protected IsoGridSquare current = null;
   private boolean destroyed = false;
   private boolean firstUpdate = true;
   protected Vector2 hitDir = new Vector2();
   private boolean AllowBehaviours = true;
   private float impulsex = 0.0F;
   private float impulsey = 0.0F;
   private float limpulsex = 0.0F;
   private float limpulsey = 0.0F;
   private float hitForce = 0.0F;
   private float hitFromAngle;
   protected int ID = 0;
   public IsoGridSquare last = null;
   protected IsoGridSquare movingSq = null;
   public float lx;
   public float ly;
   public float lz;
   public float nx;
   public float ny;
   private int PathFindIndex = -1;
   protected boolean solid = true;
   private float StateEventDelayTimer = 0.0F;
   private Thumpable thumpTarget = null;
   protected float width = 0.24F;
   public float x;
   public float y;
   public float z;
   private boolean bAltCollide = false;
   protected boolean shootable = true;
   private IsoZombie lastTargettedBy = null;
   protected boolean Collidable = true;
   protected float scriptnx = 0.0F;
   protected float scriptny = 0.0F;
   public Vector2 reqMovement = new Vector2();
   protected String ScriptModule = "none";
   protected String ScriptName = "none";
   private Stack ActiveInInstances = new Stack();
   public static IsoMovingObject.TreeSoundManager treeSoundMgr = new IsoMovingObject.TreeSoundManager();
   public IsoSpriteInstance def = null;
   protected Vector2 movementLastFrame = new Vector2();
   protected float weight = 1.0F;
   private static Vector2 tempo = new Vector2();
   private float feelersize = 0.5F;
   boolean bOnFloor = false;

   public static int getIDCount() {
      return IDCount;
   }

   public IsoBuilding getBuilding() {
      if (this.current == null) {
         return null;
      } else {
         IsoRoom var1 = this.current.getRoom();
         return var1 == null ? null : var1.building;
      }
   }

   public MasterRegion getMasterRegion() {
      return this.current != null ? this.current.getMasterRegion() : null;
   }

   public static void setIDCount(int var0) {
      IDCount = var0;
   }

   public static Vector2 getTempo() {
      return tempo;
   }

   public static void setTempo(Vector2 var0) {
      tempo = var0;
   }

   public float getWeight() {
      return this.weight;
   }

   public float getWeight(float var1, float var2) {
      return this.weight;
   }

   public void onMouseRightClick(int var1, int var2) {
      if (this.square.getZ() == (int)IsoPlayer.getInstance().getZ() && this.DistToProper(IsoPlayer.getInstance()) <= 2.0F) {
         IsoPlayer.instance.setDragObject(this);
      }

   }

   public String getObjectName() {
      return "IsoMovingObject";
   }

   public void onMouseRightReleased() {
   }

   public IsoMovingObject(IsoCell var1) {
      this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
      if (var1 != null) {
         this.ID = IDCount++;
         if (this.getCell().isSafeToAdd()) {
            this.getCell().getObjectList().add(this);
         } else {
            this.getCell().getAddList().add(this);
         }

      }
   }

   public IsoMovingObject(IsoCell var1, boolean var2) {
      this.ID = IDCount++;
      this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
      if (var2) {
         if (this.getCell().isSafeToAdd()) {
            this.getCell().getObjectList().add(this);
         } else {
            this.getCell().getAddList().add(this);
         }
      }

   }

   public IsoMovingObject(IsoCell var1, IsoGridSquare var2, IsoSprite var3, boolean var4) {
      this.ID = IDCount++;
      this.sprite = var3;
      if (var4) {
         if (this.getCell().isSafeToAdd()) {
            this.getCell().getObjectList().add(this);
         } else {
            this.getCell().getAddList().add(this);
         }
      }

   }

   public IsoMovingObject() {
      this.ID = IDCount++;
      this.getCell().getAddList().add(this);
   }

   public void collideCharacter() {
   }

   public void collideWith(IsoObject var1) {
      if (this instanceof IsoGameCharacter && var1 instanceof IsoGameCharacter) {
         LuaEventManager.triggerEvent("OnCharacterCollide", (IsoGameCharacter)this, (IsoGameCharacter)var1);
      } else {
         LuaEventManager.triggerEvent("OnObjectCollide", this, var1);
      }

   }

   public void doStairs() {
      if (this.current != null) {
         if (this.last != null) {
            if (!(this instanceof IsoPhysicsObject)) {
               IsoGridSquare var1 = this.current;
               if (var1.z > 0 && (var1.Has(IsoObjectType.stairsTN) || var1.Has(IsoObjectType.stairsTW)) && this.z - (float)((int)this.z) < 0.1F) {
                  IsoGridSquare var2 = IsoWorld.instance.CurrentCell.getGridSquare(var1.x, var1.y, var1.z - 1);
                  if (var2 != null && (var2.Has(IsoObjectType.stairsTN) || var2.Has(IsoObjectType.stairsTW))) {
                     var1 = var2;
                  }
               }

               if (this instanceof IsoGameCharacter && (this.last.Has(IsoObjectType.stairsTN) || this.last.Has(IsoObjectType.stairsTW))) {
                  this.z = (float)Math.round(this.z);
               }

               float var4 = this.z;
               if (var1.Has(IsoObjectType.stairsTN)) {
                  var4 = (float)var1.getZ() + GameTime.getInstance().Lerp(0.6666F, 1.0F, 1.0F - (this.y - (float)var1.getY()));
               } else if (var1.Has(IsoObjectType.stairsTW)) {
                  var4 = (float)var1.getZ() + GameTime.getInstance().Lerp(0.6666F, 1.0F, 1.0F - (this.x - (float)var1.getX()));
               } else if (var1.Has(IsoObjectType.stairsMN)) {
                  var4 = (float)var1.getZ() + GameTime.getInstance().Lerp(0.3333F, 0.6666F, 1.0F - (this.y - (float)var1.getY()));
               } else if (var1.Has(IsoObjectType.stairsMW)) {
                  var4 = (float)var1.getZ() + GameTime.getInstance().Lerp(0.3333F, 0.6666F, 1.0F - (this.x - (float)var1.getX()));
               } else if (var1.Has(IsoObjectType.stairsBN)) {
                  var4 = (float)var1.getZ() + GameTime.getInstance().Lerp(0.01F, 0.3333F, 1.0F - (this.y - (float)var1.getY()));
               } else if (var1.Has(IsoObjectType.stairsBW)) {
                  var4 = (float)var1.getZ() + GameTime.getInstance().Lerp(0.01F, 0.3333F, 1.0F - (this.x - (float)var1.getX()));
               }

               if (this instanceof IsoGameCharacter) {
                  State var3 = ((IsoGameCharacter)this).getCurrentState();
                  if (var3 == ClimbOverFenceState.instance() || var3 == ClimbThroughWindowState.instance()) {
                     return;
                  }

                  if (var3 == ClimbOverFenceState2.instance() || var3 == ClimbThroughWindowState2.instance()) {
                     if (var1.HasStairs() && this.z > var4) {
                        this.z = Math.max(var4, this.z - 0.075F * GameTime.getInstance().getMultiplier());
                     }

                     return;
                  }
               }

               if (Math.abs(var4 - this.z) < 0.95F) {
                  this.z = var4;
               }

            }
         }
      }
   }

   public int getID() {
      return this.ID;
   }

   public int getPathFindIndex() {
      return this.PathFindIndex;
   }

   public float getScreenX() {
      return IsoUtils.XToScreen(this.x, this.y, this.z, 0);
   }

   public float getScreenY() {
      return IsoUtils.YToScreen(this.x, this.y, this.z, 0);
   }

   public Thumpable getThumpTarget() {
      return this.thumpTarget;
   }

   public Vector2 getVectorFromDirection(Vector2 var1) {
      var1.x = 0.0F;
      var1.y = 0.0F;
      switch(this.dir) {
      case S:
         var1.x = 0.0F;
         var1.y = 1.0F;
         break;
      case N:
         var1.x = 0.0F;
         var1.y = -1.0F;
         break;
      case E:
         var1.x = 1.0F;
         var1.y = 0.0F;
         break;
      case W:
         var1.x = -1.0F;
         var1.y = 0.0F;
         break;
      case NW:
         var1.x = -1.0F;
         var1.y = -1.0F;
         break;
      case NE:
         var1.x = 1.0F;
         var1.y = -1.0F;
         break;
      case SW:
         var1.x = -1.0F;
         var1.y = 1.0F;
         break;
      case SE:
         var1.x = 1.0F;
         var1.y = 1.0F;
      }

      var1.normalize();
      return var1;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }

   public IsoGridSquare getSquare() {
      return this.current != null ? this.current : this.square;
   }

   public IsoBuilding getCurrentBuilding() {
      if (this.current == null) {
         return null;
      } else {
         return this.current.getRoom() == null ? null : this.current.getRoom().building;
      }
   }

   public void Hit(HandWeapon var1, IsoGameCharacter var2, float var3, boolean var4, float var5) {
   }

   public void Move(Vector2 var1) {
      this.nx += var1.x * GameTime.instance.getMultiplier();
      this.ny += var1.y * GameTime.instance.getMultiplier();
      this.reqMovement.x = var1.x;
      this.reqMovement.y = var1.y;
      if (this instanceof IsoPlayer) {
         if (var1.x != 0.0F || var1.y != 0.0F) {
            boolean var2 = false;
         }

         IsoGridSquare var3 = this.current;
         this.current = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)this.y, (double)((int)this.z));
      }

   }

   public boolean isCharacter() {
      return this instanceof IsoGameCharacter;
   }

   public float DistTo(int var1, int var2) {
      return IsoUtils.DistanceManhatten((float)var1, (float)var2, this.x, this.y);
   }

   public float DistTo(IsoMovingObject var1) {
      return IsoUtils.DistanceManhatten(this.x, this.y, var1.x, var1.y);
   }

   public float DistToProper(IsoObject var1) {
      return IsoUtils.DistanceTo(this.x, this.y, var1.getX(), var1.getY());
   }

   public float DistToSquared(IsoMovingObject var1) {
      return IsoUtils.DistanceToSquared(this.x, this.y, var1.x, var1.y);
   }

   public float DistToSquared(float var1, float var2) {
      return IsoUtils.DistanceToSquared(var1, var2, this.x, this.y);
   }

   public boolean getAllowBehaviours() {
      if (this instanceof IsoZombie) {
         return false;
      } else if (this.ScriptName.equals("none")) {
         return this.AllowBehaviours;
      } else {
         ScriptCharacter var1 = ScriptManager.instance.getCharacter(this.ScriptModule + "." + this.ScriptName);
         if (var1 == null) {
            return this.AllowBehaviours;
         } else {
            return this.AllowBehaviours && var1.AllowBehaviours();
         }
      }
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      float var3 = var1.getFloat();
      float var4 = var1.getFloat();
      this.x = this.lx = this.nx = this.scriptnx = var1.getFloat() + (float)(IsoWorld.saveoffsetx * 300);
      this.y = this.ly = this.ny = this.scriptny = var1.getFloat() + (float)(IsoWorld.saveoffsety * 300);
      this.z = this.lz = var1.getFloat();
      this.dir = IsoDirections.fromIndex(var1.getInt());
      if (var1.get() != 0) {
         if (this.table == null) {
            this.table = LuaManager.platform.newTable();
         }

         this.table.load(var1, var2);
      }

   }

   public void save(ByteBuffer var1) throws IOException {
      var1.put((byte)(this.Serialize() ? 1 : 0));
      var1.putInt(this.getObjectName().hashCode());
      var1.putFloat(this.offsetX);
      var1.putFloat(this.offsetY);
      var1.putFloat(this.x);
      var1.putFloat(this.y);
      var1.putFloat(this.z);
      var1.putInt(this.dir.index());
      if (this.table != null && !this.table.isEmpty()) {
         var1.put((byte)1);
         this.table.save(var1);
      } else {
         var1.put((byte)0);
      }

   }

   public void removeFromWorld() {
      IsoCell var1 = this.getCell();
      if (var1.isSafeToAdd()) {
         var1.getObjectList().remove(this);
         var1.getRemoveList().remove(this);
      } else {
         var1.getRemoveList().add(this);
      }

      var1.getAddList().remove(this);
      super.removeFromWorld();
   }

   public void removeFromSquare() {
      if (this.current != null) {
         this.current.getMovingObjects().remove(this);
      }

      if (this.last != null) {
         this.last.getMovingObjects().remove(this);
      }

      if (this.movingSq != null) {
         this.movingSq.getMovingObjects().remove(this);
      }

      this.current = this.last = this.movingSq = null;
      if (this.square != null) {
         this.square.getStaticMovingObjects().remove(this);
      }

      super.removeFromSquare();
   }

   public IsoGridSquare getFuturWalkedSquare() {
      if (this.current != null) {
         IsoGridSquare var1 = this.getFeelerTile(this.feelersize);
         if (var1 != null && var1 != this.current) {
            return var1;
         }
      }

      return null;
   }

   public float getGlobalMovementMod() {
      return this.getGlobalMovementMod(true);
   }

   public float getGlobalMovementMod(boolean var1) {
      if (this instanceof IsoPlayer && (((IsoPlayer)this).GhostMode || !((IsoPlayer)this).getAccessLevel().equals("None") || ((IsoPlayer)this).isNoClip())) {
         return 1.0F;
      } else {
         if (this.current != null && this.z - (float)((int)this.z) < 0.5F) {
            if (this.current.getProperties() != null && !this.current.getProperties().isBush && this.current.getProperties().Is("SpeedFactor")) {
               return Float.parseFloat(this.current.getProperties().Val("SpeedFactor")) / 100.0F;
            }

            if (this.current.Has(IsoObjectType.tree) || this.current.getProperties() != null && this.current.getProperties().isBush) {
               if (var1) {
                  this.doTreeNoises();
               }

               for(int var5 = 1; var5 < this.current.getObjects().size(); ++var5) {
                  IsoObject var6 = (IsoObject)this.current.getObjects().get(var5);
                  if (var6 instanceof IsoTree) {
                     var6.setRenderEffect(RenderEffectType.Vegetation_Rustle);
                     return ((IsoTree)var6).getSlowFactor(this);
                  }

                  if (var6.getSprite() != null && var6.getSprite().isBush) {
                     var6.setRenderEffect(RenderEffectType.Vegetation_Rustle);
                     return 0.6F;
                  }
               }

               return 0.3F;
            }

            IsoGridSquare var2 = this.getFeelerTile(this.feelersize);
            if (var2 != null && var2 != this.current && (var2.Has(IsoObjectType.tree) || var2.getProperties() != null && var2.getProperties().isBush)) {
               if (var1) {
                  this.doTreeNoises();
               }

               for(int var3 = 1; var3 < var2.getObjects().size(); ++var3) {
                  IsoObject var4 = (IsoObject)var2.getObjects().get(var3);
                  if (var4 instanceof IsoTree) {
                     var4.setRenderEffect(RenderEffectType.Vegetation_Rustle);
                     return ((IsoTree)var4).getSlowFactor(this);
                  }

                  if (var4.getSprite() != null && var4.getSprite().isBush) {
                     var4.setRenderEffect(RenderEffectType.Vegetation_Rustle);
                     return 0.6F;
                  }
               }

               return 0.3F;
            }
         }

         if (this.current != null && this.current.HasStairs()) {
            return 0.75F;
         } else {
            return 1.0F;
         }
      }
   }

   private void doTreeNoises() {
      if (!GameServer.bServer) {
         if (!(this instanceof IsoPhysicsObject)) {
            if (this.current != null) {
               if (Rand.Next(Rand.AdjustForFramerate(50)) == 0) {
                  treeSoundMgr.addSquare(this.current);
               }
            }
         }
      }
   }

   public void postupdate() {
      boolean var1;
      if (this instanceof IsoZombie && GameServer.bServer && ((IsoZombie)this).getStateMachine().getCurrent() != ZombieStandState.instance()) {
         var1 = false;
      }

      if (this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
         IsoPlayer.instance = (IsoPlayer)this;
         IsoCamera.CamCharacter = (IsoPlayer)this;
      }

      float var8;
      if (GameClient.bClient && this instanceof IsoZombie) {
         if (this.bx == 0.0F) {
            this.bx = this.x;
            this.by = this.y;
         }

         tempo.x = this.x - this.bx;
         tempo.y = this.y - this.by;
         var8 = this.bspeed;
         if (this instanceof IsoZombie) {
            var8 *= GameTime.getInstance().getServerMultiplier();
         }

         if (tempo.getLength() > 2.0F) {
            var8 = tempo.getLength();
         }

         if (var8 > 0.001F && (tempo.x != 0.0F || tempo.y != 0.0F)) {
            if (var8 < tempo.getLength()) {
               tempo.setLength(var8);
            }

            this.bx += tempo.x;
            this.by += tempo.y;
         } else {
            this.bx = this.x;
            this.by = this.y;
         }

         if (this.movingSq != null) {
            this.movingSq.getMovingObjects().remove(this);
            this.movingSq = null;
         }

         this.last = this.current = this.square = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)this.y, (double)this.z);
         if (this.current != null && !this.current.getMovingObjects().contains(this)) {
            this.current.getMovingObjects().add(this);
            this.movingSq = this.current;
         }

         this.getGlobalMovementMod();
      } else {
         this.ensureOnTile();
         if (this.lastTargettedBy != null && (this.lastTargettedBy.getHealth() <= 0.0F || this.lastTargettedBy.getBodyDamage().getHealth() <= 0.0F)) {
            this.lastTargettedBy = null;
         }

         if (this.lastTargettedBy != null && this.TimeSinceZombieAttack > 120) {
            this.lastTargettedBy = null;
         }

         ++this.TimeSinceZombieAttack;
         if (this instanceof IsoPlayer) {
            var1 = false;
            ((IsoPlayer)this).setLastCollidedW(this.collidedW);
            ((IsoPlayer)this).setLastCollidedN(this.collidedN);
            IsoPlayer var2 = (IsoPlayer)this;
         }

         if (!this.destroyed) {
            if (!this.getAllowBehaviours() && this instanceof IsoSurvivor) {
               this.nx = this.scriptnx;
               this.ny = this.scriptny;
            }

            this.collidedThisFrame = false;
            this.collidedN = false;
            this.collidedS = false;
            this.collidedW = false;
            this.collidedE = false;
            this.CollidedWithDoor = false;
            this.last = this.current;
            this.CollidedObject = null;
            this.nx += this.impulsex;
            this.ny += this.impulsey;
            if (this.nx < 0.0F) {
               this.nx = 0.0F;
            }

            if (this.ny < 0.0F) {
               this.ny = 0.0F;
            }

            tempo.set(this.nx - this.x, this.ny - this.y);
            if (tempo.getLength() > 1.0F) {
               tempo.normalize();
               this.nx = this.x + tempo.getX();
               this.ny = this.y + tempo.getY();
            }

            this.impulsex = 0.0F;
            this.impulsey = 0.0F;
            if (!GameClient.bClient && this instanceof IsoZombie && (int)this.z == 0 && ((IsoZombie)this).getCurrentBuilding() == null && !this.isInLoadedArea((int)this.nx, (int)this.ny) && (((IsoZombie)this).getCurrentState() == PathFindState.instance() || ((IsoZombie)this).getCurrentState() == WalkTowardState.instance())) {
               ZombiePopulationManager.instance.virtualizeZombie((IsoZombie)this);
            } else {
               this.collidedWithVehicle = false;
               float var9;
               if (this instanceof IsoGameCharacter && !this.isOnFloor() && ((IsoGameCharacter)this).getVehicle() == null && (!(this instanceof IsoPlayer) || !((IsoPlayer)this).isNoClip())) {
                  var8 = this.nx;
                  var9 = this.ny;
                  PolygonalMap2.instance.resolveCollision((IsoGameCharacter)this, this.nx, this.ny);
                  if (var8 != this.nx || var9 != this.ny) {
                     this.collidedWithVehicle = true;
                  }
               }

               var8 = this.nx;
               var9 = this.ny;
               float var3 = 0.0F;
               boolean var4 = false;
               float var5;
               float var6;
               if (this.Collidable) {
                  if (this.bAltCollide) {
                     this.DoCollide(2);
                  } else {
                     this.DoCollide(1);
                  }

                  if (this.collidedN || this.collidedS) {
                     this.ny = this.ly;
                     this.DoCollideNorS();
                  }

                  if (this.collidedW || this.collidedE) {
                     this.nx = this.lx;
                     this.DoCollideWorE();
                  }

                  if (this.bAltCollide) {
                     this.DoCollide(1);
                  } else {
                     this.DoCollide(2);
                  }

                  this.bAltCollide = !this.bAltCollide;
                  if (this.collidedN || this.collidedS) {
                     this.ny = this.ly;
                     this.DoCollideNorS();
                     var4 = true;
                  }

                  if (this.collidedW || this.collidedE) {
                     this.nx = this.lx;
                     this.DoCollideWorE();
                     var4 = true;
                  }

                  var3 = Math.abs(this.nx - this.lx) + Math.abs(this.ny - this.ly);
                  var5 = this.nx;
                  var6 = this.ny;
                  this.nx = var8;
                  this.ny = var9;
                  if (this.Collidable && var4) {
                     if (this.bAltCollide) {
                        this.DoCollide(2);
                     } else {
                        this.DoCollide(1);
                     }

                     if (this.collidedN || this.collidedS) {
                        this.ny = this.ly;
                        this.DoCollideNorS();
                     }

                     if (this.collidedW || this.collidedE) {
                        this.nx = this.lx;
                        this.DoCollideWorE();
                     }

                     if (this.bAltCollide) {
                        this.DoCollide(1);
                     } else {
                        this.DoCollide(2);
                     }

                     if (this.collidedN || this.collidedS) {
                        this.ny = this.ly;
                        this.DoCollideNorS();
                        var4 = true;
                     }

                     if (this.collidedW || this.collidedE) {
                        this.nx = this.lx;
                        this.DoCollideWorE();
                        var4 = true;
                     }

                     if (Math.abs(this.nx - this.lx) + Math.abs(this.ny - this.ly) < var3) {
                        this.nx = var5;
                        this.ny = var6;
                     }
                  }
               }

               if (this.collidedThisFrame) {
                  this.current = this.last;
               }

               var5 = this.nx - this.x;
               var6 = this.ny - this.y;
               if (Math.abs(var5) > 0.01F || Math.abs(var6) > 0.01F) {
                  var5 *= this.getGlobalMovementMod();
                  var6 *= this.getGlobalMovementMod();
               }

               this.x += var5;
               this.y += var6;
               if (GameClient.bClient) {
                  if (this.bx == 0.0F) {
                     this.bx = this.x;
                     this.by = this.y;
                  }

                  tempo.x = this.x - this.bx;
                  tempo.y = this.y - this.by;
                  float var7 = this.bspeed;
                  if (this instanceof IsoZombie) {
                     var7 *= GameTime.getInstance().getServerMultiplier();
                  }

                  if (tempo.getLength() > 2.0F) {
                     var7 = tempo.getLength();
                  }

                  if (!(var7 > 0.001F) || tempo.x == 0.0F && tempo.y == 0.0F) {
                     this.bx = this.x;
                     this.by = this.y;
                  } else {
                     if (var7 < tempo.getLength()) {
                        tempo.setLength(var7);
                     }

                     this.bx += tempo.x;
                     this.by += tempo.y;
                  }
               }

               if (this instanceof IsoPlayer) {
                  this.bx = this.x;
                  this.by = this.y;
               }

               this.doStairs();
               this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z);
               if (this.current == null) {
                  for(int var10 = (int)this.z; var10 >= 0; --var10) {
                     this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, var10);
                     if (this.current != null) {
                        break;
                     }
                  }

                  if (this.current == null && this.last != null) {
                     this.current = this.last;
                     this.x = this.nx = this.scriptnx = (float)this.current.getX() + 0.5F;
                     this.y = this.ny = this.scriptny = (float)this.current.getY() + 0.5F;
                  }
               }

               if (this.movingSq != null) {
                  this.movingSq.getMovingObjects().remove(this);
                  this.movingSq = null;
               }

               if (this.current != null && !this.current.getMovingObjects().contains(this)) {
                  this.current.getMovingObjects().add(this);
                  this.movingSq = this.current;
               }

               this.ensureOnTile();
               this.square = this.current;
               this.scriptnx = this.nx;
               this.scriptny = this.ny;
               this.firstUpdate = false;
            }
         }
      }
   }

   public void ensureOnTile() {
      if (this.current == null) {
         if (!(this instanceof IsoPlayer)) {
            if (this instanceof IsoSurvivor) {
               IsoWorld.instance.CurrentCell.Remove(this);
               IsoWorld.instance.CurrentCell.getSurvivorList().remove((IsoSurvivor)this);
            }

            return;
         }

         boolean var1 = true;
         boolean var2 = false;
         if (this.last != null && (this.last.Has(IsoObjectType.stairsTN) || this.last.Has(IsoObjectType.stairsTW))) {
            this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z + 1);
            var1 = false;
         }

         if (this.current == null) {
            this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z);
            return;
         }

         if (var1) {
            this.x = this.nx = this.scriptnx = (float)this.current.getX() + 0.5F;
            this.y = this.ny = this.scriptny = (float)this.current.getY() + 0.5F;
         }

         this.z = (float)this.current.getZ();
      }

   }

   public void preupdate() {
      this.nx = this.x;
      this.ny = this.y;
   }

   public void renderlast() {
   }

   public void spotted(IsoMovingObject var1, boolean var2) {
   }

   public void update() {
      if (this.def == null) {
         this.def = IsoSpriteInstance.get(this.sprite);
      }

      this.movementLastFrame.x = this.x - this.lx;
      this.movementLastFrame.y = this.y - this.ly;
      this.lx = this.x;
      this.ly = this.y;
      this.lz = this.z;
      this.square = this.current;
      if (this.sprite != null) {
         this.sprite.update(this.def);
      }

      this.StateEventDelayTimer -= GameTime.instance.getMultiplier();
   }

   private void Collided() {
      this.collidedThisFrame = true;
   }

   public int compareToY(IsoMovingObject var1) {
      if (this.sprite == null && var1.sprite == null) {
         return 0;
      } else if (this.sprite != null && var1.sprite == null) {
         return -1;
      } else if (this.sprite == null && var1.sprite != null) {
         return 1;
      } else {
         float var2 = IsoUtils.YToScreen(this.x, this.y, this.z, 0);
         float var3 = IsoUtils.YToScreen(var1.x, var1.y, var1.z, 0);
         double var4 = (double)var2;
         double var6 = (double)var3;
         if (var4 > var6) {
            return 1;
         } else {
            return var4 < var6 ? -1 : 0;
         }
      }
   }

   public float distToNearestCamCharacter() {
      float var1 = Float.MAX_VALUE;

      for(int var2 = 0; var2 < IsoPlayer.numPlayers; ++var2) {
         IsoPlayer var3 = IsoPlayer.players[var2];
         if (var3 != null) {
            var1 = Math.min(var1, this.DistTo(var3));
         }
      }

      return var1;
   }

   public void seperate() {
      if (this.current != null) {
         if (this.solid) {
            if (!(this instanceof IsoPlayer) || !((IsoPlayer)this).GhostMode) {
               if (this instanceof IsoGameCharacter) {
                  if (((IsoGameCharacter)this).getStateMachine().getCurrent() == StaggerBackDieState.instance() || ((IsoGameCharacter)this).getStateMachine().getCurrent() == FakeDeadZombieState.instance()) {
                     return;
                  }

                  if (GameClient.bClient && this.isOnFloor()) {
                     return;
                  }
               }

               if (this.z < 0.0F) {
                  this.z = 0.0F;
               }

               for(int var1 = -1; var1 <= 1; ++var1) {
                  for(int var2 = -1; var2 <= 1; ++var2) {
                     IsoGridSquare var3 = this.getCell().getGridSquare(this.current.getX() + var1, this.current.getY() + var2, (int)this.z);
                     if (var3 != null) {
                        for(int var4 = 0; var4 < var3.getMovingObjects().size(); ++var4) {
                           IsoMovingObject var5 = (IsoMovingObject)var3.getMovingObjects().get(var4);
                           if (!(var5 instanceof IsoZombieGiblets) && var5 != this && var5.solid && (!(var5 instanceof IsoPlayer) || !((IsoPlayer)var5).GhostMode) && !var5.isOnFloor()) {
                              float var6 = this.width + var5.width;
                              if (tempo == null) {
                                 tempo = new Vector2(this.nx, this.ny);
                              } else {
                                 tempo.x = this.nx;
                                 tempo.y = this.ny;
                              }

                              Vector2 var10000 = tempo;
                              var10000.x -= var5.nx;
                              var10000 = tempo;
                              var10000.y -= var5.ny;
                              if (!(Math.abs(this.z - var5.z) > 0.3F)) {
                                 float var7;
                                 if (this instanceof IsoGameCharacter && var5 instanceof IsoGameCharacter) {
                                    var7 = tempo.getLength();
                                    if (var7 < var6 && (GameServer.bServer || this.distToNearestCamCharacter() < 60.0F) && var7 < var6) {
                                       tempo.setLength((var7 - var6) / 8.0F);
                                       if (((IsoGameCharacter)var5).compareMovePriority((IsoGameCharacter)this) >= 0) {
                                          this.nx -= tempo.x;
                                          this.ny -= tempo.y;
                                       }

                                       if (((IsoGameCharacter)this).compareMovePriority((IsoGameCharacter)var5) >= 0) {
                                          var5.nx += tempo.x;
                                          var5.ny += tempo.y;
                                       }

                                       this.collideWith(var5);
                                       var5.collideWith(this);
                                    }
                                 } else if (!(this instanceof IsoGameCharacter) || !(var5 instanceof BaseVehicle)) {
                                    var7 = tempo.getLength();
                                    if (var7 < var6 * 1.0F) {
                                       CollisionManager.instance.AddContact(this, var5);
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

            }
         }
      }
   }

   private boolean DoCollide(int var1) {
      this.current = this.getCell().getGridSquare((int)this.nx, (int)this.ny, (int)this.z);
      int var2 = (int)this.z;
      int var3;
      int var4;
      int var5;
      if (this instanceof IsoMolotovCocktail) {
         for(var3 = (int)this.z; var3 > 0; --var3) {
            for(var4 = -1; var4 <= 1; ++var4) {
               for(var5 = -1; var5 <= 1; ++var5) {
                  IsoGridSquare var6 = this.getCell().createNewGridSquare((int)this.nx + var5, (int)this.ny + var4, var3, false);
                  if (var6 != null) {
                     var6.RecalcAllWithNeighbours(true);
                  }
               }
            }
         }
      }

      if (this.current != null) {
         if (!this.current.TreatAsSolidFloor()) {
            this.current = this.getCell().getGridSquare((int)this.nx, (int)this.ny, (int)this.z);
         }

         if (this.current == null) {
            return false;
         }

         this.current = this.getCell().getGridSquare((int)this.nx, (int)this.ny, (int)this.z);
      }

      if (this.current != this.last && this.last != null && this.current != null) {
         if (this == IsoCamera.CamCharacter) {
            IsoWorld.instance.CurrentCell.lightUpdateCount = 10;
         }

         var3 = this.current.getX() - this.last.getX();
         var4 = this.current.getY() - this.last.getY();
         var5 = this.current.getZ() - this.last.getZ();
         boolean var19 = false;
         boolean var7 = false;
         if (var3 != 0 && var4 == 0) {
            var19 = true;
         }

         boolean var8 = false;
         if (this.last.testCollideAdjacent(this, var3, var4, var5) || this.current == null) {
            var8 = true;
         }

         if (var8) {
            if (this.last.getX() < this.current.getX()) {
               this.collidedE = true;
            }

            if (this.last.getX() > this.current.getX()) {
               this.collidedW = true;
            }

            if (this.last.getY() < this.current.getY()) {
               this.collidedS = true;
            }

            if (this.last.getY() > this.current.getY()) {
               this.collidedN = true;
            }

            if (this instanceof IsoZombie) {
               IsoZombie var9;
               if (this.collidedW && !this.collidedN && !this.collidedS && this.last.Is(IsoFlagType.HoppableW)) {
                  var9 = (IsoZombie)this;
                  if (!var9.bCrawling) {
                     if (var9.getCurrentState() != StaggerBackState.instance() && var9.getCurrentState() != AttackState.instance()) {
                        var9.StateMachineParams.put(0, IsoDirections.W);
                        var9.getStateMachine().changeState(ClimbOverFenceState.instance());
                     } else {
                        var9.getStateMachine().changeState(AttackState.instance());
                     }
                  }
               }

               if (this.collidedN && !this.collidedE && !this.collidedW && this.last.Is(IsoFlagType.HoppableN)) {
                  var9 = (IsoZombie)this;
                  if (!var9.bCrawling) {
                     if (var9.getCurrentState() != StaggerBackState.instance() && var9.getCurrentState() != AttackState.instance()) {
                        var9.StateMachineParams.put(0, IsoDirections.N);
                        var9.getStateMachine().changeState(ClimbOverFenceState.instance());
                     } else {
                        var9.getStateMachine().changeState(AttackState.instance());
                     }
                  }
               }

               IsoZombie var10;
               IsoGridSquare var22;
               if (this.collidedS && !this.collidedE && !this.collidedW) {
                  var22 = this.last.nav[IsoDirections.S.index()];
                  if (var22 != null && var22.Is(IsoFlagType.HoppableN)) {
                     var10 = (IsoZombie)this;
                     if (!var10.bCrawling) {
                        if (var10.getCurrentState() != StaggerBackState.instance() && var10.getCurrentState() != AttackState.instance()) {
                           var10.StateMachineParams.put(0, IsoDirections.S);
                           var10.getStateMachine().changeState(ClimbOverFenceState.instance());
                        } else {
                           var10.getStateMachine().changeState(AttackState.instance());
                        }
                     }
                  }
               }

               if (this.collidedE && !this.collidedN && !this.collidedS) {
                  var22 = this.last.nav[IsoDirections.E.index()];
                  if (var22 != null && var22.Is(IsoFlagType.HoppableW)) {
                     var10 = (IsoZombie)this;
                     if (!var10.bCrawling) {
                        if (var10.getCurrentState() != StaggerBackState.instance() && var10.getCurrentState() != AttackState.instance()) {
                           var10.StateMachineParams.put(0, IsoDirections.E);
                           var10.getStateMachine().changeState(ClimbOverFenceState.instance());
                        } else {
                           var10.getStateMachine().changeState(AttackState.instance());
                        }
                     }
                  }
               }
            }

            if (var1 == 2) {
               if ((this.collidedS || this.collidedN) && (this.collidedE || this.collidedW)) {
                  this.collidedS = false;
                  this.collidedN = false;
               }
            } else if (var1 == 1 && (this.collidedS || this.collidedN) && (this.collidedE || this.collidedW)) {
               this.collidedW = false;
               this.collidedE = false;
            }

            this.current = this.last;
            this.Collided();
            return true;
         }
      } else if (this.nx != this.lx || this.ny != this.ly) {
         if (this instanceof IsoPlayer) {
            boolean var11 = false;
         }

         if (this.current == null) {
            if (this.nx < this.lx) {
               this.collidedW = true;
            }

            if (this.nx > this.lx) {
               this.collidedE = true;
            }

            if (this.ny < this.ly) {
               this.collidedN = true;
            }

            if (this.ny > this.ly) {
               this.collidedS = true;
            }

            this.nx = this.lx;
            this.ny = this.ly;
            this.current = this.last;
            this.Collided();
            return true;
         }

         if (this instanceof IsoGameCharacter) {
            IsoGameCharacter var12 = (IsoGameCharacter)this;
            if (var12.getPath2() != null) {
               PathFindBehavior2 var14 = var12.getPathFindBehavior2();
               if ((int)var14.getTargetX() == (int)this.x && (int)var14.getTargetY() == (int)this.y && (int)var14.getTargetZ() == (int)this.z) {
                  return false;
               }
            }
         }

         IsoGridSquare var13 = this.getFeelerTile(this.feelersize);
         if (!GameServer.bServer && this instanceof IsoPlayer && var13 != this.current && ((IsoPlayer)this).TargetSpeed > 0.05F && !((IsoPlayer)this).JustMoved && var13 != null) {
            IsoObject var15 = this.current.getDoorTo(var13);
            if (var15 != null) {
               IsoGridSquare var17;
               if (var15 instanceof IsoDoor) {
                  var17 = this.getFeelerTile(this.feelersize * 1.5F);
                  if (!((IsoDoor)var15).open && var17 == var13) {
                     ((IsoDoor)var15).ToggleDoor((IsoPlayer)this);
                  }
               } else if (var15 instanceof IsoThumpable) {
                  var17 = this.getFeelerTile(this.feelersize * 1.5F);
                  if (!((IsoThumpable)var15).open && var17 == var13) {
                     ((IsoThumpable)var15).ToggleDoor((IsoPlayer)this);
                  }
               }
            }
         }

         if (this instanceof IsoGameCharacter && ((IsoGameCharacter)this).isClimbing()) {
            var13 = this.current;
         }

         if (var13 != null && var13 != this.current && this.current != null) {
            if (this == IsoCamera.CamCharacter) {
               boolean var16 = false;
            }

            if (this.current.testCollideAdjacent(this, var13.getX() - this.current.getX(), var13.getY() - this.current.getY(), var13.getZ() - this.current.getZ())) {
               if (this.last != null) {
                  if (this.current.getX() < var13.getX()) {
                     this.collidedE = true;
                  }

                  if (this.current.getX() > var13.getX()) {
                     this.collidedW = true;
                  }

                  if (this.current.getY() < var13.getY()) {
                     this.collidedS = true;
                  }

                  if (this.current.getY() > var13.getY()) {
                     this.collidedN = true;
                  }

                  if (this instanceof IsoZombie) {
                     IsoZombie var18;
                     if (this.collidedW && !this.collidedN && !this.collidedS && this.current.Is(IsoFlagType.HoppableW)) {
                        var18 = (IsoZombie)this;
                        if (!var18.bCrawling) {
                           if (var18.getCurrentState() != StaggerBackState.instance() && var18.getCurrentState() != AttackState.instance()) {
                              var18.StateMachineParams.put(0, IsoDirections.W);
                              var18.getStateMachine().changeState(ClimbOverFenceState.instance());
                           } else {
                              var18.getStateMachine().changeState(AttackState.instance());
                           }
                        }
                     }

                     if (this.collidedN && !this.collidedE && !this.collidedW && this.current.Is(IsoFlagType.HoppableN)) {
                        var18 = (IsoZombie)this;
                        if (!var18.bCrawling) {
                           if (var18.getCurrentState() != StaggerBackState.instance() && var18.getCurrentState() != AttackState.instance()) {
                              var18.StateMachineParams.put(0, IsoDirections.N);
                              var18.getStateMachine().changeState(ClimbOverFenceState.instance());
                           } else {
                              var18.getStateMachine().changeState(AttackState.instance());
                           }
                        }
                     }

                     IsoGridSquare var20;
                     IsoZombie var21;
                     if (this.collidedS && !this.collidedE && !this.collidedW) {
                        var20 = this.last.nav[IsoDirections.S.index()];
                        if (var20 != null && var20.Is(IsoFlagType.HoppableN)) {
                           var21 = (IsoZombie)this;
                           if (!var21.bCrawling) {
                              if (var21.getCurrentState() != StaggerBackState.instance() && var21.getCurrentState() != AttackState.instance()) {
                                 var21.StateMachineParams.put(0, IsoDirections.S);
                                 var21.getStateMachine().changeState(ClimbOverFenceState.instance());
                              } else {
                                 var21.getStateMachine().changeState(AttackState.instance());
                              }
                           }
                        }
                     }

                     if (this.collidedE && !this.collidedN && !this.collidedS) {
                        var20 = this.last.nav[IsoDirections.E.index()];
                        if (var20 != null && var20.Is(IsoFlagType.HoppableW)) {
                           var21 = (IsoZombie)this;
                           if (!var21.bCrawling) {
                              if (var21.getCurrentState() != StaggerBackState.instance() && var21.getCurrentState() != AttackState.instance()) {
                                 var21.StateMachineParams.put(0, IsoDirections.E);
                                 var21.getStateMachine().changeState(ClimbOverFenceState.instance());
                              } else {
                                 var21.getStateMachine().changeState(AttackState.instance());
                              }
                           }
                        }
                     }
                  }

                  if (var1 == 2 && (this.collidedS || this.collidedN) && (this.collidedE || this.collidedW)) {
                     this.collidedS = false;
                     this.collidedN = false;
                  }

                  if (var1 == 1 && (this.collidedS || this.collidedN) && (this.collidedE || this.collidedW)) {
                     this.collidedW = false;
                     this.collidedE = false;
                  }
               }

               this.Collided();
               return true;
            }
         }
      }

      return false;
   }

   public IsoGridSquare getFeelerTile(float var1) {
      Vector2 var2 = tempo;
      var2.x = this.nx - this.lx;
      var2.y = this.ny - this.ly;
      var2.setLength(var1);
      return this.getCell().getGridSquare((int)(this.x + var2.x), (int)(this.y + var2.y), (int)this.z);
   }

   public void DoCollideNorS() {
      this.ny = this.ly;
   }

   public void DoCollideWorE() {
      this.nx = this.lx;
   }

   public int getTimeSinceZombieAttack() {
      return this.TimeSinceZombieAttack;
   }

   public void setTimeSinceZombieAttack(int var1) {
      this.TimeSinceZombieAttack = var1;
   }

   public boolean isCollidedE() {
      return this.collidedE;
   }

   public void setCollidedE(boolean var1) {
      this.collidedE = var1;
   }

   public boolean isCollidedN() {
      return this.collidedN;
   }

   public void setCollidedN(boolean var1) {
      this.collidedN = var1;
   }

   public IsoObject getCollidedObject() {
      return this.CollidedObject;
   }

   public void setCollidedObject(IsoObject var1) {
      this.CollidedObject = var1;
   }

   public boolean isCollidedS() {
      return this.collidedS;
   }

   public void setCollidedS(boolean var1) {
      this.collidedS = var1;
   }

   public boolean isCollidedThisFrame() {
      return this.collidedThisFrame;
   }

   public void setCollidedThisFrame(boolean var1) {
      this.collidedThisFrame = var1;
   }

   public boolean isCollidedW() {
      return this.collidedW;
   }

   public void setCollidedW(boolean var1) {
      this.collidedW = var1;
   }

   public boolean isCollidedWithDoor() {
      return this.CollidedWithDoor;
   }

   public void setCollidedWithDoor(boolean var1) {
      this.CollidedWithDoor = var1;
   }

   public boolean isCollidedWithVehicle() {
      return this.collidedWithVehicle;
   }

   public IsoGridSquare getCurrentSquare() {
      return this.current;
   }

   public IsoMetaGrid.Zone getCurrentZone() {
      return this.current != null ? this.current.getZone() : null;
   }

   public void setCurrent(IsoGridSquare var1) {
      this.current = var1;
   }

   public boolean isDestroyed() {
      return this.destroyed;
   }

   public void setDestroyed(boolean var1) {
      this.destroyed = var1;
   }

   public boolean isFirstUpdate() {
      return this.firstUpdate;
   }

   public void setFirstUpdate(boolean var1) {
      this.firstUpdate = var1;
   }

   public Vector2 getHitDir() {
      return this.hitDir;
   }

   public void setHitDir(Vector2 var1) {
      this.hitDir.set(var1);
   }

   public boolean isAllowBehaviours() {
      return this.AllowBehaviours;
   }

   public void setAllowBehaviours(boolean var1) {
      this.AllowBehaviours = var1;
   }

   public float getImpulsex() {
      return this.impulsex;
   }

   public void setImpulsex(float var1) {
      this.impulsex = var1;
   }

   public float getImpulsey() {
      return this.impulsey;
   }

   public void setImpulsey(float var1) {
      this.impulsey = var1;
   }

   public float getLimpulsex() {
      return this.limpulsex;
   }

   public void setLimpulsex(float var1) {
      this.limpulsex = var1;
   }

   public float getLimpulsey() {
      return this.limpulsey;
   }

   public void setLimpulsey(float var1) {
      this.limpulsey = var1;
   }

   public float getHitForce() {
      return this.hitForce;
   }

   public void setHitForce(float var1) {
      this.hitForce = var1;
   }

   public float getHitFromAngle() {
      return this.hitFromAngle;
   }

   public void setHitFromAngle(float var1) {
      this.hitFromAngle = var1;
   }

   public void setID(int var1) {
      this.ID = var1;
   }

   public IsoGridSquare getLastSquare() {
      return this.last;
   }

   public void setLast(IsoGridSquare var1) {
      this.last = var1;
   }

   public float getLx() {
      return this.lx;
   }

   public void setLx(float var1) {
      this.lx = var1;
   }

   public float getLy() {
      return this.ly;
   }

   public void setLy(float var1) {
      this.ly = var1;
   }

   public float getLz() {
      return this.lz;
   }

   public void setLz(float var1) {
      this.lz = var1;
   }

   public float getNx() {
      return this.nx;
   }

   public void setNx(float var1) {
      this.nx = var1;
   }

   public float getNy() {
      return this.ny;
   }

   public void setNy(float var1) {
      this.ny = var1;
   }

   public void setNoDamage(boolean var1) {
      this.noDamage = var1;
   }

   public boolean getNoDamage() {
      return this.noDamage;
   }

   public void setPathFindIndex(int var1) {
      this.PathFindIndex = var1;
   }

   public boolean isSolid() {
      return this.solid;
   }

   public void setSolid(boolean var1) {
      this.solid = var1;
   }

   public float getStateEventDelayTimer() {
      return this.StateEventDelayTimer;
   }

   public void setStateEventDelayTimer(float var1) {
      this.StateEventDelayTimer = var1;
   }

   public void setThumpTarget(Thumpable var1) {
      this.thumpTarget = var1;
   }

   public float getWidth() {
      return this.width;
   }

   public void setWidth(float var1) {
      this.width = var1;
   }

   public void setX(float var1) {
      this.x = var1;
      this.nx = var1;
      this.scriptnx = var1;
   }

   public void setY(float var1) {
      this.y = var1;
      this.ny = var1;
      this.scriptny = var1;
   }

   public void setZ(float var1) {
      this.z = var1;
      this.lz = var1;
      this.bz = var1;
   }

   public boolean isbAltCollide() {
      return this.bAltCollide;
   }

   public void setbAltCollide(boolean var1) {
      this.bAltCollide = var1;
   }

   public boolean isShootable() {
      return this.shootable;
   }

   public void setShootable(boolean var1) {
      this.shootable = var1;
   }

   public IsoZombie getLastTargettedBy() {
      return this.lastTargettedBy;
   }

   public void setLastTargettedBy(IsoZombie var1) {
      this.lastTargettedBy = var1;
   }

   public boolean isCollidable() {
      return this.Collidable;
   }

   public void setCollidable(boolean var1) {
      this.Collidable = var1;
   }

   public float getScriptnx() {
      return this.scriptnx;
   }

   public void setScriptnx(float var1) {
      this.scriptnx = var1;
   }

   public float getScriptny() {
      return this.scriptny;
   }

   public void setScriptny(float var1) {
      this.scriptny = var1;
   }

   public String getScriptModule() {
      return this.ScriptModule;
   }

   public void setScriptModule(String var1) {
      this.ScriptModule = var1;
   }

   public String getScriptName() {
      return this.ScriptName;
   }

   public void setScriptName(String var1) {
      this.ScriptName = var1;
   }

   public Stack getActiveInInstances() {
      return this.ActiveInInstances;
   }

   public void setActiveInInstances(Stack var1) {
      this.ActiveInInstances = var1;
   }

   public Vector2 getMovementLastFrame() {
      return this.movementLastFrame;
   }

   public void setMovementLastFrame(Vector2 var1) {
      this.movementLastFrame = var1;
   }

   public void setWeight(float var1) {
      this.weight = var1;
   }

   public float getFeelersize() {
      return this.feelersize;
   }

   public void setFeelersize(float var1) {
      this.feelersize = var1;
   }

   public void setOnFloor(boolean var1) {
      this.bOnFloor = var1;
   }

   public boolean isOnFloor() {
      return this.bOnFloor;
   }

   public void Despawn() {
   }

   public boolean isCloseKilled() {
      return this.closeKilled;
   }

   public void setCloseKilled(boolean var1) {
      this.closeKilled = var1;
   }

   public void setBlendSpeed(float var1) {
      this.bspeed = var1;
   }

   public Vector2 getFacingPosition(Vector2 var1) {
      var1.set(this.getX(), this.getY());
      return var1;
   }

   private boolean isInLoadedArea(int var1, int var2) {
      int var3;
      if (GameServer.bServer) {
         for(var3 = 0; var3 < ServerMap.instance.LoadedCells.size(); ++var3) {
            ServerMap.ServerCell var4 = (ServerMap.ServerCell)ServerMap.instance.LoadedCells.get(var3);
            if (var1 >= var4.WX * 70 && var1 < (var4.WX + 1) * 70 && var2 >= var4.WY * 70 && var2 < (var4.WY + 1) * 70) {
               return true;
            }
         }
      } else {
         for(var3 = 0; var3 < IsoPlayer.numPlayers; ++var3) {
            IsoChunkMap var5 = IsoWorld.instance.CurrentCell.ChunkMap[var3];
            if (!var5.ignore && var1 >= var5.getWorldXMinTiles() && var1 < var5.getWorldXMaxTiles() && var2 >= var5.getWorldYMinTiles() && var2 < var5.getWorldYMaxTiles()) {
               return true;
            }
         }
      }

      return false;
   }

   public static class TreeSoundManager {
      private ArrayList squares = new ArrayList();
      private long[] soundTime = new long[6];
      private Comparator comp = new Comparator() {
         public int compare(IsoGridSquare var1, IsoGridSquare var2) {
            float var3 = TreeSoundManager.this.getClosestListener((float)var1.x + 0.5F, (float)var1.y + 0.5F, (float)var1.z);
            float var4 = TreeSoundManager.this.getClosestListener((float)var2.x + 0.5F, (float)var2.y + 0.5F, (float)var2.z);
            if (var3 > var4) {
               return 1;
            } else {
               return var3 < var4 ? -1 : 0;
            }
         }
      };

      public void addSquare(IsoGridSquare var1) {
         if (!this.squares.contains(var1)) {
            this.squares.add(var1);
         }

      }

      public void update() {
         if (!this.squares.isEmpty()) {
            Collections.sort(this.squares, this.comp);
            long var1 = System.currentTimeMillis();

            for(int var3 = 0; var3 < this.soundTime.length && var3 < this.squares.size(); ++var3) {
               IsoGridSquare var4 = (IsoGridSquare)this.squares.get(var3);
               if (!(this.getClosestListener((float)var4.x + 0.5F, (float)var4.y + 0.5F, (float)var4.z) > 20.0F)) {
                  int var5 = this.getFreeSoundSlot(var1);
                  if (var5 == -1) {
                     break;
                  }

                  Audio var6 = null;
                  float var7 = 0.05F;
                  float var8 = 16.0F;
                  float var9 = 0.29999998F;
                  if (GameClient.bClient) {
                     var6 = SoundManager.instance.PlayWorldSoundImpl("Bushes", false, var4.getX(), var4.getY(), var4.getZ(), var7, var8, var9, false);
                  } else {
                     BaseSoundEmitter var10 = IsoWorld.instance.getFreeEmitter((float)var4.x + 0.5F, (float)var4.y + 0.5F, (float)var4.z);
                     if (var10.playSound("Bushes") != 0L) {
                        this.soundTime[var5] = var1;
                     }
                  }

                  if (var6 != null) {
                     this.soundTime[var5] = var1;
                  }
               }
            }

            this.squares.clear();
         }
      }

      private float getClosestListener(float var1, float var2, float var3) {
         float var4 = Float.MAX_VALUE;

         for(int var5 = 0; var5 < IsoPlayer.numPlayers; ++var5) {
            IsoPlayer var6 = IsoPlayer.players[var5];
            if (var6 != null && var6.getCurrentSquare() != null) {
               float var7 = var6.getX();
               float var8 = var6.getY();
               float var9 = var6.getZ();
               float var10 = IsoUtils.DistanceTo(var7, var8, var9 * 3.0F, var1, var2, var3 * 3.0F);
               if (var6.HasTrait("HardOfHearing")) {
                  var10 *= 4.5F;
               }

               if (var10 < var4) {
                  var4 = var10;
               }
            }
         }

         return var4;
      }

      private int getFreeSoundSlot(long var1) {
         long var3 = Long.MAX_VALUE;
         int var5 = -1;

         for(int var6 = 0; var6 < this.soundTime.length; ++var6) {
            if (this.soundTime[var6] < var3) {
               var3 = this.soundTime[var6];
               var5 = var6;
            }
         }

         if (var1 - var3 < 1000L) {
            return -1;
         } else {
            return var5;
         }
      }
   }
}
