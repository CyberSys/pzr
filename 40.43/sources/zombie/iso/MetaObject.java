package zombie.iso;


public class MetaObject {
	int type;
	int x;
	int y;
	RoomDef def;
	boolean bUsed = false;

	public MetaObject(int int1, int int2, int int3, RoomDef roomDef) {
		this.type = int1;
		this.x = int2;
		this.y = int3;
		this.def = roomDef;
	}

	public RoomDef getRoom() {
		return this.def;
	}

	public void setUsed(boolean boolean1) {
		this.bUsed = boolean1;
	}

	public boolean getUsed() {
		return this.bUsed;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getType() {
		return this.type;
	}
}
