package zombie.characters.WornItems;

import java.util.ArrayList;


public final class BodyLocationGroup {
	protected final String id;
	protected final ArrayList locations = new ArrayList();

	public BodyLocationGroup(String string) {
		if (string == null) {
			throw new NullPointerException("id is null");
		} else if (string.isEmpty()) {
			throw new IllegalArgumentException("id is empty");
		} else {
			this.id = string;
		}
	}

	public BodyLocation getLocation(String string) {
		for (int int1 = 0; int1 < this.locations.size(); ++int1) {
			BodyLocation bodyLocation = (BodyLocation)this.locations.get(int1);
			if (bodyLocation.isID(string)) {
				return bodyLocation;
			}
		}

		return null;
	}

	public BodyLocation getLocationNotNull(String string) {
		BodyLocation bodyLocation = this.getLocation(string);
		if (bodyLocation == null) {
			throw new RuntimeException("unknown location \"" + string + "\"");
		} else {
			return bodyLocation;
		}
	}

	public BodyLocation getOrCreateLocation(String string) {
		BodyLocation bodyLocation = this.getLocation(string);
		if (bodyLocation == null) {
			bodyLocation = new BodyLocation(this, string);
			this.locations.add(bodyLocation);
		}

		return bodyLocation;
	}

	public BodyLocation getLocationByIndex(int int1) {
		return int1 >= 0 && int1 < this.size() ? (BodyLocation)this.locations.get(int1) : null;
	}

	public int size() {
		return this.locations.size();
	}

	public void setExclusive(String string, String string2) {
		BodyLocation bodyLocation = this.getLocationNotNull(string);
		BodyLocation bodyLocation2 = this.getLocationNotNull(string2);
		bodyLocation.setExclusive(string2);
		bodyLocation2.setExclusive(string);
	}

	public boolean isExclusive(String string, String string2) {
		BodyLocation bodyLocation = this.getLocationNotNull(string);
		this.checkValid(string2);
		return bodyLocation.exclusive.contains(string2);
	}

	public void setHideModel(String string, String string2) {
		BodyLocation bodyLocation = this.getLocationNotNull(string);
		this.checkValid(string2);
		bodyLocation.setHideModel(string2);
	}

	public boolean isHideModel(String string, String string2) {
		BodyLocation bodyLocation = this.getLocationNotNull(string);
		this.checkValid(string2);
		return bodyLocation.isHideModel(string2);
	}

	public int indexOf(String string) {
		for (int int1 = 0; int1 < this.locations.size(); ++int1) {
			BodyLocation bodyLocation = (BodyLocation)this.locations.get(int1);
			if (bodyLocation.isID(string)) {
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
			throw new RuntimeException("unknown location \"" + string + "\"");
		}
	}

	public void setMultiItem(String string, boolean boolean1) {
		BodyLocation bodyLocation = this.getLocationNotNull(string);
		bodyLocation.setMultiItem(boolean1);
	}

	public boolean isMultiItem(String string) {
		BodyLocation bodyLocation = this.getLocationNotNull(string);
		return bodyLocation.isMultiItem();
	}

	public ArrayList getAllLocations() {
		return this.locations;
	}
}
