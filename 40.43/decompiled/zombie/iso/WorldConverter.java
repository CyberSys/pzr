package zombie.iso;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import zombie.GameWindow;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionRegions;
import zombie.erosion.season.ErosionIceQueen;
import zombie.gameStates.GameLoadingState;
import zombie.gameStates.IngameState;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.CoopSlave;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.vehicles.VehicleManager;

public class WorldConverter {
   public static WorldConverter instance = new WorldConverter();
   public static boolean converting;
   int oldID = 0;
   public HashMap TilesetConversions = null;
   IsoSpriteManager sprManager;

   public void convert(String var1, IsoSpriteManager var2) throws IOException {
      this.sprManager = var2;
      File var3 = new File(GameWindow.getGameModeCacheDir() + File.separator + var1 + File.separator + "map_ver.bin");
      if (var3.exists()) {
         converting = true;
         FileInputStream var4 = new FileInputStream(var3);
         DataInputStream var5 = new DataInputStream(var4);
         int var6 = var5.readInt();
         var5.close();
         if (var6 < 143) {
            if (var6 < 24) {
               GameLoadingState.build23Stop = true;
               return;
            }

            try {
               this.convert(var1, var6, 143);
            } catch (Exception var8) {
               IngameState.createWorld(var1);
               IngameState.copyWorld(var1 + "_backup", var1);
               var8.printStackTrace();
            }
         }

         converting = false;
      }

   }

   private void convert(String var1, int var2, int var3) {
      if (!GameClient.bClient) {
         GameLoadingState.convertingWorld = true;
         String var4 = Core.GameSaveWorld;
         IngameState.createWorld(var1 + "_backup");
         IngameState.copyWorld(var1, Core.GameSaveWorld);
         Core.GameSaveWorld = var4;
         if (var3 >= 14 && var2 < 14) {
            try {
               this.convertchunks(var1, 25, 25);
            } catch (IOException var8) {
               var8.printStackTrace();
            }
         } else if (var2 == 7) {
            try {
               this.convertchunks(var1);
            } catch (IOException var7) {
               var7.printStackTrace();
            }
         }

         if (var2 <= 4) {
            this.loadconversionmap(var2, "tiledefinitions");
            this.loadconversionmap(var2, "newtiledefinitions");

            try {
               this.convertchunks(var1);
            } catch (IOException var6) {
               var6.printStackTrace();
            }
         }

         GameLoadingState.convertingWorld = false;
      }
   }

   private void convertchunks(String var1) throws IOException {
      IsoCell var2 = new IsoCell(this.sprManager, 300, 300);
      IsoChunkMap var3 = new IsoChunkMap(var2);
      File var4 = new File(GameWindow.getGameModeCacheDir() + File.separator + var1 + File.separator);
      if (!var4.exists()) {
         var4.mkdir();
      }

      String[] var5 = var4.list();
      String[] var6 = var5;
      int var7 = var5.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String var9 = var6[var8];
         if (var9.contains(".bin") && !var9.equals("map.bin") && !var9.equals("map_p.bin") && !var9.matches("map_p[0-9]+\\.bin") && !var9.equals("map_t.bin") && !var9.equals("map_c.bin") && !var9.equals("map_ver.bin") && !var9.equals("map_sand.bin") && !var9.equals("map_mov.bin") && !var9.equals("map_meta.bin") && !var9.equals("map_cm.bin") && !var9.equals("pc.bin") && !var9.startsWith("zpop_") && !var9.startsWith("chunkdata_")) {
            String[] var10 = var9.replace(".bin", "").replace("map_", "").split("_");
            int var11 = Integer.parseInt(var10[0]);
            int var12 = Integer.parseInt(var10[1]);
            var3.LoadChunkForLater(var11, var12, 0, 0);
            var3.SwapChunkBuffers();
            var3.getChunk(0, 0).Save(true);
         }
      }

   }

   private void convertchunks(String var1, int var2, int var3) throws IOException {
      IsoCell var4 = new IsoCell(this.sprManager, 300, 300);
      new IsoChunkMap(var4);
      File var6 = new File(GameWindow.getGameModeCacheDir() + File.separator + var1 + File.separator);
      if (!var6.exists()) {
         var6.mkdir();
      }

      String[] var7 = var6.list();
      IsoWorld.saveoffsetx = var2;
      IsoWorld.saveoffsety = var3;
      IsoWorld.instance.MetaGrid.Create();
      WorldStreamer.instance.create();
      String[] var8 = var7;
      int var9 = var7.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         String var11 = var8[var10];
         if (var11.contains(".bin") && !var11.equals("map.bin") && !var11.equals("map_p.bin") && !var11.matches("map_p[0-9]+\\.bin") && !var11.equals("map_t.bin") && !var11.equals("map_c.bin") && !var11.equals("map_ver.bin") && !var11.equals("map_sand.bin") && !var11.equals("map_mov.bin") && !var11.equals("map_meta.bin") && !var11.equals("map_cm.bin") && !var11.equals("pc.bin") && !var11.startsWith("zpop_") && !var11.startsWith("chunkdata_")) {
            String[] var12 = var11.replace(".bin", "").replace("map_", "").split("_");
            int var13 = Integer.parseInt(var12[0]);
            int var14 = Integer.parseInt(var12[1]);
            IsoChunk var15 = new IsoChunk(var4);
            var15.refs.add(var4.ChunkMap[0]);
            WorldStreamer.instance.addJobConvert(var15, 0, 0, var13, var14);

            while(!var15.bLoaded) {
               try {
                  Thread.sleep(20L);
               } catch (InterruptedException var18) {
                  var18.printStackTrace();
               }
            }

            var15.wx += var2 * 30;
            var15.wy += var3 * 30;
            var15.jobType = IsoChunk.JobType.Convert;
            var15.Save(true);
            File var16 = new File(GameWindow.getGameModeCacheDir() + File.separator + var1 + File.separator + var11);

            while(!ChunkSaveWorker.instance.toSaveQueue.isEmpty()) {
               try {
                  Thread.sleep(13L);
               } catch (InterruptedException var19) {
                  var19.printStackTrace();
               }
            }

            var16.delete();
         }
      }

   }

   private void loadconversionmap(int var1, String var2) {
      String var3 = "media/" + var2 + "_" + var1 + ".tiles";
      File var4 = new File(var3);
      if (var4.exists()) {
         try {
            RandomAccessFile var5 = new RandomAccessFile(var4.getAbsolutePath(), "r");
            int var6 = IsoWorld.readInt(var5);

            for(int var7 = 0; var7 < var6; ++var7) {
               Thread.sleep(4L);
               String var8 = IsoWorld.readString(var5);
               String var9 = var8.trim();
               IsoWorld.readString(var5);
               int var10 = IsoWorld.readInt(var5);
               int var11 = IsoWorld.readInt(var5);
               int var12 = IsoWorld.readInt(var5);

               for(int var13 = 0; var13 < var12; ++var13) {
                  IsoSprite var14 = (IsoSprite)this.sprManager.NamedMap.get(var9 + "_" + var13);
                  if (this.TilesetConversions == null) {
                     this.TilesetConversions = new HashMap();
                  }

                  this.TilesetConversions.put(this.oldID, var14.ID);
                  ++this.oldID;
                  int var15 = IsoWorld.readInt(var5);

                  for(int var16 = 0; var16 < var15; ++var16) {
                     var8 = IsoWorld.readString(var5);
                     String var17 = var8.trim();
                     var8 = IsoWorld.readString(var5);
                     String var18 = var8.trim();
                  }
               }
            }
         } catch (Exception var19) {
         }
      }

   }

   public void softreset(IsoSpriteManager var1) {
      this.sprManager = var1;
      String var2 = GameServer.ServerName;
      Core.GameSaveWorld = var2;
      IsoCell var3 = new IsoCell(this.sprManager, 300, 300);
      new IsoChunkMap(var3);
      File var5 = new File(GameWindow.getGameModeCacheDir() + File.separator + var2 + File.separator);
      if (!var5.exists()) {
         var5.mkdir();
      }

      String[] var6 = var5.list();
      if (CoopSlave.instance != null) {
         CoopSlave.instance.sendMessage("softreset-count", (String)null, Integer.toString(var6.length));
      }

      IsoWorld.instance.MetaGrid.Create();
      ServerMap.instance.init(IsoWorld.instance.MetaGrid);
      new ErosionIceQueen(this.sprManager);
      ErosionRegions.init();
      WorldStreamer.instance.create();
      VehicleManager.instance = new VehicleManager();
      int var7 = var6.length;
      DebugLog.log("processing " + var7 + " files");
      String[] var8 = var6;
      int var9 = var6.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         String var11 = var8[var10];
         --var7;
         if (!var11.contains("descriptors") && var11.contains(".bin") && !var11.equals("map.bin") && !var11.equals("map_p.bin") && !var11.matches("map_p[0-9]+\\.bin") && !var11.equals("map_c.bin") && !var11.equals("map_ver.bin") && !var11.equals("map_sand.bin") && !var11.equals("map_mov.bin") && !var11.equals("map_cm.bin") && !var11.equals("pc.bin") && !var11.startsWith("chunkdata_") && !var11.startsWith("gos_")) {
            File var12;
            if (var11.startsWith("zpop_")) {
               var12 = new File(GameWindow.getGameModeCacheDir() + File.separator + var2 + File.separator + var11);
               var12.delete();
            } else if (var11.equals("map_t.bin")) {
               var12 = new File(GameWindow.getGameModeCacheDir() + File.separator + var2 + File.separator + var11);
               var12.delete();
            } else if (!var11.equals("map_meta.bin") && !var11.equals("map_zone.bin")) {
               if (var11.equals("reanimated.bin")) {
                  var12 = new File(GameWindow.getGameModeCacheDir() + File.separator + var2 + File.separator + var11);
                  var12.delete();
               } else {
                  System.out.println("Soft clearing chunk: " + var11);
                  String[] var19 = var11.replace(".bin", "").replace("map_", "").split("_");
                  int var13 = Integer.parseInt(var19[0]);
                  int var14 = Integer.parseInt(var19[1]);
                  IsoChunk var15 = new IsoChunk(var3);
                  var15.refs.add(var3.ChunkMap[0]);
                  WorldStreamer.instance.addJobWipe(var15, 0, 0, var13, var14);

                  while(!var15.bLoaded) {
                     try {
                        Thread.sleep(20L);
                     } catch (InterruptedException var18) {
                        var18.printStackTrace();
                     }
                  }

                  var15.jobType = IsoChunk.JobType.Convert;
                  var15.FloorBloodSplats.clear();

                  try {
                     var15.Save(true);
                  } catch (IOException var17) {
                     var17.printStackTrace();
                  }

                  var15.doReuseGridsquares();
                  if (var7 % 100 == 0) {
                     DebugLog.log(var7 + " files to go");
                  }

                  if (CoopSlave.instance != null && var7 % 10 == 0) {
                     CoopSlave.instance.sendMessage("softreset-remaining", (String)null, Integer.toString(var7));
                  }
               }
            } else {
               var12 = new File(GameWindow.getGameModeCacheDir() + File.separator + var2 + File.separator + var11);
               var12.delete();
            }
         }
      }

      GameServer.ResetID = Rand.Next(10000000);
      ServerOptions.instance.putSaveOption("ResetID", (new Integer(GameServer.ResetID)).toString());
      IsoWorld.instance.CurrentCell = null;
      DebugLog.log("soft-reset complete, server terminated");
      if (CoopSlave.instance != null) {
         CoopSlave.instance.sendMessage("softreset-finished", (String)null, "");
      }

      SteamUtils.shutdown();
      System.exit(0);
   }
}
