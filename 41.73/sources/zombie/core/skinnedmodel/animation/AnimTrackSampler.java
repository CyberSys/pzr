package zombie.core.skinnedmodel.animation;

import org.lwjgl.util.vector.Matrix4f;


public interface AnimTrackSampler {

	float getTotalTime();

	boolean isLooped();

	void moveToTime(float float1);

	float getCurrentTime();

	void getBoneMatrix(int int1, Matrix4f matrix4f);

	int getNumBones();
}
