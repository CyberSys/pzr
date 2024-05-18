package zombie.popman;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import zombie.GameTime;
import zombie.MapCollisionData;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.VirtualZombieManager;
import zombie.WorldSoundManager;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.WalkTowardState;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;

public final class ZombiePopulationManager {
   public static final ZombiePopulationManager instance = new ZombiePopulationManager();
   protected static final int SQUARES_PER_CHUNK = 10;
   protected static final int CHUNKS_PER_CELL = 30;
   protected static final int SQUARES_PER_CELL = 300;
   protected static final byte ZOMBIE_DEAD = 1;
   protected static final byte ZOMBIE_FAKE_DEAD = 2;
   protected static final byte ZOMBIE_CRAWLER = 3;
   protected static final byte ZOMBIE_WALKER = 4;
   protected int minX;
   protected int minY;
   protected int width;
   protected int height;
   protected boolean bStopped;
   protected boolean bClient;
   private final DebugCommands dbgCommands = new DebugCommands();
   private final LoadedAreas loadedAreas = new LoadedAreas(false);
   private final LoadedAreas loadedServerCells = new LoadedAreas(true);
   private PlayerSpawns playerSpawns = new PlayerSpawns();
   private short[] realZombieCount;
   private short[] realZombieCount2;
   private long realZombieUpdateTime = 0L;
   private final ArrayList saveRealZombieHack = new ArrayList();
   private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
   public float[] radarXY;
   public int radarCount;
   public boolean radarRenderFlag;
   public boolean radarRequestFlag;

   public static void init() {
      String var0 = "";
      if ("1".equals(System.getProperty("zomboid.debuglibs.popman"))) {
         DebugLog.log("***** Loading debug version of PZPopMan");
         var0 = "d";
      }

      if (System.getProperty("os.name").contains("OS X")) {
         System.loadLibrary("PZPopMan");
      } else if (System.getProperty("sun.arch.data.model").equals("64")) {
         System.loadLibrary("PZPopMan64" + var0);
      } else {
         System.loadLibrary("PZPopMan32" + var0);
      }

   }

   private static native void n_init(boolean var0, boolean var1, int var2, int var3, int var4, int var5);

   private static native void n_config(float var0, float var1, float var2, int var3, float var4, float var5, float var6, float var7, int var8);

   private static native void n_updateMain(float var0, double var1);

   private static native boolean n_hasDataForThread();

   private static native void n_updateThread();

   private static native boolean n_shouldWait();

   private static native void n_beginSaveRealZombies(int var0);

   private static native void n_saveRealZombies(int var0, ByteBuffer var1);

   private static native void n_save();

   private static native void n_stop();

   private static native void n_addZombie(float var0, float var1, float var2, byte var3, short var4, byte var5, int var6, int var7);

   private static native void n_aggroTarget(int var0, int var1, int var2);

   private static native void n_loadChunk(int var0, int var1, boolean var2);

   private static native void n_loadedAreas(int var0, int[] var1, boolean var2);

   protected static native void n_realZombieCount(short var0, short[] var1);

   protected static native void n_spawnHorde(int var0, int var1, int var2, int var3, float var4, float var5, int var6);

   private static native void n_worldSound(int var0, int var1, int var2, int var3);

   private static native int n_getAddZombieCount();

   private static native int n_getAddZombieData(int var0, ByteBuffer var1);

   private static native boolean n_hasRadarData();

   private static native void n_requestRadarData();

   private static native int n_getRadarZombieData(float[] var0);

   private static void noise(String var0) {
   }

   public void init(IsoMetaGrid var1) {
      this.bClient = GameClient.bClient;
      if (!this.bClient) {
         this.minX = var1.getMinX();
         this.minY = var1.getMinY();
         this.width = var1.getWidth();
         this.height = var1.getHeight();
         this.bStopped = false;
         n_init(this.bClient, GameServer.bServer, this.minX, this.minY, this.width, this.height);
         this.onConfigReloaded();
      }
   }

   public void onConfigReloaded() {
      SandboxOptions.ZombieConfig var1 = SandboxOptions.instance.zombieConfig;
      n_config((float)var1.PopulationMultiplier.getValue(), (float)var1.PopulationStartMultiplier.getValue(), (float)var1.PopulationPeakMultiplier.getValue(), var1.PopulationPeakDay.getValue(), (float)var1.RespawnHours.getValue(), (float)var1.RespawnUnseenHours.getValue(), (float)var1.RespawnMultiplier.getValue() * 100.0F, (float)var1.RedistributeHours.getValue(), var1.FollowSoundDistance.getValue());
   }

   public void playerSpawnedAt(int var1, int var2, int var3) {
      this.playerSpawns.addSpawn(var1, var2, var3);
   }

   public void addChunkToWorld(IsoChunk var1) {
      if (!this.bClient) {
         n_loadChunk(var1.wx, var1.wy, true);
      }
   }

   public void removeChunkFromWorld(IsoChunk var1) {
      if (!this.bClient) {
         if (!this.bStopped) {
            n_loadChunk(var1.wx, var1.wy, false);

            for(int var2 = 0; var2 < 8; ++var2) {
               for(int var3 = 0; var3 < 10; ++var3) {
                  for(int var4 = 0; var4 < 10; ++var4) {
                     IsoGridSquare var5 = var1.getGridSquare(var4, var3, var2);
                     if (var5 != null && !var5.getMovingObjects().isEmpty()) {
                        for(int var6 = 0; var6 < var5.getMovingObjects().size(); ++var6) {
                           IsoMovingObject var7 = (IsoMovingObject)var5.getMovingObjects().get(var6);
                           if (var7 instanceof IsoZombie) {
                              IsoZombie var8 = (IsoZombie)var7;
                              if ((!GameServer.bServer || !var8.bIndoorZombie) && !var8.isReanimatedPlayer()) {
                                 byte var9 = 4;
                                 if (var8.isFakeDead()) {
                                    var9 = 2;
                                 } else if (var8.bCrawling) {
                                    var9 = 3;
                                 }

                                 if (var2 == 0 && var5.getRoom() == null && (var8.getCurrentState() == WalkTowardState.instance() || var8.getCurrentState() == PathFindState.instance())) {
                                    n_addZombie(var8.x, var8.y, var8.z, (byte)var8.dir.index(), GameServer.bServer ? (short)var8.getDescriptor().getID() : 0, var9, var8.getPathTargetX(), var8.getPathTargetY());
                                 } else {
                                    n_addZombie(var8.x, var8.y, var8.z, (byte)var8.dir.index(), GameServer.bServer ? (short)var8.getDescriptor().getID() : 0, var9, -1, -1);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (GameServer.bServer) {
               MapCollisionData.instance.notifyThread();
            }

         }
      }
   }

   public void virtualizeZombie(IsoZombie var1) {
      byte var2 = 4;
      if (var1.isFakeDead()) {
         var2 = 2;
      } else if (var1.bCrawling) {
         var2 = 3;
      }

      n_addZombie(var1.x, var1.y, var1.z, (byte)var1.dir.index(), GameServer.bServer ? (short)var1.getDescriptor().getID() : 0, var2, var1.getPathTargetX(), var1.getPathTargetY());
      var1.removeFromWorld();
      var1.removeFromSquare();
   }

   public void setAggroTarget(int var1, int var2, int var3) {
      n_aggroTarget(var1, var2, var3);
   }

   public void createHordeFromTo(int var1, int var2, int var3, int var4, int var5) {
      n_spawnHorde(var1, var2, 0, 0, (float)var3, (float)var4, var5);
   }

   public void createHordeInAreaTo(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      n_spawnHorde(var1, var2, var3, var4, (float)var5, (float)var6, var7);
   }

   public void addWorldSound(int var1, int var2, int var3, int var4) {
      if (!this.bClient) {
         n_worldSound(var1, var2, var3, var4);
      }
   }

   public void addWorldSound(WorldSoundManager.WorldSound var1) {
      if (!this.bClient) {
         if (var1.radius >= 50) {
            if (!var1.sourceIsZombie) {
               n_worldSound(var1.x, var1.y, var1.radius, var1.volume);
            }
         }
      }
   }

   private void updateRealZombieCount() {
      if (this.realZombieCount == null || this.realZombieCount.length != this.width * this.height) {
         this.realZombieCount = new short[this.width * this.height];
         this.realZombieCount2 = new short[this.width * this.height * 2];
      }

      Arrays.fill(this.realZombieCount, (short)0);
      ArrayList var1 = IsoWorld.instance.CurrentCell.getZombieList();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         IsoZombie var3 = (IsoZombie)var1.get(var2);
         int var4 = (int)(var3.x / 300.0F) - this.minX;
         int var5 = (int)(var3.y / 300.0F) - this.minY;
         ++this.realZombieCount[var4 + var5 * this.width];
      }

      short var6 = 0;

      for(int var7 = 0; var7 < this.width * this.height; ++var7) {
         if (this.realZombieCount[var7] > 0) {
            this.realZombieCount2[var6 * 2 + 0] = (short)var7;
            this.realZombieCount2[var6 * 2 + 1] = this.realZombieCount[var7];
            ++var6;
         }
      }

      n_realZombieCount(var6, this.realZombieCount2);
   }

   public void updateMain() {
      if (!this.bClient) {
         long var1 = System.currentTimeMillis();
         n_updateMain(GameTime.getInstance().getMultiplier(), GameTime.getInstance().getWorldAgeHours());
         int var3 = 0;
         int var4 = 0;
         int var5 = n_getAddZombieCount();
         int var6 = 0;

         while(var6 < var5) {
            this.byteBuffer.clear();
            int var7 = n_getAddZombieData(var6, this.byteBuffer);
            var6 += var7;

            for(int var8 = 0; var8 < var7; ++var8) {
               float var9 = this.byteBuffer.getFloat();
               float var10 = this.byteBuffer.getFloat();
               float var11 = this.byteBuffer.getFloat();
               IsoDirections var12 = IsoDirections.fromIndex(this.byteBuffer.get());
               short var13 = this.byteBuffer.getShort();
               byte var14 = this.byteBuffer.get();
               int var15 = this.byteBuffer.getInt();
               int var16 = this.byteBuffer.getInt();
               if (GameServer.bServer && SharedDescriptors.getDescriptor(var13) == null) {
                  var13 = (short)SharedDescriptors.pickRandomDescriptorID();
               }

               if (var15 == -1) {
                  this.addZombieStanding(var9, var10, var11, var12, var13, var14);
                  ++var3;
               } else {
                  this.addZombieMoving(var9, var10, var11, var12, var13, var14, var15, var16);
                  ++var4;
               }
            }
         }

         if (var3 > 0) {
            noise("unloaded -> real " + var5);
         }

         if (var4 > 0) {
            noise("virtual -> real " + var5);
         }

         if (this.radarRenderFlag && this.radarXY != null) {
            if (this.radarRequestFlag) {
               if (n_hasRadarData()) {
                  this.radarCount = n_getRadarZombieData(this.radarXY);
                  this.radarRenderFlag = false;
                  this.radarRequestFlag = false;
               }
            } else {
               n_requestRadarData();
               this.radarRequestFlag = true;
            }
         }

         this.updateLoadedAreas();
         if (this.realZombieUpdateTime + 5000L < var1) {
            this.realZombieUpdateTime = var1;
            this.updateRealZombieCount();
         }

         if (GameServer.bServer) {
            MPDebugInfo.instance.serverUpdate();
         }

         boolean var17 = n_hasDataForThread();
         boolean var18 = MapCollisionData.instance.hasDataForThread();
         if (var17 || var18) {
            MapCollisionData.instance.notifyThread();
         }

         this.playerSpawns.update();
      }
   }

   private void addZombieStanding(float var1, float var2, float var3, IsoDirections var4, short var5, byte var6) {
      IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare((int)var1, (int)var2, (int)var3);
      if (var7 != null) {
         label45: {
            if (var7.SolidFloorCached) {
               if (!var7.SolidFloor) {
                  break label45;
               }
            } else if (!var7.TreatAsSolidFloor()) {
               break label45;
            }

            if (!this.playerSpawns.allowZombie(var7)) {
               noise("removed zombie near player spawn " + (int)var1 + "," + (int)var2 + "," + (int)var3);
               return;
            }

            VirtualZombieManager.instance.choices.clear();
            VirtualZombieManager.instance.choices.add(var7);
            IsoZombie var8;
            if (GameServer.bServer) {
               var8 = VirtualZombieManager.instance.createRealZombieAlways(var5, var4.index(), false);
            } else {
               var8 = VirtualZombieManager.instance.createRealZombieAlways(var4.index(), false);
            }

            if (var8 != null) {
               var8.setX(var1);
               var8.setY(var2);
               if (var6 == 2) {
                  var8.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
                  var8.sprite = var8.legsSprite;
                  var8.changeState(FakeDeadZombieState.instance());
               } else if (var6 == 3) {
                  var8.bCrawling = true;
                  var8.setOnFloor(true);
                  var8.walkVariant = "ZombieWalk";
                  var8.DoZombieStats();
                  return;
               }

               return;
            }

            return;
         }
      }

      noise("real -> unloaded");
      n_addZombie(var1, var2, var3, (byte)var4.index(), var5, var6, -1, -1);
   }

   private void addZombieMoving(float var1, float var2, float var3, IsoDirections var4, short var5, byte var6, int var7, int var8) {
      IsoGridSquare var9 = IsoWorld.instance.CurrentCell.getGridSquare((int)var1, (int)var2, (int)var3);
      if (var9 != null) {
         label47: {
            if (var9.SolidFloorCached) {
               if (!var9.SolidFloor) {
                  break label47;
               }
            } else if (!var9.TreatAsSolidFloor()) {
               break label47;
            }

            VirtualZombieManager.instance.choices.clear();
            VirtualZombieManager.instance.choices.add(var9);
            IsoZombie var10;
            if (GameServer.bServer) {
               var10 = VirtualZombieManager.instance.createRealZombieAlways(var5, var4.index(), false);
            } else {
               var10 = VirtualZombieManager.instance.createRealZombieAlways(var4.index(), false);
            }

            if (var10 != null) {
               var10.setX(var1);
               var10.setY(var2);
               if (var6 == 3) {
                  var10.bCrawling = true;
                  var10.setOnFloor(true);
                  var10.walkVariant = "ZombieWalk";
                  var10.DoZombieStats();
               }

               if (Math.abs((float)var7 - var1) > 1.0F || Math.abs((float)var8 - var2) > 1.0F) {
                  var10.AllowRepathDelay = -1.0F;
                  var10.pathToLocation(var7, var8, 0);
                  return;
               }
            }

            return;
         }
      }

      noise("real -> virtual " + var1 + "," + var2);
      n_addZombie(var1, var2, var3, (byte)var4.index(), var5, var6, var7, var8);
   }

   public void updateThread() {
      n_updateThread();
   }

   public boolean shouldWait() {
      synchronized(MapCollisionData.instance.renderLock) {
         return n_shouldWait();
      }
   }

   public void updateLoadedAreas() {
      if (this.loadedAreas.set()) {
         n_loadedAreas(this.loadedAreas.count, this.loadedAreas.areas, false);
      }

      if (GameServer.bServer && this.loadedServerCells.set()) {
         n_loadedAreas(this.loadedServerCells.count, this.loadedServerCells.areas, true);
      }

   }

   public void dbgSpawnTimeToZero(int var1, int var2) {
      if (!this.bClient || GameClient.accessLevel.equals("admin")) {
         this.dbgCommands.SpawnTimeToZero(var1, var2);
      }
   }

   public void dbgClearZombies(int var1, int var2) {
      if (!this.bClient || GameClient.accessLevel.equals("admin")) {
         this.dbgCommands.ClearZombies(var1, var2);
      }
   }

   public void dbgSpawnNow(int var1, int var2) {
      if (!this.bClient || GameClient.accessLevel.equals("admin")) {
         this.dbgCommands.SpawnNow(var1, var2);
      }
   }

   public void beginSaveRealZombies() {
      if (!this.bClient) {
         this.saveRealZombieHack.clear();
         ArrayList var1 = IsoWorld.instance.CurrentCell.getZombieList();

         int var2;
         for(var2 = 0; var2 < var1.size(); ++var2) {
            IsoZombie var3 = (IsoZombie)var1.get(var2);
            if (!var3.isReanimatedPlayer() && (!GameServer.bServer || !var3.bIndoorZombie)) {
               this.saveRealZombieHack.add(var3);
            }
         }

         var2 = this.saveRealZombieHack.size();
         n_beginSaveRealZombies(var2);

         int var4;
         for(int var9 = 0; var9 < var2; n_saveRealZombies(var4, this.byteBuffer)) {
            this.byteBuffer.clear();
            var4 = 0;

            while(var9 < var2) {
               int var5 = this.byteBuffer.position();
               IsoZombie var6 = (IsoZombie)this.saveRealZombieHack.get(var9++);
               this.byteBuffer.putFloat(var6.x);
               this.byteBuffer.putFloat(var6.y);
               this.byteBuffer.putFloat(var6.z);
               this.byteBuffer.put((byte)var6.dir.index());
               this.byteBuffer.putShort(GameServer.bServer ? (short)var6.getDescriptor().getID() : 0);
               byte var7;
               if (var6.isFakeDead()) {
                  var7 = 2;
               } else if (var6.bCrawling) {
                  var7 = 3;
               } else {
                  var7 = 4;
               }

               this.byteBuffer.put(var7);
               ++var4;
               int var8 = this.byteBuffer.position() - var5;
               if (this.byteBuffer.position() + var8 > this.byteBuffer.capacity()) {
                  break;
               }
            }
         }

         this.saveRealZombieHack.clear();
      }
   }

   public void endSaveRealZombies() {
      if (!this.bClient) {
         ;
      }
   }

   public void save() {
      if (!this.bClient) {
         n_save();
      }
   }

   public void stop() {
      if (!this.bClient) {
         this.bStopped = true;
         n_stop();
         this.loadedAreas.clear();
         this.radarXY = null;
         this.radarCount = 0;
         this.radarRenderFlag = false;
         this.radarRequestFlag = false;
      }
   }
}
