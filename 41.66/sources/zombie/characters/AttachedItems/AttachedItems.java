package zombie.characters.AttachedItems;

import java.util.ArrayList;
import java.util.function.Consumer;
import zombie.inventory.InventoryItem;


public final class AttachedItems {
	protected final AttachedLocationGroup group;
	protected final ArrayList items = new ArrayList();

	public AttachedItems(AttachedLocationGroup attachedLocationGroup) {
		this.group = attachedLocationGroup;
	}

	public AttachedItems(AttachedItems attachedItems) {
		this.group = attachedItems.group;
		this.copyFrom(attachedItems);
	}

	public void copyFrom(AttachedItems attachedItems) {
		if (this.group != attachedItems.group) {
			throw new RuntimeException("group=" + this.group.id + " other.group=" + attachedItems.group.id);
		} else {
			this.items.clear();
			this.items.addAll(attachedItems.items);
		}
	}

	public AttachedLocationGroup getGroup() {
		return this.group;
	}

	public AttachedItem get(int int1) {
		return (AttachedItem)this.items.get(int1);
	}

	public void setItem(String string, InventoryItem inventoryItem) {
		this.group.checkValid(string);
		int int1 = this.indexOf(string);
		if (int1 != -1) {
			this.items.remove(int1);
		}

		if (inventoryItem != null) {
			this.remove(inventoryItem);
			int int2 = this.items.size();
			for (int int3 = 0; int3 < this.items.size(); ++int3) {
				AttachedItem attachedItem = (AttachedItem)this.items.get(int3);
				if (this.group.indexOf(attachedItem.getLocation()) > this.group.indexOf(string)) {
					int2 = int3;
					break;
				}
			}

			AttachedItem attachedItem2 = new AttachedItem(string, inventoryItem);
			this.items.add(int2, attachedItem2);
		}
	}

	public InventoryItem getItem(String string) {
		this.group.checkValid(string);
		int int1 = this.indexOf(string);
		return int1 == -1 ? null : ((AttachedItem)this.items.get(int1)).item;
	}

	public InventoryItem getItemByIndex(int int1) {
		return int1 >= 0 && int1 < this.items.size() ? ((AttachedItem)this.items.get(int1)).getItem() : null;
	}

	public void remove(InventoryItem inventoryItem) {
		int int1 = this.indexOf(inventoryItem);
		if (int1 != -1) {
			this.items.remove(int1);
		}
	}

	public void clear() {
		this.items.clear();
	}

	public String getLocation(InventoryItem inventoryItem) {
		int int1 = this.indexOf(inventoryItem);
		return int1 == -1 ? null : ((AttachedItem)this.items.get(int1)).getLocation();
	}

	public boolean contains(InventoryItem inventoryItem) {
		return this.indexOf(inventoryItem) != -1;
	}

	public int size() {
		return this.items.size();
	}

	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	public void forEach(Consumer consumer) {
		for (int int1 = 0; int1 < this.items.size(); ++int1) {
			consumer.accept((AttachedItem)this.items.get(int1));
		}
	}

	private int indexOf(String string) {
		for (int int1 = 0; int1 < this.items.size(); ++int1) {
			AttachedItem attachedItem = (AttachedItem)this.items.get(int1);
			if (attachedItem.location.equals(string)) {
				return int1;
			}
		}

		return -1;
	}

	private int indexOf(InventoryItem inventoryItem) {
		for (int int1 = 0; int1 < this.items.size(); ++int1) {
			AttachedItem attachedItem = (AttachedItem)this.items.get(int1);
			if (attachedItem.getItem() == inventoryItem) {
				return int1;
			}
		}

		return -1;
	}
}
