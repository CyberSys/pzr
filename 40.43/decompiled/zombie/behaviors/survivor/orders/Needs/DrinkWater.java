package zombie.behaviors.survivor.orders.Needs;

import java.util.ArrayList;
import java.util.Iterator;
import zombie.behaviors.survivor.orders.GotoBuildingOrder;
import zombie.behaviors.survivor.orders.GotoNextTo;
import zombie.behaviors.survivor.orders.Order;
import zombie.behaviors.survivor.orders.OrderSequence;
import zombie.behaviors.survivor.orders.LittleTasks.GotoRoomOrder;
import zombie.behaviors.survivor.orders.LittleTasks.UseItemOnIsoObject;
import zombie.characters.IsoGameCharacter;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.IsoRoomExit;

public class DrinkWater extends OrderSequence {
   static ArrayList choices = new ArrayList();

   public DrinkWater(IsoGameCharacter var1) {
      super(var1);
      this.type = "DrinkWater";
   }

   public void initOrder() {
      IsoBuilding var1 = null;
      if (this.character.getCurrentSquare().getRoom() != null && this.character.getCurrentSquare().getRoom().building.hasWater()) {
         var1 = this.character.getCurrentSquare().getRoom().building;
      }

      if (var1 == null && this.character.getDescriptor().getGroup().Safehouse != null && this.character.getDescriptor().getGroup().Safehouse.hasWater()) {
         var1 = this.character.getDescriptor().getGroup().Safehouse;
      }

      IsoBuilding var3;
      if (var1 == null) {
         Iterator var2 = IsoWorld.instance.getCell().getBuildingList().iterator();

         while(var2 != null && var2.hasNext()) {
            var3 = (IsoBuilding)var2.next();
            if (var3.hasWater()) {
               choices.add(var3);
            }
         }
      }

      float var9 = 1.0E7F;

      for(int var10 = 0; var10 < choices.size(); ++var10) {
         IsoBuilding var4 = (IsoBuilding)choices.get(var10);

         for(int var5 = 0; var5 < var4.Exits.size(); ++var5) {
            float var6 = IsoUtils.DistanceManhatten((float)((IsoRoomExit)var4.Exits.get(var5)).x, (float)((IsoRoomExit)var4.Exits.get(var5)).y, this.character.x, this.character.y);
            if (var6 < var9) {
               var1 = var4;
               break;
            }
         }
      }

      choices.clear();
      var3 = null;
      if (var1 != null) {
         if (this.character.getCurrentBuilding() != var1) {
            this.Orders.add(new GotoBuildingOrder(this.character, var1));
         }

         Iterator var11 = var1.Rooms.iterator();

         while(var11 != null && var11.hasNext()) {
            IsoRoom var12 = (IsoRoom)var11.next();
            if (!var12.WaterSources.isEmpty()) {
               IsoObject var13 = null;

               for(int var14 = 0; var14 < var12.WaterSources.size(); ++var14) {
                  if (((IsoObject)var12.WaterSources.get(var14)).hasWater()) {
                     var13 = (IsoObject)var12.WaterSources.get(var14);
                     break;
                  }
               }

               if (var13 != null) {
                  GotoRoomOrder var15 = new GotoRoomOrder(this.character, var12);
                  this.Orders.add(var15);
                  if (this.character.getInventory().getWaterContainerCount() > 0) {
                     this.Orders.add(new GotoNextTo(this.character, var13.square.getX(), var13.square.getY(), var13.square.getZ()));
                     ArrayList var7 = this.character.getInventory().getAllWaterFillables();

                     for(int var8 = 0; var8 < var7.size(); ++var8) {
                        this.Orders.add(new UseItemOnIsoObject(this.character, ((InventoryItem)var7.get(var8)).getType(), var13));
                     }
                  }

                  return;
               }
            }
         }

      }
   }

   public boolean ActedThisFrame() {
      return ((Order)this.Orders.get(this.ID)).ActedThisFrame();
   }
}
