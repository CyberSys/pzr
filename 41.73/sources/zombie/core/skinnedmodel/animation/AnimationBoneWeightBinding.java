package zombie.core.skinnedmodel.animation;

import java.util.function.Consumer;
import zombie.core.skinnedmodel.advancedanimation.AnimBoneWeight;
import zombie.core.skinnedmodel.model.SkinningBone;


public class AnimationBoneWeightBinding extends AnimationBoneBinding {
	private float m_weight;
	private boolean m_includeDescendants;

	public AnimationBoneWeightBinding(AnimBoneWeight animBoneWeight) {
		this(animBoneWeight.boneName, animBoneWeight.weight, animBoneWeight.includeDescendants);
	}

	public AnimationBoneWeightBinding(String string, float float1, boolean boolean1) {
		super(string);
		this.m_weight = 1.0F;
		this.m_includeDescendants = true;
		this.m_weight = float1;
		this.m_includeDescendants = boolean1;
	}

	public float getWeight() {
		return this.m_weight;
	}

	public void setWeight(float float1) {
		this.m_weight = float1;
	}

	public boolean getIncludeDescendants() {
		return this.m_includeDescendants;
	}

	public void setIncludeDescendants(boolean boolean1) {
		this.m_includeDescendants = boolean1;
	}

	public void forEachDescendant(Consumer consumer) {
		if (this.m_includeDescendants) {
			SkinningBone skinningBone = this.getBone();
			if (skinningBone != null) {
				skinningBone.forEachDescendant(consumer);
			}
		}
	}
}
