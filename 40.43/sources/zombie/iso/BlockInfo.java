package zombie.iso;


public class BlockInfo {
	public boolean ThroughDoor = false;
	public boolean ThroughStairs = false;
	public boolean ThroughWindow = false;

	public boolean isThroughDoor() {
		return this.ThroughDoor;
	}

	public boolean isThroughStairs() {
		return this.ThroughStairs;
	}

	public boolean isThroughWindow() {
		return this.ThroughWindow;
	}
}
