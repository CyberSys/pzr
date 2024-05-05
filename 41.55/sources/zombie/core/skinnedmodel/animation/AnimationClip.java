package zombie.core.skinnedmodel.animation;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Quaternion;


public final class AnimationClip {
	public final String Name;
	public StaticAnimation staticClip;
	private final AnimationClip.KeyframeByBoneIndexElement[] m_KeyFramesByBoneIndex;
	public float Duration;
	private final List m_rootMotionKeyframes = new ArrayList();
	private final Keyframe[] KeyframeArray;
	private static final Quaternion orientation = new Quaternion(-0.07107F, 0.0F, 0.0F, 0.07107F);

	public AnimationClip(float float1, List list, String string, boolean boolean1) {
		this.Duration = float1;
		this.KeyframeArray = (Keyframe[])list.toArray(new Keyframe[0]);
		this.Name = string;
		this.m_KeyFramesByBoneIndex = new AnimationClip.KeyframeByBoneIndexElement[60];
		ArrayList arrayList = new ArrayList();
		int int1 = this.KeyframeArray.length - (boolean1 ? 0 : 1);
		for (int int2 = 0; int2 < 60; ++int2) {
			arrayList.clear();
			for (int int3 = 0; int3 < int1; ++int3) {
				Keyframe keyframe = this.KeyframeArray[int3];
				if (keyframe.Bone == int2) {
					arrayList.add(keyframe);
				}
			}

			this.m_KeyFramesByBoneIndex[int2] = new AnimationClip.KeyframeByBoneIndexElement(arrayList);
		}
	}

	public Keyframe[] getBoneFramesAt(int int1) {
		return this.m_KeyFramesByBoneIndex[int1].m_keyframes;
	}

	public int getRootMotionFrameCount() {
		return this.m_rootMotionKeyframes.size();
	}

	public Keyframe getRootMotionFrameAt(int int1) {
		return (Keyframe)this.m_rootMotionKeyframes.get(int1);
	}

	public Keyframe[] getKeyframes() {
		return this.KeyframeArray;
	}

	private static class KeyframeByBoneIndexElement {
		final Keyframe[] m_keyframes;

		KeyframeByBoneIndexElement(List list) {
			this.m_keyframes = (Keyframe[])list.toArray(new Keyframe[0]);
		}
	}
}
