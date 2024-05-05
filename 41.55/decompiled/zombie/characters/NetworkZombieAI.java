package zombie.characters;

import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverWallState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.LungeState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.ThumpState;
import zombie.ai.states.WalkTowardState;
import zombie.core.math.PZMath;
import zombie.core.utils.UpdateTimer;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.iso.IsoDirections;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.packets.DeadBodyPacket;
import zombie.network.packets.HitPacket;
import zombie.network.packets.ZombiePacket;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;

public class NetworkZombieAI {
   private UpdateTimer[] timer = null;
   private boolean[] needExtraUpdate = null;
   private PathFindBehavior2 pfb2 = null;
   public IsoZombie zombie = null;
   public boolean usePathFind = false;
   public float targetX = 0.0F;
   public float targetY = 0.0F;
   public int targetZ = 0;
   public int targetT = 0;
   public IsoMovingObject moveToTarget = null;
   public NetworkCharacter.PredictionMoveTypes predictionType;
   public int predictionTime;
   public boolean isClimbing;
   private int owner = -1;
   private byte flags;
   private byte direction;
   public DeadBodyPacket deadZombie;
   public HitPacket.HitVehicle hitVehicle;
   public float reanimateTimer;
   public boolean DebugInterfaceActive = false;

   public NetworkZombieAI(IsoZombie var1) {
      this.zombie = var1;
      this.predictionType = NetworkCharacter.PredictionMoveTypes.None;
      this.isClimbing = false;
      this.predictionTime = 0;
      this.flags = 0;
      this.deadZombie = null;
      this.hitVehicle = null;
      this.reanimateTimer = 0.0F;
      this.pfb2 = this.zombie.getPathFindBehavior2();
      if (GameClient.bClient) {
         this.newOwner(this.owner);
      }

      this.timer = new UpdateTimer[512];
      this.needExtraUpdate = new boolean[512];

      for(int var2 = 0; var2 < 512; ++var2) {
         this.timer[var2] = new UpdateTimer();
         this.needExtraUpdate[var2] = false;
      }

   }

   public void reset() {
      for(int var1 = 0; var1 < 512; ++var1) {
         this.needExtraUpdate[var1] = false;
      }

      this.usePathFind = true;
      this.targetX = this.zombie.getX();
      this.targetY = this.zombie.getY();
      this.targetZ = (byte)((int)this.zombie.getZ());
      this.targetT = (int)(GameTime.getServerTime() / 1000000L + 500L);
      this.moveToTarget = null;
      this.predictionType = NetworkCharacter.PredictionMoveTypes.None;
      this.isClimbing = false;
      this.predictionTime = 0;
      this.flags = 0;
      this.deadZombie = null;
      this.hitVehicle = null;
      this.reanimateTimer = 0.0F;
      this.newOwner(-1);
   }

   public void extraUpdate() {
      for(int var1 = 0; var1 < 512; ++var1) {
         this.needExtraUpdate[var1] = true;
      }

   }

   public boolean isUpdateNeeded(int var1) {
      if (this.zombie.networkAI.hitVehicle != null) {
         return false;
      } else if (this.needExtraUpdate[var1]) {
         this.needExtraUpdate[var1] = false;
         return true;
      } else {
         return this.timer[var1].check();
      }
   }

   private long getUpdateTime(int var1) {
      return this.timer[var1].getTime();
   }

   private void setUpdateTimer(float var1, int var2) {
      this.timer[var2].reset((long)PZMath.clamp((int)var1, 200, 3800));
   }

   private void setUsingExtrapolation(ZombiePacket var1, int var2, int var3) {
      if (this.zombie.isMoving()) {
         Vector2 var4 = this.zombie.dir.ToVector();
         this.zombie.networkCharacter.checkReset(var2);
         NetworkCharacter.Transform var5 = this.zombie.networkCharacter.predict(500, var2, this.zombie.x, this.zombie.y, var4.x, var4.y);
         var1.x = var5.position.x;
         var1.y = var5.position.y;
         var1.z = (byte)((int)this.zombie.z);
         var1.t = var5.time;
         var1.type = (byte)NetworkCharacter.PredictionMoveTypes.ExtrapolationMoving.ordinal();
         this.setUpdateTimer(300.0F, var3);
      } else {
         var1.x = this.zombie.x;
         var1.y = this.zombie.y;
         var1.z = (byte)((int)this.zombie.z);
         var1.t = var2 + 3800;
         var1.type = (byte)NetworkCharacter.PredictionMoveTypes.ExtrapolationStatic.ordinal();
         this.setUpdateTimer(2280.0F, var3);
      }

   }

   private void setUsingThump(ZombiePacket var1, long var2, int var4) {
      var1.x = ((IsoObject)this.zombie.getThumpTarget()).getX();
      var1.y = ((IsoObject)this.zombie.getThumpTarget()).getY();
      var1.z = (byte)((int)((IsoObject)this.zombie.getThumpTarget()).getZ());
      float var5 = (float)Math.sqrt((double)((var1.x - this.zombie.x) * (var1.x - this.zombie.x) + (var1.y - this.zombie.y) * (var1.y - this.zombie.y)));
      float var6 = var5 / 6.66E-4F;
      var1.t = (int)Math.ceil((double)((float)var2 + var6));
      var1.type = (byte)NetworkCharacter.PredictionMoveTypes.Thump.ordinal();
      this.setUpdateTimer(2280.0F, var4);
   }

   private void setUsingClimb(ZombiePacket var1, long var2, int var4) {
      var1.x = this.zombie.getTarget().getX();
      var1.y = this.zombie.getTarget().getY();
      var1.z = (byte)((int)this.zombie.getTarget().getZ());
      float var5 = (float)Math.sqrt((double)((var1.x - this.zombie.x) * (var1.x - this.zombie.x) + (var1.y - this.zombie.y) * (var1.y - this.zombie.y)));
      float var6 = var5 / 2.66E-4F;
      var1.t = (int)Math.ceil((double)((float)var2 + var6));
      var1.type = (byte)NetworkCharacter.PredictionMoveTypes.Climb.ordinal();
      this.setUpdateTimer(2280.0F, var4);
   }

   private void setUsingLungeState(ZombiePacket var1, long var2, int var4) {
      float var5 = IsoUtils.DistanceTo(this.zombie.target.x, this.zombie.target.y, this.zombie.x, this.zombie.y);
      float var6;
      if (var5 > 5.0F) {
         var1.x = (this.zombie.x + this.zombie.target.x) * 0.5F;
         var1.y = (this.zombie.y + this.zombie.target.y) * 0.5F;
         var1.z = (byte)((int)this.zombie.target.z);
         var6 = var5 * 0.5F / 5.0E-4F * this.zombie.speedMod;
         var1.t = (int)Math.ceil((double)((float)var2 + var6));
         var1.type = (byte)NetworkCharacter.PredictionMoveTypes.LungeHalf.ordinal();
         this.setUpdateTimer(var6 * 0.6F, var4);
      } else {
         var1.x = this.zombie.target.x;
         var1.y = this.zombie.target.y;
         var1.z = (byte)((int)this.zombie.target.z);
         var6 = var5 / 5.0E-4F * this.zombie.speedMod;
         var1.t = (int)Math.ceil((double)((float)var2 + var6));
         var1.type = (byte)NetworkCharacter.PredictionMoveTypes.Lunge.ordinal();
         this.setUpdateTimer(var6 * 0.6F, var4);
      }

   }

   private void setUsingWalkTowardState(ZombiePacket var1, long var2, int var4) {
      float var5 = this.pfb2.getPathLength();
      float var6;
      if (var5 > 5.0F) {
         var1.x = (this.zombie.x + this.pfb2.getTargetX()) * 0.5F;
         var1.y = (this.zombie.y + this.pfb2.getTargetY()) * 0.5F;
         var1.z = (byte)((int)this.pfb2.getTargetZ());
         var6 = var5 * 0.5F / 5.0E-4F * this.zombie.speedMod;
         var1.t = (int)Math.ceil((double)((float)var2 + var6));
         var1.type = (byte)NetworkCharacter.PredictionMoveTypes.WalkHalf.ordinal();
         this.setUpdateTimer(var6 * 0.6F, var4);
      } else {
         var1.x = this.pfb2.getTargetX();
         var1.y = this.pfb2.getTargetY();
         var1.z = (byte)((int)this.pfb2.getTargetZ());
         var6 = var5 / 5.0E-4F * this.zombie.speedMod;
         var1.t = (int)Math.ceil((double)((float)var2 + var6));
         var1.type = (byte)NetworkCharacter.PredictionMoveTypes.Walk.ordinal();
         this.setUpdateTimer(var6 * 0.6F, var4);
      }

   }

   private void setUsingPathFindState(ZombiePacket var1, long var2, int var4) {
      var1.x = this.pfb2.getTargetX();
      var1.y = this.pfb2.getTargetY();
      var1.z = (byte)((int)this.pfb2.getTargetZ());
      float var5 = this.pfb2.getPathLength() / 5.0E-4F * this.zombie.speedMod;
      var1.t = (int)Math.ceil((double)((float)var2 + var5));
      var1.type = (byte)NetworkCharacter.PredictionMoveTypes.PathFind.ordinal();
      this.setUpdateTimer(var5 * 0.6F, var4);
   }

   public void set(ZombiePacket var1, int var2) {
      int var3 = (int)(GameTime.getServerTime() / 1000000L);
      var1.owner = this.owner;
      var1.booleanVariables = NetworkZombieVariables.getBooleanVariables(this.zombie);
      var1.target = NetworkZombieVariables.getInt(this.zombie, 1);
      var1.eatBodyTarget = NetworkZombieVariables.getInt(this.zombie, 4);
      var1.smParamTargetAngle = NetworkZombieVariables.getInt(this.zombie, 18);
      var1.speedMod = this.zombie.speedMod;
      var1.walkType = this.zombie.getVariable("zombieWalkType").getValueString();
      var1.realx = this.zombie.x;
      var1.realy = this.zombie.y;
      var1.realz = (byte)((int)this.zombie.z);
      var1.realdir = (byte)this.zombie.dir.index();
      var1.realHealth = this.zombie.getHealth();
      if (this.zombie.getThumpTarget() != null && this.zombie.getCurrentState() == ThumpState.instance()) {
         this.setUsingThump(var1, (long)var3, var2);
      } else if (this.zombie.getTarget() != null && !this.isClimbing && (this.zombie.getCurrentState() == ClimbOverFenceState.instance() || this.zombie.getCurrentState() == ClimbOverWallState.instance() || this.zombie.getCurrentState() == ClimbThroughWindowState.instance())) {
         this.setUsingClimb(var1, (long)var3, var2);
         this.isClimbing = true;
      } else if (this.zombie.getCurrentState() == WalkTowardState.instance()) {
         this.setUsingWalkTowardState(var1, (long)var3, var2);
      } else if (this.zombie.getCurrentState() == LungeState.instance()) {
         this.setUsingLungeState(var1, (long)var3, var2);
      } else if (this.zombie.getCurrentState() == PathFindState.instance() && this.zombie.isMoving()) {
         this.setUsingPathFindState(var1, (long)var3, var2);
      } else {
         this.setUsingExtrapolation(var1, var3, var2);
      }

      Vector2 var4 = this.zombie.dir.ToVector();
      this.zombie.networkCharacter.updateExtrapolationPoint(var3, this.zombie.x, this.zombie.y, var4.x, var4.y);
      if (DebugOptions.instance.MultiplayerShowTeleport.getValue()) {
         DebugLog.log(DebugType.Multiplayer, getPredictionDebug(this.zombie, var1, var3, this.getUpdateTime(var2)));
         if (this.getUpdateTime(var2) > (long)var1.t && this.predictionType != NetworkCharacter.PredictionMoveTypes.PathFind && this.predictionType != NetworkCharacter.PredictionMoveTypes.Thump) {
            DebugLog.log(DebugType.Multiplayer, "Prediction update in going to be missed!");
         }
      }

   }

   public void parse(ZombiePacket var1, ByteBuffer var2) {
      int var3 = (int)(GameTime.getServerTime() / 1000000L);
      if (DebugOptions.instance.MultiplayerShowTeleport.getValue()) {
         this.zombie.debugData.put(var3, getPredictionDebug(this.zombie, var1, var3, (long)var3));
      }

      if (this.owner != var1.owner) {
         this.newOwner(var1);
      }

      if (var1.t > var3 && this.usePathFind) {
         this.pfb2.pathToLocationF(var1.x, var1.y, (float)var1.z);
         this.pfb2.walkingOnTheSpot.reset(this.zombie.x, this.zombie.y);
         this.pfb2.setTargetT(var1.t);
      }

      if (DebugOptions.instance.MultiplayerShowTeleport.getValue() && IsoPlayer.getInstance().getDistanceSq(this.zombie) < 1600.0F) {
         if (var3 > var1.t && this.predictionType != NetworkCharacter.PredictionMoveTypes.PathFind && this.predictionType != NetworkCharacter.PredictionMoveTypes.Thump) {
            DebugLog.log(DebugType.Multiplayer, String.format("Late prediction Z_%d [type=%s, distance=%f, current=%d, prediction=%d, diff=%d]", var1.id, NetworkCharacter.PredictionMoveTypes.values()[var1.type].toString(), IsoUtils.DistanceTo(this.zombie.x, this.zombie.y, var1.x, var1.y), var3, var1.t, var3 - var1.t));
         }

         if (var3 > this.predictionTime && this.predictionTime != 0 && this.predictionType != NetworkCharacter.PredictionMoveTypes.PathFind && this.predictionType != NetworkCharacter.PredictionMoveTypes.Thump) {
            DebugLog.log(DebugType.Multiplayer, String.format("Missed prediction Z_%d [type=%s, distance=%f, current=%d, prediction=%d, diff=%d]", var1.id, this.predictionType, IsoUtils.DistanceTo(this.zombie.x, this.zombie.y, var1.x, var1.y), var3, this.predictionTime, var3 - this.predictionTime));
         }
      }

      this.predictionTime = var1.t;
      if (this.zombie.strike == null) {
         this.targetX = var1.x;
         this.targetY = var1.y;
         this.targetZ = var1.z;
         this.targetT = var1.t;
         this.moveToTarget = null;
         this.predictionType = NetworkCharacter.PredictionMoveTypes.values()[var1.type];
      }

      NetworkZombieVariables.setInt(this.zombie, (short)1, var1.target);
      if (!this.isLocalControl()) {
         NetworkZombieVariables.setInt(this.zombie, (short)4, var1.eatBodyTarget);
         NetworkZombieVariables.setInt(this.zombie, (short)18, var1.smParamTargetAngle);
         NetworkZombieVariables.setBooleanVariables(this.zombie, var1.booleanVariables);
         this.zombie.speedMod = var1.speedMod;
         this.zombie.setWalkType(var1.walkType);
      }

      this.zombie.realx = var1.realx;
      this.zombie.realy = var1.realy;
      this.zombie.realz = var1.realz;
      this.zombie.realdir = IsoDirections.fromIndex(var1.realdir);
      this.zombie.lastUpdateX = this.zombie.x;
      this.zombie.lastUpdateY = this.zombie.y;
      this.zombie.lastUpdateT = (float)(GameTime.getServerTime() / 1000000L);
      if ((IsoUtils.DistanceToSquared(this.zombie.x, this.zombie.y, this.zombie.realx, this.zombie.realy) > 9.0F || this.zombie.z != (float)this.zombie.realz) && (!this.isLocalControl() || IsoUtils.DistanceToSquared(this.zombie.x, this.zombie.y, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y) > 2.0F)) {
         NetworkTeleport.teleport(this.zombie, NetworkTeleport.Type.teleportation, this.zombie.realx, this.zombie.realy, this.zombie.realz, 1.0F);
      }

   }

   public void update() {
      if (this.hitVehicle != null && (GameServer.bServer || this.hitVehicle.isTimeout())) {
         this.zombie.doHit(this.hitVehicle.vehicle);
      }

      if (GameClient.bClient) {
         if (this.zombie.target != null) {
            this.zombie.setTargetSeenTime(this.zombie.getTargetSeenTime() + GameTime.getInstance().getRealworldSecondsSinceLastUpdate());
            if (!PolygonalMap2.instance.lineClearCollide(this.zombie.x, this.zombie.y, this.zombie.target.x, this.zombie.target.y, (int)this.zombie.z, (IsoMovingObject)null, false, true) && IsoUtils.DistanceToSquared(this.zombie.target.x, this.zombie.target.y, this.zombie.x, this.zombie.y) < 25.0F) {
               this.moveToTarget = this.zombie.target;
            }
         }

         if (this.zombie.strike != null) {
            long var1 = this.zombie.strike.lifeTime - System.currentTimeMillis();
            if (var1 >= 0L && var1 <= 3000L) {
               this.moveToTarget = this.zombie.strike.player;
            } else {
               this.zombie.strike = null;
            }
         }

         if (IsoPlayer.getInstance().getDistanceSq(this.zombie) > 1600.0F) {
            this.predictionTime = 0;
         }
      } else if (GameServer.bServer) {
         int var4 = -1;
         if (this.zombie.target != null && this.zombie.target instanceof IsoPlayer && !((IsoPlayer)this.zombie.target).isDead() && !this.zombie.isDead() && IsoUtils.DistanceToSquared(this.zombie.target.x, this.zombie.target.y, this.zombie.x, this.zombie.y) < 25.0F && !((IsoPlayer)this.zombie.target).isSeatedInVehicle()) {
            var4 = ((IsoPlayer)this.zombie.target).OnlineID;
         }

         if (this.owner != var4) {
            this.owner = var4;
            this.extraUpdate();
         }

         byte var2 = (byte)((this.zombie.getVariableBoolean("bMoving") ? 1 : 0) | (this.zombie.getVariableBoolean("bPathfind") ? 2 : 0));
         if (this.flags != var2) {
            this.flags = var2;
            this.extraUpdate();
         }

         byte var3 = (byte)IsoDirections.fromAngleActual(this.zombie.getForwardDirection()).index();
         if (this.direction != var3) {
            this.direction = var3;
            this.extraUpdate();
         }
      }

   }

   private void newOwner(int var1) {
      this.owner = var1;
      if (var1 == -1) {
         this.zombie.movingOnNetwork = true;
      } else {
         this.zombie.movingOnNetwork = false;
      }

   }

   private void newOwner(ZombiePacket var1) {
      this.owner = var1.owner;
      this.zombie.movingOnNetwork = var1.owner == -1;
   }

   public int getOwner() {
      return this.owner;
   }

   public boolean isLocalControl() {
      return IsoPlayer.getInstance() != null && this.zombie.networkAI.getOwner() == IsoPlayer.getInstance().getOnlineID();
   }

   public static String getPredictionDebug(IsoGameCharacter var0, ZombiePacket var1, int var2, long var3) {
      return String.format("Prediction Z_%d [owner=%d, type=%s, distance=%f], time [current=%d, prediction=%d, diff=%d, next=%d], states [current=%s, previous=%s]", var1.id, var1.owner, NetworkCharacter.PredictionMoveTypes.values()[var1.type].toString(), IsoUtils.DistanceTo(var0.x, var0.y, var1.x, var1.y), var2, var1.t, var1.t - var2, var3 - (long)var2, var0.getCurrentStateName(), var0.getPreviousStateName());
   }
}
