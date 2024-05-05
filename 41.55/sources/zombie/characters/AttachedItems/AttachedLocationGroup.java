package zombie.characters.AttachedItems;

import java.util.ArrayList;


public final class AttachedLocationGroup {
	protected final String id;
	protected final ArrayList locations = new ArrayList();

	public AttachedLocationGroup(String string) {
		if (string == null) {
			throw new NullPointerException("id is null");
		} else if (string.isEmpty()) {
			throw new IllegalArgumentException("id is empty");
		} else {
			this.id = string;
		}
	}

	public AttachedLocation getLocation(String string) {
		for (int int1 = 0; int1 < this.locations.size(); ++int1) {
			AttachedLocation attachedLocation = (AttachedLocation)this.locations.get(int1);
			if (attachedLocation.id.equals(string)) {
				return attachedLocation;
			}
		}

		return null;
	}

	public AttachedLocation getOrCreateLocation(String string) {
		AttachedLocation attachedLocation = this.getLocation(string);
		if (attachedLocation == null) {
			attachedLocation = new AttachedLocation(this, string);
			this.locations.add(attachedLocation);
		}

		return attachedLocation;
	}

	public AttachedLocation getLocationByIndex(int int1) {
		return int1 >= 0 && int1 < this.size() ? (AttachedLocation)this.locations.get(int1) : null;
	}

	public int size() {
		return this.locations.size();
	}

	public int indexOf(String string) {
		for (int int1 = 0; int1 < this.locations.size(); ++int1) {
			AttachedLocation attachedLocation = (AttachedLocation)this.locations.get(int1);
			if (attachedLocation.id.equals(string)) {
				return int1;
			}
		}

		return -1;
	}

	public void checkValid(String string) {
		if (string == null) {
			throw new NullPointerException("locationId is null");
		} else if (string.isEmpty()) {
			throw new IllegalArgumentException("locationId is empty");
		} else if (this.indexOf(string) == -1) {
			throw new RuntimeException("no such location \"" + string + "\"");
		}
	}
}
