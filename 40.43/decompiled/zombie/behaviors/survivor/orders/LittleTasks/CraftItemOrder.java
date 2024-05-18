package zombie.behaviors.survivor.orders.LittleTasks;

import zombie.behaviors.Behavior;
import zombie.behaviors.survivor.orders.Order;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.inventory.RecipeManager;
import zombie.inventory.types.Drainable;
import zombie.scripting.objects.Recipe;

public class CraftItemOrder extends Order {
   IsoGameCharacter chr;
   Recipe rec;

   public CraftItemOrder(IsoGameCharacter var1, Recipe var2) {
      super(var1);
      this.chr = var1;
      this.rec = var2;
   }

   public Behavior.BehaviorResult process() {
      return Behavior.BehaviorResult.Succeeded;
   }

   public void initOrder() {
      this.PerformMakeItem();
   }

   public boolean complete() {
      return true;
   }

   public void update() {
   }

   void DoDrainOnItem(InventoryItem var1, String var2) {
      if (RecipeManager.DoesWipeUseDelta(var1.getType(), var2)) {
         ((Drainable)var1).setUsedDelta(0.0F);
      }

      if (RecipeManager.DoesUseItemUp(var1.getType(), this.rec)) {
         float var3 = RecipeManager.UseAmount(var1.getType(), this.rec, this.chr);

         for(int var4 = 0; (float)var4 < var3; ++var4) {
            var1.Use(true);
         }
      }

   }

   void PerformMakeItem() {
      Object var1 = null;
      this.character.getInventory().AddItem(this.rec.module.name + "." + this.rec.Result.type);
   }
}
