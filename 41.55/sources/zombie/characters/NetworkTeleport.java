package zombie.characters;

import java.util.Comparator;
import java.util.Map.Entry;
import zombie.GameTime;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.MPStatisticClient;
import zombie.network.packets.PlayerPacket;


public class NetworkTeleport {
	private NetworkTeleport.Type teleportType;
	private IsoGameCharacter character;
	private boolean setNewPos;
	private float nx;
	private float ny;
	private byte nz;
	public float ndirection;
	private float tx;
	private float ty;
	private byte tz;
	private long tt;
	private long startTime;
	private long duration;

	public NetworkTeleport(IsoGameCharacter gameCharacter, NetworkTeleport.Type type, float float1, float float2, byte byte1, float float3) {
		this.teleportType = NetworkTeleport.Type.none;
		this.character = null;
		this.setNewPos = false;
		this.nx = 0.0F;
		this.ny = 0.0F;
		this.nz = 0;
		this.tx = 0.0F;
		this.ty = 0.0F;
		this.tz = 0;
		this.tt = 0L;
		this.character = gameCharacter;
		this.setNewPos = false;
		this.nx = float1;
		this.ny = float2;
		this.nz = byte1;
		this.teleportType = type;
		this.startTime = System.currentTimeMillis();
		this.duration = (long)(1000.0 * (double)float3);
		gameCharacter.setTeleport(this);
		if (Core.bDebug && DebugOptions.instance.MultiplayerShowTeleport.getValue() && gameCharacter instanceof IsoZombie) {
			NetworkCharacter.PredictionMoveTypes predictionMoveTypes = ((IsoZombie)gameCharacter).networkAI.predictionType;
			long long1 = (long)((IsoZombie)gameCharacter).networkAI.predictionTime;
			gameCharacter.debugData.entrySet().stream().sorted(Entry.comparingByKey(Comparator.naturalOrder())).forEach((var0)->{
				DebugLog.log(DebugType.Multiplayer, "==> " + (String)var0.getValue());
			});

			DebugLog.log(DebugType.Multiplayer, String.format("NetworkTeleport Z_%d distance=%.3f, prediction=%s, time=%d", gameCharacter.getOnlineID(), IsoUtils.DistanceTo(gameCharacter.x, gameCharacter.y, float1, float2), predictionMoveTypes, long1));
			gameCharacter.teleportDebug = new NetworkTeleport.NetworkTeleportDebug((short)gameCharacter.getOnlineID(), gameCharacter.x, gameCharacter.y, gameCharacter.z, float1, float2, (float)byte1, long1, predictionMoveTypes);
		}
	}

	public void process() {
		float float1 = Math.min(1.0F, (float)(System.currentTimeMillis() - this.startTime) / (float)this.duration);
		switch (this.teleportType) {
		case disappearing: 
			if (float1 < 0.99F) {
				this.character.setAlphaAndTarget(1.0F - float1);
			} else {
				this.stop();
			}

			break;
		
		case teleportation: 
			if (float1 < 0.5F) {
				if (this.character.isoPlayer == null || this.character.isoPlayer != null && this.character.isoPlayer.spottedByPlayer) {
					this.character.setAlphaAndTarget(1.0F - float1 * 2.0F);
				}
			} else if (float1 < 0.99F) {
				if (!this.setNewPos) {
					this.setNewPos = true;
					this.character.setX(this.nx);
					this.character.setY(this.ny);
					this.character.setZ((float)this.nz);
					this.character.ensureOnTile();
				}

				if (this.character.isoPlayer == null || this.character.isoPlayer != null && this.character.isoPlayer.spottedByPlayer) {
					this.character.setAlphaAndTarget((float1 - 0.5F) * 2.0F);
				}
			} else {
				this.stop();
			}

			break;
		
		case materialization: 
			if (float1 < 0.99F) {
				this.character.setAlphaAndTarget(float1);
			} else {
				this.stop();
			}

		
		}
	}

	public void stop() {
		this.character.setTeleport((NetworkTeleport)null);
		switch (this.teleportType) {
		case disappearing: 
			this.character.setTargetAlpha(0.0F);
			break;
		
		case teleportation: 
		
		case materialization: 
			this.character.setTargetAlpha(1.0F);
		
		}
		this.character = null;
	}

	public static boolean teleport(IsoGameCharacter gameCharacter, NetworkTeleport.Type type, float float1, float float2, byte byte1, float float3) {
		if (!gameCharacter.isTeleporting()) {
			if (gameCharacter instanceof IsoZombie) {
				MPStatisticClient.getInstance().incrementZombiesTeleports();
			} else {
				MPStatisticClient.getInstance().incrementRemotePlayersTeleports();
			}

			new NetworkTeleport(gameCharacter, type, float1, float2, byte1, float3);
			return true;
		} else {
			return false;
		}
	}

	public static boolean teleport(IsoGameCharacter gameCharacter, PlayerPacket playerPacket, float float1) {
		if (!gameCharacter.isTeleporting()) {
			if (gameCharacter instanceof IsoZombie) {
				MPStatisticClient.getInstance().incrementZombiesTeleports();
			} else {
				MPStatisticClient.getInstance().incrementRemotePlayersTeleports();
			}

			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((double)gameCharacter.x, (double)gameCharacter.y, (double)gameCharacter.z);
			if (square == null) {
				IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare((double)playerPacket.realx, (double)playerPacket.realy, (double)playerPacket.realz);
				gameCharacter.setAlphaAndTarget(0.0F);
				gameCharacter.setX(playerPacket.realx);
				gameCharacter.setY(playerPacket.realy);
				gameCharacter.setZ((float)playerPacket.realz);
				gameCharacter.ensureOnTile();
				int int1 = (int)(GameTime.getServerTime() / 1000000L);
				float float2 = 0.5F * Math.min(1.0F, Math.max(0.0F, ((float)int1 + float1 * 1000.0F - (float)playerPacket.realt) / (float)(playerPacket.t - playerPacket.realt)));
				NetworkTeleport networkTeleport = new NetworkTeleport(gameCharacter, NetworkTeleport.Type.materialization, float2 * playerPacket.x + (1.0F - float2) * playerPacket.realx, float2 * playerPacket.y + (1.0F - float2) * playerPacket.realy, (byte)((int)(float2 * (float)playerPacket.z + (1.0F - float2) * (float)playerPacket.realz)), float1);
				networkTeleport.ndirection = playerPacket.direction;
				networkTeleport.tx = playerPacket.x;
				networkTeleport.ty = playerPacket.y;
				networkTeleport.tz = playerPacket.z;
				networkTeleport.tt = (long)playerPacket.t;
				return true;
			} else {
				int int2 = (int)(GameTime.getServerTime() / 1000000L);
				float float3 = 0.5F * Math.min(1.0F, Math.max(0.0F, ((float)int2 + float1 * 1000.0F - (float)playerPacket.realt) / (float)(playerPacket.t - playerPacket.realt)));
				NetworkTeleport networkTeleport2 = new NetworkTeleport(gameCharacter, NetworkTeleport.Type.teleportation, float3 * playerPacket.x + (1.0F - float3) * playerPacket.realx, float3 * playerPacket.y + (1.0F - float3) * playerPacket.realy, (byte)((int)(float3 * (float)playerPacket.z + (1.0F - float3) * (float)playerPacket.realz)), float1);
				networkTeleport2.ndirection = playerPacket.direction;
				networkTeleport2.tx = playerPacket.x;
				networkTeleport2.ty = playerPacket.y;
				networkTeleport2.tz = playerPacket.z;
				networkTeleport2.tt = (long)playerPacket.t;
				return true;
			}
		} else {
			return false;
		}
	}

	public static void update(IsoGameCharacter gameCharacter, PlayerPacket playerPacket) {
		if (gameCharacter.isTeleporting()) {
			NetworkTeleport networkTeleport = gameCharacter.getTeleport();
			if (networkTeleport.teleportType == NetworkTeleport.Type.teleportation) {
				float float1 = Math.min(1.0F, (float)(System.currentTimeMillis() - networkTeleport.startTime) / (float)networkTeleport.duration);
				if (float1 < 0.5F) {
					int int1 = (int)(GameTime.getServerTime() / 1000000L);
					float float2 = 0.5F * Math.min(1.0F, Math.max(0.0F, ((float)int1 + (float)networkTeleport.duration * 1000.0F - (float)playerPacket.realt) / (float)(playerPacket.t - playerPacket.realt)));
					networkTeleport.nx = float2 * playerPacket.x + (1.0F - float2) * playerPacket.realx;
					networkTeleport.ny = float2 * playerPacket.y + (1.0F - float2) * playerPacket.realy;
					networkTeleport.nz = (byte)((int)(float2 * (float)playerPacket.z + (1.0F - float2) * (float)playerPacket.realz));
				}

				networkTeleport.ndirection = playerPacket.direction;
				networkTeleport.tx = playerPacket.x;
				networkTeleport.ty = playerPacket.y;
				networkTeleport.tz = playerPacket.z;
				networkTeleport.tt = (long)playerPacket.t;
			}
		}
	}

	public static enum Type {

		none,
		disappearing,
		teleportation,
		materialization;

		private static NetworkTeleport.Type[] $values() {
			return new NetworkTeleport.Type[]{none, disappearing, teleportation, materialization};
		}
	}

	public static class NetworkTeleportDebug {
		short id;
		float nx;
		float ny;
		float nz;
		float lx;
		float ly;
		float lz;
		long time;
		NetworkCharacter.PredictionMoveTypes type;

		public NetworkTeleportDebug(short short1, float float1, float float2, float float3, float float4, float float5, float float6, long long1, NetworkCharacter.PredictionMoveTypes predictionMoveTypes) {
			this.id = short1;
			this.nx = float4;
			this.ny = float5;
			this.nz = float6;
			this.lx = float1;
			this.ly = float2;
			this.lz = float3;
			this.time = long1;
			this.type = predictionMoveTypes;
		}
	}
}
