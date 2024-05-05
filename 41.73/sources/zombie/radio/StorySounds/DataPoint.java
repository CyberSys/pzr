package zombie.radio.StorySounds;


public final class DataPoint {
	protected float time = 0.0F;
	protected float intensity = 0.0F;

	public DataPoint(float float1, float float2) {
		this.setTime(float1);
		this.setIntensity(float2);
	}

	public float getTime() {
		return this.time;
	}

	public void setTime(float float1) {
		if (float1 < 0.0F) {
			float1 = 0.0F;
		}

		if (float1 > 1.0F) {
			float1 = 1.0F;
		}

		this.time = float1;
	}

	public float getIntensity() {
		return this.intensity;
	}

	public void setIntensity(float float1) {
		if (float1 < 0.0F) {
			float1 = 0.0F;
		}

		if (float1 > 1.0F) {
			float1 = 1.0F;
		}

		this.intensity = float1;
	}
}
