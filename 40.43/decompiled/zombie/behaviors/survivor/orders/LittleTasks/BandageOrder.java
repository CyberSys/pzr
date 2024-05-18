package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;

public class BandageOrder extends Order {
   IsoGameCharacter chr;

   public BandageOrder(IsoGameCharacter var1) {
      super(var1);
      this.chr = var1;
   }

   public Behavior.BehaviorResult process() {
      return Behavior.BehaviorResult.Succeeded;
   }

   public void initOrder() {
      InventoryItem var1 = this.chr.getInventory().getBestBandage(this.chr.getDescriptor());
      if (var1 != null) {
         this.chr.getBodyDamage().UseBandageOnMostNeededPart();
         var1.Use();
      }
   }

   public boolean complete() {
      return true;
   }

   public void update() {
   }
}
