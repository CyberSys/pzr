package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.core.math.PZMath;
import zombie.core.properties.PropertyContainer;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSprite;


public class IsoStackedWasherDryer extends IsoObject {
	private final ClothingWasherLogic m_washer = new ClothingWasherLogic(this);
	private final ClothingDryerLogic m_dryer = new ClothingDryerLogic(this);

	public IsoStackedWasherDryer(IsoCell cell) {
		super(cell);
	}

	public IsoStackedWasherDryer(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		super(cell, square, sprite);
	}

	public String getObjectName() {
		return "StackedWasherDryer";
	}

	public void createContainersFromSpriteProperties() {
		super.createContainersFromSpriteProperties();
		PropertyContainer propertyContainer = this.getProperties();
		if (propertyContainer != null) {
			ItemContainer itemContainer;
			if (this.getContainerByType("clothingwasher") == null) {
				itemContainer = new ItemContainer("clothingwasher", this.getSquare(), this);
				if (propertyContainer.Is("ContainerCapacity")) {
					itemContainer.Capacity = PZMath.tryParseInt(propertyContainer.Val("ContainerCapacity"), 20);
				}

				if (this.getContainer() == null) {
					this.setContainer(itemContainer);
				} else {
					this.addSecondaryContainer(itemContainer);
				}
			}

			if (this.getContainerByType("clothingdryer") == null) {
				itemContainer = new ItemContainer("clothingdryer", this.getSquare(), this);
				if (propertyContainer.Is("ContainerCapacity")) {
					itemContainer.Capacity = PZMath.tryParseInt(propertyContainer.Val("ContainerCapacity"), 20);
				}

				if (this.getContainer() == null) {
					this.setContainer(itemContainer);
				} else {
					this.addSecondaryContainer(itemContainer);
				}
			}
		}
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.m_washer.load(byteBuffer, int1, boolean1);
		this.m_dryer.load(byteBuffer, int1, boolean1);
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		this.m_washer.save(byteBuffer, boolean1);
		this.m_dryer.save(byteBuffer, boolean1);
	}

	public void update() {
		this.m_washer.update();
		this.m_dryer.update();
	}

	public void addToWorld() {
		IsoCell cell = this.getCell();
		cell.addToProcessIsoObject(this);
	}

	public void removeFromWorld() {
		super.removeFromWorld();
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		this.m_washer.saveChange(string, kahluaTable, byteBuffer);
		this.m_dryer.saveChange(string, kahluaTable, byteBuffer);
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		this.m_washer.loadChange(string, byteBuffer);
		this.m_dryer.loadChange(string, byteBuffer);
	}

	public boolean isItemAllowedInContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		return this.m_washer.isItemAllowedInContainer(itemContainer, inventoryItem) || this.m_dryer.isItemAllowedInContainer(itemContainer, inventoryItem);
	}

	public boolean isRemoveItemAllowedFromContainer(ItemContainer itemContainer, InventoryItem inventoryItem) {
		return this.m_washer.isRemoveItemAllowedFromContainer(itemContainer, inventoryItem) || this.m_dryer.isRemoveItemAllowedFromContainer(itemContainer, inventoryItem);
	}

	public boolean isWasherActivated() {
		return this.m_washer.isActivated();
	}

	public void setWasherActivated(boolean boolean1) {
		this.m_washer.setActivated(boolean1);
	}

	public boolean isDryerActivated() {
		return this.m_dryer.isActivated();
	}

	public void setDryerActivated(boolean boolean1) {
		this.m_dryer.setActivated(boolean1);
	}
}
