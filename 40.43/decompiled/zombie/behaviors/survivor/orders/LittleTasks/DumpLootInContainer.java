package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.LootBuilding;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;

public class DumpLootInContainer extends Order {
   IsoGameCharacter chr;
   ItemContainer con;

   public DumpLootInContainer(IsoGameCharacter var1, ItemContainer var2) {
      super(var1);
      this.chr = var1;
      this.con = var2;
   }

   public Behavior.BehaviorResult process() {
      for(int var1 = 0; var1 < this.chr.getInventory().Items.size(); ++var1) {
         InventoryItem var2 = (InventoryItem)this.chr.getInventory().Items.get(var1);
         boolean var3 = ((IsoSurvivor)this.chr).SatisfiedWithInventory(LootBuilding.LootStyle.Safehouse, IsoSurvivor.SatisfiedBy.Food);
         boolean var4 = ((IsoSurvivor)this.chr).SatisfiedWithInventory(LootBuilding.LootStyle.Safehouse, IsoSurvivor.SatisfiedBy.Weapons);
         int var5 = this.chr.getInventory().getWaterContainerCount();
         if (var2.CanStoreWater) {
            if (var5 > 2) {
               this.chr.getInventory().Remove(var2);
               this.con.AddItem(var2);
            }
         } else if (var3 && var2 instanceof Food) {
            this.chr.getInventory().Remove(var2);
            this.con.AddItem(var2);
         } else if (var4 && var2 instanceof HandWeapon) {
            this.chr.getInventory().Remove(var2);
            this.con.AddItem(var2);
         } else if (!(var2 instanceof HandWeapon) && !(var2 instanceof Food)) {
            this.chr.getInventory().Remove(var2);
            this.con.AddItem(var2);
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
