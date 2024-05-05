package zombie.iso.areas.isoregion;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.isoregion.data.DataChunk;
import zombie.iso.areas.isoregion.data.DataRoot;
import zombie.iso.areas.isoregion.data.DataSquarePos;
import zombie.iso.areas.isoregion.regions.IChunkRegion;
import zombie.iso.areas.isoregion.regions.IWorldRegion;
import zombie.iso.areas.isoregion.regions.IsoWorldRegion;
import zombie.network.GameClient;
import zombie.network.GameServer;


public final class IsoRegions {
	public static final int SINGLE_CHUNK_PACKET_SIZE = 1024;
	public static final int CHUNKS_DATA_PACKET_SIZE = 65536;
	public static boolean PRINT_D = false;
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
	private static final int SQUARE_CHANGE_WARN_THRESHOLD = 20;
	private static int SQUARE_CHANGE_PER_TICK = 0;
	private static String cacheDir;
	private static File cacheDirFile;
	private static File headDataFile;
	private static final Map chunkFileNames = new HashMap();
	private static IsoRegionWorker regionWorker;
	private static DataRoot dataRoot;
	private static IsoRegionsLogger logger;
	protected static int lastChunkX = -1;
	protected static int lastChunkY = -1;
	private static byte previousFlags = 0;

	public static File getHeaderFile() {
		return headDataFile;
	}

	public static File getDirectory() {
		return cacheDirFile;
	}

	public static File getChunkFile(int int1, int int2) {
		int int3 = hash(int1, int2);
		if (chunkFileNames.containsKey(int3)) {
			File file = (File)chunkFileNames.get(int3);
			if (file != null) {
				return (File)chunkFileNames.get(int3);
			}
		}

		String string = cacheDir + "datachunk_" + int1 + "_" + int2 + ".bin";
		File file2 = new File(string);
		chunkFileNames.put(int3, file2);
		return file2;
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

	protected static DataRoot getDataRoot() {
		return dataRoot;
	}

	public static void init() {
		if (!Core.bDebug) {
			PRINT_D = false;
			DataSquarePos.DEBUG_POOL = false;
		}

		logger = new IsoRegionsLogger(PRINT_D);
		chunkFileNames.clear();
		String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("isoregiondata");
		cacheDir = string + File.separator;
		cacheDirFile = new File(cacheDir);
		if (!cacheDirFile.exists()) {
			cacheDirFile.mkdir();
		}

		String string2 = cacheDir + "RegionHeader.bin";
		headDataFile = new File(string2);
		previousFlags = 0;
		dataRoot = new DataRoot();
		regionWorker = new IsoRegionWorker();
		regionWorker.create();
		regionWorker.load();
	}

	public static IsoRegionsLogger getLogger() {
		return logger;
	}

	public static void log(String string) {
		logger.log(string);
	}

	public static void log(String string, Color color) {
		logger.log(string, color);
	}

	public static void warn(String string) {
		logger.warn(string);
	}

	public static void reset() {
		previousFlags = 0;
		regionWorker.stop();
		regionWorker = null;
		dataRoot = null;
		chunkFileNames.clear();
	}

	public static void receiveServerUpdatePacket(ByteBuffer byteBuffer) {
		if (regionWorker == null) {
			logger.warn("IsoRegion cannot receive server packet, regionWorker == null.");
		} else {
			if (GameClient.bClient) {
				regionWorker.readServerUpdatePacket(byteBuffer);
			}
		}
	}

	public static void receiveClientRequestFullDataChunks(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		if (regionWorker == null) {
			logger.warn("IsoRegion cannot receive client packet, regionWorker == null.");
		} else {
			if (GameServer.bServer) {
				regionWorker.readClientRequestFullUpdatePacket(byteBuffer, udpConnection);
			}
		}
	}

	public static void update() {
		if (Core.bDebug && SQUARE_CHANGE_PER_TICK > 20) {
			logger.warn("IsoRegion Warning -> " + SQUARE_CHANGE_PER_TICK + " squares have been changed in one tick.");
		}

		SQUARE_CHANGE_PER_TICK = 0;
		if (IsoRegionWorker.isRequestingBufferSwap.get()) {
			logger.log("IsoRegion Swapping DataRoot");
			DataRoot dataRoot = dataRoot;
			dataRoot = regionWorker.getRootBuffer();
			regionWorker.setRootBuffer(dataRoot);
			IsoRegionWorker.isRequestingBufferSwap.set(false);
			if (!GameServer.bServer) {
				clientResetCachedRegionReferences();
			}
		}

		if (!GameClient.bClient && !GameServer.bServer && DEBUG_LOAD_ALL_CHUNKS && Core.bDebug) {
			int int1 = (int)IsoPlayer.getInstance().getX() / 10;
			int int2 = (int)IsoPlayer.getInstance().getY() / 10;
			if (lastChunkX != int1 || lastChunkY != int2) {
				lastChunkX = int1;
				lastChunkY = int2;
				regionWorker.readSurroundingChunks(int1, int2, IsoChunkMap.ChunkGridWidth - 2, true);
			}
		}

		regionWorker.update();
		logger.update();
	}

	protected static void forceRecalcSurroundingChunks() {
		if (Core.bDebug && !GameClient.bClient) {
			logger.log("[DEBUG] Forcing a full load/recalculate of chunks surrounding player.", Colors.Gold);
			int int1 = (int)IsoPlayer.getInstance().getX() / 10;
			int int2 = (int)IsoPlayer.getInstance().getY() / 10;
			regionWorker.readSurroundingChunks(int1, int2, IsoChunkMap.ChunkGridWidth - 2, true, true);
		}
	}

	public static byte getSquareFlags(int int1, int int2, int int3) {
		return dataRoot.getSquareFlags(int1, int2, int3);
	}

	public static IWorldRegion getIsoWorldRegion(int int1, int int2, int int3) {
		return dataRoot.getIsoWorldRegion(int1, int2, int3);
	}

	public static DataChunk getDataChunk(int int1, int int2) {
		return dataRoot.getDataChunk(int1, int2);
	}

	public static IChunkRegion getChunkRegion(int int1, int int2, int int3) {
		return dataRoot.getIsoChunkRegion(int1, int2, int3);
	}

	public static void ResetAllDataDebug() {
		if (Core.bDebug) {
			if (!GameServer.bServer && !GameClient.bClient) {
				regionWorker.addDebugResetJob();
			}
		}
	}

	private static void clientResetCachedRegionReferences() {
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
										square.setIsoWorldRegion((IsoWorldRegion)null);
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
					++SQUARE_CHANGE_PER_TICK;
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
				if (square.Is(IsoFlagType.WindowN) || square.Is(IsoFlagType.windowN) || square.Is(IsoFlagType.DoorWallN)) {
					int1 |= 4;
				}
			}

			if (!square.Is(IsoFlagType.WallSE) && (square.Is(IsoFlagType.cutW) || square.Has(IsoObjectType.doorFrW))) {
				int1 |= 2;
				if (square.Is(IsoFlagType.WindowW) || square.Is(IsoFlagType.windowW) || square.Is(IsoFlagType.DoorWallW)) {
					int1 |= 8;
				}
			}

			if (square.HasStairsNorth() || square.HasStairsWest()) {
				int1 |= 32;
			}
		}

		return (byte)int1;
	}

	protected static IsoRegionWorker getRegionWorker() {
		return regionWorker;
	}
}
