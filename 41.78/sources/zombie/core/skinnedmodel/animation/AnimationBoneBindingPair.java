package zombie.core.skinnedmodel.animation;

import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.util.StringUtils;


public final class AnimationBoneBindingPair {
	public final AnimationBoneBinding boneBindingA;
	public final AnimationBoneBinding boneBindingB;

	public AnimationBoneBindingPair(String string, String string2) {
		this.boneBindingA = new AnimationBoneBinding(string);
		this.boneBindingB = new AnimationBoneBinding(string2);
	}

	public void setSkinningData(SkinningData skinningData) {
		this.boneBindingA.setSkinningData(skinningData);
		this.boneBindingB.setSkinningData(skinningData);
	}

	public SkinningBone getBoneA() {
		return this.boneBindingA.getBone();
	}

	public SkinningBone getBoneB() {
		return this.boneBindingB.getBone();
	}

	public boolean isValid() {
		return this.getBoneA() != null && this.getBoneB() != null;
	}

	public boolean matches(String string, String string2) {
		return StringUtils.equalsIgnoreCase(this.boneBindingA.boneName, string) && StringUtils.equalsIgnoreCase(this.boneBindingB.boneName, string2);
	}

	public int getBoneIdxA() {
		return getBoneIdx(this.getBoneA());
	}

	public int getBoneIdxB() {
		return getBoneIdx(this.getBoneB());
	}

	private static int getBoneIdx(SkinningBone skinningBone) {
		return skinningBone != null ? skinningBone.Index : -1;
	}

	public String toString() {
		String string = System.lineSeparator();
		String string2 = this.getClass().getName();
		return string2 + string + "{" + string + "\tboneBindingA:" + StringUtils.indent(String.valueOf(this.boneBindingA)) + string + "\tboneBindingB:" + StringUtils.indent(String.valueOf(this.boneBindingB)) + string + "}";
	}
}
