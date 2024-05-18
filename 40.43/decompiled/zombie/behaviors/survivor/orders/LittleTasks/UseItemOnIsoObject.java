package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoObject;

public class UseItemOnIsoObject extends Order {
   IsoGameCharacter chr;
   String inv = null;
   IsoObject obj;

   public UseItemOnIsoObject(IsoGameCharacter var1, String var2, IsoObject var3) {
      super(var1);
      this.chr = var1;
      this.inv = var2;
      this.obj = var3;
   }

   public Behavior.BehaviorResult process() {
      return Behavior.BehaviorResult.Succeeded;
   }

   public void initOrder() {
      InventoryItem var1 = this.chr.getInventory().FindAndReturn(this.inv);
      if (var1 != null) {
         this.obj.useItemOn(var1);
         var1.Use();
      }
   }

   public boolean complete() {
      return true;
   }

   public void update() {
   }
}
