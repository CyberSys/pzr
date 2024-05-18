package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;

public class TakeItemFromContainer extends Order {
   IsoGameCharacter chr;
   ItemContainer con;
   String type;

   public TakeItemFromContainer(IsoGameCharacter var1, ItemContainer var2, String var3) {
      super(var1);
      this.chr = var1;
      this.con = var2;
      this.type = var3;
   }

   public Behavior.BehaviorResult process() {
      if (this.type.contains("Type:")) {
         InventoryItem var1;
         if (this.type.contains("Food")) {
            var1 = this.con.getBestFood(this.chr.getDescriptor());
            this.con.Remove(var1);
            this.chr.getInventory().AddItem(var1);
         }

         if (this.type.contains("Weapon")) {
            var1 = this.con.getBestWeapon(this.chr.getDescriptor());
            this.con.Remove(var1);
            this.chr.getInventory().AddItem(var1);
         }
      }

      for(int var3 = 0; var3 < this.con.Items.size(); ++var3) {
         InventoryItem var2 = (InventoryItem)this.con.Items.get(var3);
         if (var2.getType().equals(this.type)) {
            this.con.Remove(var2);
            this.chr.getInventory().AddItem(var2);
         }
      }

      return Behavior.BehaviorResult.Succeeded;
   }

   public boolean complete() {
      return true;
   }

   public void update() {
   }
}
