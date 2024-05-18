package zombie.inventory;

import zombie.core.Core;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.Food;
import zombie.network.GameClient;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;

public class InventoryItemFactory {
   public static InventoryItem CreateItem(String var0) {
      return CreateItem(var0, 1.0F);
   }

   public static InventoryItem CreateItem(String var0, float var1) {
      InventoryItem var2 = null;
      Item var3 = ScriptManager.instance.FindItem(var0);
      if (var3 == null) {
         return null;
      } else {
         var2 = var3.InstanceItem((String)null);
         if (GameClient.bClient && (Core.getInstance().getPoisonousBerry() == null || Core.getInstance().getPoisonousBerry().isEmpty())) {
            Core.getInstance().setPoisonousBerry(GameClient.poisonousBerry);
         }

         if (GameClient.bClient && (Core.getInstance().getPoisonousMushroom() == null || Core.getInstance().getPoisonousMushroom().isEmpty())) {
            Core.getInstance().setPoisonousMushroom(GameClient.poisonousMushroom);
         }

         if (var0.equals(Core.getInstance().getPoisonousBerry())) {
            ((Food)var2).Poison = true;
            ((Food)var2).setPoisonLevelForRecipe(1);
            ((Food)var2).setPoisonDetectionLevel(1);
            ((Food)var2).setPoisonPower(5);
            ((Food)var2).setUseForPoison((new Float(Math.abs(((Food)var2).getHungChange()) * 100.0F)).intValue());
         }

         if (var0.equals(Core.getInstance().getPoisonousMushroom())) {
            ((Food)var2).Poison = true;
            ((Food)var2).setPoisonLevelForRecipe(2);
            ((Food)var2).setPoisonDetectionLevel(2);
            ((Food)var2).setPoisonPower(10);
            ((Food)var2).setUseForPoison((new Float(Math.abs(((Food)var2).getHungChange()) * 100.0F)).intValue());
         }

         var2.id = (long)(Rand.Next(100000000) + 1233423);
         if (var2 instanceof Drainable) {
            ((Drainable)var2).setUsedDelta(var1);
         }

         return var2;
      }
   }

   public static InventoryItem CreateItem(String var0, float var1, String var2) {
      InventoryItem var3 = null;
      Item var4 = ScriptManager.instance.getItem(var0);
      if (var4 == null) {
         DebugLog.log(var0 + " item not found.");
         return null;
      } else {
         var3 = var4.InstanceItem(var2);
         if (var3 == null) {
         }

         if (var3 instanceof Drainable) {
            ((Drainable)var3).setUsedDelta(var1);
         }

         return var3;
      }
   }

   public static InventoryItem CreateItem(String var0, String var1, String var2, String var3) {
      InventoryItem var4 = new InventoryItem(var0, var1, var2, var3);
      var4.id = (long)(Rand.Next(100000000) + 1233423);
      return var4;
   }

   public static enum ItemConcreteTypes {
   }
}
