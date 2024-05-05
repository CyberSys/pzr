package zombie.characters;

import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.debug.DebugOptions;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.MPStatisticClient;
import zombie.network.NetworkVariables;
import zombie.network.packets.PlayerPacket;


public class NetworkTeleport {
	public static boolean enable = true;
	public static boolean enableInstantTeleport = true;
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
		this.character = gameCharacter;
		this.setNewPos = false;
		this.nx = float1;
		this.ny = float2;
		this.nz = byte1;
		this.teleportType = type;
		this.startTime = System.currentTimeMillis();
		this.duration = (long)(1000.0 * (double)float3);
		gameCharacter.setTeleport(this);
		if (Core.bDebug && gameCharacter.getNetworkCharacterAI() != null && DebugOptions.instance.MultiplayerShowTeleport.getValue()) {
			gameCharacter.getNetworkCharacterAI().setTeleportDebug(new NetworkTeleport.NetworkTeleportDebug(gameCharacter.getOnlineID(), gameCharacter.x, gameCharacter.y, gameCharacter.z, float1, float2, (float)byte1, gameCharacter.getNetworkCharacterAI().predictionType));
		}
	}

	public void process(int int1) {
		if (!enable) {
			this.character.setX(this.nx);
			this.character.setY(this.ny);
			this.character.setZ((float)this.nz);
			this.character.ensureOnTile();
			this.character.setTeleport((NetworkTeleport)null);
			this.character = null;
		} else {
			boolean boolean1 = this.character.getCurrentSquare().isCanSee(int1);
			float float1 = Math.min(1.0F, (float)(System.currentTimeMillis() - this.startTime) / (float)this.duration);
			switch (this.teleportType) {
			case disappearing: 
				if (float1 < 0.99F) {
					this.character.setAlpha(int1, Math.min(this.character.getAlpha(int1), 1.0F - float1));
				} else {
					this.stop(int1);
				}

				break;
			
			case teleportation: 
				if (float1 < 0.5F) {
					if (this.character.isoPlayer == null || this.character.isoPlayer != null && this.character.isoPlayer.spottedByPlayer) {
						this.character.setAlpha(int1, Math.min(this.character.getAlpha(int1), 1.0F - float1 * 2.0F));
					}
				} else if (float1 < 0.99F) {
					if (!this.setNewPos) {
						this.setNewPos = true;
						this.character.setX(this.nx);
						this.character.setY(this.ny);
						this.character.setZ((float)this.nz);
						this.character.ensureOnTile();
						this.character.getNetworkCharacterAI().resetSpeedLimiter();
					}

					if (this.character.isoPlayer == null || this.character.isoPlayer != null && this.character.isoPlayer.spottedByPlayer) {
						this.character.setAlpha(int1, Math.min(this.character.getTargetAlpha(int1), (float1 - 0.5F) * 2.0F));
					}
				} else {
					this.stop(int1);
				}

				break;
			
			case materialization: 
				if (float1 < 0.99F) {
					this.character.setAlpha(int1, Math.min(this.character.getTargetAlpha(int1), float1));
				} else {
					this.stop(int1);
				}

			
			}
		}
	}

	public void stop(int int1) {
		this.character.setTeleport((NetworkTeleport)null);
		switch (this.teleportType) {
		case disappearing: 
			this.character.setAlpha(int1, Math.min(this.character.getAlpha(int1), 0.0F));
		
		default: 
			this.character = null;
		
		}
	}

	public static boolean teleport(IsoGameCharacter gameCharacter, NetworkTeleport.Type type, float float1, float float2, byte byte1, float float3) {
		if (!enable) {
			return false;
		} else {
			if (gameCharacter.getCurrentSquare() != null && enableInstantTeleport) {
				boolean boolean1 = false;
				for (int int1 = 0; int1 < 4; ++int1) {
					if (gameCharacter.getCurrentSquare().isCanSee(int1)) {
						boolean1 = true;
						break;
					}
				}

				IsoGridSquare square = LuaManager.GlobalObject.getCell().getGridSquare((int)float1, (int)float2, byte1);
				if (square != null) {
					for (int int2 = 0; int2 < 4; ++int2) {
						if (square.isCanSee(int2)) {
							boolean1 = true;
							break;
						}
					}
				}

				if (!boolean1) {
					gameCharacter.setX(float1);
					gameCharacter.setY(float2);
					gameCharacter.setZ((float)byte1);
					gameCharacter.ensureOnTile();
					return false;
				}
			}

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
	}

	public static boolean teleport(IsoGameCharacter gameCharacter, PlayerPacket playerPacket, float float1) {
		if (!enable) {
			return false;
		} else {
			if (LuaManager.GlobalObject.getCell().getGridSquare((int)playerPacket.x, (int)playerPacket.y, playerPacket.z) == null) {
				gameCharacter.setX(playerPacket.x);
				gameCharacter.setY(playerPacket.y);
				gameCharacter.setZ((float)playerPacket.z);
				gameCharacter.realx = playerPacket.realx;
				gameCharacter.realy = playerPacket.realy;
				gameCharacter.realz = playerPacket.realz;
				gameCharacter.realdir = IsoDirections.fromIndex(playerPacket.realdir);
				gameCharacter.ensureOnTile();
			}

			IsoGridSquare square;
			if (gameCharacter.getCurrentSquare() != null && enableInstantTeleport) {
				boolean boolean1 = false;
				for (int int1 = 0; int1 < 4; ++int1) {
					if (gameCharacter.getCurrentSquare().isCanSee(int1)) {
						boolean1 = true;
						break;
					}
				}

				square = LuaManager.GlobalObject.getCell().getGridSquare((int)playerPacket.x, (int)playerPacket.y, playerPacket.z);
				if (square != null) {
					for (int int2 = 0; int2 < 4; ++int2) {
						if (square.isCanSee(int2)) {
							boolean1 = true;
							break;
						}
					}
				}

				if (!boolean1) {
					gameCharacter.setX(playerPacket.x);
					gameCharacter.setY(playerPacket.y);
					gameCharacter.setZ((float)playerPacket.z);
					gameCharacter.ensureOnTile();
					return false;
				}
			}

			if (!gameCharacter.isTeleporting()) {
				if (gameCharacter instanceof IsoZombie) {
					MPStatisticClient.getInstance().incrementZombiesTeleports();
				} else {
					MPStatisticClient.getInstance().incrementRemotePlayersTeleports();
				}

				IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare((double)gameCharacter.x, (double)gameCharacter.y, (double)gameCharacter.z);
				if (square2 == null) {
					square = IsoWorld.instance.CurrentCell.getGridSquare((double)playerPacket.realx, (double)playerPacket.realy, (double)playerPacket.realz);
					gameCharacter.setAlphaAndTarget(0.0F);
					gameCharacter.setX(playerPacket.realx);
					gameCharacter.setY(playerPacket.realy);
					gameCharacter.setZ((float)playerPacket.realz);
					gameCharacter.ensureOnTile();
					float float2 = 0.5F;
					NetworkTeleport networkTeleport = new NetworkTeleport(gameCharacter, NetworkTeleport.Type.materialization, float2 * playerPacket.x + (1.0F - float2) * playerPacket.realx, float2 * playerPacket.y + (1.0F - float2) * playerPacket.realy, (byte)((int)(float2 * (float)playerPacket.z + (1.0F - float2) * (float)playerPacket.realz)), float1);
					networkTeleport.ndirection = playerPacket.direction;
					networkTeleport.tx = playerPacket.x;
					networkTeleport.ty = playerPacket.y;
					networkTeleport.tz = playerPacket.z;
					return true;
				} else {
					float float3 = 0.5F;
					NetworkTeleport networkTeleport2 = new NetworkTeleport(gameCharacter, NetworkTeleport.Type.teleportation, float3 * playerPacket.x + (1.0F - float3) * playerPacket.realx, float3 * playerPacket.y + (1.0F - float3) * playerPacket.realy, (byte)((int)(float3 * (float)playerPacket.z + (1.0F - float3) * (float)playerPacket.realz)), float1);
					networkTeleport2.ndirection = playerPacket.direction;
					networkTeleport2.tx = playerPacket.x;
					networkTeleport2.ty = playerPacket.y;
					networkTeleport2.tz = playerPacket.z;
					return true;
				}
			} else {
				return false;
			}
		}
	}

	public static void update(IsoGameCharacter gameCharacter, PlayerPacket playerPacket) {
		if (gameCharacter.isTeleporting()) {
			NetworkTeleport networkTeleport = gameCharacter.getTeleport();
			if (networkTeleport.teleportType == NetworkTeleport.Type.teleportation) {
				float float1 = Math.min(1.0F, (float)(System.currentTimeMillis() - networkTeleport.startTime) / (float)networkTeleport.duration);
				if (float1 < 0.5F) {
					float float2 = 0.5F;
					networkTeleport.nx = float2 * playerPacket.x + (1.0F - float2) * playerPacket.realx;
					networkTeleport.ny = float2 * playerPacket.y + (1.0F - float2) * playerPacket.realy;
					networkTeleport.nz = (byte)((int)(float2 * (float)playerPacket.z + (1.0F - float2) * (float)playerPacket.realz));
				}

				networkTeleport.ndirection = playerPacket.direction;
				networkTeleport.tx = playerPacket.x;
				networkTeleport.ty = playerPacket.y;
				networkTeleport.tz = playerPacket.z;
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
		NetworkVariables.PredictionTypes type;

		public NetworkTeleportDebug(short short1, float float1, float float2, float float3, float float4, float float5, float float6, NetworkVariables.PredictionTypes predictionTypes) {
			this.id = short1;
			this.nx = float4;
			this.ny = float5;
			this.nz = float6;
			this.lx = float1;
			this.ly = float2;
			this.lz = float3;
			this.type = predictionTypes;
		}

		public float getDistance() {
			return IsoUtils.DistanceTo(this.lx, this.ly, this.lz, this.nx, this.ny, this.nz);
		}
	}
}
