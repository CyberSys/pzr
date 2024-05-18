package zombie.scripting.objects;


public class Zone extends BaseScriptObject {
	public int x;
	public int y;
	public int x2;
	public int y2;
	public int z = 0;
	public String name;

	public Zone() {
	}

	public Zone(String string, int int1, int int2, int int3, int int4) {
		this.name = new String(string);
		this.x = int1;
		this.y = int2;
		this.x2 = int3;
		this.y2 = int4;
	}

	public Zone(String string, int int1, int int2, int int3, int int4, int int5) {
		this.name = new String(string);
		this.x = int1;
		this.y = int2;
		this.x2 = int3;
		this.y2 = int4;
		this.z = int5;
	}

	public void Load(String string, String[] stringArray) {
		this.name = new String(string);
		this.x = Integer.parseInt(stringArray[0].trim());
		this.y = Integer.parseInt(stringArray[1].trim());
		this.x2 = Integer.parseInt(stringArray[2].trim());
		this.y2 = Integer.parseInt(stringArray[3].trim());
		if (stringArray.length > 4) {
			this.z = Integer.parseInt(stringArray[4].trim());
		}
	}
}
