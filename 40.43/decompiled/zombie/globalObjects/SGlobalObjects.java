package zombie.globalObjects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.iso.SliceY;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.TableNetworkUtils;

public final class SGlobalObjects {
   protected static final ArrayList systems = new ArrayList();
   public static final String PREFIX = "gos_";

   public static void noise(String var0) {
      if (Core.bDebug) {
         DebugLog.log("SGlobalObjects: " + var0);
      }

   }

   public static SGlobalObjectSystem registerSystem(String var0) {
      SGlobalObjectSystem var1 = getSystemByName(var0);
      if (var1 == null) {
         var1 = newSystem(var0);
         var1.load();
      }

      return var1;
   }

   public static SGlobalObjectSystem newSystem(String var0) throws IllegalStateException {
      if (getSystemByName(var0) != null) {
         throw new IllegalStateException("system with that name already exists");
      } else {
         noise("newSystem " + var0);
         SGlobalObjectSystem var1 = new SGlobalObjectSystem(var0);
         systems.add(var1);
         return var1;
      }
   }

   public static int getSystemCount() {
      return systems.size();
   }

   public static SGlobalObjectSystem getSystemByIndex(int var0) {
      return var0 >= 0 && var0 < systems.size() ? (SGlobalObjectSystem)systems.get(var0) : null;
   }

   public static SGlobalObjectSystem getSystemByName(String var0) {
      for(int var1 = 0; var1 < systems.size(); ++var1) {
         SGlobalObjectSystem var2 = (SGlobalObjectSystem)systems.get(var1);
         if (var2.name.equals(var0)) {
            return var2;
         }
      }

      return null;
   }

   public static void update() {
      for(int var0 = 0; var0 < systems.size(); ++var0) {
         SGlobalObjectSystem var1 = (SGlobalObjectSystem)systems.get(var0);
         var1.update();
      }

   }

   public static void chunkLoaded(int var0, int var1) {
      for(int var2 = 0; var2 < systems.size(); ++var2) {
         SGlobalObjectSystem var3 = (SGlobalObjectSystem)systems.get(var2);
         var3.chunkLoaded(var0, var1);
      }

   }

   public static void initSystems() {
      if (!GameClient.bClient) {
         LuaEventManager.triggerEvent("OnSGlobalObjectSystemInit");
         if (!GameServer.bServer) {
            try {
               SliceY.SliceBuffer.clear();
               saveInitialStateForClient(SliceY.SliceBuffer);
               SliceY.SliceBuffer.flip();
               CGlobalObjects.loadInitialState(SliceY.SliceBuffer);
            } catch (Throwable var4) {
               ExceptionLogger.logException(var4);
            } finally {
               SliceY.SliceBuffer.clear();
            }

         }
      }
   }

   public static void saveInitialStateForClient(ByteBuffer var0) throws IOException {
      var0.put((byte)systems.size());

      for(int var1 = 0; var1 < systems.size(); ++var1) {
         SGlobalObjectSystem var2 = (SGlobalObjectSystem)systems.get(var1);
         KahluaTable var3 = var2.getInitialStateForClient();
         GameWindow.WriteStringUTF(var0, var2.name);
         if (var3 != null && !var3.isEmpty()) {
            var0.put((byte)1);
            TableNetworkUtils.save(var3, var0);
         } else {
            var0.put((byte)0);
         }
      }

   }

   public static boolean receiveClientCommand(String var0, String var1, IsoPlayer var2, KahluaTable var3) {
      if (!var0.startsWith("gos_")) {
         return false;
      } else {
         noise("receiveClientCommand " + var0 + " " + var1 + " OnlineID=" + var2.getOnlineID());
         String var4 = var0.substring("gos_".length());
         SGlobalObjectSystem var5 = getSystemByName(var4);
         if (var5 == null) {
            throw new IllegalStateException("system '" + var4 + "' not found");
         } else {
            var5.receiveClientCommand(var1, var2, var3);
            return true;
         }
      }
   }

   public static void load() {
   }

   public static void save() {
      for(int var0 = 0; var0 < systems.size(); ++var0) {
         SGlobalObjectSystem var1 = (SGlobalObjectSystem)systems.get(var0);
         var1.save();
      }

   }

   public static void Reset() {
      for(int var0 = 0; var0 < systems.size(); ++var0) {
         SGlobalObjectSystem var1 = (SGlobalObjectSystem)systems.get(var0);
         var1.Reset();
      }

      systems.clear();
      GlobalObjectLookup.Reset();
   }
}
