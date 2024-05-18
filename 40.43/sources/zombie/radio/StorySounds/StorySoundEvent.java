package zombie.radio.StorySounds;

import java.util.ArrayList;


public class StorySoundEvent {
	protected String name;
	protected ArrayList eventSounds;

	public StorySoundEvent() {
		this("Unnamed");
	}

	public StorySoundEvent(String string) {
		this.eventSounds = new ArrayList();
		this.name = string;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String string) {
		this.name = string;
	}

	public ArrayList getEventSounds() {
		return this.eventSounds;
	}

	public void setEventSounds(ArrayList arrayList) {
		this.eventSounds = arrayList;
	}
}
