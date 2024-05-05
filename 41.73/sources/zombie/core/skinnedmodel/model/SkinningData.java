package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.lwjgl.util.vector.Matrix4f;


public final class SkinningData {
	public HashMap AnimationClips;
	public List BindPose;
	public List InverseBindPose;
	public List BoneOffset = new ArrayList();
	public List SkeletonHierarchy;
	public HashMap BoneIndices;
	private SkinningBoneHierarchy m_boneHieararchy = null;

	public SkinningData(HashMap hashMap, List list, List list2, List list3, List list4, HashMap hashMap2) {
		this.AnimationClips = hashMap;
		this.BindPose = list;
		this.InverseBindPose = list2;
		this.SkeletonHierarchy = list4;
		for (int int1 = 0; int1 < list4.size(); ++int1) {
			Matrix4f matrix4f = (Matrix4f)list3.get(int1);
			this.BoneOffset.add(matrix4f);
		}

		this.BoneIndices = hashMap2;
	}

	private void validateBoneHierarchy() {
		if (this.m_boneHieararchy == null) {
			this.m_boneHieararchy = new SkinningBoneHierarchy();
			this.m_boneHieararchy.buildBoneHiearchy(this);
		}
	}

	public int numBones() {
		return this.SkeletonHierarchy.size();
	}

	public int numRootBones() {
		return this.getBoneHieararchy().numRootBones();
	}

	public int getParentBoneIdx(int int1) {
		return (Integer)this.SkeletonHierarchy.get(int1);
	}

	public SkinningBone getBoneAt(int int1) {
		return this.getBoneHieararchy().getBoneAt(int1);
	}

	public SkinningBone getBone(String string) {
		Integer integer = (Integer)this.BoneIndices.get(string);
		return integer == null ? null : this.getBoneAt(integer);
	}

	public SkinningBone getRootBoneAt(int int1) {
		return this.getBoneHieararchy().getRootBoneAt(int1);
	}

	public SkinningBoneHierarchy getBoneHieararchy() {
		this.validateBoneHierarchy();
		return this.m_boneHieararchy;
	}
}
