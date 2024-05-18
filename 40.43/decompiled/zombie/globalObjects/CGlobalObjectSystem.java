package zombie.globalObjects;

import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.spnetwork.SinglePlayerClient;

public class CGlobalObjectSystem {
   protected final String name;
   protected final KahluaTable modData;

   public CGlobalObjectSystem(String var1) {
      this.name = var1;
      this.modData = LuaManager.platform.newTable();
   }

   public KahluaTable getModData() {
      return this.modData;
   }

   public void sendCommand(String var1, IsoPlayer var2, KahluaTable var3) {
      if (GameServer.bServer) {
         throw new IllegalStateException("can't call this method on the server");
      } else {
         if (GameClient.bClient) {
            GameClient.instance.sendClientCommand(var2, "gos_" + this.name, var1, var3);
         } else {
            SinglePlayerClient.sendClientCommand(var2, "gos_" + this.name, var1, var3);
         }

      }
   }

   public void receiveServerCommand(String var1, KahluaTable var2) {
      Object var3 = this.modData.rawget("OnServerCommand");
      if (var3 == null) {
         throw new IllegalStateException("OnServerCommand method undefined for system '" + this.name + "'");
      } else {
         LuaManager.caller.pcall(LuaManager.thread, var3, this.modData, var1, var2);
      }
   }

   public void Reset() {
      this.modData.wipe();
   }
}
