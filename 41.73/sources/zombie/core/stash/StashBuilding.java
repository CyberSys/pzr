package zombie.core.stash;


public final class StashBuilding {
	public int buildingX;
	public int buildingY;
	public String stashName;

	public StashBuilding(String string, int int1, int int2) {
		this.stashName = string;
		this.buildingX = int1;
		this.buildingY = int2;
	}

	public String getName() {
		return this.stashName;
	}
}
