package zombie.core.skinnedmodel.animation;

import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.util.StringUtils;


public class AnimationBoneBinding {
	public final String boneName;
	private SkinningBone m_bone = null;
	private SkinningData m_skinningData;

	public AnimationBoneBinding(String string) {
		this.boneName = string;
	}

	public SkinningData getSkinningData() {
		return this.m_skinningData;
	}

	public void setSkinningData(SkinningData skinningData) {
		if (this.m_skinningData != skinningData) {
			this.m_skinningData = skinningData;
			this.m_bone = null;
		}
	}

	public SkinningBone getBone() {
		if (this.m_bone == null) {
			this.initBone();
		}

		return this.m_bone;
	}

	private void initBone() {
		if (this.m_skinningData == null) {
			this.m_bone = null;
		} else {
			this.m_bone = this.m_skinningData.getBone(this.boneName);
		}
	}

	public String toString() {
		String string = System.lineSeparator();
		String string2 = this.getClass().getName();
		return string2 + string + "{" + string + "\tboneName:\"" + this.boneName + "\"" + string + "\tm_bone:" + StringUtils.indent(String.valueOf(this.m_bone)) + string + "}";
	}
}
