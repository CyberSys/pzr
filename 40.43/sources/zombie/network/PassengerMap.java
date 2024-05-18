package zombie.network;

import java.nio.ByteBuffer;
import org.joml.Vector3f;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.vehicles.BaseVehicle;


public final class PassengerMap {
	private static final int CHUNKS = 7;
	private static final int MAX_PASSENGERS = 16;
	private static final PassengerMap.PassengerLocal[] perPlayerPngr = new PassengerMap.PassengerLocal[4];
	private static final PassengerMap.DriverLocal[] perPlayerDriver = new PassengerMap.DriverLocal[4];

	public static void updatePassenger(IsoPlayer player) {
		if (player != null && player.getVehicle() != null && !player.getVehicle().isDriver(player)) {
			IsoGameCharacter gameCharacter = player.getVehicle().getDriver();
			if (gameCharacter instanceof IsoPlayer && !((IsoPlayer)gameCharacter).isLocalPlayer()) {
				PassengerMap.PassengerLocal passengerLocal = perPlayerPngr[player.PlayerIndex];
				passengerLocal.chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[player.PlayerIndex];
				passengerLocal.updateLoaded();
			}
		}
	}

	public static void serverReceivePacket(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		byte byte1 = byteBuffer.get();
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		long long1 = byteBuffer.getLong();
		IsoPlayer player = udpConnection.players[byte1];
		if (player != null && player.getVehicle() != null) {
			IsoGameCharacter gameCharacter = player.getVehicle().getDriver();
			if (gameCharacter instanceof IsoPlayer && gameCharacter != player) {
				UdpConnection udpConnection2 = GameServer.getConnectionFromPlayer((IsoPlayer)gameCharacter);
				if (udpConnection2 != null) {
					ByteBufferWriter byteBufferWriter = udpConnection2.startPacket();
					PacketTypes.doPacket((short)16, byteBufferWriter);
					byteBufferWriter.putShort(player.getVehicle().VehicleID);
					byteBufferWriter.putByte((byte)player.getVehicle().getSeat(player));
					byteBufferWriter.putInt(int1);
					byteBufferWriter.putInt(int2);
					byteBufferWriter.putLong(long1);
					udpConnection2.endPacket();
				}
			}
		}
	}

	public static void clientReceivePacket(ByteBuffer byteBuffer) {
		short short1 = byteBuffer.getShort();
		byte byte1 = byteBuffer.get();
		int int1 = byteBuffer.getInt();
		int int2 = byteBuffer.getInt();
		long long1 = byteBuffer.getLong();
		for (int int3 = 0; int3 < IsoPlayer.numPlayers; ++int3) {
			IsoPlayer player = IsoPlayer.players[int3];
			if (player != null && player.getVehicle() != null) {
				BaseVehicle baseVehicle = player.getVehicle();
				if (baseVehicle.VehicleID == short1 && baseVehicle.isDriver(player)) {
					PassengerMap.DriverLocal driverLocal = perPlayerDriver[int3];
					PassengerMap.PassengerRemote passengerRemote = driverLocal.passengers[byte1];
					if (passengerRemote == null) {
						passengerRemote = driverLocal.passengers[byte1] = new PassengerMap.PassengerRemote();
					}

					passengerRemote.setLoaded(int1, int2, long1);
				}
			}
		}
	}

	public static boolean isChunkLoaded(BaseVehicle baseVehicle, int int1, int int2) {
		if (!GameClient.bClient) {
			return false;
		} else if (baseVehicle != null && int1 >= 0 && int2 >= 0) {
			IsoGameCharacter gameCharacter = baseVehicle.getDriver();
			if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
				int int3 = ((IsoPlayer)gameCharacter).PlayerIndex;
				PassengerMap.DriverLocal driverLocal = perPlayerDriver[int3];
				for (int int4 = 1; int4 < baseVehicle.getMaxPassengers(); ++int4) {
					PassengerMap.PassengerRemote passengerRemote = driverLocal.passengers[int4];
					if (passengerRemote != null && passengerRemote.wx != -1) {
						IsoGameCharacter gameCharacter2 = baseVehicle.getCharacter(int4);
						if (gameCharacter2 instanceof IsoPlayer && !((IsoPlayer)gameCharacter2).isLocalPlayer()) {
							int int5 = passengerRemote.wx - 3;
							int int6 = passengerRemote.wy - 3;
							if (int1 >= int5 && int2 >= int6 && int1 < int5 + 7 && int2 < int6 + 7 && (passengerRemote.loaded & 1L << int1 - int5 + (int2 - int6) * 7) == 0L) {
								return false;
							}
						} else {
							passengerRemote.wx = -1;
						}
					}
				}

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static void render(int int1) {
		if (GameClient.bClient) {
			IsoPlayer player = IsoPlayer.players[int1];
			if (player != null && player.getVehicle() != null) {
				BaseVehicle baseVehicle = player.getVehicle();
				int int2 = Core.TileScale;
				byte byte1 = 10;
				float float1 = 0.1F;
				float float2 = 0.1F;
				float float3 = 0.1F;
				float float4 = 0.75F;
				float float5 = 0.0F;
				PassengerMap.DriverLocal driverLocal = perPlayerDriver[int1];
				for (int int3 = 1; int3 < baseVehicle.getMaxPassengers(); ++int3) {
					PassengerMap.PassengerRemote passengerRemote = driverLocal.passengers[int3];
					if (passengerRemote != null && passengerRemote.wx != -1) {
						IsoGameCharacter gameCharacter = baseVehicle.getCharacter(int3);
						if (gameCharacter instanceof IsoPlayer && !((IsoPlayer)gameCharacter).isLocalPlayer()) {
							for (int int4 = 0; int4 < 7; ++int4) {
								for (int int5 = 0; int5 < 7; ++int5) {
									boolean boolean1 = (passengerRemote.loaded & 1L << int5 + int4 * 7) != 0L;
									if (!boolean1) {
										float float6 = (float)((passengerRemote.wx - 3 + int5) * byte1);
										float float7 = (float)((passengerRemote.wy - 3 + int4) * byte1);
										float float8 = IsoUtils.XToScreenExact(float6, float7 + (float)byte1, float5, 0);
										float float9 = IsoUtils.YToScreenExact(float6, float7 + (float)byte1, float5, 0);
										SpriteRenderer.instance.renderPoly((int)float8, (int)float9, (int)(float8 + (float)(byte1 * 64 / 2 * int2)), (int)(float9 - (float)(byte1 * 32 / 2 * int2)), (int)(float8 + (float)(byte1 * 64 * int2)), (int)float9, (int)(float8 + (float)(byte1 * 64 / 2 * int2)), (int)(float9 + (float)(byte1 * 32 / 2 * int2)), float1, float2, float3, float4);
									}
								}
							}
						} else {
							passengerRemote.wx = -1;
						}
					}
				}
			}
		}
	}

	public static void Reset() {
		for (int int1 = 0; int1 < 4; ++int1) {
			PassengerMap.PassengerLocal passengerLocal = perPlayerPngr[int1];
			passengerLocal.wx = -1;
			PassengerMap.DriverLocal driverLocal = perPlayerDriver[int1];
			for (int int2 = 0; int2 < 16; ++int2) {
				PassengerMap.PassengerRemote passengerRemote = driverLocal.passengers[int2];
				if (passengerRemote != null) {
					passengerRemote.wx = -1;
				}
			}
		}
	}

	static  {
	for (int var0 = 0; var0 < 4; ++var0) {
		perPlayerPngr[var0] = new PassengerMap.PassengerLocal(var0);
		perPlayerDriver[var0] = new PassengerMap.DriverLocal();
	}
	}

	private static final class DriverLocal {
		final PassengerMap.PassengerRemote[] passengers;

		private DriverLocal() {
			this.passengers = new PassengerMap.PassengerRemote[16];
		}

		DriverLocal(Object object) {
			this();
		}
	}

	private static final class PassengerRemote {
		int wx;
		int wy;
		long loaded;

		private PassengerRemote() {
			this.wx = -1;
			this.wy = -1;
			this.loaded = 0L;
		}

		void setLoaded(int int1, int int2, long long1) {
			this.wx = int1;
			this.wy = int2;
			this.loaded = long1;
		}

		PassengerRemote(Object object) {
			this();
		}
	}

	private static final class PassengerLocal {
		final int playerIndex;
		IsoChunkMap chunkMap;
		int wx = -1;
		int wy = -1;
		long loaded = 0L;

		PassengerLocal(int int1) {
			this.playerIndex = int1;
		}

		boolean setLoaded() {
			int int1 = this.chunkMap.WorldX;
			int int2 = this.chunkMap.WorldY;
			Vector3f vector3f = IsoPlayer.players[this.playerIndex].getVehicle().jniLinearVelocity;
			float float1 = Math.abs(vector3f.x);
			float float2 = Math.abs(vector3f.z);
			boolean boolean1 = vector3f.x < 0.0F && float1 > float2;
			boolean boolean2 = vector3f.x > 0.0F && float1 > float2;
			boolean boolean3 = vector3f.z < 0.0F && float2 > float1;
			boolean boolean4 = vector3f.z > 0.0F && float2 > float1;
			if (boolean2) {
				++int1;
			} else if (boolean1) {
				--int1;
			} else if (boolean3) {
				--int2;
			} else if (boolean4) {
				++int2;
			}

			long long1 = 0L;
			for (int int3 = 0; int3 < 7; ++int3) {
				for (int int4 = 0; int4 < 7; ++int4) {
					IsoChunk chunk = this.chunkMap.getChunk(IsoChunkMap.ChunkGridWidth / 2 - 3 + int4, IsoChunkMap.ChunkGridWidth / 2 - 3 + int3);
					if (chunk != null && chunk.bLoaded) {
						long1 |= 1L << int4 + int3 * 7;
					}
				}
			}

			boolean boolean5 = int1 != this.wx || int2 != this.wy || long1 != this.loaded;
			if (boolean5) {
				this.wx = int1;
				this.wy = int2;
				this.loaded = long1;
			}

			return boolean5;
		}

		void updateLoaded() {
			if (this.setLoaded()) {
				this.clientSendPacket(GameClient.connection);
			}
		}

		void clientSendPacket(UdpConnection udpConnection) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.doPacket((short)16, byteBufferWriter);
			byteBufferWriter.putByte((byte)this.playerIndex);
			byteBufferWriter.putInt(this.wx);
			byteBufferWriter.putInt(this.wy);
			byteBufferWriter.putLong(this.loaded);
			udpConnection.endPacket();
		}
	}
}
