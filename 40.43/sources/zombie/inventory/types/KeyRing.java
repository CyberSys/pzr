package zombie.inventory.types;

import java.util.ArrayList;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemType;
import zombie.scripting.objects.Item;


public class KeyRing extends InventoryItem {
	private ArrayList keys = new ArrayList();

	public KeyRing(String string, String string2, String string3, String string4) {
		super(string, string2, string3, string4);
		this.cat = ItemType.KeyRing;
	}

	public int getSaveType() {
		return Item.Type.KeyRing.ordinal();
	}

	public void addKey(Key key) {
		this.keys.add(key);
	}

	public boolean containsKeyId(int int1) {
		for (int int2 = 0; int2 < this.keys.size(); ++int2) {
			if (((Key)this.keys.get(int2)).getKeyId() == int1) {
				return true;
			}
		}

		return false;
	}

	public String getCategory() {
		return this.mainCategory != null ? this.mainCategory : "Key Ring";
	}

	public ArrayList getKeys() {
		return this.keys;
	}

	public void setKeys(ArrayList arrayList) {
		this.keys = arrayList;
	}
}
