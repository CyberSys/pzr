package zombie.behaviors.survivor.orders;

import zombie.behaviors.survivor.orders.LittleTasks.DumpLootInContainer;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.inventory.ItemContainer;
import zombie.iso.areas.IsoBuilding;

class DumpLootOrder extends OrderSequence {
   public DumpLootOrder(IsoGameCharacter var1, IsoBuilding var2) {
      super(var1);
      if (var2.container.size() != 0) {
         ItemContainer var3 = (ItemContainer)var2.container.get(Rand.Next(var2.container.size()));
         this.Orders.add(new GotoNextTo(var1, var3.parent.square.getX(), var3.parent.square.getY(), var3.parent.square.getZ()));
         this.Orders.add(new DumpLootInContainer(var1, var3));
      }
   }
}
