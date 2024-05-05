package zombie.characters;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import zombie.GameTime;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.Core;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;
import zombie.network.GameServer;
import zombie.network.NetworkVariables;
import zombie.network.PacketValidator;
import zombie.network.packets.DeadCharacterPacket;
import zombie.network.packets.hit.IMovable;
import zombie.network.packets.hit.VehicleHitPacket;


public abstract class NetworkCharacterAI {
	private static final short VEHICLE_HIT_DELAY_MS = 500;
	private final NetworkCharacterAI.SpeedChecker speedChecker = new NetworkCharacterAI.SpeedChecker();
	public NetworkVariables.PredictionTypes predictionType;
	protected DeadCharacterPacket deadBody;
	protected VehicleHitPacket vehicleHit;
	protected float timestamp;
	protected BaseAction action;
	protected String performingAction;
	protected long noCollisionTime;
	protected boolean wasLocal;
	protected final HitReactionNetworkAI hitReaction;
	private final IsoGameCharacter character;
	public NetworkTeleport.NetworkTeleportDebug teleportDebug;
	public final HashMap debugData = new LinkedHashMap(){
    
    protected boolean removeEldestEntry(Entry var1) {
        return this.size() > 10;
    }
};

	public NetworkCharacterAI(IsoGameCharacter gameCharacter) {
		this.character = gameCharacter;
		this.deadBody = null;
		this.wasLocal = false;
		this.vehicleHit = null;
		this.noCollisionTime = 0L;
		this.hitReaction = new HitReactionNetworkAI(gameCharacter);
		this.predictionType = NetworkVariables.PredictionTypes.None;
		this.clearTeleportDebug();
		this.speedChecker.reset();
	}

	public void reset() {
		this.deadBody = null;
		this.wasLocal = false;
		this.vehicleHit = null;
		this.noCollisionTime = 0L;
		this.hitReaction.finish();
		this.predictionType = NetworkVariables.PredictionTypes.None;
		this.clearTeleportDebug();
		this.speedChecker.reset();
	}

	public void setLocal(boolean boolean1) {
		this.wasLocal = boolean1;
	}

	public boolean wasLocal() {
		return this.wasLocal;
	}

	public NetworkTeleport.NetworkTeleportDebug getTeleportDebug() {
		return this.teleportDebug;
	}

	public void clearTeleportDebug() {
		this.teleportDebug = null;
		this.debugData.clear();
	}

	public void setTeleportDebug(NetworkTeleport.NetworkTeleportDebug networkTeleportDebug) {
		this.teleportDebug = networkTeleportDebug;
		this.debugData.entrySet().stream().sorted(Entry.comparingByKey(Comparator.naturalOrder())).forEach((var0)->{
			if (Core.bDebug) {
				DebugLog.log(DebugType.Multiplayer, "==> " + (String)var0.getValue());
			}
		});
		if (Core.bDebug) {
			DebugLog.log(DebugType.Multiplayer, String.format("NetworkTeleport %s id=%d distance=%.3f prediction=%s", this.character.getClass().getSimpleName(), this.character.getOnlineID(), networkTeleportDebug.getDistance(), this.predictionType));
		}
	}

	public void addTeleportData(int int1, String string) {
		this.debugData.put(int1, string);
	}

	public void processDeadBody() {
		if (this.isSetDeadBody() && !this.hitReaction.isSetup() && !this.hitReaction.isStarted()) {
			this.deadBody.process();
			this.setDeadBody((DeadCharacterPacket)null);
		}
	}

	public void setDeadBody(DeadCharacterPacket deadCharacterPacket) {
		this.deadBody = deadCharacterPacket;
		DebugLog.Death.trace(deadCharacterPacket == null ? "processed" : "postpone");
	}

	public boolean isSetDeadBody() {
		return this.deadBody != null && this.deadBody.isConsistent();
	}

	public void setPerformingAction(String string) {
		this.performingAction = string;
	}

	public String getPerformingAction() {
		return this.performingAction;
	}

	public void setAction(BaseAction baseAction) {
		this.action = baseAction;
	}

	public BaseAction getAction() {
		return this.action;
	}

	public void startAction() {
		if (this.action != null) {
			this.action.start();
		}
	}

	public void stopAction() {
		if (this.action != null) {
			this.setOverride(false, (String)null, (String)null);
			this.action.stop();
		}
	}

	public void setOverride(boolean boolean1, String string, String string2) {
		if (this.action != null) {
			this.action.chr.forceNullOverride = boolean1;
			this.action.chr.overridePrimaryHandModel = string;
			this.action.chr.overrideSecondaryHandModel = string2;
			this.action.chr.resetModelNextFrame();
		}
	}

	public void processVehicleHit() {
		this.vehicleHit.tryProcessInternal();
		this.setVehicleHit((VehicleHitPacket)null);
	}

	public void setVehicleHit(VehicleHitPacket vehicleHitPacket) {
		this.vehicleHit = vehicleHitPacket;
		this.timestamp = (float)TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime());
		DebugLog.Damage.noise(vehicleHitPacket == null ? "processed" : "postpone");
	}

	public boolean isSetVehicleHit() {
		return this.vehicleHit != null && this.vehicleHit.isConsistent();
	}

	public void resetVehicleHitTimeout() {
		this.timestamp = (float)(TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime()) - 500L);
		if (this.vehicleHit == null) {
			DebugLog.Damage.noise("VehicleHit is not set");
		}
	}

	public boolean isVehicleHitTimeout() {
		boolean boolean1 = (float)TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime()) - this.timestamp >= 500.0F;
		if (boolean1) {
			DebugLog.Damage.noise("VehicleHit timeout");
		}

		return boolean1;
	}

	public void updateHitVehicle() {
		if (this.isSetVehicleHit() && this.isVehicleHitTimeout()) {
			this.processVehicleHit();
		}
	}

	public boolean isCollisionEnabled() {
		return this.noCollisionTime == 0L;
	}

	public boolean isNoCollisionTimeout() {
		boolean boolean1 = GameTime.getServerTimeMills() > this.noCollisionTime;
		if (boolean1) {
			this.setNoCollision(0L);
		}

		return boolean1;
	}

	public void setNoCollision(long long1) {
		if (long1 == 0L) {
			this.noCollisionTime = 0L;
			if (Core.bDebug) {
				DebugLog.log(DebugType.Multiplayer, "SetNoCollision: disabled");
			}
		} else {
			this.noCollisionTime = GameTime.getServerTimeMills() + long1;
			if (Core.bDebug) {
				DebugLog.log(DebugType.Multiplayer, "SetNoCollision: enabled for " + long1 + " ms");
			}
		}
	}

	public boolean checkPosition(UdpConnection udpConnection, IsoGameCharacter gameCharacter, float float1, float float2) {
		if (GameServer.bServer && gameCharacter.isAlive()) {
			this.speedChecker.set(float1, float2, gameCharacter.isSeatedInVehicle());
			NetworkCharacterAI.SpeedChecker speedChecker = this.speedChecker;
			String string = gameCharacter.getClass().getSimpleName();
			boolean boolean1 = PacketValidator.checkType2(udpConnection, speedChecker, string + NetworkCharacterAI.SpeedChecker.class.getSimpleName());
			if (32 == udpConnection.accessLevel) {
				boolean1 = true;
			}

			return boolean1;
		} else {
			return true;
		}
	}

	public void resetSpeedLimiter() {
		this.speedChecker.reset();
	}

	private static class SpeedChecker implements IMovable {
		private static final int checkDelay = 5000;
		private static final int checkInterval = 1000;
		private final UpdateLimit updateLimit = new UpdateLimit(5000L);
		private final Vector2 position = new Vector2();
		private boolean isInVehicle;
		private float speed;

		public float getSpeed() {
			return this.speed;
		}

		public boolean isVehicle() {
			return this.isInVehicle;
		}

		private void set(float float1, float float2, boolean boolean1) {
			if (this.updateLimit.Check()) {
				if (5000L == this.updateLimit.getDelay()) {
					this.updateLimit.Reset(1000L);
					this.position.set(0.0F, 0.0F);
					this.speed = 0.0F;
				}

				this.isInVehicle = boolean1;
				if (this.position.getLength() != 0.0F) {
					this.speed = IsoUtils.DistanceTo(this.position.x, this.position.y, float1, float2);
				}

				this.position.set(float1, float2);
			}
		}

		private void reset() {
			this.updateLimit.Reset(5000L);
			this.isInVehicle = false;
			this.position.set(0.0F, 0.0F);
			this.speed = 0.0F;
		}

		public String getDescription() {
			return "SpeedChecker: speed=" + this.speed + " x=" + this.position.x + " y=" + this.position.y + " vehicle=" + this.isInVehicle;
		}
	}
}
