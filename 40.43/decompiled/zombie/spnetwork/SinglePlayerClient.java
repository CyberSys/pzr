package zombie.spnetwork;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.globalObjects.CGlobalObjects;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.TableNetworkUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;

public class SinglePlayerClient {
   private static final ArrayList MainLoopNetData = new ArrayList();
   public static final UdpEngine udpEngine = new SinglePlayerClient.UdpEngineClient();
   public static final UdpConnection connection;

   public static void addIncoming(short var0, ByteBuffer var1) {
      ZomboidNetData var2;
      if (var1.remaining() > 2048) {
         var2 = ZomboidNetDataPool.instance.getLong(var1.remaining());
      } else {
         var2 = ZomboidNetDataPool.instance.get();
      }

      var2.read(var0, var1, connection);
      synchronized(MainLoopNetData) {
         MainLoopNetData.add(var2);
      }
   }

   public static void update() {
      if (!GameClient.bClient) {
         for(int var0 = 0; var0 < IsoPlayer.numPlayers; ++var0) {
            if (IsoPlayer.players[var0] != null) {
               IsoPlayer.players[var0].OnlineID = var0;
            }
         }

         synchronized(MainLoopNetData) {
            for(int var1 = 0; var1 < MainLoopNetData.size(); ++var1) {
               ZomboidNetData var2 = (ZomboidNetData)MainLoopNetData.get(var1);
               mainLoopDealWithNetData(var2);
               MainLoopNetData.remove(var1--);
            }

         }
      }
   }

   private static void mainLoopDealWithNetData(ZomboidNetData var0) {
      ByteBuffer var1 = var0.buffer;

      try {
         switch(var0.type) {
         case 57:
            receiveServerCommand(var1);
            break;
         case 59:
            receiveObjectChange(var1);
         }
      } finally {
         ZomboidNetDataPool.instance.discard(var0);
      }

   }

   private static void delayPacket(int var0, int var1, int var2) {
   }

   private static IsoPlayer getPlayerByID(int var0) {
      return IsoPlayer.players[var0];
   }

   private static void receiveObjectChange(ByteBuffer var0) {
      byte var1 = var0.get();
      int var2;
      String var3;
      if (var1 == 1) {
         var2 = var0.getInt();
         var3 = GameWindow.ReadString(var0);
         if (Core.bDebug) {
            DebugLog.log("receiveObjectChange " + var3);
         }

         IsoPlayer var4 = getPlayerByID(var2);
         if (var4 != null) {
            var4.loadChange(var3, var0);
         }
      } else if (var1 == 2) {
         short var9 = var0.getShort();
         var3 = GameWindow.ReadString(var0);
         if (Core.bDebug) {
            DebugLog.log("receiveObjectChange " + var3);
         }

         BaseVehicle var11 = VehicleManager.instance.getVehicleByID(var9);
         if (var11 != null) {
            var11.loadChange(var3, var0);
         } else if (Core.bDebug) {
            DebugLog.log("receiveObjectChange: unknown vehicle id=" + var9);
         }
      } else {
         var2 = var0.getInt();
         int var10 = var0.getInt();
         int var12 = var0.getInt();
         int var5 = var0.getInt();
         String var6 = GameWindow.ReadString(var0);
         if (Core.bDebug) {
            DebugLog.log("receiveObjectChange " + var6);
         }

         IsoGridSquare var7 = IsoWorld.instance.CurrentCell.getGridSquare(var2, var10, var12);
         if (var7 == null) {
            delayPacket(var2, var10, var12);
            return;
         }

         if (var7 != null && var5 >= 0 && var5 < var7.getObjects().size()) {
            IsoObject var8 = (IsoObject)var7.getObjects().get(var5);
            var8.loadChange(var6, var0);
         } else if (var7 != null) {
            if (Core.bDebug) {
               DebugLog.log("receiveObjectChange: index=" + var5 + " is invalid x,y,z=" + var2 + "," + var10 + "," + var12);
            }
         } else if (Core.bDebug) {
            DebugLog.log("receiveObjectChange: sq is null x,y,z=" + var2 + "," + var10 + "," + var12);
         }
      }

   }

   public static void sendClientCommand(IsoPlayer var0, String var1, String var2, KahluaTable var3) {
      ByteBufferWriter var4 = connection.startPacket();
      PacketTypes.doPacket((short)57, var4);
      var4.putByte((byte)(var0 != null ? var0.PlayerIndex : -1));
      var4.putUTF(var1);
      var4.putUTF(var2);
      if (var3 != null && !var3.isEmpty()) {
         var4.putByte((byte)1);

         try {
            KahluaTableIterator var5 = var3.iterator();

            while(var5.advance()) {
               if (!TableNetworkUtils.canSave(var5.getKey(), var5.getValue())) {
                  DebugLog.log("ERROR: sendClientCommand: can't save key,value=" + var5.getKey() + "," + var5.getValue());
               }
            }

            TableNetworkUtils.save(var3, var4.bb);
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      } else {
         var4.putByte((byte)0);
      }

      connection.endPacketImmediate();
   }

   private static void receiveServerCommand(ByteBuffer var0) {
      String var1 = GameWindow.ReadString(var0);
      String var2 = GameWindow.ReadString(var0);
      boolean var3 = var0.get() == 1;
      KahluaTable var4 = null;
      if (var3) {
         var4 = LuaManager.platform.newTable();

         try {
            TableNetworkUtils.load(var4, var0);
         } catch (Exception var6) {
            var6.printStackTrace();
            return;
         }
      }

      if (!CGlobalObjects.receiveServerCommand(var1, var2, var4)) {
         LuaEventManager.triggerEvent("OnServerCommand", var1, var2, var4);
      }
   }

   public static void Reset() {
      Iterator var0 = MainLoopNetData.iterator();

      while(var0.hasNext()) {
         ZomboidNetData var1 = (ZomboidNetData)var0.next();
         ZomboidNetDataPool.instance.discard(var1);
      }

      MainLoopNetData.clear();
   }

   static {
      connection = new UdpConnection(udpEngine);
   }

   private static final class UdpEngineClient extends UdpEngine {
      private UdpEngineClient() {
      }

      public void Send(ByteBuffer var1) {
         SinglePlayerServer.udpEngine.Receive(var1);
      }

      public void Receive(ByteBuffer var1) {
         int var2 = var1.get() & 255;
         short var3 = var1.getShort();
         SinglePlayerClient.addIncoming(var3, var1);
      }

      // $FF: synthetic method
      UdpEngineClient(Object var1) {
         this();
      }
   }
}
