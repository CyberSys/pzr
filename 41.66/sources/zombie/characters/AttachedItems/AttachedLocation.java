package zombie.characters.AttachedItems;


public final class AttachedLocation {
	protected final AttachedLocationGroup group;
	protected final String id;
	protected String attachmentName;

	public AttachedLocation(AttachedLocationGroup attachedLocationGroup, String string) {
		if (string == null) {
			throw new NullPointerException("id is null");
		} else if (string.isEmpty()) {
			throw new IllegalArgumentException("id is empty");
		} else {
			this.group = attachedLocationGroup;
			this.id = string;
		}
	}

	public void setAttachmentName(String string) {
		if (this.id == null) {
			throw new NullPointerException("attachmentName is null");
		} else if (this.id.isEmpty()) {
			throw new IllegalArgumentException("attachmentName is empty");
		} else {
			this.attachmentName = string;
		}
	}

	public String getAttachmentName() {
		return this.attachmentName;
	}

	public String getId() {
		return this.id;
	}
}
