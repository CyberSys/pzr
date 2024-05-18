package zombie.scripting.objects;

import zombie.iso.IsoGridSquare;
import zombie.iso.areas.IsoRoom;


public class Room extends BaseScriptObject {
	public int x;
	public int y;
	public int z;
	public String name;
	public IsoRoom room = null;

	public Room() {
	}

	public Room(String string, IsoRoom room) {
		this.name = string;
		this.room = room;
		IsoGridSquare square = (IsoGridSquare)room.TileList.get(0);
		this.x = square.getX();
		this.y = square.getY();
		this.z = square.getZ();
	}

	public void Load(String string, String[] stringArray) {
		this.name = string;
		this.x = Integer.parseInt(stringArray[0].trim());
		this.y = Integer.parseInt(stringArray[1].trim());
		this.z = Integer.parseInt(stringArray[2].trim());
	}
}
