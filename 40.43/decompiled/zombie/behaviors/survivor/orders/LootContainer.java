package zombie.behaviors.survivor.orders;

import zombie.behaviors.Behavior;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoSurvivor;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;

class LootContainer extends Order {
   IsoGameCharacter chr;
   ItemContainer con;
   LootBuilding.LootStyle style;

   public LootContainer(IsoGameCharacter var1, ItemContainer var2, LootBuilding.LootStyle var3) {
      super(var1);
      this.chr = var1;
      this.con = var2;
      this.style = var3;
   }

   public Behavior.BehaviorResult process() {
      for(int var1 = 0; var1 < this.con.Items.size(); ++var1) {
         InventoryItem var2 = (InventoryItem)this.con.Items.get(var1);
         if (var2.CanStoreWater) {
            this.con.Remove(var2);
            this.chr.getInventory().AddItem(var2);
         } else if ((!(var2 instanceof Food) || ((IsoSurvivor)this.chr).SatisfiedWithInventory(this.style, IsoSurvivor.SatisfiedBy.Food)) && this.style != LootBuilding.LootStyle.Extreme && !this.chr.getDescriptor().getGroup().HasNeed("Type:Food")) {
            if ((!(var2 instanceof HandWeapon) || ((IsoSurvivor)this.chr).SatisfiedWithInventory(this.style, IsoSurvivor.SatisfiedBy.Weapons)) && this.style != LootBuilding.LootStyle.Extreme && !this.chr.getDescriptor().getGroup().HasNeed("Type:Weapon")) {
               if (this.chr.getDescriptor().getGroup().HasNeed(var2.getType())) {
                  this.con.Remove(var2);
                  this.chr.getInventory().AddItem(var2);
               }
            } else {
               this.con.Remove(var2);
               this.chr.getInventory().AddItem(var2);
            }
         } else {
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
