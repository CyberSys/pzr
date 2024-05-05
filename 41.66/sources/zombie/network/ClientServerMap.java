package zombie.network;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Consumer;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.Texture;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;


public final class ClientServerMap {
	private static final int ChunksPerServerCell = 5;
	private static final int SquaresPerServerCell = 50;
	int playerIndex;
	int centerX;
	int centerY;
	int chunkGridWidth;
	int width;
	boolean[] loaded;
	private static boolean[] isLoaded;
	private static Texture trafficCone;

	public ClientServerMap(int int1, int int2, int int3, int int4) {
		this.playerIndex = int1;
		this.centerX = int2;
		this.centerY = int3;
		this.chunkGridWidth = int4;
		this.width = (int4 - 1) * 10 / 50;
		if ((int4 - 1) * 10 % 50 != 0) {
			++this.width;
		}

		++this.width;
		this.loaded = new boolean[this.width * this.width];
	}

	public int getMinX() {
		return (this.centerX / 10 - this.chunkGridWidth / 2) / 5;
	}

	public int getMinY() {
		return (this.centerY / 10 - this.chunkGridWidth / 2) / 5;
	}

	public int getMaxX() {
		return this.getMinX() + this.width - 1;
	}

	public int getMaxY() {
		return this.getMinY() + this.width - 1;
	}

	public boolean isValidCell(int int1, int int2) {
		return int1 >= 0 && int2 >= 0 && int1 < this.width && int2 < this.width;
	}

	public boolean setLoaded() {
		if (!GameServer.bServer) {
			return false;
		} else {
			int int1 = ServerMap.instance.getMinX();
			int int2 = ServerMap.instance.getMinY();
			int int3 = this.getMinX();
			int int4 = this.getMinY();
			boolean boolean1 = false;
			for (int int5 = 0; int5 < this.width; ++int5) {
				for (int int6 = 0; int6 < this.width; ++int6) {
					ServerMap.ServerCell serverCell = ServerMap.instance.getCell(int3 + int6 - int1, int4 + int5 - int2);
					boolean boolean2 = serverCell == null ? false : serverCell.bLoaded;
					boolean1 |= this.loaded[int6 + int5 * this.width] != boolean2;
					this.loaded[int6 + int5 * this.width] = boolean2;
				}
			}

			return boolean1;
		}
	}

	public boolean setPlayerPosition(int int1, int int2) {
		if (!GameServer.bServer) {
			return false;
		} else {
			int int3 = this.getMinX();
			int int4 = this.getMinY();
			this.centerX = int1;
			this.centerY = int2;
			return this.setLoaded() || int3 != this.getMinX() || int4 != this.getMinY();
		}
	}

	public static boolean isChunkLoaded(int int1, int int2) {
		if (!GameClient.bClient) {
			return false;
		} else if (int1 >= 0 && int2 >= 0) {
			for (int int3 = 0; int3 < IsoPlayer.numPlayers; ++int3) {
				ClientServerMap clientServerMap = GameClient.loadedCells[int3];
				if (clientServerMap != null) {
					int int4 = int1 / 5 - clientServerMap.getMinX();
					int int5 = int2 / 5 - clientServerMap.getMinY();
					if (clientServerMap.isValidCell(int4, int5) && clientServerMap.loaded[int4 + int5 * clientServerMap.width]) {
						return true;
					}
				}
			}

			return false;
		} else {
			return false;
		}
	}

	public static void characterIn(UdpConnection udpConnection, int int1) {
		if (GameServer.bServer) {
			ClientServerMap clientServerMap = udpConnection.loadedCells[int1];
			if (clientServerMap != null) {
				IsoPlayer player = udpConnection.players[int1];
				if (player != null) {
					if (clientServerMap.setPlayerPosition((int)player.x, (int)player.y)) {
						clientServerMap.sendPacket(udpConnection);
					}
				}
			}
		}
	}

	public void sendPacket(UdpConnection udpConnection) {
		if (GameServer.bServer) {
			ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
			PacketTypes.PacketType.ServerMap.doPacket(byteBufferWriter);
			byteBufferWriter.putByte((byte)this.playerIndex);
			byteBufferWriter.putInt(this.centerX);
			byteBufferWriter.putInt(this.centerY);
			for (int int1 = 0; int1 < this.width; ++int1) {
				for (int int2 = 0; int2 < this.width; ++int2) {
					byteBufferWriter.putBoolean(this.loaded[int2 + int1 * this.width]);
				}
			}

			PacketTypes.PacketType.ServerMap.send(udpConnection);
		}
	}

	public static void receivePacket(ByteBuffer byteBuffer) {
		if (GameClient.bClient) {
			byte byte1 = byteBuffer.get();
			int int1 = byteBuffer.getInt();
			int int2 = byteBuffer.getInt();
			ClientServerMap clientServerMap = GameClient.loadedCells[byte1];
			if (clientServerMap == null) {
				clientServerMap = GameClient.loadedCells[byte1] = new ClientServerMap(byte1, int1, int2, IsoChunkMap.ChunkGridWidth);
			}

			clientServerMap.centerX = int1;
			clientServerMap.centerY = int2;
			for (int int3 = 0; int3 < clientServerMap.width; ++int3) {
				for (int int4 = 0; int4 < clientServerMap.width; ++int4) {
					clientServerMap.loaded[int4 + int3 * clientServerMap.width] = byteBuffer.get() == 1;
				}
			}
		}
	}

	public static void render(int int1) {
		if (GameClient.bClient) {
			IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.getChunkMap(int1);
			if (chunkMap != null && !chunkMap.ignore) {
				int int2 = Core.TileScale;
				byte byte1 = 10;
				float float1 = 0.1F;
				float float2 = 0.1F;
				float float3 = 0.1F;
				float float4 = 0.75F;
				float float5 = 0.0F;
				if (trafficCone == null) {
					trafficCone = Texture.getSharedTexture("street_decoration_01_26");
				}

				Texture texture = trafficCone;
				if (isLoaded == null || isLoaded.length < IsoChunkMap.ChunkGridWidth * IsoChunkMap.ChunkGridWidth) {
					isLoaded = new boolean[IsoChunkMap.ChunkGridWidth * IsoChunkMap.ChunkGridWidth];
				}

				int int3;
				int int4;
				IsoChunk chunk;
				for (int3 = 0; int3 < IsoChunkMap.ChunkGridWidth; ++int3) {
					for (int4 = 0; int4 < IsoChunkMap.ChunkGridWidth; ++int4) {
						chunk = chunkMap.getChunk(int4, int3);
						if (chunk != null) {
							isLoaded[int4 + int3 * IsoChunkMap.ChunkGridWidth] = isChunkLoaded(chunk.wx, chunk.wy);
						}
					}
				}

				for (int3 = 0; int3 < IsoChunkMap.ChunkGridWidth; ++int3) {
					for (int4 = 0; int4 < IsoChunkMap.ChunkGridWidth; ++int4) {
						chunk = chunkMap.getChunk(int4, int3);
						if (chunk != null) {
							boolean boolean1 = isLoaded[int4 + int3 * IsoChunkMap.ChunkGridWidth];
							float float6;
							float float7;
							if (boolean1 && texture != null) {
								IsoChunk chunk2 = chunkMap.getChunk(int4, int3 - 1);
								if (chunk2 != null && !isLoaded[int4 + (int3 - 1) * IsoChunkMap.ChunkGridWidth]) {
									for (int int5 = 0; int5 < byte1; ++int5) {
										float6 = IsoUtils.XToScreenExact((float)(chunk.wx * byte1 + int5), (float)(chunk.wy * byte1), float5, 0);
										float7 = IsoUtils.YToScreenExact((float)(chunk.wx * byte1 + int5), (float)(chunk.wy * byte1), float5, 0);
										SpriteRenderer.instance.render(texture, float6 - (float)(texture.getWidth() / 2), float7, (float)texture.getWidth(), (float)texture.getHeight(), 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
									}
								}

								IsoChunk chunk3 = chunkMap.getChunk(int4, int3 + 1);
								float float8;
								if (chunk3 != null && !isLoaded[int4 + (int3 + 1) * IsoChunkMap.ChunkGridWidth]) {
									for (int int6 = 0; int6 < byte1; ++int6) {
										float7 = IsoUtils.XToScreenExact((float)(chunk.wx * byte1 + int6), (float)(chunk.wy * byte1 + (byte1 - 1)), float5, 0);
										float8 = IsoUtils.YToScreenExact((float)(chunk.wx * byte1 + int6), (float)(chunk.wy * byte1 + (byte1 - 1)), float5, 0);
										SpriteRenderer.instance.render(texture, float7 - (float)(texture.getWidth() / 2), float8, (float)texture.getWidth(), (float)texture.getHeight(), 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
									}
								}

								IsoChunk chunk4 = chunkMap.getChunk(int4 - 1, int3);
								float float9;
								if (chunk4 != null && !isLoaded[int4 - 1 + int3 * IsoChunkMap.ChunkGridWidth]) {
									for (int int7 = 0; int7 < byte1; ++int7) {
										float8 = IsoUtils.XToScreenExact((float)(chunk.wx * byte1), (float)(chunk.wy * byte1 + int7), float5, 0);
										float9 = IsoUtils.YToScreenExact((float)(chunk.wx * byte1), (float)(chunk.wy * byte1 + int7), float5, 0);
										SpriteRenderer.instance.render(texture, float8 - (float)(texture.getWidth() / 2), float9, (float)texture.getWidth(), (float)texture.getHeight(), 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
									}
								}

								IsoChunk chunk5 = chunkMap.getChunk(int4 + 1, int3);
								if (chunk5 != null && !isLoaded[int4 + 1 + int3 * IsoChunkMap.ChunkGridWidth]) {
									for (int int8 = 0; int8 < byte1; ++int8) {
										float9 = IsoUtils.XToScreenExact((float)(chunk.wx * byte1 + (byte1 - 1)), (float)(chunk.wy * byte1 + int8), float5, 0);
										float float10 = IsoUtils.YToScreenExact((float)(chunk.wx * byte1 + (byte1 - 1)), (float)(chunk.wy * byte1 + int8), float5, 0);
										SpriteRenderer.instance.render(texture, float9 - (float)(texture.getWidth() / 2), float10, (float)texture.getWidth(), (float)texture.getHeight(), 1.0F, 1.0F, 1.0F, 1.0F, (Consumer)null);
									}
								}
							}

							if (!boolean1) {
								float float11 = (float)(chunk.wx * byte1);
								float float12 = (float)(chunk.wy * byte1);
								float6 = IsoUtils.XToScreenExact(float11, float12 + (float)byte1, float5, 0);
								float7 = IsoUtils.YToScreenExact(float11, float12 + (float)byte1, float5, 0);
								SpriteRenderer.instance.renderPoly((float)((int)float6), (float)((int)float7), (float)((int)(float6 + (float)(byte1 * 64 / 2 * int2))), (float)((int)(float7 - (float)(byte1 * 32 / 2 * int2))), (float)((int)(float6 + (float)(byte1 * 64 * int2))), (float)((int)float7), (float)((int)(float6 + (float)(byte1 * 64 / 2 * int2))), (float)((int)(float7 + (float)(byte1 * 32 / 2 * int2))), float1, float2, float3, float4);
							}
						}
					}
				}
			}
		}
	}

	public static void Reset() {
		Arrays.fill(GameClient.loadedCells, (Object)null);
		trafficCone = null;
	}
}
