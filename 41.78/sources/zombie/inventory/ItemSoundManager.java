package zombie.inventory;

import java.util.ArrayList;
import zombie.audio.BaseSoundEmitter;
import zombie.iso.IsoWorld;


public final class ItemSoundManager {
	private static final ArrayList items = new ArrayList();
	private static final ArrayList emitters = new ArrayList();
	private static final ArrayList toAdd = new ArrayList();
	private static final ArrayList toRemove = new ArrayList();
	private static final ArrayList toStopItems = new ArrayList();
	private static final ArrayList toStopEmitters = new ArrayList();

	public static void addItem(InventoryItem inventoryItem) {
		if (inventoryItem != null && !items.contains(inventoryItem)) {
			toRemove.remove(inventoryItem);
			int int1 = toStopItems.indexOf(inventoryItem);
			if (int1 != -1) {
				toStopItems.remove(int1);
				BaseSoundEmitter baseSoundEmitter = (BaseSoundEmitter)toStopEmitters.remove(int1);
				items.add(inventoryItem);
				emitters.add(baseSoundEmitter);
			} else if (!toAdd.contains(inventoryItem)) {
				toAdd.add(inventoryItem);
			}
		}
	}

	public static void removeItem(InventoryItem inventoryItem) {
		toAdd.remove(inventoryItem);
		int int1 = items.indexOf(inventoryItem);
		if (inventoryItem != null && int1 != -1) {
			if (!toRemove.contains(inventoryItem)) {
				toRemove.add(inventoryItem);
			}
		}
	}

	public static void removeItems(ArrayList arrayList) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			removeItem((InventoryItem)arrayList.get(int1));
		}
	}

	public static void update() {
		int int1;
		if (!toStopItems.isEmpty()) {
			for (int1 = 0; int1 < toStopItems.size(); ++int1) {
				BaseSoundEmitter baseSoundEmitter = (BaseSoundEmitter)toStopEmitters.get(int1);
				baseSoundEmitter.stopAll();
				IsoWorld.instance.returnOwnershipOfEmitter(baseSoundEmitter);
			}

			toStopItems.clear();
			toStopEmitters.clear();
		}

		BaseSoundEmitter baseSoundEmitter2;
		InventoryItem inventoryItem;
		if (!toAdd.isEmpty()) {
			for (int1 = 0; int1 < toAdd.size(); ++int1) {
				inventoryItem = (InventoryItem)toAdd.get(int1);
				assert !items.contains(inventoryItem);
				items.add(inventoryItem);
				baseSoundEmitter2 = IsoWorld.instance.getFreeEmitter();
				IsoWorld.instance.takeOwnershipOfEmitter(baseSoundEmitter2);
				emitters.add(baseSoundEmitter2);
			}

			toAdd.clear();
		}

		if (!toRemove.isEmpty()) {
			for (int1 = 0; int1 < toRemove.size(); ++int1) {
				inventoryItem = (InventoryItem)toRemove.get(int1);
				assert items.contains(inventoryItem);
				int int2 = items.indexOf(inventoryItem);
				items.remove(int2);
				BaseSoundEmitter baseSoundEmitter3 = (BaseSoundEmitter)emitters.get(int2);
				emitters.remove(int2);
				toStopItems.add(inventoryItem);
				toStopEmitters.add(baseSoundEmitter3);
			}

			toRemove.clear();
		}

		for (int1 = 0; int1 < items.size(); ++int1) {
			inventoryItem = (InventoryItem)items.get(int1);
			baseSoundEmitter2 = (BaseSoundEmitter)emitters.get(int1);
			ItemContainer itemContainer = inventoryItem.getOutermostContainer();
			if (itemContainer != null) {
				if (itemContainer.containingItem != null && itemContainer.containingItem.getWorldItem() != null) {
					if (itemContainer.containingItem.getWorldItem().getWorldObjectIndex() == -1) {
						itemContainer = null;
					}
				} else if (itemContainer.parent != null) {
					if (itemContainer.parent.getObjectIndex() == -1 && itemContainer.parent.getMovingObjectIndex() == -1 && itemContainer.parent.getStaticMovingObjectIndex() == -1) {
						itemContainer = null;
					}
				} else {
					itemContainer = null;
				}
			}

			if (itemContainer != null || inventoryItem.getWorldItem() != null && inventoryItem.getWorldItem().getWorldObjectIndex() != -1) {
				inventoryItem.updateSound(baseSoundEmitter2);
				baseSoundEmitter2.tick();
			} else {
				removeItem(inventoryItem);
			}
		}
	}

	public static void Reset() {
		items.clear();
		emitters.clear();
		toAdd.clear();
		toRemove.clear();
		toStopItems.clear();
		toStopEmitters.clear();
	}
}
