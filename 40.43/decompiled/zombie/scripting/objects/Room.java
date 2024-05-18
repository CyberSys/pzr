package zombie.scripting.objects;

import zombie.iso.IsoGridSquare;
import zombie.iso.areas.IsoRoom;

public class Room extends BaseScriptObject {
   public int x;
   public int y;
   public int z;
   public String name;
   public IsoRoom room = null;

   public Room() {
   }

   public Room(String var1, IsoRoom var2) {
      this.name = var1;
      this.room = var2;
      IsoGridSquare var3 = (IsoGridSquare)var2.TileList.get(0);
      this.x = var3.getX();
      this.y = var3.getY();
      this.z = var3.getZ();
   }

   public void Load(String var1, String[] var2) {
      this.name = var1;
      this.x = Integer.parseInt(var2[0].trim());
      this.y = Integer.parseInt(var2[1].trim());
      this.z = Integer.parseInt(var2[2].trim());
   }
}
