package zombie.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;
import zombie.VirtualZombieManager;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.sprite.IsoAnim;

public class ZombieUpdatePacker {
   public static ZombieUpdatePacker instance = new ZombieUpdatePacker();
   public static final int ZombieMaxRangeToPlayer = 70;
   public ZombieUpdatePacker.PlayerZombiePackInfo[] packInfo = new ZombieUpdatePacker.PlayerZombiePackInfo[512];
   public static int ZombiePacketsSentThisTime = 0;
   static final int ZOMBIE_UPDATE_SIZE = 21;
   static Vector2 tempo = new Vector2();
   private static final boolean SendZombieState = false;
   private ArrayList nearest = new ArrayList();
   private final float NEAR_DIST = 4.0F;

   public void addZombieToPacker(IsoZombie var1) {
      if (var1.OnlineID != -1) {
         if (var1.legsSprite.CurrentAnim.name.contains("Stagger")) {
            boolean var2 = false;
         }

         for(int var6 = 0; var6 < GameServer.udpEngine.connections.size(); ++var6) {
            UdpConnection var3 = (UdpConnection)GameServer.udpEngine.connections.get(var6);
            if (var3.isFullyConnected()) {
               double var4 = ServerOptions.instance.ZombieUpdateRadiusLowPriority.getValue();
               if (var4 == 0.0D) {
                  if (var3.ReleventTo(var1.x, var1.y)) {
                     this.doAddZombie(var1, var3);
                  }
               } else if (var3.ReleventToPlayers((double)var1.x, (double)var1.y, var4)) {
                  this.doAddZombie(var1, var3);
               }
            }
         }

      }
   }

   private void doAddZombie(IsoZombie var1, UdpConnection var2) {
      int var3 = var2.index;
      if (this.packInfo[var3] == null) {
         this.packInfo[var3] = new ZombieUpdatePacker.PlayerZombiePackInfo();
      }

      if (!this.packInfo[var3].zombies.contains(var1)) {
         this.packInfo[var3].zombies.add(var1);
         this.packInfo[var3].guid = var2.getConnectedGUID();
      }
   }

   public void clearZombies() {
      int var1 = GameServer.udpEngine.getMaxConnections();

      for(int var2 = 0; var2 < var1; ++var2) {
         if (this.packInfo[var2] != null) {
            this.packInfo[var2].zombies.clear();
         }
      }

   }

   public void packZombiesIntoPackets() {
      if (!GameServer.bFastForward) {
         this.addZombies();
         ZombiePacketsSentThisTime = 0;
         int var1 = GameServer.udpEngine.getMaxConnections();

         for(int var2 = 0; var2 < var1; ++var2) {
            if (this.packInfo[var2] != null && !this.packInfo[var2].zombies.isEmpty()) {
               long var3 = this.packInfo[var2].guid;
               UdpConnection var5 = GameServer.udpEngine.getActiveConnection(var3);
               if (var5 != null) {
                  for(int var6 = this.packInfo[var2].zombies.size() - 1; var6 >= 0; --var6) {
                     if (((IsoZombie)this.packInfo[var2].zombies.get(var6)).OnlineID == -1) {
                        this.packInfo[var2].zombies.remove(var6);
                     }
                  }

                  while(!this.packInfo[var2].zombies.isEmpty()) {
                     ByteBufferWriter var7 = var5.startPacket();
                     PacketTypes.doPacket((short)10, var7);
                     this.addZombies(var7, this.packInfo[var2]);
                     var5.endPacketSuperHighUnreliable();
                     ++ZombiePacketsSentThisTime;
                  }
               }
            }
         }

      }
   }

   public void updateZombiesFromPacket(ByteBuffer var1) {
      short var2 = var1.getShort();

      for(short var3 = 0; var3 < var2; ++var3) {
         short var4 = var1.getShort();
         float var5 = var1.getFloat();
         float var6 = var1.getFloat();
         float var7 = var1.getFloat();
         float var8 = var5;
         float var9 = var6;
         byte var10 = var1.get();
         byte var11 = var1.get();
         byte var12 = var1.get();
         byte var13 = var1.get();
         byte var14 = var1.get();
         boolean var15 = (var14 & 1) != 0;
         boolean var16 = (var14 & 2) != 0;
         boolean var17 = (var14 & 4) != 0;
         boolean var18 = (var14 & 8) != 0;
         int var19 = var14 >> 4 & 7;
         boolean var20 = (var14 & 128) != 0;
         short var21 = var1.getShort();
         byte var22 = (byte)(var11 >> 2);
         boolean var23 = (var11 & 2) != 0;
         boolean var24 = (var11 & 1) != 0;
         Object var25 = null;
         boolean var26 = false;

         try {
            IsoZombie var27 = (IsoZombie)GameClient.IDToZombieMap.get(var4);
            if (var27 == null) {
               IsoGridSquare var28 = IsoWorld.instance.CurrentCell.getGridSquare((double)var8, (double)var9, (double)var7);
               if (var28 != null) {
                  VirtualZombieManager.instance.choices.clear();
                  VirtualZombieManager.instance.choices.add(var28);
                  var27 = VirtualZombieManager.instance.createRealZombieAlways(var21, 0, false);
                  if (var27 != null) {
                     var27.setFakeDead(false);
                     var27.OnlineID = var4;
                     GameClient.IDToZombieMap.put(var4, var27);
                     var27.bx = var8;
                     var27.by = var9;

                     for(int var29 = 0; var29 < IsoPlayer.numPlayers; ++var29) {
                        IsoPlayer var30 = IsoPlayer.players[var29];
                        if (var28.isCanSee(var29)) {
                           var27.alpha[var29] = var27.targetAlpha[var29] = 1.0F;
                        }

                        if (var30 != null && var30.ReanimatedCorpseID == var4) {
                           var30.ReanimatedCorpseID = -1;
                           var30.ReanimatedCorpse = var27;
                        }
                     }
                  }

                  var27.serverState = (String)var25;
                  var26 = true;
               }

               if (var27 == null) {
                  continue;
               }
            }

            IsoAnim var34 = (IsoAnim)var27.legsSprite.AnimStack.get(var10);
            if (var34 != null && (var34.equals("ZombieDeath") || var34.equals("ZombieStaggerBack") || var34.equals("ZombieGetUp")) && var26) {
               GameClient.instance.RecentlyDied.add(var4);
               VirtualZombieManager.instance.removeZombieFromWorld(var27);
            } else {
               var27.PlayAnim(var34.name);
               var27.setDir(var12);
               var27.angle.set(var27.dir.ToVector());
               tempo.x = var8 - var27.bx;
               tempo.y = var9 - var27.by;
               var27.reqMovement.x = tempo.x;
               var27.reqMovement.y = tempo.y;
               var27.reqMovement.normalize();
               float var35 = tempo.getLength() / 5.0F;
               var35 = Math.max(var35, 0.1F);

               for(int var36 = 0; var36 < IsoPlayer.numPlayers; ++var36) {
                  IsoPlayer var31 = IsoPlayer.players[var36];
                  if (var31 != null && !var31.isDead() && (int)var31.z == (int)var27.z) {
                     float var32 = IsoUtils.DistanceToSquared(var8, var9, var31.x, var31.y);
                     if (var32 < 16.0F) {
                        var35 *= Math.max((1.0F - var32 / 16.0F) * 4.5F, 1.0F);
                        break;
                     }
                  }
               }

               var27.setBlendSpeed(var35);
               var27.lastRemoteUpdate = 0;
               var27.setX(var8);
               var27.setY(var9);
               var27.setZ(var7);
               var27.setLx(var8);
               var27.setLy(var9);
               var27.def.Finished = var24;
               var27.def.Frame = (float)var22;
               var27.setOnFloor(var15);
               var27.bCrawling = var16;
               var27.setIgnoreMovementForDirection(var17);
               var27.def.AnimFrameIncrease = (float)var13 / 128.0F;
               var27.def.Looped = var23;
               var27.thumpFlag = var19;
               var27.mpIdleSound = var20;
               if (var18) {
                  var27.SetOnFire();
               } else {
                  var27.StopBurning();
               }

               if (!IsoWorld.instance.CurrentCell.getZombieList().contains(var27)) {
                  IsoWorld.instance.CurrentCell.getZombieList().add(var27);
               }

               if (!IsoWorld.instance.CurrentCell.getObjectList().contains(var27)) {
                  IsoWorld.instance.CurrentCell.getObjectList().add(var27);
               }

               var27.serverState = (String)var25;
            }
         } catch (Exception var33) {
            var33.printStackTrace();
         }
      }

   }

   private void addZombies(ByteBufferWriter var1, ZombieUpdatePacker.PlayerZombiePackInfo var2) {
      int var3 = var1.bb.remaining() / 21;
      if (var3 > var2.zombies.size()) {
         var3 = var2.zombies.size();
      }

      var1.putShort((short)var3);

      for(int var4 = 0; var4 < var3; ++var4) {
         long var5 = (long)var1.bb.position();
         IsoZombie var7 = (IsoZombie)var2.zombies.pop();
         var1.putShort(var7.OnlineID);
         byte var8 = (byte)var7.legsSprite.AnimStack.indexOf(var7.legsSprite.CurrentAnim);
         byte var9 = (byte)((int)var7.def.Frame);
         byte var10 = (byte)(var7.def.Finished ? 1 : 0);
         byte var11 = (byte)(var7.def.Looped ? 1 : 0);
         byte var12 = (byte)(var10 | var11 << 1 | var9 << 2);
         byte var13 = (byte)(var7.isOnFloor() ? 1 : 0);
         byte var14 = (byte)(var7.bCrawling ? 1 : 0);
         byte var15 = (byte)(var7.IgnoreMovementForDirection ? 1 : 0);
         byte var16 = (byte)(var7.isOnFire() ? 1 : 0);
         byte var17 = (byte)var7.thumpFlag;
         byte var18 = (byte)(var7.mpIdleSound ? 1 : 0);
         var7.thumpSent = true;
         byte var19 = (byte)(var13 | var14 << 1 | var15 << 2 | var16 << 3 | var17 << 4 | var18 << 7);
         var1.putFloat(var7.x);
         var1.putFloat(var7.y);
         var1.putFloat(var7.z);
         var1.putByte(var8);
         var1.putByte(var12);
         var1.putByte((byte)var7.dir.index());
         var1.putByte((byte)((int)(var7.def.AnimFrameIncrease * 128.0F)));
         var1.putByte(var19);
         var1.putShort((short)var7.getDescriptor().getID());

         assert (long)var1.bb.position() - var5 == 21L;
      }

   }

   private void addZombies() {
      for(int var1 = 0; var1 < GameServer.Players.size(); ++var1) {
         IsoPlayer var2 = (IsoPlayer)GameServer.Players.get(var1);
         UdpConnection var3 = GameServer.getConnectionFromPlayer(var2);
         if (var3 != null && var3.isFullyConnected()) {
            var2.zombiesToSend.update();

            for(int var4 = 0; var4 < 300; ++var4) {
               IsoZombie var5 = var2.zombiesToSend.getZombie(var4);
               if (var5 == null) {
                  break;
               }

               this.doAddZombie(var5, var3);
            }
         }
      }

   }

   private static class NearestComparator implements Comparator {
      public static ZombieUpdatePacker.NearestComparator instance = new ZombieUpdatePacker.NearestComparator();
      public IsoMovingObject testPlayer;

      public void init(IsoMovingObject var1) {
         this.testPlayer = var1;
      }

      public int compare(IsoZombie var1, IsoZombie var2) {
         float var3 = var1.DistToSquared(this.testPlayer);
         float var4 = var2.DistToSquared(this.testPlayer);
         if (var3 < var4) {
            return -1;
         } else {
            return var3 > var4 ? 1 : 0;
         }
      }
   }

   public class PlayerZombiePackInfo {
      public Stack zombies = new Stack();
      public long guid;
   }
}
