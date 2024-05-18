package zombie.globalObjects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.network.TableNetworkUtils;

public final class CGlobalObjects {
   protected static final ArrayList systems = new ArrayList();
   protected static final HashMap initialState = new HashMap();
   public static final String PREFIX = "gos_";

   public static void noise(String var0) {
      if (Core.bDebug) {
         DebugLog.log("CGlobalObjects: " + var0);
      }

   }

   public static CGlobalObjectSystem registerSystem(String var0) {
      CGlobalObjectSystem var1 = getSystemByName(var0);
      if (var1 == null) {
         var1 = newSystem(var0);
         KahluaTable var2 = (KahluaTable)initialState.get(var0);
         if (var2 != null) {
            KahluaTableIterator var3 = var2.iterator();

            while(var3.advance()) {
               var1.modData.rawset(var3.getKey(), var3.getValue());
            }
         }
      }

      return var1;
   }

   public static CGlobalObjectSystem newSystem(String var0) throws IllegalStateException {
      if (getSystemByName(var0) != null) {
         throw new IllegalStateException("system with that name already exists");
      } else {
         noise("newSystem " + var0);
         CGlobalObjectSystem var1 = new CGlobalObjectSystem(var0);
         systems.add(var1);
         return var1;
      }
   }

   public static int getSystemCount() {
      return systems.size();
   }

   public static CGlobalObjectSystem getSystemByIndex(int var0) {
      return var0 >= 0 && var0 < systems.size() ? (CGlobalObjectSystem)systems.get(var0) : null;
   }

   public static CGlobalObjectSystem getSystemByName(String var0) {
      for(int var1 = 0; var1 < systems.size(); ++var1) {
         CGlobalObjectSystem var2 = (CGlobalObjectSystem)systems.get(var1);
         if (var2.name.equals(var0)) {
            return var2;
         }
      }

      return null;
   }

   public static void initSystems() {
      LuaEventManager.triggerEvent("OnCGlobalObjectSystemInit");
   }

   public static void loadInitialState(ByteBuffer var0) throws IOException {
      byte var1 = var0.get();

      for(int var2 = 0; var2 < var1; ++var2) {
         String var3 = GameWindow.ReadStringUTF(var0);
         if (var0.get() != 0) {
            KahluaTable var4 = LuaManager.platform.newTable();
            initialState.put(var3, var4);
            TableNetworkUtils.load(var4, var0);
         }
      }

   }

   public static boolean receiveServerCommand(String var0, String var1, KahluaTable var2) {
      if (!var0.startsWith("gos_")) {
         return false;
      } else {
         noise("receiveServerCommand " + var0 + " " + var1);
         String var3 = var0.substring(4);
         CGlobalObjectSystem var4 = getSystemByName(var3);
         if (var4 == null) {
            throw new IllegalStateException("system '" + var3 + "' not found");
         } else {
            var4.receiveServerCommand(var1, var2);
            return true;
         }
      }
   }

   public static void Reset() {
      for(int var0 = 0; var0 < systems.size(); ++var0) {
         CGlobalObjectSystem var1 = (CGlobalObjectSystem)systems.get(var0);
         var1.Reset();
      }

      systems.clear();
      initialState.clear();
   }
}
