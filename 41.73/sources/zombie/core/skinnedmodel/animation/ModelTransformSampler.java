package zombie.core.skinnedmodel.animation;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.model.SkinningBone;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.debug.DebugOptions;
import zombie.util.IPooledObject;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.list.PZArrayUtil;


public class ModelTransformSampler extends PooledObject implements AnimTrackSampler {
	private AnimationPlayer m_sourceAnimPlayer;
	private AnimationTrack m_track;
	private float m_currentTime = 0.0F;
	private SkinningData m_skinningData;
	private BoneTransform[] m_boneTransforms;
	private Matrix4f[] m_boneModelTransforms;
	private static final Pool s_pool = new Pool(ModelTransformSampler::new);

	private void init(AnimationPlayer animationPlayer, AnimationTrack animationTrack) {
		this.m_sourceAnimPlayer = animationPlayer;
		this.m_track = AnimationTrack.createClone(animationTrack, AnimationTrack::alloc);
		SkinningData skinningData = this.m_sourceAnimPlayer.getSkinningData();
		int int1 = skinningData.numBones();
		this.m_skinningData = skinningData;
		this.m_boneModelTransforms = (Matrix4f[])PZArrayUtil.newInstance(Matrix4f.class, this.m_boneModelTransforms, int1, Matrix4f::new);
		this.m_boneTransforms = (BoneTransform[])PZArrayUtil.newInstance(BoneTransform.class, this.m_boneTransforms, int1, BoneTransform::alloc);
	}

	public static ModelTransformSampler alloc(AnimationPlayer animationPlayer, AnimationTrack animationTrack) {
		ModelTransformSampler modelTransformSampler = (ModelTransformSampler)s_pool.alloc();
		modelTransformSampler.init(animationPlayer, animationTrack);
		return modelTransformSampler;
	}

	public void onReleased() {
		this.m_sourceAnimPlayer = null;
		this.m_track = (AnimationTrack)Pool.tryRelease((IPooledObject)this.m_track);
		this.m_skinningData = null;
		this.m_boneTransforms = (BoneTransform[])Pool.tryRelease((IPooledObject[])this.m_boneTransforms);
	}

	public float getTotalTime() {
		return this.m_track.getDuration();
	}

	public boolean isLooped() {
		return this.m_track.isLooping();
	}

	public void moveToTime(float float1) {
		this.m_currentTime = float1;
		this.m_track.setCurrentTimeValue(float1);
		this.m_track.Update(0.0F);
		for (int int1 = 0; int1 < this.m_boneTransforms.length; ++int1) {
			this.updateBoneAnimationTransform(int1);
		}
	}

	private void updateBoneAnimationTransform(int int1) {
		Vector3f vector3f = ModelTransformSampler.L_updateBoneAnimationTransform.pos;
		Quaternion quaternion = ModelTransformSampler.L_updateBoneAnimationTransform.rot;
		Vector3f vector3f2 = ModelTransformSampler.L_updateBoneAnimationTransform.scale;
		Keyframe keyframe = ModelTransformSampler.L_updateBoneAnimationTransform.key;
		AnimationBoneBinding animationBoneBinding = this.m_sourceAnimPlayer.getCounterRotationBone();
		boolean boolean1 = animationBoneBinding != null && animationBoneBinding.getBone() != null && animationBoneBinding.getBone().Index == int1;
		keyframe.setIdentity();
		AnimationTrack animationTrack = this.m_track;
		this.getTrackTransform(int1, animationTrack, vector3f, quaternion, vector3f2);
		if (boolean1 && animationTrack.getUseDeferredRotation()) {
			Vector3f vector3f3;
			if (DebugOptions.instance.Character.Debug.Animate.ZeroCounterRotationBone.getValue()) {
				vector3f3 = ModelTransformSampler.L_updateBoneAnimationTransform.rotAxis;
				Matrix4f matrix4f = ModelTransformSampler.L_updateBoneAnimationTransform.rotMat;
				matrix4f.setIdentity();
				vector3f3.set(0.0F, 1.0F, 0.0F);
				matrix4f.rotate(-1.5707964F, vector3f3);
				vector3f3.set(1.0F, 0.0F, 0.0F);
				matrix4f.rotate(-1.5707964F, vector3f3);
				HelperFunctions.getRotation(matrix4f, quaternion);
			} else {
				vector3f3 = HelperFunctions.ToEulerAngles(quaternion, ModelTransformSampler.L_updateBoneAnimationTransform.rotEulers);
				HelperFunctions.ToQuaternion((double)vector3f3.x, (double)vector3f3.y, 1.5707963705062866, quaternion);
			}
		}

		boolean boolean2 = animationTrack.getDeferredMovementBoneIdx() == int1;
		if (boolean2) {
			Vector3f vector3f4 = animationTrack.getCurrentDeferredCounterPosition(ModelTransformSampler.L_updateBoneAnimationTransform.deferredPos);
			vector3f.x += vector3f4.x;
			vector3f.y += vector3f4.y;
			vector3f.z += vector3f4.z;
		}

		keyframe.Position.set(vector3f);
		keyframe.Rotation.set(quaternion);
		keyframe.Scale.set(vector3f2);
		this.m_boneTransforms[int1].set(keyframe.Position, keyframe.Rotation, keyframe.Scale);
	}

	private void getTrackTransform(int int1, AnimationTrack animationTrack, Vector3f vector3f, Quaternion quaternion, Vector3f vector3f2) {
		animationTrack.get(int1, vector3f, quaternion, vector3f2);
	}

	public float getCurrentTime() {
		return this.m_currentTime;
	}

	public void getBoneMatrix(int int1, Matrix4f matrix4f) {
		if (int1 == 0) {
			this.m_boneTransforms[0].getMatrix(this.m_boneModelTransforms[0]);
			matrix4f.load(this.m_boneModelTransforms[0]);
		} else {
			SkinningBone skinningBone = this.m_skinningData.getBoneAt(int1);
			SkinningBone skinningBone2 = skinningBone.Parent;
			BoneTransform.mul(this.m_boneTransforms[skinningBone.Index], this.m_boneModelTransforms[skinningBone2.Index], this.m_boneModelTransforms[skinningBone.Index]);
			matrix4f.load(this.m_boneModelTransforms[skinningBone.Index]);
		}
	}

	public int getNumBones() {
		return this.m_skinningData.numBones();
	}

	public static class L_updateBoneAnimationTransform {
		public static final Vector3f pos = new Vector3f();
		public static final Quaternion rot = new Quaternion();
		public static final Vector3f scale = new Vector3f();
		public static final Keyframe key = new Keyframe(new Vector3f(0.0F, 0.0F, 0.0F), new Quaternion(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F));
		public static final Vector3f rotAxis = new Vector3f();
		public static final Matrix4f rotMat = new Matrix4f();
		public static final Vector3f rotEulers = new Vector3f();
		public static final Vector3f deferredPos = new Vector3f();
	}
}
