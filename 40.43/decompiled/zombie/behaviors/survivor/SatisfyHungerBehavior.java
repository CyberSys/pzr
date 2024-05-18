package zombie.behaviors.survivor;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemType;

public class SatisfyHungerBehavior extends Behavior {
   ObtainItemBehavior obtain = new ObtainItemBehavior();

   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      this.obtain.FindItem = ItemType.Food;
      this.obtain.Found = false;
      Behavior.BehaviorResult var3 = this.obtain.process(var1, var2);
      if (var3 == Behavior.BehaviorResult.Succeeded) {
         InventoryItem var4 = var2.getInventory().Remove(ItemType.Food);
         var2.Eat(var4);
         this.reset();
      }

      return var3;
   }

   public void reset() {
      this.obtain.reset();
      this.obtain.FindItem = ItemType.Food;
      this.obtain.Found = false;
      this.obtain.HaveLocation = false;
      this.obtain.DoneFindItem = false;
      this.obtain.container = null;
   }

   public boolean valid() {
      return true;
   }
}
