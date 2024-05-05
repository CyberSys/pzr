package zombie.inventory;

import java.util.ArrayList;
import zombie.characters.IsoGameCharacter;
import zombie.debug.DebugLog;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoMannequin;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameClient;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.Type;
import zombie.vehicles.VehiclePart;


public final class ItemUser {
	private static final ArrayList tempItems = new ArrayList();

	public static void UseItem(InventoryItem inventoryItem) {
		DrainableComboItem drainableComboItem = (DrainableComboItem)Type.tryCastTo(inventoryItem, DrainableComboItem.class);
		if (drainableComboItem != null) {
			drainableComboItem.setDelta(drainableComboItem.getDelta() - drainableComboItem.getUseDelta());
			InventoryItem inventoryItem2;
			if (drainableComboItem.uses > 1) {
				int int1 = drainableComboItem.uses - 1;
				drainableComboItem.uses = 1;
				CreateItem(drainableComboItem.getFullType(), tempItems);
				byte byte1 = 0;
				if (byte1 < tempItems.size()) {
					inventoryItem2 = (InventoryItem)tempItems.get(byte1);
					inventoryItem2.setUses(int1);
					AddItem(drainableComboItem, inventoryItem2);
				}
			}

			if (drainableComboItem.getDelta() <= 1.0E-4F) {
				drainableComboItem.setDelta(0.0F);
				if (drainableComboItem.getReplaceOnDeplete() == null) {
					UseItem(drainableComboItem, false, false);
				} else {
					String string = drainableComboItem.getReplaceOnDepleteFullType();
					CreateItem(string, tempItems);
					for (int int2 = 0; int2 < tempItems.size(); ++int2) {
						inventoryItem2 = (InventoryItem)tempItems.get(int2);
						inventoryItem2.setFavorite(drainableComboItem.isFavorite());
						AddItem(drainableComboItem, inventoryItem2);
					}

					RemoveItem(drainableComboItem);
				}
			}

			drainableComboItem.updateWeight();
		} else {
			UseItem(inventoryItem, false, false);
		}
	}

	public static void UseItem(InventoryItem inventoryItem, boolean boolean1, boolean boolean2) {
		if (inventoryItem.isDisappearOnUse() || boolean1) {
			--inventoryItem.uses;
			if (inventoryItem.replaceOnUse != null && !boolean2 && !boolean1) {
				String string = inventoryItem.replaceOnUse;
				if (!string.contains(".")) {
					string = inventoryItem.module + "." + string;
				}

				CreateItem(string, tempItems);
				for (int int1 = 0; int1 < tempItems.size(); ++int1) {
					InventoryItem inventoryItem2 = (InventoryItem)tempItems.get(int1);
					inventoryItem2.setConditionFromModData(inventoryItem);
					AddItem(inventoryItem, inventoryItem2);
					inventoryItem2.setFavorite(inventoryItem.isFavorite());
				}
			}

			if (inventoryItem.uses <= 0) {
				if (inventoryItem.keepOnDeplete) {
					return;
				}

				RemoveItem(inventoryItem);
			} else if (GameClient.bClient && !inventoryItem.isInPlayerInventory()) {
				GameClient.instance.sendItemStats(inventoryItem);
			}
		}
	}

	public static void CreateItem(String string, ArrayList arrayList) {
		arrayList.clear();
		Item item = ScriptManager.instance.FindItem(string);
		if (item == null) {
			DebugLog.General.warn("ERROR: ItemUses.CreateItem: can\'t find " + string);
		} else {
			int int1 = item.getCount();
			for (int int2 = 0; int2 < int1; ++int2) {
				InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
				if (inventoryItem == null) {
					return;
				}

				arrayList.add(inventoryItem);
			}
		}
	}

	public static void AddItem(InventoryItem inventoryItem, InventoryItem inventoryItem2) {
		IsoWorldInventoryObject worldInventoryObject = inventoryItem.getWorldItem();
		if (worldInventoryObject != null && worldInventoryObject.getWorldObjectIndex() == -1) {
			worldInventoryObject = null;
		}

		if (worldInventoryObject != null) {
			worldInventoryObject.getSquare().AddWorldInventoryItem(inventoryItem2, 0.0F, 0.0F, 0.0F, true);
		} else {
			if (inventoryItem.container != null) {
				VehiclePart vehiclePart = inventoryItem.container.vehiclePart;
				if (!inventoryItem.isInPlayerInventory() && GameClient.bClient) {
					inventoryItem.container.addItemOnServer(inventoryItem2);
				}

				inventoryItem.container.AddItem(inventoryItem2);
				if (vehiclePart != null) {
					vehiclePart.setContainerContentAmount(vehiclePart.getItemContainer().getCapacityWeight());
				}
			}
		}
	}

	public static void RemoveItem(InventoryItem inventoryItem) {
		IsoWorldInventoryObject worldInventoryObject = inventoryItem.getWorldItem();
		if (worldInventoryObject != null && worldInventoryObject.getWorldObjectIndex() == -1) {
			worldInventoryObject = null;
		}

		if (worldInventoryObject != null) {
			worldInventoryObject.getSquare().transmitRemoveItemFromSquare(worldInventoryObject);
			if (inventoryItem.container != null) {
				inventoryItem.container.Items.remove(inventoryItem);
				inventoryItem.container.setDirty(true);
				inventoryItem.container.setDrawDirty(true);
				inventoryItem.container = null;
			}
		} else {
			if (inventoryItem.container != null) {
				IsoObject object = inventoryItem.container.parent;
				VehiclePart vehiclePart = inventoryItem.container.vehiclePart;
				if (object instanceof IsoGameCharacter) {
					IsoGameCharacter gameCharacter = (IsoGameCharacter)object;
					if (inventoryItem instanceof Clothing) {
						((Clothing)inventoryItem).Unwear();
					}

					gameCharacter.removeFromHands(inventoryItem);
					if (gameCharacter.getClothingItem_Back() == inventoryItem) {
						gameCharacter.setClothingItem_Back((InventoryItem)null);
					}
				} else if (!inventoryItem.isInPlayerInventory() && GameClient.bClient) {
					inventoryItem.container.removeItemOnServer(inventoryItem);
				}

				inventoryItem.container.Items.remove(inventoryItem);
				inventoryItem.container.setDirty(true);
				inventoryItem.container.setDrawDirty(true);
				inventoryItem.container = null;
				if (object instanceof IsoDeadBody) {
					((IsoDeadBody)object).checkClothing(inventoryItem);
				}

				if (object instanceof IsoMannequin) {
					((IsoMannequin)object).checkClothing(inventoryItem);
				}

				if (vehiclePart != null) {
					vehiclePart.setContainerContentAmount(vehiclePart.getItemContainer().getCapacityWeight());
				}
			}
		}
	}
}
