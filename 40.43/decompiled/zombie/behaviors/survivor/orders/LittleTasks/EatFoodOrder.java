package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Food;

public class EatFoodOrder extends Order {
   IsoGameCharacter chr;

   public EatFoodOrder(IsoGameCharacter var1) {
      super(var1);
      this.chr = var1;
   }

   public Behavior.BehaviorResult process() {
      return Behavior.BehaviorResult.Succeeded;
   }

   public void initOrder() {
      InventoryItem var1 = this.chr.getInventory().getBestFood(this.chr.getDescriptor());
      if (var1 != null) {
         this.chr.Eat(var1);
         this.chr.getBodyDamage().JustAteFood((Food)var1);
         var1.Use();
      }
   }

   public boolean complete() {
      return true;
   }

   public void update() {
   }
}
