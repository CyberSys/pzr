package zombie.characters;

import java.util.LinkedList;
import zombie.GameTime;
import zombie.ai.states.FishingState;
import zombie.core.math.PZMath;
import zombie.core.utils.UpdateLimit;
import zombie.core.utils.UpdateTimer;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.network.GameServer;
import zombie.network.packets.EventUpdatePacket;
import zombie.network.packets.PlayerPacket;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleManager;

public class NetworkPlayerAI {
   public final LinkedList events = new LinkedList();
   public NetworkPlayerAI.Event lastEvent = null;
   public boolean pressedMovement = false;
   public float climbSpeed = 0.16F;
   public float climbDownSpeed = 0.16F;
   IsoPlayer player;
   private PathFindBehavior2 pfb2 = null;
   private final UpdateTimer timer = new UpdateTimer();
   private byte lastDirection = 0;
   private boolean needUpdate = false;
   public boolean usePathFind = false;
   public float targetX = 0.0F;
   public float targetY = 0.0F;
   public int targetZ = 0;
   public int targetT = 0;
   public boolean needToMovingUsingPathFinder = false;
   public boolean forcePathFinder = false;
   public Vector2 direction = new Vector2();
   public Vector2 distance = new Vector2();
   public boolean moving = false;
   public static float controlQuality = 0.0F;
   public byte footstepSoundRadius = 0;
   public int lastBooleanVariables = 0;
   public float lastForwardDirection = 0.0F;
   public float lastPlayerMoveDirLen = 0.0F;
   private static final int predictInterval = 1000;

   public NetworkPlayerAI(IsoPlayer var1) {
      this.player = var1;
      this.pfb2 = this.player.getPathFindBehavior2();
   }

   public void needToUpdate() {
      this.needUpdate = true;
   }

   public boolean isNeedToUpdate() {
      int var1 = NetworkPlayerVariables.getBooleanVariables(this.player);
      byte var2 = (byte)((int)(this.player.playerMoveDir.getDirection() * 10.0F));
      if (!this.timer.check() && var1 == this.lastBooleanVariables && this.lastDirection == var2 && !this.needUpdate) {
         return false;
      } else {
         this.lastDirection = var2;
         this.needUpdate = false;
         return true;
      }
   }

   public void setUpdateTimer(float var1) {
      this.timer.reset((long)PZMath.clamp((int)var1, 200, 3800));
   }

   private void setUsingExtrapolation(PlayerPacket var1, int var2, int var3) {
      Vector2 var4 = this.player.dir.ToVector();
      this.player.networkCharacter.checkResetPlayer(var2);
      NetworkCharacter.Transform var5 = this.player.networkCharacter.predict(var3, var2, this.player.x, this.player.y, var4.x, var4.y);
      if (this.player.z == this.pfb2.getTargetZ() && !PolygonalMap2.instance.lineClearCollide(this.player.x, this.player.y, var5.position.x, var5.position.y, (int)this.player.z, (IsoMovingObject)null)) {
         var1.x = var5.position.x;
         var1.y = var5.position.y;
         var1.z = (byte)((int)this.pfb2.getTargetZ());
         var1.t = var5.time;
      } else {
         Vector2 var6 = PolygonalMap2.instance.getCollidepoint(this.player.x, this.player.y, var5.position.x, var5.position.y, (int)this.player.z, (IsoMovingObject)null, 2);
         var1.x = var6.x;
         var1.y = var6.y;
         var1.z = (byte)((int)this.player.z);
         var1.t = (int)((float)var2 + (float)var3 * IsoUtils.DistanceTo(this.player.x, this.player.y, var6.x, var6.y) / IsoUtils.DistanceTo(this.player.x, this.player.y, var5.position.x, var5.position.y));
      }

      var1.usePathFinder = false;
   }

   private void setUsingPathFindState(PlayerPacket var1, int var2) {
      this.player.networkCharacter.checkResetPlayer(var2);
      var1.x = this.pfb2.getTargetX();
      var1.y = this.pfb2.getTargetY();
      var1.z = (byte)((int)this.pfb2.getTargetZ());
      var1.t = -1;
      var1.usePathFinder = true;
   }

   public boolean set(PlayerPacket var1) {
      int var2 = (int)(GameTime.getServerTime() / 1000000L);
      var1.realx = this.player.x;
      var1.realy = this.player.y;
      var1.realz = (byte)((int)this.player.z);
      var1.realdir = (byte)this.player.dir.index();
      var1.realt = var2;
      if (this.player.vehicle == null) {
         var1.VehicleID = -1;
         var1.VehicleSeat = -1;
      } else {
         var1.VehicleID = this.player.vehicle.VehicleID;
         var1.VehicleSeat = (short)this.player.vehicle.getSeat(this.player);
      }

      boolean var3 = this.timer.check();
      if (var3) {
         this.setUpdateTimer(600.0F);
      }

      if (this.pfb2.isMovingUsingPathFind()) {
         this.setUsingPathFindState(var1, var2);
      } else {
         this.setUsingExtrapolation(var1, var2, 1000);
      }

      boolean var4 = (double)this.player.playerMoveDir.getLength() < 0.01D && this.lastPlayerMoveDirLen > 0.01F;
      this.lastPlayerMoveDirLen = this.player.playerMoveDir.getLength();
      var1.booleanVariables = NetworkPlayerVariables.getBooleanVariables(this.player);
      this.pressedMovement = false;
      boolean var5 = this.lastBooleanVariables != var1.booleanVariables;
      this.lastBooleanVariables = var1.booleanVariables;
      var1.direction = this.player.getForwardDirection().getDirection();
      boolean var6 = Math.abs(this.lastForwardDirection - var1.direction) > 0.2F;
      this.lastForwardDirection = var1.direction;
      var1.footstepSoundRadius = this.footstepSoundRadius;
      return var3 || var5 || var6 || this.player.JustMoved || var4;
   }

   public void parse(PlayerPacket var1) {
      if (!this.player.isTeleporting()) {
         this.targetX = var1.x;
         this.targetY = var1.y;
         this.targetZ = var1.z;
         this.targetT = var1.t;
         if (this.targetX == this.player.x && this.targetY == this.player.y) {
            this.player.JustMoved = false;
         } else {
            this.player.JustMoved = true;
         }

         this.needToMovingUsingPathFinder = var1.usePathFinder;
         this.direction.set((float)Math.cos((double)var1.direction), (float)Math.sin((double)var1.direction));
         this.distance.set(var1.x - this.player.x, var1.y - this.player.y);
         if (this.usePathFind) {
            this.pfb2.pathToLocationF(var1.x, var1.y, (float)var1.z);
            this.pfb2.walkingOnTheSpot.reset(this.player.x, this.player.y);
            this.pfb2.setTargetT(this.targetT);
         }

         BaseVehicle var2 = VehicleManager.instance.getVehicleByID(var1.VehicleID);
         NetworkPlayerVariables.setBooleanVariables(this.player, var1.booleanVariables);
         this.player.setbSeenThisFrame(false);
         this.player.setbCouldBeSeenThisFrame(false);
         this.player.TimeSinceLastNetData = 0;
         this.player.ensureOnTile();
         this.player.realx = var1.realx;
         this.player.realy = var1.realy;
         this.player.realz = var1.realz;
         this.player.realdir = IsoDirections.fromIndex(var1.realdir);
         this.player.lastUpdateX = this.player.x;
         this.player.lastUpdateY = this.player.y;
         this.player.lastUpdateT = (float)(GameTime.getServerTime() / 1000000L);
         int var3 = (int)(GameTime.getServerTime() / 1000000L);
         if (var1.t == var1.realt) {
            controlQuality = 1.0F;
         } else {
            controlQuality = 1.0F - Math.min(1.0F, Math.max(0.0F, (float)((var3 - var1.realt) / (var1.t - var1.realt))));
         }

         this.footstepSoundRadius = var1.footstepSoundRadius;
         String var10000;
         IsoGameCharacter var4;
         if (this.player.getVehicle() == null) {
            if (var2 != null) {
               if (var1.VehicleSeat >= 0 && var1.VehicleSeat < var2.getMaxPassengers()) {
                  var4 = var2.getCharacter(var1.VehicleSeat);
                  if (var4 == null) {
                     if (GameServer.bDebug) {
                        DebugLog.log(this.player.getUsername() + " got in vehicle " + var2.VehicleID + " seat " + var1.VehicleSeat);
                     }

                     var2.enterRSync(var1.VehicleSeat, this.player, var2);
                  } else if (var4 != this.player) {
                     var10000 = this.player.getUsername();
                     DebugLog.log(var10000 + " got in same seat as " + ((IsoPlayer)var4).getUsername());
                     this.player.sendObjectChange("exitVehicle");
                  }
               } else {
                  DebugLog.log(this.player.getUsername() + " invalid seat vehicle " + var2.VehicleID + " seat " + var1.VehicleSeat);
               }
            }
         } else if (var2 != null) {
            if (var2 == this.player.getVehicle() && this.player.getVehicle().getSeat(this.player) != -1) {
               var4 = var2.getCharacter(var1.VehicleSeat);
               if (var4 == null) {
                  if (var2.getSeat(this.player) != var1.VehicleSeat) {
                     var2.switchSeatRSync(this.player, var1.VehicleSeat);
                  }
               } else if (var4 != this.player) {
                  var10000 = this.player.getUsername();
                  DebugLog.log(var10000 + " switched to same seat as " + ((IsoPlayer)var4).getUsername());
                  this.player.sendObjectChange("exitVehicle");
               }
            } else {
               var10000 = this.player.getUsername();
               DebugLog.log(var10000 + " vehicle/seat remote " + var2.VehicleID + "/" + var1.VehicleSeat + " local " + this.player.getVehicle().VehicleID + "/" + this.player.getVehicle().getSeat(this.player));
               this.player.sendObjectChange("exitVehicle");
            }
         } else {
            this.player.getVehicle().exitRSync(this.player);
            this.player.setVehicle((BaseVehicle)null);
         }

      }
   }

   public static class Event {
      public static final UpdateLimit eventTimer = new UpdateLimit(7000L);
      public short id;
      public float x;
      public float y;
      public float z;
      public byte dir;
      public byte name;
      public String type1;
      public String type2;
      public String type3;
      public String type4;
      public float param1;
      public float param2;
      public float walkInjury;
      public float walkSpeed;
      public int booleanVariables;

      public Event(NetworkPlayerAI.Event var1) {
         this.set(var1);
         eventTimer.Reset(5000L);
      }

      public Event() {
      }

      public void set(NetworkPlayerAI.Event var1) {
         this.id = var1.id;
         this.x = var1.x;
         this.y = var1.y;
         this.z = var1.z;
         this.dir = var1.dir;
         this.name = var1.name;
         this.type1 = var1.type1;
         this.type2 = var1.type2;
         this.type3 = var1.type3;
         this.type4 = var1.type4;
         this.param1 = var1.param1;
         this.param2 = var1.param2;
         this.walkInjury = var1.walkInjury;
         this.walkSpeed = var1.walkSpeed;
         this.booleanVariables = var1.booleanVariables;
      }

      public boolean isMovableEvent() {
         if (this.name != -1 && this.name != -2) {
            if (this.name >= 0 && this.name < EventUpdatePacket.EventUpdate.values().length) {
               EventUpdatePacket.EventUpdate var1 = EventUpdatePacket.EventUpdate.values()[this.name];
               return EventUpdatePacket.EventUpdate.EventBandage.equals(var1) || EventUpdatePacket.EventUpdate.EventWearClothing.equals(var1) || EventUpdatePacket.EventUpdate.EventEating.equals(var1) || EventUpdatePacket.EventUpdate.EventDrinking.equals(var1) || EventUpdatePacket.EventUpdate.EventAttachItem.equals(var1) || EventUpdatePacket.EventUpdate.EventTakeWater.equals(var1) || EventUpdatePacket.EventUpdate.EventClimbFence.equals(var1) || EventUpdatePacket.EventUpdate.EventFallClimb.equals(var1) || EventUpdatePacket.EventUpdate.EventReloading.equals(var1);
            } else {
               return false;
            }
         } else {
            return "Drink".equals(this.type1) || "Eat".equals(this.type1);
         }
      }

      public String getDescription() {
         String var1;
         if (this.name == -1) {
            var1 = "start action";
         } else if (this.name == -2) {
            var1 = "end action";
         } else if (this.name >= 0 && this.name < EventUpdatePacket.EventUpdate.values().length) {
            var1 = EventUpdatePacket.EventUpdate.values()[this.name].name();
         } else {
            var1 = "unknown";
         }

         return String.format("%s(%d), %s %s  %s %s %f %f", var1, this.name, this.type1, this.type2, this.type3, this.type4, this.param1, this.param2);
      }

      public IsoWindow getWindow(IsoPlayer var1) {
         IsoDirections[] var2 = IsoDirections.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            IsoDirections var5 = var2[var4];
            IsoObject var6 = var1.getContextDoorOrWindowOrWindowFrame(var5);
            if (var6 instanceof IsoWindow) {
               return (IsoWindow)var6;
            }
         }

         return null;
      }

      public IsoObject getObject(IsoPlayer var1) {
         IsoDirections[] var2 = IsoDirections.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            IsoDirections var5 = var2[var4];
            IsoObject var6 = var1.getContextDoorOrWindowOrWindowFrame(var5);
            if (var6 instanceof IsoWindow || var6 instanceof IsoThumpable || IsoWindowFrame.isWindowFrame(var6)) {
               return var6;
            }
         }

         return null;
      }

      private IsoDirections checkCurrentIsEventGridSquareFence(IsoPlayer var1) {
         IsoGridSquare var3 = var1.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z);
         IsoGridSquare var4 = var1.getCell().getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z);
         IsoGridSquare var5 = var1.getCell().getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z);
         IsoDirections var2;
         if (var3.Is(IsoFlagType.HoppableN)) {
            var2 = IsoDirections.N;
         } else if (var3.Is(IsoFlagType.HoppableW)) {
            var2 = IsoDirections.W;
         } else if (var4.Is(IsoFlagType.HoppableN)) {
            var2 = IsoDirections.S;
         } else if (var5.Is(IsoFlagType.HoppableW)) {
            var2 = IsoDirections.E;
         } else {
            var2 = IsoDirections.Max;
         }

         return var2;
      }

      public boolean isTimeout() {
         return eventTimer.Check();
      }

      public boolean process(IsoPlayer var1) {
         boolean var2 = false;
         var1.overridePrimaryHandModel = null;
         var1.overrideSecondaryHandModel = null;
         if (!this.requireNonMoving(this.name) || var1.getCurrentSquare() == var1.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z) && !var1.isPlayerMoving()) {
            NetworkPlayerVariables.setBooleanVariables(var1, this.booleanVariables);
            var1.setVariable("WalkInjury", this.walkInjury);
            var1.setVariable("WalkSpeed", this.walkSpeed);
            if (this.name == -1) {
               var1.setVariable("PerformingAction", this.type1);
               var1.setVariable("IsPerformingAnAction", true);
               var1.overridePrimaryHandModel = this.type2;
               var1.overrideSecondaryHandModel = this.type3;
               if (!StringUtils.isNullOrEmpty(this.type2) || !StringUtils.isNullOrEmpty(this.type3)) {
                  var1.forceNullOverride = true;
               }

               var1.resetModelNextFrame();
               var2 = true;
            } else if (this.name == -2) {
               NetworkPlayerAI.Event var3 = var1.networkAI.lastEvent;
               if (var3 != null && CharacterActionAnims.Reload.name().equals(var3.type1)) {
               }

               var1.clearNetworkEvents();
               var2 = true;
            } else if (this.name >= 0 && this.name < EventUpdatePacket.EventUpdate.values().length) {
               EventUpdatePacket.EventUpdate var8 = EventUpdatePacket.EventUpdate.values()[this.name];
               IsoWindow var4;
               switch(var8) {
               case EventCleanBlood:
                  var1.setVariable("PerformingAction", this.type1);
                  var1.setVariable("LootPosition", this.type2);
                  var1.forceNullOverride = true;
                  var1.overridePrimaryHandModel = this.type3;
                  var1.overrideSecondaryHandModel = this.type4;
                  var1.resetModelNextFrame();
                  var1.setVariable("IsPerformingAnAction", true);
                  return true;
               case EventToggleTorch:
                  if (var1.getPrimaryHandItem() != null && var1.getPrimaryHandItem().canEmitLight()) {
                     var1.getPrimaryHandItem().setActivatedRemote(this.param1 == 1.0F);
                  } else if (var1.getSecondaryHandItem() != null && var1.getSecondaryHandItem().canEmitLight()) {
                     var1.getSecondaryHandItem().setActivatedRemote(this.param1 == 1.0F);
                  }
               case EventFallClimb:
                  if (this.param1 == 1.0F) {
                     var1.setVariable("ClimbFenceOutcome", this.type1);
                     var1.setVariable("BumpDone", true);
                     var1.setFallOnFront(true);
                  }

                  return true;
               case EventWashClothing:
                  var1.setVariable("PerformingAction", this.type1);
                  var1.setVariable("LootPosition", "");
                  var1.overridePrimaryHandModel = null;
                  var1.overrideSecondaryHandModel = null;
                  var1.resetModelNextFrame();
                  var1.setVariable("IsPerformingAnAction", true);
                  return true;
               case collideWithWall:
                  var1.setCollideType(this.type1);
                  var1.actionContext.reportEvent("collideWithWall");
                  return true;
               case EventTakeWater:
                  var1.setVariable("PerformingAction", this.type1);
                  var1.setVariable("FoodType", this.type2);
                  var1.forceNullOverride = true;
                  var1.overridePrimaryHandModel = this.type3;
                  var1.overrideSecondaryHandModel = this.type4;
                  var1.resetModelNextFrame();
                  var1.setVariable("IsPerformingAnAction", true);
                  return true;
               case EventLootItem:
                  var1.setVariable("PerformingAction", "Loot");
                  var1.setVariable("LootPosition", this.type1);
                  var1.setVariable("IsPerformingAnAction", true);
                  var1.forceNullOverride = true;
                  return true;
               case EventAttachItem:
                  var1.setVariable("PerformingAction", this.type1);
                  var1.setVariable("AttachAnim", this.type2);
                  var1.setVariable("IsPerformingAnAction", true);
                  return true;
               case EventReloading:
                  var1.setVariable("PerformingAction", "Reload");
                  var1.setVariable("WeaponReloadType", this.type1);
                  var1.setVariable("isLoading", Boolean.parseBoolean(this.type2));
                  var1.setVariable("isRacking", Boolean.parseBoolean(this.type3));
                  var1.setVariable("isUnloading", Boolean.parseBoolean(this.type4));
                  var1.setVariable("IsPerformingAnAction", true);
                  return true;
               case EventBandage:
                  var1.setVariable("PerformingAction", "Bandage");
                  var1.setVariable("IsPerformingAnAction", true);
                  var1.setVariable("BandageType", this.type1);
                  break;
               case EventRead:
                  var1.setVariable("PerformingAction", "read");
                  var1.setVariable("IsPerformingAnAction", true);
                  var1.setVariable("ReadType", this.type1);
                  var1.forceNullOverride = true;
                  var1.overrideSecondaryHandModel = this.type2;
                  var1.resetModelNextFrame();
                  return true;
               case EventWearClothing:
                  var1.setVariable("PerformingAction", "WearClothing");
                  var1.setVariable("WearClothingLocation", this.type1);
                  var1.setVariable("IsPerformingAnAction", true);
                  return true;
               case EventEating:
                  var1.setVariable("PerformingAction", this.type1);
                  var1.setVariable("FoodType", this.type2);
                  var1.forceNullOverride = true;
                  var1.overridePrimaryHandModel = this.type3;
                  var1.overrideSecondaryHandModel = this.type4;
                  var1.resetModelNextFrame();
                  var1.setVariable("IsPerformingAnAction", true);
                  return true;
               case EventFishing:
                  var1.setVariable("FishingStage", this.type1);
                  if (!FishingState.instance().equals(var1.getCurrentState())) {
                     var1.setVariable("forceGetUp", true);
                     var1.actionContext.reportEvent("EventFishing");
                  }

                  return true;
               case EventFitness:
                  var1.setVariable("ExerciseType", this.type1);
                  var1.actionContext.reportEvent("EventFitness");
                  var2 = true;
                  break;
               case EventUpdateFitness:
                  var1.clearVariable("ExerciseHand");
                  var1.setVariable("ExerciseType", this.type2);
                  if (!StringUtils.isNullOrEmpty(this.type1)) {
                     var1.setVariable("ExerciseHand", this.type1);
                  }

                  var1.setVariable("FitnessStruggle", this.param1 == 1.0F);
                  var1.setPrimaryHandItem((InventoryItem)null);
                  var1.setSecondaryHandItem((InventoryItem)null);
                  var1.overridePrimaryHandModel = null;
                  var1.overrideSecondaryHandModel = null;
                  var1.overridePrimaryHandModel = this.type3;
                  var1.overrideSecondaryHandModel = this.type4;
                  var1.resetModelNextFrame();
                  return true;
               case EventEmote:
                  var1.setVariable("emote", this.type1);
                  var1.actionContext.reportEvent("EventEmote");
                  return true;
               case EventSitOnGround:
                  var1.actionContext.reportEvent("EventSitOnGround");
                  var2 = true;
                  break;
               case EventClimbRope:
                  var1.networkAI.climbSpeed = this.param1;
                  var1.networkAI.climbDownSpeed = this.param2;
                  var1.climbSheetRope();
                  return true;
               case EventClimbDownRope:
                  var1.networkAI.climbSpeed = this.param1;
                  var1.networkAI.climbDownSpeed = this.param2;
                  var1.climbDownSheetRope();
                  return true;
               case EventClimbFence:
                  IsoDirections var11 = this.checkCurrentIsEventGridSquareFence(var1);
                  if (var11 != IsoDirections.Max) {
                     var1.climbOverFence(var11);
                     var2 = true;
                     if (var1.isSprinting()) {
                        var1.setVariable("VaultOverSprint", true);
                     }

                     if (var1.isRunning()) {
                        var1.setVariable("VaultOverRun", true);
                     }

                     return true;
                  }
                  break;
               case EventClimbWall:
                  IsoDirections[] var10 = IsoDirections.values();
                  int var5 = var10.length;

                  for(int var6 = 0; var6 < var5; ++var6) {
                     IsoDirections var7 = var10[var6];
                     if (var1.climbOverWall(var7)) {
                        return true;
                     }
                  }

                  return var2;
               case EventClimbWindow:
                  IsoObject var9 = this.getObject(var1);
                  if (var9 instanceof IsoWindow) {
                     var1.climbThroughWindow((IsoWindow)var9);
                     return true;
                  }

                  if (var9 instanceof IsoThumpable) {
                     var1.climbThroughWindow((IsoThumpable)var9);
                     return true;
                  }

                  if (IsoWindowFrame.isWindowFrame(var9)) {
                     var1.climbThroughWindowFrame(var9);
                     return true;
                  }
                  break;
               case EventOpenWindow:
                  var4 = this.getWindow(var1);
                  if (var4 != null) {
                     var1.openWindow(var4);
                     var2 = true;
                  }
                  break;
               case EventCloseWindow:
                  var4 = this.getWindow(var1);
                  if (var4 != null) {
                     var1.closeWindow(var4);
                     var2 = true;
                  }
                  break;
               case EventSmashWindow:
                  var4 = this.getWindow(var1);
                  if (var4 != null) {
                     var1.smashWindow(var4);
                     var2 = true;
                  }
                  break;
               case wasBumped:
                  var1.setBumpDone(false);
                  var1.setVariable("BumpFallAnimFinished", false);
                  var1.setBumpType(this.type1);
                  var1.setBumpFallType(this.type2);
                  var1.setBumpFall(this.param1 > 0.0F);
                  var1.setBumpStaggered(this.param2 > 0.0F);
                  var1.reportEvent("wasBumped");
                  return true;
               default:
                  DebugLog.log(DebugType.Multiplayer, "Remote player: unprocessed event type");
               }
            }
         }

         return var2;
      }

      private boolean requireNonMoving(byte var1) {
         if (var1 != -1 && var1 != -2) {
            EventUpdatePacket.EventUpdate var2 = EventUpdatePacket.EventUpdate.values()[var1];
            return var2 == EventUpdatePacket.EventUpdate.EventClimbWindow || var2 == EventUpdatePacket.EventUpdate.EventClimbFence || var2 == EventUpdatePacket.EventUpdate.EventClimbDownRope || var2 == EventUpdatePacket.EventUpdate.EventClimbRope || var2 == EventUpdatePacket.EventUpdate.EventClimbWall;
         } else {
            return false;
         }
      }
   }
}
