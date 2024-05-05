package zombie.characters;

import zombie.GameTime;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverWallState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.LungeState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.ThumpState;
import zombie.ai.states.WalkTowardState;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.core.utils.UpdateTimer;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.iso.IsoDirections;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetworkVariables;
import zombie.network.packets.ZombiePacket;
import zombie.popman.NetworkZombieSimulator;
import zombie.vehicles.PathFindBehavior2;


public class NetworkZombieAI extends NetworkCharacterAI {
	private final UpdateTimer timer;
	private final PathFindBehavior2 pfb2;
	public final IsoZombie zombie;
	public boolean usePathFind = false;
	public float targetX = 0.0F;
	public float targetY = 0.0F;
	public int targetZ = 0;
	public boolean isClimbing;
	private byte flags;
	private byte direction;
	public final NetworkZombieMind mindSync;
	public short reanimatedBodyID;
	public boolean DebugInterfaceActive = false;

	public NetworkZombieAI(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.zombie = (IsoZombie)gameCharacter;
		this.isClimbing = false;
		this.flags = 0;
		this.pfb2 = this.zombie.getPathFindBehavior2();
		this.timer = new UpdateTimer();
		this.mindSync = new NetworkZombieMind(this.zombie);
		gameCharacter.ulBeatenVehicle.Reset(400L);
		this.reanimatedBodyID = -1;
	}

	public void reset() {
		super.reset();
		this.usePathFind = true;
		this.targetX = this.zombie.getX();
		this.targetY = this.zombie.getY();
		this.targetZ = (byte)((int)this.zombie.getZ());
		this.isClimbing = false;
		this.flags = 0;
		this.zombie.getHitDir().set(0.0F, 0.0F);
		this.reanimatedBodyID = -1;
	}

	public void extraUpdate() {
		NetworkZombieSimulator.getInstance().addExtraUpdate(this.zombie);
	}

	private long getUpdateTime() {
		return this.timer.getTime();
	}

	public void setUpdateTimer(float float1) {
		this.timer.reset((long)PZMath.clamp((int)float1, 200, 3800));
	}

	private void setUsingExtrapolation(ZombiePacket zombiePacket, int int1) {
		if (this.zombie.isMoving()) {
			Vector2 vector2 = this.zombie.dir.ToVector();
			this.zombie.networkCharacter.checkReset(int1);
			NetworkCharacter.Transform transform = this.zombie.networkCharacter.predict(500, int1, this.zombie.x, this.zombie.y, vector2.x, vector2.y);
			zombiePacket.x = transform.position.x;
			zombiePacket.y = transform.position.y;
			zombiePacket.z = (byte)((int)this.zombie.z);
			zombiePacket.moveType = NetworkVariables.PredictionTypes.Moving;
			this.setUpdateTimer(300.0F);
		} else {
			zombiePacket.x = this.zombie.x;
			zombiePacket.y = this.zombie.y;
			zombiePacket.z = (byte)((int)this.zombie.z);
			zombiePacket.moveType = NetworkVariables.PredictionTypes.Static;
			this.setUpdateTimer(2280.0F);
		}
	}

	private void setUsingThump(ZombiePacket zombiePacket) {
		zombiePacket.x = ((IsoObject)this.zombie.getThumpTarget()).getX();
		zombiePacket.y = ((IsoObject)this.zombie.getThumpTarget()).getY();
		zombiePacket.z = (byte)((int)((IsoObject)this.zombie.getThumpTarget()).getZ());
		zombiePacket.moveType = NetworkVariables.PredictionTypes.Thump;
		this.setUpdateTimer(2280.0F);
	}

	private void setUsingClimb(ZombiePacket zombiePacket) {
		zombiePacket.x = this.zombie.getTarget().getX();
		zombiePacket.y = this.zombie.getTarget().getY();
		zombiePacket.z = (byte)((int)this.zombie.getTarget().getZ());
		zombiePacket.moveType = NetworkVariables.PredictionTypes.Climb;
		this.setUpdateTimer(2280.0F);
	}

	private void setUsingLungeState(ZombiePacket zombiePacket, long long1) {
		if (this.zombie.target == null) {
			this.setUsingExtrapolation(zombiePacket, (int)long1);
		} else {
			float float1 = IsoUtils.DistanceTo(this.zombie.target.x, this.zombie.target.y, this.zombie.x, this.zombie.y);
			float float2;
			if (float1 > 5.0F) {
				zombiePacket.x = (this.zombie.x + this.zombie.target.x) * 0.5F;
				zombiePacket.y = (this.zombie.y + this.zombie.target.y) * 0.5F;
				zombiePacket.z = (byte)((int)this.zombie.target.z);
				float2 = float1 * 0.5F / 5.0E-4F * this.zombie.speedMod;
				zombiePacket.moveType = NetworkVariables.PredictionTypes.LungeHalf;
				this.setUpdateTimer(float2 * 0.6F);
			} else {
				zombiePacket.x = this.zombie.target.x;
				zombiePacket.y = this.zombie.target.y;
				zombiePacket.z = (byte)((int)this.zombie.target.z);
				float2 = float1 / 5.0E-4F * this.zombie.speedMod;
				zombiePacket.moveType = NetworkVariables.PredictionTypes.Lunge;
				this.setUpdateTimer(float2 * 0.6F);
			}
		}
	}

	private void setUsingWalkTowardState(ZombiePacket zombiePacket) {
		float float1;
		if (this.zombie.getPath2() == null) {
			float float2 = this.pfb2.getPathLength();
			if (float2 > 5.0F) {
				zombiePacket.x = (this.zombie.x + this.pfb2.getTargetX()) * 0.5F;
				zombiePacket.y = (this.zombie.y + this.pfb2.getTargetY()) * 0.5F;
				zombiePacket.z = (byte)((int)this.pfb2.getTargetZ());
				float1 = float2 * 0.5F / 5.0E-4F * this.zombie.speedMod;
				zombiePacket.moveType = NetworkVariables.PredictionTypes.WalkHalf;
			} else {
				zombiePacket.x = this.pfb2.getTargetX();
				zombiePacket.y = this.pfb2.getTargetY();
				zombiePacket.z = (byte)((int)this.pfb2.getTargetZ());
				float1 = float2 / 5.0E-4F * this.zombie.speedMod;
				zombiePacket.moveType = NetworkVariables.PredictionTypes.Walk;
			}
		} else {
			zombiePacket.x = this.pfb2.pathNextX;
			zombiePacket.y = this.pfb2.pathNextY;
			zombiePacket.z = (byte)((int)this.zombie.z);
			float1 = IsoUtils.DistanceTo(this.zombie.x, this.zombie.y, this.pfb2.pathNextX, this.pfb2.pathNextY) / 5.0E-4F * this.zombie.speedMod;
			zombiePacket.moveType = NetworkVariables.PredictionTypes.Walk;
		}

		this.setUpdateTimer(float1 * 0.6F);
	}

	private void setUsingPathFindState(ZombiePacket zombiePacket) {
		zombiePacket.x = this.pfb2.pathNextX;
		zombiePacket.y = this.pfb2.pathNextY;
		zombiePacket.z = (byte)((int)this.zombie.z);
		float float1 = IsoUtils.DistanceTo(this.zombie.x, this.zombie.y, this.pfb2.pathNextX, this.pfb2.pathNextY) / 5.0E-4F * this.zombie.speedMod;
		zombiePacket.moveType = NetworkVariables.PredictionTypes.PathFind;
		this.setUpdateTimer(float1 * 0.6F);
	}

	public void set(ZombiePacket zombiePacket) {
		int int1 = (int)(GameTime.getServerTime() / 1000000L);
		zombiePacket.booleanVariables = NetworkZombieVariables.getBooleanVariables(this.zombie);
		zombiePacket.realHealth = (short)NetworkZombieVariables.getInt(this.zombie, (short)0);
		zombiePacket.target = (short)NetworkZombieVariables.getInt(this.zombie, (short)1);
		zombiePacket.speedMod = (short)NetworkZombieVariables.getInt(this.zombie, (short)2);
		zombiePacket.timeSinceSeenFlesh = NetworkZombieVariables.getInt(this.zombie, (short)3);
		zombiePacket.smParamTargetAngle = NetworkZombieVariables.getInt(this.zombie, (short)4);
		zombiePacket.walkType = NetworkVariables.WalkType.fromString(this.zombie.getVariableString("zombieWalkType"));
		zombiePacket.realX = this.zombie.x;
		zombiePacket.realY = this.zombie.y;
		zombiePacket.realZ = (byte)((int)this.zombie.z);
		this.zombie.realState = NetworkVariables.ZombieState.fromString(this.zombie.getAdvancedAnimator().getCurrentStateName());
		zombiePacket.realState = this.zombie.realState;
		zombiePacket.reanimatedBodyID = this.reanimatedBodyID;
		if (this.zombie.getThumpTarget() != null && this.zombie.getCurrentState() == ThumpState.instance()) {
			this.setUsingThump(zombiePacket);
		} else if (this.zombie.getTarget() != null && !this.isClimbing && (this.zombie.getCurrentState() == ClimbOverFenceState.instance() || this.zombie.getCurrentState() == ClimbOverWallState.instance() || this.zombie.getCurrentState() == ClimbThroughWindowState.instance())) {
			this.setUsingClimb(zombiePacket);
			this.isClimbing = true;
		} else if (this.zombie.getCurrentState() == WalkTowardState.instance()) {
			this.setUsingWalkTowardState(zombiePacket);
		} else if (this.zombie.getCurrentState() == LungeState.instance()) {
			this.setUsingLungeState(zombiePacket, (long)int1);
		} else if (this.zombie.getCurrentState() == PathFindState.instance() && this.zombie.isMoving()) {
			this.setUsingPathFindState(zombiePacket);
		} else {
			this.setUsingExtrapolation(zombiePacket, int1);
		}

		Vector2 vector2 = this.zombie.dir.ToVector();
		this.zombie.networkCharacter.updateExtrapolationPoint(int1, this.zombie.x, this.zombie.y, vector2.x, vector2.y);
		if (DebugOptions.instance.MultiplayerLogPrediction.getValue() && Core.bDebug) {
			DebugLog.log(DebugType.Multiplayer, getPredictionDebug(this.zombie, zombiePacket, int1, this.getUpdateTime()));
		}
	}

	public void parse(ZombiePacket zombiePacket) {
		int int1 = (int)(GameTime.getServerTime() / 1000000L);
		if (DebugOptions.instance.MultiplayerLogPrediction.getValue()) {
			this.zombie.getNetworkCharacterAI().addTeleportData(int1, getPredictionDebug(this.zombie, zombiePacket, int1, (long)int1));
		}

		if (this.usePathFind) {
			this.pfb2.pathToLocationF(zombiePacket.x, zombiePacket.y, (float)zombiePacket.z);
			this.pfb2.walkingOnTheSpot.reset(this.zombie.x, this.zombie.y);
		}

		this.targetX = zombiePacket.x;
		this.targetY = zombiePacket.y;
		this.targetZ = zombiePacket.z;
		this.predictionType = zombiePacket.moveType;
		NetworkZombieVariables.setInt(this.zombie, (short)1, zombiePacket.target);
		NetworkZombieVariables.setInt(this.zombie, (short)3, zombiePacket.timeSinceSeenFlesh);
		if (this.zombie.isRemoteZombie()) {
			NetworkZombieVariables.setInt(this.zombie, (short)2, zombiePacket.speedMod);
			NetworkZombieVariables.setInt(this.zombie, (short)4, zombiePacket.smParamTargetAngle);
			NetworkZombieVariables.setBooleanVariables(this.zombie, zombiePacket.booleanVariables);
			this.zombie.setWalkType(zombiePacket.walkType.toString());
			this.zombie.realState = zombiePacket.realState;
		}

		this.zombie.realx = zombiePacket.realX;
		this.zombie.realy = zombiePacket.realY;
		this.zombie.realz = zombiePacket.realZ;
		if ((IsoUtils.DistanceToSquared(this.zombie.x, this.zombie.y, this.zombie.realx, this.zombie.realy) > 9.0F || this.zombie.z != (float)this.zombie.realz) && (this.zombie.isRemoteZombie() || IsoPlayer.getInstance() != null && IsoUtils.DistanceToSquared(this.zombie.x, this.zombie.y, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y) > 2.0F)) {
			NetworkTeleport.teleport(this.zombie, NetworkTeleport.Type.teleportation, this.zombie.realx, this.zombie.realy, this.zombie.realz, 1.0F);
		}
	}

	public void preupdate() {
		if (GameClient.bClient) {
			if (this.zombie.target != null) {
				this.zombie.setTargetSeenTime(this.zombie.getTargetSeenTime() + GameTime.getInstance().getRealworldSecondsSinceLastUpdate());
			}
		} else if (GameServer.bServer) {
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

	public static String getPredictionDebug(IsoGameCharacter gameCharacter, ZombiePacket zombiePacket, int int1, long long1) {
		return String.format("Prediction Z_%d [type=%s, distance=%f], time [current=%d, next=%d], states [current=%s, previous=%s]", zombiePacket.id, zombiePacket.moveType.toString(), IsoUtils.DistanceTo(gameCharacter.x, gameCharacter.y, zombiePacket.x, zombiePacket.y), int1, long1 - (long)int1, gameCharacter.getCurrentStateName(), gameCharacter.getPreviousStateName());
	}
}
