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

   public static boolean HasFlags(byte var0, byte var1) {
      return (var0 & var1) == var1;
   }

   public static byte GetOppositeDir(byte var0) {
      if (var0 == 0) {
         return 2;
      } else if (var0 == 1) {
         return 3;
      } else if (var0 == 2) {
         return 0;
      } else if (var0 == 3) {
         return 1;
      } else if (var0 == 4) {
         return 5;
      } else {
         return (byte)(var0 == 5 ? 4 : -1);
      }
   }

   public static void setDebugLoadAllChunks(boolean var0) {
      DEBUG_LOAD_ALL_CHUNKS = var0;
   }

   public static boolean isDebugLoadAllChunks() {
      return DEBUG_LOAD_ALL_CHUNKS;
   }

   public static int hash(int var0, int var1) {
      return var1 << 16 ^ var0;
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

   public static void receiveServerUpdatePacket(ByteBuffer var0) {
      if (regionWorker == null) {
         DebugLog.log("IsoRegion cannot receive server packet, regionWorker == null.");
      } else {
         if (GameClient.bClient) {
            regionWorker.readServerUpdatePacket(var0);
         }

      }
   }

   public static void receiveClientRequestFullDataChunks(ByteBuffer var0, UdpConnection var1) {
      if (regionWorker == null) {
         DebugLog.log("IsoRegion cannot receive client packet, regionWorker == null.");
      } else {
         if (GameServer.bServer) {
            regionWorker.readClientRequestFullUpdatePacket(var0, var1);
         }

      }
   }

   public static void update() {
      if (IsoRegionWorker.isRequestingBufferSwap.get()) {
         DebugLog.log("IsoRegion Swapping DataRoot");
         DataRoot var0 = dataRoot;
         dataRoot = regionWorker.getRootBuffer();
         regionWorker.setRootBuffer(var0);
         IsoRegionWorker.isRequestingBufferSwap.set(false);
         if (!GameServer.bServer) {
            clientResetDataCacheForChunks();
         }
      }

      if (!GameClient.bClient && !GameServer.bServer && DEBUG_LOAD_ALL_CHUNKS) {
         int var2 = (int)IsoPlayer.instance.getX() / 10;
         int var1 = (int)IsoPlayer.instance.getY() / 10;
         if (lastChunkX != var2 || lastChunkY != var1) {
            lastChunkX = var2;
            lastChunkY = var1;
            regionWorker.readSurroundingChunks(var2, var1, IsoChunkMap.ChunkGridWidth - 2, true);
         }
      }

      regionWorker.update();
   }

   public static byte getSquareFlags(int var0, int var1, int var2) {
      return dataRoot.getDataSquare(var0, var1, var2);
   }

   public static MasterRegion getMasterRegion(int var0, int var1, int var2) {
      return dataRoot.getMasterRegion(var0, var1, var2);
   }

   public static DataChunk getDataChunk(int var0, int var1) {
      return dataRoot.getDataChunk(var0, var1);
   }

   public static ChunkRegion getChunkRegion(int var0, int var1, int var2) {
      return dataRoot.getChunkRegion(var0, var1, var2);
   }

   public static void ResetAllDataDebug() {
      if (!GameServer.bServer && !GameClient.bClient) {
         regionWorker.addDebugResetJob();
      }
   }

   private static void clientResetDataCacheForChunks() {
      if (!GameServer.bServer) {
         byte var0 = 0;
         byte var1 = 0;
         int var2 = IsoChunkMap.ChunkGridWidth;
         int var3 = IsoChunkMap.ChunkGridWidth;
         IsoChunkMap var4 = IsoWorld.instance.getCell().getChunkMap(IsoPlayer.getPlayerIndex());
         if (var4 != null) {
            for(int var7 = var0; var7 < var2; ++var7) {
               for(int var8 = var1; var8 < var3; ++var8) {
                  IsoChunk var5 = var4.getChunk(var7, var8);
                  if (var5 != null) {
                     for(int var9 = 0; var9 <= var5.maxLevel; ++var9) {
                        for(int var10 = 0; var10 < var5.squares[0].length; ++var10) {
                           IsoGridSquare var6 = var5.squares[var9][var10];
                           if (var6 != null) {
                              var6.setMasterRegion((MasterRegion)null);
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   public static void setPreviousFlags(IsoGridSquare var0) {
      previousFlags = calculateSquareFlags(var0);
   }

   public static void squareChanged(IsoGridSquare var0) {
      squareChanged(var0, false);
   }

   public static void squareChanged(IsoGridSquare var0, boolean var1) {
      if (!GameClient.bClient) {
         if (var0 != null) {
            byte var2 = calculateSquareFlags(var0);
            if (var2 != previousFlags) {
               regionWorker.addSquareChangedJob(var0.getX(), var0.getY(), var0.getZ(), var1, var2);
               previousFlags = 0;
            }
         }
      }
   }

   protected static byte calculateSquareFlags(IsoGridSquare var0) {
      int var1 = 0;
      if (var0 != null) {
         if (var0.Is(IsoFlagType.solidfloor)) {
            var1 |= 16;
         }

         if (var0.Is(IsoFlagType.cutN) || var0.Has(IsoObjectType.doorFrN)) {
            var1 |= 1;
            if (var0.Is(IsoFlagType.WindowN) || var0.Is(IsoFlagType.windowN) || var0.Is("DoorWallN")) {
               var1 |= 4;
            }
         }

         if (!var0.Is(IsoFlagType.WallSE) && (var0.Is(IsoFlagType.cutW) || var0.Has(IsoObjectType.doorFrW))) {
            var1 |= 2;
            if (var0.Is(IsoFlagType.WindowW) || var0.Is(IsoFlagType.windowW) || var0.Is("DoorWallW")) {
               var1 |= 8;
            }
         }

         if (var0.HasStairsNorth() || var0.HasStairsWest()) {
            var1 |= 32;
         }
      }

      return (byte)var1;
   }
}
