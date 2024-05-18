package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoDeadBody;

public class RDSBleach extends RandomizedDeadSurvivorBase {
   public void randomizeDeadSurvivor(BuildingDef var1) {
      for(int var2 = 0; var2 < var1.rooms.size(); ++var2) {
         RoomDef var3 = (RoomDef)var1.rooms.get(var2);
         if ("kitchen".equals(var3.name)) {
            IsoDeadBody var4 = super.createRandomDeadBody(var3);
            if (var4 != null) {
               int var5 = Rand.Next(2, 5);

               for(int var6 = 0; var6 < var5; ++var6) {
                  InventoryItem var7 = InventoryItemFactory.CreateItem("Base.BleachEmpty");
                  var4.getSquare().AddWorldInventoryItem(var7, Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
               }

               InventoryItem var8 = InventoryItemFactory.CreateItem("Base.BleachEmpty");
               var4.getContainer().addItem(var8);
               return;
            }
         }
      }

   }
}
