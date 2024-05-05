package zombie.core.skinnedmodel.animation.sharedskele;

import org.lwjgl.util.vector.Matrix4f;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.animation.AnimTrackSampler;
import zombie.debug.DebugOptions;


public class SharedSkeleAnimationTrack implements AnimTrackSampler {
	private int m_numFrames;
	private float m_totalTime;
	private boolean m_isLooped;
	private SharedSkeleAnimationTrack.BoneTrack[] m_boneTracks;
	private float m_currentTime = 0.0F;

	public void set(AnimTrackSampler animTrackSampler, float float1) {
		float float2 = animTrackSampler.getTotalTime();
		boolean boolean1 = animTrackSampler.isLooped();
		int int1 = animTrackSampler.getNumBones();
		this.m_totalTime = float2;
		this.m_numFrames = PZMath.max((int)(float2 * float1 + 0.99F), 1);
		this.m_isLooped = boolean1;
		this.m_boneTracks = new SharedSkeleAnimationTrack.BoneTrack[int1];
		for (int int2 = 0; int2 < int1; ++int2) {
			this.m_boneTracks[int2] = new SharedSkeleAnimationTrack.BoneTrack();
			this.m_boneTracks[int2].m_animationData = new float[this.m_numFrames * 16];
		}

		Matrix4f matrix4f = new Matrix4f();
		float float3 = float2 / (float)(this.m_numFrames - 1);
		for (int int3 = 0; int3 < this.m_numFrames; ++int3) {
			float float4 = float3 * (float)int3;
			animTrackSampler.moveToTime(float4);
			for (int int4 = 0; int4 < int1; ++int4) {
				animTrackSampler.getBoneMatrix(int4, matrix4f);
				int int5 = int3 * 16;
				SharedSkeleAnimationTrack.BoneTrack boneTrack = this.m_boneTracks[int4];
				float[] floatArray = boneTrack.m_animationData;
				floatArray[int5] = matrix4f.m00;
				floatArray[int5 + 1] = matrix4f.m01;
				floatArray[int5 + 2] = matrix4f.m02;
				floatArray[int5 + 3] = matrix4f.m03;
				floatArray[int5 + 4] = matrix4f.m10;
				floatArray[int5 + 5] = matrix4f.m11;
				floatArray[int5 + 6] = matrix4f.m12;
				floatArray[int5 + 7] = matrix4f.m13;
				floatArray[int5 + 8] = matrix4f.m20;
				floatArray[int5 + 9] = matrix4f.m21;
				floatArray[int5 + 10] = matrix4f.m22;
				floatArray[int5 + 11] = matrix4f.m23;
				floatArray[int5 + 12] = matrix4f.m30;
				floatArray[int5 + 13] = matrix4f.m31;
				floatArray[int5 + 14] = matrix4f.m32;
				floatArray[int5 + 15] = matrix4f.m33;
			}
		}
	}

	public float getTotalTime() {
		return this.m_totalTime;
	}

	public boolean isLooped() {
		return this.m_isLooped;
	}

	public void moveToTime(float float1) {
		this.m_currentTime = float1;
	}

	public float getCurrentTime() {
		return this.m_currentTime;
	}

	public void getBoneMatrix(int int1, Matrix4f matrix4f) {
		float float1 = this.m_totalTime;
		int int2 = this.m_numFrames;
		float float2 = this.getCurrentTime();
		float float3 = float2 / float1;
		float float4 = float3 * (float)(int2 - 1);
		if (this.isLooped()) {
			this.sampleAtTime_Looped(matrix4f, int1, float4);
		} else {
			this.sampleAtTime_NonLooped(matrix4f, int1, float4);
		}
	}

	public int getNumBones() {
		return this.m_boneTracks != null ? this.m_boneTracks.length : 0;
	}

	private void sampleAtTime_NonLooped(Matrix4f matrix4f, int int1, float float1) {
		int int2 = (int)float1;
		float float2 = float1 - (float)int2;
		int int3 = PZMath.clamp(int2, 0, this.m_numFrames - 1);
		int int4 = PZMath.clamp(int3 + 1, 0, this.m_numFrames - 1);
		boolean boolean1 = DebugOptions.instance.Animation.SharedSkeles.AllowLerping.getValue();
		this.sampleBoneData(int1, int3, int4, float2, boolean1, matrix4f);
	}

	private void sampleAtTime_Looped(Matrix4f matrix4f, int int1, float float1) {
		int int2 = (int)float1;
		float float2 = float1 - (float)int2;
		int int3 = int2 % this.m_numFrames;
		int int4 = (int3 + 1) % this.m_numFrames;
		boolean boolean1 = DebugOptions.instance.Animation.SharedSkeles.AllowLerping.getValue();
		this.sampleBoneData(int1, int3, int4, float2, boolean1, matrix4f);
	}

	private void sampleBoneData(int int1, int int2, int int3, float float1, boolean boolean1, Matrix4f matrix4f) {
		int int4 = int2 * 16;
		SharedSkeleAnimationTrack.BoneTrack boneTrack = this.m_boneTracks[int1];
		float[] floatArray = boneTrack.m_animationData;
		if (int2 != int3 && boolean1) {
			int int5 = int3 * 16;
			matrix4f.m00 = PZMath.lerp(floatArray[int4], floatArray[int5], float1);
			matrix4f.m01 = PZMath.lerp(floatArray[int4 + 1], floatArray[int5 + 1], float1);
			matrix4f.m02 = PZMath.lerp(floatArray[int4 + 2], floatArray[int5 + 2], float1);
			matrix4f.m03 = PZMath.lerp(floatArray[int4 + 3], floatArray[int5 + 3], float1);
			matrix4f.m10 = PZMath.lerp(floatArray[int4 + 4], floatArray[int5 + 4], float1);
			matrix4f.m11 = PZMath.lerp(floatArray[int4 + 5], floatArray[int5 + 5], float1);
			matrix4f.m12 = PZMath.lerp(floatArray[int4 + 6], floatArray[int5 + 6], float1);
			matrix4f.m13 = PZMath.lerp(floatArray[int4 + 7], floatArray[int5 + 7], float1);
			matrix4f.m20 = PZMath.lerp(floatArray[int4 + 8], floatArray[int5 + 8], float1);
			matrix4f.m21 = PZMath.lerp(floatArray[int4 + 9], floatArray[int5 + 9], float1);
			matrix4f.m22 = PZMath.lerp(floatArray[int4 + 10], floatArray[int5 + 10], float1);
			matrix4f.m23 = PZMath.lerp(floatArray[int4 + 11], floatArray[int5 + 11], float1);
			matrix4f.m30 = PZMath.lerp(floatArray[int4 + 12], floatArray[int5 + 12], float1);
			matrix4f.m31 = PZMath.lerp(floatArray[int4 + 13], floatArray[int5 + 13], float1);
			matrix4f.m32 = PZMath.lerp(floatArray[int4 + 14], floatArray[int5 + 14], float1);
			matrix4f.m33 = PZMath.lerp(floatArray[int4 + 15], floatArray[int5 + 15], float1);
		} else {
			matrix4f.m00 = floatArray[int4];
			matrix4f.m01 = floatArray[int4 + 1];
			matrix4f.m02 = floatArray[int4 + 2];
			matrix4f.m03 = floatArray[int4 + 3];
			matrix4f.m10 = floatArray[int4 + 4];
			matrix4f.m11 = floatArray[int4 + 5];
			matrix4f.m12 = floatArray[int4 + 6];
			matrix4f.m13 = floatArray[int4 + 7];
			matrix4f.m20 = floatArray[int4 + 8];
			matrix4f.m21 = floatArray[int4 + 9];
			matrix4f.m22 = floatArray[int4 + 10];
			matrix4f.m23 = floatArray[int4 + 11];
			matrix4f.m30 = floatArray[int4 + 12];
			matrix4f.m31 = floatArray[int4 + 13];
			matrix4f.m32 = floatArray[int4 + 14];
			matrix4f.m33 = floatArray[int4 + 15];
		}
	}

	private static class BoneTrack {
		private float[] m_animationData;
	}
}
