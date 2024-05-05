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

	public NetworkZombieAI(IsoZombie zombie) {
		this.zombie = zombie;
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
		for (int int1 = 0; int1 < 512; ++int1) {
			this.timer[int1] = new UpdateTimer();
			this.needExtraUpdate[int1] = false;
		}
	}

	public void reset() {
		for (int int1 = 0; int1 < 512; ++int1) {
			this.needExtraUpdate[int1] = false;
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
		for (int int1 = 0; int1 < 512; ++int1) {
			this.needExtraUpdate[int1] = true;
		}
	}

	public boolean isUpdateNeeded(int int1) {
		if (this.zombie.networkAI.hitVehicle != null) {
			return false;
		} else if (this.needExtraUpdate[int1]) {
			this.needExtraUpdate[int1] = false;
			return true;
		} else {
			return this.timer[int1].check();
		}
	}

	private long getUpdateTime(int int1) {
		return this.timer[int1].getTime();
	}

	private void setUpdateTimer(float float1, int int1) {
		this.timer[int1].reset((long)PZMath.clamp((int)float1, 200, 3800));
	}

	private void setUsingExtrapolation(ZombiePacket zombiePacket, int int1, int int2) {
		if (this.zombie.isMoving()) {
			Vector2 vector2 = this.zombie.dir.ToVector();
			this.zombie.networkCharacter.checkReset(int1);
			NetworkCharacter.Transform transform = this.zombie.networkCharacter.predict(500, int1, this.zombie.x, this.zombie.y, vector2.x, vector2.y);
			zombiePacket.x = transform.position.x;
			zombiePacket.y = transform.position.y;
			zombiePacket.z = (byte)((int)this.zombie.z);
			zombiePacket.t = transform.time;
			zombiePacket.type = (byte)NetworkCharacter.PredictionMoveTypes.ExtrapolationMoving.ordinal();
			this.setUpdateTimer(300.0F, int2);
		} else {
			zombiePacket.x = this.zombie.x;
			zombiePacket.y = this.zombie.y;
			zombiePacket.z = (byte)((int)this.zombie.z);
			zombiePacket.t = int1 + 3800;
			zombiePacket.type = (byte)NetworkCharacter.PredictionMoveTypes.ExtrapolationStatic.ordinal();
			this.setUpdateTimer(2280.0F, int2);
		}
	}

	private void setUsingThump(ZombiePacket zombiePacket, long long1, int int1) {
		zombiePacket.x = ((IsoObject)this.zombie.getThumpTarget()).getX();
		zombiePacket.y = ((IsoObject)this.zombie.getThumpTarget()).getY();
		zombiePacket.z = (byte)((int)((IsoObject)this.zombie.getThumpTarget()).getZ());
		float float1 = (float)Math.sqrt((double)((zombiePacket.x - this.zombie.x) * (zombiePacket.x - this.zombie.x) + (zombiePacket.y - this.zombie.y) * (zombiePacket.y - this.zombie.y)));
		float float2 = float1 / 6.66E-4F;
		zombiePacket.t = (int)Math.ceil((double)((float)long1 + float2));
		zombiePacket.type = (byte)NetworkCharacter.PredictionMoveTypes.Thump.ordinal();
		this.setUpdateTimer(2280.0F, int1);
	}

	private void setUsingClimb(ZombiePacket zombiePacket, long long1, int int1) {
		zombiePacket.x = this.zombie.getTarget().getX();
		zombiePacket.y = this.zombie.getTarget().getY();
		zombiePacket.z = (byte)((int)this.zombie.getTarget().getZ());
		float float1 = (float)Math.sqrt((double)((zombiePacket.x - this.zombie.x) * (zombiePacket.x - this.zombie.x) + (zombiePacket.y - this.zombie.y) * (zombiePacket.y - this.zombie.y)));
		float float2 = float1 / 2.66E-4F;
		zombiePacket.t = (int)Math.ceil((double)((float)long1 + float2));
		zombiePacket.type = (byte)NetworkCharacter.PredictionMoveTypes.Climb.ordinal();
		this.setUpdateTimer(2280.0F, int1);
	}

	private void setUsingLungeState(ZombiePacket zombiePacket, long long1, int int1) {
		float float1 = IsoUtils.DistanceTo(this.zombie.target.x, this.zombie.target.y, this.zombie.x, this.zombie.y);
		float float2;
		if (float1 > 5.0F) {
			zombiePacket.x = (this.zombie.x + this.zombie.target.x) * 0.5F;
			zombiePacket.y = (this.zombie.y + this.zombie.target.y) * 0.5F;
			zombiePacket.z = (byte)((int)this.zombie.target.z);
			float2 = float1 * 0.5F / 5.0E-4F * this.zombie.speedMod;
			zombiePacket.t = (int)Math.ceil((double)((float)long1 + float2));
			zombiePacket.type = (byte)NetworkCharacter.PredictionMoveTypes.LungeHalf.ordinal();
			this.setUpdateTimer(float2 * 0.6F, int1);
		} else {
			zombiePacket.x = this.zombie.target.x;
			zombiePacket.y = this.zombie.target.y;
			zombiePacket.z = (byte)((int)this.zombie.target.z);
			float2 = float1 / 5.0E-4F * this.zombie.speedMod;
			zombiePacket.t = (int)Math.ceil((double)((float)long1 + float2));
			zombiePacket.type = (byte)NetworkCharacter.PredictionMoveTypes.Lunge.ordinal();
			this.setUpdateTimer(float2 * 0.6F, int1);
		}
	}

	private void setUsingWalkTowardState(ZombiePacket zombiePacket, long long1, int int1) {
		float float1 = this.pfb2.getPathLength();
		float float2;
		if (float1 > 5.0F) {
			zombiePacket.x = (this.zombie.x + this.pfb2.getTargetX()) * 0.5F;
			zombiePacket.y = (this.zombie.y + this.pfb2.getTargetY()) * 0.5F;
			zombiePacket.z = (byte)((int)this.pfb2.getTargetZ());
			float2 = float1 * 0.5F / 5.0E-4F * this.zombie.speedMod;
			zombiePacket.t = (int)Math.ceil((double)((float)long1 + float2));
			zombiePacket.type = (byte)NetworkCharacter.PredictionMoveTypes.WalkHalf.ordinal();
			this.setUpdateTimer(float2 * 0.6F, int1);
		} else {
			zombiePacket.x = this.pfb2.getTargetX();
			zombiePacket.y = this.pfb2.getTargetY();
			zombiePacket.z = (byte)((int)this.pfb2.getTargetZ());
			float2 = float1 / 5.0E-4F * this.zombie.speedMod;
			zombiePacket.t = (int)Math.ceil((double)((float)long1 + float2));
			zombiePacket.type = (byte)NetworkCharacter.PredictionMoveTypes.Walk.ordinal();
			this.setUpdateTimer(float2 * 0.6F, int1);
		}
	}

	private void setUsingPathFindState(ZombiePacket zombiePacket, long long1, int int1) {
		zombiePacket.x = this.pfb2.getTargetX();
		zombiePacket.y = this.pfb2.getTargetY();
		zombiePacket.z = (byte)((int)this.pfb2.getTargetZ());
		float float1 = this.pfb2.getPathLength() / 5.0E-4F * this.zombie.speedMod;
		zombiePacket.t = (int)Math.ceil((double)((float)long1 + float1));
		zombiePacket.type = (byte)NetworkCharacter.PredictionMoveTypes.PathFind.ordinal();
		this.setUpdateTimer(float1 * 0.6F, int1);
	}

	public void set(ZombiePacket zombiePacket, int int1) {
		int int2 = (int)(GameTime.getServerTime() / 1000000L);
		zombiePacket.owner = this.owner;
		zombiePacket.booleanVariables = NetworkZombieVariables.getBooleanVariables(this.zombie);
		zombiePacket.target = NetworkZombieVariables.getInt(this.zombie, 1);
		zombiePacket.eatBodyTarget = NetworkZombieVariables.getInt(this.zombie, 4);
		zombiePacket.smParamTargetAngle = NetworkZombieVariables.getInt(this.zombie, 18);
		zombiePacket.speedMod = this.zombie.speedMod;
		zombiePacket.walkType = this.zombie.getVariable("zombieWalkType").getValueString();
		zombiePacket.realx = this.zombie.x;
		zombiePacket.realy = this.zombie.y;
		zombiePacket.realz = (byte)((int)this.zombie.z);
		zombiePacket.realdir = (byte)this.zombie.dir.index();
		zombiePacket.realHealth = this.zombie.getHealth();
		if (this.zombie.getThumpTarget() != null && this.zombie.getCurrentState() == ThumpState.instance()) {
			this.setUsingThump(zombiePacket, (long)int2, int1);
		} else if (this.zombie.getTarget() != null && !this.isClimbing && (this.zombie.getCurrentState() == ClimbOverFenceState.instance() || this.zombie.getCurrentState() == ClimbOverWallState.instance() || this.zombie.getCurrentState() == ClimbThroughWindowState.instance())) {
			this.setUsingClimb(zombiePacket, (long)int2, int1);
			this.isClimbing = true;
		} else if (this.zombie.getCurrentState() == WalkTowardState.instance()) {
			this.setUsingWalkTowardState(zombiePacket, (long)int2, int1);
		} else if (this.zombie.getCurrentState() == LungeState.instance()) {
			this.setUsingLungeState(zombiePacket, (long)int2, int1);
		} else if (this.zombie.getCurrentState() == PathFindState.instance() && this.zombie.isMoving()) {
			this.setUsingPathFindState(zombiePacket, (long)int2, int1);
		} else {
			this.setUsingExtrapolation(zombiePacket, int2, int1);
		}

		Vector2 vector2 = this.zombie.dir.ToVector();
		this.zombie.networkCharacter.updateExtrapolationPoint(int2, this.zombie.x, this.zombie.y, vector2.x, vector2.y);
		if (DebugOptions.instance.MultiplayerShowTeleport.getValue()) {
			DebugLog.log(DebugType.Multiplayer, getPredictionDebug(this.zombie, zombiePacket, int2, this.getUpdateTime(int1)));
			if (this.getUpdateTime(int1) > (long)zombiePacket.t && this.predictionType != NetworkCharacter.PredictionMoveTypes.PathFind && this.predictionType != NetworkCharacter.PredictionMoveTypes.Thump) {
				DebugLog.log(DebugType.Multiplayer, "Prediction update in going to be missed!");
			}
		}
	}

	public void parse(ZombiePacket zombiePacket, ByteBuffer byteBuffer) {
		int int1 = (int)(GameTime.getServerTime() / 1000000L);
		if (DebugOptions.instance.MultiplayerShowTeleport.getValue()) {
			this.zombie.debugData.put(int1, getPredictionDebug(this.zombie, zombiePacket, int1, (long)int1));
		}

		if (this.owner != zombiePacket.owner) {
			this.newOwner(zombiePacket);
		}

		if (zombiePacket.t > int1 && this.usePathFind) {
			this.pfb2.pathToLocationF(zombiePacket.x, zombiePacket.y, (float)zombiePacket.z);
			this.pfb2.walkingOnTheSpot.reset(this.zombie.x, this.zombie.y);
			this.pfb2.setTargetT(zombiePacket.t);
		}

		if (DebugOptions.instance.MultiplayerShowTeleport.getValue() && IsoPlayer.getInstance().getDistanceSq(this.zombie) < 1600.0F) {
			if (int1 > zombiePacket.t && this.predictionType != NetworkCharacter.PredictionMoveTypes.PathFind && this.predictionType != NetworkCharacter.PredictionMoveTypes.Thump) {
				DebugLog.log(DebugType.Multiplayer, String.format("Late prediction Z_%d [type=%s, distance=%f, current=%d, prediction=%d, diff=%d]", zombiePacket.id, NetworkCharacter.PredictionMoveTypes.values()[zombiePacket.type].toString(), IsoUtils.DistanceTo(this.zombie.x, this.zombie.y, zombiePacket.x, zombiePacket.y), int1, zombiePacket.t, int1 - zombiePacket.t));
			}

			if (int1 > this.predictionTime && this.predictionTime != 0 && this.predictionType != NetworkCharacter.PredictionMoveTypes.PathFind && this.predictionType != NetworkCharacter.PredictionMoveTypes.Thump) {
				DebugLog.log(DebugType.Multiplayer, String.format("Missed prediction Z_%d [type=%s, distance=%f, current=%d, prediction=%d, diff=%d]", zombiePacket.id, this.predictionType, IsoUtils.DistanceTo(this.zombie.x, this.zombie.y, zombiePacket.x, zombiePacket.y), int1, this.predictionTime, int1 - this.predictionTime));
			}
		}

		this.predictionTime = zombiePacket.t;
		if (this.zombie.strike == null) {
			this.targetX = zombiePacket.x;
			this.targetY = zombiePacket.y;
			this.targetZ = zombiePacket.z;
			this.targetT = zombiePacket.t;
			this.moveToTarget = null;
			this.predictionType = NetworkCharacter.PredictionMoveTypes.values()[zombiePacket.type];
		}

		NetworkZombieVariables.setInt(this.zombie, (short)1, zombiePacket.target);
		if (!this.isLocalControl()) {
			NetworkZombieVariables.setInt(this.zombie, (short)4, zombiePacket.eatBodyTarget);
			NetworkZombieVariables.setInt(this.zombie, (short)18, zombiePacket.smParamTargetAngle);
			NetworkZombieVariables.setBooleanVariables(this.zombie, zombiePacket.booleanVariables);
			this.zombie.speedMod = zombiePacket.speedMod;
			this.zombie.setWalkType(zombiePacket.walkType);
		}

		this.zombie.realx = zombiePacket.realx;
		this.zombie.realy = zombiePacket.realy;
		this.zombie.realz = zombiePacket.realz;
		this.zombie.realdir = IsoDirections.fromIndex(zombiePacket.realdir);
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
				long long1 = this.zombie.strike.lifeTime - System.currentTimeMillis();
				if (long1 >= 0L && long1 <= 3000L) {
					this.moveToTarget = this.zombie.strike.player;
				} else {
					this.zombie.strike = null;
				}
			}

			if (IsoPlayer.getInstance().getDistanceSq(this.zombie) > 1600.0F) {
				this.predictionTime = 0;
			}
		} else if (GameServer.bServer) {
			int int1 = -1;
			if (this.zombie.target != null && this.zombie.target instanceof IsoPlayer && !((IsoPlayer)this.zombie.target).isDead() && !this.zombie.isDead() && IsoUtils.DistanceToSquared(this.zombie.target.x, this.zombie.target.y, this.zombie.x, this.zombie.y) < 25.0F && !((IsoPlayer)this.zombie.target).isSeatedInVehicle()) {
				int1 = ((IsoPlayer)this.zombie.target).OnlineID;
			}

			if (this.owner != int1) {
				this.owner = int1;
				this.extraUpdate();
			}

			byte byte1 = (byte)((this.zombie.getVariableBoolean("bMoving") ? 1 : 0) | (this.zombie.getVariableBoolean("bPathfind") ? 2 : 0));
			if (this.flags != byte1) {
				this.flags = byte1;
				this.extraUpdate();
			}

			byte byte2 = (byte)IsoDirections.fromAngleActual(this.zombie.getForwardDirection()).index();
			if (this.direction != byte2) {
				this.direction = byte2;
				this.extraUpdate();
			}
		}
	}

	private void newOwner(int int1) {
		this.owner = int1;
		if (int1 == -1) {
			this.zombie.movingOnNetwork = true;
		} else {
			this.zombie.movingOnNetwork = false;
		}
	}

	private void newOwner(ZombiePacket zombiePacket) {
		this.owner = zombiePacket.owner;
		this.zombie.movingOnNetwork = zombiePacket.owner == -1;
	}

	public int getOwner() {
		return this.owner;
	}

	public boolean isLocalControl() {
		return IsoPlayer.getInstance() != null && this.zombie.networkAI.getOwner() == IsoPlayer.getInstance().getOnlineID();
	}

	public static String getPredictionDebug(IsoGameCharacter gameCharacter, ZombiePacket zombiePacket, int int1, long long1) {
		return String.format("Prediction Z_%d [owner=%d, type=%s, distance=%f], time [current=%d, prediction=%d, diff=%d, next=%d], states [current=%s, previous=%s]", zombiePacket.id, zombiePacket.owner, NetworkCharacter.PredictionMoveTypes.values()[zombiePacket.type].toString(), IsoUtils.DistanceTo(gameCharacter.x, gameCharacter.y, zombiePacket.x, zombiePacket.y), int1, zombiePacket.t, zombiePacket.t - int1, long1 - (long)int1, gameCharacter.getCurrentStateName(), gameCharacter.getPreviousStateName());
	}
}
