package zombie.popman;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;

public final class MPDebugInfo {
   public static final MPDebugInfo instance = new MPDebugInfo();
   private final ArrayList loadedCells = new ArrayList();
   private final ObjectPool cellPool = new ObjectPool() {
      protected MPDebugInfo.MPCell makeObject() {
         return new MPDebugInfo.MPCell();
      }
   };
   private final LoadedAreas loadedAreas = new LoadedAreas(false);
   public ArrayList repopEvents = new ArrayList();
   private final ObjectPool repopEventPool = new ObjectPool() {
      protected MPDebugInfo.MPRepopEvent makeObject() {
         return new MPDebugInfo.MPRepopEvent();
      }
   };
   private short repopEpoch = 0;
   private long requestTime = 0L;
   private boolean requestFlag = false;
   private boolean requestPacketReceived = false;
   private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
   private float RESPAWN_EVERY_HOURS = 1.0F;
   private float REPOP_DISPLAY_HOURS = 0.5F;

   private static native boolean n_hasData(boolean var0);

   private static native void n_requestData();

   private static native int n_getLoadedCellsCount();

   private static native int n_getLoadedCellsData(int var0, ByteBuffer var1);

   private static native int n_getLoadedAreasCount();

   private static native int n_getLoadedAreasData(int var0, ByteBuffer var1);

   private static native int n_getRepopEventCount();

   private static native int n_getRepopEventData(int var0, ByteBuffer var1);

   private void requestServerInfo() {
      if (GameClient.bClient) {
         long var1 = System.currentTimeMillis();
         if (this.requestTime + 1000L <= var1) {
            this.requestTime = var1;
            ByteBufferWriter var3 = GameClient.connection.startPacket();
            PacketTypes.doPacket((short)4, var3);
            var3.bb.put((byte)1);
            var3.bb.putShort(this.repopEpoch);
            GameClient.connection.endPacket();
         }
      }
   }

   public void clientPacket(ByteBuffer var1) {
      if (GameClient.bClient) {
         byte var2 = var1.get();
         short var3;
         int var4;
         if (var2 == 1) {
            this.cellPool.release(this.loadedCells);
            this.loadedCells.clear();
            this.RESPAWN_EVERY_HOURS = var1.getFloat();
            var3 = var1.getShort();

            for(var4 = 0; var4 < var3; ++var4) {
               MPDebugInfo.MPCell var5 = (MPDebugInfo.MPCell)this.cellPool.alloc();
               var5.cx = var1.getShort();
               var5.cy = var1.getShort();
               var5.currentPopulation = var1.getShort();
               var5.desiredPopulation = var1.getShort();
               var5.lastRepopTime = var1.getFloat();
               this.loadedCells.add(var5);
            }

            this.loadedAreas.clear();
            short var10 = var1.getShort();

            for(int var11 = 0; var11 < var10; ++var11) {
               short var6 = var1.getShort();
               short var7 = var1.getShort();
               short var8 = var1.getShort();
               short var9 = var1.getShort();
               this.loadedAreas.add(var6, var7, var8, var9);
            }
         }

         if (var2 == 2) {
            this.repopEventPool.release(this.repopEvents);
            this.repopEvents.clear();
            this.repopEpoch = var1.getShort();
            var3 = var1.getShort();

            for(var4 = 0; var4 < var3; ++var4) {
               MPDebugInfo.MPRepopEvent var12 = (MPDebugInfo.MPRepopEvent)this.repopEventPool.alloc();
               var12.wx = var1.getShort();
               var12.wy = var1.getShort();
               var12.worldAge = var1.getFloat();
               this.repopEvents.add(var12);
            }
         }

      }
   }

   public void serverPacket(ByteBuffer var1, UdpConnection var2) {
      if (GameServer.bServer) {
         if (var2.accessLevel.equals("admin")) {
            byte var3 = var1.get();
            short var4;
            if (var3 == 1) {
               this.requestTime = System.currentTimeMillis();
               this.requestPacketReceived = true;
               var4 = var1.getShort();
               ByteBufferWriter var5 = var2.startPacket();
               PacketTypes.doPacket((short)4, var5);
               var5.bb.put((byte)1);
               var5.bb.putFloat(this.RESPAWN_EVERY_HOURS);
               var5.bb.putShort((short)this.loadedCells.size());

               int var6;
               for(var6 = 0; var6 < this.loadedCells.size(); ++var6) {
                  MPDebugInfo.MPCell var7 = (MPDebugInfo.MPCell)this.loadedCells.get(var6);
                  var5.bb.putShort(var7.cx);
                  var5.bb.putShort(var7.cy);
                  var5.bb.putShort(var7.currentPopulation);
                  var5.bb.putShort(var7.desiredPopulation);
                  var5.bb.putFloat(var7.lastRepopTime);
               }

               var5.bb.putShort((short)this.loadedAreas.count);

               for(var6 = 0; var6 < this.loadedAreas.count; ++var6) {
                  int var12 = var6 * 4;
                  var5.bb.putShort((short)this.loadedAreas.areas[var12++]);
                  var5.bb.putShort((short)this.loadedAreas.areas[var12++]);
                  var5.bb.putShort((short)this.loadedAreas.areas[var12++]);
                  var5.bb.putShort((short)this.loadedAreas.areas[var12++]);
               }

               if (var4 != this.repopEpoch) {
                  var3 = 2;
               }

               var2.endPacket();
            }

            if (var3 != 2) {
               short var10;
               if (var3 == 3) {
                  var4 = var1.getShort();
                  var10 = var1.getShort();
                  ZombiePopulationManager.instance.dbgSpawnTimeToZero(var4, var10);
               } else if (var3 == 4) {
                  var4 = var1.getShort();
                  var10 = var1.getShort();
                  ZombiePopulationManager.instance.dbgClearZombies(var4, var10);
               } else if (var3 == 5) {
                  var4 = var1.getShort();
                  var10 = var1.getShort();
                  ZombiePopulationManager.instance.dbgSpawnNow(var4, var10);
               }
            } else {
               ByteBufferWriter var8 = var2.startPacket();
               PacketTypes.doPacket((short)4, var8);
               var8.bb.put((byte)2);
               var8.bb.putShort(this.repopEpoch);
               var8.bb.putShort((short)this.repopEvents.size());

               for(int var9 = 0; var9 < this.repopEvents.size(); ++var9) {
                  MPDebugInfo.MPRepopEvent var11 = (MPDebugInfo.MPRepopEvent)this.repopEvents.get(var9);
                  var8.bb.putShort((short)var11.wx);
                  var8.bb.putShort((short)var11.wy);
                  var8.bb.putFloat(var11.worldAge);
               }

               var2.endPacket();
            }
         }
      }
   }

   public void request() {
      if (GameServer.bServer) {
         this.requestTime = System.currentTimeMillis();
      }
   }

   private void addRepopEvent(int var1, int var2, float var3) {
      float var4 = (float)GameTime.getInstance().getWorldAgeHours();

      while(!this.repopEvents.isEmpty() && ((MPDebugInfo.MPRepopEvent)this.repopEvents.get(0)).worldAge + this.REPOP_DISPLAY_HOURS < var4) {
         this.repopEventPool.release(this.repopEvents.remove(0));
      }

      this.repopEvents.add(((MPDebugInfo.MPRepopEvent)this.repopEventPool.alloc()).init(var1, var2, var3));
      ++this.repopEpoch;
   }

   public void serverUpdate() {
      if (GameServer.bServer) {
         long var1 = System.currentTimeMillis();
         if (this.requestTime + 10000L < var1) {
            this.requestFlag = false;
            this.requestPacketReceived = false;
         } else {
            int var3;
            int var4;
            int var5;
            int var6;
            short var8;
            if (this.requestFlag) {
               if (n_hasData(false)) {
                  this.requestFlag = false;
                  this.cellPool.release(this.loadedCells);
                  this.loadedCells.clear();
                  this.loadedAreas.clear();
                  var3 = n_getLoadedCellsCount();
                  var4 = 0;

                  while(var4 < var3) {
                     this.byteBuffer.clear();
                     var5 = n_getLoadedCellsData(var4, this.byteBuffer);
                     var4 += var5;

                     for(var6 = 0; var6 < var5; ++var6) {
                        MPDebugInfo.MPCell var7 = (MPDebugInfo.MPCell)this.cellPool.alloc();
                        var7.cx = this.byteBuffer.getShort();
                        var7.cy = this.byteBuffer.getShort();
                        var7.currentPopulation = this.byteBuffer.getShort();
                        var7.desiredPopulation = this.byteBuffer.getShort();
                        var7.lastRepopTime = this.byteBuffer.getFloat();
                        this.loadedCells.add(var7);
                     }
                  }

                  var3 = n_getLoadedAreasCount();
                  var4 = 0;

                  while(var4 < var3) {
                     this.byteBuffer.clear();
                     var5 = n_getLoadedAreasData(var4, this.byteBuffer);
                     var4 += var5;

                     for(var6 = 0; var6 < var5; ++var6) {
                        boolean var12 = this.byteBuffer.get() == 0;
                        var8 = this.byteBuffer.getShort();
                        short var9 = this.byteBuffer.getShort();
                        short var10 = this.byteBuffer.getShort();
                        short var11 = this.byteBuffer.getShort();
                        this.loadedAreas.add(var8, var9, var10, var11);
                     }
                  }
               }
            } else if (this.requestPacketReceived) {
               n_requestData();
               this.requestFlag = true;
               this.requestPacketReceived = false;
            }

            if (n_hasData(true)) {
               var3 = n_getRepopEventCount();
               var4 = 0;

               while(var4 < var3) {
                  this.byteBuffer.clear();
                  var5 = n_getRepopEventData(var4, this.byteBuffer);
                  var4 += var5;

                  for(var6 = 0; var6 < var5; ++var6) {
                     short var13 = this.byteBuffer.getShort();
                     var8 = this.byteBuffer.getShort();
                     float var14 = this.byteBuffer.getFloat();
                     this.addRepopEvent(var13, var8, var14);
                  }
               }
            }

         }
      }
   }

   boolean isRespawnEnabled() {
      if (IsoWorld.getZombiesDisabled()) {
         return false;
      } else {
         return !(this.RESPAWN_EVERY_HOURS <= 0.0F);
      }
   }

   public void render(ZombiePopulationRenderer var1, float var2) {
      this.requestServerInfo();
      float var3 = (float)GameTime.getInstance().getWorldAgeHours();
      IsoMetaGrid var4 = IsoWorld.instance.MetaGrid;
      var1.outlineRect((float)(var4.minX * 300) * 1.0F, (float)(var4.minY * 300) * 1.0F, (float)((var4.maxX - var4.minX + 1) * 300) * 1.0F, (float)((var4.maxY - var4.minY + 1) * 300) * 1.0F, 1.0F, 1.0F, 1.0F, 0.25F);

      int var5;
      MPDebugInfo.MPCell var6;
      float var7;
      for(var5 = 0; var5 < this.loadedCells.size(); ++var5) {
         var6 = (MPDebugInfo.MPCell)this.loadedCells.get(var5);
         var1.outlineRect((float)(var6.cx * 300), (float)(var6.cy * 300), 300.0F, 300.0F, 1.0F, 1.0F, 1.0F, 0.25F);
         if (this.isRespawnEnabled()) {
            var7 = Math.min(var3 - var6.lastRepopTime, this.RESPAWN_EVERY_HOURS) / this.RESPAWN_EVERY_HOURS;
            if (var6.lastRepopTime > var3) {
               var7 = 0.0F;
            }

            var1.outlineRect((float)(var6.cx * 300 + 1), (float)(var6.cy * 300 + 1), 298.0F, 298.0F, 0.0F, 1.0F, 0.0F, var7 * var7);
         }
      }

      for(var5 = 0; var5 < this.loadedAreas.count; ++var5) {
         int var11 = var5 * 4;
         int var12 = this.loadedAreas.areas[var11++];
         int var8 = this.loadedAreas.areas[var11++];
         int var9 = this.loadedAreas.areas[var11++];
         int var10 = this.loadedAreas.areas[var11++];
         var1.outlineRect((float)(var12 * 10), (float)(var8 * 10), (float)(var9 * 10), (float)(var10 * 10), 0.7F, 0.7F, 0.7F, 1.0F);
      }

      for(var5 = 0; var5 < this.repopEvents.size(); ++var5) {
         MPDebugInfo.MPRepopEvent var13 = (MPDebugInfo.MPRepopEvent)this.repopEvents.get(var5);
         if (!(var13.worldAge + this.REPOP_DISPLAY_HOURS < var3)) {
            var7 = 1.0F - (var3 - var13.worldAge) / this.REPOP_DISPLAY_HOURS;
            var7 = Math.max(var7, 0.1F);
            var1.outlineRect((float)(var13.wx * 10), (float)(var13.wy * 10), 50.0F, 50.0F, 0.0F, 0.0F, 1.0F, var7);
         }
      }

      for(var5 = 0; var5 < IsoWorld.instance.CurrentCell.getZombieList().size(); ++var5) {
         IsoZombie var14 = (IsoZombie)IsoWorld.instance.CurrentCell.getZombieList().get(var5);
         var7 = 1.0F;
         float var15 = 1.0F;
         float var16 = 0.0F;
         var1.renderZombie(var14.x, var14.y, var7, var15, var16);
      }

      var1.renderZombie(IsoPlayer.instance.x, IsoPlayer.instance.y, 0.0F, 0.5F, 0.0F);
      if (var2 > 0.25F) {
         for(var5 = 0; var5 < this.loadedCells.size(); ++var5) {
            var6 = (MPDebugInfo.MPCell)this.loadedCells.get(var5);
            var1.renderCellInfo(var6.cx, var6.cy, var6.currentPopulation, var6.desiredPopulation, var6.lastRepopTime + this.RESPAWN_EVERY_HOURS - var3);
         }
      }

   }

   private static final class MPRepopEvent {
      public int wx;
      public int wy;
      public float worldAge;

      private MPRepopEvent() {
      }

      public MPDebugInfo.MPRepopEvent init(int var1, int var2, float var3) {
         this.wx = var1;
         this.wy = var2;
         this.worldAge = var3;
         return this;
      }

      // $FF: synthetic method
      MPRepopEvent(Object var1) {
         this();
      }
   }

   private static final class MPCell {
      public short cx;
      public short cy;
      public short currentPopulation;
      public short desiredPopulation;
      public float lastRepopTime;

      private MPCell() {
      }

      MPDebugInfo.MPCell init(int var1, int var2, int var3, int var4, float var5) {
         this.cx = (short)var1;
         this.cy = (short)var2;
         this.currentPopulation = (short)var3;
         this.desiredPopulation = (short)var4;
         this.lastRepopTime = var5;
         return this;
      }

      // $FF: synthetic method
      MPCell(Object var1) {
         this();
      }
   }
}
