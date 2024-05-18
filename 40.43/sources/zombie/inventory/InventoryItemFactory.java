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

	public static InventoryItem CreateItem(String string) {
		return CreateItem(string, 1.0F);
	}

	public static InventoryItem CreateItem(String string, float float1) {
		InventoryItem inventoryItem = null;
		Item item = ScriptManager.instance.FindItem(string);
		if (item == null) {
			return null;
		} else {
			inventoryItem = item.InstanceItem((String)null);
			if (GameClient.bClient && (Core.getInstance().getPoisonousBerry() == null || Core.getInstance().getPoisonousBerry().isEmpty())) {
				Core.getInstance().setPoisonousBerry(GameClient.poisonousBerry);
			}

			if (GameClient.bClient && (Core.getInstance().getPoisonousMushroom() == null || Core.getInstance().getPoisonousMushroom().isEmpty())) {
				Core.getInstance().setPoisonousMushroom(GameClient.poisonousMushroom);
			}

			if (string.equals(Core.getInstance().getPoisonousBerry())) {
				((Food)inventoryItem).Poison = true;
				((Food)inventoryItem).setPoisonLevelForRecipe(1);
				((Food)inventoryItem).setPoisonDetectionLevel(1);
				((Food)inventoryItem).setPoisonPower(5);
				((Food)inventoryItem).setUseForPoison((new Float(Math.abs(((Food)inventoryItem).getHungChange()) * 100.0F)).intValue());
			}

			if (string.equals(Core.getInstance().getPoisonousMushroom())) {
				((Food)inventoryItem).Poison = true;
				((Food)inventoryItem).setPoisonLevelForRecipe(2);
				((Food)inventoryItem).setPoisonDetectionLevel(2);
				((Food)inventoryItem).setPoisonPower(10);
				((Food)inventoryItem).setUseForPoison((new Float(Math.abs(((Food)inventoryItem).getHungChange()) * 100.0F)).intValue());
			}

			inventoryItem.id = (long)(Rand.Next(100000000) + 1233423);
			if (inventoryItem instanceof Drainable) {
				((Drainable)inventoryItem).setUsedDelta(float1);
			}

			return inventoryItem;
		}
	}

	public static InventoryItem CreateItem(String string, float float1, String string2) {
		InventoryItem inventoryItem = null;
		Item item = ScriptManager.instance.getItem(string);
		if (item == null) {
			DebugLog.log(string + " item not found.");
			return null;
		} else {
			inventoryItem = item.InstanceItem(string2);
			if (inventoryItem == null) {
			}

			if (inventoryItem instanceof Drainable) {
				((Drainable)inventoryItem).setUsedDelta(float1);
			}

			return inventoryItem;
		}
	}

	public static InventoryItem CreateItem(String string, String string2, String string3, String string4) {
		InventoryItem inventoryItem = new InventoryItem(string, string2, string3, string4);
		inventoryItem.id = (long)(Rand.Next(100000000) + 1233423);
		return inventoryItem;
	}
	public static enum ItemConcreteTypes {

;
	}
}
