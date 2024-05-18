package zombie.behaviors.survivor;

import zombie.ai.astar.Path;
import zombie.behaviors.Behavior;
import zombie.behaviors.DecisionPath;
import zombie.behaviors.general.PathFindBehavior;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemType;

public class ObtainItemBehavior extends Behavior {
   public ItemType FindItem;
   ItemContainer container;
   boolean DoneFindItem;
   FindKnownItemBehavior findItem;
   boolean Found;
   boolean HaveLocation;
   boolean LocationIsInventory;
   PathFindBehavior pathFind;

   public ObtainItemBehavior() {
      this.FindItem = ItemType.None;
      this.DoneFindItem = false;
      this.findItem = new FindKnownItemBehavior();
      this.Found = false;
      this.HaveLocation = false;
      this.LocationIsInventory = false;
      this.pathFind = new PathFindBehavior("ObtainItem");
   }

   public Behavior.BehaviorResult process(DecisionPath var1, IsoGameCharacter var2) {
      Behavior.BehaviorResult var3;
      if (!this.HaveLocation) {
         if (!this.DoneFindItem) {
            this.DoneFindItem = true;
            this.findItem.reset();
            this.findItem.FindItem = this.FindItem;
            var3 = this.findItem.process(var1, var2);
            if (var3 == Behavior.BehaviorResult.Failed) {
               return Behavior.BehaviorResult.Working;
            }

            if (var3 == Behavior.BehaviorResult.Succeeded) {
               if (this.findItem.LocationIsInventory) {
                  this.LocationIsInventory = true;
                  this.Found = true;
                  return Behavior.BehaviorResult.Succeeded;
               }

               this.LocationIsInventory = false;
               this.Found = true;
               this.container = this.findItem.container;
               this.pathFind.reset();
               this.pathFind.sx = (int)var2.getX();
               this.pathFind.sy = (int)var2.getY();
               this.pathFind.sz = (int)var2.getZ();
               this.pathFind.tx = this.container.SourceGrid.getX();
               this.pathFind.ty = this.container.SourceGrid.getY();
               this.pathFind.tz = this.container.SourceGrid.getZ();
               this.HaveLocation = true;
            }
         }
      } else {
         var3 = this.pathFind.process(var1, var2);
         if (var3 == Behavior.BehaviorResult.Succeeded) {
            InventoryItem var4 = this.container.Remove(this.FindItem);
            this.pathFind.reset();
            var2.setPath((Path)null);
            if (var4 != null) {
               var2.getInventory().AddItem(var4);
               return var3;
            }
         }

         if (var3 == Behavior.BehaviorResult.Failed) {
            return var3;
         }
      }

      return Behavior.BehaviorResult.Working;
   }

   public void reset() {
      this.HaveLocation = false;
      this.findItem.reset();
   }

   public boolean valid() {
      return true;
   }
}
