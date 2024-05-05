package zombie.characters;

import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.WornItems;
import zombie.inventory.InventoryItem;


public interface ILuaGameCharacterClothing {

	void dressInNamedOutfit(String string);

	void dressInPersistentOutfit(String string);

	void dressInPersistentOutfitID(int int1);

	String getOutfitName();

	WornItems getWornItems();

	void setWornItems(WornItems wornItems);

	InventoryItem getWornItem(String string);

	void setWornItem(String string, InventoryItem inventoryItem);

	void removeWornItem(InventoryItem inventoryItem);

	void clearWornItems();

	BodyLocationGroup getBodyLocationGroup();

	void setClothingItem_Head(InventoryItem inventoryItem);

	void setClothingItem_Torso(InventoryItem inventoryItem);

	void setClothingItem_Back(InventoryItem inventoryItem);

	void setClothingItem_Hands(InventoryItem inventoryItem);

	void setClothingItem_Legs(InventoryItem inventoryItem);

	void setClothingItem_Feet(InventoryItem inventoryItem);

	void Dressup(SurvivorDesc survivorDesc);
}
