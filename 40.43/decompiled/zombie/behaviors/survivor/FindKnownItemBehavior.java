package zombie.behaviors.survivor;

import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemType;

public class FindKnownItemBehavior extends Behavior {
   public ItemType FindItem;
   public boolean Found;
   public boolean LocationIsInventory;
   ItemContainer container;

   public FindKnownItemBehavior() {
      this.FindItem = ItemType.None;
      this.Found = false;
      this.LocationIsInventory = false;
   }

   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      this.Found = false;
      if (var2.getInventory().HasType(this.FindItem)) {
         this.LocationIsInventory = true;
         this.Found = true;
         return Behavior.BehaviorResult.Succeeded;
      } else {
         this.LocationIsInventory = false;
         if (var2.getCurrentSquare().getRoom() != null) {
            this.container = var2.getCurrentSquare().getRoom().building.getContainerWith(this.FindItem);
            if (this.container != null) {
               this.Found = true;
               return Behavior.BehaviorResult.Succeeded;
            }
         }

         return Behavior.BehaviorResult.Failed;
      }
   }

   public void reset() {
   }

   public boolean valid() {
      return true;
   }
}
