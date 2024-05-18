package zombie.scripting.objects;

import zombie.characters.Talker;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;


public class ScriptTalker extends BaseScriptObject {
	public int x;
	public int y;
	public int z;
	public String name;
	public String type;
	Talker ac;

	public void Load(String string, String[] stringArray) {
		this.name = string;
		this.type = stringArray[0].trim();
		this.x = Integer.parseInt(stringArray[1].trim());
		this.y = Integer.parseInt(stringArray[2].trim());
		this.z = Integer.parseInt(stringArray[3].trim());
	}

	public Talker getActual() {
		if (this.ac != null) {
			return this.ac;
		} else {
			IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);
			for (int int1 = 0; int1 < square.getObjects().size(); ++int1) {
				IsoObject object = (IsoObject)square.getObjects().get(int1);
				if (object instanceof Talker && ((Talker)object).getTalkerType().equals(this.type)) {
					this.ac = (Talker)object;
					return (Talker)object;
				}
			}

			return null;
		}
	}
}
