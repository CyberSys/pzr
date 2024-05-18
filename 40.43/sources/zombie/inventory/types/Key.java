package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemType;
import zombie.scripting.objects.Item;


public class Key extends InventoryItem {
	private int keyId = -1;
	private boolean padlock = false;
	private int numberOfKey = 0;
	private boolean digitalPadlock = false;
	public static final Key[] highlightDoor = new Key[4];

	public Key(String string, String string2, String string3, String string4) {
		super(string, string2, string3, string4);
		this.cat = ItemType.Key;
	}

	public int getSaveType() {
		return Item.Type.Key.ordinal();
	}

	public void takeKeyId() {
		if (this.getContainer() != null && this.getContainer().getSourceGrid() != null && this.getContainer().getSourceGrid().getBuilding() != null && this.getContainer().getSourceGrid().getBuilding().def != null) {
			this.setKeyId(this.getContainer().getSourceGrid().getBuilding().def.getKeyId());
		}
	}

	public static void setHighlightDoors(int int1, InventoryItem inventoryItem) {
		if (inventoryItem instanceof Key && !((Key)inventoryItem).isPadlock() && !((Key)inventoryItem).isDigitalPadlock()) {
			highlightDoor[int1] = (Key)inventoryItem;
		} else {
			highlightDoor[int1] = null;
		}
	}

	public int getKeyId() {
		return this.keyId;
	}

	public void setKeyId(int int1) {
		this.keyId = int1;
	}

	public String getCategory() {
		return this.mainCategory != null ? this.mainCategory : "Key";
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.putInt(this.getKeyId());
		byteBuffer.put((byte)this.numberOfKey);
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.setKeyId(byteBuffer.getInt());
		if (int1 >= 82) {
			this.numberOfKey = byteBuffer.get();
		}
	}

	public boolean isPadlock() {
		return this.padlock;
	}

	public void setPadlock(boolean boolean1) {
		this.padlock = boolean1;
	}

	public int getNumberOfKey() {
		return this.numberOfKey;
	}

	public void setNumberOfKey(int int1) {
		this.numberOfKey = int1;
	}

	public boolean isDigitalPadlock() {
		return this.digitalPadlock;
	}

	public void setDigitalPadlock(boolean boolean1) {
		this.digitalPadlock = boolean1;
	}
}
