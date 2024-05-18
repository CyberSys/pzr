package zombie.globalObjects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.Lua.LuaManager;

public final class GlobalObject {
   protected SGlobalObjectSystem system;
   protected int x;
   protected int y;
   protected int z;
   protected final KahluaTable modData;
   private static KahluaTable tempTable;

   GlobalObject(SGlobalObjectSystem var1, int var2, int var3, int var4) {
      this.system = var1;
      this.x = var2;
      this.y = var3;
      this.z = var4;
      this.modData = LuaManager.platform.newTable();
   }

   public SGlobalObjectSystem getSystem() {
      return this.system;
   }

   public void setLocation(int var1, int var2, int var3) {
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public KahluaTable getModData() {
      return this.modData;
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      boolean var3 = var1.get() == 0;
      if (!var3) {
         this.modData.load(var1, var2);
      }

   }

   public void save(ByteBuffer var1) throws IOException {
      var1.putInt(this.x);
      var1.putInt(this.y);
      var1.put((byte)this.z);
      if (tempTable == null) {
         tempTable = LuaManager.platform.newTable();
      }

      tempTable.wipe();
      KahluaTableIterator var2 = this.modData.iterator();

      while(var2.advance()) {
         Object var3 = var2.getKey();
         if (this.system.objectModDataKeys.contains(var3)) {
            tempTable.rawset(var3, this.modData.rawget(var3));
         }
      }

      if (tempTable.isEmpty()) {
         var1.put((byte)0);
      } else {
         var1.put((byte)1);
         tempTable.save(var1);
         tempTable.wipe();
      }

   }

   public void Reset() {
      this.system = null;
      this.modData.wipe();
   }
}
