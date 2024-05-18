package zombie.scripting.objects;


public class Waypoint extends BaseScriptObject {
	public int x;
	public int y;
	public int z;
	public String name;

	public void Load(String string, String[] stringArray) {
		this.name = new String(string);
		this.x = Integer.parseInt(stringArray[0].trim());
		this.y = Integer.parseInt(stringArray[1].trim());
		this.z = Integer.parseInt(stringArray[2].trim());
	}
}
