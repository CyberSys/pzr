package zombie.characters.WornItems;

import java.util.ArrayList;
import java.util.function.Consumer;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.InventoryContainer;
import zombie.util.StringUtils;


public final class WornItems {
	protected final BodyLocationGroup group;
	protected final ArrayList items = new ArrayList();

	public WornItems(BodyLocationGroup bodyLocationGroup) {
		this.group = bodyLocationGroup;
	}

	public WornItems(WornItems wornItems) {
		this.group = wornItems.group;
		this.copyFrom(wornItems);
	}

	public void copyFrom(WornItems wornItems) {
		if (this.group != wornItems.group) {
			throw new RuntimeException("group=" + this.group.id + " other.group=" + wornItems.group.id);
		} else {
			this.items.clear();
			this.items.addAll(wornItems.items);
		}
	}

	public BodyLocationGroup getBodyLocationGroup() {
		return this.group;
	}

	public WornItem get(int int1) {
		return (WornItem)this.items.get(int1);
	}

	public void setItem(String string, InventoryItem inventoryItem) {
		this.group.checkValid(string);
		int int1;
		if (!this.group.isMultiItem(string)) {
			int1 = this.indexOf(string);
			if (int1 != -1) {
				this.items.remove(int1);
			}
		}

		WornItem wornItem;
		for (int1 = 0; int1 < this.items.size(); ++int1) {
			wornItem = (WornItem)this.items.get(int1);
			if (this.group.isExclusive(string, wornItem.location)) {
				this.items.remove(int1--);
			}
		}

		if (inventoryItem != null) {
			this.remove(inventoryItem);
			int1 = this.items.size();
			for (int int2 = 0; int2 < this.items.size(); ++int2) {
				WornItem wornItem2 = (WornItem)this.items.get(int2);
				if (this.group.indexOf(wornItem2.getLocation()) > this.group.indexOf(string)) {
					int1 = int2;
					break;
				}
			}

			wornItem = new WornItem(string, inventoryItem);
			this.items.add(int1, wornItem);
		}
	}

	public InventoryItem getItem(String string) {
		this.group.checkValid(string);
		int int1 = this.indexOf(string);
		return int1 == -1 ? null : ((WornItem)this.items.get(int1)).item;
	}

	public InventoryItem getItemByIndex(int int1) {
		return int1 >= 0 && int1 < this.items.size() ? ((WornItem)this.items.get(int1)).getItem() : null;
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
		return int1 == -1 ? null : ((WornItem)this.items.get(int1)).getLocation();
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
			consumer.accept((WornItem)this.items.get(int1));
		}
	}

	public void setFromItemVisuals(ItemVisuals itemVisuals) {
		this.clear();
		for (int int1 = 0; int1 < itemVisuals.size(); ++int1) {
			ItemVisual itemVisual = (ItemVisual)itemVisuals.get(int1);
			String string = itemVisual.getItemType();
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(string);
			if (inventoryItem != null) {
				if (inventoryItem.getVisual() != null) {
					inventoryItem.getVisual().copyFrom(itemVisual);
					inventoryItem.synchWithVisual();
				}

				if (inventoryItem instanceof Clothing && !StringUtils.isNullOrWhitespace(inventoryItem.getBodyLocation())) {
					this.setItem(inventoryItem.getBodyLocation(), inventoryItem);
				} else if (inventoryItem instanceof InventoryContainer && !StringUtils.isNullOrWhitespace(((InventoryContainer)inventoryItem).canBeEquipped())) {
					this.setItem(((InventoryContainer)inventoryItem).canBeEquipped(), inventoryItem);
				}
			}
		}
	}

	public void getItemVisuals(ItemVisuals itemVisuals) {
		itemVisuals.clear();
		for (int int1 = 0; int1 < this.items.size(); ++int1) {
			InventoryItem inventoryItem = ((WornItem)this.items.get(int1)).getItem();
			ItemVisual itemVisual = inventoryItem.getVisual();
			if (itemVisual != null) {
				itemVisual.setInventoryItem(inventoryItem);
				itemVisuals.add(itemVisual);
			}
		}
	}

	public void addItemsToItemContainer(ItemContainer itemContainer) {
		for (int int1 = 0; int1 < this.items.size(); ++int1) {
			InventoryItem inventoryItem = ((WornItem)this.items.get(int1)).getItem();
			int int2 = inventoryItem.getVisual().getHolesNumber();
			inventoryItem.setCondition(inventoryItem.getConditionMax() - int2 * 3);
			itemContainer.AddItem(inventoryItem);
		}
	}

	private int indexOf(String string) {
		for (int int1 = 0; int1 < this.items.size(); ++int1) {
			WornItem wornItem = (WornItem)this.items.get(int1);
			if (wornItem.location.equals(string)) {
				return int1;
			}
		}

		return -1;
	}

	private int indexOf(InventoryItem inventoryItem) {
		for (int int1 = 0; int1 < this.items.size(); ++int1) {
			WornItem wornItem = (WornItem)this.items.get(int1);
			if (wornItem.getItem() == inventoryItem) {
				return int1;
			}
		}

		return -1;
	}
}
