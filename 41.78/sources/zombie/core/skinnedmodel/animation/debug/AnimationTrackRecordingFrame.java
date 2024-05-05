package zombie.core.skinnedmodel.animation.debug;

import java.util.List;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.iso.Vector2;


public final class AnimationTrackRecordingFrame extends GenericNameWeightRecordingFrame {
	private Vector2 m_deferredMovement = new Vector2();

	public AnimationTrackRecordingFrame(String string) {
		super(string);
	}

	public void reset() {
		super.reset();
		this.m_deferredMovement.set(0.0F, 0.0F);
	}

	public void logAnimWeights(List list, int[] intArray, float[] floatArray, Vector2 vector2) {
		for (int int1 = 0; int1 < intArray.length; ++int1) {
			int int2 = intArray[int1];
			if (int2 < 0) {
				break;
			}

			float float1 = floatArray[int1];
			AnimationTrack animationTrack = (AnimationTrack)list.get(int2);
			String string = animationTrack.name;
			int int3 = animationTrack.getLayerIdx();
			this.logWeight(string, int3, float1);
		}

		this.m_deferredMovement.set(vector2);
	}

	public Vector2 getDeferredMovement() {
		return this.m_deferredMovement;
	}

	public void writeHeader(StringBuilder stringBuilder) {
		stringBuilder.append(",");
		stringBuilder.append("dm.x").append(",").append("dm.y");
		super.writeHeader(stringBuilder);
	}

	protected void writeData(StringBuilder stringBuilder) {
		stringBuilder.append(",");
		stringBuilder.append(this.getDeferredMovement().x).append(",").append(this.getDeferredMovement().y);
		super.writeData(stringBuilder);
	}
}
