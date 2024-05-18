package zombie.scripting.objects;

import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.interfaces.Activatable;


public class ScriptActivatable extends BaseScriptObject {
	public int x;
	public int y;
	public int z;
	public String name;
	public String type;
	Activatable ac;

	public void Load(String string, String[] stringArray) {
		this.name = string;
		this.type = stringArray[0].trim();
		this.x = Integer.parseInt(stringArray[1].trim());
		this.y = Integer.parseInt(stringArray[2].trim());
		this.z = Integer.parseInt(stringArray[3].trim());
	}

	public boolean IsActivated() {
		Activatable activatable = this.getActual();
		return activatable == null ? false : activatable.Activated();
	}

	public void Toggle() {
		Activatable activatable = this.getActual();
		if (activatable != null) {
			activatable.Toggle();
		}
	}

	public Activatable getActual() {
		if (this.ac != null) {
			return this.ac;
		} else {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);
			for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
				IsoObject object = (IsoObject)square.getObjects().get(int1);
				if (object instanceof Activatable && ((Activatable)object).getActivatableType().equals(this.type)) {
					this.ac = (Activatable)object;
					return (Activatable)object;
				}
			}

			return null;
		}
	}
}
