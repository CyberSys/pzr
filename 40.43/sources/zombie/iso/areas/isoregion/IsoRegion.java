package zombie.iso.areas.isoregion;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class IsoRegion {
	public static boolean PRINT_D = true;
	public static final int CELL_DIM = 300;
	public static final int CELL_CHUNK_DIM = 30;
	public static final int CHUNK_DIM = 10;
	public static final int CHUNK_MAX_Z = 8;
	public static final byte BIT_EMPTY = 0;
	public static final byte BIT_WALL_N = 1;
	public static final byte BIT_WALL_W = 2;
	public static final byte BIT_PATH_WALL_N = 4;
	public static final byte BIT_PATH_WALL_W = 8;
	public static final byte BIT_HAS_FLOOR = 16;
	public static final byte BIT_STAIRCASE = 32;
	public static final byte BIT_HAS_ROOF = 64;
	public static final byte DIR_NONE = -1;
	public static final byte DIR_N = 0;
	public static final byte DIR_W = 1;
	public static final byte DIR_2D_NW = 2;
	public static final byte DIR_S = 2;
	public static final byte DIR_E = 3;
	public static final byte DIR_2D_MAX = 4;
	public static final byte DIR_TOP = 4;
	public static final byte DIR_BOT = 5;
	public static final byte DIR_MAX = 6;
	protected static final int CHUNK_LOAD_DIMENSIONS = 7;
	protected static boolean DEBUG_LOAD_ALL_CHUNKS = false;
	public static final String FILE_PRE = "datachunk_";
	public static final String FILE_SEP = "_";
	public static final String FILE_EXT = ".bin";
	public static final String FILE_DIR = "isoregiondata";
	private static IsoRegionWorker regionWorker;
	private static DataRoot dataRoot;
	protected static int lastChunkX = -1;
	protected static int lastChunkY = -1;
	private static byte previousFlags = 0;

	public static boolean HasFlags(byte byte1, byte byte2) {
		return (byte1 & byte2) == byte2;
	}

	public static byte GetOppositeDir(byte byte1) {
		if (byte1 == 0) {
			return 2;
		} else if (byte1 == 1) {
			return 3;
		} else if (byte1 == 2) {
			return 0;
		} else if (byte1 == 3) {
			return 1;
		} else if (byte1 == 4) {
			return 5;
		} else {
			return (byte)(byte1 == 5 ? 4 : -1);
		}
	}

	public static void setDebugLoadAllChunks(boolean boolean1) {
		DEBUG_LOAD_ALL_CHUNKS = boolean1;
	}

	public static boolean isDebugLoadAllChunks() {
		return DEBUG_LOAD_ALL_CHUNKS;
	}

	public static int hash(int int1, int int2) {
		return int2 << 16 ^ int1;
	}

	public static void init() {
		dataRoot = new DataRoot();
		regionWorker = new IsoRegionWorker();
		regionWorker.create();
		regionWorker.load();
	}

	public static void reset() {
		previousFlags = 0;
		regionWorker.stop();
		regionWorker = null;
		dataRoot = null;
	}

	public static void receiveServerUpdatePacket(ByteBuffer byteBuffer) {
		if (regionWorker == null) {
			DebugLog.log("IsoRegion cannot receive server packet, regionWorker == null.");
		} else {
			if (GameClient.bClient) {
				regionWorker.readServerUpdatePacket(byteBuffer);
			}
		}
	}

	public static void receiveClientRequestFullDataChunks(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (regionWorker == null) {
			DebugLog.log("IsoRegion cannot receive client packet, regionWorker == null.");
		} else {
			if (GameServer.bServer) {
				regionWorker.readClientRequestFullUpdatePacket(byteBuffer, udpConnection);
			}
		}
	}

	public static void update() {
		if (IsoRegionWorker.isRequestingBufferSwap.get()) {
			DebugLog.log("IsoRegion Swapping DataRoot");
			DataRoot dataRoot = dataRoot;
			dataRoot = regionWorker.getRootBuffer();
			regionWorker.setRootBuffer(dataRoot);
			IsoRegionWorker.isRequestingBufferSwap.set(false);
			if (!GameServer.bServer) {
				clientResetDataCacheForChunks();
			}
		}

		if (!GameClient.bClient && !GameServer.bServer && DEBUG_LOAD_ALL_CHUNKS) {
			int int1 = (int)IsoPlayer.instance.getX() / 10;
			int int2 = (int)IsoPlayer.instance.getY() / 10;
			if (lastChunkX != int1 || lastChunkY != int2) {
				lastChunkX = int1;
				lastChunkY = int2;
				regionWorker.readSurroundingChunks(int1, int2, IsoChunkMap.ChunkGridWidth - 2, true);
			}
		}

		regionWorker.update();
	}

	public static byte getSquareFlags(int int1, int int2, int int3) {
		return dataRoot.getDataSquare(int1, int2, int3);
	}

	public static MasterRegion getMasterRegion(int int1, int int2, int int3) {
		return dataRoot.getMasterRegion(int1, int2, int3);
	}

	public static DataChunk getDataChunk(int int1, int int2) {
		return dataRoot.getDataChunk(int1, int2);
	}

	public static ChunkRegion getChunkRegion(int int1, int int2, int int3) {
		return dataRoot.getChunkRegion(int1, int2, int3);
	}

	public static void ResetAllDataDebug() {
		if (!GameServer.bServer && !GameClient.bClient) {
			regionWorker.addDebugResetJob();
		}
	}

	private static void clientResetDataCacheForChunks() {
		if (!GameServer.bServer) {
			byte byte1 = 0;
			byte byte2 = 0;
			int int1 = IsoChunkMap.ChunkGridWidth;
			int int2 = IsoChunkMap.ChunkGridWidth;
			IsoChunkMap chunkMap = IsoWorld.instance.getCell().getChunkMap(IsoPlayer.getPlayerIndex());
			if (chunkMap != null) {
				for (int int3 = byte1; int3 < int1; ++int3) {
					for (int int4 = byte2; int4 < int2; ++int4) {
						IsoChunk chunk = chunkMap.getChunk(int3, int4);
						if (chunk != null) {
							for (int int5 = 0; int5 <= chunk.maxLevel; ++int5) {
								for (int int6 = 0; int6 < chunk.squares[0].length; ++int6) {
									IsoGridSquare square = chunk.squares[int5][int6];
									if (square != null) {
										square.setMasterRegion((MasterRegion)null);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static void setPreviousFlags(IsoGridSquare square) {
		previousFlags = calculateSquareFlags(square);
	}

	public static void squareChanged(IsoGridSquare square) {
		squareChanged(square, false);
	}

	public static void squareChanged(IsoGridSquare square, boolean boolean1) {
		if (!GameClient.bClient) {
			if (square != null) {
				byte byte1 = calculateSquareFlags(square);
				if (byte1 != previousFlags) {
					regionWorker.addSquareChangedJob(square.getX(), square.getY(), square.getZ(), boolean1, byte1);
					previousFlags = 0;
				}
			}
		}
	}

	protected static byte calculateSquareFlags(IsoGridSquare square) {
		int int1 = 0;
		if (square != null) {
			if (square.Is(IsoFlagType.solidfloor)) {
				int1 |= 16;
			}

			if (square.Is(IsoFlagType.cutN) || square.Has(IsoObjectType.doorFrN)) {
				int1 |= 1;
				if (square.Is(IsoFlagType.WindowN) || square.Is(IsoFlagType.windowN) || square.Is("DoorWallN")) {
					int1 |= 4;
				}
			}

			if (!square.Is(IsoFlagType.WallSE) && (square.Is(IsoFlagType.cutW) || square.Has(IsoObjectType.doorFrW))) {
				int1 |= 2;
				if (square.Is(IsoFlagType.WindowW) || square.Is(IsoFlagType.windowW) || square.Is("DoorWallW")) {
					int1 |= 8;
				}
			}

			if (square.HasStairsNorth() || square.HasStairsWest()) {
				int1 |= 32;
			}
		}

		return (byte)int1;
	}
}
