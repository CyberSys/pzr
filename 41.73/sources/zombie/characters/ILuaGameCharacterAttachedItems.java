package zombie.characters;

import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.AttachedItems.AttachedLocationGroup;
import zombie.inventory.InventoryItem;


public interface ILuaGameCharacterAttachedItems {

	AttachedItems getAttachedItems();

	void setAttachedItems(AttachedItems attachedItems);

	InventoryItem getAttachedItem(String string);

	void setAttachedItem(String string, InventoryItem inventoryItem);

	void removeAttachedItem(InventoryItem inventoryItem);

	void clearAttachedItems();

	AttachedLocationGroup getAttachedLocationGroup();
}
