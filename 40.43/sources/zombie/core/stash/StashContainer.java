package zombie.core.stash;


public class StashContainer {
	public String room;
	public String containerSprite;
	public String containerType;
	public int contX = -1;
	public int contY = -1;
	public int contZ = -1;
	public String containerItem;

	public StashContainer(String string, String string2, String string3) {
		if (string == null) {
			this.room = "all";
		} else {
			this.room = string;
		}

		this.containerSprite = string2;
		this.containerType = string3;
	}
}
