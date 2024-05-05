package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSprite;


public class IsoClothingWasher extends IsoObject {
	private final ClothingWasherLogic m_logic = new ClothingWasherLogic(this);

	public IsoClothingWasher(IsoCell cell) {
		super(cell);
	}

	public IsoClothingWasher(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		super(cell, square, sprite);
	}

	public String getObjectName() {
		return "ClothingWasher";
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.m_logic.load(byteBuffer, int1, boolean1);
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		this.m_logic.save(byteBuffer, boolean1);
	}

	public void update() {
		this.m_logic.update();
	}

	public void addToWorld() {
		IsoCell cell = this.getCell();
		cell.addToProcessIsoObject(this);
	}

	public void removeFromWorld() {
		super.removeFromWorld();
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		this.m_logic.saveChange(string, kahluaTable, byteBuffer);
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		this.m_logic.loadChange(string, byteBuffer);
	}

	public boolean isItemAllowedInContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		return this.m_logic.isItemAllowedInContainer(itemContainer, inventoryItem);
	}

	public boolean isRemoveItemAllowedFromContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		return this.m_logic.isRemoveItemAllowedFromContainer(itemContainer, inventoryItem);
	}

	public boolean isActivated() {
		return this.m_logic.isActivated();
	}

	public void setActivated(boolean boolean1) {
		this.m_logic.setActivated(boolean1);
	}
}
