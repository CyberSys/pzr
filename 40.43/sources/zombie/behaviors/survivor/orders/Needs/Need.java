package zombie.behaviors.survivor.orders.Needs;


public class Need {
	public int priority;
	public String item;
	public int numToSatisfy = 1;

	public Need(String string, int int1) {
		this.item = string;
		this.priority = int1;
	}
}
