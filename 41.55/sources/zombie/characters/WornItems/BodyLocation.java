package zombie.characters.WornItems;

import java.util.ArrayList;


public final class BodyLocation {
	protected final BodyLocationGroup group;
	protected final String id;
	protected final ArrayList aliases = new ArrayList();
	protected final ArrayList exclusive = new ArrayList();
	protected final ArrayList hideModel = new ArrayList();
	protected boolean bMultiItem = false;

	public BodyLocation(BodyLocationGroup bodyLocationGroup, String string) {
		this.checkId(string, "id");
		this.group = bodyLocationGroup;
		this.id = string;
	}

	public BodyLocation addAlias(String string) {
		this.checkId(string, "alias");
		if (this.aliases.contains(string)) {
			return this;
		} else {
			this.aliases.add(string);
			return this;
		}
	}

	public BodyLocation setExclusive(String string) {
		this.checkId(string, "otherId");
		if (this.aliases.contains(string)) {
			return this;
		} else if (this.exclusive.contains(string)) {
			return this;
		} else {
			this.exclusive.add(string);
			return this;
		}
	}

	public BodyLocation setHideModel(String string) {
		this.checkId(string, "otherId");
		if (this.hideModel.contains(string)) {
			return this;
		} else {
			this.hideModel.add(string);
			return this;
		}
	}

	public boolean isMultiItem() {
		return this.bMultiItem;
	}

	public BodyLocation setMultiItem(boolean boolean1) {
		this.bMultiItem = boolean1;
		return this;
	}

	public boolean isHideModel(String string) {
		return this.hideModel.contains(string);
	}

	public boolean isExclusive(String string) {
		return this.group.isExclusive(this.id, string);
	}

	public boolean isID(String string) {
		return this.id.equals(string) || this.aliases.contains(string);
	}

	private void checkId(String string, String string2) {
		if (string == null) {
			throw new NullPointerException(string2 + " is null");
		} else if (string.isEmpty()) {
			throw new IllegalArgumentException(string2 + " is empty");
		}
	}
}
