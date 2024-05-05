package zombie.characters.AttachedItems;

import java.util.ArrayList;


public final class AttachedModelName {
	public String attachmentNameSelf;
	public String attachmentNameParent;
	public String modelName;
	public float bloodLevel;
	public ArrayList children;

	public AttachedModelName(AttachedModelName attachedModelName) {
		this.attachmentNameSelf = attachedModelName.attachmentNameSelf;
		this.attachmentNameParent = attachedModelName.attachmentNameParent;
		this.modelName = attachedModelName.modelName;
		this.bloodLevel = attachedModelName.bloodLevel;
		for (int int1 = 0; int1 < attachedModelName.getChildCount(); ++int1) {
			AttachedModelName attachedModelName2 = attachedModelName.getChildByIndex(int1);
			this.addChild(new AttachedModelName(attachedModelName2));
		}
	}

	public AttachedModelName(String string, String string2, float float1) {
		this.attachmentNameSelf = string;
		this.attachmentNameParent = string;
		this.modelName = string2;
		this.bloodLevel = float1;
	}

	public AttachedModelName(String string, String string2, String string3, float float1) {
		this.attachmentNameSelf = string;
		this.attachmentNameParent = string2;
		this.modelName = string3;
		this.bloodLevel = float1;
	}

	public void addChild(AttachedModelName attachedModelName) {
		if (this.children == null) {
			this.children = new ArrayList();
		}

		this.children.add(attachedModelName);
	}

	public int getChildCount() {
		return this.children == null ? 0 : this.children.size();
	}

	public AttachedModelName getChildByIndex(int int1) {
		return (AttachedModelName)this.children.get(int1);
	}
}
