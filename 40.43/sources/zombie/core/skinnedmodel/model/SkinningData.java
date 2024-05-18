package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;


public class SkinningData {
	public HashMap AnimationClips;
	public List BindPose;
	public List InverseBindPose;
	public List BoneOffset = new ArrayList();
	public List SkeletonHierarchy;
	public HashMap BoneIndices;

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

	public void BindPoseCopyTo(Matrix4f[] matrix4fArray, int int1) {
		for (int int2 = int1; int2 < this.BindPose.size(); ++int2) {
			matrix4fArray[int2] = new Matrix4f((Matrix4fc)this.BindPose.get(int2));
		}
	}
}
