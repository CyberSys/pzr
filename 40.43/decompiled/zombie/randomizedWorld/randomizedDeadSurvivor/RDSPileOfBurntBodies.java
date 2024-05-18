package zombie.randomizedWorld.randomizedDeadSurvivor;

import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoDeadBody;

public class RDSPileOfBurntBodies extends RandomizedDeadSurvivorBase {
   public void randomizeDeadSurvivor(BuildingDef var1) {
      IsoGridSquare var2 = var1.getFreeSquareInRoom();
      if (var2 != null) {
         IsoDeadBody var3 = super.createRandomDeadBody(var2.getX(), var2.getY(), var2.getZ());
         int var4 = Rand.Next(2, 5);

         for(int var5 = 0; var5 < var4; ++var5) {
            InventoryItem var6 = InventoryItemFactory.CreateItem("Base.WhiskeyEmpty");
            var3.getSquare().AddWorldInventoryItem(var6, Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F));
         }

         InventoryItem var7 = InventoryItemFactory.CreateItem("Base.WhiskeyEmpty");
         var3.getContainer().addItem(var7);
      }
   }
}
