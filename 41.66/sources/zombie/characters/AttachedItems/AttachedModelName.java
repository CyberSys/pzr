package zombie.characters.AttachedItems;


public final class AttachedModelName {
	public String attachmentName;
	public String modelName;
	public float bloodLevel;

	public AttachedModelName(AttachedModelName attachedModelName) {
		this.attachmentName = attachedModelName.attachmentName;
		this.modelName = attachedModelName.modelName;
		this.bloodLevel = attachedModelName.bloodLevel;
	}

	public AttachedModelName(String string, String string2, float float1) {
		this.attachmentName = string;
		this.modelName = string2;
		this.bloodLevel = float1;
	}
}
