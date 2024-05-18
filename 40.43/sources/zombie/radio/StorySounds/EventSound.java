package zombie.radio.StorySounds;

import java.util.ArrayList;
import zombie.core.Color;


public class EventSound {
	protected String name;
	protected Color color;
	protected ArrayList dataPoints;
	protected ArrayList storySounds;

	public EventSound() {
		this("Unnamed");
	}

	public EventSound(String string) {
		this.color = new Color(1.0F, 1.0F, 1.0F);
		this.dataPoints = new ArrayList();
		this.storySounds = new ArrayList();
		this.name = string;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String string) {
		this.name = string;
	}

	public Color getColor() {
		return this.color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public ArrayList getDataPoints() {
		return this.dataPoints;
	}

	public void setDataPoints(ArrayList arrayList) {
		this.dataPoints = arrayList;
	}

	public ArrayList getStorySounds() {
		return this.storySounds;
	}

	public void setStorySounds(ArrayList arrayList) {
		this.storySounds = arrayList;
	}
}
