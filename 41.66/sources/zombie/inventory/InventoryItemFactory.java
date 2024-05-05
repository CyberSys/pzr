package zombie.inventory;

import zombie.core.Core;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.Food;
import zombie.inventory.types.Moveable;
import zombie.inventory.types.Radio;
import zombie.network.GameClient;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.Type;
import zombie.world.ItemInfo;
import zombie.world.WorldDictionary;


public final class InventoryItemFactory {

	public static InventoryItem CreateItem(String string) {
		return CreateItem(string, 1.0F);
	}

	public static InventoryItem CreateItem(String string, Food food) {
		InventoryItem inventoryItem = CreateItem(string, 1.0F);
		Food food2 = (Food)Type.tryCastTo(inventoryItem, Food.class);
		if (food2 == null) {
			return null;
		} else {
			food2.setBaseHunger(food.getBaseHunger());
			food2.setHungChange(food.getHungChange());
			food2.setBoredomChange(food.getBoredomChange());
			food2.setUnhappyChange(food.getUnhappyChange());
			food2.setCarbohydrates(food.getCarbohydrates());
			food2.setLipids(food.getLipids());
			food2.setProteins(food.getProteins());
			food2.setCalories(food.getCalories());
			return inventoryItem;
		}
	}

	public static InventoryItem CreateItem(String string, float float1) {
		return CreateItem(string, float1, true);
	}

	public static InventoryItem CreateItem(String string, float float1, boolean boolean1) {
		InventoryItem inventoryItem = null;
		Item item = null;
		boolean boolean2 = false;
		String string2 = null;
		try {
			if (string.startsWith("Moveables.") && !string.equalsIgnoreCase("Moveables.Moveable")) {
				String[] stringArray = string.split("\\.");
				string2 = stringArray[1];
				boolean2 = true;
				string = "Moveables.Moveable";
			}

			item = ScriptManager.instance.FindItem(string, boolean1);
		} catch (Exception exception) {
			DebugLog.log("couldn\'t find item " + string);
		}

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

			inventoryItem.id = Rand.Next(2146250223) + 1233423;
			if (inventoryItem instanceof Drainable) {
				((Drainable)inventoryItem).setUsedDelta(float1);
			}

			if (boolean2) {
				inventoryItem.type = string2;
				inventoryItem.fullType = inventoryItem.module + "." + string2;
				if (inventoryItem instanceof Moveable && !((Moveable)inventoryItem).ReadFromWorldSprite(string2) && inventoryItem instanceof Radio) {
					DebugLog.log("InventoryItemFactory -> Radio item = " + (string != null ? string : "unknown"));
				}
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
		inventoryItem.id = Rand.Next(2146250223) + 1233423;
		return inventoryItem;
	}

	public static InventoryItem CreateItem(short short1) {
		ItemInfo itemInfo = WorldDictionary.getItemInfoFromID(short1);
		if (itemInfo != null && itemInfo.isValid()) {
			String string = itemInfo.getFullType();
			if (string != null) {
				InventoryItem inventoryItem = CreateItem(string, 1.0F, false);
				if (inventoryItem != null) {
					return inventoryItem;
				}

				DebugLog.log("InventoryItemFactory.CreateItem() unknown item type \"" + (string != null ? string : "unknown") + "\", registry id = \"" + short1 + "\". Make sure all mods used in save are installed.");
			} else {
				DebugLog.log("InventoryItemFactory.CreateItem() unknown item with registry ID \"" + short1 + "\". Make sure all mods used in save are installed.");
			}
		} else if (itemInfo == null) {
			DebugLog.log("InventoryItemFactory.CreateItem() unknown item with registry ID \"" + short1 + "\". Make sure all mods used in save are installed.");
		} else {
			DebugLog.log("InventoryItemFactory.CreateItem() cannot create item: " + itemInfo.ToString());
		}

		return null;
	}
}
