package zombie.core.skinnedmodel.advancedanimation;


public final class AnimBoneWeight {
	public String boneName;
	public float weight = 1.0F;
	public boolean includeDescendants = true;

	public AnimBoneWeight() {
	}

	public AnimBoneWeight(String string, float float1) {
		this.boneName = string;
		this.weight = float1;
		this.includeDescendants = true;
	}
}
