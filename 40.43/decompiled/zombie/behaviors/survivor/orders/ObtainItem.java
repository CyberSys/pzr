package zombie.behaviors.survivor.orders;

import java.util.Stack;
import zombie.behaviors.survivor.orders.LittleTasks.CraftItemOrder;
import zombie.behaviors.survivor.orders.LittleTasks.TakeItemFromContainer;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoBuilding;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Recipe;

public class ObtainItem extends OrderSequence {
   Stack items = new Stack();
   public float priority = 1.0F;

   public ObtainItem(IsoGameCharacter var1, Stack var2, int var3) {
      super(var1);
      this.items.addAll(var2);
      this.priority = (float)var3;
   }

   public ObtainItem(IsoGameCharacter var1, String var2, int var3) {
      super(var1);
      this.items.add(var2);
      this.priority = (float)var3;
   }

   public void initOrder() {
      int var1;
      for(var1 = 0; var1 < this.items.size(); ++var1) {
         String var2 = (String)this.items.get(var1);
         if (this.character.getInventory().contains(var2)) {
            return;
         }
      }

      if (this.character.getCurrentSquare().getRoom() == null || this.character.getCurrentSquare().getRoom().building == null || this.character.getCurrentSquare().getRoom().building == this.character.getDescriptor().getGroup().Safehouse || this.character.getCurrentSquare().getRoom().building == null || !this.CheckBuildingForItems(this.character.getCurrentSquare().getRoom().building)) {
         if (this.character.getDescriptor().getGroup().Safehouse == null || !this.CheckBuildingForItems(this.character.getDescriptor().getGroup().Safehouse)) {
            if (!IsoWorld.instance.CurrentCell.getBuildingList().isEmpty()) {
               IsoBuilding var6 = (IsoBuilding)IsoWorld.instance.CurrentCell.getBuildingList().get(Rand.Next(IsoWorld.instance.CurrentCell.getBuildingList().size()));
               this.CheckBuildingForItems(var6);
            }

            if (this.Orders.isEmpty()) {
               boolean var7 = false;
               int var8 = Rand.Next(this.items.size());
               Stack var3 = ScriptManager.instance.getAllRecipesFor((String)this.items.get(var8));
               if (var3.size() > 0) {
                  Recipe var4 = (Recipe)var3.get(Rand.Next(var3.size()));

                  for(int var5 = 0; var5 < var4.Source.size(); ++var5) {
                  }

                  this.Orders.add(new CraftItemOrder(this.character, var4));
               }
            }

            if (this.Orders.isEmpty()) {
               for(var1 = 0; var1 < this.items.size(); ++var1) {
                  this.character.getDescriptor().getGroup().AddNeed((String)this.items.get(var1), (int)this.priority);
               }
            }

         }
      }
   }

   public boolean ActedThisFrame() {
      if (this.Orders.isEmpty()) {
         return false;
      } else {
         return this.Orders.get(this.ID) instanceof GotoNextTo || this.Orders.get(this.ID) instanceof GotoBuildingOrder;
      }
   }

   private boolean CheckBuildingForItems(IsoBuilding var1) {
      int var2 = this.Orders.size();

      for(int var3 = 0; var3 < var1.container.size(); ++var3) {
         for(int var4 = 0; var4 < this.items.size(); ++var4) {
            String var5 = (String)this.items.get(var4);
            if (((ItemContainer)var1.container.get(var3)).contains(var5)) {
               ItemContainer var6 = (ItemContainer)var1.container.get(var3);
               this.Orders.add(new GotoNextTo(this.character, var6.parent.square.getX(), var6.parent.square.getY(), var6.parent.square.getZ()));
               this.Orders.add(new TakeItemFromContainer(this.character, var6, var5));
               if (this.character.getCurrentSquare().getRoom() == null || this.character.getCurrentSquare().getRoom().building != var1) {
                  this.Orders.insertElementAt(new GotoBuildingOrder(this.character, var1), var2);
               }

               return true;
            }
         }
      }

      return false;
   }
}
