package zombie.inventory.types;

import zombie.inventory.InventoryItem;
import zombie.scripting.objects.Item;


public class ComboItem extends InventoryItem {

	public ComboItem(String string, String string2, String string3, String string4) {
		super(string, string2, string3, string4);
	}

	public ComboItem(String string, String string2, String string3, Item item) {
		super(string, string2, string3, item);
	}

	public int getSaveType() {
		return Item.Type.Normal.ordinal();
	}
}
