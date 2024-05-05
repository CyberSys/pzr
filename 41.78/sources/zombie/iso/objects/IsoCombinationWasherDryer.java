package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaEventManager;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.objects.interfaces.IClothingWasherDryerLogic;
import zombie.iso.sprite.IsoSprite;


public class IsoCombinationWasherDryer extends IsoObject {
	private final ClothingWasherLogic m_washer = new ClothingWasherLogic(this);
	private final ClothingDryerLogic m_dryer = new ClothingDryerLogic(this);
	private IClothingWasherDryerLogic m_logic;

	public IsoCombinationWasherDryer(IsoCell cell) {
		super(cell);
		this.m_logic = this.m_washer;
	}

	public IsoCombinationWasherDryer(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		super(cell, square, sprite);
		this.m_logic = this.m_washer;
	}

	public String getObjectName() {
		return "CombinationWasherDryer";
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.m_logic = (IClothingWasherDryerLogic)(byteBuffer.get() == 0 ? this.m_washer : this.m_dryer);
		this.m_washer.load(byteBuffer, int1, boolean1);
		this.m_dryer.load(byteBuffer, int1, boolean1);
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.put((byte)(this.m_logic == this.m_washer ? 0 : 1));
		this.m_washer.save(byteBuffer, boolean1);
		this.m_dryer.save(byteBuffer, boolean1);
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
		if ("mode".equals(string)) {
			byteBuffer.put((byte)(this.isModeWasher() ? 0 : 1));
		} else {
			this.m_logic.saveChange(string, kahluaTable, byteBuffer);
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		if ("mode".equals(string)) {
			if (byteBuffer.get() == 0) {
				this.setModeWasher();
			} else {
				this.setModeDryer();
			}
		} else {
			this.m_logic.loadChange(string, byteBuffer);
		}
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

	public void setModeWasher() {
		if (!this.isModeWasher()) {
			this.m_dryer.switchModeOff();
			this.m_logic = this.m_washer;
			this.getContainer().setType("clothingwasher");
			this.m_washer.switchModeOn();
			LuaEventManager.triggerEvent("OnContainerUpdate");
		}
	}

	public void setModeDryer() {
		if (!this.isModeDryer()) {
			this.m_washer.switchModeOff();
			this.m_logic = this.m_dryer;
			this.getContainer().setType("clothingdryer");
			this.m_dryer.switchModeOn();
			LuaEventManager.triggerEvent("OnContainerUpdate");
		}
	}

	public boolean isModeWasher() {
		return this.m_logic == this.m_washer;
	}

	public boolean isModeDryer() {
		return this.m_logic == this.m_dryer;
	}
}
