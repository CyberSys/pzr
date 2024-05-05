package zombie.core.skinnedmodel.animation.debug;

import zombie.util.list.PZArrayUtil;


public class GenericNameWeightRecordingFrame extends GenericNameValueRecordingFrame {
	private float[] m_weights = new float[0];

	public GenericNameWeightRecordingFrame(String string) {
		super(string, "_weights");
	}

	protected void onColumnAdded() {
		this.m_weights = PZArrayUtil.add(this.m_weights, 0.0F);
	}

	public void logWeight(String string, int int1, float float1) {
		int int2 = this.getOrCreateColumn(string, int1);
		float[] floatArray = this.m_weights;
		floatArray[int2] += float1;
	}

	public int getOrCreateColumn(String string, int int1) {
		String string2 = int1 != 0 ? int1 + ":" : "";
		String string3 = String.format("%s%s", string2, string);
		int int2 = super.getOrCreateColumn(string3);
		if (this.m_weights[int2] == 0.0F) {
			return int2;
		} else {
			int int3 = 1;
			while (true) {
				String string4 = String.format("%s%s-%d", string2, string, int3);
				int2 = super.getOrCreateColumn(string4);
				if (this.m_weights[int2] == 0.0F) {
					return int2;
				}

				++int3;
			}
		}
	}

	public float getWeightAt(int int1) {
		return this.m_weights[int1];
	}

	public String getValueAt(int int1) {
		return String.valueOf(this.getWeightAt(int1));
	}

	public void reset() {
		int int1 = 0;
		for (int int2 = this.m_weights.length; int1 < int2; ++int1) {
			this.m_weights[int1] = 0.0F;
		}
	}
}
