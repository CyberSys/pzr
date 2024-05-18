package zombie.scripting.objects;

import zombie.inventory.ItemContainer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;


public class ScriptContainer extends BaseScriptObject {
	public int x;
	public int y;
	public int z;
	public String name;
	public String type;
	ItemContainer ac;

	public void Load(String string, String[] stringArray) {
		this.name = string;
		this.type = stringArray[0].trim();
		this.x = Integer.parseInt(stringArray[1].trim());
		this.y = Integer.parseInt(stringArray[2].trim());
		this.z = Integer.parseInt(stringArray[3].trim());
	}

	public boolean HasInventory(String string) {
		ItemContainer itemContainer = this.getActual();
		return itemContainer == null ? false : itemContainer.contains(string);
	}

	public ItemContainer getActual() {
		if (this.ac != null) {
			return this.ac;
		} else {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);
			for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
				IsoObject object = (IsoObject)square.getObjects().get(int1);
				if (object.container != null && object.container.type.equals(this.type)) {
					this.ac = object.container;
					return object.container;
				}
			}

			return null;
		}
	}
}
