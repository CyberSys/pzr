package zombie.core.skinnedmodel.model;

import java.util.Iterator;
import java.util.Map.Entry;
import zombie.util.list.PZArrayUtil;


public final class SkinningBoneHierarchy {
	private boolean m_boneHieararchyValid = false;
	private SkinningBone[] m_allBones = null;
	private SkinningBone[] m_rootBones = null;

	public boolean isValid() {
		return this.m_boneHieararchyValid;
	}

	public void buildBoneHiearchy(SkinningData skinningData) {
		this.m_rootBones = new SkinningBone[0];
		this.m_allBones = new SkinningBone[skinningData.numBones()];
		PZArrayUtil.arrayPopulate(this.m_allBones, SkinningBone::new);
		int int1;
		SkinningBone skinningBone;
		for (Iterator iterator = skinningData.BoneIndices.entrySet().iterator(); iterator.hasNext(); skinningBone.Children = new SkinningBone[0]) {
			Entry entry = (Entry)iterator.next();
			int1 = (Integer)entry.getValue();
			String string = (String)entry.getKey();
			skinningBone = this.m_allBones[int1];
			skinningBone.Index = int1;
			skinningBone.Name = string;
		}

		for (int int2 = 0; int2 < skinningData.numBones(); ++int2) {
			SkinningBone skinningBone2 = this.m_allBones[int2];
			int1 = skinningData.getParentBoneIdx(int2);
			if (int1 > -1) {
				skinningBone2.Parent = this.m_allBones[int1];
				skinningBone2.Parent.Children = (SkinningBone[])PZArrayUtil.add(skinningBone2.Parent.Children, skinningBone2);
			} else {
				this.m_rootBones = (SkinningBone[])PZArrayUtil.add(this.m_rootBones, skinningBone2);
			}
		}

		this.m_boneHieararchyValid = true;
	}

	public int numRootBones() {
		return this.m_rootBones.length;
	}

	public SkinningBone getBoneAt(int int1) {
		return this.m_allBones[int1];
	}

	public SkinningBone getRootBoneAt(int int1) {
		return this.m_rootBones[int1];
	}
}
