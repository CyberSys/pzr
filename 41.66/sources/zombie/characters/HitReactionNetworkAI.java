package zombie.characters;

import zombie.GameTime;
import zombie.ai.states.PlayerFallDownState;
import zombie.ai.states.PlayerKnockedDown;
import zombie.ai.states.PlayerOnGroundState;
import zombie.ai.states.ZombieFallDownState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoDirections;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.network.GameServer;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;


public class HitReactionNetworkAI {
	private static final float G = 2.0F;
	private static final float DURATION = 600.0F;
	public final Vector2 startPosition = new Vector2();
	public final Vector2 finalPosition = new Vector2();
	public byte finalPositionZ = 0;
	public final Vector2 startDirection = new Vector2();
	public final Vector2 finalDirection = new Vector2();
	private float startAngle;
	private float finalAngle;
	private final IsoGameCharacter character;
	private long startTime;

	public HitReactionNetworkAI(IsoGameCharacter gameCharacter) {
		this.character = gameCharacter;
		this.startTime = 0L;
	}

	public boolean isSetup() {
		return this.finalPosition.x != 0.0F && this.finalPosition.y != 0.0F;
	}

	public boolean isStarted() {
		return this.startTime > 0L;
	}

	public void start() {
		if (this.isSetup() && !this.isStarted()) {
			this.startTime = GameTime.getServerTimeMills();
			if (this.startPosition.x != this.character.x || this.startPosition.y != this.character.y) {
				DebugLog.Multiplayer.warn("HitReaction start shifted");
			}

			if (Core.bDebug) {
				DebugLog.log(DebugType.Damage, String.format("HitReaction start id=%d: %s / %s => %s ", this.character.getOnlineID(), this.getActualDescription(), this.getStartDescription(), this.getFinalDescription()));
			}
		}
	}

	public void finish() {
		if (this.startTime != 0L && Core.bDebug) {
			DebugLog.log(DebugType.Damage, String.format("HitReaction finish id=%d: %s / %s => %s ", this.character.getOnlineID(), this.getActualDescription(), this.getStartDescription(), this.getFinalDescription()));
		}

		this.startTime = 0L;
		this.setup(0.0F, 0.0F, (byte)0, 0.0F);
	}

	public void setup(float float1, float float2, byte byte1, Float Float1) {
		this.startPosition.set(this.character.x, this.character.y);
		this.finalPosition.set(float1, float2);
		this.finalPositionZ = byte1;
		this.startDirection.set(this.character.getForwardDirection());
		this.startAngle = this.character.getAnimAngleRadians();
		Vector2 vector2 = (new Vector2()).set(this.finalPosition.x - this.startPosition.x, this.finalPosition.y - this.startPosition.y);
		if (Float1 == null) {
			vector2.normalize();
			Float1 = vector2.dot(this.character.getForwardDirection());
			PZMath.lerp(this.finalDirection, vector2, this.character.getForwardDirection(), Math.abs(Float1));
			IsoMovingObject.getVectorFromDirection(this.finalDirection, IsoDirections.fromAngle(this.finalDirection));
		} else {
			this.finalDirection.setLengthAndDirection(Float1, 1.0F);
		}

		this.finalAngle = Float1;
		if (this.isSetup() && Core.bDebug) {
			DebugLog.log(DebugType.Damage, String.format("HitReaction setup id=%d: %s / %s => %s ", this.character.getOnlineID(), this.getActualDescription(), this.getStartDescription(), this.getFinalDescription()));
		}
	}

	private void moveInternal(float float1, float float2, float float3, float float4) {
		this.character.nx = float1;
		this.character.ny = float2;
		this.character.setDir(IsoDirections.fromAngle(float3, float4));
		this.character.setForwardDirection(float3, float4);
		this.character.getAnimationPlayer().SetForceDir(this.character.getForwardDirection());
	}

	public void moveFinal() {
		this.moveInternal(this.finalPosition.x, this.finalPosition.y, this.finalDirection.x, this.finalDirection.y);
		this.character.lx = this.character.nx = this.character.x = this.finalPosition.x;
		this.character.ly = this.character.ny = this.character.y = this.finalPosition.y;
		this.character.setCurrent(IsoWorld.instance.CurrentCell.getGridSquare((double)((int)this.finalPosition.x), (double)((int)this.finalPosition.y), (double)this.character.z));
		if (Core.bDebug) {
			DebugLog.log(DebugType.Damage, String.format("HitReaction final id=%d: %s / %s => %s ", this.character.getOnlineID(), this.getActualDescription(), this.getStartDescription(), this.getFinalDescription()));
			DebugLog.log(DebugType.Multiplayer, "HitReaction final (): " + this.getDescription());
		}
	}

	public void move() {
		if (this.finalPositionZ != (byte)((int)this.character.z)) {
			DebugLog.log(String.format("HitReaction interrupt id=%d: z-final:%d z-current=%d", this.character.getOnlineID(), this.finalPositionZ, (byte)((int)this.character.z)));
			this.finish();
		} else {
			float float1 = Math.min(1.0F, Math.max(0.0F, (float)(GameTime.getServerTimeMills() - this.startTime) / 600.0F));
			if (this.startPosition.x == this.finalPosition.x && this.startPosition.y == this.finalPosition.y) {
				float1 = 1.0F;
			}

			if (float1 < 1.0F) {
				float1 = (PZMath.gain(float1 * 0.5F + 0.5F, 2.0F) - 0.5F) * 2.0F;
				this.moveInternal(PZMath.lerp(this.startPosition.x, this.finalPosition.x, float1), PZMath.lerp(this.startPosition.y, this.finalPosition.y, float1), PZMath.lerp(this.startDirection.x, this.finalDirection.x, float1), PZMath.lerp(this.startDirection.y, this.finalDirection.y, float1));
			} else {
				this.moveFinal();
				this.finish();
			}
		}
	}

	public boolean isDoSkipMovement() {
		if (this.character instanceof IsoZombie) {
			return this.character.isCurrentState(ZombieFallDownState.instance()) || this.character.isCurrentState(ZombieOnGroundState.instance());
		} else if (!(this.character instanceof IsoPlayer)) {
			return false;
		} else {
			return this.character.isCurrentState(PlayerFallDownState.instance()) || this.character.isCurrentState(PlayerKnockedDown.instance()) || this.character.isCurrentState(PlayerOnGroundState.instance());
		}
	}

	private String getStartDescription() {
		return String.format("start=[ pos=( %f ; %f ) dir=( %f ; %f ) angle=%f ]", this.startPosition.x, this.startPosition.y, this.startDirection.x, this.startDirection.y, this.startAngle);
	}

	private String getFinalDescription() {
		return String.format("final=[ pos=( %f ; %f ) dir=( %f ; %f ) angle=%f ]", this.finalPosition.x, this.finalPosition.y, this.finalDirection.x, this.finalDirection.y, this.finalAngle);
	}

	private String getActualDescription() {
		return String.format("actual=[ pos=( %f ; %f ) dir=( %f ; %f ) angle=%f ]", this.character.x, this.character.y, this.character.getForwardDirection().getX(), this.character.getForwardDirection().getY(), this.character.getAnimAngleRadians());
	}

	public String getDescription() {
		return String.format("start=%d | (x=%f,y=%f;a=%f;l=%f)", this.startTime, this.finalPosition.x, this.finalPosition.y, this.finalAngle, IsoUtils.DistanceTo(this.startPosition.x, this.startPosition.y, this.finalPosition.x, this.finalPosition.y));
	}

	public static void CalcHitReactionWeapon(IsoGameCharacter gameCharacter, IsoGameCharacter gameCharacter2, HandWeapon handWeapon) {
		HitReactionNetworkAI hitReactionNetworkAI = gameCharacter2.getHitReactionNetworkAI();
		if (gameCharacter2.isOnFloor()) {
			hitReactionNetworkAI.setup(gameCharacter2.x, gameCharacter2.y, (byte)((int)gameCharacter2.z), gameCharacter2.getAnimAngleRadians());
		} else {
			Vector2 vector2 = new Vector2();
			Float Float1 = gameCharacter2.calcHitDir(gameCharacter, handWeapon, vector2);
			if (gameCharacter2 instanceof IsoPlayer) {
				vector2.x = (vector2.x + gameCharacter2.x + ((IsoPlayer)gameCharacter2).networkAI.targetX) * 0.5F;
				vector2.y = (vector2.y + gameCharacter2.y + ((IsoPlayer)gameCharacter2).networkAI.targetY) * 0.5F;
			} else {
				vector2.x += gameCharacter2.x;
				vector2.y += gameCharacter2.y;
			}

			vector2.x = PZMath.roundFromEdges(vector2.x);
			vector2.y = PZMath.roundFromEdges(vector2.y);
			if (PolygonalMap2.instance.lineClearCollide(gameCharacter2.x, gameCharacter2.y, vector2.x, vector2.y, (int)gameCharacter2.z, (IsoMovingObject)null, false, true)) {
				vector2.x = gameCharacter2.x;
				vector2.y = gameCharacter2.y;
			}

			hitReactionNetworkAI.setup(vector2.x, vector2.y, (byte)((int)gameCharacter2.z), Float1);
		}

		if (hitReactionNetworkAI.isSetup()) {
			hitReactionNetworkAI.start();
			if (Core.bDebug) {
				DebugLog.log(DebugType.Multiplayer, "HitReaction start (local): " + hitReactionNetworkAI.getDescription());
			}
		}
	}

	public static void CalcHitReactionVehicle(IsoGameCharacter gameCharacter, BaseVehicle baseVehicle) {
		HitReactionNetworkAI hitReactionNetworkAI = gameCharacter.getHitReactionNetworkAI();
		if (!hitReactionNetworkAI.isStarted()) {
			if (gameCharacter.isOnFloor()) {
				hitReactionNetworkAI.setup(gameCharacter.x, gameCharacter.y, (byte)((int)gameCharacter.z), gameCharacter.getAnimAngleRadians());
			} else {
				Vector2 vector2 = new Vector2();
				gameCharacter.calcHitDir(vector2);
				if (gameCharacter instanceof IsoPlayer) {
					vector2.x = (vector2.x + gameCharacter.x + ((IsoPlayer)gameCharacter).networkAI.targetX) * 0.5F;
					vector2.y = (vector2.y + gameCharacter.y + ((IsoPlayer)gameCharacter).networkAI.targetY) * 0.5F;
				} else {
					vector2.x += gameCharacter.x;
					vector2.y += gameCharacter.y;
				}

				vector2.x = PZMath.roundFromEdges(vector2.x);
				vector2.y = PZMath.roundFromEdges(vector2.y);
				if (PolygonalMap2.instance.lineClearCollide(gameCharacter.x, gameCharacter.y, vector2.x, vector2.y, (int)gameCharacter.z, baseVehicle, false, true)) {
					vector2.x = gameCharacter.x;
					vector2.y = gameCharacter.y;
				}

				hitReactionNetworkAI.setup(vector2.x, vector2.y, (byte)((int)gameCharacter.z), (Float)null);
			}

			if (Core.bDebug) {
				DebugLog.log(DebugType.Multiplayer, "HitReaction setup (vehicle): " + hitReactionNetworkAI.getDescription());
			}
		}

		if (hitReactionNetworkAI.isSetup()) {
			hitReactionNetworkAI.start();
			if (Core.bDebug) {
				DebugLog.log(DebugType.Multiplayer, "HitReaction start (vehicle): " + hitReactionNetworkAI.getDescription());
			}
		}
	}

	public void process(float float1, float float2, float float3, float float4) {
		this.setup(float1, float2, (byte)((int)float3), float4);
		if (Core.bDebug) {
			DebugLog.log(DebugType.Damage, "Fall setup (remote): " + this.getDescription());
		}

		this.start();
		if (Core.bDebug) {
			DebugLog.log(DebugType.Damage, "Fall start (remote): " + this.getDescription());
		}

		if (GameServer.bServer) {
			this.moveFinal();
			this.finish();
			if (Core.bDebug) {
				DebugLog.log(DebugType.Damage, "Fall final (remote): " + this.getDescription());
			}
		}
	}
}
