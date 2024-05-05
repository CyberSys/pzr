package zombie.network.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import zombie.VirtualZombieManager;
import zombie.ai.State;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.NetworkCharacter;
import zombie.characters.NetworkZombieVariables;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.MPStatistic;
import zombie.network.PacketTypes;

public class ZombieUpdateInfoPacket {
   private static final boolean SendZombieState = false;
   private final ZombieUpdateInfoPacket.PlayerZombiePackInfo[] packInfo = new ZombieUpdateInfoPacket.PlayerZombiePackInfo[512];

   public void clear() {
      int var1 = this.packInfo.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         if (this.packInfo[var2] != null) {
            this.packInfo[var2].zombies.clear();
         }
      }

   }

   public void send() {
      if (!GameServer.bFastForward) {
         this.addZombiesToPackInfo();
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
                     this.writeZombies(var7, this.packInfo[var2], var2);
                     var5.endPacketImmediate();
                  }
               }
            }
         }

      }
   }

   public void receive(ByteBuffer var1) {
      if (DebugOptions.instance.Network.Client.UpdateZombiesFromPacket.getValue()) {
         short var2 = var1.getShort();

         for(short var3 = 0; var3 < var2; ++var3) {
            this.parseZombie(var1);
         }

      }
   }

   private void parseZombie(ByteBuffer var1) {
      ZombiePacket var2 = ZombieUpdateInfoPacket.l_receive.zombiePacket;
      var2.parse(var1);
      Object var3 = null;

      try {
         IsoZombie var4 = (IsoZombie)GameClient.IDToZombieMap.get(var2.id);
         if (var4 == null) {
            IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare((double)var2.realx, (double)var2.realy, (double)var2.realz);
            if (var5 != null) {
               VirtualZombieManager.instance.choices.clear();
               VirtualZombieManager.instance.choices.add(var5);
               var4 = VirtualZombieManager.instance.createRealZombieAlways(var2.descriptorID, var2.realdir, false);
               if (var4 == null) {
                  DebugLog.log("Error: VirtualZombieManager can't create zombie");
               } else {
                  var4.setFakeDead(false);
                  var4.OnlineID = var2.id;
                  GameClient.IDToZombieMap.put(var2.id, var4);
                  var4.lx = var4.nx = var4.x = var2.realx;
                  var4.ly = var4.ny = var4.y = var2.realy;
                  var4.lz = var4.z = (float)var2.realz;
                  var4.setDir(IsoDirections.fromIndex(var2.realdir));
                  var4.setForwardDirection(var4.dir.ToVector());
                  var4.setCurrent(var5);
                  var4.setHealth(var2.realHealth);
                  var4.networkAI.targetX = var2.x;
                  var4.networkAI.targetY = var2.y;
                  var4.networkAI.targetZ = var2.z;
                  var4.networkAI.targetT = var2.t;
                  var4.networkAI.predictionType = NetworkCharacter.PredictionMoveTypes.values()[var2.type];
                  var4.networkAI.moveToTarget = null;
                  NetworkZombieVariables.setInt(var4, (short)1, var2.target);
                  NetworkZombieVariables.setInt(var4, (short)4, var2.eatBodyTarget);
                  NetworkZombieVariables.setInt(var4, (short)18, var2.smParamTargetAngle);
                  NetworkZombieVariables.setBooleanVariables(var4, var2.booleanVariables);
                  var4.speedMod = var2.speedMod;
                  var4.setWalkType(var2.walkType);
                  if (var4.isReanimatedPlayer()) {
                     var4.getStateMachine().changeState((State)null, (Iterable)null);
                  }

                  for(int var6 = 0; var6 < IsoPlayer.numPlayers; ++var6) {
                     IsoPlayer var7 = IsoPlayer.players[var6];
                     if (var5.isCanSee(var6)) {
                        var4.setAlphaAndTarget(var6, 1.0F);
                     }

                     if (var7 != null && var7.ReanimatedCorpseID == var2.id) {
                        var7.ReanimatedCorpseID = -1;
                        var7.ReanimatedCorpse = var4;
                     }
                  }

                  var4.serverState = (String)var3;
               }

               var4.serverState = (String)var3;
            } else {
               float var10000 = IsoUtils.DistanceManhatten(var2.x, var2.y, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y);
               DebugLog.log("Error: GridSquare blank for zombie unspooling: Distance to player =" + var10000 + " Zombie.OID=" + var2.id);
            }

            if (var4 == null) {
               return;
            }
         }

         if (var4.networkAI.hitVehicle == null) {
            var4.networkAI.parse(var2, var1);
         }

         var4.lastRemoteUpdate = 0;
         if (!IsoWorld.instance.CurrentCell.getZombieList().contains(var4)) {
            IsoWorld.instance.CurrentCell.getZombieList().add(var4);
         }

         if (!IsoWorld.instance.CurrentCell.getObjectList().contains(var4)) {
            IsoWorld.instance.CurrentCell.getObjectList().add(var4);
         }

         var4.serverState = (String)var3;
      } catch (Exception var8) {
         var8.printStackTrace();
      }

   }

   private void writeZombies(ByteBufferWriter var1, ZombieUpdateInfoPacket.PlayerZombiePackInfo var2, int var3) {
      int var4 = var1.bb.remaining() / 57;
      var4 = PZMath.clamp(var4, 0, var2.zombies.size());
      var1.putShort((short)var4);

      for(int var5 = 0; var5 < var4; ++var5) {
         IsoZombie var6 = (IsoZombie)var2.zombies.pop();
         writeZombie(var1, var6, var3);
      }

   }

   public static void writeZombie(ByteBufferWriter var0, IsoZombie var1, int var2) {
      ZombiePacket var3 = ZombieUpdateInfoPacket.l_send.zombiePacket;
      var3.set(var1, var2);
      var3.write(var0);
   }

   private void addZombiesToPackInfo() {
      int var1 = 0;
      long var2 = System.currentTimeMillis();
      ArrayList var4 = IsoWorld.instance.CurrentCell.getZombieList();
      Iterator var5 = GameServer.udpEngine.connections.iterator();

      while(var5.hasNext()) {
         UdpConnection var6 = (UdpConnection)var5.next();
         int var7 = 0;

         for(int var8 = 0; var7 < var4.size() && var8 < 300; ++var7) {
            IsoZombie var9 = (IsoZombie)var4.get(var7);
            if (var9 != null && !var9.isDead() && var9.networkAI.isUpdateNeeded(var6.index) && var6.RelevantToPlayers((double)var9.x, (double)var9.y, 40.0D)) {
               this.addZombieToPackInfo(var9, var6.index, var6.getConnectedGUID());
               ++var1;
               ++var8;
            }
         }
      }

      long var10 = System.currentTimeMillis();
      MPStatistic.instance.count1(var10 - var2);
      MPStatistic.instance.count2((long)var1);
      MPStatistic.instance.count3((long)var4.size());
   }

   private void addZombieToPackInfo(IsoZombie var1, int var2, long var3) {
      if (this.packInfo[var2] == null) {
         this.packInfo[var2] = new ZombieUpdateInfoPacket.PlayerZombiePackInfo();
      }

      if (!this.packInfo[var2].zombies.contains(var1)) {
         this.packInfo[var2].zombies.add(var1);
         this.packInfo[var2].guid = var3;
      }
   }

   public static class PlayerZombiePackInfo {
      public final Stack zombies = new Stack();
      public long guid;
   }

   private static class l_receive {
      static final Vector2 diff = new Vector2();
      static final ZombiePacket zombiePacket = new ZombiePacket();
   }

   private static class l_send {
      static final ZombiePacket zombiePacket = new ZombiePacket();
   }
}
