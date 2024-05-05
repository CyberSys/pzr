package zombie.characters.AttachedItems;

import zombie.inventory.InventoryItem;


public final class AttachedItem {
	protected final String location;
	protected final InventoryItem item;

	public AttachedItem(String string, InventoryItem inventoryItem) {
		if (string == null) {
			throw new NullPointerException("location is null");
		} else if (string.isEmpty()) {
			throw new IllegalArgumentException("location is empty");
		} else if (inventoryItem == null) {
			throw new NullPointerException("item is null");
		} else {
			this.location = string;
			this.item = inventoryItem;
		}
	}

	public String getLocation() {
		return this.location;
	}

	public InventoryItem getItem() {
		return this.item;
	}
}
