package zombie.scripting.objects;

import zombie.inventory.ItemContainer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;

public class ScriptContainer extends BaseScriptObject {
   public int x;
   public int y;
   public int z;
   public String name;
   public String type;
   ItemContainer ac;

   public void Load(String var1, String[] var2) {
      this.name = var1;
      this.type = var2[0].trim();
      this.x = Integer.parseInt(var2[1].trim());
      this.y = Integer.parseInt(var2[2].trim());
      this.z = Integer.parseInt(var2[3].trim());
   }

   public boolean HasInventory(String var1) {
      ItemContainer var2 = this.getActual();
      return var2 == null ? false : var2.contains(var1);
   }

   public ItemContainer getActual() {
      if (this.ac != null) {
         return this.ac;
      } else {
         IsoGridSquare var1 = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);

         for(int var2 = 0; var2 < var1.getObjects().size(); ++var2) {
            IsoObject var3 = (IsoObject)var1.getObjects().get(var2);
            if (var3.container != null && var3.container.type.equals(this.type)) {
               this.ac = var3.container;
               return var3.container;
            }
         }

         return null;
      }
   }
}
