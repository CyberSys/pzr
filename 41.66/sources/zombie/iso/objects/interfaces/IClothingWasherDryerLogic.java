package zombie.iso.objects.interfaces;

import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;


public interface IClothingWasherDryerLogic {

	void update();

	void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer);

	void loadChange(String string, ByteBuffer byteBuffer);

	ItemContainer getContainer();

	boolean isItemAllowedInContainer(ItemContainer itemContainer, InventoryItem inventoryItem);

	boolean isRemoveItemAllowedFromContainer(ItemContainer itemContainer, InventoryItem inventoryItem);

	boolean isActivated();

	void setActivated(boolean boolean1);

	void switchModeOn();

	void switchModeOff();
}
